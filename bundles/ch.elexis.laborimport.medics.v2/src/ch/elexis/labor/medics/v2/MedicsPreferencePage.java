package ch.elexis.labor.medics.v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.model.IMandator;
import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.core.ui.e4.jface.preference.OsPathEditorGroup;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.labor.medics.v2.order.WebAis;

public class MedicsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String DOWNLOAD_DIR = "medics/download"; //$NON-NLS-1$
	public static final String UPLOAD_DIR = "medics/upload"; //$NON-NLS-1$
	public static final String IMED_DIR = "medics/uploadimed"; //$NON-NLS-1$
	public static final String ARCHIV_DIR = "medics/archiv"; //$NON-NLS-1$
	public static final String ERROR_DIR = "medics/error"; //$NON-NLS-1$
	public static final String DOKUMENT_CATEGORY = "medics/extern"; //$NON-NLS-1$
	public static final String DELETE_ARCHIV_DAYS = "medics/del_archiv/days"; //$NON-NLS-1$
	public static final String PATHS_GLOBAL = "medics/paths_global"; //$NON-NLS-1$

	private static final String DEFAULT_DOWNLOAD = StringUtils.EMPTY;
	private static final String DEFAULT_UPLOAD = StringUtils.EMPTY;
	private static final String DEFAULT_IMED = StringUtils.EMPTY;
	private static final String DEFAULT_ARCHIV = StringUtils.EMPTY;
	private static final String DEFAULT_DOKUMENT_CATEGORY = Messages.MedicsPreferencePage_documentCategoryName;
	private static final int DEFAULT_DELETE_ARCHIV_DAYS = 30;

	public static final String CFG_MEDICS_ORDER_API = "medics/order_api"; //$NON-NLS-1$

	private List<WebAisMandatorComposite> webaisMandators;
	private Composite mandatorsContainer;

	private BooleanFieldEditor bStoreGlobal;

	private OsPathEditorGroup pathGroup;

	public MedicsPreferencePage() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.LOCAL));
		getPreferenceStore().setDefault(DOWNLOAD_DIR, DEFAULT_DOWNLOAD);
		getPreferenceStore().setDefault(UPLOAD_DIR, DEFAULT_UPLOAD);
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

		bStoreGlobal.setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		bStoreGlobal.load();
		updatePathStores(ConfigServiceHolder.getGlobal(PATHS_GLOBAL, false));

		Button btnChangeApi = new Button(container, SWT.PUSH);
		btnChangeApi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (StringUtils.isEmpty(ConfigServiceHolder.get().get(CFG_MEDICS_ORDER_API, ""))) {
					ConfigServiceHolder.get().set(CFG_MEDICS_ORDER_API, "web");
					btnChangeApi.setText("Wechsel auf HL7 API");

					mandatorsContainer.setVisible(true);
					((GridData) mandatorsContainer.getLayoutData()).exclude = false;
				} else {
					ConfigServiceHolder.get().set(CFG_MEDICS_ORDER_API, "");
					btnChangeApi.setText("Wechsel auf Web API (neu)");

					mandatorsContainer.setVisible(false);
					((GridData) mandatorsContainer.getLayoutData()).exclude = true;
				}
				layoutParentShell(container);
			}
		});
		btnChangeApi.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		mandatorsContainer = new Composite(container, SWT.NONE);
		mandatorsContainer.setLayout(new GridLayout());
		mandatorsContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label separator = new Label(mandatorsContainer, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

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

		if (StringUtils.isEmpty(ConfigServiceHolder.get().get(CFG_MEDICS_ORDER_API, ""))) {
			btnChangeApi.setText("Wechsel auf Web API (neu)");
			mandatorsContainer.setVisible(false);
			((GridData) mandatorsContainer.getLayoutData()).exclude = true;
		} else {
			btnChangeApi.setText("Wechsel auf HL7 API");
			mandatorsContainer.setVisible(true);
			((GridData) mandatorsContainer.getLayoutData()).exclude = false;
		}

		return container;
	}

	public void updateMandatorComposites() {
		webaisMandators.forEach(w -> w.updateCredentials());
	}

	@Override
	protected void createFieldEditors() {
		bStoreGlobal = new BooleanFieldEditor(PATHS_GLOBAL,
				ch.elexis.core.l10n.Messages.PreferencesServer_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(PATHS_GLOBAL, global);
					updatePathStores(global);
				}
			}
		};
		addField(bStoreGlobal);

		pathGroup = new OsPathEditorGroup(getFieldEditorParent(), SWT.NONE);
		pathGroup.addPathEditor(DOWNLOAD_DIR, Messages.MedicsPreferencePage_labelDownloadDir);
		pathGroup.addPathEditor(UPLOAD_DIR, Messages.MedicsPreferencePage_labelUploadDir);
		pathGroup.addPathEditor(IMED_DIR, Messages.MedicsPreferencePage_labelUploadDirimed);
		pathGroup.addPathEditor(ARCHIV_DIR, Messages.MedicsPreferencePage_labelArchivDir);
		pathGroup.addPathEditor(ERROR_DIR, Messages.MedicsPreferencePage_labelErrorDir);

		addField(new StringFieldEditor(DOKUMENT_CATEGORY, Messages.MedicsPreferencePage_labelDocumentCategory,
				getFieldEditorParent()));
		addField(new StringFieldEditor(DELETE_ARCHIV_DAYS, "Archiv bereinigen (Tage)", getFieldEditorParent()));
	}

	private void updatePathStores(boolean global) {
		pathGroup.setPreferenceStore(new ConfigServicePreferenceStore(global ? Scope.GLOBAL : Scope.LOCAL));
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		pathGroup.adjustHorizontalSpan();
	}

	public static String getDownloadDir() {
		return getPath(DOWNLOAD_DIR);
	}

	public static String getDokumentKategorie() {
		return LocalConfigService.get(DOKUMENT_CATEGORY, DEFAULT_DOKUMENT_CATEGORY);
	}

	public static int getDeleteArchivDays() {
		return ConfigServiceHolder.get().getLocal(DELETE_ARCHIV_DAYS, DEFAULT_DELETE_ARCHIV_DAYS);
	}

	public static String getUploadDirimed() {
		return getPath(IMED_DIR);
	}

	public static String getUploadDir() {
		return getPath(UPLOAD_DIR);
	}

	private static String getPath(String preference) {
		IConfigService configService = ConfigServiceHolder.get();
		String value = configService.get(PATHS_GLOBAL, false)
				? PreferencesUtil.getOsSpecificGlobalPreference(preference, configService)
				: PreferencesUtil.getOsSpecificLocalPreference(preference, configService);
		return StringUtils.defaultString(value);
	}

	public static IVirtualFilesystemHandle getDirectoryHandle(String pathOrUrl) throws IOException {
		IVirtualFilesystemService vfsService = VirtualFilesystemServiceHolder.get();
		return IVirtualFilesystemService.isUrl(pathOrUrl) ? vfsService.of(pathOrUrl)
				: vfsService.of(new File(pathOrUrl));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	public static void layoutParentShell(Composite composite) {
		Composite parent = composite.getParent();
		while (parent != null && !(parent instanceof Shell)) {
			parent = parent.getParent();
		}
		if (parent instanceof Shell) {
			parent.layout(true, true);
		}
	}
}
