package ch.elexis.regiomed.order.ui.model;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse.ProductResult;

public class SearchProductViewModel {

	private final ProductResult product;
	private final int index;

	public SearchProductViewModel(ProductResult product, int index) {
		this.product = product;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public String getProdName() {
		return escapeHtml(product.prodName);
	}

	public String getEan() {
		return escapeHtml(product.ean);
	}

	public String getMessage() {
		return escapeHtml(product.message);
	}

	public String getPrice() {
		return String.format("%.2f", product.price);
	}

	public String getStock() {
		return escapeHtml(product.availableInventory);
	}

	public boolean hasStock() {
		return StringUtils.isNotBlank(product.availableInventory);
	}

	private String escapeHtml(String text) {
		if (StringUtils.isBlank(text))
			return "";
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}
}