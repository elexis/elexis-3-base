package ch.elexis.icpc.fire.model;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.ISticker;
import ch.elexis.core.types.Gender;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Sticker;
import ch.elexis.icpc.fire.ui.Preferences;
import ch.rgw.tools.TimeTool;

public class ReportBuilder {
	
	private Report report;
	
	private Map<BigInteger, TPatient> patients;
	
	private Map<BigInteger, TDoctor> doctors;
	
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
	
	public void addKonsultation(BigInteger patId, BigInteger docId, Konsultation konsultation)
		throws DatatypeConfigurationException{
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
			
			TStatus tStatus = createPatientTStatus(patient);
			tPatient.setStatus(tStatus);
			
			report.getPatients().getPatient().add(tPatient);
			patients.put(patId, tPatient);
		}
		
		return patId;
	}
	
	private TStatus createPatientTStatus(Patient patient){
		String insurer = null;
		Boolean mc = null;
		Query<Fall> qbe = new Query<>(Fall.class, Fall.PATIENT_ID, patient.getId());
		qbe.add(Fall.FLD_XGESETZ, Query.EQUALS, "KVG");
		qbe.orderBy(true, Fall.FLD_DATUM_VON);
		List<Fall> qre = qbe.execute();
		for (Fall fall : qre) {
			if (fall.isOpen()) {
				String kostentraeger = (String) fall.getInfoElement("Kostenträger");
				if(kostentraeger == null) {
					kostentraeger = fall.get(Fall.FLD_KOSTENTRAEGER);
				}
				if (kostentraeger != null && !kostentraeger.equals(patient.getId())) {
					Kontakt costBearer = Kontakt.load(kostentraeger);
					if (costBearer.isValid() && costBearer.istOrganisation()) {
						insurer = costBearer.getLabel();
						break;
					}
				}
			}
		}
		List<ISticker> stickers = patient.getStickers();
		for (ISticker iSticker : stickers) {
			String stickerId = ((Sticker) iSticker).getId();
			if (CoreHub.globalCfg.get(Preferences.CFG_BASE_IS_HAM_STICKER + stickerId, false)) {
				mc = true;
				break;
			}
		}
		if (insurer != null || mc != null) {
			TStatus tStatus = fireConfig.getFactory().createTStatus();
			tStatus.setInsurer(insurer);
			tStatus.setMc(mc);
			return tStatus;
		}
		return null;
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
			tDoctor.setFirstName(mandant.getVorname());
			tDoctor.setLastName(mandant.getName());
			
			try {
				tDoctor.setGeburtstag(
					XmlUtil.getXmlGregorianCalendar(new TimeTool(mandant.getGeburtsdatum())));
			} catch (DatatypeConfigurationException e) {
				LoggerFactory.getLogger(ReportBuilder.class).warn("date error", e);
			}
			
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
	
	/**
	 * handle finishing tasks
	 */
	public void finish(){
		consultationBuilder.handleUnreferencedStopMedisPerPatient(fireConfig, report);
	}
}
