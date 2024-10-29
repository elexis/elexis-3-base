package ch.elexis.importer.aeskulap.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.IUserBuilder;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IXidService;
import ch.elexis.core.types.Gender;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.elexis.importer.aeskulap.core.test.AllTests;

public class AeskulapImporterTest {

	private static IAeskulapImporter importer;

	@BeforeClass
	public static void beforeClass() {
		importer = OsgiServiceUtil.getService(IAeskulapImporter.class).orElse(null);
		assertNotNull(importer);

		IPerson _mandator = new IContactBuilder.PersonBuilder(CoreModelServiceHolder.get(), "mandator1", "Anton",
				LocalDate.now().minusYears(50), Gender.MALE).mandator().buildAndSave();
		IMandator mandator = CoreModelServiceHolder.get().load(_mandator.getId(), IMandator.class).get();
		ContextServiceHolder.get().setActiveMandator(mandator);

		IPerson mandatorPerson = CoreModelServiceHolder.get().load(mandator.getId(), IPerson.class).get();
		IUser user = new IUserBuilder(CoreModelServiceHolder.get(), "user_at", mandatorPerson).buildAndSave();

		ContextServiceHolder.get().setActiveUser(user);
	}

	@Test
	public void testRun() {
		List<IAeskulapImportFile> files = importer.setImportDirectory(AllTests.getTestDirectory());
		assertFalse(files.isEmpty());
		SubMonitor subMonitor = SubMonitor.convert(new NullProgressMonitor(), files.size());
		List<IAeskulapImportFile> imported = importer.importFiles(files, false, subMonitor);
		assertTrue(imported.isEmpty());

		IXidService xidService = OsgiServiceUtil.getService(IXidService.class).orElse(null);
		assertNotNull(xidService);
		Optional<IPatient> patient = xidService.findObject(IAeskulapImporter.XID_IMPORT_PATIENT, "197", IPatient.class);
		assertTrue(patient.isPresent());
		IPatient importedPatient = patient.get();
		assertEquals("Test", importedPatient.getLastName());

		List<ILabResult> importedLabResults = CoreModelServiceHolder.get().getQuery(ILabResult.class)
				.and(ModelPackage.Literals.ILAB_RESULT__PATIENT, COMPARATOR.EQUALS, importedPatient).execute();
		assertFalse(importedLabResults.isEmpty());

		IDocumentStore omnivoreDocumentStore = OsgiServiceUtil
				.getService(IDocumentStore.class, "(storeid=ch.elexis.data.store.omnivore)").orElse(null);
		assertNotNull(omnivoreDocumentStore);
		List<IDocument> importedDocuments = omnivoreDocumentStore.getDocuments(importedPatient.getId(), null, null,
				null);
		assertFalse(importedDocuments.isEmpty());
	}
}
