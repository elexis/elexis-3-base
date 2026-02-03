package at.medevit.elexis.aerztekasse.core;

import java.io.File;
import java.util.Optional;

import ch.elexis.core.model.IMandator;
import ch.rgw.tools.Result;

public interface IAerztekasseService {

	public static final String AERZTEKASSE_GLN = "7611910000009";

	public static final String AERZTEKASSE_SNAPSHOT_MODE = "aerztekasseSnapshotMode";

	public static final String cfgBase = "net/medshare/connector/finance/aerztekasse"; //$NON-NLS-1$

	// Globale Einstellungen
	public static final String cfgUsername = cfgBase + "/username"; //$NON-NLS-1$
	public static final String cfgPassword = cfgBase + "/password"; //$NON-NLS-1$
	public static final String cfgArchiveDir = cfgBase + "/archivedir"; //$NON-NLS-1$
	public static final String cfgErrorDir = cfgBase + "/errordir"; //$NON-NLS-1$
	public static final String cfgAccount = cfgBase + "/account"; //$NON-NLS-1$
	public static final String cfgDeploymentLevel = cfgBase + "/deploymentlevel"; //$NON-NLS-1$

	/**
	 * Send all invoice xml files of the provided sendDirectory to the Aerztekasse
	 * REST service. After sending the files are moved to archive directory inside
	 * the send directory.
	 * 
	 * @param invoiceFilename
	 * @return
	 */
	public Result<Object> sendFiles(File sendDirectory);

	/**
	 * Send a single invoice xml file to the Aerztekasse REST service.
	 * 
	 * @param invoiceFile
	 * @return
	 */
	public Result<Object> sendFile(File invoiceFile);

	/**
	 * Test if there is a client id available. Such an id should be provided by a
	 * fragment.
	 * 
	 * @return
	 */
	public boolean hasClientId();

	/**
	 * Test if user credentials are available.
	 * 
	 * @return
	 */
	public boolean hasCredentials();

	/**
	 * Set the global username
	 * 
	 * @param string
	 */
	public void setUsername(String string);

	/**
	 * Get the global username
	 * 
	 * @return
	 */
	public Optional<String> getUsername();

	/**
	 * Get the global password
	 * 
	 * @param string
	 */
	public void setPassword(String string);

	/**
	 * Get the global password.
	 * 
	 * @return
	 */
	public Optional<String> getPassword();


	/**
	 * Ablehnung -> Rechnung fehler, und schreiben Info rein wenn möglich. bei
	 * fehler in globaler error verzeichnis
	 */

	/**
	 * Set the global archive directory
	 * 
	 * @param archiveDir
	 */
	public void setGlobalArchiveDir(String archiveDir);

	/**
	 * Get the global archive directory
	 * 
	 * @param archiveDir
	 * @return
	 */
	public Optional<String> getGlobalArchiveDir();

	/**
	 * Set the global error directory
	 * 
	 * @param errorDir
	 */
	public void setGlobalErrorDir(String errorDir);

	/**
	 * Set the account string for the {@link IMandator}.
	 * 
	 * @param mandator
	 * @param account
	 */
	public void setAccount(IMandator mandator, String account);

	/**
	 * Get the configured account String for the provided {@link IMandator}
	 * 
	 * @param mandator
	 */
	public Optional<String> getAccount(IMandator mandator);
}
