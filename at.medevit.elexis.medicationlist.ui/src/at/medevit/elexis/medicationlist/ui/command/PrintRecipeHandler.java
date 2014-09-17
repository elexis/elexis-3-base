package at.medevit.elexis.medicationlist.ui.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.ElexisConfigurationConstants;
import ch.elexis.core.ui.views.RezeptBlatt;
import ch.elexis.data.Prescription;
import ch.elexis.data.Rezept;

public class PrintRecipeHandler extends AbstractHandler {
	
	private static Logger log = LoggerFactory.getLogger(PrintRecipeHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		StructuredSelection ss = (StructuredSelection) HandlerUtil.getCurrentSelection(event);
		List<Prescription> toPrint;
		if (ss.isEmpty()) {
			Prescription[] fixmedikation =
				ElexisEventDispatcher.getSelectedPatient().getFixmedikation();
			toPrint = Arrays.asList(fixmedikation);
		} else {
			toPrint = new ArrayList<Prescription>();
			for (@SuppressWarnings("unchecked")
			Iterator<Prescription> iter = ss.iterator(); iter.hasNext();) {
				Prescription element = iter.next();
				toPrint.add(element);
			}
		}
		
		Rezept rp = new Rezept(ElexisEventDispatcher.getSelectedPatient());
		
		for (Prescription prescription : toPrint) {
			rp.addPrescription(prescription);
		}
		
		try {
			RezeptBlatt rpb =
				(RezeptBlatt) HandlerUtil.getActiveSite(event).getPage()
					.showView(ElexisConfigurationConstants.rezeptausgabe);
			rpb.createRezept(rp);
		} catch (PartInitException e) {
			log.error("Error opening RezeptBlatt", e);
		}
		
		return null;
	}
	
}
