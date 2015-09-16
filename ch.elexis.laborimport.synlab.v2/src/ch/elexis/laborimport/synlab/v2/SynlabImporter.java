package ch.elexis.laborimport.synlab.v2;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.ui.importer.div.importers.multifile.MultiFileParser;
import ch.elexis.core.ui.importer.div.importers.multifile.strategy.DefaultImportStrategyFactory;
import ch.elexis.core.ui.util.ImporterPage;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

public class SynlabImporter extends ImporterPage {
	enum Mode {
			FILE, DIRECTORY
	}
	
	private static final Logger log = LoggerFactory.getLogger(SynlabImporter.class);
	
	public static final String PLUGIN_ID = "ch.elexis.laborimport.synlab.v2";
	public static final String MY_LAB = "SynlabV2";
	
	private Button btnFile, btnDirectory, btnBrowse;
	private Text txtFile, txtDownloadDir;
	
	private MultiFileParser mfParser = new MultiFileParser(MY_LAB);
	
	public SynlabImporter(){}
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		Result<Object> result = null;
		File file = resolveLocationIfValid(results[1]);
		// location is invalid
		if (file == null) {
			log.warn("No resolvable file or directory location found for Synlab (V2) import");
			result = new Result<Object>(SEVERITY.WARNING, 1,
				Messages.SynlabImporter_ImportLocationNotResolvable, results[1], true);
		} else {
			Mode mode = Mode.valueOf(results[0]);
			switch (mode) {
			case FILE:
				result = mfParser.importFromFile(file, new DefaultImportStrategyFactory());
				break;
			case DIRECTORY:
				result = mfParser.importFromDirectory(file, new DefaultImportStrategyFactory());
				break;
			default:
				result = new Result<Object>(SEVERITY.ERROR, 2,
					Messages.SynlabImporter_NoModeSelected, null, true);
				break;
			}
		}
		return ResultAdapter.getResultAsStatus(result);
	}
	
	/**
	 * check's if path is set and file actually exists
	 * 
	 * @param path
	 * @return the file/directory if valid, null otherwise
	 */
	private File resolveLocationIfValid(String path){
		if (path == null || path.isEmpty()) {
			return null;
		}
		
		File file = new File(path);
		if (file == null || !file.exists()) {
			return null;
		}
		return file;
	}
	
	@Override
	public Composite createPage(Composite parent){
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		area.setLayout(new GridLayout(3, false));
		
		// initialize results array with empty strings
		results = new String[2];
		Arrays.fill(results, "");
		
		SelectionAdapter btnSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				Button button = (Button) e.getSource();
				
				if (button.getSelection()) {
					boolean directoryMode = button == btnDirectory;
					if (directoryMode) {
						btnFile.setSelection(false);
						btnBrowse.setEnabled(false);
						results[0] = Mode.DIRECTORY.toString();
						results[1] = txtDownloadDir.getText();
					} else {
						btnBrowse.setEnabled(true);
						btnDirectory.setSelection(false);
						results[0] = Mode.FILE.toString();
						results[1] = txtFile.getText();
					}
					txtFile.setEnabled(!directoryMode);
					txtDownloadDir.setEnabled(directoryMode);
				}
			}
		};
		
		// import selected file only
		btnFile = new Button(area, SWT.RADIO);
		btnFile.setText(Messages.SynlabImporter_FromFile);
		btnFile.addSelectionListener(btnSelectionAdapter);
		
		txtFile = new Text(area, SWT.BORDER);
		txtFile.setEditable(false);
		txtFile.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		btnBrowse = new Button(area, SWT.PUSH);
		btnBrowse.setText(Messages.SynlabImporter_Browse);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				FileDialog fileDlg = new FileDialog(area.getShell(), SWT.OPEN);
				fileDlg.setFilterExtensions(new String[] {
					"*.hl7"
				});
				String filename = fileDlg.open();
				txtFile.setText(filename);
				results[1] = filename;
			}
		});
		
		// auto import from selected directory
		btnDirectory = new Button(area, SWT.RADIO);
		btnDirectory.setText(Messages.SynlabImporter_AutoImport);
		btnDirectory.addSelectionListener(btnSelectionAdapter);
		
		txtDownloadDir = new Text(area, SWT.BORDER);
		txtDownloadDir.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		txtDownloadDir
			.setText(CoreHub.localCfg.get(SynlabPreferences.CFG_SYNLAB2_DOWNLOAD_DIR, ""));
		txtDownloadDir.setEditable(false);
		
		new Label(area, SWT.NONE);
		
		Label lblImage = new Label(area, SWT.NONE);
		lblImage.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 3, 1));
		lblImage.setImage(ResourceManager.getPluginImage("ch.elexis.laborimport.synlab.v2",
			"icons/logo_synlab_ch.gif"));
		return area;
	}
	
	@Override
	public String getTitle(){
		return Messages.SynlabImporter_Title + MY_LAB;
	}
	
	@Override
	public String getDescription(){
		return Messages.SynlabImporter_Description;
	}
}
