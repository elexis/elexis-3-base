/*******************************************************************************
 * Copyright (c) 2010-2011, Medelexis AG
 * All rights reserved.
 *******************************************************************************/

package org.iatrix.bestellung.rose;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrder;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.exchange.ArticleUtil;
import ch.elexis.core.ui.exchange.IDataSender;
import ch.elexis.core.ui.exchange.XChangeException;
import ch.elexis.core.ui.exchange.elements.XChangeElement;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.BestellView;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class Sender implements IDataSender {
	private static final String XML_NEWLINE = "\r\n";
	
	private static final String ORDER_URL = "http://xml.estudio.zur-rose.hin.ch/orderXML/";
	private static final String ENCODING = "UTF-8";
	
	private static final String DEFAULT_ASAS_PROXY_HOST = "localhost";
	private static final String DEFAULT_ASAS_PROXY_PORT = "5016";
	
	protected Log log = Log.get("iatrix-bestellung-rose");
	
	private final List<String> orderRequests = new ArrayList<String>();
	
	private int counter;
	
	@Override
	public boolean canHandle(Class<? extends PersistentObject> clazz){
		if (clazz.equals(ch.elexis.data.Bestellung.class)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void finalizeExport() throws XChangeException{
		if (counter == 0) {
			log.log("Order contains no articles to order from Rose supplier", Log.INFOS);
			return;
		}
		
		// get proxy settings and store old values
		Properties systemSettings = System.getProperties();
		String oldProxyHost = systemSettings.getProperty("http.proxyHost");
		String oldProxyPort = systemSettings.getProperty("http.proxyPort");
		
		// set new values
		systemSettings.put("http.proxyHost",
			ConfigServiceHolder.getGlobal(Constants.CFG_ASAS_PROXY_HOST, DEFAULT_ASAS_PROXY_HOST));
		systemSettings.put("http.proxyPort",
			ConfigServiceHolder.getGlobal(Constants.CFG_ASAS_PROXY_PORT, DEFAULT_ASAS_PROXY_PORT));
		System.setProperties(systemSettings);
		
		try {
			for (String orderRequest : orderRequests) {
				System.out.println(orderRequest);
				
				try {
					String postString = "order=" + URLEncoder.encode(orderRequest, ENCODING);
					
					URL serverURL = new URL(ORDER_URL);
					HttpURLConnection httpConnection =
						(HttpURLConnection) serverURL.openConnection();
					httpConnection.setRequestMethod("POST");
					HttpURLConnection.setFollowRedirects(true);
					httpConnection.setDoInput(true);
					httpConnection.setDoOutput(true);
					httpConnection.setUseCaches(false);
					PrintWriter out = new PrintWriter(httpConnection.getOutputStream());
					out.println(postString);
					out.close();
					
					BufferedReader bufferedReader =
						new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
					String line;
					StringBuffer responseMessage = new StringBuffer();
					while ((line = bufferedReader.readLine()) != null) {
						responseMessage.append(line);
					}
					bufferedReader.close();
					System.err.println("prescription response: " + responseMessage.toString());
					
					// TODO parse message for errors
				} catch (Exception ex) {
					ExHandler.handle(ex);
					SWTHelper.alert("Fehler bei Übermittlung",
						"Die Bestellung konnte nicht gesendet werden. Bitte überprüfen Sie den Zustand in eStudio.");
					throw new XChangeException(
						"Die Bestellung konnte nicht gesendet werden. Bitte überprüfen Sie den Zustand in eStudio.");
				}
				/*
				 * <?xml version="1.0" encoding="ISO-8859-1"?><order-response
				 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" error-code="0"
				 * xsi:type="java:ch.clustertec.estudio.order.OrderResponse"
				 * ><error-message>ok</error-message></order-response>
				 */
			}
		} finally {
			// restore old proxy settings
			if (oldProxyHost != null || oldProxyPort != null) {
				systemSettings = System.getProperties();
				if (oldProxyHost != null) {
					systemSettings.put("http.proxyHost", oldProxyHost);
				}
				if (oldProxyPort != null) {
					systemSettings.put("http.proxyPort", oldProxyPort);
				}
				System.setProperties(systemSettings);
			}
		}
		
	}
	
	@Override
	public XChangeElement store(Object output) throws XChangeException{
		if (output instanceof IOrder) {
			IOrder order = (IOrder) output;
			return addOrder(order);
		} else {
			// should never happen...
			throw new XChangeException(
				"Can't handle object of class " + output.getClass().getName());
		}
	}
	
	private String escapeXmlAttribute(String value){
		String escaped = value;
		escaped = escaped.replaceAll("&", "&amp;");
		escaped = escaped.replaceAll("\"", "&qt;");
		escaped = escaped.replaceAll("<", "&lt;");
		escaped = escaped.replaceAll(">", "&gt;");
		
		return escaped;
	}
	
	/**
	 * Add order to be submitted
	 * 
	 * @param order
	 *            the order to submit
	 * @return ok on success, error if order is null or order doesn't contain any items or items are
	 *         not valid
	 */
	private XChangeElement addOrder(IOrder order) throws XChangeException{
		counter = 0;
		if (order == null) {
			// order must not be null
			throw new XChangeException("Die Bestellung ist leer.");
		}
		
		List<IOrderEntry> items = order.getEntries();
		if (items == null || items.size() == 0) {
			// no items
			throw new XChangeException("Die Bestellung ist leer.");
		}
		
		String clientNrRose = ConfigServiceHolder
			.getGlobal(Constants.CFG_ROSE_CLIENT_NUMBER, Constants.DEFAULT_ROSE_CLIENT_NUMBER)
			.trim();
		
		if (StringTool.isNothing(clientNrRose)) {
			throw new XChangeException(
				"Kundennummer ist nicht konfiguriert. (Einstellungen/Datenaustausch/zur Rose (Bestellungen))");
		}
		
		String supplier = ConfigServiceHolder.getGlobal(Constants.CFG_ROSE_SUPPLIER, null);
		String selDialogTitle = "Kein 'Zur Rose' Lieferant definiert";
		IContact roseSupplier = BestellView.resolveDefaultSupplier(supplier, selDialogTitle);
		if (roseSupplier == null) {
			return null;
		}
		

		/*
		 * Example XML:
		 * 
		 * <?xml version="1.0" encoding="UTF-8"?> <order
		 * xmlns="http://estudio.clustertec.ch/schemas/order"
		 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation=
		 * "http://estudio.clustertec.ch/schemas/order http://estudio.clustertec.ch/schemas/order/order.xsd"
		 * user="test" password="test" deliveryType="1"> <product pharmacode="1234567"
		 * eanId="7600000000000" description="ASPIRIN" quantity="1" positionType="1"/> </order>
		 */
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<?xml version=\"1.0\" encoding=\"" + ENCODING + " \"?>" + XML_NEWLINE);
		sb.append("<order" + " xmlns=\"http://estudio.clustertec.ch/schemas/order\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"http://estudio.clustertec.ch/schemas/order http://estudio.clustertec.ch/schemas/order/order.xsd\""
			+ " clientNrRose=\"" + escapeXmlAttribute(clientNrRose) + "\""
			+ " user=\"elexis\" password=\"elexis\"" // must be present or we will have an error
			+ " deliveryType=\"1\"" + ">" + XML_NEWLINE);
		
		for (IOrderEntry item : items) {
			IArticle artikel = item.getArticle();
			IContact artSupplier = item.getProvider();
			// only add zurRose line items
			if (roseSupplier.equals(artSupplier)) {
				String pharmacode = ArticleUtil.getPharmaCode(artikel);
				String eanId = ArticleUtil.getEan(artikel);
				String description = artikel.getName();
				int quantity = item.getAmount();
				
				if (StringTool.isNothing(pharmacode) || StringTool.isNothing(eanId)
					|| StringTool.isNothing(description) || quantity < 1) {
					
					StringBuffer msg = new StringBuffer();
					msg.append("Der Artikel " + PersistentObject.checkNull(description)
						+ " (Pharma-Code " + PersistentObject.checkNull(pharmacode)
						+ ") ist nicht korrekt konfiguriert: ");
					if (StringTool.isNothing(pharmacode)) {
						msg.append("Ungültiger Pharmacode. ");
					}
					if (StringTool.isNothing(eanId)) {
						msg.append("Ungültiger EAN-Code. ");
					}
					if (quantity < 1) {
						msg.append("Ungültige Anzahl. ");
					}
					msg.append("Bitte korrigieren Sie diese Fehler.");
					
					SWTHelper.alert("Fehlerhafter Artikel", msg.toString());
					
					throw new XChangeException("Fehlerhafter Artikel: Pharamcode: " + pharmacode
						+ ", EAN: " + eanId + ", Name: " + description + ", Anzahl: " + quantity);
				}
				
				sb.append("<product" + " pharmacode=\"" + escapeXmlAttribute(pharmacode) + "\""
					+ " eanId=\"" + escapeXmlAttribute(eanId) + "\"" + " description=\""
					+ escapeXmlAttribute(description) + "\"" + " quantity=\"" + quantity + "\""
					+ " positionType=\"1\"" + "/>" + XML_NEWLINE);
				counter++;
			}
		}
		sb.append("</order>" + XML_NEWLINE);
		
		orderRequests.add(sb.toString());
		return null;
	}
}
