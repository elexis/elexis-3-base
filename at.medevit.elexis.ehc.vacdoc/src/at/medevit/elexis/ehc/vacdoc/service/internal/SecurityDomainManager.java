package at.medevit.elexis.ehc.vacdoc.service.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.openhealthtools.ihe.atna.nodeauth.NoSecurityDomainException;
import org.openhealthtools.ihe.atna.nodeauth.SecurityDomain;
import org.openhealthtools.ihe.atna.nodeauth.SecurityDomainException;
import org.openhealthtools.ihe.atna.nodeauth.context.NodeAuthModuleContext;

/**
 *
 * @author roeland
 * @version 1.0
 * @since Oct 7, 2015 11:01:19 AM
 *
 */
public class SecurityDomainManager {

	/**
	 * Default constructor to instanciate the object
	 */
	public SecurityDomainManager() {
	}

	public static void generateSecurityDomain(String aName, AtnaSecurityConfiguration aAtnaConfig)
			throws SecurityDomainException {

		// define the security stuff
		final Properties securityProps = new Properties();
		// fill keystore properties)
		securityProps.setProperty("javax.net.ssl.keyStore", aAtnaConfig.getKeyStoreFile().getPath());
		securityProps.setProperty("javax.net.ssl.keyStorePassword", aAtnaConfig.getKeyStorePassword());
		if ((aAtnaConfig.getKeyStoreType() != null) && (!"".equalsIgnoreCase(aAtnaConfig.getKeyStoreType()))) {
			securityProps.setProperty("javax.net.ssl.keyStoreType", aAtnaConfig.getKeyStoreType());
		}

		// fill truststore properties
		securityProps.setProperty("javax.net.ssl.trustStore", aAtnaConfig.getTrustStoreFile().getPath());
		securityProps.setProperty("javax.net.ssl.trustStorePassword", aAtnaConfig.getTrustStorePassword());
		if ((aAtnaConfig.getTrustStoreType() != null) && (!"".equalsIgnoreCase(aAtnaConfig.getTrustStoreType()))) {
			securityProps.setProperty("javax.net.ssl.trustStoreType", aAtnaConfig.getTrustStoreType());
		}

		// create the securit domain
		final SecurityDomain testAtnaSecurityDomain = new SecurityDomain(aName, securityProps);

		// register security domain
		NodeAuthModuleContext.getContext().getSecurityDomainManager().registerSecurityDomain(testAtnaSecurityDomain);
	}

	public static void addUriToSecurityDomain(String aName, URI aUri)
			throws NoSecurityDomainException, URISyntaxException {
		NodeAuthModuleContext.getContext().getSecurityDomainManager().registerURItoSecurityDomain(aUri, aName);
	}

}
