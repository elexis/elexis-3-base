package ch.elexis.base.ch.arzttarife.pandemie.model;

import java.time.LocalDate;
import java.util.Optional;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.VatInfo;

public class PandemieLeistung
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.PandemieLeistung>
		implements Identifiable, IPandemieLeistung {
	
	public static final String STS_CLASS = "ch.elexis.data.PandemieLeistung";
	
	private static IBillableOptifier<PandemieLeistung> optifier;
	private IBillableVerifier verifier;
	
	public PandemieLeistung(ch.elexis.core.jpa.entities.PandemieLeistung entity){
		super(entity);
		verifier = new DefaultVerifier();
	}
	
	@Override
	public IBillableOptifier<PandemieLeistung> getOptifier(){
		if (optifier == null) {
			optifier = new AbstractOptifier<PandemieLeistung>(CoreModelServiceHolder.get()) {
				
				@Override
				protected void setPrice(PandemieLeistung billable, IBilled billed){
					Optional<IBillingSystemFactor> billingFactor =
						BillingServiceHolder.get().getBillingSystemFactor(getCodeSystemName(),
							billed.getEncounter().getDate());
					if (billingFactor.isPresent()) {
						billed.setFactor(billingFactor.get().getFactor());
					} else {
						billed.setFactor(1.0);
					}
					int points = 0;
					// use cents if set
					if (billable.getCents() > 0) {
						points = billable.getCents();
					} else {
						points = billable.getTaxpoints();
					}
					billed.setPoints(points);
				}
			};
		}
		return optifier;
	}
	
	@Override
	public IBillableVerifier getVerifier(){
		return verifier;
	}
	
	@Override
	public String getCodeSystemName(){
		return ch.elexis.core.jpa.entities.PandemieLeistung.CODESYSTEM_NAME;
	}
	
	@Override
	public String getCodeSystemCode(){
		return "351";
	}
	
	@Override
	public VatInfo getVatInfo(){
		return VatInfo.VAT_NONE;
	}
	
	@Override
	public String getCode(){
		return getEntity().getCode();
	}
	
	@Override
	public void setCode(String value){
		getEntity().setCode(value);
	}
	
	@Override
	public String getText(){
		return getEntity().getTitle();
	}
	
	@Override
	public void setText(String value){
		getEntity().setTitle(value);
	}
	
	@Override
	public String getDescription(){
		return getEntity().getDescription();
	}
	
	@Override
	public void setDescription(String value){
		getEntity().setDescription(value);
	}
	
	@Override
	public String getChapter(){
		return getEntity().getChapter();
	}
	
	@Override
	public void setChapter(String value){
		getEntity().setChapter(value);
	}
	
	@Override
	public int getCents(){
		return getEntity().getCents();
	}
	
	@Override
	public void setCents(int value){
		getEntity().setCents(value);
	}
	
	@Override
	public String getLabel(){
		return "(" + getCode() + ") " + getText();
	}
	
	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists){
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}
	
	@Override
	public IXid getXid(String domain){
		return XidServiceHolder.get().getXid(this, domain);
	}
	
	@Override
	public void setId(String id){
		getEntityMarkDirty().setId(id);
	}
	
	@Override
	public LocalDate getValidFrom(){
		return getEntity().getValidFrom();
	}
	
	@Override
	public LocalDate getValidTo(){
		return getEntity().getValidTo();
	}
	
	@Override
	public void setValidFrom(LocalDate value){
		getEntityMarkDirty().setValidFrom(value);
	}
	
	@Override
	public void setValidTo(LocalDate value){
		getEntityMarkDirty().setValidTo(value);
	}
	
	@Override
	public int getTaxpoints(){
		return getEntity().getTaxpoints();
	}
	
	@Override
	public void setTaxpoints(int value){
		getEntityMarkDirty().setTaxpoints(value);
	}
}
