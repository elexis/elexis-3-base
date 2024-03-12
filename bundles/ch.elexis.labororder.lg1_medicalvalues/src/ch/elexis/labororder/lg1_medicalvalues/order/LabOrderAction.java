package ch.elexis.labororder.lg1_medicalvalues.order;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.PartInitException;

import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

import org.apache.http.client.utils.URIBuilder;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;

import ch.elexis.labororder.lg1_medicalvalues.order.model.Patient;
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
		IPatient patient = ContextServiceHolder.get().getActivePatient().get();
		if (patient != null) {
			try {
				URL url = buildOrderCreationUrl(patient);
				PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
			} catch (URISyntaxException | MalformedURLException e) {
                                LoggerFactory.getLogger(getClass()).error("Error building medicalvalues order creation API URL", e);
                                MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
                                        "Es ist ein Fehler beim Erstellen der URL für die Auftragserstellung aufgetreten.\n\n" + e.getLocalizedMessage());
                        } catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Error contacting LG1 web service", e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				        "Es ist ein Fehler beim LG1 Aufruf aufgetreten.\n\n" + e.getLocalizedMessage());
			} catch (PartInitException e) {
                                LoggerFactory.getLogger(getClass()).error("Error opening browser with medicalvalues order creation API URL", e);
                                MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
                                        "Es ist ein Fehler beim Öffnen des Browsers aufgetreten.\n\n" + e.getLocalizedMessage());
                        } catch (IllegalArgumentException e) {
				LoggerFactory.getLogger(getClass()).error("Error calling medicalvalues order creation API", e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				        "Es fehlen zur Auftragserstellung benötigte Patientendaten.\n\n" + e.getLocalizedMessage());
			}
		}
	}

	private URL buildOrderCreationUrl(IPatient patient) throws URISyntaxException, MalformedURLException {
		URIBuilder builder = new URIBuilder("https://oe.lg1.lan/mdi/diagnostic-intelligence/orders/importPatientAndCreateOrder");

                Patient lg1Patient = Patient.of(patient);
                lg1Patient.toMedicalvaluesOrderCreationAPIQueryParams(builder);

		return builder.build().toURL();
	}
}
