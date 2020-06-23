package at.medevit.elexis.loinc.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import at.medevit.elexis.loinc.model.ILoincCodeService;
import at.medevit.elexis.loinc.model.LoincCode;
import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.CodingSystem;
import ch.elexis.core.findings.codes.ICodingContribution;

@Component
public class LoincCodingContribution implements ICodingContribution
{
	private ILoincCodeService iLoincCodeService;
	
	@Activate
	public void activate(){
		this.iLoincCodeService = new LoincCodeService();
	}
	
	@Override
	public String getCodeSystem(){
		return CodingSystem.LOINC_CODESYSTEM.getSystem();
	}
	
	@Override
	public List<ICoding> getCodes(){
		List<ICoding> codings = new ArrayList<>();
		for (LoincCode c : iLoincCodeService.getAllCodes()) {
			codings.add((ICoding) c);
		}
		return codings;
	}
	
	@Override
	public Optional<ICoding> getCode(String code){
		return Optional.ofNullable((ICoding) iLoincCodeService.getByCode(code));
	}
	
}
