package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

public abstract class AbstractBrowserFunction extends BrowserFunction {
	
	public AbstractBrowserFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public void updateCalendarHeight(){
		String updateHeight =
			"$('#calendar').fullCalendar('option', 'contentHeight', (%d - $('#calendar').find('.fc-view-container').offset().top));";
		int browserHeight = getBrowser().getBounds().height;
		getBrowser().execute(String.format(updateHeight, browserHeight));
	}
	
	public void redraw(){
		String refetchEvents = "$('#calendar').fullCalendar('rerenderEvents');";
		getBrowser().execute(refetchEvents);
	}
	
	@Override
	public boolean isDisposed(){
		return super.isDisposed();
	}
	
	protected LocalDateTime getDateTimeArg(Object object){
		if (object instanceof String) {
			if (((String) object).length() == 10) {
				return LocalDate.parse((String) object).atStartOfDay();
			} else if (((String) object).length() == 19) {
				return LocalDateTime.parse((String) object);
			}
		} else {
			throw new IllegalArgumentException("Unexpected argument [" + object + "]");
		}
		return null;
	}
	
	protected LocalDate getDateArg(Object object){
		if (object instanceof String) {
			if (((String) object).length() == 10) {
				return LocalDate.parse((String) object);
			}
		} else {
			throw new IllegalArgumentException("Unexpected argument [" + object + "]");
		}
		return null;
	}
}
