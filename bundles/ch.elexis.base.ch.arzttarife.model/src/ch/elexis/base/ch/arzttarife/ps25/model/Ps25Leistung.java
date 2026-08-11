package ch.elexis.base.ch.arzttarife.ps25.model;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.ArzttarifeConstants;
import ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;

public class Ps25Leistung extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.Ps25Leistung>
		implements IPs25Leistung {

	public static final String STS_CLASS = "ch.elexis.data.Ps25Leistung";

	private static IBillableOptifier<Ps25Leistung> optifier;
	private IBillableVerifier verifier;

	public Ps25Leistung(ch.elexis.core.jpa.entities.Ps25Leistung entity) {
		super(entity);
		verifier = new DefaultVerifier();
	}

	@Override
	public synchronized IBillableOptifier<Ps25Leistung> getOptifier() {
		if (optifier == null) {
			optifier = new AbstractOptifier<Ps25Leistung>(CoreModelServiceHolder.get(), ContextServiceHolder.get()) {
				@Override
				protected void setPrice(Ps25Leistung billable, IBilled billed) {
					Optional<IBillingSystemFactor> factor = getFactor(billed.getEncounter());
					if (factor.isPresent()) {
						billed.setFactor(factor.get().getFactor());
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
					return BillingServiceHolder.get()
							.getBillingSystemFactor(ArzttarifeConstants.PS25_MULTIPLICATOR_NAME, encounter.getDate());
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
		return ch.elexis.core.jpa.entities.Ps25Leistung.CODESYSTEM_NAME;
	}

	@Override
	public String getCode() {
		return getEntity().getCode();
	}

	@Override
	public void setCode(String value) {
		getEntityMarkDirty().setCode(value);
	}

	@Override
	public String getText() {
		return getEntity().getText();
	}

	@Override
	public void setText(String value) {
		getEntityMarkDirty().setMehrleistung(value);
	}

	@Override
	public LocalDate getValidFrom() {
		return getEntity().getValidFrom();
	}

	@Override
	public LocalDate getValidTo() {
		return getEntity().getValidUntil();
	}

	@Override
	public String getTP() {
		return getEntity().getTaxpunkte();
	}

	@Override
	public void setTP(String value) {
		getEntityMarkDirty().setTaxpunkte(value);
	}

	@Override
	public String getSubChapter() {
		return getEntity().getUnterkapitel();
	}

	@Override
	public void setSubChapter(String value) {
		getEntityMarkDirty().setUnterkapitel(value);
	}

	@Override
	public String getChapter() {
		return getEntity().getFachgebietKapitel();
	}

	@Override
	public void setChapter(String value) {
		getEntityMarkDirty().setFachgebietKapitel(value);
	}

	@Override
	public String getHonorarEmpfaenger() {
		return getEntity().getHonorarEmpfaenger();
	}

	@Override
	public void setHonorarEmpfaenger(String value) {
		getEntityMarkDirty().setHonorarEmpfaenger(value);
	}

	@Override
	public String getMehrleistungBei() {
		return getEntity().getMehrleistungBei();
	}

	@Override
	public void setMehrleistungBei(String value) {
		getEntityMarkDirty().setMehrleistungBei(value);
	}

	@Override
	public String getSpezifikation() {
		return getEntity().getSpezifikation();
	}

	@Override
	public void setSpezifikation(String value) {
		getEntityMarkDirty().setSpezifikation(value);
	}

	@Override
	public String getAnwendungsRegeln() {
		return getEntity().getAnwendungsregeln();
	}

	@Override
	public void setAnwendungsRegeln(String value) {
		getEntityMarkDirty().setAnwendungsregeln(value);
	}

	@Override
	public String getStufe() {
		return getEntity().getStufe();
	}

	@Override
	public void setStufe(String value) {
		getEntityMarkDirty().setStufe(value);
	}

	@Override
	public String getMoeglicheKombination() {
		return getEntity().getMoeglicheKombination();
	}

	@Override
	public void setMoeglicheKombination(String value) {
		getEntity().setMoeglicheKombination(value);
	}

	@Override
	public String getMehrleistungsTyp() {
		return getEntity().getMehrleistungstyp();
	}

	@Override
	public void setMehrleistungsTyp(String value) {
		getEntityMarkDirty().setMehrleistungstyp(value);
	}

	@Override
	public String getMehrleistung() {
		return getEntity().getMehrleistung();
	}

	@Override
	public void setMehrleistung(String value) {
		getEntityMarkDirty().setMehrleistung(value);
	}

	@Override
	public String getLabel() {
		return getCode() + " - " + getChapter()
				+ (StringUtils.isNotBlank(getSubChapter()) ? ", " + getSubChapter() : StringUtils.EMPTY) + ", "
				+ StringUtils.SPACE + getText();
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
