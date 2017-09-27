package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDate;
import java.util.List;

public interface IAgendaComposite {
	
	public enum AgendaSpanSize {
			MIN5("5 min", "00:05:00"), MIN10("10 min", "00:10:00"), MIN15("15 min", "00:15:00"),
			MIN20("20 min", "00:20:00"),
			MIN30("30 min", "00:30:00");
		
		private String label;
		private String calendarString;
		
		private AgendaSpanSize(String label, String calendarString){
			this.label = label;
			this.calendarString = calendarString;
		}
		
		public String getLabel(){
			return label;
		}
		
		public Object getCalendarString(){
			return calendarString;
		}
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
}
