/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jdom.Document;

import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.tarmed.printer.XML40Printer;
import ch.elexis.tarmed.printer.XML44Printer;

/**
 * This is a pop-in replacement for RnPrintView. To avoid several problems around OpenOffice based
 * bills we keep things easier here. Thus this approach does not optimize printer access but rather
 * waits for each page to be printed before starting the next.
 * 
 * We also corrected several problems around the TrustCenter-system. Tokens are printed only on TG
 * bills and only if the mandator has a TC contract. Tokens are computed correctly now with the TC
 * number as identifier in TG bills and left as ESR in TP bills.
 * 
 * @author Gerry
 * 
 */
public class RnPrintView2 extends ViewPart {
	public static final String ID = "ch.elexis.arzttarife_ch.printview2";
	
	TextContainer text;
	
	public RnPrintView2(){
		
	}
	
	@Override
	public void createPartControl(final Composite parent){
		text = new TextContainer(getViewSite());
		text.getPlugin().createContainer(parent, new ITextPlugin.ICallback() {
			
			@Override
			public void save(){
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean saveAs(){
				// TODO Auto-generated method stub
				return false;
			}
		});
		text.getPlugin().setParameter(ITextPlugin.Parameter.NOUI);
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Druckt die Rechnung auf eine Vorlage, deren Ränder alle auf 0.5cm eingestellt sein müssen,
	 * und die unterhalb von 170 mm leer ist. (Papier mit EZ-Schein wird erwartet) Zweite und
	 * Folgeseiten müssen gem Tarmedrechnung formatiert sein.
	 * 
	 * @param rn
	 *            die Rechnung
	 * @param saveFile
	 *            Filename für eine XML-Kopie der Rechnung oder null: Keine Kopie
	 * @param withForms
	 * @param monitor
	 * @return
	 */
	public boolean doPrint(final Rechnung rn, final IRnOutputter.TYPE rnType,
		final String saveFile, final boolean withESR, final boolean withForms,
		final boolean doVerify, final IProgressMonitor monitor){
		XMLExporter xmlex = new XMLExporter();
		Document xmlRn = xmlex.doExport(rn, saveFile, rnType, doVerify);
		if (rn.getStatus() == RnStatus.FEHLERHAFT) {
			return false;
		}
		// check if we are working with 4.0 or 4.4 tarmed xml
		if (XMLExporter.getXmlVersion(xmlRn.getRootElement()).equals("4.0")) {
			XML40Printer xmlPrinter = new XML40Printer(text);
			return xmlPrinter.doPrint(rn, xmlRn, rnType, saveFile, withESR, withForms, doVerify,
				monitor);
		} else if (XMLExporter.getXmlVersion(xmlRn.getRootElement()).equals("4.4")) {
			XML44Printer xmlPrinter = new XML44Printer(text);
			return xmlPrinter.doPrint(rn, xmlRn, rnType, saveFile, withESR, withForms, doVerify,
				monitor);
		} else {
			SWTHelper.showError("Fehler beim Drucken",
				"Die Rechnung ist in keinem gültigen XML Format");
			rn.addTrace(Rechnung.REJECTED, "XML Format");
			return false;
		}
		

	}
}
