package ch.elexis.base.ch.arzttarife.adjuster;

import org.apache.commons.lang.StringUtils;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.interfaces.IVerrechenbarAdjuster;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.TimeTool;

public class TarmedVerrechnebarAdjuster implements IVerrechenbarAdjuster {
	
	
	@Override
	public IVerrechenbar adjust(IVerrechenbar verrechenbar, Konsultation kons){
		if (verrechenbar instanceof TarmedLeistung) {
			TarmedLeistung leistung = (TarmedLeistung) verrechenbar;
			String leistungLaw = leistung.get(TarmedLeistung.FLD_LAW);
			// check if a law for a leistung is specified
			if (StringUtils.isNotEmpty(leistungLaw)) {
				Fall fall = kons.getFall();
				String law = fall.getConfiguredBillingSystemLaw().name();
				
				// law is not compatible for this leistung law look for compatible one
				if (!leistungLaw.equalsIgnoreCase(law)) {
					verrechenbar = TarmedLeistung.getFromCode(leistung.getCode(),
						new TimeTool(kons.getDateTime()), fall.getAbrechnungsSystem());
				}
			}
		}
		return verrechenbar;
	}
}
