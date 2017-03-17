package at.medevit.elexis.gdt.defaultfilecp.ui;

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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.defaultfilecp.FileCommPartner;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;

public class FileCommPartnerComposite extends Composite {
	
	private final FileCommPartner fileCommPartner;
	private final IPreferencePage preferencePage;
	private final ScrolledComposite scrolledComposite;
	
	private Text txtIdReceiver;
	private Text txtIdShortReceiver;
	private Text txtExchangeDir;
	private Text txtExchangeInDir;
	private Text txtExchangeOutDir;
	private Text txtExecutable;
	private Text txtAdditionalParam;
	private Button[] btnFileTypes = new Button[2];
	
	public FileCommPartnerComposite(IPreferencePage preferencePage, Composite editorParent,
		FileCommPartner fileCommPartner){
		super(editorParent, SWT.BORDER);
		this.preferencePage = preferencePage;
		this.fileCommPartner = fileCommPartner;
		createElements();
		scrolledComposite = findScrolledComposite();
	}
	
	private void createElements(){
		this.setLayoutData(new GridData(SWT.FILL, SWT.BORDER, true, false, 3, 1));
		this.setLayout(new GridLayout(3, false));
		
		GridData gridData1Col = SWTHelper.getFillGridData(1, true, 1, false);
		gridData1Col.widthHint = 150;
		GridData gridData2Col = SWTHelper.getFillGridData(2, true, 1, false);
		GridData gridData3Col = SWTHelper.getFillGridData(3, true, 1, false);
		
		Label label = new Label(this, SWT.CENTER);
		label.setText(fileCommPartner.getName());
		label.setLayoutData(gridData3Col);
		label.setBackground(UiDesk.getColor(UiDesk.COL_LIGHTGREY));
		
		new Label(this, SWT.NONE).setText("Lange GDT ID Receiver");
		txtIdReceiver = new Text(this, SWT.BORDER);
		txtIdReceiver.setLayoutData(gridData2Col);
		txtIdReceiver.setText(getValueByConfigKey(fileCommPartner.getFileTransferIdReceiver()));
		
		new Label(this, SWT.NONE).setText("Kurze GDT ID Receiver");
		txtIdShortReceiver = new Text(this, SWT.BORDER);
		txtIdShortReceiver.setLayoutData(gridData2Col);
		txtIdShortReceiver
			.setText(getValueByConfigKey(fileCommPartner.getFileTransferShortIdReceiver()));

		new Label(this, SWT.NONE).setText("Standard-Austausch-Verzeichnis");
		txtExchangeDir = new Text(this, SWT.BORDER);
		txtExchangeDir.setLayoutData(gridData1Col);
		txtExchangeDir.setText(getValueByConfigKey(fileCommPartner.getFileTransferDirectory()));
		
		Button btnExchangeDir = new Button(this, SWT.PUSH);
		btnExchangeDir.setText("Browse...");
		btnExchangeDir.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				dlg.setFilterPath(txtExchangeDir.getText());
				dlg.setText("Ordner suchen");
				
				String dir = dlg.open();
				if (dir != null) {
					txtExchangeDir.setText(dir);
				}
			}
		});
		
		new Label(this, SWT.NONE).setText("Verzeichnis Eingehend");
		txtExchangeInDir = new Text(this, SWT.BORDER);
		txtExchangeInDir.setLayoutData(gridData1Col);
		txtExchangeInDir.setText(getValueByConfigKey(fileCommPartner.getFileTransferInDirectory()));
		
		Button btnExchangeInDir = new Button(this, SWT.PUSH);
		btnExchangeInDir.setText("Browse...");
		btnExchangeInDir.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				dlg.setFilterPath(txtExchangeInDir.getText());
				dlg.setText("Ordner suchen");
				
				String dir = dlg.open();
				if (dir != null) {
					txtExchangeInDir.setText(dir);
				}
			}
		});
		
		new Label(this, SWT.NONE).setText("Verzeichnis Ausgehend");
		txtExchangeOutDir = new Text(this, SWT.BORDER);
		txtExchangeOutDir.setLayoutData(gridData1Col);
		txtExchangeOutDir
			.setText(getValueByConfigKey(fileCommPartner.getFileTransferOutDirectory()));
		
		Button btnExchangeOutDir = new Button(this, SWT.PUSH);
		btnExchangeOutDir.setText("Browse...");
		btnExchangeOutDir.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				dlg.setFilterPath(txtExchangeOutDir.getText());
				dlg.setText("Ordner suchen");
				
				String dir = dlg.open();
				if (dir != null) {
					txtExchangeOutDir.setText(dir);
				}
			}
		});
		
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
		
		new Label(this, SWT.NONE).setText("Verarbeitungsprogramm");
		txtExecutable = new Text(this, SWT.BORDER);
		txtExecutable.setLayoutData(gridData1Col);
		txtExecutable.setText(getValueByConfigKey(fileCommPartner.getFileTransferExecuteable()));
		
		Button btnExecutable = new Button(this, SWT.PUSH);
		btnExecutable.setText("Browse...");
		
		btnExecutable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				FileDialog dlg = new FileDialog(getShell());
				dlg.setFilterPath(txtExecutable.getText());
				dlg.setText("Datei suchen");
				
				String dir = dlg.open();
				if (dir != null) {
					txtExecutable.setText(dir);
				}
			}
		});
		
		new Label(this, SWT.NONE).setText("Zusatzparameter");
		txtAdditionalParam = new Text(this, SWT.BORDER);
		txtAdditionalParam.setLayoutData(gridData2Col);
		txtAdditionalParam.setText(getValueByConfigKey(fileCommPartner.getFileAdditionalParams()));
		
		Label seperator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperator.setLayoutData((new GridData(SWT.FILL, SWT.NONE, true, true, 3, 1)));
		
		Label itemText = new Label(this, SWT.NONE);
		itemText.setText("Gerät");
		
		Composite compositeBtns = new Composite(this, SWT.NONE);
		compositeBtns.setLayout(new GridLayout(2, false));
		compositeBtns.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
		Button btnRemove = new Button(compositeBtns, SWT.CENTER);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				remove();
			}
		});
		btnRemove.setText("Entfernen");
		btnRemove.setEnabled(
			!FileCommPartner.DEFAULT_COMM_PARTNER_NAME.equals(fileCommPartner.getName()));
		
		Button btnAdd = new Button(compositeBtns, SWT.CENTER);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				InputDialog inDlg =
					new InputDialog(getShell(), "Gerät", "Gerät hinzufügen", "", null); //$NON-NLS-1$
				if (inDlg.open() == Dialog.OK) {
					String name = inDlg.getValue();
					if (!add(name))
					{
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
								"Der Gerätename wird bereits verwendet.");
					}
				}
			}
		});
		btnAdd.setText("Hinzufügen");
	}
	
	private String getValueByConfigKey(String cfgKey){
		return CoreHub.localCfg.get(cfgKey, "");
	}
	
	private boolean add(String name){
		if (fileCommPartner.addFileCommPartner(name)) {
			((GDTPreferencePageFileTransfer) preferencePage).createNewFileCommPartnerComposite(name);
			refreshParent(getParent());
			return true;
		}
		return false;
	}
	
	private void remove(){
		fileCommPartner.removeFileCommPartner(fileCommPartner.getName());
		CoreHub.localCfg.remove(fileCommPartner.getFileTransferIdReceiver());
		CoreHub.localCfg.remove(fileCommPartner.getFileTransferShortIdReceiver());
		CoreHub.localCfg.remove(fileCommPartner.getFileTransferDirectory());
		CoreHub.localCfg.remove(fileCommPartner.getFileTransferInDirectory());
		CoreHub.localCfg.remove(fileCommPartner.getFileTransferOutDirectory());
		CoreHub.localCfg.remove(fileCommPartner.getFileTransferUsedType());
		CoreHub.localCfg.remove(fileCommPartner.getFileTransferExecuteable());
		CoreHub.localCfg.remove(fileCommPartner.getFileAdditionalParams());
		
		Composite parent = getParent();
		dispose();
		refreshParent(parent);
	}
	
	public void save(){
		if (!isDisposed()) {
			CoreHub.localCfg.set(fileCommPartner.getFileTransferIdReceiver(),
				txtIdReceiver.getText());
			CoreHub.localCfg.set(fileCommPartner.getFileTransferShortIdReceiver(),
				txtIdShortReceiver.getText());
			CoreHub.localCfg.set(fileCommPartner.getFileTransferDirectory(),
				txtExchangeDir.getText());
			CoreHub.localCfg.set(fileCommPartner.getFileTransferInDirectory(),
				txtExchangeInDir.getText());
			CoreHub.localCfg.set(fileCommPartner.getFileTransferOutDirectory(),
				txtExchangeOutDir.getText());
			CoreHub.localCfg.set(fileCommPartner.getFileTransferUsedType(),
				btnFileTypes[1].getSelection() ? GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND
						: GDTConstants.GDT_FILETRANSFER_TYP_FEST);
			CoreHub.localCfg.set(fileCommPartner.getFileTransferExecuteable(),
				txtExecutable.getText());
			CoreHub.localCfg.set(fileCommPartner.getFileAdditionalParams(),
				txtAdditionalParam.getText());
		}
	}
	
	private void refreshParent(Composite parent){
		if (scrolledComposite != null) {
			scrolledComposite.setMinSize(parent.getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
		parent.layout();
	}
	
	private ScrolledComposite findScrolledComposite(){
		Composite parent = this;
		for (int i = 0; i < 10; i++) {
			parent = parent.getParent();
			if (parent instanceof ScrolledComposite) {
				return (ScrolledComposite) parent;
			}
			
		}
		return null;
	}
}
