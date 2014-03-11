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

package com.hilotec.elexis.messwerte.v2.data;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.scripting.Interpreter;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Script;
import ch.rgw.tools.Log;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.messwerte.v2.data.typen.IMesswertTyp;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypBool;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypCalc;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypData;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypDate;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypEnum;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypNum;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypStr;

/**
 * Abstrakte Basisklasse fuer die einzelnen Messwerttypen
 * 
 * @author Antoine Kaufmann
 */
public abstract class MesswertBase {
	
	public static String ICON_TRANSPARENT = "transparent.png"; //$NON-NLS-1$
	protected static String ICON_RED = "pin_red.png"; //$NON-NLS-1$
	protected static String ICON_YELLOW = "pin_yellow.png"; //$NON-NLS-1$
	protected static String ICON_GREEN = "pin_green.png"; //$NON-NLS-1$
	protected static String ICON_BLUE = "pin_blue.png"; //$NON-NLS-1$
	protected static String ICON_BLACK = "pin_black.png"; //$NON-NLS-1$
	
	protected final Log log = Log.get("Messwerte"); //$NON-NLS-1$
	
	private final String name;
	private final String title;
	private final String unit;
	protected boolean editable = true;
	private String validpattern;
	private String invalidmessage;
	private String size;
	private boolean isShown = false;
	protected Widget widget;
	
	private Boolean alerts = false;
	
	private Double highAlert = Double.MAX_VALUE;
	private Double highWarning = Double.MAX_VALUE;
	private Double lowWarning = Double.MIN_VALUE;
	private Double lowAlert = Double.MIN_VALUE;
	
	public final static int SEVERITY_LEVEL_UNDEFINED = Integer.MAX_VALUE;
	public final static int SEVERITY_LEVEL_LOW_ALERT = -2;
	public final static int SEVERITY_LEVEL_LOW_WARNING = -1;
	public final static int SEVERITY_LEVEL_NORMAL = 0;
	public final static int SEVERITY_LEVEL_HIGH_WARNING = 1;
	public final static int SEVERITY_LEVEL_HIGH_ALERT = 2;
	
	/**
	 * Eigentlicher Code der Formel
	 */
	protected String formula;
	
	/**
	 * Interpreter, der benutzt werden soll, um die
	 */
	protected String interpreter;
	
	/**
	 * Liste mit den Variablen die fuer die Formel gesetzt werden sollen
	 */
	protected final ArrayList<CalcVar> variables = new ArrayList<CalcVar>();
	
	public MesswertBase(String n, String t, String u){
		name = n;
		title = t;
		unit = u;
	}
	
	public String getName(){
		return name;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getUnit(){
		return unit;
	}
	
	public boolean isEditable(){
		return editable;
	}
	
	public void setEditable(boolean editable){
		this.editable = editable;
	}
	
	public String getValidpattern(){
		return validpattern;
	}
	
	public void setValidpattern(String validpattern){
		this.validpattern = validpattern;
	}
	
	public String getInvalidmessage(){
		return invalidmessage;
	}
	
	public void setInvalidmessage(String invalidmessage){
		this.invalidmessage = invalidmessage;
	}
	
	public String getSize(){
		return size;
	}
	
	public void setSize(String size){
		this.size = size;
	}
	
	public void saveInput(Messwert messwert){}
	
	public boolean checkInput(Messwert messwert, String pattern){
		IMesswertTyp typ = messwert.getTyp();
		if (typ.isAlertEnabled()) {
			String path =
				PlatformHelper.getBasePath("com.hilotec.elexis.messwerte.v2") + File.separator //$NON-NLS-1$
					+ "rsc" + File.separator; //$NON-NLS-1$
			Label lab = messwert.getIconLabel();
			Image image = null;
			switch (typ.getSeverityLevel(widget)) {
			case MesswertBase.SEVERITY_LEVEL_UNDEFINED:
				image = new Image(lab.getDisplay(), path + ICON_TRANSPARENT);
				break;
			case MesswertBase.SEVERITY_LEVEL_HIGH_ALERT:
				image = new Image(lab.getDisplay(), path + ICON_RED);
				break;
			case MesswertBase.SEVERITY_LEVEL_HIGH_WARNING:
				image = new Image(lab.getDisplay(), path + ICON_YELLOW);
				break;
			case MesswertBase.SEVERITY_LEVEL_NORMAL:
				image = new Image(lab.getDisplay(), path + ICON_GREEN);
				break;
			case MesswertBase.SEVERITY_LEVEL_LOW_WARNING:
				image = new Image(lab.getDisplay(), path + ICON_BLUE);
				break;
			case MesswertBase.SEVERITY_LEVEL_LOW_ALERT:
				image = new Image(lab.getDisplay(), path + ICON_BLACK);
				break;
			}
			if (image != null)
				lab.setImage(image);
			
		}
		return true;
	}
	
	public String getActualValue(){
		return ""; //$NON-NLS-1$
	}
	
	public boolean isShown(){
		return isShown;
	}
	
	public void setShown(boolean isShown){
		this.isShown = isShown;
	}
	
	/**
	 * Kontext des Interpreters vorbereiten um die Formel auswerten zu koennen. Dabei werden die
	 * Variablen importiert.
	 * 
	 * TODO: Ist noch Beanshell-spezifisch
	 * 
	 * @param interpreter
	 *            Interpreter
	 * @param messung
	 *            Messung in der die Formel ausgewertet werden soll
	 * @throws EvalError
	 */
	protected void interpreterSetzeKontext(Interpreter interpreter, Messung messung)
		throws ElexisException{
		for (CalcVar cv : variables) {
			Object wert = holeVariable(messung, cv.getName(), cv.getSource());
			if (wert != null) {
				interpreter.setValue(cv.getName(), wert);
			}
		}
		interpreter.setValue("actPatient", ElexisEventDispatcher.getSelectedPatient()); //$NON-NLS-1$
		interpreter.setValue("actFall", ElexisEventDispatcher.getSelected(Fall.class)); //$NON-NLS-1$
		interpreter.setValue("actKons", ElexisEventDispatcher.getSelected(Konsultation.class)); //$NON-NLS-1$
		interpreter.setValue("actMandant", CoreHub.actMandant); //$NON-NLS-1$
		interpreter.setValue("actUser", CoreHub.actUser); //$NON-NLS-1$
		interpreter.setValue("Elexis", Hub.plugin); //$NON-NLS-1$
	}
	
	/**
	 * Wert einer Variable fuer die Formel bestimmen
	 * 
	 * @param messung
	 *            Messung in der die Formel ausgewertet werten soll
	 * @param name
	 *            Name der Variable. Kann mit . getrennt sein, wenn sich links vom Punkt jeweils ein
	 *            Data-Feld befindet, dabei bezieht sich der Teil rechts vom Punkt auf das Feld in
	 *            dem referenzierten Objekt.
	 * @param source
	 *            Quelle der Variable
	 * 
	 * @return Wert der dem Interpreter uebergeben werden soll. Haengt vom typ der Variable ab.
	 */
	protected Object holeVariable(Messung messung, String name, String source){
		if (messung == null) {
			return "messung?"; //$NON-NLS-1$
		}
		if (source == null) {
			return "source?"; //$NON-NLS-1$
		}
		String[] parts = source.split("\\."); //$NON-NLS-1$
		Messwert messwert = messung.getMesswert(parts[0]);
		IMesswertTyp typ = messwert.getTyp();
		
		if (parts.length == 1) {
			if (typ instanceof MesswertTypNum) {
				if (typ.isShown()) {
					return Double.parseDouble(typ.getActualValue());
				} else {
					return Double.parseDouble(messwert.getWert());
				}
			} else if (typ instanceof MesswertTypBool) {
				if (typ.isShown()) {
					return Boolean.parseBoolean(typ.getActualValue());
				} else {
					return Boolean.parseBoolean(messwert.getWert());
				}
			} else if (typ instanceof MesswertTypStr) {
				if (typ.isShown()) {
					return typ.getActualValue();
				} else {
					return messwert.getWert();
				}
			} else if (typ instanceof MesswertTypCalc) {
				if (typ.isShown()) {
					return Double.parseDouble(typ.getActualValue());
				} else {
					return Double.parseDouble(messwert.getDarstellungswert());
				}
			} else if (typ instanceof MesswertTypEnum) {
				if (typ.isShown()) {
					return Integer.parseInt(typ.getActualValue());
				} else {
					return Integer.parseInt(messwert.getWert());
				}
			} else if (typ instanceof MesswertTypDate) {
				TimeTool tt;
				if (typ.isShown()) {
					tt = new TimeTool(typ.getActualValue());
				} else {
					tt = new TimeTool(messwert.getWert());
				}
				return tt.getTimeInMillis();
				// if (typ.isShown()) {
				// TimeTool tt = new TimeTool(typ.getActualValue());
				// return tt.get(TimeTool.YEAR) * 365 + tt.get(TimeTool.DAY_OF_YEAR);
				// } else {
				// TimeTool tt = new TimeTool(messwert.getWert());
				// return tt.get(TimeTool.YEAR) * 365 + tt.get(TimeTool.DAY_OF_YEAR);
				// }
			} else if (typ instanceof MesswertTypData) {
				log.log(MessageFormat.format(Messages.MesswertBase_Failure1, name,
					Messages.MesswertBase_DataField), Log.ERRORS);
				return null;
			}
		}
		
		if (!(typ instanceof MesswertTypData)) {
			log.log(MessageFormat.format(Messages.MesswertBase_Failure1, name,
				Messages.MesswertBase_NoData), Log.ERRORS);
			return null;
		}
		MesswertTypData t = (MesswertTypData) typ;
		Messung dm = t.getMessung(messwert);
		return holeVariable(dm, name + "." + parts[0], source.substring(source.indexOf(".") + 1)); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Interne Klasse die eine Variable fuer die Formel darstellt(nur deklaration).
	 * 
	 * @author Antoine Kaufmann
	 */
	private class CalcVar {
		/**
		 * Name der Variable
		 */
		private final String name;
		
		/**
		 * Quelle der Variable(meist Feldname in der Messung)
		 */
		private final String source;
		
		CalcVar(String n, String s){
			name = n;
			source = s;
		}
		
		String getName(){
			return name;
		}
		
		String getSource(){
			return source;
		}
	}
	
	/**
	 * Neue Variable hinzufuegen
	 * 
	 * @param name
	 *            Name der Variable
	 * @param source
	 *            Quelle fuer den Variableninhalt
	 */
	public void addVariable(String name, String source){
		variables.add(new CalcVar(name, source));
	}
	
	/**
	 * Formel, die berechnet werden soll, setzen.
	 * 
	 * @param f
	 *            Formel
	 * @param i
	 *            Interpreter fuer die Formel
	 */
	public void setFormula(String f, String i){
		formula = f;
		interpreter = i;
	}
	
	public String evalateFormula(String formula, Messwert messwert){
		return evalateFormula(formula, messwert, ""); //$NON-NLS-1$
	}
	
	public String evalateFormula(String formula, Messwert messwert, String defaultValue){
		try {
			Interpreter interpreter = Script.getInterpreterFor(formula);
			Messung messung = null;
			if (messwert != null)
				messung = messwert.getMessung();
			interpreterSetzeKontext(interpreter, messung);
			Object wert = interpreter.run(formula, false);
			if (wert == null)
				wert = defaultValue;
			return String.valueOf(wert);
		} catch (ElexisException e) {
			e.printStackTrace();
			String message =
				MessageFormat
					.format(Messages.MesswertBase_Failure2, formula.trim(), e.getMessage());
			log.log(message, Log.ERRORS);
			if (System.getProperty("com.hilotec.unitests") == null
				|| !System.getProperty("com.hilotec.unitests").equals("1"))
				SWTHelper.showError(Messages.DataAccessor_Title, message);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	
	public void setLowAlertValue(String value){
		alerts = true;
		lowAlert = Double.MIN_VALUE;
		try {
			lowAlert = Double.parseDouble(value);
		} finally {}
	}
	
	public void setLowWarningValue(String value){
		alerts = true;
		lowWarning = Double.MIN_VALUE;
		try {
			lowWarning = Double.parseDouble(value);
		} finally {}
	}
	
	public void setHighAlertValue(String value){
		alerts = true;
		highAlert = Double.MAX_VALUE;
		try {
			highAlert = Double.parseDouble(value);
		} finally {}
	}
	
	public void setHighWarningValue(String value){
		alerts = true;
		highWarning = Double.MAX_VALUE;
		try {
			highWarning = Double.parseDouble(value);
		} finally {}
	}
	
	public Boolean isAlertEnabled(){
		return alerts;
	}
	
	public int getSeverityLevel(Widget widget){
		int retVal = SEVERITY_LEVEL_UNDEFINED;
		try {
			String strValue = ((Text) widget).getText();
			Double value = Double.parseDouble(strValue);
			retVal = SEVERITY_LEVEL_NORMAL;
			if (value > highAlert) {
				retVal = SEVERITY_LEVEL_HIGH_ALERT;
			} else if (value > highWarning) {
				retVal = SEVERITY_LEVEL_HIGH_WARNING;
			} else if (value < lowAlert) {
				retVal = SEVERITY_LEVEL_LOW_ALERT;
			} else if (value < lowWarning) {
				retVal = SEVERITY_LEVEL_LOW_WARNING;
			}
			
		} catch (Exception e) {}
		
		return retVal;
	}
	
}
