package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedOptifier;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedUtil;
import ch.elexis.base.ch.arzttarife.tarmed.prefs.PreferenceConstants;
import ch.elexis.core.jpa.entities.Verrechnet;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.INativeQuery;
import ch.rgw.tools.Result;

public class TardocLimitation {

	private int amount;

	private String per;
	private String operator;

	private LimitationUnit limitationUnit;
	private int limitationAmount;

	private int electronicBilling;

	private boolean skip = false;

	private TardocLeistung tarmedLeistung;
	private TardocGroup tarmedGroup;

	public enum LimitationUnit {
		LOCATION_SESSION, SIDE, SESSION, PATIENT_SESSION, COVERAGE, STAY, TESTSERIES, PREGNANCY, BIRTH, RADIANTEXPOSURE,
		TRANSMITTAL, AUTOPSY, EXPERTISE, INTERVENTION_SESSION, CATEGORY_DAY, DAY, WEEK, MONTH, YEAR, JOINTREGION,
		REGION_SIDE, JOINTREGION_SIDE, MAINSERVICE, SESSION_YEAR, SESSION_COVERAGE, SESSION_PATIENT;

		public static LimitationUnit from(int parseInt) {
			switch (parseInt) {
			case 6:
				return LOCATION_SESSION;
			case 7:
				return SESSION;
			case 8:
				return COVERAGE;
			case 9:
				return PATIENT_SESSION;
			case 10:
				return SIDE;
			case 11:
				return STAY;
			case 12:
				return TESTSERIES;
			case 13:
				return PREGNANCY;
			case 14:
				return BIRTH;
			case 15:
			case 31:
				return RADIANTEXPOSURE;
			case 16:
				return TRANSMITTAL;
			case 17:
				return AUTOPSY;
			case 18:
				return EXPERTISE;
			case 19:
				return INTERVENTION_SESSION;
			case 20:
				return CATEGORY_DAY;
			case 21:
				return DAY;
			case 22:
				return WEEK;
			case 23:
				return MONTH;
			case 26:
				return YEAR;
			case 40:
				return JOINTREGION;
			case 41:
				return REGION_SIDE;
			case 42:
				return JOINTREGION_SIDE;
			case 45:
				return MAINSERVICE;
			case 51:
				return SESSION_YEAR;
			case 52:
				return SESSION_COVERAGE;
			case 53:
			case 54:
				return SESSION_PATIENT;
			}
			return null;
		}

	}

	/**
	 * Factory method for creating {@link TarmedLimitation} objects of
	 * {@link TarmedLeistung} limitations.
	 *
	 * @param limitation
	 * @return
	 */
	public static TardocLimitation of(String limitation) {
		TardocLimitation ret = new TardocLimitation();

		String[] parts = limitation.split(","); //$NON-NLS-1$
		if (parts.length >= 5) {
			if (parts[0] != null && !parts[0].isEmpty()) {
				ret.operator = parts[0].trim();
			}
			if (parts[1] != null && !parts[1].isEmpty()) {
				ret.amount = Float.valueOf(parts[1].trim()).intValue();
			}
			if (parts[2] != null && !parts[2].isEmpty()) {
				ret.limitationAmount = Float.valueOf(parts[2].trim()).intValue();
			}
			if (parts[3] != null && !parts[3].isEmpty()) {
				ret.per = parts[3].trim();
			}
			if (parts[4] != null && !parts[4].isEmpty()) {
				ret.limitationUnit = LimitationUnit.from(Float.valueOf(parts[4].trim()).intValue());
			}
		}
		if (parts.length >= 6) {
			if (parts[5] != null && !parts[5].isEmpty()) {
				ret.electronicBilling = Float.valueOf(parts[5].trim()).intValue();
			}
		} else {
			ret.electronicBilling = 0;
		}
		return ret;
	}

	public TardocLimitation setTardocLeistung(TardocLeistung tarmedLeistung) {
		this.tarmedLeistung = tarmedLeistung;
		return this;
	}

	public TardocLimitation setTardocGroup(TardocGroup tarmedGroup) {
		this.tarmedGroup = tarmedGroup;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (limitationUnit == LimitationUnit.SESSION) {
			sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
					+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perSession);
		} else if (limitationUnit == LimitationUnit.SIDE) {
			sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
					+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perSide);
		} else if (limitationUnit == LimitationUnit.DAY) {
			sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
					+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perDay);
		} else if (limitationUnit == LimitationUnit.WEEK) {
			if (tarmedGroup != null) {
				sb.append(String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_groupmax,
						tarmedGroup.getCode()) + amount
						+ String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perWeeks,
								limitationAmount));
			} else {
				sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount + String
						.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perWeeks, limitationAmount));
			}
		} else if (limitationUnit == LimitationUnit.MONTH) {
			if (tarmedGroup != null) {
				sb.append(String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_groupmax,
						tarmedGroup.getCode()) + amount
						+ String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perMonth,
								limitationAmount));
			} else {
				sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount + String
						.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perMonth, limitationAmount));
			}
		} else if (limitationUnit == LimitationUnit.YEAR) {
			if (tarmedGroup != null) {
				sb.append(String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_groupmax,
						tarmedGroup.getCode()) + amount
						+ String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perYears,
								limitationAmount));
			} else {
				sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount + String
						.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perYears, limitationAmount));
			}
		} else if (limitationUnit == LimitationUnit.COVERAGE) {
			sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
					+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perCoverage);
		} else if (limitationUnit == LimitationUnit.PATIENT_SESSION) {
			sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
					+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perPatient);
		} else {
			sb.append("amount " + amount + "x unit " + limitationAmount + "x" + limitationUnit);
		}
		return sb.toString();
	}

	public boolean isTestable() {
		return limitationUnit == LimitationUnit.SIDE || limitationUnit == LimitationUnit.SESSION
				|| limitationUnit == LimitationUnit.DAY || limitationUnit == LimitationUnit.WEEK
				|| limitationUnit == LimitationUnit.MONTH || limitationUnit == LimitationUnit.YEAR
				|| limitationUnit == LimitationUnit.COVERAGE || limitationUnit == LimitationUnit.PATIENT_SESSION;
	}

	public Result<IBilled> test(IEncounter kons, IBilled newVerrechnet) {
		if (limitationUnit == LimitationUnit.SIDE || limitationUnit == LimitationUnit.SESSION) {
			return testSideOrSession(kons, newVerrechnet);
		} else if (limitationUnit == LimitationUnit.DAY) {
			return testDay(kons, newVerrechnet);
		} else if (limitationUnit == LimitationUnit.WEEK || limitationUnit == LimitationUnit.MONTH
				|| limitationUnit == LimitationUnit.YEAR) {
			return testDuration(kons, newVerrechnet);
		} else if (limitationUnit == LimitationUnit.COVERAGE) {
			return testCoverage(kons, newVerrechnet);
		} else if (limitationUnit == LimitationUnit.PATIENT_SESSION) {
			return testPatientSession(kons, newVerrechnet);
		}
		return new Result<IBilled>(null);
	}

	private Result<IBilled> testPatientSession(IEncounter kons, IBilled newVerrechnet) {
		Result<IBilled> ret = new Result<IBilled>(null);
		if (shouldSkipTest()) {
			return ret;
		}
		if (limitationAmount == 1 && operator.equals("<=")) {
			IPatient patient = kons.getPatient();
			List<IEncounter> encounters = patient.getCoverages().stream().flatMap(c -> c.getEncounters().stream())
					.collect(Collectors.toList());
			List<IBilled> alreadyBilled = encounters.stream()
					.flatMap(e -> filterWithSameCode(newVerrechnet, e.getBilled()).stream())
					.collect(Collectors.toList());
			double alreadyBilledAmount = alreadyBilled.stream().mapToDouble(b -> b.getAmount()).sum();
			if (alreadyBilledAmount > amount) {
				ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), null, false);
			}
		}
		return ret;
	}

	private Result<IBilled> testCoverage(IEncounter kons, IBilled verrechnet) {
		Result<IBilled> ret = new Result<IBilled>(null);
		if (shouldSkipTest()) {
			return ret;
		}
		if (operator.equals("<=")) {
			if (tarmedGroup == null) {
				List<IBilled> verrechnetByCoverage = getVerrechnetByCoverageAndCode(kons, tarmedLeistung.getCode());
				verrechnetByCoverage = filterWithSameCode(verrechnet, verrechnetByCoverage);
				if (getVerrechnetCount(verrechnetByCoverage) > amount) {
					ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), null,
							false);
				}
			} else {
				List<IBilled> allVerrechnetOfGroup = new ArrayList<>();
				List<String> serviceCodes = tarmedGroup.getServices();
				for (String code : serviceCodes) {
					List<IBilled> verrechnetByCoverageAndCode = getVerrechnetByCoverageAndCode(kons, code);
					allVerrechnetOfGroup.addAll(verrechnetByCoverageAndCode);
				}
				int verrechnetCount = getVerrechnetCount(allVerrechnetOfGroup);
				if (verrechnetCount > amount) {
					ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), null,
							false);
				}
			}
		}
		return ret;
	}

	private Result<IBilled> testDuration(IEncounter kons, IBilled verrechnet) {
		Result<IBilled> ret = new Result<IBilled>(null);
		if (shouldSkipTest()) {
			return ret;
		}
		if (operator.equals("<=")) {
			if (tarmedGroup == null) {
				List<IBilled> verrechnetByMandant = getVerrechnetByRechnungsstellerAndCodeDuringPeriod(kons,
						verrechnet.getBillable().getCode());
				if (getVerrechnetCount(verrechnetByMandant) > amount) {
					ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(),
							verrechnet,
							false);
				}
			} else {
				List<IBilled> allVerrechnetOfGroup = new ArrayList<>();
				List<String> serviceCodes = tarmedGroup.getServices();
				for (String code : serviceCodes) {
					allVerrechnetOfGroup.addAll(getVerrechnetByRechnungsstellerAndCodeDuringPeriod(kons, code));
				}
				if (getVerrechnetCount(allVerrechnetOfGroup) > amount) {
					ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(),
							verrechnet,
							false);
				}
			}
		}
		return ret;
	}

	private int getVerrechnetCount(List<IBilled> verrechnete) {
		int ret = 0;
		for (IBilled verrechnet : verrechnete) {
			ret += verrechnet.getAmount();
		}
		return ret;
	}

	// @formatter:off
	private static final String VERRECHNET_BYPATIENT_ANDCODE = "SELECT leistungen.ID FROM leistungen, behandlungen, faelle"
	+ " WHERE leistungen.deleted = '0'"
	+ " AND leistungen.deleted = behandlungen.deleted"
	+ " AND leistungen.BEHANDLUNG = behandlungen.ID"
	+ " AND leistungen.KLASSE = 'ch.elexis.data.TarmedLeistung'"
	+ " AND faelle.ID = behandlungen.fallID"
	+ " AND faelle.PatientID = ?1"
	+ " AND leistungen.LEISTG_CODE like ?2"
	+ " ORDER BY behandlungen.Datum ASC";
	// @formatter:on

	public static List<IBilled> findVerrechnetByPatientCodeDuringPeriod(IPatient patient, String code) {
		List<IBilled> all = new ArrayList<>();
		INativeQuery nativeQuery = CoreModelServiceHolder.get().getNativeQuery(VERRECHNET_BYPATIENT_ANDCODE);
		Map<Integer, Object> parameterMap = nativeQuery.getIndexedParameterMap(Integer.valueOf(1), patient.getId(),
				Integer.valueOf(2), code + "%");
		Iterator<?> result = nativeQuery.executeWithParameters(parameterMap).iterator();
		while (result.hasNext()) {
			String next = result.next().toString();
			IBilled load = CoreModelServiceHolder.get().load(next, IBilled.class).get();
			all.add(load);
		}
		return all;
	}

	/**
	 * Get {@link Verrechnet} which are in the matching period for the kons and the
	 * code. <br />
	 * The first period starts with the first time a {@link Verrechnet} with the
	 * code was created by the mandant. From then on periods with the duration
	 * specified by {@link LimitationUnit} and limitaiton amount are calculated and
	 * the {@link Verrechnet} during the periods are collected. The collected
	 * {@link Verrechnet} of the period matching the kons date are returned, or
	 * empty if no such period exists yet.
	 *
	 * @param kons
	 * @param code
	 * @return
	 */
	private List<IBilled> getVerrechnetByRechnungsstellerAndCodeDuringPeriod(IEncounter kons, String code) {
		IContact rechnungssteller = kons.getMandator().getBiller();

		if (rechnungssteller != null) {
			List<IBilled> all = findVerrechnetByPatientCodeDuringPeriod(kons.getCoverage().getPatient(), code);
			// filter for matching rechnungssteller
			all = all.parallelStream().filter(billed -> {
				IEncounter encounter = billed.getEncounter();
				IMandator mandator = null;
				IContact biller = null;
				if (encounter != null) {
					mandator = encounter.getMandator();
					if (mandator != null) {
						biller = mandator.getBiller();
						if (biller != null) {
							return biller.equals(rechnungssteller);
						}
					}
				}
				LoggerFactory.getLogger(getClass()).warn("Missing object in chain for IBilled [{}]: [{}], [{}], [{}]",
						billed.getId(), encounter, mandator, biller);
				return false;
			}).collect(Collectors.toList());
			all = filterValidCodeForKonsultation(code, kons, all);
			// now group in time periods since first verrechnet
			LocalDate konsDate = kons.getDate();
			List<VerrechnetPeriod> grouped = getGroupedByPeriod(all);
			// lookup period matching konsDate
			for (VerrechnetPeriod verrechnetPeriod : grouped) {
				if (verrechnetPeriod.isDateInPeriod(konsDate)) {
					return verrechnetPeriod.getVerrechnete();
				}
			}
		}
		return Collections.emptyList();
	}

	private List<VerrechnetPeriod> getGroupedByPeriod(List<IBilled> verrechnete) {
		if (!verrechnete.isEmpty()) {
			List<VerrechnetPeriod> ret = new ArrayList<>();
			for (IBilled verrechnet : verrechnete) {
				if (ret.isEmpty()) {
					ret.add(new VerrechnetPeriod(verrechnet));
				} else {
					boolean added = false;
					for (VerrechnetPeriod verrechnetPeriod : ret) {
						if (verrechnetPeriod.isInPeriod(verrechnet)) {
							verrechnetPeriod.addVerrechnet(verrechnet);
							added = true;
							break;
						}
					}
					// start new period
					if (!added) {
						ret.add(new VerrechnetPeriod(verrechnet));
					}
				}
			}
			return ret;
		}
		return Collections.emptyList();
	}

	private class VerrechnetPeriod {
		private LocalDate start;
		private LocalDate end;

		private List<IBilled> verrechnete;

		private VerrechnetPeriod(IBilled verrechnet) {
			start = verrechnet.getEncounter().getDate();
			if (limitationUnit == LimitationUnit.WEEK) {
				end = start.plus(limitationAmount, ChronoUnit.WEEKS);
			} else if (limitationUnit == LimitationUnit.MONTH) {
				end = start.plus(limitationAmount, ChronoUnit.MONTHS);
			} else if (limitationUnit == LimitationUnit.YEAR) {
				end = start.plus(limitationAmount, ChronoUnit.YEARS);
			}
			verrechnete = new ArrayList<>();
			verrechnete.add(verrechnet);
		}

		public List<IBilled> getVerrechnete() {
			return verrechnete;
		}

		private boolean isInPeriod(IBilled verrechnet) {
			LocalDate matchDate = verrechnet.getEncounter().getDate();
			return isDateInPeriod(matchDate);
		}

		private boolean isDateInPeriod(LocalDate date) {
			return (date.isAfter(start) || date.isEqual(start)) && (date.isBefore(end) || date.isEqual(end));
		}

		private void addVerrechnet(IBilled verrechnet) {
			verrechnete.add(verrechnet);
		}
	}

	// @formatter:off
	private static final String VERRECHNET_BYMANDANT_ANDCODE_DURING = "SELECT leistungen.ID FROM leistungen, behandlungen, faelle"
	+ " WHERE leistungen.deleted = '0'"
	+ " AND leistungen.deleted = behandlungen.deleted"
	+ " AND leistungen.BEHANDLUNG = behandlungen.ID"
	+ " AND leistungen.KLASSE = 'ch.elexis.data.TardocLeistung'"
	+ " AND faelle.ID = behandlungen.fallID"
	+ " AND faelle.PatientID = ?1"
	+ " AND leistungen.LEISTG_CODE like ?2"
	+ " AND behandlungen.Datum >= ?3"
	+ " AND behandlungen.MandantID = ?4";
	// @formatter:on

	private List<IBilled> getVerrechnetByMandantAndCodeDuring(IEncounter kons, String code) {
		LocalDate fromDate = getDuringStartDate(kons);
		IMandator mandant = kons.getMandator();
		List<IBilled> ret = new ArrayList<>();
		if (fromDate != null && mandant != null) {

			INativeQuery nativeQuery = CoreModelServiceHolder.get().getNativeQuery(VERRECHNET_BYMANDANT_ANDCODE_DURING);
			Map<Integer, Object> parameterMap = nativeQuery.getIndexedParameterMap(Integer.valueOf(1),
					kons.getCoverage().getPatient().getId(), Integer.valueOf(2), code + "%", Integer.valueOf(3),
					fromDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), Integer.valueOf(4), mandant.getId());
			Iterator<?> result = nativeQuery.executeWithParameters(parameterMap).iterator();
			while (result.hasNext()) {
				String next = result.next().toString();
				IBilled load = CoreModelServiceHolder.get().load(next, IBilled.class).get();
				ret.add(load);
			}

			// PreparedStatement pstm = PersistentObject.getDefaultConnection()
			// .getPreparedStatement(VERRECHNET_BYMANDANT_ANDCODE_DURING);
			// try {
			// pstm.setString(1, kons.getCoverage().getPatient().getId());
			// pstm.setString(2, code + "%");
			// pstm.setString(3, fromDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			// pstm.setString(4, mandant.getId());
			// ResultSet resultSet = pstm.executeQuery();
			// while (resultSet.next()) {
			// ret.add(Verrechnet.load(resultSet.getString(1)));
			// }
			// resultSet.close();
			// } catch (SQLException e) {
			// LoggerFactory.getLogger(getClass()).error("Error during lookup", e);
			// } finally {
			// PersistentObject.getDefaultConnection().releasePreparedStatement(pstm);
			// }
		}
		return ret;
	}

	// @formatter:off
	private static final String VERRECHNET_BYCOVERAGE_ANDCODE = "SELECT leistungen.ID FROM leistungen, behandlungen"
	+ " WHERE leistungen.deleted = '0'"
	+ " AND leistungen.deleted = behandlungen.deleted"
	+ " AND leistungen.BEHANDLUNG = behandlungen.ID"
	+ " AND leistungen.KLASSE = 'ch.elexis.data.TarmedLeistung'"
	+ " AND leistungen.LEISTG_CODE like ?1"
	+ " AND behandlungen.FallID = ?2";
	// @formatter:on

	private List<IBilled> getVerrechnetByCoverageAndCode(IEncounter kons, String code) {
		List<IBilled> ret = new ArrayList<>();
		if (kons != null && kons.getCoverage() != null) {

			INativeQuery nativeQuery = CoreModelServiceHolder.get().getNativeQuery(VERRECHNET_BYCOVERAGE_ANDCODE);
			Map<Integer, Object> parameterMap = nativeQuery.getIndexedParameterMap(Integer.valueOf(1), code + "%",
					Integer.valueOf(2), kons.getCoverage().getId());
			Iterator<?> result = nativeQuery.executeWithParameters(parameterMap).iterator();
			while (result.hasNext()) {
				String next = result.next().toString();
				IBilled load = CoreModelServiceHolder.get().load(next, IBilled.class).get();
				ret.add(load);
			}
		}
		return ret;
	}

	private LocalDate getDuringStartDate(IEncounter kons) {
		LocalDate konsDate = kons.getDate();
		LocalDate ret = null;
		if (limitationUnit == LimitationUnit.WEEK) {
			ret = konsDate.minus(limitationAmount, ChronoUnit.WEEKS);
		} else if (limitationUnit == LimitationUnit.MONTH) {
			ret = konsDate.minus(limitationAmount, ChronoUnit.MONTHS);
		} else if (limitationUnit == LimitationUnit.YEAR) {
			ret = konsDate.minus(limitationAmount, ChronoUnit.YEARS);
		}
		if (tarmedLeistung != null && ret != null) {
			LocalDate leistungDate = tarmedLeistung.getValidFrom();
			if (ret.isBefore(leistungDate)) {
				ret = leistungDate;
			}
		}
		return ret;
	}

	/**
	 * Filter the list of {@link Verrechnet} that only instances with the same code
	 * field (Tarmed code, startdate and law) as the provided {@link Verrechnet} are
	 * in the resulting list.
	 *
	 * @param verrechnet
	 * @return
	 */
	private List<IBilled> filterWithSameCode(IBilled verrechnet, List<IBilled> list) {
		List<IBilled> ret = new ArrayList<>();
		String matchCode = verrechnet.getCode();
		if (matchCode != null && !matchCode.isEmpty()) {
			for (IBilled element : list) {
				if (matchCode.equals(element.getCode())) {
					ret.add(element);
				}
			}
		}
		return ret;
	}

	/**
	 * Filter the list of {@link IBilled} that only instances with the same code
	 * field (Tarmed code, startdate and law) as the valid {@link TarmedLeistung}
	 * for the provided {@link Konsultation}. This filters {@link IBilled} with a
	 * {@link TarmedLeistung} from a different catalog.
	 *
	 * @param verrechnet
	 * @return
	 */
	private List<IBilled> filterValidCodeForKonsultation(String code, IEncounter kons, List<IBilled> list) {
		List<IBilled> ret = new ArrayList<>();
		BillingLaw law = kons.getCoverage().getBillingSystem().getLaw();
		IBillable validForKons = TardocLeistung.getFromCode(code, kons.getDate(), law.name());
		if (validForKons != null) {
			String matchCode = validForKons.getCode();
			if (matchCode != null && !matchCode.isEmpty()) {
				for (IBilled element : list) {
					if (matchCode.equals(element.getCode())) {
						ret.add(element);
					}
				}
			}
		} else {
			ret.addAll(list);
		}
		return ret;
	}

	private Result<IBilled> testDay(IEncounter kons, IBilled verrechnet) {
		Result<IBilled> ret = new Result<IBilled>(null);
		if (shouldSkipTest()) {
			return ret;
		}
		if (limitationAmount == 1 && operator.equals("<=")) {
			if (getVerrechnetAmount(verrechnet) > amount) {
				ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), null, false);
			}
		}
		return ret;
	}

	private Result<IBilled> testSideOrSession(IEncounter kons, IBilled verrechnet) {
		Result<IBilled> ret = new Result<IBilled>(null);
		if (shouldSkipTest()) {
			return ret;
		}
		if (limitationAmount == 1 && operator.equals("<=")) {
			if (getVerrechnetAmount(verrechnet) > amount) {
				if (limitationUnit == LimitationUnit.SESSION) {
					ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), null,
							false);
				} else if (limitationUnit == LimitationUnit.SIDE) {
					ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), null,
							false);
				}
			}
		}
		return ret;
	}

	private List<IBilled> getSameVerrechnetOfKons(IBilled verrechnet) {
		List<IBilled> ret = new ArrayList<>();
		Class<? extends IBilled> verrechnetClass = verrechnet.getClass();
		String verrechnetCode = verrechnet.getCode();
		if (verrechnetClass != null && verrechnetCode != null) {
			IEncounter kons = verrechnet.getEncounter();
			for (IBilled leistung : kons.getBilled()) {
				if (verrechnetClass.equals(verrechnet.getClass()) && verrechnetCode.equals(leistung.getCode())) {
					// for side limit, only add with same side
					if (limitationUnit == LimitationUnit.SIDE) {
						if (TardocLeistung.getSide(verrechnet).equals(TardocLeistung.getSide(leistung))) {
							ret.add(leistung);
						}
					} else {
						ret.add(leistung);
					}
				}
			}
		}
		return ret;
	}

	private int getVerrechnetAmount(IBilled verrechnet) {
		List<IBilled> sameVerrechnet = getSameVerrechnetOfKons(verrechnet);
		return getVerrechnetCount(sameVerrechnet);
	}

	private boolean shouldSkipTest() {
		if (skip) {
			return skip;
		}
		return shouldSkipElectronicBilling();
	}

	private boolean shouldSkipElectronicBilling() {
		if (electronicBilling > 0) {
			return TarmedUtil.getConfigValue(getClass(), IMandator.class, PreferenceConstants.BILL_ELECTRONICALLY,
					false);
		}
		return false;
	}

	public LimitationUnit getLimitationUnit() {
		return limitationUnit;
	}

	public int getAmount() {
		return amount;
	}

	public void setSkip(boolean value) {
		this.skip = true;
	}
}
