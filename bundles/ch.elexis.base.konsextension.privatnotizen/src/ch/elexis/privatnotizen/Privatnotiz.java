/*******************************************************************************
 * Copyright (c) 2006, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *  $Id: Privatnotiz.java 814 2006-08-28 20:00:33Z rgw_ch $
 *******************************************************************************/

package ch.elexis.privatnotizen;

import ch.elexis.core.model.util.ElexisIdGenerator;
import ch.elexis.data.Mandant;
import ch.elexis.data.PersistentObject;

/**
 * Die Datenklasse für Privatnotizen. Eine Privatnotiz besteht aus einer ID und
 * einem Text. Die ID wiederum ist aus der ID des "Besitzers" und einer UID
 * zusammengesetzt. Zum Speichern wird der Einfachheit halber die
 * Elexis-Standardtabelle HEAP2 verwendet.
 *
 * @author Gerry
 *
 */
public class Privatnotiz extends PersistentObject {

	static {
		addMapping("HEAP2", "text=S:C:Contents"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void setText(String tx) {
		set("text", tx); //$NON-NLS-1$
	}

	public String getText() {
		return get("text"); //$NON-NLS-1$
	}

	@Override
	public String getLabel() {
		return Mandant.load(getMandantID()).getLabel();
	}

	public String getMandantID() {
		return getId().split(":")[0]; //$NON-NLS-1$
	}

	public Privatnotiz(Mandant mandant) {
		String id = mandant.getId() + ":" + ElexisIdGenerator.generateId(); //$NON-NLS-1$
		create(id);
	}

	public static Privatnotiz load(String id) {
		return new Privatnotiz(id);
	}

	@Override
	protected String getTableName() {
		return "HEAP2"; //$NON-NLS-1$
	}

	protected Privatnotiz(String id) {
		super(id);
	}

	protected Privatnotiz() {
	}
}
