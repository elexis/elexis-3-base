package ch.elexis.data.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.data.PandemieLeistung;
import ch.rgw.tools.TimeTool;

public class PandemieImporterTest {
	
	@Test
	public void performImport() throws FileNotFoundException, SQLException{
		PandemieReferenceDataImporter importer = new PandemieReferenceDataImporter();
		Status retStatus =
			(Status) importer.performImport(new NullProgressMonitor(),
				PandemieImporterTest.class
					.getResourceAsStream("/rsc/pandemie_201102.xlsx"),
				null);
		assertEquals(IStatus.OK, retStatus.getCode());
		
		PandemieLeistung pauschale =
			(PandemieLeistung) PandemieLeistung.getFromCode("01.01.1050",
				new TimeTool("02.11.2020"));
		assertNotNull(pauschale);
		assertNotNull(pauschale.getText());
		assertEquals("2250", pauschale.get(PandemieLeistung.FLD_CENTS));
	}
}
