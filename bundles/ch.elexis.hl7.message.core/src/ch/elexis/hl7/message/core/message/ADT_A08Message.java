package ch.elexis.hl7.message.core.message;

import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.Map;

import ca.uhn.hl7v2.HL7Exception;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Anwender;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.hl7.data.HL7Konsultation;
import ch.elexis.hl7.data.HL7Mandant;
import ch.elexis.hl7.data.HL7Patient;
import ch.elexis.hl7.message.core.IHL7MessageService;
import ch.elexis.hl7.v231.HL7_ADT_A08;
import ch.rgw.tools.StringTool;

public class ADT_A08Message implements IHL7Message {
	
	@Override
	public String getMessage(Map<String, Object> context) throws ElexisException{
		if (context == null || !context.isEmpty()) {
			String uniqueMessageControlID = StringTool.unique("MessageControlID"); //$NON-NLS-1$
			String uniqueProcessingID = StringTool.unique("ProcessingID"); //$NON-NLS-1$
			
			String receivingApplication =
				(String) context.get(IHL7MessageService.CONTEXT_RECEIVINGAPPLICATION);
			String receivingFacility =
				(String) context.get(IHL7MessageService.CONTEXT_RECEIVINGFACILITY);
			
			Mandant eMandant = (Mandant) context.get(IHL7MessageService.CONTEXT_MANDANTOR);
			HL7Mandant mandant = HL7MessageUtil.mandantOf(eMandant);
			mandant.setLabel(eMandant.get(Anwender.FLD_LABEL));
			mandant.setEan(eMandant.getXid(DOMAIN_EAN));
			
			HL7_ADT_A08 message = new HL7_ADT_A08("CHELEXIS", "PATDATA", receivingApplication, "",
				receivingFacility, uniqueMessageControlID, uniqueProcessingID, mandant);
			Patient ePatient = (Patient) context.get(IHL7MessageService.CONTEXT_PATIENT);
			HL7Patient patient = HL7MessageUtil.patientOf(ePatient);
			
			Konsultation eConsultation =
				(Konsultation) context.get(IHL7MessageService.CONTEXT_CONSULTATION);
			HL7Konsultation consultation = HL7MessageUtil.consultationOf(eConsultation);
			
			try {
				return message.createText(patient, consultation);
			} catch (HL7Exception e) {
				throw new ElexisException("Error creating HL7 message, see wrapped exception", e);
			}
		}
		throw new ElexisException("No context for creating HL7 message available");
	}
	
	@Override
	public String getHL7Version(){
		return "2.3.1";
	}
}
