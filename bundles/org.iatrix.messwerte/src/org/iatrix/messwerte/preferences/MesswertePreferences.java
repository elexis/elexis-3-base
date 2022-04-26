package org.iatrix.messwerte.preferences;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.iatrix.messwerte.Constants;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Labor;
import ch.elexis.data.Query;
import ch.rgw.tools.StringTool;

public class MesswertePreferences extends PreferencePage implements IWorkbenchPreferencePage {
	// name: Messwerte Iatrix
	// category: Iatrix
	public static final String ID = "org.iatrix.messwerte.preferences.MesswertePreferences";

	private static final int VISIBLE_NUMBER_OF_LABORS = 5;

	private ListViewer ownLaborsList;
	private Spinner spinnerNoCol;

	public MesswertePreferences() {
	}

	public MesswertePreferences(String title) {
		super(title);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public MesswertePreferences(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		layout.topMargin = 0;
		layout.bottomMargin = 0;
		mainArea.setLayout(layout);

		TableWrapData twd;
		Label label;

		Text infoText = new Text(mainArea, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		infoText.setLayoutData(SWTHelper.getFillTableWrapData(2, true, 1, false));
		infoText.setText(
				"Bitte wählen Sie die Labors aus, für welche Sie die Werte" + " in der Praxis selber ermitteln.");

		label = new Label(mainArea, SWT.NONE);
		label.setText("Praxislabors:");

		ownLaborsList = new ListViewer(mainArea, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		List list = ownLaborsList.getList();
		twd = SWTHelper.getFillTableWrapData(1, true, 1, false);
		twd.heightHint = VISIBLE_NUMBER_OF_LABORS * list.getItemHeight();
		list.setLayoutData(twd);

		ownLaborsList.setContentProvider(new LaborsListContentProvider());
		ownLaborsList.setLabelProvider(new LaborsListLabelProvider());
		ownLaborsList.setInput(this);

		Label lblNoCols = new Label(mainArea, SWT.NONE);
		lblNoCols.setText("Spalten pro Seite:");

		spinnerNoCol = new Spinner(mainArea, SWT.BORDER);
		spinnerNoCol.setPageIncrement(1);
		spinnerNoCol.setMaximum(10);
		spinnerNoCol.setMinimum(1);
		spinnerNoCol.setSelection(7);

		loadFromConfig();

		return mainArea;
	}

	private void loadFromConfig() {
		java.util.List<Labor> labors = new ArrayList<Labor>();

		String localLabors = ConfigServiceHolder.getGlobal(Constants.CFG_LOCAL_LABORS,
				Constants.CFG_DEFAULT_LOCAL_LABORS);
		String[] laborIds = localLabors.split("\\s*,\\s*");
		for (String laborId : laborIds) {
			if (!StringTool.isNothing(laborId)) {
				Labor labor = Labor.load(laborId);
				if (labor != null && labor.exists()) {
					labors.add(labor);
				}
			}
		}

		IStructuredSelection selection = new StructuredSelection(labors);
		ownLaborsList.setSelection(selection);

		spinnerNoCol.setSelection(CoreHub.localCfg.get(Constants.CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS,
				new Integer(Constants.CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS_DEFAULT)));
	}

	private void storeToConfig() {
		java.util.List<String> selectedLaborsIds = new ArrayList<String>();

		IStructuredSelection selection = (IStructuredSelection) ownLaborsList.getSelection();
		for (Object element : selection.toArray()) {
			if (element instanceof Labor) {
				Labor labor = (Labor) element;
				selectedLaborsIds.add(labor.getId());
			}
		}

		String cfgValue = StringTool.join(selectedLaborsIds, ",");
		ConfigServiceHolder.setGlobal(Constants.CFG_LOCAL_LABORS, cfgValue);

		if (spinnerNoCol.getSelection() != CoreHub.localCfg.get(Constants.CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS,
				Constants.CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS_DEFAULT)) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Information",
					"Bitte schliessen Sie den Messwerte Iatrix View \n "
							+ "und öffnen Sie Ihn dann erneut um die Änderungen wirksam zu machen.");
			CoreHub.localCfg.set(Constants.CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS, spinnerNoCol.getSelection());
		}

	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}

	protected void performDefaults() {
		// default for labors list: no selection
		IStructuredSelection selection = new StructuredSelection();
		ownLaborsList.setSelection(selection);

		// default for number of columns: CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS_DEFAULT
		// String
		spinnerNoCol.setSelection(Constants.CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS_DEFAULT);

		super.performDefaults();
	}

	public boolean performOk() {
		storeToConfig();

		return true;
	}

	class LaborsListContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			Query<Labor> query = new Query<Labor>(Labor.class);
			query.orderBy(false, "Name");
			java.util.List<Labor> labors = query.execute();
			if (labors != null) {
				return labors.toArray();
			} else {
				return new Object[] {};
			}
		}

		public void dispose() {
			// nothing to do
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// nothing to do
		}

	}

	class LaborsListLabelProvider extends LabelProvider {
		public String getText(Object element) {
			if (element instanceof Labor) {
				Labor labor = (Labor) element;
				return labor.getLabel();
			} else
				return super.getText(element);
		}
	}
}
