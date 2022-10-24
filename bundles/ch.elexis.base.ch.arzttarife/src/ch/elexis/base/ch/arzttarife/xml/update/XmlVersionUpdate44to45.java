package ch.elexis.base.ch.arzttarife.xml.update;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.fd.invoice440.request.BalanceType;
import ch.fd.invoice440.request.RecordDrugType;
import ch.fd.invoice440.request.RecordTarmedType;
import ch.fd.invoice450.request.BalanceTGType;
import ch.fd.invoice450.request.BalanceTPType;
import ch.fd.invoice450.request.ServiceExType;
import ch.fd.invoice450.request.ServiceType;
import ch.fd.invoice450.request.VatRateType;
import ch.fd.invoice450.request.VatType;
import ch.fd.invoice450.request.XtraDrugType;

public class XmlVersionUpdate44to45 {

	private IInvoice invoice;

	public XmlVersionUpdate44to45(IInvoice invoice) {
		this.invoice = invoice;
	}

	public void update() {
		try {
			Optional<?> model44 = getExistingXmlModel(invoice, "4.4");
			Optional<?> model45 = getExistingXmlModel(invoice, "4.5");
			if (model44.isPresent() && model45.isPresent()) {
				LoggerFactory.getLogger(getClass())
						.info("Updating tarmed xml 4.4 to 4.5 of invoice [" + invoice.getNumber() + "]");

				updateBalance((ch.fd.invoice440.request.RequestType) model44.get(),
						(ch.fd.invoice450.request.RequestType) model45.get());

				updateServices((ch.fd.invoice440.request.RequestType) model44.get(),
						(ch.fd.invoice450.request.RequestType) model45.get());

				// save updated xml
				setExistingXml(invoice,
						getAsJdomDocument((ch.fd.invoice450.request.RequestType) model45.get()).orElse(null));
			} else {
				if (model44.isEmpty()) {
					LoggerFactory.getLogger(getClass())
							.error("Could not load tarmed 4.4 model of invoice [" + invoice.getNumber() + "]");
				}
				if (model45.isEmpty()) {
					LoggerFactory.getLogger(getClass())
							.error("Could not load tarmed 4.5 model of invoice [" + invoice.getNumber() + "]");
				}
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass())
					.error("Error updating tarmed 4.4 model of invoice [" + invoice.getNumber() + "]", e);
		}
	}

	private void updateBalance(ch.fd.invoice440.request.RequestType model44,
			ch.fd.invoice450.request.RequestType model45) {
		BalanceType balance44 = model44.getPayload().getBody().getBalance();

		if (model44.getPayload().getBody().getTiersGarant() != null) {
			BalanceTGType balanceTGType = new BalanceTGType();

			balanceTGType.setCurrency(balance44.getCurrency());
			balanceTGType.setAmountPrepaid(balance44.getAmountPrepaid());
			if (model44.getPayload().getReminder() != null) {
				balanceTGType.setAmountReminder(balance44.getAmountReminder());
			}
			balanceTGType.setAmount(balance44.getAmount());
			balanceTGType.setAmountDue(balance44.getAmountDue());
			balanceTGType.setAmountObligations(balance44.getAmountObligations());
			balanceTGType.setVat(updateVat(balance44.getVat()));

			model45.getPayload().getBody().getTiersGarant().setBalance(balanceTGType);
		} else if (model44.getPayload().getBody().getTiersPayant() != null) {
			BalanceTPType balanceTPType = new BalanceTPType();

			balanceTPType.setCurrency(balance44.getCurrency());
			if (model44.getPayload().getReminder() != null) {
				balanceTPType.setAmountReminder(balance44.getAmountReminder());
			}
			balanceTPType.setAmount(balance44.getAmount());
			balanceTPType.setAmountDue(balance44.getAmountDue());
			balanceTPType.setAmountObligations(balance44.getAmountObligations());
			balanceTPType.setVat(updateVat(balance44.getVat()));

			model45.getPayload().getBody().getTiersPayant().setBalance(balanceTPType);
		}
	}

	private VatType updateVat(ch.fd.invoice440.request.VatType vat) {
		VatType vatType = new VatType();

		if (StringUtils.isNotBlank(vat.getVatNumber())) {
			vatType.setVatNumber(vat.getVatNumber());
		}
		vatType.setVat(vat.getVat());
		for (ch.fd.invoice440.request.VatRateType rate : vat.getVatRate()) {
			VatRateType vatRateType = new VatRateType();

			vatRateType.setVatRate(rate.getVatRate());
			vatRateType.setAmount(rate.getAmount());
			vatRateType.setVat(rate.getVat());
			vatType.getVatRate().add(vatRateType);
		}
		return vatType;
	}

	private void updateServices(ch.fd.invoice440.request.RequestType model44,
			ch.fd.invoice450.request.RequestType model45) {
		for (Object record : model44.getPayload().getBody().getServices().getRecordTarmedOrRecordDrgOrRecordLab()) {
			if (record instanceof RecordTarmedType) {
				ServiceExType serviceExType = new ServiceExType();
				serviceExType.setTreatment("ambulatory");
				serviceExType.setBillingRole("both");
				serviceExType.setMedicalRole("self_employed");

				serviceExType.setRefCode(((RecordTarmedType) record).getRefCode());
				serviceExType.setBodyLocation(((RecordTarmedType) record).getBodyLocation());
				serviceExType.setUnitMt(((RecordTarmedType) record).getUnitMt());
				serviceExType.setUnitFactorMt(((RecordTarmedType) record).getUnitFactorMt());
				serviceExType.setScaleFactorMt(((RecordTarmedType) record).getScaleFactorMt());
				serviceExType.setExternalFactorMt(((RecordTarmedType) record).getExternalFactorMt());
				serviceExType.setAmountMt(((RecordTarmedType) record).getAmountMt());
				
				serviceExType.setUnitTt(((RecordTarmedType) record).getUnitTt());
				serviceExType.setUnitFactorTt(((RecordTarmedType) record).getUnitFactorTt());
				serviceExType.setScaleFactorTt(((RecordTarmedType) record).getScaleFactorTt());
				serviceExType.setExternalFactorTt(((RecordTarmedType) record).getExternalFactorTt());
				serviceExType.setAmountTt(((RecordTarmedType) record).getAmountTt());

				serviceExType.setAmount(((RecordTarmedType) record).getAmount());
				serviceExType.setVatRate(((RecordTarmedType) record).getVatRate());
				serviceExType.setRemark(((RecordTarmedType) record).getRemark());

				serviceExType.setTariffType(((RecordTarmedType) record).getTariffType());
				serviceExType.setCode(((RecordTarmedType) record).getCode());
				serviceExType.setQuantity(((RecordTarmedType) record).getQuantity());
				serviceExType.setSession(((RecordTarmedType) record).getSession());
				serviceExType.setName(((RecordTarmedType) record).getName());
				serviceExType.setDateBegin(((RecordTarmedType) record).getDateBegin());
				serviceExType.setProviderId(((RecordTarmedType) record).getProviderId());
				serviceExType.setResponsibleId(((RecordTarmedType) record).getResponsibleId());
				serviceExType.setObligation(((RecordTarmedType) record).isObligation());

				serviceExType.setRecordId(((RecordTarmedType) record).getRecordId());
				model45.getPayload().getBody().getServices().getServiceExOrService().add(serviceExType);
			} else {
				ServiceType serviceType = new ServiceType();

				serviceType.setAmount(getPropDouble("amount", record));
				serviceType.setVatRate(getPropDouble("vatRate", record));
				serviceType.setUnit(getPropDouble("unit", record));
				serviceType.setUnitFactor(getPropDouble("unitFactor", record));
				serviceType.setTariffType(getPropString("tariffType", record));
				serviceType.setCode(getPropString("code", record));
				serviceType.setQuantity(getPropDouble("quantity", record));
				serviceType.setSession(getPropInteger("session", record));
				serviceType.setName(getPropString("name", record));
				serviceType.setProviderId(getPropString("providerId", record));
				serviceType.setResponsibleId(getPropString("responsibleId", record));
				serviceType.setObligation(getPropBoolean("obligation", record));

				serviceType.setDateBegin(getPropDate("dateBegin", record));

				if (record instanceof RecordDrugType) {
					XtraDrugType drugType = new XtraDrugType();
					if (((RecordDrugType) record).getXtraDrug() != null) {
						drugType.setIndicated(((RecordDrugType) record).getXtraDrug().isIndicated());
					} else {
						drugType.setIndicated(false);
					}
					serviceType.setXtraDrug(drugType);
				}

				serviceType.setRecordId(getPropInteger("recordId", record));
				model45.getPayload().getBody().getServices().getServiceExOrService().add(serviceType);
			}
		}
	}

	private Boolean getPropBoolean(String string, Object record) {
		try {
			return Boolean.parseBoolean(BeanUtils.getProperty(record, string));
		} catch (NumberFormatException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting double value [" + string + "]", e);
			throw new IllegalStateException("Error getting double value [" + string + "]");
		}
	}

	private XMLGregorianCalendar getPropDate(String string, Object record) {
		try {
			String value = BeanUtils.getProperty(record, string);
			// 2022-10-04T00:00:00
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
		} catch (DatatypeConfigurationException | NumberFormatException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting double value [" + string + "]", e);
			throw new IllegalStateException("Error getting double value [" + string + "]");
		}
	}

	private Integer getPropInteger(String string, Object record) {
		try {
			return Integer.parseInt(BeanUtils.getProperty(record, string));
		} catch (NumberFormatException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting double value [" + string + "]", e);
			throw new IllegalStateException("Error getting double value [" + string + "]");
		}
	}

	private Double getPropDouble(String string, Object record) {
		try {
			return Double.parseDouble(BeanUtils.getProperty(record, string));
		} catch (NumberFormatException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting double value [" + string + "]", e);
			throw new IllegalStateException("Error getting double value [" + string + "]");
		}
	}

	private String getPropString(String string, Object record) {
		try {
			return BeanUtils.getProperty(record, string);
		} catch (NumberFormatException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting double value [" + string + "]", e);
			throw new IllegalStateException("Error getting double value [" + string + "]");
		}
	}

	private Optional<?> getExistingXmlModel(IInvoice invoice, String version) {
		IBlob blob = CoreModelServiceHolder.get().load(XMLExporter.PREFIX + invoice.getNumber(), IBlob.class)
				.orElse(null);
		if (blob != null && blob.getStringContent() != null && !blob.getStringContent().isEmpty()) {
			if ("4.5".equals(version)) {
				ch.fd.invoice450.request.RequestType invoiceRequest = TarmedJaxbUtil
						.unmarshalInvoiceRequest450(new ByteArrayInputStream(blob.getStringContent().getBytes()));
				return Optional.ofNullable(invoiceRequest);
			} else if ("4.4".equals(version)) {
				ch.fd.invoice440.request.RequestType invoiceRequest = TarmedJaxbUtil
						.unmarshalInvoiceRequest440(new ByteArrayInputStream(blob.getStringContent().getBytes()));
				return Optional.ofNullable(invoiceRequest);
			}
		}
		return Optional.empty();
	}

	private void setExistingXml(IInvoice invoice, Document document) throws IOException {
		StringWriter stringWriter = new StringWriter();
		XMLOutputter xout = new XMLOutputter(Format.getCompactFormat());
		xout.output(document, stringWriter);
		IBlob blob = CoreModelServiceHolder.get().load(XMLExporter.PREFIX + invoice.getNumber(), IBlob.class)
				.orElseGet(() -> {
			IBlob newBlob = CoreModelServiceHolder.get().create(IBlob.class);
					newBlob.setId(XMLExporter.PREFIX + invoice.getNumber());
			return newBlob;
		});
		blob.setStringContent(stringWriter.toString());
		CoreModelServiceHolder.get().save(blob);
	}

	protected Optional<Document> getAsJdomDocument(ch.fd.invoice450.request.RequestType request) {
		if (request != null) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			TarmedJaxbUtil.marshallInvoiceRequest(request, outputStream);
			return getAsJdomDocument(outputStream);
		}
		return Optional.empty();
	}

	private Optional<Document> getAsJdomDocument(ByteArrayOutputStream outputStream) {
		if (outputStream != null) {
			SAXBuilder builder = new SAXBuilder();
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
				return Optional.of(builder.build(inputStream));
			} catch (IOException | JDOMException e) {
				LoggerFactory.getLogger(getClass()).error("Error loading as jdom document", e);
			}
		}
		return Optional.empty();
	}
}
