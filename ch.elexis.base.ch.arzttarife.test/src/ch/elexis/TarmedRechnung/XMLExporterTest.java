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
import org.jdom.Namespace;
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
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;

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
				exporter.doExport(rechnung, getTempDestination(), IRnOutputter.TYPE.ORIG,
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
	
	@Test
	public void doExportExisting4Test() throws IOException{
		Namespace namespace = Namespace.getNamespace("http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$

		TestSzenario szenario = TestData.getTestSzenarioInstance();
		Rechnung existing = szenario.getExistingRechnung(TestData.EXISTING_4_RNR);
		existing.addZahlung(new Money(1.0), "test", new TimeTool());
		XMLExporter exporter = new XMLExporter();
		Document result =
			exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.COPY, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		Element invoice = result.getRootElement().getChild("invoice", namespace);//$NON-NLS-1$
		Element balance = invoice.getChild("balance", namespace);//$NON-NLS-1$
		String prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("1.00", prepaid);

		result =
 exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.STORNO, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		invoice = result.getRootElement().getChild("invoice", namespace);//$NON-NLS-1$
		balance = invoice.getChild("balance", namespace);//$NON-NLS-1$
		String due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("0.00", due);
	}
	
	@Test
	public void doExportExisting44Test() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		Rechnung existing = szenario.getExistingRechnung(TestData.EXISTING_44_RNR);
		existing.addZahlung(new Money(1.0), "test", new TimeTool());
		XMLExporter exporter = new XMLExporter();
		Document result =
			exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.COPY, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		Element payload = result.getRootElement().getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		Element body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$
		Element balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$

		String prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("1.00", prepaid);
		
		result = exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.STORNO, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		payload = result.getRootElement().getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$
		balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$
		String due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("0.00", due);
	}

	private String getTempDestination(){
		return CoreHub.getTempDir().getAbsolutePath() + File.separator + "tarmedTest.xml";
	}

	private void printFaildDocument(Document result) throws IOException{
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(result, System.err);
	}
}
