package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedOptifier;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TardocVerifier implements IBillableVerifier {

	@Override
	public Result<IBillable> verifyAdd(IBillable billable, IEncounter encounter, double amount) {
		Result<IBillable> ret = Result.OK();
		if (billable instanceof TardocLeistung) {
			Result<IBilled> validDateResult = checkValidDate(encounter, (TardocLeistung) billable, null);
			if (!validDateResult.isOK()) {
				return new Result<IBillable>(validDateResult.getSeverity(), validDateResult.getCode(),
						validDateResult.getMessages().get(0).getText(), billable, false);
			}
		}
		return ret;
	}

	public Result<IBilled> checkValidDate(IEncounter kons, TardocLeistung tardocLeistung, IBilled newVerrechnet) {
		TimeTool date = new TimeTool(kons.getDate());
		LocalDate dVon = tardocLeistung.getValidFrom();
		if (dVon != null) {
			TimeTool tVon = new TimeTool(dVon);
			if (date.isBefore(tVon)) {
				return new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.NOTYETVALID,
						tardocLeistung.getCode() + StringUtils.SPACE + Messages.TarmedOptifier_NotYetValid,
						newVerrechnet, false);
			}
		}
		LocalDate dBis = tardocLeistung.getValidTo();
		if (dBis != null) {
			TimeTool tBis = new TimeTool(dBis);
			if (date.isAfter(tBis)) {
				return new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.NOMOREVALID,
						tardocLeistung.getCode() + StringUtils.SPACE + Messages.TarmedOptifier_NoMoreValid,
						newVerrechnet, false);
			}
		}
		return new Result<IBilled>(newVerrechnet);
	}

	@Override
	public Result<IBilled> verify(IEncounter encounter) {
		Result<IBilled> ret = new Result<IBilled>();
		for (IBilled vv : encounter.getBilled()) {
			IBillable iv = vv.getBillable();
			if (iv instanceof TardocLeistung) {
				TardocLeistung tardocLeistung = (TardocLeistung) iv;
				Result<IBilled> validDateResult = checkValidDate(encounter, tardocLeistung, vv);
				if (!validDateResult.isOK()) {
					ret.add(validDateResult);
				}
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
