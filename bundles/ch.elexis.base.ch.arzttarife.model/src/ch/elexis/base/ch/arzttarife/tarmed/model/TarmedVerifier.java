package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.TarmedLeistungAge;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TarmedVerifier implements IBillableVerifier {

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
			if (iv instanceof TarmedLeistung) {
				TarmedLeistung tarmedLeistung = (TarmedLeistung) iv;
				Result<IBilled> limitResult = checkLimitations(encounter, tarmedLeistung, vv);
				if (!limitResult.isOK()) {
					ret.add(limitResult);
				}
				Result<IBilled> ageResult = checkAge(encounter, tarmedLeistung, vv);
				if (!ageResult.isOK()) {
					ret.add(ageResult);
				}
			}
		}
		return ret;
	}

	/**
	 * Check service and group limitations of the {@link TarmedLeistung} on the
	 * {@link IEncounter} date.
	 * 
	 * @param kons
	 * @param tarmedLeistung
	 * @param newVerrechnet
	 * @return
	 */
	public Result<IBilled> checkLimitations(IEncounter kons, TarmedLeistung tarmedLeistung, IBilled newVerrechnet) {
		// service limitations
		List<TarmedLimitation> limitations = tarmedLeistung.getLimitations();
		for (TarmedLimitation tarmedLimitation : limitations) {
			if (tarmedLimitation.isTestable()) {
				Result<IBilled> result = tarmedLimitation.test(kons, newVerrechnet);
				if (!result.isOK()) {
					return result;
				}
			}
		}
		// group limitations
		List<String> groups = tarmedLeistung.getServiceGroups(kons.getDate());
		for (String groupName : groups) {
			Optional<ITarmedGroup> group = TarmedGroup.find(groupName, tarmedLeistung.getLaw(), kons.getDate());
			if (group.isPresent()) {
				limitations = group.get().getLimitations();
				for (TarmedLimitation tarmedLimitation : limitations) {
					if (tarmedLimitation.isTestable()) {
						Result<IBilled> result = tarmedLimitation.test(kons, newVerrechnet);
						if (!result.isOK()) {
							return result;
						}
					}
				}
			}
		}
		return new Result<IBilled>(newVerrechnet);
	}
	
	/**
	 * Check the age limitation of the {@link TarmedLeistung}, if there is one, on
	 * the {@link IEncounter} date.
	 * 
	 * @param kons
	 * @param tarmedLeistung
	 * @param newVerrechnet
	 * @return
	 */
	public Result<IBilled> checkAge(IEncounter kons, TarmedLeistung tarmedLeistung, IBilled newVerrechnet) {
		Map<String, String> ext = tarmedLeistung.getExtension().getLimits();
		String limitsString = ext.get(ch.elexis.core.jpa.entities.TarmedLeistung.EXT_FLD_SERVICE_AGE);
		if (StringUtils.isNotBlank(limitsString)) {
			LocalDateTime consDate = new TimeTool(kons.getDate()).toLocalDateTime();
			IPatient patient = kons.getCoverage().getPatient();
			LocalDateTime geburtsdatum = patient.getDateOfBirth();
			if (geburtsdatum == null) {
				return new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.PATIENTAGE,
						"Patienten Alter nicht ok, kein Geburtsdatum angegeben", null, false);
			}
			long patientAgeDays = patient.getAgeAtIn(consDate, ChronoUnit.DAYS);

			List<TarmedLeistungAge> ageLimits = TarmedLeistungAge.of(limitsString, consDate);
			for (TarmedLeistungAge tarmedLeistungAge : ageLimits) {
				if (tarmedLeistungAge.isValidOn(consDate.toLocalDate())) {
					// if only one of the limits is set, check only that limit
					if (tarmedLeistungAge.getFromDays() >= 0 && !(tarmedLeistungAge.getToDays() >= 0)) {
						if (patientAgeDays < tarmedLeistungAge.getFromDays()) {
							return new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.PATIENTAGE,
									"Patient ist zu jung, verrechenbar ab " + tarmedLeistungAge.getFromText(),
									newVerrechnet,
									false);
						}
					} else if (tarmedLeistungAge.getToDays() >= 0 && !(tarmedLeistungAge.getFromDays() >= 0)) {
						if (patientAgeDays > tarmedLeistungAge.getToDays()) {
							return new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.PATIENTAGE,
									"Patient ist zu alt, verrechenbar bis " + tarmedLeistungAge.getToText(),
									newVerrechnet,
									false);
						}
					} else if (tarmedLeistungAge.getToDays() >= 0 && tarmedLeistungAge.getFromDays() >= 0) {
						if (tarmedLeistungAge.getToDays() < tarmedLeistungAge.getFromDays()) {
							if (patientAgeDays > tarmedLeistungAge.getToDays()
									&& patientAgeDays < tarmedLeistungAge.getFromDays()) {
								return new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.PATIENTAGE,
										"Patienten Alter nicht ok, verrechenbar " + tarmedLeistungAge.getText(),
										newVerrechnet,
										false);
							}
						} else {
							if (patientAgeDays > tarmedLeistungAge.getToDays()
									|| patientAgeDays < tarmedLeistungAge.getFromDays()) {
								return new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.PATIENTAGE,
										"Patienten Alter nicht ok, verrechenbar " + tarmedLeistungAge.getText(),
										newVerrechnet,
										false);
							}
						}
					}
				}
			}			
		}
		return new Result<IBilled>(newVerrechnet);
	}
}
