/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

public class ArtikelstammLabelProvider extends LabelProvider {
	
	private static Image emptyTransparent = ResourceManager.getPluginImage(
		"at.medevit.ch.artikelstamm.ui", "rsc/icons/emptyTransparent.png");
	private static Image pharmaMain = ResourceManager.getPluginImage(
		"at.medevit.ch.artikelstamm.ui", "rsc/icons/pharma.png");
	private static Image nonPharmaMain = ResourceManager.getPluginImage(
		"at.medevit.ch.artikelstamm.ui", "rsc/icons/nonPharma.png");
	private static Image slMain = ResourceManager.getPluginImage("at.medevit.ch.artikelstamm.ui",
		"rsc/icons/sl.png");
	
	@Override
	public String getText(Object element){
		IArtikelstammItem item = (IArtikelstammItem) element;
		StringBuilder sb = new StringBuilder();
		if (item.getDeductible() > 0) {
			sb.append("[" + item.getDeductible() + "%] ");
		}
		sb.append(item.getLabel());
		return sb.toString();
	}
	
	@Override
	public Image getImage(Object element){
		IArtikelstammItem item = (IArtikelstammItem) element;
		
		switch (item.getType()) {
		case N:
			return nonPharmaMain;
		case P:
			return (item.isInSLList()) ? slMain : pharmaMain;
		}
		return emptyTransparent;
	}
	
}
