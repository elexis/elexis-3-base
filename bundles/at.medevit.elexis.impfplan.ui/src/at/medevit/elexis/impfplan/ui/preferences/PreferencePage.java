package at.medevit.elexis.impfplan.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.wb.swt.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.impfplan.ui.VaccinationView;
import at.medevit.elexis.impfplan.ui.handlers.ImportLegacyVaccinationsHandler;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.util.SWTHelper;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private static Logger log = LoggerFactory.getLogger(PreferencePage.class);

	public static final String ID = "at.medevit.elexis.impfplan.ui.preferences";

	public static final String PREFBASE = "plugins/impfplan/";
	public static final String VAC_PDF_OUTPUTDIR = PREFBASE + "outputdir";
	public static final String VAC_SORT_ORDER = PREFBASE + "sortorder";
	public static final String VAC_BILLING_POS = PREFBASE + "defleistungen";
	public static final String VAC_SHOW_SIDE = PREFBASE + "showside";
	public static final String VAC_AUTO_BILL = PREFBASE + "autobill";
	public static final String VAC_DEFAULT_SIDE = PREFBASE + "defaultside";

	private Text txtLog;
	private Label lblInfo;
	private Button btnImport;
	private boolean visibleImportPart;

	public PreferencePage() {
		try {
			Class formerVaccClass = Class.forName("ch.elexis.impfplan.model.Vaccination");
			visibleImportPart = true;
		} catch (ClassNotFoundException e1) {
			log.debug("ch.elexis.impfplan Plugin not present - disable import in preferences");
			visibleImportPart = false;
		}
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.USER));
		getPreferenceStore().setDefault(VAC_AUTO_BILL, true);
	}

	@Override
	protected void createFieldEditors() {
		DirectoryFieldEditor editor = new DirectoryFieldEditor(VAC_PDF_OUTPUTDIR, "Druck-Ausgabeverzeichnis",
				getFieldEditorParent());
		addField(editor);

		BooleanFieldEditor bfAutoBillEditor = new BooleanFieldEditor(VAC_AUTO_BILL,
				"Impfungen automatisch mit Position 00.0010 verrechnen", getFieldEditorParent());
		addField(bfAutoBillEditor);

		BooleanFieldEditor bfEditor = new BooleanFieldEditor(VAC_SORT_ORDER, "Sortierung von neu-alt (neueste oben)",
				getFieldEditorParent());
		addField(bfEditor);

		BooleanFieldEditor bfShowSideEditor = new BooleanFieldEditor(VAC_SHOW_SIDE,
				"Seite (auf welcher geimpft wurde) einblenden", getFieldEditorParent());
		addField(bfShowSideEditor);

		RadioGroupFieldEditor radioGroup = new RadioGroupFieldEditor(VAC_DEFAULT_SIDE,
				"Standard Seite (nur relevant, wenn Seite einblenden aktiv ist)", 2,
				new String[][] { { "links", "left" }, { "rechts", "right" } }, getFieldEditorParent(), true);
		addField(radioGroup);

		Composite area = new Composite(getFieldEditorParent().getParent(), SWT.NONE);
		area.setLayoutData(new GridData(GridData.FILL_BOTH));
		area.setLayout(new GridLayout(1, false));

		lblInfo = new Label(area, SWT.NONE);
		lblInfo.setText("Um Impfungen aus dem früherem Impfplan zu importieren drücken Sie 'Import starten'");

		btnImport = new Button(area, SWT.PUSH);
		btnImport.setImage(ResourceManager.getPluginImage("at.medevit.elexis.impfplan.ui", "rsc/icons/start_task.png"));
		btnImport.setText("Import starten");
		btnImport.setToolTipText("Impfungen aus anderem Impflan importieren");
		btnImport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
						.getService(IHandlerService.class);
				try {
					Object returnMsg = handlerService.executeCommand(ImportLegacyVaccinationsHandler.COMMAND_ID, null);
					if (returnMsg != null) {
						txtLog.setText((String) returnMsg);
					}
				} catch (Exception ex) {
					log.error(
							"Exception while trying to execute command: " + ImportLegacyVaccinationsHandler.COMMAND_ID,
							ex);
					SWTHelper.showError("Fehler", "Fehler beim Versuch den Impf-Import auszuführen!");
				}
			};
		});

		txtLog = new Text(area, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtLog.setText("Import log...");

		setImportFieldVisibility();
	}

	private void setImportFieldVisibility() {
		lblInfo.setVisible(visibleImportPart);
		btnImport.setVisible(visibleImportPart);
		txtLog.setVisible(visibleImportPart);
	}

	@Override
	public boolean performOk() {
		VaccinationView vaccView = (VaccinationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView(VaccinationView.PART_ID);
		if (vaccView != null) {
			vaccView.updateUi(true); // as query needs to be ordered
		}
		return super.performOk();
	}
}
