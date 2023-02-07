package ch.elexis.base.ch.arzttarife.physio.model;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;

public class PhysioLeistung extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.PhysioLeistung>
		implements Identifiable, IPhysioLeistung {

	public static final String STS_CLASS = "ch.elexis.data.PhysioLeistung";

	private static IBillableOptifier<PhysioLeistung> optifier;
	private IBillableVerifier verifier;

	public PhysioLeistung(ch.elexis.core.jpa.entities.PhysioLeistung entity) {
		super(entity);
		verifier = new DefaultVerifier();
	}

	@Override
	public synchronized IBillableOptifier<PhysioLeistung> getOptifier() {
		if (optifier == null) {
			optifier = new AbstractOptifier<PhysioLeistung>(CoreModelServiceHolder.get(), ContextServiceHolder.get()) {

				@Override
				protected void setPrice(PhysioLeistung billable, IBilled billed) {
					Optional<IBillingSystemFactor> billingFactor = getFactor(billed.getEncounter());
					if (billingFactor.isPresent()) {
						billed.setFactor(billingFactor.get().getFactor());
					} else {
						billed.setFactor(1.0);
					}
					int points = 0;
					if (billable.getTP() != null) {
						try {
							points = Integer.valueOf(billable.getTP());
						} catch (NumberFormatException ne) {
							// ignore ...
						}
					}
					billed.setPoints(points);
				}

				@Override
				public Optional<IBillingSystemFactor> getFactor(IEncounter encounter) {
					return BillingServiceHolder.get().getBillingSystemFactor(
							encounter.getCoverage().getBillingSystem().getName(), encounter.getDate());
				}
			};
		}
		return optifier;
	}

	@Override
	public IBillableVerifier getVerifier() {
		return verifier;
	}

	@Override
	public String getCodeSystemName() {
		return ch.elexis.core.jpa.entities.PhysioLeistung.CODESYSTEM_NAME;
	}

	@Override
	public String getCodeSystemCode() {
		return "311";
	}

	@Override
	public String getCode() {
		return getZiffer();
	}

	@Override
	public void setCode(String value) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getText() {
		return getEntity().getTitel();
	}

	@Override
	public void setText(String value) {
		getEntity().setTitel(value);

	}

	@Override
	public LocalDate getValidFrom() {
		return getEntity().getValidFrom();
	}

	@Override
	public void setValidFrom(LocalDate value) {
		getEntity().setValidFrom(value);
	}

	@Override
	public LocalDate getValidTo() {
		return getEntity().getValidUntil();
	}

	@Override
	public void setValidTo(LocalDate value) {
		getEntity().setValidUntil(value);
	}

	@Override
	public String getTP() {
		return getEntity().getTp();
	}

	@Override
	public void setTP(String value) {
		getEntity().setTp(value);
	}

	@Override
	public String getZiffer() {
		return getEntity().getZiffer();
	}

	@Override
	public void setZiffer(String value) {
		getEntity().setZiffer(value);
	}

	@Override
	public String getDescription() {
		return getEntity().getDescription();
	}

	@Override
	public void setDescription(String value) {
		getEntity().setDescription(value);
	}

	@Override
	public String getLabel() {
		return getZiffer() + StringUtils.SPACE + getText();
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}

	@Override
	public IXid getXid(String domain) {
		return XidServiceHolder.get().getXid(this, domain);
	}
}
