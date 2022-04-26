package ch.elexis.hl7.message.core.message;

import java.util.List;
import java.util.Map;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.hl7.message.core.IHL7MessageService;

/**
 * Subclasses represent a HL7 message.
 *
 * @author thomas
 *
 */
public interface IHL7Message {

	/**
	 * Test if the context contains the objects needed to create the message. The
	 * returned List contains the missing keys (e.g.
	 * {@link IHL7MessageService#CONTEXT_PATIENT}), or empty if the context is
	 * valid.
	 *
	 * @param context
	 * @return
	 */
	public List<String> validateContext(Map<String, Object> context);

	/**
	 * Get the message string.
	 *
	 * @param context
	 * @return
	 * @throws ElexisException
	 */
	public String getMessage(Map<String, Object> context) throws ElexisException;

	/**
	 * Get the HL7 version of the message.
	 *
	 * @return
	 */
	public String getHL7Version();
}
