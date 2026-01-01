package ch.elexis.regiomed.order.ui;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;

public class RegiomedCheckTemplate {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); //$NON-NLS-1$

	public static String generateHtml(RegiomedOrderResponse response) {
		StringBuilder html = new StringBuilder();
		String logoSrc = loadLogoBase64();
		String tableHeader = getTableHeader();

		html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>"); //$NON-NLS-1$
		html.append("<style>").append(CSS_STYLES).append("</style>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append(JS_SCRIPT);
		html.append("</head><body>"); //$NON-NLS-1$
		html.append("<div class='container'>"); //$NON-NLS-1$

		html.append("<div class='header'><div class='header-left'>"); //$NON-NLS-1$
		if (logoSrc != null) {
			html.append("<img src='").append(logoSrc).append("' class='logo-img' alt='Regiomed Logo'>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		html.append("<div class='logo-text'>").append(Messages.RegiomedCheckTemplate_OrderTitle).append("</div></div>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("<div>").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</div></div>"); //$NON-NLS-1$ //$NON-NLS-2$

		boolean hasErrors = response.articlesNOK > 0 || !response.checkSuccess;
		String statusClass = hasErrors ? "status-warn" : "status-ok"; //$NON-NLS-1$ //$NON-NLS-2$
		html.append("<div class='status-box ").append(statusClass).append("'>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("<h3>").append(Messages.RegiomedCheckTemplate_CheckResult).append("</h3>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("<p>").append(escapeHtml(response.message)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("</div>"); //$NON-NLS-1$

		List<RegiomedOrderResponse.ArticleResult> okItems = null;
		List<RegiomedOrderResponse.ArticleResult> nokItems = null;

		if (response.articles != null) {
			okItems = response.articles.stream().filter(a -> a.success).collect(Collectors.toList());
			nokItems = response.articles.stream().filter(a -> !a.success).collect(Collectors.toList());
		}

		if (nokItems != null && !nokItems.isEmpty()) {
			html.append("<div class='hint-box'>"); //$NON-NLS-1$
			html.append(Messages.RegiomedCheckTemplate_HintText);
			html.append("</div>"); //$NON-NLS-1$

			html.append("<div class='section-title warning-section'>") //$NON-NLS-1$
					.append(Messages.RegiomedCheckTemplate_ProblematicItems).append("</div>"); //$NON-NLS-1$
			html.append(tableHeader);

			int i = 0;
			for (RegiomedOrderResponse.ArticleResult item : nokItems) {
				String rowId = "nok_row_" + i++; //$NON-NLS-1$
				html.append(buildTableRow(rowId, item, true));
			}
			html.append("</tbody></table>"); //$NON-NLS-1$
		}

		if (okItems != null && !okItems.isEmpty()) {
			html.append("<div class='section-title'>").append(Messages.RegiomedCheckTemplate_AvailableItems) //$NON-NLS-1$
					.append("</div>"); //$NON-NLS-1$
			html.append(tableHeader);
			int i = 0;
			for (RegiomedOrderResponse.ArticleResult item : okItems) {
				String rowId = "ok_row_" + i++; //$NON-NLS-1$
				html.append(buildTableRow(rowId, item, false));
			}
			html.append("</tbody></table>"); //$NON-NLS-1$
		}
		html.append("</div></body></html>"); //$NON-NLS-1$
		return html.toString();
	}

	private static String buildTableRow(String rowId, RegiomedOrderResponse.ArticleResult item, boolean isError) {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr id='").append(rowId).append("'>"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("<td>").append(escapeHtml(item.description)).append("<br><small style='color:#888'>") //$NON-NLS-1$ //$NON-NLS-2$
				.append(Messages.RegiomedCheckTemplate_PharmaLabel).append(" ") //$NON-NLS-1$
				.append(item.pharmaCode).append("</small></td>"); //$NON-NLS-1$
		sb.append("<td>").append(item.quantity).append("</td>"); //$NON-NLS-1$ //$NON-NLS-2$

		String colorStyle = isError ? "style='color:#dc3545'" : StringUtils.EMPTY; //$NON-NLS-1$
		String infoText = item.info != null ? item.info : StringUtils.EMPTY;
		sb.append("<td ").append(colorStyle).append(">").append(escapeHtml(infoText)).append("</td>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		if (isError) {
			sb.append("<td><span class='badge badge-error'>").append(Messages.RegiomedCheckTemplate_BadgeError) //$NON-NLS-1$
					.append("</span></td>"); //$NON-NLS-1$
		} else {
			sb.append("<td><span class='badge badge-ok'>").append(Messages.RegiomedCheckTemplate_BadgeOk) //$NON-NLS-1$
					.append("</span></td>"); //$NON-NLS-1$
		}

		sb.append("<td><button class='btn-delete' onclick=\"removeArticle('").append(rowId).append("', '") //$NON-NLS-1$ //$NON-NLS-2$
				.append(item.pharmaCode).append("', '").append(item.eanID).append("')\">") //$NON-NLS-1$ //$NON-NLS-2$
				.append(Messages.RegiomedCheckTemplate_BtnDelete).append("</button></td>"); //$NON-NLS-1$

		sb.append("</tr>"); //$NON-NLS-1$
		return sb.toString();
	}

	private static String loadLogoBase64() {
		try {
			Bundle bundle = FrameworkUtil.getBundle(RegiomedCheckTemplate.class);
			if (bundle == null)
				return null;
			URL url = bundle.getEntry("rsc/regiomed_logo.png"); //$NON-NLS-1$
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

	private static String escapeHtml(String text) {
		if (StringUtils.isBlank(text))
			return StringUtils.EMPTY;
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
	}

	private static String getTableHeader() {
		return "<table><thead><tr><th>" + Messages.RegiomedCheckTemplate_ColArticle + "</th><th>" //$NON-NLS-1$ //$NON-NLS-2$
				+ Messages.RegiomedCheckTemplate_ColAmount + "</th><th>" + Messages.RegiomedCheckTemplate_ColInfo //$NON-NLS-1$
				+ "</th><th>" + Messages.RegiomedCheckTemplate_ColStatus + "</th><th>" //$NON-NLS-1$ //$NON-NLS-2$
				+ Messages.RegiomedCheckTemplate_ColAction + "</th></tr></thead><tbody>"; //$NON-NLS-1$
	}

	private static final String CSS_STYLES = """
			body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f6f9; color: #333; margin: 0; padding: 20px; }
			.container { background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); padding: 25px; max-width: 900px; margin: auto; }

			.header { border-bottom: 2px solid #005b96; padding-bottom: 15px; margin-bottom: 20px; display: flex; align-items: center; justify-content: space-between; }
			.header-left { display: flex; align-items: center; }
			.logo-img { height: 100px; margin-right: 15px; width: auto; }
			.logo-text { font-size: 24px; font-weight: bold; color: #005b96; }

			.status-box { padding: 10px; border-radius: 3px; margin-bottom: 10px; font-size: 14px; line-height: 1.5; }
			.status-ok { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
			.status-warn { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; }

			.hint-box {
				background-color: #e2f0fb;
				border-left: 4px solid #005b96;
				color: #004085;
				padding: 5px;
				margin-bottom: 20px;
				border-radius: 4px;
				font-size: 14px;
			}

			h3 { margin-top: 0; color: #005b96; font-size: 18px; }

			table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 13px; }
			th { text-align: left; background: #adcacf; padding: 8px; border-bottom: 2px solid #ddd; color: #555; }
			td { padding: 8px; border-bottom: 1px solid #eee; vertical-align: top; }
			tr:last-child td { border-bottom: none; }

			.badge { padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; }
			.badge-ok { background: #d4edda; color: #155724; }
			.badge-error { background: #f8d7da; color: #721c24; }

			.section-title { margin-top: 25px; margin-bottom: 10px; font-weight: bold; color: #333; border-left: 4px solid #005b96; padding-left: 10px; }
			.warning-section { border-left-color: #dc3545; }

			.btn-delete {
				background-color: #fff;
				border: 1px solid #dc3545;
				color: #dc3545;
				padding: 4px 8px;
				border-radius: 4px;
				cursor: pointer;
				font-size: 11px;
				font-weight: bold;
				text-decoration: none;
				display: inline-block;
			}
			.btn-delete:hover {
				background-color: #dc3545;
				color: white;
			}
			"""; //$NON-NLS-1$

	private static final String JS_SCRIPT = """
			<script>
			function removeArticle(rowId, pharma, ean) {
				var row = document.getElementById(rowId);
				if(row) {
					row.style.opacity = '0.3';
					row.style.textDecoration = 'line-through';
					var btn = row.querySelector('.btn-delete');
					if(btn) btn.disabled = true;
				}
				window.location = 'regiomed:remove:' + pharma + ':' + ean;
			}
			</script>
			"""; //$NON-NLS-1$
}