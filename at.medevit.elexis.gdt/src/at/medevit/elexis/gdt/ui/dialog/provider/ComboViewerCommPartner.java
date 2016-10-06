package at.medevit.elexis.gdt.ui.dialog.provider;

import org.eclipse.jface.viewers.LabelProvider;

import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;

public class ComboViewerCommPartner extends LabelProvider {
	
	@Override
	public String getText(Object element){
		IGDTCommunicationPartner cp = (IGDTCommunicationPartner) element;
		return cp.getLabel();
	}
	
}
