package ch.elexis.pdfBills.test;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.LoggerFactory;

import ch.elexis.core.rcp.utils.PlatformHelper;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.pdfBills.ElexisPDFGeneratorTest;
import ch.elexis.pdfBills.QrRnOutputter;
import ch.elexis.pdfBills.RnOutputter;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ElexisPDFGeneratorTest.class })
public class AllTests {

	public static final File fragmentRsc = new File(PlatformHelper.getBasePath("ch.elexis.pdfBills.test"), "rsc");

	public static final File pluginRsc = new File(PlatformHelper.getBasePath("ch.elexis.pdfBills"), "rsc");

	public static Document getBillDocument(String string) {
		File xmlFile = new File(fragmentRsc, "xml" + File.separator + string + ".xml");
		if (xmlFile.exists()) {
			try {
				SAXBuilder builder = new SAXBuilder();
				return builder.build(xmlFile);
			} catch (JDOMException | IOException e) {
				LoggerFactory.getLogger(AllTests.class).error("Error loading XML document", e);
			}
		}
		return null;
	}

	public static String getBillXmlFilePath(String string) {
		File result = new File(fragmentRsc, "xml" + File.separator + string + ".xml");
		return result.getAbsolutePath();
	}

	public static void setOutputDir(String string) {
		LocalConfigService.set(RnOutputter.CFG_ROOT + RnOutputter.PDFDIR,
				fragmentRsc.getAbsolutePath() + File.separator + string);
		LocalConfigService.set(QrRnOutputter.CFG_ROOT + QrRnOutputter.PDFDIR,
				fragmentRsc.getAbsolutePath() + File.separator + string);
		String pdfDir = fragmentRsc.getAbsolutePath() + File.separator + string;
		File patPath = new File(pdfDir);
		if (!patPath.exists()) {
			LoggerFactory.getLogger(AllTests.class).warn("Created directory {} ", pdfDir);
			patPath.mkdirs();
		}
	}
}
