package org.iatrix.bestellung.rose.service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.iatrix.bestellung.rose.AdditionalClientNumber;
import org.iatrix.bestellung.rose.AdditionalClientNumberSelectorDialog;
import org.iatrix.bestellung.rose.Constants; // ggf. eigener Constants-Helfer
import org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrder;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.model.OrderEntryState;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.exchange.ArticleUtil;
import ch.elexis.core.ui.exchange.XChangeException;
import ch.elexis.core.ui.views.BestellView;
import gs1.ecom.ecom_common.xsd.EcomEntityIdentificationType;
import gs1.ecom.ecom_common.xsd.OrderLogisticalInformationType;
import gs1.ecom.ecom_common.xsd.OrderTypeCodeType;
import gs1.ecom.ecom_common.xsd.TransactionalPartyType;
import gs1.ecom.ecom_common.xsd.TransactionalTradeItemType;
import gs1.ecom.order.xsd.ObjectFactory;
import gs1.ecom.order.xsd.OrderLineItemType;
import gs1.ecom.order.xsd.OrderMessageType;
import gs1.ecom.order.xsd.OrderType;
import gs1.shared.shared_common.xsd.AdditionalPartyIdentificationType;
import gs1.shared.shared_common.xsd.AdditionalTradeItemIdentificationType;
import gs1.shared.shared_common.xsd.Description1000Type;
import gs1.shared.shared_common.xsd.DocumentStatusEnumerationType;
import gs1.shared.shared_common.xsd.ExtensionType;
import gs1.shared.shared_common.xsd.QuantityType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;

public class Gs1OrderXmlGenerator {

	private List<IOrderEntry> exportedEntries = new ArrayList<>();
	private boolean forceRandomId = false;

	public void setForceRandomId(boolean forceRandom) {
		this.forceRandomId = forceRandom;
	}

	public String createOrderXml(IOrder order) throws XChangeException {
		if (order == null || order.getEntries().isEmpty()) {
			throw new XChangeException("The order is empty."); //$NON-NLS-1$
		}
		exportedEntries.clear();
		String supplier = ConfigServiceHolder.getGlobal(Constants.CFG_ROSE_SUPPLIER, null);
		String selDialogTitle = Messages.OrderSupplierNotDefined;
		IContact roseSupplier = BestellView.resolveDefaultSupplier(supplier, selDialogTitle);
		if (roseSupplier == null) {
			throw new XChangeException(Messages.OrderSupplierNotDefined);
		}

		try {
			ObjectFactory factory = new ObjectFactory();

			StandardBusinessDocumentHeader sbdh = new StandardBusinessDocumentHeader();
			sbdh.setHeaderVersion(Constants.HEADER_VERSION);

			Partner sender = new Partner();
			PartnerIdentification senderId = new PartnerIdentification();
			senderId.setAuthority(Constants.SENDER_AUTHORITY);
			senderId.setValue(Constants.SENDER_NAME);
			sender.setIdentifier(senderId);
			sbdh.getSender().add(sender);

			Partner receiver = new Partner();
			PartnerIdentification receiverId = new PartnerIdentification();
			receiverId.setAuthority(Constants.RECEIVER_AUTHORITY);
			receiverId.setValue(Constants.RECEIVER_NAME);
			receiver.setIdentifier(receiverId);
			sbdh.getReceiver().add(receiver);

			DocumentIdentification docId = new DocumentIdentification();
			docId.setStandard(Constants.STANDARD_GS1);
			docId.setTypeVersion(Constants.DOCUMENT_VERSION);
			docId.setInstanceIdentifier(order.getId());
			docId.setType(Constants.DOCUMENT_TYPE);
			docId.setMultipleType(false);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"); //$NON-NLS-1$
			String formattedDateTime = LocalDateTime.now().format(formatter);
			XMLGregorianCalendar creationTimestamp = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(formattedDateTime);

			docId.setCreationDateAndTime(creationTimestamp);
			sbdh.setDocumentIdentification(docId);

			OrderMessageType orderMessageType = factory.createOrderMessageType();
			orderMessageType.setStandardBusinessDocumentHeader(sbdh);

			OrderType orderType = factory.createOrderType();

			EcomEntityIdentificationType entityId = new EcomEntityIdentificationType();
			if (forceRandomId) {
				entityId.setEntityIdentification(UUID.randomUUID().toString());
				forceRandomId = false;
			} else {
				entityId.setEntityIdentification(UUID.nameUUIDFromBytes(order.getId().getBytes()).toString());
			}
			orderType.setOrderIdentification(entityId);

			OrderTypeCodeType orderTypeCode = new OrderTypeCodeType();
			orderTypeCode.setValue(Constants.ORDER_TYPE_CODE);
			orderType.setOrderTypeCode(orderTypeCode);
			orderType.setCreationDateTime(creationTimestamp);
			orderType.setDocumentStatusCode(DocumentStatusEnumerationType.ORIGINAL);
			ExtensionType extensionType = new ExtensionType();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Element originElement = doc.createElement(Constants.DOCUMENT_ELEMENT);
			originElement.setTextContent(Constants.SENDER_NAME);

			extensionType.getAny().add(originElement);

			orderType.setExtension(extensionType);

			Description1000Type additionalOrderInstruction = new Description1000Type();
			additionalOrderInstruction.setLanguageCode(Constants.LANGUAGE_CODE);
			additionalOrderInstruction.setValue(Constants.ADDITIONAL_ORDER_INSTRUCTION);
			orderType.getAdditionalOrderInstruction().add(additionalOrderInstruction);

			String buyerNo = getClientNumber();

			TransactionalPartyType buyer = new TransactionalPartyType();
			AdditionalPartyIdentificationType buyerAdditionalId = new AdditionalPartyIdentificationType();
			buyerAdditionalId.setValue(buyerNo);
			buyerAdditionalId.setAdditionalPartyIdentificationTypeCode(Constants.BUYER_ASSIGNED_ID);
			buyer.getAdditionalPartyIdentification().add(buyerAdditionalId);

			TransactionalPartyType seller = new TransactionalPartyType();
			AdditionalPartyIdentificationType sellerAdditionalId = new AdditionalPartyIdentificationType();
			sellerAdditionalId.setValue(Constants.SELLER_NUMBER);
			sellerAdditionalId.setAdditionalPartyIdentificationTypeCode(Constants.SELLER_ASSIGNED_ID);
			seller.getAdditionalPartyIdentification().add(sellerAdditionalId);

			orderType.setBuyer(buyer);
			orderType.setSeller(seller);

			OrderLogisticalInformationType logisticalInfo = new OrderLogisticalInformationType();
			orderType.setOrderLogisticalInformation(logisticalInfo);

			int lineNumber = 1;
			for (IOrderEntry entry : order.getEntries()) {
				if (entry.getState() != OrderEntryState.OPEN) {
					continue;
				}
				IContact artSupplier = entry.getProvider();

				if (!roseSupplier.equals(artSupplier)) {
					continue;
				}
				exportedEntries.add(entry);
				OrderLineItemType lineItem = factory.createOrderLineItemType();
				lineItem.setLineItemNumber(BigInteger.valueOf(lineNumber++));

				QuantityType requestedQuantity = new QuantityType();
				requestedQuantity.setValue(BigDecimal.valueOf(entry.getAmount()));
				lineItem.setRequestedQuantity(requestedQuantity);

				TransactionalTradeItemType tradeItem = new TransactionalTradeItemType();
				String gtin = entry.getArticle().getGtin();
				if (gtin.length() == 13) {
					tradeItem.setGtin("0" + gtin); //$NON-NLS-1$
				} else if (gtin.length() == 14) {
					tradeItem.setGtin(gtin);
				} else {
					throw new IllegalArgumentException("Invalid GTIN length: " + gtin.length()); //$NON-NLS-1$
				}

				if (entry.getArticle().getAtcCode() != null && !entry.getArticle().getAtcCode().isEmpty()) {
					String pharmacode = ArticleUtil.getPharmaCode(entry.getArticle());
					if (pharmacode.matches("\\d{7}")) { //$NON-NLS-1$
						AdditionalTradeItemIdentificationType additionalTradeId = new AdditionalTradeItemIdentificationType();
						additionalTradeId.setValue(pharmacode);
						additionalTradeId.setAdditionalTradeItemIdentificationTypeCode(Constants.PHARMACODE_CH);
						tradeItem.getAdditionalTradeItemIdentification().add(additionalTradeId);
					}
				}
				lineItem.setTransactionalTradeItem(tradeItem);
				orderType.getOrderLineItem().add(lineItem);
			}
			orderMessageType.getOrder().add(orderType);
			JAXBContext jaxbContext = JAXBContext.newInstance(OrderMessageType.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			JAXBElement<OrderMessageType> jaxbElement = factory.createOrderMessage(orderMessageType);
			StringWriter sw = new StringWriter();
			marshaller.marshal(jaxbElement, sw);
			return sw.toString();

		} catch (Exception e) {
			throw new XChangeException("Error when generating the order XML via JAXB: " + e.getMessage()); //$NON-NLS-1$
		}
	}

	private String getClientNumber() {
		String number = ConfigServiceHolder
				.getGlobal(Constants.CFG_ROSE_CLIENT_NUMBER, Constants.DEFAULT_ROSE_CLIENT_NUMBER).trim();
		if (AdditionalClientNumber.isConfigured()) {
			AdditionalClientNumberSelectorDialog dialog = new AdditionalClientNumberSelectorDialog(
					Display.getDefault().getActiveShell());
			if (dialog.open() == Dialog.OK) {
				Object[] dialogResult = dialog.getResult();
				if (dialogResult != null && dialogResult[0] instanceof AdditionalClientNumber) {
					number = ((AdditionalClientNumber) dialogResult[0]).getClientNumber();
				}
			}
		}
		return number;
	}

	public List<IOrderEntry> getExportedEntries() {
		return exportedEntries;
	}
}
