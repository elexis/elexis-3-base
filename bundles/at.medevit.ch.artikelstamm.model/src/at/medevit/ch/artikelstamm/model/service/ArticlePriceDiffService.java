package at.medevit.ch.artikelstamm.model.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.Money;

/**
 * Compares old and new article master data to find price changes and
 * automatically adjusts prices in open encounters of the current month.
 * (Stripped version without History-Logging for PR #2)
 */
@Component
public class ArticlePriceDiffService {

	private static final Logger log = LoggerFactory.getLogger(ArticlePriceDiffService.class);
	public static final String PREFERENCE_AUTO_ADJUST_OPEN_ENCOUNTERS = "rdus.autoAdjustOpenEncounters"; //$NON-NLS-1$
	private Map<String, BigDecimal> oldPrices = new HashMap<>();
	private Map<String, BigDecimal> newPrices = new HashMap<>();

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

	public void captureOldPrices() {
		List<IArtikelstammItem> allArticles = ArtikelstammModelServiceHolder.get().findAll(IArtikelstammItem.class);
		oldPrices = allArticles.stream().filter(article -> !"VERSION".equals(article.getId())).collect( //$NON-NLS-1$
				Collectors.toMap(IArtikelstammItem::getId, a -> toBigDecimal(a.getSellingPrice()), (a, b) -> a));
		log.info("Cached old article master data ({} items).", oldPrices.size()); //$NON-NLS-1$
	}

	public void captureNewPrices() {
		List<IArtikelstammItem> allArticles = ArtikelstammModelServiceHolder.get().findAll(IArtikelstammItem.class);
		newPrices = allArticles.stream().filter(article -> !"VERSION".equals(article.getId())).collect( //$NON-NLS-1$
				Collectors.toMap(IArtikelstammItem::getId, a -> toBigDecimal(a.getSellingPrice()), (a, b) -> a));
		log.info("Loaded new article master data ({} items).", newPrices.size()); //$NON-NLS-1$
	}

	/**
	 * Compares all articles and returns the changes (old → new).
	 */
	public Map<IArtikelstammItem, PriceChange> compare() {
		Map<IArtikelstammItem, PriceChange> diffs = new HashMap<>();

		for (String id : newPrices.keySet()) {
			BigDecimal newPrice = newPrices.get(id);
			BigDecimal oldPrice = oldPrices.get(id);

			if (oldPrice == null || newPrice == null) {
				continue;
			}
			if (newPrice.compareTo(oldPrice) != 0) {
				IArtikelstammItem art = ArtikelstammModelServiceHolder.get().load(id, IArtikelstammItem.class)
						.orElse(null);
				if (art != null) {
					diffs.put(art, new PriceChange(oldPrice, newPrice));
				}
			}
		}
		log.info("Found {} price changes.", diffs.size()); //$NON-NLS-1$
		return diffs;
	}

	/**
	 * Updates prices in all open encounters of the current month and posts UI
	 * events.
	 */
	public UpdateStatistics updateOpenEncounters(Map<IArtikelstammItem, PriceChange> priceChanges) {
		UpdateStatistics stats = new UpdateStatistics();
		boolean autoAdjust = ConfigServiceHolder.getGlobal(PREFERENCE_AUTO_ADJUST_OPEN_ENCOUNTERS, true);

		if (priceChanges.isEmpty()) {
			log.info("No price changes — nothing to update."); //$NON-NLS-1$
			return stats;
		}
		if (!autoAdjust) {
			log.info("Auto-adjustment is DISABLED via Preference."); //$NON-NLS-1$
			return stats;
		}

		Map<String, PriceChange> changeMap = priceChanges.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));

		YearMonth currentMonth = YearMonth.now();
		LocalDate firstDayOfMonth = currentMonth.atDay(1);
		LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();

		List<IEncounter> openEncounters = CoreModelServiceHolder.get().getQuery(IEncounter.class)
				.and(ModelPackage.Literals.IENCOUNTER__INVOICE, ch.elexis.core.services.IQuery.COMPARATOR.EQUALS, null)
				.and(ModelPackage.Literals.IENCOUNTER__DATE, ch.elexis.core.services.IQuery.COMPARATOR.GREATER_OR_EQUAL,
						firstDayOfMonth)
				.and(ModelPackage.Literals.IENCOUNTER__DATE, ch.elexis.core.services.IQuery.COMPARATOR.LESS_OR_EQUAL,
						lastDayOfMonth)
				.execute();

		stats.totalEncounters = openEncounters.size();
		for (IEncounter encounter : openEncounters) {
			updateEncounterPrices(encounter, changeMap, stats);
		}

		ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IEncounter.class);
		return stats;
	}

	/**
	 * Applies price changes for a single encounter.
	 */
	private void updateEncounterPrices(IEncounter encounter, Map<String, PriceChange> changeMap,
			UpdateStatistics stats) {
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
						log.debug("Encounter {} ({}): {} — price set from {} to {}.", encounter.getId(), //$NON-NLS-1$
								encounter.getDate(), article.getName(), priceChange.oldPrice(), priceChange.newPrice());
						CoreModelServiceHolder.get().save(billed);
						ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, billed);
					} catch (IllegalStateException e) {
						log.warn("Cannot set price for {}: {}", article.getName(), e.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}

		if (changed) {
			try {
				CoreModelServiceHolder.get().save(encounter);
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, encounter);
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