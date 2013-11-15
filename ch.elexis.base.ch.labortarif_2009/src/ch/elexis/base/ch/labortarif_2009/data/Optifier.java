/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.base.ch.labortarif_2009.data;

import java.util.List;

import ch.elexis.base.ch.labortarif_2009.ui.Preferences;
import ch.elexis.core.data.Konsultation;
import ch.elexis.core.data.Query;
import ch.elexis.core.data.Verrechnet;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;
import ch.rgw.tools.TimeTool;

public class Optifier implements IOptifier {
	/**
	 * Add and recalculate the various possible amendments
	 */
	public Result<IVerrechenbar> add(IVerrechenbar code, Konsultation kons){
		boolean bOptify =
			CoreHub.userCfg.get(ch.elexis.core.constants.Preferences.LEISTUNGSCODES_OPTIFY, true);
		if (code instanceof Labor2009Tarif) {
			// Gültigkeit gemäss Datum prüfen
			if (bOptify) {
				TimeTool date = new TimeTool(kons.getDatum());
				Labor2009Tarif tarif = ((Labor2009Tarif) code);
				if (!tarif.isValidOn(date)) {
					TimeTool validFrom = new TimeTool(tarif.get(Labor2009Tarif.FLD_GUELTIG_VON));
					TimeTool validTo = new TimeTool(tarif.get(Labor2009Tarif.FLD_GUELTIG_BIS));
					return new Result<IVerrechenbar>(Result.SEVERITY.ERROR, 2,
						code.getCode() + " (" + validFrom.toString(TimeTool.DATE_GER) + "-"
							+ validTo.toString(TimeTool.DATE_GER)
							+ ") Gültigkeit beinhaltet nicht das Konsultationsdatum "
							+ kons.getDatum(), null, false);
				}
			}
			
			new Verrechnet(code, kons, 1);
			Result<Object> res = optify(kons);
			if (res.isOK()) {
				return new Result<IVerrechenbar>(code);
			} else {
				return new Result<IVerrechenbar>(res.getSeverity(), res.getCode(), res.toString(),
					code, true);
			}
		}
		return new Result<IVerrechenbar>(SEVERITY.ERROR, 2, "No Lab2009Tariff", null, true); //$NON-NLS-1$
	}
	
	public Result<Object> optify(Konsultation kons){
		if (CoreHub.localCfg.get(Preferences.OPTIMIZE, true) == false) {
			return new Result<Object>(kons);
		}
		try {
			boolean haveKons = false;
			TimeTool date = new TimeTool(kons.getDatum());
			TimeTool deadline = CoreHub.globalCfg.getDate(Preferences.OPTIMIZE_ADDITION_DEADLINE);
			if (deadline == null)
				deadline = new TimeTool(Preferences.OPTIMIZE_ADDITION_INITDEADLINE);
			
			if (date.isBefore(new TimeTool("01.07.2009"))) { //$NON-NLS-1$
				return new Result<Object>(SEVERITY.WARNING, 3, "Code not yet valid", null, false); //$NON-NLS-1$
			}
			
			List<Verrechnet> list = kons.getLeistungen();
			Verrechnet v470710 = null;
			Verrechnet v470720 = null;
			Verrechnet v4708 = null;
			int z4708 = 0;
			int z4707 = 0;
			int z470710 = 0;
			int z470720 = 0;
			
			for (Verrechnet v : list) {
				IVerrechenbar iv = v.getVerrechenbar();
				if (iv instanceof Labor2009Tarif) {
					String cc = v.getVerrechenbar().getCode();
					if (cc.equals("4708.00")) { // Übergangszuschlag //$NON-NLS-1$
						v4708 = v;
					} else if (cc.equals("4707.00")) { // Pauschale //$NON-NLS-1$
						if (z4707 < 1) {
							z4707 = 1;
						} else {
							return new Result<Object>(SEVERITY.WARNING, 1,
								"4707.00 only once per cons", v, false); //$NON-NLS-1$
						}
					} else if (cc.equals("4707.10")) { // Fachbereich C //$NON-NLS-1$
						v470710 = v;
					} else if (cc.equals("4707.20")) { // Fachbereich //$NON-NLS-1$
						// nicht-C
						v470720 = v;
					} else if (cc.equals("4703.00") || cc.equals("4701.00") || cc.equals("4704.00") || cc.equals("4706.00")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						continue;
					} else {
						Labor2009Tarif vlt = (Labor2009Tarif) iv;
						if (vlt.get(Labor2009Tarif.FLD_FACHBEREICH).indexOf("C") > -1) { //$NON-NLS-1$
							z470710 += v.getZahl();
						} else {
							z470720 += v.getZahl();
						}
						z4708 += v.getZahl();
					}
				} else if (iv.getCode().equals("00.0010") || iv.getCode().equals("00.0060")) { // Kons erste 5 Minuten //$NON-NLS-1$ //$NON-NLS-2$
					haveKons = true;
				}
			}
			// reduce amendments to max. 24 TP
			while (((4 + 2 * z470710 + z470720) > 26) && z470710 > 0) {
				z470710--;
			}
			while (((4 + 2 * z470710 + z470720) > 24) && z470720 > 0) {
				z470720--;
			}
			
			if (z470710 == 0 || haveKons == false) {
				if (v470710 != null) {
					v470710.delete();
				}
			} else {
				if (v470710 == null) {
					v470710 = doCreate(kons, "4707.10"); //$NON-NLS-1$
				}
				v470710.setZahl(z470710);
			}
			
			if (z470720 == 0 || haveKons == false) {
				if (v470720 != null) {
					v470720.delete();
				}
			} else {
				if (v470720 == null) {
					v470720 = doCreate(kons, "4707.20"); //$NON-NLS-1$
				}
				v470720.setZahl(z470720);
			}
			
			if (z4707 == 0 && ((z470710 + z470720) > 0) && haveKons == true) {
				doCreate(kons, "4707.00"); //$NON-NLS-1$
			}
			if (z4708 > 0 && haveKons == true) {
				if (v4708 == null) {
					if (date.isBefore(deadline)) {
						v4708 = doCreate(kons, "4708.00"); //$NON-NLS-1$
					}
				} else {
					if (date.isAfterOrEqual(deadline)) {
						v4708.delete();
						return new Result<Object>(
							SEVERITY.WARNING,
							2,
							"4708.00 only until " + deadline.toString(TimeTool.DATE_GER), null, false); //$NON-NLS-1$
					}
				}
			}
			if (v4708 != null) {
				v4708.setZahl(z4708);
			}
			return new Result<Object>(kons);
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return new Result<Object>(SEVERITY.ERROR, 1, "Tariff not installed correctly", null, //$NON-NLS-1$
				true);
			
		}
		
	}
	
	public Result<Verrechnet> remove(Verrechnet code, Konsultation kons){
		List<Verrechnet> l = kons.getLeistungen();
		l.remove(code);
		code.delete();
		Result<Object> res = optify(kons);
		if (res.isOK()) {
			return new Result<Verrechnet>(code);
		} else {
			return new Result<Verrechnet>(res.getSeverity(), res.getCode(), res.toString(), code,
				true);
		}
	}
	
	private Verrechnet doCreate(Konsultation kons, String code) throws Exception{
		Query<Labor2009Tarif> query = new Query<Labor2009Tarif>(Labor2009Tarif.class);
		query.add(Labor2009Tarif.FLD_CODE, Query.EQUALS, code);
		List<Labor2009Tarif> list = query.execute();
		Labor2009Tarif tarif = null;
		for (Labor2009Tarif labor2009Tarif : list) {
			if (labor2009Tarif.isValidOn(new TimeTool(kons.getDatum()))) {
				tarif = labor2009Tarif;
				break;
			}
		}
		
		if (tarif != null) {
			return new Verrechnet(tarif, kons, 1);
		} else {
			throw new Exception("Tariff not installed correctly"); //$NON-NLS-1$
		}
		
	}
	
}
