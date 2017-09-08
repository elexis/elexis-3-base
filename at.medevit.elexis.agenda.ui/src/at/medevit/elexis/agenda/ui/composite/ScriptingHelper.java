package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.swt.browser.Browser;

import at.medevit.elexis.agenda.ui.composite.IAgendaComposite.AgendaSpanSize;

public class ScriptingHelper {
	
	private Browser browser;
	
	public ScriptingHelper(Browser browser){
		this.browser = browser;
	}
	
	public void setSelectedSpanSize(AgendaSpanSize size){
		String slotDuration = "$('#calendar').fullCalendar('option', 'slotDuration', '%s');";
		String script = String.format(slotDuration, size.getCalendarString());
		browser.execute(script);
	}
	
	/**
	 * parses a time of format 1900 to 19:00:00
	 * 
	 * @param time
	 * @return
	 */
	private String parseTime(String time){
		if (time.length() < 6 || time.lastIndexOf(":") != 5) {
			StringBuilder builder = new StringBuilder(6);
			time = time.replaceAll(":", "");
			int length = time.length();
			for (int i = 0; i < 6; i++) {
				if (i > 0 && i % 2 == 0) {
					builder.append(":");
				}
				if (i < length) {
					char c = time.charAt(i);
					builder.append(c);
				} else {
					builder.append("0");
				}
			}
			return builder.toString();
		}
		return time;
	}
	
	public void setCalenderTime(String dayStartsAt, String dayEndsAt){
		
		String endsAt = "$('#calendar').fullCalendar('option', 'maxTime', '%s');";
		String startAt = "$('#calendar').fullCalendar('option', 'minTime', '%s');";
		String script = String.format(endsAt, parseTime(dayEndsAt))
			+ String.format(startAt, parseTime(dayStartsAt));
		browser.execute(script);
	}
	
	public void setSelectedDate(LocalDate date){
		String gotoDate = "$('#calendar').fullCalendar('gotoDate', '%s');";
		String script = String.format(gotoDate, date.toString());
		browser.execute(script);
	}
	
	public void refetchEvents(){
		String refetchEvents = "$('#calendar').fullCalendar('refetchEvents');";
		browser.execute(refetchEvents);
	}
	
	public void initializeResources(List<String> selectedResources){
		String updateResourceIds = "$('#calendar').fullCalendar('getView').setResourceIds(%s);";
		String script = String.format(updateResourceIds, getResourceIdsString(selectedResources));
		browser.execute(script);
	}
	
	private Object getResourceIdsString(List<String> selectedResources){
		StringBuilder ret = new StringBuilder();
		ret.append("[");
		for (String calendar : selectedResources) {
			if (ret.length() > 1) {
				ret.append(",");
			}
			ret.append("'").append(calendar).append("'");
		}
		ret.append("]");
		return ret;
	}
}
