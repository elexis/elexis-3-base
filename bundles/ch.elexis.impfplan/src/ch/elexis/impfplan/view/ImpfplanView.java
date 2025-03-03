/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.impfplan.view;

import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Patient;
import ch.elexis.impfplan.controller.ImpfplanController;
import ch.elexis.impfplan.controller.VaccinationSorter;
import ch.elexis.impfplan.model.Vaccination;
import ch.elexis.impfplan.model.VaccinationType;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;

public class ImpfplanView extends ViewPart implements IRefreshable {
	private IAction addVacination, printVaccinations, removeVaccination;
	TableViewer tvVaccsDone;
	TableViewer tvVaccsRecommended;
	int[] columnWidths = new int[] { 300, 100 };
	String[] columnTitles = new String[] { Messages.ImpfplanView_vaccinationColumn, Messages.ImpfplanView_dateColumn };
	ScrolledForm form;
	VaccinationSorter sorter = new VaccinationSorter();
	private boolean isFirstTime = true;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	@Inject
	void activePatient(@Optional IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			tvVaccsDone.refresh();
			tvVaccsRecommended.refresh();
			if (patient != null) {
				addVacination.setEnabled(true);
				printVaccinations.setEnabled(true);
			}
		}, tvVaccsDone);
	}

	@Override
	public void createPartControl(Composite parent) {
		form = UiDesk.getToolkit().createScrolledForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new GridLayout());
		makeActions();
		ViewMenus menu = new ViewMenus(getViewSite());
		menu.createToolbar(addVacination, printVaccinations);
		Label lblVaccsDone = new Label(body, SWT.NONE);
		lblVaccsDone.setText(Messages.ImpfplanView_vaccinationsDOne);
		Table tVaccsDone = new Table(body, SWT.FULL_SELECTION);
		tVaccsDone.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		for (int i = 0; i < columnTitles.length; i++) {
			TableColumn tc = new TableColumn(tVaccsDone, SWT.NONE);
			tc.setWidth(columnWidths[i]);
			tc.setText(columnTitles[i]);
		}
		tVaccsDone.setHeaderVisible(true);
		tVaccsDone.setLinesVisible(true);
		tvVaccsDone = new TableViewer(tVaccsDone);
		tvVaccsDone.setContentProvider(new ContentProviderAdapter() {

			@Override
			public Object[] getElements(Object inputElement) {
				Patient actPatient = ElexisEventDispatcher.getSelectedPatient();
				if (actPatient != null) {
					Collection<Vaccination> r = ImpfplanController.getVaccinations(actPatient);
					return r.toArray();

				}
				return new Object[0];
			}
		});
		MenuManager contextMenu = new MenuManager();
		contextMenu.add(removeVaccination);
		tvVaccsDone.getControl().setMenu(contextMenu.createContextMenu(tvVaccsDone.getControl()));
		tvVaccsDone.setSorter(sorter);
		tvVaccsDone.setLabelProvider(new VaccinationLabelProvider());

		Label lblVaccsReccomended = new Label(body, SWT.NONE);
		lblVaccsReccomended.setText(Messages.ImpfplanView_vaccinationsRecommended);
		Table tVaccsRecommended = new Table(body, SWT.FULL_SELECTION);
		tVaccsRecommended.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		for (int i = 0; i < columnTitles.length; i++) {
			TableColumn tc = new TableColumn(tVaccsRecommended, SWT.NONE);
			tc.setWidth(columnWidths[i]);
			tc.setText(columnTitles[i]);
		}
		tVaccsRecommended.setHeaderVisible(true);
		tVaccsRecommended.setLinesVisible(true);
		tvVaccsRecommended = new TableViewer(tVaccsRecommended);

		tvVaccsRecommended.setContentProvider(new ContentProviderAdapter() {

			@Override
			public Object[] getElements(Object inputElement) {
				Patient actPatient = ElexisEventDispatcher.getSelectedPatient();
				if (actPatient != null) {
					try {
						List<VaccinationType> r = VaccinationType.findDueFor(actPatient);
						return r.toArray();

					} catch (ElexisException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				return new Object[0];
			}
		});
		tvVaccsRecommended.setSorter(sorter);
		tvVaccsRecommended.setLabelProvider(new VaccinationLabelProvider());
		tvVaccsRecommended.setInput(this);

		tvVaccsDone.setInput(this);

		boolean enable = ElexisEventDispatcher.getSelectedPatient() != null;
		addVacination.setEnabled(enable);
		printVaccinations.setEnabled(enable);
		getSite().getPage().addPartListener(udpateOnVisible);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		if (isFirstTime) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Ansicht veraltet", "Die Ansicht "
					+ getTitle()
					+ " ist veraltet und wird nicht mehr unterstützt. Bitte verwenden Sie die Impfliste Ansicht.");
			isFirstTime = false;
		}
	}

	private void makeActions() {
		addVacination = new Action(Messages.ImpfplanView_vaccinateActionTitle) {
			{
				setToolTipText(Messages.ImpfplanView_vaccinateActionTooltip);
				setImageDescriptor(Images.IMG_ADDITEM.getImageDescriptor());
			}

			@Override
			public void run() {
				AddVaccinationDialog dlg = new AddVaccinationDialog(getViewSite().getShell());
				if (dlg.open() == Dialog.OK) {
					new Vaccination(dlg.result, ElexisEventDispatcher.getSelectedPatient(), new TimeTool(dlg.date),
							dlg.bUnexact);
					tvVaccsDone.refresh();
					tvVaccsRecommended.refresh();
				}
			}

		};

		printVaccinations = new Action(Messages.ImpfplanView_printActionTitle) {
			{
				setToolTipText(Messages.ImpfplanView_printActionTooltip);
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());

			}

			@Override
			public void run() {
				ImpfplanPrinter ipr = new ImpfplanPrinter(getSite().getShell());
				ipr.open();
			}
		};

		removeVaccination = new Action(Messages.ImpfplanView_removeActionTitle) {
			{
				setToolTipText(Messages.ImpfplanView_removeActionTooltip);
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}

			@Override
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) tvVaccsDone.getSelection();
				if (!sel.isEmpty()) {
					Vaccination v = (Vaccination) sel.getFirstElement();
					if (v.delete()) {
						tvVaccsDone.remove(v);
					}
				}

			}
		};
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}
}
