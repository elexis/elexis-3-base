/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.agenda.ui.week;

import java.util.ArrayList;

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
import ch.elexis.agenda.ui.BaseView;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.dialogs.DateSelectorDialog;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class AgendaWeek extends BaseView {
	private IAction weekFwdAction, weekBackAction, showCalendarAction;

	private ProportionalSheet sheet;
	private ColumnHeader header;
	private boolean isFirstTime = true;
	public AgendaWeek() {

	}

	public ColumnHeader getHeader() {
		return header;
	}

	@Override
	protected void create(Composite parent) {
		makePrivateActions();
		Composite wrapper = new Composite(parent, SWT.NONE);
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
		TimeTool tt = new TimeTool();
		for (String s : getDisplayedDays()) {
			tt.set(s);
			checkDay(null, tt);
		}
	}

	void clear() {
		sheet.clear();
	}

	@Override
	protected IPlannable getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void refresh() {
		TimeTool ttMonday = agenda.getActDate();
		ttMonday.set(TimeTool.DAY_OF_WEEK, TimeTool.MONDAY);
		StringBuilder sb = new StringBuilder(ttMonday.toString(TimeTool.DATE_GER));
		ttMonday.addDays(6);
		sb.append("-").append(ttMonday.toString(TimeTool.DATE_GER)); //$NON-NLS-1$

		showCalendarAction.setText(sb.toString());
		sheet.refresh();
		
	}

	@Override
	public void setFocus() {
		if (isFirstTime) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), (Messages.OUTDATED_VIEW),
					(Messages.THE_VIEW)
							+ getTitle() + " "
							+ (Messages.OUTDATED_USE_OTHER_VIEW));
			isFirstTime = false;
		}

		refresh();
	}

	public String[] getDisplayedDays() {
		TimeTool ttMonday = Activator.getDefault().getActDate();
		ttMonday.set(TimeTool.DAY_OF_WEEK, TimeTool.MONDAY);
		ttMonday.chop(3);
		String resources = CoreHub.localCfg.get(PreferenceConstants.AG_DAYSTOSHOW,
				StringTool.join(TimeTool.Wochentage, ",")); //$NON-NLS-1$
		if (resources == null) {
			return new String[0];
		} else {
			ArrayList<String> ret = new ArrayList<String>(resources.length());
			for (TimeTool.DAYS wd : TimeTool.DAYS.values()) {
				if (resources.indexOf(wd.fullName) != -1) {
					ret.add(ttMonday.toString(TimeTool.DATE_COMPACT));
				}
				ttMonday.addDays(1);
			}
			return ret.toArray(new String[0]);
		}
	}

	private void makePrivateActions() {
		weekFwdAction = new Action(Messages.AgendaWeek_weekForward) {
			{
				setToolTipText(Messages.AgendaWeek_showNextWeek);
				setImageDescriptor(Images.IMG_NEXT.getImageDescriptor());
			}

			@Override
			public void run() {
				agenda.addDays(7);
				TimeTool tt = new TimeTool();
				for (String s : getDisplayedDays()) {
					tt.set(s);
					checkDay(null, tt);
				}
				refresh();
			}
		};

		weekBackAction = new Action(Messages.AgendaWeek_weekBackward) {
			{
				setToolTipText(Messages.AgendaWeek_showPreviousWeek);
				setImageDescriptor(Images.IMG_PREVIOUS.getImageDescriptor());
			}

			@Override
			public void run() {
				agenda.addDays(-7);
				TimeTool tt = new TimeTool();
				for (String s : getDisplayedDays()) {
					tt.set(s);
					checkDay(null, tt);
				}
				refresh();
			}
		};
		showCalendarAction = new Action(Messages.AgendaWeek_selectWeek) {
			{
				setToolTipText(Messages.AgendaWeek_showCalendarToSelect);
				// setImageDescriptor(Activator.getImageDescriptor("icons/calendar.png"));
			}

			@Override
			public void run() {
				DateSelectorDialog dsl = new DateSelectorDialog(getViewSite().getShell(), agenda.getActDate());
				if (dsl.open() == Dialog.OK) {
					agenda.setActDate(dsl.getSelectedDate());
					TimeTool tt = new TimeTool();
					for (String s : getDisplayedDays()) {
						tt.set(s);
						checkDay(null, tt);
					}

					refresh();
				}
			}
		};

		final IAction zoomAction = new Action(Messages.AgendaWeek_zoom, Action.AS_DROP_DOWN_MENU) {
			Menu mine;
			{
				setToolTipText(Messages.AgendaWeek_setZoomFactor);
				setImageDescriptor(Activator.getImageDescriptor("icons/zoom.png")); //$NON-NLS-1$
				setMenuCreator(new IMenuCreator() {

					public void dispose() {
						mine.dispose();
					}

					public Menu getMenu(Control parent) {
						mine = new Menu(parent);
						fillMenu();
						return mine;
					}

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
				for (String s : new String[] { "40", "60", "80", "100", "120", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						"140", "160", "200", "300" }) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					MenuItem it = new MenuItem(mine, SWT.RADIO);
					it.setText(s + "%"); //$NON-NLS-1$
					it.setData(s);
					it.setSelection(Integer.parseInt(s) == currentFactor);
					it.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							MenuItem mi = (MenuItem) e.getSource();
							int scale = Integer.parseInt(mi.getText().split("%")[0]); //$NON-NLS-1$
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
		tmr.add(weekBackAction);
		tmr.add(showCalendarAction);
		tmr.add(weekFwdAction);
		tmr.add(new Separator());
		tmr.add(zoomAction);
	}

}
