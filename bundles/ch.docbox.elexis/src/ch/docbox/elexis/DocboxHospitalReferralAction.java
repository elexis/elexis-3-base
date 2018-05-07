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
import java.util.List;

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
import org.hl7.v3.POCDMT000040RecordTarget;

import ch.docbox.cdach.DocboxCDA;
import ch.docbox.ws.cdachservices.CDACHServices;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.FallConstants;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.text.model.Samdas;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.swissmedicalsuite.HCardBrowser;

/**
 * Implements the action to refer a patient to the hospital in docbox
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class DocboxHospitalReferralAction extends DocboxAction {
	private IWorkbenchWindow window;
	private Patient patient;
	private Fall fall;
	public boolean meinPatient = false;
	
	protected static Log log = Log.get("DocboxHospitalReferralAction"); //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public DocboxHospitalReferralAction(){}
	
	private LabResult getLatestLabResult(String name, List<LabResult> list){
		TimeTool ttLabResult = new TimeTool();
		LabResult labResult = null;
		if (list != null && name != null) {
			for (LabResult result : list) {
				String label = result.getItem().getName();
				if (name.equals(label)) {
					if (labResult == null) {
						labResult = result;
						ttLabResult.setDate(labResult.getDate());
					} else {
						if (result.getDate() != null) {
							TimeTool ttResult = new TimeTool();
							ttResult.setDate(result.getDate());
							if (ttResult.isAfter(ttLabResult)) {
								ttLabResult = ttResult;
								labResult = result;
							}
						}
					}
				}
			}
		}
		return labResult;
	}
	
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
				if (patient == null) {
					MessageBox box =
						new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_ERROR);
					box.setText(Messages.DocboxHospitalReferralAction_NoPatientSelectedText);
					box.setMessage(Messages.DocboxHospitalReferralAction_NoPatientSelectedMessage);
					box.open();
					return;
				}
				fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
				Konsultation konsultation =
					(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
				if (fall == null) {
					if (konsultation == null || konsultation.getFall() != fall) {
						konsultation = patient.getLetzteKons(false);
					}
					if (konsultation != null) {
						fall = konsultation.getFall();
					}
				}
				if (konsultation == null && fall != null) {
					konsultation = fall.getLetzteBehandlung();
				}
				final Konsultation kons = konsultation;
				
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
									makeReferral(kons);
								} catch (Exception e) {
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
						// while (!done && (UiDesk.getTopShell()!=null &&
						// !UiDesk.getTopShell().isDisposed())) {
						// if (!UiDesk.getDisplay().readAndDispatch()) {
						// UiDesk.getDisplay().sleep();
						// }
						// }
						while (!done) {
							if (!UiDesk.getDisplay().readAndDispatch())
								UiDesk.getDisplay().sleep();
						}
						log.log("thread ended", Log.DEBUGMSG);
						
					}
				};
				BusyIndicator.showWhile(UiDesk.getDisplay(), longJob);
				
				if (UserDocboxPreferences.useHCard()) {
					HCardBrowser hCardBrowser =
						new HCardBrowser(UserDocboxPreferences.getDocboxLoginID(false),
							UserDocboxPreferences.getDocboxBrowserUrl());
					if (meinPatient) {
						hCardBrowser.setMyPatient();
					} else {
						hCardBrowser.setHospitalReferral();
					}
				} else {
					DocboxView docboxView =
						(DocboxView) window.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView("ch.docbox.elexis.DocboxView");
					if (docboxView != null) {
						if (meinPatient) {
							docboxView.setMyPatient();
						} else {
							docboxView.setHospitalReferral();
						}
					}
				}
			}
		} catch (PartInitException e) {
			log.log(e, "runaction", Log.DEBUGMSG);
		}
	}
	
	private void makeReferral(Konsultation konsultation){
		DocboxCDA docboxCDA = new DocboxCDA();
		log.log("invoking makeReferral", Log.DEBUGMSG);
		
		try {
			log.log("addMedikamente", Log.DEBUGMSG);
			addMedikamente(patient, docboxCDA);
			log.log("addAllergien", Log.DEBUGMSG);
			addAllergien(patient, docboxCDA);
			log.log("addDiagnose", Log.DEBUGMSG);
			addDiagnose(patient, docboxCDA);
			log.log("addAnamnese", Log.DEBUGMSG);
			addAnamnese(konsultation, docboxCDA);
			log.log("addPersoenlicheAnamnese", Log.DEBUGMSG);
			addPersoenlicheAnamnese(patient, docboxCDA);
			log.log("addEinweisungsgrund", Log.DEBUGMSG);
			addEinweisungsgrund(fall, docboxCDA);
			log.log("addVersicherung", Log.DEBUGMSG);
			addVersicherung(fall, docboxCDA);
			log.log("addLabordaten", Log.DEBUGMSG);
			addLaborDaten(patient, docboxCDA);
			log.log("addBemerkungen", Log.DEBUGMSG);
			addBemerkungen(patient, docboxCDA);
		} catch (Exception e) {
			log.log(e, "makereferral", Log.DEBUGMSG);
		}
		
		Date birthday = null;
		if (!"".equals(patient.getGeburtsdatum())) {
			TimeTool ttBirthday = new TimeTool(patient.getGeburtsdatum());
			birthday = ttBirthday.getTime();
		}
		
		String phone = patient.get(Patient.FLD_PHONE1);
		String phone2 = patient.get(Patient.FLD_PHONE2);
		
		POCDMT000040RecordTarget recordTarget =
			docboxCDA.getRecordTarget(patient.getPatCode(), null, patient.getAnschrift()
				.getStrasse(), patient.getAnschrift().getPlz(), patient.getAnschrift().getOrt(),
				phone, phone2, patient.getNatel(), patient.getMailAddress(), patient.getVorname(),
				patient.getName(), "w".equals(patient.getGeschlecht()), "m".equals(patient
					.getGeschlecht()), false, birthday);
		
		POCDMT000040Author author =
			docboxCDA.getAuthor(CoreHub.actMandant.get(Person.TITLE),
				CoreHub.actMandant.getVorname(), CoreHub.actMandant.getName(),
				CoreHub.actMandant.getNatel(), null, null, CoreHub.actMandant.getMailAddress(),
				null, null, null);
		POCDMT000040Custodian custodian =
			docboxCDA.getCustodian(null, null, null, null, null, null);
		
		log.log(
			"Invoking addReferral for patient " + patient.getVorname() + " " + patient.getName(),
			Log.DEBUGMSG);
		
		ClinicalDocumentType _addReferral_document = new ClinicalDocumentType();
		_addReferral_document.setClinicalDocument(docboxCDA.getClinicalDocument("", recordTarget,
			author, custodian, null, docboxCDA.getCodeReferral(), null, null));
		
		log.log(docboxCDA.marshallIntoString(_addReferral_document.getClinicalDocument()),
			Log.DEBUGMSG);
		
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
		
		log.log("makeReferral ended...", Log.DEBUGMSG);
		
		log.log("addReferral._addReferral_success=" + _addReferral_success.value, Log.DEBUGMSG);
		log.log("addReferral._addReferral_message=" + _addReferral_message.value, Log.DEBUGMSG);
		log.log("addReferral._addReferral_documentID=" + _addReferral_documentID.value,
			Log.DEBUGMSG);
	}
	
	/**
	 * @param patient
	 * @param docboxCDA
	 */
	private void addLaborDaten(Patient patient, DocboxCDA docboxCDA){
		try {
			Query<LabResult> qbe = new Query<LabResult>(LabResult.class);
			qbe.add("PatientID", "=", patient.getId());
			List<LabResult> list = qbe.execute();
			LabResult result = getLatestLabResult("Thrombozyten", list);
			if (result != null) {
				docboxCDA.addThrombozyten(result.getResult());
			}
			result = getLatestLabResult("Kreatinin", list);
			if (result != null) {
				docboxCDA.addKreatininwert(result.getResult());
			}
		} catch (Exception e) {
			log.log(e, "addLaborDaten", Log.DEBUGMSG);
			ExHandler.handle(e);
		}
	}
	
	private void addVersicherung(Fall fall, DocboxCDA docboxCDA){
		if (fall != null) {
			Kontakt costBearer = fall.getCostBearer();
			if ("UVG".equals(fall.getAbrechnungsSystem())) {
				try {
					if (costBearer != null) {
						docboxCDA.addUnfallversicherung(costBearer.getLabel());
					}
				} catch (Exception e) {
					log.log(e, "addUnfallversicherung", Log.DEBUGMSG);
					ExHandler.handle(e);
				}
				try {
					docboxCDA
						.addUnfallversicherungPolicenummer(fall.getRequiredString("Unfallnummer"));
				} catch (Exception e) {
					log.log(e, "Unfallnummer", Log.DEBUGMSG);
					ExHandler.handle(e);
				}
			}
			if ("KVG".equals(fall.getAbrechnungsSystem())) {
				try {
					if (costBearer != null) {
						docboxCDA.addKrankenkasse(costBearer.getLabel());
					}
				} catch (Exception e) {
					log.log(e, "addKrankenkasse", Log.DEBUGMSG);
					ExHandler.handle(e);
				}
				try {
					docboxCDA
						.addKrankenkassePolicenummer(fall.getRequiredString("Versicherungsnummer"));
				} catch (Exception e) {
					log.log(e, "addKrankenkassePolicenummer", Log.DEBUGMSG);
					ExHandler.handle(e);
				}
			}
			
		}
	}
	
	/**
	 * Einweisungsgrund: Kein Mapping für Prävention/Geburtsgebrechen
	 * 
	 * @param fall
	 * @param docboxCDA
	 */
	private void addEinweisungsgrund(Fall fall, DocboxCDA docboxCDA){
		if (fall != null) {
			if (FallConstants.TYPE_DISEASE.equals(fall.getGrund())) {
				docboxCDA.addEinweisungsgrund("Krankheit");
			} else if (FallConstants.TYPE_ACCIDENT.equals(fall.getGrund())) {
				docboxCDA.addEinweisungsgrund("Unfall");
			} else if (FallConstants.TYPE_MATERNITY.equals(fall.getGrund())) {
				docboxCDA.addEinweisungsgrund("Mutterschaft");
			} else if (FallConstants.TYPE_OTHER.equals(fall.getGrund())) {
				docboxCDA.addEinweisungsgrund("Anderer");
			}
		}
	}
	
	private void addAnamnese(Konsultation konsultation, DocboxCDA docboxCDA){
		if (konsultation != null && konsultation.getEintrag() != null) {
			String anamnese = konsultation.getEintrag().getHead();
			Samdas samdas = new Samdas(anamnese);
			anamnese = samdas.getRecordText();
			
			if (!StringTool.isNothing(anamnese)) {
				docboxCDA.addAnamnese(anamnese);
			}
		}
	}
	
	private void addPersoenlicheAnamnese(Patient patient, DocboxCDA docboxCDA){
		String anamnese = patient.getPersAnamnese();
		if (!StringTool.isNothing(anamnese)) {
			docboxCDA.addPerseoenlicheAnamnese(anamnese);
		}
	}
	
	private void addDiagnose(Patient patient, DocboxCDA docboxCDA){
		String diagnose = patient.getDiagnosen();
		if (!StringTool.isNothing(diagnose)) {
			docboxCDA.addDiagnose(diagnose);
		}
	}
	
	private void addBemerkungen(Patient patient, DocboxCDA docboxCDA){
		String bemerkung = patient.getBemerkung();
		if (!StringTool.isNothing(bemerkung)) {
			docboxCDA.addErgaenzungenLeistung(bemerkung);
		}
	}
	
	private void addAllergien(Patient patient, DocboxCDA docboxCDA){
		String risks = patient.get("Allergien");
		if (!StringTool.isNothing(risks)) {
			docboxCDA.addAllergien(true, risks);
		}
	}
	
	/**
	 * docbox Klinischen Angaben - Medikamente
	 * 
	 * @param patient
	 * @param docboxCDA
	 */
	private void addMedikamente(Patient patient, DocboxCDA docboxCDA){
		List<Prescription> prescriptions = patient.getMedication(EntryType.FIXED_MEDICATION);
		if (prescriptions != null && !prescriptions.isEmpty()) {
			String[] medikamente = new String[prescriptions.size()];
			for (int i = 0; i < prescriptions.size(); ++i) {
				medikamente[i] = prescriptions.get(i).getLabel();
			}
			docboxCDA.addMedikamente(medikamente);
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