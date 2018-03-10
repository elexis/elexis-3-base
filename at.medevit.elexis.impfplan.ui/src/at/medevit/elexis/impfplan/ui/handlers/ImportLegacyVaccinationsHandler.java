package at.medevit.elexis.impfplan.ui.handlers;

import java.util.List;
import java.util.Map;

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
 * Calls the vaccination import progress and prepares import result for further usage. This class is
 * only usable if the optional ch.elexis.impflan dependency is resolvable.
 * 
 * @author Lucia
 *
 */
public class ImportLegacyVaccinationsHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(ImportLegacyVaccinationsHandler.class);
	
	public static final String COMMAND_ID =
		"at.medevit.elexis.impfplan.ui.command.ImportOtherVaccinations";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Mandant mandant = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
		
		IProgressService progService = PlatformUI.getWorkbench().getProgressService();
		ImportLegacyVaccinationsProgress ivProgress =
			new ImportLegacyVaccinationsProgress(mandant.storeToString());
		try {
			progService.runInUI(progService, ivProgress, null);
		} catch (Exception e) {
			log.error("Error running ImportVaccinationsProgress", e);
			return "Fehler beim Impf-Import Prozess";
		}
		
		// give user feedback about import status
		StringBuilder sb = new StringBuilder();
		if (ivProgress.isAbnormalImport()) {
			List<Vaccination> alreadyImported = ivProgress.getAlreadyImportedVaccinations();
			Map<Vaccination, ErrorCode> errorMap = ivProgress.getErrorMap();
			
			if (!errorMap.isEmpty()) {
				sb.append("Fehler beim Import:\n");
				for (Vaccination vacc : errorMap.keySet()) {
					ErrorCode eCode = errorMap.get(vacc);
					// show id in case patient could not be resolved
					if (eCode.equals(ErrorCode.PATIENT_NOTFOUND)) {
						sb.append(vacc.getPatientId() + " : "
							+ vacc.getVaccinationType().getLabel() + " - " + eCode.toString());
						
					} else {
						sb.append(vacc.getLabel() + " - " + eCode.toString());
					}
					sb.append("\n");
				}
				sb.append("\n");
			}
			
			if (!alreadyImported.isEmpty()) {
				sb.append("Bereits importiert:\n");
				for (Vaccination vacc : alreadyImported) {
					sb.append(vacc.getLabel());
					sb.append("\n");
				}
			}
		} else {
			sb.append("Import erfolgreich abgeschlossen!");
		}
		
		return sb.toString();
	}
	
}
