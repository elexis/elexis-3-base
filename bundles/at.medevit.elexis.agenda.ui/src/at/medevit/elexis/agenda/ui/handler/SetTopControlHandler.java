package at.medevit.elexis.agenda.ui.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.view.AgendaView;

public class SetTopControlHandler {

	@Inject
	private EPartService partService;

	@Execute
	public Object execute(MPart part,
			@Named("at.medevit.elexis.agenda.ui.command.parameter.controlId") String controlId) {
		partService.showPart(part, PartState.ACTIVATE);
		if (part.getObject() instanceof AgendaView) {
			((AgendaView) part.getObject()).setTopControl(controlId);
		} else {
			LoggerFactory.getLogger(getClass()).error("Part object class " + part.getObject() + " unknown"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
}
