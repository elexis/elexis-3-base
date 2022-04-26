/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui;

import java.util.List;

public class VaccinationPlanHeaderDefinition {
	public final String id;
	final String name;
	final List<String> base;
	final List<String> extended;

	public VaccinationPlanHeaderDefinition(String id, String name, List<String> base, List<String> extended) {
		this.id = id;
		this.name = name;
		this.base = base;
		this.extended = extended;
	}
}
