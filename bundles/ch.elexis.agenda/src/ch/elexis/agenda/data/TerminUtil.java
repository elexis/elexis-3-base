package ch.elexis.agenda.data;

import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.rgw.tools.TimeTool;

public class TerminUtil {
	
	public static void updateBoundaries(String resource, TimeTool date){
		
		AppointmentServiceHolder.get().updateBoundaries(resource,  date.toLocalDate());
		
		/**
		 * DEPRECATED JPA
		String day = date.toString(TimeTool.DATE_COMPACT);
		Query<Termin> qbe = new Query<Termin>(Termin.class, Termin.TABLENAME, false, new String [] {Termin.FLD_LINKGROUP, Termin.FLD_DAUER, Termin.FLD_BEGINN, Termin.FLD_TAG, Termin.FLD_GRUND, Termin.FLD_PATIENT, Termin.FLD_DELETED, Termin.FLD_TERMINSTATUS, Termin.FLD_TERMINTYP, Termin.FLD_BEREICH, Termin.FLD_STATUSHIST});
		qbe.add(Termin.FLD_TAG, Query.EQUALS, day);
		qbe.add(Termin.FLD_BEREICH, Query.EQUALS, resource);
		
		List<Termin> resList = qbe.execute();
		// check whether the only entries are appointments if yes also check
		// whether some "Tagesgrenzen" are missing
		for (Termin termin : resList) {
			if (termin.getType().equals(Termin.typReserviert())) {
				return;
			}
		}
		
		Hashtable<String, String> map = Plannables.getDayPrefFor(resource);
		int d = date.get(Calendar.DAY_OF_WEEK);
		String ds = map.get(TimeTool.wdays[d - 1]);
		if (StringTool.isNothing(ds)) {
			// default für Tagesgrenzen falls nicht definiert
			ds = "0000-0800\n1800-2359"; //$NON-NLS-1$
		}
		String[] flds = ds.split("\r*\n\r*"); //$NON-NLS-1$
		for (String fld : flds) {
			String from = fld.substring(0, 4);
			String until = fld.replaceAll("-", "").substring(4); //$NON-NLS-1$ //$NON-NLS-2$
			// Lege Termine für die Tagesgrenzen an
			new Termin(resource, day, TimeTool.getMinutesFromTimeString(from),
				TimeTool.getMinutesFromTimeString(until), Termin.typReserviert(),
				Termin.statusLeer());
		}
		
		*/
	}
	
}
