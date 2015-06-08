package at.medevit.elexis.cobasmira.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import at.medevit.elexis.cobasmira.model.CobasMiraMessage;
import ch.rgw.tools.TimeTool;

import com.swtdesigner.ResourceManager;

public class CobasMiraLogLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {
	
	@Override
	public Image getColumnImage(Object element, int columnIndex){
		CobasMiraMessage elem = (CobasMiraMessage) element;
		int status = elem.getElexisStatus();
		
		if (columnIndex == 3) {
			if (status == CobasMiraMessage.ELEXIS_RESULT_INTEGRATION_OK) {
				return ResourceManager.getPluginImage("at.medevit.elexis.cobasmira",
					"rsc/check2.png");
			} else if (status == CobasMiraMessage.ELEXIS_RESULT_LABITEM_NOT_FOUND) {
				return ResourceManager.getPluginImage("at.medevit.elexis.cobasmira",
					"rsc/error.png");
			} else if (status == CobasMiraMessage.ELEXIS_RESULT_PATIENT_NOT_FOUND) {
				return ResourceManager.getPluginImage("at.medevit.elexis.cobasmira",
					"rsc/error.png");
			} else if (status == CobasMiraMessage.ELEXIS_RESULT_RESULT_ALREADY_HERE) {
				return ResourceManager.getPluginImage("at.medevit.elexis.cobasmira",
					"rsc/unknown.png");
			} else if (status == CobasMiraMessage.ELEXIS_RESULT_CONTROL_OK) {
				return ResourceManager.getPluginImage("at.medevit.elexis.cobasmira",
					"rsc/check2.png");
			} else if (status == CobasMiraMessage.ELEXIS_RESULT_CONTROL_ERR) {
				return ResourceManager.getPluginImage("at.medevit.elexis.cobasmira",
					"rsc/error.png");
			}
		}
		return null;
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex){
		if (element instanceof CobasMiraMessage) {
			CobasMiraMessage elem = (CobasMiraMessage) element;
			int blockType = elem.getBlockType();
			
			if (columnIndex == 0) {
				TimeTool entryDate = elem.getEntryDate();
				return entryDate.toString(TimeTool.FULL_GER);
			}
			if (columnIndex == 1)
				return elem.getBlockTypeString();
			
			if (columnIndex == 2) {
				if (blockType == CobasMiraMessage.BLOCK_TYPE_PATIENT_RESULTS) {
					int noResults = elem.getNoPatientResults();
					if (noResults != 1)
						return noResults + " Resultat(e) erhalten.";
					return "Resultat erhalten: " + elem.getSinglePatientResultInfo();
				} else if (blockType == CobasMiraMessage.BLOCK_TYPE_ERROR_MESSAGE) {
					return elem.getErrorMessageString();
				} else if (blockType == CobasMiraMessage.BLOCK_TYPE_RACK_INFORMATION) {
					return "";
				}
			}
			
			if (columnIndex == 3) {
				int status = elem.getElexisStatus();
				if (status == CobasMiraMessage.ELEXIS_RESULT_IGNORED)
					return "";
				if (status == CobasMiraMessage.ELEXIS_RESULT_INTEGRATION_OK)
					return "OK";
				if (status == CobasMiraMessage.ELEXIS_RESULT_LABITEM_NOT_FOUND)
					return "Laboritem nicht gefunden";
				if (status == CobasMiraMessage.ELEXIS_RESULT_PATIENT_NOT_FOUND)
					return "Patient nicht gefunden";
				if (status == CobasMiraMessage.ELEXIS_RESULT_CONTROL_OK)
					return "Protokolliert";
				if (status == CobasMiraMessage.ELEXIS_RESULT_CONTROL_ERR)
					return "Fehler bei Protokollierung";
			}
		}
		return "not defined";
	}
}
