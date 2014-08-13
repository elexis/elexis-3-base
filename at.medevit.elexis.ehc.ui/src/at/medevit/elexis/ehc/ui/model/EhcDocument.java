/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.service.ServiceComponent;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;
import ehealthconnector.cda.documents.ch.CdaCh;

public class EhcDocument extends PersistentObject {
	private static Logger logger = LoggerFactory.getLogger(EhcDocument.class);
	
	public static final String TABLENAME = "at_medevit_elexis_ehc_document"; //$NON-NLS-1$
	public static final String VERSION = "1.0.0"; //$NON-NLS-1$
	
	public static final String VERSIONID = "VERSION"; //$NON-NLS-1$
	
	public static final String FLD_NAME = "name"; //$NON-NLS-1$
	public static final String FLD_TIMESTAMP = "timestamp"; //$NON-NLS-1$
	public static final String FLD_PATIENT = "patient"; //$NON-NLS-1$
	public static final String FLD_LOCATION = "location"; //$NON-NLS-1$
	
	// @formatter:off
	static final String create = 
			"CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$
			"ID VARCHAR(25) primary key, " + //$NON-NLS-1$
			"lastupdate BIGINT," +
			"deleted CHAR(1) default '0'," + //$NON-NLS-1$
			
			"name VARCHAR(255)," + //$NON-NLS-1$
			"timestamp VARCHAR(16)," + //$NON-NLS-1$
			"patient VARCHAR(128)," + //$NON-NLS-1$
			"location VARCHAR(255)" + //$NON-NLS-1$
			
			");" + //$NON-NLS-1$
			"CREATE INDEX ehcdoc1 ON " + TABLENAME + " (" + FLD_PATIENT + ");" + //$NON-NLS-1$
			"INSERT INTO " + TABLENAME + " (ID," + FLD_PATIENT + ") VALUES (" + JdbcLink.wrap(VERSIONID) + "," + JdbcLink.wrap(VERSION) + ");"; //$NON-NLS-1$
	// @formatter:on
	
	static {
		addMapping(TABLENAME, FLD_NAME, FLD_TIMESTAMP, FLD_PATIENT, FLD_LOCATION);
		
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(create);
		} else {
			EhcDocument version = load(VERSIONID);
			VersionInfo vi = new VersionInfo(version.get(FLD_PATIENT));
			if (vi.isOlder(VERSION)) {
				// we should update eg. with createOrModifyTable(update.sql);
				// And then set the new version
				version.set(FLD_PATIENT, VERSION);
			}
		}
	}
	
	public EhcDocument(){
		// TODO Auto-generated constructor stub
	}
	
	public EhcDocument(String id){
		super(id);
	}
	
	public static EhcDocument load(final String id){
		return new EhcDocument(id);
	}
	
	public EhcDocument(String name, URL location, TimeTool creation){
		create(null);
		Patient patient = getPatientFromDocument(location);
		String[] fields = {
			FLD_NAME, FLD_TIMESTAMP, FLD_PATIENT, FLD_LOCATION
		};
		String[] vals =
			new String[] {
				name, creation.toString(TimeTool.TIMESTAMP),
				patient != null ? patient.getId() : "", location.toString()
			};
		set(fields, vals);
	}
	
	private Patient getPatientFromDocument(URL location){
		try {
			CdaCh document = ServiceComponent.getEhcService().getDocument(location.openStream());
			ehealthconnector.cda.documents.ch.Patient patient = document.cGetPatient();
		} catch (IOException e) {
			logger.error("Could not open location.", e);
		}
		return null;
	}
	
	public String getName(){
		return get(FLD_NAME);
	}
	
	public void setName(String name){
		set(FLD_NAME, name);
	}
	
	public URL getLocation(){
		try {
			return new URL(get(FLD_LOCATION));
		} catch (MalformedURLException e) {
			logger.error("Could create URL for location.", e);
		}
		return null;
	}
	
	public void setLocation(URL location){
		set(FLD_LOCATION, location.toString());
	}
	
	public Patient getPatient(){
		String id = get(FLD_PATIENT);
		Patient patient = Patient.load(id);
		if (patient.exists()) {
			return patient;
		}
		return null;
	}
	
	public void setPatient(Patient patient){
		set(FLD_PATIENT, patient.getId());
	}
	
	public static boolean documentExists(URL url){
		Query<EhcDocument> qd = new Query<EhcDocument>(EhcDocument.class);
		qd.add(FLD_LOCATION, Query.EQUALS, url.toString());
		List<EhcDocument> existing = qd.execute();
		return !existing.isEmpty();
	}
	
	@Override
	public String getLabel(){
		return getName();
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
}
