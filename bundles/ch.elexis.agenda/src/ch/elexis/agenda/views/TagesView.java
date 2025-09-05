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

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.di.UIEventTopic;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.slf4j.LoggerFactory;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IAppointmentSeries;
import ch.elexis.core.model.format.PersonFormatUtil;
import ch.elexis.core.model.util.ElexisIdGenerator;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.dialogs.DateSelectorDialog;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;

public class TagesView extends BaseAgendaView {
	public static final String ID = "ch.elexis.agenda.tagesview"; //$NON-NLS-1$
	public static final String DATE_CHANGED = "ch/elexis/agenda/DATE_CHANGED"; //$NON-NLS-1$
	Button bDay, bToday, bPrint;
	Text tDetail;

	Label lCreator;

	@org.eclipse.e4.core.di.annotations.Optional
	@Inject
	void onDateChanged(@UIEventTopic(DATE_CHANGED) Object ignored) {
		Display.getDefault().asyncExec(this::updateDate);
	}

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

		@Override
		public Color getBackground(Object element, int columnIndex) {
			if (element instanceof IAppointment) {
				IAppointment p = (IAppointment) element;
				return getStateColor(p);
			}
			return null;
		}

		@Override
		public Color getForeground(Object element, int columnIndex) {
			if (element instanceof IAppointment) {
				IAppointment p = (IAppointment) element;
				return SWTHelper.getContrast(getTypColor(p));
			}
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof IAppointment) {
				IAppointment p = (IAppointment) element;
				if (p.isRecurring())
					return UiDesk.getImage(Activator.IMG_RECURRING_DATE);
				return Plannables.getTypImage(p.getType());
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IAppointment) {
				IAppointment p = (IAppointment) element;
				StringBuilder sb = new StringBuilder();
				sb.append(DateTimeFormatter.ofPattern("HH:mm").format(p.getStartTime())).append("-") //$NON-NLS-1$
						.append(DateTimeFormatter.ofPattern("HH:mm").format(p.getEndTime())).append(StringUtils.SPACE);
				sb.append(
						p.isRecurring()
								? AppointmentServiceHolder.get().getAppointmentSeries(p).get().getRootAppointment()
										.getSubjectOrPatient()
								: p.getSubjectOrPatient());

				// show reason if its configured
				if (ConfigServiceHolder.getUser(PreferenceConstants.AG_SHOW_REASON, false)) {
					String grund = p.getReason();
					if (!StringTool.isNothing(grund)) {
						String[] tokens = grund.split("[\n\r]+"); //$NON-NLS-1$
						if (tokens.length > 0) {
							sb.append(", " + tokens[0]); //$NON-NLS-1$
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
	public void setAppointment(IAppointment appointment) {
		StringBuilder sb = new StringBuilder(200);
		sb.append(DateTimeFormatter.ofPattern("HH:mm").format(appointment.getStartTime())).append("-") //$NON-NLS-1$ //$NON-NLS-2$
				.append(DateTimeFormatter.ofPattern("HH:mm").format(appointment.getEndTime())) //$NON-NLS-1$
				.append(StringUtils.SPACE);
		if (appointment.isRecurring()) {
			Optional<IAppointmentSeries> series = AppointmentServiceHolder.get().getAppointmentSeries(appointment);
			if (series.isPresent() && series.get().getContact() != null && series.get().getContact().isPerson()) {
				sb.append(PersonFormatUtil.getPersonalia(series.get().getContact().asIPerson()));
			}
		} else {
			if (appointment.getContact() != null && appointment.getContact().isPerson()) {
				sb.append(PersonFormatUtil.getPersonalia(appointment.getContact().asIPerson()));
			}
		}
		sb.append("\n(") //$NON-NLS-1$ //$NON-NLS-2$
				.append(appointment.getType()).append(",").append(appointment.getState()).append(")\n--------\n") //$NON-NLS-1$ //$NON-NLS-2$
				.append(appointment.getReason());
		sb.append("\n--------\n").append(appointment.getStateHistoryFormatted("dd.MM.yyyy HH:mm:ss"));
		tDetail.setText(sb.toString());
		sb.setLength(0);
		sb.append(StringTool.unNull(appointment.getCreatedBy())).append("/") //$NON-NLS-1$
				.append(getCreateTime(appointment).toString(TimeTool.FULL_GER));
		lCreator.setText(sb.toString());
		agenda.dispatchTermin(appointment);

	}

	public TimeTool getCreateTime(IAppointment appointment) {
		int min = 0;
		try {
			min = Integer.parseInt(appointment.getCreated());
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass())
					.warn("Could not parse appointment create time [" + appointment.getCreated() + "]");
		}
		return new TimeTool(min, 60000);
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
