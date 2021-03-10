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
import java.util.Arrays;

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

import ch.elexis.actions.Activator;
import ch.elexis.agenda.BereichSelectionHandler;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.IPlannable;
import ch.elexis.agenda.data.TagesNachricht;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.series.SerienTermin;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeSpan;
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
	private static final int[] DEFAULT_COLUMN_WIDTHS = {
		60, 60, 105, 80, 300, 200
	};
	
	DateTime calendar;
	Composite cButtons;
	Text dayMessage;
	Text terminDetail;
	Label lbDetails;
	Label lbDayString;
	private int[] sashWeights = null;
	private SashForm sash;
	private static Button[] bChange;
	
	private static final String[] columnTitles = {
		"von", "bis", "Typ", "Status", "Personalien", "Grund"
	};
	
	public AgendaGross(){
		BereichSelectionHandler.addBereichSelectionListener(this);
	}
	
	@Override
	public void create(Composite parent){
		parent.setLayout(new GridLayout());
		
		cButtons = new Composite(parent, SWT.BORDER);
		RowLayout rl = new RowLayout();
		cButtons.setLayout(rl);
		cButtons.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayout(new GridLayout());
		sash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		String[] bereiche =
			ConfigServiceHolder.getGlobal(PreferenceConstants.AG_BEREICHE, Messages.TagesView_14)
				.split(","); //$NON-NLS-1$
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
		
		Composite ret = new Composite(sash, SWT.NONE);
		Composite right = new Composite(sash, SWT.BORDER);
		
		ret.setLayout(new GridLayout());
		right.setLayout(new GridLayout());
		right.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		tv = new TableViewer(ret, SWT.FULL_SELECTION | SWT.SINGLE);
		tv.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		calendar = new DateTime(right, SWT.CALENDAR | SWT.CALENDAR_WEEKNUMBERS);
		calendar.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		calendar.setDate(agenda.getActDate().get(TimeTool.YEAR),
			agenda.getActDate().get(TimeTool.MONTH),
			agenda.getActDate().get(TimeTool.DAY_OF_MONTH));
		Button bToday = new Button(right, SWT.PUSH);
		bToday.setText(Messages.AgendaGross_today);
		bToday.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		bToday.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0){
				TimeTool dat = new TimeTool();
				agenda.setActDate(dat);
				calendar.setDate(dat.get(TimeTool.YEAR), dat.get(TimeTool.MONTH),
					dat.get(TimeTool.DAY_OF_MONTH));
				updateDate();
			}
			
		});
		dayMessage = SWTHelper.createText(right, 4, SWT.V_SCROLL);
		
		// set text field's maximum width to the width of the calendar
		GridData gd = (GridData) dayMessage.getLayoutData();
		gd.widthHint = calendar.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		
		dayMessage.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent arg0){
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
			public void widgetSelected(SelectionEvent arg0){
				LocalDate localDate =
					LocalDate.of(calendar.getYear(), calendar.getMonth() + 1, calendar.getDay());
				agenda.setActDate(new TimeTool(localDate));
				updateDate();
			}
			
		});
		
		sash.setWeights(sashWeights == null ? new int[] {
			70, 30
		} : sashWeights);

		tv.getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e){
				saveColumnSizes();
			}
		});		
		// set initial widget values
		initialize();
	}
	
	/*
	 * Intialize dayMessage field
	 */
	protected void initialize(){
		setDayMessage();
	}
	
	protected void setDayMessage(){
		TagesNachricht tn = TagesNachricht.load(agenda.getActDate());
		lbDayString.setText(""); //$NON-NLS-1$
		dayMessage.setText(""); //$NON-NLS-1$
		if (tn.exists()) {
			lbDayString.setText(tn.getZeile());
			dayMessage.setText(tn.getLangtext());
		}
	}
	
	protected void updateDate(){
		setDayMessage();
		/*
		 * if (pinger != null) { pinger.doSync(); }
		 */
		tv.refresh();
		tv.getTable().getColumn(0).pack();
	}
	
	private void saveColumnSizes(){
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
	
	private int[] loadColumnWidths(){
		int colWidth[] = DEFAULT_COLUMN_WIDTHS;
		
		// load user preferences if settings require it
		if (ConfigServiceHolder.getUser(PreferenceConstants.AG_BIG_SAVE_COLUMNWIDTH, true)) {
			String defaultColWidths =
				Arrays.toString(DEFAULT_COLUMN_WIDTHS).replace("[", "").replace("]", "");
			String userColWidths =
				ConfigServiceHolder.getUser(PreferenceConstants.AG_BIG_COLUMNWIDTH, defaultColWidths);
			
			String[] widthStrings = userColWidths.split(SEPARATOR);
			for (int i = 0; i < widthStrings.length; i++) {
				colWidth[i] = Integer.parseInt(widthStrings[i].trim());
			}
		}
		return colWidth;
	}
	
	private class AgendaLabelProvider extends LabelProvider implements ITableColorProvider,
			ITableLabelProvider {
		
		public Color getBackground(Object element, int columnIndex){
			if (element instanceof IPlannable) {
				IPlannable p = (IPlannable) element;
				if (columnIndex == 3) {
					return Plannables.getStatusColor(p);
				} else {
					return Plannables.getTypColor(p);
				}
			}
			return null;
		}
		
		public Color getForeground(Object element, int columnIndex){
			if (element instanceof IPlannable) {
				IPlannable p = (IPlannable) element;
				if (columnIndex == 3) {
					return SWTHelper.getContrast(Plannables.getStatusColor(p));
				} else {
					return SWTHelper.getContrast(Plannables.getTypColor(p));
				}
			}
			return null;
		}
		
		public Image getColumnImage(Object element, int columnIndex){
			if (columnIndex != 4)
				return null;
			if (element instanceof IPlannable) {
				IPlannable ip = (IPlannable) element;
				if (ip.isRecurringDate())
					return UiDesk.getImage(Activator.IMG_RECURRING_DATE);
			}
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex){
			if (element instanceof IPlannable) {
				IPlannable ip = (IPlannable) element;
				switch (columnIndex) {
				case 0:
					return Plannables.getStartTimeAsString(ip);
				case 1:
					return Plannables.getEndTimeAsString(ip);
				case 2:
					return ip.getType();
				case 3:
					return ip.getStatus();
				case 4:
					return ip.isRecurringDate() ? new SerienTermin(ip).getRootTermin().getTitle()
							: ip.getTitle();
				case 5:
					if (ip instanceof Termin) {
						Termin termin = (Termin) ip;
						String grund = termin.getGrund();
						if (grund != null) {
							String[] tokens = grund.split("[\r\n]+"); //$NON-NLS-1$
							if (tokens.length > 0) {
								grund = tokens[0];
							}
						}
						return grund == null ? "" : grund; //$NON-NLS-1$
					} else {
						return ""; //$NON-NLS-1$
					}
				}
			}
			return "?"; //$NON-NLS-1$
		}
		
	}
	
	private class ChangeBereichAdapter extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent ev){
			Button source = (Button) ev.getSource();
			String bereich = source.getText();
			setBereich(bereich);
			/*
			 * if (pinger != null) { pinger.doSync(); }
			 */
			tv.refresh();
		}
		
	}
	
	@Override
	public void setTermin(Termin t){
		Kontakt pat = t.getKontakt();
		StringBuilder sb = new StringBuilder(200);
		TimeSpan ts = t.getTimeSpan();
		sb.append(ts.from.toString(TimeTool.TIME_SMALL))
			.append("-").append(ts.until.toString(TimeTool.TIME_SMALL)) //$NON-NLS-1$
			.append(" ");
		if (t.isRecurringDate()) {
			sb.append(new SerienTermin(t).getRootTermin().getPersonalia());
		} else {
			sb.append(t.getPersonalia());
		}
		sb.append("\n(") //$NON-NLS-1$ //$NON-NLS-2$
			.append(t.getType())
			.append(",").append(t.getStatus()).append(")\n--------\n").append(t.getGrund()); //$NON-NLS-1$ //$NON-NLS-2$
		terminDetail.setText(sb.toString());
		sb.setLength(0);
		sb.append(StringTool.unNull(t.get(Termin.FLD_CREATOR))).append("/").append(
			t.getCreateTime().toString(TimeTool.FULL_GER));
		lbDetails.setText(sb.toString());
		ElexisEventDispatcher.fireSelectionEvent(t);
		if (pat != null) {
			ElexisEventDispatcher.fireSelectionEvent(pat);
		}
	}
	
	private void makePrivateActions(){
		newViewAction = new Action(Messages.AgendaGross_newWindow) {
			@Override
			public void run(){
				try {
					getViewSite().getPage().showView(ID, StringTool.unique("Agenda"), //$NON-NLS-1$
						IWorkbenchPage.VIEW_VISIBLE);
				} catch (PartInitException e) {
					ExHandler.handle(e);
				}
			}
		};
	}
	
	@Override
	public void bereichSelectionEvent(String bereich){
		super.bereichSelectionEvent(bereich);
		for (Button b : bChange) {
			if (!b.isDisposed()) {
				if (b.getText().equalsIgnoreCase(bereich)) {
					b.setSelection(true);
				} else {
					b.setSelection(false);
				}
			}
		}
		
	}
	
	@Override
	public void saveState(IMemento memento){
		int[] w = sash.getWeights();
		memento.putString(CFG_VERTRELATION,
			Integer.toString(w[0]) + StringConstants.COMMA + Integer.toString(w[1]));
		
		super.saveState(memento);
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException{
		if (memento == null) {
			sashWeights = new int[] {
				70, 30
			};
		} else {
			String state = memento.getString(CFG_VERTRELATION);
			if (state == null) {
				state = "70,30"; //$NON-NLS-1$
			}
			String[] sw = state.split(StringConstants.COMMA);
			sashWeights = new int[] {
				Integer.parseInt(sw[0]), Integer.parseInt(sw[1])
			};
		}
		super.init(site, memento);
	}
}
