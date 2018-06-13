package ch.elexis.hl7.message.core;

import java.util.Map;

import ch.elexis.core.exceptions.ElexisException;

public interface IHL7MessageService {
	
	public String CONTEXT_PATIENT = "context.patient";
	public String CONTEXT_CONSULTATION = "context.consultation";
	public String CONTEXT_MANDANTOR = "context.mandator";
	
	public String CONTEXT_RECEIVINGAPPLICATION = "context.msg.receivingapplication";
	public String CONTEXT_RECEIVINGFACILITY = "context.msg.receivingfacility";
	
	public String CONTEXT_HL7VERSION_HINT = "context.hl7version";
	
	/**
	 * Get the HL7 message of typ messageTyp (ADT_A08, ...). If context is null the service tries to
	 * get the context from the active selection.
	 * 
	 * @param context
	 * @return
	 */
	
	/**
	 * Get the HL7 message of typ messageTyp (ADT_A08, ...).
	 * 
	 * @param messageTyp
	 * @param context
	 * @return
	 * @throws ElexisException
	 *             if unknown messageTyp, or no context data
	 */
	public String getMessage(String messageTyp, Map<String, Object> context) throws ElexisException;
}
