/*******************************************************************************
 * Copyright (c) 2006-2010, Gerry Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gerry Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.agenda.data;

import java.io.FileWriter;
import java.util.List;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.scripting.CSVWriter;
import ch.rgw.tools.ExHandler;

public class CSVExporter {
	
	public void doExport(String filename, List<Termin> termine){
		try {
			CSVWriter csv = new CSVWriter(new FileWriter(filename));
			String[] header =
				new String[] {
					"UUID", "Bereich", "Typ", "Datum", "Startzeit", "Dauer", "Grund",
					"Patient-UUID-oder-Name"
				};
			String[] fields =
				new String[] {
					"ID", Termin.FLD_BEREICH, Termin.FLD_TERMINTYP, Termin.FLD_TAG,
					Termin.FLD_BEGINN, Termin.FLD_DAUER, Termin.FLD_GRUND, Termin.FLD_PATIENT
				};
			csv.writeNext(header);
			for (Termin t : termine) {
				String[] line = new String[fields.length];
				t.get(fields, line);
				csv.writeNext(line);
			}
			csv.close();
			SWTHelper.showInfo("Termine exportiert", "Der Export nach " + filename
				+ " ist abgeschlossen");
		} catch (Exception ex) {
			ExHandler.handle(ex);
			SWTHelper.showError("Fehler", ex.getMessage());
		}
		
	}
	
}
