package ch.elexis.importer.aeskulap.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.IUserBuilder;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IXidService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.Country;
import ch.elexis.core.types.Gender;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile.Type;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.elexis.importer.aeskulap.core.test.AllTests;
import ch.elexis.omnivore.model.IDocumentHandle;

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

		Optional<IOrganization> existingGuarantor = XidServiceHolder.get().findObject(XidConstants.DOMAIN_EAN,
				"7601003002775", IOrganization.class);
		if (existingGuarantor.isEmpty()) {
			IOrganization guarantor = new IContactBuilder.OrganizationBuilder(CoreModelServiceHolder.get(),
					"Helsana Versicherungen AG (inkl. Avanex)").build();
			guarantor.setCity("Zürich");
			guarantor.setZip("8081");
			guarantor.setCountry(Country.CH);
			CoreModelServiceHolder.get().save(guarantor);
			XidServiceHolder.get().addXid(guarantor, XidConstants.DOMAIN_EAN, "7601003002775", true);

			// test 2 contacts with same ean
			guarantor = new IContactBuilder.OrganizationBuilder(CoreModelServiceHolder.get(), "Helsana Versicherungen")
					.build();
			guarantor.setCity("Zürich");
			guarantor.setZip("8081");
			guarantor.setCountry(Country.CH);
			CoreModelServiceHolder.get().save(guarantor);
			XidServiceHolder.get().addXid(guarantor, XidConstants.DOMAIN_EAN, "7601003002775", true);
		}
	}

	@After
	public void afterTest() {
		for (IPatient existingPatient : CoreModelServiceHolder.get().getQuery(IPatient.class).execute()) {
			IXid xid = existingPatient.getXid(IAeskulapImporter.XID_IMPORT_PATIENT);
			if (xid != null) {
				CoreModelServiceHolder.get().remove(xid);
			}
			CoreModelServiceHolder.get().remove(existingPatient);
		}
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

		assertFalse(patient.get().getCoverages().isEmpty());
		assertNotNull(patient.get().getCoverages().get(0).getGuarantor());
	}

	@Test
	public void removePatientDuplicates() {
		// create existing patients
		IPatient existingMatchNoAhv = new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), "Vanessa",
				"Test", LocalDate.of(1976, 3, 7), Gender.FEMALE).buildAndSave();

		IPatient existingMatchAhv = new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), "Martina",
				"Test Doppelname",
				LocalDate.of(1980, 8, 29), Gender.FEMALE).buildAndSave();
		existingMatchAhv.addXid(XidConstants.DOMAIN_AHV, "7561234567897", true);

		IPatient existingMatchWithData = new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), "Nathalie",
				"Test", LocalDate.of(1984, 3, 28), Gender.FEMALE).buildAndSave();
		// run import
		List<IAeskulapImportFile> files = importer.setImportDirectory(AllTests.getTestDirectory());
		assertFalse(files.isEmpty());
		// do not import coverage, as patients with coverage are not removed
		files = files.stream().filter(f -> f.getType() != Type.COVERAGE).toList();
		SubMonitor subMonitor = SubMonitor.convert(new NullProgressMonitor(), files.size());
		List<IAeskulapImportFile> imported = importer.importFiles(files, false, subMonitor);
		assertTrue(imported.isEmpty());
		assertEquals(2, queryPatients("Vanessa", "Test", null, false).size());
		assertEquals(2, queryPatients(null, null, "7561234567897", false).size());
		assertEquals(2, queryPatients("Nathalie", "Test", null, false).size());

		IPatient importedWithData = queryPatients("Nathalie", "Test", null, false).stream()
				.filter(p -> !getDocuments(p).isEmpty()).findFirst().get();
		int documentsSize = getDocuments(importedWithData).size();
		assertTrue(documentsSize > 0);

		// run remove duplicates
		importer.removePatientDuplicates(new NullProgressMonitor());

		assertEquals(1, queryPatients("Vanessa", "Test", null, false).size());
		assertEquals(1, queryPatients(null, null, "7561234567897", false).size());
		assertEquals(1, queryPatients("Nathalie", "Test", null, false).size());

		assertEquals(documentsSize, getDocuments(existingMatchWithData).size());
	}

	private IModelService omnivoreModelService;

	private List<IDocumentHandle> getDocuments(IPatient patient) {
		if (omnivoreModelService == null) {
			omnivoreModelService = OsgiServiceUtil.getService(IModelService.class,
					"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)").get();
		}
		IQuery<IDocumentHandle> documentQuery = omnivoreModelService.getQuery(IDocumentHandle.class);
		documentQuery.and("kontakt", COMPARATOR.EQUALS, patient);
		return documentQuery.execute();
	}

	private List<IPatient> queryPatients(String firstname, String lastname, String ahv, boolean includedeleted) {
		if (StringUtils.isNotBlank(ahv)) {
			List<IPatient> found = XidServiceHolder.get().findObjects(XidConstants.DOMAIN_AHV, ahv, IPatient.class);
			if (!includedeleted) {
				found = found.stream().filter(p -> !p.isDeleted()).toList();
			}
			return found;
		} else if (StringUtils.isNotBlank(firstname) && StringUtils.isNotBlank(lastname)) {
			IQuery<IPatient> query = CoreModelServiceHolder.get().getQuery(IPatient.class, includedeleted);
			query.and(ModelPackage.Literals.IPERSON__FIRST_NAME, COMPARATOR.EQUALS, firstname);
			query.and(ModelPackage.Literals.IPERSON__LAST_NAME, COMPARATOR.EQUALS, lastname);
			return query.execute();
		}
		return Collections.emptyList();
	}
}
