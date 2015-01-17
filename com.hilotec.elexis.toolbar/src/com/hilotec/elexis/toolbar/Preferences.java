package com.hilotec.elexis.toolbar;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{
	public static final String CFG_PERSPEKTIVEN = "hilotec/toolbar/perspektiven";
	public Preferences() {
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}
	
	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		addField(new PerspektivenListe(CFG_PERSPEKTIVEN, "Perspektiven",
			getFieldEditorParent()));
		
	}
}



class PerspektivenListe extends ListEditor {
	public PerspektivenListe(String n, String i, Composite p){
		super(n, i, p);
	}
	
	@Override
	protected String createList(String[] items){
		return StringTool.join(items, ",");
	}
	
	@Override
	protected String getNewInputObject(){
		PerspektivenAuswahl pa = new PerspektivenAuswahl(getShell());
		if (pa.open() == Dialog.OK && pa.selection != null) {
			return pa.selection.getId();
		}
		return null;
	}
	
	@Override
	protected String[] parseString(String stringList){
		return stringList.split(","); //$NON-NLS-1$
	}
	
}

class PerspektivenAuswahl extends Dialog {
	public IPerspectiveDescriptor selection;
	private Combo list;
	private IPerspectiveDescriptor[] plist; 
	
	protected PerspektivenAuswahl(Shell parentShell){
		super(parentShell);
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		list = new Combo(parent, 0);
		list.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		plist = PlatformUI.getWorkbench().getPerspectiveRegistry().
			getPerspectives();
		for (IPerspectiveDescriptor pd: plist) {
			list.add(pd.getId());
		}
		
		return list;
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText("Perspektivenauswahl");
	}
	
	@Override
	protected void okPressed(){
		int i = list.getSelectionIndex();
		if (i == -1)
			selection = null;
		else
			selection = plist[i];

		super.okPressed();
		
	};
}

