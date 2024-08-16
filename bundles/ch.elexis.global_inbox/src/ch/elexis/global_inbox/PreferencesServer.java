package ch.elexis.global_inbox;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.tasks.model.TaskTriggerTypeParameter;
import ch.elexis.core.ui.e4.dialog.VirtualFilesystemUriEditorDialog;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.global_inbox.core.handler.TaskManagerHandler;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.TransientCategory;
import ch.elexis.omnivore.model.util.CategoryUtil;
import ch.elexis.omnivore.ui.util.CategorySelectDialog;


public class PreferencesServer extends PreferencePage implements IWorkbenchPreferencePage {

    public static final String PREFERENCE_BRANCH = "plugins/global_inbox_server/"; //$NON-NLS-1$
    public static final String PREF_DIR = PREFERENCE_BRANCH + "dir"; //$NON-NLS-1$
    public static final String PREF_DIR_DEFAULT = StringUtils.EMPTY;
    public static final String PREF_TITLE_COMPLETION = PREFERENCE_BRANCH + "titleCompletions"; //$NON-NLS-1$
    public static final String STOREFSGLOBAL = PREFERENCE_BRANCH + "store_in_fs_global"; //$NON-NLS-1$
    public static final String PREF_DEVICES = PREFERENCE_BRANCH + "devices"; //$NON-NLS-1$
    public static final String PREF_SELECTED_DEVICE = PREFERENCE_BRANCH + "selectedDevice"; //$NON-NLS-1$
	public static final String PREF_DEVICE_DIR_PREFIX = PREFERENCE_BRANCH + "device_dir_"; //$NON-NLS-1$
	public static final String PREF_OMNIVORE_DIR_STRUCTURE = PREFERENCE_BRANCH + "omnivore_dir_structure"; //$NON-NLS-1$

	private Text newDeviceText;
	private Text deviceDirText;
	private Text omnivoreDirText;
	private Text urlText;

	private Combo deviceCombo;

	private Map<String, String> deviceDirMap = new HashMap<>();
	private Composite mainComposite;

	@Reference
	private ITaskService taskService;

	private TaskManagerHandler taskManagerHandler;

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		taskService = OsgiServiceUtil.getService(ITaskService.class).orElse(null);
		IVirtualFilesystemService virtualFilesystemService = OsgiServiceUtil.getService(IVirtualFilesystemService.class)
				.orElse(null);
		taskManagerHandler = new TaskManagerHandler(taskService, virtualFilesystemService);
	}

    public PreferencesServer() {
        setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
    }

    @Override
	protected Control createContents(Composite parent) {
		this.mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));

		Composite storeFSGlobalComposite = new Composite(mainComposite, SWT.NONE);
		storeFSGlobalComposite.setLayout(new GridLayout(1, false));
		storeFSGlobalComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Button storeFSGlobalButton = new Button(storeFSGlobalComposite, SWT.CHECK);
		storeFSGlobalButton.setText("Dateisystem Einstellungen global speichern");
		storeFSGlobalButton.setSelection(ConfigServiceHolder.getGlobal(STOREFSGLOBAL, false));
		storeFSGlobalButton.addListener(SWT.Selection,
				e -> ConfigServiceHolder.get().set(STOREFSGLOBAL, storeFSGlobalButton.getSelection()));

		Composite contentComposite = new Composite(mainComposite, SWT.NONE);
		contentComposite.setLayout(new GridLayout(3, false));
		contentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		int labelWidth = 150;

		Label deviceLabel = new Label(contentComposite, SWT.NONE);
		deviceLabel.setText("Neues Gerät hinzufügen:");
		GridData deviceLabelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		deviceLabelGridData.widthHint = labelWidth;
		deviceLabel.setLayoutData(deviceLabelGridData);

		newDeviceText = new Text(contentComposite, SWT.BORDER);
		GridData newDeviceTextGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		newDeviceText.setLayoutData(newDeviceTextGridData);

		Button addButton = new Button(contentComposite, SWT.PUSH);
		addButton.setImage(Images.IMG_NEW.getImage());
		addButton.setText("Hinzufügen");
		addButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		addButton.addListener(SWT.Selection, e -> {
			addNewDevice(newDeviceText.getText(), contentComposite);
			newDeviceText.setText("");
		});

		Label comboLabel = new Label(contentComposite, SWT.NONE);
		comboLabel.setText("Gerät auswählen:");
		GridData comboLabelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		comboLabelGridData.widthHint = labelWidth;
		comboLabel.setLayoutData(comboLabelGridData);

		deviceCombo = new Combo(contentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData deviceComboGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		deviceCombo.setLayoutData(deviceComboGridData);
		deviceCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDeviceFields();
			}
		});

		Button deleteButton = new Button(contentComposite, SWT.PUSH);
		deleteButton.setImage(Images.IMG_DELETE.getImage());
		deleteButton.setText("Löschen");
		deleteButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		deleteButton.addListener(SWT.Selection, e -> deleteSelectedDevice(deviceCombo.getText(), contentComposite));

		Label dirLabel = new Label(contentComposite, SWT.NONE);
		dirLabel.setText("Export-Verzeichnis");
		GridData dirLabelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		dirLabelGridData.widthHint = labelWidth;
		dirLabel.setLayoutData(dirLabelGridData);

		deviceDirText = new Text(contentComposite, SWT.BORDER);
		GridData deviceDirTextGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		deviceDirTextGridData.widthHint = 300;
		deviceDirText.setLayoutData(deviceDirTextGridData);

		Button browseButton = new Button(contentComposite, SWT.PUSH);
		browseButton.setText("Durchsuchen...");
		browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		browseButton.addListener(SWT.Selection, e -> {
			DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
			String dir = directoryDialog.open();
			if (dir != null) {
				deviceDirText.setText(dir);
			}
		});

		Label urlLabel = new Label(contentComposite, SWT.NONE);
		urlLabel.setText(Messages.InboxView_inbox);
		GridData urlLabelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		urlLabelGridData.widthHint = labelWidth;
		urlLabel.setLayoutData(urlLabelGridData);

		urlText = new Text(contentComposite, SWT.BORDER);
		GridData urlTextGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		urlText.setLayoutData(urlTextGridData);
		urlText.setEnabled(false);

		Button searchButton = new Button(contentComposite, SWT.PUSH);
		searchButton.setText("Durchsuchen...");
		searchButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		searchButton.addListener(SWT.Selection, e -> {
			IVirtualFilesystemService virtualFilesystemService = VirtualFilesystemServiceHolder.get();
			URI inputUri = null;
			try {
				String _urlText = urlText.getText();
				if (StringUtils.isNotBlank(_urlText)) {
					IVirtualFilesystemHandle fileHandle = virtualFilesystemService.of(_urlText, false);
					inputUri = fileHandle.toURL().toURI();
				}
			} catch (URISyntaxException | IOException ex) {
				LoggerFactory.getLogger(PreferencesServer.class).error("Error converting URL to URI", ex);
			}
			VirtualFilesystemUriEditorDialog dialog = new VirtualFilesystemUriEditorDialog(getShell(),
					virtualFilesystemService, inputUri);
			int open = dialog.open();
			if (IDialogConstants.OK_ID == open) {
				String _url = dialog.getValue().toString();
				String referenceId = deviceCombo.getText();
				taskManagerHandler.createAndConfigureTask(referenceId, _url, deviceDirText.getText());
				urlText.setText(_url);
			}
		});

		new Label(contentComposite, SWT.NONE);

		Label omnivoreDirLabel = new Label(contentComposite, SWT.NONE);
		omnivoreDirLabel.setText("Omnivore Ordner struktur:");
		GridData omnivoreDirLabelGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		omnivoreDirLabel.setLayoutData(omnivoreDirLabelGridData);

		omnivoreDirText = new Text(contentComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);
		GridData omnivoreDirTextGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3);
		omnivoreDirTextGridData.heightHint = 150;
		omnivoreDirTextGridData.widthHint = 250;
		omnivoreDirText.setLayoutData(omnivoreDirTextGridData);
		Runnable updateCategoriesText = () -> {
			List<String> categories = CategoryUtil.getCategoriesNames();
			StringBuilder categoriesText = new StringBuilder();
			for (String category : categories) {
				categoriesText.append(category).append("\n");
			}
			omnivoreDirText.setText(categoriesText.toString());
		};

		updateCategoriesText.run();

		Composite buttonComposite2 = new Composite(contentComposite, SWT.NONE);
		buttonComposite2.setLayout(new GridLayout(1, false));
		GridData buttonComposite2GridData = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 3);
		buttonComposite2.setLayoutData(buttonComposite2GridData);

		Button bNewCat = new Button(buttonComposite2, SWT.PUSH);
		bNewCat.setImage(Images.IMG_NEW.getImage());
		bNewCat.setToolTipText("Neues Verzeichnis hinzufügen");
		bNewCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog id = new InputDialog(getShell(), "Neues Verzeichnis", "Neues Verzeichnis hinzufügen:", null,
						null);
				if (id.open() == Dialog.OK) {
					CategoryUtil.addCategory(id.getValue());
					updateCategoriesText.run();
				}
			}
		});

		Button bEditCat = new Button(buttonComposite2, SWT.PUSH);
		bEditCat.setImage(Images.IMG_EDIT.getImage());
		bEditCat.setToolTipText("Verzeichnis umbenennen\nUm zu umbenennen, bitte markieren");
		bEditCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String old = omnivoreDirText.getSelectionText().trim();
				if (old.isEmpty()) {
					return;
				}
				InputDialog id = new InputDialog(getShell(), MessageFormat.format("Verzeichnis '{0}' umbenennen", old),
						"Geben Sie den neuen Namen für das Verzeichnis ein:", old, null);
				if (id.open() == Dialog.OK) {
					String nn = id.getValue();
					CategoryUtil.renameCategory(old, nn);
					updateCategoriesText.run();
				}
			}
		});

		Button bDeleteCat = new Button(buttonComposite2, SWT.PUSH);
		bDeleteCat.setImage(Images.IMG_DELETE.getImage());
		bDeleteCat.setToolTipText("Verzeichnis löschen");
		bDeleteCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String old = omnivoreDirText.getSelectionText().trim();
				if (old.isEmpty()) {
					return;
				}
				List<IDocumentHandle> documents = CategoryUtil.getDocumentsWithCategoryByName(old);
				if (documents.isEmpty()) {
					CategoryUtil.removeCategory(old, null);
					updateCategoriesText.run();
				} else {
					CategorySelectDialog catSelectDialog = new CategorySelectDialog(getShell(),
							MessageFormat.format("Kategorie " + old + " löschen", old),
							"Es gibt Dokumente in dieser Kategorie. Bitte wählen Sie eine neue Kategorie für diese Dokumente.",
							CategoryUtil.getCategoriesNames());
					if (catSelectDialog.open() == Dialog.OK) {
						String newCategory = catSelectDialog.getSelectedCategory();
						for (IDocumentHandle document : documents) {
							document.setCategory(new TransientCategory(newCategory));
						}
						CategoryUtil.removeCategory(old, newCategory);
						updateCategoriesText.run();
					}
				}
			}
		});

		loadDeviceData();
		updateDeviceCombo();
		updateDeviceFields();
		return mainComposite;
	}

	private String[][] getDeviceEntries() {
        String devices = getPreferenceStore().getString(PREF_DEVICES);
        if (StringUtils.isNotBlank(devices)) {
            String[] deviceArray = devices.split(",");
            String[][] deviceEntries = new String[deviceArray.length][2];
            for (int i = 0; i < deviceArray.length; i++) {
                deviceEntries[i][0] = deviceArray[i];
                deviceEntries[i][1] = deviceArray[i];
            }
            return deviceEntries;
        }
        return new String[0][0];
    }

	private void deleteSelectedDevice(String selectedDevice, Composite mainComposite) {
		if (StringUtils.isNotBlank(selectedDevice)) {
			String devices = getPreferenceStore().getString(PREF_DEVICES);
			String[] deviceArray = devices.split(",");
			StringBuilder newDevices = new StringBuilder();
			for (String device : deviceArray) {
				if (!device.equals(selectedDevice)) {
					if (newDevices.length() > 0) {
						newDevices.append(",");
					}
					newDevices.append(device);
				}
			}
			getPreferenceStore().setValue(PREF_DEVICES, newDevices.toString());
			getPreferenceStore().setToDefault(PREF_DEVICE_DIR_PREFIX + selectedDevice);
			getPreferenceStore().setToDefault(PREF_SELECTED_DEVICE);
			if (taskManagerHandler.getTaskDescriptorByReferenceId(selectedDevice) != null) {
				taskManagerHandler.deleteTaskDescriptorByReferenceId(selectedDevice);
			}
			updateDeviceCombo();
		}
	}

	private void addNewDevice(String deviceName, Composite mainComposite) {
        if (StringUtils.isNotBlank(deviceName)) {
            String devices = getPreferenceStore().getString(PREF_DEVICES);
            if (StringUtils.isNotBlank(devices)) {
                devices += "," + deviceName;
            } else {
                devices = deviceName;
            }
            getPreferenceStore().setValue(PREF_DEVICES, devices);
			updateDeviceCombo();
		}
	}

	private void updateDeviceCombo() {
		deviceCombo.removeAll();
		String[][] deviceEntries = getDeviceEntries();
		for (String[] deviceEntry : deviceEntries) {
			deviceCombo.add(deviceEntry[0]);
		}
		if (deviceEntries.length > 0) {
			deviceCombo.select(0);
		}
		updateDeviceFields();
	}

	private void updateDeviceFields() {
		String selectedDevice = deviceCombo.getText();
		if (StringUtils.isNotBlank(selectedDevice)) {
			String dir = getPreferenceStore().getString(PREF_DEVICE_DIR_PREFIX + selectedDevice);
			deviceDirText.setText(StringUtils.defaultString(dir));
			String url = (taskManagerHandler.getTaskDescriptorByReferenceId(selectedDevice) != null)
					? taskManagerHandler.getTaskDescriptorByReferenceId(selectedDevice).getTriggerParameters()
							.get(TaskTriggerTypeParameter.FILESYSTEM_CHANGE.URL)
					: StringUtils.EMPTY;
			urlText.setText((url != null) ? url : StringUtils.EMPTY);
		} else {
			deviceDirText.setText("");
			urlText.setText("");
		}
	}

	private void loadDeviceData() {
		String devices = getPreferenceStore().getString(PREF_DEVICES);
		if (StringUtils.isNotBlank(devices)) {
			String[] deviceArray = devices.split(",");
			for (String device : deviceArray) {
				String dir = getPreferenceStore().getString(PREF_DEVICE_DIR_PREFIX + device);
				if (StringUtils.isNotBlank(dir)) {
					deviceDirMap.put(device, dir);
				}
			}
		}
	}

	@Override
	public boolean performOk() {
		String devices = getPreferenceStore().getString(PREF_DEVICES);
		if (StringUtils.isNotBlank(devices)) {
			String[] deviceArray = devices.split(",");
			for (String device : deviceArray) {
				String dir = getPreferenceStore().getString(PREF_DEVICE_DIR_PREFIX + device);
				String url = (taskManagerHandler.getTaskDescriptorByReferenceId(device) != null)
						? taskManagerHandler.getTaskDescriptorByReferenceId(device).getTriggerParameters().get(
								TaskTriggerTypeParameter.FILESYSTEM_CHANGE.URL)
						: StringUtils.EMPTY;

				if (StringUtils.isNotBlank(dir) && StringUtils.isNotBlank(url)) {
					if (taskManagerHandler.getTaskDescriptorByReferenceId(device) == null) {
						taskManagerHandler.createAndConfigureTask(device, url, dir);
					}
				} else {
					continue;
				}
			}
		}
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		loadDeviceData();
		updateDeviceCombo();
	}
}
