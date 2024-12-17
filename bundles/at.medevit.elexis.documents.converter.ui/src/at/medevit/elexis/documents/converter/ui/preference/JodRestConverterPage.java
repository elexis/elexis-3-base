package at.medevit.elexis.documents.converter.ui.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class JodRestConverterPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public JodRestConverterPage() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		StringFieldEditor jodRestBasPath = new StringFieldEditor("jodrestconverter/basepath", "JODconverter REST URL",
				getFieldEditorParent());
		jodRestBasPath.getTextControl(getFieldEditorParent()).setMessage("https://tools.medelexis.ch/jodconverter/");
		addField(jodRestBasPath);
	}
}
