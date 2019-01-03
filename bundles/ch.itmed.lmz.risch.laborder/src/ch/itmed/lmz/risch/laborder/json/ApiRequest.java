/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.lmz.risch.laborder.json;

import ch.itmed.lmz.risch.laborder.gdt.GdtEncoder;

public final class ApiRequest {
	private String FormID;
    private String Benutzer;
    private String BenutzerMandant;
    private String Verordnender;
    private String VerordnenderMandatorID;
    private String Meldungssprache;
    private String ComputerName;
    private String PatDataFormatID = "GDT3.0";
    private String PatData;
    
    public ApiRequest(final String formId) throws UnsupportedOperationException {
    	FormID = formId;
    	Benutzer = "default";
    	BenutzerMandant = "Mandant";
    	Verordnender = "Verordnender";
    	VerordnenderMandatorID = "VerordnenderMandatorID";
    	Meldungssprache = "Meldungssprache";
    	ComputerName = "ComputerName";
    	PatData = new GdtEncoder(formId).toString();
    }
}
