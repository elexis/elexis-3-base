package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import com.equo.chromium.swt.Browser;

import at.medevit.elexis.agenda.ui.composite.IAgendaComposite.AgendaSpanSize;
import at.medevit.elexis.agenda.ui.rcprap.SingleSourceUtil;

public class ScriptingHelper {

	private Browser browser;

	private volatile boolean doScroll;

	public ScriptingHelper(Browser browser) {
		this.browser = browser;
	}

	public void setSelectedSpanSize(AgendaSpanSize size) {
		String slotDuration = "$('#calendar').fullCalendar('option', 'slotDuration', '%s');"; //$NON-NLS-1$
		String script = String.format(slotDuration, size.getCalendarString());
		SingleSourceUtil.executeScript(browser, script);
		scrollToNow();
	}

	/**
	 * parses a time of format 1900 to 19:00:00
	 *
	 * @param time
	 * @return
	 */
	private String parseTime(String time) {
		if (time.length() < 6 || time.lastIndexOf(":") != 5) { //$NON-NLS-1$
			StringBuilder builder = new StringBuilder(6);
			time = time.replaceAll(":", StringUtils.EMPTY); //$NON-NLS-1$
			int length = time.length();
			for (int i = 0; i < 6; i++) {
				if (i > 0 && i % 2 == 0) {
					builder.append(":"); //$NON-NLS-1$
				}
				if (i < length) {
					char c = time.charAt(i);
					builder.append(c);
				} else {
					builder.append("0"); //$NON-NLS-1$
				}
			}
			return builder.toString();
		}
		return time;
	}

	public void setCalenderTime(String dayStartsAt, String dayEndsAt) {

		String endsAt = "$('#calendar').fullCalendar('option', 'maxTime', '%s');"; //$NON-NLS-1$
		String startAt = "$('#calendar').fullCalendar('option', 'minTime', '%s');"; //$NON-NLS-1$
		String script = String.format(endsAt, parseTime(dayEndsAt)) + String.format(startAt, parseTime(dayStartsAt));
		SingleSourceUtil.executeScript(browser, script);
		scrollToNow();
	}

	public void setSelectedDate(LocalDate date) {
		String gotoDate = "$('#calendar').fullCalendar('gotoDate', '%s');"; //$NON-NLS-1$
		String script = String.format(gotoDate, date.toString());
		SingleSourceUtil.executeScript(browser, script);
	}

	public void setFontSize(int sizePx) {
		String bodyFontSize = "$('body').css('font-size', '%dpx');"; //$NON-NLS-1$
		String script = String.format(bodyFontSize, sizePx);
		SingleSourceUtil.executeScript(browser, script);
	}

	public void setFontFamily(String family) {
		String bodyFontFamily = "$('body').css('font-family', '%s');"; //$NON-NLS-1$
		String script = String.format(bodyFontFamily, family);
		SingleSourceUtil.executeScript(browser, script);
	}

	public void refetchEvents() {
		String refetchEvents = "$('#calendar').fullCalendar('refetchEvents');"; //$NON-NLS-1$
		SingleSourceUtil.executeScript(browser, refetchEvents);
	}

	public void refetchResources() {
		String refetchResources = "$('#calendar').fullCalendar('refetchResources');";//$NON-NLS-1$
		SingleSourceUtil.executeScript(browser, refetchResources);
	}

	public void scrollToNow() {
		if (doScroll) {
			String script = "var now = $('#calendar').fullCalendar('getNow'); if (now >= $('#calendar').fullCalendar('getView').intervalStart && now < $('#calendar').fullCalendar('getView').intervalEnd){ setTimeout( function(){$('.fc-scroller').scrollTop($('.fc-now-indicator').position().top - ($('#calendar').height() / 2) );}  , 500 );}"; //$NON-NLS-1$
			SingleSourceUtil.executeScript(browser, script);
		}
	}

	public void setScrollToNow(boolean value) {
		this.doScroll = value;
		scrollToNow();
	}
}
