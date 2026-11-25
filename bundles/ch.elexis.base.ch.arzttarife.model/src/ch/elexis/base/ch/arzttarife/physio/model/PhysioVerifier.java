package ch.elexis.base.ch.arzttarife.physio.model;

import java.util.List;

import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.rgw.tools.Result;

public class PhysioVerifier extends DefaultVerifier implements IBillableVerifier {

	/**
	 * Check the limitations of the {@link PhysioLeistung} on the {@link IEncounter}
	 * (Sitzung).
	 * 
	 * @param encounter
	 * @param physioLeistung
	 * @param newBilled
	 * @return
	 */
	public Result<IBilled> checkLimitations(IEncounter encounter, PhysioLeistung physioLeistung, IBilled newBilled) {
		List<PhysioLimitation> limitations = PhysioLimitation.get(physioLeistung);
		for (PhysioLimitation limitation : limitations) {
			Result<IBilled> result = limitation.test(encounter, newBilled);
			if (!result.isOK()) {
				return result;
			}
		}
		return new Result<IBilled>(newBilled);
	}
}
