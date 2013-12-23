/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.images;

import java.io.ByteArrayInputStream;

import org.eclipse.swt.graphics.Image;

import ch.elexis.base.konsextension.bildanzeige.Messages;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class Bild extends PersistentObject {
	public static final String DBVERSION = "1.2.0"; //$NON-NLS-1$
	public static final String TABLENAME = "BILDANZEIGE"; //$NON-NLS-1$
	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$ //$NON-NLS-2$
		"ID				VARCHAR(25) primary key," + //$NON-NLS-1$
		"lastupdate		BIGINT," + //$NON-NLS-1$
		"deleted		CHAR(1) default '0'," + //$NON-NLS-1$
		"PatID			VARCHAR(25)," + //$NON-NLS-1$
		"Datum			CHAR(8)," + //$NON-NLS-1$
		"Title 			VARCHAR(30)," + //$NON-NLS-1$
		"Info			TEXT," + //$NON-NLS-1$
		"Keywords		VARCHAR(80)," + //$NON-NLS-1$
		"isRef			char(2)," + //$NON-NLS-1$
		"Bild			BLOB);" + //$NON-NLS-1$
		"CREATE INDEX BANZ1 ON " + TABLENAME + " (PatID);" + //$NON-NLS-1$ //$NON-NLS-2$
		"CREATE INDEX BANZ2 ON " + TABLENAME + " (Keywords);" + //$NON-NLS-1$ //$NON-NLS-2$
		"INSERT INTO " + TABLENAME + " (ID, TITLE) VALUES ('1','" + DBVERSION + "');"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	static {
		addMapping(TABLENAME, "PatID", "Datum=S:D:Datum", "Titel=Title", "Keywords", "Bild", "Info"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		Bild start = load("1"); //$NON-NLS-1$
		if (start == null) {
			init();
		} else {
			VersionInfo vi = new VersionInfo(start.get("Titel")); //$NON-NLS-1$
			if (vi.isOlder(DBVERSION)) {
				if (vi.isOlder("1.2.0")) { //$NON-NLS-1$
					getConnection().exec("ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;"); //$NON-NLS-1$ //$NON-NLS-2$
					start.set("Titel", DBVERSION); //$NON-NLS-1$
				}
				if (vi.isOlder("1.1.0")) { //$NON-NLS-1$
					getConnection().exec(
						"ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';"); //$NON-NLS-1$ //$NON-NLS-2$
					start.set("Titel", DBVERSION); //$NON-NLS-1$
				} else {
					SWTHelper.showError(Messages.Bild_VersionConflict,
						Messages.Bild_BadVersionNUmber);
				}
			}
		}
	}
	
	/**
	 * Tabelle neu erstellen
	 */
	public static void init(){
		createOrModifyTable(createDB);
	}
	
	public Bild(Patient patient, String Titel, byte[] data){
		if (patient == null) {
			SWTHelper.showError(Messages.Bild_NoPatientSelected,
				Messages.Bild_YouShouldSelectAPatient);
			return;
		}
		create(null);
		set(new String[] {
			"PatID", "Titel", "Datum" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}, patient.getId(), Titel, new TimeTool().toString(TimeTool.DATE_COMPACT));
		setBinary("Bild", data); //$NON-NLS-1$
	}
	
	public Patient getPatient(){
		return Patient.load(get("PatID")); //$NON-NLS-1$
	}
	
	@Override
	public String getLabel(){
		StringBuilder sb = new StringBuilder();
		sb.append(checkNull(get("Titel"))).append(" (").append(get("Datum")).append(")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		return sb.toString();
	}
	
	public String getDate(){
		return get("Datum"); //$NON-NLS-1$
	}
	
	public String getTitle(){
		return get("Titel"); //$NON-NLS-1$
	}
	
	public String getInfo(){
		return get("Info"); //$NON-NLS-1$
	}
	
	/**
	 * Image des Bildes erzeugen. Achtung: dieses muss nach Gebrauch mit dispose() wieder entsorgt
	 * werden.
	 * 
	 * @return ein SWT-Image
	 */
	public Image createImage(){
		byte[] data = getBinary("Bild"); //$NON-NLS-1$
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		Image ret = new Image(UiDesk.getDisplay(), bais);
		return ret;
	}
	
	public static Bild load(String ID){
		Bild ret = new Bild(ID);
		if (ret.exists()) {
			return ret;
		}
		return null;
	}
	
	public byte[] getData(){
		return getBinary("Bild"); //$NON-NLS-1$
	}
	
	@Override
	protected String getTableName(){
		return "BILDANZEIGE"; //$NON-NLS-1$
	}
	
	protected Bild(String id){
		super(id);
	}
	
	protected Bild(){}
}
