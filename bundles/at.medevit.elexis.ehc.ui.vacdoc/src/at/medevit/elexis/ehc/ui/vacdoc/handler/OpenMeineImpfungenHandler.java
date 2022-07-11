package at.medevit.elexis.ehc.ui.vacdoc.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.handlers.HandlerUtil;
import org.ehealth_connector.common.mdht.Identificator;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.vacdoc.service.MeineImpfungenServiceHolder;
import at.medevit.elexis.ehc.vacdoc.service.MeineImpfungenService;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.Patient;

public class OpenMeineImpfungenHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Patient patient = ElexisEventDispatcher.getSelectedPatient();
			if (patient != null) {
				ProgressMonitorDialog progress = new ProgressMonitorDialog(HandlerUtil.getActiveShell(event));
				try {
					progress.run(false, true, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							monitor.beginTask("Patient auf meineimpfungen suchen ...", IProgressMonitor.UNKNOWN);

							List<org.ehealth_connector.common.mdht.Patient> patients = MeineImpfungenServiceHolder
									.getService().getPatients(patient);
							if (patients != null && !patients.isEmpty()) {
								if (patients.size() == 1) {
									StringBuilder link = new StringBuilder();
									link.append(MeineImpfungenServiceHolder.getService().getBaseUrl());
									if (link.lastIndexOf("/") != (link.length() - 1)) { //$NON-NLS-1$
										link.append("/"); //$NON-NLS-1$
									}
									if (ConfigServiceHolder.getMandator(MeineImpfungenService.CONFIG_USECERTAUTH,
											false)) {
										link.append("certauth/"); //$NON-NLS-1$
									}
									getPatientId(patients.get(0)).ifPresent(pid -> {
										link.append("specialist-person-home.html?personId=").append(pid); //$NON-NLS-1$
										Program.launch(link.toString());
									});
								} else {
									MessageDialog.openError(HandlerUtil.getActiveShell(event), "meineimpfungen", //$NON-NLS-1$
											"Mehrere Patienten für [" + patient.getLabel(false)
													+ "] auf meineimpfungen gefunden.");
								}
							} else {
								MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "meineimpfungen",
										"Kein Patient [" + patient.getLabel(false) + "] auf meineimpfungen gefunden.");
							}
							monitor.done();
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					LoggerFactory.getLogger(OpenMeineImpfungenHandler.class).warn("Exception on patient lookup", e); //$NON-NLS-1$
					MessageDialog.openError(HandlerUtil.getActiveShell(event), "meineimpfungen",
							"Es ist ein Fehler aufgetreten.");
				}
			} else {
				MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "meineimpfungen",
						"Kein Patient ausgewählt");
			}
		} catch (IllegalStateException ise) {
			LoggerFactory.getLogger(OpenMeineImpfungenHandler.class).error("Service not available", ise); //$NON-NLS-1$
			MessageDialog.openError(HandlerUtil.getActiveShell(event), "meineimpfungen",
					"meineimpfungen nicht verfügbar");
		}
		return null;
	}

	private Optional<String> getPatientId(org.ehealth_connector.common.mdht.Patient ehcPatient) {
		List<Identificator> ids = ehcPatient.getIds();
		if (ids != null && !ids.isEmpty()) {
			for (Identificator identificator : ids) {
				if (MeineImpfungenService.PDQ_REQUEST_PATID_OID.equals(identificator.getRoot())) {
					return Optional.of(identificator.getExtension());
				}
			}
		}
		return Optional.empty();
	}
}
