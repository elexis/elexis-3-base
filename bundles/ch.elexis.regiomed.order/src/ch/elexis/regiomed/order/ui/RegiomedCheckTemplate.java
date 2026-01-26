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

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
	private static final Logger log = LoggerFactory.getLogger(RegiomedCheckTemplate.class);
	private static Configuration cfg;

	static {
		cfg = new Configuration(Configuration.VERSION_2_3_32);
		cfg.setClassForTemplateLoading(RegiomedCheckTemplate.class, "/rsc");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setWrapUncheckedExceptions(true);
	}

	public static String generateHtml(RegiomedOrderResponse response, boolean isSearchAvailable, Set<String> removed,
			Map<String, String> replacements, Map<String, String> replacementNames, Set<String> forcedItems) {

		try {
			RenderingContext context = createContext(response, isSearchAvailable, removed, replacements,
					replacementNames, forcedItems);

			Map<String, Object> root = new HashMap<>();
			root.put("cssContent", loadResourceFile("/rsc/styles.css"));

			List<ArticleResult> allArticles = response.getArticles() != null ? response.getArticles()
					: Collections.emptyList();

			List<ArticleViewModel> nokItems = new ArrayList<>();
			List<ArticleViewModel> okItems = new ArrayList<>();

			int i = 0;
			for (ArticleResult item : allArticles) {
				boolean isError = isCalculatedError(item, context.alternativesMap());
				String rowId = (isError ? "nok_row_" : "ok_row_") + i++;
				ArticleViewModel vm = new ArticleViewModel(item, rowId, isError, context);

				if (isError) {
					nokItems.add(vm);
				} else {
					okItems.add(vm);
				}
			}

			root.put("nokItems", nokItems);
			root.put("okItems", okItems);

			boolean hasActiveErrors = nokItems.stream().anyMatch(vm -> !vm.isHandled());
			root.put("hasActiveErrors", hasActiveErrors);
			root.put("responseMessage", response.getMessage());
			root.put("currentDate", LocalDateTime.now().format(DATE_FORMATTER));
			root.put("logoBase64", context.imgLogo());
			root.put("imgWarning", context.imgWarning());
			root.put("isSearchAvailable", isSearchAvailable);
			root.put("messages", loadMessagesMap());

			Template temp = cfg.getTemplate("regiomed_result_html.ftlh");
			StringWriter out = new StringWriter();
			temp.process(root, out);

			return out.toString();

		} catch (Exception e) {
			log.error("Error generating HTML for Regiomed check result", e);
			return "<html><body><h1>Error generating template</h1><pre>" + e.getMessage() + "</pre></body></html>";
		}
	}

	public static String generateSearchResultRows(List<ProductResult> products) {
		try {
			Map<String, Object> root = new HashMap<>();

			if (products == null || products.isEmpty()) {
				root.put("products", Collections.emptyList());
				root.put("noResultsMsg", Messages.RegiomedCheckTemplate_NoResults);
			} else {
				List<SearchProductViewModel> viewModels = new ArrayList<>();
				boolean anyHasStock = false;

				for (int i = 0; i < products.size(); i++) {
					SearchProductViewModel vm = new SearchProductViewModel(products.get(i), i);
					viewModels.add(vm);
					if (vm.hasStock()) {
						anyHasStock = true;
					}
				}
				root.put("products", viewModels);
				root.put("hasStockColumn", anyHasStock);
				root.put("noResultsMsg", Messages.RegiomedCheckTemplate_NoResults);
			}

			Template temp = cfg.getTemplate("regiomed_search_rows_html.ftlh");
			StringWriter out = new StringWriter();
			temp.process(root, out);
			return out.toString().replace("\r", "").replace("\n", "");

		} catch (Exception e) {
			log.error("Error generating search result rows template", e);
			return "<tr><td colspan='5' style='color:red'>Error: " + e.getMessage().replace("'", "") + "</td></tr>";
		}
	}

	private static String loadResourceFile(String path) {
		try (InputStream in = RegiomedCheckTemplate.class.getResourceAsStream(path)) {
			if (in == null)
				return "/* File not found: " + path + " */";
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.warn("Could not load resource file: {}", path, e);
			return "/* Error loading " + path + ": " + e.getMessage() + " */";
		}
	}

	private static RenderingContext createContext(RegiomedOrderResponse response, boolean isSearchAvailable,
			Set<String> removed, Map<String, String> replacements, Map<String, String> replacementNames,
			Set<String> forcedItems) {

		Map<String, List<AlternativeResult>> altsMap = Collections.emptyMap();
		if (response.getAlternatives() != null && !response.getAlternatives().isEmpty()) {
			altsMap = response.getAlternatives().stream()
					.collect(Collectors.groupingBy(a -> getKey(a.getPharmaCodeOrg(), a.getEanIDOrg())));
		}

		String imgEdit = null;
		try (InputStream in = Images.IMG_EDIT.getImageAsInputStream(ImageSize._16x16_DefaultIconSize)) {
			imgEdit = "data:image/png;base64," + Base64.getEncoder().encodeToString(in.readAllBytes());
		} catch (Exception e) {
			log.debug("Could not load edit icon", e);
		}

		String imgWarning = null;
		try (InputStream in = Images.IMG_AUSRUFEZ.getImageAsInputStream(ImageSize._16x16_DefaultIconSize)) {
			imgWarning = "data:image/png;base64," + Base64.getEncoder().encodeToString(in.readAllBytes());
		} catch (Exception e) {
			log.debug("Could not load warning icon", e);
		}

		return new RenderingContext(isSearchAvailable, removed, replacements, replacementNames, forcedItems, altsMap,
				loadLogoBase64("rsc/logo/regiomed_logo.png"), imgWarning, imgEdit);
	}

	private static boolean isCalculatedError(ArticleResult a, Map<String, List<AlternativeResult>> alternativesMap) {
		if (!a.isSuccess())
			return true;
		if (a.getAvailableInventory() > 0 && a.getQuantity() > a.getAvailableInventory())
			return true;
		String key = getKey(a.getPharmaCode(), a.getEanID());
		boolean hasAlternatives = alternativesMap.containsKey(key) && !alternativesMap.get(key).isEmpty();
		if (hasAlternatives)
			return !a.isSuccessAvailability();
		return false;
	}

	private static String getKey(long pharma, long ean) {
		return pharma + ":" + ean;
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
				return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
			}
		} catch (Exception e) {
			log.debug("Could not load logo: {}", imageUrl, e);
			return null;
		}
	}

	private static Map<String, String> loadMessagesMap() {
		Map<String, String> m = new HashMap<>();
		m.put("successApplied", Messages.RegiomedCheckTemplate_SuccessApplied);
		m.put("successAppliedPrefix", Messages.RegiomedCheckTemplate_SuccessAppliedPrefix);
		m.put("orderTitle", Messages.RegiomedCheckTemplate_OrderTitle);
		m.put("checkResult", Messages.RegiomedCheckTemplate_CheckResult);
		m.put("allChecksSuccess", Messages.RegiomedCheckTemplate_AllChecksSuccess);
		m.put("hintText", Messages.RegiomedCheckTemplate_HintText);
		m.put("problematicItems", Messages.RegiomedCheckTemplate_ProblematicItems);
		m.put("correctedItems", Messages.RegiomedCheckTemplate_CorrectedItems);
		m.put("availableItems", Messages.RegiomedCheckTemplate_AvailableItems);
		m.put("colArticle", Messages.RegiomedCheckTemplate_ColArticle);
		m.put("colAmount", Messages.RegiomedCheckTemplate_ColAmount);
		m.put("colInfo", Messages.RegiomedCheckTemplate_ColInfo);
		m.put("colStatus", Messages.RegiomedCheckTemplate_ColStatus);
		m.put("colAction", Messages.RegiomedCheckTemplate_ColAction);
		m.put("colName", Messages.RegiomedCheckTemplate_ColName);
		m.put("colPrice", Messages.RegiomedCheckTemplate_ColPrice);
		m.put("pharmaLabel", Messages.RegiomedCheckTemplate_PharmaLabel);
		m.put("stockLabel", Messages.RegiomedCheckTemplate_StockLabel);
		m.put("availableAlternatives", Messages.RegiomedCheckTemplate_AvailableAlternatives);
		m.put("noAlternativeAvailable", Messages.RegiomedCheckTemplate_NoAlternativeAvailable);
		m.put("btnReset", Messages.Core_Reset);
		m.put("btnForce", Messages.RegiomedCheckTemplate_ForceOrderBtn);
		m.put("btnSearch", Messages.RegiomedCheckTemplate_SearchAltBtn);
		m.put("btnReplace", Messages.RegiomedCheckTemplate_BtnReplace);
		m.put("btnDelete", Messages.RegiomedCheckTemplate_BtnDelete);
		m.put("badgeOk", Messages.RegiomedCheckTemplate_BadgeOk);
		m.put("badgeReplaced", Messages.RegiomedCheckTemplate_BadgeReplaced);
		m.put("badgeOrder", Messages.RegiomedCheckTemplate_BadgeOrder);
		m.put("badgeError", Messages.RegiomedCheckTemplate_BadgeError);
		m.put("clickToEdit", Messages.RegiomedCheckTemplate_ClickToEdit);
		m.put("changeQtyTitle", Messages.RegiomedCheckTemplate_ChangeQtyTitle);
		m.put("enterNewQty", Messages.RegiomedCheckTemplate_EnterNewQty);
		m.put("cancel", Messages.RegiomedCheckTemplate_Cancel);
		m.put("apply", Messages.RegiomedCheckTemplate_Apply);
		m.put("errorTitle", Messages.RegiomedCheckTemplate_ErrorTitle);
		m.put("understood", Messages.RegiomedCheckTemplate_Understood);
		m.put("searchAltTitle", Messages.RegiomedCheckTemplate_SearchAltTitle);
		m.put("searchPlaceholder", Messages.RegiomedCheckTemplate_SearchPlaceholder);
		m.put("searchBtn", Messages.RegiomedCheckTemplate_SearchBtn);
		m.put("searching", Messages.RegiomedCheckTemplate_Searching);
		m.put("close", Messages.RegiomedCheckTemplate_Close);
		m.put("invalidQtyAlert", Messages.RegiomedCheckTemplate_InvalidQtyAlert);
		return m;
	}
}