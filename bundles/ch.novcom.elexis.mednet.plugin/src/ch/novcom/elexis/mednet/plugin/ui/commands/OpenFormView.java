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
package ch.novcom.elexis.mednet.plugin.ui.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6301;
import at.medevit.elexis.gdt.tools.GDTSatzNachrichtHelper;
import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;
import ch.novcom.elexis.mednet.plugin.MedNet;

public class OpenFormView extends AbstractHandler {
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(OpenFormView.class.getName());
	/**
	 * Encoding used to generate GDT files
	 */
	private static Charset GDT_ENCODING = Charset.forName("ISO-8859-1");//$NON-NLS-1$
	
	public static final String ID = "ch.novcom.elexis.mednet.plugin.ui.commands.openformview";//$NON-NLS-1$
	
	/**
	 * The Format in which birthdate is delivered by the Patient.getGeburtsdatum() function
	 */
	protected final static SimpleDateFormat fromDatabase = new SimpleDateFormat("dd.MM.yyyy");//$NON-NLS-1$
	/**
	 * The Format in which birthdate should be delivered in the GDT Files
	 */
	protected final static SimpleDateFormat toGDT = new SimpleDateFormat("ddMMyyyy");//$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String logPrefix = "execute() - ";//$NON-NLS-1$
		Patient pat = null;
		
		//First of all find the selected Patient
		ISelection selection =
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		IStructuredSelection strucSelection = (IStructuredSelection) selection;
		if (selection != null & selection instanceof IStructuredSelection) {
			pat = (Patient) strucSelection.getFirstElement();
		}
		

		String configuredGDTId = CoreHub.localCfg.get(GDTPreferenceConstants.CFG_GDT_ID, "");
		if (pat == null) {
			pat = ElexisEventDispatcher.getSelectedPatient();
		}
		if (pat == null) {
			return null;
		}
		
		String socialSecurityNumber = pat.getXid(XidConstants.DOMAIN_AHV); // AHV-Nummer
		String birthdate = "";
		try {
			birthdate = OpenFormView.toGDT.format(OpenFormView.fromDatabase.parse(pat.getGeburtsdatum()));
		}
		catch(ParseException pe) {
			LOGGER.error(logPrefix+"Unable to parse birthdate "+ pat.getGeburtsdatum() );//$NON-NLS-1$
		}
		
		//If we have the selected Patient, we can create a simple GDT File
		GDTSatzNachricht6301 gdt6301 = new GDTSatzNachricht6301(
			pat.get(Patient.FLD_PATID),
			pat.getName(),
			pat.getVorname(),
			birthdate,
			null, 
			pat.get(Patient.TITLE),
			socialSecurityNumber,
			pat.get(Patient.FLD_ZIP) + " " + pat.get(Patient.FLD_PLACE),
			pat.get(Patient.FLD_STREET),
			null,
			GDTSatzNachrichtHelper.bestimmeGeschlechtsWert(pat.get(Patient.FLD_SEX)),
			null,
			null,
			null,
			null,
			configuredGDTId,
			GDTConstants.ZEICHENSATZ_IBM_CP_437 + "",
			GDTConstants.GDT_VERSION
		);
		
		File file = null;
		//Finally we can write this GDT to a temporary file that will be deleted after elexis has been stopped
		try {
			file = File.createTempFile("mednet-"+pat.get(Patient.FLD_PATID)+"-", ".gdt");//$NON-NLS-1$
			file.deleteOnExit();
			Files.write(file.toPath(), String.join("\n",gdt6301.getMessage()).getBytes(OpenFormView.GDT_ENCODING));
		}
		catch(IOException ioe){
			//If there are some ioeException logs it
			LOGGER.error(logPrefix+"IOException creating gdtFile.",ioe);//$NON-NLS-1$
			MedNet.openFormview(null);
			return null;
		}
		
		//and we call mednet
		MedNet.openFormview(file.toPath());
		return null;
	}
	
}
