package ch.berchtold.emanuel.privatrechnung.model.internal;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.berchtold.emanuel.privatrechnung.model.IPrivatLeistung;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.jpa.entities.BerchtoldPrivatLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;

public class Leistung
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.BerchtoldPrivatLeistung>
		implements IPrivatLeistung {
	
	private static final String CODESYSTEM_NAME = "Privat";
	private static IBillableOptifier<IPrivatLeistung> optifier;
	private static IBillableVerifier verifier;
	
	public static final String STS_CLASS = "ch.berchtold.emanuel.privatrechnung.data.Leistung";
	
	public Leistung(BerchtoldPrivatLeistung entity){
		super(entity);
	}
	
	@Override
	public String getCodeSystemName(){
		return CODESYSTEM_NAME;
	}
	
	@Override
	public String getCode(){
		return getEntity().getShortName();
	}
	
	@Override
	public void setCode(String value){
		getEntityMarkDirty().setShortName(value);
	}
	
	@Override
	public String getText(){
		return getEntity().getName();
	}
	
	@Override
	public void setText(String value){
		getEntityMarkDirty().setName(value);
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
	public IBillableOptifier<IPrivatLeistung> getOptifier(){
		if (optifier == null) {
			optifier = new AbstractOptifier<IPrivatLeistung>(CoreModelServiceHolder.get(), ContextServiceHolder.get()) {
				
				@Override
				protected void setPrice(IPrivatLeistung billable, IBilled billed){
					billed.setFactor(1.0);
					billed.setNetPrice(billable.getNetPrice());
					billed.setPoints(billable.getPrice().getCents());
				}
				
			};
		}
		return optifier;
	}
	
	@Override
	public IBillableVerifier getVerifier(){
		if (verifier == null) {
			verifier = new DefaultVerifier();
		}
		return verifier;
	}
	
	@Override
	public Money getNetPrice(){
		return new Money(getEntity().getCost());
	}
	
	@Override
	public Money getPrice(){
		return new Money(getEntity().getPrice());
	}
	
	@Override
	public boolean isValidOn(LocalDate date){
		boolean validFrom = getValidFrom().isBefore(date) || getValidFrom().isEqual(date);
		if (validFrom && getValidTo() != null) {
			return getValidTo().isAfter(date) || getValidTo().isEqual(date);
		}
		return validFrom;
	}
	
	private LocalDate getValidTo(){
		return getEntity().getValidTo() != null ? getEntity().getValidTo() : LocalDate.MAX;
	}
	
	private LocalDate getValidFrom(){
		return getEntity().getValidFrom() != null ? getEntity().getValidFrom() : LocalDate.MIN;
	}
	
	@Override
	public void setParent(String string){
		getEntityMarkDirty().setParent(string);
	}
	
	@Override
	public void setCost(String string){
		try {
			getEntityMarkDirty().setCost(Integer.parseInt(string));
		} catch (NumberFormatException e) {
			LoggerFactory.getLogger(getClass()).error("Error setting cost", e);
		}
	}
	
	@Override
	public void setPrice(String string){
		try {
			getEntityMarkDirty().setPrice(Integer.parseInt(string));
		} catch (NumberFormatException e) {
			LoggerFactory.getLogger(getClass()).error("Error setting price", e);
		}
	}
	
	@Override
	public void setTime(String string){
		try {
			getEntityMarkDirty().setTime(Integer.parseInt(string));
		} catch (NumberFormatException e) {
			LoggerFactory.getLogger(getClass()).error("Error setting price", e);
		}
	}
	
	@Override
	public void setValidFrom(String string){
		if (StringUtils.isNotBlank(string)) {
			getEntityMarkDirty().setValidFrom(new TimeTool(string).toLocalDate());
		}
	}
	
	@Override
	public void setValidTo(String string){
		if (StringUtils.isNotBlank(string)) {
			getEntityMarkDirty().setValidTo(new TimeTool(string).toLocalDate());
		}
	}
	
	@Override
	public String getLabel(){
		return getCode() + " - " + getText();
	}
}
