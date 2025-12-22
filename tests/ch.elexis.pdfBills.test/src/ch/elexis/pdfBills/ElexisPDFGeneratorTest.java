package ch.elexis.pdfBills;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ch.elexis.pdfBills.test.AllTests;

public class ElexisPDFGeneratorTest {

	// @Test
	// public void create400Generator(){
	// String billXmlFile = AllTests.getBillXmlFilePath("400_tg_kvg_de");
	// assertNotNull(billXmlFile);
	// AllTests.setOutputDir("output");
	// ElexisPDFGenerator generator = new ElexisPDFGenerator(billXmlFile,
	// "400_tg_kvg");
	// assertEquals("4.0", generator.getXmlVersion());
	// assertTrue(generator.isTierGarant());
	// }
	//
	// @Test
	// public void generate400TGBill(){
	// String billXmlFile = AllTests.getBillXmlFilePath("400_tg_kvg_de");
	// assertNotNull(billXmlFile);
	// AllTests.setOutputDir("output");
	// ElexisPDFGenerator generator =
	// new ElexisPDFGenerator(billXmlFile, "400_tg_kvg");
	// generator.printBill(AllTests.pluginRsc);
	// }
	//
	// @Test
	// public void create440Generator(){
	// String billXmlFile = AllTests.getBillXmlFilePath("md_440_tg_kvg_de");
	// assertNotNull(billXmlFile);
	// AllTests.setOutputDir("output");
	// ElexisPDFGenerator generator = new ElexisPDFGenerator(billXmlFile,
	// "md_440_tg_kvg_de");
	// assertEquals("4.4", generator.getXmlVersion());
	// assertTrue(generator.isTierGarant());
	//
	// billXmlFile = AllTests.getBillXmlFilePath("md_440_tp_kvg_de");
	// assertNotNull(billXmlFile);
	// AllTests.setOutputDir("output");
	// generator = new ElexisPDFGenerator(billXmlFile, "md_440_tp_kvg_de");
	// assertEquals("4.4", generator.getXmlVersion());
	// assertFalse(generator.isTierGarant());
	// }
	//
	// @Test
	// public void generate440TGBill(){
	// String billXmlFile = AllTests.getBillXmlFilePath("md_440_tg_kvg_de");
	// assertNotNull(billXmlFile);
	// AllTests.setOutputDir("output");
	// ElexisPDFGenerator generator =
	// new ElexisPDFGenerator(billXmlFile, "440_tg_kvg");
	// generator.printBill(AllTests.pluginRsc);
	//
	// billXmlFile = AllTests.getBillXmlFilePath("md_440_tg_uvg_3783");
	// assertNotNull(billXmlFile);
	// AllTests.setOutputDir("output");
	// generator = new ElexisPDFGenerator(billXmlFile, "440_tg_uvg_3783");
	// generator.printBill(AllTests.pluginRsc);
	//
	// billXmlFile = AllTests.getBillXmlFilePath("md_440_tg_kvg_3793");
	// assertNotNull(billXmlFile);
	// AllTests.setOutputDir("output");
	// generator = new ElexisPDFGenerator(billXmlFile, "440_tg_kvg_3793");
	// generator.printBill(AllTests.pluginRsc);
	// }
	//
	// @Test
	// public void generate440TPBill(){
	// String billXmlFile = AllTests.getBillXmlFilePath("md_440_tp_kvg_de");
	// assertNotNull(billXmlFile);
	// AllTests.setOutputDir("output");
	// ElexisPDFGenerator generator = new ElexisPDFGenerator(billXmlFile,
	// "440_tp_kvg");
	// generator.printBill(AllTests.pluginRsc);
	// }

	@Test
	public void generate450TPBill() {
		String billXmlFile = AllTests.getBillXmlFilePath("md_450_tp_kvg_de");
		assertNotNull(billXmlFile);
		AllTests.setOutputDir("output");
		ElexisPDFGenerator generator = new ElexisPDFGenerator(billXmlFile, "450_tp_kvg");
		generator.printBill(AllTests.pluginRsc);
	}

	@Test
	public void generate450TGQRBill() {
		String billXmlFile = AllTests.getBillXmlFilePath("450_qr_tg_mvg_de");
		assertNotNull(billXmlFile);
		AllTests.setOutputDir("output");
		ElexisPDFGenerator generator = new ElexisPDFGenerator(billXmlFile, "450_qr_tg_mvg");
		generator.printQrBill(AllTests.pluginRsc);
	}

	@Test
	public void generate500TGQRBill() {
		String billXmlFile = AllTests.getBillXmlFilePath("500_qr_tg_vvg_de");
		assertNotNull(billXmlFile);
		AllTests.setOutputDir("output");
		ElexisPDFGenerator generator = new ElexisPDFGenerator(billXmlFile, "500_qr_tg_vvg");
		generator.printQrBill(AllTests.pluginRsc);
	}

	@Test
	public void generate500TPQRBill() {
		String billXmlFile = AllTests.getBillXmlFilePath("500_qr_tp_kvg_de");
		assertNotNull(billXmlFile);
		AllTests.setOutputDir("output");
		ElexisPDFGenerator generator = new ElexisPDFGenerator(billXmlFile, "500_qr_tp_kvg");
		generator.printQrBill(AllTests.pluginRsc);
	}

	@Test
	public void generate500TP12QRBill() {
		String billXmlFile = AllTests.getBillXmlFilePath("500_12qr_tp_kvg_de");
		assertNotNull(billXmlFile);
		AllTests.setOutputDir("output");
		ElexisPDFGenerator generator = new ElexisPDFGenerator(billXmlFile, "500_12qr_tp_kvg");
		generator.printQrBill(AllTests.pluginRsc);
	}
}
