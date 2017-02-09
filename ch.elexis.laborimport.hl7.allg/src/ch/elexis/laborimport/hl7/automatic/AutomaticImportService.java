package ch.elexis.laborimport.hl7.automatic;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.ui.importer.div.importers.DefaultHL7Parser;
import ch.elexis.laborimport.hl7.universal.LinkLabContactResolver;
import ch.elexis.laborimport.hl7.universal.Preferences;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

@Component
public class AutomaticImportService {
	public static final String MY_LAB = "Eigenlabor";
	
	private HL7Parser hlp = new DefaultHL7Parser(MY_LAB);
	
	private Timer timer = new Timer(true);
	
	@Activate
	public void activate(){
		timer.schedule(new AutomaticImportTask(), 5000, 5000);
	}
	
	private class AutomaticImportTask extends TimerTask {
		private final ReentrantLock running = new ReentrantLock();
		
		@Override
		public void run(){
			if (CoreHub.localCfg.get(Preferences.CFG_DIRECTORY_AUTOIMPORT, false)) {
				File dir =
					new File(CoreHub.localCfg.get(Preferences.CFG_DIRECTORY, File.separator));
				if ((dir.exists()) && (dir.isDirectory()) && isElexisRunning()) {
					if (shouldImport(dir)) {
						if (running.tryLock()) {
							runImport(dir);
							running.unlock();
						} else {
							LoggerFactory.getLogger(AutomaticImportTask.class).warn(
								"Import from [" + dir.getAbsolutePath() + "] already running");
						}
					}
				}
			}
		}
		
		private boolean isElexisRunning(){
			return CoreHub.globalCfg != null && ElexisEventDispatcher.getSelectedMandator() != null;
		}
		
		private boolean shouldImport(File dir){
			return !getImportFiles(dir).isEmpty();
		}
		
		private List<File> getImportFiles(File dir){
			List<File> ret = new ArrayList<>();
			String[] filenames = dir.list(new FilenameFilter() {
				
				public boolean accept(File arg0, String arg1){
					if (arg1.toLowerCase().endsWith(".hl7")) {
						return true;
					}
					return false;
				}
			});
			for (String string : filenames) {
				File file = new File(dir, string);
				long currentMillis = System.currentTimeMillis();
				if (file.exists() && !file.isDirectory()
					&& (file.lastModified() + 10000) < currentMillis) {
					ret.add(file);
				}
			}
			return ret;
		}
		
		private void runImport(File dir){
			File archiveDir = new File(dir, "archive");
			if (!archiveDir.exists()) {
				archiveDir.mkdir();
			}
			File errorDir = new File(dir, "fehlerhaft");
			if (!errorDir.exists()) {
				errorDir.mkdir();
			}
			int err = 0;
			int files = 0;
			Result<?> r = null;
			List<File> importFiles = getImportFiles(dir);
			for (File importFile : importFiles) {
				files++;
				Display display = Display.getDefault();
				if (display != null) {
					ImportFileRunnable runnable = new ImportFileRunnable(importFile, archiveDir);
					display.syncExec(runnable);
					r = runnable.getResult();
					if (!r.isOK()) {
						err++;
						File errFile = new File(errorDir, importFile.getName());
						importFile.renameTo(errFile);
					}
				}
			}
			if (err > 0) {
				ResultAdapter.displayResult(r, Integer.toString(err) + " von "
					+ Integer.toString(files) + " Dateien hatten Fehler\n");
			}
		}
	}
	
	private class ImportFileRunnable implements Runnable {
		
		private File file;
		private File archive;
		
		private Result<?> result;
		
		public ImportFileRunnable(File importFile, File archiveDir){
			this.file = importFile;
			this.archive = archiveDir;
		}
		
		public Result<?> getResult(){
			return result;
		}
		
		@Override
		public void run(){
			try {
				result = hlp.importFile(file, archive, null, new LinkLabContactResolver(), false);
			} catch (IOException e) {
				result = new Result<>();
				result.add(SEVERITY.ERROR, 1, e.getMessage(), null, true);
			}
		}
	}
}
