/*******************************************************************************
 * Copyright (c) 2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    
 *******************************************************************************/

package com.hilotec.elexis.pluginstatistiken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.hilotec.elexis.pluginstatistiken.config.Konfiguration;
import com.hilotec.elexis.pluginstatistiken.config.KonfigurationQuery;

import ch.rgw.tools.Log;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;

/**
 * Dataprovider, der Archie mit den gewuenschten Daten versorgt.
 * 
 * @author Antoine Kaufmann
 */
public class PluginDataProvider extends AbstractTimeSeries {
	Konfiguration konfig;
	KonfigurationQuery query = null;
	String currentQuery;
	String queries[];
	private static final String DATE_PRESCRIPTION_FORMAT = "dd.MM.yyyy";
	
	public PluginDataProvider(){
		super("Pluginstatistiken");
		konfig = Konfiguration.getInstance();
		
		List<KonfigurationQuery> ql = konfig.getQueries();
		queries = new String[ql.size()];
		int i = 0;
		for (KonfigurationQuery kq : ql) {
			queries[i++] = kq.getTitle();
		}
	}
	
	/**
	 * Eigentliche Daten fuer die Statistik zusammenstellen
	 */
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		if (query == null) {
			return Status.CANCEL_STATUS;
		}
		
		final List<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>();
		
		final SimpleDateFormat gerFormat = new SimpleDateFormat(DATE_PRESCRIPTION_FORMAT);
		List<Datensatz> data;
		try {
			data =
				query.getDaten(gerFormat.format(this.getStartDate().getTime()),
					gerFormat.format(this.getEndDate().getTime()), monitor);
		} catch (PluginstatistikException e) {
			Log.get("Messwertstatistiken").log(e.getMessage(), Log.ERRORS);
			return Status.CANCEL_STATUS;
		}
		List<String> names = query.getColNames();
		
		for (Datensatz ds : data) {
			Comparable<?>[] row = new Comparable<?>[names.size()];
			for (int i = 0; i < names.size(); i++) {
				row[i] = ds.getFeld(names.get(i));
			}
			content.add(row);
		}
		
		this.dataSet.setContent(content);
		
		monitor.done();
		return Status.OK_STATUS;
	}
	
	/**
	 * Spaltenueberschriften zusammensuchen
	 */
	@Override
	protected List<String> createHeadings(){
		return query.getColNames();
	}
	
	@Override
	public String getDescription(){
		return "Pluginstatistiken";
	}
	
	/**
	 * Combo-Feld um die zu benutzende Abfrage auszuwaehlen
	 */
	@GetProperty(name = "Abfrage", index = 0, description = "Zu benutzende Abfrage um die Daten zu sammeln", widgetType = WidgetTypes.VENDOR, vendorClass = OurComboWidget.class)
	public String getAbfrage(){
		if (query == null) {
			return queries[0];
		}
		return query.getTitle();
	}
	
	@SetProperty(name = "Abfrage")
	public void setAbfrage(final String q){
		List<KonfigurationQuery> queries = konfig.getQueries();
		for (int i = 0; i < queries.size(); i++) {
			if (queries.get(i).getTitle().equals(q)) {
				query = queries.get(i);
				break;
			}
		}
	}
}
