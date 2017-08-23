package at.medevit.elexis.outbox.ui.propertytester;

import java.util.List;

import at.medevit.elexis.outbox.model.OutboxElement;

public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {
	
	public PropertyTester(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		if ("enabled".equals(property)) { //$NON-NLS-1$
			try {
				if (receiver instanceof List<?>) {
					List<?> receiverAsList = (List<?>) receiver;
					for (Object o : receiverAsList) {
						if (o instanceof OutboxElement) {
							OutboxElement outboxElement = (OutboxElement) o;
							String lbl = outboxElement.getLabel();
							if (lbl == null || !lbl.toLowerCase().endsWith(".xml")) {
								return false;
							}
						}
					}
					return receiverAsList.size() > 0;
				}
			} catch (Exception ise) {
				// do nothing, false is returned
			}
		}
		return false;
	}
	
}