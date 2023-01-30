package at.medevit.elexis.emediplan;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import at.medevit.elexis.emediplan.ui.ImportEMediplanDialog;
import ch.elexis.barcode.scanner.BarcodeScannerMessage;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.medication.views.MedicationView;

@Component(property = { EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.EVENT_UPDATE })
public class StartupHandler implements EventHandler {
	private static Logger logger = LoggerFactory.getLogger(StartupHandler.class);

	public static void openEMediplanImportDialog(String chunk, String selectedPatientId) {
		Medication medication = EMediplanServiceHolder.getService().createModelFromChunk(chunk);

		// from inbox the patient id is available
		if (selectedPatientId != null && medication.Patient != null) {
			medication.Patient.patientId = selectedPatientId;
		}

		EMediplanServiceHolder.getService().addExistingArticlesToMedication(medication);
		if (medication != null) {
			if (medication.Patient != null && medication.Patient.patientId != null) {
				Optional<IPatient> patient = CoreModelServiceHolder.get().load(medication.Patient.patientId,
						IPatient.class);
				if (patient.isPresent()) {
					ContextServiceHolder.get().setActivePatient(patient.get());

					UiDesk.getDisplay().asyncExec(new Runnable() {
						public void run() {
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
										.showView(MedicationView.PART_ID);
							} catch (PartInitException e) {
								logger.warn("cannot open view with id: " + MedicationView.PART_ID, e); //$NON-NLS-1$
							}
							logger.debug("Opening ImportEMediplanDialog"); //$NON-NLS-1$
							ImportEMediplanDialog dlg = new ImportEMediplanDialog(UiDesk.getTopShell(), medication,
									selectedPatientId != null);
							dlg.open();
						}
					});
				}
			}
		}
	}

	private boolean hasMediplanHeader(String chunk) {
		return chunk.startsWith("CHMED"); //$NON-NLS-1$
	}

	public static String getDecodedJsonString(@NonNull String encodedJson) {
		String content = encodedJson.substring(9);
		byte[] zipped = Base64.getMimeDecoder().decode(content);
		StringBuilder sb = new StringBuilder();
		try {
			GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(zipped));
			InputStreamReader reader = new InputStreamReader(gzip);
			BufferedReader in = new BufferedReader(reader);
			// Probably only single json line, but just to be sure ...
			String read;
			while ((read = in.readLine()) != null) {
				sb.append(read);
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(StartupHandler.class).error("Error decoding json", e); //$NON-NLS-1$
			throw new IllegalStateException("Error decoding json", e); //$NON-NLS-1$
		}
		return sb.toString();
	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(ElexisEventTopics.EVENT_UPDATE)) {
			if (event.getProperty("org.eclipse.e4.data") instanceof BarcodeScannerMessage) {
				BarcodeScannerMessage b = (BarcodeScannerMessage) event.getProperty("org.eclipse.e4.data");
				if (hasMediplanHeader(b.getChunk())) {
					openEMediplanImportDialog(b.getChunk(), null);
				}
			}
		}
	}
}
