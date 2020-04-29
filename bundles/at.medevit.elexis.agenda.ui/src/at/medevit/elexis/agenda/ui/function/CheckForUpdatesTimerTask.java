package at.medevit.elexis.agenda.ui.function;

import java.util.TimerTask;

import org.eclipse.e4.ui.di.UISynchronize;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class CheckForUpdatesTimerTask extends TimerTask {

	private LoadEventsFunction loadEventsFunction;
	private UISynchronize uiSynchronize;

	public CheckForUpdatesTimerTask(LoadEventsFunction loadEventsFunction, UISynchronize uiSynchronize) {
		this.loadEventsFunction = loadEventsFunction;
		this.uiSynchronize = uiSynchronize;
	}

	@Override
	public void run() {
		long currentLastUpdate = CoreModelServiceHolder.get().getHighestLastUpdate(IAppointment.class);
		if (loadEventsFunction.knownLastUpdate != 0 && loadEventsFunction.knownLastUpdate < currentLastUpdate) {
			uiSynchronize.asyncExec(new Runnable() {
				@Override
				public void run() {
					loadEventsFunction.scriptingHelper.refetchEvents();
				}
			});
		}
	}

}
