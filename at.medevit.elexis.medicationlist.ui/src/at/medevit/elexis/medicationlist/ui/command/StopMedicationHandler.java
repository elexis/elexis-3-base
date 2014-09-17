package at.medevit.elexis.medicationlist.ui.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.data.Prescription;

public class StopMedicationHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		StructuredSelection ss = (StructuredSelection) HandlerUtil.getCurrentSelection(event);
		Prescription presc = (Prescription) ss.getFirstElement();
		if (presc == null)
			return null;
		
		
		
		
		return null;
	}
	
}
