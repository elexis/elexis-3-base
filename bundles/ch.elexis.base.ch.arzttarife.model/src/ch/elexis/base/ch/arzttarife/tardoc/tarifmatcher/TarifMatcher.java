package ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulantePauschalenTyp;
import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.base.ch.arzttarife.ambulatory.model.AmbulatoryAllowance;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.oaat_otma.PatientCase;
import ch.oaat_otma.Service;
import ch.oaat_otma.casemaster.CasemasterError.CasemasterErrorType;
import ch.oaat_otma.casemaster.CasemasterResult;
import ch.oaat_otma.mapper.MapperLogEntry;
import ch.oaat_otma.mapper.MapperLogEntry.MapperLogEntryLevel;
import ch.oaat_otma.mapper.MapperResult;
import ch.rgw.tools.Result;

public class TarifMatcher<T extends IBillable> {

	public static final int OK = 0;
	public static final int PREISAENDERUNG = 1;
	public static final int KUMULATION = 2;
	public static final int KOMBINATION = 3;
	public static final int EXKLUSION = 4;
	public static final int INKLUSION = 5;
	public static final int LEISTUNGSTYP = 6;
	public static final int NOTYETVALID = 7;
	public static final int NOMOREVALID = 8;
	public static final int PATIENTAGE = 9;
	public static final int EXKLUSIVE = 10;
	public static final int EXKLUSIONSIDE = 11;

	private CasemasterService casemasterService;

	private PatientClassificationSystemService patientClassificationSystemService;

	private MapperService mapperService;
	private IBillableOptifier<T> optifier;

	public TarifMatcher(IBillableOptifier<T> optifier) {
		this.optifier = optifier;
	}

	@SuppressWarnings("unchecked")
	public Result<IBilled> evaluate(IBilled billed, IEncounter encounter) {
		if (casemasterService == null) {
			casemasterService = OsgiServiceUtil.getService(CasemasterService.class).orElseThrow();
		}
		if (patientClassificationSystemService == null) {
			patientClassificationSystemService = OsgiServiceUtil.getService(PatientClassificationSystemService.class)
					.orElseThrow();
		}
		if (mapperService == null) {
			mapperService = OsgiServiceUtil.getService(MapperService.class).orElseThrow();
		}

		Result<IBilled> ret = new Result<IBilled>(billed);

		CasemasterResult result = casemasterService.getResult(billed, encounter);
		if (!result.errors.isEmpty()) {
			if (result.errors.get(0).type == CasemasterErrorType.MISSING_OR_INVALID_DIAGNOSIS) {
				return new Result<IBilled>(Result.SEVERITY.WARNING, KOMBINATION,
						"Abrechnung einer Trigger Position " + billed.getCode() + " ohne ICD Diagnose nicht möglich.",
						null, false);
			}
		}
		if (!result.patientCases.isEmpty() && !isPauschale(billed)) {
			for (PatientCase patientCase : result.patientCases) {
				patientClassificationSystemService.getResult(patientCase);

				if (patientCase.getGrouperResult() != null) {
					if (StringUtils.isNoneBlank(patientCase.getGrouperResult().group)
							&& !"NO.ambP".equals(patientCase.getGrouperResult().group)) {
						// TRIGGER
						AmbulatoryAllowance pauschale = AmbulatoryAllowance.getFromCode(
								patientCase.getGrouperResult().group, AmbulantePauschalenTyp.PAUSCHALE,
								patientCase.getEntryDate());
						if (pauschale != null) {
							ret = optifier.add((T) pauschale, encounter, 1, false);
							if (ret.isOK()) {
								for (IBilled encounterBilled : encounter.getBilled()) {
									for (Service service : new ArrayList<>(patientCase.getServices())) {
										if (service.isUsed()
												&& encounterBilled.getCode().replace(".", "").equals(service.code)) {
											optifier.remove(encounterBilled, encounter);
											break;
										}
									}
								}
							}
						}
					} else {
						MapperResult mapperResult = mapperService.getResult(patientCase);
						List<MapperLogEntry> noneInfoLog = new ArrayList<>(
								mapperResult.log.stream().filter(l -> l.level != MapperLogEntryLevel.INFO).toList());
						if (!noneInfoLog.isEmpty()) {
							noneInfoLog.sort((l, r) -> {
								return logEntrySortMap().get(l.level).compareTo(logEntrySortMap().get(r.level));
							});
							noneInfoLog.forEach(li -> LoggerFactory.getLogger(getClass())
									.info("Non info mapper log entry " + li.level + " - " + li.message));
							return new Result<IBilled>(Result.SEVERITY.WARNING, getWarningCode(noneInfoLog.get(0)),
									getWarningMessage(noneInfoLog.get(0)), null, false);
						}
					}
				}
			}
		}
		return ret;
	}

	private Map<MapperLogEntryLevel, Integer> logEntrySortMap() {
		return Map.of(MapperLogEntryLevel.MISSING_MASTER, Integer.valueOf(1),
				MapperLogEntryLevel.TARDOC_VALIDATION_DELETE, Integer.valueOf(2),
				MapperLogEntryLevel.TARDOC_VALIDATION_UPDATE, Integer.valueOf(2),
				MapperLogEntryLevel.LKAAT_VALIDATION_TRIGGER, Integer.valueOf(3),
				MapperLogEntryLevel.LKAAT_VALIDATION_NOT_FOUND, Integer.valueOf(3),
				MapperLogEntryLevel.LKAAT_VALIDATION_DUPLICATE, Integer.valueOf(3));

	}

	private boolean isPauschale(IBilled billed) {
		return billed.getBillable() instanceof IAmbulatoryAllowance
				&& ((IAmbulatoryAllowance) billed.getBillable()).getTyp() == AmbulantePauschalenTyp.PAUSCHALE;
	}

	private String getWarningMessage(MapperLogEntry mapperLogEntry) {
		switch (mapperLogEntry.level) {
		case MISSING_MASTER:
			return "Für die Zuschlagsleistung " + mapperLogEntry.tardocCode
					+ " konnte keine passende Hauptleistung gefunden werden.";
		case TARDOC_VALIDATION_DELETE:
			if (mapperLogEntry.message != null && mapperLogEntry.message.contains("side")) {
				return "Bei der Leistung  " +  mapperLogEntry.tardocCode + "  muss die Seite angegeben werden.";
			}
			return "Die Leistung " +  mapperLogEntry.tardocCode + " ist nicht möglich.";
		case TARDOC_VALIDATION_UPDATE:
			if (StringUtils.isNotBlank(mapperLogEntry.message)) {
				return "Die Leistung " + mapperLogEntry.tardocCode + " ist nicht möglich.\n" + mapperLogEntry.message;
			}
			return "Die Leistung ist nicht möglich.";
		case LKAAT_VALIDATION_TRIGGER:
			return "Für die Trigger Position " + mapperLogEntry.tardocCode
					+ " konnte keine passende Pauschale abgerechnet werden.";
		case LKAAT_VALIDATION_NOT_FOUND:
			return "Die Position ist neben der Pauschale "
					+ mapperLogEntry.serviceCode + " nicht möglich.";
		case LKAAT_VALIDATION_DUPLICATE:
			return "Die Position ist wurde dupliziert.";
		default:
			throw new IllegalArgumentException("Unexpected value: " + mapperLogEntry.level);
		}
	}

	private int getWarningCode(MapperLogEntry mapperLogEntry) {
		switch (mapperLogEntry.level) {
		case MISSING_MASTER:
			return KOMBINATION;
		case TARDOC_VALIDATION_DELETE:
			return LEISTUNGSTYP;
		case TARDOC_VALIDATION_UPDATE:
			return LEISTUNGSTYP;
		case LKAAT_VALIDATION_TRIGGER:
			return LEISTUNGSTYP;
		case LKAAT_VALIDATION_NOT_FOUND:
			return LEISTUNGSTYP;
		case LKAAT_VALIDATION_DUPLICATE:
			return KOMBINATION;
		default:
			throw new IllegalArgumentException("Unexpected value: " + mapperLogEntry.level);
		}
	}
}
