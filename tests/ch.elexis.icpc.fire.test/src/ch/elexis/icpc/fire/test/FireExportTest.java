package ch.elexis.icpc.fire.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Artikel;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.elexis.data.Xid.XIDException;
import ch.elexis.icpc.fire.handlers.ExportFireHandler;
import ch.elexis.icpc.fire.model.Report;
import ch.elexis.icpc.fire.ui.Preferences;
import ch.rgw.tools.TimeTool;

public class FireExportTest {

	static Patient testPatient;
	static Artikel artikel;
	static Prescription prescription;

	@BeforeClass
	public static void beforeClass() throws XIDException {

		configureExporter();

		testPatient = new Patient("Name", "Vorname", "26.8.2011", "m");
		Fall fall = testPatient.neuerFall("TestFall", "TestGrund", "KVG");
		Konsultation kons = new Konsultation(fall);
		TimeTool datum = new TimeTool("20180101");
		kons.setDatum(datum.toString(TimeTool.DATE_COMPACT), false);

		artikel = new Artikel("TestArtikel", "TestTyp");
		prescription = new Prescription(artikel, testPatient, "1-0-0", "TestPrescription");
		prescription.setBeginDate(datum.toString(TimeTool.DATE_GER));

		new Xid(kons.getMandant(), XidConstants.DOMAIN_EAN, "12345678901234");
	}

	private static void configureExporter() {
		CoreHub.globalCfg.set(Preferences.CFG_BD_SYST, "Vitaldaten:Systolisch");
		CoreHub.globalCfg.set(Preferences.CFG_BD_DIAST, "Vitaldaten:Diastolisch");
		CoreHub.globalCfg.set(Preferences.CFG_PULS, "Vitaldaten:Puls");
		CoreHub.globalCfg.set(Preferences.CFG_HEIGHT, "Vitaldaten:Grösse");
		CoreHub.globalCfg.set(Preferences.CFG_WEIGHT, "Vitaldaten:Gewicht");
		CoreHub.globalCfg.set(Preferences.CFG_BU, "Vitaldaten:Bauchumfang-Diabetiker");
	}

	@Test
	public void testMultipleExports() throws Exception {
		// perform initial export
		Report report = exportAll();
		assertEquals(1, report.getConsultations().getConsultation().size());
		assertEquals(1, report.getConsultations().getConsultation().get(0).getMedis().getMedi().size());
		CoreHub.globalCfg.set("ICPC_FIRE_LAST_UPLOAD", new TimeTool("20180101").toString(TimeTool.DATE_COMPACT));

		// perform another export with no changes
		report = exportAll();
		assertNull(report.getConsultations());
		CoreHub.globalCfg.set("ICPC_FIRE_LAST_UPLOAD", new TimeTool("20180201").toString(TimeTool.DATE_COMPACT));
		
		// now stop the medication, add another unreferenced for fun
		prescription.stop(new TimeTool("20180204"));
		Prescription prescription2 = new Prescription(artikel, testPatient, "1-2-3", "TestPrescription");
		prescription2.set(Prescription.FLD_DATE_FROM, new TimeTool("20180202").toString(TimeTool.DATE_COMPACT));
		prescription2.set(Prescription.FLD_DATE_UNTIL, new TimeTool("20180203").toString(TimeTool.DATE_COMPACT));
		report = exportAll();
		assertEquals(2, report.getConsultations().getConsultation().size());
		assertEquals(1, report.getConsultations().getConsultation().get(0).getMedis().getMedi().size());
		assertEquals(1, report.getConsultations().getConsultation().get(1).getMedis().getMedi().size());
		CoreHub.globalCfg.set("ICPC_FIRE_LAST_UPLOAD", new TimeTool("20180301").toString(TimeTool.DATE_COMPACT));
		
		// perform another export with no changes
		report = exportAll();
		assertNull(report.getConsultations());
	}

	private Report exportAll() throws Exception {
		File testFile = File.createTempFile("temp", Long.toString(System.nanoTime()));
		Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
		IRunnableWithProgress exportJob = new ExportFireHandler().createReportExportRunnable(qbe.execute(),
				testFile.getAbsolutePath());
		NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
		exportJob.run(nullProgressMonitor);

		Object fireReport = unmarshallFireReport(new FileInputStream(testFile));
		assertNotNull(fireReport);
		assertTrue(fireReport instanceof Report);
		return (Report) fireReport;
	}

	public static Object unmarshallFireReport(InputStream inStream) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Report.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		return unmarshaller.unmarshal(inStream);

	}

}
