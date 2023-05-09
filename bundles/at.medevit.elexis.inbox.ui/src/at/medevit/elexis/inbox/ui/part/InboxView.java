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

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.model.IInboxUpdateListener;
import at.medevit.elexis.inbox.ui.InboxModelServiceHolder;
import at.medevit.elexis.inbox.ui.InboxServiceHolder;
import at.medevit.elexis.inbox.ui.command.AutoActivePatientHandler;
import at.medevit.elexis.inbox.ui.part.action.InboxFilterAction;
import at.medevit.elexis.inbox.ui.part.model.GroupedInboxElements;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementContentProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementUiExtension;
import at.medevit.elexis.inbox.ui.preferences.Preferences;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.views.controls.PagingComposite;

public class InboxView extends ViewPart {

	private PagingComposite pagingComposite;

	private Text filterText;
	private TableViewer viewer;

	private boolean reloadPending;

	private InboxElementContentProvider contentProvider;
	private boolean setAutoSelectPatient;

	@Optional
	@Inject
	public void activeMandator(IMandator mandator) {
		Display.getDefault().asyncExec(() -> {
			reload();
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		pagingComposite = new PagingComposite(composite, SWT.NONE) {
			@Override
			public void runPaging() {
				viewer.refresh();
			}
		};

		Composite filterComposite = new Composite(composite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		filterComposite.setLayoutData(data);
		filterComposite.setLayout(new GridLayout(2, false));

		filterText = new Text(filterComposite, SWT.SEARCH);
		filterText.setMessage("Patienten Filter");
		data = new GridData(GridData.FILL_HORIZONTAL);
		filterText.setLayoutData(data);
		WidgetProperties.text(SWT.Modify).observeDelayed(500, filterText)
				.addValueChangeListener(new IValueChangeListener<String>() {
					@Override
					public void handleValueChange(ValueChangeEvent<? extends String> event) {
						contentProvider.setSearchText(event.diff.getNewValue());
						viewer.refresh();
					}
				});

		ToolBarManager menuManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP);
		menuManager.createControl(filterComposite);

		viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		viewer.getControl().setLayoutData(gd);

		contentProvider = new InboxElementContentProvider(this);
		viewer.setContentProvider(contentProvider);

		viewer.getTable().setHeaderVisible(true);
		comparator = new InboxElementComparator();
		viewer.setComparator(comparator);

		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(60);
		column.getColumn().setText("Kategorie");
		column.setLabelProvider(new ColumnLabelProvider() {

			private InboxElementUiExtension extension = new InboxElementUiExtension();

			@Override
			public Image getImage(Object element) {
				if (element instanceof IInboxElement) {
					return extension.getImage((IInboxElement) element);
				}
				return super.getImage(element);
			}

			@Override
			public String getText(Object element) {
				return null;
			}
		});

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(250);
		column.getColumn().setText("Patient");
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IInboxElement) {
					return ((IInboxElement) element).getPatient() != null
							? ((IInboxElement) element).getPatient().getLabel()
							: "?";
				}
				return super.getText(element);
			}
		});
		column.getColumn().addSelectionListener(getSelectionAdapter(column.getColumn(), 1));

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(80);
		column.getColumn().setText("Datum");
		column.setLabelProvider(new ColumnLabelProvider() {

			private InboxElementUiExtension extension = new InboxElementUiExtension();

			@Override
			public String getText(Object element) {
				if (element instanceof IInboxElement) {
					LocalDate objectDate = extension.getObjectDate((IInboxElement) element);
					if (objectDate != null) {
						return objectDate.toString();
					}
					return "?";
				}
				return super.getText(element);
			}
		});
		column.getColumn().addSelectionListener(getSelectionAdapter(column.getColumn(), 2));

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(250);
		column.getColumn().setText("Beschreibung");
		column.setLabelProvider(new ColumnLabelProvider() {

			private InboxElementUiExtension extension = new InboxElementUiExtension();

			@Override
			public String getText(Object element) {
				if (element instanceof IInboxElement) {
					return extension.getText((IInboxElement) element);
				}
				return super.getText(element);
			}
		});

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.setLabelProvider(new EmulatedCheckBoxLabelProvider() {
			@Override
			protected boolean isChecked(Object element) {
				if (element instanceof IInboxElement) {
					return ((IInboxElement) element).getState() == State.SEEN;
				}
				return false;
			}
		});
		column.setEditingSupport(new CheckBoxColumnEditingSupport(viewer));
		column.getColumn().setText("Visiert");
		column.getColumn().setWidth(25);

//		viewer.setLabelProvider(new InboxElementLabelProvider());

//		viewer.addCheckStateListener(new ICheckStateListener() {
//
//			public void checkStateChanged(CheckStateChangedEvent event) {
//				if (event.getElement() instanceof PatientInboxElements) {
//					PatientInboxElements patientInbox = (PatientInboxElements) event.getElement();
//					for (IInboxElement inboxElement : patientInbox.getElements()) {
//						State newState = toggleInboxElementState(inboxElement);
//						if (newState == State.NEW) {
//							viewer.setChecked(inboxElement, false);
//						} else {
//							viewer.setChecked(inboxElement, true);
//						}
//						contentProvider.refreshElement(inboxElement);
//					}
//					contentProvider.refreshElement(patientInbox);
//				} else if (event.getElement() instanceof IInboxElement) {
//					IInboxElement inboxElement = (IInboxElement) event.getElement();
//					toggleInboxElementState(inboxElement);
//					contentProvider.refreshElement(inboxElement);
//				}
//				viewer.refresh(false);
//			}
//		});

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
							ContextServiceHolder.get().setActivePatient(((IInboxElement) selectedElement).getPatient());
						} else if (selectedElement instanceof PatientInboxElements) {
							ContextServiceHolder.get()
									.setActivePatient(((PatientInboxElements) selectedElement).getPatient());
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
									LoggerFactory.getLogger(InboxView.class).warn("drop error", e); //$NON-NLS-1$
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
							viewer.refresh(false);
						}
					});
				}
			}
		});

		reload();

		MenuManager ctxtMenuManager = new MenuManager();
		Menu menu = ctxtMenuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(ctxtMenuManager, viewer);

		getSite().setSelectionProvider(viewer);

		setAutoSelectPatientState(ConfigServiceHolder.getUser(Preferences.INBOX_PATIENT_AUTOSELECT, false));
	}

	private InboxElementComparator comparator;

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				viewer.getTable().setSortDirection(comparator.getDirection());
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
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

	public PagingComposite getPagingComposite() {
		return pagingComposite;
	}

	public void reload() {
		List<IInboxElement> input = getOpenInboxElements();
		viewer.setInput(input);
		viewer.refresh();
	}

	public StructuredViewer getViewer() {
		return viewer;
	}

	private class InboxElementComparator extends ViewerComparator {
		private int propertyIndex;
		private static final int DESCENDING = 1;
		private int direction = DESCENDING;

		private InboxElementUiExtension extension = new InboxElementUiExtension();

		public InboxElementComparator() {
			this.propertyIndex = 0;
			direction = DESCENDING;
		}

		public int getDirection() {
			return direction == 1 ? SWT.DOWN : SWT.UP;
		}

		public void setColumn(int column) {
			if (column == this.propertyIndex) {
				// Same column as last sort; toggle the direction
				direction = 1 - direction;
			} else {
				// New column; do an ascending sort
				this.propertyIndex = column;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			IInboxElement i1 = (IInboxElement) e1;
			IInboxElement i2 = (IInboxElement) e2;
			int rc = 0;
			switch (propertyIndex) {
			case 0:
			case 1:
				IPatient p1 = i1.getPatient();
				IPatient p2 = i2.getPatient();
				String txt1 = p1 != null ? p1.getLabel() : StringUtils.EMPTY;
				String txt2 = p2 != null ? p2.getLabel() : StringUtils.EMPTY;
				rc = txt1.toLowerCase().compareTo(txt2.toLowerCase());
				break;
			case 2:
				LocalDate t1 = extension.getObjectDate(i1);
				LocalDate t2 = extension.getObjectDate(i2);
				t1 = (t1 == null ? LocalDate.EPOCH : t1);
				t2 = (t2 == null ? LocalDate.EPOCH : t2);
				rc = t1.compareTo(t2);
				break;
			default:
				rc = 0;
			}
			// If descending order, flip the direction
			if (direction == DESCENDING) {
				rc = -rc;
			}
			return rc;
		}

	}

	@Optional
	@Inject
	public void setFixLayout(MPart part,
			@Named(ch.elexis.core.constants.Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}

	private abstract static class EmulatedCheckBoxLabelProvider extends ColumnLabelProvider {

		private static Image CHECKED = AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.inbox.ui", //$NON-NLS-1$
				"rsc/img/checked_checkbox.png").createImage(); //$NON-NLS-1$

		private static Image UNCHECKED = AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.inbox.ui", //$NON-NLS-1$
				"rsc/img/unchecked_checkbox.png").createImage(); //$NON-NLS-1$

		@Override
		public String getText(Object element) {
			return null;
		}

		@Override
		public Image getImage(Object element) {
			return isChecked(element) ? CHECKED : UNCHECKED;
		}

		protected abstract boolean isChecked(Object element);
	}

	private class CheckBoxColumnEditingSupport extends EditingSupport {

		private final TableViewer tableViewer;

		public CheckBoxColumnEditingSupport(TableViewer viewer) {
			super(viewer);
			this.tableViewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object o) {
			return new CheckboxCellEditor(null, SWT.CHECK);
		}

		@Override
		protected boolean canEdit(Object o) {
			return true;
		}

		@Override
		protected Object getValue(Object o) {
			IInboxElement element = (IInboxElement) o;
			return element.getState() == State.SEEN;
		}

		@Override
		protected void setValue(Object o, Object value) {
			IInboxElement element = (IInboxElement) o;
			if (Boolean.TRUE.equals(value)) {
				element.setState(State.SEEN);
				if (!(element instanceof GroupedInboxElements)) {
					InboxModelServiceHolder.get().save(element);
				}
			} else {
				element.setState(State.NEW);
				if (!(element instanceof GroupedInboxElements)) {
					InboxModelServiceHolder.get().save(element);
				}
			}
			tableViewer.refresh();
			Display.getDefault().timerExec(2500, () -> {
				contentProvider.refreshElement(element);
				tableViewer.refresh();
			});
		}
	}
}
