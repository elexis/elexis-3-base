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
import ch.elexis.core.model.IAppointment;

public class CopyHandler {

	@Inject
	private ESelectionService selectionService;

	private static Optional<IAppointment> copyAppontment = Optional.empty();

	@Execute
	public Object execute(MPart part) {
		Optional<IAppointment> appointment = getSelectedAppointment();

		appointment.ifPresent(appt -> {
			SideBarComposite sideBar = null;
			if (part.getObject() instanceof AgendaView) {
				AgendaView view = (AgendaView) part.getObject();
				sideBar = view.getParallelSideBarComposite();
			}
			if (sideBar != null) {
				sideBar.addcopyAppointment(appt);
			}
			copyAppontment = Optional.of(appt);
		});
		return null;
	}

	private Optional<IAppointment> getSelectedAppointment() {
		try {
			ISelection activeSelection = (ISelection) selectionService.getSelection();
			if (activeSelection instanceof StructuredSelection && !((StructuredSelection) activeSelection).isEmpty()) {
				Object element = ((StructuredSelection) activeSelection).getFirstElement();
				if (element instanceof IAppointment) {
					return Optional.of((IAppointment) element);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error copying appointment", e); //$NON-NLS-1$
		}
		return Optional.empty();
	}

	public static Optional<IAppointment> getCopiedAppointment() {
		return copyAppontment;
	}

	public static void clearCopiedAppointment() {
		copyAppontment = Optional.empty();
	}
}
