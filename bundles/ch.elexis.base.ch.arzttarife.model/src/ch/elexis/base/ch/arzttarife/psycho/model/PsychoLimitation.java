package ch.elexis.base.ch.arzttarife.psycho.model;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation.LimitationUnit;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedOptifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.INativeQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.rgw.tools.Result;

public class PsychoLimitation {

	private static final String SEPARATOR = ";";

	private static Map<String, List<PsychoLimitation>> limitations;

	public synchronized static List<PsychoLimitation> get(IPsychoLeistung leistung) {
		if (limitations == null) {
			limitations = loadLimitations();
		}
		return limitations.get(leistung.getCode());

	}

	private static Map<String, List<PsychoLimitation>> loadLimitations() {
		limitations = new HashMap<String, List<PsychoLimitation>>();
		Optional<IBlob> blob = CoreModelServiceHolder.get().load(LIMITATION_ID, IBlob.class);
		if (blob.isPresent()) {
			StringReader stringReader = new StringReader(blob.get().getStringContent());
			try (CSVReader reader = new CSVReaderBuilder(stringReader)
					.withCSVParser(new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build())
					.withKeepCarriageReturn(false).withSkipLines(1).build()) {
				for (String[] line : reader.readAll()) {
					if (StringUtils.isNotBlank(line[0])) {
						List<PsychoLimitation> list = limitations.get(line[0]);
						if (list == null) {
							list = new ArrayList<>();
						}

						PsychoLimitation limit = new PsychoLimitation(TYPE.AMOUNT);
						limit.posnr = line[0];
						if (line[1].indexOf("x") != -1) {
							String[] parts = line[1].split("x");
							limit.limitSessions = Integer.parseInt(parts[0]);
							limit.limitationAmount = Integer.parseInt(parts[1]);
						} else {
							limit.limitationAmount = Integer.parseInt(line[1]);
						}
						limit.limitReference = line[2];
						limit.including = line[3];
						list.add(limit);

						if (StringUtils.isNotBlank(line[4])) {
							PsychoLimitation limitExclusion = new PsychoLimitation(TYPE.EXCLUSION);
							limitExclusion.exclusion = line[4];
							list.add(limitExclusion);
						}
						if (StringUtils.isNotBlank(line[5])) {
							PsychoLimitation limitExclusive = new PsychoLimitation(TYPE.EXCLUSIVE);
							limitExclusive.exclusive = line[5];
							list.add(limitExclusive);
						}
						limitations.put(limit.posnr, list);
					}
				}
			} catch (IOException e) {
				LoggerFactory.getLogger(PsychoLimitation.class).error("Exception loading limitations", e);
			}
		}

		return limitations;
	}

	public enum TYPE {
		AMOUNT, EXCLUSION, EXCLUSIVE
	}

	private TYPE type;

	private String posnr;

	private String including;

	private Integer limitationAmount;

	private Integer limitSessions;

	private String limitReference;

	private String exclusion;

	private String exclusive;

	public PsychoLimitation(TYPE type) {
		this.type = type;
	}

	public TYPE getType() {
		return type;
	}

	public Result<IBilled> test(IEncounter encounter, IBilled newBilled) {
		return test(encounter, newBilled, null);
	}

	public Result<IBilled> test(IEncounter encounter, IBilled newBilled, IBilled newIncludingBilled) {
		if (type == TYPE.AMOUNT) {
			if ("sitzung".equalsIgnoreCase(limitReference)) {
				return testSession(encounter, newBilled, newIncludingBilled);
			} else if (limitReference.toLowerCase().contains("tag")) {
				return testDays(encounter, newBilled, newIncludingBilled);
			}
		} else if (type == TYPE.EXCLUSION) {
			return testExclusion(encounter, newBilled);
		} else if (type == TYPE.EXCLUSIVE) {
			return testExclusive(encounter, newBilled);
		}
		return new Result<IBilled>(null);
	}

	private Result<IBilled> testExclusion(IEncounter encounter, IBilled newBilled) {
		Result<IBilled> ret = new Result<IBilled>(null);
		List<String> exclusionCodes = getExclusionCodes();
		if (!exclusionCodes.isEmpty()) {
			List<String> encounterCodes = encounter.getBilled().stream().map(b -> b.getCode()).toList();
			if (exclusionCodes.stream().filter(ec -> encounterCodes.contains(ec)).findFirst().isPresent()) {
				ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.EXKLUSIVE, toString(), newBilled,
						false);
			}
		}
		return ret;
	}

	private Result<IBilled> testExclusive(IEncounter encounter, IBilled newBilled) {
		Result<IBilled> ret = new Result<IBilled>(null);
		List<String> exclusiveCodes = getExclusiveCodes();
		if (!exclusiveCodes.isEmpty()) {
			List<String> encounterCodes = encounter.getBilled().stream().map(b -> b.getCode()).toList();
			if (exclusiveCodes.stream().filter(ec -> encounterCodes.contains(ec)).findFirst().isEmpty()) {
				ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.EXKLUSIVE, toString(), newBilled,
						false);
			}
		}
		return ret;
	}

	public List<String> getExclusiveCodes() {
		if (type == TYPE.EXCLUSIVE && StringUtils.isNotBlank(exclusive)) {
			return Arrays.asList(exclusive.split(SEPARATOR));
		}
		return Collections.emptyList();
	}

	public List<String> getExclusionCodes() {
		if (type == TYPE.EXCLUSION && StringUtils.isNotBlank(exclusion)) {
			return Arrays.asList(exclusion.split(SEPARATOR));
		}
		return Collections.emptyList();
	}

	private Result<IBilled> testDays(IEncounter encounter, IBilled newBilled, IBilled newIncludingBilled) {
		Result<IBilled> ret = new Result<IBilled>(null);
		List<IBilled> verrechnetByMandant = Collections.emptyList();
		if (limitReference.toLowerCase().contains("rechnungssteller")) {
			verrechnetByMandant = new ArrayList<>(
					getVerrechnetByRechnungsstellerAndCodeDuringPeriod(encounter, newBilled.getBillable().getCode()));
		} else {
			verrechnetByMandant = new ArrayList<>(getVerrechnetByPatientAndRechnungsstellerAndCodeDuringPeriod(
					encounter, newBilled.getBillable().getCode()));
		}
		// replace value from database with current
		verrechnetByMandant.remove(newBilled);
		verrechnetByMandant.add(newBilled);
		if (limitSessions != null && limitSessions > 0) {
			Map<IEncounter, List<IBilled>> sessionBilledMap = getBilledPerSession(verrechnetByMandant);
			if (sessionBilledMap.size() > limitSessions) {
				ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), newBilled,
						false);
			} else {
				for (List<IBilled> billedList : sessionBilledMap.values()) {
					if (getBilledCount(billedList) > limitationAmount) {
						ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(),
								newBilled, false);
						break;
					}
				}
			}
		} else {
			if (getBilledCount(verrechnetByMandant) > limitationAmount) {
				ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), newBilled,
						false);
			}
		}
		return ret;
	}

	private Map<IEncounter, List<IBilled>> getBilledPerSession(List<IBilled> billedList) {
		Map<IEncounter, List<IBilled>> ret = new HashMap<IEncounter, List<IBilled>>();
		for (IBilled billed : billedList) {
			List<IBilled> list = ret.get(billed.getEncounter());
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(billed);
			ret.put(billed.getEncounter(), list);
		}
		return ret;
	}

	private int getDays() {
		if (Character.isDigit(limitReference.charAt(0))) {
			String[] parts = limitReference.split(" ");
			return Integer.parseInt(parts[0]);
		} else {
			return 1;
		}
	}

	private Result<IBilled> testSession(IEncounter encounter, IBilled billed, IBilled newIncludingBilled) {
		Result<IBilled> ret = new Result<IBilled>(null);
		if (getBilledAmount(billed, newIncludingBilled) > limitationAmount) {
			ret = new Result<IBilled>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION, toString(), null, false);
		}
		return ret;
	}

	private int getBilledAmount(IBilled newBilled, IBilled newIncludingBilled) {
		List<IBilled> sameVerrechnet = getSameBilledOfKons(newBilled);
		// replace value from database with current
		sameVerrechnet.remove(newBilled);
		sameVerrechnet.add(newBilled);
		if (StringUtils.isNotBlank(including)) {
			List<IBilled> includingBilled = new ArrayList<>(
					newBilled.getEncounter().getBilled().stream().filter(b -> isIncluding(b.getCode())).toList());
			if (!includingBilled.isEmpty()) {
				if (newIncludingBilled != null) {
					includingBilled.remove(newIncludingBilled);
					includingBilled.add(newIncludingBilled);
				}
				sameVerrechnet.addAll(includingBilled);
			}
		}
		return getBilledCount(sameVerrechnet);
	}

	private List<IBilled> getSameBilledOfKons(IBilled verrechnet) {
		List<IBilled> ret = new ArrayList<>();
		Class<? extends IBilled> verrechnetClass = verrechnet.getClass();
		String verrechnetCode = verrechnet.getCode();
		if (verrechnetClass != null && verrechnetCode != null) {
			IEncounter kons = verrechnet.getEncounter();
			for (IBilled leistung : kons.getBilled()) {
				if (verrechnetClass.equals(verrechnet.getClass()) && verrechnetCode.equals(leistung.getCode())) {
					ret.add(leistung);
				}
			}
		}
		return ret;
	}

	private int getBilledCount(List<IBilled> billedList) {
		int ret = 0;
		for (IBilled billed : billedList) {
			ret += billed.getAmount();
		}
		return ret;
	}

	/**
	 * Get {@link IBilled} which are in the matching period for the kons and the
	 * code. <br />
	 * The first period starts with the first time a {@link IBilled} with the code
	 * was created by the mandant. From then on periods with the duration specified
	 * by {@link LimitationUnit} and limitaiton amount are calculated and the
	 * {@link IBilled} during the periods are collected. The collected
	 * {@link IBilled} of the period matching the kons date are returned, or empty
	 * if no such period exists yet.
	 *
	 * @param kons
	 * @param code
	 * @return
	 */
	private List<IBilled> getVerrechnetByRechnungsstellerAndCodeDuringPeriod(IEncounter kons, String code) {
		IContact rechnungssteller = kons.getMandator().getBiller();

		if (rechnungssteller != null) {
			List<IBilled> all = CoreModelServiceHolder.get().getQuery(IBilled.class)
					.and("klasse", COMPARATOR.EQUALS, "ch.elexis.data.PsychoLeistung")
					.and("leistungenCode", COMPARATOR.LIKE, code + "%").execute();
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
			// now group in time periods since first verrechnet
			LocalDate konsDate = kons.getDate();
			List<BilledPeriod> grouped = getGroupedByPeriod(all);
			// lookup period matching konsDate
			for (BilledPeriod verrechnetPeriod : grouped) {
				if (verrechnetPeriod.isDateInPeriod(konsDate)) {
					return verrechnetPeriod.getBilledList();
				}
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Get {@link IBilled} which are in the matching period for the kons and the
	 * code. <br />
	 * The first period starts with the first time a {@link IBilled} with the code
	 * was created by the mandant. From then on periods with the duration specified
	 * by {@link LimitationUnit} and limitaiton amount are calculated and the
	 * {@link IBilled} during the periods are collected. The collected
	 * {@link IBilled} of the period matching the kons date are returned, or empty
	 * if no such period exists yet.
	 *
	 * @param kons
	 * @param code
	 * @return
	 */
	private List<IBilled> getVerrechnetByPatientAndRechnungsstellerAndCodeDuringPeriod(IEncounter kons, String code) {
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
			// now group in time periods since first verrechnet
			LocalDate konsDate = kons.getDate();
			List<BilledPeriod> grouped = getGroupedByPeriod(all);
			// lookup period matching konsDate
			for (BilledPeriod verrechnetPeriod : grouped) {
				if (verrechnetPeriod.isDateInPeriod(konsDate)) {
					return verrechnetPeriod.getBilledList();
				}
			}
		}
		return Collections.emptyList();
	}

	// @formatter:off
	private static final String VERRECHNET_BYPATIENT_ANDCODE = "SELECT leistungen.ID FROM leistungen, behandlungen, faelle"
	+ " WHERE leistungen.deleted = '0'"
	+ " AND leistungen.deleted = behandlungen.deleted"
	+ " AND leistungen.BEHANDLUNG = behandlungen.ID"
	+ " AND leistungen.KLASSE = 'ch.elexis.data.PsychoLeistung'"
	+ " AND faelle.ID = behandlungen.fallID"
	+ " AND faelle.PatientID = ?1"
	+ " AND leistungen.LEISTG_CODE like ?2"
	+ " ORDER BY behandlungen.Datum ASC";
	// @formatter:on

	public static final String LIMITATION_ID = "Psycho_Limitation";

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

	private List<BilledPeriod> getGroupedByPeriod(List<IBilled> billedList) {
		if (!billedList.isEmpty()) {
			List<BilledPeriod> ret = new ArrayList<>();
			for (IBilled verrechnet : billedList) {
				if (ret.isEmpty()) {
					ret.add(new BilledPeriod(verrechnet));
				} else {
					boolean added = false;
					for (BilledPeriod verrechnetPeriod : ret) {
						if (verrechnetPeriod.isInPeriod(verrechnet)) {
							verrechnetPeriod.addVerrechnet(verrechnet);
							added = true;
							break;
						}
					}
					// start new period
					if (!added) {
						ret.add(new BilledPeriod(verrechnet));
					}
				}
			}
			return ret;
		}
		return Collections.emptyList();
	}

	private class BilledPeriod {
		private LocalDate start;
		private LocalDate end;

		private List<IBilled> verrechnete;

		private BilledPeriod(IBilled verrechnet) {
			start = verrechnet.getEncounter().getDate();
			end = start.plus(getDays(), ChronoUnit.DAYS);
			verrechnete = new ArrayList<>();
			verrechnete.add(verrechnet);
		}

		public List<IBilled> getBilledList() {
			return verrechnete;
		}

		private boolean isInPeriod(IBilled verrechnet) {
			LocalDate matchDate = verrechnet.getEncounter().getDate();
			return isDateInPeriod(matchDate);
		}

		private boolean isDateInPeriod(LocalDate date) {
			return (date.isAfter(start) || date.isEqual(start)) && (date.isBefore(end) || date.isEqual(end));
		}

		private void addVerrechnet(IBilled billed) {
			verrechnete.add(billed);
		}
	}

	public boolean isIncluding(String code) {
		if (StringUtils.isNotBlank(including)) {
			return including.equals(code);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (type == TYPE.AMOUNT) {
			if ("sitzung".equalsIgnoreCase(limitReference)) {
				sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + limitationAmount
						+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perSession);
			} else if (limitReference.toLowerCase().contains("tag")) {
				if (getDays() > 1) {
					sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + limitationAmount
							+ String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perDays, getDays()));
				} else {
					if (limitSessions != null && limitSessions > 0) {
						sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + limitationAmount
								+ " Mal in " + limitSessions + " Konsultationen pro Tag");
					} else {
						sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + limitationAmount
								+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perDay);
					}
				}
			}
		} else if (type == TYPE.EXCLUSIVE) {
			sb.append("Code nur mit den Leistungen " + getExclusiveCodes().stream().collect(Collectors.joining(","))
					+ " verrechenbar");
		} else if (type == TYPE.EXCLUSION) {
			sb.append("Code nicht mit den Leistungen " + getExclusionCodes().stream().collect(Collectors.joining(","))
					+ " verrechenbar");
		}
		return sb.toString();
	}

	public static void update() {
		limitations = null;
	}
}
