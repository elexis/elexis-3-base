/*******************************************************************************
 * Copyright (c) 2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    P. Chaubert - adapted to Messwerte V2
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *    
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data.typen;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import ch.elexis.core.ui.selectors.ActiveControl;
import ch.elexis.core.ui.selectors.TextField;
import ch.elexis.core.ui.util.SWTHelper;

import com.hilotec.elexis.messwerte.v2.data.Messwert;
import com.hilotec.elexis.messwerte.v2.data.MesswertBase;

/**
 * @author Antoine Kaufmann
 */
public class MesswertTypStr extends MesswertBase implements IMesswertTyp {
	String defVal = ""; //$NON-NLS-1$
	
	/**
	 * Anzahl Zeilen, die das Textfeld haben soll
	 */
	int lines = 1;
	
	public MesswertTypStr(String n, String t, String u){
		super(n, t, u);
	}
	
	public String erstelleDarstellungswert(Messwert messwert){
		return messwert.getWert();
	}
	
	public String getDefault(Messwert messwert){
		String retVal = defVal;
		if (formula != null) {
			String sWert = evalateFormula(formula, messwert, defVal);
			if (sWert != null)
				retVal = sWert;
		}
		return retVal;
	}
	
	public void setDefault(String def){
		defVal = def;
	}
	
	/**
	 * Anzahl der anzuzeigenden Zeilen setzen
	 */
	public void setLines(int l){
		lines = l;
	}
	
	public Widget createWidget(Composite parent, Messwert messwert){
		if (lines > 2) {
			widget = SWTHelper.createText(parent, lines, SWT.MULTI | SWT.V_SCROLL);
		} else if (lines == 2) {
			widget = SWTHelper.createText(parent, lines, SWT.MULTI | SWT.WRAP);
		} else {
			widget = SWTHelper.createText(parent, lines, SWT.SINGLE);
		}
		((Text) widget).setText(messwert.getWert());
		((Text) widget).setEditable(editable);
		setShown(true);
		return widget;
	}
	
	public ActiveControl createControl(Composite parent, Messwert messwert, boolean bEditable){
		int flags = 0;
		if (!bEditable) {
			flags |= TextField.READONLY;
		}
		IMesswertTyp dft = messwert.getTyp();
		String labelText = dft.getTitle();
		if (!dft.getUnit().equals("")) { //$NON-NLS-1$
			labelText += " [" + dft.getUnit() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (labelText.length() == 0) {
			flags |= TextField.HIDE_LABEL;
		}
		TextField tf = new TextField(parent, flags, labelText);
		tf.setText(messwert.getDarstellungswert());
		return tf;
	}
	
	public String getDarstellungswert(String wert){
		return wert;
	}
	
	@Override
	public void saveInput(Messwert messwert){
		messwert.setWert(((Text) widget).getText());
	}
	
	@Override
	public boolean checkInput(Messwert messwert, String pattern){
		if (((Text) widget).getText().matches(pattern) || pattern == null) {
			return true;
		}
		return false;
	}
	
	@Override
	public String getActualValue(){
		return ((Text) widget).getText();
	}
}
