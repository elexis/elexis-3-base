package at.medevit.elexis.ehc.ui.docbox.handler;

import ch.elexis.data.Brief;
import ch.elexis.data.Rezept;
import ch.rgw.tools.TimeTool;

public class SavePrescriptionUtil {
	public static void savePrescription(Rezept prescription, String contentType, byte[] content){
		Brief letter =
			new Brief("Docbox Rezept " + prescription.getDate(), new TimeTool(),
				prescription.getMandant(), null, null, Brief.RP);
		letter.setPatient(prescription.getPatient());
		letter.save(content, "pdf");
	}
}
