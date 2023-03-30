package at.medevit.elexis.impfplan.ui.handlers;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.impfplan.ui.handlers.progress.ImportLegacyVaccinationsProgress;
import at.medevit.elexis.impfplan.ui.handlers.progress.ImportLegacyVaccinationsProgress.ErrorCode;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;
import ch.elexis.impfplan.model.Vaccination;

/**
 * Calls the vaccination import progress and prepares import result for further
 * usage. This class is only usable if the optional ch.elexis.impflan dependency
 * is resolvable.
 *
 * @author Lucia
 *
 */
public class ImportLegacyVaccinationsHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(ImportLegacyVaccinationsHandler.class);

	public static final String COMMAND_ID = "at.medevit.elexis.impfplan.ui.command.ImportOtherVaccinations"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Mandant mandant = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);

		IProgressService progService = PlatformUI.getWorkbench().getProgressService();
		ImportLegacyVaccinationsProgress ivProgress = new ImportLegacyVaccinationsProgress(mandant.storeToString());
		try {
			progService.runInUI(progService, ivProgress, null);
		} catch (Exception e) {
			log.error("Error running ImportVaccinationsProgress", e); //$NON-NLS-1$
			return "Fehler beim Impf-Import Prozess";
		}

		// give user feedback about import status
		StringBuilder sb = new StringBuilder();
		if (ivProgress.isAbnormalImport()) {
			List<Vaccination> alreadyImported = ivProgress.getAlreadyImportedVaccinations();
			Map<Vaccination, ErrorCode> errorMap = ivProgress.getErrorMap();

			if (!errorMap.isEmpty()) {

				for (Vaccination vacc : errorMap.keySet()) {
					ErrorCode eCode = errorMap.get(vacc);
					// show id in case patient could not be resolved
					if (eCode.equals(ErrorCode.PATIENT_NOTFOUND)) {
						if (vacc.getPatientId().isEmpty() && vacc.getVaccinationType() == null) {
							// since a bug #8853 in impfplan trash entries with no patientid and vaccination
							// type can exists.
							// we only log this out
							log.warn("Import warn: patientId and vaccinationType is null for vaccination id: " //$NON-NLS-1$
									+ vacc.getId());
							continue;
						}
						sb.append(vacc.getPatientId() + " : " //$NON-NLS-1$
								+ (vacc.getVaccinationType() != null ? vacc.getVaccinationType().getLabel()
										: "VaccType [NULL]") //$NON-NLS-1$
								+ " - " + eCode.toString()); //$NON-NLS-1$
					} else {
						sb.append(vacc.getLabel() + " - " + eCode.toString()); //$NON-NLS-1$
					}
					sb.append(StringUtils.LF);
				}
				if (sb.length() > 0) {
					sb.insert(0, "Fehler beim Import:\n");
					sb.append(StringUtils.LF);
				}
			}

			if (!alreadyImported.isEmpty()) {
				sb.append("Bereits importiert:\n");
				for (Vaccination vacc : alreadyImported) {
					sb.append(vacc.getLabel());
					sb.append(StringUtils.LF);
				}
			}
		} else {
			sb.append("Import erfolgreich abgeschlossen!");
		}

		return sb.toString();
	}

}
