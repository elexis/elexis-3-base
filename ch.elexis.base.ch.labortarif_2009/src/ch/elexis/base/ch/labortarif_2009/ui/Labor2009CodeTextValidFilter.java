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
package ch.elexis.base.ch.labortarif_2009.ui;

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.elexis.data.Query;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.rgw.tools.TimeTool;

public class Labor2009CodeTextValidFilter extends ViewerFilter {
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private String searchString;
	private TimeTool validDate;
	
	private static final int MAX_LOOKUP = 500;
	private TimeTool compareTime = new TimeTool();
	private Object cacheLock;
	private WeakHashMap<Labor2009Tarif, TarifDescription> cache =
		new WeakHashMap<Labor2009Tarif, TarifDescription>(1000);
	
	private class TarifDescription {
		private String validFromString;
		private String validToString;
		private String code;
		private String text;
		
		public TarifDescription(Labor2009Tarif tarif){
			String[] values =
				tarif.get(true, Labor2009Tarif.FLD_GUELTIG_VON, Labor2009Tarif.FLD_GUELTIG_BIS,
					Labor2009Tarif.FLD_CODE, Labor2009Tarif.FLD_NAME);
			validFromString = values[0];
			validToString = values[1];
			code = values[2].toLowerCase();
			text = values[3].toLowerCase();
		}
		
		public boolean isValidOn(TimeTool date){
			if (validFromString != null && validFromString.trim().length() > 0) {
				compareTime.set(validFromString);
				if (compareTime.after(date))
					return false;
			}
			if (validToString != null && validToString.trim().length() > 0) {
				compareTime.set(validToString);
				if (compareTime.before(date) || compareTime.equals(date))
					return false;
			}
			return true;
		}
	}
	
	private void initCache(){
		cacheLock = new Object();
		executor.execute(new Runnable() {
			@Override
			public void run(){
				Query<Labor2009Tarif> qt = new Query<Labor2009Tarif>(Labor2009Tarif.class);
				List<Labor2009Tarif> tarifList = qt.execute();
				for (Labor2009Tarif labor2009Tarif : tarifList) {
					cache.put(labor2009Tarif, new TarifDescription(labor2009Tarif));
					if (cacheLock != null) {
						synchronized (cacheLock) {
							cacheLock.notifyAll();
						}
					}
				}
			}
		});
		// wait until init started
		synchronized (cacheLock) {
			try {
				cacheLock.wait();
				cacheLock = null;
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	public void setSearchText(String s){
		if (s == null || s.length() == 0) {
			searchString = s;
		} else {
			searchString = s.toLowerCase();
		}
	}
	
	/**
	 * Show only positions valid at date. Set date to null to show all.
	 * 
	 * @param date
	 */
	public void setValidDate(TimeTool date){
		validDate = date;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element){
		// lookup cache
		Labor2009Tarif tarif = (Labor2009Tarif) element;
		int lookup = MAX_LOOKUP;
		TarifDescription description = null;
		
		if (cache.isEmpty()) {
			initCache();
		}

		description = cache.get(tarif);

		while (description == null) {
			try {
				Thread.sleep(10);
				description = cache.get(tarif);
				if (--lookup == 0) {
					break;
				}
			} catch (InterruptedException e) {
				break;
			}
		}
		
		if (description != null) {
			if (validDate != null) {
				if (!description.isValidOn(validDate))
					return false;
			}
			
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			
			if (description.code != null && description.code.contains(searchString)) {
				return true;
			}
			
			if (description.text != null && description.text.contains(searchString)) {
				return true;
			}
		}
		return false;
	}
}
