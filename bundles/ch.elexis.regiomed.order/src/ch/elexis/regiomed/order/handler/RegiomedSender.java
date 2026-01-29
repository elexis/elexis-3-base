package ch.elexis.regiomed.order.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrder;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.OrderEntryState;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IOrderService;
import ch.elexis.core.services.holder.CodeElementServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.OrderServiceHolder;
import ch.elexis.core.status.ElexisStatus;
import ch.elexis.core.ui.exchange.ArticleUtil;
import ch.elexis.core.ui.exchange.IDataSender;
import ch.elexis.core.ui.exchange.XChangeException;
import ch.elexis.core.ui.exchange.elements.XChangeElement;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.BestellView;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.data.PersistentObject;
import ch.elexis.regiomed.order.client.RegiomedOrderClient;
import ch.elexis.regiomed.order.config.RegiomedConfig;
import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedOrderMapper;
import ch.elexis.regiomed.order.model.RegiomedOrderRequest;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.ArticleResult;
import ch.elexis.regiomed.order.model.RegiomedResponseHelper;
import ch.elexis.regiomed.order.preferences.RegiomedConstants;
import ch.elexis.regiomed.order.ui.RegiomedCheckDialog;
import ch.rgw.tools.StringTool;

public class RegiomedSender implements IDataSender {

	private static final Logger log = LoggerFactory.getLogger(RegiomedSender.class);
	private static final String ABORT_BY_USER = "ABORT_BY_USER"; //$NON-NLS-1$

	private final List<IOrderEntry> exportedEntries = new ArrayList<>();
	private final RegiomedOrderClient orderClient = new RegiomedOrderClient();
	private int counter;

	@Override
	public boolean canHandle(Class<? extends PersistentObject> clazz) {
		return clazz.equals(ch.elexis.data.Bestellung.class);
	}

	@Override
	public XChangeElement store(Object output) throws XChangeException {
		if (output instanceof IOrder order) {
			return addOrder(order);
		}
		throw new XChangeException("Can't handle object of class " + output.getClass().getName()); //$NON-NLS-1$
	}

	@Override
	public void finalizeExport() throws XChangeException {
		if (counter == 0) {
			log.info("Order contains no articles to order from Regiomed supplier"); //$NON-NLS-1$
			return;
		}
		try {
			RegiomedConfig config = RegiomedConfig.load();
			RegiomedOrderMapper mapper = new RegiomedOrderMapper();
			RegiomedOrderRequest request = mapper.mapToRequest(config, exportedEntries);
			RegiomedOrderResponse response = orderClient.sendOrderWithToken(config, request);

			if (request.isCheckOrder()) {
				response = processInteractiveOrder(config, response);
			} else {
				processDirectOrder(response);
			}

			finalizeOrder(response);

		} catch (Exception ex) {
			handleExportException(ex);
		}
	}

	private RegiomedOrderResponse processInteractiveOrder(RegiomedConfig config, RegiomedOrderResponse initialResponse)
			throws Exception {

		RegiomedCheckDialog dialog = new RegiomedCheckDialog(Display.getDefault().getActiveShell(), initialResponse);
		if (dialog.open() != org.eclipse.jface.window.Window.OK) {
			throw new XChangeException(ABORT_BY_USER);
		}
		handleDialogChanges(dialog, initialResponse);
		if (exportedEntries.isEmpty()) {
			SWTHelper.showInfo(Messages.RegiomedSender_AbortTitle, Messages.RegiomedSender_AllArticlesDeleted);
			throw new XChangeException(ABORT_BY_USER);
		}

		RegiomedOrderMapper mapper = new RegiomedOrderMapper();
		RegiomedOrderRequest finalRequest = mapper.mapToRequest(config, exportedEntries);
		finalRequest.setCheckOrder(false);
		RegiomedOrderResponse finalResponse = orderClient.sendOrderWithToken(config, finalRequest);
		if (!finalResponse.isOrderSent() && !RegiomedResponseHelper.isOverallSuccess(finalResponse)) {
			String err = RegiomedResponseHelper.buildErrorMessage(finalResponse);
			log.error("Regiomed Final Order error: {}", err); //$NON-NLS-1$
			throw new XChangeException(err);
		}
		return finalResponse;
	}

	private void processDirectOrder(RegiomedOrderResponse response) throws XChangeException {
		if (!response.isOrderSent() && !RegiomedResponseHelper.isOverallSuccess(response)) {
			String err = RegiomedResponseHelper.buildErrorMessage(response);
			log.error("Regiomed Direct Order error: {}", err); //$NON-NLS-1$
			throw new XChangeException(err);
		}
	}

	private void finalizeOrder(RegiomedOrderResponse response) {
		if (response.getArticlesNOK() > 0) {
			String msg = MessageFormat.format(Messages.RegiomedSender_OrderPartiallySentText, response.getArticlesOK(),
					response.getArticlesNOK());
			SWTHelper.showInfo(Messages.RegiomedSender_OrderPartiallySentTitle, msg);
		} else {
			if (!CoreUtil.isTestMode()) {
				SWTHelper.showInfo(Messages.RegiomedSender_SuccessTitle, Messages.RegiomedSender_SuccessText);
			}
		}
		updateOrderEntriesStatus(exportedEntries, response);
		exportedEntries.clear();
	}

	private void handleDialogChanges(RegiomedCheckDialog dialog, RegiomedOrderResponse response) {
		updateQuantities(response);
		Set<String> deletedIds = dialog.getRemovedIdentifiers();
		if (!deletedIds.isEmpty()) {
			removeDeletedEntries(deletedIds);
		}
		Map<String, String> replacements = dialog.getReplacements();
		if (!replacements.isEmpty()) {
			applyReplacements(replacements);
		}
	}

	private void handleExportException(Exception ex) throws XChangeException {
		if (ex instanceof XChangeException && ABORT_BY_USER.equals(ex.getMessage())) {
			throw (XChangeException) ex;
		}
		String err = ex.getMessage();
		ElexisStatus status = new ElexisStatus(ElexisStatus.LOG_ERRORS, "ch.elexis.regiomed.order", //$NON-NLS-1$
				ElexisStatus.CODE_NONE, err, null, ElexisStatus.LOG_ERRORS);
		StatusManager.getManager().handle(status);
		String msg = Messages.RegiomedSender_ErrorSending;
		if (StringUtils.isNotBlank(err)) {
			msg = err;
		}
		log.error("Order could not be sent to Regiomed.", ex); //$NON-NLS-1$
		SWTHelper.alert(Messages.RegiomedSender_ErrorTitle, msg);
		throw new XChangeException(msg);
	}

	private XChangeElement addOrder(IOrder order) throws XChangeException {
		counter = 0;
		exportedEntries.clear();
		if (order == null || order.getEntries() == null || order.getEntries().isEmpty()) {
			throw new XChangeException(Messages.RegiomedSender_OrderEmpty);
		}
		String supplierCfg = ConfigServiceHolder.getGlobal(RegiomedConstants.CFG_REGIOMED_SUPPLIER, StringUtils.EMPTY);
		IContact regiomedSupplier = BestellView.resolveDefaultSupplier(supplierCfg,
				Messages.RegiomedSender_NoSupplierTitle);
		if (regiomedSupplier == null) {
			return null;
		}
		for (IOrderEntry item : order.getEntries()) {
			if (!regiomedSupplier.equals(item.getProvider())) {
				continue;
			}
			validateAndAddEntry(item);
		}
		if (counter == 0) {
			throw new XChangeException(Messages.RegiomedSender_NoArticlesForSupplier);
		}

		return null;
	}

	private void validateAndAddEntry(IOrderEntry item) throws XChangeException {
		IArticle artikel = item.getArticle();
		String pharmacode = ArticleUtil.getPharmaCode(artikel);
		String eanId = ArticleUtil.getEan(artikel);
		int quantity = item.getAmount();
		boolean hasPharmacode = !StringTool.isNothing(pharmacode);
		boolean hasEan = StringUtils.isNotBlank(eanId);
		if ((!hasPharmacode && !hasEan) || quantity < 1) {
			String description = artikel.getName();
			StringBuilder msg = new StringBuilder();
			msg.append(MessageFormat.format(Messages.RegiomedSender_ArticleNotConfigured,
					StringUtils.defaultString(description)));
			if (!hasPharmacode && !hasEan) {
				msg.append(Messages.RegiomedSender_NoPharmaNoEan);
			} else if (!hasPharmacode) {
				msg.append(Messages.RegiomedSender_NoPharma);
			} else if (!hasEan) {
				msg.append(Messages.RegiomedSender_NoEan);
			}
			if (quantity < 1) {
				msg.append(Messages.RegiomedSender_InvalidAmount);
			}
			msg.append(Messages.RegiomedSender_PleaseCorrect);
			SWTHelper.alert(Messages.RegiomedSender_BadArticleTitle, msg.toString());
			throw new XChangeException("Bad Article Config: Pharmacode: " + pharmacode + ", EAN: " + eanId //$NON-NLS-1$ //$NON-NLS-2$
					+ ", Name: " + description + ", Quantity: " + quantity); //$NON-NLS-1$ //$NON-NLS-2$
		}
		exportedEntries.add(item);
		counter++;
	}

	private void updateQuantities(RegiomedOrderResponse response) {
		if (response == null || response.getArticles() == null)
			return;
		for (IOrderEntry entry : exportedEntries) {
			IArticle art = entry.getArticle();
			String pCode = cleanId(ArticleUtil.getPharmaCode(art));
			String ean = cleanId(ArticleUtil.getEan(art));

			for (ArticleResult res : response.getArticles()) {
				String resPCode = String.valueOf(res.getPharmaCode());
				String resEan = String.valueOf(res.getEanID());
				boolean match = (StringUtils.isNotBlank(pCode) && pCode.equals(resPCode))
						|| (StringUtils.isNotBlank(ean) && ean.equals(resEan));
				if (match) {
					if (entry.getAmount() != res.getQuantity()) {
						log.info("Regiomed: Menge geändert für {} von {} auf {}", art.getLabel(), entry.getAmount(),
								res.getQuantity());
						entry.setAmount(res.getQuantity());
						CoreModelServiceHolder.get().save(entry);
					}
					break;
				}
			}
		}
	}

	private void applyReplacements(Map<String, String> replacements) {
		for (IOrderEntry entry : exportedEntries) {
			IArticle art = entry.getArticle();
			String pCode = cleanId(ArticleUtil.getPharmaCode(art));
			String ean = cleanId(ArticleUtil.getEan(art));
			String key = pCode + ":" + ean; //$NON-NLS-1$
			if (replacements.containsKey(key)) {
				String newIds = replacements.get(key);
				String[] parts = newIds.split(":"); //$NON-NLS-1$
				if (parts.length >= 1) {
					String newPharma = parts[0];
					String newEan = (parts.length > 1) ? parts[1] : null;
					Optional<IArticle> optArticle = Optional.empty();
					if (StringUtils.isNotBlank(newEan) && !"0".equals(newEan)) { //$NON-NLS-1$
						optArticle = findArticle(newEan);
					}
					if (optArticle.isEmpty() && StringUtils.isNotBlank(newPharma) && !"0".equals(newPharma)) { //$NON-NLS-1$
						String searchPharma = newPharma;
						if (newPharma.length() == 6 && StringUtils.isNumeric(newPharma)) {
							searchPharma = "0" + newPharma; //$NON-NLS-1$
						}
						optArticle = findArticle(searchPharma);
						if (optArticle.isEmpty() && !searchPharma.equals(newPharma)) {
							optArticle = findArticle(newPharma);
						}
					}
					if (optArticle.isPresent()) {
						IArticle newArticle = optArticle.get();
						log.info("Regiomed: Replacing article {} with alternative {}", art.getLabel(), //$NON-NLS-1$
								newArticle.getLabel());
						entry.setArticle(newArticle);
						CoreModelServiceHolder.get().save(entry);
					} else {
						log.warn("Regiomed: Alternative not found locally. EAN: {}, Pharma: {}", newEan, newPharma); //$NON-NLS-1$
						String errorMsg = MessageFormat.format(Messages.RegiomedSender_AlternativeNotFoundLocally,
								(StringUtils.isNotBlank(newEan) ? "EAN: " + newEan : "") + " / Pharma: " + newPharma); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						SWTHelper.showError(Messages.RegiomedSender_WarningTitle, errorMsg);
					}
				}
			}
		}
	}

	private void removeDeletedEntries(Set<String> deletedIds) {
		Iterator<IOrderEntry> it = exportedEntries.iterator();
		while (it.hasNext()) {
			IOrderEntry entry = it.next();
			IArticle art = entry.getArticle();
			String key = cleanId(ArticleUtil.getPharmaCode(art)) + ":" + cleanId(ArticleUtil.getEan(art)); //$NON-NLS-1$
			if (deletedIds.contains(key)) {
				it.remove();
				deleteSingleEntry(entry);
			}
		}
	}

	private void deleteSingleEntry(IOrderEntry entry) {
		try {
			IOrder order = entry.getOrder();
			if (order != null) {
				IOrderService orderService = OsgiServiceUtil.getService(IOrderService.class)
						.orElseThrow(() -> new IllegalStateException("no order service found")); //$NON-NLS-1$
				orderService.getHistoryService().logRemove(order, entry);
				order.getEntries().remove(entry);
				CoreModelServiceHolder.get().delete(entry);
			}
		} catch (Exception e) {
			log.error("Error deleting OrderEntry", e); //$NON-NLS-1$
		}
	}

	private void updateOrderEntriesStatus(List<IOrderEntry> entries, RegiomedOrderResponse response) {
		if (response == null || response.getArticles() == null) {
			for (IOrderEntry entry : entries) {
				entry.setState(OrderEntryState.ORDERED);
				CoreModelServiceHolder.get().save(entry);
			}
			return;
		}
		List<IOrderEntry> entriesToDelete = new ArrayList<>();
		for (IOrderEntry entry : entries) {
			boolean isSuccess = false;
			boolean foundInResponse = false;
			IArticle art = entry.getArticle();
			String pharmaStr = ArticleUtil.getPharmaCode(art);
			String eanStr = ArticleUtil.getEan(art);

			for (ArticleResult res : response.getArticles()) {
				if (StringUtils.isNotBlank(pharmaStr) && String.valueOf(res.getPharmaCode()).equals(pharmaStr)) {
					isSuccess = res.isSuccess();
					foundInResponse = true;
					break;
				}
				if (StringUtils.isNotBlank(eanStr) && String.valueOf(res.getEanID()).equals(eanStr)) {
					isSuccess = res.isSuccess();
					foundInResponse = true;
					break;
				}
			}
			if (isSuccess) {
				entry.setState(OrderEntryState.ORDERED);
				CoreModelServiceHolder.get().save(entry);
			} else if (foundInResponse && !isSuccess) {
				entriesToDelete.add(entry);
			}
		}
		for (IOrderEntry toDelete : entriesToDelete) {
			deleteSingleEntry(toDelete);
		}
	}

	private Optional<IArticle> findArticle(String scanCode) {
		if (StringUtils.isBlank(scanCode) || "0".equals(scanCode)) { //$NON-NLS-1$
			return Optional.empty();
		}
		List<ICodeElementServiceContribution> contributions = CodeElementServiceHolder.get()
				.getContributionsByTyp(CodeElementTyp.ARTICLE);
		for (ICodeElementServiceContribution contribution : contributions) {
			Optional<ICodeElement> loadFromCode = contribution.loadFromCode(scanCode);
			if (loadFromCode.isPresent()) {
				if (loadFromCode.get() instanceof IArticle) {
					return loadFromCode.map(IArticle.class::cast);
				}
			}
		}
		return Optional.empty();
	}

	private String cleanId(String id) {
		if (StringUtils.isBlank(id))
			return "0"; //$NON-NLS-1$
		return id.replaceFirst("^0+(?!$)", StringUtils.EMPTY); //$NON-NLS-1$
	}

	@Override
	public boolean canHandle(Identifiable identifiable) {
		if (!(identifiable instanceof IOrder order)) {
			return false;
		}
		String cfg = ConfigServiceHolder.getGlobal(RegiomedConstants.CFG_REGIOMED_SUPPLIER, StringUtils.EMPTY);
		if (StringUtils.isBlank(cfg)) {
			return false;
		}
		String[] supplierIds = StringUtils.split(cfg, ',');
		for (String supplierId : supplierIds) {
			IContact supplier = CoreModelServiceHolder.get().load(supplierId, IContact.class).orElse(null);
			if (supplier != null && OrderServiceHolder.get().containsSupplier(order, supplier)) {
				return true;
			}
		}
		return false;
	}
}