package at.medevit.elexis.bluemedication.ui.handler;

import java.util.HashMap;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import at.medevit.elexis.bluemedication.core.BlueMedicationConstants;
import at.medevit.elexis.bluemedication.core.BlueMedicationServiceHolder;
import at.medevit.elexis.bluemedication.core.UploadResult;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.rgw.tools.Result;

public class BlueMedicationDownloadHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		StructuredSelection selection =
			(StructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (!selection.isEmpty()) {
			Object object = selection.getFirstElement();
			if (object instanceof IDocumentHandle) {
				IDocumentHandle docHandle = (IDocumentHandle) object;
					Optional<UploadResult> pending = BlueMedicationServiceHolder.getService().getPendingUploadResult(docHandle);
				if (pending.isPresent()) {
					Result<String> emediplan = BlueMedicationServiceHolder.getService()
						.downloadEMediplan(pending.get().getId());
					if (emediplan.isOK()) {
						if (useRemoteImport()) {
							if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
								"Import",
								"Möchten Sie die aktuelle Medikation mit dem erstellten eMediplan überschrieben?")) {
								ICommandService commandService =
									(ICommandService) HandlerUtil.getActiveWorkbenchWindow(event)
										.getService(ICommandService.class);
								
								Command directImportCommand = commandService
									.getCommand("at.medevit.elexis.emediplan.ui.directImport");
								
								HashMap<String, String> params = new HashMap<String, String>();
								
								params.put(
									"at.medevit.elexis.emediplan.ui.directImport.parameter.emediplan",
									emediplan.get());
								params.put(
									"at.medevit.elexis.emediplan.ui.directImport.parameter.patientid",
									docHandle.getPatient().getId());
								params.put(
									"at.medevit.elexis.emediplan.ui.directImport.parameter.stopreason",
									"BlueMedication EMediplan Import");
								
								ParameterizedCommand parametrizedCommmand =
									ParameterizedCommand.generateCommand(directImportCommand, params);
								
								try {
									PlatformUI.getWorkbench().getService(IHandlerService.class)
										.executeCommand(parametrizedCommmand, null);
								} catch (NotDefinedException | NotEnabledException
										| NotHandledException e) {
									MessageDialog.openError(Display.getDefault().getActiveShell(),
										"Fehler",
										"Beim import des eMediplan ist ein Fehler aufgetreten. Bitte starten sie den Abgleich neu.");
								}
							}
						} else {
							ICommandService commandService = (ICommandService) HandlerUtil
								.getActiveWorkbenchWindow(event).getService(ICommandService.class);
							
							Command openImportCommand = commandService
								.getCommand("at.medevit.elexis.emediplan.ui.openImport");
							
							HashMap<String, String> params = new HashMap<String, String>();
							
							params.put(
								"at.medevit.elexis.emediplan.ui.openImport.parameter.emediplan",
								emediplan.get());
							params.put(
								"at.medevit.elexis.emediplan.ui.openImport.parameter.patientid",
								docHandle.getPatient().getId());
							
							ParameterizedCommand parametrizedCommmand =
								ParameterizedCommand.generateCommand(openImportCommand, params);
							
							try {
								PlatformUI.getWorkbench().getService(IHandlerService.class)
									.executeCommand(parametrizedCommmand, null);
							} catch (NotDefinedException | NotEnabledException
									| NotHandledException e) {
								MessageDialog.openError(Display.getDefault().getActiveShell(),
									"Fehler",
									"Beim öffnen des eMediplan Import ist ein Fehler aufgetreten. Bitte starten sie den Abgleich neu.");
							}
						}
					} else {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Beim download ist ein Fehler aufgetreten. Bitte starten sie den Abgleich neu.");
					}
					// always remove pending upload
					BlueMedicationServiceHolder.getService().removePendingUploadResult(docHandle);
				}
			}
		}
		return null;
	}
	
	private boolean useRemoteImport(){
		return CoreHub.globalCfg.get(BlueMedicationConstants.CFG_USE_IMPORT, false);
	}
}
