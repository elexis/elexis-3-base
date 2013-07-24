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

package ch.elexis.base.ch.medikamente.bag.data;

import java.util.List;

import ch.elexis.core.data.PersistentObject;
import ch.elexis.core.data.Query;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

/**
 * InterView - Community based Interaction viewer - will be built on this class
 * 
 * @author Gerry
 * 
 */
public class Interaction extends PersistentObject implements Comparable<Interaction> {
	final static String TABLENAME = "CH_ELEXIS_MEDIKAMENTE_BAG_INTERACTIONS";
	final static String VERSION = "0.2.1";
	static final String createDB = "CREATE TABLE " + TABLENAME + " ("
		+ "ID			VARCHAR(25)	primary key," + "deleted		CHAR(1) default '0',"
		+ "Subst1		VARCHAR(25)," + "Subst2		VARCHAR(25),"
		+ "Type			VARCHAR(20),"
		+ "Relevance			CHAR(1),"
		+ "Contributor	VARCHAR(25)," // UID of the contributor
		+ "ContribDate		CHAR(8)," + "Description		TEXT);" + "INSERT INTO " + TABLENAME
		+ "(ID,Type) VALUES ('VERSION','" + VERSION + "');" + "CREATE INDEX CEMBI1 ON " + TABLENAME
		+ " (Subst1);" + "CREATE INDEX CEMBI2 ON " + TABLENAME + " (Subst2);";
	
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_PLUS = 1;
	public static final int TYPE_MINUS = 2;
	public static final int TYPE_TOXIC = 3;
	public static final int TYPE_HWZ = 4;
	
	public static final int RELEVANCE_UNKNOWN = 0;
	public static final int RELEVANCE_LOW = 1;
	public static final int RELEVANCE_MEDIUM = 2;
	public static final int RELEVANCE_HIGH = 3;
	
	public static final String[] INTERAKTIONSTYPEN = {
		"unbekannt", "Wirkungsverstärkung", "Wirkungsabschwächung", "Toxizität", "Halbwertszeit"
	};
	public static final String[] RELEVANCE = {
		"unbekannt", "gering", "mässig", "hoch"
	};
	
	static {
		addMapping(TABLENAME, "Subst1", "Subst2", "Type", "Relevance", "Description",
			"Contributor", "ContribDate=S:D:ContribDate");
		Interaction v = load("VERSION");
		if (v.state() < PersistentObject.DELETED) {
			createOrModifyTable(createDB);
		} else {
			VersionInfo vi = new VersionInfo(v.get("Type"));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("0.2.1")) {
					final String update =
						"ALTER TABLE " + TABLENAME + " ADD Contributor VARCHAR(25); "
							+ "ALTER TABLE " + TABLENAME + " ADD ContribDate CHAR(8);";
					createOrModifyTable(update);
					v.set("Type", VERSION);
				} else {
					SWTHelper.showError("Datenbank Fehler", "Tabelle Interactions ist zu alt");
				}
			}
		}
	}
	
	public Interaction(final Substance s1, final Substance s2, final String desc, final int t,
		final int sev){
		create(null);
		set(new String[] {
			"Subst1", "Subst2", "Description", "Type", "Relevance", "Contributor", "ContribDate"
		}, s1.getId(), s2.getId(), desc, Integer.toString(t), Integer.toString(sev),
			CoreHub.actMandant.getId(), new TimeTool().toString(TimeTool.DATE_GER));
	}
	
	public static List<Interaction> getInteractionsFor(final Substance s){
		Query<Interaction> qbe = new Query<Interaction>(Interaction.class);
		qbe.add("Subst1", "=", s.getId());
		qbe.or();
		qbe.add("Subst2", "=", s.getId());
		return qbe.execute();
	}
	
	public Substance[] getSubstances(){
		Substance[] ret = new Substance[2];
		ret[0] = Substance.load(get("Subst1"));
		ret[1] = Substance.load(get("Subst2"));
		return ret;
	}
	
	public String getDescription(){
		String description = get("Description");
		return StringTool.isNothing(description) ? "-" : description;
	}
	
	public void setDescription(final String desc){
		set("Description", desc);
	}
	
	public int getType(){
		return checkZero(get("Type"));
	}
	
	public void setType(final int typ){
		set("Type", Integer.toString(typ));
	}
	
	public int getRelevance(){
		return checkZero(get("Relevance"));
	}
	
	public void setRelevance(final int r){
		set("Relevance", Integer.toString(r));
	}
	
	@Override
	public String getLabel(){
		Substance[] s = getSubstances();
		StringBuilder sb = new StringBuilder();
		String first = s[0].getLabel();
		String second = s[1].getLabel();
		if (first.compareTo(second) > 0) {
			sb.append(second).append(" <-> ").append(first);
		} else {
			sb.append(first).append(" <-> ").append(second);
		}
		int type = getType();
		if (type > 0) {
			sb.append(": ").append(INTERAKTIONSTYPEN[getType()]).append(" (Relevanz: ")
				.append(RELEVANCE[getRelevance()]).append(")");
		}
		return sb.toString();
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static Interaction load(final String id){
		return new Interaction(id);
	}
	
	protected Interaction(final String id){
		super(id);
	}
	
	protected Interaction(){}
	
	public int compareTo(final Interaction o){
		return getLabel().compareTo(o.getLabel());
	}
	
}
