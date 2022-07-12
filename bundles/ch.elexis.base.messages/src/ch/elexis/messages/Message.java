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

package ch.elexis.messages;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Anwender;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class Message extends PersistentObject {

	public static final String FLD_TEXT = "Text"; //$NON-NLS-1$
	public static final String FLD_TIME = "time"; //$NON-NLS-1$
	public static final String FLD_FROM = "from"; //$NON-NLS-1$
	public static final String FLD_TO = "to"; //$NON-NLS-1$

	private static final String TABLENAME = "CH_ELEXIS_MESSAGES"; //$NON-NLS-1$
	private static final String VERSION = "0.2.0"; //$NON-NLS-1$
	private static final String createDB = "CREATE TABLE " + TABLENAME + " (" //$NON-NLS-1$//$NON-NLS-2$
			+ "ID			VARCHAR(25) primary key," + "lastupdate BIGINT," //$NON-NLS-1$ //$NON-NLS-2$
			+ "deleted		CHAR(1) default '0'," + "origin		VARCHAR(25)," //$NON-NLS-1$ //$NON-NLS-2$
			+ "destination	VARCHAR(25)," //$NON-NLS-1$
			+ "dateTime		CHAR(14)," // yyyymmddhhmmss //$NON-NLS-1$
			+ "msg			TEXT);" + "INSERT INTO " + TABLENAME + " (ID,origin) VALUES ('VERSION','" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ VERSION + "');"; //$NON-NLS-1$

	static {
		addMapping(TABLENAME, FLD_FROM + "=origin", FLD_TO + "=destination", FLD_TIME + "=dateTime", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
				FLD_TEXT + "=msg"); //$NON-NLS-1$

		Message ver = load("VERSION"); //$NON-NLS-1$
		if (ver.state() < PersistentObject.DELETED) {
			initialize();
		} else {
			VersionInfo vi = new VersionInfo(ver.get(FLD_FROM));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("0.2.0")) { //$NON-NLS-1$
					createOrModifyTable("ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;"); //$NON-NLS-1$ //$NON-NLS-2$
					ver.set(FLD_FROM, VERSION);
				}
			}
		}
	}

	static void initialize() {
		createOrModifyTable(createDB);
	}

	public Message(final Anwender an, final String text) {
		create(null);
		TimeTool tt = new TimeTool();
		String dt = tt.toString(TimeTool.TIMESTAMP);
		set(new String[] { FLD_FROM, FLD_TO, FLD_TIME, FLD_TEXT },
				new String[] { CoreHub.getLoggedInContact().getId(), an.getId(), dt, text });

	}

	/**
	 * @return the raw sender string
	 * @since 3.7
	 */
	public String getSenderString() {
		return get(FLD_FROM);
	}

	public Anwender getSender() {
		Anwender an = Anwender.load(getSenderString());
		return an;
	}

	public Anwender getDest() {
		Anwender an = Anwender.load(get(FLD_TO));
		return an;
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}

	public String getText() {
		return checkNull(get(FLD_TEXT));
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	public static Message load(final String id) {
		return new Message(id);
	}

	protected Message(final String id) {
		super(id);
	}

	protected Message() {
	}
}
