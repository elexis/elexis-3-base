package ch.elexis.base.ch.diagnosecodes.util;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.IKonsMakro;
import ch.elexis.data.Konsultation;
import ch.elexis.data.TICode;

public class TIMakro implements IKonsMakro {
	
	public TIMakro(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String executeMakro(String makro){
		Konsultation actKons = (Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
		
		TICode tiCode = TICode.getFromCode(makro);
		if (tiCode != null) {
			actKons.addDiagnose(tiCode);
		}
		
		return "";
	}
	
}
