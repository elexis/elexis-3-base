package ch.elexis.regiomed.order.model;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

import ch.elexis.regiomed.order.messages.Messages;

public class RegiomedOrderResponse {

	@SerializedName("client")
	public String client;

	@SerializedName("clientID")
	public long clientID;

	@SerializedName("checkSuccess")
	public boolean checkSuccess;

	@SerializedName("message")
	public String message;

	@SerializedName("deliveryType")
	public String deliveryType;

	@SerializedName("deliveryDate")
	public String deliveryDate;

	@SerializedName("articleCount")
	public int articleCount;

	@SerializedName("articlesOK")
	public int articlesOK;

	@SerializedName("articlesNOK")
	public int articlesNOK;

	@SerializedName("orderSent")
	public boolean orderSent;

	@SerializedName("errorEmailSent")
	public boolean errorEmailSent;

	@SerializedName("user")
	public int user;

	@SerializedName("patInfo")
	public String patInfo;

	@SerializedName("reference")
	public String reference;

	@SerializedName("authMethod")
	public String authMethod;

	@SerializedName("articles")
	public List<ArticleResult> articles;

	public static class ArticleResult {
		@SerializedName(value = "pharmaCode", alternate = { "PharmaCode" })
		public int pharmaCode;

		@SerializedName(value = "eanID", alternate = { "EanID" })
		public long eanID;

		@SerializedName(value = "description", alternate = { "Description" })
		public String description;

		@SerializedName(value = "quantity", alternate = { "Quantity" })
		public int quantity;

		@SerializedName(value = "success", alternate = { "Success" })
		public boolean success;

		@SerializedName(value = "info", alternate = { "Info" })
		public String info;
	}

	public boolean overallSuccess() {
		if (!checkSuccess) {
			return false;
		}
		if (articlesNOK > 0) {
			return false;
		}
		if (articles != null) {
			return articles.stream().allMatch(a -> a.success);
		}
		return true;
	}

	public String buildErrorMessage() {
		StringBuilder sb = new StringBuilder();
		if (message != null && !message.isBlank()) {
			sb.append(message);
		} else {
			sb.append(Messages.RegiomedOrderResponse_GenericErrorMessage);
		}

		if (articles != null) {
			var bad = articles.stream().filter(a -> !a.success).collect(Collectors.toList());
			if (!bad.isEmpty()) {
				sb.append(Messages.RegiomedOrderResponse_ProblematicArticles);
				for (ArticleResult ar : bad) {
					sb.append("[").append(ar.pharmaCode).append(" - ").append(ar.description); //$NON-NLS-1$ //$NON-NLS-2$
					if (ar.info != null && !ar.info.isBlank()) {
						sb.append(" (").append(ar.info).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					sb.append("] "); //$NON-NLS-1$
				}
			}
		}
		return sb.toString();
	}
}