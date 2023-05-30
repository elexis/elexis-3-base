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

import org.apache.commons.lang3.StringUtils;
import java.util.List;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
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
import ch.elexis.data.Kontakt;
import ch.elexis.data.Query;
import ch.novcom.elexis.mednet.plugin.MedNet;
import ch.novcom.elexis.mednet.plugin.data.ContactLinkRecord;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;
import ch.novcom.elexis.mednet.plugin.ui.commands.ContactLinkRecordCreate;
import ch.novcom.elexis.mednet.plugin.ui.dialog.ContactLinkRecordEditDialog;

/**
 * Configuration for the Document part of MedNet
 */
public class ContactLinkPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private TableViewer tableViewer;
	private Table table;

	private String[] tableheaders = { MedNetMessages.ContactLinkPreferences_MedNetId,
			MedNetMessages.ContactLinkPreferences_MedNetName, MedNetMessages.ContactLinkPreferences_ContactLabel,
			MedNetMessages.ContactLinkPreferences_DocImport, MedNetMessages.ContactLinkPreferences_DocImportId,
			MedNetMessages.ContactLinkPreferences_FormImport, MedNetMessages.ContactLinkPreferences_XIDDomain };

	private int[] tableColwidth = { 8, 19, 19, 19, 8, 19, 8 };

	/**
	 * Standard Constructor
	 */
	public ContactLinkPreferencePage() {
		super(MedNetMessages.ContactLinkPreferences_title);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Nothing to initialize
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createContents(org.
	 * eclipse.swt.widgets .Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {

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
				public void widgetSelected(SelectionEvent e) {
					tableViewer.refresh(true);
				}
			});
		}

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return ContactLinkRecord.getAllContactLinkRecords().toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		tableViewer.setLabelProvider(new ReceivingListLabelProvider());
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection) tableViewer.getSelection();
				Object o = sel.getFirstElement();
				if (o instanceof ContactLinkRecord) {
					ContactLinkRecord li = (ContactLinkRecord) o;
					// It looks like the clean way should be to call following funktion
					// But since it is not working we open directly the Dialog
					// ContactLinkRecordEdit.executeWithParams(li);
					ContactLinkRecordEditDialog dialog = new ContactLinkRecordEditDialog(getShell(), li);
					if (dialog.open() == Dialog.OK) {
						tableViewer.refresh();
					}
				}
			}

		});

		tableViewer.setInput(this);
		return tableComposite;
	}

	static class ReceivingListLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			ContactLinkRecord contactLinkRecord = (ContactLinkRecord) element;
			Kontakt kontakt = Kontakt.load(contactLinkRecord.getContactID());

			switch (columnIndex) {
			case 0:
				return contactLinkRecord.getMedNetID();
			case 1:
				return MedNet.getSettings().getInstitutions().get(contactLinkRecord.getMedNetID());
			case 2:
				return kontakt.getLabel(true);
			case 3:
				return (contactLinkRecord.docImport_isActive() ? "on" : "off")
						+ (contactLinkRecord.getCategoryDoc().isEmpty() ? StringUtils.EMPTY
								: " ( " + contactLinkRecord.getCategoryDoc() + " )");
			case 4:
				return contactLinkRecord.getDocImport_id();
			case 5:
				return (contactLinkRecord.formImport_isActive() ? "on" : "off")
						+ (contactLinkRecord.getCategoryForm().isEmpty() ? StringUtils.EMPTY
								: " ( " + contactLinkRecord.getCategoryForm() + " )");
			case 6:
				return contactLinkRecord.getXIDDomain();
			default:
				return "?col?"; //$NON-NLS-1$
			}
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

	};

	@Override
	protected void contributeButtons(Composite parent) {

		((GridLayout) parent.getLayout()).numColumns++;
		Button bNewItem = new Button(parent, SWT.PUSH);
		bNewItem.setText(MedNetMessages.ContactLinkPreferences_new);
		bNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					// execute the command
					IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getService(IHandlerService.class);

					handlerService.executeCommand(ContactLinkRecordCreate.COMMANDID, null);
				} catch (Exception ex) {
					throw new RuntimeException(ContactLinkRecordCreate.COMMANDID, ex);
				}
				tableViewer.refresh();
			}
		});
		((GridLayout) parent.getLayout()).numColumns++;
		Button bDelItem = new Button(parent, SWT.PUSH);
		bDelItem.setText(MedNetMessages.ContactLinkPreferences_delete);
		bDelItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) tableViewer.getSelection();
				Object o = sel.getFirstElement();
				if (o instanceof ContactLinkRecord) {
					ContactLinkRecord li = (ContactLinkRecord) o;
					if (MessageDialog.openQuestion(getShell(), MedNetMessages.ContactLinkPreferences_delete,
							MessageFormat.format(MedNetMessages.ContactLinkPreferences_reallyDelete, li.getLabel()))) {

						if (deleteRecord(li)) {
							li.removeFromDatabase();
							tableViewer.remove(li);
						} else {
							MessageDialog.openWarning(getShell(), MedNetMessages.ContactLinkPreferences_delete,
									MedNetMessages.ContactLinkPreferences_deleteFailed);
						}
					}
				}
			}
		});
		((GridLayout) parent.getLayout()).numColumns++;
		Button bDelAllItems = new Button(parent, SWT.PUSH);
		bDelAllItems.setText(MedNetMessages.ContactLinkPreferences_deleteAll);
		bDelAllItems.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (SWTHelper.askYesNo(MedNetMessages.ContactLinkPreferences_deleteAllTitle,
						MedNetMessages.ContactLinkPreferences_deleteAllExplain)) {
					Query<ContactLinkRecord> qbli = new Query<ContactLinkRecord>(ContactLinkRecord.class);
					List<ContactLinkRecord> items = qbli.execute();
					boolean success = true;
					for (ContactLinkRecord li : items) {
						if (deleteRecord(li)) {
							li.removeFromDatabase();
						} else {
							success = false;
						}
					}
					if (!success) {
						MessageDialog.openWarning(getShell(), MedNetMessages.ContactLinkPreferences_deleteAll,
								MedNetMessages.ContactLinkPreferences_deleteAllFailed);
					}
					tableViewer.refresh();
				}
			}
		});
		if (AccessControlServiceHolder.get().request(AccessControlDefaults.DELETE_LABITEMS) == false) {
			bDelAllItems.setEnabled(false);
		}
	}

	private boolean deleteRecord(ContactLinkRecord li) {
		boolean ret = true;

		Query<ContactLinkRecord> qbe = new Query<ContactLinkRecord>(ContactLinkRecord.class);
		qbe.add(ContactLinkRecord.FLD_ID, "=", li.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		List<ContactLinkRecord> list = qbe.execute();
		for (ContactLinkRecord po : list) {
			// TODO Restore this point
			// if (CoreHub.getLocalLockService().acquireLock(po).isOk()) {
			po.removeFromDatabase();
			/*
			 * CoreHub.getLocalLockService().releaseLock(po); } else { ret = false; }
			 */
		}
		return ret;
	}

	@Override
	public Point computeSize() {
		return new Point(350, 350);
	}
}
