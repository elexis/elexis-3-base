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
import ch.rgw.tools.TimeTool;

public class TagesNachricht extends PersistentObject {
	private static final String TABLENAME = "CH_ELEXIS_AGENDA_DAYMSG"; //$NON-NLS-1$

	static {
		addMapping(TABLENAME, "Zeile=Kurz", "Text=Msg"); //$NON-NLS-1$ //$NON-NLS-2$
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
