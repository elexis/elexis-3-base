package ch.elexis.hl7.message.ui.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.hl7.message.core.IHL7MessageService;
import ch.elexis.hl7.message.ui.preference.PreferenceUtil;

public class MessageUtil {
	
	public static Map<String, Object> getContext(){
		Map<String, Object> ret = new HashMap<>();
		ret.put(IHL7MessageService.CONTEXT_RECEIVINGAPPLICATION, "IHECVX");
		ret.put(IHL7MessageService.CONTEXT_RECEIVINGFACILITY, "Cardio Report");
		
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		Konsultation cons = (Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
		Mandant mandant = ElexisEventDispatcher.getSelectedMandator();
		if (patient != null && cons != null && mandant != null) {
			ret.put(IHL7MessageService.CONTEXT_PATIENT, patient);
			ret.put(IHL7MessageService.CONTEXT_CONSULTATION, cons);
			ret.put(IHL7MessageService.CONTEXT_MANDANTOR, mandant);
		} else {
			ret.clear();
		}
		return ret;
	}
	
	public static void export(String typ, String message) throws IOException{
		Optional<File> outputDir = PreferenceUtil.getOutputDirectory();
		if (outputDir.isPresent()) {
			File outputFile =
				new File(outputDir.get(), System.currentTimeMillis() + "_" + typ + ".hl7");
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
				writer.write(message);
			}
		}
	}
}
