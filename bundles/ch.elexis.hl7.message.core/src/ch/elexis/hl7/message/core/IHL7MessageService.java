package ch.elexis.hl7.message.core;

import java.util.List;
import java.util.Map;

import ch.elexis.core.exceptions.ElexisException;

public interface IHL7MessageService {

	public String CONTEXT_PATIENT = "context.patient"; //$NON-NLS-1$
	public String CONTEXT_CONSULTATION = "context.consultation"; //$NON-NLS-1$
	public String CONTEXT_MANDANTOR = "context.mandator"; //$NON-NLS-1$

	public String CONTEXT_RECEIVINGAPPLICATION = "context.msg.receivingapplication"; //$NON-NLS-1$
	public String CONTEXT_RECEIVINGFACILITY = "context.msg.receivingfacility"; //$NON-NLS-1$

	public String CONTEXT_HL7VERSION_HINT = "context.hl7version"; //$NON-NLS-1$

	/**
	 * Test if the context contains the objects needed to create the message. The
	 * returned List contains the missing keys (e.g.
	 * {@link IHL7MessageService#CONTEXT_PATIENT}), or empty if the context is
	 * valid.
	 *
	 * @param messageTyp
	 * @param context
	 * @return
	 * @throws ElexisException
	 */
	public List<String> validateContext(String messageTyp, Map<String, Object> context) throws ElexisException;

	/**
	 * Get the HL7 message of typ messageTyp (ADT_A08, ...).
	 *
	 * @param messageTyp
	 * @param context
	 * @return
	 * @throws ElexisException if unknown messageTyp, or no context data
	 */
	public String getMessage(String messageTyp, Map<String, Object> context) throws ElexisException;
}
