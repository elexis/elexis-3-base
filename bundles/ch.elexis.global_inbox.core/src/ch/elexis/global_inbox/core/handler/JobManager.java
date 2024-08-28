package ch.elexis.global_inbox.core.handler;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

public class JobManager {

	private static final JobManager instance = new JobManager();
	private final Map<String, FileMoveJob> activeJobs = new ConcurrentHashMap<>();

	private JobManager() {
	}

	public static JobManager getInstance() {
		return instance;
	}

	public void manageJob(MoveFileIdentifiedRunnable runnable, Map<String, Serializable> context, Logger logger) {
		String deviceName = runnable.getDeviceName();
		FileMoveJob activeJob = activeJobs.get(deviceName);
		if (activeJob != null && runnable.shouldRestartTask(context)) {
			activeJob.stopJob();
			activeJobs.remove(deviceName);
		}

		if (!activeJobs.containsKey(deviceName)) {
			FileMoveJob job = new FileMoveJob(runnable, context, logger);
			activeJobs.put(deviceName, job);
			job.schedule();
		}
	}

	public void removeJob(String deviceName) {
		FileMoveJob job = activeJobs.remove(deviceName);
		if (job != null) {
			job.stopJob();
		}
	}

	public void shutdown() {
		for (FileMoveJob job : activeJobs.values()) {
			job.stopJob();
		}
		activeJobs.clear();
	}

	public Map<String, FileMoveJob> getActiveJobs() {
		return activeJobs;
	}

	public void stopJobIfReferenceIdDeleted(String referenceId) {
		if (!activeJobs.containsKey(referenceId)) {
			return;
		}

		FileMoveJob job = activeJobs.remove(referenceId);
		if (job != null) {
			job.stopJob();
		}
	}
}
