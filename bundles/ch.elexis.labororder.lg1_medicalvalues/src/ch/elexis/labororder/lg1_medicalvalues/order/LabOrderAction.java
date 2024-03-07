package ch.elexis.labororder.lg1_medicalvalues.order;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.awt.Desktop;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;
import ch.elexis.labororder.lg1_medicalvalues.messages.Messages;

public class LabOrderAction extends Action {

	public LabOrderAction() {
        setId("ch.elexis.laborder.lg1_medicalvalues.laborder"); //$NON-NLS-1$
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.labororder.lg1_medicalvalues", //$NON-NLS-1$
		        "rsc/lg1_logo.png"));
		setText(Messages.LabOrderAction_nameAction);
	}

	@Override
	public void run() {
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		if (patient != null) {
			try {
				URI uri = buildOrderCreationUri(patient);
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Error contacting LG1 web service", e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				        "Es ist ein Fehler beim LG1 Aufruf aufgetreten.\n\n" + e.getLocalizedMessage());
			} catch (IllegalArgumentException e) {
				LoggerFactory.getLogger(getClass()).error("Error calling medicalvalues order creation API", e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				        "Es fehlen zur Auftragserstellung benötigte Patientendaten.\n\n" + e.getLocalizedMessage());
			} catch (URISyntaxException e) {
                                LoggerFactory.getLogger(getClass()).error("Error building medicalvalues order creation API URI", e);
                                MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
                                        "Es ist ein Fehler beim Erstellen der URL für die Auftragserstellung aufgetreten.\n\n" + e.getLocalizedMessage());
                        }
		}
	}

	private URI buildOrderCreationUri(Patient patient) throws URISyntaxException {
		URIBuilder builder = new URIBuilder("https://oe.lg1.lan/mdi/orders/importPatientAndCreateOrder");

                ch.elexis.labororder.lg1_medicalvalues.order.model.Patient lg1Patient = ch.elexis.labororder.lg1_medicalvalues.order.model.Patient.of(patient);
                lg1Patient.toMedicalvaluesOrderCreationAPIQueryParams(builder);

		return builder.build();
	}
}
