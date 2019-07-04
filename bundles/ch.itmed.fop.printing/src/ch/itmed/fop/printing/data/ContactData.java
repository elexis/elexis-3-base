package ch.itmed.fop.printing.data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.agenda.AreaType;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.itmed.fop.printing.resources.Messages;

public class ContactData {
	private Kontakt kontakt;

	public void load() throws NullPointerException {
		kontakt = (Kontakt) ElexisEventDispatcher.getSelected(Kontakt.class);
		if (kontakt == null) {
			SWTHelper.showInfo(Messages.Info_NoContact_Title, Messages.Info_NoContact_Message);
			throw new NullPointerException("No contact selected");
		}
	}

	public void loadFromAppointment() throws NullPointerException {
		Termin t = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
		if (t == null) {
			throw new NullPointerException("No appointment selected");
		}

		loadFromTermin(t);
	}

	public void loadFromAppointments() throws NullPointerException {
		Patient patient = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
		if (patient == null) {
			// TODO SWT GUI
			throw new NullPointerException("No patient selected");
		}

		Instant instant = Instant.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd").withZone(ZoneId.systemDefault());
		String currentDate = formatter.format(instant);

		Query<Termin> query = new Query<>(Termin.class);
		query.add(Termin.FLD_PATIENT, Query.EQUALS, patient.getId());
		query.add(Termin.FLD_TAG, Query.GREATER, currentDate);
		query.orderBy(false, Termin.FLD_TAG);
		List<Termin> appointments = (List<Termin>) query.execute();

		loadFromTermin(appointments.get(0));
	}

	private void loadFromTermin(Termin t) {
		String agendaSection = t.getBereich();
		String type = CoreHub.globalCfg.get(
				PreferenceConstants.AG_BEREICH_PREFIX + agendaSection + PreferenceConstants.AG_BEREICH_TYPE_POSTFIX,
				null);
		if (type != null) {
			if (type.startsWith(AreaType.CONTACT.name())) {
				kontakt = Kontakt.load(type.substring(AreaType.CONTACT.name().length() + 1));
			}
		}
	}

	public String getAddress() {
		return kontakt.getPostAnschrift(true);
	}

	public String getSalutaton() {
		return kontakt.getSalutation();
	}

}
