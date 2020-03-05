package at.medevit.elexis.agenda.ui.menu;

import java.util.HashMap;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.holder.AppointmentServiceHolder;

public class SetStatusContributionItem extends ContributionItem {
	
	@Override
	public void fill(Menu menu, int index){
		for (String t : AppointmentServiceHolder.get().getStates()) {
			MenuItem it = new MenuItem(menu, SWT.NONE);
			it.setText(t);
			it.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					MenuItem source = (MenuItem) e.getSource();
					setStatus(source.getText());
				}
			});
		}
	}
	
	private void setStatus(String statusId){
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getService(ICommandService.class);
		Command command =
			commandService.getCommand("at.medevit.elexis.agenda.ui.command.setStatus");
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("at.medevit.elexis.agenda.ui.command.parameter.statusId", statusId);
		ExecutionEvent ev = new ExecutionEvent(command, parameters, null, null);
		try {
			command.executeWithChecks(ev);
		} catch (ExecutionException | NotDefinedException | NotEnabledException
				| NotHandledException ex) {
			LoggerFactory.getLogger(getClass()).error("Error setting status", ex);
		}
	}
}
