/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.agenda.views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.slf4j.LoggerFactory;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.BereichSelectionHandler;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.TagesNachricht;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IAppointmentSeries;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.format.PersonFormatUtil;
import ch.elexis.core.model.util.ElexisIdGenerator;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/**
 * A larger view for the agenda with more features than the compact "TagesView"
 *
 * @author gerry
 *
 */
public class AgendaGross extends BaseAgendaView {
	public static final String ID = "ch.elexis.agenda.largeview"; //$NON-NLS-1$
	public static final String CFG_VERTRELATION = "vertrelation"; //$NON-NLS-1$
	private static final String SEPARATOR = ",";
	private static final int[] DEFAULT_COLUMN_WIDTHS = { 60, 60, 105, 80, 300, 200 };

	DateTime calendar;
	Composite cButtons;
	Text dayMessage;
	Text terminDetail;
	Label lbDetails;
	Label lbDayString;
	private int[] sashWeights = null;
	private SashForm sash;
	private static Button[] bChange;

	private static final String[] columnTitles = { "von", "bis", "Typ", "Status", "Personalien", "Grund" };

	public AgendaGross() {
		BereichSelectionHandler.addBereichSelectionListener(this);
	}

	@Override
	public void create(Composite parent) {
		parent.setLayout(new GridLayout());

		cButtons = new Composite(parent, SWT.BORDER);
		RowLayout rl = new RowLayout();
		cButtons.setLayout(rl);
		cButtons.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayout(new GridLayout());
		sash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		updateAreasButtons();

		Composite ret = new Composite(sash, SWT.NONE);
		Composite right = new Composite(sash, SWT.BORDER);

		ret.setLayout(new GridLayout());
		right.setLayout(new GridLayout());
		right.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		tv = new TableViewer(ret, SWT.FULL_SELECTION | SWT.SINGLE);
		tv.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		calendar = new DateTime(right, SWT.CALENDAR | SWT.CALENDAR_WEEKNUMBERS);
		calendar.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		calendar.setDate(agenda.getActDate().get(TimeTool.YEAR), agenda.getActDate().get(TimeTool.MONTH),
				agenda.getActDate().get(TimeTool.DAY_OF_MONTH));
		Button bToday = new Button(right, SWT.PUSH);
		bToday.setText(Messages.AgendaGross_today);
		bToday.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		bToday.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				TimeTool dat = new TimeTool();
				agenda.setActDate(dat);
				calendar.setDate(dat.get(TimeTool.YEAR), dat.get(TimeTool.MONTH), dat.get(TimeTool.DAY_OF_MONTH));
				updateDate();
			}

		});
		dayMessage = SWTHelper.createText(right, 4, SWT.V_SCROLL);

		// set text field's maximum width to the width of the calendar
		GridData gd = (GridData) dayMessage.getLayoutData();
		gd.widthHint = calendar.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

		dayMessage.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String tx = dayMessage.getText();
				TagesNachricht tn = TagesNachricht.load(agenda.getActDate());
				if (tn.exists()) {
					tn.setLangtext(tx);
				} else {
					tn = new TagesNachricht(agenda.getActDate(), " - ", tx); //$NON-NLS-1$
				}
			}

		});
		terminDetail = SWTHelper.createText(right, 5, SWT.NONE);
		terminDetail.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		lbDetails = new Label(right, SWT.WRAP);
		lbDetails.setText("-"); //$NON-NLS-1$
		lbDetails.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		lbDayString = new Label(ret, SWT.NONE);

		tv.setLabelProvider(new AgendaLabelProvider());
		Table table = tv.getTable();
		int[] columnWidths = loadColumnWidths();
		for (int i = 0; i < columnTitles.length; i++) {
			TableColumn tc = new TableColumn(table, SWT.NONE);
			tc.setText(columnTitles[i]);
			tc.setWidth(columnWidths[i]);
		}
		table.setHeaderVisible(true);
		makePrivateActions();
		calendar.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				LocalDate localDate = LocalDate.of(calendar.getYear(), calendar.getMonth() + 1, calendar.getDay());
				agenda.setActDate(new TimeTool(localDate));
				updateDate();
			}

		});

		sash.setWeights(sashWeights == null ? new int[] { 70, 30 } : sashWeights);

		tv.getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				saveColumnSizes();
			}
		});
		// set initial widget values
		initialize();
	}

	private void updateAreasButtons() {
		if(bChange != null && bChange.length > 0) {
			for (Button button : bChange) {
				if (button != null && !button.isDisposed()) {
					button.setVisible(false);
					button.dispose();
				}
			}
		}
		String[] bereiche = AppointmentServiceHolder.get().getAoboAreas().stream().map(a -> a.getName())
				.collect(Collectors.toList()).toArray(new String[0]);
		ChangeBereichAdapter chb = new ChangeBereichAdapter();
		bChange = new Button[bereiche.length];
		for (int i = 0; i < bereiche.length; i++) {
			bChange[i] = new Button(cButtons, SWT.RADIO);
			bChange[i].setText(bereiche[i]);
			bChange[i].addSelectionListener(chb);
			if (bereiche[i].equals(agenda.getActResource())) {
				bChange[i].setSelection(true);
			}
		}
		cButtons.layout();
	}

	/*
	 * Intialize dayMessage field
	 */
	protected void initialize() {
		setDayMessage();
	}

	protected void setDayMessage() {
		TagesNachricht tn = TagesNachricht.load(agenda.getActDate());
		lbDayString.setText(StringUtils.EMPTY);
		dayMessage.setText(StringUtils.EMPTY);
		if (tn.exists()) {
			lbDayString.setText(tn.getZeile());
			dayMessage.setText(tn.getLangtext());
		}
	}

	protected void updateDate() {
		setDayMessage();
		tv.refresh();
	}

	@Override
	protected void userChanged() {
		super.userChanged();

		updateAreasButtons();
	}

	private void saveColumnSizes() {
		if (ConfigServiceHolder.getUser(PreferenceConstants.AG_BIG_SAVE_COLUMNWIDTH, true)) {
			StringBuilder sb = new StringBuilder();
			TableColumn[] columns = tv.getTable().getColumns();
			for (TableColumn tc : columns) {
				sb.append(tc.getWidth());
				sb.append(SEPARATOR);
			}
			ConfigServiceHolder.setUser(PreferenceConstants.AG_BIG_COLUMNWIDTH, sb.toString());
		}
	}

	private int[] loadColumnWidths() {
		int colWidth[] = DEFAULT_COLUMN_WIDTHS;

		// load user preferences if settings require it
		if (ConfigServiceHolder.getUser(PreferenceConstants.AG_BIG_SAVE_COLUMNWIDTH, true)) {
			String defaultColWidths = Arrays.toString(DEFAULT_COLUMN_WIDTHS).replace("[", StringUtils.EMPTY)
					.replace("]", StringUtils.EMPTY);
			String userColWidths = ConfigServiceHolder.getUser(PreferenceConstants.AG_BIG_COLUMNWIDTH,
					defaultColWidths);

			String[] widthStrings = userColWidths.split(SEPARATOR);
			for (int i = 0; i < widthStrings.length; i++) {
				colWidth[i] = Integer.parseInt(widthStrings[i].trim());
			}
		}
		return colWidth;
	}

	private class AgendaLabelProvider extends LabelProvider implements ITableColorProvider, ITableLabelProvider {

		@Override
		public Color getBackground(Object element, int columnIndex) {
			if (element instanceof IAppointment) {
				IAppointment p = (IAppointment) element;
				if (columnIndex == 3) {
					return getStateColor(p);
				} else {
					return getTypColor(p);
				}
			}
			return null;
		}

		@Override
		public Color getForeground(Object element, int columnIndex) {
			if (element instanceof IAppointment) {
				IAppointment p = (IAppointment) element;
				if (columnIndex == 3) {
					return SWTHelper.getContrast(getStateColor(p));
				} else {
					return SWTHelper.getContrast(getTypColor(p));
				}
			}
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex != 4)
				return null;
			if (element instanceof IAppointment) {
				IAppointment ip = (IAppointment) element;
				if (ip.isRecurring())
					return UiDesk.getImage(Activator.IMG_RECURRING_DATE);
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IAppointment) {
				IAppointment ip = (IAppointment) element;
				switch (columnIndex) {
				case 0:
					return DateTimeFormatter.ofPattern("HH:mm").format(ip.getStartTime());
				case 1:
					return DateTimeFormatter.ofPattern("HH:mm").format(ip.getEndTime());
				case 2:
					return ip.getType();
				case 3:
					return ip.getState();
				case 4:
					return ip.isRecurring()
							? AppointmentServiceHolder.get().getAppointmentSeries(ip).get().getRootAppointment()
									.getSubjectOrPatient()
							: ip.getSubjectOrPatient();
				case 5:
					String grund = ip.getReason();
						if (grund != null) {
							String[] tokens = grund.split("[\r\n]+"); //$NON-NLS-1$
							if (tokens.length > 0) {
								grund = tokens[0];
							}
						}
						return grund == null ? StringUtils.EMPTY : grund;
				}
			}
			return "?"; //$NON-NLS-1$
		}

	}

	private class ChangeBereichAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent ev) {
			Button source = (Button) ev.getSource();
			String bereich = source.getText();
			setBereich(bereich);
			tv.refresh();
		}

	}

	@Override
	public void setAppointment(IAppointment appointment) {
		IContact contact = appointment.getContact();
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
		terminDetail.setText(sb.toString());
		sb.setLength(0);
		sb.append(StringTool.unNull(appointment.getCreatedBy())).append("/") //$NON-NLS-1$
				.append(getCreateTime(appointment).toString(TimeTool.FULL_GER));
		lbDetails.setText(sb.toString());
		ContextServiceHolder.get().setTyped(appointment);
		if (contact != null && contact.isPatient()) {
			ContextServiceHolder.get().setActivePatient(contact.asIPatient());
		}
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
		newViewAction = new Action(Messages.AgendaGross_newWindow) {
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

	@Override
	public void bereichSelectionEvent(String bereich) {
		super.bereichSelectionEvent(bereich);
		for (Button b : bChange) {
			if (b != null && !b.isDisposed()) {
				if (b.getText().equalsIgnoreCase(bereich)) {
					b.setSelection(true);
				} else {
					b.setSelection(false);
				}
			}
		}

	}

	@Override
	public void saveState(IMemento memento) {
		int[] w = sash.getWeights();
		memento.putString(CFG_VERTRELATION, Integer.toString(w[0]) + StringConstants.COMMA + Integer.toString(w[1]));

		super.saveState(memento);
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		if (memento == null) {
			sashWeights = new int[] { 70, 30 };
		} else {
			String state = memento.getString(CFG_VERTRELATION);
			if (state == null) {
				state = "70,30"; //$NON-NLS-1$
			}
			String[] sw = state.split(StringConstants.COMMA);
			sashWeights = new int[] { Integer.parseInt(sw[0]), Integer.parseInt(sw[1]) };
		}
		super.init(site, memento);
	}
}
