package at.medevit.elexis.agenda.ui.model;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.model.agenda.RecurringAppointment;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.AppointmentState;
import ch.elexis.core.types.AppointmentType;

/**
 * Model element representing an event than can be rendered using the javascript
 * calendar contained in this bundle. Transfer to javascript is done using JSON.
 *
 * @author thomas
 *
 */
public class Event {

	private String id;
	private String title;
	private String icon;

	private String start;
	private String end;

	private String borderColor;
	private String backgroundColor;
	private String textColor;

	private String rendering;
	private String description;
	private String resource;

	// https://fullcalendar.io/docs/event-object
	private boolean allDay = false;

	private String tel;
	private String mail;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public String getRendering() {
		return rendering;
	}

	public void setRendering(String rendering) {
		this.rendering = rendering;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public String getMail() {
		return mail;
	}

	public String getTel() {
		return tel;
	}

	/**
	 * Factory method to create a Event from an {@link IPeriod}.
	 *
	 * @param iPeriod
	 * @return
	 */
	public static Event of(IPeriod iPeriod, IContact userContact) {
		Event ret = new Event();
		ret.id = iPeriod.getId();
		ret.start = iPeriod.getStartTime().toString();
		ret.end = (iPeriod.getEndTime()) != null ? iPeriod.getEndTime().toString() : null;
		if (iPeriod instanceof IAppointment) {
			IAppointment termin = (IAppointment) iPeriod;
			ret.resource = termin.getSchedule();
			IAppointment rootTermin = null;
			if (termin.isRecurring() && (rootTermin = new RecurringAppointment(termin, CoreModelServiceHolder.get())
					.getRootAppoinemtent()) != null) {
				ret.icon = "ui-icon-arrowrefresh-1-w"; //$NON-NLS-1$
				ret.title = rootTermin.getSubjectOrPatient();
			} else {
				ret.title = termin.getSubjectOrPatient();
			}
			IContact contact = termin.getContact();
			if (contact != null) {
				if (StringUtils.isNotBlank(contact.getEmail())) {
					ret.mail = contact.getEmail();
				}
				if (ret.mail == null && StringUtils.isNotBlank(contact.getEmail2())) {
					ret.mail = contact.getEmail2();
				}
				if (StringUtils.isNotBlank(contact.getMobile())) {
					ret.tel = contact.getMobile();
				}
				if (ret.tel == null && StringUtils.isNotBlank(contact.getPhone1())) {
					ret.tel = contact.getPhone1();
				}
			}
			// fullcalendar will no create title html div if no title is blank, add space
			if (ret.title.isEmpty()) {
				ret.title = StringUtils.SPACE;
			}
			ret.description = termin.getReason().replaceAll(StringUtils.LF, "<br />") + "<br /><br />" + termin //$NON-NLS-1$ //$NON-NLS-2$
					.getStateHistoryFormatted("dd.MM.yyyy HH:mm:ss").replaceAll(StringUtils.LF, "<br />"); //$NON-NLS-1$ //$NON-NLS-2$
			ret.borderColor = AppointmentServiceHolder.get().getContactConfiguredStateColor(userContact,
					termin.getState());
			ret.backgroundColor = AppointmentServiceHolder.get().getContactConfiguredTypeColor(userContact,
					termin.getType());
			ret.textColor = getTextColor(ret.backgroundColor.substring(1));
			if (ret.end == null) {
				ret.allDay = true;
			}
		}
		return ret;
	}

	/**
	 * Test if a {@link IPeriod} is a day limit.
	 *
	 * @param iPeriod
	 * @return
	 */
	public static boolean isDayLimit(IPeriod iPeriod) {
		if (iPeriod instanceof IAppointment) {
			String type = ((IAppointment) iPeriod).getType();
			String status = ((IAppointment) iPeriod).getState();
			String emptyStateString = AppointmentServiceHolder.get().getState(AppointmentState.EMPTY);
			String reservedTypeString = AppointmentServiceHolder.get().getType(AppointmentType.BOOKED);
			return type.equals(reservedTypeString) && status.equals(emptyStateString);
		}
		return false;
	}

	/**
	 * Calculate the brightness of the bgColor and switch text color, black or
	 * white, depending on that value.
	 *
	 * @param bgColor
	 * @return
	 */
	public static String getTextColor(String bgColor) {
		try {
			if (bgColor.startsWith("#")) {
				bgColor = bgColor.substring(1);
			}
			int brightness = getPerceivedBrightness(Integer.parseInt(bgColor.substring(0, 2), 16),
					Integer.parseInt(bgColor.substring(2, 4), 16), Integer.parseInt(bgColor.substring(4, 6), 16));

			return brightness > 130 ? "#000000" : "#ffffff"; //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NumberFormatException e) {
			LoggerFactory.getLogger(Event.class)
					.warn("Backgound color of event [" + bgColor + "] is no valid color number");
			return "#ffffff"; //$NON-NLS-1$
		}
	}

	private static int getPerceivedBrightness(int red, int green, int blue) {
		return (int) Math.sqrt(red * red * .299 + green * green * .587 + blue * blue * .114);
	}
}
