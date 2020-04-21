package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.function.ContextMenuFunction;
import at.medevit.elexis.agenda.ui.function.DayClickFunction;
import at.medevit.elexis.agenda.ui.function.DoubleClickFunction;
import at.medevit.elexis.agenda.ui.function.EventDropFunction;
import at.medevit.elexis.agenda.ui.function.EventResizeFunction;
import at.medevit.elexis.agenda.ui.function.LoadEventsFunction;
import at.medevit.elexis.agenda.ui.function.SingleClickFunction;
import at.medevit.elexis.agenda.ui.function.SwitchFunction;
import at.medevit.elexis.agenda.ui.rcprap.SingleSourceUtil;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class WeekComposite extends Composite implements ISelectionProvider, IAgendaComposite {
	
	private static Logger logger = LoggerFactory.getLogger(WeekComposite.class);
	
	private Browser browser;
	private LoadEventsFunction loadEventsFunction;
	
	private ScriptingHelper scriptingHelper;
	
	private ISelection currentSelection;
	private ListenerList<ISelectionChangedListener> listeners = new ListenerList<>();
	private AgendaSpanSize currentSpanSize;
	private DayClickFunction dayClickFunction;
	
	private ESelectionService selectionService;
	
	public WeekComposite(MPart part, ESelectionService selectionService, EMenuService menuService,
		Composite parent, int style){
		this(part, selectionService, menuService, parent, style, false);
	}
	
	public WeekComposite(MPart part, ESelectionService selectionService, EMenuService menuService,
		Composite parent, int style,
		boolean enableSwitch){
		super(parent, style);
		this.selectionService = selectionService;
		setLayout(new FillLayout());
		browser = new Browser(this, SWT.NONE);
		scriptingHelper = new ScriptingHelper(browser);
		
		loadEventsFunction = new LoadEventsFunction(browser, "loadEventsFunction", scriptingHelper);
		
		new SingleClickFunction(browser, "singleClickFunction").setSelectionProvider(this);
		
		new DoubleClickFunction(browser, "doubleClickFunction");
		
		new ContextMenuFunction(part, browser, "contextMenuFunction").setSelectionProvider(this);
		
		new EventDropFunction(browser, "eventDropFunction");
		
		new EventResizeFunction(browser, "eventResizeFunction");
		
		dayClickFunction = new DayClickFunction(browser, "dayClickFunction");
		
		if (enableSwitch) {
			new SwitchFunction(part, browser, "switchFunction");
			String targetUrl = SingleSourceUtil.resolve("switchWeek.html");
			logger.debug("Open url [" + targetUrl + "]");
			browser.setUrl(targetUrl);
			
		} else {
			String targetUrl = SingleSourceUtil.resolve("defaultWeek.html");
			logger.debug("Open url [" + targetUrl + "]");
			browser.setUrl(targetUrl);

		}
		
		browser.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e){
				loadEventsFunction.updateCalendarHeight();
			}
		});
		
		// register context menu for browser
		menuService.registerContextMenu(browser, "at.medevit.elexis.agenda.ui.popupmenu.week");
		
		browser.addProgressListener(new ProgressListener() {
			@Override
			public void changed(ProgressEvent event){
				if (event.current == event.total) {
					Display.getDefault().timerExec(250, () -> {
						String dayStartsAt = ConfigServiceHolder.get()
							.get("agenda/beginnStundeTagesdarstellung", "0000");
						String dayEndsAt = ConfigServiceHolder.get()
							.get("agenda/endStundeTagesdarstellung", "2359");
						scriptingHelper.setCalenderTime(dayStartsAt, dayEndsAt);
						
						if (currentSpanSize != null) {
							setSelectedSpanSize(currentSpanSize);
						}
						
						getConfiguredFontSize().ifPresent(size -> {
							setFontSize(size);
							getConfiguredFontFamily().ifPresent(family -> setFontFamily(family));
						});
					});
				}
			}

			@Override
			public void completed(ProgressEvent event){}
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
		selectionService.setSelection(currentSelection);
	}
	
	@Override
	public void setScrollToNow(boolean value){
		scriptingHelper.setScrollToNow(value);
	}
	
	public LoadEventsFunction getLoadEventsFunction(){
		return loadEventsFunction;
	}
}
