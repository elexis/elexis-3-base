package ch.elexis.global_inbox;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.ui.e4.dialog.VirtualFilesystemUriEditorDialog;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.global_inbox.core.handler.TaskManagerHandler;
import ch.elexis.global_inbox.core.util.Constants;
import ch.elexis.global_inbox.ui.Messages;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.TransientCategory;
import ch.elexis.omnivore.model.util.CategoryUtil;
import ch.elexis.omnivore.ui.util.CategorySelectDialog;

public class PreferencesServer extends PreferencePage implements IWorkbenchPreferencePage {



	private Text newDeviceText;
	private Text deviceDirText;
//	private Text urlText;

	private Combo deviceCombo;

	private Map<String, String> deviceDirMap = new HashMap<>();
	private Composite mainComposite;
	private String lastSelectedCategory;
	private ListViewer categoryListViewer;
	private Combo patientSourceCombo;
	@Reference
	private ITaskService taskService;

	private TaskManagerHandler taskManagerHandler;

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		taskService = OsgiServiceUtil.getService(ITaskService.class).orElse(null);
		taskManagerHandler = new TaskManagerHandler(taskService);
		lastSelectedCategory = ConfigServiceHolder.getGlobal(Constants.PREF_LAST_SELECTED_CATEGORY, StringUtils.EMPTY);

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
		storeFSGlobalButton.setText(Messages.PreferencesServer_storeFSGlobal);
		storeFSGlobalButton.setSelection(ConfigServiceHolder.getGlobal(Constants.STOREFSGLOBAL, false));
		storeFSGlobalButton.addListener(SWT.Selection,
				e -> ConfigServiceHolder.get().set(Constants.STOREFSGLOBAL, storeFSGlobalButton.getSelection()));

		Composite contentComposite = new Composite(mainComposite, SWT.NONE);
		contentComposite.setLayout(new GridLayout(3, false));
		contentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		int labelWidth = 150;

		Label deviceLabel = new Label(contentComposite, SWT.NONE);
		deviceLabel.setText(Messages.PreferencesServer_addNewDevice);
		GridData deviceLabelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		deviceLabelGridData.widthHint = labelWidth;
		deviceLabel.setLayoutData(deviceLabelGridData);

		newDeviceText = new Text(contentComposite, SWT.BORDER);
		GridData newDeviceTextGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		newDeviceText.setLayoutData(newDeviceTextGridData);

		Button addButton = new Button(contentComposite, SWT.PUSH);
		addButton.setImage(Images.IMG_NEW.getImage());
		addButton.setText(Messages.Core_Add);
		addButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		addButton.addListener(SWT.Selection, e -> {
			addNewDevice(newDeviceText.getText(), contentComposite);
			newDeviceText.setText(StringUtils.EMPTY);
		});

		Label comboLabel = new Label(contentComposite, SWT.NONE);
		comboLabel.setText(Messages.PreferencesServer_selectDevice);
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
		deleteButton.setText(Messages.PreferencesServer_deleteButton);
		deleteButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		deleteButton.addListener(SWT.Selection, e -> deleteSelectedDevice(deviceCombo.getText(), contentComposite));

		Label dirLabel = new Label(contentComposite, SWT.NONE);
		dirLabel.setText(Messages.InboxView_inbox);
		GridData dirLabelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		dirLabelGridData.widthHint = labelWidth;
		dirLabel.setLayoutData(dirLabelGridData);

		deviceDirText = new Text(contentComposite, SWT.BORDER);
		GridData deviceDirTextGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		deviceDirTextGridData.widthHint = 300;
		deviceDirText.setLayoutData(deviceDirTextGridData);
		deviceDirText.setEnabled(false);
		Button browseButton = new Button(contentComposite, SWT.PUSH);
		browseButton.setText(Messages.PreferencesServer_browseButton);
		browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		browseButton.addListener(SWT.Selection, e -> {
			IVirtualFilesystemService virtualFilesystemService = VirtualFilesystemServiceHolder.get();
			URI inputUri = null;
			try {
				String currentDir = deviceDirText.getText();
				if (StringUtils.isNotBlank(currentDir)) {
					IVirtualFilesystemHandle fileHandle = virtualFilesystemService.of(currentDir, false);
					inputUri = fileHandle.toURL().toURI();
				}
			} catch (URISyntaxException | IOException ex) {
				LoggerFactory.getLogger(PreferencesServer.class).error("Error converting URL to URI", ex);
			}
			VirtualFilesystemUriEditorDialog dialog = new VirtualFilesystemUriEditorDialog(getShell(),
					virtualFilesystemService, inputUri);
			int result = dialog.open();

			if (IDialogConstants.OK_ID == result) {
				String selectedUri = dialog.getValue().toString();
				deviceDirText.setText(selectedUri);
			}
		});

		new Label(contentComposite, SWT.NONE);

		Label omnivoreDirLabel = new Label(contentComposite, SWT.NONE);
		omnivoreDirLabel.setText(Messages.PreferencesServer_omnivoreDirStructure);
		GridData omnivoreDirLabelGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		omnivoreDirLabel.setLayoutData(omnivoreDirLabelGridData);

		Composite categoryComposite = new Composite(contentComposite, SWT.NONE);
		GridLayout categoryLayout = new GridLayout(2, false);
		categoryLayout.marginWidth = 0;
		categoryLayout.marginHeight = 0;
		categoryComposite.setLayout(categoryLayout);

		GridData categoryCompositeGridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
		categoryCompositeGridData.widthHint = 250;
		categoryComposite.setLayoutData(categoryCompositeGridData);

		categoryListViewer = new ListViewer(categoryComposite, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
		GridData listViewerGridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		listViewerGridData.widthHint = 150;
		listViewerGridData.heightHint = 200;
		categoryListViewer.getList().setLayoutData(listViewerGridData);
		categoryListViewer.setContentProvider(ArrayContentProvider.getInstance());
		categoryListViewer.setLabelProvider(new LabelProvider());
		categoryListViewer.addSelectionChangedListener(event -> {
			IStructuredSelection selection = categoryListViewer.getStructuredSelection();
			String selectedCategory = (String) selection.getFirstElement();
			if (selectedCategory != null) {
				lastSelectedCategory = selectedCategory;
				ConfigServiceHolder.get().set(Constants.PREF_LAST_SELECTED_CATEGORY, lastSelectedCategory);
			}
		});

		Composite buttonComposite = new Composite(categoryComposite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, true));
		GridData buttonCompositeGridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		buttonCompositeGridData.widthHint = 50;
		buttonComposite.setLayoutData(buttonCompositeGridData);

		Button bNewCat = new Button(buttonComposite, SWT.PUSH);
		bNewCat.setImage(Images.IMG_NEW.getImage());
		bNewCat.setToolTipText(Messages.PreferencesServer_addNewDirectory);
		bNewCat.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		bNewCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog id = new InputDialog(getShell(), Messages.PreferencesServer_newDirectory,
						Messages.PreferencesServer_addNewDirectory, null,
						null);
				if (id.open() == Dialog.OK) {
					CategoryUtil.addCategory(id.getValue());
					addCategory(id.getValue());
					mainComposite.layout();
				}
			}
		});

		Button bEditCat = new Button(buttonComposite, SWT.PUSH);
		bEditCat.setImage(Images.IMG_EDIT.getImage());
		bEditCat.setToolTipText(Messages.PreferencesServer_renameDirectoryTooltip);
		bEditCat.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		bEditCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selectedCategory = lastSelectedCategory;
				if (selectedCategory.isEmpty()) {
					return;
				}
				InputDialog id = new InputDialog(getShell(),
						MessageFormat.format(Messages.PreferencesServer_renameDirectory, selectedCategory),
						Messages.PreferencesServer_enterNewNameForDirectory, selectedCategory, null);
				if (id.open() == Dialog.OK) {
					CategoryUtil.renameCategory(selectedCategory, id.getValue());
					renameCategory(selectedCategory, id.getValue());
				}
			}
		});

		Button bDeleteCat = new Button(buttonComposite, SWT.PUSH);
		bDeleteCat.setImage(Images.IMG_DELETE.getImage());
		bDeleteCat.setToolTipText(Messages.PreferencesServer_deleteDirectory);
		bDeleteCat.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		bDeleteCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selectedCategory = lastSelectedCategory;
				if (selectedCategory.isEmpty()) {
					return;
				}
				List<IDocumentHandle> documents = CategoryUtil.getDocumentsWithCategoryByName(selectedCategory);
				CategorySelectDialog catSelectDialog = new CategorySelectDialog(getShell(),
						MessageFormat.format(Messages.PreferencesServer_deleteCategory, selectedCategory),
						Messages.PreferencesServer_documentsInCategory,
						CategoryUtil.getCategoriesNames());
				if (catSelectDialog.open() == Dialog.OK) {
					String newCategory = catSelectDialog.getSelectedCategory();
					for (IDocumentHandle document : documents) {
						document.setCategory(new TransientCategory(newCategory));
					}
					CategoryUtil.removeCategory(selectedCategory, newCategory);
					removeCategory(selectedCategory, newCategory);
				}
			}
		});

		Label patientSourceLabel = new Label(contentComposite, SWT.NONE);
		patientSourceLabel.setText(Messages.PreferencesServer_patientSourceLabel);
		GridData patientSourceLabelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		patientSourceLabelGridData.widthHint = labelWidth;
		patientSourceLabel.setLayoutData(patientSourceLabelGridData);

		patientSourceCombo = new Combo(contentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData patientSourceComboGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		patientSourceCombo.setLayoutData(patientSourceComboGridData);

		patientSourceCombo.setItems(new String[] { Messages.PreferencesServer_patientSource_filePrefix,
				Messages.PreferencesServer_patientSource_folder, Messages.PreferencesServer_patientSource_hierarchy,
				Messages.PreferencesServer_patientSource_hybrid });

		loadDeviceData();
		updateDeviceCombo();
		updateDeviceFields();
		return mainComposite;
	}

	private void updateCategoriesListForDevice(String device) {
		List<String> allCategories = CategoryUtil.getCategoriesNames();
		categoryListViewer.setInput(allCategories);
		categoryListViewer.refresh();
		String selectedCategory = ConfigServiceHolder.getGlobal(Constants.PREF_CATEGORY_PREFIX + device,
				StringUtils.EMPTY);
		if (selectedCategory != null && !selectedCategory.isEmpty()) {
			categoryListViewer.setSelection(new org.eclipse.jface.viewers.StructuredSelection(selectedCategory));
			lastSelectedCategory = selectedCategory;
		} else {
			lastSelectedCategory = null;
		}
	}

	private void addCategory(String categoryName) {
		List<String> categories = getCategoriesForDevice(deviceCombo.getText());
		if (!categories.contains(categoryName)) {
			categories.add(categoryName);
			saveCategoriesForDevice(deviceCombo.getText(), categories);

		}
		updateCategoriesListForDevice(deviceCombo.getText());
	}

	private void renameCategory(String oldCategoryName, String newCategoryName) {
		List<String> categories = getCategoriesForDevice(deviceCombo.getText());
		int index = categories.indexOf(oldCategoryName);
		if (index != -1) {
			categories.set(index, newCategoryName);
			saveCategoriesForDevice(deviceCombo.getText(), categories);
		}
		lastSelectedCategory = newCategoryName;
		ConfigServiceHolder.get().set(Constants.PREF_LAST_SELECTED_CATEGORY, newCategoryName);
		updateCategoriesListForDevice(deviceCombo.getText());
	}

	private void removeCategory(String oldCategoryName, String newCategoryName) {
		List<String> categories = getCategoriesForDevice(deviceCombo.getText());
		categories.remove(oldCategoryName);
		saveCategoriesForDevice(deviceCombo.getText(), categories);
		updateCategoriesListForDevice(deviceCombo.getText());
	}

	private List<String> getCategoriesForDevice(String device) {
		String categoriesString = ConfigServiceHolder.getGlobal(Constants.PREF_CATEGORY_PREFIX + device,
				StringUtils.EMPTY);
		return StringUtils.isNotBlank(categoriesString) ? new ArrayList<>(List.of(categoriesString.split(",")))
				: new ArrayList<>();
	}

	private void saveCategoriesForDevice(String device, List<String> categories) {
		String categoriesString = String.join(",", categories);
		ConfigServiceHolder.get().set(Constants.PREF_CATEGORY_PREFIX + device, categoriesString);
	}

	private String[][] getDeviceEntries() {
		String devices = ConfigServiceHolder.getGlobal(Constants.PREF_DEVICES, StringUtils.EMPTY);
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
			String devices = ConfigServiceHolder.getGlobal(Constants.PREF_DEVICES, StringUtils.EMPTY);
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
			ConfigServiceHolder.get().set(Constants.PREF_DEVICES, newDevices.toString());
			ConfigServiceHolder.get().set(Constants.PREF_DEVICE_DIR_PREFIX + selectedDevice, StringUtils.EMPTY);
			ConfigServiceHolder.get().set(Constants.PREF_SELECTED_DEVICE, StringUtils.EMPTY);
			ConfigServiceHolder.get().set(Constants.PREF_CATEGORY_PREFIX + selectedDevice, StringUtils.EMPTY);
			if (taskManagerHandler.getTaskDescriptorByReferenceId(selectedDevice) != null) {

				taskManagerHandler.deleteTaskDescriptorByReferenceId(selectedDevice);
			}

			updateDeviceCombo();

			if (selectedDevice.equals(lastSelectedCategory)) {
				lastSelectedCategory = StringUtils.EMPTY;
				ConfigServiceHolder.get().set(Constants.PREF_LAST_SELECTED_CATEGORY, StringUtils.EMPTY);
			}
		}
	}

	private void addNewDevice(String deviceName, Composite mainComposite) {
        if (StringUtils.isNotBlank(deviceName)) {
			String devices = ConfigServiceHolder.getGlobal(Constants.PREF_DEVICES, StringUtils.EMPTY);
            if (StringUtils.isNotBlank(devices)) {
                devices += "," + deviceName;
            } else {
                devices = deviceName;
            }
			ConfigServiceHolder.get().set(Constants.PREF_DEVICES, devices);
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
			String dir = ConfigServiceHolder.getGlobal(Constants.PREF_DEVICE_DIR_PREFIX + selectedDevice,
					StringUtils.EMPTY);
			deviceDirText.setText(StringUtils.defaultString(dir));

			int strategyIndex = ConfigServiceHolder.getGlobal(Constants.PREF_PATIENT_STRATEGY_PREFIX + selectedDevice,
					0); // Default FILE_PREFIX
			if (strategyIndex >= 0 && strategyIndex < patientSourceCombo.getItemCount()) {
				patientSourceCombo.select(strategyIndex);
			} else {
				patientSourceCombo.select(0);
			}

			updateCategoriesListForDevice(selectedDevice);
		}
	}

	private void loadDeviceData() {
		String devices = ConfigServiceHolder.getGlobal(Constants.PREF_DEVICES, StringUtils.EMPTY);
		if (StringUtils.isNotBlank(devices)) {
			String[] deviceArray = devices.split(",");
			for (String device : deviceArray) {
				String dir = ConfigServiceHolder.getGlobal(Constants.PREF_DEVICE_DIR_PREFIX + device,
						StringUtils.EMPTY);
				if (StringUtils.isNotBlank(dir)) {
					deviceDirMap.put(device, dir);
				}
			}
		}
	}


	@Override
	public boolean performOk() {
		String selectedDevice = deviceCombo.getText();

		if (StringUtils.isNotBlank(selectedDevice)) {
			String destinationDir = deviceDirText.getText();
			if (lastSelectedCategory == null) {
				lastSelectedCategory = StringUtils.EMPTY;
			}

			ConfigServiceHolder.get().set(Constants.PREF_DEVICE_DIR_PREFIX + selectedDevice, deviceDirText.getText());
			ConfigServiceHolder.get().set(Constants.PREF_SELECTED_DEVICE, selectedDevice);
			ConfigServiceHolder.get().set(Constants.PREF_CATEGORY_PREFIX + selectedDevice, lastSelectedCategory);
			int strategyIndex = patientSourceCombo.getSelectionIndex();
			ConfigServiceHolder.get().set(Constants.PREF_PATIENT_STRATEGY_PREFIX + selectedDevice, strategyIndex);
			if (StringUtils.isNotBlank(destinationDir)) {
				taskManagerHandler.createAndConfigureTask(selectedDevice, destinationDir);
			}
		}
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		ConfigServiceHolder.get().set(Constants.PREF_SELECTED_DEVICE, StringUtils.EMPTY);
		String devices = ConfigServiceHolder.getGlobal(Constants.PREF_DEVICES, StringUtils.EMPTY);
		if (StringUtils.isNotBlank(devices)) {
			String[] deviceArray = devices.split(",");
			for (String device : deviceArray) {
				ConfigServiceHolder.get().set(Constants.PREF_DEVICE_DIR_PREFIX + device, StringUtils.EMPTY);
				ConfigServiceHolder.get().set(Constants.PREF_CATEGORY_PREFIX + device, StringUtils.EMPTY);
			}
		}
		updateDeviceCombo();
		updateDeviceFields();
	}
}
