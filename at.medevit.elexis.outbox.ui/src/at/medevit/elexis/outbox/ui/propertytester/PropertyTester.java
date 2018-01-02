package at.medevit.elexis.outbox.ui.propertytester;

import java.util.List;

import at.medevit.elexis.outbox.model.OutboxElement;

public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {
	
	public PropertyTester(){
	}
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		if ("enabled".equals(property)) {
			try {
				if (receiver instanceof List<?>) {
					List<?> receiverAsList = (List<?>) receiver;
					for (Object o : receiverAsList) {
						if (o instanceof OutboxElement) {
							OutboxElement outboxElement = (OutboxElement) o;
							String lbl = outboxElement.getLabel();
							// because of an issue in ehealth DocumentDescriptor we can only handle pdf and xml files.
							if (lbl == null || (!lbl.toLowerCase().endsWith(".xml")
								&& !lbl.toLowerCase().endsWith(".pdf"))) {
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