/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *    
 *******************************************************************************/
package ch.docbox.elexis;

import java.util.Date;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.hl7.v3.ClinicalDocumentType;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040Custodian;
import org.hl7.v3.POCDMT000040InformationRecipient;
import org.hl7.v3.POCDMT000040RecordTarget;

import ch.docbox.cdach.DocboxCDA;
import ch.docbox.model.DocboxContact;
import ch.docbox.ws.cdachservices.CDACHServices;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;
import ch.swissmedicalsuite.HCardBrowser;

public class DocboxTerminvereinbarungAction extends DocboxAction {
	
	private IWorkbenchWindow window;
	private Patient patient;
	private Fall fall;
	private Kontakt kontakt;
	
	protected static Log log = Log.get("DocboxTerminvereinbarungAction"); //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public DocboxTerminvereinbarungAction(){}
	
	/**
	 * The action has been activated. The argument of the method represents the 'real' action
	 * sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action){
		try {
			if (CoreHub.actUser != null) {
				patient = ElexisEventDispatcher.getSelectedPatient();
				kontakt = (Kontakt) ElexisEventDispatcher.getSelected(Kontakt.class);
				if (patient == null) {
					MessageBox box =
						new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_ERROR);
					box.setText(Messages.DocboxTerminvereinbarungAction_NoPatientSelectedText);
					box.setMessage(Messages.DocboxTerminvereinbarungAction_NoPatientSelectedMessage);
					box.open();
					return;
				}
				
				if (kontakt != null) {
					Boolean terminvereinbarung =
						(Boolean) kontakt.getInfoElement("terminvereinbarung");
					if (terminvereinbarung == null || !terminvereinbarung.booleanValue()) {
						MessageBox box =
							new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_ERROR);
						box.setText(Messages.DocboxTerminvereinbarungAction_NoDoctorSelectedText);
						box.setMessage(Messages.DocboxTerminvereinbarungAction_NoDoctorSelectedMessage);
						box.open();
					}
				}
				
				if (!hasValidDocboxCredentials()) {
					return;
				}
				
				Runnable longJob = new Runnable() {
					boolean done = false;
					
					public void run(){
						Thread thread = new Thread(new Runnable() {
							public void run(){
								log.log("job started", Log.DEBUGMSG);
								try {
									makeReferral(kontakt);
								} catch (Exception e) {
									ExHandler.handle(e);
									log.log("excetion in makereferral", Log.DEBUGMSG);
									log.log(e.toString(), Log.DEBUGMSG);
								}
								log.log("job done", Log.DEBUGMSG);
								done = true;
								if (UiDesk.getDisplay().isDisposed())
									return;
								UiDesk.getDisplay().wake();
							}
						});
						log.log("thread starting", Log.DEBUGMSG);
						thread.start();
						while (!done && !UiDesk.getTopShell().isDisposed()) {
							if (!UiDesk.getDisplay().readAndDispatch())
								UiDesk.getDisplay().sleep();
						}
						log.log("thread ended", Log.DEBUGMSG);
						
					}
				};
				BusyIndicator.showWhile(UiDesk.getDisplay(), longJob);
				
				if (UserDocboxPreferences.useHCard()) {
					new HCardBrowser(UserDocboxPreferences.getDocboxLoginID(false),
						UserDocboxPreferences.getDocboxBrowserUrl()).setTerminvereinbarung();
				} else {
					DocboxView docboxView =
						(DocboxView) window.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView("ch.docbox.elexis.DocboxView");
					if (docboxView != null) {
						docboxView.setTerminvereinbarung();
					}
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	private void makeReferral(Kontakt kontakt){
		DocboxCDA docboxCDA = new DocboxCDA();
		System.out.println("Invoking makeReferral...");
		
		try {
			addVersicherung(fall, docboxCDA);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// FIXME phone, phoneBusiness notyet found
		// Patient personaldaten
		Date birthday = null;
		if (!"".equals(patient.getGeburtsdatum())) {
			TimeTool ttBirthday = new TimeTool(patient.getGeburtsdatum());
			birthday = ttBirthday.getTime();
		}
		POCDMT000040RecordTarget recordTarget =
			docboxCDA.getRecordTarget(patient.getPatCode(), null, patient.getAnschrift()
				.getStrasse(), patient.getAnschrift().getPlz(), patient.getAnschrift().getOrt(),
				null, null, patient.getNatel(), patient.getMailAddress(), patient.getVorname(),
				patient.getName(), "w".equals(patient.getGeschlecht()), "m".equals(patient
					.getGeschlecht()), false, birthday);
		
		POCDMT000040Author author =
			docboxCDA.getAuthor(CoreHub.actMandant.get(Person.TITLE),
				CoreHub.actMandant.getVorname(), CoreHub.actMandant.getName(),
				CoreHub.actMandant.getNatel(), null, null, CoreHub.actMandant.getMailAddress(),
				null, null, null);
		POCDMT000040Custodian custodian =
			docboxCDA.getCustodian(null, null, null, null, null, null);
		
		log.log("Invoking addReferral...", Log.DEBUGMSG);
		
		POCDMT000040InformationRecipient informationRecipient = null;
		if (kontakt != null) {
			informationRecipient =
				docboxCDA.getInformationRecipient(null, null, null,
					DocboxContact.getDocboxIdFor(kontakt), null);
		}
		
		ClinicalDocumentType _addReferral_document = new ClinicalDocumentType();
		_addReferral_document.setClinicalDocument(docboxCDA.getClinicalDocument("", recordTarget,
			author, custodian, informationRecipient, docboxCDA.getCodeReferral(), null, null));
		
		log.log("makeReferral ended...", Log.DEBUGMSG);
		
		byte[] _addReferral_attachment = new byte[0];
		javax.xml.ws.Holder<java.lang.Boolean> _addReferral_success =
			new javax.xml.ws.Holder<java.lang.Boolean>();
		javax.xml.ws.Holder<java.lang.String> _addReferral_message =
			new javax.xml.ws.Holder<java.lang.String>();
		javax.xml.ws.Holder<java.lang.String> _addReferral_documentID =
			new javax.xml.ws.Holder<java.lang.String>();
		
		CDACHServices port = UserDocboxPreferences.getPort();
		
		port.addReferral(_addReferral_document, _addReferral_attachment, _addReferral_success,
			_addReferral_message, _addReferral_documentID);
		
		log.log("addReferral._addReferral_success=" + _addReferral_success.value, Log.DEBUGMSG);
		log.log("addReferral._addReferral_message=" + _addReferral_message.value, Log.DEBUGMSG);
		log.log("addReferral._addReferral_documentID=" + _addReferral_documentID.value,
			Log.DEBUGMSG);
	}
	
	private void addVersicherung(Fall fall, DocboxCDA docboxCDA){
		if (fall != null) {
			Kontakt costBearer = fall.getCostBearer();
			if ("UVG".equals(fall.getAbrechnungsSystem())) {
				try {
					docboxCDA.addUnfallversicherung(costBearer.getLabel());
				} catch (Exception e) {}
				try {
					docboxCDA
						.addUnfallversicherungPolicenummer(fall.getRequiredString("Unfallnummer"));
				} catch (Exception e) {}
			}
			if ("KVG".equals(fall.getAbrechnungsSystem())) {
				try {
					docboxCDA.addKrankenkasse(costBearer.getLabel());
				} catch (Exception e) {}
				try {
					docboxCDA
						.addKrankenkassePolicenummer(fall.getRequiredString("Versicherungsnummer"));
				} catch (Exception e) {}
			}
			
		}
	}
	
	/**
	 * Selection in the workbench has been changed. We can change the state of the 'real' action
	 * here if we want, but this can only happen after the delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection){}
	
	/**
	 * We can use this method to dispose of any system resources we previously allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose(){}
	
	/**
	 * We will cache window object in order to be able to provide parent shell for the message
	 * dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window){
		this.window = window;
	}
}
