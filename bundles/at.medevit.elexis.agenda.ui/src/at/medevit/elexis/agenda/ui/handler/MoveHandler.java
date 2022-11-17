package at.medevit.elexis.agenda.ui.handler;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.view.AgendaView;
import ch.elexis.core.model.IPeriod;

public class MoveHandler {

	@Inject
	private ESelectionService selectionService;

	@Execute
	public Object execute(MPart part) {
		Optional<IPeriod> period = getSelectedPeriod();

		period.ifPresent(p -> {
			SideBarComposite sideBar = null;
			if (part.getObject() instanceof AgendaView) {
				AgendaView view = (AgendaView) part.getObject();
				sideBar = view.getParallelSideBarComposite();
			}
			if (sideBar != null) {
				sideBar.addMovePeriod(p);
			}
		});
		return null;
	}

	private Optional<IPeriod> getSelectedPeriod() {
		try {
			ISelection activeSelection = (ISelection) selectionService.getSelection();
			if (activeSelection instanceof StructuredSelection && !((StructuredSelection) activeSelection).isEmpty()) {
				Object element = ((StructuredSelection) activeSelection).getFirstElement();
				if (element instanceof IPeriod) {
					return Optional.of((IPeriod) element);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error setting status", e); //$NON-NLS-1$
		}
		return Optional.empty();
	}
}
