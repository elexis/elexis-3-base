package ch.elexis.icpc.fire.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;

import ch.elexis.data.Artikel;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.elexis.icpc.fire.model.TConsultation.Medis;
import ch.rgw.tools.TimeTool;

public class MedisBuilder {

	private Konsultation consultation;
	private FireConfig config;
	
	public MedisBuilder(FireConfig config){
		this.config = config;
	}
	
	public MedisBuilder consultation(Konsultation consultation){
		this.consultation = consultation;
		return this;
	}
	
	public Optional<Medis> build() throws DatatypeConfigurationException{
		Patient patient = consultation.getFall().getPatient();
		Query<Prescription> query = new Query<Prescription>(Prescription.class);
		query.add(Prescription.FLD_PATIENT_ID, Query.EQUALS, patient.getId());
		TimeTool consTime = new TimeTool(consultation.getDatum());
		query.add(Prescription.FLD_DATE_FROM, Query.LIKE,
			consTime.toString(TimeTool.DATE_COMPACT) + "%");
		List<Prescription> prescriptions = query.execute();
		if (prescriptions != null && !prescriptions.isEmpty()) {
			Medis medis = config.getFactory().createTConsultationMedis();
			for (Prescription prescription : prescriptions) {
				Artikel articel = prescription.getArtikel();
				if (articel != null) {
					TMedi tMedi = config.getFactory().createTMedi();
					String beginDateString = prescription.getBeginDate();
					if (beginDateString != null && !beginDateString.isEmpty()) {
						tMedi.setBeginDate(
							XmlUtil.getXmlGregorianCalendar(new TimeTool(beginDateString)));
					}
					String stopDateString = prescription.getEndDate();
					if (stopDateString != null && !stopDateString.isEmpty()) {
						tMedi.setBeginDate(
							XmlUtil.getXmlGregorianCalendar(new TimeTool(stopDateString)));
					}
					
					String atcCode = articel.getATC_code();
					if (atcCode != null && !atcCode.isEmpty()) {
						tMedi.setAtc(atcCode);
					}
					String pharmacode = articel.getPharmaCode();
					if (pharmacode != null && !pharmacode.isEmpty()) {
						try {
							long numericPharmacode = Long.valueOf(pharmacode);
							tMedi.setPharmacode(numericPharmacode);
						} catch (NumberFormatException e) {
							//ignore and skip
						}
					}
					
					ArrayList<Float> floatDosis =
						Prescription.getDoseAsFloats(prescription.getDosis());
					if (floatDosis != null) {
						int size = floatDosis.size();
						if (size > 0) {
							tMedi.setDosisMo(floatDosis.get(0));
						}
						if (size > 1) {
							tMedi.setDosisMi(floatDosis.get(1));
						}
						if (size > 2) {
							tMedi.setDosisAb(floatDosis.get(2));
						}
						if (size > 3) {
							tMedi.setDosisNa(floatDosis.get(3));
						}
					}
					
					medis.getMedi().add(tMedi);
				}
			}
			return Optional.of(medis);
		}
		return Optional.empty();
	}
	
}
