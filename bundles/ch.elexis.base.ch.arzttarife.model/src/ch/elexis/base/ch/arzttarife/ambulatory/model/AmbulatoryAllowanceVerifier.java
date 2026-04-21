package ch.elexis.base.ch.arzttarife.ambulatory.model;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulantePauschalenTyp;
import ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher.TarifMatcher;
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

public class AmbulatoryAllowanceVerifier implements IBillableVerifier {

	private ICodeElementService codeElementService;

	private synchronized ICodeElementService getCodeElementService() {
		if (codeElementService == null) {
			codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).orElse(null);
		}
		return codeElementService;
	}

	@Override
	public Result<IBillable> verifyAdd(IBillable billable, IEncounter encounter, double amount) {
		return Result.OK();
	}

	@Override
	public Result<IBilled> verify(IEncounter encounter) {
		return Result.OK();
	}

	public Result<IBilled> checkDigni(IEncounter encounter, AmbulatoryAllowance ambulatoryLeistung) {
		if (ambulatoryLeistung.getTyp() == AmbulantePauschalenTyp.PAUSCHALE) {
			String digni = ambulatoryLeistung.getDigniQuali();
			if (StringUtils.isNotBlank(digni) && !digni.contains("9999")) {
				if (encounter.getMandator() != null) {
					List<ICodeElement> acquiredRights = ArzttarifeUtil
							.getMandantTardocAcquiredRights(encounter.getMandator(), getCodeElementService());
					if (acquiredRights != null && !acquiredRights.isEmpty()) {
						Optional<ICodeElement> found = acquiredRights.stream()
								.filter(ce -> ce.getCodeSystemName().equals(ambulatoryLeistung.getCodeSystemName())
										&& ce.getCode().equals(ambulatoryLeistung.getCode()))
								.findAny();
						if (found.isPresent()) {
							return new Result<IBilled>(null);
						}
					}

					List<ICoding> tardocSpecialist = ArzttarifeUtil.getMandantTardocSepcialist(encounter.getMandator());
					if (!tardocSpecialist.stream().anyMatch(c -> digni.contains(c.getCode()))) {
						String msg = "Der Mandant hat keine der benötigten Dignitäten [" + digni + "] der Pauschale "
								+ ambulatoryLeistung.getCode() + ".";
						return new Result<IBilled>(Result.SEVERITY.WARNING, TarifMatcher.LEISTUNGSTYP, msg, null,
								false);
					}
				}
			}
		}
		return new Result<IBilled>(null);
	}
}
