/*******************************************************************************
 * Copyright (c) 2018 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht - novcom AG
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin.ui.preferences;


import java.util.List;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Query;
import ch.novcom.elexis.mednet.plugin.data.DocumentSettingRecord;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;
import ch.novcom.elexis.mednet.plugin.ui.commands.DocumentSettingRecordCreate;
import ch.novcom.elexis.mednet.plugin.ui.commands.DocumentSettingRecordEdit;


/**
 * Configuration for the Document part of MedNet
 */
public class DocumentPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private TableViewer tableViewer;
	private Table table;
	
	private String[] tableheaders = {
			MedNetMessages.DocumentPreferences_institutionName,
			MedNetMessages.DocumentPreferences_category,
			MedNetMessages.DocumentPreferences_path,
			MedNetMessages.DocumentPreferences_errorPath ,
			MedNetMessages.DocumentPreferences_archivingPath, 
			MedNetMessages.DocumentPreferences_archivingPurgeInterval
		};

	private int[] tableColwidth = {
		14, 14, 20, 20, 20, 4
	};
	/**
	 * Standard Constructor
	 */
	public DocumentPreferencePage(){
		super(MedNetMessages.DocumentPreferences_title);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench){
		// Nothing to initialize
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createContents(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	protected Control createContents(Composite parent){

		noDefaultAndApplyButton();
		
		Composite tableComposite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData();
		tableComposite.setLayoutData(gd);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		
		for (int i = 0; i < tableheaders.length; i++) {
			TableColumn tc = new TableColumn(table, SWT.LEFT);
			tc.setText(tableheaders[i]);
			tableColumnLayout.setColumnData(tc, new ColumnWeightData(tableColwidth[i], true));
			tc.setData(i);
			tc.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					tableViewer.refresh(true);
				}
			});
		}

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			
			public Object[] getElements(Object inputElement){
				return DocumentSettingRecord.getAllDocumentSettingRecords().toArray();
			}
			
			public void dispose(){}
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput){}
			
		});
		tableViewer.setLabelProvider(new ReceivingListLabelProvider());
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event){
				IStructuredSelection sel = (IStructuredSelection) tableViewer.getSelection();
				Object o = sel.getFirstElement();
				if (o instanceof DocumentSettingRecord) {
					DocumentSettingRecord li = (DocumentSettingRecord) o;
					DocumentSettingRecordEdit.executeWithParams(li);
					tableViewer.refresh();
				}
			}
			
		});
		
		tableViewer.setInput(this);
		return tableComposite;
	}

	static class ReceivingListLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {
		
		public String getColumnText(Object element, int columnIndex){
			DocumentSettingRecord documentSettingRecord = (DocumentSettingRecord) element;
			
			switch (columnIndex) {
			case 0:
				return documentSettingRecord.getInstitutionName();
			case 1:
				return documentSettingRecord.getCategory();
			case 2:
				return documentSettingRecord.getPath().toString();
			case 3:
				return documentSettingRecord.getErrorPath().toString();
			case 4:
				return documentSettingRecord.getArchivingPath().toString();
			case 5:
				return String.valueOf(documentSettingRecord.getPurgeInterval());
			default:
				return "?col?"; //$NON-NLS-1$
			}
		}
		
		@Override
		public Image getColumnImage(Object element, int columnIndex){
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	

	@Override
	protected void contributeButtons(Composite parent){
		
		((GridLayout) parent.getLayout()).numColumns++;
		Button bNewItem = new Button(parent, SWT.PUSH);
		bNewItem.setText(MedNetMessages.DocumentPreferences_new);
		bNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				try {
					// execute the command
					IHandlerService handlerService =
						(IHandlerService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getService(IHandlerService.class);
					
					handlerService.executeCommand(DocumentSettingRecordCreate.COMMANDID, null);
				} catch (Exception ex) {
					throw new RuntimeException(DocumentSettingRecordCreate.COMMANDID, ex);
				}
				tableViewer.refresh();
			}
		});
		((GridLayout) parent.getLayout()).numColumns++;
		Button bDelItem = new Button(parent, SWT.PUSH);
		bDelItem.setText(MedNetMessages.DocumentPreferences_delete);
		bDelItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				IStructuredSelection sel = (IStructuredSelection) tableViewer.getSelection();
				Object o = sel.getFirstElement();
				if (o instanceof DocumentSettingRecord) {
					DocumentSettingRecord li = (DocumentSettingRecord) o;
					if (MessageDialog.openQuestion(getShell(), MedNetMessages.DocumentPreferences_delete,
						MessageFormat.format(MedNetMessages.DocumentPreferences_reallyDelete,
							li.getLabel()))) {
						
						if (deleteRecord(li)) {
							li.delete();
							tableViewer.remove(li);
						} else {
							MessageDialog.openWarning(getShell(), MedNetMessages.DocumentPreferences_delete,
									MedNetMessages.DocumentPreferences_deleteFailed);
						}
					}
				}
			}
		});
		((GridLayout) parent.getLayout()).numColumns++;
		Button bDelAllItems = new Button(parent, SWT.PUSH);
		bDelAllItems.setText(MedNetMessages.DocumentPreferences_deleteAll);
		bDelAllItems.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				if (SWTHelper.askYesNo(MedNetMessages.DocumentPreferences_reallyDeleteAll,
						MedNetMessages.DocumentPreferences_deleteAllExplain)) {
					Query<DocumentSettingRecord> qbli = new Query<DocumentSettingRecord>(DocumentSettingRecord.class);
					List<DocumentSettingRecord> items = qbli.execute();
					boolean success = true;
					for (DocumentSettingRecord li : items) {
						if (deleteRecord(li)) {
							li.delete();
						} else {
							success = false;
						}
					}
					if (!success) {
						MessageDialog.openWarning(getShell(), MedNetMessages.DocumentPreferences_deleteAll,
								MedNetMessages.DocumentPreferences_deleteAllFailed);
					}
					tableViewer.refresh();
				}
			}
		});
		if (CoreHub.acl.request(AccessControlDefaults.DELETE_LABITEMS) == false) {
			bDelAllItems.setEnabled(false);
		}
	}
	
	private boolean deleteRecord(DocumentSettingRecord li){
		boolean ret = true;
		
		Query<DocumentSettingRecord> qbe = new Query<DocumentSettingRecord>(DocumentSettingRecord.class);
		qbe.add(DocumentSettingRecord.FLD_INSTITUTION_ID, "=", li.getInstitutionID()); //$NON-NLS-1$ //$NON-NLS-2$
		List<DocumentSettingRecord> list = qbe.execute();
		for (DocumentSettingRecord po : list) {
			// TODO Restore this point  
			//if (CoreHub.getLocalLockService().acquireLock(po).isOk()) {
				po.delete();
			/*	CoreHub.getLocalLockService().releaseLock(po);
			} else {
				ret = false;
			}*/
		}
		return ret;
	}	

	@Override
	public Point computeSize(){
		return new Point(350, 350);
	}
}
