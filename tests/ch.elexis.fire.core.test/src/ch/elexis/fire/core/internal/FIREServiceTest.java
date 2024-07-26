package ch.elexis.fire.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ILabResultBuilder;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.types.Gender;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.fire.core.IFIREService;
import ch.elexis.fire.core.test.AllPluginTests;
import ch.rgw.tools.VersionedResource;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FIREServiceTest {

	private static IFIREService fireService;
	
	private static File initialExportFile;

	private static Bundle initialExport;

	private static Bundle incrementalExport;

	@BeforeClass
	public static void beforeClass() {
		fireService = OsgiServiceUtil.getService(IFIREService.class).get();
	}

	@Test
	public void a_initialExport() {
		List<File> files = fireService.initialExport(new NullProgressMonitor());
		assertNotNull(files);

		initialExportFile = files.get(0);
		initialExport = fireService.readBundle(files.get(0));

		assertNotNull(initialExport);
		
		String bundleJson = ModelUtil.getFhirJson(initialExport);
		assertNotNull(bundleJson);
		System.out.println(bundleJson);

		assertTrue(fireService.getInitialTimestamp() > 0);
	}

	@Test
	public void b_getFIREPatientId() {
		String firePatientId = ((FIREService) fireService)
				.getFIREPatientId(TestDatabaseInitializer.getPatient().getId());
		assertNotNull(firePatientId);
		String firePatientIdEqual = ((FIREService) fireService)
				.getFIREPatientId(TestDatabaseInitializer.getPatient().getId());
		assertEquals(firePatientId, firePatientIdEqual);
		assertNotNull(((FIREService) fireService).getPatientBundle(firePatientIdEqual, initialExport));
	}

	@Test
	public void c_incrementalExport() {
		// create patient
		IPatient patient = new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), "Test", "UpdatePatient",
				LocalDate.of(1990, 1, 1), Gender.FEMALE).build();
		patient.setPhone1("+01555124");
		patient.setMobile("+01444124");
		patient.setCity("City");
		patient.setZip("123");
		patient.setStreet("Street 2");
		patient.setDiagnosen("Test Diagnose 3\nTest Diagnose 4");
		CoreModelServiceHolder.get().save(patient);

		TestDatabaseInitializer.addAHVNumber(patient, 2);

		IQuery<ILabItem> query = CoreModelServiceHolder.get().getQuery(ILabItem.class);
		query.and(ModelPackage.Literals.ILAB_ITEM__CODE, COMPARATOR.EQUALS, "TEST NUMERIC");
		List<ILabItem> items = query.execute();
		assertFalse(items.isEmpty());
		
		ILabResult labResult = new ILabResultBuilder(CoreModelServiceHolder.get(), items.get(0), patient)
				.build();
		labResult.setObservationTime(LocalDateTime.of(2020, Month.FEBRUARY, 28, 12, 59, 23));
		labResult.setResult("121/79");
		labResult.setUnit("Bloodpressure");
		CoreModelServiceHolder.get().save(labResult);

		// update behandlung
		VersionedResource vr = TestDatabaseInitializer.getBehandlung().getVersionedEntry();
		vr.update("Test consultation\n incremental update done by user", "user");
		TestDatabaseInitializer.getBehandlung().setVersionedEntry(vr);
		CoreModelServiceHolder.get().save(TestDatabaseInitializer.getBehandlung());
		
		List<File> files = fireService.incrementalExport(fireService.getInitialTimestamp(), new NullProgressMonitor());

		incrementalExport = fireService.readBundle(files.get(0));

		assertNotNull(incrementalExport);

		String bundleJson = ModelUtil.getFhirJson(incrementalExport);
		assertNotNull(bundleJson);
		System.out.println(bundleJson);

		// test old patient
		String firePatientId = ((FIREService) fireService)
				.getFIREPatientId(TestDatabaseInitializer.getPatient().getId());
		Bundle patientBundle = ((FIREService) fireService).getPatientBundle(firePatientId, incrementalExport);
		assertNotNull(patientBundle);
		List<Patient> patients = AllPluginTests.getResourcesFromBundle(patientBundle, Patient.class);
		assertTrue(patients.isEmpty());
		List<Encounter> encounters = AllPluginTests.getResourcesFromBundle(patientBundle, Encounter.class);
		assertFalse(encounters.isEmpty());

		assertTrue(fireService.getIncrementalTimestamp() > 0);

		// test new patient
		firePatientId = ((FIREService) fireService).getFIREPatientId(patient.getId());
		patientBundle = ((FIREService) fireService).getPatientBundle(firePatientId, incrementalExport);
		patients = AllPluginTests.getResourcesFromBundle(patientBundle, Patient.class);
		assertFalse(patients.isEmpty());
		List<Observation> observations = AllPluginTests.getResourcesFromBundle(patientBundle, Observation.class);
		assertFalse(observations.isEmpty());
	}

//	@Test
//	public void d_uploadBundle() {
//		assertNotNull(initialExport);
//		assertTrue(fireService.uploadBundle(initialExport));
//	}
}
