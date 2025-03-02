/*******************************************************************************
 * Copyright (c) 2014, Pharmed Solutions GmbH
 * All rights reserved.
 *******************************************************************************/

package ch.pharmed.phmprescriber;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Artikel;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Rezept;
import ch.pharmedsolutions.www.rezeptserver.PrescriptionPortType;
import ch.pharmedsolutions.www.rezeptserver.PrescriptionResponse;
import ch.pharmedsolutions.www.rezeptserver.PrescriptionService;

public class Sender {

	// --Variables--
	private Physician ph;
	private Rezept rp;
	private Patient pat;
	private String GLN = StringUtils.EMPTY;

	private String presID;
	private String QRCode;

	private ResourceBundle messages;

	// Constructor
	public Sender(Rezept pres, Physician phys) {

		ph = phys;
		this.rp = pres;
//		rp = (Rezept) ElexisEventDispatcher.getSelected(Rezept.class);

		if (!(rp == null))
			pat = rp.getPatient();

		// Set the default language
		messages = ResourceBundle.getBundle("ch.pharmed.phmprescriber.MessagesBundle", new Locale("de", "CH")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	}

	public void setLanguage(Locale currentLocale) {

		messages = ResourceBundle.getBundle("ch.pharmed.phmprescriber.MessagesBundle", currentLocale); //$NON-NLS-1$

	}

	public ResourceBundle getMessages() {

		return this.messages;

	}

	// Send the prescriptions via SOAP-Service
	public void sendnprint() {

		// (1) Validate Input
		if (isInputValid() == false) {
			return;
		}
		;

		Shell shell = UiDesk.getDisplay().getActiveShell();

		// (2) Check, if there are shops available for the particular physician

		if (ph.hasShops()) {

			HashMap<String, String> hmShops = ph.shops;

			String[] ShopNames = hmShops.keySet().toArray(new String[0]);

			ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider());
			dialog.setElements(ShopNames);
			dialog.setTitle(messages.getString("Sender_5"));
			dialog.setMessage(messages.getString("Sender_6"));
			dialog.setMultipleSelection(false);
			dialog.setAllowDuplicates(false);

			// Assign GLN if a shop was selected
			if (!(dialog.open() != Window.OK)) {

				Object result = dialog.getFirstResult();

				this.GLN = hmShops.get((String) result);

			}

		}

		// (3) Check Interaction if enabled
		String strCFG = ConfigServiceHolder.getGlobal(Constants.CFG_INTERATCIONS, StringUtils.EMPTY);

		// If so, run the check
		if (strCFG.equals("true")) { //$NON-NLS-1$

			Interaction IA = new Interaction();

			List<String> interactions = IA.checkPrescription(rp);

			if (interactions != null) {

				// Prepare the window to display

				IADialog dialog = new IADialog(shell);
				dialog.setProductDescr(interactions);
				dialog.setResourceBundle(messages);

				dialog.create();

				if (dialog.open() != Window.OK) {

					return;
				}

			}
			;

		}

		// (3) Post the prescription and obtain the id and QR-Code String
		if (postPrescription() == false) {
			SWTHelper.alert(messages.getString("Sender_9"), messages.getString("Sender_10"));

			return;
		}

		// (4) Print the prescription
		Printer printer = new Printer(ph, rp, this.presID, this.QRCode);

	}

	private Boolean isInputValid() {

		// (1) is there a prescription object?
		if (rp == null) {
			SWTHelper.alert(messages.getString("Sender_11"), messages.getString("Sender_12"));
			return false;
		}

		// (2) Is there a ZSR-Number?
		System.out.println(ph.getZsrid().length());
		if (ph.getZsrid().length() < 7) {
			SWTHelper.alert(messages.getString("Sender_13"), messages.getString("Sender_14"));
			return false;

		}

		return true;

	}

	private Date convertStringtoDate(String strDate, String Format) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(Format);
		Date convertedDate;

		try {

			convertedDate = dateFormat.parse(strDate);
			return convertedDate;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Boolean postPrescription() {

		String defaultDateFormat = "dd.MM.yyyy"; //$NON-NLS-1$

		// ------General attributes-------------
		ch.pharmedsolutions.www.rezeptserver.Prescription prescription = new ch.pharmedsolutions.www.rezeptserver.Prescription();

		prescription.setSoftware(BigInteger.valueOf(10));
		prescription.setPassword(Constants.CFG_PHM_PASSWORD);
		prescription.setZsrId(ph.getZsrid());

		if (GLN.length() == 13) {

			prescription.setGLNTargetpharmacy(GLN);

		}

		prescription.setDate(convertStringToGregorian(rp.getDate(), defaultDateFormat));

		// -----Patient--------------------------
		ch.pharmedsolutions.www.rezeptserver.Patient patient = new ch.pharmedsolutions.www.rezeptserver.Patient();

		patient.setFirstName(pat.getVorname());
		patient.setLastName(pat.getName());
		patient.setBirthDate(convertStringToGregorian(pat.getGeburtsdatum(), defaultDateFormat));
		// Assign
		prescription.setPatient(patient);

		// -----Products-------------------------

		ch.pharmedsolutions.www.rezeptserver.ArrayOfProduct arrayOfProducts = new ch.pharmedsolutions.www.rezeptserver.ArrayOfProduct();

		ch.pharmedsolutions.www.rezeptserver.Product[] Products = new ch.pharmedsolutions.www.rezeptserver.Product[rp
				.getLines().size()];

		// Assign all products

		for (int i = 0; i < rp.getLines().size(); i = i + 1) {

			Products[i] = new ch.pharmedsolutions.www.rezeptserver.Product();

			// Line elements
			Prescription actualLine = rp.getLines().get(i);

			// Check, if posology has the right format
			Pattern pattern = Pattern
					.compile("([0-9.]{1,5})([-])([0-9.]{1,5})([-])([0-9.]{1,5})([-])([0-9.]{0,5})([0-9])"); //$NON-NLS-1$

			Matcher matcher = pattern.matcher(actualLine.getDosis());

			if (matcher.find()) {

				Products[i].setPosology(actualLine.getDosis());
				Products[i].setRemark(actualLine.getBemerkung());
			} else {
				// if there is posology, just append the remark
				if (actualLine.getDosis().length() > 0) {

					Products[i].setRemark(actualLine.getDosis() + ", " + actualLine.getBemerkung()); //$NON-NLS-1$
				}

				else {
					Products[i].setRemark(actualLine.getBemerkung());
				}
			}

			// Article elements
			Artikel article = actualLine.getArtikel();

			Products[i].setPharmacode(null);
			Products[i].setEanId(null);

			if (article.getPharmaCode().length() > 0) {
				Products[i].setPharmacode(new BigInteger(article.getPharmaCode()));

			}

			if (article.getEAN().length() > 0) {
				Products[i].setEanId(new BigInteger(article.getEAN()));

			}

			// Check, whether there is an undefined article
			if (Products[i].getPharmacode() == null & Products[i].getEanId() == null) {

				Products[i].setPharmacode(BigInteger.valueOf(111));
				Products[i].setEanId(BigInteger.valueOf(111));

				Products[i].setProductName(article.getLabel());
			}

			// Set the default to one
			Products[i].setPrescriptorQty(Integer.valueOf(1));

			// Don't set the repetition duration
			Products[i].setPrescriptorRepetitionEnd(null);

			// Assign
			arrayOfProducts.getItem().add(Products[i]);
		}

		// Assign
		prescription.setProducts(arrayOfProducts);

		PrescriptionResponse response = new PrescriptionResponse();

		try {

			// Get the information
			response = consumService(prescription);

			if (!(response == null)) {

				this.presID = response.getPrescriptionID();
				this.QRCode = response.getQRCodeString();

				System.out.println("Success"); //$NON-NLS-1$

				return true;

			}
			;

		} catch (Exception ex) {

			System.out.println("Exception: " + ex); //$NON-NLS-1$

		}

		return false;

	}

	private PrescriptionResponse consumService(ch.pharmedsolutions.www.rezeptserver.Prescription prescription) {

		PrescriptionService service = new PrescriptionService();
		PrescriptionPortType port = service.getPrescriptionPort();

		return port.postPrescription(prescription);

	}

	private XMLGregorianCalendar convertStringToGregorian(String pDate, String dateFormat) {

		GregorianCalendar c = new GregorianCalendar();

		c.setTime(this.convertStringtoDate(pDate, dateFormat));

		XMLGregorianCalendar finaldate;
		finaldate = null;

		try {

			finaldate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		} catch (DatatypeConfigurationException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return finaldate;

	}

}
