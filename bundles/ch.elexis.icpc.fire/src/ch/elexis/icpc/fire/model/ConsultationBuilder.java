package ch.elexis.icpc.fire.model;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.LoggerFactory;

import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.elexis.icpc.fire.handlers.ExportFireHandler;
import ch.elexis.icpc.fire.model.TConsultation.Diagnoses;
import ch.elexis.icpc.fire.model.TConsultation.Labors;
import ch.elexis.icpc.fire.model.TConsultation.Medis;
import ch.rgw.tools.TimeTool;

public class ConsultationBuilder {

	private Konsultation consultation;

	private VitalSignsBuilder vitalsignsBuilder;

	private DiagnosesBuilder diagnosesBuilder;

	private LaborsBuilder laborsBuilder;

	private MedisBuilder medisBuilder;

	private FireConfig config;

	public ConsultationBuilder(FireConfig config) {
		this.config = config;
		vitalsignsBuilder = new VitalSignsBuilder(config);
		diagnosesBuilder = new DiagnosesBuilder(config);
		laborsBuilder = new LaborsBuilder(config);
		medisBuilder = new MedisBuilder(config);
	}

	public ConsultationBuilder consultation(Konsultation consultation) {
		this.consultation = consultation;
		return this;
	}

	public Optional<TConsultation> build(Map<String, Set<TMedi>> unreferencedStopMedisPerPatient)
			throws DatatypeConfigurationException {
		if (consultation != null) {
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
			Optional<Medis> medis = medisBuilder.consultation(consultation).build(unreferencedStopMedisPerPatient);
			medis.ifPresent(m -> ret.setMedis(m));

			return Optional.of(ret);
		}
		return Optional.empty();
	}

	/**
	 * Consultations that where already exported, are not handled anymore - due to
	 * this medications that were open at this time, and stopped in between are not
	 * covered. We have to pick them up here
	 *
	 * @param config
	 * @return
	 *
	 * @throws DatatypeConfigurationException
	 */
	public static Map<String, Set<TMedi>> initializeUnreferencedStopMedisPerPatient(FireConfig config)
			throws DatatypeConfigurationException {
		Map<String, Set<TMedi>> unreferencedStopMedisPerPatient = new HashMap<>();

		Query<Prescription> qre = new Query<>(Prescription.class);
		qre.add(Prescription.FLD_DATE_UNTIL, Query.GREATER_OR_EQUAL,
				ExportFireHandler.getTtFrom().toString(TimeTool.DATE_COMPACT) + "%");
		qre.add(Prescription.FLD_DATE_UNTIL, Query.LESS_OR_EQUAL,
				ExportFireHandler.getTtTo().toString(TimeTool.DATE_COMPACT) + "%");
		List<Prescription> execute = qre.execute();

		for (Prescription prescription : execute) {
			TMedi tMedi = MedisBuilder.createTMedi(prescription, config);

			String patientId = prescription.get(Prescription.FLD_PATIENT_ID);
			Set<TMedi> unreferencedStoppedMedis = unreferencedStopMedisPerPatient.get(patientId);
			if (unreferencedStoppedMedis == null) {
				unreferencedStoppedMedis = new HashSet<>();
				unreferencedStopMedisPerPatient.put(patientId, unreferencedStoppedMedis);
			}
			unreferencedStoppedMedis.add(tMedi);
		}
		return unreferencedStopMedisPerPatient;
	}

	public void handleUnreferencedStopMedisPerPatient(FireConfig fireConfig, Report report,
			Map<String, Set<TMedi>> unreferencedStopMedisPerPatient) {
		Set<Entry<String, Set<TMedi>>> entrySet = unreferencedStopMedisPerPatient.entrySet();
		for (Iterator<Entry<String, Set<TMedi>>> iterator = entrySet.iterator(); iterator.hasNext();) {

			Entry<String, Set<TMedi>> entryByPatientId = (Entry<String, Set<TMedi>>) iterator.next();
			String patientId = entryByPatientId.getKey();
			Set<TMedi> unreferencedStoppedMedis = entryByPatientId.getValue();
			if (!unreferencedStoppedMedis.isEmpty()) {

				Map<String, Set<TMedi>> groupByDate = groupUnreferencedStopMedisByDate(unreferencedStoppedMedis);
				Set<Entry<String, Set<TMedi>>> entrySetGroupByDate = groupByDate.entrySet();
				for (Entry<String, Set<TMedi>> entryForPatientByDate : entrySetGroupByDate) {

					TConsultation pseudoTConsultation = fireConfig.getFactory().createTConsultation();
					BigInteger patId = fireConfig.getPatId(Patient.load(patientId));
					pseudoTConsultation.setPatId(patId);
					pseudoTConsultation.setConsType("7");
					try {
						pseudoTConsultation
								.setDate(XmlUtil.getXmlGregorianCalendar(new TimeTool(entryForPatientByDate.getKey())));
					} catch (DatatypeConfigurationException e) {
						LoggerFactory.getLogger(ConsultationBuilder.class).warn("date error", e);
					}
					Medis medis = new Medis();
					Set<TMedi> entries = entryForPatientByDate.getValue();
					Konsultation identifyingKons = entries.stream().filter(e -> e.getConsultation() != null).findFirst()
							.map(e -> e.getConsultation()).orElse(null);
					if (identifyingKons != null && identifyingKons.getMandant() != null) {
						BigInteger docId = config.getDocId(identifyingKons.getMandant());
						pseudoTConsultation.setDocId(docId);
					}
					medis.getMedi().addAll(entries);
					pseudoTConsultation.setMedis(medis);

					if (report.getConsultations() == null) {
						report.setConsultations(fireConfig.getFactory().createReportConsultations());
					}

					report.getConsultations().getConsultation().add(pseudoTConsultation);
				}

			}
			iterator.remove();
		}

		assert (unreferencedStopMedisPerPatient.isEmpty());
	}

	private Map<String, Set<TMedi>> groupUnreferencedStopMedisByDate(Set<TMedi> unreferencedStoppedMedis) {
		Map<String, Set<TMedi>> group = new HashMap<>();
		for (TMedi tMedi : unreferencedStoppedMedis) {
			String date = "" + tMedi.getEndDate().getYear() + String.format("%02d", tMedi.getEndDate().getMonth())
					+ String.format("%02d", tMedi.getEndDate().getDay());
			Set<TMedi> dateSet = group.get(date);
			if (dateSet == null) {
				dateSet = new HashSet<>();
				group.put(date, dateSet);
			}
			dateSet.add(tMedi);
		}
		return group;
	}

}
