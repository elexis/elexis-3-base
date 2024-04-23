package ch.elexis.fire.core.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.elexis.core.findings.ICondition;
import ch.elexis.core.findings.ICondition.ConditionCategory;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.codes.CodingSystem;
import ch.elexis.core.findings.migration.IMigratorService;
import ch.elexis.core.findings.util.model.TransientLocalCoding;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.data.PersistentObject;
import ch.elexis.fire.core.FIREServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FIREServiceTest.class })
public class AllPluginTests {

	private static IFindingsService findingsService;
	private static IMigratorService migratorService;

	private static IModelService coreModelService;
	private static IConfigService configService;

	@BeforeClass
	public static void beforeClass() throws IOException, SQLException {

		coreModelService = OsgiServiceUtil
				.getService(IModelService.class, "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)").get();
		configService = OsgiServiceUtil.getService(IConfigService.class).get();
		findingsService = OsgiServiceUtil.getService(IFindingsService.class).get();
		migratorService = OsgiServiceUtil.getService(IMigratorService.class).get();

		DataSource dataSource = OsgiServiceUtil.getService(DataSource.class, "(id=default)").get();
		assertTrue(PersistentObject.connect(dataSource));

		TestDatabaseInitializer initializer = new TestDatabaseInitializer();

		initializer.initializePatient();
		initializer.initializeBehandlung();
		initializer.initializeLabResult();
		initializer.initializePrescription();
		initializer.initializeAUF();

		useStructuredDiagnosis(initializer);
	}

	private static void useStructuredDiagnosis(TestDatabaseInitializer initializer) {
		configService.set(IMigratorService.DIAGNOSE_SETTINGS_USE_STRUCTURED, true);
		for (IPatient patient : coreModelService.getQuery(IPatient.class).execute()) {
			migratorService.migratePatientsFindings(patient.getId(), ICondition.class, null);
		}
		
		ICondition codedCondition = findingsService.create(ICondition.class);
		codedCondition.setPatientId(initializer.getPatient().getId());
		codedCondition.setCategory(ConditionCategory.PROBLEMLISTITEM);
		codedCondition.setCoding(
				Collections.singletonList(
						new TransientLocalCoding(CodingSystem.ICD_DE_CODESYSTEM.getSystem(), "J06.9",
								"Akute Infektion der oberen Atemwege, nicht näher bezeichnet")));
		findingsService.saveFinding(codedCondition);
	}
}
