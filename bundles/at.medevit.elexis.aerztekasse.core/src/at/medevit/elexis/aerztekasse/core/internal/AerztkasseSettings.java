package at.medevit.elexis.aerztekasse.core.internal;

import java.util.Optional;

import at.medevit.elexis.aerztekasse.core.IAerztekasseService;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.utils.CoreUtil;

public class AerztkasseSettings {


	private static String preProdTokenUrl = "https://idp.preprod.cdm.ch/f5-oauth2/v1/token";
	private static String preProdXmlImportUrl = "https://api.preprod.cdm.ch/ext/isi/api/v1/xml_import/xml";

	private static String prodTokenUrl = "https://idp.cdm.ch/f5-oauth2/v1/token";
	private static String prodXmlImportUrl = "https://api.cdm.ch/ext/isi/api/v1/xml_import/xml";

	public String getUsername() {
		if (ConfigServiceHolder.get().get(IAerztekasseService.cfgCredentialsGlobal, true)) {
			return ConfigServiceHolder.get().get(IAerztekasseService.cfgUsername, null);
		} else {
			Optional<IMandator> mandator = ContextServiceHolder.get().getActiveMandator();
			if (mandator.isPresent()) {
				return ConfigServiceHolder.get()
						.get(IAerztekasseService.cfgUsername + "/" + mandator.get().getBiller().getId(), null);
			}
		}
		return null;
	}

	public String getPassword() {
		if (ConfigServiceHolder.get().get(IAerztekasseService.cfgCredentialsGlobal, true)) {
			return ConfigServiceHolder.get().get(IAerztekasseService.cfgPassword, null);
		} else {
			Optional<IMandator> mandator = ContextServiceHolder.get().getActiveMandator();
			if (mandator.isPresent()) {
				return ConfigServiceHolder.get()
						.get(IAerztekasseService.cfgPassword + "/" + mandator.get().getBiller().getId(), null);
			}
		}
		return null;
	}

	public String getTokenUrl() {
		return isPreprod() ? preProdTokenUrl : prodTokenUrl;
	}

	public boolean isPreprod() {
		if (CoreUtil.isTestMode()) {
			return true;
		}
		boolean snapshotMode = System.getProperty(IAerztekasseService.AERZTEKASSE_SNAPSHOT_MODE) != null;
		if (snapshotMode) {
			return ConfigServiceHolder.get().get(IAerztekasseService.cfgDeploymentLevel, "PREPROD").equals("PREPROD");
		}
		return false;
	}

	public void setArchiveDirectory(String archiveDirectory) {
		ConfigServiceHolder.get().set(IAerztekasseService.cfgArchiveDir, archiveDirectory);
	}

	public String getArchiveDirectory() {
		return ConfigServiceHolder.get().get(IAerztekasseService.cfgArchiveDir, null);
	}

	public void setErrorDirectory(String archiveDirectory) {
		ConfigServiceHolder.get().set(IAerztekasseService.cfgErrorDir, archiveDirectory);
	}

	public String getErrorDirectory() {
		return ConfigServiceHolder.get().get(IAerztekasseService.cfgErrorDir, null);
	}

	public void setAccount(IContact biller, String account) {
		ConfigServiceHolder.get().set(IAerztekasseService.cfgAccount + "/" + biller.getId(), account);
	}

	public String getAccount(IContact biller) {
		return ConfigServiceHolder.get().get(IAerztekasseService.cfgAccount + "/" + biller.getId(), null);
	}

	public String getXmlImportUrl() {
		return isPreprod() ? preProdXmlImportUrl : prodXmlImportUrl;
	}
}
