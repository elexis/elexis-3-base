package ch.elexis.regiomed.order.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RegiomedOrderRequest {

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("b64Password")
    private String b64Password;

    @SerializedName("checkOrder")
    private boolean checkOrder;

    @SerializedName("deliveryType")
    private String deliveryType;

	@SerializedName("errorEmail")
	private String errorEmail;

	@SerializedName("deliveryDate")
	private String deliveryDate;

    @SerializedName("patinfo")
    private String patInfo;

    @SerializedName("sendersUniqueID")
    private String sendersUniqueID;

    @SerializedName("reference")
    private String reference;

    @SerializedName("articles")
    private List<Article> articles = new ArrayList<>();

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getB64Password() {
		return b64Password;
	}

	public void setB64Password(String b64Password) {
		this.b64Password = b64Password;
	}

	public boolean isCheckOrder() {
		return checkOrder;
	}

	public void setCheckOrder(boolean checkOrder) {
		this.checkOrder = checkOrder;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getErrorEmail() {
		return errorEmail;
	}

	public void setErrorEmail(String errorEmail) {
		this.errorEmail = errorEmail;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getPatInfo() {
		return patInfo;
	}

	public void setPatInfo(String patInfo) {
		this.patInfo = patInfo;
	}

	public String getSendersUniqueID() {
		return sendersUniqueID;
	}

	public void setSendersUniqueID(String sendersUniqueID) {
		this.sendersUniqueID = sendersUniqueID;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<Article> getArticles() {
		return articles;
    }

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public void clearPasswordForTokenAuth() {
		this.b64Password = null;
	}

    public static class Article {
        @SerializedName("pharmaCode")
		private int pharmaCode;

        @SerializedName("eanID")
		private long eanID;

        @SerializedName("description")
		private String description;

        @SerializedName("quantity")
		private int quantity;

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
    }

	public static class TokenRequestBody {
		@SerializedName("Email")
		private String email;

		@SerializedName("B64Password")
		private String b64Password;

		public TokenRequestBody(String email, String b64Password) {
			this.email = email;
			this.b64Password = b64Password;
		}

		public String getEmail() {
			return email;
		}

		public String getB64Password() {
			return b64Password;
		}
	}

	public static class TokenResponse {
		@SerializedName("data")
		private TokenData data;

		public TokenData getData() {
			return data;
		}

		public void setData(TokenData data) {
			this.data = data;
		}
	}

	public static class TokenData {
		@SerializedName("Token")
		private String token;

		@SerializedName(value = "TokenRaw", alternate = { "Token Raw", "tokenRaw" })
		private String tokenRaw;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getTokenRaw() {
			return tokenRaw;
		}

		public void setTokenRaw(String tokenRaw) {
			this.tokenRaw = tokenRaw;
		}
	}
}