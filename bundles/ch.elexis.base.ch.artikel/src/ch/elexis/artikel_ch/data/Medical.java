/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.artikel_ch.data;

import java.util.Map;

import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.ui.optifier.NoObligationOptifier;
import ch.elexis.data.Artikel;

public class Medical extends Artikel {
	
	public static final String CODESYSTEM_NAME = "Medicals";
	
	private static IOptifier noObligationOptifier = new NoObligationOptifier();
	
	@Override
	protected String getConstraint(){
		return "Typ='Medical'"; //$NON-NLS-1$
	}
	
	protected void setConstraint(){
		set("Typ", "Medical"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public String getCodeSystemName(){
		return CODESYSTEM_NAME; //$NON-NLS-1$
	}
	
	@Override
	public String getCodeSystemCode(){
		return "400";
	}
	
	@Override
	public String getCode(){
		return getPharmaCode();
	}
	
	public static Medical load(String id){
		return new Medical(id);
	}
	
	protected Medical(String id){
		super(id);
	}
	
	protected Medical(){}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
	
	public String getLabel(){
		return get("Name"); //$NON-NLS-1$
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public VatInfo getVatInfo(){
		// MWST Info is put into ext info by MedikamentImporter
		// Code Mehrwertsteuer (CMWS) - 1stellig
		// 1: voller MWSt-Satz (zur Zeit 6.5%)
		// 2: reduzierter MWSt-Satz (zur Zeit 2%)
		// 3: von der MWSt befreit
		Map info = getMap(Artikel.FLD_EXTINFO);
		String typ = (String) info.get(MedikamentImporter.MWST_TYP);
		if (typ != null && typ.equals("2"))
			return VatInfo.VAT_CH_ISMEDICAMENT;
		else if (typ != null && typ.equals("1"))
			return VatInfo.VAT_CH_NOTMEDICAMENT;
		else if (typ != null && typ.equals("3"))
			return VatInfo.VAT_NONE;
		return VatInfo.VAT_NONE;
		
	}
	
	@Override
	public IOptifier getOptifier(){
		return noObligationOptifier;
	}
}