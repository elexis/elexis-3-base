/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2012
 * 
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.views;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.messwerte.v2.data.ExportData;
import com.tiff.common.ui.datepicker.DatePickerCombo;

public class ExportDialog extends Dialog {
	
	private ExportData expData;
	private Text patNumberFrom;
	private Text patNumberTo;
	private DatePickerCombo dateFrom;
	private DatePickerCombo dateTo;
	private Button btnPatAll;
	private Button btnPatFromTo;
	private Button btnDateAll;
	private Button btnDateFromTo;
	private final Shell parent;
	private int patNrMin = -1;
	private int patNrMax = -1;
	
	public ExportDialog(Shell parent){
		super(parent);
		this.parent = parent;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public ExportDialog(Shell parent, ExportData exportData){
		super(parent);
		this.parent = parent;
		expData = exportData;
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, true));
		
		Label lblPatient = new Label(composite, SWT.NONE);
		lblPatient.setText(Messages.ExportDialog_lblPatient);
		
		Composite compPatient = new Composite(composite, SWT.NONE);
		compPatient.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		btnPatAll = new Button(compPatient, SWT.RADIO);
		btnPatAll.setLayoutData(new RowData(110, SWT.DEFAULT));
		btnPatAll.setText(Messages.ExportDialog_btnPatAll);
		
		btnPatFromTo = new Button(compPatient, SWT.RADIO);
		btnPatFromTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (btnPatFromTo.getSelection()) {
					patNumberFrom.setEnabled(true);
					patNumberTo.setEnabled(true);
					patNumberFrom.setText(Integer.toString(expData.getPatientNumberFrom()));
					patNumberTo.setText(Integer.toString(expData.getPatientNumberTo()));
				} else {
					patNumberFrom.setEnabled(false);
					patNumberTo.setEnabled(false);
					if (patNrMin == -1 || patNrMax == -1) {
						calcMinMaxPatNumbers();
					}
					patNumberFrom.setText(Integer.toString(patNrMin));
					patNumberTo.setText(Integer.toString(patNrMax));
				}
			}
		});
		btnPatFromTo.setLayoutData(new RowData(140, SWT.DEFAULT));
		btnPatFromTo.setText(Messages.ExportDialog_btnPatFromTo);
		btnPatFromTo.setSelection(true);
		// btnPatFromTo.addListener(eventType, new Lis)
		
		patNumberFrom = new Text(compPatient, SWT.BORDER | SWT.RIGHT);
		patNumberFrom.setLayoutData(new RowData(74, SWT.DEFAULT));
		patNumberFrom.setText(Integer.toString(expData.getPatientNumberFrom()));
		
		Label lblPatTo = new Label(compPatient, SWT.CENTER);
		lblPatTo.setLayoutData(new RowData(30, SWT.DEFAULT));
		lblPatTo.setText(Messages.ExportDialog_lblPatTo);
		
		patNumberTo = new Text(compPatient, SWT.BORDER | SWT.RIGHT);
		patNumberTo.setLayoutData(new RowData(74, SWT.DEFAULT));
		patNumberTo.setText(Integer.toString(expData.getPatientNumberTo()));
		
		Label lblDate = new Label(composite, SWT.NONE);
		lblDate.setText(Messages.ExportDialog_lblDate);
		
		Composite compDate = new Composite(composite, SWT.NONE);
		compDate.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		btnDateAll = new Button(compDate, SWT.RADIO);
		btnDateAll.setLayoutData(new RowData(110, SWT.DEFAULT));
		btnDateAll.setSelection(true);
		btnDateAll.setText(Messages.ExportDialog_btnDateAll);
		
		btnDateFromTo = new Button(compDate, SWT.RADIO);
		btnDateFromTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (btnDateFromTo.getSelection()) {
					dateFrom.setEnabled(true);
					dateTo.setEnabled(true);
					dateFrom.setDate(expData.getDateFrom().getTime());
					dateTo.setDate(expData.getDateTo().getTime());
				} else {
					dateFrom.setEnabled(false);
					dateTo.setEnabled(false);
					expData.setDateFrom(new TimeTool(dateFrom.getDate().getTime()));
					expData.setDateTo(new TimeTool(dateTo.getDate().getTime()));
					dateFrom.setDate(null);
					dateTo.setDate(null);
				}
			}
		});
		btnDateFromTo.setLayoutData(new RowData(140, SWT.DEFAULT));
		btnDateFromTo.setText(Messages.ExportDialog_btnDateFromTo);
		btnDateFromTo.setSelection(false);
		
		TimeTool date = new TimeTool();
		expData.setDateFrom(date);
		expData.setDateTo(date);
		
		dateFrom = new DatePickerCombo(compDate, SWT.BORDER);
		dateFrom.setEnabled(false);
		dateFrom.setLayoutData(new RowData(60, SWT.DEFAULT));
		dateFrom.setFormat(new SimpleDateFormat("dd.MM.yyyy")); //$NON-NLS-1$
		
		Label lblDatetTo = new Label(compDate, SWT.CENTER);
		lblDatetTo.setLayoutData(new RowData(30, SWT.DEFAULT));
		lblDatetTo.setText(Messages.ExportDialog_lblDateTo);
		
		dateTo = new DatePickerCombo(compDate, SWT.BORDER);
		dateTo.setEnabled(false);
		dateTo.setLayoutData(new RowData(60, SWT.DEFAULT));
		dateTo.setFormat(new SimpleDateFormat("dd.MM.yyyy")); //$NON-NLS-1$
		
		return composite;
	}
	
	@Override
	protected void okPressed(){
		try {
			if (btnPatFromTo.getSelection()) {
				int from = Integer.parseInt(patNumberFrom.getText());
				int to = Integer.parseInt(patNumberTo.getText());
				if (to < from) {
					throw new Exception(Messages.ExportDialog_Exception_PatNumber);
				}
				expData.setPatientNumberFrom(from);
				expData.setPatientNumberTo(to);
			} else {
				if (patNrMin == -1 || patNrMax == -1) {
					calcMinMaxPatNumbers();
				}
				
				expData.setPatientNumberFrom(patNrMin);
				expData.setPatientNumberTo(patNrMax);
			}
			
			if (btnDateFromTo.getSelection()) {
				Date from = dateFrom.getDate();
				Date to = dateTo.getDate();
				if (to.before(from)) {
					throw new Exception(Messages.ExportDialog_Exception_Datum);
				}
				
				expData.setDateFrom(new TimeTool(from.getTime()));
				expData.setDateTo(new TimeTool(to.getTime()));
				expData.setCheckDate(true);
				
			} else {
				expData.setDateFrom(new TimeTool(TimeTool.BEGINNING_OF_UNIX_EPOCH));
				expData.setDateTo(new TimeTool(TimeTool.END_OF_UNIX_EPOCH));
				expData.setCheckDate(false);
			}
			
			close();
		} catch (Exception e) {
			MessageDialog md =
				new MessageDialog(parent, Messages.ExportDialog_ExceptionDialog, null,
					e.getMessage(), MessageDialog.WARNING, new String[] {
						"Ok" //$NON-NLS-1$
					}, 0);
			md.open();
		}
	}
	
	@Override
	protected void configureShell(Shell newShell){
		super.configureShell(newShell);
		newShell.setText(Messages.ExportDialog_CSV_Export);
	}
	
	private void calcMinMaxPatNumbers(){
		this.getShell().setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT));
		Query<Patient> qpat = new Query<Patient>(Patient.class);
		qpat.add(Patient.FLD_PATID, Query.NOT_EQUAL, ""); //$NON-NLS-1$
		List<Patient> listPatient = qpat.execute();
		if (listPatient != null) {
			int minPatNr = Integer.MAX_VALUE;
			int maxPatNr = 0;
			for (Patient patient : listPatient) {
				int patNr = Integer.parseInt(patient.getPatCode());
				if (patNr < minPatNr) {
					minPatNr = patNr;
				}
				if (patNr > maxPatNr) {
					maxPatNr = patNr;
				}
			}
			patNrMin = minPatNr;
			patNrMax = maxPatNr;
		} else {
			patNrMin = -1;
			patNrMax = -1;
		}
		this.getShell().setCursor(null);
	}
}
