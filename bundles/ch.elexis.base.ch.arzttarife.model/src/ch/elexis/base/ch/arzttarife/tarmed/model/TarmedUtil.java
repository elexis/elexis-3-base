package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.model.service.ConfigServiceHolder;
import ch.elexis.base.ch.arzttarife.model.service.ContextServiceHolder;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.PatientConstants;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;

public class TarmedUtil {
	
	private static Properties increasedTreatment;
	
	public static boolean getConfigValue(Class<?> requestor, Class<?> configSource,
		String parameter, boolean fallback){
		IContextService contextService = ContextServiceHolder.get().orElse(null);
		if (contextService != null) {
			IContact contact;
			if (configSource.equals(IMandator.class)) {
				contact = contextService.getActiveMandator().orElse(null);
			} else {
				contact =
					contextService.getActiveUser().map(u -> u.getAssignedContact()).orElse(null);
			}
			if (contact != null) {
				IConfigService configService = ConfigServiceHolder.get().orElse(null);
				if (configService != null) {
					boolean value =
						configService.get(contact, parameter, fallback);
					if (value != fallback) {
						LoggerFactory.getLogger(requestor).info("[{}] Overriden [{}] with [{}]",
							contact.getDescription3(), parameter, value);
					}
					return value;
				}
			}
		}
		return fallback;
	}
	
	public static Optional<String> getIncreasedTreatmentCode(TarmedLeistung code){
		if (increasedTreatment == null) {
			increasedTreatment = new Properties();
			try {
				increasedTreatment.load(
					TarmedUtil.class.getResourceAsStream("/rsc/increasedtreatment.properties"));
			} catch (IOException e) {
				LoggerFactory.getLogger(TarmedUtil.class)
					.error("Loading increasedtreatment.properties failed", e);
			}
		}
		return Optional.ofNullable((String) increasedTreatment.get(code.getCode()));
	}
	
	public static boolean isIncreasedTreatment(IPatient patient){
		if (patient != null) {
			Object info = patient.getExtInfo(PatientConstants.FLD_EXTINFO_INCREASEDTREATMENT);
			if (info instanceof String) {
				return Boolean.parseBoolean((String) info);
			}
		}
		return false;
	}
}
