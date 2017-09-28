package ch.elexis.ebanking;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Assert;
import org.junit.Test;

import ch.elexis.base.ch.ebanking.esr.ESRFile;
import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.ebanking.parser.Camet054Exception;
import ch.rgw.tools.Result;


public class Test_Camt054andEsr {
	
	private boolean ingoreDates = false; //TODO dates are not identical esr11 vs camt054
	
	@Test
	public void testReadRecords() throws Camet054Exception, IOException{
		
		InputStream in = getInputStreamCamt();
		
		// read camt
		Result<List<ESRRecord>> optionalCamtRecords = readCamtFile();
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
		int i = 0;
		for (ESRRecord l : optionalCamtRecords.get()) {
			Assert.assertEquals(getTextFromRecord(l).toString(),
				getTextFromRecord(optionalEsrV11Records.get().get(i++)).toString());
		}
		
		in.close();
	}

	private StringBuilder getTextFromRecord(ESRRecord l){
		StringBuilder builder = new StringBuilder();
		builder.append("esr:" + l.getESRCode());
		builder.append("|patient:");
		builder.append(l.getPatient().getLabel());
		builder.append("|rechnung:");
		builder.append(l.getRechnung() != null ? l.getRechnung().getRnId() : "NULL");
		builder.append("|betrag:");
		builder.append(l.getBetrag().toString());
		if (!ingoreDates) {
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
	
	private InputStream getInputStreamCamt(){
		return Test_Camt054andEsr.class.getResourceAsStream(
			"/rsc/camt.054-ESR-TESTFILE.xml");
	}
	
	public Result<List<ESRRecord>> readCamtFile(){
		
		try {
			File file = File.createTempFile("camt.054-ESR-TESTFILE", ".xml");
			FileUtils.copyInputStreamToFile(
				Test_Camt054andEsr.class.getResourceAsStream("/rsc/camt.054-ESR-TESTFILE.xml"),
				file);
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
			File file = File.createTempFile("ESR_TESTFILE", ".v11");
			FileUtils.copyInputStreamToFile(
				Test_Camt054andEsr.class.getResourceAsStream("/rsc/ESR_TESTFILE.v11"), file);
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
