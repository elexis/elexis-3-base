package ch.elexis.base.ch.arzttarife.tarmedallowance.model;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.model.service.ContextServiceHolder;
import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance;
import ch.elexis.core.jpa.entities.TarmedPauschalen;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.rgw.tools.Money;

public class TarmedAllowance extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedPauschalen>
		implements Identifiable, ITarmedAllowance {

	public static final String STS_CLASS = "ch.elexis.data.TarmedPauschalen";

	private static IBillableOptifier<TarmedAllowance> optifier;
	private IBillableVerifier verifier;

	public TarmedAllowance(TarmedPauschalen entity) {
		super(entity);
		verifier = new DefaultVerifier();
	}

	@Override
	public IBillableOptifier<TarmedAllowance> getOptifier() {
		if (optifier == null) {
			optifier = new AbstractOptifier<TarmedAllowance>(CoreModelServiceHolder.get(),
					ContextServiceHolder.get().get()) {

				@Override
				protected void setPrice(TarmedAllowance billable, IBilled billed) {
					billed.setFactor(1.0);
					billed.setPoints(billable.getPrice(billed.getEncounter()).getCents());
				}
			};
		}
		return optifier;
	}

	protected Money getPrice(IEncounter iEncounter) {
		if (StringUtils.isNotBlank(getEntity().getTp())) {
			try {
				int cents = Integer.parseInt(getEntity().getTp());
				return new Money(cents);
			} catch (NumberFormatException e) {
				LoggerFactory.getLogger(getClass())
						.warn("Ignoring non integer tp value [" + getEntity().getTp() + "] of [" + this + "]");
			}
		}
		return new Money();
	}

	@Override
	public IBillableVerifier getVerifier() {
		return verifier;
	}

	@Override
	public String getCodeSystemName() {
		return TarmedPauschalen.CODESYSTEM_NAME;
	}

	@Override
	public String getCodeSystemCode() {
		return "003";
	}

	@Override
	public String getCode() {
		return getEntity().getCode();
	}

	@Override
	public void setCode(String value) {
		getEntity().setCode(value);
	}

	@Override
	public String getText() {
		return getEntity().getText();
	}

	@Override
	public void setText(String value) {
		getEntity().setText(value);
	}

	@Override
	public LocalDate getValidFrom() {
		return getEntity().getValidFrom();
	}

	@Override
	public void setValidFrom(LocalDate value) {
		// TODO Auto-generated method stub

	}

	@Override
	public LocalDate getValidTo() {
		return getEntity().getValidTo();
	}

	@Override
	public void setValidTo(LocalDate value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getChapter() {
		return getEntity().getChapter();
	}

	@Override
	public void setChapter(String value) {
		getEntity().setChapter(value);
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}

	@Override
	public IXid getXid(String domain) {
		return XidServiceHolder.get().getXid(this, domain);
	}

	@Override
	public String getLabel() {
		return "(" + getCode() + ") " + getChapter() + " - " + getText();
	}

	@Override
	public String getTP() {
		return getEntity().getTp();
	}

	@Override
	public void setTP(String value) {
		getEntityMarkDirty().setTp(value);
	}
}
