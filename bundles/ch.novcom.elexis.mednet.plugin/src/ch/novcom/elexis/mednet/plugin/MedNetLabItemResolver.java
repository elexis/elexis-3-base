/*******************************************************************************
 * Copyright (c) 2018 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht - novcom AG
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin;

import ch.elexis.core.importer.div.importers.ILabItemResolver;
import ch.elexis.hl7.model.AbstractData;
import ch.novcom.elexis.mednet.plugin.data.PatientDocumentManager;

public class MedNetLabItemResolver implements ILabItemResolver{
	String institutionName = "";
	
	public MedNetLabItemResolver(String institution){
		this.institutionName = institution;
	}
	
	@Override
	public String getTestName(AbstractData data){
		return data.getName();
	}
	
	@Override
	public String getTestGroupName(AbstractData data){
		return this.institutionName;
	}
	
	@Override
	public String getNextTestGroupSequence(AbstractData data){
		return PatientDocumentManager.DEFAULT_PRIO;
	}
	
}