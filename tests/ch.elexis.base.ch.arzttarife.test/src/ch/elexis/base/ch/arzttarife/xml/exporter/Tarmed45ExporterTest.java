package ch.elexis.base.ch.arzttarife.xml.exporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.base.ch.arzttarife.test.TestData;
import ch.elexis.base.ch.arzttarife.test.TestData.TestSzenario;
import ch.elexis.base.ch.arzttarife.xml.exporter.Tarmed45Exporter.EsrType;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.services.holder.InvoiceServiceHolder;
import ch.elexis.data.Verrechnet;
import ch.fd.invoice450.request.BalanceTGType;
import ch.fd.invoice450.request.PatientAddressType;
import ch.fd.invoice450.request.RequestType;
import ch.fd.invoice450.request.VatRateType;
import ch.fd.invoice450.request.VatType;
import ch.rgw.tools.Money;

public class Tarmed45ExporterTest {

	@Test
	public void doExportVatTest() throws IOException {
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario);
		assertNotNull(szenario.getInvoices());
		assertFalse(szenario.getInvoices().isEmpty());
		Tarmed45Exporter exporter = new Tarmed45Exporter();
		List<IInvoice> invoices = szenario.getInvoices();
		Optional<IInvoice> vatInvoice = invoices.stream().filter(i -> i.getBilled().stream()
				.filter(b -> b.getExtInfo(Verrechnet.VATSCALE) != null).findFirst().isPresent()).findFirst();
		assertTrue(vatInvoice.isPresent());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertTrue(exporter.doExport(vatInvoice.get(), output, IRnOutputter.TYPE.ORIG));

		// ensure xsd conformity
		Tarmed45Validator validator = new Tarmed45Validator();
		List<String> errors = validator.validateRequest(new ByteArrayInputStream(output.toByteArray()));
		assertTrue(Arrays.toString(errors.toArray()), errors.isEmpty());

		// unmarshall and check vat values
		RequestType vatRequest = TarmedJaxbUtil
				.unmarshalInvoiceRequest450(new ByteArrayInputStream(output.toByteArray()));
		assertNotNull(vatRequest.getPayload().getBody().getTiersGarant());
		VatType vat = vatRequest.getPayload().getBody().getTiersGarant().getBalance().getVat();
		assertNotNull(vat);
		assertEquals(3, vat.getVatRate().size());
		for (VatRateType vatRate : vat.getVatRate()) {
			if (vatRate.getVatRate() == 0.0) {
				assertEquals(0.0, vatRate.getVat(), 0.01);
			} else {
				Double expectedVat = (vatRate.getAmount() / (100.0 + vatRate.getVatRate())) * vatRate.getVatRate();
				assertEquals(expectedVat, vatRate.getVat(), 0.01);
			}
		}
	}

	@Test
	public void doExportQrTest() throws IOException {
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario);
		assertNotNull(szenario.getInvoices());
		assertFalse(szenario.getInvoices().isEmpty());
		Tarmed45Exporter exporter = new Tarmed45Exporter();
		exporter.setEsrType(EsrType.esrQR);

		List<IInvoice> invoices = szenario.getInvoices();
		Optional<IInvoice> vatInvoice = invoices.stream().filter(i -> i.getBilled().stream()
				.filter(b -> b.getExtInfo(Verrechnet.VATSCALE) != null).findFirst().isPresent()).findFirst();
		assertTrue(vatInvoice.isPresent());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertTrue(exporter.doExport(vatInvoice.get(), output, IRnOutputter.TYPE.ORIG));

		// ensure xsd conformity
		Tarmed45Validator validator = new Tarmed45Validator();
		List<String> errors = validator.validateRequest(new ByteArrayInputStream(output.toByteArray()));
		assertTrue(Arrays.toString(errors.toArray()), errors.isEmpty());
	}

	@Test
	public void doExportPatientMobileTest() throws IOException {
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario);
		assertNotNull(szenario.getInvoices());
		assertFalse(szenario.getInvoices().isEmpty());
		Tarmed45Exporter exporter = new Tarmed45Exporter();

		List<IInvoice> invoices = szenario.getInvoices();
		Optional<IInvoice> mobileInvoice = invoices.stream()
				.filter(i -> StringUtils.isNotBlank(i.getCoverage().getPatient().getMobile())).findFirst();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertTrue(exporter.doExport(mobileInvoice.get(), output, IRnOutputter.TYPE.ORIG));

		RequestType invoiceRequest = TarmedJaxbUtil
				.unmarshalInvoiceRequest450(new ByteArrayInputStream(output.toByteArray()));
		assertNotNull(invoiceRequest);
		assertNotNull(invoiceRequest.getPayload().getBody().getTiersGarant().getPatient());
		PatientAddressType patient = invoiceRequest.getPayload().getBody().getTiersGarant().getPatient();
		assertEquals(2, patient.getPerson().getTelecom().getPhone().size());
		assertEquals("444-444 44 44", patient.getPerson().getTelecom().getPhone().get(0));
	}

	@Test
	public void doExportAmountsTest() throws IOException {
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario);
		assertNotNull(szenario.getInvoices());
		assertFalse(szenario.getInvoices().isEmpty());

		List<IInvoice> invoices = szenario.getInvoices();
		IInvoice invoice = invoices.get(0);

		Money openAmount = invoice.getOpenAmount();
		Money totalAmount = invoice.getTotalAmount();
		Money demandAmount = invoice.getDemandAmount();
		assertTrue(demandAmount.isZero());
		Money payedAmount = invoice.getPayedAmount();
		assertTrue(payedAmount.isZero());

		InvoiceServiceHolder.get().addPayment(invoice, new Money(-2000), "test demand");
		InvoiceServiceHolder.get().addPayment(invoice, new Money(-1000),
				ch.elexis.core.l10n.Messages.Rechnung_Mahngebuehr1);
		InvoiceServiceHolder.get().addPayment(invoice, new Money(500), "test payment");

		assertEquals(2500, invoice.getOpenAmount().subtractMoney(openAmount).getCents());
		assertEquals(500, invoice.getPayedAmount().getCents());
		assertEquals(0, invoice.getTotalAmount().subtractMoney(totalAmount).getCents());
		assertEquals(3000, invoice.getDemandAmount().getCents());

		Tarmed45Exporter exporter = new Tarmed45Exporter();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertTrue(exporter.doExport(invoice, output, IRnOutputter.TYPE.ORIG));

		RequestType invoiceRequest = TarmedJaxbUtil
				.unmarshalInvoiceRequest450(new ByteArrayInputStream(output.toByteArray()));
		assertNotNull(invoiceRequest);
		assertNotNull(invoiceRequest.getPayload().getBody().getTiersGarant().getBalance());
		BalanceTGType balance = invoiceRequest.getPayload().getBody().getTiersGarant().getBalance();
		assertEquals(invoice.getTotalAmount().doubleValue(), balance.getAmount(), 0.03);
		assertEquals(invoice.getOpenAmount().doubleValue(), balance.getAmountDue(), 0.03);
		assertEquals(invoice.getPayedAmount().doubleValue(), balance.getAmountPrepaid(), 0.03);
		assertEquals(invoice.getDemandAmount().doubleValue(), balance.getAmountReminder(), 0.03);
	}
}