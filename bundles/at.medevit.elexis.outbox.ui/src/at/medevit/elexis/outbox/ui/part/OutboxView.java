package at.medevit.elexis.outbox.ui.part;

import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;

import at.medevit.elexis.outbox.model.IOutboxElement;
import at.medevit.elexis.outbox.model.IOutboxElementService;
import at.medevit.elexis.outbox.ui.OutboxServiceComponent;
import at.medevit.elexis.outbox.ui.command.AutoActivePatientHandler;
import at.medevit.elexis.outbox.ui.part.action.OutboxFilterAction;
import at.medevit.elexis.outbox.ui.part.model.PatientOutboxElements;
import at.medevit.elexis.outbox.ui.part.provider.IOutboxElementUiProvider;
import at.medevit.elexis.outbox.ui.part.provider.OutboxElementContentProvider;
import at.medevit.elexis.outbox.ui.part.provider.OutboxElementLabelProvider;
import at.medevit.elexis.outbox.ui.part.provider.OutboxElementUiExtension;
import at.medevit.elexis.outbox.ui.preferences.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.data.Mandant;

public class OutboxView extends ViewPart {
	
	private Text filterText;
	private TreeViewer viewer;
	
	private boolean reloadPending;
	
	private OutboxElementViewerFilter filter = new OutboxElementViewerFilter();
	
	private ElexisUiEventListenerImpl mandantChanged =
		new ElexisUiEventListenerImpl(Mandant.class, ElexisEvent.EVENT_MANDATOR_CHANGED) {
			@Override
			public void runInUi(ElexisEvent ev){
				reload();
			}
		};
	private OutboxElementContentProvider contentProvider;
	private boolean setAutoSelectPatient;
	
	@Override
	public void createPartControl(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite filterComposite = new Composite(composite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		filterComposite.setLayoutData(data);
		filterComposite.setLayout(new GridLayout(2, false));
		
		filterText = new Text(filterComposite, SWT.SEARCH);
		filterText.setMessage("Filter");
		data = new GridData(GridData.FILL_HORIZONTAL);
		filterText.setLayoutData(data);
		filterText.addModifyListener(e -> {
			if (filterText.getText().length() > 1) {
				filter.setSearchText(filterText.getText());
				viewer.refresh();
			} else {
				filter.setSearchText("");
				viewer.refresh();
			}
		});
		
		ToolBarManager menuManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP);
		menuManager.createControl(filterComposite);
		
		viewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		viewer.getControl().setLayoutData(gd);
		
		ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = filter;
		viewer.setFilters(filters);
		
		contentProvider = new OutboxElementContentProvider();
		viewer.setContentProvider(contentProvider);
		
		viewer.setLabelProvider(new OutboxElementLabelProvider());
		
		viewer.addDoubleClickListener(event -> {
			StructuredSelection selection = (StructuredSelection) viewer.getSelection();
			if (!selection.isEmpty()) {
				Object selectedObj = selection.getFirstElement();
				if (selectedObj instanceof IOutboxElement) {
					OutboxElementUiExtension extension = new OutboxElementUiExtension();
					extension.fireDoubleClicked((IOutboxElement) selectedObj);
				}
			}
		});
		
		viewer.addSelectionChangedListener(event -> {
			ISelection selection = event.getSelection();
			if (selection instanceof StructuredSelection && !selection.isEmpty()) {
				if (setAutoSelectPatient) {
					Object selectedElement = ((StructuredSelection) selection).getFirstElement();
					if (selectedElement instanceof IOutboxElement) {
						ContextServiceHolder.get()
							.setActivePatient(((IOutboxElement) selectedElement).getPatient());
					} else if (selectedElement instanceof PatientOutboxElements) {
						ContextServiceHolder.get().setActivePatient(
							((PatientOutboxElements) selectedElement).getPatient());
					}
				}
			}
		});
		
		addFilterActions(menuManager);
		
		OutboxServiceComponent.getService().addUpdateListener(element -> {
			Display.getDefault().asyncExec(new Runnable() {
				public void run(){
					contentProvider.refreshElement(element);
					viewer.refresh();
				}
			});
		});
		
		reload();
		
		MenuManager ctxtMenuManager = new MenuManager();
		Menu menu = ctxtMenuManager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
		getSite().registerContextMenu(ctxtMenuManager, viewer);
		
		ElexisEventDispatcher.getInstance().addListeners(mandantChanged);
		getSite().setSelectionProvider(viewer);
		
		setAutoSelectPatientState(
			CoreHub.userCfg.get(Preferences.OUTBOX_PATIENT_AUTOSELECT, false));
	}
	
	public void setAutoSelectPatientState(boolean value){
		setAutoSelectPatient = value;
		ICommandService service = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand(AutoActivePatientHandler.CMD_ID);
		command.getState(AutoActivePatientHandler.STATE_ID).setValue(value);
		CoreHub.userCfg.set(Preferences.OUTBOX_PATIENT_AUTOSELECT, value);
	}
	
	private void addFilterActions(ToolBarManager menuManager){
		OutboxElementUiExtension extension = new OutboxElementUiExtension();
		List<IOutboxElementUiProvider> providers = extension.getProviders();
		for (IOutboxElementUiProvider iOutboxElementUiProvider : providers) {
			ViewerFilter extensionFilter = iOutboxElementUiProvider.getFilter();
			if (extensionFilter != null) {
				OutboxFilterAction action = new OutboxFilterAction(viewer, extensionFilter,
					iOutboxElementUiProvider.getFilterImage());
				menuManager.add(action);
			}
		}
		menuManager.update(true);
	}
	
	@Override
	public void setFocus(){
		filterText.setFocus();
		
		if (reloadPending) {
			reload();
		}
	}
	
	private List<IOutboxElement> getOpenOutboxElements(){
		List<IOutboxElement> openElements = OutboxServiceComponent.getService().getOutboxElements(
			ContextServiceHolder.get().getActiveMandator().orElse(null), null,
			IOutboxElementService.State.NEW);
		return openElements;
	}
	
	private class OutboxElementViewerFilter extends ViewerFilter {
		protected String searchString;
		protected LabelProvider labelProvider = new OutboxElementLabelProvider();
		
		public void setSearchText(String s){
			// Search must be a substring of the existing value
			this.searchString = s;
		}
		
		public boolean isActive(){
			if (searchString == null || searchString.isEmpty()) {
				return false;
			}
			return true;
		}
		
		private boolean isSelect(Object leaf){
			String label = labelProvider.getText(leaf);
			if (label != null && label.contains(searchString)) {
				return true;
			}
			return false;
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element){
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			
			StructuredViewer sviewer = (StructuredViewer) viewer;
			ITreeContentProvider provider = (ITreeContentProvider) sviewer.getContentProvider();
			Object[] children = provider.getChildren(element);
			if (children != null && children.length > 0) {
				for (Object child : children) {
					if (select(viewer, element, child)) {
						return true;
					}
				}
			}
			return isSelect(element);
		}
	}
	
	public void reload(){
		if (!viewer.getControl().isVisible()) {
			reloadPending = true;
			return;
		}
		
		viewer.setInput(getOpenOutboxElements());
		reloadPending = false;
		viewer.refresh();
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(mandantChanged);
		super.dispose();
	}
	
	public TreeViewer getTreeViewer(){
		return viewer;
	}
}
