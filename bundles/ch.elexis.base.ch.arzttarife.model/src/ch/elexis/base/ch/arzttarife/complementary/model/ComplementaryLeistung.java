package ch.elexis.base.ch.arzttarife.complementary.model;

import ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;

public class ComplementaryLeistung
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.ComplementaryLeistung>
		implements IdentifiableWithXid, IComplementaryLeistung {
	
	public static final String STS_CLASS = "ch.elexis.data.ComplementaryLeistung";
	
	public ComplementaryLeistung(ch.elexis.core.jpa.entities.ComplementaryLeistung entity){
		super(entity);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public IBillableOptifier<ComplementaryLeistung> getOptifier(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IBillableVerifier getVerifier(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getCodeSystemName(){
		return ch.elexis.core.jpa.entities.ComplementaryLeistung.CODESYSTEM_NAME;
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
		return getEntity().getCodeText();
	}
	
	@Override
	public void setText(String value){
		getEntity().setCodeText(value);
	}
	
	@Override
	public String getDescription(){
		return getEntity().getDescription();
	}
	
	@Override
	public void setDescription(String value){
		getEntity().setDescription(value);
	}
	
}
