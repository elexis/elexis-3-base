package ch.elexis.regiomed.order.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrder;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.OrderEntryState;
import ch.elexis.core.services.IOrderService;
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
import ch.elexis.regiomed.order.model.RegiomedOrderRequest;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.ArticleResult;
import ch.elexis.regiomed.order.preferences.RegiomedConstants;
import ch.elexis.regiomed.order.ui.RegiomedCheckDialog;
import ch.rgw.tools.StringTool;

public class RegiomedSender implements IDataSender {

	private static final Logger log = LoggerFactory.getLogger(RegiomedSender.class);

	private final List<IOrderEntry> exportedEntries = new ArrayList<>();
	private int counter;
	private final RegiomedOrderClient orderClient = new RegiomedOrderClient();
	private static final String ABORT_BY_USER = "ABORT_BY_USER"; //$NON-NLS-1$

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

		String err = StringUtils.EMPTY;
		RegiomedOrderResponse finalResponse = null;

		try {
			RegiomedConfig config = RegiomedConfig.load();



			RegiomedOrderRequest request = RegiomedOrderRequest.fromEntries(config, exportedEntries);

			RegiomedOrderResponse response = orderClient.sendOrderWithToken(config, request);
			finalResponse = response;

			if (!request.isCheckOrder()) {
				if (!response.orderSent && !response.overallSuccess()) {
					err = response.buildErrorMessage();
					log.error("Regiomed Direct Order error: {}", err); //$NON-NLS-1$
					throw new XChangeException(err);
				}
			}

			if (request.isCheckOrder()) {
				RegiomedCheckDialog dialog = new RegiomedCheckDialog(
						org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), response);

				int result = dialog.open();
				if (result != org.eclipse.jface.window.Window.OK) {
					throw new XChangeException(ABORT_BY_USER);
				}
				Set<String> deletedIds = dialog.getRemovedIdentifiers();
				if (!deletedIds.isEmpty()) {

					removeDeletedEntries(deletedIds);

					if (exportedEntries.isEmpty()) {
						SWTHelper.showInfo(Messages.RegiomedSender_AbortTitle,
								Messages.RegiomedSender_AllArticlesDeleted);
						throw new XChangeException(ABORT_BY_USER);
					}
					request = RegiomedOrderRequest.fromEntries(config, exportedEntries);
				}

				request.setCheckOrder(false);
				response = orderClient.sendOrderWithToken(config, request);
				finalResponse = response;

				if (!response.orderSent && !response.overallSuccess()) {
					err = response.buildErrorMessage();
					log.error("Regiomed Final Order error: {}", err); //$NON-NLS-1$
					throw new XChangeException(err);
				}
			}

			if (finalResponse.articlesNOK > 0) {
				String msg = NLS.bind(Messages.RegiomedSender_OrderPartiallySentText,
						new Object[] { finalResponse.articlesOK, finalResponse.articlesNOK });

				SWTHelper.showInfo(Messages.RegiomedSender_OrderPartiallySentTitle, msg);
			} else {
				if (!CoreUtil.isTestMode()) {
				SWTHelper.showInfo(Messages.RegiomedSender_SuccessTitle, Messages.RegiomedSender_SuccessText);
				}
			}

			updateOrderEntriesStatus(exportedEntries, finalResponse);
			exportedEntries.clear();

		} catch (Exception ex) {
			if (ex instanceof XChangeException && ABORT_BY_USER.equals(ex.getMessage())) {
				throw (XChangeException) ex;
			}

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
	}

	private void removeDeletedEntries(Set<String> deletedIds) {
		Iterator<IOrderEntry> it = exportedEntries.iterator();
		while (it.hasNext()) {
			IOrderEntry entry = it.next();
			IArticle art = entry.getArticle();
			String pCode = ArticleUtil.getPharmaCode(art);
			if (StringUtils.isBlank(pCode)) {
				pCode = "0"; //$NON-NLS-1$
			} else {
				pCode = pCode.replaceFirst("^0+(?!$)", StringUtils.EMPTY); //$NON-NLS-1$
			}
			String ean = ArticleUtil.getEan(art);
			if (StringUtils.isBlank(ean)) {
				ean = "0"; //$NON-NLS-1$
			} else {
				ean = ean.replaceFirst("^0+(?!$)", StringUtils.EMPTY); //$NON-NLS-1$
			}

			String key = pCode + ":" + ean; //$NON-NLS-1$
			if (deletedIds.contains(key)) {
				it.remove();
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
		}
	}

	private void updateOrderEntriesStatus(List<IOrderEntry> entries, RegiomedOrderResponse response) {
		if (response == null || response.articles == null) {
			for (IOrderEntry entry : entries) {
				entry.setState(OrderEntryState.ORDERED);
				CoreModelServiceHolder.get().save(entry);
			}
			return;
		}

		for (IOrderEntry entry : entries) {
			boolean isSuccess = false;
			IArticle art = entry.getArticle();
			String pharmaStr = ArticleUtil.getPharmaCode(art);
			String eanStr = ArticleUtil.getEan(art);

			for (ArticleResult res : response.articles) {
				if (StringUtils.isNotBlank(pharmaStr) && String.valueOf(res.pharmaCode).equals(pharmaStr)) {
					isSuccess = res.success;
					break;
				}
				if (StringUtils.isNotBlank(eanStr) && String.valueOf(res.eanID).equals(eanStr)) {
					isSuccess = res.success;
					break;
				}
			}

			if (isSuccess) {
				entry.setState(OrderEntryState.ORDERED);
				CoreModelServiceHolder.get().save(entry);
			}
		}
	}

	private XChangeElement addOrder(IOrder order) throws XChangeException {
		counter = 0;
		exportedEntries.clear();

		if (order == null) {
			throw new XChangeException(Messages.RegiomedSender_OrderEmpty);
		}

		List<IOrderEntry> entries = order.getEntries();
		if (entries == null || entries.isEmpty()) {
			throw new XChangeException(Messages.RegiomedSender_OrderEmpty);
		}

		String supplierCfg = ConfigServiceHolder.getGlobal(RegiomedConstants.CFG_REGIOMED_SUPPLIER, StringUtils.EMPTY);
		String selDialogTitle = Messages.RegiomedSender_NoSupplierTitle;
		IContact regiomedSupplier = BestellView.resolveDefaultSupplier(supplierCfg, selDialogTitle);
		if (regiomedSupplier == null) {
			return null;
		}

		for (IOrderEntry item : entries) {
			IContact articleSupplier = item.getProvider();
			if (!regiomedSupplier.equals(articleSupplier)) {
				continue;
			}

			IArticle artikel = item.getArticle();
			String pharmacode = ArticleUtil.getPharmaCode(artikel);
			String eanId = ArticleUtil.getEan(artikel);
			String description = artikel.getName();
			int quantity = item.getAmount();

			boolean hasPharmacode = !StringTool.isNothing(pharmacode);
			boolean hasEan = !StringTool.isNothing(eanId);
			if ((!hasPharmacode && !hasEan) || quantity < 1) {
				StringBuilder msg = new StringBuilder();
				msg.append(NLS.bind(Messages.RegiomedSender_ArticleNotConfigured,
						PersistentObject.checkNull(description)));

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

		if (counter == 0) {
			throw new XChangeException(Messages.RegiomedSender_NoArticlesForSupplier);
		}

		return null;
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

//	@Override
//	public List<IContact> getSupplier() {
//		String contactId = ConfigServiceHolder.getGlobal(RegiomedConstants.CFG_REGIOMED_SUPPLIER, StringUtils.EMPTY);
//
//		if (StringUtils.isBlank(contactId)) {
//			return Collections.emptyList();
//		}
//		IContact supplier = CoreModelServiceHolder.get().load(contactId, IContact.class).orElse(null);
//
//		if (supplier != null) {
//			return Collections.singletonList(supplier);
//		}
//		return Collections.emptyList();
//	}
}