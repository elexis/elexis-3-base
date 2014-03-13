/*******************************************************************************
 * Copyright (c) 2009, SGAM Informatics and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.icpc.fire.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jdom.Document;
import org.jdom.Element;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.data.Sticker;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.XMLTool;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ExportFireHandler extends AbstractHandler {
	public static final String FIRESTICKERNAME = "Fire (ICPC)";
	private static final String CFGPARAM = "ICPC_FIRE_LAST_UPLOAD";
	private Sticker fireSticker;
	
	public ExportFireHandler(){
		String id =
			new Query<Sticker>(Sticker.class).findSingle(Sticker.NAME, Query.EQUALS,
				FIRESTICKERNAME);
		if (id == null) {
			fireSticker = new Sticker(FIRESTICKERNAME, "0066CC", "C0C0C0");
		} else {
			fireSticker = Sticker.load(id);
		}
	}
	
	/**
	 * the command has been executed, so extract extract the needed information from the application
	 * context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException{
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		String lastupdate = CoreHub.globalCfg.get(CFGPARAM, null);
		if (lastupdate == null) {
			lastupdate = "20090101";
		}
		Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
		TimeTool ttFrom = new TimeTool(lastupdate);
		qbe.add(Konsultation.DATE, Query.GREATER_OR_EQUAL, ttFrom.toString(TimeTool.DATE_COMPACT));
		List<Konsultation> konsen = qbe.execute();
		CoreHub.globalCfg.set(CFGPARAM, new TimeTool().toString(TimeTool.DATE_COMPACT));
		if (konsen.size() > 0) {
			FileDialog fd = new FileDialog(Hub.getActiveShell(), SWT.SAVE);
			fd.setFileName("elexis-fire" + new TimeTool().toString(TimeTool.DATE_COMPACT) + ".xml");
			fd.setFilterExtensions(new String[] {
				"xml"
			});
			fd.setFilterNames(new String[] {
				"XML-Dateien"
			});
			String expath = fd.open();
			if (expath != null) {
				Element eRoot = new Element("meldung");
				Document doc = new Document(eRoot);
				for (Konsultation k : konsen) {
					Fall fall = k.getFall();
					if (fall == null) {
						continue;
					}
					Patient pat = fall.getPatient();
					if (pat == null) {
						continue;
					}
					k.removeSticker(fireSticker); // TODO: remove
					if (!k.getStickers().contains(fireSticker)) {
						k.addSticker(fireSticker);
						Element eKons = new Element("konsultation");
						eKons.addContent(createSub("konsdate",
							new TimeTool(k.getDatum()).toString(TimeTool.DATE_ISO)));
						eKons.addContent(createSub("patid", pat.getPatCode()));
						eKons.addContent(createSub("patyear", Integer.toString(new TimeTool(pat
							.getGeburtsdatum()).get(TimeTool.YEAR))));
						eKons.addContent(createSub("patgender",
							pat.getGeschlecht().equals(Person.MALE) ? "male" : "female"));
						eKons.addContent(createSub("arzt",
							TarmedRequirements.getEAN(k.getMandant())));
						eRoot.addContent(eKons);
						Analyzer an = new Analyzer(k);
						an.addDiagnoseElement(eKons);
						an.addVitalElement(eKons);
						an.addLaborElements(eKons);
						an.addMediElements(eKons);
					}
				}
				if (XMLTool.writeXMLDocument(doc, expath)) {
					SWTHelper.showInfo("SGAM / Fire", "Die Datei wurde erfolgreich exportiert");
				} else {
					SWTHelper.showError("Fire", "SGAM / Fire",
						"Beim Export ist ein Fehler aufgetreten");
				}
			}
		}
		
		return null;
	}
	
	private Element createSub(String name, String contents){
		Element ret = new Element(name);
		ret.setText(contents);
		return ret;
	}
}
