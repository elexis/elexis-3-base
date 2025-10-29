package ch.elexis.agenda.util;

import ch.elexis.agenda.Messages;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.ui.util.SWTHelper;

/**
 * Utility class for working with {@link IAppointment} instances.
 * <p>
 * Provides helper methods for checking appointment states.
 * </p>
 *
 * @author Dalibor Aksic
 *
 */
public final class AppointmentUtil {

	/**
	 * Checks whether the given appointment is locked. If it is, a warning dialog
	 * will be displayed and the method returns {@code true}.
	 *
	 * @param appointment the {@link IAppointment} to check (may be {@code null})
	 * @return {@code true} if the appointment is locked, {@code false} otherwise
	 */
	public static boolean isLocked(IAppointment appointment) {
		if (appointment != null && appointment.isLocked()) {
			SWTHelper.alert(Messages.Termin_appointment_locked, Messages.Termin_appCantBeChanged);
			return true;
		}
		return false;
	}
}
