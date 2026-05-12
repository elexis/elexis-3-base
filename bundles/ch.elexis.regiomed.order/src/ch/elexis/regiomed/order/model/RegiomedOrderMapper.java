package ch.elexis.regiomed.order.model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.ui.exchange.ArticleUtil;
import ch.elexis.regiomed.order.config.RegiomedConfig;


public class RegiomedOrderMapper {

	public RegiomedOrderRequest mapToRequest(RegiomedConfig cfg, List<IOrderEntry> entries) {
		RegiomedOrderRequest req = new RegiomedOrderRequest();
		req.setUserEmail(cfg.getEmail());

		if (StringUtils.isNotBlank(cfg.getPassword())) {
			String encodedPwd = Base64.getEncoder().encodeToString(cfg.getPassword().getBytes(StandardCharsets.UTF_8));
			req.setB64Password(encodedPwd);
		} else {
			req.setB64Password(null);
		}
		req.setCheckOrder(cfg.isCheckOrder());
		req.setDeliveryType("DEFAULT"); //$NON-NLS-1$
		if (cfg.isErrorEmailEnabled() && StringUtils.isNotBlank(cfg.getErrorEmailAddress())
				&& isValidEmail(cfg.getErrorEmailAddress())) {
			req.setErrorEmail(cfg.getErrorEmailAddress().trim());
		} else {
			req.setErrorEmail(null);
		}
		req.setDeliveryDate(null);
		req.setPatInfo(null);
		req.setSendersUniqueID(null);
		req.setReference(null);
		for (IOrderEntry entry : entries) {
			req.getArticles().add(mapArticle(entry));
		}
		return req;
	}

	private RegiomedOrderRequest.Article mapArticle(IOrderEntry entry) {
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

		RegiomedOrderRequest.Article a = new RegiomedOrderRequest.Article();
		a.setPharmaCode(pharmaCode);
		a.setEanID(eanId);
		a.setDescription(art.getName());
		a.setQuantity(entry.getAmount());

		return a;
	}

	private boolean isValidEmail(String value) {
		String v = StringUtils.trimToEmpty(value);
		int at = v.indexOf('@');
		int dot = v.lastIndexOf('.');
		return at > 0 && dot > at + 1 && dot < v.length() - 1;
	}
}