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

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.selectors.ActiveControl;
import ch.elexis.core.ui.util.SWTHelper;

import com.hilotec.elexis.messwerte.v2.data.Messwert;
import com.hilotec.elexis.messwerte.v2.data.MesswertBase;

/**
 * @author Patrick Chaubert
 */
public class MesswertTypCount extends MesswertBase implements IMesswertTyp {
	
	private static final String CONFIG_BASE_NAME = "com/hilotec/messwerte/v2/"; //$NON-NLS-1$
	private String counterMode = "global_counter"; //$NON-NLS-1$
	private final DecimalFormat df = new DecimalFormat("#,000"); //$NON-NLS-1$
	private int startValue = 0;
	
	public MesswertTypCount(String n, String t, String u){
		super(n, t, u);
		df.setRoundingMode(RoundingMode.HALF_UP);
	}
	
	public String getFormatPattern(){
		return df.toPattern();
	}
	
	public void setFormatPattern(String pattern){
		df.applyPattern(pattern);
	}
	
	public String getDefault(Messwert messwert){
		return ""; //$NON-NLS-1$
	}
	
	public void setCounterMode(String cn){
		this.counterMode = cn;
	}
	
	public void setDefault(String def){}
	
	public void setStartValue(String sv){
		this.startValue = Integer.parseInt(sv);
	}
	
	public Widget createWidget(Composite parent, Messwert messwert){
		widget = SWTHelper.createText(parent, 1, SWT.NONE);
		((Text) widget).setText(messwert.getWert());
		((Text) widget).setEditable(false);
		setShown(true);
		return widget;
	}
	
	public ActiveControl createControl(Composite parent, Messwert messwert, boolean bEditable){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void saveInput(Messwert messwert){
		String s = messwert.getWert();
		if (s.equals("")) { //$NON-NLS-1$
			int value = CoreHub.globalCfg.get(CONFIG_BASE_NAME + counterMode, startValue);
			if (value < startValue) {
				value = startValue;
			} else {
				value++;
			}
			CoreHub.globalCfg.set(CONFIG_BASE_NAME + counterMode, value);
			CoreHub.globalCfg.flush();
			messwert.setWert(df.format(value));
		}
	}
	
	public String getDarstellungswert(String wert){
		return wert;
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
	
	public String erstelleDarstellungswert(Messwert messwert){
		try {
			return df.format(Double.parseDouble(messwert.getWert()));
		} catch (Exception e) {}
		return ""; //$NON-NLS-1$
	}
	
}
