package ch.elexis.labor.medics.v2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.labor.medics.v2.order.WebAis;

public class MedicsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String DOWNLOAD_DIR = "medics/download"; //$NON-NLS-1$
	public static final String IMED_DIR = "medics/uploadimed"; //$NON-NLS-1$
	public static final String ARCHIV_DIR = "medics/archiv"; //$NON-NLS-1$
	public static final String ERROR_DIR = "medics/error"; //$NON-NLS-1$
	public static final String DOKUMENT_CATEGORY = "medics/extern"; //$NON-NLS-1$
	public static final String DELETE_ARCHIV_DAYS = "medics/del_archiv/days"; //$NON-NLS-1$

	private static final String DEFAULT_DOWNLOAD = StringUtils.EMPTY;
	private static final String DEFAULT_IMED = StringUtils.EMPTY;
	private static final String DEFAULT_ARCHIV = StringUtils.EMPTY;
	private static final String DEFAULT_DOKUMENT_CATEGORY = Messages.MedicsPreferencePage_documentCategoryName;
	private static final int DEFAULT_DELETE_ARCHIV_DAYS = 30;

	private List<WebAisMandatorComposite> webaisMandators;

	public MedicsPreferencePage() {
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		getPreferenceStore().setDefault(DOWNLOAD_DIR, DEFAULT_DOWNLOAD);
		getPreferenceStore().setDefault(IMED_DIR, DEFAULT_IMED);
		getPreferenceStore().setDefault(ARCHIV_DIR, DEFAULT_ARCHIV);
		getPreferenceStore().setDefault(DOKUMENT_CATEGORY, DEFAULT_DOKUMENT_CATEGORY);
		getPreferenceStore().setDefault(DELETE_ARCHIV_DAYS, DEFAULT_DELETE_ARCHIV_DAYS);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Composite fieldEditorContainer = new Composite(container, SWT.NONE);
		fieldEditorContainer.setLayout(new GridLayout());
		fieldEditorContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// create field editors
		super.createContents(fieldEditorContainer);
		getFieldEditorParent().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Composite mandatorsContainer = new Composite(container, SWT.NONE);
		mandatorsContainer.setLayout(new GridLayout());
		mandatorsContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		if (ContextServiceHolder.get().getActiveUser().isPresent()
				&& ContextServiceHolder.get().getActiveUser().get().isAdministrator()) {
			Button useTestBtn = new Button(mandatorsContainer, SWT.CHECK);
			useTestBtn.setText("Test Portal URL verwenden (nur als Administrator)");
			useTestBtn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean value = ConfigServiceHolder.get().get(WebAis.CFG_MEDICS_LABORDER_TESTMODE, false);

					ConfigServiceHolder.get().set(WebAis.CFG_MEDICS_LABORDER_TESTMODE, !value);
				}
			});
			useTestBtn.setSelection(ConfigServiceHolder.get().get(WebAis.CFG_MEDICS_LABORDER_TESTMODE, false));
		}
		List<IMandator> mandators = CoreModelServiceHolder.get().getQuery(IMandator.class).execute();
		mandators.sort((l, r) -> {
			return l.getLabel().compareTo(r.getLabel());
		});
		webaisMandators = new ArrayList<>();
		for (IMandator mandator : mandators) {
			WebAisMandatorComposite composite = new WebAisMandatorComposite(mandator, this, mandatorsContainer,
					SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			webaisMandators.add(composite);
		}

		return container;
	}

	public void updateMandatorComposites() {
		webaisMandators.forEach(w -> w.updateCredentials());
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(DOWNLOAD_DIR, Messages.MedicsPreferencePage_labelDownloadDir,
				getFieldEditorParent()));
		addField(new DirectoryFieldEditor(IMED_DIR, Messages.MedicsPreferencePage_labelUploadDirimed,
				getFieldEditorParent()));
		addField(new DirectoryFieldEditor(ARCHIV_DIR, Messages.MedicsPreferencePage_labelArchivDir,
				getFieldEditorParent()));
		addField(new DirectoryFieldEditor(ERROR_DIR, Messages.MedicsPreferencePage_labelErrorDir,
				getFieldEditorParent()));
		addField(new StringFieldEditor(DOKUMENT_CATEGORY, Messages.MedicsPreferencePage_labelDocumentCategory,
				getFieldEditorParent()));
		addField(new StringFieldEditor(DELETE_ARCHIV_DAYS, "Archiv bereinigen (Tage)", getFieldEditorParent()));
	}

	public static String getDownloadDir() {
		return CoreHub.localCfg.get(DOWNLOAD_DIR, DEFAULT_DOWNLOAD);
	}

	public static String getDokumentKategorie() {
		return CoreHub.localCfg.get(DOKUMENT_CATEGORY, DEFAULT_DOKUMENT_CATEGORY);
	}

	public static int getDeleteArchivDays() {
		return CoreHub.localCfg.get(DELETE_ARCHIV_DAYS, DEFAULT_DELETE_ARCHIV_DAYS);
	}

	public static String getUploadDirimed() {
		return CoreHub.localCfg.get(IMED_DIR, DEFAULT_IMED);
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
