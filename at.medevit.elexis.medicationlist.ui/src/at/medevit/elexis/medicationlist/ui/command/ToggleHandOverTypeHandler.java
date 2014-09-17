package at.medevit.elexis.medicationlist.ui.command;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import at.medevit.elexis.medicationlist.model.AbgabeTyp;

public class ToggleHandOverTypeHandler extends AbstractHandler implements IElementUpdater {
	
	private AbgabeTyp state = AbgabeTyp.APPLIKATION;
	
	public static final String ID = "at.medevit.elexis.medicationlist.ui.command.toggleHandOverType";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		state = state.getNext();
		requestRefresh();
		return null;
	}
	
	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes")
	Map parameters){
		element.setText(state.acronym);
		element.setTooltip(state.tooltip);
	}
	
	protected void requestRefresh(){
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ICommandService commandService = (ICommandService) window.getService(ICommandService.class);
		if (commandService != null) {
			commandService.refreshElements(ID, null);
		}
	}
	
}
