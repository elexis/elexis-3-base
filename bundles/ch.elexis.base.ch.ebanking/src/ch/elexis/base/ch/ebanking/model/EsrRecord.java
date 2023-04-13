package ch.elexis.base.ch.ebanking.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ch.elexis.core.jpa.entities.Invoice;
import ch.elexis.core.jpa.entities.Kontakt;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.AbstractIdModelAdapter;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.esr.ESRCode;
import ch.elexis.core.model.esr.ESRRejectCode;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.Money;

public class EsrRecord extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.EsrRecord>
		implements IEsrRecord {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // $NON-NLS-1$

	public EsrRecord(ch.elexis.core.jpa.entities.EsrRecord entity) {
		super(entity);

	}

	public String getDateString() {
		return formatter.format(getDate());
	}

	public void setDateString(String value) {
		setDate(LocalDate.from(formatter.parse(value)));
	}

	@Override
	public LocalDate getDate() {
		return getEntity().getDatum() != null ? getEntity().getDatum() : LocalDate.EPOCH;
	}

	public void setDate(LocalDate value) {
		getEntityMarkDirty().setDatum(value);
	}

	@Override
	public LocalDate getImportDate() {
		return getEntity().getEingelesen() != null ? getEntity().getEingelesen() : LocalDate.EPOCH;
	}

	public void setImportDate(LocalDate value) {
		getEntityMarkDirty().setEingelesen(value);
	}

	public String getImportDateString() {
		return formatter.format(getImportDate());
	}

	public void setImportDateString(String value) {
		setImportDate(LocalDate.from(formatter.parse(value)));
	}

	@Override
	public LocalDate getProcessingDate() {
		return getEntity().getVerarbeitet() != null ? getEntity().getVerarbeitet() : LocalDate.EPOCH;
	}

	public void setProcessingDate(LocalDate value) {
		getEntityMarkDirty().setVerarbeitet(value);
	}

	public String getProcessingDateString() {
		return formatter.format(getProcessingDate());
	}

	public void setProcessingDateString(String value) {
		setProcessingDate(LocalDate.from(formatter.parse(value)));
	}

	@Override
	public LocalDate getValutaDate() {
		return getEntity().getGutschrift() != null ? getEntity().getGutschrift() : LocalDate.EPOCH;
	}

	public void setValutaDate(LocalDate value) {
		getEntityMarkDirty().setGutschrift(value);
	}

	public String getValutaDateString() {
		return formatter.format(getValutaDate());
	}

	public void setValutaDateString(String value) {
		setValutaDate(LocalDate.from(formatter.parse(value)));
	}

	@Override
	public LocalDate getBookedDate() {
		return getEntity().getGebucht() != null ? getEntity().getGebucht() : LocalDate.EPOCH;
	}

	@Override
	public void setBookedDate(LocalDate value) {
		getEntityMarkDirty().setGebucht(value);
	}

	@Override
	public IInvoice getInvoice() {
		if (getEntity().getRechnung() != null) {
			return CoreModelServiceHolder.get().adapt(getEntity().getRechnung(), IInvoice.class).get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setInvoice(IInvoice value) {
		if (value != null) {
			getEntityMarkDirty().setRechnung(((AbstractIdModelAdapter<Invoice>) value).getEntity());
		} else {
			getEntityMarkDirty().setRechnung(null);
		}
	}

	@Override
	public IPatient getPatient() {
		if (getEntity().getPatient() != null) {
			return CoreModelServiceHolder.get().adapt(getEntity().getPatient(), IPatient.class).get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPatient(IPatient value) {
		if (value != null) {
			getEntityMarkDirty().setPatient(((AbstractIdModelAdapter<Kontakt>) value).getEntity());
		} else {
			getEntityMarkDirty().setPatient(null);
		}
	}

	@Override
	public IMandator getMandator() {
		if (getEntity().getPatient() != null) {
			return CoreModelServiceHolder.get().adapt(getEntity().getMandant(), IMandator.class).get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setMandator(IContact value) {
		if (value != null) {
			getEntityMarkDirty().setMandant(((AbstractIdModelAdapter<Kontakt>) value).getEntity());
		} else {
			getEntityMarkDirty().setMandant(null);
		}
	}

	@Override
	public ESRCode getCode() {
		return getEntity().getCode();
	}

	@Override
	public ESRRejectCode getRejectCode() {
		return getEntity().getRejectcode();
	}

	@Override
	public String getFile() {
		return getEntity().getFile();
	}

	@Override
	public Money getAmount() {
		return new Money(getEntity().getBetraginrp());
	}

	public void setAmount(Money value) {
		getEntityMarkDirty().setBetraginrp(value.getCents());
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IXid getXid(String domain) {
		// TODO Auto-generated method stub
		return null;
	}

}
