package org.iatrix.bestellung.rose;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.iatrix.bestellung.rose.service.Gs1OrderXmlGenerator;
import org.iatrix.bestellung.rose.service.HttpOrderTransportService;
import org.iatrix.bestellung.rose.service.XmlValidator;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IOrder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.exchange.IDataSender;
import ch.elexis.core.ui.exchange.XChangeException;
import ch.elexis.core.ui.exchange.elements.XChangeElement;
import ch.elexis.data.PersistentObject;

public class Sender implements IDataSender {

	private final Gs1OrderXmlGenerator xmlGenerator;
	private final XmlValidator xmlValidator;
	private final HttpOrderTransportService transportService;

	private final List<String> orderRequests = new ArrayList<>();

	public Sender() {
		this.xmlGenerator = new Gs1OrderXmlGenerator();
		this.xmlValidator = new XmlValidator();
		this.transportService = new HttpOrderTransportService();
	}

	public Sender(boolean isTestMode) {
		this();
		this.transportService.setTestMode(isTestMode);
	}

	@Override
	public XChangeElement store(Object output) throws XChangeException {
		if (output instanceof IOrder) {
			IOrder order = (IOrder) output;
			String xml = xmlGenerator.createOrderXml(order);
			xmlValidator.validateXml(xml);
			orderRequests.add(xml);
			return null;
		} else {
			throw new XChangeException("Can't handle object of class " + output.getClass().getName()); //$NON-NLS-1$
		}
	}

	@Override
	public boolean canHandle(Class<? extends PersistentObject> clazz) {
		return clazz.equals(ch.elexis.data.Bestellung.class);
	}

	public void finalizeExport() throws XChangeException {
		String clientId = getClientId();
		String secretId = getClientSecret();
		String token = transportService.retrieveAccessToken(clientId, secretId);
		for (String orderRequest : orderRequests) {
			transportService.sendOrderRequest(orderRequest, token);
		}
		orderRequests.clear();
	}

	private String getClientId() {
		try (InputStream properties = getClass().getResourceAsStream("/rsc/id.properties")) {
			if (properties != null) {
				Properties idProps = new Properties();
				idProps.load(properties);
				String secret = idProps.getProperty("client_id_prod");
				if (StringUtils.isNotEmpty(secret)) {
					return secret;
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error loading id properties", e);
		}
		String prefString = ConfigServiceHolder.getGlobal(Constants.CFG_ROSE_CLIENT_SECRET, null);
		return Objects.toString(prefString, StringUtils.EMPTY);
	}

	private String getClientSecret() {
		try (InputStream properties = getClass().getResourceAsStream("/rsc/id.properties")) {
			if (properties != null) {
				Properties idProps = new Properties();
				idProps.load(properties);
				String secret = idProps.getProperty("client_secret_prod");
				if (StringUtils.isNotEmpty(secret)) {
					return secret;
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error loading id properties", e);
		}
		String prefString = ConfigServiceHolder.getGlobal(Constants.CFG_ROSE_CLIENT_SECRET, null);
		return Objects.toString(prefString, StringUtils.EMPTY);
	}
}
