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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.viewers.Viewer;

import ch.elexis.base.ch.labortarif_2009.ui.Labor2009CodeTextValidFilter.TarifDescription;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.labortarif2009.data.Labor2009Tarif;

public class Labor2009ContentProvider implements ICommonViewerContentProvider {
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private Object cacheLock = new Object();
	private boolean initialized;
	private HashMap<Labor2009Tarif, TarifDescription> cache;
	
	private void initCache(final List<Labor2009Tarif> input){
		executor.execute(new Runnable() {
			@Override
			public void run(){
				cache =
					new HashMap<Labor2009Tarif, Labor2009CodeTextValidFilter.TarifDescription>(
						input.size());
				for (Labor2009Tarif labor2009Tarif : input) {
					cache.put(labor2009Tarif, new TarifDescription(labor2009Tarif));
				}
				synchronized (cacheLock) {
					initialized = true;
					cacheLock.notifyAll();
				}
			}
		});
	}

	private List<Labor2009Tarif> elements;

	public Object[] getElements(Object inputElement){
		if (elements == null) {
			return Collections.emptyList().toArray();
		}
		return elements.toArray();
	}
	
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		if (newInput instanceof List<?>) {
			elements = (List<Labor2009Tarif>) newInput;
			initCache(elements);
		}
	}
	
	public void changed(HashMap<String, String> values){
		// TODO Auto-generated method stub
		
	}
	
	public void reorder(String field){
		// TODO Auto-generated method stub
		
	}
	
	public void selected(){
		// TODO Auto-generated method stub
		
	}
	
	public void init(){
		// TODO Auto-generated method stub
		
	}
	
	public void startListening(){
		// TODO Auto-generated method stub
		
	}
	
	public void stopListening(){
		// TODO Auto-generated method stub
		
	}
	
	public TarifDescription getDescription(Labor2009Tarif tarif){
		if (!initialized) {
			synchronized (cacheLock) {
				try {
					cacheLock.wait(5000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
		return cache.get(tarif);
	}
}
