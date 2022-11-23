/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.arzttarife.model.service.ConfigServiceHolder;
import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation.LimitationUnit;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.TarmedLeistungAge;
import ch.elexis.base.ch.arzttarife.tarmed.prefs.PreferenceConstants;
import ch.elexis.base.ch.arzttarife.tarmed.prefs.RechnungsPrefs;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.jpa.entities.Verrechnet;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.builder.IBilledBuilder;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;
import ch.rgw.tools.TimeTool;

/**
 * Dies ist eine Beispielimplementation des IOptifier Interfaces, welches einige
 * einfache Checks von Tarmed-Verrechnungen durchführt
 *
 * @author gerry
 *
 */
public class TarmedOptifier implements IBillableOptifier<TarmedLeistung> {
	private static final String TL = "TL"; //$NON-NLS-1$
	private static final String AL = "AL"; //$NON-NLS-1$
	private static final String AL_NOTSCALED = "AL_NOTSCALED"; //$NON-NLS-1$
	private static final String AL_SCALINGFACTOR = "AL_SCALINGFACTOR"; //$NON-NLS-1$
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

	private static final String CHAPTER_XRAY = "39.02";
	private static final String DEFAULT_TAX_XRAY_ROOM = "39.2000";

	boolean save = true;
	boolean bOptify = true;
	boolean bAllowOverrideStrict = false;
	private IBilled newVerrechnet;
	private String newVerrechnetSide;

	private Map<String, Object> contextMap;

	@Override
	public synchronized void putContext(String key, Object value) {
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

	/**
	 * Hier kann eine Konsultation als Ganzes nochmal überprüft werden
	 */
	public Result<Object> optify(IEncounter kons) {
		LinkedList<TarmedLeistung> postponed = new LinkedList<TarmedLeistung>();
		for (IBilled vv : kons.getBilled()) {
			IBillable iv = vv.getBillable();
			if (iv instanceof TarmedLeistung) {
				TarmedLeistung tl = (TarmedLeistung) iv;
				String tcid = tl.getCode();
				if ((tcid.equals("35.0020")) || (tcid.equals("04.1930")) //$NON-NLS-1$ //$NON-NLS-2$
						|| tcid.startsWith("00.25")) { //$NON-NLS-1$
					postponed.add(tl);
				}
			}
		}
		return null;
	}

	@Override
	public Result<IBilled> add(TarmedLeistung code, IEncounter kons, double amount, boolean save) {
		this.save = save;
		int amountInt = doubleToInt(amount);
		boolean setNonIntAmount = amount % 1 != 0;
		Result<IBilled> result = null;
		try {
			if (!code.isChapter() && amountInt >= 1) {
				result = add(code, kons);
				if (amountInt == 1) {
					return result;
				}
				for (int i = 2; i <= amountInt; i++) {
					Result<IBilled> intermediateResult = add(code, kons);
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

	/**
	 * Eine Verrechnungsposition zufügen. Der Optifier muss prüfen, ob die
	 * Verrechnungsposition im Kontext der übergebenen Konsultation verwendet werden
	 * kann und kann sie ggf. zurückweisen oder modifizieren.
	 */
	public Result<IBilled> add(TarmedLeistung code, IEncounter kons) {
		if (!(code instanceof TarmedLeistung)) {
			return new Result<IBilled>(Result.SEVERITY.ERROR, LEISTUNGSTYP, Messages.TarmedOptifier_BadType, null,
					true);
		}

		bOptify = TarmedUtil.getConfigValue(getClass(), IMandator.class, Preferences.LEISTUNGSCODES_OPTIFY, true);

		bAllowOverrideStrict = TarmedUtil.getConfigValue(getClass(), IUser.class,
				Preferences.LEISTUNGSCODES_ALLOWOVERRIDE_STRICT, false);

		TarmedLeistung tc = code;
		List<IBilled> lst = kons.getBilled();
		/*
		 * TODO Hier checken, ob dieser code mit der Dignität und Fachspezialisierung
		 * des aktuellen Mandanten usw. vereinbar ist
		 */

		Map<String, String> ext = tc.getExtension().getLimits();

		// Gültigkeit gemäss Datum und Alter prüfen
		if (bOptify) {
			TimeTool date = new TimeTool(kons.getDate());
			LocalDate dVon = code.getValidFrom();
			if (dVon != null) {
				TimeTool tVon = new TimeTool(dVon);
				if (date.isBefore(tVon)) {
					return new Result<IBilled>(Result.SEVERITY.WARNING, NOTYETVALID,
							code.getCode() + Messages.TarmedOptifier_NotYetValid, null, false);
				}
			}
			LocalDate dBis = code.getValidTo();
			if (dBis != null) {
				TimeTool tBis = new TimeTool(dBis);
				if (date.isAfter(tBis)) {
					return new Result<IBilled>(Result.SEVERITY.WARNING, NOMOREVALID,
							code.getCode() + Messages.TarmedOptifier_NoMoreValid, null, false);
				}
			}
			String ageLimits = ext.get(ch.elexis.core.jpa.entities.TarmedLeistung.EXT_FLD_SERVICE_AGE);
			if (ageLimits != null && !ageLimits.isEmpty()) {
				String errorMessage = checkAge(ageLimits, kons);
				if (errorMessage != null) {
					return new Result<IBilled>(Result.SEVERITY.WARNING, PATIENTAGE, errorMessage, null, false);
				}
			}
		}

		// replace code with increased treatment code for patients with increased
		// treatment set
		if (TarmedUtil.isIncreasedTreatment(kons.getPatient())) {
			Optional<String> increasedCode = TarmedUtil.getIncreasedTreatmentCode(code);
			if (increasedCode.isPresent()) {
				TarmedLeistung loaded = getKonsVerrechenbar(increasedCode.get(), kons);
				if (loaded != null) {
					code = loaded;
				}
			}
		}

		newVerrechnet = null;
		newVerrechnetSide = null;
		// Korrekter Fall Typ prüfen, und ggf. den code ändern
		if (tc.getCode().matches("39.002[01]") || tc.getCode().matches("39.001[0156]")) {
			String gesetz = kons.getCoverage().getBillingSystem().getLaw().name();
			if (gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0011")) {
				return this.add(getKonsVerrechenbar("39.0010", kons), kons);
			} else if (!gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0010")) {
				return this.add(getKonsVerrechenbar("39.0011", kons), kons);
			}

			if (gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0016")) {
				return this.add(getKonsVerrechenbar("39.0015", kons), kons);
			} else if (!gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0015")) {
				return this.add(getKonsVerrechenbar("39.0016", kons), kons);
			}

			if (gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0021")) {
				return this.add(getKonsVerrechenbar("39.0020", kons), kons);
			} else if (!gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0020")) {
				return this.add(getKonsVerrechenbar("39.0021", kons), kons);
			}
		}

		if (tc.getCode().matches("35.0020")) {
			List<IBilled> opCodes = getOPList(lst);
			List<IBilled> opReduction = getVerrechnetMatchingCode(lst, "35.0020");
			// updated reductions to codes, and get not yet reduced codes
			List<IBilled> availableCodes = updateOPReductions(opCodes, opReduction);
			if (availableCodes.isEmpty()) {
				return new Result<IBilled>(Result.SEVERITY.WARNING, KOMBINATION, code.getCode(), null, false);
			}
			for (IBilled verrechnet : availableCodes) {
				newVerrechnet = initializeBilled(code, kons);
				mapOpReduction(verrechnet, newVerrechnet);
			}
			return new Result<IBilled>(null);
		}

		// Ist der Hinzuzufügende Code vielleicht schon in der Liste? Dann
		// nur Zahl erhöhen.
		for (IBilled v : lst) {
			if (isInstance(v, code)) {
				if (!tc.requiresSide()) {
					newVerrechnet = v;
					if (!(",00.2530,00.2570,00.2550,00.2590,04.0620,04.1930,06.0430,06.0440,06.0730,06.0740,07.0300,24.0250,24.3250,28.0020,"
							.contains("," + code.getCode()))) {
						newVerrechnet.setAmount(newVerrechnet.getAmount() + 1);
					}
					saveBilled();
					break;
				}
			}
		}

		if (tc.requiresSide()) {
			newVerrechnetSide = getNewVerrechnetSideOrIncrement(code, lst);
		}

		// Ausschliessende Kriterien prüfen ("Nicht zusammen mit")
		if (newVerrechnet == null) {
			newVerrechnet = initializeBilled(code, kons);

			// make sure side is initialized
			if (tc.requiresSide()) {
				newVerrechnet.setExtInfo(Constants.FLD_EXT_SIDE, newVerrechnetSide);
			}
			// Exclusionen
			if (bOptify) {
				TarmedLeistung newTarmed = code;
				for (IBilled v : lst) {
					if (v.getBillable() instanceof TarmedLeistung) {
						TarmedLeistung tarmed = (TarmedLeistung) v.getBillable();
						if (tarmed != null) {
							// check if new has an exclusion for this verrechnet
							// tarmed
							Result<IBilled> resCompatible = isCompatible(v, tarmed, newVerrechnet, newTarmed, kons);
							if (resCompatible.isOK()) {
								// check if existing tarmed has exclusion for
								// new one
								resCompatible = isCompatible(newVerrechnet, newTarmed, v, tarmed, kons);
							}

							if (!resCompatible.isOK()) {
								deleteBilled(newVerrechnet);
								return resCompatible;
							}
						}
					}
				}

				if (newVerrechnet.getCode().equals("00.0750") || newVerrechnet.getCode().equals("00.0010")) {
					String excludeCode = null;
					if (newVerrechnet.getCode().equals("00.0010")) {
						excludeCode = "00.0750";
					} else {
						excludeCode = "00.0010";
					}
					for (IBilled v : lst) {
						if (v.getCode().equals(excludeCode)) {
							deleteBilled(newVerrechnet);
							return new Result<IBilled>(Result.SEVERITY.WARNING, EXKLUSION,
									"00.0750 ist nicht im Rahmen einer ärztlichen Beratung 00.0010 verrechnenbar.", //$NON-NLS-1$
									null, false);
						}
					}
				}
			}
			newVerrechnet.setExtInfo(AL, Integer.toString(tc.getAL(kons.getMandator())));
			setALScalingInfo(tc, newVerrechnet, kons.getMandator(), false);
			newVerrechnet.setExtInfo(TL, Integer.toString(tc.getTL()));
			lst.add(newVerrechnet);
		}

		// set bezug of zuschlagsleistung and referenzleistung
		if (isReferenceInfoAvailable() && shouldDetermineReference(tc)) {
			// lookup available masters
			List<IBilled> masters = getPossibleMasters(newVerrechnet, lst);
			if (masters.isEmpty()) {
				decrementOrDelete(newVerrechnet);
				return new Result<IBilled>(Result.SEVERITY.WARNING, KOMBINATION, "Für die Zuschlagsleistung "
						+ code.getCode() + " konnte keine passende Hauptleistung gefunden werden.", null, false);
			}
			if (!masters.isEmpty()) {
				String bezug = (String) newVerrechnet.getExtInfo("Bezug");
				if (bezug == null) {
					// set bezug to first available master
					newVerrechnet.setExtInfo("Bezug", masters.get(0).getCode());
				} else {
					boolean found = false;
					// lookup matching, or create new Verrechnet
					for (IBilled mVerr : masters) {
						if (mVerr.getCode().equals(bezug)) {
							// just mark as found as amount is already increased
							found = true;
						}
					}
					if (!found) {
						// create a new Verrechent and decrease amount
						newVerrechnet.setAmount(newVerrechnet.getAmount() - 1);
						saveBilled();
						newVerrechnet = initializeBilled(code, kons);
						newVerrechnet.setExtInfo("Bezug", masters.get(0).getCode());
					}
				}
			}
		}

		Result<IBilled> limitResult = checkLimitations(kons, tc, newVerrechnet);
		if (!limitResult.isOK()) {
			decrementOrDelete(newVerrechnet);
			return limitResult;
		}

		String tcid = code.getCode();

		// check if it's an X-RAY service and add default tax if so
		// default xray tax will only be added once (see above)
		if (!tc.getCode().equals(DEFAULT_TAX_XRAY_ROOM) && !tc.getCode().matches("39.002[01]")
				&& tc.getParent().getId().startsWith(CHAPTER_XRAY)) {
			if (TarmedUtil.getConfigValue(getClass(), IUser.class, Preferences.LEISTUNGSCODES_OPTIFY_XRAY, true)) {
				saveBilled();
				add(getKonsVerrechenbar(DEFAULT_TAX_XRAY_ROOM, kons), kons);
				// add 39.0020, will be changed according to case (see above)
				saveBilled();
				add(getKonsVerrechenbar("39.0020", kons), kons);
			}
		}

		// Interventionelle Schmerztherapie: Zuschlag cervical und thoracal
		else if (tcid.equals("29.2090")) {
			double sumAL = 0.0;
			double sumTL = 0.0;
			for (IBilled v : lst) {
				if (v.getBillable() instanceof TarmedLeistung) {
					TarmedLeistung tl = (TarmedLeistung) v.getBillable();
					String tlc = tl.getCode();
					double z = v.getAmount();
					if (tlc.matches("29.20[12345678]0") || (tlc.equals("29.2200"))) {
						sumAL += (z * tl.getAL(kons.getMandator())) / 2;
						sumTL += (z * tl.getTL()) / 4;
					}
				}
			}
			newVerrechnet.setPoints((int) Math.round(sumAL + sumTL));
			newVerrechnet.setExtInfo(AL, Double.toString(sumAL));
			newVerrechnet.setExtInfo(TL, Double.toString(sumTL));
		}

		// Zuschlag Kinder
		else if (tcid.equals("00.0010") || tcid.equals("00.0060")) {
			if (TarmedUtil.getConfigValue(getClass(), IMandator.class, RechnungsPrefs.PREF_ADDCHILDREN, false)) {
				ICoverage f = kons.getCoverage();
				if (f != null) {
					IPatient p = f.getPatient();
					if (p != null) {
						int alter = p.getAgeInYears();
						if (alter < 6) {
							TarmedLeistung tl = getKonsVerrechenbar("00.0040", kons);
							saveBilled();
							add(tl, kons);
						}
					}
				}
			}
		}

		// Zuschläge für Insellappen 50% auf AL und TL bei 1910,20,40,50
		else if (tcid.equals("04.1930")) { //$NON-NLS-1$
			double sumAL = 0.0;
			double sumTL = 0.0;
			for (IBilled v : lst) {
				if (v.getBillable() instanceof TarmedLeistung) {
					TarmedLeistung tl = (TarmedLeistung) v.getBillable();
					String tlc = tl.getCode();
					double z = v.getAmount();
					if (tlc.equals("04.1910") || tlc.equals("04.1920") || tlc.equals("04.1940") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							|| tlc.equals("04.1950")) { //$NON-NLS-1$
						sumAL += tl.getAL(kons.getMandator()) * z;
						sumTL += tl.getTL() * z;
						// double al = (tl.getAL() * 15) / 10.0;
						// double tel = (tl.getTL() * 15) / 10.0;
						// sum += al * z;
						// sum += tel * z;
					}
				}
			}
			// sum = sum * factor / 100.0;
			// check.setPreis(new Money(sum));
			newVerrechnet.setPoints((int) Math.round(sumAL + sumTL));
			newVerrechnet.setExtInfo(AL, Double.toString(sumAL));
			newVerrechnet.setExtInfo(TL, Double.toString(sumTL));
			newVerrechnet.setPrimaryScale(50);
		}
		// Zuschläge für 04.0620 sollte sich diese mit 70% auf die Positionen 04.0630 &
		// 04.0640 beziehen
		else if (tcid.equals("04.0620")) {
			double sumAL = 0.0;
			double sumTL = 0.0;
			for (IBilled v : lst) {
				if (v.getBillable() instanceof TarmedLeistung) {
					TarmedLeistung tl = (TarmedLeistung) v.getBillable();
					String tlc = tl.getCode();
					double z = v.getAmount();
					if (tlc.equals("04.0610") || tlc.equals("04.0630") || tlc.equals("04.0640")) {
						sumAL += tl.getAL(kons.getMandator()) * z;
						sumTL += tl.getTL() * z;
					}
				}
			}
			newVerrechnet.setPoints((int) Math.round(sumAL + sumTL));
			newVerrechnet.setExtInfo(AL, Double.toString(sumAL));
			newVerrechnet.setExtInfo(TL, Double.toString(sumTL));
			newVerrechnet.setPrimaryScale(70);
		}

		// Notfall-Zuschläge
		if (tcid.startsWith("00.25")) { //$NON-NLS-1$
			double sum = 0.0;
			int subcode = Integer.parseInt(tcid.substring(5));
			switch (subcode) {
			case 10: // Mo-Fr 7-19, Sa 7-12: 60 TP
				break;
			case 20: // Mo-Fr 19-22, Sa 12-22, So 7-22: 120 TP
				break;
			case 30: // 25% zu allen AL von 20
			case 70: // 25% zu allen AL von 60 (tel.)
				for (IBilled v : lst) {
					if (v.getBillable() instanceof TarmedLeistung) {
						TarmedLeistung tl = (TarmedLeistung) v.getBillable();
						if (tl.getCode().startsWith("00.25")) { //$NON-NLS-1$
							continue;
						}
						sum += (tl.getAL(kons.getMandator()) * v.getAmount());
						// int summand = tl.getAL() >> 2; // TODO ev. float?
						// -> Rundung?
						// ((sum.addCent(summand * v.getZahl());
					}
				}
				// check.setPreis(sum.multiply(factor));
				newVerrechnet.setPoints((int) Math.round(sum));
				newVerrechnet.setExtInfo(AL, Double.toString(sum));
				newVerrechnet.setPrimaryScale(25);
				break;
			case 40: // 22-7: 180 TP
				break;
			case 50: // 50% zu allen AL von 40
			case 90: // 50% zu allen AL von 70 (tel.)
				for (IBilled v : lst) {
					if (v.getBillable() instanceof TarmedLeistung) {
						TarmedLeistung tl = (TarmedLeistung) v.getBillable();
						if (tl.getCode().startsWith("00.25")) { //$NON-NLS-1$
							continue;
						}
						// int summand = tl.getAL() >> 1;
						// sum.addCent(summand * v.getZahl());
						sum += (tl.getAL(kons.getMandator()) * v.getAmount());
					}
				}
				// check.setPreis(sum.multiply(factor));
				newVerrechnet.setPoints((int) Math.round(sum));
				newVerrechnet.setExtInfo(AL, Double.toString(sum));
				newVerrechnet.setPrimaryScale(50);
				break;

			case 60: // Tel. Mo-Fr 19-22, Sa 12-22, So 7-22: 30 TP
				break;
			case 80: // Tel. von 22-7: 70 TP
				break;

			}
		}

		saveBilled();

		return new Result<IBilled>(newVerrechnet);
	}

	private IBilled initializeBilled(TarmedLeistung code, IEncounter kons) {
		IContact biller = ContextServiceHolder.get().getActiveUserContact().get();
		IBilled ret = new IBilledBuilder(CoreModelServiceHolder.get(), code, kons, biller).build();
		ret.setPoints(code.getAL(kons.getMandator()) + code.getTL());
		Optional<IBillingSystemFactor> systemFactor = BillingServiceHolder.get()
				.getBillingSystemFactor(kons.getCoverage().getBillingSystem().getName(), kons.getDate());
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

	private void saveBilled() {
		if (newVerrechnet != null && save) {
			CoreModelServiceHolder.get().save(newVerrechnet);
		} else {
			LoggerFactory.getLogger(TarmedOptifier.class).warn("Call on null", new Throwable("Diagnosis"));
		}
	}

	private void deleteBilled(IBilled billed) {
		if (!bAllowOverrideStrict) {
			if (save) {
				CoreModelServiceHolder.get().delete(billed);
			} else {
				billed.setDeleted(true);
			}
		}
	}

	private void decrementOrDelete(IBilled verrechnet) {
		if (!bAllowOverrideStrict) {
			double zahl = verrechnet.getAmount();
			if (zahl > 1) {
				verrechnet.setAmount(zahl - 1);
				if (save) {
					CoreModelServiceHolder.get().save(verrechnet);
				}
			} else {
				deleteBilled(verrechnet);
			}
		}
	}

	private boolean isContext(String key) {
		return getContextValue(key) != null;
	}

	private Object getContextValue(String key) {
		if (contextMap != null) {
			return contextMap.get(key);
		}
		return null;
	}

	/**
	 * If there is a AL scaling used to calculate the AL value, provide original AL
	 * and AL scaling factor in the ExtInfo of the {@link IBilled}.
	 *
	 * @param tarmed
	 * @param verrechnet
	 * @param mandant
	 */
	private void setALScalingInfo(TarmedLeistung tarmed, IBilled verrechnet, IMandator mandant, boolean isComposite) {
		double scaling = tarmed.getALScaling(mandant);
		if (scaling != 100) {
			newVerrechnet.setExtInfo(AL_NOTSCALED, Integer.toString(tarmed.getAL()));
			newVerrechnet.setExtInfo(AL_SCALINGFACTOR, Double.toString(scaling / 100));
		}
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

	private Result<IBilled> checkLimitations(IEncounter kons, TarmedLeistung tarmedLeistung, IBilled newVerrechnet) {
		if (bOptify) {
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
		}
		return new Result<IBilled>(null);
	}

	private String checkAge(String limitsString, IEncounter kons) {
		LocalDateTime consDate = new TimeTool(kons.getDate()).toLocalDateTime();
		IPatient patient = kons.getCoverage().getPatient();
		LocalDateTime geburtsdatum = patient.getDateOfBirth();
		if (geburtsdatum == null) {
			return "Patienten Alter nicht ok, kein Geburtsdatum angegeben";
		}
		long patientAgeDays = patient.getAgeAtIn(consDate, ChronoUnit.DAYS);

		List<TarmedLeistungAge> ageLimits = TarmedLeistungAge.of(limitsString, consDate);
		for (TarmedLeistungAge tarmedLeistungAge : ageLimits) {
			if (tarmedLeistungAge.isValidOn(consDate.toLocalDate())) {
				// if only one of the limits is set, check only that limit
				if (tarmedLeistungAge.getFromDays() >= 0 && !(tarmedLeistungAge.getToDays() >= 0)) {
					if (patientAgeDays < tarmedLeistungAge.getFromDays()) {
						return "Patient ist zu jung, verrechenbar ab " + tarmedLeistungAge.getFromText();
					}
				} else if (tarmedLeistungAge.getToDays() >= 0 && !(tarmedLeistungAge.getFromDays() >= 0)) {
					if (patientAgeDays > tarmedLeistungAge.getToDays()) {
						return "Patient ist zu alt, verrechenbar bis " + tarmedLeistungAge.getToText();
					}
				} else if (tarmedLeistungAge.getToDays() >= 0 && tarmedLeistungAge.getFromDays() >= 0) {
					if (tarmedLeistungAge.getToDays() < tarmedLeistungAge.getFromDays()) {
						if (patientAgeDays > tarmedLeistungAge.getToDays()
								&& patientAgeDays < tarmedLeistungAge.getFromDays()) {
							return "Patienten Alter nicht ok, verrechenbar " + tarmedLeistungAge.getText();
						}
					} else {
						if (patientAgeDays > tarmedLeistungAge.getToDays()
								|| patientAgeDays < tarmedLeistungAge.getFromDays()) {
							return "Patienten Alter nicht ok, verrechenbar " + tarmedLeistungAge.getText();
						}
					}
				}
			}
		}
		return null;
	}

	private TarmedLeistung getKonsVerrechenbar(String code, IEncounter kons) {
		if (kons.getCoverage() != null) {
			String law = kons.getCoverage().getBillingSystem().getLaw().name();
			return TarmedLeistung.getFromCode(code, kons.getDate(), law);
		}
		return null;
	}

	private boolean isReferenceInfoAvailable() {
		boolean result = false;
		IConfigService cfgs = ConfigServiceHolder.get().orElse(null);
		if (cfgs != null) {
			result = cfgs.get(PreferenceConstants.CFG_REFERENCEINFO_AVAILABLE, false);
		}
		return result;
	}

	private boolean shouldDetermineReference(TarmedLeistung tc) {
		String typ = tc.getServiceTyp();
		boolean becauseOfType = typ != null && typ.equals("Z");
		if (becauseOfType) {
			String text = tc.getText();
			return text.startsWith("+") || text.startsWith("-");
		}
		return false;
	}

	private List<IBilled> getAvailableMasters(TarmedLeistung slave, List<IBilled> lst) {
		List<IBilled> ret = new LinkedList<IBilled>();
		LocalDate konsDate = null;
		for (IBilled v : lst) {
			if (konsDate == null) {
				konsDate = v.getEncounter().getDate();
			}
			if (v.getBillable() instanceof TarmedLeistung) {
				TarmedLeistung tl = (TarmedLeistung) v.getBillable();
				if (tl.getHierarchy(konsDate).contains(slave.getCode())) { // $NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}

	private List<IBilled> getPossibleMasters(IBilled newSlave, List<IBilled> lst) {
		TarmedLeistung slaveTarmed = (TarmedLeistung) newSlave.getBillable();
		// lookup available masters
		List<IBilled> masters = getAvailableMasters(slaveTarmed, lst);
		// check which masters are left to be referenced
		int maxPerMaster = getMaxPerMaster(slaveTarmed);
		if (maxPerMaster > 0) {
			Map<IBilled, List<IBilled>> masterSlavesMap = getMasterToSlavesMap(newSlave, lst);
			for (IBilled master : masterSlavesMap.keySet()) {
				double masterCount = master.getAmount();
				int slaveCount = 0;
				for (IBilled slave : masterSlavesMap.get(master)) {
					slaveCount += slave.getAmount();
					if (slave.equals(newSlave)) {
						slaveCount--;
					}
				}
				if (masterCount <= (slaveCount * maxPerMaster)) {
					masters.remove(master);
				}
			}
		}
		return masters;
	}

	/**
	 * Creates a map of masters associated to slaves by the Bezug. This map will not
	 * contain the newSlave, as it has no Bezug set yet.
	 *
	 * @param newSlave
	 * @param lst
	 * @return
	 */
	private Map<IBilled, List<IBilled>> getMasterToSlavesMap(IBilled newSlave, List<IBilled> lst) {
		Map<IBilled, List<IBilled>> ret = new HashMap<>();
		TarmedLeistung slaveTarmed = (TarmedLeistung) newSlave.getBillable();
		// lookup available masters
		List<IBilled> masters = getAvailableMasters(slaveTarmed, lst);
		for (IBilled verrechnet : masters) {
			ret.put(verrechnet, new ArrayList<IBilled>());
		}
		// lookup other slaves with same code
		List<IBilled> slaves = getVerrechnetMatchingCode(lst, newSlave.getCode());
		// add slaves to separate master list
		for (IBilled slave : slaves) {
			String bezug = (String) slave.getExtInfo("Bezug");
			if (bezug != null && !bezug.isEmpty()) {
				for (IBilled master : ret.keySet()) {
					if (master.getCode().equals(bezug)) {
						ret.get(master).add(slave);
					}
				}
			}
		}
		return ret;
	}

	private int getMaxPerMaster(TarmedLeistung slave) {
		List<TarmedLimitation> limits = slave.getLimitations();
		for (TarmedLimitation limit : limits) {
			if (limit.getLimitationUnit() == LimitationUnit.MAINSERVICE) {
				// only an integer makes sense here
				return limit.getAmount();
			}
		}
		// default to unknown
		return -1;
	}

	/**
	 * Create a new mapping between an OP I reduction (35.0020) and a service from
	 * the OP I section.
	 *
	 * @param opVerrechnet        Verrechnet representing a service from the OP I
	 *                            section
	 * @param reductionVerrechnet Verrechnet representing the OP I reduction
	 *                            (35.0020)
	 */
	private void mapOpReduction(IBilled opVerrechnet, IBilled reductionVerrechnet) {
		TarmedLeistung opVerrechenbar = (TarmedLeistung) opVerrechnet.getBillable();
		reductionVerrechnet.setAmount(opVerrechnet.getAmount());
		reductionVerrechnet.setExtInfo(TL, Double.toString(opVerrechenbar.getTL()));
		reductionVerrechnet.setExtInfo(AL, Double.toString(0.0));
		reductionVerrechnet.setPoints(Math.round(opVerrechenbar.getTL()));
		reductionVerrechnet.setPrimaryScale(-40);
		reductionVerrechnet.setExtInfo("Bezug", opVerrechenbar.getCode());
		if (save) {
			CoreModelServiceHolder.get().save(reductionVerrechnet);
		}
	}

	/**
	 * Update existing OP I reductions (35.0020), and return a list of all not yet
	 * mapped OP I services.
	 *
	 * @param opCodes     list of all available OP I codes see
	 *                    {@link #getOPList(List)}
	 * @param opReduction list of all available reduction codes see
	 *                    {@link #getVerrechnetMatchingCode(List)}
	 * @return list of not unmapped OP I codes
	 */
	private List<IBilled> updateOPReductions(List<IBilled> opCodes, List<IBilled> opReduction) {
		List<IBilled> notMappedCodes = new ArrayList<IBilled>();
		notMappedCodes.addAll(opCodes);
		// update already mapped
		for (IBilled reductionVerrechnet : opReduction) {
			boolean isMapped = false;
			String bezug = (String) reductionVerrechnet.getExtInfo("Bezug");
			if (bezug != null && !bezug.isEmpty()) {
				for (IBilled opVerrechnet : opCodes) {
					TarmedLeistung opVerrechenbar = (TarmedLeistung) opVerrechnet.getBillable();
					String opCodeString = opVerrechenbar.getCode();
					if (bezug.equals(opCodeString)) {
						// update
						reductionVerrechnet.setAmount(opVerrechnet.getAmount());
						reductionVerrechnet.setExtInfo(TL, Double.toString(opVerrechenbar.getTL()));
						reductionVerrechnet.setExtInfo(AL, Double.toString(0.0));
						reductionVerrechnet.setPrimaryScale(-40);
						notMappedCodes.remove(opVerrechnet);
						isMapped = true;
						break;
					}
				}
			}
			if (!isMapped) {
				reductionVerrechnet.setAmount(0);
				reductionVerrechnet.setExtInfo("Bezug", StringUtils.EMPTY);
				saveBilled();
			}
		}

		return notMappedCodes;
	}

	private List<IBilled> getOPList(List<IBilled> lst) {
		List<IBilled> ret = new ArrayList<IBilled>();
		for (IBilled v : lst) {
			if (v.getBillable() instanceof TarmedLeistung) {
				TarmedLeistung tl = (TarmedLeistung) v.getBillable();
				if (tl.getSparteAsText().equals("OP I")) { //$NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}

	private List<IBilled> getVerrechnetMatchingCode(List<IBilled> lst, String code) {
		List<IBilled> ret = new ArrayList<IBilled>();
		for (IBilled v : lst) {
			if (v.getBillable() instanceof TarmedLeistung) {
				TarmedLeistung tl = (TarmedLeistung) v.getBillable();
				if (tl.getCode().equals(code)) { // $NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}

	private List<IBilled> getVerrechnetWithBezugMatchingCode(List<IBilled> lst, String code) {
		List<IBilled> ret = new ArrayList<IBilled>();
		for (IBilled v : lst) {
			if (v.getBillable() instanceof TarmedLeistung) {
				if (code.equals(v.getExtInfo("Bezug"))) { //$NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}

	private boolean isInstance(IBilled billed, ICodeElement billable) {
		boolean sameCode = (billed.getBillable().getCode().equals(billable.getCode()));
		boolean sameCodeSystemCode = (billed.getBillable().getCodeSystemCode().equals(billable.getCodeSystemCode()));
		return (sameCodeSystemCode && sameCode);
	}

	/**
	 * Always toggle the side of a specific code. Starts with left, then right, then
	 * add to the respective side.
	 *
	 * @param code
	 * @param lst
	 * @return
	 */
	private String getNewVerrechnetSideOrIncrement(IBillable code, List<IBilled> lst) {
		int countSideLeft = 0;
		IBilled leftVerrechnet = null;
		int countSideRight = 0;
		IBilled rightVerrechnet = null;

		for (IBilled v : lst) {
			if (isInstance(v, code)) {
				String side = (String) v.getExtInfo(Constants.FLD_EXT_SIDE);
				if (side.equals(Constants.SIDE_L)) {
					countSideLeft += v.getAmount();
					leftVerrechnet = v;
				} else {
					countSideRight += v.getAmount();
					rightVerrechnet = v;
				}
			}
		}
		// if side is provided by context use that side
		if (isContext(Constants.FLD_EXT_SIDE)) {
			String side = (String) getContextValue(Constants.FLD_EXT_SIDE);
			if (Constants.SIDE_L.equals(side) && countSideLeft > 0) {
				newVerrechnet = leftVerrechnet;
				newVerrechnet.setAmount(newVerrechnet.getAmount() + 1);
			} else if (Constants.SIDE_R.equals(side) && countSideRight > 0) {
				newVerrechnet = rightVerrechnet;
				newVerrechnet.setAmount(newVerrechnet.getAmount() + 1);
			}
			return side;
		}
		// toggle side if no side provided by context
		if (countSideLeft > 0 || countSideRight > 0) {
			if ((countSideLeft > countSideRight) && rightVerrechnet != null) {
				newVerrechnet = rightVerrechnet;
				newVerrechnet.setAmount(newVerrechnet.getAmount() + 1);
				saveBilled();
			} else if ((countSideLeft <= countSideRight) && leftVerrechnet != null) {
				newVerrechnet = leftVerrechnet;
				newVerrechnet.setAmount(newVerrechnet.getAmount() + 1);
				saveBilled();
			} else if ((countSideLeft > countSideRight) && rightVerrechnet == null) {
				return Constants.SIDE_R;
			}
		}
		return Constants.SIDE_L;
	}

	/**
	 * check compatibility of one tarmed with another
	 *
	 * @param tarmedCode the tarmed and it's parents code are check whether they
	 *                   have to be excluded
	 * @param tarmed     TarmedLeistung who incompatibilities are examined
	 * @param kons       {@link IEncounter} providing context
	 * @return true OK if they are compatible, WARNING if it matches an exclusion
	 *         case
	 */
	public Result<IBilled> isCompatible(TarmedLeistung tarmedCode, TarmedLeistung tarmed, IEncounter kons) {
		return isCompatible(null, tarmedCode, null, tarmed, kons);
	}

	/**
	 * check compatibility of one tarmed with another
	 *
	 * @param tarmedCodeVerrechnet the {@link Verrechnet} representing tarmedCode
	 * @param tarmedCode           the tarmed and it's parents code are check
	 *                             whether they have to be excluded
	 * @param tarmedVerrechnet     the {@link Verrechnet} representing tarmed
	 * @param tarmed               TarmedLeistung who incompatibilities are examined
	 * @param kons                 {@link IEncounter} providing context
	 * @return true OK if they are compatible, WARNING if it matches an exclusion
	 *         case
	 */
	public Result<IBilled> isCompatible(IBilled tarmedCodeVerrechnet, TarmedLeistung tarmedCode,
			IBilled tarmedVerrechnet, TarmedLeistung tarmed, IEncounter kons) {
		TimeTool date = new TimeTool(kons.getDate());
		List<TarmedExclusion> exclusions = tarmed.getExclusions(kons);
		for (TarmedExclusion tarmedExclusion : exclusions) {
			if (tarmedExclusion.isMatching(tarmedCode, kons.getDate())) {
				// exclude only if side matches
				if (tarmedExclusion.isValidSide() && tarmedCodeVerrechnet != null && tarmedVerrechnet != null) {
					String tarmedCodeSide = (String) tarmedCodeVerrechnet.getExtInfo(Constants.FLD_EXT_SIDE);
					String tarmedSide = (String) tarmedVerrechnet.getExtInfo(Constants.FLD_EXT_SIDE);
					if (tarmedSide != null && tarmedCodeSide != null) {
						if (tarmedSide.equals(tarmedCodeSide)) {
							return new Result<IBilled>(Result.SEVERITY.WARNING, EXKLUSIONSIDE,
									tarmed.getCode() + " nicht kombinierbar mit " //$NON-NLS-1$
											+ tarmedExclusion.toString() + " auf der selben Seite", //$NON-NLS-1$
									null, false);
						} else {
							// no exclusion due to different side
							continue;
						}
					}
				}
				return new Result<IBilled>(Result.SEVERITY.WARNING, EXKLUSION,
						tarmed.getCode() + " nicht kombinierbar mit " + tarmedExclusion.toString(), //$NON-NLS-1$
						null, false);
			}
		}
		// skip group exclusions check for the same service code
		if (!tarmedCode.getCode().equals(tarmed.getCode())) {
			List<String> groups = tarmed.getServiceGroups(kons.getDate());
			for (String groupName : groups) {
				Optional<ITarmedGroup> group = TarmedGroup.find(groupName, tarmed.getLaw(), kons.getDate());
				if (group.isPresent() && !tarmedCode.getServiceTyp().equals("Z")) {
					List<TarmedExclusion> groupExclusions = group.get().getExclusions(kons);
					for (TarmedExclusion tarmedExclusion : groupExclusions) {
						if (tarmedExclusion.isMatching(tarmedCode, kons.getDate())) {
							return new Result<IBilled>(Result.SEVERITY.WARNING, EXKLUSION,
									tarmed.getCode() + " nicht kombinierbar mit " //$NON-NLS-1$
											+ tarmedExclusion.toString(),
									null, false);
						}
					}
				}
			}
		}
		List<String> blocks = tarmed.getServiceBlocks(kons.getDate());
		for (String blockName : blocks) {
			if (skipBlockExclusives(blockName)) {
				continue;
			}
			List<TarmedExclusive> exclusives = TarmedKumulation.getExclusives(blockName, TarmedKumulationArt.BLOCK,
					kons.getDate(), tarmed.getLaw());
			// currently only test blocks exclusives, exclude hierarchy matches
			if (canHandleAllExculives(exclusives) && !isMatchingHierarchy(tarmedCode, tarmed, kons.getDate())
					&& !tarmedCode.getServiceTyp().equals("Z")) {
				boolean included = false;
				for (TarmedExclusive tarmedExclusive : exclusives) {
					if (tarmedExclusive.isMatching(tarmedCode, date)) {
						included = true;
					}
				}
				if (!included) {
					return new Result<IBilled>(Result.SEVERITY.WARNING, EXKLUSIVE,
							tarmed.getCode() + " nicht kombinierbar mit " //$NON-NLS-1$
									+ tarmedCode.getCode() + ", wegen Block Kumulation",
							null, false);
				}
			}
		}
		return new Result<IBilled>(Result.SEVERITY.OK, OK, "compatible", null, false);
	}

	private boolean skipBlockExclusives(String blockName) {
		try {
			Integer blockNumber = Integer.valueOf(blockName);
			if (blockNumber > 50 && blockNumber < 60) {
				return true;
			}
		} catch (NumberFormatException nfe) {
			// ignore and do not skip
		}
		return false;
	}

	private boolean isMatchingHierarchy(TarmedLeistung tarmedCode, TarmedLeistung tarmed, LocalDate date) {
		return tarmed.getHierarchy(date).contains(tarmedCode.getCode());
	}

	/**
	 * Test if we can handle all {@link TarmedExclusive}.
	 *
	 * @param exclusives
	 * @return
	 */
	private boolean canHandleAllExculives(List<TarmedExclusive> exclusives) {
		for (TarmedExclusive tarmedExclusive : exclusives) {
			if (tarmedExclusive.getSlaveType() != TarmedKumulationArt.BLOCK
					&& tarmedExclusive.getSlaveType() != TarmedKumulationArt.CHAPTER
					&& tarmedExclusive.getSlaveType() != TarmedKumulationArt.SERVICE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Eine Verrechnungsposition entfernen. Der Optifier sollte prüfen, ob die
	 * IEncounter nach Entfernung dieses Codes noch konsistent verrechnet wäre und
	 * ggf. anpassen oder das Entfernen verweigern. Diese Version macht keine
	 * Prüfungen, sondern erfüllt nur die Anfrage..
	 */
	@Override
	public Result<IBilled> remove(IBilled code, IEncounter kons) {
		List<IBilled> l = kons.getBilled();
		l.remove(code);
		deleteBilled(code);
		// if no more left, check for bezug and remove
		List<IBilled> left = getVerrechnetMatchingCode(l, code.getCode());
		if (left.isEmpty()) {
			List<IBilled> verrechnetWithBezug = getVerrechnetWithBezugMatchingCode(kons.getBilled(), code.getCode());
			for (IBilled verrechnet : verrechnetWithBezug) {
				remove(verrechnet, kons);
			}
		}
		return new Result<IBilled>(code);
	}

}
