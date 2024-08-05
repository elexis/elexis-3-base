package ch.elexis.fire.core.task;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.fire.core.IFIREService;

public class FIREExportIdentifiedRunnable implements IIdentifiedRunnable {

	public static final String RUNNABLE_ID = "fireExport";
	public static final String DESCRIPTION = "Perform FIRE (USZ) export and upload, if initial was performed incremental is performed";

	private IFIREService fireService;
	
	private int uploaded;
	private int uploadFailed;

	private Logger logger;

	public FIREExportIdentifiedRunnable(IFIREService fireService) {
		this.fireService = fireService;
	}

	@Override
	public String getId() {
		return RUNNABLE_ID;
	}

	@Override
	public String getLocalizedDescription() {
		return DESCRIPTION;
	}

	@Override
	public Map<String, Serializable> getDefaultRunContext() {
		Map<String, Serializable> defaultRunContext = new HashMap<>();
		return defaultRunContext;
	}

	@Override
	public Map<String, Serializable> run(Map<String, Serializable> runContext, IProgressMonitor progressMonitor,
			Logger logger) throws TaskException {
		this.logger = logger;
		
		Map<String, Serializable> resultMap = new HashMap<String, Serializable>();
		uploaded = 0;
		uploadFailed = 0;

		LocalDateTime initialLocalDateTime = null;
		LocalDateTime incrementalLocalDateTime = null;
		if (fireService.getInitialTimestamp() != -1) {
			Instant instant = Instant.ofEpochMilli(fireService.getInitialTimestamp());
			initialLocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		}
		if (fireService.getIncrementalTimestamp() != -1) {
			Instant instant = Instant.ofEpochMilli(fireService.getInitialTimestamp());
			incrementalLocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		}
		boolean cancelled = false;
		List<File> exported = null;
		if (initialLocalDateTime == null) {
			logger.info("Starting initial FIRE export");
			exported = fireService.initialExport(progressMonitor);
		} else {
			logger.info("Last Export was "
					+ (incrementalLocalDateTime == null ? initialLocalDateTime.toString()
							: incrementalLocalDateTime.toString())
					+ "Starting incremental FIRE export");
			exported = fireService
					.incrementalExport(incrementalLocalDateTime == null ? fireService.getInitialTimestamp()
							: fireService.getIncrementalTimestamp(), progressMonitor);
		}
		cancelled = progressMonitor.isCanceled();
		if (exported != null && !exported.isEmpty()) {
			logger.info("Exported [" + exported.size() + "] files");
			Collection<File> foundUploadFiles = FileUtils.listFiles(exported.get(0).getParentFile(),
					new String[] { "json" }, false);
			logger.info("Found [" + foundUploadFiles.size() + "] files for upload in ["
					+ exported.get(0).getParentFile().getPath() + "]");
			foundUploadFiles.forEach(f -> {
				if (fireService.uploadBundle(f)) {
					uploaded++;
					logger.info("Upload [" + f.getAbsolutePath() + "] successful");
					f.delete();
				} else {
					uploadFailed++;
					logger.warn("Upload [" + f.getAbsolutePath() + "] failed");
				}
			});
		} else {
			logger.warn("No exported files");
		}

		
		StringBuilder sb = new StringBuilder();
		sb.append("FIRE Export: ").append((initialLocalDateTime == null ? "initial" : "incremental")) //$NON-NLS-1$ //$NON-NLS-2$
				.append(" finished ").append(uploaded).append(" files uploaded ").append(uploadFailed) //$NON-NLS-2$
				.append(" files upload failed."); //$NON-NLS-1$
		if (cancelled) {
			sb.append("\nFIRE Export was cancelled.");
		}
		resultMap.put(ReturnParameter.RESULT_DATA, sb.toString());
		return resultMap;
	}
}
