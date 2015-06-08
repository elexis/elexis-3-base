package at.medevit.elexis.cobasmira.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.milyn.csv.prog.CSVListBinder;

import at.medevit.elexis.cobasmira.Activator;
import at.medevit.elexis.cobasmira.ui.Preferences;
import ch.elexis.core.data.activator.CoreHub;

public class CobasMiraMapping {
	public static final String CSV_FORMAT =
		"testNameCM,testNameShort,TestName,laborwertID,refM,refW,noDecPlaces";
	protected static List<CobasMiraMappingLabitem> cmmappings;
	
	public static List<CobasMiraMappingLabitem> getCmmappings(){
		if (cmmappings == null) {
			CobasMiraMapping.initializeMapping();
		}
		return cmmappings;
	}
	
	public static void initializeMapping(){
		String inputFilename =
			CoreHub.localCfg.get(Preferences.MAPPINGSCSVFILE, "/Users/marco/cmmli.csv");
		
		try {
			CSVListBinder binder =
				new CSVListBinder(CobasMiraMapping.CSV_FORMAT, CobasMiraMappingLabitem.class);
			cmmappings = binder.bind(new FileInputStream(new File(inputFilename)));
			cmmappings.remove(0);
		} catch (FileNotFoundException e) {
			Status status =
				new Status(IStatus.WARNING, Activator.PLUGIN_ID, "CSV Mapping File nicht gefunden",
					e);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
		}
	}
	
	public static String getId(String testName){
		for (CobasMiraMappingLabitem cMMLI : cmmappings) {
			if (cMMLI.getTestNameCM().equalsIgnoreCase(testName))
				return cMMLI.getLaborwertID();
		}
		return null;
	}
	
	public static int getNoDecPlaces(String testName){
		for (CobasMiraMappingLabitem cMMLI : cmmappings) {
			try {
				if (cMMLI.getTestNameCM().equalsIgnoreCase(testName))
					return Integer.parseInt(cMMLI.getNoDecPlaces());
			} catch (NumberFormatException e) {
				Status status =
					new Status(IStatus.WARNING, Activator.PLUGIN_ID,
						"Error parsing integer string", e);
				StatusManager.getManager().handle(status, StatusManager.SHOW);
			}
		}
		return 0;
	}
}