/*******************************************************************************
 * 
 * NOAText_jsl - A text plugin with improved stability, functionality and compatibility at least until LibreOffice 3.3.4
 * 
 * Portions Copyright (c) 2011-2012 Joerg Sigle, (c) 2007-2010 G. Weirich and Elexis, (c) 2003-2006 IOn AG
 * 
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU General Public License Version 2.1                      *
 *                                                                          * 
 * GNU General Public License Version 2.1                                   *
 * ======================================================================== *
 * Portions Copyright 2012 by Joerg Sigle                                   *
 * Portions Copyright 2007-2010 G. Weirich and Elexis                       *
 * Portions Copyright 2003-2006 by IOn AG                                   *
 *                                                                          *
 * This program is free software: you can redistribute it and/or modify     *
 * it under the terms of the GNU General Public License as published by     *
 * the Free Software Foundation, either version 2.1 of the License.         *
 *                                                                          *
 *  This program is distributed in the hope that it will be useful,         *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 *  GNU General Public License for more details.                            *
 *                                                                          *
 *  You should have received a copy of the GNU General Public License       *
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.   *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.jsigle.com                                                   *
 *  http://www.ql-recorder.com                                              *
 *  http://code.google.com/p/noa-libre                                      *
 *  http://www.elexis.ch                                                    *
 *  http://www.ion.ag                                                       *
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 * Please note: Previously, versions of the NOA library provided by         *
 * www.ion.ag and the noa-libre project carried a licensing remark          *
 * that made them available under the LGPL. However, they include portions  *
 * obtained from the YaBS project, licensed under GPL. Consequently, NOA    *
 * should have been licensed under the GPL, not LGPL, as long as no special *
 * permission of the authors of YaBS for LGPL licensing had been obtained.  *
 *                                                                          *
 * To point out the possible problem, I'm providing the files where I added *
 * contributions under the GPL for now. This move is always allowed for     *
 * LPGL licensed material. 20120623js                                       * 
 *                                                                          *
 * I'm unsure whether the GPL license might have to be propagated through   *
 * the remainder of the noa-libre related files; as it is unclear which     *
 * of these reference the YaBS material, and so on, I leave the files       *
 * where I did not make any changes unchanged so far.                       *
 *                                                                          *
 * The following license was probably included by G. Weirich and Elexis     *
 * in the NOAText.java and OOPrinter.java files. Without any special        *
 * permission, however, I guess it might not have been legally used:        *
 *                                                                          *
 * Obviously, code was directly copied from the NOA Nice Office Access      *
 * library or its examples - as indicated by preserved authorship remarks.  *
 * All NOA files I've seen so far were marked as published under LGPL       *
 * (and at least some should have been published under GPL, see above).     *
 * As far as I understand it, when you copy (not merely link to) LGPL code  *
 * the result of that operation must be licensed under GPL (probably).      *
 * If that shouldn't apply, then it must be licensed under LGPL at least.   *
 *                                                                          *
 * If that should cause an incompatibility with the Eclipse license,        *
 * which it probably does, I guess that the Eclipse license could not       *
 * have legally been used, or the resulting programs could not have         *
 * legally been shared with anybody at all.                                 *
 *                                                                          *
 * So I keep this notice for information purposes only                      *
 * and to point out an issue that needs resolving:                          *
 *                                                                          *
 * All rights reserved. This program and the accompanying materials         *
 * are made available under the terms of the Eclipse Public License v1.0    *
 * which accompanies this distribution, and is available at                 *
 * http://www.eclipse.org/legal/epl-v10.html                                *
 *                                                                          *
 * Contributors:                                                            *
 *    Joerg Sigle  - Updated to more recent noa library, then to noa-libre, *
 *                   added process monitoring output, researched Elexis and *
 *                   OpenOffice/LibreOffice stability problems,             *
 *                   improved stability by controlling menu options,        *
 *                   open dialogs, and undocked toolbars in OO/LO           *
 *                   and by adding a watchdog timer to terminate stalls.    *
 *                   Reviewed copyright notices and licenses, and applied   *
 *                   preliminary fixes for a problematic situation.         *
 *                                                                          *
 *    H. Marlovits - added support for placeholders inside controls         *
 *    G. Weirich   - initial implementation                                 *
 *                                                                          *
 *  $Id$                                                                    *
 ****************************************************************************/
package com.jsigle.noa;

import java.awt.print.PageFormat;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import com.jsigle.noa.OOPrinter.MyXPrintJobListener;
import com.sun.star.awt.FontWeight;
import com.sun.star.awt.Size;
import com.sun.star.awt.XTextComponent;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.drawing.XShape;
import com.sun.star.form.FormComponentType;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.style.ParagraphAdjust;
import com.sun.star.text.HoriOrientation;
import com.sun.star.text.RelOrientation;
import com.sun.star.text.TextContentAnchorType;
import com.sun.star.text.VertOrientation;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextFrame;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.PrintableState;
import com.sun.star.view.XPrintable;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.event.ICloseEvent;
import ag.ion.bion.officelayer.event.ICloseListener;
import ag.ion.bion.officelayer.event.IEvent;
import ag.ion.bion.officelayer.form.IFormComponent;
import ag.ion.bion.officelayer.form.IFormService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.table.ITextTablePropertyStore;
import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;
import ag.ion.noa.NOAException;
import ag.ion.noa.search.ISearchResult;
import ag.ion.noa.search.SearchDescriptor;
import ag.ion.noa4e.internal.ui.preferences.LocalOfficeApplicationPreferencesPage;
import ag.ion.noa4e.ui.widgets.OfficePanel;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
//import ch.elexis.util.viewers.CommonViewer; //20130414js: use temporary meaningful filenames -  TODO - MaybeNeeded later on. Look for configured_temp_filename below.
//import ch.elexis.views.BriefAuswahl; //20130414js: use temporary meaningful filenames -  TODO - MaybeNeeded later on. Look for configured_temp_filename below.
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Log;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class NOAText implements ITextPlugin {
	public static final String MIMETYPE_OO2 = "application/vnd.oasis.opendocument.text";
	public static LinkedList<NOAText> noas = new LinkedList<NOAText>();
	OfficePanel panel;
	ITextDocument doc;
	ICallback textHandler;
	File myFile;
	private final Log log = Log.get("NOAText");
	IOfficeApplication office;
	private String font;
	private float hi = 0;
	private int stil = -1;
	
	public NOAText(){
		System.out.println("NOAText: NOAText: noa loaded");
		System.out.println("NOAText: NOAText: computing defaultbase...");
		File base = new File(CoreHub.getBasePath());
		File fDef = new File(base.getParentFile().getParent() + "/ooo");
		System.out.println("NOAText: NOAText: Hub.getBasePath():"+CoreHub.getBasePath());
		System.out.println("NOAText: NOAText: base.getParentFile().getParent() + \"/ooo\":"+base.getParentFile().getParent() + "/ooo");
		String defaultbase;
		if (fDef.exists()) {
			defaultbase = fDef.getAbsolutePath();
			CoreHub.localCfg.set(PreferenceConstants.P_OOBASEDIR, defaultbase);
		} else {
			defaultbase = CoreHub.localCfg.get(PreferenceConstants.P_OOBASEDIR, ".");
		}
		System.out.println("NOAText: NOAText: computed defaultbase=openoffice.path.name:"+defaultbase);
		System.setProperty("openoffice.path.name", defaultbase);
	}
	
	/*
	 * We keep track on opened office windows
	 */
	private void createMe(){
		System.out.println("NOAText: createMe");
		
		if (office == null) {
			System.out.println("NOAText: Please note: createMe: office==null");
			office = EditorCorePlugin.getDefault().getManagedLocalOfficeApplication();
		}
		
		if (office == null)	System.out.println("NOAText: createMe: WARNING: still, office==null");
		else 				System.out.println("NOAText: createMe: office="+office.toString());
		
		if (panel == null)	System.out.println("NOAText: createMe: WARNING: panel==null");
		else 				System.out.println("NOAText: createMe: panel="+panel.toString());

		doc = (ITextDocument) panel.getDocument();
		
		if (doc == null)	System.out.println("NOAText: createMe: WARNING: doc==null");
		else 				System.out.println("NOAText: createMe: doc="+doc.toString());
		
		if (doc != null) {
			
			System.out.println("NOAText: createMe: doc.addCloseListener()...");
		
			doc.addCloseListener(new closeListener(office));
			
			System.out.println("NOAText: createMe: noas.add(this)...");
					
			noas.add(this);
		}
		System.out.println("NOAText: createMe ends");
	}
	
	/*
	 * We deactivate the office application as the user closes the last office window
	 */
	private void removeMe(){
		System.out.println("NOAText: removeMe begin");
		try {
			System.out.println("NOAText: removeMe: trying 1...");
				if (textHandler != null) {
				System.out.println("NOAText: removeMe: textHandler.save()...");
				textHandler.save();
				System.out.println("NOAText: removeMe: noas.remove(this)...");;
				noas.remove(this);
				if (doc != null) {
					doc.setModified(false);
					System.out.println("NOAText: removeMe: doc.close()...");
					doc.close();
				}
			}
		} catch (Exception ex) {
			System.out.println("NOAText: removeMe: WARNING: caught Exception");
			ExHandler.handle(ex);
		}
		if (noas.isEmpty()) {
			System.out.println("NOAText: removeMe: noas.isEmpty()");
			try {
				System.out.println("NOAText: removeMe: trying office.deactivate()...");
				office.deactivate();
				log.log("Office deactivated", Log.INFOS);
			} catch (OfficeApplicationException e) {
				System.out.println("NOAText: removeMe: WARNING: caught Exception");
				ExHandler.handle(e);
				log.log("Office deactivation failed", Log.ERRORS);
			}
		}
		System.out.println("NOAText: removeMe end");
	}
	
	public boolean clear(){
		System.out.println("NOAText: clear begin");
		if (textHandler != null) {
			try {
				System.out.println("NOAText: clear: textHandler.save()...");
				textHandler.save();
				System.out.println("NOAText: clear: doc.setModified(false);");
				doc.setModified(false);
				System.out.println("NOAText: clear: return true");
				return true;
			} catch (DocumentException e) {
				System.out.println("NOAText: clear: WARNING: caught Exception");
				ExHandler.handle(e);
			}
		}
		System.out.println("NOAText: clear: return false");
		return false;
	}
	
	/**
	 * Create the OOo-Container that will appear inside the view or dialog for Text-Display. Here we
	 * use a slightly adapted OfficePanel from NOA4e (www.ubion.org)
	 */
	public Composite createContainer(final Composite parent, final ICallback handler){
		System.out.println("NOAText: createContainer(parent, handler) begin");
		
		System.out.println("NOAText: createContainer(): about to new Frame() - WARNING: I CANNOT SEE WHERE THIS IS STORED?");
		System.out.println("NOAText: createContainer(): about to new Frame() - WARNING: DISABLED FOR DEBUGGING.");
		System.out.println("NOAText: createContainer(): about to new Frame() - WARNING: Under Linux: No change, if disabled, and also in EditorCorePlugin.java - see workaround over there.");
		//new Frame();
		
		System.out.println("NOAText: createContainer(): about to panel = new(OfficePanel(parent, SWT.NONE)");
		//Disabling the following statement -> Das Textplugin konnte nicht geladen werden.
		panel = new OfficePanel(parent, SWT.NONE);
		panel.setBuildAlwaysNewFrames(false);
		
        if (panel==null)		{
        						System.out.println("NOAText: createContainer(): WARNING: panel==null");
        						System.out.println("NOAText: createContainer(): WARNING: panel.getFrame() consequently undefined");
        				        }
        else					{
        						System.out.println("NOAText: createContainer(): panel="+panel.toString());
        				        if (panel.getFrame()==null)	System.out.println("NOAText: createContainer(): WARNING: panel.getFrame()==null");
        				        else						System.out.println("NOAText: createContainer(): panel.getFrame()="+panel.getFrame().toString());
        }
        
        System.out.println("NOAText: createContainer(): about to office = EditorCorePlugin.getDefault().getManagedLocalOfficeApplication();");
		office = EditorCorePlugin.getDefault().getManagedLocalOfficeApplication();

		if (office==null)	System.out.println("NOAText: createContainer(): WARNING: office==null");
        else				System.out.println("NOAText: createContainer(): office="+office.toString());
		
		System.out.println("NOAText: createContainer(): about return panel");
		
		return panel;
	}
	
	/**
	 * Create an empty text document. We simply use an empty template and save it immediately into a
	 * temporary file to avoid OOo's complaints when we close the Container or overwrite its
	 * contents.
	 */
	public boolean createEmptyDocument(){
		System.out.println("NOAText: createEmptyDocument() begin");
		try {
			System.out.println("NOAText: createEmptyDocument(): clean()...");
			clean();

			//20130221js: obsolete code from old plugin version: Bundle bundle = Platform.getBundle("ch.elexis.noatext");
			//Also see src/ag/ion/bion/workbench/office/editor/core/EditorCorePlugin.java where this string is also hardcoded.
			System.out.println("NOAText: createEmptyDocument(): about to get bundle=Platfrom.getBundle(\"com.jsigle.noatext_jsl\")");
			Bundle bundle = Platform.getBundle("com.jsigle.noatext_jsl");

			if (bundle==null) 	{	System.out.println("NOAText: createEmptyDocument(): ****************************************************");
									System.out.println("NOAText: createEmptyDocument(): WARNING: bundle==null - probably can't get empty.odt");
									System.out.println("NOAText: createEmptyDocument(): ****************************************************");
									System.out.println("NOAText: createEmptyDocument(): WARNING: THE CORRECT PLUGIN NAME MUST BE USED ABOVE!");
									System.out.println("NOAText: createEmptyDocument(): ****************************************************");
									}
			else System.out.println("NOAText: createEmptyDocument(): bundle=="+bundle.toString());
			
			Path path = new Path("rsc/empty.odt");
			
			System.out.println("NOAText: createEmptyDocument(): Copying internal ressource "+bundle.toString()+" "+path+" to new FileOutputStream...");

			InputStream is = FileLocator.openStream(bundle, path, false);
			FileOutputStream fos = new FileOutputStream(myFile);
			FileTool.copyStreams(is, fos);
			is.close();
			fos.close();

			System.out.println("NOAText: createEmptyDocument(): about to panel.loadDocument(false, myFile.getAbsolutePath(), DocumentDescriptor.DEFAULT)...");

			panel.loadDocument(false, myFile.getAbsolutePath(), DocumentDescriptor.DEFAULT);
			createMe();
			
			System.out.println("NOAText: createEmptyDocument(): SUCCESS: This probably worked out. Returning true.");
			return true;
			/*
			 * doc=(ITextDocument)office.getDocumentService().constructNewDocument(IDocument.WRITER,
			 * DocumentDescriptor.DEFAULT); if(doc!=null){
			 * doc.getPersistenceService().store(myFile.getAbsolutePath()); doc.close();
			 * panel.loadDocument(false, myFile.getAbsolutePath(), DocumentDescriptor.DEFAULT);
			 * doc=(ITextDocument)panel.getDocument(); return true; }
			 */

		} catch (Exception e) {
			System.out.println("NOAText: createEmptyDocument(): WARNING: caught Exception");
			ExHandler.handle(e);
			
		}
		System.out.println("NOAText: createEmptyDocument(): FAIL: Something probably went wrong. Returning false.");
		return false;
	}
	
	/**
	 * Load a file from a byte array. Again, wie store it first into a temporary disk file because
	 * OOo does not like documents that have no representation on disk.
	 */
	public boolean loadFromByteArray(final byte[] bs, final boolean asTemplate){
		System.out.println("NOAText: loadFromByteArray");
		if (bs == null) {
			log.log("Null-Array zum speichern!", Log.ERRORS);
			return false;
		}
		try {
			clean();
			FileOutputStream fout = new FileOutputStream(myFile);
			fout.write(bs);
			fout.close();
			panel.loadDocument(false, myFile.getAbsolutePath(), DocumentDescriptor.DEFAULT);
			createMe();
			return true;
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return false;
		}
	}
	
	/**
	 * Load a file from an input stream. Explanations
	 * 
	 * @see loadFromByteArray()
	 */
	public boolean loadFromStream(final InputStream is, final boolean asTemplate){
		System.out.println("NOAText: loadFromStream");
		try {
			clean();
			doc =
				(ITextDocument) office.getDocumentService().loadDocument(is,
					DocumentDescriptor.DEFAULT_HIDDEN);
			if (doc != null) {
				doc.getPersistenceService().store(myFile.getAbsolutePath());
				doc.close();
				panel.loadDocument(false, myFile.getAbsolutePath(), DocumentDescriptor.DEFAULT);
				createMe();
			}
		} catch (Exception e) {
			ExHandler.handle(e);
			
		}
		return false;
	}
	
	/**
	 * Store the contents of the OOo-Frame into a byte array. We save it into a temporary disk file
	 * first to ensure OOo, that the file ist really saved. That way OOo will not complain about
	 * corrupted or lost files.
	 */
	public byte[] storeToByteArray(){
		System.out.println("NOAText: storeToByteArray");
		if (doc == null) {
			return null;
		}
		try {
			doc.getPersistenceService().store(myFile.getAbsolutePath());
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
			byte[] ret = new byte[(int) myFile.length()];
			int pos = 0, len = 0;
			while (pos + (len = bis.read(ret)) != ret.length) {
				pos += len;
			}
			return ret;
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return null;
		}
	}
	
	/**
	 * Destroy the Panel with the OOo frame
	 */
	public void dispose(){
		System.out.println("NOAText: dispose");
		if (doc != null) {
			doc.close();
			doc = null;
		}
		if (panel != null) {
			panel.dispose();
		}
		
	}
	
	public boolean findOrReplace(final String pattern, final ReplaceCallback cb){
//20121021js 1.4.6 added details to monitoring output		
		System.out.println("NOAText: findOrReplace: pattern: "+pattern);
		System.out.print  ("NOAText: findOrReplace: doc:     ");
		if (doc == null)	{System.out.println("null");}
		else 				{
			System.out.print(doc.toString()+", ");
			try {
				System.out.print(doc.getLocationURL().toString());	//deprecated, but it works and I don't see a similar method. 20121021js
			} catch (DocumentException ex) {
				System.out.print("(LocationURL not available)");
			}
			System.out.println();
		}
		
		SearchDescriptor search = new SearchDescriptor(pattern);
		search.setUseRegularExpression(true);
		if (doc == null) {
//20121021js 1.4.6
//Diese Fehlermeldung ist vermutlich aus der Anfangszeit - der Fehler kann auch bei ganz anderen Dokumenten auftreten.
//Entsprechend neuen Text für die Fehlermeldung formuliert		
//			SWTHelper.showError("No doc in bill", "Fehler:",
//				"Es ist keine Rechnungsvorlage definiert");
// TODO transfer the error message to some external file of error messages!
			System.out.println("NOAText: findOrReplace: FEHLER: findOrReplace doc is null."+pattern);
			System.out.println("NOAText: findOrReplace: TODO: Bitte Fehlermeldung differenzieren und auf passende Stellen im Code verteilen,");
			System.out.println("                        TODO: ggf. mit Dialogen oder eingebautem intelligentem handling.");
			
			SWTHelper.showError("findOrReplace doc is null", "Fehler",
					 "NOAText: findOrReplace:\n\n"+
					 "Das Dokument ist nicht vorhanden oder nicht erreichbar.\n"+
					 "Enthaltene Platzhalter können nicht ersetzt werden.\n\n"+
//					 "Mögliche Ursache: Das Öffnen des Dokuments hat zu lange gedauert.\n"+
//					 "Wenn Sie LibreOffice benutzen und das Dokument viele Felder oder Makros enthält,\n"+  //<- in den Dialog zur Bestätigung des watchdog:destroy() als Hinweis schreiben!
//					 "ist das keine Fehlfunktion. Andernfalls waren vielleicht abgedockte Toolbars etc.\n"+
//					 "in LibreOffice/OpenOffice konfiguriert. Diese Einstellungen werden jetzt geändert.\n"+
//					 "Bitte entschuldigen Sie die damit einhergehende Störung.\n\n"+
					 "Sicherheitshalber sollten Sie das Office-Dokument ggf. als Backup\n"+ //<- lieber auch nach bestätigtem destroy() schreiben, wenn document null ist? Kann Elexis das selbst erledigen?
					 "extern speichern, dann Elexis und LibreOffice/OpenOffice schliessen,\n"+
					 "danach im Windows Task-Manager alle soffice.bin/soffice.exe schliessen,\n"+
					 "welche definitiv zu keinem sichtbaren oder minimierten Fenster gehören.\n\n"+
					 "Anschliessend können Sie Elexis erneut starten. Dadurch arbeiten Sie wieder\n"+
					 "in einer definierten Umgebung, wenn Sie das Dokument erneut öffnen.\n");
				
			return false;
		}
		
		
		// *** START support for replacement of placeholders inside Forms/Controls
		String cWrongNumOfArgs       = "*** Wrong number of arguments: Allowed number of arguments for this type of control: ";
		String cWrongNumOfArgs_2     = " ***";
		IFormService formService = doc.getFormService();
		IFormComponent[] formComponents;
		try {
			formComponents = formService.getFormComponents();
			for (int i = 0; i < formComponents.length; i++){	
				try	{
					IFormComponent formComponent = formComponents[i];
					// *** read control name - this may contain a replacement instruction
					XPropertySet xPSet = formComponent.getXPropertySet();
					int componentType = getFormComponentType(xPSet);
					try {
						String controlName = (String) xPSet.getPropertyValue("Name");
					} catch (UnknownPropertyException e) {
						break; // don't process if this can't be found
					} catch (WrappedTargetException e) {
						break; // don't process if this can't be found
					}
					
					// *** get the replacement specification
					String replacement = (String) xPSet.getPropertyValue("Tag");
					
					// *** do the replacement
					if (cb != null) {
						Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
						Matcher m = p.matcher(replacement);
						StringBuffer sb = new StringBuffer(replacement.length() * 4);
						while (m.find()) {
							int start = m.start();
							int end   = m.end();
							String orig = replacement.substring(start, end);
							Object replace = cb.replace(orig);
							if (replace == null) {
								m.appendReplacement(sb, "??Auswahl??");
							} else if (replace instanceof String) {
								String repl = ((String) replace).replaceAll("\\r", "\n");
								repl = repl.replaceAll("\\n\\n+", "\n");
								m.appendReplacement(sb, repl);
							} else {
								m.appendReplacement(sb, "Not a String");
							}
						}
						m.appendTail(sb);
						replacement = sb.toString();
					}
					
					// *** must save into Tag field because called repeatedly with different replacements
					xPSet.setPropertyValue("Tag", replacement);
					
					// *** split into parts
					String[] replacementParts = replacement.split("@@@");
					String replacement1 = replacementParts[0];
					String replacement2 = replacementParts.length >=2 ? replacementParts[1] : null;
					String replacement3 = replacementParts.length >=3 ? replacementParts[2] : null;
					String replacement4 = replacementParts.length >=4 ? replacementParts[3] : null;
					String replacement5 = replacementParts.length >=5 ? replacementParts[4] : null;
					
					if (StringTool.isNothing(replacement1)) replacement1 = null;
					if (StringTool.isNothing(replacement2)) replacement2 = null;
					if (StringTool.isNothing(replacement3)) replacement3 = null;
					if (StringTool.isNothing(replacement4)) replacement4 = null;
					if (StringTool.isNothing(replacement5)) replacement5 = null;
					
					//test if number of params ok,
					//if error break, show error info in tag field for debugging purposes
					String[] argumentsMapping = {
							FormComponentType.PATTERNFIELD  + ":" + 1,
							FormComponentType.FILECONTROL   + ":" + 1,
							FormComponentType.RADIOBUTTON   + ":" + 1,
							FormComponentType.CHECKBOX      + ":" + 1,
							FormComponentType.COMMANDBUTTON + ":" + 1,
							FormComponentType.FIXEDTEXT     + ":" + 1,
							FormComponentType.GROUPBOX      + ":" + 1,
							FormComponentType.IMAGEBUTTON   + ":" + 1,
							FormComponentType.IMAGECONTROL  + ":" + 1,
							FormComponentType.COMBOBOX      + ":" + 2,
							FormComponentType.LISTBOX       + ":" + 2,
							FormComponentType.DATEFIELD     + ":" + 3,
							FormComponentType.TIMEFIELD     + ":" + 3,
							FormComponentType.NUMERICFIELD  + ":" + 4,
							FormComponentType.SPINBUTTON    + ":" + 4,
							FormComponentType.CURRENCYFIELD + ":" + 4,
							FormComponentType.SCROLLBAR     + ":" + 5
					};
					for (int argi = 0; argi < argumentsMapping.length; argi++)	{
						String argMap = argumentsMapping[argi];
						int argType      = Integer.parseInt(argMap.split(":")[0]);
						int argNumOfArgs = Integer.parseInt(argMap.split(":")[1]);
						if (componentType == argType)	{
							String controlDefaultControl = (String) xPSet.getPropertyValue("DefaultControl");
							if (controlDefaultControl.equalsIgnoreCase("com.sun.star.form.control.FormattedField"))	{
								// *** special case FormattedField which is a text field
								if (replacementParts.length > argNumOfArgs) xPSet.setPropertyValue("Tag", cWrongNumOfArgs + 3 + cWrongNumOfArgs_2);
							} else	{
								// *** "normal" fields
								if (replacementParts.length > argNumOfArgs) xPSet.setPropertyValue("Tag", cWrongNumOfArgs + argNumOfArgs + cWrongNumOfArgs_2);
							}
							break;
						}
					}
					
					// if ComboBox or ListBox, then set list items if specified
					if ((componentType == FormComponentType.COMBOBOX) || (componentType == FormComponentType.LISTBOX))	{
						// *** if delimited by returns (coming from SQL-Select)
						replacement2 = replacement2.replaceAll("\\n", ";");
						if (replacement2 != null) xPSet.setPropertyValue("StringItemList",  replacement2.split(";"));
					}
					
					switch (componentType)	{
						case (FormComponentType.TEXTFIELD):
						case (FormComponentType.COMBOBOX):
						case (FormComponentType.PATTERNFIELD):
						case (FormComponentType.FILECONTROL):
							String controlDefaultControl = (String) xPSet.getPropertyValue("DefaultControl");
							if (controlDefaultControl.equalsIgnoreCase("com.sun.star.form.control.FormattedField"))	{
								// *** FormattedField
								if (isInteger(replacement1)) xPSet.setPropertyValue("EffectiveValue", new Short((short) Integer.parseInt(replacement1)));
								if (isInteger(replacement2)) xPSet.setPropertyValue("EffectiveMin",   new Short((short) Integer.parseInt(replacement2)));
								if (isInteger(replacement3)) xPSet.setPropertyValue("EffectiveMax",   new Short((short) Integer.parseInt(replacement3)));
							} else	{
								// *** simple text field
								XTextComponent xTextComponent = formComponent.getXTextComponent();
								if (replacement1 != null) xTextComponent.setText(replacement1);
							}
							break;
						case (FormComponentType.DATEFIELD):
							TimeTool timeTool = new TimeTool();
							// *** set date
							if (timeTool.set(replacement1))	{
								String yyyymmddDate = timeTool.toString(TimeTool.DATE_COMPACT);
								if (!StringTool.isNothing(yyyymmddDate)) xPSet.setPropertyValue("Date", Integer.parseInt(yyyymmddDate));
							}
							// *** set DateMin
							if (timeTool.set(replacement2))	{
								String yyyymmddDate = timeTool.toString(TimeTool.DATE_COMPACT);
								if (!StringTool.isNothing(yyyymmddDate)) xPSet.setPropertyValue("DateMin", Integer.parseInt(yyyymmddDate));
							}
							// *** set DateMax
							if (timeTool.set(replacement3))	{
								String yyyymmddDate = timeTool.toString(TimeTool.DATE_COMPACT);
								if (!StringTool.isNothing(yyyymmddDate)) xPSet.setPropertyValue("DateMax", Integer.parseInt(yyyymmddDate));
							}
							break;
						case (FormComponentType.TIMEFIELD):
							TimeTool timeTool2 = new TimeTool();
							// *** set time
							if (timeTool2.set(replacement1))	{
								String hhmmssTime = timeTool2.toString(TimeTool.TIME_FULL);
								hhmmssTime = hhmmssTime.replaceAll(":", "") + "00";
								if (!StringTool.isNothing(hhmmssTime)) xPSet.setPropertyValue("Time", Integer.parseInt(hhmmssTime));
							}
							// *** set TimeMin
							if (timeTool2.set(replacement2))	{
								String hhmmssTime = timeTool2.toString(TimeTool.TIME_FULL);
								hhmmssTime = hhmmssTime.replaceAll(":", "") + "00";
								if (!StringTool.isNothing(hhmmssTime))xPSet.setPropertyValue("TimeMin", Integer.parseInt(hhmmssTime));
							}
							// *** set TimeMax
							if (timeTool2.set(replacement3))	{
								String hhmmssTime = timeTool2.toString(TimeTool.TIME_FULL);
								hhmmssTime = hhmmssTime.replaceAll(":", "") + "00";
								if (!StringTool.isNothing(hhmmssTime)) xPSet.setPropertyValue("TimeMax", Integer.parseInt(hhmmssTime));
							}
							break;
						case (FormComponentType.NUMERICFIELD):
						case (FormComponentType.CURRENCYFIELD):
							if (isInteger(replacement1)) xPSet.setPropertyValue("Value",     new Short((short) Integer.parseInt(replacement1)));
							if (isInteger(replacement2)) xPSet.setPropertyValue("ValueMin",  new Short((short) Integer.parseInt(replacement2)));
							if (isInteger(replacement3)) xPSet.setPropertyValue("ValueMax",  new Short((short) Integer.parseInt(replacement3)));
							if (isInteger(replacement4)) xPSet.setPropertyValue("ValueStep", new Short((short) Integer.parseInt(replacement4)));
							break;
						case (FormComponentType.RADIOBUTTON):
						case (FormComponentType.CHECKBOX):
							if (isInteger(replacement1))  xPSet.setPropertyValue("State", new Short((short) Integer.parseInt(replacement1)));
							break;
						case (FormComponentType.COMMANDBUTTON):
						case (FormComponentType.FIXEDTEXT):
						case (FormComponentType.GROUPBOX):
							if (replacement1 != null) xPSet.setPropertyValue("Label", replacement1);
							break;
						case (FormComponentType.LISTBOX):
							// *** if delimited by returns (coming from SQL-Select)
							replacement1 = replacement1.replaceAll("\\n", ";");
							// *** create short[] from replacement1
							String[] splittedArgs = replacement1.split(";");
							short[] shortList = new short[splittedArgs.length];
							for (int argsi = 0; argsi < splittedArgs.length; argsi++)	{
								String argStr = splittedArgs[argsi];
								if (isInteger(argStr))	{
									short arg = (short) Integer.parseInt(argStr);
									shortList[argsi] = arg;
								}
							}
							if (replacement1 != null) xPSet.setPropertyValue("SelectedItems", shortList);
							break;
						case (FormComponentType.SPINBUTTON):
							if (isInteger(replacement3)) xPSet.setPropertyValue("SpinValueMax",  new Short((short) Integer.parseInt(replacement3)));
							if (isInteger(replacement2)) xPSet.setPropertyValue("SpinValueMin",  new Short((short) Integer.parseInt(replacement2)));
							if (isInteger(replacement4)) xPSet.setPropertyValue("SpinIncrement", new Short((short) Integer.parseInt(replacement4)));
							if (isInteger(replacement1)) xPSet.setPropertyValue("SpinValue",     new Short((short) Integer.parseInt(replacement1)));
							break;
						case (FormComponentType.SCROLLBAR):
							if (isInteger(replacement1)) xPSet.setPropertyValue("ScrollValue",    new Short((short) Integer.parseInt(replacement1)));
							if (isInteger(replacement2)) xPSet.setPropertyValue("ScrollValueMin", new Short((short) Integer.parseInt(replacement2)));
							if (isInteger(replacement3)) xPSet.setPropertyValue("ScrollValueMax", new Short((short) Integer.parseInt(replacement3)));
							if (isInteger(replacement4)) xPSet.setPropertyValue("LineIncrement",  new Short((short) Integer.parseInt(replacement4)));
							if (isInteger(replacement5)) xPSet.setPropertyValue("BlockIncrement", new Short((short) Integer.parseInt(replacement5)));
							break;
						case (FormComponentType.IMAGEBUTTON):
						case (FormComponentType.IMAGECONTROL):
							// *** doesn't work correctly... hmmmm... can anyone tell me how to get this to work???
							//     anyway: embedding into doc doesn't work in OO < 3.1
							//     so: more or less useless this way - and waiting for new OO in Elexis
 							if (replacement1 != null) xPSet.setPropertyValue("ImageURL", replacement1);
							break;
					}
				} catch (NOAException e) {
					e.printStackTrace();
				} catch (UnknownPropertyException e) {
					e.printStackTrace();
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (WrappedTargetException e) {
					e.printStackTrace();
				} catch (Exception e)	{
					// *** catch just everything so that the proc is going on...
				}
			}
		} catch (NOAException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			// *** catch just everything so that the proc is going on...
			e1.printStackTrace();
		}
		// *** END support for replacement of placeholders inside Forms/Controls
		
		ISearchResult searchResult = doc.getSearchService().findAll(search);
		if (!searchResult.isEmpty()) {
			ITextRange[] textRanges = searchResult.getTextRanges();
			if (cb != null) {
				for (ITextRange r : textRanges) {
					String orig = r.getXTextRange().getString();
					Object replace = cb.replace(orig);
					if (replace == null) {
						r.setText("??Auswahl??");
					} else if (replace instanceof String) {
						// String repl=((String)replace).replaceAll("\\r\\n[\\r\\n]*", "\n")
						String repl = ((String) replace).replaceAll("\\r", "\n");
						repl = repl.replaceAll("\\n\\n+", "\n");
						r.setText(repl);
					} else if (replace instanceof String[][]) {
						String[][] contents = (String[][]) replace;
						try {
							ITextTable textTable =
								doc.getTextTableService().constructTextTable(contents.length,
									contents[0].length);
							doc.getTextService().getTextContentService().insertTextContent(r,
								textTable);
							r.setText("");
							ITextTablePropertyStore props = textTable.getPropertyStore();
							// long w=props.getWidth();
							// long percent=w/100;
							for (int row = 0; row < contents.length; row++) {
								String[] zeile = contents[row];
								for (int col = 0; col < zeile.length; col++) {
									textTable.getCell(col, row).getTextService().getText().setText(
										zeile[col]);
								}
							}
							textTable.spreadColumnsEvenly();
							
						} catch (Exception ex) {
							ExHandler.handle(ex);
							r.setText("Fehler beim Ersetzen");
						}
						
					} else {
						r.setText("Not a String");
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean isInteger(String input)	{  
		System.out.println("NOAText: isInteger");
		try	{  
			Integer.parseInt(input);
			return true;  
		} catch(Exception e) {
			return false;
			}
		}  
	
	/** retrieves the type of a form component.
	*/
	static public int getFormComponentType(XPropertySet xComponent)	{
		//This won't work here:
		//System.out.println("NOAText: getFormComponentType");
		System.out.println("NOAText: js getFormComponentType begin - log.log() not available here");
		XPropertySetInfo xPSI = null;
	    if (null != xComponent)
	        xPSI = xComponent.getPropertySetInfo();
	    
	    if ((null != xPSI) && xPSI.hasPropertyByName("ClassId")) {
	        // get the ClassId property
	        XPropertySet xCompProps = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xComponent);
			try {
				return (Short)xCompProps.getPropertyValue("ClassId");
			} catch (UnknownPropertyException e) {
				e.printStackTrace();
			} catch (WrappedTargetException e) {
				e.printStackTrace();
			}
	     }
	    System.out.println("NOAText: js getFormComponentType end - log.log() not available here");
		return 0;
		}	
	
	public PageFormat getFormat(){
		System.out.println("NOAText: getFormat");
		return ITextPlugin.PageFormat.USER;
	}
	
	public String getMimeType(){
		System.out.println("NOAText: getMimeType");
		return MIMETYPE_OO2;
	}
	
	/**
	 * Insert a table.
	 * 
	 * @param place
	 *            A string to search for and replace with the table
	 * @param properties
	 *            properties for the table
	 * @param contents
	 *            An Array of String[]s describing each line of the table
	 * @param columnsizes
	 *            int-array describing the relative width of each column (all columns together are
	 *            taken as 100%). May be null, in that case the columns will bhe spread evenly
	 */
	public boolean insertTable(final String place, final int properties, final String[][] contents,
		final int[] columnSizes){
		System.out.println("NOAText: insertTable");
		int offset = 0;
		if ((properties & ITextPlugin.FIRST_ROW_IS_HEADER) == 0) {
			offset = 1;
		}
		SearchDescriptor search = new SearchDescriptor(place);
		search.setIsCaseSensitive(true);
		ISearchResult searchResult = doc.getSearchService().findFirst(search);
		if (!searchResult.isEmpty()) {
			ITextRange r = searchResult.getTextRanges()[0];
			
			try {
				ITextTable textTable =
					doc.getTextTableService().constructTextTable(contents.length + offset,
						contents[0].length);
				doc.getTextService().getTextContentService().insertTextContent(r, textTable);
				r.setText("");
				ITextTablePropertyStore props = textTable.getPropertyStore();
				long w = props.getWidth();
				long percent = w / 100;
				for (int row = 0; row < contents.length; row++) {
					String[] zeile = contents[row];
					for (int col = 0; col < zeile.length; col++) {
						textTable.getCell(col, row + offset).getTextService().getText().setText(
							zeile[col]);
					}
				}
				if (columnSizes == null) {
					textTable.spreadColumnsEvenly();
				} else {
					for (int col = 0; col < contents[0].length; col++) {
						textTable.getColumn(col).setWidth((short) (columnSizes[col] * percent));
					}
					
				}
				
				return true;
			} catch (Exception ex) {
				ExHandler.handle(ex);
			}
		}
		return false;
		
	}
	
	/**
	 * Insert Text and return a cursor describing the position We can not avoid using UNO here,
	 * because NOA does not give us enough control over the text cursor
	 */
	public Object insertText(final String marke, final String text, final int adjust){
		System.out.println("NOAText: insertText, return a cursor");
		SearchDescriptor search = new SearchDescriptor(marke);
		search.setIsCaseSensitive(true);
		ISearchResult searchResult = doc.getSearchService().findFirst(search);
		XText myText = doc.getXTextDocument().getText();
		XTextCursor cur = myText.createTextCursor();
		// ITextCursor cur=doc.getTextService().getCursorService().getTextCursor();
		if (!searchResult.isEmpty()) {
			ITextRange r = searchResult.getTextRanges()[0];
			cur = myText.createTextCursorByRange(r.getXTextRange());
			cur.setString(text);
			try {
				setFormat(cur);
			} catch (Exception e) {
				ExHandler.handle(e);
			}
			
			cur.collapseToEnd();
		}
		return cur;
	}
	
	/**
	 * Insert text at a position returned by insertText(String,text,adjust)
	 */
	public Object insertText(final Object pos, final String text, final int adjust){
		System.out.println("NOAText: insertText at pos returned");
		XTextCursor cur = (XTextCursor) pos;
		if (cur != null) {
			cur.setString(text);
			try {
				setFormat(cur);
			} catch (Exception e) {
				ExHandler.handle(e);
			}
			cur.collapseToEnd();
		}
		return cur;
	}
	
	/**
	 * Insert Text inside a rectangular area. Again we need UNO to get access to a Text frame.
	 */
	public Object insertTextAt(final int x, final int y, final int w, final int h,
		final String text, final int adjust){
		
		System.out.println("NOAText: insertTextAt");
		try {
			XTextDocument myDoc = doc.getXTextDocument();
			com.sun.star.lang.XMultiServiceFactory documentFactory =
				(com.sun.star.lang.XMultiServiceFactory) UnoRuntime.queryInterface(
					com.sun.star.lang.XMultiServiceFactory.class, myDoc);
			
			Object frame = documentFactory.createInstance("com.sun.star.text.TextFrame");
			
			XText docText = myDoc.getText();
			XTextFrame xFrame = (XTextFrame) UnoRuntime.queryInterface(XTextFrame.class, frame);
			
			XShape xWriterShape = (XShape) UnoRuntime.queryInterface(XShape.class, xFrame);
			
			xWriterShape.setSize(new Size(w * 100, h * 100));
			
			XPropertySet xFrameProps =
				(XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xFrame);
			
			// Setting the vertical position
			xFrameProps.setPropertyValue("AnchorPageNo", new Short((short) 1));
			xFrameProps.setPropertyValue("VertOrientRelation", RelOrientation.PAGE_FRAME);
			xFrameProps.setPropertyValue("AnchorType", TextContentAnchorType.AT_PAGE);
			xFrameProps.setPropertyValue("HoriOrient", HoriOrientation.NONE);
			xFrameProps.setPropertyValue("VertOrient", VertOrientation.NONE);
			xFrameProps.setPropertyValue("HoriOrientPosition", x * 100);
			xFrameProps.setPropertyValue("VertOrientPosition", y * 100);
			
			XTextCursor docCursor = docText.createTextCursor();
			docCursor.gotoStart(false);
			// docText.insertControlCharacter(docCursor,ControlCharacter.PARAGRAPH_BREAK,false);
			docText.insertTextContent(docCursor, xFrame, false);
			
			// get the XText from the shape
			
			// XText xShapeText = ( XText ) UnoRuntime.queryInterface( XText.class, writerShape );
			
			XText xFrameText = xFrame.getText();
			XTextCursor xtc = xFrameText.createTextCursor();
			com.sun.star.beans.XPropertySet charProps = setFormat(xtc);
			ParagraphAdjust paradj;
			switch (adjust) {
			case SWT.LEFT:
				paradj = ParagraphAdjust.LEFT;
				break;
			case SWT.RIGHT:
				paradj = ParagraphAdjust.RIGHT;
				break;
			default:
				paradj = ParagraphAdjust.CENTER;
			}
			
			charProps.setPropertyValue("ParaAdjust", paradj);
			xFrameText.insertString(xtc, text, false);
			
			return xtc;
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return false;
		}
		
	}
	
	/**
	 * Print the contents of the panel. NOA does no allow us to select printer and tray, so we do it
	 * with UNO again.
	 */
	public boolean print(final String toPrinter, final String toTray,
		final boolean waitUntilFinished){
		System.out.println("NOAText: print");
		try {
			PropertyValue[] pprops;
			if (StringTool.isNothing(toPrinter)) {
				pprops = new PropertyValue[1];
				pprops[0] = new PropertyValue();
				pprops[0].Name = "Pages";
				pprops[0].Value = "1-";
			} else {
				pprops = new PropertyValue[2];
				pprops[0] = new PropertyValue();
				pprops[0].Name = "Pages";
				pprops[0].Value = "1-";
				pprops[1] = new PropertyValue();
				pprops[1].Name = "Name";
				pprops[1].Value = toPrinter;
			}
			if (!StringTool.isNothing(toTray)) {
				XTextDocument myDoc = doc.getXTextDocument();
				// XTextDocument myDoc=(XTextDocument)
				// UnoRuntime.queryInterface(com.sun.star.text.XTextDocument.class,
				// doc);
				if (!OOPrinter.setPrinterTray(myDoc, toTray)) {
					return false;
				}
			}
			XPrintable xPrintable =
				(XPrintable) UnoRuntime.queryInterface(com.sun.star.view.XPrintable.class, doc
					.getXTextDocument());
			
			com.sun.star.view.XPrintJobBroadcaster selection =
				(com.sun.star.view.XPrintJobBroadcaster) UnoRuntime.queryInterface(
					com.sun.star.view.XPrintJobBroadcaster.class, xPrintable);
			
			MyXPrintJobListener myXPrintJobListener = new MyXPrintJobListener();
			selection.addPrintJobListener(myXPrintJobListener);
			
			// bean.getDocument().print(pprops);
			xPrintable.print(pprops);
			long timeout = System.currentTimeMillis();
			while ((myXPrintJobListener.getStatus() == null)
				|| (myXPrintJobListener.getStatus() == PrintableState.JOB_STARTED)) {
				Thread.sleep(100);
				long to = System.currentTimeMillis();
				if ((to - timeout) > 10000) {
					break;
				}
			}
			
			return true;
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return false;
		}
	}
	
	public void setFocus(){
		System.out.println("NOAText: setFocus stub");
		// TODO Auto-generated method stub
	
	}
	
	public void setFormat(final PageFormat f){
		System.out.println("NOAText: PageFormat stub");
		// TODO Auto-generated method stub
	
	}
	
	public void setSaveOnFocusLost(final boolean bSave){
		System.out.println("NOAText: setSaveOnFocusLost stub");
		// TODO Auto-generated method stub
	
	}
	
	public void showMenu(final boolean b){
		System.out.println("NOAText: showMenu stub");
		// TODO Auto-generated method stub
	
	}
	
	public void showToolbar(final boolean b){
		System.out.println("NOAText: showToolbar stub");
		// TODO Auto-generated method stub
	
	}
	
	public void setInitializationData(final IConfigurationElement config,
		final String propertyName, final Object data) throws CoreException{
		System.out.println("NOAText: setInitializationData stub");
		// TODO Auto-generated method stub
	
	}
	
	/**
	 * basically: ensure that OpenOffice is happy closing the document and create a new temporary
	 * file
	 * 
	 */
	private void clean(){
		System.out.println("NOAText: clean");
		try {
			if (doc != null) {
				doc.getPersistenceService().store(myFile.getAbsolutePath());
				// doc.close();
				myFile.delete();
			}

			//20130414js: use temporary meaningful filenames: I'm adopting the similar mechanism I created for omnivore_js 1.4.4:
			//If we don't have data to construct a meaningful filename, then fall back to the previously used method of creating a meaningless unique temp filename.
			System.out.println("NOAText: clean: constructing temp file name...");
			//TODO: CAVE: Changing the calling conventions for clean() might break compatibility with other text plugins!
			System.out.println("NOAText: TODO: Code to include Fall related info is already here in NOAText.java; but commented out, because NOAText probably knows too little.");
						
			//20130414js: Now, if we have additional data needed to create a meaningful temp filename, do just that.
			StringBuffer configured_temp_filename=new StringBuffer();
			System.out.println("NOAText: clean(): configured_temp_filename="+configured_temp_filename.toString());
			configured_temp_filename.append(LocalOfficeApplicationPreferencesPage.getNOAText_jslTemp_Filename_Element("constant1",""));
			System.out.println("NOAText: clean(): configured_temp_filename="+configured_temp_filename.toString());
			
			//TODO: We might add efficiency to similar code in Omnivore_js DocHandle.java the same way as we do here - read pat once, and only call related routines if pat!=null. 

			//TODO: Please review NOAText.java 1.4.9 addition of patient related data to temporary filename. Make sure that whenever getSelectedPatient() returns not null, the selected patient is valid for the opened/created document.
			//At the moment, I'm not aware that documents could be opened without any selected patient, and the severity of a temp file being named with an unrelated patient id is probably rather low.
			//TODO Most easily, we might simply pass the valid patient or even the configured_temp_filename portion to NOAText from whereever it is called while opening a document.
			Patient pat = ElexisEventDispatcher.getSelectedPatient();
			if (pat != null) {			
				configured_temp_filename.append(LocalOfficeApplicationPreferencesPage.getNOAText_jslTemp_Filename_Element("PID",pat.getKuerzel()));	//getPatient() liefert in etwa: ch.elexis.com@1234567; getPatient().getId() eine DB-ID; getPatient().getKuerzel() die Patientennummer.
				System.out.println("NOAText: clean(): configured_temp_filename="+configured_temp_filename.toString());
				configured_temp_filename.append(LocalOfficeApplicationPreferencesPage.getNOAText_jslTemp_Filename_Element("fn",pat.getName()));
				System.out.println("NOAText: clean(): configured_temp_filename="+configured_temp_filename.toString());
				configured_temp_filename.append(LocalOfficeApplicationPreferencesPage.getNOAText_jslTemp_Filename_Element("gn",pat.getVorname()));
				System.out.println("NOAText: clean(): configured_temp_filename="+configured_temp_filename.toString());
				configured_temp_filename.append(LocalOfficeApplicationPreferencesPage.getNOAText_jslTemp_Filename_Element("dob",pat.getGeburtsdatum()));
				System.out.println("NOAText: clean(): configured_temp_filename="+configured_temp_filename.toString());
			}

			//TODO: Most easily, we might simply pass the valid fall or even the configured_temp_filename portion to NOAText from whereever it is called while opening a document.
			//TODO: CAVE: Changing the calling conventions might break compatibility with other text plugins!
			//Vom aktuell ausgewählten (NEIN - falsche Quelle!) Fall könnte man noch eine ganze Reihe weiterer Felder dem Dateinamen hinzufügen.
			//Ich lasse das aber mal weg, um den Dateinamen einigermassen übersichtlich zu halten.
			//NEIN - falsche Quelle - ERNSTE WARNUNG: Wenn ein schon vorhandenes Dokument zum Öffnen ausgewählt wurde,
			//dann wären diese Informationen auch alle möglicherweise invalid - weil entweder noch kein Fall ausgewählt war,
			//oder weil ein anderer Fall ausgewählt war, als derjenige, zu dem das Dokument wirklich gehört.
			//I.e., man müsste nicht über selectedFall kommen, sondern von der ID des aktuellen Dokuments ausgehend
			//den zu diesem Dokument gehörige Fall erschliessen, und dann dessen Daten verwenden.
			//Oder das müsste bereits im ch.elexis...TextView.java oder ch.elexis..BriefAuswahl.java geschehen.
			//Insofern lasse ich ALLE Informationen zum ausgewählten Fall aus dem temporären Dateinamen WEG.
			//Fall selectedFall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);		
			//if (selectedFall != null) {
			//	configured_temp_filename=configured_temp_filename
			//		+selectedFall.getBeginnDatum()+"_"
			// 		+selectedFall.getBezeichnung()+"_";
			//}
			
			//TODO: Most easily, we might simply pass the valid konsultation or even the configured_temp_filename portion to NOAText from whereever it is called while opening a document.
			//TODO: CAVE: Changing the calling conventions might break compatibility with other text plugins!
			//Von der aktuell ausgewählten (NEIN - falsche Quelle!) Konsultation könnte man noch eine ganze Reihe weiterer Felder dem Dateinamen hinzufügen.
			//Ich lasse das meiste aber mal weg, um den Dateinamen einigermassen übersichtlich zu halten.
			//NEIN - falsche Quelle - ERNSTE WARNUNG: Wenn ein schon vorhandenes Dokument zum Öffnen ausgewählt wurde,
			//dann wären diese Informationen auch alle möglicherweise invalid - weil entweder noch keine Konsultation ausgewählt war,
			//oder weil eine andere Konsultation ausgewählt war, als diejenige, zu der das Dokument  wirklich gehört.
			//I.e., man müsste nicht über selectedKonsultation kommen, sondern von der ID des aktuellen Dokuments ausgehend
			//die zu diesem Dokument gehörige Konsultation erschliessen, und dann deren Daten verwenden.
			//Oder das müsste bereits im ch.elexis...TextView.java oder ch.elexis..BriefAuswahl.java geschehen.
			//Insofern lasse ich ALLE Informationen zur ausgewählten Konsultation aus dem temporären Dateinamen WEG.
			//Konsultation selectedKonsultation = (Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);		
			//if (selectedKonsultation != null) {
			//	configured_temp_filename=configured_temp_filename
			//		+"Kons"+selectedKonsultation.getDatum()+"_"
			//		+"Kons"+selectedKonsultation.getMandant()+"_"
			//		+"Kons"+selectedKonsultation.getAuthor()+"_";
			//}
			
			//TODO: Add the elexis BriefAuswahl document name to the temporary filename.
			//TODO: Most easily, we might simply pass the valid BriefAuswahl document name or even the configured_temp_filename portion to NOAText from whereever it is called while opening a document.
			//TODO: CAVE: Changing the calling conventions might break compatibility with other text plugins!
			System.out.println("NOAText: clean: TODO: Add the elexis Briefauswahl document name to the temporary filename.");
			System.out.println("NOAText: clean: TODO: Please note: This must work for creating a new document, opening an existing one, and renaming one.");
			System.out.println("NOAText: clean: TODO: Also note that documents may be opened from other sources than BriefAuswahl; i.e. Rezepte etc. and cover these ways.");
			System.out.println("NOAText: clean: TODO: The all underlying problem is that NOAText may simply not know the elexis metadata for the document it handles.");
			/*
			//FIXME: WARNING: PLEASE REVIEW: I try to obtain the elexis BriefAuswahl name of the currently opened document.
			//to do that, I must first create an instance of BriefAuswahl, because even when I explicitly designate its ctab property public,
			//I cannot simply access it here - static reference to non-static field is not possible.
			//This relies on the precondition, that actually, only one View BriefAuswahl would ever exist, and any instantiated BriefAuswahl would point to the same content.
			//And that the selection over there is only read, while this document is being opened - i.e. not when another document has been selected afterwards.
			//After all, the code does not cause any pre-compile-time errors; and as the locally created instances are probably removed on exit of this procedure,
			//one most probable unwanted result might simply be that we get a new unrelated, meaningless instance - or none at all -
			//plus some extra db read accesses, and the returned name would either be empty, or something like the first or last (=auto pre-selected) on the list,
			//which would become obvious during the first tests.
			//About as expected - I actually get some nullPointer exception, which would cause the whole opening attempt to fail.
			//Using try catch to fix this, NO component gets added to the temp filename by this code section.
			//Having limited time, I comment it out for now and leave the task of adding the elexis Briefauswahl document name to the temporary filename for later.  
			try {
				BriefAuswahl myBriefAuswahl = new BriefAuswahl();
				if (myBriefAuswahl != null) {
					CTabItem sel = myBriefAuswahl.ctab.getSelection();
					if (sel != null) {
						CommonViewer cv = (CommonViewer) sel.getData();
						Object[] o = cv.getSelection();
						if ((o != null) && (o.length > 0)) {
							Brief brief = (Brief) o[0];
							
							System.out.println("NOAText: clean: TODO: WARNING: PLEASE REVIEW: the way I tried to obtain brief.getLabel() makes certain assumptions that may not be justified.");
							System.out.println("NOAText: clean: brief.getLabel()=<"+brief.getLabel()+">");
							
							configured_temp_filename=configured_temp_filename
									+brief.getLabel()+"_";
						}
					}
				}
			} catch (Throwable throwable) {
				//do nothing
			}
			*/

			
			//FIXME: Code adopted from ominvore_js 1.4.4 PreferencePage.java. Should possibly go into some helper unit and be used from either location (plus maybe others where I might create filenames but remember that only vaguely.)
			
			//Remove all characters that shall not appear in the generated filename
			//Ich verwende kein replaceAll, weil dessen Implementation diverse erforderliche Escapes offenbar nicht erlaubt.
			//Especially, \. is not available to specify a plain dot. (Na ja: \0x2e ginge dann doch - oder sollte gehen.
			//Findet aber nichts. Im interaktiven Suchen/Ersetzen in Eclipse ist \0x2e illegal; \x2e geht eher.
			//In Java Code geht ggf. \056 (octal) . Siehe unten beim automatischen Entfernen von Dateinamen-Resten besonders aus dem docTitle.))

			//configured_temp_filename should not be null, because we allocated an empty StringBuffer above for it.
			//And it MUST not be null, because further below, we set it to filename_clean - so it should probably exist by that point in time.
			//So NO method above should set it to null.
			if ( !configured_temp_filename.toString().isEmpty() )  {
				StringBuffer filename_clean=new StringBuffer();

				for (int n=0;n<configured_temp_filename.length();n++) {
					String c=configured_temp_filename.substring(n,n+1);
					if ((c.codePointAt(0)>=32) && (!LocalOfficeApplicationPreferencesPage.cotf_unwanted_chars.contains(c))) {
						filename_clean.append(c);
					}
				}	

				configured_temp_filename=filename_clean;
			
				System.out.println("NOAText: clean: configured_temp_filename=<"+configured_temp_filename+">");
				System.out.println("NOAText: clean: about to createTempFile(\"noa_\"+configured_temp_filename, \".odt\")...");
				myFile = File.createTempFile(configured_temp_filename.toString().trim(), ".odt");
			}												
			else {
			//MessageDialog.openInformation(Desk.getTopShell(),
			//	Messages.getString("BriefAuswahlNoPatientSelected"),
			//	Messages.getString("BriefAuswahlNoPatientSelected"));		
			
			System.out.println("NOAText: clean: about to createTempFile(\"noa\", \".odt\")...");
			myFile = File.createTempFile("noa", ".odt");
			}
			
			myFile.deleteOnExit();
		} catch (Exception ex) {
			ExHandler.handle(ex);
		}
	}
	
	public boolean setFont(final String name, final int style, final float size){
		System.out.println("NOAText: setFont");
		font = name;
		hi = size;
		stil = style;
		return true;
	}
	
	public boolean setStyle(final int style){
		System.out.println("NOAText: setStyle");
		stil = style;
		return true;
	}
	
	private com.sun.star.beans.XPropertySet setFormat(final XTextCursor xtc)
		throws UnknownPropertyException, PropertyVetoException, IllegalArgumentException,
		WrappedTargetException{
		System.out.println("NOAText: setFormat");
		com.sun.star.beans.XPropertySet charProps =
			(com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(
				com.sun.star.beans.XPropertySet.class, xtc);
		if (font != null) {
			charProps.setPropertyValue("CharFontName", font);
		}
		if (hi > 0)
		{
			charProps.setPropertyValue("CharHeight", new Float(hi));
		}
		if (stil > -1)
		{
			switch (stil) {
			case SWT.MIN:
				charProps.setPropertyValue("CharWeight", 15f /* FontWeight.ULTRALIGHT */);
				break;
			case SWT.NORMAL:
				charProps.setPropertyValue("CharWeight", FontWeight.LIGHT);
				break;
			case SWT.BOLD:
				charProps.setPropertyValue("CharWeight", FontWeight.BOLD);
				break;
			}
		}
		
		return charProps;
	}

	class closeListener implements ICloseListener {
		
		private IOfficeApplication officeAplication = null;
		
		// ----------------------------------------------------------------------------
		/**
		 * Constructs a new SnippetDocumentCloseListener
		 * 
		 * @author Sebastian Rösgen
		 * @date 17.03.2006
		 */
		public closeListener(final IOfficeApplication officeAplication){
			System.out.println("NOAText: closeListener: closeListener");
				this.officeAplication = officeAplication;
		}
		
		// ----------------------------------------------------------------------------
		/**
		 * Is called when someone tries to close a listened object. Not needed in here.
		 * 
		 * @param closeEvent
		 *            close event
		 * @param getsOwnership
		 *            information about the ownership
		 * 
		 * @author Sebastian Rösgen
		 * @date 17.03.2006
		 */
		public void queryClosing(final ICloseEvent closeEvent, final boolean getsOwnership){
			System.out.println("NOAText: closeListener: queryClosing nop");
			// nothing to do in here
		}
		
		// ----------------------------------------------------------------------------
		/**
		 * Is called when the listened object is closed really.
		 * 
		 * @param closeEvent
		 *            close event
		 * 
		 * @author Sebastian Rösgen
		 * @date 17.03.2006
		 */
		public void notifyClosing(final ICloseEvent closeEvent){
			System.out.println("NOAText: closeListener: notifyClosing");
			try {
				removeMe();
			} catch (Exception exception) {
				System.err.println("Error closing office application!");
				exception.printStackTrace();
			}
		}
		
		// ----------------------------------------------------------------------------
		/**
		 * Is called when the broadcaster is about to be disposed.
		 * 
		 * @param event
		 *            source event
		 * 
		 * @author Sebastian Rösgen
		 * @date 17.03.2006
		 */
		public void disposing(final IEvent event){
			System.out.println("NOAText: closeListener: disposing nop");
			// nothing to do in here
		}
		// ----------------------------------------------------------------------------
		
	}

	@Override
	public boolean isDirectOutput() {
		System.out.println("NOAText: isDirectOutput - always returns false");
		return false;
	}

	@Override
	public void setParameter(Parameter parameter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initTemplatePrintSettings(String template) {
		// TODO Auto-generated method stub
		
	}
}
