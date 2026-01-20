package ch.elexis.regiomed.order.ui;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.AlternativeResult;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.ArticleResult;
import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse.ProductResult;

public class RegiomedCheckTemplate {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); //$NON-NLS-1$

	public static String generateHtml(RegiomedOrderResponse response, boolean isSearchAvailable, Set<String> removed,
			Map<String, String> replacements, Set<String> forcedItems) {

		RenderingContext context = createContext(response, isSearchAvailable, removed, replacements, forcedItems);

		List<ArticleResult> allArticles = response.getArticles() != null ? response.getArticles()
				: Collections.emptyList();
		List<ArticleResult> nokItems = allArticles.stream().filter(a -> isCalculatedError(a, context.alternativesMap()))
				.collect(Collectors.toList());
		List<ArticleResult> okItems = allArticles.stream().filter(a -> !isCalculatedError(a, context.alternativesMap()))
				.collect(Collectors.toList());

		boolean hasActiveErrors = nokItems.stream().anyMatch(a -> {
			String key = getKey(a);
			return !isHandled(key, context);
		});

		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>"); //$NON-NLS-1$
		html.append("<style>").append(CSS_STYLES).append("</style>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append(getJsScript(context.imgWarning()));
		html.append("</head><body>"); //$NON-NLS-1$

		html.append("<div id='toast' class='toast'>").append(Messages.RegiomedCheckTemplate_SuccessApplied) //$NON-NLS-1$
				.append("</div>"); //$NON-NLS-1$

		html.append("<div class='container'>"); //$NON-NLS-1$

		renderHeader(html, context);
		renderStatusBox(html, response.getMessage(), hasActiveErrors);

		if (!nokItems.isEmpty()) {
			renderSection(html, Messages.RegiomedCheckTemplate_HintText,
					Messages.RegiomedCheckTemplate_ProblematicItems, "warning-section"); //$NON-NLS-1$
			renderTable(html, nokItems, true, context);
		}

		if (!okItems.isEmpty()) {
			renderSection(html, null, Messages.RegiomedCheckTemplate_AvailableItems, StringUtils.EMPTY);
			renderTable(html, okItems, false, context);
		}

		html.append("</div></body></html>"); //$NON-NLS-1$
		return html.toString();
	}

	public static String generateSearchResultRows(List<ProductResult> products) {
		if (products == null || products.isEmpty()) {
			return "<tr><td colspan='4' style='text-align:center; padding:20px; color:#888;'>" //$NON-NLS-1$
					+ Messages.RegiomedCheckTemplate_NoResults + "</td></tr>"; //$NON-NLS-1$
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < products.size(); i++) {
			ProductResult p = products.get(i);

			sb.append("<tr id='res_row_").append(i).append("' ").append("onclick='selectSearchResult(").append(i) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					.append(")' ").append("ondblclick='applySearchResult(").append(i).append(")'>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			sb.append("<td>").append(escapeHtml(p.prodName)).append("</td>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<td>").append(escapeHtml(p.ean)).append("</td>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<td class='status-cell'>").append(escapeHtml(p.message)).append("</td>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<td class='price-cell'>").append(String.format("%.2f", p.price)).append("</td>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			sb.append("</tr>"); //$NON-NLS-1$
		}
		return sb.toString();
	}

	private static void renderHeader(StringBuilder html, RenderingContext ctx) {
		html.append("<div class='header'><div class='header-left'>"); //$NON-NLS-1$
		if (ctx.imgLogo() != null) {
			html.append("<img src='").append(ctx.imgLogo()).append("' class='logo-img' alt='Regiomed Logo'>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		html.append("<div class='logo-text'>").append(Messages.RegiomedCheckTemplate_OrderTitle).append("</div></div>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("<div>").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</div></div>"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static void renderStatusBox(StringBuilder html, String message, boolean hasErrors) {
		String statusClass = hasErrors ? "status-warn" : "status-ok"; //$NON-NLS-1$ //$NON-NLS-2$
		html.append("<div class='status-box ").append(statusClass).append("'>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("<h3>").append(Messages.RegiomedCheckTemplate_CheckResult).append("</h3>"); //$NON-NLS-1$ //$NON-NLS-2$

		if (hasErrors) {
			html.append("<p>").append(escapeHtml(message)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			html.append("<p>").append(Messages.RegiomedCheckTemplate_AllChecksSuccess).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		html.append("</div>"); //$NON-NLS-1$
	}

	private static void renderSection(StringBuilder html, String hintText, String title, String titleClass) {
		if (StringUtils.isNotBlank(hintText)) {
			html.append("<div class='hint-box'>").append(hintText).append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		html.append("<div class='section-title ").append(titleClass).append("'>").append(title).append("</div>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private static void renderTable(StringBuilder html, List<ArticleResult> items, boolean isErrorTable,
			RenderingContext ctx) {
		html.append("<table><thead><tr><th>").append(Messages.RegiomedCheckTemplate_ColArticle).append("</th>") //$NON-NLS-1$ //$NON-NLS-2$
				.append("<th>").append(Messages.RegiomedCheckTemplate_ColAmount).append("</th>").append("<th>") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				.append(Messages.RegiomedCheckTemplate_ColInfo).append("</th>").append("<th>") //$NON-NLS-1$ //$NON-NLS-2$
				.append(Messages.RegiomedCheckTemplate_ColStatus).append("</th>").append("<th>") //$NON-NLS-1$ //$NON-NLS-2$
				.append(Messages.RegiomedCheckTemplate_ColAction).append("</th></tr></thead><tbody>"); //$NON-NLS-1$

		int i = 0;
		for (ArticleResult item : items) {
			String rowId = (isErrorTable ? "nok_row_" : "ok_row_") + i++; //$NON-NLS-1$ //$NON-NLS-2$
			renderTableRow(html, rowId, item, isErrorTable, ctx);
		}
		html.append("</tbody></table>"); //$NON-NLS-1$
	}

	private static void renderTableRow(StringBuilder sb, String rowId, ArticleResult item, boolean isErrorTable,
			RenderingContext ctx) {
		String key = getKey(item);
		List<AlternativeResult> alts = ctx.alternativesMap().getOrDefault(key, Collections.emptyList());

		boolean isReplaced = ctx.replacements() != null && ctx.replacements().containsKey(key);
		boolean isRemoved = ctx.removed() != null && ctx.removed().contains(key);
		boolean isForced = ctx.forcedItems() != null && ctx.forcedItems().contains(key);
		boolean isStockError = item.getAvailableInventory() > 0 && item.getQuantity() > item.getAvailableInventory();
		boolean hasAlternatives = isErrorTable && !alts.isEmpty();

		String rowStyle = StringUtils.EMPTY;
		if (isReplaced)
			rowStyle = "style='background-color:#e6f7ff;'"; //$NON-NLS-1$
		else if (isRemoved)
			rowStyle = "style='opacity:0.3; text-decoration:line-through;'"; //$NON-NLS-1$
		else if (isForced)
			rowStyle = "style='background-color:#fff3cd;'"; //$NON-NLS-1$

		sb.append("<tr id='").append(rowId).append("' ").append(rowStyle).append(">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		sb.append("<td>").append(escapeHtml(item.getDescription())).append("<br><small style='color:#888'>") //$NON-NLS-1$ //$NON-NLS-2$
				.append(Messages.RegiomedCheckTemplate_PharmaLabel).append(StringUtils.SPACE)
				.append(item.getPharmaCode()).append("<br>").append(Messages.RegiomedCheckTemplate_StockLabel) //$NON-NLS-1$
				.append(StringUtils.SPACE)
				.append(item.getAvailableInventory()).append("<br>") //$NON-NLS-1$
				.append(Messages.Core_EAN).append(": ").append(item.getEanID()).append("</small></td>"); //$NON-NLS-1$ //$NON-NLS-2$

		sb.append("<td class='qty-editable' title='" + Messages.RegiomedCheckTemplate_ClickToEdit //$NON-NLS-1$
				+ "' onclick=\"changeQuantity('") //$NON-NLS-1$
				.append(item.getPharmaCode()).append("', '").append(item.getEanID()).append("', '") //$NON-NLS-1$ //$NON-NLS-2$
				.append(item.getQuantity()).append("')\">").append(item.getQuantity()); //$NON-NLS-1$

		if (ctx.imgEdit() != null) {
			sb.append(" <img src='").append(ctx.imgEdit()) //$NON-NLS-1$
					.append("' style='height:16px; width:16px; vertical-align:text-bottom; opacity:0.6;' alt='Edit'>"); //$NON-NLS-1$
		} else {
			sb.append(" <span style='font-size:10px; color:#999;'>✎</span>"); //$NON-NLS-1$
		}
		sb.append("</td>"); //$NON-NLS-1$

		String colorStyle = isErrorTable && !isReplaced && !isRemoved && !isForced ? "style='color:#dc3545'" //$NON-NLS-1$
				: StringUtils.EMPTY;
		sb.append("<td ").append(colorStyle).append(">"); //$NON-NLS-1$ //$NON-NLS-2$

		renderInfoColumnContent(sb, item, isStockError, rowId, alts, hasAlternatives, isErrorTable,
				isReplaced || isRemoved);

		sb.append("</td>"); //$NON-NLS-1$

		sb.append("<td>"); //$NON-NLS-1$
		if (isErrorTable) {
			if (isReplaced)
				sb.append(
						createBadge("badge-replaced", "status_" + rowId, Messages.RegiomedCheckTemplate_BadgeReplaced)); //$NON-NLS-1$ //$NON-NLS-2$
			else if (isForced)
				sb.append(createBadge(null, null, Messages.RegiomedCheckTemplate_BadgeOrder,
						"background:#ffc107; color:#856404")); //$NON-NLS-1$
			else
				sb.append(createBadge("badge-error", "status_" + rowId, Messages.RegiomedCheckTemplate_BadgeError)); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			sb.append(createBadge("badge-ok", null, Messages.RegiomedCheckTemplate_BadgeOk)); //$NON-NLS-1$
		}
		sb.append("</td>"); //$NON-NLS-1$

		sb.append("<td>"); //$NON-NLS-1$
		renderActionButtons(sb, rowId, item, ctx, isStockError, isErrorTable, isForced, isReplaced, isRemoved,
				hasAlternatives);
		sb.append("</td></tr>"); //$NON-NLS-1$
	}

	private static void renderInfoColumnContent(StringBuilder sb, ArticleResult item, boolean isStockError,
			String rowId, List<AlternativeResult> alts, boolean hasAlternatives, boolean isErrorTable,
			boolean isDisabled) {
		String originalMsg = StringUtils
				.defaultString(StringUtils.isBlank(item.getInfo()) ? item.getAvailMsg() : item.getInfo());

		if (isStockError) {
			String errorMsg = MessageFormat.format(Messages.RegiomedCheckTemplate_QtyExceedsStock, item.getQuantity(),
					item.getAvailableInventory());
			sb.append(errorMsg);

			if (StringUtils.isNotBlank(originalMsg) && !originalMsg.contains("übersteigt Bestand")) { //$NON-NLS-1$
				sb.append("<br><small style='color:#666'>").append(Messages.RegiomedCheckTemplate_NoteLabel) //$NON-NLS-1$
						.append(StringUtils.SPACE)
						.append(escapeHtml(originalMsg)).append("</small>"); //$NON-NLS-1$
			}
		} else {
			sb.append(escapeHtml(originalMsg));
		}

		if (StringUtils.isNotBlank(item.getAvailMsgOrg()) && !item.getAvailMsgOrg().equals(item.getAvailMsg())
				&& !item.getAvailMsgOrg().equals(originalMsg)) {
			sb.append("<br><small style='color:#666'>").append(Messages.RegiomedCheckTemplate_InfoLabel) //$NON-NLS-1$
					.append(StringUtils.SPACE)
					.append(escapeHtml(item.getAvailMsgOrg()))
					.append("</small>"); //$NON-NLS-1$
		}

		if (hasAlternatives) {
			sb.append("<div class='alt-container'><div style='font-weight:bold; margin-top:5px;'>") //$NON-NLS-1$
					.append(Messages.RegiomedCheckTemplate_AvailableAlternatives).append("</div>"); //$NON-NLS-1$

			sb.append("<select id='sel_").append(rowId).append("' class='alt-select' ") //$NON-NLS-1$ //$NON-NLS-2$
					.append(isDisabled ? "disabled" : StringUtils.EMPTY).append(">"); //$NON-NLS-1$ //$NON-NLS-3$
			for (AlternativeResult alt : alts) {
				String label = alt.getDescription() + (alt.getPrice() != null && alt.getPrice() > 0
						? " (CHF " + String.format("%.2f", alt.getPrice()) + ")" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						: StringUtils.EMPTY);
				sb.append("<option value='").append(alt.getPharmaCode()).append(":").append(alt.getEanID()).append("'>") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						.append(escapeHtml(label)).append("</option>"); //$NON-NLS-1$
			}
			sb.append("</select></div>"); //$NON-NLS-1$
		} else if (isErrorTable && !isStockError) {
			sb.append("<div style='margin-top:5px; font-style:italic; font-size:12px; color:#dc3545;'>") //$NON-NLS-1$
					.append(Messages.RegiomedCheckTemplate_NoAlternativeAvailable).append("</div>"); //$NON-NLS-1$
		}
	}

	private static void renderActionButtons(StringBuilder sb, String rowId, ArticleResult item, RenderingContext ctx,
			boolean isStockError, boolean isError, boolean isForced, boolean isReplaced, boolean isRemoved,
			boolean hasAlternatives) {

		boolean showForceBtn = isError && isStockError && !isForced && !isReplaced && !isRemoved;
		String btnDisabled = (isReplaced || isRemoved) ? "disabled" : StringUtils.EMPTY; //$NON-NLS-1$

		int buttonCount = 1;
		if (showForceBtn)
			buttonCount++;
		if (ctx.isSearchAvailable)
			buttonCount++;
		if (hasAlternatives)
			buttonCount++;

		sb.append("<div class='action-btn-container ").append(buttonCount > 2 ? "layout-column" : "layout-row") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				.append("'>"); //$NON-NLS-1$

		if (showForceBtn) {
			sb.append("<button class='btn-base' style='border:1px solid #ffc107; color:#856404' onclick=\"forceOrder('") //$NON-NLS-1$
					.append(item.getPharmaCode()).append("', '").append(item.getEanID()) //$NON-NLS-1$
					.append("')\">").append(Messages.RegiomedCheckTemplate_ForceOrderBtn).append("</button>"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (ctx.isSearchAvailable) {
			String safeDesc = escapeHtml(item.getDescription()).replace("'", "\\'"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<button class='btn-base btn-search' onclick=\"openSearchModal('").append(rowId).append("', '") //$NON-NLS-1$ //$NON-NLS-2$
					.append(item.getPharmaCode()).append("', '").append(item.getEanID()).append("', '").append(safeDesc) //$NON-NLS-1$ //$NON-NLS-2$
					.append("')\" ").append(btnDisabled).append(">").append(Messages.RegiomedCheckTemplate_SearchAltBtn) //$NON-NLS-1$ //$NON-NLS-2$
					.append("</button>"); //$NON-NLS-1$
		}

		if (hasAlternatives) {
			sb.append("<button class='btn-base btn-replace' onclick=\"replaceArticle('").append(rowId).append("', '") //$NON-NLS-1$ //$NON-NLS-2$
					.append(item.getPharmaCode()).append("', '").append(item.getEanID()).append("')\" ") //$NON-NLS-1$ //$NON-NLS-2$
					.append(btnDisabled).append(">").append(Messages.RegiomedCheckTemplate_BtnReplace) //$NON-NLS-1$
					.append("</button>"); //$NON-NLS-1$
		}

		sb.append("<button class='btn-base btn-delete' onclick=\"removeArticle('").append(rowId).append("', '") //$NON-NLS-1$ //$NON-NLS-2$
				.append(item.getPharmaCode()).append("', '").append(item.getEanID()).append("')\" ").append(btnDisabled) //$NON-NLS-1$ //$NON-NLS-2$
				.append(">").append(Messages.RegiomedCheckTemplate_BtnDelete).append("</button>"); //$NON-NLS-1$ //$NON-NLS-2$

		sb.append("</div>"); //$NON-NLS-1$
	}

	private static RenderingContext createContext(RegiomedOrderResponse response, boolean isSearchAvailable,
			Set<String> removed, Map<String, String> replacements, Set<String> forcedItems) {

		Map<String, List<AlternativeResult>> altsMap = Collections.emptyMap();
		if (response.getAlternatives() != null && !response.getAlternatives().isEmpty()) {
			altsMap = response.getAlternatives().stream()
					.collect(Collectors.groupingBy(a -> getKey(a.getPharmaCodeOrg(), a.getEanIDOrg())));
		}

		return new RenderingContext(isSearchAvailable, removed, replacements, forcedItems, altsMap,
				loadLogoBase64("rsc/regiomed_logo.png"), loadLogoBase64("rsc/warning.png"), //$NON-NLS-1$ //$NON-NLS-2$
				loadLogoBase64("rsc/edit.png")); //$NON-NLS-1$
	}

	private static String createBadge(String cssClass, String id, String text) {
		return createBadge(cssClass, id, text, null);
	}

	private static String createBadge(String cssClass, String id, String text, String inlineStyle) {
		StringBuilder sb = new StringBuilder("<span class='badge"); //$NON-NLS-1$
		if (cssClass != null)
			sb.append(StringUtils.SPACE).append(cssClass);
		sb.append("'"); //$NON-NLS-1$
		if (id != null)
			sb.append(" id='").append(id).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		if (inlineStyle != null)
			sb.append(" style='").append(inlineStyle).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(">").append(text).append("</span>"); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}

	private static boolean isCalculatedError(ArticleResult a, Map<String, List<AlternativeResult>> alternativesMap) {
		if (!a.isSuccess())
			return true;
		if (a.getAvailableInventory() > 0 && a.getQuantity() > a.getAvailableInventory())
			return true;

		String key = getKey(a);
		boolean hasAlternatives = alternativesMap.containsKey(key) && !alternativesMap.get(key).isEmpty();
		if (hasAlternatives) {
			return !a.isSuccessAvailability();
		}
		return false;
	}

	private static boolean isHandled(String key, RenderingContext ctx) {
		return (ctx.removed != null && ctx.removed.contains(key))
				|| (ctx.replacements != null && ctx.replacements.containsKey(key))
				|| (ctx.forcedItems != null && ctx.forcedItems.contains(key));
	}

	private static String getKey(ArticleResult a) {
		return getKey(a.getPharmaCode(), a.getEanID());
	}

	private static String getKey(long pharma, long ean) {
		return pharma + ":" + ean; //$NON-NLS-1$
	}

	private static String escapeHtml(String text) {
		if (StringUtils.isBlank(text))
			return StringUtils.EMPTY;
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
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
			return null;
		}
	}

	private record RenderingContext(boolean isSearchAvailable, Set<String> removed, Map<String, String> replacements,
			Set<String> forcedItems, Map<String, List<AlternativeResult>> alternativesMap, String imgLogo,
			String imgWarning, String imgEdit) {
	}

	private static String getJsScript(String warningSrc) {
		return """
				<div id="qtyModal" class="modal-overlay">
					<div class="modal-content">
						<div class="modal-header">%s</div>
						<div>%s</div>
						<div style="margin-top:10px;">
							<input type="number" id="qtyInput" class="modal-input" min="1" onkeypress="handleEnterQty(event)">
						</div>
						<div class="modal-footer">
							<button class="btn-modal btn-cancel" onclick="closeQtyModal()">%s</button>
							<button class="btn-modal btn-confirm" onclick="submitQty()">%s</button>
						</div>
					</div>
				</div>

				<div id="errorModal" class="modal-overlay" style="z-index: 3000;">
				    <div class="modal-content">
						<div class="modal-header" style="color:#dc3545;">
				             <img src='%s' style='height:24px;vertical-align:text-bottom;margin-right:8px;'> <span id="modalTitle">%s</span>
				        </div>
				        <div id="modalBody" class="modal-body"></div>
				        <div class="modal-footer">
				            <button class="btn-modal btn-cancel" onclick="closeErrorModal()">%s</button>
				        </div>
				    </div>
				</div>

				<div id="searchModal" class="modal-overlay" style="z-index: 2000;">
					<div class="modal-content modal-large">
						<div class="modal-header">%s</div>
						<div class="search-bar">
							<input type="text" id="searchInput" class="modal-input" spellcheck="false" placeholder="%s" onkeyup="handleSearchInput(event)">
							<button class="btn-modal btn-confirm" onclick="triggerSearch()">%s</button>
						</div>
						<div class="results-container">
							<div id="loading" class="loading">%s</div>
							<table class="results-table">
								<thead>
									<tr>
										<th>%s</th>
										<th style="width:100px;">EAN</th>
										<th style="width:150px;">%s</th>
										<th style="width:70px; text-align:right;">%s</th>
									</tr>
								</thead>
								<tbody id="searchResultsBody">
								</tbody>
							</table>
						</div>
						<div class="modal-footer">
							<button id="btnApply" class="btn-modal btn-confirm" onclick="applySelected()" disabled>%s</button>
							<button class="btn-modal btn-cancel" onclick="closeSearchModal()">%s</button>
						</div>
					</div>
				</div>

				<script>
				var currentPharma = '';
				var currentEan = '';
				var currentSearchRowId = '';

				var searchTypingTimer;
				var selectedSearchIndex = -1;

				document.addEventListener('keydown', function(e) {
					if (e.key === 'Escape') {
						var err = document.getElementById('errorModal');
						if (err && err.style.display === 'flex') {
							closeErrorModal();
							return;
						}

						var s = document.getElementById('searchModal');
						if (s && s.style.display === 'flex') {
							closeSearchModal();
							return;
						}

						var q = document.getElementById('qtyModal');
						if (q && q.style.display === 'flex') {
							closeQtyModal();
							return;
						}

						try {
							window.closeMainDialog();
						} catch(e) {
						}
					}
				});

				function openSearchModal(rowId, pharma, ean, currentName) {
					currentSearchRowId = rowId;
					currentPharma = pharma;
					currentEan = ean;
					selectedSearchIndex = -1;

					var input = document.getElementById('searchInput');
					input.value = currentName;
					document.getElementById('searchResultsBody').innerHTML = '';
					document.getElementById('loading').style.display = 'none';
					document.getElementById('btnApply').disabled = true;

					document.getElementById('searchModal').style.display = 'flex';

					input.focus();
					var len = input.value.length;
					input.setSelectionRange(len, len);

					if(len >= 3) {
						triggerSearch();
					}
				}

				function closeSearchModal() {
					document.getElementById('searchModal').style.display = 'none';
				}

				function handleSearchInput(e) {
					if(e.key === 'Escape') return;

					clearTimeout(searchTypingTimer);
					if (e.key === 'Enter') {
						triggerSearch();
					} else {
						var val = document.getElementById('searchInput').value;
						if(val.length >= 3) {
							searchTypingTimer = setTimeout(triggerSearch, 500);
						}
					}
				}

				function triggerSearch() {
					var val = document.getElementById('searchInput').value;
					if(val.length < 3) return;

					document.getElementById('loading').style.display = 'block';
					document.getElementById('searchResultsBody').innerHTML = '';
					selectedSearchIndex = -1;
					document.getElementById('btnApply').disabled = true;

					window.location = 'regiomed:searchQuery:' + encodeURIComponent(val);
				}

				function fillSearchResults(htmlRows) {
					document.getElementById('loading').style.display = 'none';
					document.getElementById('searchResultsBody').innerHTML = htmlRows;
				}

				function selectSearchResult(index) {
					selectedSearchIndex = index;

					var rows = document.querySelectorAll('#searchResultsBody tr');
					rows.forEach(function(r) { r.classList.remove('selected-row'); });

					var row = document.getElementById('res_row_' + index);
					if(row) row.classList.add('selected-row');

					document.getElementById('btnApply').disabled = false;
				}

				function applySearchResult(index) {
					selectSearchResult(index);
					applySelected();
				}

				function applySelected() {
					if(selectedSearchIndex > -1) {
						window.location = 'regiomed:selectResult:' + selectedSearchIndex + ':' + currentSearchRowId + ':' + currentPharma + ':' + currentEan;
					}
				}

				function changeQuantity(pharma, ean, currentQty) {
					currentPharma = pharma;
					currentEan = ean;
					var input = document.getElementById('qtyInput');
					input.value = currentQty;
					document.getElementById('qtyModal').style.display = 'flex';
					input.focus();
					input.select();
				}

				function closeQtyModal() {
					document.getElementById('qtyModal').style.display = 'none';
				}

				function handleEnterQty(e) {
					if(e.key === 'Enter') submitQty();
				}

				function submitQty() {
					var val = document.getElementById('qtyInput').value;
					if (val != null && val != "" && !isNaN(val) && val > 0) {
						closeQtyModal();
						window.location = 'regiomed:updateQty:' + currentPharma + ':' + currentEan + ':' + val;
					} else {
						alert("%s");
					}
				}

				function removeArticle(rowId, pharma, ean) {
				    var row = document.getElementById(rowId);
				    if(row) {
				        row.style.opacity = '0.3';
				        row.style.textDecoration = 'line-through';
				        disableButtons(row);
				    }
				    window.location = 'regiomed:remove:' + pharma + ':' + ean;
				}

				function replaceArticle(rowId, orgPharma, orgEan) {
				    var sel = document.getElementById('sel_' + rowId);
				    if(sel) {
				        var val = sel.value;
				        var row = document.getElementById(rowId);
				        if(row) {
				            row.style.backgroundColor = '#e6f7ff';
				            var statusSpan = document.getElementById('status_' + rowId);
				            if(statusSpan) {
				                statusSpan.className = 'badge badge-replaced';
				                statusSpan.innerText = '%s';
				            }
				            disableButtons(row);
				        }
				        window.location = 'regiomed:replace:' + orgPharma + ':' + orgEan + ':' + val;
				    }
				}

				function forceOrder(pharma, ean) {
					window.location = 'regiomed:force:' + pharma + ':' + ean;
				}

				function disableButtons(row) {
				    var btns = row.querySelectorAll('button');
				    btns.forEach(function(btn) { btn.disabled = true; });
				    var sels = row.querySelectorAll('select');
				    sels.forEach(function(s) { s.disabled = true; });
				}

				function updateRowSuccess(rowId, badgeText) {
					closeSearchModal();
					showToast('%s ' + badgeText);

				    var row = document.getElementById(rowId);
				    if(row) {
				        row.style.backgroundColor = '#e6f7ff';
				        var btns = row.querySelectorAll('button');
				        btns.forEach(function(btn) { btn.disabled = true; });

				        var statusSpan = document.getElementById('status_' + rowId);
				        if(statusSpan) {
				            statusSpan.className = 'badge badge-replaced';
				            statusSpan.innerText = badgeText;
				        } else {
				             var cells = row.getElementsByTagName('td');
				             if(cells.length > 3) {
				                 cells[3].innerHTML = '<span class="badge badge-replaced">' + badgeText + '</span>';
				             }
				        }
				    }
				}

				function showErrorModal(title, message) {
				    document.getElementById('modalTitle').innerText = title;
				    document.getElementById('modalBody').innerText = message;
				    document.getElementById('errorModal').style.display = 'flex';
				}

				function closeErrorModal() {
				    document.getElementById('errorModal').style.display = 'none';
				}

				function showToast(msg) {
					var t = document.getElementById('toast');
					t.innerText = msg;
					t.className = 'toast show';
					setTimeout(function(){ t.className = t.className.replace('show', ''); }, 3000);
				}
				</script>
				""" //$NON-NLS-1$
				.formatted(Messages.RegiomedCheckTemplate_ChangeQtyTitle, Messages.RegiomedCheckTemplate_EnterNewQty,
						Messages.RegiomedCheckTemplate_Cancel, Messages.RegiomedCheckTemplate_Apply,
						StringUtils.defaultString(warningSrc), Messages.RegiomedCheckTemplate_ErrorTitle,
						Messages.RegiomedCheckTemplate_Understood, Messages.RegiomedCheckTemplate_SearchAltTitle,
						Messages.RegiomedCheckTemplate_SearchPlaceholder, Messages.RegiomedCheckTemplate_SearchBtn,
						Messages.RegiomedCheckTemplate_Searching, Messages.RegiomedCheckTemplate_ColName,
						Messages.RegiomedCheckTemplate_ColStatus, Messages.RegiomedCheckTemplate_ColPrice,
						Messages.RegiomedCheckTemplate_Apply, Messages.RegiomedCheckTemplate_Close,
						Messages.RegiomedCheckTemplate_InvalidQtyAlert, Messages.RegiomedCheckTemplate_BadgeReplaced,
						Messages.RegiomedCheckTemplate_SuccessAppliedPrefix
				);
	}

	private static final String CSS_STYLES = """
			body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f6f9; color: #333; margin: 0; padding: 20px; min-height: 100vh; box-sizing: border-box; }
			.container { background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); padding: 25px; max-width: 900px; margin: auto; min-height: 80vh; }

			.qty-editable { cursor: pointer; text-decoration: underline dotted #999; }
			.qty-editable:hover { background-color: #e6f7ff; color: #005b96; }
			.header { border-bottom: 2px solid #005b96; padding-bottom: 15px; margin-bottom: 20px; display: flex; align-items: center; justify-content: space-between; }
			.header-left { display: flex; align-items: center; }
			.logo-img { height: 100px; margin-right: 15px; width: auto; }
			.logo-text { font-size: 24px; font-weight: bold; color: #005b96; }
			.status-box { padding: 10px; border-radius: 3px; margin-bottom: 10px; font-size: 14px; line-height: 1.5; }
			.status-ok { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
			.status-warn { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; }
			.hint-box { background-color: #e2f0fb; border-left: 4px solid #005b96; color: #004085; padding: 5px; margin-bottom: 20px; border-radius: 4px; font-size: 14px; }
			h3 { margin-top: 0; color: #005b96; font-size: 18px; }
			table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 13px; }
			th { text-align: left; background: #adcacf; padding: 8px; border-bottom: 2px solid #ddd; color: #555; }
			td { padding: 8px; border-bottom: 1px solid #eee; vertical-align: top; }
			tr:last-child td { border-bottom: none; }
			.badge { padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; }
			.badge-ok { background: #d4edda; color: #155724; }
			.badge-error { background: #f8d7da; color: #721c24; }
			.badge-replaced { background: #cce5ff; color: #004085; }
			.section-title { margin-top: 25px; margin-bottom: 10px; font-weight: bold; color: #333; border-left: 4px solid #005b96; padding-left: 10px; }
			.warning-section { border-left-color: #dc3545; }
			.action-btn-container { display: flex; gap: 5px; width: 100%; }
			.layout-column { flex-direction: column; }
			.layout-row { flex-direction: row; }
			.btn-base { flex: 1; box-sizing: border-box; padding: 5px 8px; border-radius: 4px; cursor: pointer; font-size: 11px; font-weight: bold; text-align: center; margin: 0; white-space: nowrap; }
			.btn-delete { background-color: #fff; border: 1px solid #dc3545; color: #dc3545; }
			.btn-delete:hover { background-color: #dc3545; color: white; }
			.btn-replace { background-color: #fff; border: 1px solid #007bff; color: #007bff; }
			.btn-replace:hover { background-color: #007bff; color: white; }
			.btn-search { background-color: #fff; border: 1px solid #17a2b8; color: #17a2b8; }
			.btn-search:hover { background-color: #17a2b8; color: white; }
			.alt-select { max-width: 250px; width: 100%; padding: 2px; font-size: 12px; margin-top: 2px; border: 1px solid #ccc; border-radius: 3px; }

			.modal-overlay { display: none; position: fixed; z-index: 2000; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); justify-content: center; align-items: center; }
			.modal-content { background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.3); width: 350px; max-width: 90%; text-align: left; animation: fadeIn 0.2s; display: flex; flex-direction: column; }

			.modal-large { width: 700px; height: 700px; max-height: 90vh; }

			.modal-header { font-size: 16px; font-weight: bold; color: #005b96; margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 10px; flex-shrink: 0; }
			.modal-input { width: 100%; padding: 8px; font-size: 14px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; margin-bottom: 15px; }
			.modal-footer { text-align: right; display: flex; gap: 10px; justify-content: flex-end; margin-top: 15px; flex-shrink: 0; }
			.btn-modal { padding: 6px 12px; border-radius: 4px; border: none; cursor: pointer; font-size: 13px; }
			.btn-confirm { background-color: #005b96; color: white; }
			.btn-confirm:hover { background-color: #004470; }
			.btn-confirm:disabled { background-color: #ccc; cursor: not-allowed; }
			.btn-cancel { background-color: #e2e6ea; color: #333; }
			.btn-cancel:hover { background-color: #dae0e5; }

			.search-bar { display: flex; gap: 10px; margin-bottom: 10px; flex-shrink: 0; }
			.search-bar .modal-input { margin-bottom: 0; }

			.results-container { flex-grow: 1; overflow-y: auto; border: 1px solid #eee; border-radius: 4px; position: relative; height: 100%; }

			.results-table { width: 100%; border-collapse: collapse; font-size: 13px; margin: 0; }
			.results-table th { position: sticky; top: 0; background: #f1f3f5; z-index: 10; padding: 8px; text-align: left; color: #555; font-weight: bold; border-bottom: 1px solid #ddd; }
			.results-table td { padding: 8px; border-bottom: 1px solid #f5f5f5; cursor: pointer; }
			.results-table tr:hover { background-color: #e6f7ff; }
			.selected-row { background-color: #cce5ff !important; outline: 1px solid #005b96; }
			.loading { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); color: #888; font-style: italic; display: none; }
			.price-cell { text-align: right; font-weight: bold; }
			.status-cell { font-size: 11px; color: #666; }

			.toast { visibility: hidden; min-width: 250px; background-color: #333; color: #fff; text-align: center; border-radius: 4px; padding: 12px; position: fixed; z-index: 4000; left: 50%; top: 30px; transform: translateX(-50%); font-size: 14px; box-shadow: 0 4px 10px rgba(0,0,0,0.3); }
			.toast.show { visibility: visible; animation: fadein 0.5s, fadeout 0.5s 2.5s; }
			@keyframes fadein { from {top: 0; opacity: 0;} to {top: 30px; opacity: 1;} }
			@keyframes fadeout { from {top: 30px; opacity: 1;} to {top: 0; opacity: 0;} }
			@keyframes fadeIn { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
			"""; //$NON-NLS-1$
}