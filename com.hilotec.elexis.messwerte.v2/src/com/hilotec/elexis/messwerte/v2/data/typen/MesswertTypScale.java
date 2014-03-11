/*******************************************************************************
 * Copyright (c) 2009-2010, A. Kaufmann and Elexis
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

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;

import ch.elexis.core.ui.selectors.ActiveControl;
import ch.elexis.core.ui.selectors.SpinnerField;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.Log;

import com.hilotec.elexis.messwerte.v2.data.Messwert;
import com.hilotec.elexis.messwerte.v2.data.MesswertBase;

/**
 * @author Antoine Kaufmann
 */
public class MesswertTypScale extends MesswertBase implements IMesswertTyp {
	int defVal = 0;
	
	/**
	 * Kleinster auswaehlbarer Wert
	 */
	int min = 0;
	
	/**
	 * Groesster auswaehlbarer Wert
	 */
	int max = 0;
	
	public MesswertTypScale(String n, String t, String u){
		super(n, t, u);
	}
	
	public String erstelleDarstellungswert(Messwert messwert){
		return messwert.getWert();
	}
	
	public String getDefault(Messwert messwert){
		Integer retVal = defVal;
		if (formula != null) {
			String sWert = evalateFormula(formula, messwert, retVal.toString());
			try {
				retVal = Integer.parseInt(sWert);
			} catch (Exception e) {
				log.log(MessageFormat.format(Messages.MesswertTypScale_CastFailure, sWert),
					Log.ERRORS);
			}
		}
		return retVal.toString();
	}
	
	public void setDefault(String str){
		defVal = Integer.parseInt(str);
	}
	
	/**
	 * Groesster auswaehlbarer Wert setzen
	 */
	public void setMax(int m){
		max = m;
	}
	
	/**
	 * Kleinster auswaehlbarer Wert setzen
	 */
	public void setMin(int m){
		min = m;
	}
	
	public Widget createWidget(Composite parent, Messwert messwert){
		widget = new Spinner(parent, SWT.NONE);
		((Spinner) widget).setMinimum(min);
		((Spinner) widget).setMaximum(max);
		((Spinner) widget).setSelection(Integer.parseInt(messwert.getWert()));
		((Spinner) widget).setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		setShown(true);
		return widget;
	}
	
	public String getDarstellungswert(String wert){
		return wert;
	}
	
	@Override
	public void saveInput(Messwert messwert){
		messwert.setWert(Integer.toString(((Spinner) widget).getSelection()));
	}
	
	@Override
	public boolean checkInput(Messwert messwert, String pattern){
		super.checkInput(messwert, pattern);
		String value = ((Spinner) widget).getText();
		if (value.matches(pattern) || pattern == null) {
			return true;
		}
		return false;
	}
	
	public ActiveControl createControl(Composite parent, Messwert messwert, boolean bEditable){
		IMesswertTyp dft = messwert.getTyp();
		String labelText = dft.getTitle();
		if (!dft.getUnit().equals("")) { //$NON-NLS-1$
			labelText += " [" + dft.getUnit() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		SpinnerField sf = new SpinnerField(parent, 0, labelText, min, max);
		sf.setText(messwert.getDarstellungswert());
		return sf;
	}
	
	@Override
	public String getActualValue(){
		return ((Spinner) widget).getText();
	}
}
