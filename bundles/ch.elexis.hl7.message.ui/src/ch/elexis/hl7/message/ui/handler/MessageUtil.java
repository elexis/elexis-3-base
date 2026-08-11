package ch.elexis.hl7.message.ui.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.hl7.message.core.IHL7MessageService;
import ch.elexis.hl7.message.ui.preference.PreferenceUtil;

public class MessageUtil {

	public static Map<String, Object> getContext() {
		Map<String, Object> ret = new HashMap<>();
		IContextService contextService = ContextServiceHolder.get();
		Patient patient = contextService.getActivePatient()
				.map(p -> NoPoUtil.loadAsPersistentObject(p, Patient.class)).orElse(null);
		Konsultation cons = contextService.getTyped(IEncounter.class)
				.map(e -> NoPoUtil.loadAsPersistentObject(e, Konsultation.class)).orElse(null);
		Mandant mandant = contextService.getActiveMandator()
				.map(m -> NoPoUtil.loadAsPersistentObject(m, Mandant.class)).orElse(null);
		if (patient != null) {
			ret.put(IHL7MessageService.CONTEXT_PATIENT, patient);
		}
		if (cons != null) {
			ret.put(IHL7MessageService.CONTEXT_CONSULTATION, cons);
		}
		if (mandant != null) {
			ret.put(IHL7MessageService.CONTEXT_MANDANTOR, mandant);
		}
		return ret;
	}

	public static void export(String typ, String message, String encoding) throws IOException {
		Optional<IVirtualFilesystemHandle> outputDir = PreferenceUtil.getOutputDirectoryHandle();
		if (outputDir.isEmpty()) {
			throw new IOException("No usable HL7 message output directory configured"); //$NON-NLS-1$
		}
		IVirtualFilesystemHandle outputFile = outputDir.get().subFile(System.currentTimeMillis() + "_" + typ + ".hl7"); //$NON-NLS-1$ //$NON-NLS-2$
		try (OutputStream out = outputFile.openOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, encoding))) {
			writer.write(message);
		}
	}
}
