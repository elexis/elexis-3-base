package ch.elexis.base.ch.arzttarife.adjuster;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.interfaces.IVerrechenbarAdjuster;
import ch.elexis.data.Konsultation;
import ch.elexis.data.TarmedLeistung;

public class TarmedVerrechnebarAdjuster implements IVerrechenbarAdjuster {
	
	
	@Override
	public IVerrechenbar adjust(IVerrechenbar verrechenbar, Konsultation kons){
		if (verrechenbar instanceof TarmedLeistung) {
			System.out.println("ADJUST " + verrechenbar.getCode() + " for " + kons.getLabel());
			// TODO add code for validating and changing according to context of kons
		}
		return verrechenbar;
	}
}
