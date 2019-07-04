package ch.itmed.fop.printing.data;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Messages;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;

public class PatientData {
	private Patient patient;

	public void load() throws NullPointerException {
		patient = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
		if (patient == null) {
			SWTHelper.showInfo("Kein Patient ausgewählt", "Bitte wählen Sie vor dem Drucken einen Patient!");
			throw new NullPointerException("No patient selected");
		}
	}

	public void loadFromAgenda() throws NullPointerException {
		Termin t = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
		if (t == null) {
			throw new NullPointerException("No appointment selected");
		}

		String pid = t.getKontakt().getId();
		patient = Patient.load(pid);
	}

	public String getFirstName() {
		return patient.getVorname();
	}

	public String getLastName() {
		return patient.getName();
	}

	public String getBirthdate() {
		return patient.getGeburtsdatum();
	}

	public String getSex() {
		//return patient.getGender().value();
		return patient.getGeschlecht();
	}

	public String getPid() {
		return PersistentObject.checkNull(patient.get(Patient.FLD_PATID));
	}

	public String getSalutation() {
		String salutation;
		if (patient.getGeschlecht().equals(Person.MALE)) {
			salutation = Messages.Contact_SalutationM;
		} else {
			salutation = Messages.Contact_SalutationF;
		}
		return salutation;
	}

	public String getTitle() {
		return PersistentObject.checkNull(patient.get("Titel"));
	}

	public String getPostalCode() {
		return PersistentObject.checkNull(patient.get(Patient.FLD_ZIP));
	}

	public String getCity() {
		return PersistentObject.checkNull(patient.get(Patient.FLD_PLACE));
	}

	public String getCountry() {
		return PersistentObject.checkNull(patient.get("Land"));
	}

	public String getStreet() {
		return PersistentObject.checkNull(patient.get(Patient.FLD_STREET));
	}

	public String getPhone1() {
		return PersistentObject.checkNull(patient.get("Telefon1"));
	}

	public String getPhone2() {
		return PersistentObject.checkNull(patient.get("Telefon2"));
	}

	public String getMobilePhone() {
		return PersistentObject.checkNull(patient.get("NatelNr"));
	}

	public String getCompleteAddress() {
		return patient.getPostAnschrift(true);
	}

	public String getOrderNumer() {
		return patient.getAuftragsnummer();
	}
}