package ch.elexis.data.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.ComplementaryLeistung;
import ch.rgw.tools.TimeTool;

public class ComplementaryImporterTest {
	
	@Test
	public void performImport() throws FileNotFoundException, SQLException{
		ComplementaryReferenceDataImporter importer = new ComplementaryReferenceDataImporter();
		Status retStatus =
			(Status) importer.performImport(new NullProgressMonitor(),
				ComplementaryImporterTest.class
					.getResourceAsStream("/rsc/complementary_170822.csv"),
				null);
		assertEquals(IStatus.OK, retStatus.getCode());
		
		IVerrechenbar bachFlowers = ComplementaryLeistung.getFromCode("1022", new TimeTool());
		assertNotNull(bachFlowers);
		assertNotNull(bachFlowers.getText());
	}
}
