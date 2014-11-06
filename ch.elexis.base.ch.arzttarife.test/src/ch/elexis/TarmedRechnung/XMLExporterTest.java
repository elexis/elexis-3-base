package ch.elexis.TarmedRechnung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.test.TestData;
import ch.elexis.base.ch.arzttarife.test.TestData.TestSzenario;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;

public class XMLExporterTest {
	@Test
	public void doExportTest() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario.getRechnungen());
		assertFalse(szenario.getRechnungen().isEmpty());
		XMLExporter exporter = new XMLExporter();
		List<Rechnung> rechnungen = szenario.getRechnungen();
		for (Rechnung rechnung : rechnungen) {
			Document result =
				exporter.doExport(rechnung, CoreHub.getTempDir()
					.getAbsolutePath() + File.separator + "tarmedTest.xml", IRnOutputter.TYPE.ORIG,
					true);
			assertNotNull(result);
			if (rechnung.getStatus() == RnStatus.FEHLERHAFT) {
				printFaildDocument(result);
				fail();
			}
			// check if the leistung is included
			Element root = result.getRootElement();
			Iterator<?> iter = root.getDescendants(new ElementFilter("record_tarmed"));
			assertTrue(iter.hasNext());
			Element record = (Element) iter.next();
			Attribute code = record.getAttribute("code");
			assertEquals("00.0010", code.getValue());
			// check if patient is included
			iter = root.getDescendants(new ElementFilter("patient"));
			assertTrue(iter.hasNext());
			Element patient = (Element) iter.next();
			assertNotNull(patient);
		}
	}
	
	private void printFaildDocument(Document result) throws IOException{
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(result, System.err);
	}
}
