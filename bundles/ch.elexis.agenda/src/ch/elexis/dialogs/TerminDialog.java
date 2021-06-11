/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation, adapted from JavaAgenda
 *    
 *******************************************************************************/

package ch.elexis.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiff.common.ui.datepicker.DatePicker;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.acl.ACLContributor;
import ch.elexis.agenda.data.IPlannable;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.data.Termin.Free;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.agenda.util.TimeInput;
import ch.elexis.agenda.util.TimeInput.TimeInputListener;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.core.ui.util.NumberInput;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

/**
 * Dialog zur Eingabe eines oder mehrerer Termine in die Agenda.
 * 
 * @author gerry
 * 
 */
public class TerminDialog extends TitleAreaDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(TerminDialog.class);
	private static ICommandService cmdService = (ICommandService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ICommandService.class);
	private static Category cmdCategory = cmdService.getCategory("ch.elexis.agenda.commands");
	
	public enum CollisionErrorLevel {
			ERROR, WARNING
	}
	
	DatePicker dp;
	TimeInput tiVon;
	TimeInput tiBis;
	int ts = ConfigServiceHolder.getUser("agenda/dayView/Start", 7); //$NON-NLS-1$
	int te = ConfigServiceHolder.getUser("agenda/dayView/End", 19); //$NON-NLS-1$
	int tagStart = ts * 60; // 7 Uhr
	int tagEnd = te * 60;
	int[] rasterValues = new int[] {
		5, 10, 15, 30
	};
	int rasterIndex = ConfigServiceHolder.getUser("agenda/dayView/raster", 3); //$NON-NLS-1$
	Hashtable<String, String> tMap;
	double minutes;
	double pixelPerMinute;
	
	NumberInput niDauer;
	ArrayList<Termin> lTermine;
	List lTerminListe;
	// Button bPrev,bNext;
	Button bLocked, bSerie;
	Button bSave, bDelete, bChange, bPrint, bFuture, bEmergency;
	Slider slider;
	DayOverview dayBar;
	
	Kontakt actKontakt;
	IPlannable actPlannable;
	// TagesView base;
	// String[] bereiche;
	Text tNr, tName, tBem;
	Combo cbTyp, cbStatus, cbMandant;
	Text tGrund;
	Activator agenda = Activator.getDefault();
	boolean bModified;
	private String msg;
	
	private CollisionErrorLevel collisionErrorLevel = CollisionErrorLevel.ERROR;
	
	public TerminDialog(IPlannable act){
		super(UiDesk.getTopShell());
		// base=parent;
		if (act == null) {
			act = new Termin.Free(agenda.getActDate().toString(TimeTool.DATE_COMPACT), 0, 30);
		}
		if (act instanceof Termin) {
			Termin lact = (Termin) act;
			actKontakt = lact.getKontakt();
		} else {
			actKontakt = ElexisEventDispatcher.getSelectedPatient();
		}
		Color green = UiDesk.getColor(UiDesk.COL_GREEN);
		if (green == null) {
			UiDesk.getColorRegistry().put(UiDesk.COL_GREEN, new RGB(0, 255, 0));
		}
		actPlannable = act;
		tMap = Plannables.getTimePrefFor(agenda.getActResource());
		tMap.put(Termin.typFrei(), "0"); //$NON-NLS-1$
		tMap.put(Termin.typReserviert(), "0"); //$NON-NLS-1$
	}
	
	/**
	 * Use this constructor for creating a new {@link Termin} from a date and for a resource.
	 * 
	 * @param date
	 * @param resource
	 * @param patient
	 */
	public TerminDialog(@NonNull TimeTool date, @NonNull String resource, Patient patient){
		super(UiDesk.getTopShell());
		Free act = new Termin.Free(date.toString(TimeTool.DATE_COMPACT),
			date.get(TimeTool.HOUR_OF_DAY) * 60 + date.get(TimeTool.MINUTE), 30);
		actKontakt = patient;
		Color green = UiDesk.getColor(UiDesk.COL_GREEN);
		if (green == null) {
			UiDesk.getColorRegistry().put(UiDesk.COL_GREEN, new RGB(0, 255, 0));
		}
		actPlannable = act;
		agenda.setActResource(resource);
		tMap = Plannables.getTimePrefFor(agenda.getActResource());
		tMap.put(Termin.typFrei(), "0"); //$NON-NLS-1$
		tMap.put(Termin.typReserviert(), "0"); //$NON-NLS-1$
	}
	
	public static void setActResource(String resource){
		Activator.getDefault().setActResource(resource);
	}
	
	@Override
	protected Control createDialogArea(final Composite parent){
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite ret = new Composite(sc, SWT.NONE);
		sc.setContent(ret);
		// ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout());
		Composite topRow = new Composite(ret, SWT.BORDER);
		topRow.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		topRow.setLayout(new GridLayout(3, true));
		// oben links
		dp = new DatePicker(topRow, SWT.NONE);
		dp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		dp.setDate(agenda.getActDate().getTime());
		// actDate.setTime(dp.getDate());
		dp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				// actDate.setTime(dp.getDate());
				agenda.setActDate(new TimeTool(dp.getDate().getTime()));
				dayBar.recalc();
				dayBar.redraw();
				slider.set();
			}
			
		});
		// oben mitte
		Composite topCenter = new Composite(topRow, SWT.NONE);
		topCenter.setLayout(new GridLayout(3, true));
		topCenter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		tiVon = new TimeInput(topCenter, Messages.TerminDialog_startTime);
		tiVon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		tiVon.addListener(new TimeInputListener() {
			public void changed(){
				slider.set();
			}
			
		});
		niDauer = new NumberInput(topCenter, Messages.TerminDialog_duration);
		niDauer.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		niDauer.getControl().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				slider.set();
			}
		});
		niDauer.setMinMaxInc(1, 1440, 5);
		tiBis = new TimeInput(topCenter, Messages.TerminDialog_endTime);
		tiBis.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		tiBis.addListener(new TimeInputListener() {
			public void changed(){
				int mVon = tiVon.getTimeAsMinutes();
				int mBis = tiBis.getTimeAsMinutes();
				niDauer.setValue(mBis - mVon);
				slider.set();
			}
		});
		lTermine = new ArrayList<Termin>();
		lTerminListe = new List(topCenter, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		lTerminListe.setLayoutData(SWTHelper.getFillGridData(3, true, 1, true));
		lTerminListe.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(final SelectionEvent e){
				int idx = lTerminListe.getSelectionIndex();
				if ((idx > -1) && (idx < lTermine.size())) {
					actPlannable = lTermine.get(idx);
					setAll();
				}
			}
			
		});
		
		// oben rechts
		Composite topRight = new Composite(topRow, SWT.NONE);
		topRight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		topRight.setLayout(new GridLayout(2, true));
		Label sep = new Label(topRight, SWT.NONE);
		sep.setText(" "); //$NON-NLS-1$
		sep.setLayoutData(SWTHelper.getFillGridData(2, false, 1, false));
		/*
		 * bPrev=new Button(topRight,SWT.PUSH); bPrev.setText("<--"); //$NON-NLS-1$
		 * bPrev.setToolTipText(Messages.TerminDialog_earlier); bNext=new Button(topRight,SWT.PUSH);
		 * bNext.setText("-->"); //$NON-NLS-1$ bNext.setToolTipText(Messages.TerminDialog_later);
		 */
		bLocked = new Button(topRight, SWT.CHECK);
		bLocked.setText(Messages.TerminDialog_locked);
		bLocked.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				if (actPlannable instanceof Termin) {
					((Termin) actPlannable).setLocked(bLocked.getSelection());
				}
				setEnablement();
			}
		});
		if (CoreHub.acl.request(ACLContributor.CHANGE_APPLOCK) == false) {
			bLocked.setEnabled(false);
		}
		bSerie = new Button(topRight, SWT.CHECK);
		bSerie.setText(Messages.TerminDialog_serie);
		bSave = new Button(topRight, SWT.PUSH);
		bSave.setText(Messages.TerminDialog_set);
		bSave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		bSave.setToolTipText(Messages.TerminDialog_createTermin);
		bSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				createTermin(true);
			}
		});
		Point s = bSave.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		bChange = new Button(topRight, SWT.PUSH);
		bChange.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		bChange.setText(Messages.TerminDialog_change);
		bChange.setToolTipText(Messages.TerminDialog_changeTermin);
		bDelete = new Button(topRight, SWT.PUSH);
		bDelete.setText(Messages.TerminDialog_delete);
		bDelete.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		bDelete.setToolTipText(Messages.TerminDialog_deleteTermin);
		bDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				if (actPlannable instanceof Termin) {
					lTerminListe.remove(lTerminListe.getSelectionIndex());
					lTermine.remove(actPlannable);
					((Termin) actPlannable).delete();
					ElexisEventDispatcher.reload(Termin.class);
					dayBar.recalc();
					dayBar.redraw();
					setEnablement();
				}
				super.widgetSelected(e);
			}
			
		});
		Button bSearch = new Button(topRight, SWT.PUSH);
		bSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bSearch.setText(Messages.TerminDialog_find);
		
		bSearch.setToolTipText(Messages.TerminDialog_findTermin);
		bSearch.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(final SelectionEvent e){
				if (actKontakt != null) {
					Query<Termin> qbe = new Query<Termin>(Termin.class);
					qbe.add("Wer", "=", actKontakt.getId());
					qbe.add("deleted", "<>", "1");
					if (bFuture.getSelection() == false) {
						qbe.add("Tag", ">", new TimeTool().toString(TimeTool.DATE_COMPACT));
					}
					java.util.List<Termin> list = qbe.execute();
					// if past events are included, list future events first (sort desc)
					// if past events not included, list nearest future event first (sort asc)
					if (bFuture.getSelection()) {
						list.sort(new Comparator<Termin>() {
							@Override
							public int compare(Termin t1, Termin t2){
								return t2.getStartTime().compareTo(t1.getStartTime());
							}
						});
					} else {
						list.sort(new Comparator<Termin>() {
							@Override
							public int compare(Termin t1, Termin t2){
								return t1.getStartTime().compareTo(t2.getStartTime());
							}
						});
					}
					lTermine.clear();
					lTerminListe.removeAll();
					if ((list != null) && (list.size() > 0)) {
						for (Termin t : list) {
							lTermine.add(t);
							String label = t.getLabel();
							logger.info(label);
							lTerminListe.add(label);
						}
						lTerminListe.select(0);
					}
				}
			}
			
		});
		bPrint = new Button(topRight, SWT.PUSH);
		bPrint.setText(Messages.TerminDialog_print);
		bPrint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		bPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				Command cmd = cmdService.getCommand("ch.elexis.agenda.commands.printAppointmentLabel");
				if (!cmd.isDefined()) {
					cmd.define("Print Appointment", "Print Appointment Command", cmdCategory);
				}

				try {
					ExecutionEvent ee = new ExecutionEvent(cmd,
						Collections.singletonMap("ch.elexis.agenda.param.appointmentids",
							lTermine.stream().map(t -> t.getId()).collect(Collectors.joining(","))),
						null, null);
					cmd.executeWithChecks(ee);
				} catch (Exception ex) {
					ExHandler.handle(ex);
					logger.error("Failed to execute command ch.elexis.agenda.commands.printAppointmentLabel", ex);
				}
			}
			
		});
		bFuture = new Button(topRight, SWT.CHECK);
		bFuture.setText(Messages.TerminDialog_past);
		
		bEmergency = new Button(topRight, SWT.CHECK);
		bEmergency.setText(Messages.TerminDialog_emergency);
		
		// Balken
		Composite cBar = new Composite(ret, SWT.BORDER);
		cBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		cBar.setLayout(new GridLayout());
		dayBar = new DayOverview(cBar);
		dayBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		slider = new Slider(dayBar);
		
		// unten
		Composite cBottom = new Composite(ret, SWT.BORDER);
		cBottom.setLayout(new GridLayout(3, false));
		cBottom.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		// Zeile 1
		new Label(cBottom, SWT.NONE).setText("PatientID"); //$NON-NLS-1$
		Composite cLinks = new Composite(cBottom, SWT.NONE);
		cLinks.setLayout(new RowLayout(SWT.HORIZONTAL));
		new Label(cLinks, SWT.NONE).setText("Personalien ");
		
		Hyperlink hlText = new Hyperlink(cLinks, SWT.NONE);
		hlText.setText(Messages.TerminDialog_enterPersonalia);
		hlText.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent e){
				InputDialog inp = new InputDialog(getShell(), Messages.TerminDialog_enterText,
					Messages.TerminDialog_enterFreeText, "", null); //$NON-NLS-1$
				if (inp.open() == Dialog.OK) {
					tName.setText(inp.getValue());
					tNr.setText(""); //$NON-NLS-1$
					actKontakt = null;
				}
			}
		});
		hlText.setForeground(UiDesk.getColor(UiDesk.COL_BLUE));
		// new Label(cLinks,SWT.SEPARATOR|SWT.VERTICAL);
		Hyperlink hlSelect = new Hyperlink(cLinks, SWT.NONE);
		hlSelect.setForeground(UiDesk.getColor(UiDesk.COL_BLUE));
		hlSelect.setText("(Kontakt auswählen)");
		hlSelect.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent e){
				KontaktSelektor ksl =
					new KontaktSelektor(getShell(), Kontakt.class, "Termin zuordnen",
						"Bitte suchen Sie aus, wer den Termin hat", Kontakt.DEFAULT_SORT);
				if (ksl.open() == Dialog.OK) {
					actKontakt = (Kontakt) ksl.getSelection();
					if (actKontakt != null) {
						String phoneEnhancedLabel = getPhoneEnhancedLabel(actKontakt);
						tName.setText(phoneEnhancedLabel);
						tNr.setText(actKontakt.get(Kontakt.FLD_SHORT_LABEL));
					}
					setEnablement();
				}
			}
		});
		
		new Label(cBottom, SWT.NONE).setText(Messages.TerminDialog_Mandator);
		// Zeile 2
		tNr = new Text(cBottom, SWT.BORDER | SWT.READ_ONLY);
		tName = new Text(cBottom, SWT.BORDER | SWT.READ_ONLY);
		tName.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cbMandant = new Combo(cBottom, SWT.SINGLE);
		cbMandant.setItems(agenda.getResources());
		cbMandant.setText(agenda.getActResource());
		cbMandant.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e){
				setAll();
			}
			
		});
		// Zeile 3
		Label lBem = new Label(cBottom, SWT.NONE);
		lBem.setText(Messages.TerminDialog_remarks);
		lBem.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
		// Zeile 4
		tBem = new Text(cBottom, SWT.BORDER | SWT.READ_ONLY);
		tBem.setLayoutData(SWTHelper.getFillGridData(3, true, 1, true));
		new Label(cBottom, SWT.NONE).setText(Messages.TerminDialog_typeandstate);
		Label lGrund = new Label(cBottom, SWT.NONE);
		lGrund.setText(Messages.TerminDialog_reason);
		lGrund.setLayoutData(SWTHelper.getFillGridData(2, false, 1, false));
		cbTyp = new Combo(cBottom, SWT.SINGLE | SWT.READ_ONLY);
		setItemTypes();
		Point pTyp = cbTyp.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		tGrund = new Text(cBottom, SWT.BORDER | SWT.MULTI);
		tGrund.setLayoutData(SWTHelper.getFillGridData(2, true, 2, true));
		cbStatus = new Combo(cBottom, SWT.SINGLE | SWT.READ_ONLY);
		cbStatus.setItems(Termin.TerminStatus);
		Point pStatus = cbStatus.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int xMax = Math.max(pTyp.x, pStatus.x);
		cbTyp.setLayoutData(new GridData(xMax, SWT.DEFAULT));
		cbStatus.setLayoutData(new GridData(xMax, SWT.DEFAULT));
		StatusTypListener statusTypListener = new StatusTypListener();
		cbTyp.addSelectionListener(statusTypListener);
		cbStatus.addSelectionListener(statusTypListener);
		
		String val = tMap.get(Termin.TerminTypes[1]);
		if (val == null) {
			val = tMap.get(Messages.TerminDialog_32);
		}
		niDauer.setValue(Integer.parseInt(val));
		bSerie.setEnabled(false);
		// bSearch.setEnabled(false);
		// bNext.setEnabled(false);
		// bPrev.setEnabled(false);
		ret.setSize(ret.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return sc;
	}
	
	/**
	 * mögliche Termintypen setzen (nur die, die eine Dauer!=0 haben)
	 * 
	 */
	private void setItemTypes(){
		cbTyp.removeAll();
		for (String t : Termin.TerminTypes) {
			String ts = tMap.get(t);
			if ((ts != null) && (ts.equals("0"))) { //$NON-NLS-1$
				continue;
			}
			cbTyp.add(t);
		}
	}
	
	/**
	 * Status- oder Typänderungen setzen
	 * 
	 * @author gerry
	 * 
	 */
	class StatusTypListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent e){
			if (e.getSource().equals(cbTyp)) {
				String type = cbTyp.getItem(cbTyp.getSelectionIndex());
				if (actPlannable instanceof Termin) {
					((Termin) actPlannable).setType(type);
					bModified = true;
				} else if (actPlannable instanceof Termin.Free) {
					Hashtable<String, String> map =
						Plannables.getTimePrefFor(agenda.getActResource());
					String nt = map.get(type);
					if (nt == null) {
						nt = map.get("std"); //$NON-NLS-1$
						if (nt == null) {
							nt = "10"; //$NON-NLS-1$
						}
					}
					int en = Integer.parseInt(nt);
					niDauer.setValue(en);
					slider.set();
				}
			} else if (e.getSource().equals(cbStatus)) {
				if (actPlannable instanceof Termin) {
					((Termin) actPlannable)
						.setStatus(cbStatus.getItem(cbStatus.getSelectionIndex()));
					bModified = true;
				}
			}
			setEnablement();
		}
		
	}
	
	@Override
	public void create(){
		setShellStyle(getShellStyle() | SWT.RESIZE);
		super.create();
		
		setMessage(Messages.TerminDialog_editTermins);
		setTitleImage(Images.IMG_LOGO.getImage());
		getShell().setText(Messages.TerminDialog_termin);
		dayBar.recalc();
		dayBar.redraw();
		if (actPlannable instanceof Termin) {
			lTerminListe.add(((Termin) actPlannable).getLabel());
			lTermine.add((Termin) actPlannable);
			lTerminListe.select(0);
		}
		if (actKontakt == null) {
			setTitle(Messages.TerminDialog_noPatSelected);
			tNr.setText(""); //$NON-NLS-1$
			if (actPlannable instanceof Termin) {
				tName.setText(((Termin) actPlannable).getPersonalia());
			} else {
				tName.setText(""); //$NON-NLS-1$
			}
			tBem.setText(""); //$NON-NLS-1$
		} else {
			setTitle(actKontakt.getLabel());
			tNr.setText(Kontakt.FLD_SHORT_LABEL);
			String phoneEnhancedLabel = getPhoneEnhancedLabel(actKontakt);
			tName.setText(phoneEnhancedLabel);
			tBem.setText(actKontakt.getBemerkung());
		}
		
		setAll();
	}
	
	private void setAll(){
		tiVon.setTimeInMinutes(actPlannable.getStartMinute());
		dp.setDate(new TimeTool(actPlannable.getDay()).getTime());
		// actDate.setTime(dp.getDate());
		agenda.setActDate(new TimeTool(dp.getDate().getTime()));
		agenda.setActResource(cbMandant.getText());
		// actBereich = cbMandant.getText();
		
		if (actPlannable instanceof Termin.Free) {
			setCombo(cbTyp, Termin.typStandard(), 0);
			setCombo(cbStatus, Termin.statusStandard(), 0);
			bChange.setEnabled(false);
			String dauer = tMap.get(Termin.typStandard());
			if (dauer == null) {
				dauer = tMap.get(Messages.TerminDialog_40);
			}
			if (dauer == null) {
				dauer = "15"; //$NON-NLS-1$
			}
			niDauer.setValue(Integer.parseInt(dauer));
		} else {
			
			Termin actTermin = (Termin) actPlannable;
			tGrund.setText(actTermin.getGrund());
			setCombo(cbTyp, actTermin.getType(), 0);
			setCombo(cbStatus, actTermin.getStatus(), 0);
			bLocked.setSelection(actTermin.getFlag(Termin.SW_LOCKED));
			niDauer.getControl().setSelection(actPlannable.getDurationInMinutes());
			bEmergency.setSelection(StringUtils.equals(actTermin.get(Termin.FLD_PRIORITY), "1"));
		}
		dayBar.recalc();
		dayBar.redraw();

		slider.set();
	}
	
	public void setCollisionErrorLevel(CollisionErrorLevel level){
		this.collisionErrorLevel = level;
	}
	
	private void enable(final boolean mode){
		msg = Messages.TerminDialog_editTermins;
		if (collisionErrorLevel == CollisionErrorLevel.ERROR) {
			bChange.setEnabled(mode);
			bSave.setEnabled(mode);
			getButton(IDialogConstants.OK_ID).setEnabled(mode);
		}
		slider.setBackground(UiDesk.getColor(UiDesk.COL_LIGHTGREY)); //$NON-NLS-1$
		
		if (!mode) {
			slider.setBackground(UiDesk.getColor(UiDesk.COL_DARKGREY)); //$NON-NLS-1$
			msg = Messages.TerminDialog_editTermins + "\n\t" + Messages.TerminDialog_collision;
		}
		
		getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run(){
				if (collisionErrorLevel == CollisionErrorLevel.ERROR) {
					setMessage(msg, mode ? IMessageProvider.NONE : IMessageProvider.ERROR);
				} else if (collisionErrorLevel == CollisionErrorLevel.WARNING) {
					setMessage(msg, mode ? IMessageProvider.NONE : IMessageProvider.WARNING);
				}
			}
		});
	}
	
	private void setEnablement(){
		TimeSpan ts = new TimeSpan(tiVon.setTimeTool(agenda.getActDate()), niDauer.getValue());
		if (actPlannable instanceof Termin.Free) {
			if (Plannables.collides(ts, dayBar.list, null)
				|| terminOverlaps(agenda.getActDate(), ts)) {
				enable(false);
			} else {
				enable(true);
			}
			// bLocked.setEnabled(false);
		} else {
			if (Plannables.collides(ts, dayBar.list, (Termin) actPlannable)) {
				enable(false);
			} else {
				if (bModified) {
					if (actKontakt != null && terminOverlaps(agenda.getActDate(), ts)) {
						enable(false);
						return;
					}
					enable(true);
				} else {
					enable(false);
				}
			}
			// bLocked.setEnabled(true);
		}
		
	}
	
	private void setCombo(final Combo combo, final String value, final int def){
		String[] elems = combo.getItems();
		int idx = StringTool.getIndex(elems, value);
		if (idx == -1) {
			idx = def;
		}
		combo.select(idx);
	}
	
	/**
	 * Klasse für die übersichtliche Darstellung eines Tages-Balkens
	 * 
	 * @author Gerry
	 * 
	 */
	class DayOverview extends Composite implements PaintListener {
		
		Point d;
		int sep;
		
		java.util.List<IPlannable> list;
		
		DayOverview(final Composite parent){
			super(parent, SWT.NONE);
			this.setData("org.eclipse.e4.ui.css.CssClassName", "CustomComposite");
			addPaintListener(this);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(final MouseEvent e){
					if (e.y > sep + 2) {
						rasterIndex = (rasterIndex >= rasterValues.length) ? 0 : rasterIndex + 1;
						ConfigServiceHolder.setUser("agenda/dayView/raster", rasterIndex); //$NON-NLS-1$
						redraw();
					} else {
						// super.mouseDown(e);
						Point loc = slider.getLocation();
						slider.setLocation(e.x, loc.y);
						slider.updateTimes();
					}
				}
				
			});
		}
		
		/**
		 * Tagesbalken neu kalkulieren (Startzeit, Endzeit, pixel pro Minute etc.)
		 * 
		 */
		void recalc(){
			list = Plannables.loadTermine(agenda.getActResource(), agenda.getActDate());
			
			tagStart = ts * 60;
			tagEnd = te * 60;
			int i = 0;
			IPlannable pi = list.get(i);
			// Tagesanzeige ca. eine halbe Stunde vor dem ersten reservierten
			// Zeitraum anfangen
			if (pi.getType().equals(Termin.typReserviert())) {
				tagStart = pi.getDurationInMinutes() < 31 ? 0 : pi.getDurationInMinutes() - 30;
			} else {
				tagStart = 0;
			}
			i = list.size() - 1;
			pi = list.get(i);
			int end = pi.getStartMinute() + pi.getDurationInMinutes();
			if (end < 1408) {
				tagEnd = 1439;
			}
			if (tagStart != 0) {
				tagStart = tagStart / 60 * 60;
			}
			if (tagEnd < 1439) {
				tagEnd = (tagEnd + 30) / 60 * 60;
			}
			minutes = tagEnd - tagStart;
			d = this.getSize();
			pixelPerMinute = d.x / minutes;
			sep = d.y / 2;
			/*
			 * double minutes=(tagEnd-tagStart); minutewidth=d.x/minutes;
			 */
		}
		
		@Override
		public Point computeSize(final int wHint, final int hHint, final boolean changed){
			return new Point(getParent().getSize().x, 40);
		}

		/**
		 * Tagesbalken zeichnen
		 */
		public void paintControl(final PaintEvent pe){
			GC g = pe.gc;
			Color def = g.getBackground();
			// Balken zeichnen
			g.setBackground(UiDesk.getColor(UiDesk.COL_GREEN));
			Rectangle r = new Rectangle(0, 0, d.x, sep - 2);
			g.fillRectangle(r);
			
			// Termine darauf zeichnen
			for (IPlannable p : list) {
				Plannables.paint(g, p, r, tagStart, tagEnd);
			}
			
			// Lineal zeichnen
			g.setBackground(def);
			g.setFont(UiDesk.getFont(Preferences.USR_SMALLFONT));
			
			g.drawLine(0, sep, d.x, sep);
			if (rasterIndex >= rasterValues.length) {
				rasterIndex = 0;
				ConfigServiceHolder.setUser("agenda/dayView/raster", rasterIndex); //$NON-NLS-1$
			}
			double chunkwidth = rasterValues[rasterIndex] * pixelPerMinute;
			int chunksPerHour = 60 / rasterValues[rasterIndex];
			int ch = chunksPerHour - 1;
			int hr = tagStart / 60;
			if (chunkwidth < 0.1) {
				return;
			}
			for (double x = 0; x <= d.x; x += chunkwidth) {
				int lx = (int) Math.round(x);
				if (++ch == chunksPerHour) {
					g.drawLine(lx, sep - 1, lx, sep + 6);
					g.drawString(Integer.toString(hr++), lx, sep + 6);
					ch = 0;
				} else {
					g.drawLine(lx, sep, lx, sep + 4);
				}
			}
			
			slider.redraw();
		}
	}
	
	private class Slider extends Composite implements MouseListener, MouseMoveListener {
		boolean isDragging;
		
		Slider(final Composite parent){
			super(parent, SWT.BORDER);
			setBackground(UiDesk.getColor(UiDesk.COL_RED)); //$NON-NLS-1$
			addMouseListener(this);
			addMouseMoveListener(this);
		}
		
		void set(){
			int v = tiVon.getTimeAsMinutes();
			int d = niDauer.getValue();
			Rectangle r = getParent().getBounds();
			int x = (int) Math.round((v - tagStart) * pixelPerMinute);
			int w = (int) Math.round(d * pixelPerMinute);
			setBounds(x, 0, w, r.height / 2);
			tiBis.setTimeInMinutes(v + d);
			bModified = true;
			setEnablement();
		}
		
		public void mouseDoubleClick(final MouseEvent e){}
		
		public void mouseDown(final MouseEvent e){
			isDragging = true;
		}
		
		public void mouseUp(final MouseEvent e){
			if (isDragging) {
				isDragging = false;
				updateTimes();
			}
		}
		
		public void mouseMove(final MouseEvent e){
			if (isDragging) {
				Point loc = getLocation();
				int x = loc.x + e.x;
				setLocation(x, loc.y);
			}
			
		}
		
		public void updateTimes(){
			Point loc = getLocation();
			Rectangle rec = getParent().getBounds();
			double minutes = tagEnd - tagStart;
			double minutesPerPixel = minutes / rec.width;
			int minute = (int) Math.round(loc.x * minutesPerPixel) + tagStart;
			int raster = rasterValues[rasterIndex];
			minute = ((minute + (raster >> 1)) / raster) * raster;
			tiVon.setTimeInMinutes(minute);
			set();
		}
		
	}
	
	public void setTime(TimeTool time){
		tiVon.setText(time.toString(TimeTool.TIME_SMALL));
		slider.set();
	}
	
	@Override
	protected void okPressed(){
		createTermin(false);
		super.okPressed();
	}
	
	private void createTermin(final boolean bMulti){
		int von = tiVon.getTimeAsMinutes();
		int bis = von + niDauer.getValue();
		String typ = cbTyp.getItem(cbTyp.getSelectionIndex());
		String status = cbStatus.getItem(cbStatus.getSelectionIndex());
		String priority = bEmergency.getSelection() ? "1" : "0";
		Termin actTermin = null;
		if (actPlannable instanceof Termin.Free) {
			Termin newTermin = new Termin(agenda.getActResource(),
				agenda.getActDate().toString(TimeTool.DATE_COMPACT), von, bis, typ, status,
				priority);
			actTermin = newTermin;
		} else {
			actTermin = (Termin) actPlannable;
			if (bMulti) {
				Termin clonedTermin = (Termin) actTermin.clone();
				AcquireLockBlockingUi.aquireAndRun(clonedTermin, new ILockHandler() {
					@Override
					public void lockFailed(){
						clonedTermin.delete();
					}
					
					@Override
					public void lockAcquired(){
						// do nothing
						
					}
				});
				actTermin = clonedTermin;
			}
			
			actTermin.set(new String[] {
				"BeiWem", "Tag", "Beginn", "Dauer", "Typ", "Status", Termin.FLD_PRIORITY
			}, new String[] {
				agenda.getActResource(), agenda.getActDate().toString(TimeTool.DATE_COMPACT),
				Integer.toString(von), Integer.toString(bis - von), typ, status, priority
			});
		}
		lTerminListe.add(actTermin.getLabel());
		lTermine.add(actTermin);
		
		if (actKontakt != null) {
			actTermin.setKontakt(actKontakt);
		} else {
			actTermin.set("Wer", tName.getText()); //$NON-NLS-1$
		}
		actTermin.setGrund(tGrund.getText());
		actTermin.set(Termin.FLD_CREATOR,
			ContextServiceHolder.get().getActiveUser().get().getLabel());
		
		if (bLocked.getSelection()) {
			actTermin.setFlag(Termin.SW_LOCKED);
		}
		
		ElexisEventDispatcher.reload(Termin.class);
		
		dayBar.recalc();
		dayBar.redraw();
		actPlannable = actTermin;
		setEnablement();
	}
	
	/**
	 * checks if the given appointment overlaps with any other appointment of this patient
	 * 
	 * @param actDate
	 *            date of the appointment
	 * @param terminTS
	 *            timespan of the appointment
	 * @return true if there is a collision
	 */
	private boolean terminOverlaps(TimeTool actDate, TimeSpan terminTS){
		boolean avoidPatientDoubleBooking =
			CoreHub.localCfg.get(PreferenceConstants.AG_AVOID_PATIENT_DOUBLE_BOOKING,
				PreferenceConstants.AG_AVOID_PATIENT_DOUBLE_BOOKING_DEFAULT);
		
		if (avoidPatientDoubleBooking && actKontakt != null) {
			Query<Termin> tQuery = new Query<Termin>(Termin.class);
			tQuery.add(Termin.FLD_PATIENT, Query.EQUALS, actKontakt.getId());
			tQuery.add(Termin.FLD_TAG, Query.EQUALS, actDate.toString(TimeTool.DATE_COMPACT));
			java.util.List<Termin> termine = tQuery.execute();
			
			if (actPlannable instanceof Termin.Free) {
				for (Termin t : termine) {
					TimeSpan ts = t.getTimeSpan();
					if (ts.overlap(terminTS) != null) {
						return true;
					}
				}
			} else {
				Termin curTermin = (Termin) actPlannable;
				for (Termin t : termine) {
					// only compare if it's not the currently open appointment
					if (!t.getId().equals(curTermin.getId())) {
						TimeSpan ts = t.getTimeSpan();
						if (ts.overlap(terminTS) != null) {
							return true;
						}
					}
				}
			}
			return false;
		}
		return false;
	}
	
	private String getPhoneEnhancedLabel(Kontakt actKontakt){
		String telephoneLabel = actKontakt.getTelephoneLabel();
		String label = actKontakt.getLabel()
			+ ((telephoneLabel.length() > 0) ? " (" + telephoneLabel + ")" : "");
		return label;
	}
}
