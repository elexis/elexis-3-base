/*******************************************************************************
 * Copyright (c) 2005-2006, Daniel Lutz and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Lutz - initial implementation
 *    G. Weirich - small changes to follow API changes
 *    
 *******************************************************************************/

package ch.elexis.extdoc.dialogs;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.BackgroundJob;
import ch.elexis.actions.JobPool;
import ch.elexis.actions.BackgroundJob.BackgroundJobListener;
import ch.elexis.data.Patient;
import ch.elexis.extdoc.Messages;
import ch.elexis.extdoc.preferences.PreferenceConstants;
import ch.elexis.extdoc.util.ListFiles;
import ch.elexis.extdoc.util.MatchPatientToPath;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.elexis.extdoc.util.*;

public class VerifierDialog extends TitleAreaDialog {
	private Patient actPatient;
	
	TableViewer viewer;
	
	// work-around to get the job
	// TODO cleaner design
	BackgroundJob globalJob;
	
	class DataLoader extends BackgroundJob {
		public DataLoader(String jobName){
			super(jobName);
		}
		
		public IStatus execute(IProgressMonitor monitor){
			List<File> list;
			
			String[] paths = PreferenceConstants.getActiveBasePaths();
			
			if (actPatient != null) {
				result = MatchPatientToPath.getFilesForPatient(actPatient, null);
			} else {
				result = Messages.ExterneDokumente_no_patient_found;
			}
			
			return Status.OK_STATUS;
		}
		
		public int getSize(){
			return 1;
		}
	}
	
	class VerifierContentProvider implements IStructuredContentProvider, BackgroundJobListener {
		private String BASE_JOBNAME = Messages.VerifierDialog_verify_job_name;
		
		BackgroundJob job;
		
		public VerifierContentProvider(){
			// TODO remove job from JobPool when it has finished.
			// for now, we just use unique names.
			String jobName = BASE_JOBNAME + " " + StringTool.unique(BASE_JOBNAME); //$NON-NLS-1$
			job = new DataLoader(jobName);
			globalJob = job;
			if (JobPool.getJobPool().getJob(job.getJobname()) == null) {
				JobPool.getJobPool().addJob(job);
			}
			job.addListener(this);
			
		}
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput){}
		
		public void dispose(){
			job.removeListener(this);
			// JobPool.getJobPool().
		}
		
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object parent){
			Object result = job.getData();
			if (result == null) {
				JobPool.getJobPool().activate(job.getJobname(), Job.LONG);
				return new String[] {
					Messages.ExterneDokumente_loading
				};
			} else {
				if (result instanceof List) {
					return ((List) result).toArray();
				} else if (result instanceof String) {
					return new Object[] {
						result
					};
				} else {
					return null;
				}
			}
		}
		
		public void jobFinished(BackgroundJob j){
			viewer.refresh(true);
			
		}
	}
	
	class VerifierLabelProvider extends LabelProvider implements ITableLabelProvider {
		private static final int STATUS_COLUMN = 0;
		private static final int DATE_COLUMN = 1;
		private static final int NAME_COLUMN = 2;
		
		public String getColumnText(Object obj, int index){
			switch (index) {
			case DATE_COLUMN:
				return getDate(obj);
			case NAME_COLUMN:
				return getText(obj);
			}
			return ""; //$NON-NLS-1$
		}
		
		public String getText(Object obj){
			if (obj instanceof File) {
				File file = (File) obj;
				return file.getName();
			} else if (obj instanceof String) {
				return obj.toString();
			} else {
				return ""; //$NON-NLS-1$
			}
		}
		
		public String getDate(Object obj){
			if (obj instanceof File) {
				File file = (File) obj;
				long modified = file.lastModified();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(modified);
				String modifiedTime =
					DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
						cal.getTime());
				return modifiedTime;
			} else {
				return ""; //$NON-NLS-1$
			}
		}
		
		public Image getColumnImage(Object obj, int index){
			switch (index) {
			case STATUS_COLUMN:
				return Desk.getImage(Desk.IMG_FEHLER);
			case NAME_COLUMN:
				return getImage(obj);
			}
			return null;
		}
		
		public Image getImage(Object obj){
			if (!(obj instanceof File)) {
				return null;
			}
			
			File file = (File) obj;
			if (file.isDirectory()) {
				return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FOLDER);
			} else {
				return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FILE);
			}
		}
	}
	
	class TimestampComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2){
			if (e1 == null) {
				return 1;
			}
			if (e2 == null) {
				return -1;
			}
			
			File file1 = (File) e1;
			File file2 = (File) e2;
			
			long modified1 = file1.lastModified();
			long modified2 = file2.lastModified();
			
			if (modified1 < modified2) {
				return -1;
			} else if (modified1 > modified2) {
				return 1;
			} else {
				return 0;
			}
			
		}
	}
	
	public VerifierDialog(Shell parent, Patient patient){
		super(parent);
		
		actPatient = patient;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		parent.setLayout(new GridLayout());
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		composite.setLayout(new GridLayout());
		
		viewer =
			new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		
		Table table = viewer.getTable();
		table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		
		TableColumn tc;
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(""); //$NON-NLS-1$
		tc.setWidth(40);
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.ExterneDokumente_file_date);
		tc.setWidth(120);
		tc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				// TODO sort by Datum
				// http://www.vogella.de/articles/EclipseJFaceTable/article.html#sortcolumns
			}
		});
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.VerifierDialog_name);
		tc.setWidth(200);
		tc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				// TODO sort by Nummer
			}
		});
		
		viewer.setContentProvider(new VerifierContentProvider());
		viewer.setLabelProvider(new VerifierLabelProvider());
		viewer.setComparator(new TimestampComparator());
		viewer.setInput(this);
		
		// edit file properties at if double clicked
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event){
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if (selection != null) {
					Object element = selection.getFirstElement();
					if (element instanceof File) {
						openFileEditorDialog((File) element);
					}
				}
			}
		});
		
		return composite;
	}
	
	private void openFileEditorDialog(File file){
		FileEditDialog fed = new FileEditDialog(getShell(), file);
		fed.open();
		refresh();
	}
	
	private void refresh(){
		globalJob.invalidate();
		viewer.refresh(true);
	}
	
	@Override
	public void create(){
		super.create();
		setMessage(Messages.ExterneDokumente_verify_files_Belong_to_patient);
		setTitle(Messages.ExterneDokumente_verify_files);
		getShell().setText(Messages.ExterneDokumente_verify_files);
		setTitleImage(Desk.getImage(Desk.IMG_LOGO48));
	}
}
