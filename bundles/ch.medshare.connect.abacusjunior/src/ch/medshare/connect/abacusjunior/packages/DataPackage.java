package ch.medshare.connect.abacusjunior.packages;

import java.util.Collections;
import java.util.MissingResourceException;

import ch.elexis.core.importer.div.importers.TransientLabResult;
import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil;

import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class DataPackage extends Package {
	
	public DataPackage(char id, String message){
		super(id, message);
	}
	
	public void fetchResults(Patient actPatient){
		TimeTool date = new TimeTool();
		;
		
		for (String line : getMessage().split("\n")) {
			String[] cells = line.split("\t");
			if (cells.length >= 2) {
				if (cells[0].equals("DATE")) {
					date.set(cells[1]);
					continue;
				}
				if (cells[0].equals("TIME")) {
					date.set(cells[1].substring(0, 2) + ":" + cells[1].substring(2, 4) + ":"
						+ cells[1].substring(4, 6));
					continue;
				}
				
				try {
					Value val = Value.getValue(cells[0]);
					
					TransientLabResult result =
						val.fetchValue(actPatient, cells[1], cells.length >= 3 ? cells[2] : "",
							date);
					new LabImportUtil().importLabResults(Collections.singletonList(result),
						new DefaultLabImportUiHandler());
				} catch (MissingResourceException ex) {
					// Value will not be recorded
				}
			}
		}
	}
}
