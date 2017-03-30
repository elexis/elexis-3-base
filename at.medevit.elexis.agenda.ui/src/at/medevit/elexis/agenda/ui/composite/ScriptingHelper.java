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
