package at.medevit.elexis.bluemedication.ui.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.services.IEvaluationService;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.bluemedication.core.BlueMedicationServiceHolder;
import at.medevit.elexis.bluemedication.core.UploadResult;
import ch.elexis.omnivore.data.DocHandle;
import ch.rgw.tools.Result;

public class BlueMedicationUploadHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		StructuredSelection selection =
			(StructuredSelection) HandlerUtil.getCurrentSelection(event);
		String resulttyp = event.getParameter("at.medevit.elexis.bluemedication.ui.resulttyp");
		if (!selection.isEmpty()) {
			Object object = selection.getFirstElement();
			if (object instanceof DocHandle) {
				DocHandle docHandle = (DocHandle) object;
				if (docHandle.getMimeType().toLowerCase().endsWith("pdf")
						|| docHandle.getTitle().toLowerCase().endsWith(".pdf")) {
					Shell activeshell = HandlerUtil.getActiveShell(event);
					ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(activeshell);
					try {
						progressDialog.run(true, false, new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException{
								monitor.beginTask(
									"BlueMedication Upload von " + docHandle.getLabel(),
									IProgressMonitor.UNKNOWN);
								Result<UploadResult> result = BlueMedicationServiceHolder
									.getService().uploadDocument(docHandle.getPatient(),
												docHandle.createTemporaryFile(docHandle.getTitle()), resulttyp);
								if (result.isOK()) {
									Display.getDefault().syncExec(() -> {
										// open the uri of the result
										Program.launch(result.get().getUrl());
										BlueMedicationServiceHolder.getService()
											.addPendingUploadResult(docHandle, result.get());
										BlueMedicationServiceHolder.getService()
											.startPollForResult(docHandle, result.get(),
												new Consumer<Object>() {
													@Override
													public void accept(Object object){
														Display.getDefault().asyncExec(() -> {
															download(object);
														});
													}
												});
									});
								} else {
									List<Result<UploadResult>.msg> messages = result.getMessages();
									if (messages != null && !messages.isEmpty()) {
										String text = messages.get(0).getText();
										if (StringUtils.isNotBlank(text)
											&& text.startsWith("Error result code [")) {
											String resultCode = text.substring(
												text.indexOf('[') + 1,
												text.indexOf(']'));
											if (StringUtils.isNotBlank(resultCode)) {
												if ("A6".equals(resultCode)) {
													Display.getDefault().syncExec(() -> {
														MessageDialog.openError(
															Display.getDefault().getActiveShell(),
															"BlueMedication",
															"Der Medikationsabgleich kann in BlueMedication nicht durchgeführt werden.\n"
																+ "Bitte melden Sie den Fehler A6 an help.bluemedication@bluecare.ch");
													});
												} else {
													Display.getDefault().syncExec(() -> {
														MessageDialog.openError(
															Display.getDefault().getActiveShell(),
															"BlueMedication",
															"Beim Aufruf von BlueMedication ist ein technischer Fehler aufgetreten\n"
																+ "Bitte melden Sie den Fehler "
																+ resultCode
																+ " an help.bluemedication@bluecare.ch");
													});
												}
												return;
											}
										}
									}
									Display.getDefault().syncExec(() -> {
										MessageDialog.openError(
											Display.getDefault().getActiveShell(), "BlueMedication",
											"Beim Hochladen der Datei ist ein Fehler aufgetreten.\n\nBitte HIN client Konfiguration prüfen.");
									});
								}
							}
						});
					} catch (InvocationTargetException | InterruptedException e) {
						MessageDialog.openError(activeshell, "BlueMedication",
							"BlueMedication Upload konnte nicht gestartet werden.");
						LoggerFactory.getLogger(getClass()).error("Error on upload", e);
					}
				}
			}
		}
		return null;
	}
	
	private void download(Object object){
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getService(ICommandService.class);
		
		IEvaluationService evaluationService = (IEvaluationService) PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getService(IEvaluationService.class);
		EvaluationContext context =
			new EvaluationContext(evaluationService.getCurrentState(),
				evaluationService.getCurrentState().getDefaultVariable());
		context.addVariable("selection", new StructuredSelection(object));
		Command downloadCommand =
			commandService.getCommand("at.medevit.elexis.emediplan.ui.blueMedicationDownload");

		HashMap<String, String> params = new HashMap<String, String>();
		ParameterizedCommand parametrizedCommmand =
			ParameterizedCommand.generateCommand(downloadCommand, params);
		try {
			PlatformUI.getWorkbench().getService(IHandlerService.class)
				.executeCommandInContext(parametrizedCommmand, null, context);
		} catch (NotDefinedException | NotEnabledException | NotHandledException
				| ExecutionException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				"Beim automatischen Download ist ein Fehler aufgetreten. Bitte starten sie den Abgleich neu.");
		}
	}
}
