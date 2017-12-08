package ch.elexis.icpc.fire.model;

import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;

import ch.elexis.data.Konsultation;
import ch.elexis.icpc.fire.model.TConsultation.Diagnoses;
import ch.elexis.icpc.fire.model.TConsultation.Labors;
import ch.elexis.icpc.fire.model.TConsultation.Medis;

public class ConsultationBuilder {

	private Konsultation consultation;
	
	private VitalSignsBuilder vitalsignsBuilder;
	
	private DiagnosesBuilder diagnosesBuilder;
	
	private LaborsBuilder laborsBuilder;
	
	private MedisBuilder medisBuilder;
	
	private FireConfig config;
	
	public ConsultationBuilder(FireConfig config){
		this.config = config;
		vitalsignsBuilder = new VitalSignsBuilder(config);
		diagnosesBuilder = new DiagnosesBuilder(config);
		laborsBuilder = new LaborsBuilder(config);
		medisBuilder = new MedisBuilder(config);
	}
	
	public ConsultationBuilder consultation(Konsultation consultation){
		this.consultation = consultation;
		return this;
	}
	
	public Optional<TConsultation> build() throws DatatypeConfigurationException{
		if(consultation != null) {
			TConsultation ret = config.getFactory().createTConsultation();
			// vital
			Optional<TVital> vital = vitalsignsBuilder.consultation(consultation).build();
			vital.ifPresent(v -> ret.setVital(v));
			// diagnoses
			Optional<Diagnoses> diagnoses = diagnosesBuilder.consultation(consultation).build();
			diagnoses.ifPresent(d -> ret.setDiagnoses(d));
			// labors
			Optional<Labors> labors = laborsBuilder.consultation(consultation).build();
			labors.ifPresent(l -> ret.setLabors(l));
			// medis
			Optional<Medis> medis = medisBuilder.consultation(consultation).build();
			medis.ifPresent(m -> ret.setMedis(m));
			
			return Optional.of(ret);
		}
		return Optional.empty();
	}
	
}
