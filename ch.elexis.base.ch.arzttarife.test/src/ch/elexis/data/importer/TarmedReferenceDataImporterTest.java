package ch.elexis.data.importer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.data.importer.TarmedReferenceDataImporter;
import ch.rgw.tools.JdbcLink;

public class TarmedReferenceDataImporterTest {
	private String codeGutachtenKatA = "00.2310";
	private String codeAerztlGutachten = "00.07";
	private String codeTapingKat1 = "01.0110";
	private String codeBesuchErste5Min = "00.0060";
	
	@Test
	public void performImportTest() throws FileNotFoundException, SQLException{
		File tarmedFile =
			new File(System.getProperty("user.dir") + File.separator + "rsc" + File.separator
				+ "tarmed.mdb");
		InputStream tarmedInStream = new FileInputStream(tarmedFile);
		
		TarmedReferenceDataImporter importer = new TarmedReferenceDataImporter();
		importer.suppressRestartDialog();
		Status retStatus =
			(Status) importer.performImport(new NullProgressMonitor(), tarmedInStream, null);
		assertEquals(IStatus.OK, retStatus.getCode());
		
		JdbcLink cacheDb = new JdbcLink("org.h2.Driver", "jdbc:h2:mem:tarmed_import", "hsql");
		cacheDb.connect("", "");
		ResultSet res =
			cacheDb.getStatement().query(
				String.format("SELECT * FROM %sLEISTUNG", "TARMED_IMPORT_"));
		
		List<String> codes = new ArrayList<String>();
		while (res != null && res.next()) {
			String code = res.getString("LNR");
			codes.add(code);
			
			String knrCode = res.getString("KNR");
			codes.add(knrCode);
		}
		
		Assert.assertTrue(codes.contains(codeAerztlGutachten));
		Assert.assertTrue(codes.contains(codeGutachtenKatA));
		Assert.assertTrue(codes.contains(codeTapingKat1));
		Assert.assertTrue(codes.contains(codeBesuchErste5Min));
	}
}
