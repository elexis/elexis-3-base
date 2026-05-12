package ch.elexis.regiomed.order.service;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.regiomed.order.client.RegiomedOrderClient;
import ch.elexis.regiomed.order.config.RegiomedConfig;
import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedAlternativesResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderRequest;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.AlternativeResult;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.ArticleResult;
import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse;
import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse.ProductResult;

public class RegiomedServerService {

	private static final Logger log = LoggerFactory.getLogger(RegiomedServerService.class);

	public ArticleResult validateReplacement(ProductResult selected) throws Exception {
		RegiomedConfig config = RegiomedConfig.load();
		RegiomedOrderClient client = new RegiomedOrderClient();

		RegiomedOrderRequest request = new RegiomedOrderRequest();
		request.setUserEmail(config.getEmail());
		request.setCheckOrder(true);
		request.setDeliveryType("DEFAULT");

		RegiomedOrderRequest.Article art = new RegiomedOrderRequest.Article();
		art.setPharmaCode(selected.pharmaCode);
		try {
			art.setEanID(StringUtils.isNotBlank(selected.ean) ? Long.parseLong(selected.ean) : 0);
		} catch (Exception e) {
			art.setEanID(0);
		}
		art.setDescription(selected.prodName);
		art.setQuantity(1);

		request.getArticles().add(art);

		RegiomedOrderResponse resp = client.sendOrderWithToken(config, request);

		if (resp != null && resp.getArticles() != null) {
			for (ArticleResult res : resp.getArticles()) {
				if (res.getPharmaCode() == selected.pharmaCode) {
					if (!res.isSuccess()) {
						throw new Exception(Objects.toString(res.getInfo(), Messages.RegiomedCheckDialog_UnknownError));
					}
					return res;
				}
			}
		}
		return null;
	}

	public ArticleResult validateReplacement(AlternativeResult alt) throws Exception {
		ProductResult temp = new ProductResult();
		temp.pharmaCode = alt.getPharmaCode();
		temp.ean = String.valueOf(alt.getEanID());
		temp.prodName = alt.getDescription();
		return validateReplacement(temp);
	}

	public RegiomedProductLookupResponse searchProducts(String query) throws Exception {
		RegiomedConfig config = RegiomedConfig.load();
		RegiomedOrderClient client = new RegiomedOrderClient();
		return client.searchProducts(config, query);
	}

	public RegiomedAlternativesResponse fetchAlternatives(String pharmaCode) {
		try {
			RegiomedOrderClient client = new RegiomedOrderClient();
			return client.getAlternatives(RegiomedConfig.load(), "PCAVAIL", pharmaCode);
		} catch (Exception e) {
			log.error("Error fetching alternatives", e);
			return null;
		}
	}
}