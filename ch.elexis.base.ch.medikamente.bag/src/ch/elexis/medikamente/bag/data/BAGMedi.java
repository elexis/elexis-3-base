/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.medikamente.bag.data;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Artikel;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Organisation;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.VersionInfo;

/**
 * This Article is a medicament taken from the BAG (Swiss federal dep. of health)
 * 
 * @author Gerry
 * 
 */
public class BAGMedi extends Artikel implements Comparable<BAGMedi> {
	public static final String EXTTABLE = "CH_ELEXIS_MEDIKAMENTE_BAG_EXT";
	public static final String JOINTTABLE = "CH_ELEXIS_MEDIKAMENTE_BAG_JOINT";
	static final String VERSION = "0.1.2";
	public static final String IMG_GENERIKUM = "ch.elexis.medikamente.bag.generikum";
	public static final String IMG_HAS_GENERIKA = "ch.elexis.medikamente.bag.has_generika";
	public static final String IMG_ORIGINAL = "ch.elexis.medikamente.bag.original";
	static final IOptifier bagOptifier = new BAGOptifier();
	
	static final String extDB = "CREATE TABLE " + EXTTABLE + " ("
		+ "ID				VARCHAR(25) primary key," + "lastupdate BIGINT,"
		+ "deleted			CHAR(1) default '0'," + "keywords			VARCHAR(80)," + "prescription		TEXT,"
		+ "KompendiumText	TEXT" + ");";
	
	static final String jointDB = "CREATE TABLE " + JOINTTABLE + "("
		+ "ID				VARCHAR(25) primary key," + "product			VARCHAR(25),"
		+ "substance         VARCHAR(25)" + ");" + "CREATE INDEX CHEMBJ1 ON " + JOINTTABLE
		+ " (product);" + "CREATE INDEX CHEMBJ2 ON " + JOINTTABLE + " (substance);"
		+ "INSERT INTO " + JOINTTABLE + " (ID,substance) VALUES('VERSION','" + VERSION + "');";
	
	public static final String CODESYSTEMNAME = "Medikament";
	public static final String DOMAIN_PHARMACODE = "www.xid.ch/id/pk";
	
	static {
		addMapping(Artikel.TABLENAME, "Gruppe=ExtId", "Generikum=Codeclass",
			"inhalt=JOINT:substance:product:" + JOINTTABLE, "keywords=EXT:" + EXTTABLE
				+ ":keywords", "prescription=EXT:" + EXTTABLE + ":prescription",
			"KompendiumText=EXT:" + EXTTABLE + ":KompendiumText");
		
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_PHARMACODE, "Pharmacode",
			Xid.ASSIGNMENT_REGIONAL);
		
		if (!tableExists(JOINTTABLE)) {
			createOrModifyTable(jointDB);
			createOrModifyTable(extDB);
		} else {
			String v =
				getConnection().queryString(
					"SELECT substance FROM " + JOINTTABLE + " WHERE ID='VERSION';");
			VersionInfo vi = new VersionInfo(v);
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("0.1.1")) {
					createOrModifyTable(extDB);
				}
				if (vi.isOlder("0.1.2")) {
					createOrModifyTable("ALTER TABLE " + EXTTABLE + " add lastupdate BIGINT;");
				}
				getConnection()
					.exec(
						"UPDATE " + JOINTTABLE + " SET substance='" + VERSION
							+ "' WHERE ID='VERSION';");
			}
		}
		// make sure, the substances table is created
		Substance.load("VERSION");
		String imgroot = "icons" + File.separator;
		UiDesk.getImageRegistry().put(IMG_GENERIKUM,
			BAGMediFactory.loadImageDescriptor(imgroot + "ggruen.png"));
		UiDesk.getImageRegistry().put(IMG_HAS_GENERIKA,
			BAGMediFactory.loadImageDescriptor(imgroot + "orot.png"));
		UiDesk.getImageRegistry().put(IMG_ORIGINAL,
			BAGMediFactory.loadImageDescriptor(imgroot + "oblau.ico"));
	}
	
	/**
	 * Create a BAGMEdi from a line of the BAG file
	 * 
	 * @param row
	 *            the line
	 */
	public BAGMedi(final String name, final String pharmacode){
		super(name, CODESYSTEMNAME, pharmacode);
		set("Klasse", getClass().getName());
	}
	
	public boolean isGenericum(){
		return checkNull(get("Generikum")).startsWith("G");
	}
	
	public boolean hasGenerica(){
		return get("Generikum").startsWith("O");
	}
	
	public List<Substance> getSubstances(){
		List<String[]> cnt = getList("inhalt", new String[0]);
		ArrayList<Substance> ret = new ArrayList<Substance>(cnt.size());
		for (String[] s : cnt) {
			ret.add(Substance.load(s[0]));
		}
		return ret;
	}
	
	public SortedSet<Interaction> getInteraktionen(){
		List<Substance> substances = getSubstances();
		SortedSet<Interaction> ret = new TreeSet<Interaction>();
		for (Substance s : substances) {
			List<Interaction> interactions = s.getInteractions();
			ret.addAll(interactions);
		}
		return ret;
	}
	
	public SortedSet<Interaction> getInteraktionenMit(final BAGMedi other){
		List<Substance> ls1 = getSubstances();
		List<Substance> ls2 = other.getSubstances();
		SortedSet<Interaction> ret = new TreeSet<Interaction>();
		for (Substance s1 : ls1) {
			if (ls2.contains(s1)) {
				continue;
			}
			for (Substance s2 : ls2) {
				ret = (SortedSet<Interaction>) s1.getInteractionsWith(s2, ret);
			}
		}
		return ret;
	}
	
	public Kontakt getHersteller(){
		return Kontakt.load(getExt("HerstellerID"));
	}
	
	@SuppressWarnings("unchecked")
	public void update(final String[] row){
		Query<Organisation> qo = new Query<Organisation>(Organisation.class);
		String id = qo.findSingle("Name", "=", row[0]);
		if (id == null) {
			Organisation o = new Organisation(row[0], "Pharma");
			id = o.getId();
		}
		Map exi = getMap("ExtInfo");
		exi.put("HerstellerID", id);
		set("Generikum", row[1]);
		exi.put("Pharmacode", row[2]);
		exi.put("BAG-Dossier", row[3]);
		exi.put("Swissmedic-Nr.", row[4]);
		exi.put("Swissmedic-Liste", row[5]);
		exi.put("Kassentyp", "1");
		try {
			setEKPreis(new Money(Double.parseDouble(row[8])));
		} catch (NumberFormatException nex) {
			setEKPreis(new Money());
			log.warn("Parse error preis " + row[7] + ": " + row[8] + "/" + row[9]);
			
		}
		try {
			setVKPreis(new Money(Double.parseDouble(row[9])));
		} catch (NumberFormatException ex) {
			setVKPreis(new Money());
			log.warn("Parse error preis " + row[7] + ": " + row[8] + "/" + row[9]);
			
		}
		
		if (row[10].equals("Y")) {
			exi.put("Limitatio", "Y");
			exi.put("LimitatioPts", row[11]);
		} else {
			exi.remove("Limitation");
		}
		if (row.length > 13) {
			if (!StringTool.isNothing(row[13])) {
				String[] substName = row[13].split("\\|");
				LinkedList<Substance> substances = new LinkedList<Substance>();
				for (String n : substName) {
					Substance s = Substance.find(n);
					if (s == null) {
						s = new Substance(n, row[12]);
					}
					substances.add(s);
				}
				deleteList("inhalt");
				for (Substance s : substances) {
					addToList("inhalt", s.getId(), new String[0]);
					s = null;
				}
				substances = null;
				
			}
			
		}
		if (row.length > 12) {
			set("Gruppe", row[12]);
		}
		setMap("ExtInfo", exi);
	}
	
	@Override
	protected String getConstraint(){
		return "Typ='Medikament'";
	}
	
	@Override
	protected void setConstraint(){
		set("Typ", "Medikament");
	}
	
	@Override
	public String getCodeSystemName(){
		return CODESYSTEMNAME;
	}
	
	@Override
	public String getCodeSystemCode(){
		String gtin = getEAN();
		if (gtin != null && gtin.length() > 3) {
			return "402";
		}
		return super.getCodeSystemCode();
	}
	
	@Override
	public String getCode(){
		return getPharmaCode();
	}
	
	public static BAGMedi load(final String id){
		return new BAGMedi(id);
	}
	
	protected BAGMedi(final String id){
		super(id);
	}
	
	protected BAGMedi(){}
	
	public int compareTo(final BAGMedi arg0){
		return (getLabel().compareTo(arg0.getLabel()));
	}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
	
	@Override
	public boolean delete(){
		String sql = "UPDATE " + EXTTABLE + " SET deleted='1' WHERE ID=" + getWrappedId();
		getConnection().exec(sql);
		return super.delete();
	}
	
	@Override
	public IOptifier getOptifier(){
		return bagOptifier;
	}
	
}
