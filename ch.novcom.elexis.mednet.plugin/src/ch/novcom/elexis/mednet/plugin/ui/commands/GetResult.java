/*******************************************************************************
 * Copyright (c) 2017 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ch.novcom.elexis.mednet.plugin.DocumentImporterPage;

public class GetResult extends AbstractHandler {

	public static final String ID = "ch.novcom.elexis.mednet.plugin.ui.commands.getresult";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		DocumentImporterPage dip = new DocumentImporterPage();

		try {
			dip.doImport(null);
		} catch (Exception e) {
			throw new ExecutionException("Unable to run DocumentImporter");
		}
		
		return null;
	}

}
