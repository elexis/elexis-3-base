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

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.Holder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.hl7.v3.AD;
import org.hl7.v3.AdxpCity;
import org.hl7.v3.AdxpPostalCode;
import org.hl7.v3.AdxpStreetAddressLine;
import org.hl7.v3.ClinicalDocumentType;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.EnPrefix;
import org.hl7.v3.II;
import org.hl7.v3.ON;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040IntendedRecipient;
import org.hl7.v3.POCDMT000040Organization;
import org.hl7.v3.POCDMT000040Person;

import ch.docbox.cdach.CdaChXPath;
import ch.docbox.cdach.DocboxCDA;
import ch.docbox.model.CdaMessage;
import ch.docbox.model.DocboxContact;
import ch.docbox.ws.cdachservices.AppointmentType;
import ch.docbox.ws.cdachservices.CDACHServices;
import ch.docbox.ws.cdachservices.DocumentInfoType;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.elexis.data.Xid;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class DocboxBackgroundJob extends Job {
	
	protected static Log log = Log.get("DocboxBackgroundJob"); //$NON-NLS-1$
	
	public DocboxBackgroundJob(){
		super(Messages.DocboxBackgroundJob_Title);
		this.setUser(false);
		this.setPriority(Job.LONG);
	}
	
	public synchronized int fetchAppointments(final IProgressMonitor monitor){
		CDACHServices port = UserDocboxPreferences.getPort();
		int count = 0;
		List<DocboxTermin> docboxTermine = DocboxTermin.getDocboxTermine();
		monitor.worked(20);
		List<AppointmentType> appointments = port.getCalendar();
		if (appointments != null) {
			for (AppointmentType appointment : appointments) {
				DocboxTermin docboxTermin = new DocboxTermin();
				if (appointment.getState() != null
					&& ((appointment.getState().contains("salesrepresentative") && UserDocboxPreferences.isAppointmentsPharmaVisits()) //$NON-NLS-1$
						|| (appointment.getState().contains("emergencyservice") && UserDocboxPreferences
							.isAppointmentsEmergencyService()) || (appointment.getState().contains(
						"terminierung") && UserDocboxPreferences.isAppointmentsTerminvereinbarung()))) { //$NON-NLS-1$
					++count;
					docboxTermin
						.create(appointment, UserDocboxPreferences.getAppointmentsBereich());
					if (docboxTermine.contains(docboxTermin)) {
						docboxTermine.remove(docboxTermin);
					}
				}
			}
			monitor.worked(20);
			for (DocboxTermin docboxTermin : docboxTermine) {
				docboxTermin.delete();
			}
		}
		monitor.worked(20);
		return count;
	}
	
	public synchronized int updateDoctorDirectory(final IProgressMonitor monitor){
		CDACHServices port = UserDocboxPreferences.getPort();
		int count = 0;
		
		String myDocboxId = (String) CoreHub.actMandant.getInfoElement("docboxId");
		if (myDocboxId == null || "".equals(myDocboxId)) {
			updateDoctorDirectoryByApp(port, count, "self");
		}
		count = updateDoctorDirectoryByApp(port, count, "doctodoc");
		monitor.worked(30);
		count += updateDoctorDirectoryByApp(port, count, "terminvereinbarung");
		monitor.worked(30);
		return count;
	}
	
	private int updateDoctorDirectoryByApp(CDACHServices port, int count, String application){
		List<POCDMT000040IntendedRecipient> recipients = port.getRecipients(application);
		if (recipients != null) {
			for (POCDMT000040IntendedRecipient recipient : recipients) {
				String docboxId = "";
				String ean = "";
				String given = "";
				String family = "";
				String prefix = "";
				List<II> iis = recipient.getId();
				for (II ii : iis) {
					if ("1.3.88".equals(ii.getRoot())) {
						ean = ii.getExtension();
					}
					if (CdaChXPath.getOidUserDocboxId().equals(ii.getRoot())) {
						docboxId = ii.getExtension();
					}
				}
				POCDMT000040Person person = recipient.getInformationRecipient();
				POCDMT000040Organization organization = recipient.getReceivedOrganization();
				
				List<PN> pns = person.getName();
				for (PN pn : pns) {
					List<Serializable> ens = pn.getContent();
					for (Serializable en : ens) {
						if (ens != null) {
							JAXBElement<?> t = (JAXBElement<?>) en;
							if (t.getDeclaredType().getName().equals(EnGiven.class.getName())) {
								given = ((EnGiven) t.getValue()).content();
							}
							if (t.getDeclaredType().getName().equals(EnFamily.class.getName())) {
								family = ((EnFamily) t.getValue()).content();
							}
							if (t.getDeclaredType().getName().equals(EnPrefix.class.getName())) {
								prefix = ((EnPrefix) t.getValue()).content();
								if (prefix != null && prefix.length() > 20) {
									prefix = prefix.substring(0, 19);
								}
							}
						}
					}
				}
				
				String organizationName = "";
				List<ON> ons = organization.getName();
				if (ons != null) {
					for (ON on : ons) {
						if (on != null) {
							List<Serializable> ens = on.getContent();
							if (ens != null) {
								for (Serializable en : ens) {
									organizationName = en.toString();
								}
							}
						}
					}
				}
				
				boolean first = false;
				String streetAdressLine = "";
				String city = "";
				String plz = "";
				
				List<AD> ads = organization.getAddr();
				for (AD ad : ads) {
					List<Serializable> ens = ad.getContent();
					if (ens != null) {
						for (Serializable en : ens) {
							JAXBElement<?> t = (JAXBElement<?>) en;
							if (t.getDeclaredType().getName()
								.equals(AdxpStreetAddressLine.class.getName())) {
								if (!first) {
									streetAdressLine =
										((AdxpStreetAddressLine) t.getValue()).content();
									first = true;
								} else {
									String content =
										((AdxpStreetAddressLine) t.getValue()).content();
									if (content != null && content.length() > 0) {
										streetAdressLine += content;
									}
								}
							}
							if (t.getDeclaredType().getName().equals(AdxpCity.class.getName())) {
								city = ((AdxpCity) t.getValue()).content();
							}
							if (t.getDeclaredType().getName()
								.equals(AdxpPostalCode.class.getName())) {
								plz = ((AdxpPostalCode) t.getValue()).content();
							}
						}
					}
				}
				
				if (docboxId.length() > 0) {
					++count;
					Person p = null;
					Kontakt cMatching = DocboxContact.findContactForDocboxId(docboxId);
					if (cMatching != null) {
						p = (Person) cMatching;
					}
					if (p == null && "self".equals(application)) {
						p = (Person) ElexisEventDispatcher.getSelected(Mandant.class);
						new DocboxContact(docboxId, p);
					}
					if (p == null || UserDocboxPreferences.isDocboxTest()) {
						boolean newPerson = p == null;
						
						if (newPerson) {
							p = new Person(family, given, "", "");
							new DocboxContact(docboxId, p);
						}
						
						p.set(Person.NAME, family);
						p.set(Person.FIRSTNAME, given);
						p.set(Person.TITLE, prefix);
						
						if (!PersistentObject.checkNull(p.get(Person.FLD_IS_USER)).equals(
							StringConstants.ONE)) {
							p.set(Kontakt.FLD_NAME3, organizationName);
						}
						p.set(Kontakt.FLD_STREET, streetAdressLine);
						p.set(Kontakt.FLD_ZIP, plz);
						p.set(Kontakt.FLD_PLACE, city);
						
						p.addXid(Xid.DOMAIN_EAN, ean, true);
					}
					p.setInfoElement(application, true);
				}
			}
		}
		return count;
	}
	
	/**
	 * 
	 * @return true if everything successful, false if a warning or error occurred
	 */
	public synchronized int fetchInboxClinicalDocuments(final IProgressMonitor monitor){
		log.log("fetchInboxClinicalDocuments", Log.DEBUGMSG);//$NON-NLS-1$
		boolean result = true;
		int count = 0;
		CDACHServices port = UserDocboxPreferences.getPort();
		List<DocumentInfoType> documentInfoTypes = port.getInboxClinicalDocuments(null);
		int maxworked = 60;
		int worked = 0;
		if (documentInfoTypes != null) {
			DocboxCDA docboxCDA = new DocboxCDA();
			for (int j = 0; j < documentInfoTypes.size(); ++j) {
				DocumentInfoType documentInfoType = documentInfoTypes.get(j);
				String id = documentInfoType.getDocumentID();
				CdaMessage cdaMessage = CdaMessage.getCdaMessageEvenIfDocsDeleted(id);
				if (cdaMessage == null || (!cdaMessage.isDownloaded() && !cdaMessage.isDeleted())) {
					if (cdaMessage == null) {
						GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
						if (documentInfoType.getCreationDate() != null) {
							cal.setTimeInMillis(documentInfoType.getCreationDate()
								.toGregorianCalendar().getTimeInMillis());
						} else {
							cal = null;
						}
						cdaMessage = new CdaMessage(id, documentInfoType.getTitle(), cal);
						log.log("creating messseage with id " + id,//$NON-NLS-1$
							Log.DEBUGMSG);
					} else {
						log.log("redo download for document id " + id,//$NON-NLS-1$
							Log.DEBUGMSG);
					}
					Holder<ClinicalDocumentType> clincialDocumentTypeHolder =
						new Holder<ClinicalDocumentType>();
					Holder<byte[]> attachmentHolder = new Holder<byte[]>();
					port.getClinicalDocument(id, clincialDocumentTypeHolder, attachmentHolder);
					boolean unzipSuccessful = true;
					if (attachmentHolder.value != null) {
						if (!cdaMessage.unzipAttachment(attachmentHolder.value)) {
							log.log("unzip of attachment failed" + id,//$NON-NLS-1$
								Log.DEBUGMSG);
							unzipSuccessful = false;
						}
					}
					if (clincialDocumentTypeHolder.value != null && unzipSuccessful) {
						String receivedCDA =
							docboxCDA.marshallIntoString(clincialDocumentTypeHolder.value
								.getClinicalDocument());
						CdaChXPath cdaChXPath = new CdaChXPath();
						cdaChXPath.setPatientDocument(receivedCDA);
						if (cdaMessage.setCda(receivedCDA)) {
							String firstName = cdaChXPath.getPatientFirstName();
							String lastName = cdaChXPath.getPatientLastName();
							boolean first = !StringTool.isNothing(firstName);
							boolean last = !StringTool.isNothing(firstName);
							String patient = (first ? firstName : "") //$NON-NLS-1$
								+ (first && last ? " " : "")//$NON-NLS-1$ //$NON-NLS-2$
								+ (last ? lastName : "");//$NON-NLS-1$
							firstName = cdaChXPath.getAuthorFirstName();
							lastName = cdaChXPath.getAuthorLastName();
							String organization = cdaChXPath.getCustodianHospitalName();
							boolean org = !StringTool.isNothing(organization);
							first = !StringTool.isNothing(firstName);
							last = !StringTool.isNothing(lastName);
							String sender = (first ? firstName : "")//$NON-NLS-1$
								+ (first && last ? " " : "")//$NON-NLS-1$ //$NON-NLS-2$
								+ (last ? lastName : "")//$NON-NLS-1$
								+ (org && (first || last) ? ", " : "")//$NON-NLS-1$ //$NON-NLS-2$
								+ (org ? organization : "");//$NON-NLS-1$
							if (!cdaMessage.setDownloaded(sender, patient)) {
								log.log("failed to set cda message downloaded with id "//$NON-NLS-1$
									+ id, Log.DEBUGMSG);
								result = false;
							} else {
								ElexisEventDispatcher.update(cdaMessage);
								++count;
							}
						} else {
							log.log("failed to set cda message downloaded with id "//$NON-NLS-1$
								+ id, Log.DEBUGMSG);
							result = false;
						}
					}
				}
				double tmpWorked = (maxworked * (j + 1)) / documentInfoTypes.size();
				int newWorked = (int) tmpWorked - worked;
				if (newWorked > 0) {
					if (newWorked > 0) {
						monitor.worked(newWorked);
						worked += newWorked;
					}
				}
			}
		}
		ElexisEventDispatcher.reload(CdaMessage.class);
		
		int newWorked = maxworked - worked;
		if (newWorked > 0) {
			monitor.worked(newWorked);
		}
		return result ? count : -1;
	}
	
	public static void showResultInPopup(final String message){
		Display display = UiDesk.getDisplay();
		display.asyncExec(new Runnable() {
			public void run(){
				Display display = UiDesk.getDisplay();
				Shell shell = UiDesk.getTopShell();
				if (shell != null) {
					final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
					tip.setMessage(message);
					Tray tray = display.getSystemTray();
					TrayItem item = null;
					if (tray != null) {
						item = new TrayItem(tray, SWT.NONE);
						item.setImage(Activator
							.getImageDescriptor("icons/docbox16.png").createImage());//$NON-NLS-1$
						tip.setText("docbox");//$NON-NLS-1$
						item.setToolTip(tip);
					} else {
						tip.setText("docbox");//$NON-NLS-1$
						tip.setLocation(400, 400);
					}
					tip.setVisible(true);
					try {
						// delay for five seconds
						Thread.sleep(5000L);
					} catch (InterruptedException e) {
						ExHandler.handle(e);
					}
					tip.setVisible(false);
					tip.dispose();
					if (item != null) {
						item.setVisible(false);
						item.dispose();
					}
				}
			}
		});
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor){
		boolean success = true;
		String msg = ""; //$NON-NLS-1$
		log.log("running", Log.DEBUGMSG);
		if (CoreHub.actUser != null) {
			try {
				if (CoreHub.actUser != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
					
					log.log("beginTask", Log.DEBUGMSG);
					
					monitor.beginTask(Messages.DocboxBackgroundJob_Title, 200);
					monitor.worked(10);
					
					if (!monitor.isCanceled()) {
						log.log("fetchInboxClinicalDocuments", Log.DEBUGMSG);
						int downloadedDocuments = fetchInboxClinicalDocuments(monitor);
						if (downloadedDocuments > 0) {
							msg +=
								String.format(Messages.DocboxBackgroundJob_DocumentsDownloaded,
									downloadedDocuments);
						}
						log.log(msg, Log.DEBUGMSG);
					}
					
					if (UserDocboxPreferences.hasAgendaPlugin()
						&& UserDocboxPreferences.downloadAppointments() && !monitor.isCanceled()) {
						log.log("fetchAppointments", Log.DEBUGMSG);
						int downloadedAppointments = fetchAppointments(monitor);
						if (!"".equals(msg)) { //$NON-NLS-1$
							msg += "\n"; //$NON-NLS-1$
						}
						msg +=
							String.format(Messages.DocboxBackgroundJob_AppointmentsUpdated,
								downloadedAppointments);
						log.log(msg, Log.DEBUGMSG);
					} else {
						monitor.worked(60);
					}
					
					if (!monitor.isCanceled()) {
						log.log("updateDoctorDirectory", Log.DEBUGMSG);
						int count = updateDoctorDirectory(monitor);
						if (!"".equals(msg)) { //$NON-NLS-1$
							msg += "\n"; //$NON-NLS-1$
						}
						msg +=
							String.format(Messages.DocboxBackgroundJob_DoctorDirecotoryUpdated,
								count);
						log.log(msg, Log.DEBUGMSG);
					} else {
						monitor.worked(60);
					}
					monitor.worked(10);
					
					monitor.done();
					if (msg != null && msg.length() > 0) {
						showResultInPopup(msg);
					}
					
				} else {}
			} catch (Exception e) {
				ExHandler.handle(e);
				log.log(e, "error in task", Log.DEBUGMSG);
			}
		}
		log.log("stopped", Log.DEBUGMSG);
		return (success ? Status.OK_STATUS : Status.CANCEL_STATUS);
	}
	
}
