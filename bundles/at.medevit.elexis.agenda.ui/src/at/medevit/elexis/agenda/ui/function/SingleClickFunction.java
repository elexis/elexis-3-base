package at.medevit.elexis.agenda.ui.function;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class SingleClickFunction extends BrowserFunction {
	
	private ISelectionProvider selectionProvider;
	
	public SingleClickFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 1) {
			IAppointment termin = CoreModelServiceHolder.get()
				.load((String) arguments[0], IAppointment.class).orElse(null);
			ContextServiceHolder.get().getRootContext().setTyped(termin);
			if (selectionProvider != null) {
				selectionProvider.setSelection(new StructuredSelection(termin));
			}
			IContact contact = termin.getContact();
			if (contact != null && contact.isPatient()) {
				ContextServiceHolder.get().setActivePatient(
					CoreModelServiceHolder.get().load(contact.getId(), IPatient.class).get());
			}
		} else if (arguments.length == 0) {
			if (selectionProvider != null) {
				selectionProvider.setSelection(StructuredSelection.EMPTY);
			}
		}
		return null;
	}
	
	public void setSelectionProvider(ISelectionProvider selectionProvider){
		this.selectionProvider = selectionProvider;
	}
}
