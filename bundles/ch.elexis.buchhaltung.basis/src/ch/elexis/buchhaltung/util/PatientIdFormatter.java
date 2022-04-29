/*******************************************************************************
 * Copyright (c) 2006-2010, Gerry Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gerry Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.buchhaltung.util;

import org.apache.commons.lang3.StringUtils;
import ch.rgw.tools.StringTool;

public class PatientIdFormatter {

	int stellen;

	public PatientIdFormatter(int stellen) {
		this.stellen = stellen;
	}

	public String format(String id) {
		if (id == null) {
			id = StringUtils.EMPTY;
		}
		return StringTool.pad(StringTool.LEFT, '0', id, stellen);
	}
}
