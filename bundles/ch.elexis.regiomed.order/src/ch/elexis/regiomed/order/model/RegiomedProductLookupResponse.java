package ch.elexis.regiomed.order.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RegiomedProductLookupResponse {

	@SerializedName("Products")
	public List<ProductResult> products;

	public static class ProductResult {
		@SerializedName("Sort")
		public int sort;

		@SerializedName("key_Prod")
		public long keyProd;

		@SerializedName("EAN")
		public String ean;

		@SerializedName("PC")
		public int pharmaCode;

		@SerializedName("Price")
		public double price;

		@SerializedName("Prodname")
		public String prodName;

		@SerializedName("hersteller")
		public String manufacturer;

		@SerializedName("Status")
		public String status;

		@SerializedName("Message")
		public String message;

		@SerializedName("Score")
		public double score;

		@SerializedName("AvailableInventory")
		public String availableInventory;
	}
}