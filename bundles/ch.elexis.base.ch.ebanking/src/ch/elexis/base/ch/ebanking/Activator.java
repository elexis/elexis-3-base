package ch.elexis.base.ch.ebanking;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

import ch.elexis.base.ch.ebanking.command.LoadESRFileHandler;

public class Activator extends AbstractUIPlugin {

	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		UIJob job = new UIJob("InitCommandsWorkaround") {

			public IStatus runInUIThread(@SuppressWarnings("unused") IProgressMonitor monitor) {

				ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getService(ICommandService.class);
				Command command = commandService.getCommand(LoadESRFileHandler.COMMAND_ID);
				command.isEnabled();
				return new Status(IStatus.OK, "my.plugin.id", "Init commands workaround performed succesfully");
			}

		};
		job.schedule();
	}
}
