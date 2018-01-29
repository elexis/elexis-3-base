package at.medevit.elexis.ehc.ui.vacdoc.property;

import at.medevit.elexis.ehc.ui.vacdoc.service.MeineImpfungenServiceHolder;

public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {
	
	public PropertyTester(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		if ("enabled".equals(property)) { //$NON-NLS-1$
			try {
				return MeineImpfungenServiceHolder.getService().isVaild();
			} catch (IllegalStateException ise) {
				// do nothing, false is returned
			}
		}
		return false;
	}
	
}
