/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    M. Descher - fixes on table creation and correct updating
 *
 *******************************************************************************/
package ch.elexis.agenda.data;

import ch.elexis.data.PersistentObject;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class TagesNachricht extends PersistentObject {
	private static final String TABLENAME = "CH_ELEXIS_AGENDA_DAYMSG"; //$NON-NLS-1$
	private static final String VERSION = "0.4.0"; //$NON-NLS-1$
	private static final String createDB = "CREATE TABLE " + TABLENAME + "(" //$NON-NLS-1$ //$NON-NLS-2$
			+ "ID		VARCHAR(8) primary key," //$NON-NLS-1$
			+ "deleted	CHAR(1) default '0'," + "Kurz		VARCHAR(80)," //$NON-NLS-1$
			+ "Msg		TEXT," //$NON-NLS-1$
			+ "lastupdate BIGINT default '0'" //$NON-NLS-1$
			+ ");" //$NON-NLS-1$
			+ "INSERT INTO " + TABLENAME + " (ID,Kurz) VALUES ('1','" + VERSION + "');"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String update020 = "ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';" + "UPDATE " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ TABLENAME + " SET Kurz='0.2.0' WHERE ID='1';"; //$NON-NLS-1$
	private static final String update030 = "ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT default 0;" //$NON-NLS-1$ //$NON-NLS-2$
			+ "UPDATE " + TABLENAME + " SET Kurz='0.3.0' WHERE ID='1';"; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String update040 = "ALTER TABLE " + TABLENAME + " MODIFY ID VARCHAR(8) primary key;" //$NON-NLS-1$ //$NON-NLS-2$
			+ "UPDATE " + TABLENAME + " SET Kurz='0.4.0' WHERE ID='1';"; //$NON-NLS-1$ //$NON-NLS-2$

	static {
		addMapping(TABLENAME, "Zeile=Kurz", "Text=Msg"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!tableExists(TABLENAME)) {
			try {
				createOrModifyTable(createDB);
				// ByteArrayInputStream bais = new
				// ByteArrayInputStream(createDB.getBytes("UTF-8"));
				// getConnection().execScript(bais, true, false);
			} catch (Exception ex) {
				ExHandler.handle(ex);
			}
		} else {
			VersionInfo vi = new VersionInfo(TagesNachricht.load("1").get(Messages.TagesNachricht_29)); //$NON-NLS-1$
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("0.2.0")) { //$NON-NLS-1$
					createOrModifyTable(update020);
				} else if (vi.isOlder("0.3.0")) { //$NON-NLS-1$
					createOrModifyTable(update030);
				} else if (vi.isOlder("0.4.0")) { //$NON-NLS-1$
					createOrModifyTable(update040);
				}
			}

		}

	}

	public TagesNachricht(TimeTool date, String kurz, String lang) {
		if (date == null) {
			date = new TimeTool();
		}
		create(date.toString(TimeTool.DATE_COMPACT));
		set(new String[] { "Zeile", "Text" //$NON-NLS-1$ //$NON-NLS-2$
		}, kurz, lang);
	}

	public String getZeile() {
		return get("Zeile"); //$NON-NLS-1$
	}

	public String getLangtext() {
		return get("Text"); //$NON-NLS-1$
	}

	public void setZeile(String zeile) {
		set("Zeile", zeile); //$NON-NLS-1$
	}

	public void setLangtext(String text) {
		set("Text", text); //$NON-NLS-1$
	}

	@Override
	public String getLabel() {
		return get("Zeile"); //$NON-NLS-1$
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	protected TagesNachricht(final String id) {
		super(id);
	}

	protected TagesNachricht() {
	}

	public static TagesNachricht load(TimeTool tt) {
		return new TagesNachricht(tt.toString(TimeTool.DATE_COMPACT));
	}

	public static TagesNachricht load(final String id) {
		return new TagesNachricht(id);
	}
}
