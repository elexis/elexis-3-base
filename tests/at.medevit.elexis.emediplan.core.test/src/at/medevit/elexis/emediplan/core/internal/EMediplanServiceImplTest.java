package at.medevit.elexis.emediplan.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.junit.Test;

import at.medevit.elexis.emediplan.core.EMediplanUtil;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament;
import at.medevit.elexis.emediplan.core.model.print.Medication;
import at.medevit.elexis.emediplan.core.test.AllTests;
import at.medevit.elexis.emediplan.core.test.TestData;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;

public class EMediplanServiceImplTest {
	

	private static final boolean WRITE_AND_OPEN = false;
	
	@Test
	public void getJsonString() throws IOException{
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		List<IPatient> patients = TestData.getTestSzenarioInstance().getPatients();
		Optional<String> jsonString =
			impl.getJsonString(TestData.getTestSzenarioInstance().getMandator(), patients.get(0),
				getPatientMedication(patients.get(0)), false);
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		assertTrue(jsonString.get().contains("\"Auth\":\"2000000000002\""));
		assertTrue(jsonString.get().contains("\"LName\":\"Spitzkiel\""));
		assertTrue(jsonString.get().contains("7680336700282"));
		assertTrue(jsonString.get().contains("\"D\":[1.0,1.0,1.0,1.0]"));
		assertTrue(jsonString.get().contains("4881026"));
		
		jsonString = impl.getJsonString(TestData.getTestSzenarioInstance().getMandator(),
			patients.get(1), getPatientMedication(patients.get(1)), true);
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		assertTrue(jsonString.get().contains("\"LName\":\"Zirbelkiefer\""));
		assertTrue(jsonString.get().contains("7680336700282"));
		assertTrue(
			jsonString.get().contains("\"Nm\":\"Dsc\",\"Val\":\"ASPIRIN C Brausetabl 10 Stk\""));
		assertTrue(jsonString.get().contains("{\"Nm\":\"TkgSch\",\"Val\":\"Cnt\"}"));
		assertFalse(jsonString.get().contains("4881026"));
	}
	
	@Test
	public void getModelFromChunk(){
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		List<IPatient> patients = TestData.getTestSzenarioInstance().getPatients();
		Optional<String> jsonString =
			impl.getJsonString(TestData.getTestSzenarioInstance().getMandator(), patients.get(0),
				getPatientMedication(patients.get(0)), true);
		String encodedString = EMediplanUtil.getEncodedJson(jsonString.get());
		// reload model
		at.medevit.elexis.emediplan.core.model.chmed16a.Medication model =
			impl.createModelFromChunk(encodedString);
		assertNotNull(model);
		// test medicaments private fields
		assertEquals(4, model.Medicaments.size());
		for (Medicament medicament : model.Medicaments) {
			assertEquals(2, medicament.PFields.size());
			assertTrue(StringUtils.isNotBlank(impl.getPFieldValue(medicament, "Dsc")));
			assertTrue(StringUtils.isNotBlank(impl.getPFieldValue(medicament, "TkgSch")));
		}
	}
	
	@Test
	public void getEncodedJsonString() throws IOException{
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		List<IPatient> patients = TestData.getTestSzenarioInstance().getPatients();
		Optional<String> jsonString =
			impl.getJsonString(TestData.getTestSzenarioInstance().getMandator(), patients.get(0),
				getPatientMedication(patients.get(0)), false);
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		String encodedString = EMediplanUtil.getEncodedJson(jsonString.get());
		assertNotNull(encodedString);
		assertFalse(encodedString.isEmpty());
		assertTrue(encodedString.startsWith("CHMED16A1"));
		assertTrue(encodedString.length() > 9);
	}
	
	@Test
	public void getQrCode() throws IOException{
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		List<IPatient> patients = TestData.getTestSzenarioInstance().getPatients();
		Optional<String> jsonString =
			impl.getJsonString(TestData.getTestSzenarioInstance().getMandator(), patients.get(0),
				getPatientMedication(patients.get(0)), false);
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		String encodedString = EMediplanUtil.getEncodedJson(jsonString.get());
		assertNotNull(encodedString);
		assertFalse(encodedString.isEmpty());
		Optional<Image> qr = impl.getQrCode(encodedString);
		assertTrue(qr.isPresent());
	}
	
	@Test
	public void getDecodedJsonString() throws IOException{
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		List<IPatient> patients = TestData.getTestSzenarioInstance().getPatients();
		Optional<String> jsonString =
			impl.getJsonString(TestData.getTestSzenarioInstance().getMandator(), patients.get(0),
				getPatientMedication(patients.get(0)), false);
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		String encodedString = EMediplanUtil.getEncodedJson(jsonString.get());
		assertNotNull(encodedString);
		assertFalse(encodedString.isEmpty());
		String decodedString = EMediplanUtil.getDecodedJsonString(encodedString);
		assertNotNull(decodedString);
		assertFalse(decodedString.isEmpty());
		assertEquals(jsonString.get(), decodedString);
	}
	
	@Test
	public void getJaxbModel(){
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		List<IPatient> patients = TestData.getTestSzenarioInstance().getPatients();
		Optional<Medication> jaxbModel =
			impl.getJaxbModel(TestData.getTestSzenarioInstance().getMandator(), patients.get(0),
				getPatientMedication(patients.get(0)));
		assertTrue(jaxbModel.isPresent());
		assertTrue(jaxbModel.get() instanceof Medication);
	}
	
	@Test
	public void exportEMediplanPdf() throws FileNotFoundException, IOException{
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		List<IPatient> patients = TestData.getTestSzenarioInstance().getPatients();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		impl.exportEMediplanPdf(
			TestData.getTestSzenarioInstance().getMandator(), patients.get(0),
			getPatientMedication(patients.get(0)), output);
		if (WRITE_AND_OPEN) {
			try (FileOutputStream fout = new FileOutputStream(
				new File(CoreHub.getWritableUserDir(), "emediplan_test.pdf"))) {
				fout.write(output.toByteArray());
			}
			Program.launch(CoreHub.getWritableUserDir().getAbsolutePath() + File.separator
				+ "emediplan_test.pdf");
		}
		assertTrue(output.size() > 100);
	}
	
	@Test
	public void loadNbPackFloat() throws IOException{
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		at.medevit.elexis.emediplan.core.model.chmed16a.Medication model =
			impl.createModelFromJsonString(AllTests.getAsString("/rsc/NbPack_float.json"));
		assertNotNull(model);
	}
	
	@Test
	public void loadNbPackInt() throws IOException{
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		at.medevit.elexis.emediplan.core.model.chmed16a.Medication model =
			impl.createModelFromJsonString(AllTests.getAsString("/rsc/NbPack_int.json"));
		assertNotNull(model);
	}
	
	private List<IPrescription> getPatientMedication(IPatient patient){
		List<IPrescription> medication = patient
			.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION, EntryType.RESERVE_MEDICATION));
		return medication;
	}
	
}
