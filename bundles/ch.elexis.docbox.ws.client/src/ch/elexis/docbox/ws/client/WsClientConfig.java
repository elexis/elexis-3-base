package ch.elexis.docbox.ws.client;

import org.apache.commons.lang3.StringUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class WsClientConfig {

	public static String TESTLOGINIDPREFIX = "TEST_";

	public static final String USR_DEFDOCBXLOGINID = "docbox/loginid"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXPASSWORD = "docbox/password"; //$NON-NLS-1$

	public static final String USR_SECRETKEY = "docbox/secretkey"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXP12PATH = "docbox/p12path"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXP12PASSWORD = "docbox/p12password"; //$NON-NLS-1$

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

	public static String getSecretkey() {
		if (ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return getSha1DocboxSecretKey();
		}
		return StringUtils.EMPTY;
	}

	public static String getP12Path() {
		if (ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return ConfigServiceHolder.getMandator(USR_DEFDOCBOXP12PATH, StringUtils.EMPTY);
		}
		return StringUtils.EMPTY;
	}

	public static String getP12Password() {
		if (ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return ConfigServiceHolder.getMandator(USR_DEFDOCBOXP12PASSWORD, StringUtils.EMPTY);
		}
		return StringUtils.EMPTY;
	}

	public static String getDocboxServiceUrl() {
		String test = isDocboxTest() ? "test" : StringUtils.EMPTY; //$NON-NLS-1$
		String host = getHost();
		return "https://" + host + "/cgi-bin/WebObjects/docboxservice" + test + ".woa/ws/CDACHServicesV2"; //$NON-NLS-1$//$NON-NLS-2$
	}

	private static String getHost() {
		String host = StringUtils.EMPTY;
		if (isDocboxTest()) {
			host = "ihe.test.docbox.ch"; //$NON-NLS-1$
		} else {
			host = "ihe.docbox.ch"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return host;
	}

	/**
	 * if loginID is prefix with TEST_ we use the test system
	 *
	 * @param loginID
	 * @return
	 */
	private static String getSha1DocboxSecretKey() {
		String docboxSha1SecretKey = StringUtils.EMPTY;
		if (isDocboxTest()) {
			return WsClientUtil.getSHA1("docboxtest");
		}
		URL baseUrl = ch.docbox.ws.cdachservicesv2.CDACHServicesV2_Service.class.getResource(StringUtils.EMPTY);
		try {
			URL url = new URL(baseUrl + "/product.key");
			InputStream in = url.openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			docboxSha1SecretKey = bufferedReader.readLine();
		} catch (Exception e) {
			docboxSha1SecretKey = WsClientUtil
					.getSHA1(ConfigServiceHolder.getMandator(USR_SECRETKEY, StringUtils.EMPTY));
		}
		return docboxSha1SecretKey;
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
}
