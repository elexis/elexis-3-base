package ch.elexis.regiomed.order.model;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.SerializedName;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.ui.exchange.ArticleUtil;
import ch.elexis.regiomed.order.config.RegiomedConfig;

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

	public void setCheckOrder(boolean checkOrder) {
		this.checkOrder = checkOrder;
	}

	public boolean isCheckOrder() {
		return this.checkOrder;
	}

    public static RegiomedOrderRequest fromEntries(RegiomedConfig cfg, List<IOrderEntry> entries) {
        RegiomedOrderRequest req = new RegiomedOrderRequest();

        req.userEmail = cfg.getEmail();

		if (StringUtils.isNotBlank(cfg.getPassword())) {
            req.b64Password = Base64.getEncoder()
                    .encodeToString(cfg.getPassword().getBytes(StandardCharsets.UTF_8));
        } else {
            req.b64Password = null;
        }

        req.checkOrder = cfg.isCheckOrder();
        req.deliveryType = "DEFAULT"; //$NON-NLS-1$

		if (cfg.isErrorEmailEnabled() && StringUtils.isNotBlank(cfg.getErrorEmailAddress())
				&& isValidEmail(cfg.getErrorEmailAddress())) {
			req.errorEmail = cfg.getErrorEmailAddress().trim();
		} else {
			req.errorEmail = null;
		}

		req.deliveryDate = null;
        req.patInfo = null;
		req.sendersUniqueID = null;
		req.reference = null;

        for (IOrderEntry entry : entries) {
            IArticle art = entry.getArticle();
            String pharmaCodeStr = ArticleUtil.getPharmaCode(art);
            String eanStr = ArticleUtil.getEan(art);
			int pharmaCode = 0;
			long eanId = 0L;

			try {
				if (StringUtils.isNotBlank(pharmaCodeStr)) {
					pharmaCode = Integer.parseInt(pharmaCodeStr.trim());
                }
			} catch (NumberFormatException e) {
				// Parsing error -> 0 remains unchanged
            }

			try {
				if (StringUtils.isNotBlank(eanStr)) {
					eanId = Long.parseLong(eanStr.trim());
                }
			} catch (NumberFormatException e) {
				// Parsing error -> 0 remains unchanged
            }

            Article a = new Article();
			a.pharmaCode = pharmaCode;
			a.eanID = eanId;
            a.description = art.getName();
            a.quantity = entry.getAmount();
            req.articles.add(a);
        }

        return req;
    }

	private static boolean isValidEmail(String value) {
		String v = StringUtils.trimToEmpty(value);
		int at = v.indexOf('@');
		int dot = v.lastIndexOf('.');
		return at > 0 && dot > at + 1 && dot < v.length() - 1;
	}

	public void clearPasswordForTokenAuth() {
		this.b64Password = null;
	}

    public static class Article {
        @SerializedName("pharmaCode")
        public int pharmaCode;

        @SerializedName("eanID")
        public long eanID;

        @SerializedName("description")
        public String description;

        @SerializedName("quantity")
        public int quantity;
    }

	public static class TokenRequestBody {
		@SerializedName("Email")
		public String email;
		@SerializedName("B64Password")
		public String b64Password;

		public TokenRequestBody(String email, String b64Password) {
			this.email = email;
			this.b64Password = b64Password;
		}
	}

	public static class TokenResponse {
		@SerializedName("data")
		public TokenData data;
	}

	public static class TokenData {
		@SerializedName("Token")
		public String token;
		@SerializedName(value = "TokenRaw", alternate = { "Token Raw", "tokenRaw" })
		public String tokenRaw;
	}
}