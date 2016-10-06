/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt.ui.dialog.provider;

import org.eclipse.jface.viewers.LabelProvider;

import at.medevit.elexis.gdt.constants.GDTConstants;

public class ComboViewerVersichertenartLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element){;
		String value = (String) element;
		if(value.equalsIgnoreCase(GDTConstants.VERSICHERTENART_FAMILIENVERSICHERTER+"")) return "Familie";
		if(value.equalsIgnoreCase(GDTConstants.VERSICHERTENART_RENTNER+"")) return "Rentner";
		if(value.equalsIgnoreCase(GDTConstants.VERSICHERTENART_MITGLIED+"")) return "Mitglied";
		return "Unbekannt";
	}
}
