package ch.elexis.global_inbox.ui.parts;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.data.util.LocalLock;
import ch.elexis.core.ui.util.viewers.CommonContentProviderAdapter;
import ch.elexis.global_inbox.Preferences;
import ch.elexis.global_inbox.internal.service.GlobalInboxEntryFactory;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.GlobalInboxUtil;

public class GlobalInboxContentProvider extends CommonContentProviderAdapter {
	
	private static final String LOCAL_LOCK_INBOXIMPORT = "GlobalInboxImport";
	private final Pattern PATIENT_MATCH_PATTERN = Pattern.compile("([0-9]+)_(.+)");
	
	private Logger log;
	private List<GlobalInboxEntry> entries;
	private GlobalInboxPart view;
	private LoadJob loader;
	private GlobalInboxUtil giutil;
	
	@Override
	public void dispose(){
		super.dispose();
	}
	
	public IStatus reload(){
		return loader.run(null);
	}
	
	public GlobalInboxContentProvider(GlobalInboxPart view){
		this.view = view;
		log = LoggerFactory.getLogger(getClass());
		entries = new ArrayList<GlobalInboxEntry>();
		loader = new LoadJob();
		loader.schedule(1000);
		giutil = new GlobalInboxUtil();
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		return entries == null ? null : entries.toArray();
	}
	
	private void addFilesInDirRecursive(File dir){
		List<String> allFilesInDir = new ArrayList<>();
		
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				addFilesInDirRecursive(file);
			} else {
				// match patient prefix auto import pattern
				Matcher matcher = PATIENT_MATCH_PATTERN.matcher(file.getName());
				if (matcher.matches()) {
					String patientNo = matcher.group(1);
					String fileName = matcher.group(2);
					String tryImportForPatient =
						giutil.tryImportForPatient(file, patientNo, fileName);
					if (tryImportForPatient != null) {
						// TODO does this match the up-until-now behavior?
						log.info("Auto imported file [{}], document id is [{}]", file,
							tryImportForPatient);
						continue;
					}
				}
				
				allFilesInDir.add(file.getAbsolutePath());
			}
		}
		
		// extension file names are always longer than the orig filenames
		// so in order to identify them beforehand we sort the filenames by length
		allFilesInDir.sort(Comparator.comparingInt(String::length));
		
		List<File> extensionFiles = new ArrayList<File>();
		for (String string : allFilesInDir) {
			File file = new File(string);
			if (extensionFiles.contains(file)) {
				continue;
			}
			
			// are there extension-files to this file?
			// e.g. orig file: scan.pdf, ext file: scan.pdf.edam.xml
			File[] _extensionFiles = dir.listFiles((_dir, _name) -> _name.startsWith(file.getName())
				&& !Objects.equals(_name, file.getName()));
			extensionFiles.addAll(Arrays.asList(_extensionFiles));
			GlobalInboxEntry globalInboxEntry =
				GlobalInboxEntryFactory.createEntry(file, _extensionFiles);
			//			GlobalInboxEntry globalInboxEntry = new GlobalInboxEntry(file, _extensionFiles);
			entries.add(globalInboxEntry);
		}
	}
	
	class LoadJob extends Job {
		
		public LoadJob(){
			super("GlobalInbox"); //$NON-NLS-1$
			setPriority(DECORATE);
			setUser(false);
			setSystem(true);
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor){
			LocalLock lock = new LocalLock(LOCAL_LOCK_INBOXIMPORT);
			if (lock.tryLock()) {
				String filepath = CoreHub.localCfg.get(Preferences.PREF_DIR, null);
				File dir = null;
				if (filepath == null) {
					filepath = Preferences.PREF_DIR_DEFAULT;
					CoreHub.localCfg.set(Preferences.PREF_DIR, Preferences.PREF_DIR_DEFAULT);
				}
				dir = new File(filepath);
				if (!dir.isDirectory()) {
					if (view != null) {
						return Status.CANCEL_STATUS;
					} else {
						return Status.OK_STATUS;
					}
				}
				Object dm =
					Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
				if (dm == null) {
					log.warn("No document management service found.");
					return Status.OK_STATUS;
				}
				IDocumentManager documentManager = (IDocumentManager) dm;
				String[] cats = documentManager.getCategories();
				
				if (cats != null) {
					for (String cat : cats) {
						File subdir = new File(dir, cat);
						if (!subdir.exists()) {
							subdir.mkdirs();
						}
					}
				}
				
				entries.clear();
				addFilesInDirRecursive(dir);
				if (view != null) {
					view.reload();
				}
				schedule(120000L);
			} else {
				log.info("Skipping document import, lock present.");
				long lockMillis = lock.getLockCurrentMillis();
				if (lockMillis == -1 || (System.currentTimeMillis() - lockMillis) > (120000L * 2)) {
					log.warn("Removing pending lock " + lock.getLockMessage() + "@" + lockMillis);
					lock.unlock();
				}
			}
			// unlock if the lock is managed by this instance
			LocalLock.getManagedLock(LOCAL_LOCK_INBOXIMPORT)
				.ifPresent(localDocumentLock -> localDocumentLock.unlock());
			return Status.OK_STATUS;
		}
	}
}