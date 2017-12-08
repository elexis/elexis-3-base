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
package at.medevit.elexis.medietikette.data;

import java.util.ArrayList;
import java.util.List;

import at.medevit.elexis.medietikette.Messages;
import ch.elexis.core.data.interfaces.IDataAccess;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.rgw.tools.Result;

public class DataAccessor implements IDataAccess {
	
	private Element[] elements = {
		new Element(IDataAccess.TYPE.STRING, "Name", //$NON-NLS-1$
			"[Medietikette:-:-:Name]", null, 0), new Element(IDataAccess.TYPE.STRING, "Preis", //$NON-NLS-1$ //$NON-NLS-2$
			"[Medietikette:-:-:Preis]", null, 0), new Element(IDataAccess.TYPE.STRING, "OPGroesse", //$NON-NLS-1$ //$NON-NLS-2$
			"[Medietikette:-:-:OPGroesse]", null, 0), new Element(IDataAccess.TYPE.STRING, "Dosis", //$NON-NLS-1$ //$NON-NLS-2$
			"[Medietikette:-:-:Dosis]", null, 0), new Element(IDataAccess.TYPE.STRING, "EAN", //$NON-NLS-1$ //$NON-NLS-2$
			"[Medietikette:-:-:EAN]", null, 0), new Element(IDataAccess.TYPE.STRING, "Pharmacode", //$NON-NLS-1$ //$NON-NLS-2$
			"[Medietikette:-:-:Pharmacode]", null, 0), //$NON-NLS-1$
		new Element(IDataAccess.TYPE.STRING, "Vorschrift", //$NON-NLS-1$
			"[Medietikette:-:-:Vorschrift]", null, 0) //$NON-NLS-1$
		};
	
	ArrayList<Element> elementsList;
	
	private static Prescription selectedPrescription;
	private static Artikel selectedArticel;
	
	public DataAccessor(){
		// initialize the list of defined elements
		elementsList = new ArrayList<Element>();
		for (int i = 0; i < elements.length; i++)
			elementsList.add(elements[i]);
	}
	
	@Override
	public String getName(){
		return Messages.DataAccessor_Name;
	}
	
	@Override
	public String getDescription(){
		return Messages.DataAccessor_Description;
	}
	
	@Override
	public List<Element> getList(){
		return elementsList;
	}
	
	@Override
	public Result<Object> getObject(String descriptor, PersistentObject dependentObject,
		String dates, String[] params){
		Result<Object> ret = null;
		
		if (descriptor.equals("Name")) { //$NON-NLS-1$
			if (selectedArticel != null)
				ret = new Result<Object>(selectedArticel.get(Artikel.FLD_NAME));
			else
				ret =
					new Result<Object>(Result.SEVERITY.ERROR, IDataAccess.OBJECT_NOT_FOUND,
						"Kein Artikel selektiert.", //$NON-NLS-1$
						null, false);
		} else if (descriptor.equals("Preis")) { //$NON-NLS-1$
			if (selectedArticel != null)
				ret = new Result<Object>(selectedArticel.getVKPreis().toString());
			else
				ret =
					new Result<Object>(Result.SEVERITY.ERROR, IDataAccess.OBJECT_NOT_FOUND,
						"Kein Artikel selektiert.", //$NON-NLS-1$
						null, false);
		} else if (descriptor.equals("OPGroesse")) { //$NON-NLS-1$
			if (selectedArticel != null)
				ret = new Result<Object>(selectedArticel.getPackungsGroesseDesc());
			else
				ret =
					new Result<Object>(Result.SEVERITY.ERROR, IDataAccess.OBJECT_NOT_FOUND,
						"Kein Artikel selektiert.", //$NON-NLS-1$
						null, false);
		} else if (descriptor.equals("EAN")) { //$NON-NLS-1$
			if (selectedArticel != null)
				ret = new Result<Object>(selectedArticel.getEAN());
			else
				ret =
					new Result<Object>(Result.SEVERITY.ERROR, IDataAccess.OBJECT_NOT_FOUND,
						"Kein Artikel selektiert.", //$NON-NLS-1$
						null, false);
		} else if (descriptor.equals("Pharmacode")) { //$NON-NLS-1$
			if (selectedArticel != null)
				ret = new Result<Object>(selectedArticel.getPharmaCode());
			else
				ret =
					new Result<Object>(Result.SEVERITY.ERROR, IDataAccess.OBJECT_NOT_FOUND,
						"Kein Artikel selektiert.", //$NON-NLS-1$
						null, false);
		} else if (descriptor.equals("Dosis")) { //$NON-NLS-1$
			if (selectedPrescription != null)
				ret = new Result<Object>(selectedPrescription.getDosis());
			else
				ret = new Result<Object>(""); //$NON-NLS-1$
		} else if (descriptor.equals("Vorschrift")) { //$NON-NLS-1$
			if (selectedPrescription != null)
				ret = new Result<Object>(selectedPrescription.getBemerkung());
			else
				ret = new Result<Object>(""); //$NON-NLS-1$
		}
		return ret;
		
	}
	
	public static void setSelectedArticel(Artikel articel){
		selectedArticel = articel;
	}
	
	public static void setSelectedPrescription(Prescription prescription){
		selectedPrescription = prescription;
	}
}
