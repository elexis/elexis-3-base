package ch.elexis.regiomed.order.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RegiomedAlternativesResponse {

	@SerializedName("Article")
	private ArticleInfo article;

	@SerializedName("Alternatives")
	private List<AlternativeItem> alternatives = new ArrayList<>();

	public ArticleInfo getArticle() {
		return article;
	}

	public void setArticle(ArticleInfo article) {
		this.article = article;
	}

	public List<AlternativeItem> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<AlternativeItem> alternatives) {
		this.alternatives = alternatives;
	}

	public static class ArticleInfo {
		@SerializedName("key_Prod")
		private long keyProd;

		@SerializedName("Pharmacode")
		private int pharmaCode;

		@SerializedName("EAN")
		private long ean;

		@SerializedName("Prodname")
		private String prodName;

		@SerializedName("GalAvailState")
		private String availState;

		@SerializedName("GalAvailMessage")
		private String availMessage;

		public long getKeyProd() {
			return keyProd;
		}

		public void setKeyProd(long keyProd) {
			this.keyProd = keyProd;
		}

		public int getPharmaCode() {
			return pharmaCode;
		}

		public void setPharmaCode(int pharmaCode) {
			this.pharmaCode = pharmaCode;
		}

		public long getEan() {
			return ean;
		}

		public void setEan(long ean) {
			this.ean = ean;
		}

		public String getProdName() {
			return prodName;
		}

		public void setProdName(String prodName) {
			this.prodName = prodName;
		}

		public String getAvailState() {
			return availState;
		}

		public void setAvailState(String availState) {
			this.availState = availState;
		}

		public String getAvailMessage() {
			return availMessage;
		}

		public void setAvailMessage(String availMessage) {
			this.availMessage = availMessage;
		}
	}

	public static class AlternativeItem {
		@SerializedName("AltType")
		private String altType;

		@SerializedName("key_Prod")
		private long keyProd;

		@SerializedName("Pharmacode")
		private int pharmaCode;

		@SerializedName("EAN")
		private long ean;

		@SerializedName("Prodname")
		private String prodName;

		@SerializedName("Price")
		private double price;

		@SerializedName("GalAvailState")
		private String availState;

		@SerializedName("GalAvailMessage")
		private String availMessage;

		public String getAltType() {
			return altType;
		}

		public void setAltType(String altType) {
			this.altType = altType;
		}

		public long getKeyProd() {
			return keyProd;
		}

		public void setKeyProd(long keyProd) {
			this.keyProd = keyProd;
		}

		public int getPharmaCode() {
			return pharmaCode;
		}

		public void setPharmaCode(int pharmaCode) {
			this.pharmaCode = pharmaCode;
		}

		public long getEan() {
			return ean;
		}

		public void setEan(long ean) {
			this.ean = ean;
		}

		public String getProdName() {
			return prodName;
		}

		public void setProdName(String prodName) {
			this.prodName = prodName;
		}

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public String getAvailState() {
			return availState;
		}

		public void setAvailState(String availState) {
			this.availState = availState;
		}

		public String getAvailMessage() {
			return availMessage;
		}

		public void setAvailMessage(String availMessage) {
			this.availMessage = availMessage;
		}
	}
}