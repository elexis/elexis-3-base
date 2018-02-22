package ch.elexis.icpc.fire.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.LoggerFactory;

import ch.elexis.core.model.prescription.EntryType;
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
	
	public Optional<Medis> build(Map<String, Set<TMedi>> unreferencedStopMedisPerPatient) throws DatatypeConfigurationException{
		Patient patient = consultation.getFall().getPatient();
		Query<Prescription> query = new Query<Prescription>(Prescription.class);
		query.add(Prescription.FLD_PATIENT_ID, Query.EQUALS, patient.getId());
		TimeTool consTime = new TimeTool(consultation.getDatum());
		query.add(Prescription.FLD_DATE_FROM, Query.LIKE,
			consTime.toString(TimeTool.DATE_COMPACT) + "%");
		
		List<Prescription> prescriptions = query.execute();
		Set<TMedi> unreferencedStoppedMedis = unreferencedStopMedisPerPatient.get(patient.getId());
		if(unreferencedStoppedMedis!=null) {
			for (Iterator<TMedi> iterator = unreferencedStoppedMedis.iterator(); iterator.hasNext();) {
				TMedi tMedi = (TMedi) iterator.next();
				if(tMedi.getEndDate().toString().startsWith(new TimeTool(consTime).toString(TimeTool.DATE_MYSQL))) {
					// if there exists a TMedi with stop date same as this consultation
					// add it again
					prescriptions.add(Prescription.load(tMedi.getId()));
					iterator.remove();
				}
			}
		}
		
		if (prescriptions != null && !prescriptions.isEmpty()) {
			Medis medis = config.getFactory().createTConsultationMedis();
			for (Prescription prescription : prescriptions) {
				Artikel article = prescription.getArtikel();
				if (article != null) {
					TMedi tMedi = config.getFactory().createTMedi();
					tMedi.setId(prescription.getId());
					String beginDateString = prescription.getBeginDate();
					if (beginDateString != null && !beginDateString.isEmpty()) {
						tMedi.setBeginDate(
							XmlUtil.getXmlGregorianCalendar(new TimeTool(beginDateString)));
					}
					String atcCode = article.getATC_code();
					if (atcCode != null && !atcCode.isEmpty()) {
						tMedi.setAtc(atcCode);
					}
					String pharmacode = article.getPharmaCode();
					if (pharmacode != null && !pharmacode.isEmpty()) {
						try {
							long numericPharmacode = Long.valueOf(pharmacode);
							tMedi.setPharmacode(numericPharmacode);
						} catch (NumberFormatException e) {
							//ignore and skip
						}
					}
					
					String gtin = article.getGTIN();
					if (gtin != null && !gtin.isEmpty()) {
						try {
							long gtinL = Long.valueOf(gtin);
							tMedi.setGTIN(BigInteger.valueOf(gtinL));
						} catch (NumberFormatException e) {
							LoggerFactory.getLogger(MedisBuilder.class)
								.warn("no numeric gtin found", e);
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
					
					String stopReason = prescription.getStopReason();
					if (stopReason != null && !stopReason.isEmpty()) {
						tMedi.setStopGrund((short) 99);
					}
					else {
						tMedi.setStopGrund((short) 0);
					}
					
					tMedi.setMediDauer(getType(prescription.getEntryType()));
					
					String stopDateString = prescription.getEndDate();
					if (stopDateString != null && !stopDateString.isEmpty()) {
						tMedi.setEndDate(
							XmlUtil.getXmlGregorianCalendar(new TimeTool(stopDateString)));
						if(!stopDateString.startsWith(consultation.getDatum())) {
							// this medication has to be re-referenced in another consultation
							// as the stopping of a medication has to be pointed out again
							if(unreferencedStoppedMedis==null) {
								unreferencedStoppedMedis = new HashSet<>();
								unreferencedStopMedisPerPatient.put(patient.getId(), unreferencedStoppedMedis);
							}
							unreferencedStoppedMedis.add(tMedi);
						}
					}
					
					medis.getMedi().add(tMedi);
				}
			}
			if (!medis.getMedi().isEmpty()) {
				return Optional.of(medis);
			}
			
		}
		return Optional.empty();
	}
	
	private String getType(EntryType entryType){
		switch (entryType) {
		case FIXED_MEDICATION:
			return "Fix";
		case RESERVE_MEDICATION:
			return "Reserve";
		case SYMPTOMATIC_MEDICATION:
			return "Symptom";
		default:
			return "";
		}
	}
	
}
