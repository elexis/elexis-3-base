package at.medevit.elexis.agenda.ui.view;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.composite.WeekComposite;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;

public class WeekView extends ViewPart {
	
	private WeekComposite composite;
	
	public WeekView(){
		// TODO Auto-generated constructor stub
	}
	
	@Inject
	@Optional
	public void reload(@UIEventTopic(ElexisEventTopics.EVENT_RELOAD) Class<?> clazz){
		if (IAppointment.class.equals(clazz)) {
			if (composite != null && !composite.isDisposed()) {
				composite.refetchEvents();
			}
		}
	}
	
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
		composite = new WeekComposite(getSite(), container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sideBar.setAgendaComposite(composite);
	}
	
	@Override
	public void setFocus(){
		if (composite != null && !composite.isDisposed()) {
			composite.setFocus();
		}
	}
}
