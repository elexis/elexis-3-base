package ch.elexis.regiomed.order.ui.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse.ProductResult;

public class SearchProductViewModel {

	private final ProductResult product;
	private final int index;
	private final Map<String, Integer> localStocks = new HashMap<>();

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

	public String getManufacturer() {
		return escapeHtml(product.manufacturer);
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

	public void addLocalStock(String stockCode, int quantity) {
		localStocks.put(stockCode, quantity);
	}

	public Map<String, Integer> getLocalStocks() {
		return localStocks;
	}

	public int getTotalLocalStock() {
		return localStocks.values().stream().mapToInt(Integer::intValue).sum();
	}

	public String getLocalStocksJson() {
		String json = localStocks.entrySet().stream().map(e -> "\"" + e.getKey() + "\":" + e.getValue())
				.collect(Collectors.joining(","));
		return "{" + json + "}";
	}

	public int getColorIndexForStock(String stockCode) {
		if (stockCode == null)
			return 0;
		return Math.abs(stockCode.hashCode()) % 5;
	}

	public String getRowColorClass() {
		if (localStocks.isEmpty()) {
			return "";
		}
		if (localStocks.size() > 1) {
			return "stock-row-mixed";
		} else {
			String stockCode = localStocks.keySet().iterator().next();
			return "stock-row-" + getColorIndexForStock(stockCode);
		}
	}

	private String escapeHtml(String text) {
		if (StringUtils.isBlank(text))
			return "";
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}

	private int getHueForStock(String stockCode) {
		if (stockCode == null)
			return 0;
		return Math.abs(stockCode.hashCode() + 10) % 360;
	}

	public String getStockBadgeStyle(String stockCode) {
		int hue = getHueForStock(stockCode);
		return "background-color: hsl(" + hue + ", 30%, 50%); color: white;";
	}

	public String getRowStyle() {
		if (localStocks.isEmpty()) {
			return "";
		}
		if (localStocks.size() > 1) {
			return "background-color: #fffdf0; border-left: 4px solid #e0c060;";
		} else {
			String stockCode = localStocks.keySet().iterator().next();
			int hue = getHueForStock(stockCode);
			return "background-color: hsl(" + hue + ", 50%, 92%); border-left: 4px solid hsl(" + hue + ", 50%, 40%);";
		}
	}
}