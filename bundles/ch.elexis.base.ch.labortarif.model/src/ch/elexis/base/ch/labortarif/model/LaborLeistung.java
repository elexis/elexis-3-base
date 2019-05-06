package ch.elexis.base.ch.labortarif.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.base.ch.labortarif.LaborTarifConstants;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.VatInfo;
import ch.rgw.tools.StringTool;

public class LaborLeistung
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.Labor2009Tarif>
		implements Identifiable, ILaborLeistung {
	
	public static final String STS_CLASS = "ch.elexis.labortarif2009.data.Labor2009Tarif";
	
	private static IBillableOptifier<ILaborLeistung> optifier;
	private IBillableVerifier verifier;
	
	public LaborLeistung(ch.elexis.core.jpa.entities.Labor2009Tarif entity){
		super(entity);
		verifier = new DefaultVerifier();
	}
	
	@Override
	public IBillableVerifier getVerifier(){
		return verifier;
	}
	
	@Override
	public String getCodeSystemName(){
		return LaborTarifConstants.CODESYSTEM_NAME;
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
		return StringTool.getFirstLine(getEntity().getName(), 80);
	}
	
	@Override
	public void setText(String value){
		getEntity().setName(value);
	}
	
	@Override
	public synchronized IBillableOptifier<ILaborLeistung> getOptifier(){
		if (optifier == null) {
			optifier = new LaborLeistungOptifier(CoreModelServiceHolder.get());
		}
		return optifier;
	}
	
	@Override
	public int getPoints(){
		return (int) Math.round(getEntity().getTp() * 100.0);
	}
	
	@Override
	public LocalDate getValidFrom(){
		return getEntity().getGueltigVon();
	}
	
	@Override
	public LocalDate getValidTo(){
		return getEntity().getGueltigBis();
	}
	
	@Override
	public boolean isValidOn(LocalDate date){
		boolean validFrom = getValidFrom().isBefore(date) || getValidFrom().isEqual(date);
		if(validFrom && getValidTo() != null) {
			return getValidTo().isAfter(date) || getValidTo().isEqual(date);
		}
		return validFrom;
	}
	
	@Override
	public String getChapter(){
		return getEntity().getChapter();
	}
	
	@Override
	public String getSpeciality(){
		return getEntity().getFachbereich();
	}
	
	@Override
	public String getLabel(){
		String text = StringTool.getFirstLine(getText(), 80);
		
		if (StringUtils.isNotBlank(getCode())) {
			StringBuilder sb = new StringBuilder(getCode()).append(" ").append(text) //$NON-NLS-1$
				.append(" (").append(getSpeciality()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			if (getValidFrom() != null) {
				sb.append(" (").append(getValidFrom().format(dateFormatter)); //$NON-NLS-1$
				if (getValidTo() != null) {
					sb.append("-").append(getValidTo().format(dateFormatter)).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					sb.append("-").append(" ").append(")"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			return sb.toString();
		} else {
			return "?"; //$NON-NLS-1$
		}
	}
	
	@Override
	public String getLimitation(){
		return getEntity().getLimitatio();
	}
	
	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists){
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}
	
	@Override
	public IXid getXid(String domain){
		return XidServiceHolder.get().getXid(this, domain);
	}
}
