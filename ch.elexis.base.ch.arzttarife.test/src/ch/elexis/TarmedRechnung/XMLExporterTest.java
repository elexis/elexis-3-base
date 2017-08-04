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

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.After;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.test.TestData;
import ch.elexis.base.ch.arzttarife.test.TestData.TestSzenario;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;

public class XMLExporterTest {
	
	@After
	public void teardown() throws Exception{
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run(){
				PlatformUI.getWorkbench().saveAllEditors(false); // do not confirm saving
				PlatformUI.getWorkbench().saveAll(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(), null, false);
				if (PlatformUI.getWorkbench() != null) // null if run from Eclipse-IDE
				{
					// needed if run as surefire test from using mvn install
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.closeAllPerspectives(false, true);
					} catch (Exception e) {
						
						System.out.println(e.getMessage());
					}
				}
			}
		});
		
	}
	
	@Test
	public void doExportTest() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario.getRechnungen());
		assertFalse(szenario.getRechnungen().isEmpty());
		XMLExporter exporter = new XMLExporter();
		List<Rechnung> rechnungen = szenario.getRechnungen();
		for (Rechnung rechnung : rechnungen) {
			Document result =
				exporter.doExport(rechnung, getTempDestination(), IRnOutputter.TYPE.ORIG, true);
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
			if (patient.getAttributeValue("birthdate").equals("1957-04-14T00:00:00")) {
				Iterator telcoms = patient.getDescendants(new ElementFilter("telecom"));
				if (!telcoms.hasNext()) {
					printFaildDocument(result);
					fail();
				}
			} else {
				Iterator telcoms = patient.getDescendants(new ElementFilter("telecom"));
				if (telcoms.hasNext()) {
					printFaildDocument(result);
					fail();
				}
			}
		}
	}
	
	@Test
	public void doExportVatTest() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario.getRechnungen());
		assertFalse(szenario.getRechnungen().isEmpty());
		XMLExporter exporter = new XMLExporter();
		List<Rechnung> rechnungen = szenario.getRechnungen();
		for (Rechnung rechnung : rechnungen) {
			Document result =
				exporter.doExport(rechnung, getTempDestination(), IRnOutputter.TYPE.ORIG, true);
			assertNotNull(result);
			if (rechnung.getStatus() == RnStatus.FEHLERHAFT) {
				printFaildDocument(result);
				fail();
			}
			// check if the vat is included
			Element root = result.getRootElement();
			Iterator<?> iter = root.getDescendants(new ElementFilter("vat_rate"));
			assertTrue(iter.hasNext());
			while (iter.hasNext()) {
				Element vat_rate = (Element) iter.next();
				Attribute rate = vat_rate.getAttribute("vat_rate");
				Attribute vat = vat_rate.getAttribute("vat");
				if ("0.00".equals(rate.getValue())) {
					assertEquals(0.0, Double.parseDouble(vat.getValue()), 0.01);
				} else {
					Attribute amount = vat_rate.getAttribute("amount");
					Double rateDouble = Double.parseDouble(rate.getValue());
					Double amountDouble = Double.parseDouble(amount.getValue());
					Double expectedVat = (amountDouble / (100.0 + rateDouble)) * rateDouble;
					assertEquals(expectedVat, Double.parseDouble(vat.getValue()), 0.01);
				}
			}
		}
	}
	
	@Test
	public void doExportObligationTest() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario.getRechnungen());
		assertFalse(szenario.getRechnungen().isEmpty());
		XMLExporter exporter = new XMLExporter();
		List<Rechnung> rechnungen = szenario.getRechnungen();
		for (Rechnung rechnung : rechnungen) {
			Document result =
				exporter.doExport(rechnung, getTempDestination(), IRnOutputter.TYPE.ORIG, true);
			assertNotNull(result);
			if (rechnung.getStatus() == RnStatus.FEHLERHAFT) {
				printFaildDocument(result);
				fail();
			}
			// check if the amount_obligations amount of the balance element is correct
			Element root = result.getRootElement();
			Iterator<?> iter = root.getDescendants(new ElementFilter("balance"));
			assertTrue(iter.hasNext());
			while (iter.hasNext()) {
				Element balance = (Element) iter.next();
				Attribute amount = balance.getAttribute("amount");
				Attribute obligations = balance.getAttribute("amount_obligations");
				assertNotNull(amount);
				assertNotNull(obligations);
				Double amountDouble = Double.parseDouble(amount.getValue());
				Double obligationsDouble = Double.parseDouble(obligations.getValue());
				assertTrue(amountDouble > 0.0D);
				assertTrue(obligationsDouble > 0.0D);
				assertTrue(amountDouble > obligationsDouble);
			}
		}
	}
	
	@Test
	public void doExportPrepaidTest() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario.getRechnungen());
		assertFalse(szenario.getRechnungen().isEmpty());
		XMLExporter exporter = new XMLExporter();
		List<Rechnung> rechnungen = szenario.getRechnungen();
		for (Rechnung rechnung : rechnungen) {
			Document result =
				exporter.doExport(rechnung, getTempDestination(), IRnOutputter.TYPE.ORIG, true);
			assertNotNull(result);
			if (rechnung.getStatus() == RnStatus.FEHLERHAFT) {
				printFaildDocument(result);
				fail();
			}
			// check if the amount_prepaid amount of the balance element is correct
			Element root = result.getRootElement();
			Iterator<?> iter = root.getDescendants(new ElementFilter("balance"));
			assertTrue(iter.hasNext());
			while (iter.hasNext()) {
				Element balance = (Element) iter.next();
				Attribute amount = balance.getAttribute("amount");
				Attribute prepaid = balance.getAttribute("amount_prepaid");
				assertNotNull(amount);
				assertNotNull(prepaid);
				Double amountDouble = Double.parseDouble(amount.getValue());
				Double prepaidDouble = Double.parseDouble(prepaid.getValue());
				assertTrue(amountDouble > 0.0D);
				assertTrue(prepaidDouble == 0.0D);
			}
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
		
		result = exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.STORNO, true);
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
	public void doReExportExistingPrepaid4Test() throws IOException{
		Namespace namespace = Namespace.getNamespace("http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$
		
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		Rechnung existing = szenario.getExistingRechnung(TestData.EXISTING_4_2_RNR);
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
		assertEquals("10.00", prepaid);
		String amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("17.76", amount);
		String due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("7.75", due);
		
		result = exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.ORIG, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		invoice = result.getRootElement().getChild("invoice", namespace);//$NON-NLS-1$
		balance = invoice.getChild("balance", namespace);//$NON-NLS-1$
		prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("10.00", prepaid);
		amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("17.76", amount);
		due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("7.75", due);
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
	
	@Test
	public void doReExportExistingPrepaid44Test() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		Rechnung existing = szenario.getExistingRechnung(TestData.EXISTING_44_2_RNR);
		XMLExporter exporter = new XMLExporter();
		Document result =
			exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.ORIG, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		Element payload = result.getRootElement().getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		Element body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$
		Element balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$	
		String prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("4000.00", prepaid);
		String due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("1217.75", due);
		String amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("5217.76", amount);
		
		result = exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.ORIG, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		payload = result.getRootElement().getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$
		balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$
		prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("4000.00", prepaid);
		due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("1217.75", due);
		amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("5217.76", amount);
	}
	
	@Test
	public void fixErrorneousBill44Test() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		Rechnung erroneous = szenario.getExistingRechnung(TestData.ERRONEOUS_44_1_RNR);
		XMLExporter exporter = new XMLExporter();
		Document result =
			exporter.doExport(erroneous, getTempDestination(), IRnOutputter.TYPE.ORIG, true);
		assertNotNull(result);
		if (erroneous.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		Element payload = result.getRootElement().getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		Element body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$
		Element balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$	
		String prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("4000.00", prepaid);
		String due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("1217.75", due);
		String amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("5217.76", amount);
		
		erroneous.addZahlung(new Money(10.0), "test", new TimeTool());
		result = exporter.doExport(erroneous, getTempDestination(), IRnOutputter.TYPE.ORIG, true);
		assertNotNull(result);
		if (erroneous.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		payload = result.getRootElement().getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$
		balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$
		prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("4010.00", prepaid);
		due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("1207.75", due);
		amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("5217.76", amount);
	}
	
	@Test
	public void doExportReminder4Test() throws IOException{
		Namespace namespace = Namespace.getNamespace("http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$
		
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		Rechnung existing = szenario.getExistingRechnung(TestData.EXISTING_4_3_RNR);
		// set reminder booking and state
		existing.addZahlung(new Money(10.0).multiply(-1.0),
			ch.elexis.data.Messages.Rechnung_Mahngebuehr1, null);
		existing.setStatus(InvoiceState.DEMAND_NOTE_1.numericValue());
		// output the bill
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
		assertEquals("0.00", prepaid);
		String amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("17.76", amount);
		String due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("17.75", due);
		// customer pays including reminder fee
		existing.addZahlung(new Money(27.75), "", null);
		result =
			exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.COPY, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		invoice = result.getRootElement().getChild("invoice", namespace);//$NON-NLS-1$
		balance = invoice.getChild("balance", namespace);//$NON-NLS-1$
		prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("17.76", prepaid);
		amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("17.76", amount);
		due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("0.00", due);
		assertEquals(existing.getInvoiceState(), InvoiceState.PAID);
	}
	
	@Test
	public void doExportReminder44Test() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		Rechnung existing = szenario.getExistingRechnung(TestData.EXISTING_44_3_RNR);
		// set reminder booking and state
		existing.addZahlung(new Money(10.0).multiply(-1.0),
			ch.elexis.data.Messages.Rechnung_Mahngebuehr1, null);
		existing.setStatus(InvoiceState.DEMAND_NOTE_1.numericValue());
		// output the bill
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
		assertEquals("0.00", prepaid);
		String due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("5227.76", due);
		String amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("5217.76", amount);
		String reminder = balance.getAttributeValue("amount_reminder");
		assertEquals("10.00", reminder);
		// set reminder booking and state
		existing.addZahlung(new Money(10.0).multiply(-1.0),
			ch.elexis.data.Messages.Rechnung_Mahngebuehr2, null);
		existing.setStatus(InvoiceState.DEMAND_NOTE_2.numericValue());
		// output the bill
		exporter = new XMLExporter();
		result = exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.COPY, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		payload = result.getRootElement().getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$
		balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$
		prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("0.00", prepaid);
		due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("5237.76", due);
		amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("5217.76", amount);
		reminder = balance.getAttributeValue("amount_reminder");
		assertEquals("20.00", reminder);
		// customer pays without reminder fee
		existing.addZahlung(new Money(5217.76), "", null);
		result = exporter.doExport(existing, getTempDestination(), IRnOutputter.TYPE.COPY, true);
		assertNotNull(result);
		if (existing.getStatus() == RnStatus.FEHLERHAFT) {
			printFaildDocument(result);
			fail();
		}
		payload = result.getRootElement().getChild("payload", XMLExporter.nsinvoice);//$NON-NLS-1$
		body = payload.getChild("body", XMLExporter.nsinvoice);//$NON-NLS-1$
		balance = body.getChild("balance", XMLExporter.nsinvoice);//$NON-NLS-1$
		prepaid = balance.getAttributeValue("amount_prepaid");//$NON-NLS-1$
		assertEquals("5217.76", prepaid);
		due = balance.getAttributeValue("amount_due");//$NON-NLS-1$
		assertEquals("20.00", due);
		amount = balance.getAttributeValue("amount");//$NON-NLS-1$
		assertEquals("5217.76", amount);
		reminder = balance.getAttributeValue("amount_reminder");
		assertEquals("20.00", reminder);
	}
	
	private String getTempDestination(){
		return CoreHub.getTempDir().getAbsolutePath() + File.separator + "tarmedTest.xml";
	}
	
	private void printFaildDocument(Document result) throws IOException{
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(result, System.err);
	}
}
