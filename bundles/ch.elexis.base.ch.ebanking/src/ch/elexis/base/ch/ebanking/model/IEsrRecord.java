package ch.elexis.base.ch.ebanking.model;

import java.time.LocalDate;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.esr.ESRCode;
import ch.elexis.core.model.esr.ESRRejectCode;
import ch.rgw.tools.Money;

public interface IEsrRecord extends Identifiable, Deleteable {

	public LocalDate getDate();

	public LocalDate getImportDate();

	public LocalDate getProcessingDate();

	public LocalDate getBookedDate();

	public void setBookedDate(LocalDate value);

	public Money getAmount();

	public LocalDate getValutaDate();

	public String getFile();

	public ESRCode getCode();

	public ESRRejectCode getRejectCode();

	public IInvoice getInvoice();

	public void setInvoice(IInvoice iInvoice);

	public IPatient getPatient();

	public void setPatient(IPatient iPatient);

	public IContact getMandator();

	public void setMandator(IContact iContact);
}
