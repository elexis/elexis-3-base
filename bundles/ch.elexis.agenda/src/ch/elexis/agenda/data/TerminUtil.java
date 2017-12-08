package ch.elexis.agenda.data;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import ch.elexis.agenda.util.Plannables;
import ch.elexis.data.Query;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class TerminUtil {
	
	public static void updateBoundaries(String resource, TimeTool date){
		String day = date.toString(TimeTool.DATE_COMPACT);
		Query<Termin> qbe = new Query<Termin>(Termin.class);
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
	}
	
}
