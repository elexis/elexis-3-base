package at.medevit.elexis.gdt.defaultfilecp.ui.view;

import java.util.HashMap;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.data.GDTProtokoll;
import at.medevit.elexis.gdt.defaultfilecp.FileCommPartner;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;

public class GdtView extends ViewPart {
	
	public GdtView(){
		
	}
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout());
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		sc.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Composite composite = new Composite(sc, SWT.NONE);
		composite.setLayout(new GridLayout());
		
		sc.setContent(composite);
		
		createContents(composite);
		
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createContents(Composite composite){
		for (String id : FileCommPartner.getAllFileCommPartnersArray())
		{
			FileCommPartner fileCommPartner = new FileCommPartner(id);
			String fileCommPartnerName =
				fileCommPartner.getSettings().get(fileCommPartner.getFileTransferName(), "");
			
			String lastExaminationId = getLastExaminationId(fileCommPartner);
			
			Composite content = new Composite(composite, SWT.BORDER);
			content.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
			content.setLayout(new GridLayout(3, false));
			
			Label label = new Label(content, SWT.RIGHT);
			label.setText("");
			label.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
			label.setBackground(UiDesk.getColor(UiDesk.COL_LIGHTGREY));
			
			label = new Label(content, SWT.NONE);
			label.setText("Gerätename " + fileCommPartnerName);
			label.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
			
			Button btnNewExamination = new Button(content, SWT.PUSH);
			btnNewExamination.setText("Neue Untersuchung anfordern");
			btnNewExamination.setData(fileCommPartner.getId());
			btnNewExamination.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event){
					if (ElexisEventDispatcher.getSelectedPatient() == null) {
						openPatientNotSelectedDialog();
					}
					else {
						ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
							.getService(ICommandService.class);
						Command command = commandService
							.getCommand("at.medevit.elexis.gdt.command.NeueUntersuchungAnfordern");
						if (command != null) {
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("at.medevit.elexis.gdt.cmd.parameter.targetId",
								String.valueOf(((Button) event.getSource()).getData()));
							
							ExecutionEvent ee =
								new ExecutionEvent(command, params, null, getSite().getPage());
							try {
								command.executeWithChecks(ee);
							} catch (CommandException e) {
								LoggerFactory.getLogger(GdtView.class).error("command execution",
									e);
								openPatientNotSelectedDialog();
							}
						}
					}
					
				}
			});
				
			Button btnBaseDataRequest = new Button(content, SWT.PUSH);
			btnBaseDataRequest.setText("Stammdaten übermitteln");
			btnBaseDataRequest.setData(fileCommPartner.getId());
			btnBaseDataRequest.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event){
					
					if (ElexisEventDispatcher.getSelectedPatient() == null) {
						openPatientNotSelectedDialog();
					} else {
						ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
							.getService(ICommandService.class);
						Command command = commandService
							.getCommand("at.medevit.elexis.gdt.command.StammdatenUebermitteln");
						if (command != null) {
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("at.medevit.elexis.gdt.cmd.parameter.targetId",
								String.valueOf(((Button) event.getSource()).getData()));
							
							ExecutionEvent ee =
								new ExecutionEvent(command, params, null, getSite().getPage());
							try {
								command.executeWithChecks(ee);
							} catch (CommandException e) {
								LoggerFactory.getLogger(GdtView.class).error("command execution",
									e);
								MessageDialog.openError(getSite().getShell(), "Error",
									"Stammdatenübermittlung fehlgeschlagen.");
							}
						}
					}
				}
			});
			
			Button btnShowLastExamination = new Button(content, SWT.PUSH);
			btnShowLastExamination.setText("Letzte Untersuchung anzeigen");
			btnShowLastExamination.setData(lastExaminationId);
			btnShowLastExamination.setEnabled(lastExaminationId != null);
			btnShowLastExamination.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event){
					
					ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
						.getService(ICommandService.class);
					Command command = commandService
						.getCommand("at.medevit.elexis.gdt.command.DatenEinerUntersuchungAnzeigen");
					if (command != null) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put(
							"at.medevit.elexis.gdt.command.DatenEinerUntersuchungAnzeigen.gdtProtokollSource",
							String.valueOf(((Button) event.getSource()).getData()));
						ExecutionEvent ee =
							new ExecutionEvent(command, params, null, getSite().getPage());
						try {
							command.executeWithChecks(ee);
						} catch (CommandException e) {
							LoggerFactory.getLogger(GdtView.class).error("command execution", e);
							MessageDialog.openError(getSite().getShell(), "Error",
								"Letzte Untersuchung anzeigen fehlgeschlagen.");
						}
					}
				}
				
			});
			
		}
	}
	
	@Override
	public void setFocus(){
	}
	
	private String getLabel(FileCommPartner fileCommPartner){
		return fileCommPartner.getSettings().get(fileCommPartner.getFileTransferName(), "") + " ("
			+ fileCommPartner.getSettings().get(fileCommPartner.getFileTransferDirectory(), "")
			+ ")";
		
	}
	
	private void openPatientNotSelectedDialog(){
		MessageDialog.openError(getSite().getShell(), "Error",
			"Kein Patient ausgewählt.");
	}
	
	public String getLastExaminationId(FileCommPartner fileCommPartner){
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (pat != null) {
			String label = getLabel(fileCommPartner);
			GDTProtokoll[] prot = GDTProtokoll.getEntriesForPatient(pat);
			for (GDTProtokoll gdtProtokoll : prot) {
				if (Integer
					.parseInt(gdtProtokoll
						.getMessageType()) == GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN
					&& label.equals(gdtProtokoll.getGegenstelle())) {
					return gdtProtokoll.getId();
				}
			}
		}
		return null;
	}
}
