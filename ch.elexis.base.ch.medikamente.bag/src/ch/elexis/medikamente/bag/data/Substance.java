/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.medikamente.bag.data;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.VersionInfo;

public class Substance extends PersistentObject {
	public static final String TABLENAME = "CH_ELEXIS_MEDIKAMENTE_BAG_SUBSTANCE";
	static final String VERSION = "0.3.0";
	static final String createDB = "CREATE TABLE " + TABLENAME + "("
		+ "ID		VARCHAR(25) primary key,"
		+ "lastupdate	BIGINT,"
		+ "deleted	CHAR(1) default '0',"
		+ "gruppe	VARCHAR(10)," // therap. gruppe
		+ "name		VARCHAR(254)" + ");" + "CREATE INDEX CEMBS1 ON " + TABLENAME + " (gruppe);"
		+ "CREATE INDEX CEMBS2 ON " + TABLENAME + " (name);" + "INSERT INTO " + TABLENAME
		+ " (ID,name) VALUES ('VERSION','" + VERSION + "');";
	
	private static final String UPD020 = "ALTER TABLE " + TABLENAME
		+ " MODIFY gruppe VARCHAR(10); ALTER TABLE " + TABLENAME + " MODIFY name VARCHAR(250);";
	
	private static final String UPD030 = "ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;";
	static {
		addMapping(TABLENAME, "name", "gruppe", "medis=JOINT:product:substance:"
			+ BAGMedi.JOINTTABLE, "interactions=JOINT:Subst1:Subst2:" + Interaction.TABLENAME);
		Substance v = load("VERSION");
		if (v.state() < PersistentObject.DELETED) {
			createOrModifyTable(createDB);
		} else {
			VersionInfo vi = new VersionInfo(v.get("name"));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("0.1.0")) {
					SWTHelper.showError("Datenbank Fehler", "Tabelle Substance ist zu alt");
				}
				if (vi.isOlder("0.2.0")) {
					createOrModifyTable(UPD020);
				}
				if (vi.isOlder("0.3.0")) {
					createOrModifyTable(UPD030);
				}
				v.set("name", VERSION);
				
			}
		}
	}
	
	@Override
	public String getLabel(){
		return get("name");
	}
	
	public Substance(final String name, final String group){
		create(null);
		set(new String[] {
			"name", "gruppe"
		}, StringTool.limitLength(name, 250), group);
	}
	
	public SortedSet<BAGMedi> findMedis(SortedSet<BAGMedi> list){
		if (list == null) {
			list = new TreeSet<BAGMedi>();
		}
		List<String[]> lMedis = getList("medis", new String[0]);
		for (String[] r : lMedis) {
			BAGMedi bm = BAGMedi.load(r[0]);
			list.add(bm);
		}
		return list;
	}
	
	public List<Substance> sameGroup(){
		return allFromGroup(get("gruppe"));
	}
	
	public static Substance find(final String name){
		String id = new Query<Substance>(Substance.class).findSingle("name", "=", name);
		if (id != null) {
			return load(id);
		}
		return null;
	}
	
	public static List<Substance> allFromGroup(final String group){
		return new Query<Substance>(Substance.class, "gruppe", group).execute();
		
	}
	
	public List<Interaction> getInteractions(){
		return Interaction.getInteractionsFor(this);
	}
	
	public Collection<Interaction> getInteractionsWith(Substance other, SortedSet<Interaction> old){
		if (old == null) {
			old = new TreeSet<Interaction>();
		}
		Query<Interaction> qbe = new Query<Interaction>(Interaction.class);
		qbe.startGroup();
		qbe.add("Subst1", "=", getId());
		qbe.add("Subst2", "=", other.getId());
		qbe.endGroup();
		qbe.or();
		qbe.startGroup();
		qbe.add("Subst1", "=", other.getId());
		qbe.and();
		qbe.add("Subst2", "=", getId());
		qbe.endGroup();
		return qbe.execute(old);
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static Substance load(final String id){
		return new Substance(id);
	}
	
	protected Substance(){}
	
	protected Substance(final String id){
		super(id);
	}
	
	/*
	 * public static class Interaction{ Substance subst; int type; String description; int severity;
	 * 
	 * }
	 */
}
