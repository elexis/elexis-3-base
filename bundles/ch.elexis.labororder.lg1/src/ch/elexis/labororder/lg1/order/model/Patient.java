package ch.elexis.labororder.lg1.order.model;

import org.apache.commons.lang3.StringUtils;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.types.Gender;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.labororder.lg1.messages.Messages;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.TimeTool;

public class Patient {
	String id = StringUtils.EMPTY;
	String dateofbirth = StringUtils.EMPTY;
	String gender = StringUtils.EMPTY;
	String title = StringUtils.EMPTY;
	String lastname = StringUtils.EMPTY;
	String firstname = StringUtils.EMPTY;
	String street = StringUtils.EMPTY;
	String zip = StringUtils.EMPTY;
	String city = StringUtils.EMPTY;
	String country = StringUtils.EMPTY;
	String insurancenumber = StringUtils.EMPTY;
	String insurancename = StringUtils.EMPTY;
	String insurancegln = StringUtils.EMPTY;
	String billing = StringUtils.EMPTY;

	public static Patient of(ch.elexis.data.Patient patient) {
		Patient ret = new Patient();
		ret.id = patient.getPatCode();
		ret.dateofbirth = new TimeTool(patient.getGeburtsdatum()).toString(TimeTool.DATE_COMPACT);
		ret.gender = patient.getGender() == Gender.FEMALE ? "female" : "male";
		ret.title = patient.get(ch.elexis.data.Patient.TITLE);
		ret.lastname = patient.getName();
		ret.firstname = patient.getVorname();
		ret.street = patient.getAnschrift().getStrasse();
		ret.zip = patient.getAnschrift().getPlz();
		ret.city = patient.getAnschrift().getOrt();
		ret.country = patient.getAnschrift().getLand();
		Fall fall = getFall(patient);
		if (fall == null) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					Messages.LabOrderAction_errorTitleNoFallSelected,
					Messages.LabOrderAction_errorMessageNoFallSelected);
		}
		ret.insurancenumber = getInsuranceOrCaseNumber(fall);
		ret.insurancename = getInsuranceName(fall);
		ret.insurancegln = getInsuranceGln(fall);
		ret.billing = getBilling(fall);
		return ret;
	}

	private static String getBilling(Fall fall) {
		Kontakt costBearer = fall.getCostBearer();
		Kontakt guarantor = fall.getGarant();
		if (costBearer == null) {
			costBearer = fall.getGarant();
		}
		if (costBearer != null && costBearer.istOrganisation()) {
			if (guarantor.equals(costBearer)) {
				return "tp";
			}
		}
		return "tg";
	}

	private static String getInsuranceName(Fall fall) {
		Kontakt costBearer = fall.getCostBearer();
		if (costBearer == null) {
			costBearer = fall.getGarant();
		}
		return costBearer.getLabel(true);
	}

	private static String getInsuranceGln(Fall fall) {
		Kontakt costBearer = fall.getCostBearer();
		if (costBearer == null) {
			costBearer = fall.getGarant();
		}
		return costBearer.getXid(DOMAIN_EAN);
	}

	private static Fall getFall(ch.elexis.data.Patient patient) {
		Fall fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
		if (fall != null && fall.getPatient() != null && !patient.getId().equals(fall.getPatient().getId())) {
			fall = null;
		}
		if (fall == null) {
			List<Fall> offeneFaelleList = new ArrayList<>();
			for (Fall tmpFall : patient.getFaelle()) {
				if (tmpFall.isOpen()) {
					offeneFaelleList.add(tmpFall);
				}
			}
			if (offeneFaelleList.size() == 1) {
				fall = offeneFaelleList.get(0);
			}
		}
		return fall;
	}

	private static String getInsuranceOrCaseNumber(final Fall fall) {
		String nummer = null;
		BillingLaw gesetz = BillingLaw.KVG;
		try {
			gesetz = fall.getConfiguredBillingSystemLaw();
		} catch (IllegalArgumentException e) {
			// unknown billing system, lets try with KVG
		}
		if (gesetz != null) {
			if (gesetz == BillingLaw.IV) {
				nummer = fall.getRequiredString(TarmedRequirements.CASE_NUMBER);
			} else if (gesetz == BillingLaw.UVG) {
				nummer = fall.getRequiredString(TarmedRequirements.ACCIDENT_NUMBER);
			} else {
				nummer = fall.getRequiredString(TarmedRequirements.INSURANCE_NUMBER);
			}
		}
		if (nummer == null) {
			nummer = fall.getInfoString(TarmedRequirements.CASE_NUMBER);
			if (StringUtils.EMPTY.equals(nummer)) {
				nummer = fall.getInfoString(TarmedRequirements.ACCIDENT_NUMBER);
			}
			if (StringUtils.EMPTY.equals(nummer)) {
				nummer = fall.getInfoString(TarmedRequirements.INSURANCE_NUMBER);
			}
		}
		return nummer;
	}
}
