package ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.utils.CoreUtil;
import ch.oaat_otma.PatientCase;
import ch.oaat_otma.Service;
import ch.oaat_otma.Side;
import ch.oaat_otma.casemaster.Casemaster;
import ch.oaat_otma.casemaster.CasemasterResult;
import ch.oaat_otma.casemaster.Patient;
import ch.oaat_otma.casemaster.Session;
import ch.oaat_otma.grouper.ClassificationSystemReader;
import ch.oaat_otma.grouper.PCSError;
import ch.oaat_otma.grouper.PatientClassificationSystem;
import ch.oaat_otma.grouper.parser.ParseError;
import ch.oaat_otma.mapper.Mapper;
import ch.oaat_otma.mapper.MapperLogEntry;
import ch.oaat_otma.mapper.MapperLogEntry.MapperLogEntryLevel;
import ch.oaat_otma.mapper.MapperResult;
import ch.oaat_otma.mapper.ServiceCatalog;
import ch.oaat_otma.mapper.TardocCatalog;

public class TestTarifMatcher {

	private static final String CAP_ASSIGNMENT_FILENAME = "system_ambP_11c_cap_assignment.json";

	private static final String TARDOC_FILENAME = "tardoc_TARDOC_1.4c_de.json";
	private static final String LKAAT_FILENAME = "lkaat_1.0c.json";

	private static final String CLASS_LKAAT_FILENAME = "system_ambP_11c_lkaat.json";

	private Casemaster caseMaster;

	private PatientClassificationSystem patientClassificationSystemService;

	private Mapper mapper;

	public TestTarifMatcher() {
		initCasemaster();
		initClassificationSystem();
		initMapper();
	}

	public void run() {
		Session session = new Session(1, LocalDate.of(2026, 1, 1));
		session.addService(new Service("AA.00.0010", Side.NONE, 1, LocalDate.of(2026, 1, 1), 1));

		Patient patient = new Patient();
		patient.setBirthDate(LocalDate.of(2000, 1, 1));
		patient.setSex("W");
		patient.setSessions(Collections.singletonList(session));

		CasemasterResult casemasterResult = caseMaster.apply(patient);
		for (PatientCase patientCase : casemasterResult.patientCases) {
			patientClassificationSystemService.evaluate(patientCase);
			if ("NO.ambP".equals(patientCase.getGrouperResult().group)) {
				MapperResult mapperResult = mapper.map(patientCase);
				List<MapperLogEntry> noneInfoLog = new ArrayList<>(mapperResult.log.stream()
						.filter(l -> l.level != MapperLogEntryLevel.INFO && l.level != MapperLogEntryLevel.WARNING)
						.toList());
				if (!noneInfoLog.isEmpty()) {
					System.out.println("Unerwartet! LKAAT_VALIDATION_DUPLICATE?");
				}
			}
		}
	}

	private void initMapper() {
		TardocCatalog tardocCatalog = null;
		ServiceCatalog serviceCatalog = null;

		File rootDir = CoreUtil.getWritableUserDir();
		File tarifmatcherdir = new File(rootDir, "tarifmatcher");
		if (!tarifmatcherdir.exists()) {
			tarifmatcherdir.mkdir();
		}
		File tardocFile = new File(tarifmatcherdir, TARDOC_FILENAME);
		try (OutputStream out = new FileOutputStream(new File(tarifmatcherdir, TARDOC_FILENAME))) {
			IOUtils.copy(MapperService.class.getResourceAsStream("/rsc/mapper/" + TARDOC_FILENAME), out);
			tardocCatalog = TardocCatalog.readCatalog(tardocFile);
		} catch (IOException | ParseError e) {
			LoggerFactory.getLogger(getClass()).error("Error initializing Mapper", e);
		}
		File lkaatFile = new File(tarifmatcherdir, LKAAT_FILENAME);
		try (OutputStream out = new FileOutputStream(new File(tarifmatcherdir, LKAAT_FILENAME))) {
			IOUtils.copy(MapperService.class.getResourceAsStream("/rsc/mapper/" + LKAAT_FILENAME), out);
			serviceCatalog = ServiceCatalog.readCatalog(lkaatFile);
		} catch (IOException | ParseError e) {
			LoggerFactory.getLogger(getClass()).error("Error initializing Mapper", e);
		}
		mapper = new Mapper(serviceCatalog, tardocCatalog);
	}

	private void initCasemaster() {
		File rootDir = CoreUtil.getWritableUserDir();
		File tarifmatcherdir = new File(rootDir, "tarifmatcher");
		if (!tarifmatcherdir.exists()) {
			tarifmatcherdir.mkdir();
		}
		File capAssignmentFile = new File(tarifmatcherdir, CAP_ASSIGNMENT_FILENAME);
		try (OutputStream out = new FileOutputStream(new File(tarifmatcherdir, CAP_ASSIGNMENT_FILENAME))) {
			IOUtils.copy(CasemasterService.class.getResourceAsStream("/rsc/casemaster/" + CAP_ASSIGNMENT_FILENAME),
					out);
			caseMaster = new Casemaster(capAssignmentFile);
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error initializing Casemaster", e);
		}
	}

	private void initClassificationSystem() {
		File rootDir = CoreUtil.getWritableUserDir();
		File tarifmatcherdir = new File(rootDir, "tarifmatcher");
		if (!tarifmatcherdir.exists()) {
			tarifmatcherdir.mkdir();
		}
		File lkaatFile = new File(tarifmatcherdir, CLASS_LKAAT_FILENAME);
		try (OutputStream out = new FileOutputStream(new File(tarifmatcherdir, CLASS_LKAAT_FILENAME))) {
			IOUtils.copy(PatientClassificationSystemService.class
					.getResourceAsStream("/rsc/grouper/" + CLASS_LKAAT_FILENAME), out);
			ClassificationSystemReader reader = new ClassificationSystemReader();
			patientClassificationSystemService = reader.readFromFile(lkaatFile);

			List<PCSError> errors = patientClassificationSystemService.check();
			assert (errors.size() == 0);
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error initializing Casemaster", e);
		}
	}
}
