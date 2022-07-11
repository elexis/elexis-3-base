package at.medevit.elexis.gdt.defaultfilecp.ui.view;

import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.Map;

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
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;

public class GdtView extends ViewPart {

	private Map<String, Button> mapExaminations = new HashMap<String, Button>();
	private Composite composite;

	private final ElexisUiEventListenerImpl eeli_pat = new ElexisUiEventListenerImpl(Patient.class,
			ElexisEvent.EVENT_SELECTED) {

		@Override
		public void runInUi(ElexisEvent ev) {
			refreshLastExaminations();
		}
	};

	public GdtView() {

	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		sc.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		composite = new Composite(sc, SWT.NONE);
		composite.setLayout(new GridLayout());

		sc.setContent(composite);

		createContents(composite);

		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createContents(Composite composite) {
		mapExaminations.clear();
		for (String id : FileCommPartner.getAllFileCommPartnersArray()) {
			FileCommPartner fileCommPartner = new FileCommPartner(id);
			String fileCommPartnerName = fileCommPartner.getSettings().getString(fileCommPartner.getFileTransferName());

			String lastExaminationId = getLastExaminationId(fileCommPartner);

			Composite content = new Composite(composite, SWT.BORDER);
			content.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
			content.setLayout(new GridLayout(3, false));

			Label label = new Label(content, SWT.RIGHT);
			label.setText(StringUtils.EMPTY);
			label.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
			label.setBackground(UiDesk.getColor(UiDesk.COL_LIGHTGREY));

			label = new Label(content, SWT.NONE);
			label.setText("Gerät: " + fileCommPartnerName);
			label.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

			Button btnNewExamination = new Button(content, SWT.PUSH);
			btnNewExamination.setText(StringUtils.EMPTY);
			btnNewExamination.setImage(Images.IMG_SYSTEM_MONITOR.getImage());
			btnNewExamination.setToolTipText("Neue Untersuchung anfordern");
			btnNewExamination.setData(fileCommPartner.getId());
			btnNewExamination.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {
					if (ElexisEventDispatcher.getSelectedPatient() == null) {
						openPatientNotSelectedDialog();
					} else {
						ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
								.getService(ICommandService.class);
						Command command = commandService
								.getCommand("at.medevit.elexis.gdt.command.NeueUntersuchungAnfordern"); //$NON-NLS-1$
						if (command != null) {
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("at.medevit.elexis.gdt.cmd.parameter.targetId", //$NON-NLS-1$
									String.valueOf(((Button) event.getSource()).getData()));

							ExecutionEvent ee = new ExecutionEvent(command, params, null, getSite().getPage());
							try {
								command.executeWithChecks(ee);
							} catch (CommandException e) {
								LoggerFactory.getLogger(GdtView.class).error("command execution", e); //$NON-NLS-1$
								openPatientNotSelectedDialog();
							}
						}
					}

				}
			});

			Button btnBaseDataRequest = new Button(content, SWT.PUSH);
			btnBaseDataRequest.setText(StringUtils.EMPTY);
			btnBaseDataRequest.setImage(Images.IMG_USER_IDLE.getImage());
			btnBaseDataRequest.setToolTipText("Stammdaten übermitteln");
			btnBaseDataRequest.setData(fileCommPartner.getId());
			btnBaseDataRequest.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {

					if (ElexisEventDispatcher.getSelectedPatient() == null) {
						openPatientNotSelectedDialog();
					} else {
						ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
								.getService(ICommandService.class);
						Command command = commandService
								.getCommand("at.medevit.elexis.gdt.command.StammdatenUebermitteln"); //$NON-NLS-1$
						if (command != null) {
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("at.medevit.elexis.gdt.cmd.parameter.targetId", //$NON-NLS-1$
									String.valueOf(((Button) event.getSource()).getData()));

							ExecutionEvent ee = new ExecutionEvent(command, params, null, getSite().getPage());
							try {
								command.executeWithChecks(ee);
							} catch (CommandException e) {
								LoggerFactory.getLogger(GdtView.class).error("command execution", e); //$NON-NLS-1$
								MessageDialog.openError(getSite().getShell(), "Error",
										"Stammdatenübermittlung fehlgeschlagen.");
							}
						}
					}
				}
			});

			// tooltips are not shown for disabled buttons therefore we use a composite as
			// wrapper with that tooltip
			String lastExaminationToolTip = "Letzte Untersuchung anzeigen";
			Composite lastExaminationComposite = new Composite(content, SWT.NONE);
			lastExaminationComposite.setLayout(new GridLayout(1, false));
			lastExaminationComposite.setToolTipText(lastExaminationToolTip);

			Button btnShowLastExamination = new Button(lastExaminationComposite, SWT.PUSH);
			btnShowLastExamination.setText(StringUtils.EMPTY);
			btnShowLastExamination.setToolTipText(lastExaminationToolTip);
			btnShowLastExamination.setImage(Images.IMG_EYE_WO_SHADOW.getImage());
			btnShowLastExamination.setData(lastExaminationId);
			btnShowLastExamination.setEnabled(lastExaminationId != null);
			btnShowLastExamination.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {

					ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
							.getService(ICommandService.class);
					Command command = commandService
							.getCommand("at.medevit.elexis.gdt.command.DatenEinerUntersuchungAnzeigen"); //$NON-NLS-1$
					if (command != null) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("at.medevit.elexis.gdt.command.DatenEinerUntersuchungAnzeigen.gdtProtokollSource", //$NON-NLS-1$
								String.valueOf(((Button) event.getSource()).getData()));
						ExecutionEvent ee = new ExecutionEvent(command, params, null, getSite().getPage());
						try {
							command.executeWithChecks(ee);
						} catch (CommandException e) {
							LoggerFactory.getLogger(GdtView.class).error("command execution", e); //$NON-NLS-1$
							MessageDialog.openError(getSite().getShell(), "Error",
									"Letzte Untersuchung anzeigen fehlgeschlagen.");
						}
					}
				}

			});

			if (id != null) {
				mapExaminations.put(id, btnShowLastExamination);
			}
			ElexisEventDispatcher.getInstance().addListeners(eeli_pat);
		}
	}

	@Override
	public void setFocus() {
	}

	private String getLabel(FileCommPartner fileCommPartner) {
		return fileCommPartner.getSettings().getString(fileCommPartner.getFileTransferName()) + " (" //$NON-NLS-1$
				+ fileCommPartner.getSettings().getString(fileCommPartner.getFileTransferDirectory()) + ")"; //$NON-NLS-1$
	}

	private void openPatientNotSelectedDialog() {
		MessageDialog.openError(getSite().getShell(), "Error", "Kein Patient ausgewählt.");
	}

	public String getLastExaminationId(FileCommPartner fileCommPartner) {
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (pat != null) {
			String label = getLabel(fileCommPartner);
			GDTProtokoll[] prot = GDTProtokoll.getEntriesForPatient(pat);
			for (GDTProtokoll gdtProtokoll : prot) {
				if (Integer.parseInt(
						gdtProtokoll.getMessageType()) == GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN
						&& label.equals(gdtProtokoll.getGegenstelle())) {
					return gdtProtokoll.getId();
				}
			}
		}
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		ElexisEventDispatcher.getInstance().removeListeners(eeli_pat);
	}

	private void refreshLastExaminations() {
		if (mapExaminations != null) {
			for (String id : mapExaminations.keySet()) {
				Button btn = mapExaminations.get(id);
				if (btn != null) {
					String exId = getLastExaminationId(new FileCommPartner(id));
					btn.setData(exId);
					btn.setEnabled(exId != null);
				}
			}

			if (composite != null) {
				composite.layout(true);
			}
		}
	}
}
