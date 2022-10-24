package ch.elexis.base.ch.arzttarife.xml.update;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Optional;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.base.ch.arzttarife.test.TestData;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.fd.invoice450.request.RequestType;

public class XmlVersionUpdate44to45Test {

	private static IModelService coreModelService;

	@BeforeClass
	public static void beforeClass() {
		coreModelService = OsgiServiceUtil
				.getService(IModelService.class, "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)").get();

	}

	@AfterClass
	public static void afterClass() {
		OsgiServiceUtil.ungetService(coreModelService);
	}

	@Test
	public void testUpdate212() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("212", "/rsc/update/212.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "212", invoice);
		coreModelService.save(invoice);

		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getBody().getTiersPayant());
		assertEquals(2, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(5, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

//		printDocument(update.getAsJdomDocument((RequestType) updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	@Test
	public void testUpdate212_m1() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("212", "/rsc/update/212_m1.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "212", invoice);
		coreModelService.save(invoice);

		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getBody().getTiersPayant());
		assertNotNull(updatedRequest.getPayload().getReminder());
		assertEquals(2, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(5, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

//		printDocument(update.getAsJdomDocument((RequestType) updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	@Test
	public void testUpdate212_m2() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("212", "/rsc/update/212_m2.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "212", invoice);
		coreModelService.save(invoice);

		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getBody().getTiersPayant());
		assertNotNull(updatedRequest.getPayload().getReminder());
		assertEquals("2", updatedRequest.getPayload().getReminder().getReminderLevel());
		assertEquals(2, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(5, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

//		printDocument(update.getAsJdomDocument((RequestType) updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	@Test
	public void testUpdate213() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("213", "/rsc/update/213.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "213", invoice);
		coreModelService.save(invoice);
		
		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getBody().getTiersGarant());
		assertEquals(5, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(16, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

		// printDocument(update.getAsJdomDocument((RequestType)
		// updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	@Test
	public void testUpdate213_m1() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("213", "/rsc/update/213_m1.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "213", invoice);
		coreModelService.save(invoice);

		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getReminder());
		assertEquals(5, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(16, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

//		printDocument(update.getAsJdomDocument((RequestType) updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	@Test
	public void testUpdate213_m2() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("213", "/rsc/update/213_m2.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "213", invoice);
		coreModelService.save(invoice);

		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getReminder());
		assertEquals("2", updatedRequest.getPayload().getReminder().getReminderLevel());
		assertEquals(5, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(16, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

//		printDocument(update.getAsJdomDocument((RequestType) updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	@Test
	public void testUpdate213_m3() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("213", "/rsc/update/213_m3.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "213", invoice);
		coreModelService.save(invoice);

		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getReminder());
		assertEquals("3", updatedRequest.getPayload().getReminder().getReminderLevel());
		assertEquals(5, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(16, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

//		printDocument(update.getAsJdomDocument((RequestType) updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	@Test
	public void testUpdate214() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("214", "/rsc/update/214.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "214", invoice);
		coreModelService.save(invoice);

		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getBody().getTiersGarant());

		assertEquals("CHF", updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getCurrency());
		assertEquals(0.0, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountPrepaid(),
				0.0001);
		assertEquals(268.77, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmount(), 0.0001);
		assertEquals(268.75, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountDue(),
				0.0001);
		assertEquals(254.02, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountObligations(),
				0.0001);
		assertEquals(1.84, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getVat().getVat(),
				0.0001);
		assertEquals(3,
				updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getVat().getVatRate().size());

		assertEquals(4, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(18, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

//		printDocument(update.getAsJdomDocument((RequestType) updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	@Test
	public void testUpdate214_m1() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("214", "/rsc/update/214_m1.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "214", invoice);
		coreModelService.save(invoice);

		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getBody().getTiersGarant());
		assertNotNull(updatedRequest.getPayload().getReminder());
		assertEquals("1", updatedRequest.getPayload().getReminder().getReminderLevel());

		assertEquals("CHF", updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getCurrency());
		assertEquals(0.0, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountPrepaid(),
				0.0001);
		assertEquals(268.77, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmount(), 0.0001);
		assertEquals(278.75, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountDue(),
				0.0001);
		assertEquals(10, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountReminder(),
				0.0001);
		assertEquals(254.02, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountObligations(),
				0.0001);
		assertEquals(1.84, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getVat().getVat(),
				0.0001);
		assertEquals(3,
				updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getVat().getVatRate().size());

		assertEquals(4, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(18, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

//		printDocument(update.getAsJdomDocument((RequestType) updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	@Test
	public void testUpdate214_m2() throws IOException, JDOMException {
		IBlob blob = TestData.createTestInvoiceBlobWithXml("214", "/rsc/update/214_m2.xml");
		assertNotNull(blob);
		IInvoice invoice = coreModelService.create(IInvoice.class);
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setRemark("TEST");
		coreModelService.setEntityProperty("number", "214", invoice);
		coreModelService.save(invoice);

		XmlVersionUpdate44to45 update = new XmlVersionUpdate44to45(invoice);
		update.update();

		Optional<?> updatedModel = getExistingXmlModel(invoice, "4.5");
		assertTrue(updatedModel.isPresent());
		RequestType updatedRequest = (RequestType) updatedModel.get();
		assertNotNull(updatedRequest.getPayload().getBody().getTiersGarant());
		assertNotNull(updatedRequest.getPayload().getReminder());
		assertEquals("2", updatedRequest.getPayload().getReminder().getReminderLevel());

		assertEquals("CHF", updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getCurrency());
		assertEquals(0.0, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountPrepaid(),
				0.0001);
		assertEquals(268.77, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmount(), 0.0001);
		assertEquals(278.75, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountDue(),
				0.0001);
		assertEquals(10, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountReminder(),
				0.0001);
		assertEquals(254.02, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getAmountObligations(),
				0.0001);
		assertEquals(1.84, updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getVat().getVat(),
				0.0001);
		assertEquals(3,
				updatedRequest.getPayload().getBody().getTiersGarant().getBalance().getVat().getVatRate().size());

		assertEquals(4, updatedRequest.getPayload().getBody().getTreatment().getDiagnosis().size());
		assertEquals(18, updatedRequest.getPayload().getBody().getServices().getServiceExOrService().size());

//		printDocument(update.getAsJdomDocument((RequestType) updatedModel.get()).get(), System.out);
		coreModelService.remove(invoice);
		coreModelService.remove(blob);
	}

	private void printDocument(Document result, OutputStream out) throws IOException {
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(result, out);
	}

	private Optional<?> getExistingXmlModel(IInvoice invoice, String version) {
		IBlob blob = CoreModelServiceHolder.get().load(XMLExporter.PREFIX + invoice.getNumber(), IBlob.class)
				.orElse(null);
		if (blob != null && blob.getStringContent() != null && !blob.getStringContent().isEmpty()) {
			if ("4.5".equals(version)) {
				ch.fd.invoice450.request.RequestType invoiceRequest = TarmedJaxbUtil
						.unmarshalInvoiceRequest450(new ByteArrayInputStream(blob.getStringContent().getBytes()));
				return Optional.ofNullable(invoiceRequest);
			}
		}
		return Optional.empty();
	}
}
