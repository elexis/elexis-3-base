package ch.elexis.base.ch.arzttarife.physio.model;

import java.time.LocalDate;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;

public class PhysioLeistung
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.PhysioLeistung>
		implements IdentifiableWithXid, IPhysioLeistung {
	
	public static final String STS_CLASS = "ch.elexis.data.PhysioLeistung";
	
	private static IBillableOptifier<PhysioLeistung> optifier;
	private IBillableVerifier verifier;
	
	public PhysioLeistung(ch.elexis.core.jpa.entities.PhysioLeistung entity){
		super(entity);
		verifier = new DefaultVerifier();
	}
	
	@Override
	public synchronized IBillableOptifier<PhysioLeistung> getOptifier(){
		if (optifier == null) {
			optifier = new AbstractOptifier<PhysioLeistung>(CoreModelServiceHolder.get()) {
				
				@Override
				protected void setPrice(PhysioLeistung billable, IBilled billed){
										billed.setFactor(1.0);
//										billed.setNetPrice(billable.getPurchasePrice());
					//					Money sellingPrice = billable.getSellingPrice();
					//					if (sellingPrice == null) {
					//						//						sellingPrice =  MargePreference.calculateVKP(getPurchasePrice());
					//					}
					//					//					if (!billable.isInSLList()) {
					//					//						// noObligationOptifier
					//					//					} else {
					//					//						// defaultOptifier
					//					//					}
					//					int vkPreis = sellingPrice.getCents();
					//					double pkgSize = Math.abs(billable.getPackageSize());
					//					double vkUnits = billable.getSellingSize();
					//					if ((pkgSize > 0.0) && (vkUnits > 0.0) && (pkgSize != vkUnits)) {
					//						billed.setPoints((int) Math.round(vkUnits * (vkPreis / pkgSize)));
					//					} else {
					//						billed.setPoints((int) Math.round(vkPreis));
					//					}
					
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
		return ch.elexis.core.jpa.entities.PhysioLeistung.CODESYSTEM_NAME;
	}
	
	@Override
	public String getCode(){
		return getZiffer();
	}
	
	@Override
	public void setCode(String value){
		// TODO Auto-generated method stub
	}
	
	@Override
	public String getText(){
		return getEntity().getTitel();
	}
	
	@Override
	public void setText(String value){
		getEntity().setTitel(value);
		
	}
	
	@Override
	public LocalDate getValidFrom(){
		return getEntity().getValidFrom();
	}
	
	@Override
	public void setValidFrom(LocalDate value){
		getEntity().setValidFrom(value);
	}
	
	@Override
	public LocalDate getValidTo(){
		return getEntity().getValidUntil();
	}
	
	@Override
	public void setValidTo(LocalDate value){
		getEntity().setValidUntil(value);
	}
	
	@Override
	public String getTP(){
		return getEntity().getTp();
	}
	
	@Override
	public void setTP(String value){
		getEntity().setTp(value);
	}
	
	@Override
	public String getZiffer(){
		return getEntity().getZiffer();
	}
	
	@Override
	public void setZiffer(String value){
		getEntity().setZiffer(value);
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
	public String getLabel(){
		return getZiffer() + " " + getText();
	}
}
