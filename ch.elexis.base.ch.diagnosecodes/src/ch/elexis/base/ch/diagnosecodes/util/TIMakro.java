package ch.elexis.base.ch.diagnosecodes.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.IKonsMakro;
import ch.elexis.data.Konsultation;
import ch.elexis.data.TICode;

public class TIMakro implements IKonsMakro {
	
	private static Logger logger = LoggerFactory.getLogger(TIMakro.class);
	
	public TIMakro(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String executeMakro(String makro){
		Konsultation actKons = (Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
		
		try {
			TICode tiCode = TICode.getFromCode(makro);
			if (tiCode != null) {
				actKons.addDiagnose(tiCode);
			}
		} catch (Exception e) {
			logger.debug("Could not resolve TI Code [" + makro + "]");
		}
		
		return "";
	}
	
}
