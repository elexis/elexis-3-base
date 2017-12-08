package ch.elexis.docbox.ws.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import ch.elexis.core.data.activator.CoreHub;

public class WsClientConfig {
	
	public static String TESTLOGINIDPREFIX = "TEST_";

	public static final String USR_DEFDOCBXLOGINID = "docbox/loginid"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXPASSWORD = "docbox/password"; //$NON-NLS-1$

	public static final String USR_SECRETKEY = "docbox/secretkey"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXP12PATH = "docbox/p12path"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXP12PASSWORD = "docbox/p12password"; //$NON-NLS-1$

	public static String getUsername(){
		if (CoreHub.mandantCfg != null) {
			return getDocboxLoginID(false);
		}
		return "";
	}
	
	public static String getPassword(){
		if (CoreHub.mandantCfg != null) {
			return getSha1DocboxPassword();
		}
		return "";
	}
	
	public static String getSecretkey(){
		if (CoreHub.mandantCfg != null) {
			return getSha1DocboxSecretKey();
		}
		return "";
	}

	public static String getP12Path() {
		if (CoreHub.mandantCfg != null) {
			return CoreHub.mandantCfg.get(USR_DEFDOCBOXP12PATH, "");
		}
		return "";
	}
	
	public static String getP12Password(){
		if (CoreHub.mandantCfg != null) {
			return CoreHub.mandantCfg.get(USR_DEFDOCBOXP12PASSWORD, "");
		}
		return "";
	}
	
	public static String getDocboxServiceUrl(){
		String test = isDocboxTest() ? "test" : ""; //$NON-NLS-1$ //$NON-NLS-2$
		String host = getHost();
		return "https://" + host + "/cgi-bin/WebObjects/docboxservice" + test + ".woa/ws/CDACHServicesV2"; //$NON-NLS-1$//$NON-NLS-2$
	}
	
	private static String getHost(){
		String host = "";
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
	private static String getSha1DocboxSecretKey(){
		String docboxSha1SecretKey = "";
		if (isDocboxTest()) {
			return WsClientUtil.getSHA1("docboxtest");
		}
		URL baseUrl = ch.docbox.ws.cdachservicesv2.CDACHServicesV2_Service.class.getResource("");
		try {
			URL url = new URL(baseUrl + "/product.key");
			InputStream in = url.openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			docboxSha1SecretKey = bufferedReader.readLine();
		} catch (Exception e) {
			docboxSha1SecretKey = WsClientUtil.getSHA1(CoreHub.mandantCfg.get(USR_SECRETKEY, ""));
		}
		return docboxSha1SecretKey;
	}
	
	private static boolean isDocboxTest(){
		return getDocboxLoginID(true) != null && getDocboxLoginID(true).startsWith("TEST_");
	}
	
	private static String getDocboxLoginID(boolean prefixed){
		String loginId = CoreHub.mandantCfg.get(USR_DEFDOCBXLOGINID, "");//$NON-NLS-1$
		if (!prefixed && loginId.startsWith(TESTLOGINIDPREFIX)) {
			loginId = loginId.substring(TESTLOGINIDPREFIX.length());
		}
		return loginId;
	}
	
	public static String getSha1DocboxPassword(){
		String sha1Password = CoreHub.mandantCfg.get(USR_DEFDOCBOXPASSWORD, "");//$NON-NLS-1$
		return sha1Password;
	}
}
