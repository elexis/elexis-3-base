package at.medevit.elexis.gdt.ui.dialog.provider;

import org.eclipse.jface.viewers.LabelProvider;

import at.medevit.elexis.gdt.constants.GDTConstants;

public class ComboViewerGeschlechtLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element){
		String value = (String) element;
		if(value.equalsIgnoreCase(GDTConstants.SEX_MALE+"")) return "Männlich";
		if(value.equalsIgnoreCase(GDTConstants.SEX_FEMALE+"")) return "Weiblich";
		return "Unbekannt";
	}
	
}
