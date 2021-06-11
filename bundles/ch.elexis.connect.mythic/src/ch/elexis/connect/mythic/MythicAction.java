/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.connect.mythic;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.importer.div.importers.TransientLabResult;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.core.ui.importer.div.rs232.Connection;
import ch.elexis.core.ui.importer.div.rs232.Connection.ComPortListener;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.LabItem;
import ch.elexis.data.Patient;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class MythicAction extends Action implements ComPortListener {
	
	private static final int MODE_AWAIT_START = 0;
	private static final int MODE_AWAIT_LINES = 1;
	private final int mode = 0;
	
	Connection ctrl =
		new Connection("Elexis-Mythic", CoreHub.localCfg.get(Preferences.PORT, "COM1"),
			CoreHub.localCfg.get(Preferences.PARAMS, "9600,8,n,1"), this);
	ILaboratory myLab;
	Patient actPatient;
	
	public MythicAction(){
		super("Mythic", AS_CHECK_BOX);
		setToolTipText("Daten von Mythic einlesen");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.connect.mythic", //$NON-NLS-1$
			"icons/mythic.ico"));
			
		myLab = LabImportUtilHolder.get().getOrCreateLabor("Mythic");
	}
	
	@Override
	public void run(){
		if (isChecked()) {
			KontaktSelektor ksl =
				new KontaktSelektor(Hub.getActiveShell(), Patient.class, "Patient auswählen",
					"Wem soll der Mythic-Befund zugeordnet werden?", Patient.DEFAULT_SORT);
			ksl.create();
			ksl.getShell().setText("Mythic Patientenauswahl");
			if (ksl.open() == org.eclipse.jface.dialogs.Dialog.OK) {
				actPatient = (Patient) ksl.getSelection();
				if (ctrl.connect()) {
					ctrl.readLine((byte) 13, 600);
					return;
				} else {
					SWTHelper.showError("Fehler mit Port", "Konnte seriellen Port nicht öffnen");
				}
			}
		} else {
			if (ctrl.isOpen()) {
				actPatient = null;
				ctrl.sendBreak();
				ctrl.close();
			}
		}
		setChecked(false);
	}
	
	public void gotBreak(final Connection connection){
		actPatient = null;
		connection.close();
		SWTHelper.showError("Mythic", "Datenübertragung wurde unterbrochen");
		
	}
	
	public void gotChunk(final Connection connection, final String data){
		
		// System.out.println(data+"\n");
		if (actPatient != null) {
			if (data.startsWith("END_RESULT")) {
				actPatient = null;
				ctrl.close(); // That's it!
				setChecked(false); // Pop out Mythic-Button
				ElexisEventDispatcher.reload(LabItem.class); // and tell everybody, we're finished
			} else {
				fetchResult(data);
			}
			
		}
		
	}
	
	private void fetchResult(final String data){
		String[] line = data.split(";");
		int idx = StringTool.getIndex(results, line[0]);
		if (idx != -1) {
			if (line.length > 7) {
				String ref = "";
				if (StringUtils.isNotBlank(line[5]) && StringUtils.isNotBlank(line[6])) {
					ref = line[5] + "-" + line[6];
				} else if (StringUtils.isNotBlank(line[5])) {
					ref = ">" + line[5];
				} else if (StringUtils.isNotBlank(line[6])) {
					ref = "<" + line[6];
				}
				
				ILabItem li = LabImportUtilHolder.get().getLabItem(line[0], myLab);
				if (li == null) {
					li = LabImportUtilHolder.get().createLabItem(line[0], line[0], myLab, ref, ref,
						units[idx], LabItemTyp.NUMERIC, "MTH Mythic", "50");
				}
				
				String comment = "";
				if ((line[2].length() > 0) || (line[3].length() > 0)) {
					comment = line[2] + ";" + line[3];
				}
				IPatient iPatient = CoreModelServiceHolder.get()
					.load(actPatient.getId(), IPatient.class)
					.orElse(null);
				
				TransientLabResult tLabResult =
					new TransientLabResult.Builder(iPatient, myLab, li, line[1])
						.date(new TimeTool()).ref(ref).comment(comment)
						.build(LabImportUtilHolder.get());
				LabImportUtilHolder.get().importLabResults(Collections.singletonList(tLabResult),
					new DefaultLabImportUiHandler());
			}
		}
	}
	
	public void timeout(){
		ctrl.close();
		SWTHelper.showError("Mythic", "Das Gerät antwortet nicht");
		setChecked(false);
	}
	
	String[] results = {
		"WBC", "RBC", "HGB", "HCT", "MCV", "MCH", "MCHC", "RDW", "PLT", "MPV", "THT", "PDW", "LYM%",
		"MON%", "GRA%", "LYM", "MON", "GRA"
	};
	String[] units = {
		"G/l", "G/l", "g/dl", "%", "fl", "pg", "g/dl", "%", "G/l", "fl", "%", "%", "%", "%", "%",
		"G/l", "G/l", "G/l"
	};
}
