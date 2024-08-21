package at.medevit.elexis.agenda.ui.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class AppointmentLoader {

	public Optional<IAppointment> findAppointmentById(String appointmentId) {
		IQuery<IAppointment> query = CoreModelServiceHolder.get().getQuery(IAppointment.class);
		query.and("id", COMPARATOR.EQUALS, appointmentId);
		List<IAppointment> results = query.execute();

		if (results.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(results.get(0));
		}
	}

	public static List<IAppointment> findLinkedAppointments(IAppointment mainAppointment) {
		List<IAppointment> linkedAppointments = new ArrayList<>();
		String extension = mainAppointment.getExtension();

		if (extension != null && !extension.isEmpty()) {
			String[] parts = extension.split(",");
			for (String part : parts) {
				if (part.startsWith("Kombi:")) {
					String id = part.replace("Kombi:", "").trim();
					Optional<IAppointment> linkedAppointment = new AppointmentLoader().findAppointmentById(id);
					linkedAppointment.ifPresent(linkedAppointments::add);
				}
			}
		}

		return linkedAppointments;
	}


	public static boolean isMainAppointment(IAppointment appointment) {
		String extension = appointment.getExtension();
		if (extension != null && !extension.isEmpty()) {
			String[] parts = extension.split(",");
			for (String part : parts) {
				if (part.startsWith("Main:")) {
					String mainId = part.replace("Main:", "").trim();
					return appointment.getId().equals(mainId);
				}
			}
		}
		return false;
	}

	public static String extractMainAppointmentId(String extension) {
		String[] parts = extension.split(",");
		for (String part : parts) {
			if (part.startsWith("Main:")) {
				return part.replace("Main:", "").trim();
			}
		}
		return null;
	}
}
