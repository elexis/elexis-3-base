package ch.elexis.regiomed.order.ui.model;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.AlternativeResult;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.ArticleResult;

public class ArticleViewModel {

	private final ArticleResult item;
	private final String rowId;
	private final boolean isErrorTable;
	private final RenderingContext ctx;
	private final boolean isStockError;
	private final boolean isReplaced;
	private final boolean isRemoved;
	private final boolean isForced;
	private final String key;

	public ArticleViewModel(ArticleResult item, String rowId, boolean isErrorTable, RenderingContext ctx) {
		this.item = item;
		this.rowId = rowId;
		this.isErrorTable = isErrorTable;
		this.ctx = ctx;
		this.key = getKey(item);

		this.isReplaced = ctx.replacements() != null && ctx.replacements().containsKey(key);
		this.isRemoved = ctx.removed() != null && ctx.removed().contains(key);
		this.isForced = ctx.forcedItems() != null && ctx.forcedItems().contains(key);
		this.isStockError = item.getAvailableInventory() > 0 && item.getQuantity() > item.getAvailableInventory();
	}

	public String getRowId() {
		return rowId;
	}

	public String getDescription() {
		return escapeHtml(item.getDescription());
	}

	public String getEscapedDescription() {
		return escapeHtml(item.getDescription()).replace("'", "\\'");
	}

	public String getPharmaCode() {
		return String.valueOf(item.getPharmaCode());
	}

	public String getEan() {
		return String.valueOf(item.getEanID());
	}

	public String getStock() {
		return String.valueOf(item.getAvailableInventory());
	}

	public int getQuantity() {
		return item.getQuantity();
	}

	public boolean isStockError() {
		return isStockError;
	}

	public boolean isErrorTable() {
		return isErrorTable;
	}

	public boolean isDisabled() {
		return isReplaced || isRemoved || isForced;
	}

	public boolean isHandled() {
		return isRemoved || isReplaced || isForced;
	}

	public boolean getShowEditIcon() {
		return !isRemoved;
	}

	public String getRowClass() {
		if (isReplaced)
			return "row-replaced";
		if (isForced)
			return "row-forced";
		return "";
	}

	public String getContentClass() {
		if (isRemoved)
			return "state-removed";
		return "";
	}

	public String getInfoClass() {
		if (isRemoved)
			return "state-removed";
		if (isErrorTable && !isReplaced && !isRemoved && !isForced)
			return "text-danger";
		return "";
	}

	public String getBadgeClass() {
		if (isReplaced)
			return "badge-replaced";
		if (isForced)
			return "badge-forced";
		if (isErrorTable && !isRemoved)
			return "badge-error";
		if (isErrorTable && isRemoved)
			return "badge-error";
		return "badge-ok";
	}

	public String getInfoText() {
		StringBuilder sb = new StringBuilder();
		String originalMsg = StringUtils
				.defaultString(StringUtils.isBlank(item.getInfo()) ? item.getAvailMsg() : item.getInfo());
		if (isStockError && !isReplaced) {
			String standardErrorMsg = MessageFormat.format(Messages.RegiomedCheckTemplate_QtyExceedsStock,
					item.getQuantity(), item.getAvailableInventory());
			sb.append(standardErrorMsg);
			if (StringUtils.isNotBlank(originalMsg) && !originalMsg.contains(standardErrorMsg)) {
				sb.append("<br><small style='color:#666'>").append(Messages.RegiomedCheckTemplate_NoteLabel).append(" ")
						.append(escapeHtml(originalMsg)).append("</small>");
			}
		} else {
			sb.append(escapeHtml(originalMsg));
		}
		String availMsg = item.getAvailMsg();
		String orgMsg = item.getAvailMsgOrg();
		if (StringUtils.isNotBlank(orgMsg) && !availMsg.equalsIgnoreCase(orgMsg.trim())) {
			sb.append("<br><small style='color:#666'>").append(escapeHtml(orgMsg)).append("</small>");
		}

		return sb.toString();
	}

	public String getReplacementName() {
		if (!isReplaced)
			return null;

		String newKey = ctx.replacements().get(key);
		if (newKey == null)
			return null;

		String name = null;
		if (ctx.replacementNames() != null && ctx.replacementNames().containsKey(key)) {
			name = escapeHtml(ctx.replacementNames().get(key));
		} else {
			List<AlternativeResult> alts = ctx.alternativesMap().get(key);
			if (alts != null) {
				for (AlternativeResult alt : alts) {
					String altKey = alt.getPharmaCode() + ":" + alt.getEanID();
					if (altKey.equals(newKey)) {
						name = escapeHtml(alt.getDescription());
						break;
					}
				}
			}
		}

		if (name == null)
			return null;
		if (ctx.replacementInventory() != null && ctx.replacementInventory().containsKey(key)) {
			int stock = ctx.replacementInventory().get(key);
			if (stock != Integer.MAX_VALUE) {
				return name + ") (" + Messages.RegiomedCheckTemplate_StockLabel + StringUtils.SPACE + stock;
			}
		}

		return name;
	}

	public boolean getHasAlternatives() {
		List<AlternativeResult> alts = ctx.alternativesMap().get(key);
		return isErrorTable && alts != null && !alts.isEmpty();
	}

	public List<Map<String, String>> getAlternatives() {
		List<AlternativeResult> alts = ctx.alternativesMap().get(key);
		if (alts == null)
			return Collections.emptyList();

		return alts.stream().map(a -> {
			Map<String, String> m = new HashMap<>();
			String label = a.getDescription()
					+ (a.getPrice() != null && a.getPrice() > 0 ? " (CHF " + String.format("%.2f", a.getPrice()) + ")"
							: "");
			m.put("label", label);
			m.put("value", a.getPharmaCode() + ":" + a.getEanID());
			return m;
		}).collect(Collectors.toList());
	}

	public String getBadgeText() {
		if (isReplaced)
			return Messages.RegiomedCheckTemplate_BadgeReplaced;
		if (isForced)
			return Messages.RegiomedCheckTemplate_BadgeOrder;
		if (isErrorTable)
			return Messages.RegiomedCheckTemplate_BadgeError;
		return Messages.RegiomedCheckTemplate_BadgeOk;
	}

	public boolean getCanReset() {
		return isReplaced || isRemoved || isForced;
	}

	public boolean getShowForceBtn() {
		return isErrorTable && isStockError && !isForced && !isReplaced && !isRemoved;
	}

	public int getBtnCount() {
		int c = 1;
		if (getShowForceBtn())
			c++;
		if (ctx.isSearchAvailable())
			c++;
		if (getHasAlternatives())
			c++;
		return c;
	}

	private String getKey(ArticleResult a) {
		return a.getPharmaCode() + ":" + a.getEanID();
	}

	private String escapeHtml(String text) {
		if (StringUtils.isBlank(text))
			return "";
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}
}