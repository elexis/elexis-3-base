package at.medevit.elexis.agenda.ui.handler;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.view.AgendaView;
import at.medevit.elexis.agenda.ui.view.ParallelView;
import ch.elexis.core.data.interfaces.IPeriod;

public class MoveHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Optional<IPeriod> period = getSelectedPeriod();
		
		period.ifPresent(p -> {
			IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActivePart();
			SideBarComposite sideBar = null;
			if (activePart instanceof AgendaView) {
				AgendaView view = (AgendaView) activePart;
				sideBar = view.getParallelSideBarComposite();
			} else if (activePart instanceof ParallelView) {
				ParallelView view = (ParallelView) activePart;
				sideBar = view.getSideBarComposite();
			}
			if (sideBar != null) {
				sideBar.addMovePeriod(p);
			}
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
