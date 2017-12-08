package at.medevit.elexis.agenda.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import at.medevit.elexis.agenda.ui.composite.ParallelComposite;
import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;

public class ParallelView extends ViewPart {
	
	private ParallelComposite composite;
	
	private ElexisUiEventListenerImpl reloadListener =
		new ElexisUiEventListenerImpl(Termin.class, ElexisEvent.EVENT_RELOAD) {
			@Override
			public void runInUi(ElexisEvent ev){
				if (composite != null && !composite.isDisposed()) {
					composite.refetchEvents();
				}
			}
		};
	
	public ParallelView(){}
	
	@Override
	public void createPartControl(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		
		SideBarComposite sideBar = new SideBarComposite(container, SWT.NONE);
		sideBar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		composite = new ParallelComposite(getSite(), container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sideBar.setAgendaComposite(composite);
		
		ElexisEventDispatcher.getInstance().addListeners(reloadListener);
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(reloadListener);
	}
	
	@Override
	public void setFocus(){
		if (composite != null && !composite.isDisposed()) {
			composite.setFocus();
		}
	}
}
