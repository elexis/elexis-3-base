package at.medevit.ch.artikelstamm.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.model.history.PriceChangeEntry;
import at.medevit.ch.artikelstamm.model.history.PriceChangeEntry.EncounterInfo;
import at.medevit.ch.artikelstamm.model.service.ArticlePriceDiffService.PriceChange;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class PriceChangeLogger {

	private static final Logger log = LoggerFactory.getLogger(PriceChangeLogger.class);
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public void logPriceChanges(Map<IArtikelstammItem, PriceChange> priceChanges,
			Map<String, List<EncounterInfo>> updatedEncounters, boolean autoAdjustEnabled) {

		if (priceChanges == null || priceChanges.isEmpty()) {
			log.debug("No price changes to log."); //$NON-NLS-1$
			return;
		}

		try {
			LocalDate today = LocalDate.now();
			IBlob blob = createOrUpdateBlobEntry(today);
			List<PriceChangeEntry> entries = buildEntries(priceChanges, updatedEncounters, autoAdjustEnabled);
			String json = gson.toJson(entries);
			blob.setStringContent(json);
			blob.setDate(today);
			CoreModelServiceHolder.get().save(blob);
			log.info("Saved price changes to HEAP (IBlob) [{}]: {} article(s) changed.", blob.getId(), //$NON-NLS-1$
					priceChanges.size());
		} catch (Exception e) {
			log.error("Failed to persist price changes to HEAP.", e); //$NON-NLS-1$
		}
	}

	private IBlob createOrUpdateBlobEntry(LocalDate date) {
		String blobId = "ArtikelstammPriceUpdate_" + date.toString(); //$NON-NLS-1$
		return CoreModelServiceHolder.get().load(blobId, IBlob.class).orElseGet(() -> {
			IBlob newBlob = CoreModelServiceHolder.get().create(IBlob.class);
			newBlob.setId(blobId);
			return newBlob;
		});
	}

	private List<PriceChangeEntry> buildEntries(Map<IArtikelstammItem, PriceChange> priceChanges,
			Map<String, List<EncounterInfo>> updatedEncounters, boolean autoAdjustEnabled) {
		List<PriceChangeEntry> entries = new ArrayList<>();
		priceChanges.forEach((article, change) -> {
			String details = String.format("%s: %.2f -> %.2f CHF", article.getName(), change.oldPrice().doubleValue(), //$NON-NLS-1$
					change.newPrice().doubleValue());
			String extra = String.format("GTIN: %s, ATC: %s%s", article.getGtin() != null ? article.getGtin() : "N/A", //$NON-NLS-1$ //$NON-NLS-2$
					article.getAtcCode() != null ? article.getAtcCode() : "N/A", //$NON-NLS-1$
					autoAdjustEnabled ? StringUtils.EMPTY : " | AutoAdjust: OFF"); //$NON-NLS-1$
			List<EncounterInfo> infos = (updatedEncounters != null) ? updatedEncounters.get(article.getId()) : null;
			entries.add(new PriceChangeEntry("PRICE_CHANGE", "SYSTEM", details, extra, infos)); //$NON-NLS-1$ //$NON-NLS-2$
		});
		return entries;
	}
}