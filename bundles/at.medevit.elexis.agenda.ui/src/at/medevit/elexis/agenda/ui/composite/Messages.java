package at.medevit.elexis.agenda.ui.composite;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
	public static String AgendaUI_DayOverView_create_or_change;
	public static String AgendaUI_DayOverView_date_collision;
	public static String AgendaUI_SideBar_abort_move_date;
	public static String AgendaUI_SideBar_auto_scroll_to_now;
	public static String AgendaUI_SideBar_create_new_series;
	public static String AgendaUI_SideBar_move_date;
	public static String AgendaUI_SideBar_range;
	public static String AgendaUI_SideBar_steps;
	public static String AppointmentDetailComposite_date_type_or_status;
	public static String AppointmentDetailComposite_delete;
	public static String AppointmentDetailComposite_duration;
	public static String AppointmentDetailComposite_expand;
	public static String AppointmentDetailComposite_expand_hover;
	public static String AppointmentDetailComposite_freetext;
	public static String AppointmentDetailComposite_insert;
	public static String AppointmentDetailComposite_isAllDay;
	public static String AppointmentDetailComposite_name_birthday_patNr_or_free;
	public static String AppointmentDetailComposite_no_patient_selected;
	public static String AppointmentDetailComposite_planned_dates;
	public static String AppointmentDetailComposite_print;
	public static String AppointmentDetailComposite_range;
	public static String AppointmentDetailComposite_reason;
	public static String AppointmentDetailComposite_search;
	public static String AppointmentDetailComposite_search_contact_via_fields;
	public static String AppointmentDetailComposite_search_contact_via_fields_hover;
	public static String AppointmentDetailComposite_starting_from;
	public static String AppointmentDetailComposite_tag;
	public static String AppointmentDetailComposite_until;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
