package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.rcprap.SingleSourceUtil;
import at.medevit.elexis.agenda.ui.view.AgendaView;

public abstract class AbstractBrowserFunction extends BrowserFunction {

	public AbstractBrowserFunction(Browser browser, String name) {
		super(browser, name);
	}

	public static Optional<SideBarComposite> getActiveSideBar(MPart part) {
		SideBarComposite sideBar = null;
		if (part.getObject() instanceof AgendaView) {
			AgendaView view = (AgendaView) part.getObject();
			sideBar = view.getParallelSideBarComposite();
		}
		return Optional.ofNullable(sideBar);
	}

	public void updateCalendarHeight() {
		String updateHeight = "$('#calendar').fullCalendar('option', 'contentHeight', (%d - $('#calendar').find('.fc-view-container').offset().top));"; //$NON-NLS-1$
		int browserHeight = getBrowser().getBounds().height;
		String script = String.format(updateHeight, browserHeight);
		SingleSourceUtil.executeScript(getBrowser(), script);
	}

	public void redraw() {
		String refetchEvents = "$('#calendar').fullCalendar('rerenderEvents');"; //$NON-NLS-1$
		SingleSourceUtil.executeScript(getBrowser(), refetchEvents);
	}

	@Override
	public boolean isDisposed() {
		return super.isDisposed();
	}

	protected LocalDateTime getDateTimeArg(Object object) {
		if (object instanceof String) {
			if (((String) object).length() == 10) {
				return LocalDate.parse((String) object).atStartOfDay();
			} else if (((String) object).length() == 19) {
				return LocalDateTime.parse((String) object);
			}
		} else {
			throw new IllegalArgumentException("Unexpected argument [" + object + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

	protected LocalDate getDateArg(Object object) {
		if (object instanceof String) {
			if (((String) object).length() == 10) {
				return LocalDate.parse((String) object);
			}
		} else {
			throw new IllegalArgumentException("Unexpected argument [" + object + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
}
