package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.util.List;
import java.util.Optional;

import ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.rgw.tools.Result;

public class TardocVerifier implements IBillableVerifier {

	@Override
	public Result<IBillable> verifyAdd(IBillable billable, IEncounter encounter, double amount) {
		// TODO currently no add verification, verification is performed by optifier
		return Result.OK();
	}

	@Override
	public Result<IBilled> verify(IEncounter encounter) {
		Result<IBilled> ret = new Result<IBilled>();
		for (IBilled vv : encounter.getBilled()) {
			IBillable iv = vv.getBillable();
			if (iv instanceof TardocLeistung) {
				TardocLeistung tardocLeistung = (TardocLeistung) iv;
				Result<IBilled> limitResult = checkLimitations(encounter, tardocLeistung, vv);
				if (!limitResult.isOK()) {
					ret.add(limitResult);
				}
			}
		}
		return ret;
	}

	/**
	 * Check service and group limitations of the {@link TardocLeistung} on the
	 * {@link IEncounter} date.
	 * 
	 * @param kons
	 * @param tarmedLeistung
	 * @param newVerrechnet
	 * @return
	 */
	public Result<IBilled> checkLimitations(IEncounter kons, TardocLeistung tardocLeistung, IBilled newVerrechnet) {
		// service limitations
		List<TardocLimitation> limitations = tardocLeistung.getLimitations();
		for (TardocLimitation tardocLimitation : limitations) {
			if (tardocLimitation.isTestable()) {
				Result<IBilled> result = tardocLimitation.test(kons, newVerrechnet);
				if (!result.isOK()) {
					return result;
				}
			}
		}
		// group limitations
		List<String> groups = tardocLeistung.getServiceGroups(kons.getDate());
		for (String groupName : groups) {
			Optional<ITardocGroup> group = TardocGroup.find(groupName, tardocLeistung.getLaw(), kons.getDate());
			if (group.isPresent()) {
				limitations = group.get().getLimitations();
				for (TardocLimitation tardocLimitation : limitations) {
					if (tardocLimitation.isTestable()) {
						Result<IBilled> result = tardocLimitation.test(kons, newVerrechnet);
						if (!result.isOK()) {
							return result;
						}
					}
				}
			}
		}
		return new Result<IBilled>(newVerrechnet);
	}
}
