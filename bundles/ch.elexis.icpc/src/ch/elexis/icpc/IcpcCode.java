/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.icpc;

import java.util.List;

import org.eclipse.jface.action.IAction;

import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.Tree;
import ch.rgw.tools.VersionInfo;

public class IcpcCode extends PersistentObject implements IDiagnose {
	static final String TABLENAME = "CH_ELEXIS_ICPC";
	static final String VERSION = "1.2.1";
	
	public static final String createDB = "CREATE TABLE " + TABLENAME + " ("
		+ "ID			CHAR(3) primary key," + "lastupdate BIGINT," + "deleted	CHAR(1) default '0',"
		+ "component	CHAR(2)," + "short		VARCHAR(80)," + "synonyms   VARCHAR(250),"
		+ "icd10		    TEXT," + "txt			TEXT," + "criteria		TEXT," + "inclusion 		TEXT,"
		+ "exclusion		TEXT," + "consider		TEXT," + "note			TEXT);" + "create index " + TABLENAME
		+ "_IDX1 ON " + TABLENAME + " (component);" + "INSERT INTO " + TABLENAME
		+ " (ID,txt) VALUES ('ver','" + VERSION + "');";
	
	private static Tree root;
	private String realCode;
	
	static {
		addMapping(TABLENAME, "component", "text=txt", "short", "icd10", "criteria", "inclusion",
			"exclusion", "consider", "note", "synonyms");
		IcpcCode ver = IcpcCode.load("ver");
		if (!ver.exists()) {
			if (PersistentObject.tableExists(TABLENAME)) {
				createOrModifyTable("DROP TABLE " + TABLENAME);
			}
			createOrModifyTable(createDB);
		}
		VersionInfo vi = new VersionInfo(ver.get("text"));
		
		if (vi.isOlder(VERSION)) {
			if (vi.isOlder("1.2.1")) {
				createOrModifyTable("ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;");
				ver.set("text", VERSION);
			}
		}
		
	}
	
	public static void initialize(){
		UiDesk.syncExec(new Runnable() {
			
			public void run(){
				createOrModifyTable(createDB);
			}
		});
		
	}
	
	public static Tree getRoot(){
		if (root == null) {
			reload();
		}
		return root;
	}
	
	@Override
	public String getLabel(){
		if (realCode == null) {
			return getId() + " " + get("short");
		}
		return realCode + " " + get("short");
	}
	
	public void setLabel(String l){
		realCode = l;
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public String getCode(){
		return getId();
	}
	
	public String getCodeSystemCode(){
		return "999";
	}
	
	public String getCodeSystemName(){
		return "Icpc";
	}
	
	public String getText(){
		return get("text");
	}
	
	protected IcpcCode(){}
	
	protected IcpcCode(String id){
		super(id);
	}
	
	public static IcpcCode load(String id){
		return new IcpcCode(id);
	}
	
	public static final String[] classes = {
		Messages.IcpcCode_class_A, Messages.IcpcCode_class_B, Messages.IcpcCode_class_D,
		Messages.IcpcCode_class_F, Messages.IcpcCode_class_H, Messages.IcpcCode_class_K,
		Messages.IcpcCode_class_L, Messages.IcpcCode_class_N, Messages.IcpcCode_class_P,
		Messages.IcpcCode_class_R, Messages.IcpcCode_class_S, Messages.IcpcCode_class_T,
		Messages.IcpcCode_class_U, Messages.IcpcCode_class_W, Messages.IcpcCode_class_X,
		Messages.IcpcCode_class_Y, Messages.IcpcCode_class_Z
	};
	/*
	 * public static final String[] components_de={ };
	 */
	public static final String[] components = {
		Messages.IcpcCode_comp_1, Messages.IcpcCode_comp_2, Messages.IcpcCode_comp_3,
		Messages.IcpcCode_comp_4, Messages.IcpcCode_comp_5, Messages.IcpcCode_comp_6,
		Messages.IcpcCode_comp_7
	};
	
	/**
	 * Fetch a list of all IcpcCodes in the specified class and component.
	 * 
	 * @param cl
	 *            Class
	 * @param cmp
	 *            Component
	 * @param rev
	 *            Reverse order
	 * @return List of IcpcCodes.
	 */
	public static List<IcpcCode> loadAllFromComponent(String cl, String cmp, boolean rev){
		Query<IcpcCode> qbe = new Query<IcpcCode>(IcpcCode.class);
		qbe.add("component", StringTool.equals, cmp.substring(0, 1));
		qbe.startGroup();
		qbe.add("ID", "Like", "*%");
		qbe.or();
		qbe.add("ID", "Like", cl.substring(0, 1) + "%");
		qbe.endGroup();
		qbe.orderBy(rev, new String[] {
			"ID"
		});
		List<IcpcCode> list = qbe.execute();
		for (IcpcCode code : list) {
			code.setLabel(cl.substring(0, 1) + code.getId().substring(1));
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static void reload(){
		IcpcCode ic = IcpcCode.load("ver");
		if (!ic.exists()) {
			initialize();
		} else {
			VersionInfo vi = new VersionInfo(ic.getText());
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("1.1.0")) {
					getConnection().exec(
						"ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';");
					ic.set("text", VERSION);
				}
				if (vi.isOlder("1.2.0")) {
					getConnection().equals(
						"ALTER TABLE " + TABLENAME + " ADD synonyms VARCHAR(255)");
				}
			}
		}
		IcpcCode.root = new Tree(null, null);
		
		for (int i = classes.length - 1; i >= 0; i--) {
			String cl = classes[i];
			Tree tClass = new Tree(IcpcCode.root, cl);
			for (int j = components.length - 1; j >= 0; j--) {
				String cmp = components[j];
				Tree tComp = new Tree(tClass, cmp);
				List<IcpcCode> list = loadAllFromComponent(cl, cmp, true);
				for (IcpcCode code : list) {
					new Tree(tComp, code);
				}
			}
			
		}
		
	}
	
	@Override
	public boolean isDragOK(){
		if (getId().length() == 3) {
			return true;
		}
		return super.isDragOK();
	}
	
	@Override
	public String storeToString(){
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName()).append("::").append(getId()).append("::");
		sb.append(realCode == null ? " " : realCode);
		return sb.toString();
	}
	
	public List<IAction> getActions(Verrechnet kontext){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getActions(Object context){
		// TODO Auto-generated method stub
		return null;
	}
}
