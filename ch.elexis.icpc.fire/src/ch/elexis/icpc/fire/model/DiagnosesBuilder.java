package ch.elexis.icpc.fire.model;

import java.util.List;
import java.util.Optional;

import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.icpc.Encounter;
import ch.elexis.icpc.IcpcCode;
import ch.elexis.icpc.fire.model.TConsultation.Diagnoses;

public class DiagnosesBuilder {

	private Konsultation consultation;
	private FireConfig config;
	
	public DiagnosesBuilder(FireConfig config){
		this.config = config;
	}
	
	public DiagnosesBuilder consultation(Konsultation consultation){
		this.consultation = consultation;
		return this;
	}
	
	public Optional<Diagnoses> build(){
		Query<Encounter> qbe = new Query<Encounter>(Encounter.class);
		qbe.add("KonsID", "=", consultation.getId());
		List<Encounter> encounters = qbe.execute();
		if(!encounters.isEmpty()) {
			Diagnoses ret = config.getFactory().createTConsultationDiagnoses();
			for (Encounter enc : encounters) {
				IcpcCode diag = enc.getDiag();
				if (diag != null) {
					TDiagnose tDiag = config.getFactory().createTDiagnose();
					tDiag.setIcpc(diag.getCode());
					tDiag.setDescription(diag.getText());
					ret.getDiagnose().add(tDiag);
				}
			}
			if (!ret.getDiagnose().isEmpty()) {
				return Optional.of(ret);
			}
			
		}
		return Optional.empty();
	}
	
}
