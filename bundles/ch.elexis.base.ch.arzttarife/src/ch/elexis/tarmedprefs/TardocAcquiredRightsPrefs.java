package ch.elexis.tarmedprefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulantePauschalenTyp;
import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.dialogs.DiagnoseSelektor;
import ch.elexis.core.ui.dialogs.ServiceSelektor;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.icons.Images;
import jakarta.inject.Inject;

public class TardocAcquiredRightsPrefs extends PreferencePage implements IWorkbenchPreferencePage {

	@Inject
	private ICodeElementService codeElementService;

	private Optional<IMandator> currentMandator;

	private List<ICodeElement> currentAcquiredRightsCoding;

	private TableViewer viewer;
	private ToolBarManager toolbarmgr;

	@Override
	public void init(IWorkbench workbench) {
		CoreUiUtil.injectServices(this);
		setMessage("Konfiguration der Besitzstand Leistungen pro Mandant.");

		currentAcquiredRightsCoding = new ArrayList<>();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());

		final ComboViewer mandatorCombo = new ComboViewer(ret, SWT.BORDER);
		mandatorCombo.setContentProvider(ArrayContentProvider.getInstance());
		mandatorCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IMandator) {
					return ((IMandator) element).getDescription1() + StringUtils.SPACE
							+ ((IMandator) element).getDescription2() + " (" + ((IMandator) element).getDescription3()
							+ ")";
				}
				return super.getText(element);
			}
		});
		List<IMandator> input = CoreModelServiceHolder.get().getQuery(IMandator.class)
				.orderBy(ModelPackage.Literals.ICONTACT__DESCRIPTION1, ORDER.ASC).execute();
		mandatorCombo.setInput(input);
		mandatorCombo.getCombo().addListener(SWT.MouseWheel, event -> {
			if (!mandatorCombo.getCombo().isFocusControl()) {
				event.doit = false;
			}
		});
		mandatorCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		toolbarmgr = new ToolBarManager();
		toolbarmgr.add(new AddTardocAcquiredRightAction());
		toolbarmgr.add(new AddAllowanceAcquiredRightAction());
		toolbarmgr.add(new RemoveAcquiredRightAction());
		ToolBar toolbar = toolbarmgr.createControl(ret);
		toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));

		Composite tableComposite = new Composite(ret, SWT.NONE);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableComposite.setLayout(new FillLayout());
		viewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.getTable().setHeaderVisible(true);

		TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);

		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText("Code");
		columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(30, 60));
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICodeElement) {
					return ((ICodeElement) element).getCode();
				}
				return super.getText(element);
			}
		});

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText("System");
		columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(30, 60));
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICodeElement) {
					return ((ICodeElement) element).getCodeSystemName();
				}
				return super.getText(element);
			}
		});

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText("Text");
		columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(70, 140));
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICodeElement) {
					return ((ICodeElement) element).getText();
				}
				return super.getText(element);
			}
		});

		currentMandator = ContextServiceHolder.get().getActiveMandator();
		if (currentMandator.isPresent()) {
			mandatorCombo.setSelection(new StructuredSelection(currentMandator.get()));
			currentAcquiredRightsCoding = new ArrayList<>(
					ArzttarifeUtil.getMandantTardocAcquiredRights(currentMandator.get(), codeElementService));
			refresh();
		}

		mandatorCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// save possible changes
				save();

				currentMandator = Optional.of((IMandator) mandatorCombo.getStructuredSelection().getFirstElement());
				currentAcquiredRightsCoding = new ArrayList<>(
						ArzttarifeUtil.getMandantTardocAcquiredRights(currentMandator.get(), codeElementService));
				refresh();
			}
		});

		return ret;
	}

	@Override
	public boolean performOk() {
		save();
		return super.performOk();
	}

	private void save() {
		if (currentMandator.isPresent()) {
			ArzttarifeUtil.setMandantTardocAcquiredRights(currentMandator.get(), currentAcquiredRightsCoding);
			CoreModelServiceHolder.get().save(currentMandator.get());
		}
	}

	private void refresh() {
		if (currentMandator.isPresent()) {
			currentAcquiredRightsCoding.sort((l, r) -> {
				return l.getCode().compareTo(r.getCode());
			});
			viewer.setInput(currentAcquiredRightsCoding);
		} else {
			viewer.setInput(Collections.emptyList());
		}
		viewer.refresh();
	}

	private class AddTardocAcquiredRightAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return Images.IMG_NEW.getImageDescriptor();
		}

		@Override
		public String getToolTipText() {
			return "Tardoc Besitzstand hinzufügen";
		}

		@Override
		public void run() {
			ServiceSelektor serviceSelektor = new ServiceSelektor(Display.getDefault().getActiveShell(), "TARDOC",
					true);
			if (serviceSelektor.open() == DiagnoseSelektor.OK) {
				Object[] sel = serviceSelektor.getResult();
				if (sel != null && sel.length > 0) {
					for (Object object : sel) {
						ICodeElement service = (ICodeElement) object;
						if (!currentAcquiredRightsCoding.contains(service)) {
							currentAcquiredRightsCoding.add(service);
						}
					}
					refresh();
				}
			}
		}
	}

	private class AddAllowanceAcquiredRightAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return Images.IMG_NEW.getImageDescriptor();
		}

		@Override
		public String getToolTipText() {
			return "Ambulante Pauschale Besitzstand hinzufügen";
		}

		@Override
		public void run() {
			ServiceSelektor serviceSelektor = new ServiceSelektor(Display.getDefault().getActiveShell(),
					"Ambulantepauschalen", true);
			// dignitaet is known for pauschale not trigger
			serviceSelektor
					.filterServices(t -> ((IAmbulatoryAllowance) t).getTyp() == AmbulantePauschalenTyp.PAUSCHALE);
			if (serviceSelektor.open() == DiagnoseSelektor.OK) {
				Object[] sel = serviceSelektor.getResult();
				if (sel != null && sel.length > 0) {
					for (Object object : sel) {
						ICodeElement service = (ICodeElement) object;
						if (!currentAcquiredRightsCoding.contains(service)) {
							currentAcquiredRightsCoding.add(service);
						}
					}
					refresh();
				}
			}
		}
	}

	private class RemoveAcquiredRightAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return Images.IMG_DELETE.getImageDescriptor();
		}

		@Override
		public String getToolTipText() {
			return "Besitzstand entfernen";
		}

		@Override
		public void run() {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection != null && !selection.isEmpty()) {
				for (Object object : selection) {
					if (object instanceof ICodeElement) {
						currentAcquiredRightsCoding.remove(object);
					}
				}
				refresh();
			}
		}
	}
}
