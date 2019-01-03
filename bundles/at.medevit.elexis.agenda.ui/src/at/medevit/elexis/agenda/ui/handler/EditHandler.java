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

import ch.elexis.agenda.data.IPlannable;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.model.IPersistentObject;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.dialogs.TerminDialog;
import ch.elexis.dialogs.TerminDialog.CollisionErrorLevel;

public class EditHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Optional<IPeriod> period = getSelectedPeriod();
		
		period.ifPresent(p -> {
			AcquireLockBlockingUi.aquireAndRun((IPersistentObject) p, new ILockHandler() {
				@Override
				public void lockFailed(){
					// do nothing
				}
				
				@Override
				public void lockAcquired(){
					TerminDialog dlg = new TerminDialog((IPlannable) p);
					dlg.setCollisionErrorLevel(CollisionErrorLevel.WARNING);
					dlg.open();
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
