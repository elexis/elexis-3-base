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

import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.Holder;

import org.apache.commons.lang3.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class DocboxBackgroundJob extends Job {

	private static Logger log = LoggerFactory.getLogger(DocboxBackgroundJob.class);

	public DocboxBackgroundJob() {
		super(Messages.DocboxBackgroundJob_Title);
		this.setUser(false);
		this.setPriority(Job.LONG);
	}

	public synchronized int fetchAppointments(final IProgressMonitor monitor) {
		CDACHServices port = UserDocboxPreferences.getPort();
		int count = 0;
		List<DocboxTermin> docboxTermine = DocboxTermin.getDocboxTermine();
		monitor.worked(20);
		List<AppointmentType> appointments = port.getCalendar();
		if (appointments != null) {
			for (AppointmentType appointment : appointments) {
				DocboxTermin docboxTermin = new DocboxTermin();
				if (appointment.getState() != null && ((appointment.getState().contains("salesrepresentative") //$NON-NLS-1$
						&& UserDocboxPreferences.isAppointmentsPharmaVisits())
						|| (appointment.getState().contains("emergencyservice")
								&& UserDocboxPreferences.isAppointmentsEmergencyService())
						|| (appointment.getState().contains("terminierung") //$NON-NLS-1$
								&& UserDocboxPreferences.isAppointmentsTerminvereinbarung()))) {
					++count;
					docboxTermin.create(appointment, UserDocboxPreferences.getAppointmentsBereich());
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

	public synchronized int updateDoctorDirectory(final IProgressMonitor monitor) {
		CDACHServices port = UserDocboxPreferences.getPort();
		int count = 0;

		String myDocboxId = (String) CoreHub.actMandant.getInfoElement("docboxId");
		if (myDocboxId == null || StringUtils.EMPTY.equals(myDocboxId)) {
			updateDoctorDirectoryByApp(port, count, "self");
		}
		count = updateDoctorDirectoryByApp(port, count, "doctodoc");
		monitor.worked(30);
		count += updateDoctorDirectoryByApp(port, count, "terminvereinbarung");
		monitor.worked(30);
		return count;
	}

	private int updateDoctorDirectoryByApp(CDACHServices port, int count, String application) {
		List<POCDMT000040IntendedRecipient> recipients = port.getRecipients(application);
		if (recipients != null) {
			for (POCDMT000040IntendedRecipient recipient : recipients) {
				String docboxId = StringUtils.EMPTY;
				String ean = StringUtils.EMPTY;
				String given = StringUtils.EMPTY;
				String family = StringUtils.EMPTY;
				String prefix = StringUtils.EMPTY;
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

				String organizationName = StringUtils.EMPTY;
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
				String streetAdressLine = StringUtils.EMPTY;
				String city = StringUtils.EMPTY;
				String plz = StringUtils.EMPTY;

				List<AD> ads = organization.getAddr();
				for (AD ad : ads) {
					List<Serializable> ens = ad.getContent();
					if (ens != null) {
						for (Serializable en : ens) {
							JAXBElement<?> t = (JAXBElement<?>) en;
							if (t.getDeclaredType().getName().equals(AdxpStreetAddressLine.class.getName())) {
								if (!first) {
									streetAdressLine = ((AdxpStreetAddressLine) t.getValue()).content();
									first = true;
								} else {
									String content = ((AdxpStreetAddressLine) t.getValue()).content();
									if (content != null && content.length() > 0) {
										streetAdressLine += content;
									}
								}
							}
							if (t.getDeclaredType().getName().equals(AdxpCity.class.getName())) {
								city = ((AdxpCity) t.getValue()).content();
							}
							if (t.getDeclaredType().getName().equals(AdxpPostalCode.class.getName())) {
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
							p = new Person(family, given, StringUtils.EMPTY, StringUtils.EMPTY);
							new DocboxContact(docboxId, p);
						} else {
							log.warn("newPerson is false, skipping intialization cMatching: " + cMatching
									+ "/ docboxId: " + docboxId);
							continue;
						}

						p.set(Person.NAME, family);
						p.set(Person.FIRSTNAME, given);
						p.set(Person.TITLE, prefix);

						if (!PersistentObject.checkNull(p.get(Person.FLD_IS_USER)).equals(StringConstants.ONE)) {
							p.set(Kontakt.FLD_NAME3, organizationName);
						}
						p.set(Kontakt.FLD_STREET, streetAdressLine);
						p.set(Kontakt.FLD_ZIP, plz);
						p.set(Kontakt.FLD_PLACE, city);

						p.addXid(DOMAIN_EAN, ean, true);
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
	public synchronized int fetchInboxClinicalDocuments(final IProgressMonitor monitor) {
		log.debug("fetchInboxClinicalDocuments");//$NON-NLS-1$
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
							cal.setTimeInMillis(
									documentInfoType.getCreationDate().toGregorianCalendar().getTimeInMillis());
						} else {
							cal = null;
						}
						cdaMessage = new CdaMessage(id, documentInfoType.getTitle(), cal);
						log.debug("creating messseage with id " + id);//$NON-NLS-1$
					} else {
						log.debug("redo download for document id " + id);//$NON-NLS-1$
					}
					Holder<ClinicalDocumentType> clincialDocumentTypeHolder = new Holder<ClinicalDocumentType>();
					Holder<byte[]> attachmentHolder = new Holder<byte[]>();
					port.getClinicalDocument(id, clincialDocumentTypeHolder, attachmentHolder);
					boolean unzipSuccessful = true;
					if (attachmentHolder.value != null) {
						if (!cdaMessage.unzipAttachment(attachmentHolder.value)) {
							log.debug("unzip of attachment failed" + id);//$NON-NLS-1$
							unzipSuccessful = false;
						}
					}
					if (clincialDocumentTypeHolder.value != null && unzipSuccessful) {
						String receivedCDA = docboxCDA
								.marshallIntoString(clincialDocumentTypeHolder.value.getClinicalDocument());
						CdaChXPath cdaChXPath = new CdaChXPath();
						cdaChXPath.setPatientDocument(receivedCDA);
						if (cdaMessage.setCda(receivedCDA)) {
							String firstName = cdaChXPath.getPatientFirstName();
							String lastName = cdaChXPath.getPatientLastName();
							boolean first = !StringTool.isNothing(firstName);
							boolean last = !StringTool.isNothing(firstName);
							String patient = (first ? firstName : StringUtils.EMPTY)
									+ (first && last ? StringUtils.SPACE : StringUtils.EMPTY)// $NON-NLS-1$
									+ (last ? lastName : StringUtils.EMPTY);
							firstName = cdaChXPath.getAuthorFirstName();
							lastName = cdaChXPath.getAuthorLastName();
							String organization = cdaChXPath.getCustodianHospitalName();
							boolean org = !StringTool.isNothing(organization);
							first = !StringTool.isNothing(firstName);
							last = !StringTool.isNothing(lastName);
							String sender = (first ? firstName : StringUtils.EMPTY)
									+ (first && last ? StringUtils.SPACE : StringUtils.EMPTY)// $NON-NLS-1$
									+ (last ? lastName : StringUtils.EMPTY)
									+ (org && (first || last) ? ", " : StringUtils.EMPTY)//$NON-NLS-1$
									+ (org ? organization : StringUtils.EMPTY);
							if (!cdaMessage.setDownloaded(sender, patient)) {
								log.debug("failed to set cda message downloaded with id " + id);//$NON-NLS-1$
								result = false;
							} else {
								ElexisEventDispatcher.update(cdaMessage);
								++count;
							}
						} else {
							log.debug("failed to set cda message downloaded with id " + id);//$NON-NLS-1$
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

	public static void showResultInPopup(final String message) {
		Display display = UiDesk.getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				Display display = UiDesk.getDisplay();
				Shell shell = UiDesk.getTopShell();
				if (shell != null) {
					final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
					tip.setMessage(message);
					Tray tray = display.getSystemTray();
					TrayItem item = null;
					if (tray != null) {
						item = new TrayItem(tray, SWT.NONE);
						item.setImage(Activator.getImageDescriptor("icons/docbox16.png").createImage());//$NON-NLS-1$
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
	protected IStatus run(IProgressMonitor monitor) {
		boolean success = true;
		String msg = StringUtils.EMPTY;
		log.debug("running");
		if (CoreHub.getLoggedInContact() != null) {
			try {
				if (CoreHub.getLoggedInContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {

					log.debug("beginTask");

					monitor.beginTask(Messages.DocboxBackgroundJob_Title, 200);
					monitor.worked(10);

					if (!monitor.isCanceled()) {
						log.debug("fetchInboxClinicalDocuments");
						int downloadedDocuments = fetchInboxClinicalDocuments(monitor);
						if (downloadedDocuments > 0) {
							msg += String.format(Messages.DocboxBackgroundJob_DocumentsDownloaded, downloadedDocuments);
						}
						log.debug(msg);
					}

					if (UserDocboxPreferences.hasAgendaPlugin() && UserDocboxPreferences.downloadAppointments()
							&& !monitor.isCanceled()) {
						log.debug("fetchAppointments");
						int downloadedAppointments = fetchAppointments(monitor);
						if (!StringUtils.EMPTY.equals(msg)) {
							msg += StringUtils.LF;
						}
						msg += String.format(Messages.DocboxBackgroundJob_AppointmentsUpdated, downloadedAppointments);
						log.debug(msg);
					} else {
						monitor.worked(60);
					}

					if (!monitor.isCanceled()) {
						log.debug("updateDoctorDirectory");
						int count = updateDoctorDirectory(monitor);
						if (!StringUtils.EMPTY.equals(msg)) {
							msg += StringUtils.LF;
						}
						msg += String.format(Messages.DocboxBackgroundJob_DoctorDirecotoryUpdated, count);
						log.debug(msg);
					} else {
						monitor.worked(60);
					}
					monitor.worked(10);

					monitor.done();
					if (msg != null && msg.length() > 0) {
						showResultInPopup(msg);
					}

				} else {
				}
			} catch (Exception e) {
				ExHandler.handle(e);
				log.debug("error in task", e);
			}
		}
		log.debug("stopped");
		return (success ? Status.OK_STATUS : Status.CANCEL_STATUS);
	}

}
