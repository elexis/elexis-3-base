package at.medevit.elexis.medicationlist.ui.composites;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;

import ch.elexis.data.Prescription;
import ch.rgw.tools.TimeTool;

public class MedicationCellLabelProvider extends ColumnLabelProvider {
	
	private Color prnColor, fixedMedicationColor;
	private static final int FILTER_PRESCRIPTION_AFTER_N_DAYS = 30;
	
	public MedicationCellLabelProvider(){
		prnColor = SWTResourceManager.getColor(102, 205, 170);
		fixedMedicationColor = SWTResourceManager.getColor(238, 232, 170);
	}

	@Override
	public Color getBackground(Object element){
		Prescription pres = (Prescription) element;
		if(pres.isAsNeededMedication()) return prnColor;
//		if(pres.isFixedMediation()) return fixedMedicationColor;
		
		return null;
	}
	
	@Override
	public Color getForeground(Object element){
		Prescription pres = (Prescription) element;
		if(!MedicationCellLabelProvider.isNotHistorical((Prescription) element)) {
			return SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY);
		}
		if(!pres.isFixedMediation()) return SWTResourceManager.getColor(SWT.COLOR_DARK_RED);
		return super.getForeground(element);
	}

	
	public static boolean isNotHistorical(Prescription element) {
		Prescription presc = (Prescription) element;
		String[] dates = new String[2];
		presc.get(new String[] {
			Prescription.FLD_DATE_FROM, Prescription.FLD_DATE_UNTIL
		}, dates);
		
		if (dates[1].length() != 0)
			return false;
		TimeTool tt = new TimeTool(dates[0]);
		int daysTo = tt.daysTo(new TimeTool());
		if (daysTo > FILTER_PRESCRIPTION_AFTER_N_DAYS)
			return false;
		return true;
	}
	
}
