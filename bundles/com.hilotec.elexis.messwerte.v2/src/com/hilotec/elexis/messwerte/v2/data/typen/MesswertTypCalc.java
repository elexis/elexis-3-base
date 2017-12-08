/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2012
 * 
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data.typen;

import java.math.RoundingMode;
import java.text.DecimalFormat;

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
public class MesswertTypCalc extends MesswertBase implements IMesswertTyp {
	private String defVal = ""; //$NON-NLS-1$
	
	/**
	 * Decimal Format für die Anzeige
	 */
	DecimalFormat df = new DecimalFormat("#0.#"); //$NON-NLS-1$
	
	public MesswertTypCalc(String n, String t, String u){
		super(n, t, u);
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
	
	public String erstelleDarstellungswert(Messwert messwert){
		String wert = defVal;
		try {
			wert = evalateFormula(formula, messwert, defVal);
			if ("".equals(wert))
				return wert;
			try {
				return df.format(Double.parseDouble(wert));
			} catch (Exception e) {
				return df.format(wert);
			}
		} catch (Exception e) {
			// Wenn formatieren von 'wert' fehlschlägt (bei String oder Datum),
			// den Wert unformatiert zurückgeben
			return wert.toString();
		}
	}
	
	public String getDefault(Messwert messwert){
		return evalateFormula(formula, messwert, defVal);
	}
	
	public void setDefault(String str){
		defVal = str;
	}
	
	public Widget createWidget(Composite parent, Messwert messwert){
		widget = SWTHelper.createText(parent, 1, SWT.NONE);
		((Text) widget).setText(erstelleDarstellungswert(messwert));
		((Text) widget).setEditable(false);
		setShown(true);
		return widget;
	}
	
	public ActiveControl createControl(Composite parent, Messwert messwert, boolean bEditable){
		IMesswertTyp dft = messwert.getTyp();
		String labelText = dft.getTitle();
		if (!dft.getUnit().equals("")) { //$NON-NLS-1$
			labelText += " [" + dft.getUnit() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		TextField tf = new TextField(parent, ActiveControl.READONLY, labelText);
		tf.setText(erstelleDarstellungswert(messwert));
		return tf;
		
	}
	
	public void calcNewValue(Messwert messwert){
		((Text) widget).setText(erstelleDarstellungswert(messwert));
		super.checkInput(messwert, messwert.getTyp().getValidpattern());
	}
	
	public String getDarstellungswert(String wert){
		return wert;
	}
	
	@Override
	public void saveInput(Messwert messwert){
		messwert.setWert(erstelleDarstellungswert(messwert));
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
	
	@Override
	public String getActualValue(){
		return ((Text) widget).getText();
	}
}
