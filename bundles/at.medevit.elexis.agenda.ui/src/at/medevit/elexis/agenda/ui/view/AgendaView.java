package at.medevit.elexis.agenda.ui.view;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import at.medevit.elexis.agenda.ui.composite.ParallelComposite;
import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.composite.WeekComposite;
import at.medevit.elexis.agenda.ui.function.LoadEventsFunction;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class AgendaView {

	private Composite container;

	private Composite parallelParent;
	private ParallelComposite parallelComposite;

	private Composite weekParent;
	private WeekComposite weekComposite;

	private StackLayout stackLayout;

	private SideBarComposite parallelSideBar;

	private SideBarComposite weekSideBar;

	public AgendaView() {
		// TODO Auto-generated constructor stub
	}

	@Inject
	@Optional
	public void reload(@UIEventTopic(ElexisEventTopics.EVENT_RELOAD) Class<?> clazz) {
		if (IAppointment.class.equals(clazz)) {
			if (parallelComposite != null && !parallelComposite.isDisposed()) {
				parallelComposite.refetchEvents();
			}
			if (weekComposite != null && !weekComposite.isDisposed()) {
				weekComposite.refetchEvents();
			}
		}
	}

	@PostConstruct
	public void createPartControl(MPart part, ESelectionService selectionService, EMenuService menuService,
			Composite parent, UISynchronize uiSynchronize) {
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
		weekComposite = new WeekComposite(part, selectionService, menuService, weekParent, SWT.NONE, true,
				uiSynchronize);
		weekComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		CoreUiUtil.injectServicesWithContext(weekComposite);
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
		parallelComposite = new ParallelComposite(part, selectionService, menuService, parallelParent, SWT.NONE, true,
				uiSynchronize);
		CoreUiUtil.injectServicesWithContext(parallelComposite);
		parallelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parallelSideBar.setAgendaComposite(parallelComposite);
		setTopControl("parallel"); //$NON-NLS-1$
	}

	@Focus
	public void setFocus() {
		if (container != null && !container.isDisposed()) {
			container.setFocus();
		}
	}

	public void setTopControl(String name) {
		if ("parallel".equalsIgnoreCase(name)) { //$NON-NLS-1$
			stackLayout.topControl = parallelParent;
		} else if ("week".equalsIgnoreCase(name)) { //$NON-NLS-1$
			stackLayout.topControl = weekParent;
		}
		container.layout();
	}

	public SideBarComposite getParallelSideBarComposite() {
		return parallelSideBar;
	}

	public LoadEventsFunction getLoadEventsFunction() {
		if (stackLayout.topControl == parallelParent) {
			return parallelComposite.getLoadEventsFunction();
		} else if (stackLayout.topControl == weekParent) {
			return weekComposite.getLoadEventsFunction();
		}
		return null;
	}

	@Optional
	@Inject
	public void setFixLayout(MPart part, @Named(Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}
}
