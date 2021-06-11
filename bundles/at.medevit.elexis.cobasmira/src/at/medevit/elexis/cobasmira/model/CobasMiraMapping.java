package at.medevit.elexis.cobasmira.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import at.medevit.elexis.cobasmira.Activator;
import at.medevit.elexis.cobasmira.ui.Preferences;
import ch.elexis.core.data.activator.CoreHub;

public class CobasMiraMapping {
	public static final String CSV_FORMAT =
		"testNameCM,testNameShort,TestName,laborwertID,refM,refW,noDecPlaces";
	protected static List<CobasMiraMappingLabitem> cobasMappings;
	
	public static List<CobasMiraMappingLabitem> getCmmappings(){
		if (cobasMappings == null) {
			CobasMiraMapping.initializeMapping();
		}
		return cobasMappings;
	}
	
	public static void initializeMapping(){
		cobasMappings = new ArrayList<CobasMiraMappingLabitem>();
		String csvFilename =
			CoreHub.localCfg.get(Preferences.MAPPINGSCSVFILE,
				Preferences.getDefaultMappingCSVLocation());
		
		try {
			CsvToBean csvBean = new CsvToBean();
			List csvList = csvBean.parse(getMappingStrategy(), new FileReader(csvFilename));
			
			for (Object object : csvList) {
				CobasMiraMappingLabitem cmlItem = (CobasMiraMappingLabitem) object;
				cobasMappings.add(cmlItem);
			}
			
		} catch (FileNotFoundException e) {
			Status status =
				new Status(IStatus.WARNING, Activator.PLUGIN_ID, "CSV Mapping File nicht gefunden",
					e);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
		}
		cobasMappings.remove(0);
	}
	
	private static ColumnPositionMappingStrategy getMappingStrategy(){
		ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
		strategy.setType(CobasMiraMappingLabitem.class);
		String[] columns = new String[] {
			"testNameCM", "testNameShort", "TestName", "laborwertID", "refM", "refW", "noDecPlaces"
		};
		strategy.setColumnMapping(columns);
		return strategy;
	}
	
	public static String getId(String testName){
		for (CobasMiraMappingLabitem cMMLI : cobasMappings) {
			if (cMMLI.getTestNameCM().equalsIgnoreCase(testName))
				return cMMLI.getLaborwertID();
		}
		return null;
	}
	
	public static int getNoDecPlaces(String testName){
		for (CobasMiraMappingLabitem cMMLI : cobasMappings) {
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