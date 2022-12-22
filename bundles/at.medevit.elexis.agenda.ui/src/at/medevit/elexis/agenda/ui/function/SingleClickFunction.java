package at.medevit.elexis.agenda.ui.function;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

import at.medevit.elexis.agenda.ui.composite.ScriptingHelper;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class SingleClickFunction extends BrowserFunction {

	private ISelectionProvider selectionProvider;

	public SingleClickFunction(Browser browser, String name) {
		super(browser, name);
	}

	@Override
	public Object function(Object[] arguments) {
		if (arguments.length == 1) {
			IAppointment termin = CoreModelServiceHolder.get().load((String) arguments[0], IAppointment.class)
					.orElse(null);
			if (termin != null) {
				ContextServiceHolder.get().getRootContext().setNamed(ContextServiceHolder.SELECTIONFALLBACK, termin);
				if (selectionProvider != null) {
					selectionProvider.setSelection(new StructuredSelection(termin));
				}
				IContact contact = termin.getContact();
				if (contact != null && contact.isPatient()) {
					ContextServiceHolder.get().getRootContext().setNamed(ContextServiceHolder.SELECTIONFALLBACK,
							CoreModelServiceHolder.get().load(contact.getId(), IPatient.class).get());
				}
			} else {
				// the event could not be loaded, trigger refetch
				new ScriptingHelper(getBrowser()).refetchEvents();
			}
		} else if (arguments.length == 0) {
			if (selectionProvider != null) {
				selectionProvider.setSelection(StructuredSelection.EMPTY);
			}
		}
		return null;
	}

	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}
}
