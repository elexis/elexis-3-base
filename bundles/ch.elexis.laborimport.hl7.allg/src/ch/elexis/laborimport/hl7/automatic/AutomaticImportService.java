package ch.elexis.laborimport.hl7.automatic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.laborimport.hl7.command.ImportFileRunnable;
import ch.elexis.laborimport.hl7.universal.HL7ImportDirectory;
import ch.elexis.laborimport.hl7.universal.Preferences;
import ch.rgw.tools.Result;

@Component
public class AutomaticImportService {
	public static final String MY_LAB = "Eigenlabor";

	private static final long SETTLE_TIME_MILLIS = 10000;

	private Timer timer = new Timer(true);

	@Reference
	IVirtualFilesystemService vfsService;

	@Reference
	IConfigService configService;

	@Activate
	public void activate() {
		timer.schedule(new AutomaticImportTask(), 5000, 5000);
	}

	private class AutomaticImportTask extends TimerTask {
		private final ReentrantLock running = new ReentrantLock();

		@Override
		public void run() {
			if (configService.getLocal(Preferences.CFG_DIRECTORY_AUTOIMPORT, false)) {
				Optional<IVirtualFilesystemHandle> dir = HL7ImportDirectory.getDirectoryHandle(vfsService,
						HL7ImportDirectory.getDirectory(configService));
				if (dir.isPresent() && isElexisRunning()) {
					List<IVirtualFilesystemHandle> importFiles = getImportFiles(dir.get());
					if (!importFiles.isEmpty()) {
						if (running.tryLock()) {
							runImport(importFiles);
							running.unlock();
						} else {
							LoggerFactory.getLogger(AutomaticImportTask.class)
									.warn("Import from [" + dir.get().getAbsolutePath() + "] already running");
						}
					}
				}
			}
		}

		private boolean isElexisRunning() {
			return ConfigServiceHolder.isPresent() && ElexisEventDispatcher.getSelectedMandator() != null;
		}

		private List<IVirtualFilesystemHandle> getImportFiles(IVirtualFilesystemHandle dir) {
			List<IVirtualFilesystemHandle> ret = new ArrayList<>();
			try {
				IVirtualFilesystemHandle[] handles = dir
						.listHandles(handle -> "hl7".equalsIgnoreCase(handle.getExtension()));
				if (handles != null) {
					for (IVirtualFilesystemHandle handle : handles) {
						if (isReadyForImport(handle)) {
							ret.add(handle);
						}
					}
				}
			} catch (IOException e) {
				LoggerFactory.getLogger(AutomaticImportTask.class).warn("Could not read HL7 import directory", e);
			}
			return ret;
		}

		private boolean isReadyForImport(IVirtualFilesystemHandle handle) {
			Optional<File> file = handle.toFile();
			if (file.isPresent()) {
				File importFile = file.get();
				return importFile.exists() && !importFile.isDirectory()
						&& (importFile.lastModified() + SETTLE_TIME_MILLIS) < System.currentTimeMillis();
			}
			return true;
		}

		private void runImport(List<IVirtualFilesystemHandle> importFiles) {
			int err = 0;
			int files = 0;
			Result<?> r = null;
			for (IVirtualFilesystemHandle importFile : importFiles) {
				files++;
				Display display = Display.getDefault();
				if (display != null) {
					try {
						ImportFileRunnable runnable = new ImportFileRunnable(importFile, MY_LAB);
						display.syncExec(runnable);
						r = runnable.getResult();
					} catch (Exception e) {
						err = 1;
						LoggerFactory.getLogger(getClass()).warn("File error", e);
					}
				}
			}
			if (err > 0) {
				ResultAdapter.displayResult(r,
						Integer.toString(err) + " von " + Integer.toString(files) + " Dateien hatten Fehler\n");
			}
		}
	}
}
