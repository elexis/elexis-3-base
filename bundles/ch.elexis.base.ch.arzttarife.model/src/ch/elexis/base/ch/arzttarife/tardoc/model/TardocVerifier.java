package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp;
import ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher.TarifMatcher;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedOptifier;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.findings.ICoding;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TardocVerifier implements IBillableVerifier {

	private ICodeElementService codeElementService;

	private synchronized ICodeElementService getCodeElementService() {
		if (codeElementService == null) {
			codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).orElse(null);
		}
		return codeElementService;
	}

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
				Result<IBilled> digniResult = checkDigni(encounter, tardocLeistung, vv);
				if (!digniResult.isOK()) {
					ret.add(digniResult);
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

	public Result<IBilled> checkDigni(IEncounter encounter, TardocLeistung tardocLeistung, IBilled newBilled) {
		String digni = tardocLeistung.getDigniQuali();
		if (StringUtils.isNotBlank(digni) && !digni.contains("9999")) {
			if (encounter.getMandator() != null) {
				List<ICoding> tardocSpecialist = ArzttarifeUtil.getMandantTardocSepcialist(encounter.getMandator());
				if (!tardocSpecialist.stream().anyMatch(c -> digni.contains(c.getCode()))) {
					List<ICodeElement> acquiredRights = ArzttarifeUtil
							.getMandantTardocAcquiredRights(encounter.getMandator(), getCodeElementService());
					if (acquiredRights != null && !acquiredRights.isEmpty()) {
						Optional<ICodeElement> found = acquiredRights.stream()
								.filter(ce -> ce.getCodeSystemName().equals(tardocLeistung.getCodeSystemName())
										&& ce.getCode().equals(tardocLeistung.getCode()))
								.findAny();
						if (found.isPresent()) {
							return new Result<IBilled>(null);
						}
					}
					String msg = "Der Mandant hat keine der benötigten Dignitäten [" + digni + "] der Leistung "
							+ tardocLeistung.getCode() + ".";
					return new Result<IBilled>(Result.SEVERITY.WARNING, TarifMatcher.LEISTUNGSTYP, msg, null, false);
				}
			}
		}
		return new Result<IBilled>(null);
	}

	public Result<IBilled> checkCustomKumulations(List<ITardocKumulation> customKumulations, IBilled newBilled) {
		for (ITardocKumulation iTardocKumulation : customKumulations) {
			// allow inclusion
			if (iTardocKumulation.getTyp() == TardocKumulationTyp.INCLUSION) {
				continue;
			} else if (iTardocKumulation.getTyp() == TardocKumulationTyp.EXCLUSION) {
				return new Result<IBilled>(Result.SEVERITY.WARNING, TarifMatcher.KOMBINATION,
						"Die Leistung  " + iTardocKumulation.getMasterCode() + " ist nicht kombinierbar mit "
								+ iTardocKumulation.getSlaveCode() + ".",
						newBilled, false);
			}
		}
		return new Result<IBilled>(newBilled);
	}
}
