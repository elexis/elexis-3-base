package at.medevit.elexis.agenda.ui.composite;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPartSite;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.function.ContextMenuFunction;
import at.medevit.elexis.agenda.ui.function.DayClickFunction;
import at.medevit.elexis.agenda.ui.function.DoubleClickFunction;
import at.medevit.elexis.agenda.ui.function.EventDropFunction;
import at.medevit.elexis.agenda.ui.function.EventResizeFunction;
import at.medevit.elexis.agenda.ui.function.LoadEventsFunction;
import at.medevit.elexis.agenda.ui.function.SingleClickFunction;
import at.medevit.elexis.agenda.ui.function.SwitchFunction;
import ch.elexis.core.data.activator.CoreHub;

public class WeekComposite extends Composite implements ISelectionProvider, IAgendaComposite {
	
	private Browser browser;
	private LoadEventsFunction loadEventsFunction;
	
	private ScriptingHelper scriptingHelper;
	
	private ISelection currentSelection;
	private ListenerList listeners = new ListenerList();
	private AgendaSpanSize currentSpanSize;
	private DayClickFunction dayClickFunction;
	
	public WeekComposite(IWorkbenchPartSite partSite, Composite parent, int style){
		this(partSite, parent, style, false);
	}
	
	public WeekComposite(IWorkbenchPartSite partSite, Composite parent, int style,
		boolean enableSwitch){
		super(parent, style);
		setLayout(new FillLayout());
		browser = new Browser(this, SWT.NONE);
		scriptingHelper = new ScriptingHelper(browser);
		
		loadEventsFunction = new LoadEventsFunction(browser, "loadEventsFunction", scriptingHelper);
		
		new SingleClickFunction(browser, "singleClickFunction").setSelectionProvider(this);
		
		new DoubleClickFunction(browser, "doubleClickFunction");
		
		new ContextMenuFunction(browser, "contextMenuFunction").setSelectionProvider(this);
		
		new EventDropFunction(browser, "eventDropFunction");
		
		new EventResizeFunction(browser, "eventResizeFunction");
		
		dayClickFunction = new DayClickFunction(browser, "dayClickFunction");
		
		if (enableSwitch) {
			new SwitchFunction(browser, "switchFunction");
			try {
				URL url = FileLocator.toFileURL(
					FrameworkUtil.getBundle(getClass()).getResource("/rsc/html/switchWeek.html"));
				LoggerFactory.getLogger(getClass()).debug("Open url [" + url.getFile() + "]");
				browser.setUrl(url.toString());
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass())
					.error("Could not set url to /rsc/html/switchWeek.html", e);
			}
		} else {
			try {
				URL url = FileLocator.toFileURL(
					FrameworkUtil.getBundle(getClass()).getResource("/rsc/html/defaultWeek.html"));
				LoggerFactory.getLogger(getClass()).debug("Open url [" + url.getFile() + "]");
				browser.setUrl(url.toString());
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass())
					.error("Could not set url to /rsc/html/defaultWeek.html", e);
			}
		}
		
		browser.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e){
				loadEventsFunction.updateCalendarHeight();
			}
		});
		
		// register context menu for table viewer
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(browser);
		
		browser.setMenu(menu);
		if (partSite != null) {
			partSite.setSelectionProvider(this);
			partSite.registerContextMenu("at.medevit.elexis.agenda.ui.week", menuManager, this);
		}
		
		browser.addProgressListener(new ProgressAdapter() {
			@Override
			public void changed(ProgressEvent event){
				if (event.current == 0 && event.total == 0) {
					String dayStartsAt =
						CoreHub.globalCfg.get("agenda/beginnStundeTagesdarstellung", "0000");
					String dayEndsAt =
						CoreHub.globalCfg.get("agenda/endStundeTagesdarstellung", "2359");
					scriptingHelper.setCalenderTime(dayStartsAt, dayEndsAt);
					
					if (currentSpanSize != null) {
						setSelectedSpanSize(currentSpanSize);
					}
					
					getConfiguredFontSize().ifPresent(size -> {
						setFontSize(size);
						getConfiguredFontFamily().ifPresent(family -> setFontFamily(family));
					});
				}
			}
		});
	}
	
	@Override
	public boolean setFocus(){
		refetchEvents();
		return browser.setFocus();
	}
	
	public void refetchEvents(){
		scriptingHelper.refetchEvents();
	}
	
	@Override
	public void setSelectedDate(LocalDate date){
		scriptingHelper.setSelectedDate(date);
	}
	
	@Override
	public void setFontSize(int sizePx){
		scriptingHelper.setFontSize(sizePx);
	}
	
	@Override
	public void setFontFamily(String family){
		scriptingHelper.setFontFamily(family);
	}
	
	@Override
	public void setSelectedSpanSize(AgendaSpanSize size){
		currentSpanSize = size;
		scriptingHelper.setSelectedSpanSize(size);
	}
	
	@Override
	public void setSelectedResources(List<String> selectedResources){
		loadEventsFunction.setResources(selectedResources);
		dayClickFunction.setSelectedResources(selectedResources);
		refetchEvents();
	}
	
	@Override
	public String getConfigId(){
		return "week";
	}
	
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener){
		listeners.add(listener);
	}
	
	@Override
	public ISelection getSelection(){
		if (currentSelection != null) {
			return currentSelection;
		}
		return StructuredSelection.EMPTY;
	}
	
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener){
		listeners.remove(listener);
	}
	
	@Override
	public void setSelection(ISelection selection){
		currentSelection = selection;
		for (Object listener : listeners.getListeners()) {
			((ISelectionChangedListener) listener)
				.selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}
	
	@Override
	public void setScrollToNow(boolean value){
		scriptingHelper.setScrollToNow(value);
	}
	
	public LoadEventsFunction getLoadEventsFunction(){
		return loadEventsFunction;
	}
}
