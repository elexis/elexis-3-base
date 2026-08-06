package ch.pationnaire.elexis.scan.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import jakarta.inject.Inject;

public class NapsScanHandler extends AbstractHandler implements IHandler {

	@Inject
	@Service(filterExpression = "(storeid=ch.elexis.data.store.omnivore)")
	private IDocumentStore omnivoreStore;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		init();

		Optional<IPatient> activePatient = ContextServiceHolder.get().getActivePatient();
		if (activePatient.isPresent()) {
			Optional<IEncounter> activeEncounter = ContextServiceHolder.get().getTyped(IEncounter.class);
			ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
					.getService(ICommandService.class);
			IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
					.getService(IHandlerService.class);

			if (assertScanProfileAvailable(commandService, handlerService)) {
				// call documents ui to handle import of pdf for selected patient
				Command cmd = commandService.getCommand("at.medevit.elexis.documents.scan.command.napsscantofile");
				if (cmd != null) {
					try {
						IParameter iparam = cmd.getParameter(
								"at.medevit.elexis.documents.scan.command.napsscan.parameter.napsprofile");
						Parameterization params = new Parameterization(iparam, "pationnaire");

						ParameterizedCommand pc = new ParameterizedCommand(cmd,
								List.of(params).toArray(new Parameterization[1]));

						Object scanned = handlerService.executeCommand(pc, null);
						if (scanned instanceof File) {
							ICategory category = omnivoreStore.createCategory("pationnaire");
							String title = "pationnaire "
									+ LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
							IDocument document = omnivoreStore.createDocument(activePatient.get().getId(), title,
									category.getName());
							document.setCreated(new Date());
							document.setMimeType(title + ".pdf");
							try (InputStream fi = new FileInputStream((File) scanned)) {
								omnivoreStore.saveDocument(document, fi);
							}
							if (activeEncounter.isPresent()) {
								EncounterServiceHolder.get().addXRef(activeEncounter.get(), "ch.elexis.omnivore",
										document.getId(), -1, title);
							}
						} else {
							MessageDialog.openWarning(HandlerUtil.getActiveShell(event), "Scan fehlgeschlagen",
									"Bitte stellen Sie sicher das die scan Anbindung konfiguriert, und der Scanner erreichbar ist.");
						}
					} catch (Exception e) {
						LoggerFactory.getLogger(getClass()).error("Error starting scan pationnaire", e);
					}
				} else {
					MessageDialog.openWarning(HandlerUtil.getActiveShell(event), "Scan nicht möglich",
							"Es konnte keine scan Anbindung gefunden werden. Bitte installieren Sie eine scan Anbindung.");
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean assertScanProfileAvailable(ICommandService commandService, IHandlerService handlerService) {
		try {
			Command cmd = commandService.getCommand("at.medevit.elexis.documents.scan.napsprofileslist");
			if (cmd != null && cmd.isDefined()) {
				HashMap<String, String> params = new HashMap<String, String>();
				ParameterizedCommand parametrizedCommmand = ParameterizedCommand.generateCommand(cmd, params);
				List<String> profiles = (List<String>) PlatformUI.getWorkbench().getService(IHandlerService.class)
						.executeCommand(parametrizedCommmand, null);
				if (!profiles.contains("pationnaire")) {
					Display.getDefault().syncExec(() -> {
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Scan nicht möglich",
								"Es konnte keine pationnaire scan profil gefunden werden."
										+ "\nNAPS wird geöffnet, bitte erstellen Sie das pationnaire profil.");
					});
					cmd = commandService.getCommand("at.medevit.elexis.documents.scan.openapp");
					params = new HashMap<String, String>();
					parametrizedCommmand = ParameterizedCommand.generateCommand(cmd, params);
					PlatformUI.getWorkbench().getService(IHandlerService.class).executeCommand(parametrizedCommmand,
							null);
				} else {
					return true;
				}
			} else {
				Display.getDefault().asyncExec(() -> {
					MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Scan nicht möglich",
							"Es konnte keine scan Anbindung gefunden werden. Bitte installieren Sie eine scan Anbindung.");
				});
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).warn("Exception on assert scan profile", e);
		}
		return false;
	}

	private void init() {
		if (omnivoreStore == null) {
			CoreUiUtil.injectServices(this);
		}
	}
}
