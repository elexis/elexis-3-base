package ch.elexis.tasks.integration.test.internal;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;

import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.builder.ILabItemBuilder;
import ch.elexis.core.model.builder.IUserBuilder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Gender;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.utils.OsgiServiceUtil;

public class Hl7ImporterTaskIntegrationTestUtil {
	
	public static IUser prepareEnvironment(){
		IPerson _mandator = new IContactBuilder.PersonBuilder(CoreModelServiceHolder.get(), "Elisa",
			"Mandatore", LocalDate.of(2000, 12, 1), Gender.FEMALE).mandator().buildAndSave();
		
		IMandator mandator =
			CoreModelServiceHolder.get().load(_mandator.getId(), IMandator.class).orElse(null);
		
		IUser testUser =
			new IUserBuilder(CoreModelServiceHolder.get(), "user_ctx", _mandator).buildAndSave();
		
		// the patient associated with the import file(s)
		IPatient patient = new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), "Hans",
			"Muster", LocalDate.of(2011, 1, 12), Gender.MALE).build();
		// the patient number referenced in the hl7 file
		patient.setPatientNr("5083");
		CoreModelServiceHolder.get().save(patient);
		
		ICoverage coverage = new ICoverageBuilder(CoreModelServiceHolder.get(), patient,
			"testLabel", "testReason", "KVG").buildAndSave();
		
		new IEncounterBuilder(CoreModelServiceHolder.get(), coverage, mandator).buildAndSave();
		
		return testUser;
	}
	
	public static ILaboratory configureLabAndLabItemBilling(){
		ILaboratory laboratory =
			new IContactBuilder.LaboratoryBuilder(CoreModelServiceHolder.get(), "myLab")
				.buildAndSave();
		laboratory.addXid(XidConstants.XID_KONTAKT_LAB_SENDING_FACILITY, "ABXMicrosEmi", true);
		
		// bill mapping WBC
		ILabItem item =
			new ILabItemBuilder(CoreModelServiceHolder.get(), "WBC", "Lekozyten", null, null, "G/l",
				LabItemTyp.NUMERIC, "ABX", 0).origin(laboratory, "WBC", true).buildAndSave();
		item.setBillingCode("1371.00");
		// is not imported - test missing
		CoreModelServiceHolder.get().save(item);
		// bill mapping rbc
		item = new ILabItemBuilder(CoreModelServiceHolder.get(), "RBC", "Erythrozyten", null, null,
			"T/l", LabItemTyp.NUMERIC, "ABX", 0).origin(laboratory, "RBC", true).buildAndSave();
		item.setBillingCode("1281.10");
		CoreModelServiceHolder.get().save(item);
		
		return laboratory;
	}
	
	public static void importEal2009(){
		// create EAL Leistungen
		Optional<IReferenceDataImporter> laborImporter =
			OsgiServiceUtil.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=analysenliste)");
		assertTrue(laborImporter.isPresent());
		IStatus success = laborImporter.get().performImport(new NullProgressMonitor(),
			Hl7ImporterTaskIntegrationTestUtil.class
				.getResourceAsStream("/rsc/Custom_EAL_TestOnly.xls"),
			190103);
		if (!success.isOK()) {
			throw new IllegalStateException("Could not import EAL");
		}
	}
	
}
