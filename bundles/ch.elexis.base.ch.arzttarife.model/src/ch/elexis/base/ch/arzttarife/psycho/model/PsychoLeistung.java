package ch.elexis.base.ch.arzttarife.psycho.model;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.VatInfo;

public class PsychoLeistung extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.PsychoLeistung>
		implements Identifiable, IPsychoLeistung {

	public static final String STS_CLASS = "ch.elexis.data.PsychoLeistung";

	private static IBillableOptifier<PsychoLeistung> optifier;
	private IBillableVerifier verifier;

	public PsychoLeistung(ch.elexis.core.jpa.entities.PsychoLeistung entity) {
		super(entity);
		verifier = new DefaultVerifier();
	}

	@Override
	public synchronized IBillableOptifier<PsychoLeistung> getOptifier() {
		if (optifier == null) {
			optifier = new AbstractOptifier<PsychoLeistung>(CoreModelServiceHolder.get(),
					ContextServiceHolder.get()) {

				@Override
				protected void setPrice(PsychoLeistung billable, IBilled billed) {
					billed.setFactor(1.0);
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
			};
		}
		return optifier;
	}

	@Override
	public IBillableVerifier getVerifier() {
		return verifier;
	}

	@Override
	public VatInfo getVatInfo() {
		return VatInfo.VAT_CH_ISTREATMENT;
	}

	@Override
	public String getCodeSystemCode() {
		return "581";
	}

	@Override
	public String getCodeSystemName() {
		return ch.elexis.core.jpa.entities.PsychoLeistung.CODESYSTEM_NAME;
	}

	@Override
	public String getCode() {
		return getEntity().getCode();
	}

	@Override
	public void setCode(String value) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getText() {
		return getEntity().getText();
	}

	@Override
	public void setText(String value) {
		getEntityMarkDirty().setCodeText(value);

	}

	@Override
	public LocalDate getValidFrom() {
		return getEntity().getValidFrom();
	}

	@Override
	public void setValidFrom(LocalDate value) {
		getEntityMarkDirty().setValidFrom(value);
	}

	@Override
	public LocalDate getValidTo() {
		return getEntity().getValidUntil();
	}

	@Override
	public void setValidTo(LocalDate value) {
		getEntityMarkDirty().setValidUntil(value);
	}

	@Override
	public String getTP() {
		return getEntity().getTp();
	}

	@Override
	public void setTP(String value) {
		getEntityMarkDirty().setTp(value);
	}

	@Override
	public String getDescription() {
		return getEntity().getDescription();
	}

	@Override
	public void setDescription(String value) {
		getEntityMarkDirty().setDescription(value);
	}

	@Override
	public String getLabel() {
		return getCode() + StringUtils.SPACE + getText();
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
	public String getLimitations() {
		return getEntity().getLimitations();
	}

	@Override
	public void setLimitations(String value) {
		getEntityMarkDirty().setLimitations(value);
	}

	@Override
	public String getExclusions() {
		return getEntity().getExclusions();
	}

	@Override
	public void setExclusions(String value) {
		getEntityMarkDirty().setExclusions(value);
	}
}
