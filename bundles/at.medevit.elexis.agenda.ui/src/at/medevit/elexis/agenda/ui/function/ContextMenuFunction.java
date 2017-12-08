package at.medevit.elexis.agenda.ui.function;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import ch.elexis.agenda.data.Termin;

public class ContextMenuFunction extends BrowserFunction {
	
	private ISelectionProvider selectionProvider;
	
	public ContextMenuFunction(Browser browser, String name){
		super(browser, name);
		
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 1) {
			Termin termin = Termin.load((String) arguments[0]);
			if (selectionProvider != null) {
				selectionProvider.setSelection(new StructuredSelection(termin));
			}
			getBrowser().getMenu().setVisible(true);
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
