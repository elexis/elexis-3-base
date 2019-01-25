package ch.elexis.labororder.lg1.order;

import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.LocalLock;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.data.Anwender;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.hl7.data.HL7Kontakt;
import ch.elexis.hl7.data.HL7Kostentraeger;
import ch.elexis.hl7.data.HL7Mandant;
import ch.elexis.hl7.data.HL7Patient;
import ch.elexis.hl7.v26.HL7_OML_O21;
import ch.elexis.labororder.lg1.messages.Messages;
import ch.elexis.labororder.lg1.preferences.LG1PreferencePage;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.io.FileTool;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class LabOrderAction extends Action {
	
	private static final String TEXT_ENCODING = "ISO-8859-1";
	
	public LabOrderAction(){
		setId("ch.elexis.laborder.lg1.laborder"); //$NON-NLS-1$
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.labororder.lg1", //$NON-NLS-1$
			"rsc/lg1_logo.png"));
		setText(Messages.LabOrderAction_nameAction);
	}
	
	@Override
	public void run(){
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		Kontakt costBearer = null;
		Kontakt rechnungsempfaenger = null;
		Date beginDate = null;
		String vnr = ""; //$NON-NLS-1$
		String plan = ""; //$NON-NLS-1$
		// Patient und Kostentraeger bestimmen
		if (patient == null) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
				Messages.LabOrderAction_errorTitleNoPatientSelected,
				Messages.LabOrderAction_errorMessageNoPatientSelected);
		} else {
			Fall fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
			// Selected könnte noch vom vorangehendem Patienten sein
			if (fall != null && fall.getPatient() != null
				&& !patient.getId().equals(fall.getPatient().getId())) {
				fall = null;
			}
			// Wenn nur 1 Fall offen, dann wird dieser verwendet
			if (fall == null) {
				List<Fall> offeneFaelleList = new Vector<Fall>();
				for (Fall tmpFall : patient.getFaelle()) {
					if (tmpFall.isOpen()) {
						offeneFaelleList.add(tmpFall);
					}
				}
				if (offeneFaelleList.size() == 1) {
					fall = offeneFaelleList.get(0);
				}
			}
			if (fall == null) {
				MessageDialog.openError(Display.getDefault().getActiveShell(),
					Messages.LabOrderAction_errorTitleNoFallSelected,
					Messages.LabOrderAction_errorMessageNoFallSelected);
			} else {
				costBearer = fall.getCostBearer();
				if (costBearer == null) {
					costBearer = fall.getGarant();
				}
				rechnungsempfaenger = fall.getRequiredContact(Fall.FLD_EXT_RECHNUNGSEMPFAENGER);
				if (rechnungsempfaenger == null) {
					rechnungsempfaenger = fall.getGarant();
				}
				plan = fall.getAbrechnungsSystem();
				beginDate = new TimeTool(fall.getBeginnDatum()).getTime();
				vnr = getInsuranceOrCaseNumber(fall);
			}
		}
		
		boolean ok = false;
		long orderNr = -1;
		String filenamePath = "-"; //$NON-NLS-1$
		if (patient != null && costBearer != null) {
			orderNr = getNextOrderNr(patient);
			filenamePath = writeHL7File(patient, rechnungsempfaenger, costBearer, plan, beginDate,
				vnr, orderNr);
			if (filenamePath != null) {
				ok = true; // @deprecated openBrowser();
			}
		}
		
		if (ok) {
			String patLabel = "-"; //$NON-NLS-1$
			if (patient != null) {
				patLabel = patient.getLabel();
			}
			String orderNrText = ""; //$NON-NLS-1$
			if (orderNr >= 0) {
				orderNrText = new Long(orderNr).toString();
			}
			MessageDialog.openInformation(Display.getDefault().getActiveShell(),
				Messages.LabOrderAction_infoTitleLabOrderFinshed,
				MessageFormat.format(Messages.LabOrderAction_infoMessageLabOrderFinshed,
					orderNrText, patLabel, filenamePath));
		}
	}
	
	private String getInsuranceOrCaseNumber(final Fall fall){
		String nummer = null;
		BillingLaw gesetz = fall.getConfiguredBillingSystemLaw();
		if (gesetz != null) {
			if (gesetz == BillingLaw.IVG) {
				nummer = fall.getRequiredString(TarmedRequirements.CASE_NUMBER);
			} else if (gesetz == BillingLaw.UVG) {
				nummer = fall.getRequiredString(TarmedRequirements.ACCIDENT_NUMBER);
			} else {
				nummer = fall.getRequiredString(TarmedRequirements.INSURANCE_NUMBER);
			}
		}
		if (nummer == null) {
			nummer = fall.getInfoString(TarmedRequirements.CASE_NUMBER);
			if ("".equals(nummer)) { //$NON-NLS-1$
				nummer = fall.getInfoString(TarmedRequirements.ACCIDENT_NUMBER);
			}
			if ("".equals(nummer)) { //$NON-NLS-1$
				nummer = fall.getInfoString(TarmedRequirements.INSURANCE_NUMBER);
			}
		}
		
		return nummer;
	}
	
	private void fillHL7Kontakt(final HL7Kontakt hl7Kontakt, final Kontakt kontakt){
		
		String name = kontakt.get(Kontakt.FLD_NAME1);
		if (name == null) {
			name = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setName(name.trim());
		
		String firstname = kontakt.get(Kontakt.FLD_NAME2);
		if (firstname == null) {
			firstname = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setFirstname(firstname.trim());
		
		String title = kontakt.get("Titel"); //$NON-NLS-1$
		if (title == null) {
			title = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setTitle(title.trim());
		
		String phone1 = kontakt.get(Kontakt.FLD_PHONE1);
		if (phone1 == null) {
			phone1 = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setPhone1(phone1.trim());
		
		String phone2 = kontakt.get(Kontakt.FLD_PHONE2);
		if (phone2 == null) {
			phone2 = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setPhone2(phone2.trim());
		
		String email = kontakt.get(Kontakt.FLD_E_MAIL);
		if (email == null) {
			email = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setEmail(email.trim());
		
		String fax = kontakt.get(Kontakt.FLD_FAX);
		if (fax == null) {
			fax = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setFax(fax.trim());
		
		String street = kontakt.get(Kontakt.FLD_STREET);
		if (street == null) {
			street = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setAddress1(street.trim());
		
		String other = kontakt.get(Patient.FLD_NAME3);
		if (other == null) {
			other = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setAddress2(other.trim());
		
		String city = kontakt.get(Patient.FLD_PLACE);
		if (city == null) {
			city = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setCity(city.trim());
		
		String zip = kontakt.get(Patient.FLD_ZIP);
		if (zip == null) {
			zip = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setZip(zip.trim());
		
		String country = kontakt.get(Patient.FLD_COUNTRY);
		if (country == null) {
			country = ""; //$NON-NLS-1$
		}
		hl7Kontakt.setCountry(country.trim());
	}
	
	/**
	 * Creates HL7 File (V2.6)
	 * 
	 * @return full filename path of the file. Null if error happens
	 */
	private String writeHL7File(final Patient patient, final Kontakt rechnungsempfaenger,
		final Kontakt kostentraeger, final String plan, final Date beginDate, final String vnr,
		final long orderNr){
		
		String uniqueMessageControlID = StringTool.unique("MessageControlID"); //$NON-NLS-1$
		String uniqueProcessingID = StringTool.unique("ProcessingID"); //$NON-NLS-1$
		
		HL7Mandant mandant = new HL7Mandant();
		Mandant actMandant = ElexisEventDispatcher.getSelectedMandator();
		if (actMandant != null) {
			mandant.setLabel(actMandant.get(Anwender.FLD_LABEL));
			mandant.setEan(actMandant.getXid(DOMAIN_EAN));
		}
		
		HL7_OML_O21 omlO21 =
			new HL7_OML_O21("CHELEXIS", "PATDATA", Messages.LabOrderAction_receivingApplication, "",
				Messages.LabOrderAction_receivingFacility, uniqueMessageControlID,
				uniqueProcessingID, mandant);
		
		// Patient
		HL7Patient hl7Patient = new HL7Patient();
		fillHL7Kontakt(hl7Patient, patient);
		String geschlecht = patient.getGeschlecht();
		if (geschlecht != null && geschlecht.length() > 0) {
			hl7Patient.setIsMale(
				Patient.MALE.toUpperCase().equals(patient.getGeschlecht().toUpperCase()));
		}
		hl7Patient.setBirthdate(new TimeTool(patient.getGeburtsdatum()).getTime());
		hl7Patient.setPatCode(patient.getPatCode());
		
		// Rechnungsempfaenger
		HL7Kostentraeger hl7Rechnungsempfaenger = new HL7Kostentraeger();
		fillHL7Kontakt(hl7Rechnungsempfaenger, rechnungsempfaenger);
		hl7Rechnungsempfaenger.setEan(rechnungsempfaenger.getXid(DOMAIN_EAN));
		
		// Kostentraeger
		HL7Kostentraeger hl7Kostentraeger = new HL7Kostentraeger();
		fillHL7Kontakt(hl7Kostentraeger, kostentraeger);
		hl7Kostentraeger.setEan(kostentraeger.getXid(DOMAIN_EAN));
		
		try {
			String encodedMessage = omlO21.createText(hl7Patient, hl7Rechnungsempfaenger,
				hl7Kostentraeger, plan, beginDate, vnr, orderNr);
			
			// write file
			String filename =
				new Long(orderNr).toString() + "_" + patient.get(Patient.FLD_PATID) + ".hl7"; //$NON-NLS-1$ //$NON-NLS-2$
			File hl7File =
				new File(LG1PreferencePage.getUploadDir() + File.separator + filename);
			FileTool.writeFile(hl7File, encodedMessage.getBytes(TEXT_ENCODING));
			
			return hl7File.getPath();
		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), MessageFormat
				.format(Messages.LabOrderAction_errorTitleCannotCreateHL7, omlO21.getVersion()),
				e.getMessage());
		}
		return null;
	}
	
	/**
	 * Opens a browser in or outside of elexis
	 * 
	 * @return
	 */
	private long getNextOrderNr(final Patient patient) throws NumberFormatException{
		LocalLock lock = new LocalLock("ch.elexis.labororder.lg1.nextOrderNr");
		try {
			while (!lock.tryLock()) {
				// unlock if there is a hanging lock, if not changed for 2 sec.
				long millis = lock.getLockCurrentMillis();
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
					// ignore and carry on
				}
				if (millis == lock.getLockCurrentMillis()) {
					lock.unlock();
				}
			}
			
			String value = CoreHub.globalCfg.get("ch.elexis.labororder.lg1.orderNr", "0");
			Integer intValue = Integer.valueOf(value);
			long ret = intValue++;
			CoreHub.globalCfg.set("ch.elexis.labororder.lg1.orderNr", Integer.toString(intValue));
			return ret;
		} finally {
			lock.unlock();
		}
	}
}
