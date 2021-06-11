package at.medevit.elexis.outbox.ui.propertytester;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.TreeSelection;

import at.medevit.elexis.outbox.model.IOutboxElement;

public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {
	
	public PropertyTester(){
	}
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		if (receiver instanceof TreeSelection) {
			receiver = ((TreeSelection) receiver).getFirstElement();
		}
		if ("xdmEnabled".equals(property)) {
			try {
				if (receiver instanceof List<?>) {
					List<?> receiverAsList = (List<?>) receiver;
					return receiverAsList.size() > 0;
				}
			} catch (Exception ise) {
				// do nothing, false is returned
			}
		} else if ("isObjectClass".equals(property)) {
			if (StringUtils.isNotBlank((String) args[0])) {
				String className = (String) args[0];
				if (receiver instanceof IOutboxElement) {
					Object object = ((IOutboxElement) receiver).getObject();
					if (object != null) {
						return object.getClass().getSimpleName().contains(className);
					}
				}
			}
		}
		return false;
	}
	
}