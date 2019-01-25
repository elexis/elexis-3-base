package ch.elexis.global_inbox;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.List;
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
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.CommonContentProviderAdapter;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;

public class InboxContentProvider extends CommonContentProviderAdapter {
	private static Logger log = LoggerFactory.getLogger(InboxContentProvider.class);
	
	private static final String LOCAL_LOCK_INBOXIMPORT = "GlobalInboxImport";
	
	ArrayList<File> files = new ArrayList<File>();
	InboxView view;
	LoadJob loader;
	
	public void setView(InboxView view){
		this.view = view;
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
	
	public IStatus reload(){
		return loader.run(null);
	}
	
	public InboxContentProvider(){
		loader = new LoadJob();
		loader.schedule(1000);
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		return files == null ? null : files.toArray();
	}
	
	Pattern patMatch = Pattern.compile("([0-9]+)_(.+)");
	
	private void addFiles(List<File> list, File dir){
		File[] contents = dir.listFiles();
		for (File file : contents) {
			if (file.isDirectory()) {
				addFiles(list, file);
			} else {
				Matcher matcher = patMatch.matcher(file.getName());
				if (matcher.matches()) {
					String num = matcher.group(1);
					String nam = matcher.group(2);
					List<Patient> lPat = new Query(Patient.class, Patient.FLD_PATID, num).execute();
					if (lPat.size() == 1) {
						if (!isFileOpened(file)) {
							Patient pat = lPat.get(0);
							String cat = Activator.getDefault().getCategory(file);
							if (cat.equals("-") || cat.equals("??")) {
								cat = null;
							}
							IDocumentManager dm = (IDocumentManager) Extensions
								.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
							try {
								
								long heapSize = Runtime.getRuntime().totalMemory();
								long length = file.length();
								if (length >= heapSize) {
									log.warn("Skipping " + file.getAbsolutePath()
										+ " as bigger than heap size. (#3652)");
									continue;
								}
								
								GenericDocument fd = new GenericDocument(pat, nam, cat, file,
									new TimeTool().toString(TimeTool.DATE_GER), "", null);
								file.delete();
								if (CoreHub.localCfg.get(Preferences.PREF_AUTOBILLING, false)) {
									dm.addDocument(fd, true);
								} else {
									dm.addDocument(fd);
								}
							} catch (Exception ex) {
								ExHandler.handle(ex);
								SWTHelper.alert(Messages.InboxView_error, ex.getMessage());
							}
						}
					}
				}
				list.add(file);
			}
		}
	}
	
	private boolean isFileOpened(File file){
		try (FileChannel channel = new RandomAccessFile(file, "rw").getChannel();) {
			// Get an exclusive lock on the whole file
			try (FileLock lock = channel.lock();) {
				// we got a lock so this file is not opened 
				return false;
			} catch (OverlappingFileLockException e) {
				// default file is opened ...
			}
		} catch (IOException e) {
			// default file is opened ...
		}
		return true;
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
				
				files.clear();
				log.info("Adding documents from [" + dir.getAbsolutePath() + "]");
				addFiles(files, dir);
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
