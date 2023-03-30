package at.medevit.elexis.outbox.ui.part;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
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
import at.medevit.elexis.outbox.model.IOutboxElementService.State;
import at.medevit.elexis.outbox.ui.OutboxServiceComponent;
import at.medevit.elexis.outbox.ui.command.AutoActivePatientHandler;
import at.medevit.elexis.outbox.ui.part.action.OutboxFilterAction;
import at.medevit.elexis.outbox.ui.part.model.PatientOutboxElements;
import at.medevit.elexis.outbox.ui.part.provider.IOutboxElementUiProvider;
import at.medevit.elexis.outbox.ui.part.provider.OutboxElementContentProvider;
import at.medevit.elexis.outbox.ui.part.provider.OutboxElementLabelProvider;
import at.medevit.elexis.outbox.ui.part.provider.OutboxElementUiExtension;
import at.medevit.elexis.outbox.ui.part.provider.OutboxViewerComparator;
import at.medevit.elexis.outbox.ui.preferences.Preferences;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;

public class OutboxView extends ViewPart {

	private Text filterText;
	private TreeViewer viewer;

	private boolean reloadPending;

	private OutboxElementViewerFilter filter = new OutboxElementViewerFilter();

	private OutboxElementContentProvider contentProvider;
	private boolean setAutoSelectPatient;

	@Optional
	@Inject
	void mandatorChanged(IMandator mandator) {
		Display.getDefault().asyncExec(() -> {
			reload();
		});
	}

	@Optional
	@Inject
	void updateOutboxElement(@UIEventTopic(ElexisEventTopics.EVENT_UPDATE) IOutboxElement element) {
		contentProvider.refreshElement(element);
		PatientOutboxElements patientElements = contentProvider.getPatientOutboxElements(element);
		if (patientElements != null) {
			viewer.refresh(patientElements);
		} else {
			viewer.refresh();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
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
				filter.setSearchText(StringUtils.EMPTY);
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

		viewer.setComparator(new OutboxViewerComparator());

		viewer.addDoubleClickListener(event -> {
			StructuredSelection selection = (StructuredSelection) viewer.getSelection();
			if (!selection.isEmpty()) {
				Object selectedObj = selection.getFirstElement();
				if (selectedObj instanceof IOutboxElement) {
					OutboxElementUiExtension extension = new OutboxElementUiExtension();
					extension.fireDoubleClicked((IOutboxElement) selectedObj);
					updateOutboxElement((IOutboxElement) selectedObj);
				}
			}
		});

		viewer.addSelectionChangedListener(event -> {
			ISelection selection = event.getSelection();
			if (selection instanceof StructuredSelection && !selection.isEmpty()) {
				if (setAutoSelectPatient) {
					Object selectedElement = ((StructuredSelection) selection).getFirstElement();
					if (selectedElement instanceof IOutboxElement) {
						ContextServiceHolder.get().getRootContext().setNamed(ContextServiceHolder.SELECTIONFALLBACK,
								((IOutboxElement) selectedElement).getPatient());
					} else if (selectedElement instanceof PatientOutboxElements) {
						ContextServiceHolder.get()
								.setActivePatient(((PatientOutboxElements) selectedElement).getPatient());
					}
				}
			}
		});

		addFilterActions(menuManager);

		OutboxServiceComponent.get().addUpdateListener(element -> {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					updateOutboxElement(element);
				}
			});
		});

		reload();

		MenuManager ctxtMenuManager = new MenuManager();
		Menu menu = ctxtMenuManager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
		getSite().registerContextMenu(ctxtMenuManager, viewer);

		getSite().setSelectionProvider(viewer);

		setAutoSelectPatientState(ConfigServiceHolder.getUser(Preferences.OUTBOX_PATIENT_AUTOSELECT, false));
	}

	public void setAutoSelectPatientState(boolean value) {
		setAutoSelectPatient = value;
		ICommandService service = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand(AutoActivePatientHandler.CMD_ID);
		command.getState(AutoActivePatientHandler.STATE_ID).setValue(value);
		ConfigServiceHolder.setUser(Preferences.OUTBOX_PATIENT_AUTOSELECT, value);
	}

	private void addFilterActions(ToolBarManager menuManager) {
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
	public void setFocus() {
		filterText.setFocus();

		if (reloadPending) {
			reload();
		}
	}

	private List<IOutboxElement> getOpenOutboxElements(State state) {
		List<IOutboxElement> openElements = OutboxServiceComponent.get()
				.getOutboxElements(ContextServiceHolder.get().getActiveMandator().orElse(null), null, state);
		return openElements;
	}

	private class OutboxElementViewerFilter extends ViewerFilter {
		protected String searchString;
		protected LabelProvider labelProvider = new OutboxElementLabelProvider();

		public void setSearchText(String s) {
			// Search must be a substring of the existing value
			this.searchString = s;
		}

		public boolean isActive() {
			if (searchString == null || searchString.isEmpty()) {
				return false;
			}
			return true;
		}

		private boolean isSelect(Object leaf) {
			String label = labelProvider.getText(leaf);
			if (label != null && label.contains(searchString)) {
				return true;
			}
			return false;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
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

	public void reload() {
		if (!viewer.getControl().isVisible()) {
			reloadPending = true;
			return;
		}
		TreePath[] expanded = viewer.getExpandedTreePaths();

		viewer.setInput(getOpenOutboxElements(null));
		reloadPending = false;
		viewer.refresh();
		viewer.setExpandedTreePaths(expanded);
	}

	public TreeViewer getTreeViewer() {
		return viewer;
	}

	@Optional
	@Inject
	public void setFixLayout(MPart part,
			@Named(ch.elexis.core.constants.Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}
}
