package ch.elexis.fire.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.hl7.fhir.r4.model.Bundle;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.types.Gender;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.fire.core.IFIREService;
import ch.rgw.tools.VersionedResource;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FIREServiceTest {

	private static IFIREService fireService;
	
	private static Bundle initialExport;

	private static Bundle incrementalExport;

	@BeforeClass
	public static void beforeClass() {
		fireService = OsgiServiceUtil.getService(IFIREService.class).get();
	}

	@Test
	public void a_initialExport() {
		initialExport = fireService.initialExport();

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

		// update behandlung
		VersionedResource vr = TestDatabaseInitializer.getBehandlung().getVersionedEntry();
		vr.update("Test consultation\n incremental update done by user", "user");
		TestDatabaseInitializer.getBehandlung().setVersionedEntry(vr);
		CoreModelServiceHolder.get().save(TestDatabaseInitializer.getBehandlung());
		
		incrementalExport = fireService.incrementalExport(fireService.getInitialTimestamp());

		assertNotNull(incrementalExport);

		String bundleJson = ModelUtil.getFhirJson(incrementalExport);
		assertNotNull(bundleJson);
		System.out.println(bundleJson);

		String firePatientId = ((FIREService) fireService)
				.getFIREPatientId(TestDatabaseInitializer.getPatient().getId());
		assertNotNull(((FIREService) fireService).getPatientBundle(firePatientId, incrementalExport));

		assertTrue(fireService.getIncrementalTimestamp() > 0);
	}
}
