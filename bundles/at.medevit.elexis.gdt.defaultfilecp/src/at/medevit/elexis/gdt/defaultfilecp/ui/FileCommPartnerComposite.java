package at.medevit.elexis.gdt.defaultfilecp.ui;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.defaultfilecp.FileCommPartner;
import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.jface.preference.OsPathEditorGroup;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.core.utils.CoreUtil.OS;

public class FileCommPartnerComposite extends Composite {

	private final FileCommPartner fileCommPartner;
	private final IPreferencePage preferencePage;
	private final ScrolledComposite scrolledComposite;

	private Text txtName;
	private Text txtIdReceiver;
	private Text txtIdShortReceiver;
	private OsPathEditorGroup pathGroup;
	private Text txtAdditionalParam;
	private Button btnExecutableWait;
	private Button[] btnFileTypes = new Button[2];
	private Text txtGuvkDefault;

	public FileCommPartnerComposite(IPreferencePage preferencePage, ScrolledComposite scrolledComposite,
			Composite editorParent, FileCommPartner fileCommPartner) {
		super(editorParent, SWT.BORDER);
		this.preferencePage = preferencePage;
		this.fileCommPartner = fileCommPartner;
		createElements();
		this.scrolledComposite = scrolledComposite;
		refreshParent(getParent());
	}

	private void createElements() {
		this.setLayoutData(new GridData(SWT.FILL, SWT.BORDER, true, false, 3, 1));
		this.setLayout(new GridLayout(3, false));

		GridData gridData1Col = SWTHelper.getFillGridData(1, true, 1, false);
		gridData1Col.widthHint = 120;
		GridData gridData2Col = SWTHelper.getFillGridData(2, true, 1, false);
		GridData gridData3Col = SWTHelper.getFillGridData(3, true, 1, false);

		Label label = new Label(this, SWT.RIGHT);
		label.setText(StringUtils.EMPTY);
		label.setLayoutData(gridData3Col);
		label.setBackground(UiDesk.getColor(UiDesk.COL_LIGHTGREY));

		new Label(this, SWT.NONE).setText("Gerätename");
		txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(gridData2Col);
		txtName.setText(getValueByConfigKey(fileCommPartner.getFileTransferName()));

		new Label(this, SWT.NONE).setText("Lange GDT ID Receiver");
		txtIdReceiver = new Text(this, SWT.BORDER);
		txtIdReceiver.setLayoutData(gridData2Col);
		txtIdReceiver.setText(getValueByConfigKey(fileCommPartner.getFileTransferIdReceiver()));

		new Label(this, SWT.NONE).setText("Kurze GDT ID Receiver");
		txtIdShortReceiver = new Text(this, SWT.BORDER);
		txtIdShortReceiver.setLayoutData(gridData2Col);
		txtIdShortReceiver.setText(getValueByConfigKey(fileCommPartner.getFileTransferShortIdReceiver()));

		pathGroup = new OsPathEditorGroup(this, SWT.NONE);
		pathGroup.setPreferenceStore(fileCommPartner.getSettings());
		pathGroup.addPathEditor(fileCommPartner.getFileTransferDirectory(), "Standard-Austausch-Verzeichnis");
		pathGroup.addPathEditor(fileCommPartner.getFileTransferInDirectory(), "Verzeichnis Eingehend");
		pathGroup.addPathEditor(fileCommPartner.getFileTransferOutDirectory(), "Verzeichnis Ausgehend");
		pathGroup.addPathEditor(fileCommPartner.getFileTransferExecuteable(), "Verarbeitungsprogramm");
		pathGroup.addPathEditor(fileCommPartner.getFileTransferViewerExecuteable(), "Anzeigeprogramm");

		new Label(this, SWT.NONE).setText("GuvK (8402)");
		txtGuvkDefault = new Text(this, SWT.BORDER);
		txtGuvkDefault.setLayoutData(gridData1Col);
		txtGuvkDefault.setText(getValueByConfigKey(fileCommPartner.getGuvkDefault()));
		txtGuvkDefault.setTextLimit(6);

		Group groupFileTypes = new Group(this, SWT.SHADOW_IN);
		groupFileTypes.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		groupFileTypes.setText("Zu verwendender Dateityp");
		groupFileTypes.setLayout(new RowLayout(SWT.VERTICAL));
		btnFileTypes[0] = new Button(groupFileTypes, SWT.RADIO);
		btnFileTypes[0].setText("fest");
		btnFileTypes[0].setSelection(GDTConstants.GDT_FILETRANSFER_TYP_FEST
				.equals(getValueByConfigKey(fileCommPartner.getFileTransferUsedType())));

		btnFileTypes[1] = new Button(groupFileTypes, SWT.RADIO);
		btnFileTypes[1].setText("hochzählend");
		btnFileTypes[1].setSelection(GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND
				.equals(getValueByConfigKey(fileCommPartner.getFileTransferUsedType())));

		new Label(this, SWT.NONE).setText("Zusatzparameter");
		txtAdditionalParam = new Text(this, SWT.BORDER);
		txtAdditionalParam.setLayoutData(gridData2Col);
		txtAdditionalParam.setText(getValueByConfigKey(fileCommPartner.getFileAdditionalParams()));

		Label seperator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperator.setLayoutData((new GridData(SWT.FILL, SWT.NONE, true, true, 3, 1)));

		new Label(this, SWT.NONE).setText("Auf Programm Antwort warten");
		btnExecutableWait = new Button(this, SWT.CHECK);
		btnExecutableWait.setLayoutData(gridData2Col);
		btnExecutableWait.setSelection(getBooleanValueByConfigKey(fileCommPartner.getFileTransferExecuteableWait()));

		Label itemText = new Label(this, SWT.NONE);
		itemText.setText("Gerät");

		Composite compositeBtns = new Composite(this, SWT.NONE);
		compositeBtns.setLayout(new GridLayout(2, false));
		compositeBtns.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
		Button btnRemove = new Button(compositeBtns, SWT.CENTER);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				remove();
			}
		});
		btnRemove.setText("Entfernen");
		btnRemove.setEnabled(!FileCommPartner.DEFAULT_COMM_PARTNER_ID.equals(fileCommPartner.getId()));

		Button btnAdd = new Button(compositeBtns, SWT.CENTER);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog inDlg = new InputDialog(getShell(), "Gerät", "Gerät hinzufügen", StringUtils.EMPTY, null);
				if (inDlg.open() == Dialog.OK) {
					String id = UUID.randomUUID().toString();
					if (!add(id, inDlg.getValue())) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
								"Dieses Gerät wird bereits verwendet.");
					}
				}
			}
		});
		btnAdd.setText("Hinzufügen");
	}

	private Boolean getBooleanValueByConfigKey(String cfgKey) {
		return fileCommPartner.getSettings().getBoolean(cfgKey);
	}

	private String getValueByConfigKey(String cfgKey) {
		return fileCommPartner.getSettings().getString(cfgKey);
	}

	private boolean add(String id, String name) {
		if (addFileCommPartner(id)) {
			((GDTPreferencePageFileTransfer) preferencePage).createNewFileCommPartnerComposite(id, name,
					scrolledComposite);
			return true;
		}
		return false;
	}

	private boolean addFileCommPartner(String id) {
		String cfg = getAllFileCommPartners();
		if (!cfg.contains(id)) {
			updateFileCommPartner(cfg + FileCommPartner.COMM_PARTNER_SEPERATOR + id);
			return true;
		}
		return false;

	}

	private String getAllFileCommPartners() {
		return StringUtils.defaultIfBlank(
				fileCommPartner.getSettings().getString(FileCommPartner.CFG_GDT_FILETRANSFER_IDS),
				FileCommPartner.DEFAULT_COMM_PARTNER_ID);
	}

	private void removeFileCommPartner(String id) {
		String cfg = getAllFileCommPartners();
		if (cfg.contains(id)) {
			String newCfg = cfg.replaceFirst(FileCommPartner.COMM_PARTNER_SEPERATOR + id, StringUtils.EMPTY);
			updateFileCommPartner(newCfg);
		}
	}

	private void updateFileCommPartner(String cfg) {
		fileCommPartner.getSettings().setValue(FileCommPartner.CFG_GDT_FILETRANSFER_IDS, cfg);
	}

	/**
	 * Clear a path, the key without operating system suffix as well as the keys of
	 * all operating systems.
	 *
	 * @param preferenceName
	 */
	private void clearPath(String preferenceName) {
		fileCommPartner.getSettings().setValue(preferenceName, null);
		for (OS os : CoreUtil.OS.values()) {
			fileCommPartner.getSettings().setValue(PreferencesUtil.getOsSpecificPreferenceName(os, preferenceName),
					null);
		}
	}

	private void remove() {
		removeFileCommPartner(fileCommPartner.getId());
		fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferIdReceiver(), null);
		fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferShortIdReceiver(), null);
		clearPath(fileCommPartner.getFileTransferDirectory());
		clearPath(fileCommPartner.getFileTransferInDirectory());
		clearPath(fileCommPartner.getFileTransferOutDirectory());
		fileCommPartner.getSettings().setValue(fileCommPartner.getGuvkDefault(), null);
		fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferUsedType(), null);
		clearPath(fileCommPartner.getFileTransferExecuteable());
		fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferExecuteableWait(), null);
		clearPath(fileCommPartner.getFileTransferViewerExecuteable());
		fileCommPartner.getSettings().setValue(fileCommPartner.getFileAdditionalParams(), null);
		fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferName(), null);

		Composite parent = getParent();
		dispose();
		refreshParent(parent);
	}

	public void save() {
		if (!isDisposed()) {
			fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferIdReceiver(),
					txtIdReceiver.getText());
			fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferShortIdReceiver(),
					txtIdShortReceiver.getText());
			fileCommPartner.getSettings().setValue(fileCommPartner.getGuvkDefault(), txtGuvkDefault.getText());
			fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferUsedType(),
					btnFileTypes[1].getSelection() ? GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND
							: GDTConstants.GDT_FILETRANSFER_TYP_FEST);
			fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferExecuteableWait(),
					btnExecutableWait.getSelection());
			fileCommPartner.getSettings().setValue(fileCommPartner.getFileAdditionalParams(),
					txtAdditionalParam.getText());
			fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferName(), txtName.getText());
		}
	}

	private void refreshParent(Composite parent) {
		if (scrolledComposite != null) {
			scrolledComposite.setMinSize(parent.getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
		parent.layout();
	}
}
