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

package ch.elexis.agenda.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.data.IPlannable;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Query;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

/**
 * Utility-Klasse für Operationen mit und an Plannables
 * 
 * @author Gerry
 * 
 */
public final class Plannables {
	private Plannables(){}
	
	private static DecimalFormat df = new DecimalFormat("00"); //$NON-NLS-1$
	
	private static final Logger log = LoggerFactory.getLogger(Plannables.class);
	
	/** Feststellen, ob zwei Plannables sich überlappen */
	public static boolean isOverlapped(IPlannable p1, IPlannable p2){
		if (p1.getDay().equals(p2.getDay())) {
			int Beginn = p1.getStartMinute();
			int oBeginn = p2.getStartMinute();
			int Dauer = p1.getDurationInMinutes();
			int oDauer = p2.getDurationInMinutes();
			if (Beginn < oBeginn) {
				if ((Beginn + Dauer) <= oBeginn)
					return false;
				return true;
			} else if (Beginn > oBeginn) {
				if ((oBeginn + oDauer) <= Beginn)
					return false;
				return true;
			}
			return true;
		}
		return false;
	}
	
	/** Feststellen, ob zwei Plannables identisch sind */
	public static boolean isEqual(IPlannable p1, IPlannable p2){
		if (p1.getDay().equals(p2.getDay())) {
			if (p1.getStartMinute() == p2.getStartMinute()) {
				if (p1.getDurationInMinutes() == p2.getDurationInMinutes()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/** Feststellen, ob ein Plannable mit einer Liste von Planables kollidiert */
	public static boolean collides(IPlannable p1, Collection<IPlannable> list){
		for (IPlannable p2 : list) {
			if (isEqual(p1, p2)) {
				continue;
			}
			if (isOverlapped(p1, p2)) {
				return true;
			}
		}
		return false;
	}
	
	/** Feststellen, ob eine Zeitspane mit einem Plannable der Liste kollidiert */
	public static boolean collides(TimeSpan ts, Collection<IPlannable> list, Termin exclude){
		if (list == null) {
			return false;
		}
		for (IPlannable p : list) {
			if ((exclude != null) && isEqual(p, exclude)) {
				continue;
			}
			TimeTool tt = new TimeTool(p.getDay());
			tt.add(TimeTool.MINUTE, p.getStartMinute());
			TimeSpan o = new TimeSpan(tt, p.getDurationInMinutes());
			if (ts.overlap(o) != null)
				return true;
		}
		return false;
	}
	
	/** Feststellen, ob eine Zeitspane mit einem Plannable der Liste kollidiert */
	public static boolean collides(TimeSpan ts, IPlannable[] list, Termin exclude){
		if (list == null) {
			return false;
		}
		for (IPlannable p : list) {
			if ((exclude != null) && isEqual(p, exclude)) {
				continue;
			}
			TimeTool tt = new TimeTool(p.getDay());
			tt.add(TimeTool.MINUTE, p.getStartMinute());
			
			TimeSpan o = new TimeSpan(tt, p.getDurationInMinutes());
			System.out.println(ts.dump() + " / " + o.dump()); //$NON-NLS-1$
			if (ts.overlap(o) != null) {
				return true;
			}
		}
		return false;
	}
	
	/** Die einem Plannable-Typ zugeordnete Farbe holen */
	public static Color getTypColor(IPlannable p){
		String coldesc =
			ConfigServiceHolder.getUserCached(PreferenceConstants.AG_TYPCOLOR_PREFIX + p.getType(),
				"FFFFFF"); //$NON-NLS-1$
		return UiDesk.getColorFromRGB(coldesc);
		/*
		 * if(p.getType().equals(Termin.typReserviert())){ return
		 * Desk.theColorRegistry.get("weiss"); }else{ return Desk.theColorRegistry.get("schwarz"); }
		 */
	}
	
	/** Das einem Plannable-Typ zugeordnete Bild holen */
	public static Image getTypImage(IPlannable p){
		return getTypImage(p.getType());
	}
	
	/** Das einem Plannable-Titel zugeordnete Bild holen */
	public static Image getTypImage(String t){
		String ipath =
			ConfigServiceHolder.getUserCached(PreferenceConstants.AG_TYPIMAGE_PREFIX + t, null);
		if (!StringTool.isNothing(ipath)) {
			Image ret = UiDesk.getImage(ipath);
			if (ret == null) {
				UiDesk.getImageRegistry().put(ipath, Activator.getImageDescriptor(ipath));
				ret = UiDesk.getImage(ipath);
			}
			return ret;
		}
		return null;
	}
	
	/** Die einem Plannable-Status zugeordnete Farnbe holen */
	public static Color getStatusColor(IPlannable p){
		if (p.getType().equals(Termin.typReserviert())) {
			String coldesc =
				ConfigServiceHolder
					.getUserCached(PreferenceConstants.AG_TYPCOLOR_PREFIX + p.getType(), "000000"); //$NON-NLS-1$
			return UiDesk.getColorFromRGB(coldesc);
		}
		String coldesc =
			ConfigServiceHolder
				.getUserCached(PreferenceConstants.AG_STATCOLOR_PREFIX + p.getStatus(), "000000"); //$NON-NLS-1$
		return UiDesk.getColorFromRGB(coldesc);
	}
	
	/** Die Startzeit eines Plannable in hh:mm - Form holen */
	public static String getStartTimeAsString(IPlannable p){
		int s = p.getStartMinute();
		int h = s / 60;
		int m = s % 60;
		StringBuilder sb = new StringBuilder();
		sb.append(df.format(h)).append(":").append(df.format(m)); //$NON-NLS-1$
		return sb.toString();
	}
	
	/** Die End-Zeit eines Plannable in hh:mm - Form holen */
	public static String getEndTimeAsString(IPlannable p){
		int s = p.getStartMinute() + p.getDurationInMinutes();
		int h = s / 60;
		int m = s % 60;
		StringBuilder sb = new StringBuilder();
		sb.append(df.format(h)).append(":").append(df.format(m)); //$NON-NLS-1$
		return sb.toString();
	}
	
	public static Termin getFollowingTermin(String bereich, TimeTool date, Termin termin){
		List<IPlannable> list = loadTermine(bereich, date);
		boolean mark = false;
		for (IPlannable p : list) {
			if (mark) {
				return (Termin) p;
			}
			if (p.getStartMinute() == termin.getStartMinute()) {
				mark = true;
			}
		}
		return null;
	}
	
	/**
	 * Alle Termine eines Tages sortiert einlesen. Freiräume belassen.
	 * 
	 * @param mandant
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<IPlannable> loadTermine(String bereich, TimeTool date){
		if (StringTool.isNothing(bereich)) {
			return new ArrayList<IPlannable>();
		}
		
		Query<Termin> qbe = new Query<Termin>(Termin.class, Termin.TABLENAME, false, new String[] {
			Termin.FLD_TAG, Termin.FLD_BEGINN, Termin.FLD_DAUER, Termin.FLD_LASTEDIT,
			Termin.FLD_BEREICH
		});
		String day = date.toString(TimeTool.DATE_COMPACT);
		qbe.add(Termin.FLD_TAG, Query.EQUALS, day);
		qbe.and();
		
		qbe.add(Termin.FLD_BEREICH, Query.EQUALS, bereich);
		if (ConfigServiceHolder.getUserCached(PreferenceConstants.AG_SHOWDELETED, "0")
			.equals("0")) {
			qbe.and();
			qbe.add(Termin.FLD_DELETED, Query.EQUALS, "0");
		}
		List list = qbe.execute();
		if (list == null) {
			log.error(Messages.Plannables_databaseError);
			return new ArrayList<IPlannable>();
		}
		if (list.isEmpty()) {
			Hashtable<String, String> map = getDayPrefFor(bereich);
			int d = date.get(Calendar.DAY_OF_WEEK);
			String ds = map.get(TimeTool.wdays[d - 1]);
			if (StringTool.isNothing(ds)) {
				ds = "0000-0800\n1800-2359"; //$NON-NLS-1$
			}
			String[] flds = ds.split("\r*\n\r*"); //$NON-NLS-1$
			for (String fld : flds) {
				String from = fld.substring(0, 4);
				String until = fld.replaceAll("-", "").substring(4); //$NON-NLS-1$ //$NON-NLS-2$
				list.add(new Termin(bereich, date.toString(TimeTool.DATE_COMPACT), TimeTool
					.getMinutesFromTimeString(from), TimeTool.getMinutesFromTimeString(until),
					Termin.typReserviert(), Termin.statusLeer()));
			}
			
		}
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Alle Termine eines Tages sortiert einlesen und in Freiräume zwischen zwei Terminen jeweils
	 * ein Plannable vom Typ Termin.Free einsetzen, so dass eine lückenlose Liste von Plannables
	 * entsteht.
	 * */
	public static IPlannable[] loadDay(String bereich, TimeTool date){
		ArrayList<IPlannable> e = new ArrayList<IPlannable>(50);
		List<IPlannable> list = loadTermine(bereich, date);
		IPlannable n = null;
		IPlannable last = null;
		String day = date.toString(TimeTool.DATE_COMPACT);
		for (IPlannable o : (List<IPlannable>) list) {
			n = (IPlannable) o;
			if (n.getStartMinute() != 0) // Termin fängt nicht bei 0 Uhr an
			{
				if (last == null) { // Und es war auch noch keiner vorher
					// Dann neuen Anfangstermin einsetzen
					IPlannable res = new Termin.Free(day, 0, n.getStartMinute());
					e.add(res);
				} else { // Es gibt schon einen vorherigen Termin
					// Freien Eintrag einsetzen, falls Beginn von diesem nicht unmittelbar nach
					// previous
					if ((last.getStartMinute() + last.getDurationInMinutes()) < n.getStartMinute()) // Freiraum
					{
						IPlannable fr =
							new Termin.Free(day, last.getStartMinute()
								+ last.getDurationInMinutes(), n.getStartMinute());
						// Prüfen, ob ein früherer Termin mit diesem Freiraum kollidiert
						for (IPlannable p : e) {
							if (Plannables.isOverlapped(p, fr)) {
								fr.setStartMinute(p.getStartMinute() + p.getDurationInMinutes());
							}
						}
						// Freiraum nur einhängen, wenn er noch existiert
						if (fr.getStartMinute() < n.getStartMinute()) {
							fr.setDurationInMinutes(n.getStartMinute() - fr.getStartMinute());
							e.add(fr);
						}
					}
				}
			}
			e.add(n); // Eingelesenen Termin einsetzen
			last = n;
		}
		if (e.isEmpty()) // Keine Termine gefunden
		{
			Termin.Free ae = new Termin.Free( // Dann alles frei
				day, 0, 1439);
			e.add(ae);
			return e.toArray(new IPlannable[0]);
		}
		// Letzter Termin reicht nicht ans Tagesende?
		if (n != null && n.getStartMinute() + n.getDurationInMinutes() < 1439) // (23*60)+59
		{
			int b = n.getStartMinute() + n.getDurationInMinutes();
			Termin.Free en = new Termin.Free(day, b, 1439 - b);
			e.add(en);
		}
		return e.toArray(new IPlannable[0]);
		
	}
	
	/**
	 * EIn Plannable zeichnen
	 * 
	 * @param gc
	 *            Der GC, in den das Plannable gezeichnet werden soll
	 * @param p
	 *            das Plannable param r Rechteck, in das gezeichnet werden soll
	 * @param times
	 *            Anfang- und Endzeit des Bereichs, den gc abdeckt
	 */
	public static void paint(GC gc, IPlannable p, Rectangle r, int start, int end){
		double minutes = end - start;
		double pixelPerMinute = (double) r.width / minutes;
		int x = (int) Math.round((p.getStartMinute() - start) * pixelPerMinute);
		int w = (int) Math.round(p.getDurationInMinutes() * pixelPerMinute);
		gc.setBackground(getTypColor(p));
		gc.fillRectangle(x, r.y, w, r.height);
	}
	
	public static Hashtable<String, String> getTimePrefFor(String mandantLabel){
		Hashtable<String, String> map = new Hashtable<String, String>();
		String mTimes =
			ConfigServiceHolder.getGlobal(PreferenceConstants.AG_TIMEPREFERENCES + "/" + mandantLabel, ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (!StringTool.isNothing(mTimes)) {
			String[] types = mTimes.split("::"); //$NON-NLS-1$
			for (String t : types) {
				String[] line = t.split("="); //$NON-NLS-1$
				if (line.length != 2) {
					log.warn(Messages.Plannables_errorInAppointmentText + mTimes);
					continue;
				}
				map.put(line[0], line[1]);
			}
		}
		if (map.get("std") == null) { //$NON-NLS-1$
			map.put("std", "30"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return map;
	}
	
	public static void setTimePrefFor(String mandantLabel, Hashtable<String, String> map){
		StringBuilder e = new StringBuilder(200);
		Enumeration<String> keys = map.keys();
		while (keys.hasMoreElements()) {
			String k = keys.nextElement();
			e.append(k).append("=").append(map.get(k)); //$NON-NLS-1$
			if (keys.hasMoreElements()) {
				e.append("::"); //$NON-NLS-1$
			}
		}
		ConfigServiceHolder.setGlobal(
			PreferenceConstants.AG_TIMEPREFERENCES + "/" + mandantLabel, e.toString()); //$NON-NLS-1$
	}
	
	@SuppressWarnings("unchecked")
	public static Hashtable<String, String> getDayPrefFor(String mandantLabel){
		Hashtable<String, String> map =
			StringTool.foldStrings(ConfigServiceHolder.getGlobal(PreferenceConstants.AG_DAYPREFERENCES
				+ "/" //$NON-NLS-1$
				+ mandantLabel, null));
		return map == null ? new Hashtable<String, String>() : map;
	}
	
	public static void setDayPrefFor(String mandantLabel, Hashtable<String, String> map){
		String flat = StringTool.flattenStrings(map);
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_DAYPREFERENCES + "/" + mandantLabel, flat); //$NON-NLS-1$
	}
}
