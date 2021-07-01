package ch.elexis.base.ch.arzttarife.model.test;

import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.sql.DataSource;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.base.ch.arzttarife.model.tarmed.test.AllTarmedTests;
import ch.elexis.core.common.DBConnection;
import ch.elexis.core.common.DBConnection.DBType;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IUser;
import ch.elexis.core.services.IBillingService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IElexisDataSource;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IEncounterService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.utils.OsgiServiceUtil;

@RunWith(Suite.class)
@SuiteClasses({
	PhysioLeistungTest.class, ComplementaryLeistungTest.class, AllTarmedTests.class
})
public class AllTestsSuite {
	
	private static IElexisEntityManager entityManager;
	private static IModelService modelService;
	private static IModelService coreModelService;
	private static IBillingService billingService;
	private static IEncounterService encounterService;
	
	@BeforeClass
	public static void beforeClass() throws IOException, SQLException{
		if (!setInitialTarifs()) {
			// download and copy tarifs to rsc folder first !
			IReferenceDataImporter physioImporter =
				OsgiServiceUtil.getService(IReferenceDataImporter.class,
					"(" + IReferenceDataImporter.REFERENCEDATAID + "=physio)").get();
			IStatus result = physioImporter.performImport(new NullProgressMonitor(),
				AllTestsSuite.class.getResourceAsStream("/rsc/physiotarif2018_09_05.csv"), 180905);
			OsgiServiceUtil.ungetService(physioImporter);
			assertTrue(result.isOK());
			
			IReferenceDataImporter complementaryImporter =
				OsgiServiceUtil.getService(IReferenceDataImporter.class,
					"(" + IReferenceDataImporter.REFERENCEDATAID + "=complementary)").get();
			result =  complementaryImporter.performImport(new NullProgressMonitor(),
				AllTestsSuite.class.getResourceAsStream("/rsc/complementary_171229.csv"), 180206);
			assertTrue(result.isOK());
			OsgiServiceUtil.ungetService(complementaryImporter);
			
			IReferenceDataImporter tarmedImporter =
				OsgiServiceUtil.getService(IReferenceDataImporter.class,
					"(" + IReferenceDataImporter.REFERENCEDATAID + "=tarmed_34)").get();
			result =  tarmedImporter.performImport(new NullProgressMonitor(), AllTestsSuite.class
				.getResourceAsStream("/rsc/TARMED_Datenbank_01.08.00_BR_UVG_IVG_MVG.mdb"), 171019);
			assertTrue(result.isOK());
			OsgiServiceUtil.ungetService(tarmedImporter);
			
			tarmedImporter = OsgiServiceUtil.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=tarmed_kvg_34)").get();
			result = tarmedImporter.performImport(new NullProgressMonitor(), AllTestsSuite.class
				.getResourceAsStream("/rsc/TARMED__Datenbank_01.09.00_BR_KVG-27.12.2017.mdb"),
				171227);
			assertTrue(result.isOK());
			OsgiServiceUtil.ungetService(tarmedImporter);
			
			// dumpInitialTarifs();	
		}
		modelService = OsgiServiceUtil
			.getService(IModelService.class,
				"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.arzttarife.model)")
			.get();
		coreModelService = OsgiServiceUtil.getService(IModelService.class,
			"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)").get();
		billingService = OsgiServiceUtil.getService(IBillingService.class).get();
		entityManager = OsgiServiceUtil.getService(IElexisEntityManager.class).get();
		entityManager.getEntityManager(); // initialize the db		
		encounterService = OsgiServiceUtil.getService(IEncounterService.class).get();
		
		TestDatabaseInitializer tdb = new TestDatabaseInitializer(coreModelService, entityManager);
		tdb.initializeMandant();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
	}
	
	/**
	 * Create a {@link IBillingSystemFactor} for tests, <b>do not forget to remove it after test is
	 * finished</b>
	 * 
	 * @param billingSystem
	 * @param value
	 * @param from
	 * @return
	 */
	public static IBillingSystemFactor createBillingSystemFactor(String billingSystem, double value,
		LocalDate from){
		IBillingSystemFactor factor = coreModelService.create(IBillingSystemFactor.class);
		factor.setSystem(billingSystem);
		factor.setFactor(value);
		factor.setValidFrom(from);
		factor.setValidTo(LocalDate.of(9999, 12, 31));
		assertTrue(coreModelService.save(factor));
		return factor;
	}
	
	private static boolean setInitialTarifs() throws IOException{
		InputStream initialTarifsStream =
			AllTestsSuite.class.getResourceAsStream("/rsc/initialTarifs.zip");
		if (initialTarifsStream != null) {
			File testDBFolder =
				new File(System.getenv("HOME") + File.separator + "arzttarifetestdb");
			if (!testDBFolder.exists()) {
				testDBFolder.mkdir();
			}
			Path outDir = testDBFolder.toPath();
			byte[] buffer = new byte[20480];
			ZipInputStream zis = new ZipInputStream(initialTarifsStream);
			ZipEntry entry;
			// while there are entries I process them
			while ((entry = zis.getNextEntry()) != null) {
				Path filePath = outDir.resolve(entry.getName());
				
				try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
						BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {
					
					int len;
					while ((len = zis.read(buffer)) > 0) {
						bos.write(buffer, 0, len);
					}
				}
			}
			// reset the datasource
			IElexisDataSource elexisDataSource =
				OsgiServiceUtil.getService(IElexisDataSource.class).get();
			DBConnection dbConnection = new DBConnection();
			dbConnection.rdbmsType = DBType.H2;
			dbConnection.databaseName = "arzttarifemodel";
			dbConnection.username = "sa";
			dbConnection.password = "";
			dbConnection.connectionString =
				"jdbc:h2:" + outDir + File.separator + "arzttarifemodel" + ";AUTO_SERVER=TRUE";
			elexisDataSource.setDBConnection(dbConnection);
			
			return true;
		}
		return false;
	}
	
	/**
	 * Use this method to create a fresh dump of the db after tarif import.
	 */
	private static void dumpInitialTarifs(){
		DataSource dataSource = OsgiServiceUtil.getService(DataSource.class).get();
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			statement.execute(
				"BACKUP TO '" + System.getenv("HOME") + File.separator + "initialTarifs.zip'");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
		OsgiServiceUtil.ungetService(dataSource);
	}
	
	public static IModelService getModelService(){
		return modelService;
	}
	
	public static IModelService getCoreModelService(){
		return coreModelService;
	}
	
	public static IBillingService getBillingService(){
		return billingService;
	}

	public static IEncounterService getEncounterService(){
		return encounterService;
	}
}
