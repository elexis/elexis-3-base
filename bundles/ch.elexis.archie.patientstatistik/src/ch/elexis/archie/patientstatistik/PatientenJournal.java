/*******************************************************************************
 * Copyright (c) 2008, G. Weirich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.archie.patientstatistik;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;

public class PatientenJournal extends AbstractTimeSeries {
	String[] headings = { "Name", "Konsultationen", "Kosten" };

	public PatientenJournal() {
		super("Patienten");
	}

	@Override
	protected List<String> createHeadings() {
		return Arrays.asList(headings);
	}

	@Override
	public String getDescription() {
		return "Kosten pro Patient";
	}

	@Override
	protected IStatus createContent(IProgressMonitor monitor) {

		return null;
	}

}
