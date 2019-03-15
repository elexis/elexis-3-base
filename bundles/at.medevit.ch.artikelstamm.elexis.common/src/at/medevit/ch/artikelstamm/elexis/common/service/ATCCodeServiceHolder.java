package at.medevit.ch.artikelstamm.elexis.common.service;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.atc_codes.ATCCodeService;

@Component
public class ATCCodeServiceHolder {
	private static ATCCodeService atcCodeService = null;
	
	@Reference
	public void setATCCodeService(ATCCodeService atcCodeService){
		ATCCodeServiceHolder.atcCodeService = atcCodeService;
	}
	
	public static Optional<ATCCodeService> get(){
		return Optional.ofNullable(atcCodeService);
	}
}
