package ch.elexis.agenda.util;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.agenda.Messages;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;

/**
 * Utility class for working with {@link IAppointment} instances.
 * <p>
 * Provides helper methods for checking appointment states, creating shallow
 * copies, and detecting modifications between two appointment objects.
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
	public static boolean checkLocked(IAppointment appointment) {
		if (appointment != null && appointment.isLocked()) {
			SWTHelper.alert(Messages.Termin_appointment_locked, Messages.Termin_appCantBeChanged);
			return true;
		}
		return false;
	}

	/**
	 * Creates a shallow, non-persistent copy of the given appointment.
	 * <p>
	 * Only basic field values (schedule, type, state, reason, subject/patient,
	 * start/end times, and lock state) are copied. The returned appointment is
	 * <strong>not saved</strong> to the database.
	 * </p>
	 *
	 * @param appointment the appointment to copy (may be {@code null})
	 * @return a shallow copy of the given appointment, or {@code null} if the input
	 *         is {@code null}
	 */
	public static IAppointment shallowCopy(IAppointment appointment) {
		if (appointment == null) {
			return null;
		}

		IAppointment copy = CoreModelServiceHolder.get().create(IAppointment.class);
		copy.setSchedule(appointment.getSchedule());
		copy.setType(appointment.getType());
		copy.setState(appointment.getState());
		copy.setReason(appointment.getReason());
		copy.setSubjectOrPatient(appointment.getSubjectOrPatient());
		copy.setStartTime(appointment.getStartTime());
		copy.setEndTime(appointment.getEndTime());
		copy.setLocked(appointment.isLocked());
		return copy;
	}

	/**
	 * Determines whether two appointment instances differ in any of their key
	 * fields. This method performs a field-by-field comparison and returns
	 * {@code true} if any difference is detected.
	 *
	 * @param original the original appointment (may be {@code null})
	 * @param current  the current appointment (may be {@code null})
	 * @return {@code true} if the appointments differ, {@code false} if they are
	 *         equal or if one of them is {@code null}
	 */
	public static boolean isModified(IAppointment original, IAppointment current) {
		if (original == null || current == null) {
			return false;
		}

		return !(StringUtils.equals(original.getType(), current.getType())
				&& StringUtils.equals(original.getState(), current.getState())
				&& StringUtils.equals(original.getReason(), current.getReason())
				&& StringUtils.equals(original.getSchedule(), current.getSchedule())
				&& StringUtils.equals(original.getSubjectOrPatient(), current.getSubjectOrPatient())
				&& equalsDateTime(original.getStartTime(), current.getStartTime())
				&& equalsDateTime(original.getEndTime(), current.getEndTime()));
	}

	/**
	 * Compares two {@link LocalDateTime} instances for equality.
	 *
	 * @param a the first date-time (may be {@code null})
	 * @param b the second date-time (may be {@code null})
	 * @return {@code true} if both are equal or both {@code null}, {@code false}
	 *         otherwise
	 */
	private static boolean equalsDateTime(LocalDateTime a, LocalDateTime b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}
}
