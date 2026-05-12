package ch.elexis.regiomed.order.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RegiomedOrderResponse {

	@SerializedName("client")
	private String client;

	@SerializedName("clientID")
	private long clientID;

	@SerializedName("checkSuccess")
	private boolean checkSuccess;

	@SerializedName("message")
	private String message;

	@SerializedName("deliveryType")
	private String deliveryType;

	@SerializedName("deliveryDate")
	private String deliveryDate;

	@SerializedName("articleCount")
	private int articleCount;

	@SerializedName("articlesOK")
	private int articlesOK;

	@SerializedName("articlesNOK")
	private int articlesNOK;

	@SerializedName("orderSent")
	private boolean orderSent;

	@SerializedName("errorEmailSent")
	private boolean errorEmailSent;

	@SerializedName("user")
	private int user;

	@SerializedName("patInfo")
	private String patInfo;

	@SerializedName("reference")
	private String reference;

	@SerializedName("authMethod")
	private String authMethod;

	@SerializedName("articles")
	private List<ArticleResult> articles = new ArrayList<>();

	@SerializedName("alternatives")
	private List<AlternativeResult> alternatives = new ArrayList<>();

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public long getClientID() {
		return clientID;
	}

	public void setClientID(long clientID) {
		this.clientID = clientID;
	}

	public boolean isCheckSuccess() {
		return checkSuccess;
	}

	public void setCheckSuccess(boolean checkSuccess) {
		this.checkSuccess = checkSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public int getArticleCount() {
		return articleCount;
	}

	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}

	public int getArticlesOK() {
		return articlesOK;
	}

	public void setArticlesOK(int articlesOK) {
		this.articlesOK = articlesOK;
	}

	public int getArticlesNOK() {
		return articlesNOK;
	}

	public void setArticlesNOK(int articlesNOK) {
		this.articlesNOK = articlesNOK;
	}

	public boolean isOrderSent() {
		return orderSent;
	}

	public void setOrderSent(boolean orderSent) {
		this.orderSent = orderSent;
	}

	public boolean isErrorEmailSent() {
		return errorEmailSent;
	}

	public void setErrorEmailSent(boolean errorEmailSent) {
		this.errorEmailSent = errorEmailSent;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public String getPatInfo() {
		return patInfo;
	}

	public void setPatInfo(String patInfo) {
		this.patInfo = patInfo;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	public List<ArticleResult> getArticles() {
		return articles;
	}

	public void setArticles(List<ArticleResult> articles) {
		this.articles = articles;
	}

	public List<AlternativeResult> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<AlternativeResult> alternatives) {
		this.alternatives = alternatives;
	}

	public static class ArticleResult {
		@SerializedName("key_Prod")
		private int keyProd;

		@SerializedName(value = "pharmaCode", alternate = { "PharmaCode" })
		private int pharmaCode;

		@SerializedName(value = "eanID", alternate = { "EanID" })
		private long eanID;

		@SerializedName(value = "description", alternate = { "Description" })
		private String description;

		@SerializedName(value = "quantity", alternate = { "Quantity" })
		private int quantity;

		@SerializedName(value = "success", alternate = { "Success" })
		private boolean success;

		@SerializedName("successAvailability")
		private boolean successAvailability;

		@SerializedName(value = "info", alternate = { "Info" })
		private String info;

		@SerializedName("availState")
		private String availState;

		@SerializedName("availMsg")
		private String availMsg;

		@SerializedName("availableInventory")
		private int availableInventory;

		@SerializedName("availMsgOrg")
		private String availMsgOrg;

		public int getKeyProd() {
			return keyProd;
		}

		public void setKeyProd(int keyProd) {
			this.keyProd = keyProd;
		}

		public int getPharmaCode() {
			return pharmaCode;
		}

		public void setPharmaCode(int pharmaCode) {
			this.pharmaCode = pharmaCode;
		}

		public long getEanID() {
			return eanID;
		}

		public void setEanID(long eanID) {
			this.eanID = eanID;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public boolean isSuccessAvailability() {
			return successAvailability;
		}

		public void setSuccessAvailability(boolean successAvailability) {
			this.successAvailability = successAvailability;
		}

		public String getInfo() {
			return info;
		}

		public void setInfo(String info) {
			this.info = info;
		}

		public String getAvailState() {
			return availState;
		}

		public void setAvailState(String availState) {
			this.availState = availState;
		}

		public String getAvailMsg() {
			return availMsg;
		}

		public void setAvailMsg(String availMsg) {
			this.availMsg = availMsg;
		}

		public int getAvailableInventory() {
			return availableInventory;
		}

		public void setAvailableInventory(int availableInventory) {
			this.availableInventory = availableInventory;
		}

		public String getAvailMsgOrg() {
			return availMsgOrg;
		}

		public void setAvailMsgOrg(String availMsgOrg) {
			this.availMsgOrg = availMsgOrg;
		}
	}

	public static class AlternativeResult {
		@SerializedName("key_ProdOrg")
		private int keyProdOrg;

		@SerializedName("pharmaCodeOrg")
		private int pharmaCodeOrg;

		@SerializedName("eanIDOrg")
		private long eanIDOrg;

		@SerializedName("descriptionOrg")
		private String descriptionOrg;

		@SerializedName("key_Prod")
		private int keyProd;

		@SerializedName("pharmaCode")
		private int pharmaCode;

		@SerializedName("eanID")
		private long eanID;

		@SerializedName("description")
		private String description;

		@SerializedName("price")
		private Double price;

		@SerializedName("availState")
		private String availState;

		@SerializedName("availMsg")
		private String availMsg;

		@SerializedName("AltType")
		private String altType;

		public int getKeyProdOrg() {
			return keyProdOrg;
		}

		public void setKeyProdOrg(int keyProdOrg) {
			this.keyProdOrg = keyProdOrg;
		}

		public int getPharmaCodeOrg() {
			return pharmaCodeOrg;
		}

		public void setPharmaCodeOrg(int pharmaCodeOrg) {
			this.pharmaCodeOrg = pharmaCodeOrg;
		}

		public long getEanIDOrg() {
			return eanIDOrg;
		}

		public void setEanIDOrg(long eanIDOrg) {
			this.eanIDOrg = eanIDOrg;
		}

		public String getDescriptionOrg() {
			return descriptionOrg;
		}

		public void setDescriptionOrg(String descriptionOrg) {
			this.descriptionOrg = descriptionOrg;
		}

		public int getKeyProd() {
			return keyProd;
		}

		public void setKeyProd(int keyProd) {
			this.keyProd = keyProd;
		}

		public int getPharmaCode() {
			return pharmaCode;
		}

		public void setPharmaCode(int pharmaCode) {
			this.pharmaCode = pharmaCode;
		}

		public long getEanID() {
			return eanID;
		}

		public void setEanID(long eanID) {
			this.eanID = eanID;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public String getAvailState() {
			return availState;
		}

		public void setAvailState(String availState) {
			this.availState = availState;
		}

		public String getAvailMsg() {
			return availMsg;
		}

		public void setAvailMsg(String availMsg) {
			this.availMsg = availMsg;
		}

		public String getAltType() {
			return altType;
		}

		public void setAltType(String altType) {
			this.altType = altType;
		}
	}
}