package at.medevit.ch.artikelstamm.model.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.model.history.PriceChangeEntry.EncounterInfo;
import at.medevit.ch.artikelstamm.model.importer.PriceChangeListener;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQueryCursor;
import ch.rgw.tools.Money;

/**
 * Collects price changes reported by the {@link at.medevit.ch.artikelstamm.model.importer.ArtikelstammImporter}
 * during an article master data update (via {@link PriceChangeListener}) and
 * automatically adjusts prices in open encounters of the current month.
 */
@Component(service = ArticlePriceDiffService.class)
public class ArticlePriceDiffService implements PriceChangeListener {

	private static final Logger log = LoggerFactory.getLogger(ArticlePriceDiffService.class);
	public static final String PREFERENCE_AUTO_ADJUST_OPEN_ENCOUNTERS = "rdus.autoAdjustOpenEncounters"; //$NON-NLS-1$
	private final Map<String, PriceChange> collectedChanges = new HashMap<>();

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=at.medevit.ch.artikelstamm.model)")
	private IModelService artikelstammModelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private IModelService coreModelService;

	@Reference
	private IConfigService configService;

	@Reference
	private IContextService contextService;

	private PriceChangeLogger priceChangeLogger = new PriceChangeLogger();

	/**
	 * Helper: safely converts {@link Money} to {@link BigDecimal}.
	 */
	private static BigDecimal toBigDecimal(Money money) {
		if (money == null) {
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(money.getCents()).divide(BigDecimal.valueOf(100));
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * Starts the collection of price changes. Called before the import runs; the
	 * importer then reports each price change via {@link #onPriceChange}.
	 */
	public void startPriceChangeCollection() {
		collectedChanges.clear();
		log.info("Collecting article price changes during import."); //$NON-NLS-1$
	}

	@Override
	public void onPriceChange(PriceChangeEvent event) {
		if (event.oldPrice() == null) {
			// newly added article, no old price to compare against
			return;
		}
		collectedChanges.put(event.articleId(),
				new PriceChange(toBigDecimal(event.oldPrice()), toBigDecimal(event.newPrice())));
	}

	/**
	 * Applies the price changes collected during the import to all open encounters
	 * of the current month, logs the performed changes via {@link PriceChangeLogger}
	 * and releases the collected changes.
	 */
	public UpdateStatistics applyCollectedPriceChanges() {
		Map<String, PriceChange> changes = new HashMap<>(collectedChanges);
		Map<String, List<EncounterInfo>> updatedEncounters = new HashMap<>();
		try {
			log.info("Found {} price changes.", changes.size()); //$NON-NLS-1$
			UpdateStatistics stats = updateOpenEncounters(changes, updatedEncounters);
			logCollectedPriceChanges(changes, updatedEncounters);
			return stats;
		} finally {
			collectedChanges.clear();
		}
	}

	/**
	 * Persists the collected price changes and the encounters updated because of
	 * them via {@link PriceChangeLogger}.
	 */
	private void logCollectedPriceChanges(Map<String, PriceChange> changes,
			Map<String, List<EncounterInfo>> updatedEncounters) {
		if (changes.isEmpty()) {
			return;
		}
		Map<IArtikelstammItem, PriceChange> byArticle = new HashMap<>();
		for (Map.Entry<String, PriceChange> e : changes.entrySet()) {
			artikelstammModelService.load(e.getKey(), IArtikelstammItem.class)
					.ifPresent(article -> byArticle.put(article, e.getValue()));
		}
		boolean autoAdjust = configService.get(PREFERENCE_AUTO_ADJUST_OPEN_ENCOUNTERS, true);
		try {
			priceChangeLogger.logPriceChanges(byArticle, updatedEncounters, autoAdjust);
		} catch (Exception e) {
			log.warn("Failed to log price changes: {}", e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Updates prices in all open encounters of the current month and posts UI
	 * events.
	 */
	public UpdateStatistics updateOpenEncounters(Map<String, PriceChange> priceChanges,
			Map<String, List<EncounterInfo>> updatedEncounters) {
		UpdateStatistics stats = new UpdateStatistics();
		boolean autoAdjust = configService.get(PREFERENCE_AUTO_ADJUST_OPEN_ENCOUNTERS, true);

		if (priceChanges.isEmpty()) {
			log.info("No price changes — nothing to update."); //$NON-NLS-1$
			return stats;
		}
		if (!autoAdjust) {
			log.info("Auto-adjustment is DISABLED via Preference."); //$NON-NLS-1$
			return stats;
		}

		YearMonth currentMonth = YearMonth.now();
		LocalDate firstDayOfMonth = currentMonth.atDay(1);
		LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();

		IQuery<IEncounter> query = coreModelService.getQuery(IEncounter.class)
				.and(ModelPackage.Literals.IENCOUNTER__INVOICE, ch.elexis.core.services.IQuery.COMPARATOR.EQUALS, null)
				.and(ModelPackage.Literals.IENCOUNTER__DATE, ch.elexis.core.services.IQuery.COMPARATOR.GREATER_OR_EQUAL,
						firstDayOfMonth)
				.and(ModelPackage.Literals.IENCOUNTER__DATE, ch.elexis.core.services.IQuery.COMPARATOR.LESS_OR_EQUAL,
						lastDayOfMonth);
		try (IQueryCursor<IEncounter> cursor = query.executeAsCursor()) {
			stats.totalEncounters = cursor.size();
			while (cursor.hasNext()) {
				IEncounter encounter = cursor.next();
				updateEncounterPrices(encounter, priceChanges, updatedEncounters, stats);
			}
		}

		contextService.postEvent(ElexisEventTopics.EVENT_RELOAD, IEncounter.class);
		return stats;
	}

	/**
	 * Applies price changes for a single encounter.
	 */
	private void updateEncounterPrices(IEncounter encounter, Map<String, PriceChange> changeMap,
			Map<String, List<EncounterInfo>> updatedEncounters, UpdateStatistics stats) {
		boolean changed = false;
		for (IBilled billed : encounter.getBilled()) {
			if (billed.getBillable() instanceof IArtikelstammItem article) {
				PriceChange priceChange = changeMap.get(article.getId());
				if (priceChange != null && !billed.isChangedPrice()) {
					Money newPrice = new Money(priceChange.newPrice().doubleValue());
					try {
						billed.setPrice(newPrice);
						changed = true;
						stats.itemsUpdated++;
						updatedEncounters.computeIfAbsent(article.getId(), k -> new ArrayList<>())
								.add(new EncounterInfo(encounter.getId(), String.valueOf(encounter.getDate()),
										encounter.getPatient() != null ? encounter.getPatient().getLabel() : null,
										encounter.getMandator() != null ? encounter.getMandator().getLabel() : null));
						log.debug("Encounter {} ({}): {} — price set from {} to {}.", encounter.getId(), //$NON-NLS-1$
								encounter.getDate(), article.getName(), priceChange.oldPrice(), priceChange.newPrice());
						coreModelService.save(billed);
						contextService.postEvent(ElexisEventTopics.EVENT_UPDATE, billed);
					} catch (IllegalStateException e) {
						log.warn("Cannot set price for {}: {}", article.getName(), e.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}

		if (changed) {
			try {
				coreModelService.save(encounter);
				contextService.postEvent(ElexisEventTopics.EVENT_UPDATE, encounter);
				stats.encountersUpdated++;
				log.debug("Encounter {} saved successfully.", encounter.getId()); //$NON-NLS-1$
			} catch (Exception e) {
				log.error("Error while saving encounter {}.", encounter.getId(), e); //$NON-NLS-1$
			}
		}
	}

	public record PriceChange(BigDecimal oldPrice, BigDecimal newPrice) {
		@Override
		public String toString() {
			return String.format("%.2f → %.2f", oldPrice, newPrice); //$NON-NLS-1$
		}
	}

	public static class UpdateStatistics {
		public int totalEncounters = 0;
		public int encountersUpdated = 0;
		public int itemsUpdated = 0;

		@Override
		public String toString() {
			return String.format("%d/%d encounters updated (%d billed items).", encountersUpdated, totalEncounters, //$NON-NLS-1$
					itemsUpdated);
		}
	}
}