package at.medevit.elexis.gdt.propertyTester;

import org.eclipse.core.expressions.PropertyTester;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.data.GDTProtokoll;

public class GDTSatzNachrichtType extends PropertyTester {
	
	public GDTSatzNachrichtType(){}
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		GDTProtokoll gdtEntry = (GDTProtokoll) receiver;
		String messageType = gdtEntry.getMessageType();
		int messageTypeInt = Integer.parseInt(messageType);
		
		if (property.equalsIgnoreCase("isGDTSatznachricht6310")
			&& messageTypeInt == GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN)
			return true;
		
		return false;
	}
	
}
