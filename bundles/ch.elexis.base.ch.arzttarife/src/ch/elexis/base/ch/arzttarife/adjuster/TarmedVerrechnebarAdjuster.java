package ch.elexis.base.ch.arzttarife.adjuster;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IBillableAdjuster;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.holder.CodeElementServiceHolder;

@Component
public class TarmedVerrechnebarAdjuster implements IBillableAdjuster {
	
	@Override
	public IBillable adjust(IBillable billable, IEncounter encounter){
		if (billable instanceof ITarmedLeistung) {
			ITarmedLeistung leistung = (ITarmedLeistung) billable;
			String leistungLaw = leistung.getLaw();
			// check if a law for a leistung is specified
			if (StringUtils.isNotEmpty(leistungLaw)) {
				ICoverage coverage = encounter.getCoverage();
				String law = coverage.getBillingSystem().getLaw().name();
				
				// law is not compatible for this leistung law look for compatible one
				if (!leistungLaw.equalsIgnoreCase(law)) {
					Optional<ICodeElementServiceContribution> tarmedCode = CodeElementServiceHolder
						.get().getContribution(CodeElementTyp.SERVICE, "Tarmed");
					billable = (IBillable) tarmedCode.get().loadFromCode(leistung.getCode(),
						CodeElementServiceHolder.createContext(encounter)).orElse(null);
				}
			}
		}
		return billable;
	}
}
