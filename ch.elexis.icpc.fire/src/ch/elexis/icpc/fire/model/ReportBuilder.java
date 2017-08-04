package ch.elexis.icpc.fire.model;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.LoggerFactory;

import ch.elexis.core.types.Gender;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class ReportBuilder {
	
	private Report report;
	
	private HashMap<BigInteger, TPatient> patients;
	
	private HashMap<BigInteger, TDoctor> doctors;
	
	private ConsultationBuilder consultationBuilder;
	
	private FireConfig fireConfig;
	
	public ReportBuilder() throws DatatypeConfigurationException{
		patients = new HashMap<>();
		doctors = new HashMap<>();
		fireConfig = new FireConfig();
		if (fireConfig.isValid()) {
			consultationBuilder = new ConsultationBuilder(fireConfig);
		}
		report = fireConfig.getFactory().createReport();
		report.setExportDate(XmlUtil.getXmlGregorianCalendar(new TimeTool()));
	}
	
	public void addKonsultation(BigInteger patId, BigInteger docId,
		Konsultation konsultation) throws DatatypeConfigurationException{
		if (fireConfig.isValid()) {
			if (report.getConsultations() == null) {
				report.setConsultations(fireConfig.getFactory().createReportConsultations());
			}
			
			Optional<TConsultation> consultation =
				consultationBuilder.consultation(konsultation).build();
			consultation.ifPresent(c -> report.getConsultations().getConsultation().add(c));
			consultation.ifPresent(c -> c.setDocId(docId));
			consultation.ifPresent(c -> c.setPatId(patId));
			consultation.ifPresent(c -> {
				try {
					c.setDate(
						XmlUtil.getXmlGregorianCalendar(new TimeTool(konsultation.getDatum())));
				} catch (DatatypeConfigurationException e) {
					LoggerFactory.getLogger(ReportBuilder.class).warn("date error", e);
				}
			});
		}
	}
	
	public BigInteger addPatient(Patient patient){
		BigInteger patId = fireConfig.getPatId(patient);
		// only add a new patient model object if no already in the list
		if (patients.get(patId) == null) {
			if (report.getPatients() == null) {
				report.setPatients(fireConfig.getFactory().createReportPatients());
			}
			
			TPatient tPatient = fireConfig.getFactory().createTPatient();
			tPatient.setId(patId);
			Gender gender = patient.getGender();
			if (gender == Gender.MALE) {
				tPatient.setGender(true);
			} else if (gender == Gender.FEMALE) {
				tPatient.setGender(false);
			}
			String dateOfBirth = patient.getGeburtsdatum();
			if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
				TimeTool dob = new TimeTool(dateOfBirth);
				tPatient.setBirthYear(dob.get(TimeTool.YEAR));
			}
			report.getPatients().getPatient().add(tPatient);
			patients.put(patId, tPatient);
		}
		
		return patId;
	}
	
	public BigInteger addMandant(Mandant mandant){
		BigInteger docId = fireConfig.getDocId(mandant);
		// only add a new doctor model object if no already in the list
		if (doctors.get(docId) == null) {
			if (report.getDoctors() == null) {
				report.setDoctors(fireConfig.getFactory().createReportDoctors());
			}
			
			TDoctor tDoctor = fireConfig.getFactory().createTDoctor();
			tDoctor.setId(docId);
			tDoctor.setSystem("Elexis");
			
			report.getDoctors().getDoctor().add(tDoctor);
			doctors.put(docId, tDoctor);
		}
		return docId;
	}
	
	public boolean isValidConfig(){
		return fireConfig.isValid();
	}
	
	public Optional<Report> build(){
		return Optional.ofNullable(report);
	}
}
