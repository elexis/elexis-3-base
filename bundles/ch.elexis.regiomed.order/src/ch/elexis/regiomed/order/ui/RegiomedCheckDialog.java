package ch.elexis.regiomed.order.ui;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IStock;
import ch.elexis.core.model.IStockEntry;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IStockService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.regiomed.order.client.RegiomedOrderClient;
import ch.elexis.regiomed.order.config.RegiomedConfig;
import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.AlternativeResult;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.ArticleResult;
import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse;
import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse.ProductResult;
import ch.elexis.regiomed.order.service.RegiomedLocalArticleService;
import ch.elexis.regiomed.order.service.RegiomedServerService;

public class RegiomedCheckDialog extends Dialog {

	private static final Logger log = LoggerFactory.getLogger(RegiomedCheckDialog.class);

	private final RegiomedCheckController controller;
	private final RegiomedServerService serverService = new RegiomedServerService();
	private final RegiomedLocalArticleService localArticleService = new RegiomedLocalArticleService();

	private boolean searchAvailable = false;
	private List<ProductResult> currentSearchResults = Collections.emptyList();

	private Browser browser;
	private Button okButton;

	public RegiomedCheckDialog(Shell parentShell, RegiomedOrderResponse response) {
		super(parentShell);
		this.controller = new RegiomedCheckController(response, serverService);
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

		BusyIndicator.showWhile(Display.getDefault(), () -> {
			controller.loadMissingAlternatives();
		});
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
			if (parts.length >= 4) {
				controller.forceArticle(controller.makeKey(parts[2], parts[3]));
				updateStateAndUI();
			}
			break;
		case "reset":
			if (parts.length >= 4) {
				controller.resetArticle(controller.makeKey(parts[2], parts[3]));
				updateStateAndUI();
			}
			break;
		case "updateQty":
			handleUpdateQty(parts);
			break;
		case "remove":
			if (parts.length >= 4) {
				controller.removeArticle(controller.makeKey(parts[2], parts[3]));
				updateStateAndUI();
			}
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
		case "saveFilter":
			if (parts.length > 2) {
				ConfigServiceHolder.get().setActiveUserContact("ch.elexis.regiomed.stockFilter", parts[2]);
			}
			break;
		default:
			log.warn("Unknown Regiomed action: {}", action);
		}
	}

	private void handleReplace(String[] parts) {
		if (parts.length < 6)
			return;
		String orgKey = controller.makeKey(parts[2], parts[3]);
		String newKey = controller.makeKey(parts[4], parts[5]);

		if (!controller.getReplacements().containsKey(orgKey)) {
			AlternativeResult selectedAlt = findAlternativeByKey(newKey);

			if (selectedAlt != null) {
				try {
					ArticleResult validated = serverService.validateReplacement(selectedAlt);
					if (validated != null) {
						controller.replaceArticle(orgKey, newKey, selectedAlt.getDescription(),
								validated.getAvailableInventory());
						updateStateAndUI();
					}
				} catch (Exception e) {
					showJsError(selectedAlt.getDescription(), e.getMessage());
				}
			}
		}
	}

	private AlternativeResult findAlternativeByKey(String key) {
		RegiomedOrderResponse resp = controller.getResponse();
		if (resp.getAlternatives() != null) {
			for (AlternativeResult alt : resp.getAlternatives()) {
				String altKey = controller.makeKey(alt.getPharmaCode(), alt.getEanID());
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
				controller.updateQuantity(pharma, ean, newQty);
			});
			updateStateAndUI();
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
				RegiomedProductLookupResponse resp = serverService.searchProducts(query);
				Map<Integer, Map<String, Integer>> localStockMap = new HashMap<>();
				List<IStock> allStocks = new ArrayList<>();

				try {
					IStockService stockService = OsgiServiceUtil.getService(IStockService.class).orElse(null);
					if (stockService != null) {
						allStocks = stockService.getAllStocks(true, false);

						if (resp != null && resp.products != null) {
							for (int i = 0; i < resp.products.size(); i++) {
								ProductResult p = resp.products.get(i);
								IArticle localArticle = localArticleService.findLocalArticle(p.ean, p.pharmaCode,
										p.prodName);

								if (localArticle != null) {
									Map<String, Integer> stocksForProduct = new HashMap<>();
									for (IStock stock : allStocks) {
										IStockEntry entry = stockService.findStockEntryForArticleInStock(stock,
												localArticle);
										if (entry != null && entry.getCurrentStock() > 0) {
											stocksForProduct.put(stock.getCode(), entry.getCurrentStock());
										}
									}
									if (!stocksForProduct.isEmpty()) {
										localStockMap.put(i, stocksForProduct);
									}
								}
							}
						}
					}
				} catch (Exception ex) {
					log.error("Error retrieving local stock information", ex);
				}

				if (resp != null && resp.products != null) {
					currentSearchResults = resp.products;
				} else {
					currentSearchResults = Collections.emptyList();
				}
				String lastFilter = ConfigServiceHolder.get().getActiveUserContact("ch.elexis.regiomed.stockFilter",
						"ALL");
				String rowsHtml = RegiomedCheckTemplate.generateSearchResultRows(currentSearchResults, localStockMap,
						allStocks, lastFilter);
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

				try {
					ArticleResult validated = serverService.validateReplacement(selected);
					if (validated != null) {
						String orgKey = controller.makeKey(orgPharma, orgEan);
						String newKey = controller.makeKey(selected.pharmaCode, selected.ean);

						controller.replaceArticle(orgKey, newKey, selected.prodName, validated.getAvailableInventory());
						updateStateAndUI();
					}
				} catch (Exception e) {
					showJsError(selected.prodName, e.getMessage());
				}
			}
		} catch (NumberFormatException e) {
			log.error("Invalid selection index", e);
		}
	}

	private void updateStateAndUI() {
		boolean hasErrors = controller.getRemainingErrors() > 0;
		if (okButton != null && !okButton.isDisposed()) {
			okButton.setEnabled(!hasErrors);
		}
		refreshBrowser();
	}

	private void refreshBrowser() {
		if (browser != null && !browser.isDisposed()) {
			String html = RegiomedCheckTemplate.generateHtml(controller.getResponse(), searchAvailable,
					controller.getRemovedIdentifiers(), controller.getReplacements(), controller.getReplacementNames(),
					controller.getReplacementInventory(), controller.getForcedItems());
			browser.setText(html);
		}
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

	public List<ArticleResult> getDeletedArticles() {
		if (controller.getResponse().getArticles() == null)
			return new ArrayList<>();
		return controller.getResponse().getArticles().stream()
				.filter(item -> controller.getRemovedIdentifiers()
						.contains(controller.makeKey(item.getPharmaCode(), item.getEanID())))
				.collect(java.util.stream.Collectors.toList());
	}

	public Map<String, String> getReplacements() {
		return controller.getReplacements();
	}

	public Set<String> getRemovedIdentifiers() {
		return controller.getRemovedIdentifiers();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, Messages.RegiomedCheckDialog_OrderBinding, true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.RegiomedCheckDialog_Cancel, false);
		updateStateAndUI();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(950, 950);
	}
}