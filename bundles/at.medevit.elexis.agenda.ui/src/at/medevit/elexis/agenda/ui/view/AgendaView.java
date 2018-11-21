package at.medevit.elexis.agenda.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import at.medevit.elexis.agenda.ui.composite.ParallelComposite;
import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.composite.WeekComposite;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;

public class AgendaView extends ViewPart {
	
	private Composite container;
	
	private Composite parallelParent;
	private ParallelComposite parallelComposite;
	
	private Composite weekParent;
	private WeekComposite weekComposite;
	
	private StackLayout stackLayout;
	
	private ElexisUiEventListenerImpl reloadListener =
		new ElexisUiEventListenerImpl(Termin.class, ElexisEvent.EVENT_RELOAD) {
			@Override
			public void runInUi(ElexisEvent ev){
				if (parallelComposite != null && !parallelComposite.isDisposed()) {
					parallelComposite.refetchEvents();
				}
				if (weekComposite != null && !weekComposite.isDisposed()) {
					weekComposite.refetchEvents();
				}
			}
		};
	
	private SideBarComposite parallelSideBar;
	
	private SideBarComposite weekSideBar;
	
	public AgendaView(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite parent){
		container = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		container.setLayout(stackLayout);
		
		// create week composites
		weekParent = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		weekParent.setLayout(layout);
		
		weekSideBar = new SideBarComposite(weekParent, SWT.NONE);
		weekSideBar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		weekComposite = new WeekComposite(getSite(), weekParent, SWT.NONE);
		weekComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		weekSideBar.setAgendaComposite(weekComposite);
		
		// create parallel composites
		parallelParent = new Composite(container, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parallelParent.setLayout(layout);
		
		parallelSideBar = new SideBarComposite(parallelParent, true, SWT.NONE);
		parallelSideBar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		parallelComposite = new ParallelComposite(getSite(), parallelParent, SWT.NONE);
		parallelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parallelSideBar.setAgendaComposite(parallelComposite);
		
		setTopControl("parallel");
		
		ElexisEventDispatcher.getInstance().addListeners(reloadListener);
	}
	
	@Override
	public void setFocus(){
		if (container != null && !container.isDisposed()) {
			container.setFocus();
		}
	}
	
	public void setTopControl(String name){
		if ("parallel".equalsIgnoreCase(name)) {
			stackLayout.topControl = parallelParent;
		} else if ("week".equalsIgnoreCase(name)) {
			stackLayout.topControl = weekParent;
		}
		container.layout();
	}
	
	public SideBarComposite getParallelSideBarComposite(){
		return parallelSideBar;
	}
}
