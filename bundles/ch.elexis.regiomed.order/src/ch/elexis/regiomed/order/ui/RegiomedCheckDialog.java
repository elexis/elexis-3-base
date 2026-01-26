package ch.elexis.regiomed.order.ui;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
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

public class RegiomedCheckDialog extends Dialog {

	private static final Logger log = LoggerFactory.getLogger(RegiomedCheckDialog.class);

	private final RegiomedOrderResponse response;
	private final Set<String> removedIdentifiers = new HashSet<>();
	private final Map<String, String> replacements = new HashMap<>();
	private final Map<String, String> replacementNames = new HashMap<>();
	private final Set<String> forcedItems = new HashSet<>();
	private final Set<String> articlesWithAlternatives = new HashSet<>();

	private boolean searchAvailable = false;
	private int remainingErrors = 0;

	private List<ProductResult> currentSearchResults = Collections.emptyList();

	private Browser browser;
	private Button okButton;

	public RegiomedCheckDialog(Shell parentShell, RegiomedOrderResponse response) {
		super(parentShell);
		this.response = response;
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		initializeState();
	}

	private void initializeState() {
		try {
			RegiomedConfig config = RegiomedConfig.load();
			this.searchAvailable = new RegiomedOrderClient().checkSearchAvailability(config);
		} catch (Exception e) {
			log.warn("Could not check search availability", e);
			this.searchAvailable = false;
		}

		if (response.getAlternatives() != null) {
			response.getAlternatives()
					.forEach(alt -> articlesWithAlternatives.add(makeKey(alt.getPharmaCodeOrg(), alt.getEanIDOrg())));
		}
		recalcErrors();
		loadMissingAlternativesForErrors();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.RegiomedCheckDialog_Title);
		newShell.setImage(Images.IMG_LOGO.getImage());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new GridLayout(1, false));

		browser = new Browser(area, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		new BrowserFunction(browser, "closeMainDialog") {
			@Override
			public Object function(Object[] arguments) {
				Display.getDefault().asyncExec(() -> cancelPressed());
				return null;
			}
		};

		browser.addLocationListener(new LocationListener() {
			@Override
			public void changed(LocationEvent event) {
			}

			@Override
			public void changing(LocationEvent event) {
				if (event.location != null && event.location.startsWith("regiomed:")) {
					handleBrowserAction(event.location);
					event.doit = false;
				}
			}
		});

		browser.addTraverseListener(e -> {
			if (e.detail == SWT.TRAVERSE_ESCAPE) {
				e.doit = false;
			}
		});

		refreshBrowser();
		return area;
	}

	private void handleBrowserAction(String url) {
		String[] parts = url.split(":");
		if (parts.length < 2)
			return;

		String action = parts[1];

		switch (action) {
		case "force":
			handleForce(parts);
			break;
		case "reset":
			handleReset(parts);
			break;
		case "updateQty":
			handleUpdateQty(parts);
			break;
		case "remove":
			handleRemove(parts);
			break;
		case "replace":
			handleReplace(parts);
			break;
		case "searchQuery":
			handleSearchQuery(parts);
			break;
		case "selectResult":
			handleSelectResult(parts);
			break;
		default:
			log.warn("Unknown Regiomed action: {}", action);
		}
	}

	private void handleReset(String[] parts) {
		if (parts.length < 4)
			return;
		String key = makeKey(parts[2], parts[3]);

		boolean changed = false;

		if (removedIdentifiers.contains(key)) {
			removedIdentifiers.remove(key);
			changed = true;
		}

		if (replacements.containsKey(key)) {
			replacements.remove(key);
			replacementNames.remove(key);
			changed = true;
		}

		if (forcedItems.contains(key)) {
			forcedItems.remove(key);
			changed = true;
		}

		if (changed) {
			updateStateAndUI();
		}
	}

	private void handleForce(String[] parts) {
		if (parts.length < 4)
			return;
		String key = makeKey(parts[2], parts[3]);
		forcedItems.add(key);
		updateStateAndUI();
	}

	private void handleRemove(String[] parts) {
		if (parts.length < 4)
			return;
		String key = makeKey(parts[2], parts[3]);
		if (!removedIdentifiers.contains(key)) {
			removedIdentifiers.add(key);
			replacements.remove(key);
			forcedItems.remove(key);
			updateStateAndUI();
		}
	}

	private void handleReplace(String[] parts) {
		if (parts.length < 6)
			return;
		String orgKey = makeKey(parts[2], parts[3]);
		String newKey = makeKey(parts[4], parts[5]);

		if (!replacements.containsKey(orgKey)) {
			AlternativeResult selectedAlt = findAlternativeByKey(newKey);

			if (selectedAlt != null) {
				boolean allowed = validateReplacementWithServer(selectedAlt);
				if (!allowed) {
					return;
				}
				replacements.put(orgKey, newKey);
				replacementNames.put(orgKey, selectedAlt.getDescription());
				forcedItems.remove(orgKey);
				updateStateAndUI();
			}
		}
	}

	private AlternativeResult findAlternativeByKey(String key) {
		if (response.getAlternatives() != null) {
			for (AlternativeResult alt : response.getAlternatives()) {
				String altKey = makeKey(alt.getPharmaCode(), alt.getEanID());
				if (altKey.equals(key)) {
					return alt;
				}
			}
		}
		return null;
	}

	private void handleUpdateQty(String[] parts) {
		if (parts.length < 5)
			return;
		try {
			String pharma = parts[2];
			String ean = parts[3];
			int newQty = Integer.parseInt(parts[4]);

			BusyIndicator.showWhile(getShell().getDisplay(), () -> {
				performQuantityUpdate(pharma, ean, newQty);
			});
		} catch (NumberFormatException e) {
			log.error("Invalid quantity format: {}", parts[4], e);
		}
	}

	private void handleSearchQuery(String[] parts) {
		if (parts.length < 3)
			return;

		String query = URLDecoder.decode(parts[2], StandardCharsets.UTF_8);

		BusyIndicator.showWhile(getShell().getDisplay(), () -> {
			try {
				RegiomedConfig config = RegiomedConfig.load();
				RegiomedOrderClient client = new RegiomedOrderClient();
				RegiomedProductLookupResponse resp = client.searchProducts(config, query);

				if (resp != null && resp.products != null) {
					currentSearchResults = resp.products;
				} else {
					currentSearchResults = Collections.emptyList();
				}

				String rowsHtml = RegiomedCheckTemplate.generateSearchResultRows(currentSearchResults);
				String safeHtml = rowsHtml.replace("'", "\\'").replace("\n", "");
				browser.execute("fillSearchResults('" + safeHtml + "');");

			} catch (Exception e) {
				log.error("Search failed", e);
				String errorRow = "<tr><td colspan='4' style='color:red'>" + Messages.RegiomedCheckDialog_ErrorLabel
						+ ": " + e.getMessage().replace("'", "") + "</td></tr>";
				browser.execute("fillSearchResults('" + errorRow + "');");
			}
		});
	}

	private void handleSelectResult(String[] parts) {
		if (parts.length < 6)
			return;

		try {
			int index = Integer.parseInt(parts[2]);
			String orgPharma = parts[4];
			String orgEan = parts[5];

			if (index >= 0 && index < currentSearchResults.size()) {
				ProductResult selected = currentSearchResults.get(index);

				boolean allowed = validateReplacementWithServer(selected);
				if (!allowed)
					return;

				String orgKey = makeKey(orgPharma, orgEan);
				String newKey = makeKey(selected.pharmaCode, selected.ean);
				replacements.put(orgKey, newKey);
				replacementNames.put(orgKey, selected.prodName);
				forcedItems.remove(orgKey);

				updateStateAndUI();
			}

		} catch (NumberFormatException e) {
			log.error("Invalid selection index", e);
		}
	}

	private void updateStateAndUI() {
		recalcErrors();
		updateOkButtonState();
		refreshBrowser();
	}

	private void refreshBrowser() {
		if (browser != null && !browser.isDisposed()) {
			String html = RegiomedCheckTemplate.generateHtml(response, searchAvailable, removedIdentifiers,
					replacements, replacementNames, forcedItems);
			browser.setText(html);
		}
	}

	private void performQuantityUpdate(String pharma, String ean, int newQty) {
		if (response.getArticles() == null)
			return;

		boolean updated = false;

		for (ArticleResult art : response.getArticles()) {
			if (String.valueOf(art.getPharmaCode()).equals(pharma) && String.valueOf(art.getEanID()).equals(ean)) {
				art.setQuantity(newQty);
				updated = true;

				boolean isStockOK = (art.getAvailableInventory() <= 0) || (newQty <= art.getAvailableInventory());

				if (art.isSuccess() && isStockOK) {
					art.setSuccessAvailability(true);
					art.setAvailState(Messages.RegiomedCheckDialog_Yes);
					art.setAvailMsg(Messages.RegiomedCheckDialog_AvailableQtyAdjusted);
				} else if (art.isSuccess() && !isStockOK) {
					art.setSuccessAvailability(false);
					art.setAvailState(Messages.RegiomedCheckDialog_No);
					art.setAvailMsg(MessageFormat.format(Messages.RegiomedCheckDialog_QtyExceedsStock, newQty,
							art.getAvailableInventory()));

					String key = makeKey(pharma, ean);
					if (!articlesWithAlternatives.contains(key)) {
						fetchMissingAlternatives(art);
					}
				}
				break;
			}
		}

		if (updated) {
			updateStateAndUI();
		}
	}

	private void recalcErrors() {
		this.remainingErrors = 0;
		if (response.getArticles() != null) {
			for (ArticleResult a : response.getArticles()) {
				if (isCalculatedError(a)) {
					this.remainingErrors++;
				}
			}
		}
	}

	private boolean isCalculatedError(ArticleResult a) {
		String key = makeKey(a.getPharmaCode(), a.getEanID());

		if (removedIdentifiers.contains(key))
			return false;
		if (replacements.containsKey(key))
			return false;
		if (forcedItems.contains(key))
			return false;

		if (!a.isSuccess())
			return true;

		if (a.getAvailableInventory() > 0 && a.getQuantity() > a.getAvailableInventory())
			return true;

		boolean hasAlternatives = articlesWithAlternatives.contains(key);
		if (hasAlternatives) {
			return !a.isSuccessAvailability();
		}
		return false;
	}

	private void fetchMissingAlternatives(ArticleResult art) {
		try {
			RegiomedOrderClient client = new RegiomedOrderClient();
			RegiomedAlternativesResponse altResp = client.getAlternatives(RegiomedConfig.load(), "PCAVAIL",
					String.valueOf(art.getPharmaCode()));

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
		} catch (Exception e) {
			log.error("Error fetching alternatives for article {}", art.getPharmaCode(), e);
		}
	}

	private void loadMissingAlternativesForErrors() {
		if (response.getArticles() == null)
			return;

		List<ArticleResult> toLoad = response.getArticles().stream().filter(this::isCalculatedError)
				.filter(a -> !articlesWithAlternatives.contains(makeKey(a.getPharmaCode(), a.getEanID())))
				.collect(Collectors.toList());

		if (!toLoad.isEmpty()) {
			BusyIndicator.showWhile(getShell() != null ? getShell().getDisplay() : null, () -> {
				for (ArticleResult a : toLoad) {
					fetchMissingAlternatives(a);
				}
			});
		}
	}

	private boolean validateReplacementWithServer(ProductResult selected) {
		try {
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
					if (res.getPharmaCode() == selected.pharmaCode && !res.isSuccess()) {
						showJsError(selected.prodName,
								Objects.toString(res.getInfo(), Messages.RegiomedCheckDialog_UnknownError));
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			log.error("Server validation failed", e);
			SWTHelper.showError(Messages.RegiomedCheckDialog_ErrorLabel,
					Messages.RegiomedCheckDialog_ServerValidationFailed + " " + e.getMessage());
			return false;
		}
	}

	private boolean validateReplacementWithServer(AlternativeResult alt) {
		ProductResult temp = new ProductResult();
		temp.pharmaCode = alt.getPharmaCode();
		temp.ean = String.valueOf(alt.getEanID());
		temp.prodName = alt.getDescription();
		return validateReplacementWithServer(temp);
	}

	private void showJsError(String prodName, String reason) {
		String msg = Messages.RegiomedCheckDialog_ItemRejected + "\n" + prodName + "\n\n"
				+ Messages.RegiomedCheckDialog_Reason + " " + reason;
		String jsCall = "showErrorModal('" + escapeJs(Messages.RegiomedCheckDialog_NotOrderable) + "', '"
				+ escapeJs(msg).replace("\n", "\\n") + "'); unlockLastRow();";
		if (browser != null && !browser.isDisposed()) {
			browser.execute(jsCall);
		}
	}

	private String escapeJs(String text) {
		return text == null ? "" : text.replace("'", "\\'").replace("\"", "\\\"");
	}

	private String makeKey(Object pharma, Object ean) {
		return makeKey(String.valueOf(pharma), String.valueOf(ean));
	}

	private String makeKey(String pharma, String ean) {
		return StringUtils.defaultIfBlank(pharma, "0") + ":" + StringUtils.defaultIfBlank(ean, "0");
	}

	public List<ArticleResult> getDeletedArticles() {
		if (response.getArticles() == null)
			return new ArrayList<>();
		return response.getArticles().stream()
				.filter(item -> removedIdentifiers.contains(makeKey(item.getPharmaCode(), item.getEanID())))
				.collect(Collectors.toList());
	}

	public Map<String, String> getReplacements() {
		return replacements;
	}

	public Set<String> getRemovedIdentifiers() {
		return removedIdentifiers;
	}

	private void updateOkButtonState() {
		if (okButton != null && !okButton.isDisposed()) {
			okButton.setEnabled(remainingErrors <= 0);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, Messages.RegiomedCheckDialog_OrderBinding, true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.RegiomedCheckDialog_Cancel, false);
		updateOkButtonState();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(950, 950);
	}
}