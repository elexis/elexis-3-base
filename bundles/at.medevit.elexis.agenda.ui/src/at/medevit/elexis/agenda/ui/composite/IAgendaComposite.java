package at.medevit.elexis.agenda.ui.composite;

import org.apache.commons.lang3.StringUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import ch.elexis.core.services.holder.ConfigServiceHolder;

public interface IAgendaComposite {

	public enum AgendaSpanSize {
		MIN5("5 min", "00:05:00"), MIN10("10 min", "00:10:00"), MIN15("15 min", "00:15:00"),
		MIN20("20 min", "00:20:00"), MIN30("30 min", "00:30:00");

		private String label;
		private String calendarString;

		private AgendaSpanSize(String label, String calendarString) {
			this.label = label;
			this.calendarString = calendarString;
		}

		public String getLabel() {
			return label;
		}

		public Object getCalendarString() {
			return calendarString;
		}
	}

	public default Optional<Integer> getConfiguredFontSize() {
		String confFont = ConfigServiceHolder.get()
				.getActiveUserContact(ch.elexis.core.constants.Preferences.USR_AGENDAFONT, StringUtils.EMPTY);
		String[] parts = confFont.split("\\|");
		if (parts.length > 3) {
			try {
				Float floatValue = Float.parseFloat(parts[2].trim());
				return Optional.of(floatValue.intValue());
			} catch (NumberFormatException e) {
				LoggerFactory.getLogger(getClass()).debug("Could not parse font size [" + parts[2] + "]");
			}
		}
		return Optional.empty();
	}

	public default Optional<String> getConfiguredFontFamily() {
		String confFont = ConfigServiceHolder.get()
				.getActiveUserContact(ch.elexis.core.constants.Preferences.USR_AGENDAFONT, StringUtils.EMPTY);
		String[] parts = confFont.split("\\|");
		if (parts.length > 3) {
			return Optional.of(parts[1]);
		}
		return Optional.empty();
	}

	public String getConfigId();

	/**
	 * Re-fetch the events, and refresh the display.
	 */
	public void refetchEvents();

	/**
	 * Set the currently displayed date.
	 *
	 * @param date
	 */
	public void setSelectedDate(LocalDate date);

	/**
	 * Set the list of selected resources to display.
	 *
	 * @param selectedResources
	 */
	public void setSelectedResources(List<String> selectedResources);

	/**
	 * Set the span size to display.
	 *
	 * @param size
	 */
	public void setSelectedSpanSize(AgendaSpanSize size);

	/**
	 * Set if the agenda should automatically scroll to now if possible. Default is
	 * false.
	 *
	 * @param value
	 */
	public void setScrollToNow(boolean value);

	/**
	 * Set the font-size of the displayed web agenda.
	 *
	 * @param sizePx
	 */
	public void setFontSize(int sizePx);

	/**
	 * Set the font-family of the displayed web agenda.
	 *
	 * @param family
	 */
	public void setFontFamily(String family);
}
