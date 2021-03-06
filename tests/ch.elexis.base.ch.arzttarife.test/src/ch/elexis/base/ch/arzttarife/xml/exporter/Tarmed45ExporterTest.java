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

import org.junit.Test;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.base.ch.arzttarife.test.TestData;
import ch.elexis.base.ch.arzttarife.test.TestData.TestSzenario;
import ch.elexis.base.ch.arzttarife.xml.exporter.Tarmed45Exporter.EsrType;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.data.Verrechnet;
import ch.fd.invoice450.request.RequestType;
import ch.fd.invoice450.request.VatRateType;
import ch.fd.invoice450.request.VatType;

public class Tarmed45ExporterTest {
	
	@Test
	public void doExportVatTest() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario);
		assertNotNull(szenario.getInvoices());
		assertFalse(szenario.getInvoices().isEmpty());
		Tarmed45Exporter exporter = new Tarmed45Exporter();
		List<IInvoice> invoices = szenario.getInvoices();
		Optional<IInvoice> vatInvoice = invoices.stream()
			.filter(i -> i.getBilled().stream()
				.filter(b -> b.getExtInfo(Verrechnet.VATSCALE) != null).findFirst().isPresent())
			.findFirst();
		assertTrue(vatInvoice.isPresent());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertTrue(exporter.doExport(vatInvoice.get(), output, IRnOutputter.TYPE.ORIG));
		
		// ensure xsd conformity
		Tarmed45Validator validator = new Tarmed45Validator();
		List<String> errors =
			validator.validateRequest(new ByteArrayInputStream(output.toByteArray()));
		assertTrue(Arrays.toString(errors.toArray()), errors.isEmpty());
		
		// unmarshall and check vat values
		RequestType vatRequest =
			TarmedJaxbUtil
				.unmarshalInvoiceRequest450(new ByteArrayInputStream(output.toByteArray()));
		assertNotNull(vatRequest.getPayload().getBody().getTiersGarant());
		VatType vat = vatRequest.getPayload().getBody().getTiersGarant().getBalance().getVat();
		assertNotNull(vat);
		assertEquals(3, vat.getVatRate().size());
		for (VatRateType vatRate : vat.getVatRate()) {
			if (vatRate.getVatRate() == 0.0) {
				assertEquals(0.0, vatRate.getVat(), 0.01);
			} else {
				Double expectedVat =
					(vatRate.getAmount() / (100.0 + vatRate.getVatRate())) * vatRate.getVatRate();
				assertEquals(expectedVat, vatRate.getVat(), 0.01);
			}
		}
	}
	
	@Test
	public void doExportQrTest() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario);
		assertNotNull(szenario.getInvoices());
		assertFalse(szenario.getInvoices().isEmpty());
		Tarmed45Exporter exporter = new Tarmed45Exporter();
		exporter.setEsrType(EsrType.esrQR);
		
		List<IInvoice> invoices = szenario.getInvoices();
		Optional<IInvoice> vatInvoice = invoices.stream()
			.filter(i -> i.getBilled().stream()
				.filter(b -> b.getExtInfo(Verrechnet.VATSCALE) != null).findFirst().isPresent())
			.findFirst();
		assertTrue(vatInvoice.isPresent());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertTrue(exporter.doExport(vatInvoice.get(), output, IRnOutputter.TYPE.ORIG));
		
		// ensure xsd conformity
		Tarmed45Validator validator = new Tarmed45Validator();
		List<String> errors =
			validator.validateRequest(new ByteArrayInputStream(output.toByteArray()));
		assertTrue(Arrays.toString(errors.toArray()), errors.isEmpty());
	}
}