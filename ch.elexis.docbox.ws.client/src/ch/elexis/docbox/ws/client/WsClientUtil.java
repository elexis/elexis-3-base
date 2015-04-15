package ch.elexis.docbox.ws.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
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

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.docbox.ws.cdachservicesv2.CDACHServicesV2;

public class WsClientUtil {
	
	private static Logger logger = LoggerFactory.getLogger(WsClientUtil.class);

	private static char[] hex = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};
	
	public final static String toHex(byte[] v){
		String out = "";
		for (int i = 0; i < v.length; i++)
			out = out + hex[(v[i] >> 4) & 0xF] + hex[v[i] & 0xF];
		return (out);
	}

	public static String getSHA1(String password){
		if (password == null || "".equals(password)) {
			return "";
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(password.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] digest = md.digest();
		return toHex(digest);
	}

	public static boolean checkAccess(CDACHServicesV2 port){
		{
			javax.xml.ws.Holder<java.lang.Boolean> _checkAccess_success =
				new javax.xml.ws.Holder<java.lang.Boolean>();
			javax.xml.ws.Holder<java.lang.String> _checkAccess_message =
				new javax.xml.ws.Holder<java.lang.String>();
			port.checkAccess(_checkAccess_success, _checkAccess_message);
			
			logger.debug("checkAccess._checkAccess_success=" + _checkAccess_success.value);
			logger.debug("checkAccess._checkAccess_message=" + _checkAccess_message.value);
			
			return _checkAccess_success.value;
		}
	}
	
	public static void addWsSecurityAndHttpConfigWithClientCert(Service ss, final String username,
		final String password, final String p12, final String jks, final String passwordP12,
		final String passwordJks){
		
		String url = WsClientConfig.getDocboxServiceUrl();
		final boolean clientcert = url.contains("ihe");
		
		ss.setHandlerResolver(new HandlerResolver() {
			@SuppressWarnings("rawtypes")
			public List<Handler> getHandlerChain(PortInfo portInfo){
				List<Handler> handlerList = new ArrayList<Handler>();
				handlerList.add(new SecurityHandler(username, password, clientcert, p12, jks,
					passwordP12, passwordJks));
				return handlerList;
			}
		});
	}

	public static boolean isMedelexisCertAvailable(){
		InputStream keyInputStream = null;
		InputStream certInputStream = null;
		try {
			certInputStream =
				WsClientUtil.class.getResourceAsStream("/cert/MedElexis_MedElexis.p12");
			keyInputStream = WsClientUtil.class.getResourceAsStream("/cert/cert.key");
			return certInputStream != null && keyInputStream != null;
		} finally {
			if (keyInputStream != null) {
				try {
					keyInputStream.close();
				} catch (IOException e) {
					// ignore
				}
			}
			if (certInputStream != null) {
				try {
					certInputStream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public static class SecurityHandler implements SOAPHandler<SOAPMessageContext> {
		private String username;
		private String password;
		private boolean clientcert;
		private String p12;
		private String jks;
		private String passwordP12;
		private String passwordJks;
		
		public SecurityHandler(String username, String password, boolean clientcert, String p12,
			String jks, String passwordP12, String passwordJks){
			this.username = username;
			this.password = password;
			this.clientcert = clientcert;
			this.p12 = p12;
			this.jks = jks;
			this.passwordP12 = passwordP12;
			this.passwordJks = passwordJks;
		}
		
		public Set<QName> getHeaders(){
			return new TreeSet<QName>();
		}
		
		public boolean handleMessage(SOAPMessageContext context){
			Boolean outboundProperty =
				(Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				
				if (clientcert) {
					InputStream certInput = null;
					try {
						
						String trustpass = passwordJks;
						
						TrustManager[] tm = null;
						if (jks != null) {
							KeyStore keyTrustStore = KeyStore.getInstance("JKS");
							File truststore = new File(jks);
							keyTrustStore.load(new FileInputStream(truststore),
								trustpass.toCharArray());
							TrustManagerFactory trustFactory =
								TrustManagerFactory.getInstance(TrustManagerFactory
									.getDefaultAlgorithm());
							trustFactory.init(keyTrustStore);
							tm = trustFactory.getTrustManagers();
						}
						
						KeyStore keyStore = KeyStore.getInstance("PKCS12");
						char[] certPass = getCertPass();
						certInput = getCertInputStream();
						keyStore.load(certInput, certPass);
						KeyManagerFactory keyFactory =
							KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
						keyFactory.init(keyStore, certPass);
						KeyManager[] km = keyFactory.getKeyManagers();
						
						SSLContext sslContext = SSLContext.getInstance("TLS");
						sslContext.init(km, tm, null);
						HttpsURLConnection
							.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
					} catch (KeyStoreException kse) {
						logger.error("Security configuration failed with the following: "
							+ kse.getCause());
					} catch (KeyManagementException kse) {
						logger.error("Security configuration failed with the following: "
							+ kse.getCause());
					} catch (NoSuchAlgorithmException nsa) {
						logger.error("Security configuration failed with the following: "
							+ nsa.getCause());
					} catch (FileNotFoundException fnfe) {
						logger.error("Security configuration failed with the following: "
							+ fnfe.getCause());
					} catch (UnrecoverableKeyException uke) {
						logger.error("Security configuration failed with the following: "
							+ uke.getCause());
					} catch (CertificateException ce) {
						logger.error("Security configuration failed with the following: "
							+ ce.getCause());
					} catch (IOException ioe) {
						logger.error("Security configuration failed with the following: "
							+ ioe.getCause());
					} finally {
						if (certInput != null) {
							try {
								certInput.close();
							} catch (IOException e) {
								// ignore
							}
						}
					}
				}
				
				try {
					SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
					SOAPFactory factory = SOAPFactory.newInstance();
					String prefix = "wsse";
					String uri =
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
					SOAPElement securityElem = factory.createElement("Security", prefix, uri);
					SOAPElement usernameTokenEl =
						factory.createElement("UsernameToken", prefix, uri);
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
		
		private InputStream getCertInputStream(){
			// look for cert from fragment
			InputStream certInputStream =
				WsClientUtil.class.getResourceAsStream("/cert/MedElexis_MedElexis.p12");
			if (certInputStream != null) {
				logger.info("Using fragment Cert.");
				return certInputStream;
			}
			// look for configured cert
			try {
				File keystore = new File(p12);
				logger.info("Using configured Cert.");
				return new FileInputStream(keystore);
			} catch (FileNotFoundException e) {
				logger.warn("Could not load cert." + e);
			}
			return null;
		}
		
		private char[] getCertPass(){
			// look for pass from fragment
			InputStream keyInputStream = WsClientUtil.class.getResourceAsStream("/cert/cert.key");
			if (keyInputStream != null) {
				char[] chars = new char[1024];
				InputStreamReader reader = new InputStreamReader(keyInputStream);
				try {
					int len = reader.read(chars);
					byte[] decoded = Base64.decodeBase64(new String(chars, 0, len));
					return new String(decoded, "UTF-8").toCharArray();
				} catch (IOException e) {
					logger.warn("Could not load cert." + e);
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
						if (keyInputStream != null) {
							keyInputStream.close();
						}
					} catch (IOException e) {
						// ignore
					}
				}
			}
			return passwordP12.toCharArray();
		}

		public boolean handleFault(SOAPMessageContext context){
			return true;
		}
		
		public void close(MessageContext context){
			//
		}
	}
}
