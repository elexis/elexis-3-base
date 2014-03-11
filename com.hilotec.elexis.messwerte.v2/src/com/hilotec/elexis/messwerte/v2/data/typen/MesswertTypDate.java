/*******************************************************************************
 * Copyright (c) 2009-2010, Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    P. Chaubert - initial implementation 
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *    
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data.typen;

import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import ch.elexis.core.ui.selectors.ActiveControl;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.messwerte.v2.data.Messwert;
import com.hilotec.elexis.messwerte.v2.data.MesswertBase;
import com.tiff.common.ui.datepicker.DatePickerCombo;

/**
 * @author Patrick Chaubert
 */
public class MesswertTypDate extends MesswertBase implements IMesswertTyp {
	TimeTool defVal = new TimeTool(); // default: heutiges Datum
	public static String DATE_FORMAT = "dd.MM.yyyy"; //$NON-NLS-1$
	
	public MesswertTypDate(String n, String t, String u){
		super(n, t, u);
	}
	
	public String erstelleDarstellungswert(Messwert messwert){
		return messwert.getWert();
	}
	
	public String getDefault(Messwert messwert){
		TimeTool retVal = defVal;
		if (formula != null) {
			String sWert =
				evalateFormula(formula, messwert,
					new SimpleDateFormat(DATE_FORMAT).format(retVal.getTime()));
			if (sWert != null) {
				try {
					long millis = Long.parseLong(sWert);
					retVal = new TimeTool(millis);
				} catch (Exception e) {
					retVal = new TimeTool(sWert);
				}
			}
			
		}
		return new SimpleDateFormat(DATE_FORMAT).format(retVal.getTime());
	}
	
	public void setDefault(String def){
		defVal = new TimeTool(def);
	}
	
	public Widget createWidget(Composite parent, Messwert messwert){
		widget = new DatePickerCombo(parent, SWT.NONE);
		((DatePickerCombo) widget).setFormat(new SimpleDateFormat(DATE_FORMAT));
		((DatePickerCombo) widget).setDate(new TimeTool(messwert.getWert()).getTime());
		((DatePickerCombo) widget).setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		setShown(true);
		return widget;
	}
	
	public String getDarstellungswert(String wert){
		return wert;
	}
	
	@Override
	public void saveInput(Messwert messwert){
		messwert.setWert(((DatePickerCombo) widget).getText());
	}
	
	@Override
	public String getActualValue(){
		return ((DatePickerCombo) widget).getText();
	}
	
	public ActiveControl createControl(Composite parent, Messwert messwert, boolean bEditable){
		// TODO Auto-generated method stub
		return null;
	}
}
