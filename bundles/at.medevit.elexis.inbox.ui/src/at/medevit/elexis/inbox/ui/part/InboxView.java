/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.inbox.ui.part;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.model.IInboxUpdateListener;
import at.medevit.elexis.inbox.ui.InboxModelServiceHolder;
import at.medevit.elexis.inbox.ui.InboxServiceHolder;
import at.medevit.elexis.inbox.ui.command.AutoActivePatientHandler;
import at.medevit.elexis.inbox.ui.part.action.InboxFilterAction;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementContentProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementLabelProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementUiExtension;
import at.medevit.elexis.inbox.ui.preferences.Preferences;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.util.CoreUiUtil;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public class InboxView extends ViewPart {

	private Text filterText;
	private CheckboxTreeViewer viewer;

	private boolean reloadPending;

	private InboxElementViewerFilter filter = new InboxElementViewerFilter();

	private ElexisUiEventListenerImpl mandantChanged = new ElexisUiEventListenerImpl(Mandant.class,
			ElexisEvent.EVENT_MANDATOR_CHANGED) {
		@Override
		public void runInUi(ElexisEvent ev) {
			reload();
		}
	};
	private InboxElementContentProvider contentProvider;
	private boolean setAutoSelectPatient;

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Composite filterComposite = new Composite(composite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		filterComposite.setLayoutData(data);
		filterComposite.setLayout(new GridLayout(2, false));

		filterText = new Text(filterComposite, SWT.SEARCH);
		filterText.setMessage("Patienten Filter");
		data = new GridData(GridData.FILL_HORIZONTAL);
		filterText.setLayoutData(data);
		filterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (filterText.getText().length() > 1) {
					filter.setSearchText(filterText.getText());
					viewer.refresh();
				} else {
					filter.setSearchText("");
					viewer.refresh();
				}
			}
		});

		ToolBarManager menuManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP);
		menuManager.createControl(filterComposite);

		viewer = new CheckboxTreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		viewer.getControl().setLayoutData(gd);

		ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = filter;
		viewer.setFilters(filters);

		contentProvider = new InboxElementContentProvider();
		viewer.setContentProvider(contentProvider);

		viewer.setLabelProvider(new InboxElementLabelProvider());

		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getElement() instanceof PatientInboxElements) {
					PatientInboxElements patientInbox = (PatientInboxElements) event.getElement();
					for (IInboxElement inboxElement : patientInbox.getElements()) {
						State newState = toggleInboxElementState(inboxElement);
						if (newState == State.NEW) {
							viewer.setChecked(inboxElement, false);
						} else {
							viewer.setChecked(inboxElement, true);
						}
						contentProvider.refreshElement(inboxElement);
					}
					contentProvider.refreshElement(patientInbox);
				} else if (event.getElement() instanceof IInboxElement) {
					IInboxElement inboxElement = (IInboxElement) event.getElement();
					toggleInboxElementState(inboxElement);
					contentProvider.refreshElement(inboxElement);
				}
				viewer.refresh(false);
			}
		});

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if (!selection.isEmpty()) {
					Object selectedObj = selection.getFirstElement();
					if (selectedObj instanceof IInboxElement) {
						InboxElementUiExtension extension = new InboxElementUiExtension();
						extension.fireDoubleClicked((IInboxElement) selectedObj);
					}
				}
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof StructuredSelection && !selection.isEmpty()) {
					if (setAutoSelectPatient) {
						Object selectedElement = ((StructuredSelection) selection).getFirstElement();
						if (selectedElement instanceof IInboxElement) {
							Patient patient = (Patient) NoPoUtil
									.loadAsPersistentObject(((IInboxElement) selectedElement).getPatient());
							ElexisEventDispatcher.fireSelectionEvent(patient);
						} else if (selectedElement instanceof PatientInboxElements) {
							Patient patient = (Patient) NoPoUtil
									.loadAsPersistentObject(((PatientInboxElements) selectedElement).getPatient());
							ElexisEventDispatcher.fireSelectionEvent(patient);
						}
					}
				}
			}
		});

		final Transfer[] dropTransferTypes = new Transfer[] { FileTransfer.getInstance() };
		viewer.addDropSupport(DND.DROP_COPY, dropTransferTypes, new DropTargetAdapter() {

			@Override
			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

			@Override
			public void drop(DropTargetEvent event) {
				if (dropTransferTypes[0].isSupportedType(event.currentDataType)) {
					String[] files = (String[]) event.data;
					IPatient patient = null;

					if (event.item != null) {
						Object data = event.item.getData();
						if (data instanceof IInboxElement) {
							patient = ((IInboxElement) data).getPatient();
						} else if (data instanceof PatientInboxElements) {
							patient = ((PatientInboxElements) data).getPatient();
						}
					}

					if (patient == null) {
						// fallback
						patient = ContextServiceHolder.get().getActivePatient().orElse(null);
					}
					if (patient != null) {
						if (files != null) {
							for (String file : files) {
								try {
									InboxServiceHolder.get().createInboxElement(patient,
											ContextServiceHolder.get().getActiveMandator().orElse(null), file, true);
								} catch (Exception e) {
									LoggerFactory.getLogger(InboxView.class).warn("drop error", e);
								}
							}
						}

						viewer.refresh();
					} else {
						MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warnung",
								"Bitte wählen Sie zuerst einen Patienten aus.");
					}

				}
			}

		});

		addFilterActions(menuManager);

		InboxServiceHolder.get().addUpdateListener(new IInboxUpdateListener() {
			public void update(final IInboxElement element) {
				if (viewer != null && !viewer.getControl().isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							contentProvider.refreshElement(element);
							viewer.refresh();
						}
					});
				}
			}
		});

		reload();

		MenuManager ctxtMenuManager = new MenuManager();
		Menu menu = ctxtMenuManager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
		getSite().registerContextMenu(ctxtMenuManager, viewer);

		ElexisEventDispatcher.getInstance().addListeners(mandantChanged);
		getSite().setSelectionProvider(viewer);

		setAutoSelectPatientState(ConfigServiceHolder.getUser(Preferences.INBOX_PATIENT_AUTOSELECT, false));
	}

	public void setAutoSelectPatientState(boolean value) {
		setAutoSelectPatient = value;
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand(AutoActivePatientHandler.CMD_ID);
		command.getState(AutoActivePatientHandler.STATE_ID).setValue(value);
		ConfigServiceHolder.setUser(Preferences.INBOX_PATIENT_AUTOSELECT, value);
	}

	private void addFilterActions(ToolBarManager menuManager) {
		InboxElementUiExtension extension = new InboxElementUiExtension();
		List<IInboxElementUiProvider> providers = extension.getProviders();
		for (IInboxElementUiProvider iInboxElementUiProvider : providers) {
			ViewerFilter extensionFilter = iInboxElementUiProvider.getFilter();
			if (extensionFilter != null) {
				InboxFilterAction action = new InboxFilterAction(viewer, extensionFilter,
						iInboxElementUiProvider.getFilterImage());
				menuManager.add(action);
			}
		}
		menuManager.update(true);
	}

	private State toggleInboxElementState(IInboxElement inboxElement) {
		if (inboxElement.getState() == State.NEW) {
			inboxElement.setState(State.SEEN);
			InboxModelServiceHolder.get().save(inboxElement);
			return State.SEEN;
		} else if (inboxElement.getState() == State.SEEN) {
			inboxElement.setState(State.NEW);
			InboxModelServiceHolder.get().save(inboxElement);
			return State.NEW;
		}
		return State.NEW;
	}

	@Override
	public void setFocus() {
		filterText.setFocus();

		if (reloadPending) {
			reload();
		}
	}

	private List<IInboxElement> getOpenInboxElements() {
		List<IInboxElement> openElements = InboxServiceHolder.get().getInboxElements(
				ContextServiceHolder.get().getActiveMandator().orElse(null), null, IInboxElementService.State.NEW);
		return openElements;
	}

	private class InboxElementViewerFilter extends ViewerFilter {
		protected String searchString;
		protected InboxElementLabelProvider labelProvider = new InboxElementLabelProvider();

		public void setSearchText(String s) {
			// Search must be a substring of the existing value
			this.searchString = s != null ? s.toLowerCase() : s;
		}

		public boolean isActive() {
			if (searchString == null || searchString.isEmpty()) {
				return false;
			}
			return true;
		}

		private boolean isSelect(Object leaf) {
			String label = labelProvider.getText(leaf);
			if (label != null && label.toLowerCase().contains(searchString)) {
				return true;
			}
			return false;
		}

		private boolean isVisible(Object element) {
			if (element instanceof IInboxElement) {
				return labelProvider.isVisible((IInboxElement) element);
			}
			return true;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IInboxElement && !isVisible(element)) {
				return false;
			}
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			if (element instanceof PatientInboxElements) {
				return isSelect(element);
			} else {
				return true;
			}
		}
	}

	public void reload() {
		if (!viewer.getControl().isVisible()) {
			reloadPending = true;
			return;
		}

		viewer.setInput(getOpenInboxElements());
		reloadPending = false;
		viewer.refresh();
	}

	@Override
	public void dispose() {
		ElexisEventDispatcher.getInstance().removeListeners(mandantChanged);
		super.dispose();
	}

	public CheckboxTreeViewer getCheckboxTreeViewer() {
		return viewer;
	}

	@Optional
	@Inject
	public void setFixLayout(MPart part,
			@Named(ch.elexis.core.constants.Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}
}
