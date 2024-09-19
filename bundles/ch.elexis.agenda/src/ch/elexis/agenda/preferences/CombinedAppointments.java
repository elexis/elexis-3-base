package ch.elexis.agenda.preferences;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.dialogs.AddKombiTerminDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CombinedAppointments extends PreferencePage implements IWorkbenchPreferencePage {

	private ListViewer areaListViewer;
	private TableViewer appointmentTableViewer;
	private ToolBarManager toolbarmgr;
	private String selectedArea;
	private List<TableEditor> editors = new ArrayList<>();
	private Button deleteButton;

	public CombinedAppointments() {
		super(Messages.CombinedAppointments_Titel);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite areaComposite = new Composite(sashForm, SWT.NONE);
		areaComposite.setLayout(new GridLayout(1, false));
		areaListViewer = new ListViewer(areaComposite, SWT.BORDER | SWT.V_SCROLL);
		areaListViewer.setContentProvider(ArrayContentProvider.getInstance());
		areaListViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		populateAreaList();
		Composite tableComposite = new Composite(sashForm, SWT.NONE);
		tableComposite.setLayout(new GridLayout(1, false));
		toolbarmgr = new ToolBarManager();
		toolbarmgr.add(new AddKombiTermin());
		ToolBar toolbar = toolbarmgr.createControl(tableComposite);
		toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		Composite tableContainer = new Composite(tableComposite, SWT.NONE);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableContainer.setLayout(tableColumnLayout);
		tableContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		appointmentTableViewer = new TableViewer(tableContainer, SWT.BORDER | SWT.FULL_SELECTION);
		appointmentTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		Table appointmentTable = appointmentTableViewer.getTable();
		appointmentTable.setHeaderVisible(true);
		appointmentTable.setLinesVisible(true);
		createColumns(appointmentTableViewer, tableColumnLayout);
		sashForm.setWeights(new int[] { 1, 3 });
		appointmentTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem[] selection = appointmentTable.getSelection();
				if (selection.length > 0) {
					String[] selectedAppointment = (String[]) selection[0].getData();
					editAppointment(selectedAppointment);
				}
			}
		});
		return container;
	}

	private void populateAreaList() {
		List<String> areas = ConfigServiceHolder.get().getAsList(PreferenceConstants.AG_TERMINTYPEN);
		List<String> filteredAppointmentTypes = areas.stream().skip(2).collect(Collectors.toList());
		areaListViewer.setInput(filteredAppointmentTypes);
		areaListViewer.addSelectionChangedListener(event -> {
			selectedArea = (String) ((org.eclipse.jface.viewers.StructuredSelection) event.getSelection())
					.getFirstElement();
			populateAppointments(selectedArea);
		});
	}

	private void populateAppointments(String area) {
		clearAllEditors();
		List<String> kombiTermineList = ConfigServiceHolder.get()
				.getAsList(PreferenceConstants.AG_KOMBITERMINE + "/" + area);
		List<String[]> appointments = new ArrayList<>();
		for (String kombiTermin : kombiTermineList) {
			kombiTermin = kombiTermin.replaceAll("[{}]", "");
			String[] elements = kombiTermin.split(";");
			appointments.add(elements);
		}
		appointmentTableViewer.setInput(appointments);
		createColumns(appointmentTableViewer,
				(TableColumnLayout) appointmentTableViewer.getTable().getParent().getLayout());
		appointmentTableViewer.refresh();
	}

	private void createColumns(TableViewer viewer, TableColumnLayout layout) {
		String[] titles = { Messages.AddCombiTerminDialogReason, Messages.AppointmentDetailComposite_range,
				Messages.Core_Type, Messages.CombinedAppointmentsWhen, Messages.CombinedAppointmentsSpacing,
				Messages.Core_Date_Duration, "" };
		int[] bounds = { 70, 70, 50, 40, 30, 30, 20 };
		if (titles.length != bounds.length) {
			throw new IllegalStateException("Titles and bounds arrays must have the same length");
		}
		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			TableColumn col = column.getColumn();
			col.setText(titles[i]);
			layout.setColumnData(col, new ColumnWeightData(bounds[i]));
			if (i < titles.length - 1) {
				column.setLabelProvider(new org.eclipse.jface.viewers.ColumnLabelProvider() {
					@Override
					public void update(ViewerCell cell) {
						String[] element = (String[]) cell.getElement();
						if (cell.getColumnIndex() < element.length) {
							cell.setText(element[cell.getColumnIndex()]);
						}
					}
				});
			} else {
				column.setLabelProvider(new org.eclipse.jface.viewers.ColumnLabelProvider() {
					@Override
					public void update(ViewerCell cell) {
						TableItem item = (TableItem) cell.getItem();
						deleteButton = new Button((Composite) cell.getViewerRow().getControl(), SWT.NONE);
						deleteButton.setImage(Images.IMG_DELETE.getImage());
						deleteButton.addListener(SWT.Selection, e -> {
							int index = appointmentTableViewer.getTable().indexOf(item);
							deleteAppointment(index);
						});
						TableEditor editor = new TableEditor(item.getParent());
						editor.grabHorizontal = true;
						editor.grabVertical = true;
						editor.setEditor(deleteButton, item, cell.getColumnIndex());
						editors.add(editor);
					}
				});
			}
		}
	}

	private void editAppointment(String[] appointmentData) {
		AddKombiTerminDialog dialog = new AddKombiTerminDialog(getShell(), selectedArea, appointmentData);
		if (dialog.open() == Window.OK) {
			populateAppointments(selectedArea);
		}
	}

	private void deleteAppointment(int index) {
		clearAllEditors();
		@SuppressWarnings("unchecked")
		List<String[]> currentAppointments = (List<String[]>) appointmentTableViewer.getInput();
		currentAppointments.remove(index);
		List<String> updatedKombiTermine = new ArrayList<>();
		for (String[] appointment : currentAppointments) {
			String combined = "{" + String.join(";", appointment) + "}";
			updatedKombiTermine.add(combined);
		}
		ConfigServiceHolder.setGlobalAsList(PreferenceConstants.AG_KOMBITERMINE + "/" + selectedArea,
				updatedKombiTermine);
		appointmentTableViewer.refresh();
		populateAppointments(selectedArea);
	}


	public void init(IWorkbench workbench) {
		// Initialization code if needed
	}

	private class AddKombiTermin extends Action {

		@Override
		public ImageDescriptor getImageDescriptor() {
			return Images.IMG_NEW.getImageDescriptor();
		}

		@Override
		public String getToolTipText() {
			return Messages.CombinedAppointments_ToolTipText;
		}

		@Override
		public void run() {
			if (selectedArea != null) {
				AddKombiTerminDialog dialog = new AddKombiTerminDialog(getShell(), selectedArea);
				if (dialog.open() == Window.OK) {
					populateAppointments(selectedArea);
				}
			}
		}
	}

	private void clearAllEditors() {
		for (TableEditor editor : editors) {
			editor.getEditor().dispose();
			editor.dispose();
		}
		editors.clear();
	}
}
