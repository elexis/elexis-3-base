package ch.elexis.global_inbox.core.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
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

import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.data.util.LocalLock;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.viewers.CommonContentProviderAdapter;
import ch.elexis.global_inbox.core.model.GlobalInboxEntry;
import ch.elexis.global_inbox.core.util.Constants;
import ch.elexis.global_inbox.core.util.GlobalInboxUtil;
import ch.elexis.omnivore.model.util.CategoryUtil;

public class GlobalInboxContentProvider extends CommonContentProviderAdapter {

	private static final String LOCAL_LOCK_INBOXIMPORT = "GlobalInboxImport"; //$NON-NLS-1$
	private final Pattern PATIENT_MATCH_PATTERN = Pattern.compile("([0-9]+)_(.+)"); //$NON-NLS-1$

	private Logger log;
	private List<GlobalInboxEntry> entries;
	private List<GlobalInboxEntry> loadJobList;

	private LoadJob loader;
	private GlobalInboxUtil giutil;
	private String deviceName;

	@Override
	public void dispose() {
		super.dispose();
	}

	public IStatus reload() {
		return loader.run(null);
	}

	public GlobalInboxContentProvider(String deviceName) {
		log = LoggerFactory.getLogger(getClass());
		entries = new ArrayList<>();
		loadJobList = new ArrayList<>();
		loader = new LoadJob();
		loader.schedule(1000);
		giutil = new GlobalInboxUtil();
		this.deviceName = deviceName;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return entries == null ? null : entries.toArray();
	}

	class LoadJob extends Job {

		public LoadJob() {
			super("GlobalInbox"); //$NON-NLS-1$
			setPriority(DECORATE);
			setUser(false);
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			LocalLock lock = new LocalLock(LOCAL_LOCK_INBOXIMPORT);
			if (lock.tryLock()) {
				String filepath = GlobalInboxUtil.getDirectory(Constants.PREF_DIR_DEFAULT, deviceName);
				File dir = null;
				if (filepath == null) {
					filepath = Constants.PREF_DIR_DEFAULT;
					ConfigServiceHolder.get().setLocal(Constants.PREF_DIR, Constants.PREF_DIR_DEFAULT);
				}
				dir = new File(filepath);
				Object dm = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
				if (dm == null) {
					log.warn("No document management service found."); //$NON-NLS-1$
					return Status.OK_STATUS;
				}
				List<String> cats = CategoryUtil.getCategoriesNames();
				if (cats != null) {
					for (String cat : cats) {
						File subdir = new File(dir, cat);
						if (!subdir.exists()) {
							subdir.mkdirs();
						}
					}
				}
				loadJobList.clear();
				addFilesInDirRecursive(dir);
				filterAndPopulate();
				schedule(120000L);
			} else {
				long lockMillis = lock.getLockCurrentMillis();
				if (lockMillis == -1 || (System.currentTimeMillis() - lockMillis) > (120000L * 2)) {
					log.warn("Removing pending lock " + lock.getLockMessage() + "@" + lockMillis); //$NON-NLS-1$ //$NON-NLS-2$
					lock.unlock();
				}
			}
			LocalLock.getManagedLock(LOCAL_LOCK_INBOXIMPORT).ifPresent(localDocumentLock -> localDocumentLock.unlock());
			return Status.OK_STATUS;
		}

		private void filterAndPopulate() {
			for (Iterator<GlobalInboxEntry> iterator = entries.iterator(); iterator.hasNext();) {
				GlobalInboxEntry gie = iterator.next();
				if (!loadJobList.contains(gie)) {
					iterator.remove();
				}
			}
			for (GlobalInboxEntry gie : loadJobList) {
				if (!entries.contains(gie)) {
					gie = GlobalInboxEntryFactory.populateExtensionInformation(gie);
					entries.add(gie);
				}
			}
		}

		private void addFilesInDirRecursive(File dir) {
			List<String> allFilesInDirRecursive = new ArrayList<>();
			File[] files = dir.listFiles();
			if (files == null)
				return;
			for (File file : files) {
				if (file.isHidden() || file.getName().startsWith(".")) {
					continue;
				}
				if (file.isDirectory()) {
					addFilesInDirRecursive(file);
				} else {
					allFilesInDirRecursive.add(file.getAbsolutePath());
				}
				if (file.isDirectory()) {
					addFilesInDirRecursive(file);
				} else {
					Matcher matcher = PATIENT_MATCH_PATTERN.matcher(file.getName());
					if (matcher.matches()) {
						String patientNo = matcher.group(1);
						String fileName = matcher.group(2);
						String tryImportForPatient = giutil.tryImportForPatient(file, patientNo, fileName);
						if (tryImportForPatient != null) {
							log.info("Auto imported file [{}], document id is [{}]", file, tryImportForPatient); //$NON-NLS-1$
							continue;
						}
					}
					allFilesInDirRecursive.add(file.getAbsolutePath());
				}
			}
			allFilesInDirRecursive.sort(Comparator.comparingInt(String::length));
			List<File> extensionFiles = new ArrayList<File>();
			for (String string : allFilesInDirRecursive) {
				File file = new File(string);
				if (extensionFiles.contains(file)) {
					continue;
				}
				File[] _extensionFiles = dir.listFiles(
						(_dir, _name) -> _name.startsWith(file.getName()) && !Objects.equals(_name, file.getName()));
				extensionFiles.addAll(Arrays.asList(_extensionFiles));
				GlobalInboxEntry globalInboxEntry = GlobalInboxEntryFactory.createEntry(file, _extensionFiles);
				loadJobList.add(globalInboxEntry);
			}
		}
	}

	void destroy() {
		giutil = null;
		entries = null;
		loader.cancel();
		loader = null;
	}
}
