package ch.elexis.regiomed.order.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedAlternativesResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.AlternativeResult;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.ArticleResult;
import ch.elexis.regiomed.order.service.RegiomedServerService;

public class RegiomedCheckController {

	private final RegiomedOrderResponse response;
	private final RegiomedServerService serverService;

	private final Set<String> removedIdentifiers = new HashSet<>();
	private final Map<String, String> replacements = new HashMap<>();
	private final Map<String, String> replacementNames = new HashMap<>();
	private final Map<String, Integer> replacementInventory = new HashMap<>();
	private final Set<String> forcedItems = new HashSet<>();
	private final Set<String> articlesWithAlternatives = new HashSet<>();

	public RegiomedCheckController(RegiomedOrderResponse response, RegiomedServerService serverService) {
		this.response = response;
		this.serverService = serverService;

		if (response.getAlternatives() != null) {
			response.getAlternatives()
					.forEach(alt -> articlesWithAlternatives.add(makeKey(alt.getPharmaCodeOrg(), alt.getEanIDOrg())));
		}
	}

	public void replaceArticle(String orgKey, String newKey, String newName, int newStock) {
		replacements.put(orgKey, newKey);
		replacementNames.put(orgKey, newName);
		replacementInventory.put(orgKey, newStock);
		forcedItems.remove(orgKey);
		updateArticleStatus(orgKey);
	}

	public void updateQuantity(String pharma, String ean, int newQty) {
		if (response.getArticles() == null)
			return;
		String key = makeKey(pharma, ean);

		for (ArticleResult art : response.getArticles()) {
			if (makeKey(art.getPharmaCode(), art.getEanID()).equals(key)) {
				art.setQuantity(newQty);
				updateArticleStatus(key);
				break;
			}
		}
	}

	public void removeArticle(String key) {
		if (!removedIdentifiers.contains(key)) {
			removedIdentifiers.add(key);
			replacements.remove(key);
			replacementInventory.remove(key);
			forcedItems.remove(key);
		}
	}

	public void resetArticle(String key) {
		removedIdentifiers.remove(key);
		replacements.remove(key);
		replacementNames.remove(key);
		replacementInventory.remove(key);
		forcedItems.remove(key);
	}

	public void forceArticle(String key) {
		forcedItems.add(key);
	}

	public void loadMissingAlternatives() {
		if (response.getArticles() == null)
			return;
		List<ArticleResult> toLoad = response.getArticles().stream().filter(this::isCalculatedError)
				.filter(a -> !articlesWithAlternatives.contains(makeKey(a.getPharmaCode(), a.getEanID())))
				.collect(Collectors.toList());
		for (ArticleResult a : toLoad) {
			fetchMissingAlternatives(a);
		}
	}

	private void updateArticleStatus(String key) {
		for (ArticleResult art : response.getArticles()) {
			if (makeKey(art.getPharmaCode(), art.getEanID()).equals(key)) {

				boolean isReplaced = replacements.containsKey(key);
				int qty = art.getQuantity();
				int stock = isReplaced ? replacementInventory.getOrDefault(key, Integer.MAX_VALUE)
						: art.getAvailableInventory();

				boolean isStockOK = (stock <= 0) || (qty <= stock);

				if (isStockOK) {
					art.setSuccessAvailability(true);
					art.setAvailState(Messages.RegiomedCheckDialog_Yes);
					String successMsg = Messages.RegiomedCheckDialog_AvailableQtyAdjusted;
					art.setAvailMsg(successMsg);

					if (isReplaced) {
						art.setSuccess(true);
						art.setInfo(successMsg);
					}
				} else {
					art.setSuccessAvailability(false);
					if (isReplaced) {
						art.setSuccess(false);
					}
					art.setAvailState(Messages.RegiomedCheckDialog_No);

					String displayStock = (stock == Integer.MAX_VALUE) ? "?" : String.valueOf(stock);
					String errorMsg = MessageFormat.format(Messages.RegiomedCheckDialog_QtyExceedsStock, qty,
							displayStock);

					art.setAvailMsg(errorMsg);
					art.setInfo(errorMsg);

					if (!articlesWithAlternatives.contains(key)) {
						fetchMissingAlternatives(art);
					}
				}
				break;
			}
		}
	}

	private void fetchMissingAlternatives(ArticleResult art) {
		RegiomedAlternativesResponse altResp = serverService.fetchAlternatives(String.valueOf(art.getPharmaCode()));

		if (altResp != null && altResp.getAlternatives() != null && !altResp.getAlternatives().isEmpty()) {
			if (response.getAlternatives() == null) {
				response.setAlternatives(new ArrayList<>());
			}

			List<AlternativeResult> converted = altResp.getAlternatives().stream().map(item -> {
				AlternativeResult res = new AlternativeResult();
				res.setPharmaCodeOrg(art.getPharmaCode());
				res.setEanIDOrg(art.getEanID());
				res.setDescriptionOrg(art.getDescription());
				res.setPharmaCode(item.getPharmaCode());
				res.setEanID(item.getEan());
				res.setDescription(item.getProdName());
				res.setPrice(item.getPrice());
				res.setAvailState(item.getAvailState());
				res.setAvailMsg(item.getAvailMessage());
				res.setAltType(item.getAltType());
				return res;
			}).collect(Collectors.toList());

			response.getAlternatives().addAll(converted);
			articlesWithAlternatives.add(makeKey(art.getPharmaCode(), art.getEanID()));
		}
	}

	public boolean isCalculatedError(ArticleResult a) {
		String key = makeKey(a.getPharmaCode(), a.getEanID());

		if (removedIdentifiers.contains(key))
			return false;
		if (forcedItems.contains(key))
			return false;

		if (replacements.containsKey(key)) {
			return !a.isSuccess();
		}

		if (!a.isSuccess())
			return true;
		if (a.getAvailableInventory() > 0 && a.getQuantity() > a.getAvailableInventory())
			return true;

		if (articlesWithAlternatives.contains(key)) {
			return !a.isSuccessAvailability();
		}
		return false;
	}

	public int getRemainingErrors() {
		if (response.getArticles() == null)
			return 0;
		return (int) response.getArticles().stream().filter(this::isCalculatedError).count();
	}

	public String makeKey(Object pharma, Object ean) {
		return StringUtils.defaultIfBlank(String.valueOf(pharma), "0") + ":"
				+ StringUtils.defaultIfBlank(String.valueOf(ean), "0");
	}

	public Map<String, String> getReplacements() {
		return replacements;
	}

	public Map<String, String> getReplacementNames() {
		return replacementNames;
	}

	public Map<String, Integer> getReplacementInventory() {
		return replacementInventory;
	}

	public Set<String> getRemovedIdentifiers() {
		return removedIdentifiers;
	}

	public Set<String> getForcedItems() {
		return forcedItems;
	}

	public RegiomedOrderResponse getResponse() {
		return response;
	}
}