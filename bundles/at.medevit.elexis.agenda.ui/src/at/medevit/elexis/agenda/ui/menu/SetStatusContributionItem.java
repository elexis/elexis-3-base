package at.medevit.elexis.agenda.ui.menu;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import jakarta.inject.Inject;

public class SetStatusContributionItem {

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	@Inject
	private ESelectionService eSelectionService;

	@AboutToShow
	public void fill(List<MMenuElement> items) {
		IStructuredSelection selection = (IStructuredSelection) eSelectionService.getSelection();
		IAppointment selectedAppointment = (selection != null) ? (IAppointment) selection.getFirstElement() : null;
		String state = (selectedAppointment != null) ? selectedAppointment.getState() : null;

		for (String t : AppointmentServiceHolder.get().getStates()) {
			MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE.createDirectMenuItem();
			dynamicItem.setType(ItemType.CHECK);
			dynamicItem.setLabel(t);
			dynamicItem.setContributionURI("bundleclass://at.medevit.elexis.agenda.ui/" + getClass().getName()); //$NON-NLS-1$
			dynamicItem.setSelected(t.equalsIgnoreCase(state));
			items.add(dynamicItem);
		}
	}

	@Execute
	private void setStatus(MDirectMenuItem menuItem) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("at.medevit.elexis.agenda.ui.command.parameter.statusId", //$NON-NLS-1$
				menuItem.getLabel());
		ParameterizedCommand command = commandService.createCommand("at.medevit.elexis.agenda.ui.command.setStatus", //$NON-NLS-1$
				parameters);
		if (command != null) {
			handlerService.executeHandler(command);
		} else {
			LoggerFactory.getLogger(getClass()).error("Command not found"); //$NON-NLS-1$
		}

		// ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
		// .getActiveWorkbenchWindow().getService(ICommandService.class);
		// Command command =
		// commandService.getCommand();
		//
		//
		// ExecutionEvent ev = new ExecutionEvent(command, parameters, null, null);
		// try {
		// command.executeWithChecks(ev);
		// } catch (ExecutionException | NotDefinedException | NotEnabledException
		// | NotHandledException ex) {
		// LoggerFactory.getLogger(getClass()).error("Error setting status", ex);
		// }
	}
}
