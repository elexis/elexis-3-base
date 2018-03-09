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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import ch.elexis.core.ui.selectors.ActiveControl;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;

import com.hilotec.elexis.messwerte.v2.data.Messung;
import com.hilotec.elexis.messwerte.v2.data.MessungKonfiguration;
import com.hilotec.elexis.messwerte.v2.data.Messwert;
import com.hilotec.elexis.messwerte.v2.data.MesswertBase;

/**
 * @author Antoine Kaufmann
 */
public class MesswertTypData extends MesswertBase implements IMesswertTyp {
	/**
	 * Messungstyp der Messungen die ausgewaehlt werden koennen
	 */
	String refType;
	
	/**
	 * Liste mit den moeglichen Auswahlen fuer die Combo. Notwendig damit dem Index beim saveInput()
	 * auch wieder der passende Messung zugeordnet werden kann.
	 */
	List<Messung> refChoices;
	
	public MesswertTypData(String n, String t, String u){
		super(n, t, u);
	}
	
	public String erstelleDarstellungswert(Messwert messwert){
		if (messwert.getWert().equals("")) { //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
		Messung m = Messung.load(messwert.getWert());
		return m.getDatum();
	}
	
	public String getDefault(Messwert messwert){
		return ""; //$NON-NLS-1$
	}
	
	public void setDefault(String str){}
	
	/**
	 * Typ der auswaehlbaren Messungen setzen
	 * 
	 * @param t
	 *            Typ
	 */
	public void setRefType(String t){
		refType = t;
	}
	
	public Widget createWidget(Composite parent, Messwert messwert){
		Patient patient = messwert.getMessung().getPatient();
		widget = new Combo(parent, SWT.DROP_DOWN);
		
		refChoices =
			Messung.getPatientMessungen(patient,
				MessungKonfiguration.getInstance().getTypeByName(refType));
		for (int i = 0; i < refChoices.size(); i++) {
			Messung messung = refChoices.get(i);
			((Combo) widget).add(messung.getDatum(), i);
		}
		
		if (!messwert.getWert().equals("")) { //$NON-NLS-1$
			for (int i = 0; i < refChoices.size(); i++) {
				if (refChoices.get(i).getId().equals(messwert.getWert())) {
					((Combo) widget).select(i);
				}
			}
		} else if (refChoices.size() > 0) {
			((Combo) widget).select(0);
		}
		
		((Combo) widget).setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		return widget;
	}
	
	public String getDarstellungswert(String wert){
		String retVal = ""; //$NON-NLS-1$
		if (wert != null) {
			if (!"".equals(wert)) { //$NON-NLS-1$
				Messung m = Messung.load(wert);
				retVal = m.getDatum();
			}
		}
		return retVal;
	}
	
	@Override
	public void saveInput(Messwert messwert){
		int selected = ((Combo) widget).getSelectionIndex();
		messwert.setWert(refChoices.get(selected).getId());
	}
	
	/**
	 * Messung zu einem Data-Messwert heraussuchen
	 * 
	 * @param messwert
	 *            Messwert
	 * 
	 * @return Messwert oder null, wenn noch keine Messung zugewiesen ist
	 */
	public Messung getMessung(Messwert messwert){
		if (messwert.getWert().equals("")) { //$NON-NLS-1$
			return null;
		}
		
		return Messung.load(messwert.getWert());
	}
	
	public ActiveControl createControl(Composite parent, Messwert messwert, boolean bEditable){
		// TODO Auto-generated method stub
		return null;
	}
}
