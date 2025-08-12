package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher.CasemasterService;
import ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher.MapperService;
import ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher.PatientClassificationSystemService;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedUtil;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.model.builder.IBilledBuilder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.oaat_otma.PatientCase;
import ch.oaat_otma.casemaster.CasemasterResult;
import ch.oaat_otma.grouper.GrouperDecision;
import ch.oaat_otma.mapper.MapperLogEntry;
import ch.oaat_otma.mapper.MapperLogEntry.MapperLogEntryLevel;
import ch.oaat_otma.mapper.MapperResult;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

public class TardocOptifier implements IBillableOptifier<TardocLeistung> {

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

	private Map<String, Object> contextMap;

	private CasemasterService casemasterService;

	private PatientClassificationSystemService patientClassificationSystemService;

	private MapperService mapperService;

	@Override
	public Result<IBilled> add(TardocLeistung code, IEncounter encounter, double amount, boolean save) {
		int amountInt = doubleToInt(amount);
		boolean setNonIntAmount = amount % 1 != 0;
		Result<IBilled> result = null;
		try {
			if (!code.isChapter() && amountInt >= 1) {
				result = add(code, encounter, save);
				if (amountInt == 1) {
					return result;
				}
				for (int i = 2; i <= amountInt; i++) {
					Result<IBilled> intermediateResult = add(code, encounter, save);
					if (!intermediateResult.isOK()) {
						result.addMessage(SEVERITY.WARNING, intermediateResult.toString(), result.get());
						return result;
					} else {
						result = intermediateResult;
					}
				}
				return result;
			} else {
				return Result.OK();
			}
		} finally {
			if (setNonIntAmount && result != null && result.get() != null) {
				result.get().setAmount(amount);
				if (save) {
					CoreModelServiceHolder.get().save(result.get());
				}
			}
		}
	}

	private Result<IBilled> add(TardocLeistung code, IEncounter encounter, boolean save) {
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

		boolean bOptify = TarmedUtil.getConfigValue(getClass(), IMandator.class, Preferences.LEISTUNGSCODES_OPTIFY,
				true);

		boolean bAllowOverrideStrict = TarmedUtil.getConfigValue(getClass(), IUser.class,
				Preferences.LEISTUNGSCODES_ALLOWOVERRIDE_STRICT, false);

		IBilled newVerrechnet = initializeBilled(code, encounter, save);

		List<IEncounter> encounters = encounter.getCoverage().getEncounters().stream()
				.filter(e -> e.getInvoice() == null || e.getInvoiceState() == InvoiceState.CANCELLED).toList();
		CasemasterResult result = casemasterService.getResult(encounters);
		if (!result.errors.isEmpty()) {
			return new Result<IBilled>(Result.SEVERITY.WARNING, KOMBINATION, "Für die Zuschlagsleistung "
					+ code.getCode() + " konnte keine passende Hauptleistung gefunden werden.", null, false);
		}
		if (!result.patientCases.isEmpty()) {
			for (PatientCase patientCase : result.patientCases) {
				List<GrouperDecision> grouperDecisions = patientClassificationSystemService.getResult(patientCase);
				if (!grouperDecisions.isEmpty()) {
					System.out.println("asfas");
				}
				MapperResult mapperResult = mapperService.getResult(patientCase);
				List<MapperLogEntry> noneInfoLog = mapperResult.log.stream()
						.filter(l -> l.level != MapperLogEntryLevel.INFO).toList();
				if (!noneInfoLog.isEmpty()) {
					noneInfoLog.forEach(li -> System.out.println(li));
					return new Result<IBilled>(
							Result.SEVERITY.WARNING, getWarningCode(noneInfoLog.get(0)),
							getWarningMessage(noneInfoLog.get(0)),
							null, false);
				}
			}
		}
		return new Result<IBilled>(newVerrechnet);
	}

	private String getWarningMessage(MapperLogEntry mapperLogEntry) {
		switch (mapperLogEntry.level) {
		case MISSING_MASTER:
			return "Für die Zuschlagsleistung " + mapperLogEntry.tardocCode
					+ " konnte keine passende Hauptleistung gefunden werden.";
		default:
			throw new IllegalArgumentException("Unexpected value: " + mapperLogEntry.level);
		}
	}

	private int getWarningCode(MapperLogEntry mapperLogEntry) {
		switch (mapperLogEntry.level) {
		case MISSING_MASTER:
			return KOMBINATION;
		default:
			throw new IllegalArgumentException("Unexpected value: " + mapperLogEntry.level);
		}
	}

	@Override
	public void putContext(String key, Object value) {
		if (contextMap == null) {
			contextMap = new HashMap<String, Object>();
		}
		contextMap.put(key, value);
	}

	@Override
	public void clearContext() {
		if (contextMap != null) {
			contextMap.clear();
		}
	}

	@Override
	public Result<IBilled> remove(IBilled billed, IEncounter encounter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<IBillingSystemFactor> getFactor(IEncounter encounter) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	private IBilled initializeBilled(TardocLeistung code, IEncounter kons, boolean save) {
		IContact biller = ContextServiceHolder.get().getActiveUserContact().get();
		IBilled ret = new IBilledBuilder(CoreModelServiceHolder.get(), code, kons, biller).build();
		ret.setPoints(code.getAL(kons.getMandator()) + code.getIPL());
		Optional<IBillingSystemFactor> systemFactor = getFactor(kons);
		if (systemFactor.isPresent()) {
			ret.setFactor(systemFactor.get().getFactor());
		} else {
			ret.setFactor(1.0);
		}
		if (save) {
			CoreModelServiceHolder.get().save(ret);
		}
		return ret;
	}

	/**
	 * Get double as int rounded half up.
	 *
	 * @param value
	 * @return
	 */
	private int doubleToInt(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(0, RoundingMode.HALF_UP);
		if (bd.intValue() > 0) {
			return bd.intValue();
		} else {
			return 1;
		}
	}
}
