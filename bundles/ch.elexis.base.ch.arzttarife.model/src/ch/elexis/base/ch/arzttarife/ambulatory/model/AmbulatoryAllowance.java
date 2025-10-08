package ch.elexis.base.ch.arzttarife.ambulatory.model;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulantePauschalenTyp;
import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.model.service.ContextServiceHolder;
import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.core.jpa.entities.AmbulantePauschalen;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.rgw.tools.Money;

public class AmbulatoryAllowance extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.AmbulantePauschalen>
		implements Identifiable, IAmbulatoryAllowance {

	public static final String STS_CLASS = "ch.elexis.data.AmbulantePauschalen";

	private static IBillableOptifier<AmbulatoryAllowance> optifier;
	private IBillableVerifier verifier;

	public AmbulatoryAllowance(AmbulantePauschalen entity) {
		super(entity);
		verifier = new DefaultVerifier();
	}

	@Override
	public IBillableOptifier<AmbulatoryAllowance> getOptifier() {
		if (optifier == null) {
			optifier = new AmbulatoryAllowanceOptifier(CoreModelServiceHolder.get(),
					ContextServiceHolder.get().get());
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
		return AmbulantePauschalen.CODESYSTEM_NAME;
	}

	@Override
	public String getCodeSystemCode() {
		return getTyp() == AmbulantePauschalenTyp.PAUSCHALE ? "005" : "TMA";
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
		return StringUtils.defaultString(getEntity().getChapter());
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
		return "(" + getCode() + ") " + getTyp().getCode() + StringUtils.SPACE + getChapter() + " - " + getText();
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
	public String getDigniQuali() {
		return getEntity().getDigniQuali();
	}

	@Override
	public AmbulantePauschalenTyp getTyp() {
		if (StringUtils.isNotBlank(getEntity().getTyp())) {
			return AmbulantePauschalenTyp.fromCode(getEntity().getTyp());
		}
		return AmbulantePauschalenTyp.PAUSCHALE;
	}

	@Override
	public void setTyp(AmbulantePauschalenTyp value) {
		getEntityMarkDirty().setTyp(value.getCode());
	}

	public static AmbulatoryAllowance getFromCode(String code, AmbulantePauschalenTyp typ, LocalDate date) {
		IQuery<IAmbulatoryAllowance> query = ArzttarifeModelServiceHolder.get().getQuery(IAmbulatoryAllowance.class);
		query.and("code", COMPARATOR.EQUALS, code);
		if (typ != null) {
			query.and("typ", COMPARATOR.EQUALS, typ.getCode());
		}

		List<IAmbulatoryAllowance> leistungen = query.execute();
		for (IAmbulatoryAllowance ambulanteLeistung : leistungen) {
			if ((date.isAfter(ambulanteLeistung.getValidFrom()) || date.equals(ambulanteLeistung.getValidFrom()))
					&& (date.isBefore(ambulanteLeistung.getValidTo()) || date.equals(ambulanteLeistung.getValidTo()))) {
				return (AmbulatoryAllowance) ambulanteLeistung;
			}
		}
		return null;
	}
}
