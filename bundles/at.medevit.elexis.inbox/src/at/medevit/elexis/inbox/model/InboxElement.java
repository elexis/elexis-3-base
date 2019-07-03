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
package at.medevit.elexis.inbox.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import at.medevit.elexis.inbox.model.IInboxElementService.State;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.Identifiable;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.VersionInfo;

public class InboxElement extends PersistentObject {
	public static final String TABLENAME = "at_medevit_elexis_inbox"; //$NON-NLS-1$
	public static final String VERSION = "1.0.0"; //$NON-NLS-1$
	
	public static final String VERSIONID = "VERSION"; //$NON-NLS-1$
	
	public static final String FLD_PATIENT = "patient"; //$NON-NLS-1$
	public static final String FLD_MANDANT = "mandant"; //$NON-NLS-1$
	public static final String FLD_STATE = "state"; //$NON-NLS-1$
	public static final String FLD_OBJECT = "object"; //$NON-NLS-1$
	
	public static String getStateLabel(State state){
		switch (state) {
		case NEW:
			return "Neu";
		case SEEN:
			return "Gesehen";
		default:
			return "???";
		}
	}
	
	// @formatter:off
	static final String create = 
			"CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$
			"ID VARCHAR(25) primary key, " + //$NON-NLS-1$
			"lastupdate BIGINT," +
			"deleted CHAR(1) default '0'," + //$NON-NLS-1$
			
			"patient VARCHAR(128)," + //$NON-NLS-1$
			"mandant VARCHAR(128)," + //$NON-NLS-1$
			"state VARCHAR(1)," + //$NON-NLS-1$
			"object VARCHAR(128)" + //$NON-NLS-1$
			
			");" + //$NON-NLS-1$
			"CREATE INDEX inbox1 ON " + TABLENAME + " (" + FLD_PATIENT + ");" + //$NON-NLS-1$
			"CREATE INDEX inbox2 ON " + TABLENAME + " (" + FLD_MANDANT + ");" + //$NON-NLS-1$
			"INSERT INTO " + TABLENAME + " (ID," + FLD_PATIENT + ") VALUES (" + JdbcLink.wrap(VERSIONID) + "," + JdbcLink.wrap(VERSION) + ");"; //$NON-NLS-1$
	// @formatter:on
	
	static {
		addMapping(TABLENAME, FLD_PATIENT, FLD_MANDANT, FLD_STATE, FLD_OBJECT);
		
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(create);
		} else {
			InboxElement version = load(VERSIONID);
			VersionInfo vi = new VersionInfo(version.get(FLD_PATIENT));
			if (vi.isOlder(VERSION)) {
				// we should update eg. with createOrModifyTable(update.sql);
				// And then set the new version
				version.set(FLD_PATIENT, VERSION);
			}
		}
	}
	
	public InboxElement(String id){
		super(id);
	}
	
	public InboxElement(Patient patient, Kontakt mandant, PersistentObject object){
		this(patient, mandant, object.storeToString());
	}
	
	public InboxElement(Patient patient, Kontakt mandant, String element){
		create(null);
		set(FLD_PATIENT, patient != null ? patient.getId() : "");
		set(FLD_MANDANT, mandant.getId());
		set(FLD_OBJECT, element);
		set(FLD_STATE, Integer.toString(State.NEW.ordinal()));
	}
	
	public InboxElement(IPatient patient, IMandator mandant, Identifiable object){
		create(null);
		set(FLD_PATIENT, patient != null ? patient.getId() : "");
		set(FLD_MANDANT, mandant.getId());
		set(FLD_OBJECT, StoreToStringServiceHolder.getStoreToString(object));
		set(FLD_STATE, Integer.toString(State.NEW.ordinal()));
	}
	
	public InboxElement(){
		// TODO Auto-generated constructor stub
	}
	
	public static InboxElement load(final String id){
		return new InboxElement(id);
	}
	
	@Override
	public String getLabel(){
		Object element = getObject();
		if (element instanceof PersistentObject) {
			return ((PersistentObject) element).getLabel();
		} else if (element instanceof Path) {
			return ((Path) element).getFileName().toString();
		}
		return "InboxElement " + this.getId() + " with no object.";
	}
	
	/**
	 * Loads the PersistentObject and returns it, if it exists in the db. Else null is returned.
	 * 
	 * @return
	 */
	public Object getObject(){
		String uri = get(FLD_OBJECT);
		InboxElementType inboxElementType = InboxElementType.parseType(uri);
		
		if (inboxElementType != null) {
			switch (inboxElementType) {
			case DB:
				PersistentObject object = CoreHub.poFactory.createFromString(uri);
				if (object != null && object.exists()) {
					return object;
				}
				Identifiable identifiable =
					StoreToStringServiceHolder.get().loadFromString(uri).orElse(null);
				if (identifiable != null) {
					return identifiable;
				}
				break;
			case FILE:
				String refFile = uri.substring(InboxElementType.FILE.getPrefix().length());
				Path p = Paths.get(refFile);
				return p;
			default:
				break;
			}
		}
		return null;
	}
	

	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public State getState(){
		String stateStr = checkNull(get(FLD_STATE));
		if (stateStr.isEmpty()) {
			return State.NEW;
		} else {
			return State.values()[Integer.parseInt(stateStr.trim())];
		}
	}
	
	public void setState(State state){
		set(FLD_STATE, Integer.toString(state.ordinal()));
	}
	
	public Patient getPatient(){
		Patient ret = Patient.load(checkNull(get(FLD_PATIENT)));
		if (ret.exists()) {
			return ret;
		}
		return null;
	}
	
	public Mandant getMandant(){
		Mandant ret = Mandant.load(checkNull(get(FLD_MANDANT)));
		if (ret.exists()) {
			return ret;
		}
		return null;
	}
	
	public void setMandant(Mandant mandant){
		set(FLD_MANDANT, mandant.getId());
	}
}
