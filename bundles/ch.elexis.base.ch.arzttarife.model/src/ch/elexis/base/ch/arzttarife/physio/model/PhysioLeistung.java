package ch.elexis.base.ch.arzttarife.physio.model;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;

public class PhysioLeistung extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.PhysioLeistung>
		implements Identifiable, IPhysioLeistung {

	public static final String STS_CLASS = "ch.elexis.data.PhysioLeistung";

	private static IBillableOptifier<PhysioLeistung> optifier;
	private IBillableVerifier verifier;

	public PhysioLeistung(ch.elexis.core.jpa.entities.PhysioLeistung entity) {
		super(entity);
		verifier = new PhysioVerifier();
	}

	@Override
	public synchronized IBillableOptifier<PhysioLeistung> getOptifier() {
		if (optifier == null) {
			optifier = new PhysioOptifier(CoreModelServiceHolder.get(), ContextServiceHolder.get());
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
	public String getKapitel() {
		return getEntity().getKapitel();
	}

	@Override
	public void setKapitel(String value) {
		getEntity().setKapitel(value);
	}

	@Override
	public String getInterpretation() {
		return getEntity().getInterpretation();
	}

	@Override
	public void setInterpretation(String value) {
		getEntity().setInterpretation(value);
	}

	@Override
	public String getLabel() {
		return getCode() + StringUtils.SPACE + getText()
				+ ((getEntity().getLaw() != null && !getEntity().getLaw().isEmpty()) ? " (" + getEntity().getLaw() + ")"
						: StringUtils.EMPTY);
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
	public String getLaw() {
		return getEntity().getLaw();
	}
}
