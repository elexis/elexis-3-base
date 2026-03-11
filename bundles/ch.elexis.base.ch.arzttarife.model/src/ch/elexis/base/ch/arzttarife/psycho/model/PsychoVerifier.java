package ch.elexis.base.ch.arzttarife.psycho.model;

import java.util.List;

import ch.elexis.base.ch.arzttarife.psycho.model.PsychoLimitation.TYPE;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.rgw.tools.Result;

public class PsychoVerifier implements IBillableVerifier {

	@Override
	public Result<IBillable> verifyAdd(IBillable billable, IEncounter encounter, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<IBilled> verify(IEncounter encounter) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Check service and group limitations of the {@link PsychoLeistung} on the
	 * {@link IEncounter} date.
	 * 
	 * @param kons
	 * @param psychoLeistung
	 * @param newBilled
	 * @return
	 */
	public Result<IBilled> checkLimitations(IEncounter kons, PsychoLeistung psychoLeistung, IBilled newBilled) {
		List<PsychoLimitation> limitations = PsychoLimitation.get(psychoLeistung);
		for (PsychoLimitation limitation : limitations) {
			Result<IBilled> result = limitation.test(kons, newBilled);
			if (!result.isOK()) {
				return result;
			}
			// retest amount limitations for including
			if (limitation.getType() == TYPE.EXCLUSIVE) {
				List<String> exclusiveCodes = limitation.getExclusiveCodes();
				List<IBilled> exclusiveBilledList = kons.getBilled().stream()
						.filter(billed -> exclusiveCodes.contains(billed.getCode())
								&& billed.getBillable() instanceof PsychoLeistung)
						.toList();
				for (IBilled exclusiveBilled : exclusiveBilledList) {
					List<PsychoLimitation> exclusiveLimitations = PsychoLimitation
							.get((PsychoLeistung) exclusiveBilled.getBillable());
					List<PsychoLimitation> exclusiveLimitationsAmount = exclusiveLimitations.stream()
							.filter(el -> el.getType() == TYPE.AMOUNT && el.isIncluding(psychoLeistung.getCode()))
							.toList();
					for (PsychoLimitation exclusiveLimitationAmount : exclusiveLimitationsAmount) {
						result = exclusiveLimitationAmount.test(kons, exclusiveBilled, newBilled);
						if (!result.isOK()) {
							return result;
						}
					}
				}
			}
		}
		return new Result<IBilled>(newBilled);
	}
}
