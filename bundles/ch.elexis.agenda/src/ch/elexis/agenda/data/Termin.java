/*******************************************************************************
 * Copyright (c) 2005-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.data;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.acl.ACLContributor;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IPeriod;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

/**
 * Termin-Klasse für Agenda
 */

public class Termin extends PersistentObject implements Cloneable, Comparable<Termin>, IPlannable, IPeriod {

	public static final String FLD_BEREICH = "BeiWem"; //$NON-NLS-1$
	public static final String FLD_TERMINTYP = "Typ"; //$NON-NLS-1$
	public static final String FLD_PATIENT = "Wer"; //$NON-NLS-1$
	public static final String FLD_EXTENSION = "Extension"; //$NON-NLS-1$
	public static final String FLD_TERMINSTATUS = "Status"; //$NON-NLS-1$
	public static final String FLD_CREATOR = "ErstelltVon"; //$NON-NLS-1$
	public static final String FLD_GRUND = "Grund"; //$NON-NLS-1$
	public static final String FLD_DAUER = "Dauer"; //$NON-NLS-1$
	public static final String FLD_BEGINN = "Beginn"; //$NON-NLS-1$
	public static final String FLD_PRIORITY = "priority"; //$NON-NLS-1$
	public static final String FLD_CASE_TYPE = "caseType"; //$NON-NLS-1$
	public static final String FLD_INSURANCE_TYPE = "insuranceType"; //$NON-NLS-1$
	public static final String FLD_TREATMENT_REASON = "treatmentReason"; //$NON-NLS-1$
	public static final String FLD_TAG = "Tag"; //$NON-NLS-1$
	public static final String FLD_LASTEDIT = "lastedit"; //$NON-NLS-1$
	public static final String FLD_STATUSHIST = "StatusHistory"; //$NON-NLS-1$
	public static final String FLD_LINKGROUP = "linkgroup";
	public static final String TABLENAME = "AGNTERMINE";
	public static final String VERSION = "1.2.7"; //$NON-NLS-1$
	public static String[] TerminTypes;
	public static String[] TerminStatus;
	public static String[] TerminBereiche;
	private static final JdbcLink j = getConnection();

	public static final Cache<String, Boolean> cachedAttributeKeys = CacheBuilder.newBuilder()
			.expireAfterWrite(30, TimeUnit.SECONDS).build();

	static {
		addMapping("AGNTERMINE", "BeiWem=Bereich", FLD_PATIENT + "=PatID", FLD_TAG, FLD_BEGINN, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				FLD_DAUER, FLD_GRUND, "Typ=TerminTyp", FLD_TERMINSTATUS + "=TerminStatus", FLD_CREATOR, //$NON-NLS-1$ //$NON-NLS-2$
				"ErstelltWann=Angelegt", FLD_LASTEDIT, "PalmID", "flags", FLD_DELETED, FLD_EXTENSION, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
																										// //$NON-NLS-4$
				FLD_LINKGROUP, FLD_STATUSHIST, FLD_PRIORITY); // $NON-NLS-1$
		TimeTool.setDefaultResolution(60000);
		TerminTypes = CoreHub.globalCfg.getStringArray(PreferenceConstants.AG_TERMINTYPEN);
		TerminStatus = CoreHub.globalCfg.getStringArray(PreferenceConstants.AG_TERMINSTATUS);
		TerminBereiche = CoreHub.globalCfg.get(PreferenceConstants.AG_BEREICHE, Messages.TagesView_14).split(","); //$NON-NLS-1$
		if ((TerminTypes == null) || (TerminTypes.length < 3)) {
			TerminTypes = new String[] { Messages.Termin_range_free, Messages.Termin_range_locked,
					Messages.Termin_normalAppointment };
		}
		if ((TerminStatus == null) || (TerminStatus.length < 2)) {
			TerminStatus = new String[] { "-", Messages.Termin_plannedAppointment //$NON-NLS-1$
			};
		}
	}

	// Terminstatus fix
	static public final int LEER = 0;

	// Termintypen fix
	static public final int FREI = 0;
	static public final int RESERVIERT = 1;
	static public final int STANDARD = 2;

	// Status-Flags (dipSwitch)
	static public final byte SW_LOCKED = 4; // 0x10

	// static String[] Users;

	/**
	 * Tabelle neu erstellen
	 */
	public static void init() {
		try {
			ConfigServiceHolder.setUser(PreferenceConstants.AG_SHOWDELETED + "_default", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			ConfigServiceHolder.setGlobal(PreferenceConstants.AG_TERMINTYPEN + "_default", //$NON-NLS-1$
					Messages.Termin_freeLockedNormalExtraVisit);
			ConfigServiceHolder.setGlobal(PreferenceConstants.AG_TERMINSTATUS + "_default", //$NON-NLS-1$
					Messages.Termin_plannedHereFinishedMissed);
			ConfigServiceHolder.setUser(PreferenceConstants.AG_TYPIMAGE_PREFIX + Termin.typFrei(), "icons/gruen.png"); //$NON-NLS-1$
			ConfigServiceHolder.setUser(PreferenceConstants.AG_TYPIMAGE_PREFIX + Termin.typReserviert(),
					"icons/einbahn.png"); //$NON-NLS-1$
			ConfigServiceHolder.setUser(PreferenceConstants.AG_TYPIMAGE_PREFIX + Messages.Termin_normal,
					"icons/kons.ico"); // $NON-NLS-2$
			ConfigServiceHolder.setUser(PreferenceConstants.AG_TYPIMAGE_PREFIX + Messages.Termin_extra,
					"icons/blaulicht.ico"); // $NON-NLS-2$
			ConfigServiceHolder.setUser(PreferenceConstants.AG_TYPIMAGE_PREFIX + Messages.Termin_visit,
					"icons/ambulanz.ico"); // $NON-NLS-2$
			new ACLContributor().initializeDefaults(AccessControlServiceHolder.get());
		} catch (Exception ex) {
			ExHandler.handle(ex);
		}

	}

	public static void addBereich(String bereich) {
		String nber = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_BEREICHE, Messages.TagesView_14);
		nber += "," + bereich; //$NON-NLS-1$
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_BEREICHE, nber);
		TerminBereiche = nber.split(","); //$NON-NLS-1$
	}

	public static void addType(String typ) {
		String tt = StringTool.join(TerminTypes, ",") + "," + typ; //$NON-NLS-1$ //$NON-NLS-2$
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_TERMINTYPEN, tt);
		TerminTypes = tt.split(","); //$NON-NLS-1$
	}

	public Termin() {/* leer */
	}

	public Termin(final String id) {
		super(id);
	}

	/**
	 * exists() liefert false wenn der Termin gelöscht ist...
	 *
	 * @param id
	 * @return
	 */
	public static Termin load(final String id) {
		Termin ret = new Termin(id);
		return ret;
	}

	public Termin(final String bereich, final TimeSpan ts, final String typ) {
		String tag = ts.from.toString(TimeTool.DATE_COMPACT);
		int von = ts.from.get(TimeTool.HOUR_OF_DAY) * 60 + ts.from.get(TimeTool.MINUTE);
		int dauer = ts.getSeconds() / 60;

		create(null);

		String stamp = createTimeStamp();
		set(new String[] { FLD_BEREICH, FLD_TAG, FLD_BEGINN, FLD_DAUER, FLD_TERMINTYP, FLD_TERMINSTATUS, "ErstelltWann",
				FLD_LASTEDIT, FLD_STATUSHIST }, bereich, tag, Integer.toString(von), Integer.toString(dauer), typ,
				statusStandard(), stamp, stamp, statusline(statusStandard()));

	}

	public Termin(final String bereich, final String Tag, final int von, final int bis, final String typ,
			final String status) {
		create(null);

		String ts = createTimeStamp();
		set(new String[] { FLD_BEREICH, FLD_TAG, FLD_BEGINN, FLD_DAUER, FLD_TERMINTYP, FLD_TERMINSTATUS, "ErstelltWann",
				FLD_LASTEDIT, FLD_STATUSHIST }, bereich, Tag, Integer.toString(von), Integer.toString(bis - von), typ,
				status, ts, ts, statusline(statusStandard()));
	}

	public Termin(final String bereich, final String Tag, final int von, final int bis, final String typ,
			final String status, final String priority) {
		this(bereich, Tag, von, bis, typ, status);
		set(FLD_PRIORITY, priority);
	}

	/**
	 * Einen Termin mit vorgegebener ID erstellen. Wird nur vom Importer gebraucth
	 */
	public Termin(final String ID, final String bereich, final String Tag, final int von, final int bis,
			final String typ, final String status) {
		create(ID);
		String ts = createTimeStamp();
		set(new String[] { FLD_BEREICH, FLD_TAG, FLD_BEGINN, FLD_DAUER, FLD_TERMINTYP, FLD_TERMINSTATUS, "ErstelltWann",
				FLD_LASTEDIT, FLD_STATUSHIST }, bereich, Tag, Integer.toString(von), Integer.toString(bis - von), typ,
				status, ts, ts, statusline(statusStandard()));
	}

	@Override
	public String getKey(String field) {
		String key = super.getKey(field);
		cachedAttributeKeys.put(key, true);
		return key;
	}

	@Override
	public void clearCachedAttributes() {
		Iterator<Entry<String, Boolean>> iterator = cachedAttributeKeys.asMap().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Boolean> key = iterator.next();
			if (key.getKey().contains(getId())) {
				getDefaultConnection().getCache().remove(key.getKey());
			}
			iterator.remove();
		}
	}

	/*
	 * public Termin(Mandant BeiWem, String Tag,int von, int bis, String typ, String
	 * status){ create(null); String ts=getTimeStamp(); set(new String[]{"BeiWem"
	 * ,"Tag","Beginn","Dauer","Typ","Status","ErstelltWann","lastedit"},
	 * BeiWem.getId(),Tag,Integer.toString(von),Integer.toString(bis-von),
	 * typ,status,ts,ts); }
	 */
	@Override
	public Object clone() {
		Termin ret = new Termin(get(FLD_BEREICH), get(FLD_TAG), getStartMinute(), getStartMinute() + getDauer(),
				getType(), getStatus(), get(FLD_PRIORITY));
		Kontakt k = getKontakt();
		if (k != null) {
			ret.setKontakt(getKontakt());
		}
		return ret;
	}

	@Override
	public boolean isRecurringDate() {
		Termin t = load(get(FLD_LINKGROUP));
		if (t != null && t.exists())
			return true;
		return false;
	}

	/** Den Standard-Termintyp holen */
	public static String typStandard() {
		return TerminTypes[STANDARD];
	}

	/** Den Termintyp mit der Bedeutung "frei" holen */
	public static String typFrei() {
		return TerminTypes[FREI];
	}

	/** Den Termintyp mit der Bedeutung "reserviert" holen */
	public static String typReserviert() {
		return TerminTypes[RESERVIERT];
	}

	/** Den Terminstatus mit der Bedeutung "undefiniert" holen */
	public static String statusLeer() {
		return TerminStatus[LEER];
	}

	/** Den Standard-Terminstatus für neue Termine holen */
	public static String statusStandard() {
		return TerminStatus[1];
	}

	/**
	 * if the linked list is 0 only the original entry is returned; if we have
	 * linked elements, all linked elements are returned
	 *
	 * @param orig
	 * @return
	 */
	public static List<Termin> getLinked(final Termin orig) {
		if (StringTool.isNothing(orig.get(FLD_LINKGROUP))) {
			return Collections.singletonList(orig);
		}

		Query<Termin> qbe = new Query<Termin>(Termin.class);
		qbe.add(FLD_LINKGROUP, Query.EQUALS, orig.get(FLD_LINKGROUP));
		return qbe.execute();
	}

	/**
	 * Aktuelle Zeit in Minuten als int in einem String verpackt.
	 *
	 * @return Timestamp
	 */
	public static String createTimeStamp() {
		return Integer.toString(TimeTool.getTimeInSeconds() / 60);
	}

	public TimeTool getModifyTime() {
		int min = checkZero(get(FLD_LASTEDIT));
		TimeTool ret = new TimeTool(min, 60000);
		return ret;
	}

	public TimeTool getCreateTime() {
		int min = checkZero(get("ErstelltWann"));
		return new TimeTool(min, 60000);
	}

	/**
	 * @deprecated
	 * @param flag
	 */
	@Deprecated
	public void setFlag(final byte flag) {
		int flags = checkZero(get("flags"));
		flags |= 1 << flag;
		set(new String[] { "flags", FLD_LASTEDIT }, new String[] { Integer.toString(flags), createTimeStamp() });
	}

	/**
	 * @deprecated
	 * @param flag
	 */
	@Deprecated
	public void clrFlag(final byte flag) {
		int flags = checkZero(get("flags"));
		flags &= ~(1 << flag);
		set(new String[] { "flags", FLD_LASTEDIT }, new String[] { Integer.toString(flag), createTimeStamp() });
	}

	/**
	 * @deprecated
	 * @param flag
	 */
	@Deprecated
	public boolean getFlag(final byte flag) {
		int flags = checkZero(get("flags"));
		return ((flags & (1 << flag)) != 0);
	}

	public boolean isLocked() {
		return getFlag(SW_LOCKED);
	}

	public void setLocked(final boolean mode) {
		if (mode) {
			setFlag(SW_LOCKED);
		} else {
			clrFlag(SW_LOCKED);
		}
	}

	public boolean checkLock() {
		if (isLocked()) {
			SWTHelper.alert(Messages.Termin_appointment_locked, Messages.Termin_appCantBeChanged);
			return true;
		}
		return false;
	}

	public boolean delete(boolean askForConfirmation) {
		boolean confirmed = !askForConfirmation;
		if (checkLock()) {
			return false;
		}
		String linkgroup = get(FLD_LINKGROUP); // $NON-NLS-1$
		boolean isLinked = linkgroup != null && !linkgroup.isEmpty();

		if (isLinked && askForConfirmation) {
			MessageDialog msd = new MessageDialog(UiDesk.getTopShell(), Messages.Termin_deleteSeries, null,
					Messages.Termin_thisAppIsPartOfSerie, MessageDialog.QUESTION,
					new String[] { Messages.Termin_yes, Messages.Termin_no }, 1);
			int retval = msd.open();
			if (retval == SWT.DEFAULT) {
				return false;
			}
			confirmed = (retval == Dialog.OK);
		}
		if (isLinked) {
			List<Termin> linked = getLinked(this);
			if (confirmed) {
				// delete whole series
				for (Termin ae : linked) {
					ae.set(new String[] { FLD_LASTEDIT, FLD_DELETED },
							new String[] { createTimeStamp(), StringConstants.ONE });
				}
			} else {
				if (getId().equals(linkgroup)) {
					// move root information
					if (linked.size() > 1) {
						int index = 0;
						Termin moveto = linked.get(index);
						while (moveto.getId().equals(linkgroup)) {
							moveto = linked.get(++index);
						}
						moveto.set(Termin.FLD_PATIENT, get(Termin.FLD_PATIENT));
						moveto.set(Termin.FLD_GRUND, get(Termin.FLD_GRUND));
						moveto.set(Termin.FLD_CREATOR, get(Termin.FLD_CREATOR));
						moveto.set(Termin.FLD_EXTENSION, get(Termin.FLD_EXTENSION));
						for (Termin termin : linked) {
							termin.set(Termin.FLD_LINKGROUP, moveto.getId());
						}
					}
				}
				// delete this
				set(new String[] { FLD_DELETED, FLD_LASTEDIT }, StringConstants.ONE, createTimeStamp());
			}
		} else {
			// delete this
			set(new String[] { FLD_DELETED, FLD_LASTEDIT }, StringConstants.ONE, createTimeStamp());
		}
		return true;
	}

	@Override
	public boolean delete() {
		return delete(true);
	}

	public void setType(final String Type) {
		if (!checkLock()) {
			if (StringTool.isNothing(Type)) {
				return;
			}
			if (Type.equals(typFrei())) {
				super.delete();
			} else {
				set(new String[] { FLD_TERMINTYP, FLD_LASTEDIT }, Type, createTimeStamp());
			}
		}
	}

	private String statusline(final String stat) {
		return createTimeStamp() + ";" + stat;
	}

	public void setStatus(final String stat) {
		if (StringTool.isNothing(stat)) {
			return;
		}
		if (!checkLock()) {
			set(FLD_STATUSHIST, get(FLD_STATUSHIST) + StringTool.lf + statusline(stat));
			set(new String[] { FLD_TERMINSTATUS, FLD_LASTEDIT }, stat, createTimeStamp());
		}
	}

	/**
	 * Mehrzeiliger String der die History der Statusaenderungen dieses Termins
	 * abrufen
	 */
	public String getStatusHistoryDesc() {
		return getStatusHistoryDesc(false);
	}

	public String getStatusHistoryDesc(boolean fullTime) {
		StringBuilder sb = new StringBuilder();

		String lines[] = get(FLD_STATUSHIST).split(StringTool.lf);
		for (String l : lines) {
			String f[] = l.split(";");
			if (f.length != 2)
				continue;

			TimeTool tt = new TimeTool(checkZero(f[0]), 60000);
			if (fullTime) {
				sb.append(tt.toString(TimeTool.FULL_GER)).append(": ").append(f[1]).append(StringTool.lf);
			} else {
				sb.append(tt.toString(TimeTool.TIME_SMALL)).append(": ").append(f[1]).append(StringTool.lf);
			}
		}

		return sb.toString();
	}

	@Override
	public boolean isValid() {
		int l = checkZero(get(FLD_DAUER));
		if (l <= 0) {
			return false;
		}
		return true;
	}

	public void setGrund(final String grund) {
		if (!checkLock()) {
			set(new String[] { FLD_GRUND, FLD_LASTEDIT }, grund, createTimeStamp());
		}
	}

	public String getGrund() {
		return get(FLD_GRUND);
	}

	public void set(final String bereich, final String tag, final int von, final int bis, final String typ,
			final String status) {
		if (!checkLock()) {
			set(new String[] { FLD_BEREICH, FLD_TAG, FLD_BEGINN, FLD_DAUER, FLD_TERMINTYP, FLD_LASTEDIT }, bereich, tag,
					Integer.toString(von), Integer.toString(bis - von), typ, createTimeStamp());
			setStatus(status);
		}
	}

	public void set(final String bereich, final TimeTool wann, final int dauer, final String typ, final String status,
			final Patient pat, final String Grund) {
		String Tag = wann.toString(TimeTool.DATE_COMPACT);
		int Beginn = wann.get(TimeTool.HOUR_OF_DAY) * 60 + wann.get(TimeTool.MINUTE);
		set(new String[] { FLD_BEREICH, FLD_TAG, FLD_BEGINN, FLD_DAUER, FLD_TERMINTYP, FLD_PATIENT, FLD_GRUND,
				FLD_LASTEDIT }, bereich, Tag, Integer.toString(Beginn), Integer.toString(dauer), typ, status,
				pat.getId(), Grund, createTimeStamp());
		setStatus(status);
	}

	/*
	 * public String getPersonalia(int width,FontRenderContext frc) { return
	 * StringUtils.EMPTY; }
	 */
	public String getPersonalia() {
		String patid = get(FLD_PATIENT);
		Patient pat = Patient.load(patid);
		String Personalien = StringUtils.EMPTY;
		if (pat.exists()) {
			Personalien = pat.getPersonalia();
		} else {
			Personalien = patid;
		}
		if (get(FLD_DELETED).equals(StringConstants.ONE)) {
			return Personalien + Messages.Termin_deleted;
		}
		return Personalien;
	}

	@Override
	public String getStatus() {
		return get(FLD_TERMINSTATUS);
	}

	public int getLastedit() {
		return getInt(FLD_LASTEDIT);
	}

	/**
	 * For whom is the appointment
	 *
	 * @param pers
	 */
	public void setKontakt(final Kontakt pers) {
		if (!checkLock()) {
			set(new String[] { FLD_PATIENT, FLD_LASTEDIT }, pers.getId(), createTimeStamp());
		}
	}

	public void setText(final String text) {
		if (!checkLock()) {
			set(new String[] { FLD_PATIENT, FLD_LASTEDIT }, text, createTimeStamp());
		}
	}

	/**
	 * For whom is the appointment?
	 *
	 * @return
	 */
	public Kontakt getKontakt() {
		String pid = get(FLD_PATIENT);
		Patient pat = Patient.load(pid);
		if (pat.exists()) {
			return pat;
		}
		return null;
	}

	@Override
	public String getText() {
		return get(FLD_PATIENT);
	}

	/*
	 * public Mandant getMandant(){ return Mandant.load(get("BeiWem")); }
	 */
	public String getBereich() {
		return get(FLD_BEREICH);
	}

	/**
	 * Algorithmus f�r Aufsplittung Name/Vorname/GebDat: was dem match
	 * [0-9][0-9]*\.[0-9][0-9]*\.[0-9]+ folgt, ist das Geburtsdatum Was davor steht,
	 * ist Name und Vorname, wobei das letzte Wort der Vorname ist und alles davor
	 * zum Namen gerechnet wird.
	 *
	 * @return Ein StringArray mit 3 Elementen: Name, Vorname, GebDat. Jedes Element
	 *         kann StringUtils.EMPTY sein, keines ist null.
	 */
	public static String[] findID(final String pers) {

		String[] ret = new String[3];
		ret[0] = StringConstants.EMPTY;
		ret[1] = StringConstants.EMPTY;
		ret[2] = StringConstants.EMPTY;
		if (StringTool.isNothing(pers)) {
			return ret;
		}
		String[] p1 = pers.split("[\\s,][\\s,]*[\\s,]*"); //$NON-NLS-1$
		if (p1.length == 1) {
			ret[0] = p1[0];
			return ret;
		}

		String nam, vn, gd;
		nam = StringUtils.EMPTY;
		vn = null;
		gd = null;
		for (int i = p1.length - 1; i >= 0; i--) {
			p1[i] = p1[i].trim();
			if (p1[i].matches("\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}")) { //$NON-NLS-1$
				if (gd == null) {
					gd = p1[i];
				}
			} else {
				if (vn == null) {
					vn = p1[i];
				} else {
					nam = p1[i] + StringUtils.SPACE + nam;
				}
			}
		}
		if (nam != null) {
			ret[0] = nam;
		}
		if (vn != null) {
			ret[1] = vn;
		}
		if (gd != null) {
			TimeTool tt = new TimeTool(gd);
			ret[2] = tt.toString(TimeTool.DATE);
		}
		ret[0] = ret[0].trim();
		ret[1] = ret[1].trim();
		ret[2] = ret[2].trim();
		return ret;
	}

	/** standard equals: Gleiche Zeit, gleiche Dauer, gleicher Bereich */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof Termin) {
			return super.isMatching((Termin) o, 0, FLD_TAG, FLD_BEGINN, FLD_DAUER, FLD_BEREICH);
		}
		return false;
	}

	/** Exakte Übereinstimmung */
	public boolean isEqual(final Termin ae) {
		return super.isMatching(ae, 0, FLD_TAG, FLD_BEGINN, FLD_DAUER, FLD_BEREICH, FLD_TERMINTYP, FLD_TERMINSTATUS,
				FLD_CREATOR, FLD_PATIENT);
	}

	@Override
	public TimeTool getStartTime() {
		String[] res = new String[2];
		get(new String[] { FLD_TAG, FLD_BEGINN }, res);
		TimeTool start = new TimeTool(res[0]);
		start.addMinutes(checkZero(res[1]));
		return start;
	}

	public TimeSpan getTimeSpan() {
		String[] res = new String[3];
		get(new String[] { FLD_TAG, FLD_BEGINN, FLD_DAUER }, res);
		TimeTool start = new TimeTool(res[0]);
		start.addMinutes(checkZero(res[1]));
		return new TimeSpan(start, checkZero(res[2]));
	}

	@Override
	public void setStartTime(final TimeTool t) {
		if (checkLock()) {
			return;
		}
		String Tag = t.toString(TimeTool.DATE_COMPACT);
		int Beginn = (t.get(TimeTool.HOUR_OF_DAY) * 60) + t.get(TimeTool.MINUTE);
		if (Beginn > 0) {
			set(new String[] { FLD_TAG, FLD_BEGINN, FLD_LASTEDIT }, Tag, Integer.toString(Beginn), createTimeStamp());
		}
	}

	@Override
	public void setEndTime(final TimeTool o) {
		if (!checkLock()) {
			TimeSpan ts = getTimeSpan();
			ts.until = o;
			set(new String[] { FLD_DAUER, FLD_LASTEDIT }, Integer.toString(ts.getSeconds() / 60), createTimeStamp());
		}
	}

	/*
	 * public boolean setMandant(Mandant m) { set(new
	 * String[]{"BeiWem","lastedit"},m.getId(),getTimeStamp()); return true; }
	 */
	public void setBereich(final String bereich) {
		if (!checkLock()) {
			set(new String[] { FLD_BEREICH, FLD_LASTEDIT }, bereich, createTimeStamp());
		}
	}

	@Override
	public String toString() {
		return toString(2);
	}

	public String toString(final int level) {
		String[] vals = new String[4];
		get(new String[] { FLD_TAG, FLD_DAUER, FLD_BEGINN, FLD_BEREICH }, vals);
		TimeTool d = new TimeTool(vals[0]);
		d.addMinutes(checkZero(vals[2]));
		String f = d.toString(TimeTool.WEEKDAY) + ", " + d.toString(TimeTool.LARGE_GER); //$NON-NLS-1$
		if (level > 0) {
			d.addMinutes(checkZero(vals[1]));
			f += "-" + d.toString(TimeTool.TIME_SMALL);
		}
		if (level > 1) {
			f += " (" + vals[3] + ")";

		}
		return f;
	}

	public int getBeginn() {
		return getInt(FLD_BEGINN);
	}

	public static String intTimeToString(final int t) {
		int hour = t / 60;
		int minute = t - (hour * 60);
		StringBuffer ret = new StringBuffer();
		ret.append(StringTool.pad(StringTool.LEFT, '0', Integer.toString(hour), 2));
		ret.append(":");
		ret.append(StringTool.pad(StringTool.LEFT, '0', Integer.toString(minute), 2));
		return ret.toString();
	}

	public int getDauer() {
		return getInt(FLD_DAUER);
	}

	static int TimeInMinutes(final TimeTool t) {
		return (t.get(TimeTool.HOUR_OF_DAY) * 60) + t.get(TimeTool.MINUTE);
	}

	public static class remark extends PersistentObject {

		public String bemerkung;
		static {
			addMapping("agnRemarks", "remark"); //$NON-NLS-1$
		}

		public remark(final String id) {
			super(id);
			if (exists()) {
				bemerkung = get("remark");
			} else {
				create(id);
				bemerkung = StringUtils.EMPTY;
			}
		}

		public void set(final String newval) {
			if (StringTool.isNothing(newval)) {
				j.exec("DELETE from agnRemarks WHERE ID=" + getWrappedId()); //$NON-NLS-1$
			} else {
				set("remark", newval);
			}
			bemerkung = newval;
		}

		@Override
		protected String getTableName() {
			return "agnRemarks"; //$NON-NLS-1$
		}

		@Override
		public String getLabel() {
			return bemerkung;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Termin o) {
		TimeSpan t0 = getTimeSpan();
		TimeSpan t1 = o.getTimeSpan();
		if (t0.from.isAfter(t1.from)) {
			return 1;
		} else if (t0.from.isBefore(t1.from)) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	protected String getTableName() {
		return "AGNTERMINE"; //$NON-NLS-1$
	}

	public String dump() {
		StringBuffer res = new StringBuffer(200);
		String[] fields = { FLD_TAG, FLD_BEREICH, FLD_PATIENT, FLD_TERMINTYP, FLD_TERMINSTATUS };
		String[] result = new String[fields.length];
		get(fields, result);
		// result[1]=Mandant.load(result[1]).getLabel();
		result[2] = Patient.load(result[2]).get("Name");
		for (int i = 0; i < fields.length; i++) {
			res.append(fields[i]).append("=").append(result[i]).append(",");
		}
		return res.toString();
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		TimeSpan ts = getTimeSpan();
		sb.append(new TimeTool(getDay()).toString(TimeTool.DATE_GER)).append(",");
		sb.append(ts.from.toString(TimeTool.TIME_SMALL)).append("-").append(ts.until.toString(TimeTool.TIME_SMALL))
				.append(StringUtils.SPACE).append(getPersonalia()).append(" (").append(getType()).append(",")
				.append(getStatus()).append(") ");
		return sb.toString();
	}

	@Override
	public String getDay() {
		return get(FLD_TAG);
	}

	@Override
	public int getDurationInMinutes() {
		return getInt(FLD_DAUER);
	}

	@Override
	public int getStartMinute() {
		return checkZero(get(FLD_BEGINN));
	}

	@Override
	public String getTitle() {
		return getPersonalia();
	}

	@Override
	public String getReason() {
		return getGrund();
	}

	@Override
	public String getType() {
		return get(FLD_TERMINTYP);
	}

	@Override
	public void setStartMinute(final int min) {
		if (!checkLock()) {
			set(new String[] { FLD_BEGINN, FLD_LASTEDIT }, Integer.toString(min), createTimeStamp());
		}
	}

	@Override
	public void setDurationInMinutes(final int min) {
		if (!checkLock()) {
			set(new String[] { FLD_DAUER, FLD_LASTEDIT }, Integer.toString(min), createTimeStamp());
		}
	}

	@Override
	public int getCacheTime() {
		return 5;
	}

	public static class Free implements IPlannable {
		String day;
		int start, length;

		public Free(final String d, final int s, final int l) {
			day = d;
			start = s;
			length = l;
		}

		@Override
		public String getDay() {
			return day;
		}

		@Override
		public int getDurationInMinutes() {
			return length;
		}

		@Override
		public int getStartMinute() {
			return start;
		}

		@Override
		public String getStatus() {
			return Termin.statusLeer();
		}

		@Override
		public String getText() {
			return StringConstants.EMPTY;
		}

		@Override
		public String getTitle() {
			// return "-";
			return String.format(Messages.MinutesFree, length);
		}

		@Override
		public String getType() {
			return Termin.typFrei();
		}

		@Override
		public void setStartMinute(final int min) {
			start = min;
		}

		@Override
		public void setDurationInMinutes(final int min) {
			length = min;
		}

		@Override
		public boolean isRecurringDate() {
			return false;
		}

		@Override
		public String getReason() {
			return StringUtils.EMPTY;
		}
	}

	@Override
	public boolean isDragOK() {
		return true;
	}

	public boolean crossesTimeFrame(int begin, int dauer) {
		int aMin = getBeginn();
		int aMax = getBeginn() + getDauer();

		if ((aMax <= begin) || (aMin >= (begin + dauer)))
			return false;
		return true;

	}

	/**
	 * check if an appointemnt exists for this time and duration in this area
	 *
	 * @param area       the agenda area to check
	 * @param time       with date and start time to check
	 * @param duration   span to check
	 * @param idToIgnore of {@link Termin} that should be ignored, may be null if
	 *                   none has to be ignored
	 * @return true if a {@link Termin} exists during this time, false otherwise
	 */
	public static boolean overlaps(String area, TimeTool time, int duration, @Nullable String idToIgnore) {
		Query<Termin> tQuery = new Query<Termin>(Termin.class);
		tQuery.add(Termin.FLD_BEREICH, Query.EQUALS, area);
		tQuery.add(Termin.FLD_TAG, Query.EQUALS, time.toString(TimeTool.DATE_COMPACT));

		List<Termin> termine = tQuery.execute();
		TimeSpan tsInQuestion = new TimeSpan(time, duration);
		tsInQuestion.until.getTimeAsLong();

		for (Termin t : termine) {
			String id = t.getId();
			if (!id.equals(idToIgnore)) {
				if (tsInQuestion.overlap(t.getTimeSpan()) != null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public TimeTool getEndTime() {
		String[] vals = new String[3];
		get(new String[] { FLD_TAG, FLD_BEGINN, FLD_DAUER }, vals);
		TimeTool ret = new TimeTool(vals[0]);
		ret.addMinutes(checkZero(vals[1]));
		ret.addMinutes(checkZero(vals[2]));
		return ret;
	}

	/**
	 * Convenience conversion method, loads object via model service
	 *
	 * @return
	 * @since 3.8
	 * @throws IllegalStateException if entity could not be loaded
	 */
	public IAppointment toIAppointment() {
		return CoreModelServiceHolder.get().load(getId(), IAppointment.class, true)
				.orElseThrow(() -> new IllegalStateException("Could not convert Termin [" + getId() + "]"));
	}
}