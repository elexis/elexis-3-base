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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.junit.Test;

import at.medevit.elexis.emediplan.core.model.print.Medication;
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
				getPatientMedication(patients.get(0)));
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		assertTrue(jsonString.get().contains("\"Auth\":\"2000000000002\""));
		assertTrue(jsonString.get().contains("\"LName\":\"Spitzkiel\""));
		assertTrue(jsonString.get().contains("7680336700282"));
		assertTrue(jsonString.get().contains("\"Off\":28800"));
		assertTrue(jsonString.get().contains("5390827"));
		
		jsonString = impl.getJsonString(TestData.getTestSzenarioInstance().getMandator(),
			patients.get(1), getPatientMedication(patients.get(1)));
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		assertTrue(jsonString.get().contains("\"LName\":\"Zirbelkiefer\""));
		assertTrue(jsonString.get().contains("7680336700282"));
		assertTrue(jsonString.get().contains("\"Off\":28800"));
		assertFalse(jsonString.get().contains("5390827"));
	}
	
	@Test
	public void getEncodedJsonString() throws IOException{
		EMediplanServiceImpl impl = new EMediplanServiceImpl();
		List<IPatient> patients = TestData.getTestSzenarioInstance().getPatients();
		Optional<String> jsonString =
			impl.getJsonString(TestData.getTestSzenarioInstance().getMandator(), patients.get(0),
				getPatientMedication(patients.get(0)));
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		String encodedString = impl.getEncodedJson(jsonString.get());
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
				getPatientMedication(patients.get(0)));
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		String encodedString = impl.getEncodedJson(jsonString.get());
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
				getPatientMedication(patients.get(0)));
		assertTrue(jsonString.isPresent());
		assertFalse(jsonString.get().isEmpty());
		String encodedString = impl.getEncodedJson(jsonString.get());
		assertNotNull(encodedString);
		assertFalse(encodedString.isEmpty());
		String decodedString = impl.getDecodedJsonString(encodedString);
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
	
	private List<IPrescription> getPatientMedication(IPatient patient){
		List<IPrescription> medication = patient
			.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION, EntryType.RESERVE_MEDICATION));
		return medication;
	}
	
}
