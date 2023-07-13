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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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

	private TableViewerColumn mandatorColumn;

	@Optional
	@Inject
	public void activeMandator(IMandator mandator) {
		Display.getDefault().asyncExec(() -> {
			if (selectedMandators == null || selectedMandators.isEmpty()) {
				reload();
			}
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
		filterText.setMessage("Patienten Suche nach Nachname, Vorname, Geburtsdatum oder #PatientenNr");
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

		viewer = new TableViewer(composite,
				SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		viewer.getControl().setLayoutData(gd);

		contentProvider = new InboxElementContentProvider(this);
		viewer.setContentProvider(contentProvider);

		viewer.getTable().setHeaderVisible(true);
		comparator = new InboxElementComparator();
		viewer.setComparator(comparator);

		ColumnViewerToolTipSupport.enableFor(viewer);
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(50);
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

		mandatorColumn = new TableViewerColumn(viewer, SWT.NONE);
		mandatorColumn.getColumn().setWidth(0);
		mandatorColumn.getColumn().setText("Mandant");
		mandatorColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IInboxElement) {
					return ((IInboxElement) element).getMandator() != null
							? ((IInboxElement) element).getMandator().getLabel()
							: "?";
				}
				return super.getText(element);
			}
		});
		mandatorColumn.getColumn().addSelectionListener(getSelectionAdapter(mandatorColumn.getColumn(), 0));

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

			private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

			@Override
			public String getText(Object element) {
				if (element instanceof IInboxElement) {
					LocalDate objectDate = extension.getObjectDate((IInboxElement) element);
					if (objectDate != null) {
						return dateFormat.format(objectDate);
					}
					return "?";
				}
				return super.getText(element);
			}
		});
		SelectionAdapter selectionAdapter = getSelectionAdapter(column.getColumn(), 2);
		selectionAdapter.widgetSelected(null);
		column.getColumn().addSelectionListener(selectionAdapter);

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

			@Override
			public String getToolTipText(Object element) {
				if (element instanceof IInboxElement) {
					return extension.getTooltipText((IInboxElement) element);
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
		column.getColumn().setWidth(35);

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

		viewer.getControl().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.SPACE) {
					IStructuredSelection currentSelection = viewer.getStructuredSelection();
					if (currentSelection != null && !currentSelection.isEmpty()
							&& currentSelection.getFirstElement() instanceof IInboxElement) {
						IInboxElement element = (IInboxElement) currentSelection.getFirstElement();
						setInboxElementState(element, element.getState() != State.SEEN);
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
					if (((StructuredSelection) selection).getFirstElement() instanceof IInboxElement) {
						InboxElementUiExtension extension = new InboxElementUiExtension();
						extension
								.fireSingleClicked((IInboxElement) ((StructuredSelection) selection).getFirstElement());
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

	private List<IMandator> selectedMandators;

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
			List<ViewerFilter> extensionFilters = iInboxElementUiProvider.getFilters();
			for (ViewerFilter extensionFilter : extensionFilters) {
				InboxFilterAction action = new InboxFilterAction(viewer, extensionFilter,
						iInboxElementUiProvider.getFilterImage(extensionFilter));
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

	public List<IInboxElement> getOpenInboxElements() {
		if (selectedMandators == null || selectedMandators.isEmpty()) {
			mandatorColumn.getColumn().setWidth(0);
			return InboxServiceHolder.get().getInboxElements(
					ContextServiceHolder.get().getActiveMandator().orElse(null), null, IInboxElementService.State.NEW);
		} else {
			mandatorColumn.getColumn().setWidth(75);
			List<IInboxElement> mandatorsElements = new ArrayList<>();
			for (IMandator mandator : selectedMandators) {
				mandatorsElements.addAll(
						InboxServiceHolder.get().getInboxElements(mandator, null, IInboxElementService.State.NEW));
			}
			return mandatorsElements;
		}
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
				IMandator m1 = i1.getMandator();
				IMandator m2 = i2.getMandator();
				String mtxt1 = m1 != null ? m1.getLabel() : StringUtils.EMPTY;
				String mtxt2 = m2 != null ? m2.getLabel() : StringUtils.EMPTY;
				rc = mtxt1.toLowerCase().compareTo(mtxt2.toLowerCase());
				break;
			case 1:
				IPatient p1 = i1.getPatient();
				IPatient p2 = i2.getPatient();
				String ptxt1 = p1 != null ? p1.getLabel() : StringUtils.EMPTY;
				String ptxt2 = p2 != null ? p2.getLabel() : StringUtils.EMPTY;
				rc = ptxt1.toLowerCase().compareTo(ptxt2.toLowerCase());
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
			setInboxElementState((IInboxElement) o, (Boolean) value);
		}
	}

	private void setInboxElementState(IInboxElement element, Boolean value) {
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
		viewer.refresh();
		Display.getDefault().timerExec(2500, () -> {
			if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
				contentProvider.refreshElement(element);
				viewer.refresh();
			}
		});
	}

	public void setSelectedMandators(List<IMandator> mandators) {
		this.selectedMandators = mandators;
		reload();
	}
}
