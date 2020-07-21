package at.medevit.elexis.emediplan.ui.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.data.Brief;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class SaveEMediplanUtil {
	
	public static String writeTempPdf(ByteArrayOutputStream pdf)
		throws FileNotFoundException, IOException{
		File pdfFile = File.createTempFile("eMediplan_" + System.currentTimeMillis(), ".pdf");
		try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
			fos.write(pdf.toByteArray());
			fos.flush();
		}
		return pdfFile.getAbsolutePath();
	}
	
	public static IDocument saveEMediplan(IPatient patient, IMandator mandant, byte[] content){
		TimeTool now = new TimeTool();
		Brief letter = new Brief("eMediplan " + now.toString(TimeTool.DATE_GER), now,
			Mandant.load(mandant.getId()), null, null, Brief.UNKNOWN);
		letter.setPatient(Patient.load(patient.getId()));
		letter.save(content, "pdf");
		return CoreModelServiceHolder.get().load(letter.getId(), IDocumentLetter.class)
			.orElse(null);
	}
}
