package ch.elexis.icpc.fire.model;

import java.util.List;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;

import ch.elexis.core.model.ILabItem;
import ch.elexis.core.types.Gender;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.data.Konsultation;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.icpc.fire.model.TConsultation.Labors;
import ch.rgw.tools.TimeTool;

public class LaborsBuilder {

	private Konsultation consultation;
	private FireConfig config;
	
	public LaborsBuilder(FireConfig config){
		this.config = config;
	}
	
	public LaborsBuilder consultation(Konsultation consultation){
		this.consultation = consultation;
		return this;
	}
	
	public Optional<Labors> build() throws DatatypeConfigurationException{
		Patient patient = consultation.getFall().getPatient();
		TimeTool ttDate = new TimeTool(consultation.getDatum());
		String dayString = ttDate.toString(TimeTool.DATE_COMPACT);
		Query<LabResult> query = new Query<LabResult>(LabResult.class);
		query.add(LabResult.PATIENT_ID, Query.EQUALS, patient.getId());
		query.startGroup();
		query.add(LabResult.DATE, Query.EQUALS, dayString);
		query.or();
		query.add(LabResult.OBSERVATIONTIME, Query.LIKE, dayString + "%");
		query.endGroup();
		
		List<LabResult> results = query.execute();
		if (results != null && !results.isEmpty()) {
			Labors labors = config.getFactory().createTConsultationLabors();
			for (LabResult labResult : results) {
				ILabItem labItem = labResult.getItem();
				LabItemTyp labItemTyp = labItem.getTyp();
				if (labItemTyp == LabItemTyp.NUMERIC || labItemTyp == LabItemTyp.ABSOLUTE
					|| labItemTyp == LabItemTyp.TEXT) {
					TLabor tLabor = config.getFactory().createTLabor();
					tLabor.setAnalyse(labItem.getName());
					tLabor.setAnalyseKurz(labItem.getKuerzel());
					
					String ref = getRefString(labResult, patient.getGender());
					if (ref != null) {
						String[] rx = ref.split("\\s*\\-\\s*");
						if (rx.length > 1) {
							Float min = toFloat(rx[0]);
							Float max = toFloat(rx[1]);
							if (min != null) {
								tLabor.setMin(min);
							}
							if (max != null) {
								tLabor.setMax(max);
							}
						}
					}
					
					tLabor.setEinheit(labResult.getUnit());
					tLabor.setWert(labResult.getResult());
					
					tLabor.setDate(XmlUtil.getXmlGregorianCalendar(getLabResultTime(labResult)));
					labors.getLabor().add(tLabor);
				}
			}
			if (!labors.getLabor().isEmpty()) {
				return Optional.of(labors);
			}
			
		}
		
		return Optional.empty();
	}
	
	@SuppressWarnings("deprecation")
	private TimeTool getLabResultTime(LabResult labResult){
		TimeTool time = labResult.getObservationTime();
		if (time == null) {
			time = new TimeTool(labResult.getDate());
		}
		return time;
	}
	
	private Float toFloat(String string){
		string = string.trim().replaceAll(",", ".");
		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException e) {
			// ignore
		}
		return null;
	}
	
	private String getRefString(LabResult labResult, Gender gender) {
		if (gender == Gender.FEMALE) {
			return labResult.getRefFemale();
		} else if(gender == Gender.MALE) {
			return labResult.getRefMale();
		}
		return null;
	}
}
