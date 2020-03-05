package at.medevit.elexis.agenda.ui.handler;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.slf4j.LoggerFactory;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;

public class SetStatusHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String statusId =
			event.getParameter("at.medevit.elexis.agenda.ui.command.parameter.statusId");
		Optional<IPeriod> period = getSelectedPeriod();
		
		period.ifPresent(p -> {
			AcquireLockBlockingUi.aquireAndRun(p, new ILockHandler() {
				@Override
				public void lockFailed(){
					// do nothing
				}
				
				@Override
				public void lockAcquired(){
					((IAppointment) p).setState(statusId);
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD,
						IAppointment.class);
				}
			});
		});
		return null;
	}
	
	private Optional<IPeriod> getSelectedPeriod(){
		try {
			ISelection activeSelection =
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
			if (activeSelection instanceof StructuredSelection
				&& !((StructuredSelection) activeSelection).isEmpty()) {
				Object element = ((StructuredSelection) activeSelection).getFirstElement();
				if (element instanceof IPeriod) {
					return Optional.of((IPeriod) element);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error setting status", e);
		}
		return Optional.empty();
	}
}
