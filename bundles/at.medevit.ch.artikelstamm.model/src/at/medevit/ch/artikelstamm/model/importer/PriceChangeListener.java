package at.medevit.ch.artikelstamm.model.importer;

import ch.rgw.tools.Money;

/**
 * Listener notified by {@link ArtikelstammImporter} whenever the selling price of
 * an article is changed during an artikelstamm import. Allows interested parties
 * to collect price changes without snapshotting the whole article master data.
 */
public interface PriceChangeListener {

	/**
	 * @param articleId id of the changed article
	 * @param oldPrice  selling price before the update, or <code>null</code> if the
	 *                  article did not exist before the import
	 * @param newPrice  selling price after the update
	 */
	record PriceChangeEvent(String articleId, Money oldPrice, Money newPrice) {
	}

	void onPriceChange(PriceChangeEvent event);
}
