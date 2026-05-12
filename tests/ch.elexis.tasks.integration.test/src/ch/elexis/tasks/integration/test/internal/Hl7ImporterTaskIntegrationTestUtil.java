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
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Gender;
import ch.elexis.core.types.LabItemTyp;

public class Hl7ImporterTaskIntegrationTestUtil {

	private IPatient patient;
	private ICoverage coverage;
	private IMandator mandator;
	private IUser user;
	private ILaboratory laboratory;
	private ILabItem item;
	private ILabItem itemGPT;

	public void prepareEnvironment() {
		IPerson _mandator = new IContactBuilder.PersonBuilder(CoreModelServiceHolder.get(), "Elisa", "Mandatore",
				LocalDate.of(2000, 12, 1), Gender.FEMALE).mandator().buildAndSave();

		mandator = CoreModelServiceHolder.get().load(_mandator.getId(), IMandator.class).orElse(null);

		user = new IUserBuilder(CoreModelServiceHolder.get(), "user_ctx", _mandator).buildAndSave();

		// the patient associated with the import file(s)
		patient = new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), "Hans", "Muster",
				LocalDate.of(2011, 1, 12), Gender.MALE).build();
		// the patient number referenced in the hl7 file
		patient.setPatientNr("5083");
		CoreModelServiceHolder.get().save(patient);

		coverage = new ICoverageBuilder(CoreModelServiceHolder.get(), patient, "testLabel", "testReason", "KVG")
				.buildAndSave();

		new IEncounterBuilder(CoreModelServiceHolder.get(), coverage, mandator).buildAndSave();

	}

	public void configureLabAndLabItemBilling() {
		laboratory = new IContactBuilder.LaboratoryBuilder(CoreModelServiceHolder.get(), "Eigenlabor").buildAndSave();
		laboratory.addXid(XidConstants.XID_KONTAKT_LAB_SENDING_FACILITY, "ABXMicrosEmi", true);

		// bill mapping WBC
		item = new ILabItemBuilder(CoreModelServiceHolder.get(), "WBC", "Lekozyten", null, null, "G/l",
				LabItemTyp.NUMERIC, "ABX", 0).origin(laboratory, "WBC", true).buildAndSave();
		item.setBillingCode("1371.00");
		// is not imported - test missing
		CoreModelServiceHolder.get().save(item);
		// bill mapping rbc
		item = new ILabItemBuilder(CoreModelServiceHolder.get(), "RBC", "Erythrozyten", null, null, "T/l",
				LabItemTyp.NUMERIC, "ABX", 0).origin(laboratory, "RBC", true).buildAndSave();
		item.setBillingCode("1281.10");
		CoreModelServiceHolder.get().save(item);

		itemGPT = new ILabItemBuilder(CoreModelServiceHolder.get(), "GPT-P", "GPT", null, null, "U/l",
				LabItemTyp.NUMERIC, "LabCube", 0).origin(laboratory, "LabCube", true).buildAndSave();
	}

	public void importEal2009() {
		// create EAL Leistungen
		Optional<IReferenceDataImporter> laborImporter = OsgiServiceUtil.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=analysenliste)");
		assertTrue(laborImporter.isPresent());
		IStatus success = laborImporter.get().performImport(new NullProgressMonitor(),
				Hl7ImporterTaskIntegrationTestUtil.class.getResourceAsStream("/rsc/Custom_EAL_TestOnly.xls"), 190103);
		if (!success.isOK()) {
			throw new IllegalStateException("Could not import EAL");
		}
	}

	public IUser getUser() {
		return user;
	}

	public ILaboratory getLaboratory() {
		return laboratory;
	}

	public ICoverage getCoverage() {
		return coverage;
	}

	public IPatient getPatient() {
		return patient;
	}

	public ILabItem getLabItem() {
		return item;
	}

	public ILabItem getItemGPT() {
		return itemGPT;
	}

	public IMandator getMandator() {
		return mandator;
	}

}
