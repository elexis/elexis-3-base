package ch.elexis.hl7.message.core.message;

import java.util.Map;

import ch.elexis.core.exceptions.ElexisException;

/**
 * Subclasses represent a HL7 message.
 * 
 * @author thomas
 *
 */
public interface IHL7Message {
	
	public String getMessage(Map<String, Object> context) throws ElexisException;
	
	public String getHL7Version();
}
