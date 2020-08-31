package ch.elexis.connector.medicosearch.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wb.swt.ResourceManager;

import ch.elexis.connector.medicosearch.MedicosearchUtil;
import ch.elexis.connector.medicosearch.Messages;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class MedicosearchPreferences extends PreferencePage implements IWorkbenchPreferencePage {
	public static final String CFG_MEDICOSEARCH_CONFIG = "medicosearch/config";
	
	private Text txtConfigFile;
	private Button btnOpen;
	private String configFileLocation;
	
	public MedicosearchPreferences(){}
	
	@Override
	public void init(IWorkbench workbench){
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		setImageDescriptor(ResourceManager.getPluginImageDescriptor(
			"ch.elexis.connector.medicosearch", "icons/medicosearch.png"));
			
		configFileLocation = MedicosearchUtil.getInstance().getConfigurationFilePath();
	}
	
	@Override
	protected Control createContents(Composite parent){
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayout(new GridLayout(3, false));
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblConfigFile = new Label(area, SWT.NONE);
		lblConfigFile.setText(Messages.PrefsConfigFile);
		
		txtConfigFile = new Text(area, SWT.BORDER);
		txtConfigFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtConfigFile.setText(configFileLocation);
		txtConfigFile.setEditable(false);
		
		btnOpen = new Button(area, SWT.PUSH);
		btnOpen.setText(Messages.PrefsOpenConfig);
		btnOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (configFileLocation != null) {
					// open with system default text editor
					Program.findProgram(".txt").execute(configFileLocation);
				}
			}
		});
		return null;
	}
}
