package ch.elexis.TarmedRechnung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.test.TestData;
import ch.elexis.base.ch.arzttarife.test.TestData.TestSzenario;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;

public class XMLExporterTest {
	@Test
	public void doExportTest(){
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario.getRechnungen());
		assertFalse(szenario.getRechnungen().isEmpty());
		XMLExporter exporter = new XMLExporter();
		Document result =
			exporter.doExport(szenario.getRechnungen().get(0), CoreHub.getTempDir()
				.getAbsolutePath() + File.separator + "tarmedTest.xml", IRnOutputter.TYPE.ORIG,
				true);
		assertNotNull(result);
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
		Attribute gender = patient.getAttribute("gender");
		assertEquals("female", gender.getValue());
	}
}
