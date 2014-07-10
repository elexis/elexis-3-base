package at.medevit.elexis.impfplan.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class VaccinationHeaderHandler extends AbstractHandler {
	
	public static final String COMMAND_ID = "at.medevit.elexis.impfplan.ui.view.vaccinationHeader";
	public static final String HEADER_TYPE_PARAM = "at.medevit.elexis.impfplan.ui.view.vaccinationHeader.headerType";
	
	public static final String HEADER_WITH_ADMINISTERED_VACCINES = "HWAV";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String parameter = event.getParameter(HEADER_TYPE_PARAM);
		return null;
	}
	
}
