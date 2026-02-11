package ch.elexis.regiomed.order.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IStock;
import ch.elexis.core.ui.icons.ImageSize;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.AlternativeResult;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.ArticleResult;
import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse.ProductResult;
import ch.elexis.regiomed.order.ui.model.ArticleViewModel;
import ch.elexis.regiomed.order.ui.model.RenderingContext;
import ch.elexis.regiomed.order.ui.model.SearchProductViewModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class RegiomedCheckTemplate {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); //$NON-NLS-1$
	private static final Logger log = LoggerFactory.getLogger(RegiomedCheckTemplate.class);
	private static Configuration cfg;

	static {
		cfg = new Configuration(Configuration.VERSION_2_3_32);
		cfg.setClassForTemplateLoading(RegiomedCheckTemplate.class, "/rsc"); //$NON-NLS-1$
		cfg.setDefaultEncoding("UTF-8"); //$NON-NLS-1$
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setWrapUncheckedExceptions(true);
	}

	public static String generateHtml(RegiomedOrderResponse response, boolean isSearchAvailable, Set<String> removed,
			Map<String, String> replacements, Map<String, String> replacementNames,
			Map<String, Integer> replacementInventory, Set<String> forcedItems) {
		try {
			RenderingContext context = createContext(response, isSearchAvailable, removed, replacements,
					replacementNames, replacementInventory, forcedItems);

			Map<String, Object> root = new HashMap<>();
			root.put("cssContent", loadResourceFile("/rsc/styles.css")); //$NON-NLS-1$ //$NON-NLS-2$

			List<ArticleResult> allArticles = response.getArticles() != null ? response.getArticles()
					: Collections.emptyList();

			List<ArticleViewModel> nokItems = new ArrayList<>();
			List<ArticleViewModel> okItems = new ArrayList<>();

			int i = 0;
			for (ArticleResult item : allArticles) {
				boolean isError = isCalculatedError(item, context.alternativesMap(), removed, replacements,
						forcedItems);

				String rowId = (isError ? "nok_row_" : "ok_row_") + i++; //$NON-NLS-1$ //$NON-NLS-2$
				ArticleViewModel vm = new ArticleViewModel(item, rowId, isError, context);

				if (isError) {
					nokItems.add(vm);
				} else {
					okItems.add(vm);
				}
			}

			root.put("nokItems", nokItems); //$NON-NLS-1$
			root.put("okItems", okItems); //$NON-NLS-1$

			boolean hasActiveErrors = nokItems.stream().anyMatch(vm -> !vm.isHandled());
			root.put("hasActiveErrors", hasActiveErrors); //$NON-NLS-1$
			root.put("responseMessage", response.getMessage()); //$NON-NLS-1$
			root.put("currentDate", LocalDateTime.now().format(DATE_FORMATTER)); //$NON-NLS-1$
			root.put("logoBase64", context.imgLogo()); //$NON-NLS-1$
			root.put("imgWarning", context.imgWarning()); //$NON-NLS-1$
			root.put("imgEdit", context.imgEdit()); //$NON-NLS-1$
			root.put("isSearchAvailable", isSearchAvailable); //$NON-NLS-1$
			root.put("messages", loadMessagesMap()); //$NON-NLS-1$

			Template temp = cfg.getTemplate("regiomed_result_html.ftlh"); //$NON-NLS-1$
			StringWriter out = new StringWriter();
			temp.process(root, out);

			return out.toString();

		} catch (Exception e) {
			log.error("Error generating HTML for Regiomed check result", e); //$NON-NLS-1$
			return "<html><body><h1>Error generating template</h1><pre>" + e.getMessage() + "</pre></body></html>"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public static String generateSearchResultRows(List<ProductResult> products,
			Map<Integer, Map<String, Integer>> localStockMap, List<IStock> allStocks, String lastFilter) {
		try {
			Map<String, Object> root = new HashMap<>();

			root.put("lastFilter", lastFilter);

			List<String> availableElexisStocks = new ArrayList<>();
			if (allStocks != null) {
				availableElexisStocks = allStocks.stream().map(IStock::getCode).collect(Collectors.toList());
			}
			root.put("availableElexisStocks", availableElexisStocks);

			if (products == null || products.isEmpty()) {
				root.put("products", Collections.emptyList()); //$NON-NLS-1$
				root.put("noResultsMsg", Messages.RegiomedCheckTemplate_NoResults); //$NON-NLS-1$
			} else {
				List<SearchProductViewModel> viewModels = new ArrayList<>();
				boolean anyHasStock = false;

				for (int i = 0; i < products.size(); i++) {
					SearchProductViewModel vm = new SearchProductViewModel(products.get(i), i);

					if (localStockMap != null && localStockMap.containsKey(i)) {
						Map<String, Integer> stocks = localStockMap.get(i);
						stocks.forEach(vm::addLocalStock);
					}

					viewModels.add(vm);
					if (vm.hasStock() || vm.getTotalLocalStock() > 0) {
						anyHasStock = true;
					}
				}
				root.put("products", viewModels); //$NON-NLS-1$
				root.put("hasStockColumn", anyHasStock); //$NON-NLS-1$
				root.put("noResultsMsg", Messages.RegiomedCheckTemplate_NoResults); //$NON-NLS-1$
			}

			Template temp = cfg.getTemplate("regiomed_search_rows_html.ftlh"); //$NON-NLS-1$
			StringWriter out = new StringWriter();
			temp.process(root, out);
			return out.toString().replace("\r", StringUtils.EMPTY).replace("\n", StringUtils.EMPTY); //$NON-NLS-1$ //$NON-NLS-3$

		} catch (Exception e) {
			log.error("Error generating search result rows template", e); //$NON-NLS-1$
			return "<tr><td colspan='5' style='color:red'>Error: " + e.getMessage().replace("'", StringUtils.EMPTY) //$NON-NLS-1$ //$NON-NLS-2$
					+ "</td></tr>"; // $NON-NLS-4$
		}
	}

	public static String generateSearchResultRows(List<ProductResult> products) {
		return generateSearchResultRows(products, null, null, "ALL");
	}

	private static String loadResourceFile(String path) {
		try (InputStream in = RegiomedCheckTemplate.class.getResourceAsStream(path)) {
			if (in == null)
				return "/* File not found: " + path + " */"; //$NON-NLS-1$ //$NON-NLS-2$
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.warn("Could not load resource file: {}", path, e); //$NON-NLS-1$
			return "/* Error loading " + path + ": " + e.getMessage() + " */"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private static RenderingContext createContext(RegiomedOrderResponse response, boolean isSearchAvailable,
			Set<String> removed, Map<String, String> replacements, Map<String, String> replacementNames,
			Map<String, Integer> replacementInventory, Set<String> forcedItems) {

		Map<String, List<AlternativeResult>> altsMap = Collections.emptyMap();
		if (response.getAlternatives() != null && !response.getAlternatives().isEmpty()) {
			altsMap = response.getAlternatives().stream()
					.collect(Collectors.groupingBy(a -> getKey(a.getPharmaCodeOrg(), a.getEanIDOrg())));
		}

		String imgEdit = null;
		try (InputStream in = Images.IMG_EDIT.getImageAsInputStream(ImageSize._16x16_DefaultIconSize)) {
			imgEdit = "data:image/png;base64," + Base64.getEncoder().encodeToString(in.readAllBytes()); //$NON-NLS-1$
		} catch (Exception e) {
			log.debug("Could not load edit icon", e); //$NON-NLS-1$
		}

		String imgWarning = null;
		try (InputStream in = Images.IMG_AUSRUFEZ.getImageAsInputStream(ImageSize._16x16_DefaultIconSize)) {
			imgWarning = "data:image/png;base64," + Base64.getEncoder().encodeToString(in.readAllBytes()); //$NON-NLS-1$
		} catch (Exception e) {
			log.debug("Could not load warning icon", e); //$NON-NLS-1$
		}

		return new RenderingContext(isSearchAvailable, removed, replacements, replacementNames, replacementInventory,
				forcedItems, altsMap, loadLogoBase64("rsc/logo/regiomed_logo.png"), imgWarning, imgEdit); //$NON-NLS-1$
	}

	private static boolean isCalculatedError(ArticleResult a, Map<String, List<AlternativeResult>> alternativesMap,
			Set<String> removed, Map<String, String> replacements, Set<String> forcedItems) {
		String key = getKey(a.getPharmaCode(), a.getEanID());
		if (removed.contains(key) || forcedItems.contains(key)) {
			return false;
		}
		if (replacements.containsKey(key)) {
			return !a.isSuccess();
		}
		if (!a.isSuccess())
			return true;
		if (a.getAvailableInventory() > 0 && a.getQuantity() > a.getAvailableInventory())
			return true;
		boolean hasAlternatives = alternativesMap.containsKey(key) && !alternativesMap.get(key).isEmpty();
		if (hasAlternatives)
			return !a.isSuccessAvailability();

		return false;
	}

	private static String getKey(long pharma, long ean) {
		return pharma + ":" + ean; //$NON-NLS-1$
	}

	private static String loadLogoBase64(String imageUrl) {
		try {
			Bundle bundle = FrameworkUtil.getBundle(RegiomedCheckTemplate.class);
			if (bundle == null)
				return null;
			URL url = bundle.getEntry(imageUrl);
			if (url == null)
				return null;
			try (InputStream in = url.openStream()) {
				byte[] imageBytes = in.readAllBytes();
				return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes); //$NON-NLS-1$
			}
		} catch (Exception e) {
			log.debug("Could not load logo: {}", imageUrl, e); //$NON-NLS-1$
			return null;
		}
	}

	private static Map<String, String> loadMessagesMap() {
		Map<String, String> m = new HashMap<>();
		m.put("successApplied", Messages.RegiomedCheckTemplate_SuccessApplied); //$NON-NLS-1$
		m.put("successAppliedPrefix", Messages.RegiomedCheckTemplate_SuccessAppliedPrefix); //$NON-NLS-1$
		m.put("orderTitle", Messages.RegiomedCheckTemplate_OrderTitle); //$NON-NLS-1$
		m.put("checkResult", Messages.RegiomedCheckTemplate_CheckResult); //$NON-NLS-1$
		m.put("allChecksSuccess", Messages.RegiomedCheckTemplate_AllChecksSuccess); //$NON-NLS-1$
		m.put("hintText", Messages.RegiomedCheckTemplate_HintText); //$NON-NLS-1$
		m.put("problematicItems", Messages.RegiomedCheckTemplate_ProblematicItems); //$NON-NLS-1$
		m.put("correctedItems", Messages.RegiomedCheckTemplate_CorrectedItems); //$NON-NLS-1$
		m.put("availableItems", Messages.RegiomedCheckTemplate_AvailableItems); //$NON-NLS-1$
		m.put("colArticle", Messages.RegiomedCheckTemplate_ColArticle); //$NON-NLS-1$
		m.put("colAmount", Messages.RegiomedCheckTemplate_ColAmount); //$NON-NLS-1$
		m.put("colInfo", Messages.RegiomedCheckTemplate_ColInfo); //$NON-NLS-1$
		m.put("colStatus", Messages.RegiomedCheckTemplate_ColStatus); //$NON-NLS-1$
		m.put("colLager", Messages.RegiomedCheckTemplate_ColLager); //$NON-NLS-1$
		m.put("colAction", Messages.RegiomedCheckTemplate_ColAction); //$NON-NLS-1$
		m.put("colName", Messages.RegiomedCheckTemplate_ColName); //$NON-NLS-1$
		m.put("colPrice", Messages.RegiomedCheckTemplate_ColPrice); //$NON-NLS-1$
		m.put("pharmaLabel", Messages.RegiomedCheckTemplate_PharmaLabel); //$NON-NLS-1$
		m.put("stockLabel", Messages.RegiomedCheckTemplate_StockLabel); //$NON-NLS-1$
		m.put("availableAlternatives", Messages.RegiomedCheckTemplate_AvailableAlternatives); //$NON-NLS-1$
		m.put("noAlternativeAvailable", Messages.RegiomedCheckTemplate_NoAlternativeAvailable); //$NON-NLS-1$
		m.put("btnReset", Messages.Core_Reset); //$NON-NLS-1$
		m.put("btnForce", Messages.RegiomedCheckTemplate_ForceOrderBtn); //$NON-NLS-1$
		m.put("btnSearch", Messages.RegiomedCheckTemplate_SearchAltBtn); //$NON-NLS-1$
		m.put("btnReplace", Messages.RegiomedCheckTemplate_BtnReplace); //$NON-NLS-1$
		m.put("btnDelete", Messages.RegiomedCheckTemplate_BtnDelete); //$NON-NLS-1$
		m.put("badgeOk", Messages.RegiomedCheckTemplate_BadgeOk); //$NON-NLS-1$
		m.put("badgeReplaced", Messages.RegiomedCheckTemplate_BadgeReplaced); //$NON-NLS-1$
		m.put("badgeOrder", Messages.RegiomedCheckTemplate_BadgeOrder); //$NON-NLS-1$
		m.put("badgeError", Messages.RegiomedCheckTemplate_BadgeError); //$NON-NLS-1$
		m.put("clickToEdit", Messages.RegiomedCheckTemplate_ClickToEdit); //$NON-NLS-1$
		m.put("changeQtyTitle", Messages.RegiomedCheckTemplate_ChangeQtyTitle); //$NON-NLS-1$
		m.put("enterNewQty", Messages.RegiomedCheckTemplate_EnterNewQty); //$NON-NLS-1$
		m.put("cancel", Messages.RegiomedCheckTemplate_Cancel); //$NON-NLS-1$
		m.put("apply", Messages.RegiomedCheckTemplate_Apply); //$NON-NLS-1$
		m.put("errorTitle", Messages.RegiomedCheckTemplate_ErrorTitle); //$NON-NLS-1$
		m.put("understood", Messages.RegiomedCheckTemplate_Understood); //$NON-NLS-1$
		m.put("searchAltTitle", Messages.RegiomedCheckTemplate_SearchAltTitle); //$NON-NLS-1$
		m.put("searchPlaceholder", Messages.RegiomedCheckTemplate_SearchPlaceholder); //$NON-NLS-1$
		m.put("searchBtn", Messages.RegiomedCheckTemplate_SearchBtn); //$NON-NLS-1$
		m.put("searching", Messages.RegiomedCheckTemplate_Searching); //$NON-NLS-1$
		m.put("close", Messages.RegiomedCheckTemplate_Close); //$NON-NLS-1$
		m.put("invalidQtyAlert", Messages.RegiomedCheckTemplate_InvalidQtyAlert); //$NON-NLS-1$
		return m;
	}
}