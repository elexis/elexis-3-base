package at.medevit.elexis.agenda.ui.menu;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.holder.AppointmentServiceHolder;

public class SetStatusContributionItem {
	
	@SuppressWarnings("restriction")
	@Inject
	private ECommandService commandService;
	
	@SuppressWarnings("restriction")
	@Inject
	private EHandlerService handlerService;
	
	@AboutToShow
	public void fill(List<MMenuElement> items){
		for (String t : AppointmentServiceHolder.get().getStates()) {
			MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE.createDirectMenuItem();
			dynamicItem.setLabel(t);
			dynamicItem.setContributionURI(
				"bundleclass://at.medevit.elexis.agenda.ui/" + getClass().getName());
			items.add(dynamicItem);
		}
	}
	
	@SuppressWarnings("restriction")
	@Execute
	private void setStatus(MDirectMenuItem menuItem){
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("at.medevit.elexis.agenda.ui.command.parameter.statusId",
			menuItem.getLabel());
		ParameterizedCommand command = commandService
			.createCommand("at.medevit.elexis.agenda.ui.command.setStatus", parameters);
		if (command != null) {
			handlerService.executeHandler(command);
		} else {
			LoggerFactory.getLogger(getClass()).error("Command not found");
		}
		
		//		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
		//			.getActiveWorkbenchWindow().getService(ICommandService.class);
		//		Command command =
		//			commandService.getCommand();
		//		
		//
		//		ExecutionEvent ev = new ExecutionEvent(command, parameters, null, null);
		//		try {
		//			command.executeWithChecks(ev);
		//		} catch (ExecutionException | NotDefinedException | NotEnabledException
		//				| NotHandledException ex) {
		//			LoggerFactory.getLogger(getClass()).error("Error setting status", ex);
		//		}
	}
}
