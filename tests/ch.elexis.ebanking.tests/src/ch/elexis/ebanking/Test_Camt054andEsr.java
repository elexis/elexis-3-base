package ch.elexis.ebanking;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.elexis.base.ch.ebanking.esr.ESRFile;
import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.base.ch.ebanking.esr.ESRRecord.MODE;
import ch.elexis.data.Patient;
import ch.elexis.ebanking.parser.Camet054Exception;
import ch.rgw.tools.Result;

public class Test_Camt054andEsr {
	
	private boolean ignoreSumRecordDates = true; // dates are not identical for sum records esr11 vs camt054
	
	@BeforeClass
	public static void init(){
		Patient p = new Patient("Test", "Nachname", "11.11.1999", "m");
		p.set(Patient.FLD_PATID, "5624");
		
		Assert.assertTrue(Patient.loadByPatientID("5624").exists());
	}
	
	@Test
	public void testReadRecords() throws Camet054Exception, IOException{
		// read camt
		Result<List<ESRRecord>> optionalCamtRecords = readCamtFile("Testfile1_1.xml");
		assertTrue(optionalCamtRecords.isOK());
		
		// read same file in esr format
		Result<List<ESRRecord>> optionalEsrV11Records = readEsrFile();
		assertTrue(optionalEsrV11Records.isOK());
		
		// print out camt with esr
		for (ESRRecord l : optionalCamtRecords.get()) {
			StringBuilder builder = getTextFromRecord(l);
			System.out.println("CAMT054:" + builder.toString());
		}
		
		for (ESRRecord l : optionalEsrV11Records.get()) {
			
			StringBuilder builder = getTextFromRecord(l);
			System.out.println("ESRV11:" + builder.toString());
		}
		
		// compare camt with esr
		Assert.assertTrue(optionalCamtRecords.get().size() == optionalEsrV11Records.get().size());
		
		for (ESRRecord l : optionalCamtRecords.get()) {
			boolean found = false;
			
			for (ESRRecord l2 : optionalEsrV11Records.get()) {
				if (getTextFromRecord(l2).toString().equals(getTextFromRecord(l).toString())) {
					found = true;
				}
			}
			if (!found) {
				System.out.println("NOT EQUAL:" + getTextFromRecord(l));
			}
			Assert.assertTrue(found);
		}
	}
	
	@Ignore("Use to test camt file that can not be included (pricay)")
	@Test
	public void testReadCamt() throws Camet054Exception, IOException {
		// read camt
		Result<List<ESRRecord>> optionalCamtRecords = readCamtFile("xxx.xml");
		assertTrue(optionalCamtRecords.isOK());
	}

	private StringBuilder getTextFromRecord(ESRRecord l){
		StringBuilder builder = new StringBuilder();
		builder.append("esr:" + l.getESRCode());
		builder.append("|patient:");
		builder.append(l.getPatient().exists() ? l.getPatient().getLabel() : "NULL");
		builder.append("|rechnung:");
		builder.append(l.getRechnung() != null ? l.getRechnung().getRnId() : "NULL");
		builder.append("|betrag:");
		builder.append(l.getBetrag().toString());
		if (!MODE.Summenrecord.equals(l.getTyp()) || !ignoreSumRecordDates) {
			builder.append("|einlesedatum:");
			builder.append(l.getEinlesedatatum());
			builder.append("|verarbeitungsdatum:");
			builder.append(l.getVerarbeitungsdatum());
			builder.append("|valuta:");
			builder.append(l.getValuta());
		}
		builder.append("|gebucht:");
		builder.append(l.getGebucht());
		builder.append("|rejectcode:");
		builder.append(l.getRejectCode());
		builder.append("|type:");
		builder.append(l.getTyp());
		return builder;
	}

	
	public Result<List<ESRRecord>> readCamtFile(String filename) {
		
		try {
			File file = File.createTempFile("camt_", ".xml");
			FileUtils.copyInputStreamToFile(Test_Camt054andEsr.class
					.getResourceAsStream("/rsc/" + filename), file);
			file.deleteOnExit();
			Assert.assertTrue(file.exists());
			ESRFile esrFile = new ESRFile();
			return esrFile.read(file, new NullProgressMonitor());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		return new Result<List<ESRRecord>>(Result.SEVERITY.ERROR, Collections.emptyList());
	}
	
	public Result<List<ESRRecord>> readEsrFile(){
		
		try {
			File file = File.createTempFile("test2", ".v11");
			FileUtils.copyInputStreamToFile(Test_Camt054andEsr.class
				.getResourceAsStream("/rsc/Testfile1_0.v11"), file);
			file.deleteOnExit();
			Assert.assertTrue(file.exists());
			ESRFile esrFile = new ESRFile();
			return esrFile.read(file, new NullProgressMonitor());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		return new Result<List<ESRRecord>>(Result.SEVERITY.ERROR, Collections.emptyList());
	}
}
