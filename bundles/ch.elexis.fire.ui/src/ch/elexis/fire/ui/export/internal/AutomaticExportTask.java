package ch.elexis.fire.ui.export.internal;

import java.time.LocalDateTime;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.fire.core.IFIREService;

public class AutomaticExportTask extends TimerTask {

	public static final String SCHEDULESHOUR_CONFIG = "ch.elexis.fire/automaticexport/scheduledhour";

	public static final String EXPORTDIR_CONFIG = "ch.elexis.fire/automaticexport/exportdir";

	public static final String SCHEDULEDDAY_CONFIG = "ch.elexis.fire/automaticexport/scheduledday";

	public static final String RUN = "ch.elexis.fire/automaticexport/run";

	private boolean shutdownRegistered;

	private IFIREService fireService;

	private static boolean running;

	public AutomaticExportTask(IFIREService fireService) {
		this.fireService = fireService;
	}

	@Override
	public void run() {
		if (!shutdownRegistered) {
			shutdownRegistered = true;
			PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
				@Override
				public boolean preShutdown(IWorkbench workbench, boolean forced) {
					if (LocalConfigService.get(RUN, false)) {
						return MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Warnung",
								"Diese Elexis Instanz soll automatische Auswertungen ausführen. Wollen Sie trotzdem beenden?");
					}
					return true;
				}

				@Override
				public void postShutdown(IWorkbench workbench) {
					// nothing to do here
				}
			});
		}

		// lookup run in local config
		if (LocalConfigService.get(RUN, false)) {
			// run daily after the configured scheduled hour (default 01:00)
			if (!wasScheduledToday() && isAfterScheduledHour()) {
				ConfigServiceHolder.setGlobal(SCHEDULEDDAY_CONFIG, LocalDateTime.now().getDayOfMonth());
			}
		}
	}

	private boolean wasScheduledToday() {
		int lastSetValue = ConfigServiceHolder.getGlobal(SCHEDULEDDAY_CONFIG, -1);
		return LocalDateTime.now().getDayOfMonth() == lastSetValue;
	}

	private boolean isAfterScheduledHour() {
		return getScheduledHour() <= LocalDateTime.now().getHour();
	}

	private Integer getScheduledHour() {
		int configuredValue = ConfigServiceHolder.getGlobal(SCHEDULESHOUR_CONFIG, 1);
		return configuredValue;
	}
}
