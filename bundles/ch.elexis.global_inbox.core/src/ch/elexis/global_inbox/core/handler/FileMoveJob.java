package ch.elexis.global_inbox.core.handler;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;

import ch.elexis.core.model.tasks.TaskException;

public class FileMoveJob extends Job {

	private final MoveFileIdentifiedRunnable runnable;
	private final Map<String, Serializable> context;
	private final Logger logger;
	private volatile boolean isStopped = false;

	public FileMoveJob(MoveFileIdentifiedRunnable runnable, Map<String, Serializable> context, Logger logger) {
		super("FileMoveJob");
		this.runnable = runnable;
		this.context = context;
		this.logger = logger;
		setSystem(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		while (!isStopped) {
			try {
				runnable.moveFiles(monitor, logger);
				new GlobalInboxContentProvider(runnable.getDeviceName());
				Thread.sleep(100000);
			} catch (TaskException e) {
				logger.error("Error in FileMoveJob", e);
				return Status.CANCEL_STATUS;
			} catch (InterruptedException e) {
				logger.error("FileMoveJob interrupted", e);
				return Status.CANCEL_STATUS;
			}
		}
		return Status.OK_STATUS;
	}

	public void stopJob() {
		isStopped = true;
		cancel();
		JobManager.getInstance().removeJob(runnable.getDeviceName());
	}
}
