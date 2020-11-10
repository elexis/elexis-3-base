package ch.elexis.base.ch.arzttarife.pandemie.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.utils.OsgiServiceUtil;

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
		
		ICodeElementService codeElementService =
			OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement pauschale =
			codeElementService.loadFromString("Pandemie", "01.01.1050", null).get();
		assertNotNull(pauschale);
		assertNotNull(pauschale.getText());
		assertEquals(2250, ((IPandemieLeistung) pauschale).getCents());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
