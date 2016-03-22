package at.medevit.elexis.ehc.vacdoc.service.internal;

import java.io.File;
import java.net.URI;

/**
 *
 * @author roeland
 * @version 1.0
 * @since Oct 7, 2015 3:17:45 PM
 *
 */
public class AtnaSecurityConfiguration {

	/** Key store password */
	private String mKeyStorePassword;
	/** key store file */
	private File mKeyStoreFile;
	/** key store type */
	private String mKeyStoreType;

	/** trust store password */
	private String mTrustStorePassword;
	/** trust store file */
	private File mTrustStoreFile;
	/** trust store type */
	private String mTrustStoreType;

	/** uri to log audits to */
	private URI mUri;

	/**
	 * Default constructor to instanciate the object
	 */
	public AtnaSecurityConfiguration() {

	}

	/**
	 * Method to get
	 *
	 * @return the keyStorePassword
	 */
	public String getKeyStorePassword() {
		return mKeyStorePassword;
	}

	/**
	 * Method to set
	 *
	 * @param keyStorePassword
	 *            the keyStorePassword to set
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		mKeyStorePassword = keyStorePassword;
	}

	/**
	 * Method to get
	 *
	 * @return the keyStoreFile
	 */
	public File getKeyStoreFile() {
		return mKeyStoreFile;
	}

	/**
	 * Method to set
	 *
	 * @param keyStoreFile
	 *            the keyStoreFile to set
	 */
	public void setKeyStoreFile(File keyStoreFile) {
		mKeyStoreFile = keyStoreFile;
	}

	/**
	 * Method to get
	 *
	 * @return the keyStoreType
	 */
	public String getKeyStoreType() {
		return mKeyStoreType;
	}

	/**
	 * Method to set
	 *
	 * @param keyStoreType
	 *            the keyStoreType to set
	 */
	public void setKeyStoreType(String keyStoreType) {
		mKeyStoreType = keyStoreType;
	}

	/**
	 * Method to get
	 *
	 * @return the trustStorePassword
	 */
	public String getTrustStorePassword() {
		return mTrustStorePassword;
	}

	/**
	 * Method to set
	 *
	 * @param trustStorePassword
	 *            the trustStorePassword to set
	 */
	public void setTrustStorePassword(String trustStorePassword) {
		mTrustStorePassword = trustStorePassword;
	}

	/**
	 * Method to get
	 *
	 * @return the trustStoreFile
	 */
	public File getTrustStoreFile() {
		return mTrustStoreFile;
	}

	/**
	 * Method to set
	 *
	 * @param trustStoreFile
	 *            the trustStoreFile to set
	 */
	public void setTrustStoreFile(File trustStoreFile) {
		mTrustStoreFile = trustStoreFile;
	}

	/**
	 * Method to get
	 *
	 * @return the trustStoreType
	 */
	public String getTrustStoreType() {
		return mTrustStoreType;
	}

	/**
	 * Method to set
	 *
	 * @param trustStoreType
	 *            the trustStoreType to set
	 */
	public void setTrustStoreType(String trustStoreType) {
		mTrustStoreType = trustStoreType;
	}

	/**
	 * Method to get
	 *
	 * @return the uri
	 */
	public URI getUri() {
		return mUri;
	}

	/**
	 * Method to set
	 *
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(URI uri) {
		mUri = uri;
	}

}
