package at.medevit.elexis.agenda.ui.function;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.slf4j.LoggerFactory;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

import at.medevit.elexis.agenda.ui.view.AgendaView;

public class SwitchFunction extends BrowserFunction {

	private MPart part;

	public SwitchFunction(MPart part, Browser browser, String name) {
		super(browser, name);
		this.part = part;
	}

	@Override
	public Object function(Object[] arguments) {
		if (arguments.length == 1) {
			String to = (String) arguments[0];

			if (part.getObject() instanceof AgendaView) {
				((AgendaView) part.getObject()).setTopControl(to);
			} else {
				LoggerFactory.getLogger(getClass()).error("Part object class " + part.getObject() + " unknown"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return null;
	}
}
