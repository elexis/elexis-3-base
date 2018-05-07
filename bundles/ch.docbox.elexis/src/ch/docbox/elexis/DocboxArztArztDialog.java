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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.hl7.v3.ClinicalDocumentType;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040Custodian;
import org.hl7.v3.POCDMT000040InformationRecipient;
import org.hl7.v3.POCDMT000040RecordTarget;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import ch.docbox.cdach.DocboxCDA;
import ch.docbox.model.DocboxContact;
import ch.docbox.ws.cdachservices.CDACHServices;
import ch.docbox.ws.cdachservices.CDACHServices_Service;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory;
import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;

public class DocboxArztArztDialog extends TitleAreaDialog {
	
	private Patient patient;
	private Kontakt kontakt;
	private DocboxCDA docboxCDA = new DocboxCDA();
	
	private Text textTitle, textMessage;
	private ArrayList<Kontakt> kontakte;
	private Combo comboDoctor;
	private org.eclipse.swt.widgets.List listAttachments;
	
	public DocboxArztArztDialog(Patient patient, Kontakt kontakt){
		super(UiDesk.getTopShell());
		this.patient = patient;
		this.kontakt = kontakt;
		this.kontakte = new ArrayList<Kontakt>();
	}
	
	@Override
	protected Control createDialogArea(final Composite parent){
		Composite com = new Composite(parent, SWT.NONE);
		com.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		com.setLayout(new GridLayout(2, false));
		
		Label label;
		
		label = new Label(com, SWT.NONE);
		label.setText(Messages.DocboxArztArztDialog_TextPatient);
		
		label = new Label(com, SWT.NONE);
		label.setText(patient.getLabel());
		
		label = new Label(com, SWT.NONE);
		label.setText(Messages.DocboxArztArztDialog_TextDoctor);
		comboDoctor = new Combo(com, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		
		Query<Kontakt> qbe = new Query<Kontakt>(Kontakt.class);
		List<Kontakt> list = qbe.execute();
		for (Kontakt k : list) {
			Boolean isDoc2Doc = (Boolean) k.getInfoElement("doctodoc");
			if (isDoc2Doc != null && isDoc2Doc.booleanValue()) {
				comboDoctor.add(k.getLabel());
				kontakte.add(k);
			}
		}
		
		if (kontakt != null) {
			comboDoctor.setText(kontakt.getLabel());
		}
		
		label = new Label(com, SWT.NONE);
		label.setText(Messages.DocboxArztArztDialog_TextTitle);
		
		textTitle = new Text(com, SWT.BORDER);
		textTitle.setText("");
		textTitle.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		Label l2 = new Label(com, SWT.NONE);
		l2.setText(Messages.DocboxArztArztDialog_TextMessage);
		
		textMessage = new Text(com, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		textMessage.setText("\n\n\n\n\n");
		textMessage.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		label = new Label(com, SWT.NONE);
		label.setText(Messages.DocboxArztArztDialog_TextAttachments);
		
		listAttachments = new org.eclipse.swt.widgets.List(com, SWT.SINGLE | SWT.V_SCROLL);
		listAttachments.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		listAttachments.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e){
				if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
					if (listAttachments.isFocusControl()) {
						int index = listAttachments.getSelectionIndex();
						if (index != 0) {
							listAttachments.remove(index);
						}
					}
				}
			}
		});
		
		// dummy emtpy label for button
		label = new Label(com, SWT.NONE);
		
		Button addAttachments = new Button(com, SWT.PUSH);
		addAttachments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0){
				FileDialog fd = new FileDialog(UiDesk.getTopShell(), SWT.MULTI);
				fd.setText("Öffnen");
				String[] filterExt =
					{
						"*.*", "*.doc", "*.docx", "*.doc", "*.gif", "*.jpeg", "*.jpg", "*.mov",
						"*.mp3", "*.mpeg", "*.pdf", "*.png", "*.ppt", "*.tiff", "*.txt", "*.xls",
						"*.xlsx", "*.zip", "*.odt", "*.html", "*.htm", "*.rtf", "*.oft", "*.pptx",
						"*.pps", "*.rpt", "*.vcf", "*.xml", "*.csv"
					};
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				if (selected != null) {
					// Append all the selected files. Since getFileNames() returns only
					// the names, and not the path, prepend the path, normalizing
					// if necessary
					String[] files = fd.getFileNames();
					for (int i = 0, n = files.length; i < n; i++) {
						StringBuffer buf = new StringBuffer();
						buf.append(fd.getFilterPath());
						if (buf.charAt(buf.length() - 1) != File.separatorChar) {
							buf.append(File.separatorChar);
						}
						buf.append(files[i]);
						listAttachments.add(buf.toString());
					}
				}
			}
		});
		addAttachments.setText(Messages.DocboxArztArztDialog_ButtonAddAttachments);
		
		return com;
	}
	
	@Override
	public void create(){
		setShellStyle(getShellStyle() | SWT.RESIZE);
		super.create();
		setMessage(Messages.DocboxArztArztDialog_Title);
		if (UiDesk.getImage(Activator.IMG_DOC2DOC) == null) {
			UiDesk.getImageRegistry().put(Activator.IMG_DOC2DOC,
				Activator.getImageDescriptor(Activator.IMG_DOC2DOC_PATH));
		}
		setTitleImage(UiDesk.getImageRegistry().get(Activator.IMG_DOC2DOC));
	}
	
	@Override
	protected void okPressed(){
		final String title = textTitle.getText();
		final String message = textMessage.getText();
		final String filename = "ueberweisung.pdf";
		final String[] attachments = listAttachments.getItems();
		final javax.xml.ws.Holder<java.lang.Boolean> success =
			new javax.xml.ws.Holder<java.lang.Boolean>();
		final javax.xml.ws.Holder<java.lang.String> errorMessage =
			new javax.xml.ws.Holder<java.lang.String>();
		final javax.xml.ws.Holder<java.lang.String> documentId =
			new javax.xml.ws.Holder<java.lang.String>();
		
		if (comboDoctor.getSelectionIndex() == -1) {
			MessageBox box = new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_ERROR);
			box.setText(Messages.DocboxArztArztAction_NoDoctorSelectedText);
			box.setMessage(Messages.DocboxArztArztAction_NoDoctorSelectedMessage);
			box.open();
			return;
		} else {
			kontakt = this.kontakte.get(comboDoctor.getSelectionIndex());
		}
		
		Runnable longJob = new Runnable() {
			boolean done = false;
			
			public void stop(String errMsg){
				if (errMsg != null) {
					errorMessage.value = errMsg;
				}
				done = true;
				UiDesk.getDisplay().wake();
			}
			
			public void run(){
				Thread thread = new Thread(new Runnable() {
					public void run(){
						success.value = Boolean.FALSE;
						try {
							final ClinicalDocumentType cda =
								getArztArztCda(null, title, message, filename, attachments);
							if (cda == null) {
								stop(Messages.DocboxArztArztAction_NoCdaGeneratedMessage);
								return;
							}
							
							byte[] xml = getClincicalDocumentSerialized(cda);
							if (xml == null) {
								stop(Messages.DocboxArztArztAction_NoXmlGeneratedMessage);
								return;
							}
							
							ByteArrayOutputStream pdfOut = getArztArztPdf(xml);
							if (pdfOut == null) {
								stop(Messages.DocboxArztArztAction_NoPdfGeneratedMessage);
								return;
							}
							
							final ByteArrayOutputStream byteArrayOutputStream =
								new ByteArrayOutputStream();
							ZipOutputStream out = new ZipOutputStream(byteArrayOutputStream);
							try {
								out.putNextEntry(new ZipEntry(filename));
								pdfOut.writeTo(out);
								out.closeEntry();
								if (attachments != null) {
									for (String attachFile : attachments) {
										String fileName = attachFile;
										if (fileName.indexOf("/") >= 0) {
											fileName =
												fileName.substring(fileName.lastIndexOf("/") + 1);
										}
										if (fileName.indexOf("\\") >= 0) {
											fileName =
												fileName.substring(fileName.lastIndexOf("\\") + 1);
										}
										out.putNextEntry(new ZipEntry(fileName));
										int len = 0;
										InputStream in = new FileInputStream(attachFile);
										byte[] buf = new byte[1024];
										while ((len = in.read(buf)) > 0) {
											out.write(buf, 0, len);
										}
										out.closeEntry();
										in.close();
									}
								}
								out.close();
							} catch (IOException e) {
								ExHandler.handle(e);
								stop(Messages.DocboxArztArztAction_NoZipGeneratedMessage);
								return;
							}
							
							CDACHServices port = UserDocboxPreferences.getPort();
							port.sendClinicalDocument(cda, byteArrayOutputStream.toByteArray(),
								success, errorMessage, documentId);
							if (!success.value.booleanValue()) {
								stop(Messages.DocboxArztArztAction_SendDocumentFailed + "\n"
									+ errorMessage.value);
								return;
							}
						} catch (Exception e) {
							ExHandler.handle(e);
							stop(Messages.DocboxArztArztAction_SendDocumentFailed + "\n"
								+ errorMessage.value);
							return;
						}
						done = true;
						UiDesk.getDisplay().wake();
					}
				});
				thread.start();
				while (!done
					&& (UiDesk.getTopShell() == null || !UiDesk.getTopShell().isDisposed())) {
					if (!UiDesk.getDisplay().readAndDispatch())
						UiDesk.getDisplay().sleep();
				}
			}
			
		};
		BusyIndicator.showWhile(UiDesk.getDisplay(), longJob);
		
		if (success != null && success.value != null && !success.value.booleanValue()) {
			MessageBox box = new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_ERROR);
			box.setText(Messages.UserDocboxPreferences_ConnectionTestWithDocbox);
			box.setMessage(errorMessage.value);
			box.open();
		} else {
			super.okPressed();
			Activator.docboxBackgroundJob.schedule();
		}
	}
	
	private byte[] getClincicalDocumentSerialized(ClinicalDocumentType cda){
		final String xml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<?xml-stylesheet type='text/xsl' href='./Terminvereinbarung.xsl'?>\n"
				+ docboxCDA.marshallIntoString(cda.getClinicalDocument());
		try {
			return xml.getBytes("UTF8");
		} catch (final UnsupportedEncodingException e) {
			ExHandler.handle(e);
		}
		return null;
	}
	
	// FIXME FOP xml-api library crashes as eclipse plugin
	// http://www.mail-archive.com/fop-users@xmlgraphics.apache.org/msg12100.html
	private ByteArrayOutputStream getArztArztPdf(byte[] xmlData){
		if (xmlData == null) {
			return null;
		}
		ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
		
		BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<IFormattedOutputFactory> fopFactoryRef =
			bundleContext.getServiceReference(IFormattedOutputFactory.class);
		if (fopFactoryRef != null) {
			try {
				IFormattedOutputFactory fopFactory = bundleContext.getService(fopFactoryRef);
				
				IFormattedOutput foOutputt = fopFactory
					.getFormattedOutputImplementation(ObjectType.XMLSTREAM, OutputType.PDF);
				ByteArrayInputStream stream = new ByteArrayInputStream(xmlData);
				URL xsl = CDACHServices_Service.class
					.getResource("/rsc/ch/docbox/ws/cdachservices/ArztArzt.xsl");
				foOutputt.transform(stream, xsl.openStream(), pdfOut);
			} catch (IllegalStateException e) {
				ExHandler.handle(e);
			} catch (IOException e) {
				ExHandler.handle(e);
			}
			bundleContext.ungetService(fopFactoryRef);
		}
		
		return pdfOut;
	}
	
	private ClinicalDocumentType getArztArztCda(Fall fall, String title, String message,
		String filename, String[] attachments){
		
		try {
			addVersicherung(fall, docboxCDA);
		} catch (Exception e) {
			ExHandler.handle(e);
		}
		
		Date birthday = null;
		if (!"".equals(patient.getGeburtsdatum())) {
			TimeTool ttBirthday = new TimeTool(patient.getGeburtsdatum());
			birthday = ttBirthday.getTime();
		}
		POCDMT000040RecordTarget recordTarget =
			docboxCDA.getRecordTarget(patient.getPatCode(), null, patient.getAnschrift()
				.getStrasse(), patient.getAnschrift().getPlz(), patient.getAnschrift().getOrt(),
				patient.get(Person.FLD_PHONE1), patient.get(Person.FLD_PHONE2), patient.getNatel(),
				patient.getMailAddress(), patient.getVorname(), patient.getName(), "w"
					.equals(patient.getGeschlecht()), "m".equals(patient.getGeschlecht()), false,
				birthday);
		
		POCDMT000040Author author =
			docboxCDA.getAuthor(CoreHub.actMandant.get(Person.TITLE),
				CoreHub.actMandant.getVorname(), CoreHub.actMandant.getName(),
				CoreHub.actMandant.getNatel(), null, null, CoreHub.actMandant.getMailAddress(),
				null, null, null);
		
		POCDMT000040Custodian custodian =
			docboxCDA.getCustodian("", docboxCDA.getAddress(CoreHub.actMandant.getAnschrift()
				.getStrasse(), null, CoreHub.actMandant.getAnschrift().getPlz(), CoreHub.actMandant
				.getAnschrift().getOrt(), "WP"), null, null, null, null);
		
		POCDMT000040InformationRecipient informationRecipient = null;
		if (kontakt != null) {
			String organization = kontakt.get(Kontakt.FLD_NAME3);
			if (kontakt.get(Kontakt.FLD_IS_USER).equals(StringConstants.ONE)) {
				organization = "";
			}
			informationRecipient =
				docboxCDA.getInformationRecipient(kontakt.get(Person.TITLE), kontakt
					.get(Kontakt.FLD_NAME2), kontakt.get(Kontakt.FLD_NAME1), DocboxContact
					.getDocboxIdFor(kontakt), docboxCDA.getOrganization(organization, null, null,
					null, kontakt.getAnschrift().getStrasse(), kontakt.getAnschrift().getPlz(),
					kontakt.getAnschrift().getOrt()));
		}
		
		docboxCDA.addComponentToBody("Notiz", message, "NOTIZ");
		
		Vector<String> attachmentsCda = new Vector<String>();
		attachmentsCda.add(filename);
		if (attachments != null) {
			for (String attach : attachments) {
				attachmentsCda.add(attach);
			}
		}
		docboxCDA.addAttachmentsDescriptionToBody(attachmentsCda);
		
		ClinicalDocumentType _addReferral_document = new ClinicalDocumentType();
		_addReferral_document.setClinicalDocument(docboxCDA.getClinicalDocument(title,
			recordTarget, author, custodian, informationRecipient, docboxCDA.getCodeReferral(),
			null, DocboxCDA.DOCBOXCDATYPE.Docbox_Arzt_Arzt));
		
		return _addReferral_document;
	}
	
	private void addVersicherung(Fall fall, DocboxCDA docboxCDA){
		if (fall != null) {
			if ("UVG".equals(fall.getAbrechnungsSystem())) {
				try {
					docboxCDA.addUnfallversicherung(fall.getCostBearer()
						.getLabel());
				} catch (Exception e) {
					ExHandler.handle(e);
				}
				try {
					docboxCDA.addUnfallversicherungPolicenummer(fall
						.getRequiredString("Unfallnummer"));
				} catch (Exception e) {
					ExHandler.handle(e);
				}
			}
			if ("KVG".equals(fall.getAbrechnungsSystem())) {
				try {
					docboxCDA.addKrankenkasse(fall.getCostBearer().getLabel());
				} catch (Exception e) {
					ExHandler.handle(e);
				}
				try {
					docboxCDA.addKrankenkassePolicenummer(fall
						.getRequiredString("Versicherungsnummer"));
				} catch (Exception e) {
					ExHandler.handle(e);
				}
			}
			
		}
	}
	
}
