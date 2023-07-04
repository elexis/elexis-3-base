package ch.elexis.docbox.ws.client;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class WsClientConfig {

	public static String TESTLOGINIDPREFIX = "TEST_";

	public static final String USR_DEFDOCBXLOGINID = "docbox/loginid"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXPASSWORD = "docbox/password"; //$NON-NLS-1$

	public static final String USR_SECRETKEY = "docbox/secretkey"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXP12PATH = "docbox/p12path"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXP12PASSWORD = "docbox/p12password"; //$NON-NLS-1$

	private static Properties basicAuthProperties;

	public static String getUsername() {
		if (ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return getDocboxLoginID(false);
		}
		return StringUtils.EMPTY;
	}

	public static String getPassword() {
		if (ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return getSha1DocboxPassword();
		}
		return StringUtils.EMPTY;
	}

	public static String getDocboxServiceUrl() {
		String host = getHost();
		return "https://" + host + "/CDACHServicesV2"; //$NON-NLS-1$//$NON-NLS-2$
	}

	private static String getHost() {
		String host = StringUtils.EMPTY;
		if (isDocboxTest()) {
			host = "soap.test.docbox.swiss"; //$NON-NLS-1$
		} else {
			host = "soap.docbox.swiss"; //$NON-NLS-1$
		}
		return host;
	}

	private static boolean isDocboxTest() {
		return getDocboxLoginID(true) != null && getDocboxLoginID(true).startsWith("TEST_");
	}

	private static String getDocboxLoginID(boolean prefixed) {
		String loginId = ConfigServiceHolder.getMandator(USR_DEFDOCBXLOGINID, StringUtils.EMPTY);
		if (!prefixed && loginId.startsWith(TESTLOGINIDPREFIX)) {
			loginId = loginId.substring(TESTLOGINIDPREFIX.length());
		}
		return loginId;
	}

	public static String getSha1DocboxPassword() {
		String sha1Password = ConfigServiceHolder.getMandator(USR_DEFDOCBOXPASSWORD, StringUtils.EMPTY);
		return sha1Password;
	}

	public static String getDocboxBasicAuthUser() {
		return getBasicAuthProperties().getProperty("user");
	}

	private static synchronized Properties getBasicAuthProperties() {
		if (basicAuthProperties == null) {
			basicAuthProperties = new Properties();
			try {
				basicAuthProperties.load(WsClientUtil.getBasicAuthInputStream());
			} catch (IOException e) {
				LoggerFactory.getLogger(WsClientConfig.class).error("Error loading basic auth properties");
			}
		}
		return basicAuthProperties;
	}

	public static String getDocboxBasicAuthPass() {
		return getBasicAuthProperties().getProperty("pass");
	}
}
