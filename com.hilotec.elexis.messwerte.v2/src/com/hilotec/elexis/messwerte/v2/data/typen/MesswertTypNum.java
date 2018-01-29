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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import ch.elexis.core.ui.selectors.ActiveControl;
import ch.elexis.core.ui.selectors.TextField;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.Log;

import com.hilotec.elexis.messwerte.v2.data.Messwert;
import com.hilotec.elexis.messwerte.v2.data.MesswertBase;

/**
 * @author Antoine Kaufmann
 */
public class MesswertTypNum extends MesswertBase implements IMesswertTyp {
	double defVal = 0.0;
	
	/**
	 * Format für die Anzeige des Wertes
	 */
	private final DecimalFormat df = new DecimalFormat("#0.#"); //$NON-NLS-1$
	
	public MesswertTypNum(String n, String t, String u){
		super(n, t, u);
		df.setRoundingMode(RoundingMode.HALF_UP);
	}
	
	public String erstelleDarstellungswert(Messwert messwert){
		try {
			return df.format(Double.parseDouble(messwert.getWert()));
		} catch (Exception e) {}
		return ""; //$NON-NLS-1$
	}
	
	public String getDefault(Messwert messwert){
		Double dWert = defVal;
		if (formula != null) {
			String sWert = evalateFormula(formula, messwert, df.format(dWert));
			try {
				dWert = Double.parseDouble(sWert);
			} catch (Exception e) {
				log.log(MessageFormat.format(Messages.MesswertTypNum_CastFailure, sWert),
					Log.ERRORS);
			}
		}
		return df.format(dWert);
	}
	
	public void setDefault(String str){
		defVal = Double.parseDouble(str);
	}
	
	public String getFormatPattern(){
		return df.toPattern();
	}
	
	public void setFormatPattern(String pattern){
		df.applyPattern(pattern);
	}
	
	public String getRoundingMode(){
		return df.getRoundingMode().toString();
	}
	
	public void setRoundingMode(String roundingMode){
		df.setRoundingMode(RoundingMode.valueOf(roundingMode));
	}
	
	public Widget createWidget(Composite parent, Messwert messwert){
		widget = SWTHelper.createText(parent, 1, SWT.SINGLE);
		try {
			((Text) widget).setText(df.format(Double.parseDouble(messwert.getWert())));
		} catch (Exception e) {
			// Fängt den Fall ab, wenn ein ungültiger Wert in der DB gespeichert ist
			((Text) widget).setText(messwert.getWert());
		}
		((Text) widget).setEditable(editable);
		setShown(true);
		return widget;
	}
	
	public String getDarstellungswert(String wert){
		return wert;
	}
	
	@Override
	public String getActualValue(){
		String s = ((Text) widget).getText();
		if (s == "") //$NON-NLS-1$
			s = "0"; //$NON-NLS-1$
		return s;
	}
	
	@Override
	public void saveInput(Messwert messwert){
		messwert.setWert(((Text) widget).getText());
	}
	
	@Override
	public boolean checkInput(Messwert messwert, String pattern){
		super.checkInput(messwert, pattern);
		String value = ((Text) widget).getText();
		if (value.matches(pattern) || pattern == null) {
			return true;
		}
		return false;
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
}
