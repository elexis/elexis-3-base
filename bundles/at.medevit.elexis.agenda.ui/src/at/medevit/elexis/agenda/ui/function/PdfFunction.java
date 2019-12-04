package at.medevit.elexis.agenda.ui.function;

import java.util.Collections;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.services.IEvaluationService;
import org.slf4j.LoggerFactory;

public class PdfFunction extends BrowserFunction {
	
	public PdfFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(Object[] arguments){
		try {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.showView("at.medevit.elexis.agenda.ui.view.agenda");
			callPdf(view);
		} catch (PartInitException e) {
			LoggerFactory.getLogger(getClass()).error("Error switching agenda", e);
		}
		return null;
	}
	
	private void callPdf(IViewPart view){
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getService(ICommandService.class);
		Command command =
			commandService.getCommand("at.medevit.elexis.agenda.ui.PrintSelectedAgenda");
		
		IEvaluationService evaluationService = (IEvaluationService) PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getService(IEvaluationService.class);
		
		ExecutionEvent ev = new ExecutionEvent(command, Collections.emptyMap(), null,
			evaluationService.getCurrentState());
		try {
			command.executeWithChecks(ev);
		} catch (ExecutionException | NotDefinedException | NotEnabledException
				| NotHandledException ex) {
			LoggerFactory.getLogger(getClass()).error("Error calling pdf", ex);
		}
	}
}
