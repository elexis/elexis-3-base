package ch.elexis.docbox.ws.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.docbox.ws.cdachservicesv2.CDACHServicesV2;

public class WsClientUtil {

	private static Logger logger = LoggerFactory.getLogger(WsClientUtil.class);

	private static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public final static String toHex(byte[] v) {
		String out = StringUtils.EMPTY;
		for (int i = 0; i < v.length; i++)
			out = out + hex[(v[i] >> 4) & 0xF] + hex[v[i] & 0xF];
		return (out);
	}

	public static String getSHA1(String password) {
		if (password == null || StringUtils.EMPTY.equals(password)) {
			return StringUtils.EMPTY;
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(password.getBytes("UTF-8"));
			byte[] digest = md.digest();
			return toHex(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.error("Error", e);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("Error", e);
		}
		return StringUtils.EMPTY;
	}

	public static boolean checkAccess(CDACHServicesV2 port) {
		{
			javax.xml.ws.Holder<java.lang.Boolean> _checkAccess_success = new javax.xml.ws.Holder<java.lang.Boolean>();
			javax.xml.ws.Holder<java.lang.String> _checkAccess_message = new javax.xml.ws.Holder<java.lang.String>();
			port.checkAccess(_checkAccess_success, _checkAccess_message);

			logger.debug("checkAccess._checkAccess_success=" + _checkAccess_success.value);
			logger.debug("checkAccess._checkAccess_message=" + _checkAccess_message.value);

			return _checkAccess_success.value;
		}
	}

	public static void addWsSecurityAndHttpConfigWithClientCert(Service ss, final String username,
			final String password) {

		ss.setHandlerResolver(new HandlerResolver() {
			@SuppressWarnings("rawtypes")
			public List<Handler> getHandlerChain(PortInfo portInfo) {
				List<Handler> handlerList = new ArrayList<Handler>();
				handlerList.add(new SecurityHandler(username, password));
				return handlerList;
			}
		});
	}

	public static boolean isMedelexisBasicAuthAvailable() {
		InputStream basicAuthInputStream = null;
		try {
			basicAuthInputStream = WsClientUtil.class.getResourceAsStream("/cert/basicauth.properties");
			return basicAuthInputStream != null;
		} finally {
			if (basicAuthInputStream != null) {
				try {
					basicAuthInputStream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public static InputStream getBasicAuthInputStream() {
		// look for cert from fragment
		InputStream certInputStream = WsClientUtil.class.getResourceAsStream("/cert/basicauth.properties");
		if (certInputStream != null) {
			logger.info("Using fragment basic auth.");
			return certInputStream;
		}
		return null;
	}

	public static class SecurityHandler implements SOAPHandler<SOAPMessageContext> {
		private String username;
		private String password;

		public SecurityHandler(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public Set<QName> getHeaders() {
			return new TreeSet<QName>();
		}

		public boolean handleMessage(SOAPMessageContext context) {
			Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				try {
					SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
					SOAPFactory factory = SOAPFactory.newInstance();
					String prefix = "wsse";
					String uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
					SOAPElement securityElem = factory.createElement("Security", prefix, uri);
					SOAPElement usernameTokenEl = factory.createElement("UsernameToken", prefix, uri);
					SOAPElement usernameEl = factory.createElement("Username", prefix, uri);
					SOAPElement passwordEl = factory.createElement("Password", prefix, uri);
					usernameEl.setTextContent(username);
					passwordEl.setTextContent(password);
					usernameTokenEl.addChildElement(usernameEl);
					usernameTokenEl.addChildElement(passwordEl);
					securityElem.addChildElement(usernameTokenEl);
					SOAPHeader header = envelope.getHeader();
					if (header == null) {
						header = envelope.addHeader();
					}
					header.addChildElement(securityElem);
				} catch (Exception e) {
					logger.error("Exception in handler: " + e);
				}
			} else {
				// inbound
			}
			return true;
		}

		public boolean handleFault(SOAPMessageContext context) {
			return true;
		}

		public void close(MessageContext context) {
			//
		}
	}
}
