package at.medevit.elexis.emediplan;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

import org.eclipse.e4.ui.workbench.UIEvents;
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
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.medication.views.MedicationView;
import ch.elexis.data.Patient;

@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class StartupHandler implements EventHandler {
	private static Logger logger = LoggerFactory.getLogger(StartupHandler.class);

	ElexisEventListener elexisEventListenerImpl;

	public static void openEMediplanImportDialog(String chunk, String selectedPatientId) {
		Medication medication = EMediplanServiceHolder.getService().createModelFromChunk(chunk);

		// from inbox the patient id is available
		if (selectedPatientId != null && medication.Patient != null) {
			medication.Patient.patientId = selectedPatientId;
		}

		EMediplanServiceHolder.getService().addExistingArticlesToMedication(medication);
		if (medication != null) {
			if (medication.Patient != null && medication.Patient.patientId != null) {
				Patient patient = Patient.load(medication.Patient.patientId);
				if (patient.exists()) {
					ElexisEventDispatcher.fireSelectionEvent(patient);

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
		logger.info("APPLICATION STARTUP COMPLETE"); //$NON-NLS-1$
		elexisEventListenerImpl = new ElexisEventListenerImpl(BarcodeScannerMessage.class, ElexisEvent.EVENT_UPDATE) {
			public void run(ElexisEvent ev) {
				BarcodeScannerMessage b = (BarcodeScannerMessage) ev.getGenericObject();
				if (hasMediplanHeader(b.getChunk())) {
					openEMediplanImportDialog(b.getChunk(), null);
				}
			}
		};
		ElexisEventDispatcher.getInstance().addListeners(elexisEventListenerImpl);
	}
}
