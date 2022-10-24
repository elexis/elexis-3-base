/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.views;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.IPlannable;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.series.SerienTermin;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.util.ElexisIdGenerator;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.dialogs.DateSelectorDialog;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public class TagesView extends BaseAgendaView {
	public static final String ID = "ch.elexis.agenda.tagesview"; //$NON-NLS-1$
	Button bDay, bToday, bPrint;
	Text tDetail;

	Label lCreator;

	public TagesView() {
		self = this;
	}

	@Override
	public void create(Composite parent) {
		parent.setLayout(new GridLayout());
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout(5, false));
		top.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		bToday = new Button(top, SWT.CENTER | SWT.PUSH | SWT.FLAT);
		bToday.setImage(UiDesk.getImage(Activator.IMG_HOME));
		bToday.setToolTipText(Messages.TagesView_showToday);
		bToday.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TimeTool dat = new TimeTool();
				agenda.setActDate(dat);
				updateDate();
			}

		});

		Button bMinus = new Button(top, SWT.PUSH);
		bMinus.setToolTipText(Messages.TagesView_previousDay);
		bMinus.setText("<"); //$NON-NLS-1$
		bMinus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TimeTool dat=Activator.getDefault().theDay;
				agenda.addDays(-1);
				updateDate();
			}
		});

		bDay = new Button(top, SWT.CENTER | SWT.PUSH | SWT.FLAT);
		bDay.setToolTipText(Messages.TagesView_selectDay);
		bDay.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		bDay.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DateSelectorDialog dsl = new DateSelectorDialog(bDay.getShell(), agenda.getActDate());
				// Point pt=bDay.getLocation();
				// dsl.getShell().setLocation(pt.x, pt.y);
				dsl.create();
				Point m = UiDesk.getDisplay().getCursorLocation();
				dsl.getShell().setLocation(m.x, m.y);
				if (dsl.open() == Dialog.OK) {
					TimeTool dat = dsl.getSelectedDate();
					agenda.setActDate(dat);
					updateDate();
				}
			}

		});
		bDay.setText(agenda.getActDate().toString(TimeTool.DATE_GER));

		Button bPlus = new Button(top, SWT.PUSH);
		bPlus.setToolTipText(Messages.TagesView_nextDay);

		bPlus.setText(">"); //$NON-NLS-1$
		bPlus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				agenda.addDays(1);
				updateDate();
			}
		});

		Button bPrint = new Button(top, SWT.CENTER | SWT.PUSH | SWT.FLAT);
		bPrint.setImage(Images.IMG_PRINTER.getImage());
		bPrint.setToolTipText(Messages.TagesView_printDay);
		bPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				printAction.run();
			}
		});

		SashForm sash = new SashForm(parent, SWT.VERTICAL);
		sash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tv = new TableViewer(sash, SWT.NONE);
		tv.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tv.setLabelProvider(new AgendaLabelProvider());

		tDetail = new Text(sash, SWT.MULTI | SWT.BORDER | SWT.WRAP);
		lCreator = new Label(parent, SWT.NONE);
		lCreator.setFont(UiDesk.getFont(Preferences.USR_SMALLFONT));
		lCreator.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		lCreator.setText(" - "); //$NON-NLS-1$

		sash.setWeights(new int[] { 80, 20 });
		makePrivateActions();
	}

	class AgendaLabelProvider extends LabelProvider implements ITableColorProvider, ITableLabelProvider {

		public Color getBackground(Object element, int columnIndex) {
			if (element instanceof IPlannable) {
				IPlannable p = (IPlannable) element;
				return Plannables.getStatusColor(p);
			}
			return null;
		}

		public Color getForeground(Object element, int columnIndex) {
			if (element instanceof IPlannable) {
				IPlannable p = (IPlannable) element;
				return SWTHelper.getContrast(Plannables.getTypColor(p));
			}
			return null;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof IPlannable) {
				IPlannable p = (IPlannable) element;
				if (p.isRecurringDate())
					return UiDesk.getImage(Activator.IMG_RECURRING_DATE);
				return Plannables.getTypImage(p);
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IPlannable) {
				IPlannable p = (IPlannable) element;
				StringBuilder sb = new StringBuilder();
				sb.append(Plannables.getStartTimeAsString(p)).append("-") //$NON-NLS-1$
						.append(Plannables.getEndTimeAsString(p)).append(StringUtils.SPACE);

				if (p.isRecurringDate()) {
					sb.append(new SerienTermin(p).getRootTermin().getTitle());
				} else {
					sb.append(p.getTitle());
				}

				// show reason if its configured
				if (ConfigServiceHolder.getUser(PreferenceConstants.AG_SHOW_REASON, false)) {
					if (p instanceof Termin) {
						String grund = ((Termin) p).getGrund();
						if (!StringTool.isNothing(grund)) {
							String[] tokens = grund.split("[\n\r]+"); //$NON-NLS-1$
							if (tokens.length > 0) {
								sb.append(", " + tokens[0]); //$NON-NLS-1$
							}
						}
					}
				}

				return sb.toString();
			}
			return "?"; //$NON-NLS-1$
		}

	}

	public void updateDate() {
		/*
		 * if (pinger != null) { pinger.doSync(); }
		 */
		bDay.setText(agenda.getActDate().toString(TimeTool.WEEKDAY) + ", " //$NON-NLS-1$
				+ agenda.getActDate().toString(TimeTool.DATE_GER));
		tv.refresh();
	}

	@Override
	public void setTermin(Termin tf) {
		Termin t = tf;
		StringBuilder sb = new StringBuilder(200);
		TimeSpan ts = t.getTimeSpan();
		sb.append(ts.from.toString(TimeTool.TIME_SMALL)).append("-").append(ts.until.toString(TimeTool.TIME_SMALL)) //$NON-NLS-1$
				.append(StringUtils.SPACE);
		if (t.isRecurringDate()) {
			sb.append(new SerienTermin(t).getRootTermin().getPersonalia());
		} else {
			sb.append(t.getPersonalia());
		}
		sb.append("\n(") //$NON-NLS-1$ //$NON-NLS-2$
				.append(t.getType()).append(",").append(t.getStatus()).append(")\n--------\n").append(t.getGrund()); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("\n--------\n").append(t.getStatusHistoryDesc());
		tDetail.setText(sb.toString());
		sb.setLength(0);
		sb.append(StringTool.unNull(t.get(Termin.FLD_CREATOR))).append("/").append( // $NON-NLS-2$
				t.getCreateTime().toString(TimeTool.FULL_GER));
		lCreator.setText(sb.toString());
		agenda.dispatchTermin(t);

	}

	private void makePrivateActions() {
		newViewAction = new Action(Messages.TagesView_newWindow) {
			@Override
			public void run() {
				try {
					getViewSite().getPage().showView(ID, ElexisIdGenerator.generateId(), IWorkbenchPage.VIEW_VISIBLE);
				} catch (PartInitException e) {
					ExHandler.handle(e);
				}
			}
		};
	}
}
