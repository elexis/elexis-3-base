package ch.elexis.hl7.message.core.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ca.uhn.hl7v2.HL7Exception;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.model.util.ElexisIdGenerator;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.hl7.data.HL7Konsultation;
import ch.elexis.hl7.data.HL7Mandant;
import ch.elexis.hl7.data.HL7Patient;
import ch.elexis.hl7.message.core.IHL7MessageService;
import ch.elexis.hl7.v231.HL7_ADT_A08;

public class ADT_A08Message implements IHL7Message {

	public List<String> validateContext(Map<String, Object> context) {
		List<String> ret = new ArrayList<>();
		if (context.get(IHL7MessageService.CONTEXT_PATIENT) == null) {
			ret.add(IHL7MessageService.CONTEXT_PATIENT);
		}
		if (context.get(IHL7MessageService.CONTEXT_MANDANTOR) == null) {
			ret.add(IHL7MessageService.CONTEXT_MANDANTOR);
		}
		if (context.get(IHL7MessageService.CONTEXT_CONSULTATION) == null) {
			ret.add(IHL7MessageService.CONTEXT_CONSULTATION);
		}
		return ret;
	}

	@Override
	public String getMessage(Map<String, Object> context) throws ElexisException {
		if (context != null && !context.isEmpty()) {
			String uniqueMessageControlID = ElexisIdGenerator.generateId();
			String uniqueProcessingID = ElexisIdGenerator.generateId();

			String receivingApplication = (String) context.get(IHL7MessageService.CONTEXT_RECEIVINGAPPLICATION);
			String receivingFacility = (String) context.get(IHL7MessageService.CONTEXT_RECEIVINGFACILITY);

			Mandant eMandant = (Mandant) context.get(IHL7MessageService.CONTEXT_MANDANTOR);
			HL7Mandant mandant = HL7MessageUtil.mandantOf(eMandant);

			HL7_ADT_A08 message = new HL7_ADT_A08("CHELEXIS", "PATDATA", receivingApplication, StringUtils.EMPTY, //$NON-NLS-1$ //$NON-NLS-2$
					receivingFacility, uniqueMessageControlID, uniqueProcessingID, mandant);
			Patient ePatient = (Patient) context.get(IHL7MessageService.CONTEXT_PATIENT);
			HL7Patient patient = HL7MessageUtil.patientOf(ePatient);

			Konsultation eConsultation = (Konsultation) context.get(IHL7MessageService.CONTEXT_CONSULTATION);
			HL7Konsultation consultation = HL7MessageUtil.consultationOf(eConsultation);

			try {
				return message.createText(patient, consultation);
			} catch (HL7Exception e) {
				throw new ElexisException("Error creating HL7 message, see wrapped exception", e); //$NON-NLS-1$
			}
		}
		throw new ElexisException("No context for creating HL7 message available"); //$NON-NLS-1$
	}

	@Override
	public String getHL7Version() {
		return "2.3.1"; //$NON-NLS-1$
	}
}
