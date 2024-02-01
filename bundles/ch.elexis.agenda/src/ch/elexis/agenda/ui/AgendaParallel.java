/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Sponsoring:
 * 	 mediX Notfallpaxis, diepraxen Stauffacher AG, Zürich
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.data.IPlannable;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.dialogs.DateSelectorDialog;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/**
 * A View to display ressources side by side in the same view.
 *
 * @author gerry
 *
 */
public class AgendaParallel extends BaseView {

	private IAction dayFwdAction, dayBackAction, showCalendarAction;
	private ProportionalSheet sheet;
	private ColumnHeader header;
	private Composite wrapper;
	private boolean isFirstTime = true;

	public AgendaParallel() {

	}

	public ColumnHeader getHeader() {
		return header;
	}

	@Override
	protected void create(Composite parent) {
		wrapper = new Composite(parent, SWT.NONE);
		wrapper.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		wrapper.setLayout(new GridLayout());
		header = new ColumnHeader(wrapper, this);
		header.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		ScrolledComposite bounding = new ScrolledComposite(wrapper, SWT.V_SCROLL);
		bounding.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		// bounding.setBackground(Desk.getColor(Desk.COL_RED));
		sheet = new ProportionalSheet(bounding, this);
		// sheet.setSize(sheet.computeSize(SWT.DEFAULT,SWT.DEFAULT));
		bounding.setContent(sheet);
		bounding.setMinSize(sheet.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		bounding.setExpandHorizontal(true);
		bounding.setExpandVertical(true);
		makePrivateActions();
		for (String s : getDisplayedResources()) {
			checkDay(s, null);
		}
		refresh();
	}

	@Override
	public void setFocus() {
		if (isFirstTime) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Ansicht veraltet", "Die Ansicht "
					+ getTitle()
					+ " ist veraltet und wird nicht mehr unterstützt. Bitte verwenden Sie die Agenda Web Ansicht.");
			isFirstTime = false;
		}
		sheet.setFocus();
	}

	@Override
	protected IPlannable getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Return the resources to display. This are by default all defined resources,
	 * but users can exclude some of them from display
	 *
	 * @return a stering array with all resources to display
	 */
	public String[] getDisplayedResources() {
		String resources = CoreHub.localCfg.get(PreferenceConstants.AG_RESOURCESTOSHOW,
				StringTool.join(agenda.getResources(), ",")); //$NON-NLS-1$
		if (resources == null) {
			return new String[0];
		} else {
			return resources.split(","); //$NON-NLS-1$
		}
	}

	void clear() {
		sheet.clear();
	}

	@Override
	protected void refresh() {
		showCalendarAction.setText(agenda.getActDate().toString(TimeTool.WEEKDAY) + ", " //$NON-NLS-1$
				+ agenda.getActDate().toString(TimeTool.DATE_GER));
		sheet.refresh();
		wrapper.layout();
		getViewSite().getActionBars().getToolBarManager().update(true);
	}

	private void makePrivateActions() {
		dayFwdAction = new Action(Messages.AgendaParallel_dayForward) {
			{
				setToolTipText(Messages.AgendaParallel_showNextDay);
				setImageDescriptor(Images.IMG_NEXT.getImageDescriptor());
			}

			@Override
			public void run() {
				agenda.addDays(1);
				for (String s : getDisplayedResources()) {
					checkDay(s, null);
				}
				refresh();
			}
		};

		dayBackAction = new Action(Messages.AgendaParallel_dayBack) {
			{
				setToolTipText(Messages.AgendaParallel_showPreviousDay);
				setImageDescriptor(Images.IMG_PREVIOUS.getImageDescriptor());
			}

			@Override
			public void run() {
				agenda.addDays(-1);
				for (String s : getDisplayedResources()) {
					checkDay(s, null);
				}

				refresh();
			}
		};
		showCalendarAction = new Action(Messages.AgendaParallel_selectDay) {
			{
				setToolTipText(Messages.AgendaParallel_showCalendarForSelcetion);
				// setImageDescriptor(Activator.getImageDescriptor("icons/calendar.png"));
			}

			@Override
			public void run() {
				DateSelectorDialog dsl = new DateSelectorDialog(getViewSite().getShell(), agenda.getActDate());
				if (dsl.open() == Dialog.OK) {
					agenda.setActDate(dsl.getSelectedDate());
					for (String s : getDisplayedResources()) {
						checkDay(s, null);
					}

					refresh();
				}
			}
		};

		final IAction zoomAction = new Action(Messages.AgendaParallel_zoom, Action.AS_DROP_DOWN_MENU) {
			Menu mine;
			{
				setToolTipText(Messages.AgendaParallel_setZoomFactor);
				setImageDescriptor(Activator.getImageDescriptor("icons/zoom.png")); //$NON-NLS-1$
				setMenuCreator(new IMenuCreator() {

					@Override
					public void dispose() {
						mine.dispose();
					}

					@Override
					public Menu getMenu(Control parent) {
						mine = new Menu(parent);
						fillMenu();
						return mine;
					}

					@Override
					public Menu getMenu(Menu parent) {
						mine = new Menu(parent);
						fillMenu();
						return mine;
					}
				});
			}

			private void fillMenu() {
				String currentFactorString = CoreHub.localCfg.get(PreferenceConstants.AG_PIXEL_PER_MINUTE, "0.4");
				int currentFactor = (int) (Float.parseFloat(currentFactorString) * 100);
				for (String s : new String[] { "40", "60", "80", "100", "120", "140", "160", "200", "300", "400", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						"500" }) {
					MenuItem it = new MenuItem(mine, SWT.RADIO);
					it.setText(s + "%"); //$NON-NLS-1$
					it.setData(s);
					it.setSelection(Integer.parseInt(s) == currentFactor);
					it.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							MenuItem mi = (MenuItem) e.getSource();
							int scale = Integer.parseInt((String) mi.getData()); // $NON-NLS-1$
							double factor = scale / 100.0;
							CoreHub.localCfg.set(PreferenceConstants.AG_PIXEL_PER_MINUTE, Double.toString(factor));
							sheet.recalc();
						}

					});
				}
			}
		};
		IToolBarManager tmr = getViewSite().getActionBars().getToolBarManager();

		tmr.add(new Separator());
		tmr.add(dayBackAction);
		tmr.add(showCalendarAction);
		tmr.add(dayFwdAction);
		tmr.add(new Separator());
		tmr.add(zoomAction);
		tmr.add(new Separator("agenda_right")); //$NON-NLS-1$
	}
}
