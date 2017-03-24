/*******************************************************************************
 * Copyright (c) 2017 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.emediplan.core;

import java.io.OutputStream;
import java.util.List;

import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;

/**
 * Service interface providing methods to import and export an eMediplabn
 * (http://emediplan.ch/de/home) with Elexis.
 * 
 * @author thomas
 *
 */
public interface EMediplanService {
	
	/**
	 * Get a PDF eMediplan (http://emediplan.ch/de/home) representation of the JSON encoded String,
	 * written to the provided {@link OutputStream}.
	 * 
	 * @param json
	 * @param output
	 */
	public void exportEMediplanPdf(Mandant author, Patient patient,
		List<Prescription> prescriptions, OutputStream output);
	
}
