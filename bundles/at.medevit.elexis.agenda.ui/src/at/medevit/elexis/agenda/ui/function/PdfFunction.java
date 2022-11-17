package at.medevit.elexis.agenda.ui.function;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.slf4j.LoggerFactory;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

import ch.elexis.core.ui.e4.util.CoreUiUtil;

public class PdfFunction extends BrowserFunction {

	@Inject
	private EPartService partService;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	private MPart part;

	public PdfFunction(MPart part, Browser browser, String name) {
		super(browser, name);
		this.part = part;
		CoreUiUtil.injectServices(this, part.getContext());
	}

	@Override
	public Object function(Object[] arguments) {
		partService.showPart(part, PartState.ACTIVATE);
		callPdf();
		return null;
	}

	private void callPdf() {
		ParameterizedCommand command = commandService.createCommand("at.medevit.elexis.agenda.ui.PrintSelectedAgenda", //$NON-NLS-1$
				null);
		if (command != null) {
			handlerService.executeHandler(command);
		} else {
			LoggerFactory.getLogger(getClass()).error("Command not found"); //$NON-NLS-1$
		}
	}
}
