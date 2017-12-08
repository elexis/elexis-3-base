package at.medevit.elexis.inbox.core.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.inbox.core.ui.LabResultLabelProvider;
import at.medevit.elexis.inbox.core.ui.LabResultLabelProvider.LabelFields;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.icons.Images;

public class InboxPreferences extends PreferencePage implements IWorkbenchPreferencePage {
	private static final String INBOX = "inbox/";
	public static final String INBOX_LABRESULT_LBL_CHOOSEN = INBOX + "labresult/label/choosen";
	public static final String INBOX_LABRESULT_LBL_AVAILABLE = INBOX + "labresult/label/available";
	
	//Default choosen / available
	public static final String DEF_CHOOSEN = LabResultLabelProvider.LabelFields.LAB_VALUE_SHORT
		.toString()
		+ ","
		+ LabResultLabelProvider.LabelFields.LAB_VALUE_NAME.toString()
		+ ","
		+ LabResultLabelProvider.LabelFields.LAB_RESULT.toString();
	
	public static final String DEF_AVAILABLE = LabResultLabelProvider.LabelFields.REF_RANGE
		.toString()
		+ ","
		+ LabResultLabelProvider.LabelFields.ORIGIN.toString()
		+ ","
		+ LabResultLabelProvider.LabelFields.DATE.toString();
	
	private static List<LabelFields> choosenLabels;
	private static ListViewer lvChoosen;
	private ListViewer lvAvailable;
	private static String[] choosen;
	private String[] available;
	private Label lblPreview;
	private String prefixPrevLabel = "Label Vorschau";
	
	public InboxPreferences(){
		super("Inbox");
		
		choosen = CoreHub.userCfg.get(INBOX_LABRESULT_LBL_CHOOSEN, DEF_CHOOSEN).split(",");
		int nrValues = LabResultLabelProvider.LabelFields.values().length;
		if (choosen.length == nrValues) {
			available = new String[] {};
		} else {
			available =
				CoreHub.userCfg.get(INBOX_LABRESULT_LBL_AVAILABLE, DEF_AVAILABLE).split(",");
		}
	}
	
	@Override
	protected Control createContents(Composite parent){
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayoutData(new GridData(GridData.FILL_BOTH));
		area.setLayout(new GridLayout(1, true));
		
		Group grpLabel = new Group(area, SWT.NONE);
		grpLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		grpLabel.setLayout(new GridLayout(3, true));
		grpLabel.setText("Laborwerte Label Optionen");
		
		Label lblChoosen = new Label(grpLabel, SWT.NONE);
		lblChoosen.setText("Gewählt");
		new Label(grpLabel, SWT.NONE);
		Label lblAvailable = new Label(grpLabel, SWT.NONE);
		lblAvailable.setText("Noch verfügbar");
		
		GridData gdLvChoosen = new GridData();
		gdLvChoosen.horizontalAlignment = SWT.FILL;
		gdLvChoosen.verticalAlignment = SWT.CENTER;
		gdLvChoosen.minimumHeight = 100;
		gdLvChoosen.heightHint = 100;
		
		lvChoosen = new ListViewer(grpLabel, SWT.BORDER | SWT.V_SCROLL);
		lvChoosen.getList().setLayoutData(gdLvChoosen);
		lvChoosen.setContentProvider(new ArrayContentProvider());
		lvChoosen.setInput(choosen);
		
		Composite btnArea = new Composite(grpLabel, SWT.NONE);
		btnArea.setLayout(new GridLayout());
		btnArea.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		
		Button btnAdd = new Button(btnArea, SWT.PUSH);
		btnAdd.setImage(Images.IMG_PREVIOUS.getImage());
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				IStructuredSelection sel = (IStructuredSelection) lvAvailable.getSelection();
				String item = (String) sel.getFirstElement();
				if (item != null) {
					lvAvailable.remove(item);
					lvChoosen.add(item);
					lblPreview.setText(getPreviewLabel());
				}
			}
		});
		
		Button btnRemove = new Button(btnArea, SWT.PUSH);
		btnRemove.setImage(Images.IMG_NEXT.getImage());
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				IStructuredSelection sel = (IStructuredSelection) lvChoosen.getSelection();
				String item = (String) sel.getFirstElement();
				if (item != null) {
					lvChoosen.remove(item);
					lvAvailable.add(item);
					lblPreview.setText(getPreviewLabel());
				}
			}
		});
		
		GridData gdLvAvailable = new GridData();
		gdLvAvailable.horizontalAlignment = SWT.FILL;
		gdLvAvailable.verticalAlignment = SWT.CENTER;
		gdLvAvailable.minimumHeight = 100;
		gdLvAvailable.heightHint = 100;
		
		lvAvailable = new ListViewer(grpLabel, SWT.BORDER | SWT.V_SCROLL);
		lvAvailable.getList().setLayoutData(gdLvAvailable);
		lvAvailable.setContentProvider(new ArrayContentProvider());
		lvAvailable.setInput(available);
		
		new Label(grpLabel, SWT.NONE);
		new Label(grpLabel, SWT.NONE);
		new Label(grpLabel, SWT.NONE);
		
		lblPreview = new Label(grpLabel, SWT.NONE);
		lblPreview.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		lblPreview.setText(getPreviewLabel());
		
		return area;
	}
	
	@Override
	public boolean performOk(){
		CoreHub.userCfg.set(INBOX_LABRESULT_LBL_CHOOSEN, getListAsString(lvChoosen.getList()
			.getItems()));
		CoreHub.userCfg.set(INBOX_LABRESULT_LBL_AVAILABLE, getListAsString(lvAvailable.getList()
			.getItems()));
		
		CoreHub.userCfg.flush();
		loadChoosenLabel();
		return super.performOk();
	}
	
	private String getPreviewLabel(){
		StringBuilder sb = new StringBuilder();
		for (String s : lvChoosen.getList().getItems()) {
			sb.append(s);
			sb.append(" ");
		}
		return prefixPrevLabel + ":\t" + sb.toString();
	}
	
	private String getListAsString(String[] items){
		StringBuilder sb = new StringBuilder();
		for (String item : items) {
			sb.append(item);
			sb.append(",");
		}
		return sb.toString();
	}
	
	@Override
	public void init(IWorkbench workbench){}
	
	private static void loadChoosenLabel(){
		String[] labels = CoreHub.userCfg.get(INBOX_LABRESULT_LBL_CHOOSEN, DEF_CHOOSEN).split(",");
		choosenLabels = new ArrayList<LabResultLabelProvider.LabelFields>();
		
		for (String label : labels) {
			LabelFields lblField = LabelFields.getEnum(label);
			if (lblField != null) {
				choosenLabels.add(lblField);
			}
		}
	}
	
	public static List<LabelFields> getChoosenLabel(){
		if (choosenLabels == null || choosenLabels.isEmpty()) {
			loadChoosenLabel();
		}
		return choosenLabels;
	}
}
