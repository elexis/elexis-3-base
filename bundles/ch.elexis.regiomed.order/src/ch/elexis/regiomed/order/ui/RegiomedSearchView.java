package ch.elexis.regiomed.order.ui;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.equo.chromium.swt.Browser;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IOrder;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.model.IStock;
import ch.elexis.core.model.IStockEntry;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IOrderService;
import ch.elexis.core.services.IStockService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.StockServiceHolder;
import ch.elexis.core.ui.constants.ExtensionPointConstantsUi;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse;
import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse.ProductResult;
import ch.elexis.regiomed.order.preferences.RegiomedConstants;
import ch.elexis.regiomed.order.service.RegiomedLocalArticleService;
import ch.elexis.regiomed.order.service.RegiomedServerService;

public class RegiomedSearchView extends ViewPart {
	public static final String ID = "ch.elexis.regiomed.order.ui.RegiomedSearchView";
	private static final Logger log = LoggerFactory.getLogger(RegiomedSearchView.class);



	private Browser browser;
	private TableViewer cartViewer;
	private List<ProductResult> currentSearchResults = new ArrayList<>();

	private List<CartItem> shoppingCart = new ArrayList<>();

	private final RegiomedServerService serverService = new RegiomedServerService();
	private final RegiomedLocalArticleService localArticleService = new RegiomedLocalArticleService();

	private class CartItem {
		ProductResult product;
		int quantity = 1;
		Map<String, Integer> localStocks = new HashMap<>();
		CartItem(ProductResult product, Map<String, Integer> localStocks) {
			this.product = product;
			this.localStocks = localStocks;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite topComposite = new Composite(sashForm, SWT.NONE);
		topComposite.setLayout(new GridLayout(1, false));

		browser = new Browser(topComposite, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.addLocationListener(new LocationListener() {
			@Override
			public void changing(LocationEvent event) {
				if (event.location != null && event.location.startsWith(RegiomedConstants.CONST_REGIOMED_URL_PREFIX)) {
					handleBrowserAction(event.location);
					event.doit = false;
				}
			}
			@Override
			public void changed(LocationEvent event) {
			}
		});

		Composite bottomComposite = new Composite(sashForm, SWT.NONE);
		bottomComposite.setLayout(new GridLayout(1, false));

		Label lblCart = new Label(bottomComposite, SWT.NONE);
		lblCart.setText(Messages.RegiomedSearchView_CartLabel);
		lblCart.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		createCartTable(bottomComposite);

		Button btnCreateOrder = new Button(bottomComposite, SWT.PUSH);
		btnCreateOrder.setText(Messages.RegiomedSearchView_CreateOrderBtn);
		btnCreateOrder.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		btnCreateOrder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createElexisOrder();
			}
		});

		sashForm.setWeights(new int[] { 60, 40 });
		refreshUI();
	}

	private void createCartTable(Composite parent) {
		cartViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = cartViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TableViewerColumn colName = new TableViewerColumn(cartViewer, SWT.NONE);
		colName.getColumn().setText(Messages.RegiomedSearchView_ColProductName);
		colName.getColumn().setWidth(250);
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CartItem) element).product.prodName;
			}
		});

		TableViewerColumn colQty = new TableViewerColumn(cartViewer, SWT.NONE);
		colQty.getColumn().setText(Messages.RegiomedSearchView_ColQuantity);
		colQty.getColumn().setWidth(60);
		colQty.getColumn().setAlignment(SWT.CENTER);
		colQty.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return String.valueOf(((CartItem) element).quantity);
			}
		});

		colQty.setEditingSupport(new EditingSupport(cartViewer) {
			@Override
			protected void setValue(Object element, Object value) {
				try {
					int newQty = Integer.parseInt((String) value);
					if (newQty > 0) {
						((CartItem) element).quantity = newQty;
						cartViewer.refresh(element);
					}
				} catch (NumberFormatException e) {
				}
			}
			@Override
			protected Object getValue(Object element) {
				return String.valueOf(((CartItem) element).quantity);
			}
			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(cartViewer.getTable());
			}
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		TableViewerColumn colLocalStock = new TableViewerColumn(cartViewer, SWT.NONE);
		colLocalStock.getColumn().setText(Messages.RegiomedSearchView_ColLocalStock);
		colLocalStock.getColumn().setWidth(140);
		colLocalStock.getColumn().setAlignment(SWT.LEFT);
		colLocalStock.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Map<String, Integer> stocks = ((CartItem) element).localStocks;
				if (stocks == null || stocks.isEmpty()) {
					return "-";
				}
				return stocks.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue())
						.collect(Collectors.joining(", "));
			}
		});

		TableViewerColumn colRegioStock = new TableViewerColumn(cartViewer, SWT.NONE);
		colRegioStock.getColumn().setText(Messages.RegiomedSearchView_ColRegioStock);
		colRegioStock.getColumn().setWidth(75);
		colRegioStock.getColumn().setAlignment(SWT.CENTER);
		colRegioStock.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CartItem) element).product.availableInventory;
			}
		});

		TableViewerColumn colEan = new TableViewerColumn(cartViewer, SWT.NONE);
		colEan.getColumn().setText(Messages.RegiomedSearchView_ColEan);
		colEan.getColumn().setWidth(110);
		colEan.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CartItem) element).product.ean;
			}
		});

		TableViewerColumn colPrice = new TableViewerColumn(cartViewer, SWT.NONE);
		colPrice.getColumn().setText(Messages.RegiomedSearchView_ColPrice);
		colPrice.getColumn().setWidth(70);
		colPrice.getColumn().setAlignment(SWT.RIGHT);
		colPrice.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return String.format("%.2f", ((CartItem) element).product.price);
			}
		});

		TableViewerColumn colDelete = new TableViewerColumn(cartViewer, SWT.NONE);
		colDelete.getColumn().setText(StringUtils.EMPTY);
		colDelete.getColumn().setWidth(35);
		colDelete.getColumn().setAlignment(SWT.RIGHT);

		colDelete.setLabelProvider(new OwnerDrawLabelProvider() {
			@Override
			protected void measure(Event event, Object element) {
				event.width = colDelete.getColumn().getWidth();
			}

			@Override
			protected void paint(Event event, Object element) {
				Image img = Images.IMG_DELETE.getImage();
				if (img != null) {
					int x = event.x + event.width + 8;
					int y = event.y + (event.height - 16);
					event.gc.drawImage(img, x, y);
				}
			}
		});

		cartViewer.setContentProvider(ArrayContentProvider.getInstance());
		cartViewer.setInput(shoppingCart);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				ViewerCell cell = cartViewer.getCell(new Point(e.x, e.y));
				if (cell != null) {
					if (cell.getColumnIndex() == 6) {
						CartItem item = (CartItem) cell.getElement();
						shoppingCart.remove(item);
						cartViewer.refresh();
					}
				}
			}
		});

		DropTarget dt = new DropTarget(table, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT);
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}
			@Override
			public void dragOver(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
				event.feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
			}
			@Override
			public void drop(DropTargetEvent event) {
				if (event.data instanceof String) {
					String data = (String) event.data;
					if (data.startsWith(ExtensionPointConstantsUi.PAYLOAD_REGIOMED_ITEM)) {
						try {
							int index = Integer.parseInt(data.split(":")[1]);
							addItemToCart(index);
						} catch (Exception ex) {
							log.error("Error processing drag & drop event", ex);
						}
					}
				}
			}
		});
	}

	private void addItemToCart(int index) {
		if (index >= 0 && index < currentSearchResults.size()) {
			ProductResult product = currentSearchResults.get(index);
			if (shoppingCart.stream().noneMatch(item -> item.product.ean.equals(product.ean))) {
				Map<String, Integer> localStockMap = new HashMap<>();
				try {
					IArticle localArticle = localArticleService.findLocalArticle(product.ean, product.pharmaCode,
							product.prodName);
					if (localArticle != null) {
						IStockService stockService = OsgiServiceUtil.getService(IStockService.class).orElse(null);
						if (stockService != null) {
							for (IStock stock : stockService.getAllStocks(true, false)) {
								IStockEntry entry = stockService.findStockEntryForArticleInStock(stock, localArticle);
								if (entry != null && entry.getCurrentStock() > 0) {
									localStockMap.put(stock.getCode(), entry.getCurrentStock());
								}
							}
						}
					}
				} catch (Exception e) {
					log.warn("Could not load Elexis stock for cart", e);
				}
				final CartItem newItem = new CartItem(product, localStockMap);
				Display.getDefault().asyncExec(() -> {
					shoppingCart.add(newItem);
					cartViewer.refresh();
				});
			}
		}
	}

	private void refreshUI() {
		browser.setText(RegiomedCheckTemplate.generateHtmlForSearch());
	}

	private void handleBrowserAction(String url) {
		String[] parts = url.split(":");
		if (parts.length < 2)
			return;
		String action = parts[1];
		if ("searchQuery".equals(action)) {
			performSearch(URLDecoder.decode(parts[2], StandardCharsets.UTF_8));
		} else if ("saveFilter".equals(action)) {
			ConfigServiceHolder.get().setActiveUserContact(RegiomedConstants.CONST_STOCK_FILTER_KEY, parts[2]);
		} else if ("openUrl".equals(action)) {
			if (parts.length > 2) {
				Program.launch(URLDecoder.decode(parts[2], StandardCharsets.UTF_8));
			}
		} else if ("selectResult".equals(action)) {
			try {
				addItemToCart(Integer.parseInt(parts[2]));
			} catch (NumberFormatException e) {
				log.error("Invalid index in selectResult: {}", parts[2]);
			}
		}
	}

	private void performSearch(String query) {
		BusyIndicator.showWhile(Display.getDefault(), () -> {
			try {
				RegiomedProductLookupResponse resp = serverService.searchProducts(query);
				if (resp == null || resp.products == null) {
					browser.execute("document.getElementById('loading').style.display = 'none';");
					return;
				}
				this.currentSearchResults = resp.products;
				Map<ProductResult, Map<String, Integer>> stockCache = new HashMap<>();
				List<IStock> allStocks = new ArrayList<>();
				IStockService stockService = OsgiServiceUtil.getService(IStockService.class).orElse(null);
				if (stockService != null) {
					allStocks = stockService.getAllStocks(true, false);
					for (ProductResult p : resp.products) {
						IArticle localArticle = localArticleService.findLocalArticle(p.ean, p.pharmaCode, p.prodName);
						if (localArticle != null) {
							Map<String, Integer> stocksForProduct = new HashMap<>();
							for (IStock stock : allStocks) {
								IStockEntry entry = stockService.findStockEntryForArticleInStock(stock, localArticle);
								if (entry != null && entry.getCurrentStock() > 0) {
									stocksForProduct.put(stock.getCode(), entry.getCurrentStock());
								}
							}
							if (!stocksForProduct.isEmpty()) {
								stockCache.put(p, stocksForProduct);
							}
						}
					}
				}

				Collections.sort(resp.products, (p1, p2) -> {
					boolean hasStock1 = stockCache.containsKey(p1);
					boolean hasStock2 = stockCache.containsKey(p2);
					return Boolean.compare(hasStock2, hasStock1);
				});

				Map<Integer, Map<String, Integer>> localStockMap = new HashMap<>();
				for (int i = 0; i < resp.products.size(); i++) {
					ProductResult p = resp.products.get(i);
					if (stockCache.containsKey(p)) {
						localStockMap.put(i, stockCache.get(p));
					}
				}

				String lastFilter = ConfigServiceHolder.get()
						.getActiveUserContact(RegiomedConstants.CONST_STOCK_FILTER_KEY,
						"ALL");

				String rowsHtml = RegiomedCheckTemplate.generateSearchResultRows(resp.products, localStockMap,
						allStocks, lastFilter, true);

				String safeHtml = rowsHtml.replace("'", "\\'").replace("\n", "");
				browser.execute("fillSearchResults('" + safeHtml + "');");
			} catch (Exception e) {
				log.error("Search failed", e);
				browser.execute("document.getElementById('loading').style.display = 'none';");
			}
		});
	}

	private void createElexisOrder() {
		if (shoppingCart.isEmpty())
			return;
		IOrderService orderService = OsgiServiceUtil.getService(IOrderService.class).orElse(null);
		if (orderService == null) {
			log.error("IOrderService not found. Order cannot be created.");
			return;
		}

		Map<IArticle, Integer> articlesToOrder = new HashMap<>();
		List<String> notFoundProducts = new ArrayList<>();

		for (CartItem item : shoppingCart) {
			IArticle localArticle = localArticleService.findLocalArticle(item.product.ean, item.product.pharmaCode,
					item.product.prodName);

			if (localArticle != null) {
				articlesToOrder.put(localArticle, item.quantity);
			} else {
				notFoundProducts.add(item.product.prodName);
			}
		}

		if (!notFoundProducts.isEmpty()) {
			MessageDialog.openWarning(getSite().getShell(), Messages.RegiomedSearchView_NotFoundTitle,
					Messages.RegiomedSearchView_NotFoundMessage
							+ String.join("\n", notFoundProducts));
		}

		if (!articlesToOrder.isEmpty()) {
			String defaultName = Messages.RegiomedSearchView_OrderPrefix
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			InputDialog dialog = new InputDialog(getSite().getShell(), Messages.RegiomedSearchView_NewOrderTitle,
					Messages.RegiomedSearchView_NewOrderMessage, defaultName, null);
			if (dialog.open() == Window.OK) {
				String orderName = dialog.getValue();
				IOrder newOrder = CoreModelServiceHolder.get().create(IOrder.class);
				newOrder.setTimestamp(LocalDateTime.now());
				newOrder.setName(orderName);
				CoreModelServiceHolder.get().save(newOrder);
				orderService.getHistoryService().logCreateOrder(newOrder);
				String mandatorId = ContextServiceHolder.get().getActiveMandator().map(IMandator::getId).orElse(null);
				IStock stock = StockServiceHolder.get().getMandatorDefaultStock(mandatorId);
				for (Map.Entry<IArticle, Integer> entrySet : articlesToOrder.entrySet()) {
					IArticle article = entrySet.getKey();
					int qty = entrySet.getValue();
					IOrderEntry entry = newOrder.addEntry(article, stock, null, qty);
					orderService.getHistoryService().logChangedAmount(newOrder, entry, 0, qty);
					CoreModelServiceHolder.get().save(entry);
				}
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IArticle.class);
				shoppingCart.clear();
				cartViewer.refresh();
			}
		}
	}

	public IArticle getArticleForDropIndex(String payload) {
		try {
			int index = Integer.parseInt(payload.split(":")[1]);
			if (index >= 0 && index < currentSearchResults.size()) {
				ProductResult product = currentSearchResults.get(index);
				return localArticleService.findLocalArticle(product.ean, product.pharmaCode, product.prodName);
			}
		} catch (Exception e) {
			log.error("Error resolving the item for the external DND drop", e);
		}
		return null;
	}

	@Override
	public void setFocus() {
		if (browser != null && !browser.isDisposed()) {
			browser.setFocus();
		}
	}
}