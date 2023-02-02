package ch.elexis.base.ch.arzttarife.complementary.model;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung;
import ch.elexis.base.ch.arzttarife.model.service.ConfigServiceHolder;
import ch.elexis.base.ch.arzttarife.model.service.ContextServiceHolder;
import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.prefs.PreferenceConstants;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.holder.XidServiceHolder;

public class ComplementaryLeistung
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.ComplementaryLeistung>
		implements Identifiable, IComplementaryLeistung {

	public static final String STS_CLASS = "ch.elexis.data.ComplementaryLeistung";

	private static IBillableOptifier<ComplementaryLeistung> optifier;
	private IBillableVerifier verifier;

	public ComplementaryLeistung(ch.elexis.core.jpa.entities.ComplementaryLeistung entity) {
		super(entity);
		verifier = new DefaultVerifier();
	}

	@Override
	public IBillableOptifier<ComplementaryLeistung> getOptifier() {
		if (optifier == null) {
			optifier = new AbstractOptifier<ComplementaryLeistung>(CoreModelServiceHolder.get(),
					ContextServiceHolder.get().get()) {

				@Override
				protected void setPrice(ComplementaryLeistung billable, IBilled billed) {
					// ignore billing system factor
					billed.setFactor(1.0);
					int points = 0;
					// configured hourly wage, or fixed value, in cents
					if (billable.isFixedValueSet()) {
						points = billable.getFixedValue();
					} else {
						points = getHourlyWage() / 12;
					}
					billed.setPoints(points);
				}

				private int getHourlyWage() {
					if (ContextServiceHolder.get().isPresent()) {
						Optional<IMandator> activeMandator = ContextServiceHolder.get().get().getActiveMandator();
						if (activeMandator.isPresent()) {
							if (ConfigServiceHolder.get().isPresent()) {
								String wageString = ConfigServiceHolder.get().get().get(activeMandator.get(),
										PreferenceConstants.COMPLEMENTARY_HOURLY_WAGE, "0");
								return Integer.valueOf(wageString);
							}
						}
					}
					LoggerFactory.getLogger(getClass())
							.warn("Could not get active mandator, billing without hourly wage");
					return 0;
				}

				@Override
				public Optional<IBillingSystemFactor> getFactor(IEncounter encounter) {
					return Optional.empty();
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
	public String getCodeSystemCode() {
		return "590";
	}

	@Override
	public String getCodeSystemName() {
		return ch.elexis.core.jpa.entities.ComplementaryLeistung.CODESYSTEM_NAME;
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
		return getEntity().getCodeText();
	}

	@Override
	public void setText(String value) {
		getEntity().setCodeText(value);
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
	public String getChapter() {
		return getEntity().getChapter();
	}

	@Override
	public void setChapter(String value) {
		getEntity().setChapter(value);
	}

	@Override
	public int getFixedValue() {
		return getEntity().getFixedValue();
	}

	@Override
	public void setFixedValue(int value) {
		getEntity().setFixedValue(value);
	}

	@Override
	public boolean isFixedValueSet() {
		return getFixedValue() != 0;
	}

	@Override
	public String getLabel() {
		return "(" + getCode() + ") " + getText();
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
	public void setId(String id) {
		getEntityMarkDirty().setId(id);
	}

	@Override
	public LocalDate getValidFrom() {
		return getEntity().getValidFrom();
	}

	@Override
	public LocalDate getValidTo() {
		return getEntity().getValidTo();
	}

	@Override
	public void setValidFrom(LocalDate value) {
		getEntityMarkDirty().setValidFrom(value);
	}

	@Override
	public void setValidTo(LocalDate value) {
		getEntityMarkDirty().setValidTo(value);
	}
}
