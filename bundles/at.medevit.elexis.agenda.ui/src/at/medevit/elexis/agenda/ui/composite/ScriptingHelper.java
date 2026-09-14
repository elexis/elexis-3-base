package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import com.equo.chromium.swt.Browser;

import at.medevit.elexis.agenda.ui.composite.IAgendaComposite.AgendaSpanSize;
import at.medevit.elexis.agenda.ui.rcprap.SingleSourceUtil;

public class ScriptingHelper {

	private static final String SET_SLOT_DURATION = "$('#calendar').fullCalendar('option', 'slotDuration', '%s');"; //$NON-NLS-1$

	/**
	 * Script changing the {@code slotDuration} (e.g., {@code 00:10:00}) while maintaining the current scroll position.
	 * <p>
	 * FullCalendar normally causes the view to drift when the slot duration changes. 
	 * To prevent visual jumping, this script synchronously calculates and reapplies the relative scroll 
	 * position (ratio of scrollTop to scrollHeight) during the update.
	 *
	 * @see #SCROLL_TO_NOW
	 */
	private static final String KEEP_SCROLL_TIME = "(function(){var c=$('#calendar');var o=c.fullCalendar('getView');" //$NON-NLS-1$
			+ "var f=(o&&o.scroller&&o.scroller.el[0].scrollHeight)" //$NON-NLS-1$
			+ "?o.scroller.getScrollTop()/o.scroller.el[0].scrollHeight:0;" //$NON-NLS-1$
			+ "c.fullCalendar('option','slotDuration','%s');" //$NON-NLS-1$
			+ "var v=c.fullCalendar('getView');" //$NON-NLS-1$
			+ "if(f&&v&&v.scroller){v.applyScroll({top:Math.round(f*v.scroller.el[0].scrollHeight)});}})();"; //$NON-NLS-1$

	/**
	 * Script vertically centering the current time in the agenda view. Takes no format arguments.
	 * <p>
	 * It safely aborts if the view is not yet rendered or if the current time is outside the visible dates. 
	 * The target position is calculated directly from the time of day rather than UI elements, 
	 * allowing it to run synchronously and without visual flickering.
	 *
	 * @see #KEEP_SCROLL_TIME
	 */
	private static final String SCROLL_TO_NOW = "(function(){var c=$('#calendar');var v=c.fullCalendar('getView');" //$NON-NLS-1$
			+ "if(!v||!v.timeGrid){return;}var n=c.fullCalendar('getNow');" //$NON-NLS-1$
			+ "if(n<v.intervalStart||n>=v.intervalEnd){return;}" //$NON-NLS-1$
			+ "var t=v.timeGrid.computeTimeTop(moment.duration(n.format('HH:mm:ss')));" //$NON-NLS-1$
			+ "v.applyScroll({top:Math.max(0,Math.ceil(t-v.scroller.el.height()/2))});})();"; //$NON-NLS-1$

	private Browser browser;

	private volatile boolean doScroll;

	public ScriptingHelper(Browser browser) {
		this.browser = browser;
	}

	public void setSelectedSpanSize(AgendaSpanSize size) {
		String script = doScroll ? String.format(SET_SLOT_DURATION, size.getCalendarString()) + SCROLL_TO_NOW
				: String.format(KEEP_SCROLL_TIME, size.getCalendarString());
		SingleSourceUtil.executeScript(browser, script);
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

	public void render() {
		String refetchEvents = "$('#calendar').fullCalendar('render');"; //$NON-NLS-1$
		SingleSourceUtil.executeScript(browser, refetchEvents);
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
			String script = "setTimeout(function(){" + SCROLL_TO_NOW + "}, 500);"; //$NON-NLS-1$ //$NON-NLS-2$
			SingleSourceUtil.executeScript(browser, script);
		}
	}

	public void setScrollToNow(boolean value) {
		this.doScroll = value;
		scrollToNow();
	}

	public void setShowWeekends(boolean value) {
		String script = "$('#calendar').fullCalendar('option', 'weekends', %s)"; //$NON-NLS-1$
		script = String.format(script, Boolean.valueOf(value));
		SingleSourceUtil.executeScript(browser, script);
	}
}
