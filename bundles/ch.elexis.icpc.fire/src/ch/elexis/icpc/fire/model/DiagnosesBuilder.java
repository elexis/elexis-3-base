package ch.elexis.icpc.fire.model;

import java.util.List;
import java.util.Optional;

import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.data.Konsultation;
import ch.elexis.icpc.fire.model.TConsultation.Diagnoses;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import ch.elexis.icpc.model.icpc.IcpcPackage;
import ch.elexis.icpc.service.IcpcModelServiceHolder;

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
		IEncounter encounter =
			CoreModelServiceHolder.get().load(consultation.getId(), IEncounter.class).orElse(null);
		IQuery<IcpcEncounter> query = IcpcModelServiceHolder.get().getQuery(IcpcEncounter.class);
		query.and(IcpcPackage.Literals.ICPC_ENCOUNTER__ENCOUNTER, COMPARATOR.EQUALS, encounter);
		List<IcpcEncounter> icpcEncounters = query.execute();
		
		if (!icpcEncounters.isEmpty()) {
			Diagnoses ret = config.getFactory().createTConsultationDiagnoses();
			for (IcpcEncounter enc : icpcEncounters) {
				IcpcCode diag = enc.getDiag();
				if (diag != null) {
					TDiagnose tDiag = config.getFactory().createTDiagnose();
					tDiag.setIcpc(diag.getCode());
					tDiag.setDescription(diag.getText());
					ret.getDiagnose().add(tDiag);
				}
				
				IcpcCode reason = enc.getRfe();
				if (reason != null)
				{
					TDiagnose tReason = config.getFactory().createTDiagnose();
					tReason.setIcpc(reason.getCode());
					tReason.setDescription(reason.getText());
					ret.getReason().add(tReason);
				}
			}
			if (!ret.getDiagnose().isEmpty()) {
				return Optional.of(ret);
			}
			
		}
		return Optional.empty();
	}
	
}
