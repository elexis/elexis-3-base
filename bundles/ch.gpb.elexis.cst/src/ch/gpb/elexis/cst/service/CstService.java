/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.befunde.Messwert;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.LabResult;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstGroup;
import ch.gpb.elexis.cst.data.CstProfile;
import ch.gpb.elexis.cst.data.CstProimmun;
import ch.gpb.elexis.cst.preferences.CstPreference;
import ch.gpb.elexis.cst.view.CstProfileEditor;
import ch.rgw.tools.StringTool;

/**
 *
 * @author daniel ludin ludin@swissonline.ch 27.06.2015 TODO: there may be
 *         unused methods
 */
public class CstService {

	private static SimpleDateFormat compactDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
	private static Logger log = LoggerFactory.getLogger(CstProfileEditor.class.getName());

	public static long getDayCountFromCompact(String start, String end) {
		long diff = -1;
		try {
			Date dateStart = compactDateFormat.parse(start);
			Date dateEnd = compactDateFormat.parse(end);

			// time is always 00:00:00 so rounding should help to ignore the
			// missing hour when going from winter to summer time as well as the
			// extra hour in the other direction
			diff = Math.round((dateEnd.getTime() - dateStart.getTime()) / (double) 86400000);
		} catch (Exception e) {
			// handle the exception according to your own situation
		}
		return diff;
	}

	/**
	 * Copies the profile to a list of patients
	 *
	 * @param source  the source profile
	 * @param target  the target profile
	 * @param mandant the mandant the profiles belong to
	 */
	public void copyProfile(CstProfile source, List<Patient> target, Mandant mandant) {
		// List<CstGroup> cstgroups = source.getCstGroups();

		for (Patient patient : target) {

			CstProfile newProfile = new CstProfile(
					Messages.Cst_Text_Copy_of + source.getName() + " (" + CstService.getGermanFromDate(new Date())
							+ ")",
					source.getDescription(), null, patient.getId(), mandant.getId(), getCompactFromDate(new Date()),
					getCompactFromDate(new Date()), "1");

			log.info("created new profile with id: " + newProfile.getId());

			CstService.copyProfile(source, newProfile);

			log.info("copy profile complete  id: " + newProfile.getId(), Log.INFOS);
		}
	}

	/**
	 * Copies all data and all records from related tables from source to target
	 * profile
	 *
	 * @param source
	 * @param target
	 */
	public static void copyProfile(CstProfile source, CstProfile target) {
		// copy the Cst Groups
		List<CstGroup> cstgroups = source.getCstGroups();

		target.addItems(cstgroups);

		// copy the ranking of the Cst Groups
		Map<Object, Object> itemRanking = (Map<Object, Object>) source.getMap(CstGroup.ITEMRANKING);

		Map<Object, Object> mapAuswahl = source.getMap(CstProfile.KEY_AUSWAHLBEFUNDE);
		if (mapAuswahl != null) {
			target.setMap(CstProfile.KEY_AUSWAHLBEFUNDE, mapAuswahl);
		} else {
			Hashtable<Object, Object> map = new Hashtable<Object, Object>();
			target.setMap(CstProfile.KEY_AUSWAHLBEFUNDE, map);

		}

		target.setMap(CstGroup.ITEMRANKING, itemRanking);

		target.setAnzeigeTyp(CstProfile.ANZEIGETYP_EFFEKTIV);

		target.setAnzeigeTyp(source.getAnzeigeTyp());
		target.setCrawlBack(source.getCrawlBack());
		target.setPeriod1DateStart(source.getPeriod1DateStart());
		target.setPeriod1DateEnd(source.getPeriod1DateEnd());

		target.setPeriod2DateStart(source.getPeriod2DateStart());
		target.setPeriod2DateEnd(source.getPeriod2DateEnd());
		target.setPeriod3DateStart(source.getPeriod3DateStart());
		target.setPeriod3DateEnd(source.getPeriod3DateEnd());

		target.setDaySpan1(source.getDaySpan1());
		target.setDaySpan2(source.getDaySpan2());
		target.setDaySpan3(source.getDaySpan3());
		target.setDiagnose(source.getDiagnose());
		target.setTherapievorschlag(source.getTherapievorschlag());

		target.setOutputHeader(source.getOutputHeader());

		/*
		 * not needed - maybe configurable? CstGastroColo dbObj =
		 * CstGastroColo.getByProfileId(source.getId());
		 *
		 * if (dbObj != null) {
		 *
		 * CstGastroColo newColo = new CstGastroColo(target.getId(),
		 * dbObj.getDatumGastro(), dbObj.getDatumColo());
		 *
		 * newColo.setProfileId(target.getId());
		 *
		 * newColo.setColoHistoBefund(dbObj.getColoHistoBefund());
		 * newColo.setColoMakroBefund(dbObj.getColoMakroBefund());
		 * newColo.setGastroMakroBefund(dbObj.getGastroMakroBefund());
		 * newColo.setGastroHistoBefund(dbObj.getGastroHistoBefund());
		 * newColo.setText1(dbObj.getText1()); newColo.setText2(dbObj.getText2());
		 * newColo.setText3(dbObj.getText3()); newColo.setText4(dbObj.getText4()); }
		 */

		// if the target is the same patient and source's ProImmun is null, try to find
		// another ProImmun in possibly existing different profiles
		CstProimmun cstProImmun = CstProimmun.getByProfileId(source.getId());

		// if the target is the source itself, copy also the ProImmun values
		if (cstProImmun != null && source.getKontaktId().equals(target.getKontaktId())) {

			CstProimmun newCstProImmun = new CstProimmun(target.getId(), cstProImmun.getDatum());
			newCstProImmun.setProfileId(target.getId());

			newCstProImmun.setTested(cstProImmun.getTested());
			newCstProImmun.setToBeTested(cstProImmun.getToBeTested());
			newCstProImmun.setText1(cstProImmun.getText1());
			newCstProImmun.setText2(cstProImmun.getText2());
			newCstProImmun.setText3(cstProImmun.getText3());
			newCstProImmun.setText4(cstProImmun.getText4());
			// newCstProImmun.setDatum(cstProImmun.getDatum());
		}

	}

	/**
	 * Converts the "elexis-date-format" (String like "yyyyMMdd") into a date object
	 *
	 * @param sDate
	 * @return date
	 */
	public static Date getDateFromCompact(String sDate) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			return df.parse(sDate);
		} catch (ParseException e) {
			log.error("Error parsing date: /" + sDate + "/" + e.toString(), Log.ERRORS);
			Status status = new Status(IStatus.WARNING, "ch.gbp.elexis.cst", e.getLocalizedMessage());
			StatusManager.getManager().handle(status, StatusManager.LOG);
		}
		return null;
	}

	public static String getCompactFromDate(Date date) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(date);
	}

	public static String generateFilename(Patient p) {
		String filePrefix = ConfigServiceHolder.getUser(CstPreference.CST_IDENTIFIER_FILEPREFIX, null);
		if (filePrefix == null) {
			filePrefix = "PREFIX-NOT-DEFINED-YET";
		}
		StringBuffer result = new StringBuffer();

		result.append(filePrefix);
		result.append("-");
		result.append(p.getVorname().substring(0, 1));
		result.append(".");
		result.append(p.getName().substring(0, 1));
		result.append(".-");

		String fileFormat = ConfigServiceHolder.getUser(CstPreference.CST_IDENTIFIER_FILEFORMAT, null);

		Date date = new Date();
		DateFormat df;
		try {
			df = new SimpleDateFormat(fileFormat);
		} catch (Exception e) {
			df = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
		}
		result.append(df.format(date));

		return result.toString();
	}

	public static String getGermanFromDate(Date date) {
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		return df.format(date);
	}

	public static String getReadableDateAndTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
		return sdf.format(date);
	}

	/*
	 * turn yyyyMMdd into dd.MM.yyyy
	 */
	public static String getGermanFromCompact(String date) {
		StringBuffer result = new StringBuffer();
		result.append(date.substring(6));

		result.append(".");
		result.append(date.substring(4, 6));

		result.append(".");
		result.append(date.substring(0, 4));

		return result.toString();
	}

	public static Date getDateFromGermanFormat(String sDate) {
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		Date date = null;

		try {
			date = df.parse(sDate);
		} catch (ParseException e) {
			log.error("Error converting date: " + e.getMessage(), Log.ERRORS);
		}
		return date;

	}

	public static String getCompactFromReadable(String sDate) {
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		Date date;

		try {
			date = df.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
			date = new Date();
		}

		DateFormat df2 = new SimpleDateFormat("yyyyMMdd");

		return df2.format(date);
	}

	/**
	 * Turns a Elexis-Date yyyyMMdd (compact date) into a normal german format
	 *
	 * @param compactDate
	 * @return a date in dd.MM.yyyy format
	 */
	public static String parseCompactDate(String compactDate) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

		if (compactDate != null) {
			try {
				Date result = df.parse(compactDate);
				String out = outputFormat.format(result);
				return out.toString();

			} catch (ParseException e) {
				log.error("Error parsing date in CstProfileEditor: " + e.toString(), Log.ERRORS);
				return compactDate;
			}
		} else {
			return compactDate;
		}

	}

	public static String getDateFromSubraction(int daysToSubtract) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -daysToSubtract);

		DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

		return outputFormat.format(c.getTime());
	}

	public static boolean isNumericString(String a) {

		Pattern pattern = Pattern.compile("^([0-9\\.])+$");
		Matcher matcher = pattern.matcher(a);

		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean isNonNumericString(String a) {

		Pattern pattern = Pattern.compile("^([A-Za-z])+$");
		Matcher matcher = pattern.matcher(a);

		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static String getMaximumOfNumbersInString(String value) {
		// Strip non-digits, ignoring decimal point
		// TODO: this will fail with values like "62 nach 15`liegen Arm rechts"
		// String value = "62.3 nach 0.01 `liegen Arm rechts";

		Pattern pattern = Pattern.compile("([0-9\\.])+");
		Matcher matcher = pattern.matcher(value);

		ArrayList<String> values = new ArrayList<String>();

		while (matcher.find()) {
			values.add(matcher.group());
		}
		Collections.sort(values); // Sort the arraylist
		String maxValue = StringUtils.EMPTY;
		try {
			// get the bottom most (highest) value from the list
			maxValue = values.get(values.size() - 1);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Error extracting maximal value: " + e.toString());
		}
		return maxValue;
	}

	/**
	 * deprecated
	 *
	 * @param mapAuswahl
	 * @param sBefundName
	 * @return
	 */
	public static boolean isSelectedInAuswahlBefunde(Map<String, Boolean> mapAuswahl, String sBefundName) {
		boolean result = false;
		Iterator<String> itKeys = mapAuswahl.keySet().iterator();

		while (itKeys.hasNext()) {
			Object key = (Object) itKeys.next();
			if (key.equals(sBefundName)) {
				result = ((Boolean) mapAuswahl.get(key)).booleanValue();
				break;
			}
		}

		return result;
	}

	public static boolean isBefundSelected(Map<Object, Object> mapAuswahl, String sBefundName) {
		boolean result = false;
		Iterator<Object> itKeys = mapAuswahl.keySet().iterator();

		while (itKeys.hasNext()) {
			Object key = (Object) itKeys.next();
			if (key.equals(sBefundName)) {
				if (!mapAuswahl.get(key).toString().equals("false")) {
					result = true;
				}

				break;
			}
		}

		return result;
	}

	public static List<String> getBefundArtenFields() {
		List<String> result = new ArrayList<String>();

		Messwert setup = Messwert.getSetup();
		Map<Object, Object> hash = setup.getMap(Messwert.FLD_BEFUNDE);
		String names = (String) hash.get(Messwert.HASH_NAMES);
		if (!StringTool.isNothing(names)) {

			for (String sNameBefund : names.split(Messwert.SETUP_SEPARATOR)) {

				String fields = (String) hash.get(sNameBefund + Messwert._FIELDS);
				if (fields == null) {
					continue;
				}

				String[] mNames = fields.split(Messwert.SETUP_SEPARATOR);
				for (int i = 0; i < mNames.length; i++) {
					result.add(mNames[i].split(Messwert.SETUP_CHECKSEPARATOR)[0]);

				}

			}

		}

		return result;
	}

	/**
	 * get the separator that can be stored with a Befund selection in the options
	 * panel and is for splitting findings that contain double values like n.n/nn.n
	 *
	 * @param mapAuswahl
	 * @param befundArt
	 * @return
	 */
	public static String getBefundArtSeparator(Map<Object, Object> mapAuswahl, String befundArt) {
		String result = null;

		// mapAuswahl may contain the key, but with a StringUtils.EMPTY value (and not
		// null), so we
		// return null explicitly on empty strings
		Iterator<Object> itKeys = mapAuswahl.keySet().iterator();
		while (itKeys.hasNext()) {
			Object key = (Object) itKeys.next();
			String sepKey = "separator_" + befundArt;

			if (key.equals(sepKey)) {

				result = ((String) mapAuswahl.get(key));
				if (result.length() == 0) {
					result = null;
				}
				break;
			}
		}

		return result;
	}

	public static String getBefundArtOfField(CstProfile profile, String fieldName) {
		Map<Object, Object> mAuswahl = profile.getMap(CstProfile.KEY_AUSWAHLBEFUNDE);
		String sBefundArt = mAuswahl.get(fieldName).toString();
		return sBefundArt;
	}

	// Deliver all distinct dates from this set of lab results, ordered
	// descendingly
	@SuppressWarnings("unchecked")
	public static List<String> getDistinctDates(
			HashMap<String, HashMap<String, HashMap<String, List<LabResult>>>> labResults) {

		HashMap<String, String> distinctDates = new HashMap<String, String>();

		Set<String> keys1 = labResults.keySet();
		for (String sKey : keys1) {
			HashMap<String, ?> hm2 = labResults.get(sKey);
			Set<String> keys2 = hm2.keySet();
			for (String sKey2 : keys2) {

				HashMap<String, ?> hm3 = (HashMap<String, ?>) hm2.get(sKey2);
				Set<String> keys3 = hm3.keySet();
				for (String sKey3 : keys3) {
					distinctDates.put(sKey3, "dummy");
				}
			}
		}
		List<String> sortedDates = new ArrayList<String>(distinctDates.keySet());
		Collections.sort(sortedDates);

		return sortedDates;
	}

	@SuppressWarnings("unchecked")
	public static LabResult getValueForNameAndDate(String name, String date, String kuerzel,
			HashMap<String, HashMap<String, HashMap<String, List<LabResult>>>> labResults) {
		// log.debug("********** name/date: " + name + "/" + date);
		LabResult result = null;
		Set<String> keys1 = labResults.keySet();

		// iterate over groups (typically Medics Labor, Eigenlabor, Sysmex Labor etc.)
		for (String sKey : keys1) {
			// log.debug("key1: " + sKey);
			HashMap<String, ?> hm2 = labResults.get(sKey);
			Set<String> keys2 = hm2.keySet();

			// sKey2 is the LabItem name
			for (String sKey2 : keys2) {

				HashMap<String, ?> hm3 = (HashMap<String, ?>) hm2.get(sKey2);
				Set<String> keys3 = hm3.keySet(); // contains the dates, ie: [20140207, 20121016, 20140904, 20130912,
													// 20120302, 20150408]
				for (String sKey3 : keys3) {
					Object obj = hm3.get(sKey3);
					List<LabResult> res = (List<LabResult>) obj;

					for (LabResult labResult : res) {

						// TODO: this lengthy condition is required, because sometimes the
						// Labitems seem to be intermixed (ie HGB / H�moglobin and LDH / LDH
						// Elektrophorese)
						if ((labResult.getItem().getName().equals(name)
								|| labResult.getItem().getKuerzel().equals(kuerzel)
								|| labResult.getItem().getName().equals(kuerzel)
								|| labResult.getItem().getKuerzel().equals(name))
								&& labResult.getDate().equals(CstService.getGermanFromCompact(date))) {
							/*
							 * if ((labResult.getItem().getName().equals(name) ||
							 * labResult.getItem().getKuerzel().equals(kuerzel)) &&
							 * labResult.getDate().equals(CstService.getGermanFromCompact(date))) {
							 *
							 */
							result = labResult;
							/*
							 * log.debug("HIT: " + result.getItem().getName() + " \tDate: " +
							 * result.getDate(), Log.DEBUGMSG);
							 */
							break;
						}

					}

				}

			}
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static boolean hasValueForName(String name, String kuerzel,
			HashMap<String, HashMap<String, HashMap<String, List<LabResult>>>> labResults) {

		// log.debug("********** name/date: " + name + "/" + date);
		boolean result = false;
		Set<String> keys1 = labResults.keySet();

		// iterate over groups (typically Medics Labor, Eigenlabor, Sysmex Labor etc.)
		for (String sKey : keys1) {
			// log.debug("key1: " + sKey);
			HashMap<String, ?> hm2 = labResults.get(sKey);
			Set<String> keys2 = hm2.keySet();

			// sKey2 is the LabItem name
			for (String sKey2 : keys2) {

				HashMap<String, ?> hm3 = (HashMap<String, ?>) hm2.get(sKey2);
				Set<String> keys3 = hm3.keySet(); // contains the dates, ie: [20140207, 20121016, 20140904, 20130912,
													// 20120302, 20150408]
				for (String sKey3 : keys3) {
					Object obj = hm3.get(sKey3);
					List<LabResult> res = (List<LabResult>) obj;

					for (LabResult labResult : res) {

						// TODO: this lengthy condition is required, because sometimes the
						// Labitems seem to be intermixed (ie HGB / H�moglobin and LDH / LDH
						// Elektrophorese)
						if (labResult.getItem().getName().equals(name)
								|| labResult.getItem().getKuerzel().equals(kuerzel)
								|| labResult.getItem().getName().equals(kuerzel)
								|| labResult.getItem().getKuerzel().equals(name)) {
							/*
							 * if ((labResult.getItem().getName().equals(name) ||
							 * labResult.getItem().getKuerzel().equals(kuerzel)) &&
							 * labResult.getDate().equals(CstService.getGermanFromCompact(date))) {
							 *
							 */
							result = true;
							/*
							 * log.debug("HIT: " + result.getItem().getName() + " \tDate: " +
							 * result.getDate(), Log.DEBUGMSG);
							 */
							break;
						}

					}

				}

			}
		}

		return result;

	}

	/**
	 *
	 * @param date date to compute from
	 * @param days can be negative Value
	 * @return
	 */

	public static int getDaysBetweenDates(Date dateStart, Date dateEnd) {

		long diff = dateEnd.getTime() - dateStart.getTime();

		long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		int result = (int) days;
		return result;
	}

	public static Date getDateByAddingDays(Date date, int days) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		Date dateBeforeOrAfter = cal.getTime();
		return dateBeforeOrAfter;
	}

	/**
	 * get the laborwerte for the Minimax display
	 *
	 * @param name       Name of laborparameter
	 * @param kuerzel
	 * @param dateStart
	 * @param dateEnd
	 * @param labResults
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static LabResult getMaxValueForTimespan(String name, String kuerzel, Date dateStart, Date dateEnd,
			HashMap<String, HashMap<String, HashMap<String, List<LabResult>>>> labResults) {
		// log.debug("********** name/date: " + name + "/" + dateStart + "/" + dateEnd);

		LabResult result = null;
		Set<String> keys1 = labResults.keySet();

		// iterate over groups
		for (String sKey : keys1) {
			// log.debug("key1: " + sKey);
			HashMap<String, ?> hm2 = labResults.get(sKey);
			Set<String> keys2 = hm2.keySet();

			// sKey2 is the LabItem name
			for (String sKey2 : keys2) {
				// log.debug("\t\tkey2: " +sKey2);
				HashMap<String, ?> hm3 = (HashMap<String, ?>) hm2.get(sKey2);
				Set<String> keys3 = hm3.keySet();
				for (String sKey3 : keys3) {
					// log.debug("\t\t\tkey3: " +sKey3);

					Object obj = hm3.get(sKey3);
					// log.debug("\t\t\tval3: " +obj.getClass());

					List<LabResult> res = (List<LabResult>) obj;

					// log.debug("\t\t\tNrResults: " +res.size());

					for (LabResult currentResult : res) {

						String sDateResult = currentResult.getDate();
						Date dateResult = CstService.getDateFromGermanFormat(sDateResult);
						/*
						 * if (currentResult.getItem().getName().equals(name) &&
						 * dateResult.after(dateStart) && dateResult.before(dateEnd)) {
						 */

						if ((currentResult.getItem().getName().equals(name)
								|| currentResult.getItem().getKuerzel().equals(kuerzel)
								|| currentResult.getItem().getName().equals(kuerzel)
								|| currentResult.getItem().getKuerzel().equals(name)) && dateResult.after(dateStart)
								&& dateResult.before(dateEnd)) {
							if (result == null) {
								result = currentResult;
								// log.debug("HIT 1: " + currentResult.getItem().getName() + " \tDate: " +
								// currentResult.getDate());
							} else {
								double dResultResult = getNumericFromLabResult(result.getResult());
								double dCurrentResult = getNumericFromLabResult(currentResult.getResult());

								if (dCurrentResult > dResultResult) {
									result = currentResult;
								}

							}
							break;
						}
					}
				}
			}
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static LabResult getMinValueForTimespan(String name, String kuerzel, Date dateStart, Date dateEnd,
			HashMap<String, HashMap<String, HashMap<String, List<LabResult>>>> labResults) {
		LabResult result = null;
		Set<String> keys1 = labResults.keySet();

		// iterate over groups
		for (String sKey : keys1) {
			HashMap<String, ?> hm2 = labResults.get(sKey);
			Set<String> keys2 = hm2.keySet();

			// sKey2 is the LabItem name
			for (String sKey2 : keys2) {
				HashMap<String, ?> hm3 = (HashMap<String, ?>) hm2.get(sKey2);
				Set<String> keys3 = hm3.keySet();
				for (String sKey3 : keys3) {

					Object obj = hm3.get(sKey3);

					List<LabResult> res = (List<LabResult>) obj;

					for (LabResult currentResult : res) {
						String sDateResult = currentResult.getDate();
						Date dateResult = CstService.getDateFromGermanFormat(sDateResult);

						/*
						 * if (currentResult.getItem().getName().equals(name) &&
						 * dateResult.after(dateStart) && dateResult.before(dateEnd)) {
						 */
						if ((currentResult.getItem().getName().equals(name)
								|| currentResult.getItem().getKuerzel().equals(kuerzel)
								|| currentResult.getItem().getName().equals(kuerzel)
								|| currentResult.getItem().getKuerzel().equals(name)) && dateResult.after(dateStart)
								&& dateResult.before(dateEnd)) {

							if (result == null) {
								result = currentResult;
							} else {
								double dResultResult = getNumericFromLabResult(result.getResult());
								double dCurrentResult = getNumericFromLabResult(currentResult.getResult());

								if (dCurrentResult < dResultResult) {
									result = currentResult;
								}
							}

							break;
						}
					}

				}

			}
		}

		return result;

	}

	public static double getNumericFromLabResult(String sResult) {
		double result = 0;

		String sRes = sResult.replaceAll("[^0-9?!\\.]", StringUtils.EMPTY);

		try {
			result = new Double(sRes).doubleValue();
			// log.error("Formatting Result: " + sResult + "/" + sRes + " formatted: " +
			// result, Log.ERRORS);
		} catch (NumberFormatException e) {
			log.error("Error formatting Result: " + sResult + "/" + sRes + "/" + e.getMessage(), Log.ERRORS);
		}
		return result;

	}

	public static String getReadableFromCompact(String sDate) {
		Date date = getDateFromCompact(sDate);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return sdf.format(date);
	}

	/**
	 * for testing and debugging purposes
	 *
	 * @param labResults
	 */
	@SuppressWarnings("unchecked")
	public static void printLaborwerte(HashMap<String, HashMap<String, HashMap<String, List<LabResult>>>> labResults) {

		Set<String> keys1 = labResults.keySet();
		for (String sKey : keys1) {
			HashMap<String, ?> hm2 = labResults.get(sKey);
			Set<String> keys2 = hm2.keySet();
			for (String sKey2 : keys2) {

				if (sKey2.length() == 0 || hm2.get(sKey2) == null) {
					continue;
				}

				HashMap<String, ?> hm3 = (HashMap<String, ?>) hm2.get(sKey2);
				Set<String> keys3 = hm3.keySet();
				for (String sKey3 : keys3) {
					Object obj = hm3.get(sKey3);

					List<LabResult> res = (List<LabResult>) obj;

					for (LabResult labResult : res) {
						// log.info("\t\t\t\tP: lab result: " + labResult, Log.INFOS);

						log.info(
								"\t\t\t\tP: lab result: " + labResult.getDate() + " Name: "
										+ labResult.getItem().getName() + " Kuerzel: "
										+ labResult.getItem().getKuerzel() + "\tResult: " + labResult.getResult(),
								Log.INFOS);

					}

				}

			}
		}
		// loop end

	}

	public static long getNrOfDaysBetween(Date dStart, Date dEnd) {
		long diff = dEnd.getTime() - dStart.getTime();
		long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return days;
	}
}
