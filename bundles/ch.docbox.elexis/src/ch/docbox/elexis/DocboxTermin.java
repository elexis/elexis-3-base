/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package ch.docbox.elexis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.docbox.ws.cdachservices.AppointmentType;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class DocboxTermin {

	private Termin elexisTermin;
	private Termin elexisTerminDayAfter;

	protected static Log log = Log.get("DocboxTermin"); //$NON-NLS-1$

	static {

	}

	@Override
	// NOTE: used for list search contains, based only on first id of object
	// hashcode
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DocboxTermin) {
			return this.hashCode() == obj.hashCode();
		}
		return super.equals(obj);
	}

	// NOTE: used for list search contains, based only on first id of object
	// hashcode
	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		if (elexisTermin != null) {
			hashCode = elexisTermin.hashCode();
		}
		return hashCode;
	}

	public DocboxTermin() {

	}

	private boolean loadTerminByDocboxUniqueId(String id) {
		elexisTermin = performDocboxIdLoad(id);
		if (elexisTermin != null && !elexisTermin.exists()) {
			elexisTermin = null;
		}
		if (elexisTermin != null && elexisTermin.isDeleted()) {
			elexisTermin.undelete();
		}
		elexisTerminDayAfter = performDocboxIdLoad(id + "2");
		if (elexisTerminDayAfter != null && !elexisTerminDayAfter.exists()) {
			elexisTerminDayAfter = null;
		}
		if (elexisTerminDayAfter != null && elexisTerminDayAfter.isDeleted()) {
			elexisTerminDayAfter.undelete();
		}
		return elexisTermin != null;
	}

	private Termin performDocboxIdLoad(String id) {
		Query<Termin> terminQuery = new Query<Termin>(Termin.class);
		terminQuery.add(Termin.FLD_EXTENSION, Query.EQUALS, id);
		List<Termin> execute = terminQuery.execute();
		if (!execute.isEmpty()) {
			return execute.get(0);
		}
		return null;
	}

	private boolean loadTermin(Termin termin) {
		elexisTermin = termin;
		elexisTerminDayAfter = performDocboxIdLoad(termin.get(Termin.FLD_EXTENSION) + "2");
		return elexisTermin != null;
	}

	static int TimeInMinutes(final TimeTool t) {
		return (t.get(TimeTool.HOUR_OF_DAY) * 60) + t.get(TimeTool.MINUTE);
	}

	private String getTerminUniqueId(String appointmentId, String bereich, boolean first) {
		return appointmentId + "[-" + bereich + "-]" + (first ? StringUtils.EMPTY : "2");

	}

	public static String getDocboxTerminId(Termin termin) {
		if (termin != null) {
			String id = termin.get(Termin.FLD_EXTENSION);
			int pos = id.indexOf("[-");
			if (pos > 0) {
				return id.substring(0, pos);
			}
		}
		return StringUtils.EMPTY;
	}

	public boolean create(AppointmentType appointment, String bereich) {

		try {

			// is it already in the database?
			loadTerminByDocboxUniqueId(getTerminUniqueId(appointment.getId(), bereich, true));

			Calendar cal = Calendar.getInstance();

			cal.setTimeInMillis(appointment.getDate().toGregorianCalendar().getTimeInMillis());
			int day = cal.get(Calendar.DAY_OF_WEEK);
			int startMinutes = cal.get(Calendar.MINUTE) + cal.get(Calendar.HOUR_OF_DAY) * 60;
			TimeTool ttFrom = new TimeTool();
			ttFrom.setTimeInMillis(cal.getTimeInMillis());

			cal.add(Calendar.MINUTE, (int) appointment.getDuration());
			TimeTool ttUntil = new TimeTool();
			ttUntil.setTimeInMillis(cal.getTimeInMillis());

			int day2 = cal.get(Calendar.DAY_OF_WEEK);
			boolean twoday = (day != day2);

			if (!twoday && elexisTerminDayAfter != null) {
				if (elexisTerminDayAfter != null) {
					if (elexisTerminDayAfter.isLocked()) {
						elexisTerminDayAfter.setLocked(false);
					}
					elexisTerminDayAfter.delete();
				}
			}

			String terminType = Termin.typReserviert();
			String terminStatus = Termin.statusStandard();

			String tag = ttFrom.toString(TimeTool.DATE_COMPACT);
			Integer von = TimeInMinutes(ttFrom); // dummy for constructor below
			Integer bis = TimeInMinutes(ttUntil); // dummy for constructor below

			if (elexisTermin == null) {
				elexisTermin = new Termin(bereich, tag, von, bis, terminType, terminStatus);
				elexisTermin.set(Termin.FLD_EXTENSION, getTerminUniqueId(appointment.getId(), bereich, true));
			}
			if (twoday) {
				if (elexisTerminDayAfter == null) {
					elexisTerminDayAfter = new Termin(bereich, tag, von, bis, terminType, terminStatus);
					elexisTerminDayAfter.set(Termin.FLD_EXTENSION,
							getTerminUniqueId(appointment.getId(), bereich, false));
				}
			}
			setLocked(false);

			Plannables.loadTermine(bereich, ttFrom); // fix for tagesgrenzen otherwise not showing
// up when empty
			if (!twoday) {
				elexisTermin.set(
						new String[] { "BeiWem", "Tag", "Beginn", "Typ", "Status", Termin.FLD_CREATOR, "Dauer" },
						new String[] { bereich, ttFrom.toString(TimeTool.DATE_COMPACT), Integer.toString(startMinutes),
								terminType, terminStatus, UserDocboxPreferences.getDocboxLoginID(false),
								Integer.toString((int) appointment.getDuration()) });
			} else {
				Plannables.loadTermine(bereich, ttUntil); // fix for tagesgrenzen otherwise not
// showing up
				elexisTermin.set(
						new String[] { "BeiWem", "Tag", "Beginn", "Typ", "Status", Termin.FLD_CREATOR, "Dauer" },
						new String[] { bereich, ttFrom.toString(TimeTool.DATE_COMPACT), Integer.toString(startMinutes),
								terminType, terminStatus, UserDocboxPreferences.getDocboxLoginID(false),
								Integer.toString(24 * 60 - startMinutes) });
				elexisTerminDayAfter.set(
						new String[] { "BeiWem", "Tag", "Beginn", "Typ", "Status", Termin.FLD_CREATOR, "Dauer" },
						new String[] { bereich, ttUntil.toString(TimeTool.DATE_COMPACT), Integer.toString(0),
								terminType, terminStatus, UserDocboxPreferences.getDocboxLoginID(false) + "-sys",
								Integer.toString((int) (startMinutes + appointment.getDuration()) % (24 * 60)) });
			}

			String text = StringUtils.EMPTY;
			String grund = StringUtils.EMPTY;

			if (appointment.getReasonDetails() != null) {
				grund = appointment.getReasonDetails();
			}
			if ("salesrepresentative-open".equals(appointment.getState())) {
				text = "offener Ärztebesuchertermin";
			} else if ("salesrepresentative-booked".equals(appointment.getState())) {
				text = String.format("%1$s (Ärztebesuchertermin): %2$s", appointment.getVisitor(),
						appointment.getReasonTitle());
			} else if ("salesrepresentative-openrequest".equals(appointment.getState())) {
				text = String.format("offene Anfrage von %1$s", appointment.getVisitor());
				grund = appointment.getReasonTitle();
			} else if ("salesrepresentative-openinvitation".equals(appointment.getState())) {
				text = String.format("offene Einladung an %1$s", appointment.getVisitor());
				grund = appointment.getReasonTitle();
			} else if ("salesrepresentative-phone".equals(appointment.getState())) {
				text = String.format("%1$s (manuell eingetragen)", appointment.getVisitor());
			} else if ("emergencyservice".equals(appointment.getState())) {
				text = String.format("Notfalldienst  %1$s", appointment.getReasonTitle());
			} else if ("terminierung-booked".equals(appointment.getState())) {
				text = appointment.getVisitor();
				grund = appointment.getReasonTitle();
				grund += "\r\n";
				grund += appointment.getReasonDetails();
			} else if ("terminierung-open".equals(appointment.getState())) {
				text = "offene Terminvereinbarung";
			} else if ("canceled".equals(appointment.getState())) {
				elexisTermin.delete();
				return true;
			}

			if (text != null && text.length() > 80) {
				int seperator = text.indexOf(",", 65);
				if (seperator > 80 || seperator <= 65) {
					seperator = 79;
				}
				text = text.substring(0, seperator);
			}

			setText(text);
			setGrund(grund);
			setLocked(true);
			log.log("Termin " + elexisTermin.getId() + StringUtils.SPACE + elexisTermin.getText() + StringUtils.SPACE
					+ elexisTermin.getGrund() + " - " + elexisTermin.getDay(), Log.DEBUGMSG);

		} catch (Exception exception) {
			LoggerFactory.getLogger(getClass()).error("Termin konnte nicht gespeichert werden", exception);
			return false;
		}

		return true;
	}

	public void setText(String text) {
		elexisTermin.setText(text);
		if (elexisTerminDayAfter != null) {
			elexisTerminDayAfter.setText(text);
		}
	}

	public void setGrund(String text) {
		elexisTermin.setGrund(text);
		if (elexisTerminDayAfter != null) {
			elexisTerminDayAfter.setGrund(text);
		}
	}

	public void setLocked(boolean locked) {
		elexisTermin.setLocked(locked);
		if (elexisTerminDayAfter != null) {
			elexisTerminDayAfter.setLocked(locked);
		}
	}

	public boolean delete() {
		if (elexisTerminDayAfter != null && elexisTerminDayAfter.exists()) {
			if (elexisTerminDayAfter.isLocked()) {
				elexisTerminDayAfter.setLocked(false);
			}
			elexisTerminDayAfter.delete();
		}
		if (elexisTermin != null && elexisTermin.exists()) {
			log.log("Terminid " + elexisTermin.getId() + StringUtils.SPACE + elexisTermin.getText() + StringUtils.SPACE
					+ elexisTermin.getGrund() + "- " + elexisTermin.getBereich(), Log.DEBUGMSG);
			if (elexisTermin.isLocked()) {
				elexisTermin.setLocked(false);
			}
			elexisTermin.delete();
			return true;
		}
		return false;
	}

	static public List<DocboxTermin> getDocboxTermine() {
		Query<Termin> terminQuery = new Query<Termin>(Termin.class);
		terminQuery.add(Termin.FLD_CREATOR, "=", UserDocboxPreferences.getDocboxLoginID(false));
		terminQuery.add("Tag", ">=", new TimeTool().toString(TimeTool.DATE_COMPACT));
		terminQuery.add("BeiWem", "=", UserDocboxPreferences.getAppointmentsBereich());
		List<Termin> termine = terminQuery.execute();
		log.log("Termine bestehend " + termine.size(), Log.DEBUGMSG);

		ArrayList<DocboxTermin> docboxTermine = new ArrayList<DocboxTermin>();
		for (Termin termin : termine) {
			DocboxTermin docboxTermin = new DocboxTermin();
			docboxTermin.loadTermin(termin);
			log.log("Terminid " + termin.getId() + StringUtils.SPACE + termin.getText() + StringUtils.SPACE
					+ termin.getGrund(), Log.DEBUGMSG);
			docboxTermine.add(docboxTermin);
		}
		return docboxTermine;
	}

}
