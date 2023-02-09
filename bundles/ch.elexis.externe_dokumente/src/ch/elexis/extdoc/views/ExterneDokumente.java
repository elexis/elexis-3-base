/*******************************************************************************
 * Copyright (c) 2006-2011, Daniel Lutz and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Lutz - initial implementation
 *    G. Weirich - small changes to follow API changes
 *    Niklaus Giger - Added new layout and support for drop
 *
 *******************************************************************************/

package ch.elexis.extdoc.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.actions.BackgroundJob;
import ch.elexis.core.ui.actions.BackgroundJob.BackgroundJobListener;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.actions.JobPool;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.CoreUiUtil;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.extdoc.Messages;
import ch.elexis.extdoc.dialogs.FileEditDialog;
import ch.elexis.extdoc.dialogs.VerifierDialog;
import ch.elexis.extdoc.preferences.PreferenceConstants;
import ch.elexis.extdoc.util.Email;
import ch.elexis.extdoc.util.ListFiles;
import ch.elexis.extdoc.util.MatchPatientToPath;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;;

/**
 * Diese Ansicht zeigt externe Dokumente an. Die Dokumente liegen in einem
 * Verzeichnis im Dateisystem. Dieses Verzeichnis kann in den Einstellungen
 * angegeben werden. Falls ein Patient ausgewaehlt ist, wird nach einem
 * bestimmten Schema nach diesem Patienten gefiltert.
 */

public class ExterneDokumente extends ViewPart implements IRefreshable {
	// private static final String NONE = "Keine Dokumente";

	// Erwartete Anzahl Dokumente falls noch nicht bekannt
	private static final int DEFAULT_SIZE = 1;
	private Button[] pathCheckBoxes = { null, null, null, null };

	private final String[] activePaths = { null, null, null, null };

	/*
	 * private Combo pathCombo;
	 */
	private TableViewer viewer;
	private Action doubleClickAction;
	private Action sendMailAction;
	private Action openFolderAction;
	private Action openAction;
	private IAction editAction;
	private IAction renameAction;
	private Action moveIntoSubDirsActions;
	private Action deleteAction;
	private Action verifyAction;
	private Patient actPatient;
	private Mandant actMandant;
	/*
	 * private String actPath = null;
	 */

	private TimestampComparator timeComparator;
	private FilenameComparator nameComparator;

	// work-around to get the job
	// TODO cleaner design
	BackgroundJob globalJob;

	// letzte bekannte Anzahl Dokumente (fuer getSize())
	int lastSize = DEFAULT_SIZE;

	private static Logger logger = null;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	@Optional
	@Inject
	void activePatient(IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			actPatient = (Patient) NoPoUtil.loadAsPersistentObject(patient);
			actMandant = CoreHub.actMandant;
			refreshInternal();
		}, viewer);
	}
	
	class DataLoader extends BackgroundJob {
		public DataLoader(String jobName) {
			super(jobName);
		}

		public IStatus execute(IProgressMonitor monitor) {
			if (actPatient != null) {
				result = MatchPatientToPath.getFilesForPatient(actPatient, activePaths);
			} else {
				result = Messages.ExterneDokumente_no_patient_found;
			}

			return Status.OK_STATUS;
		}

		public int getSize() {
			return lastSize;
		}
	}

	class ViewContentProvider implements IStructuredContentProvider, BackgroundJobListener {
		BackgroundJob job;

		public ViewContentProvider() {
			job = new DataLoader(Messages.ExterneDokumente_externe_dokumente);
			globalJob = job;
			if (JobPool.getJobPool().getJob(job.getJobname()) == null) {
				JobPool.getJobPool().addJob(job);
			}
			job.addListener(this);
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
			job.removeListener(this);
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object parent) {
			Object result = job.getData();
			if (result == null) {
				JobPool.getJobPool().activate(job.getJobname(), Job.LONG);
				return new String[] { Messages.ExterneDokumente_loading };
			} else {
				if (result instanceof List) {
					return ((List<?>) result).toArray();
				} else if (result instanceof String) {
					return new Object[] { result };
				} else {
					return null;
				}
			}
		}

		public void jobFinished(BackgroundJob j) {
			// int size=((Object[])j.getData()).length;
			viewer.refresh(true);
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		private static final int DATE_COLUMN = 0;
		private static final int NAME_COLUMN = 1;

		public String getColumnText(Object obj, int index) {
			switch (index) {
			case DATE_COLUMN:
				return getDate(obj);
			case NAME_COLUMN:
				return getText(obj);
			}
			return StringUtils.EMPTY;
		}

		public String getText(Object obj) {
			if (obj instanceof File) {
				File file = (File) obj;
				return file.getName();
			} else if (obj instanceof String) {
				return obj.toString();
			} else {
				return StringUtils.EMPTY;
			}
		}

		public String getDate(Object obj) {
			if (obj instanceof File) {
				File file = (File) obj;
				long modified = file.lastModified();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(modified);
				TimeTool tl = new TimeTool(cal.getTimeInMillis());
				String modifiedTime = String.format(Messages.ExterneDokumente_modified_time,
						tl.toString(TimeTool.DATE_ISO), tl.toString(TimeTool.TIME_SMALL));
				return modifiedTime;
			} else {
				return StringUtils.EMPTY;
			}
		}

		public Image getColumnImage(Object obj, int index) {
			switch (index) {
			case NAME_COLUMN:
				return getImage(obj);
			}
			return null;
		}

		public Image getImage(Object obj) {
			if (!(obj instanceof File)) {
				return null;
			}

			File file = (File) obj;
			if (file.isDirectory()) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			} else {
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
			}
		}
	}

	class TimestampComparator extends ViewerComparator {
		private static final int DESCENDING = 1;
		private int direction = DESCENDING;

		public TimestampComparator() {
			direction = DESCENDING;
		}

		public void changeSortOrder() {
			direction = -direction;
		}

		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 == null) {
				return direction;
			}
			if (e2 == null) {
				return -direction;
			}

			File file1 = (File) e1;
			File file2 = (File) e2;

			long modified1 = file1.lastModified();
			long modified2 = file2.lastModified();

			if (modified1 < modified2) {
				return -direction;
			} else if (modified1 > modified2) {
				return direction;
			} else {
				return 0;
			}
		}
	}

	class FilenameComparator extends ViewerComparator {
		private static final int DESCENDING = 1;
		private int direction = DESCENDING;

		public FilenameComparator() {
			direction = DESCENDING;
		}

		public void changeSortOrder() {
			direction = -direction;
		}

		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 == null) {
				return direction;
			}
			if (e2 == null) {
				return -direction;
			}

			File file1 = (File) e1;
			File file2 = (File) e2;
			return direction * file1.compareTo(file2);
		}
	}

	/**
	 * The constructor.
	 */
	public ExterneDokumente() {
	}

	public static void addFile(String f) {
		Patient act = ElexisEventDispatcher.getSelectedPatient();
		if (act == null) {
			SWTHelper.showError(Messages.ExterneDokumente_no_patient_found,
					Messages.ExterneDokumente_select_patient_first);
			return;
		}
		File file = new File(f);
		if (!file.canRead()) {
			SWTHelper.showError(Messages.ExterneDokumente_read_errpor,
					MessageFormat.format(Messages.ExterneDokumente_could_not_read_File, f));
			return;
		}
		try {
			InputStream in = new FileInputStream(f);
			OutputStream out = new FileOutputStream(MatchPatientToPath.getSubDirPath(act));
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception ex) {
			ExHandler.handle(ex);
			SWTHelper.showError(Messages.ExterneDokumente_import_failed,
					Messages.ExterneDokumente_exception_while_copying);
		}
		logger.info(Messages.ExterneDokumente_imported + file.getAbsolutePath());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		logger = LoggerFactory.getLogger(this.getClass());
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		parent.setLayout(gridLayout);

		Composite topArea = new Composite(parent, SWT.NONE);
		topArea.setLayoutData(SWTHelper.getFillGridData(1, false, 1, false));
		topArea.setLayout(new GridLayout());

		Composite bottomArea = new Composite(parent, SWT.NONE);
		bottomArea.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		bottomArea.setLayout(new GridLayout());

		// check boxes

		Composite pathArea = new Composite(topArea, SWT.NONE);
		pathArea.setLayout(new GridLayout(4, false));

		SelectionAdapter checkBoxListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refreshInternal();
			}
		};

		PreferenceConstants.PathElement[] prefs = PreferenceConstants.getPrefenceElements();
		for (int j = 0; j < prefs.length; j++) {
			PreferenceConstants.PathElement cur = prefs[j];
			boolean emptyPath = (cur.name == null || cur.name.length() == 0 || cur.baseDir == null
					|| cur.baseDir.length() == 0);
			if (j == 0 || !emptyPath) {
				pathCheckBoxes[j] = new Button(pathArea, SWT.CHECK);
				// Show the logical short name
				pathCheckBoxes[j].setText(cur.name);
				pathCheckBoxes[j].setSelection(PreferenceConstants.pathIsSelected(j));
				pathCheckBoxes[j].addSelectionListener(checkBoxListener);
				if (emptyPath) {
					pathCheckBoxes[j].setToolTipText(Messages.ExterneDokumente_not_defined_in_preferences);
				} else {
					if (!emptyPath)
						pathCheckBoxes[j].setToolTipText(cur.baseDir);
				}
			}
		}

		// table
		viewer = new TableViewer(bottomArea, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		Table table = viewer.getTable();
		table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		TableColumn tc;
		timeComparator = new TimestampComparator();
		nameComparator = new FilenameComparator();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.ExterneDokumente_file_date);
		tc.setWidth(120);
		tc.setToolTipText(Messages.ExterneDokumente_click_to_sort_by_date);
		tc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (viewer.getComparator() == timeComparator)
					timeComparator.changeSortOrder();
				else
					viewer.setComparator(timeComparator);
				viewer.refresh();
			}
		});

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.ExterneDokumente_file_name);
		tc.setWidth(400);
		tc.setToolTipText(Messages.ExterneDokumente_click_to_sort_by_name);
		tc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (viewer.getComparator() == nameComparator)
					nameComparator.changeSortOrder();
				else
					viewer.setComparator(nameComparator);
				viewer.refresh();
			}
		});

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setComparator(timeComparator);
		viewer.setInput(getViewSite());

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		Transfer[] transferTypes = new Transfer[] { FileTransfer.getInstance() };
		viewer.addDropSupport(DND.DROP_COPY, transferTypes, new DropTargetAdapter() {

			@Override
			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

			@Override
			public void drop(DropTargetEvent event) {
				String[] files = (String[]) event.data;
				for (String file : files) {
					addFile(file);
					viewer.refresh();
				}

			}

		});

		// Welcher Patient ist im aktuellen WorkbenchWindow selektiert?
		actPatient = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
		getSite().getPage().addPartListener(udpateOnVisible);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager(Messages.ExterneDokumente_pop_menu);
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ch.elexis.extdoc.views.ExterneDokumente.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(openAction);
		manager.add(openFolderAction);
		manager.add(sendMailAction);
		manager.add(renameAction);
		manager.add(editAction);
		manager.add(verifyAction);
		manager.add(moveIntoSubDirsActions);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(openAction);
		manager.add(openFolderAction);
		manager.add(sendMailAction);
		manager.add(renameAction);
		manager.add(editAction);
		manager.add(deleteAction);
		manager.add(moveIntoSubDirsActions);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(openAction);
		manager.add(editAction);
		manager.add(sendMailAction);
		manager.add(openFolderAction);
	}

	private void makeActions() {
		sendMailAction = new Action() {
			public void run() {
				Object element = null;
				List<File> attachements = new ArrayList<>();
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if (selection != null && !selection.isEmpty()) {
					Iterator<?> iterator = selection.iterator();
					while (iterator.hasNext()) {
						element = iterator.next();
						if (element instanceof File) {
							attachements.add((File) element);
						}
					}
				}
				if (actPatient != null) {
					String inhalt = Email.getEmailPreface(actPatient);
					inhalt += "\n\n\nMedikation: \n" + actPatient.getMedikation();
					inhalt += "\nAlle Konsultationen\n" + Email.getAllKonsultations(actPatient) + "\n\n";
					Email.openMailApplication(StringUtils.EMPTY, // No default to address
							null, inhalt, attachements);
				}
			}
		};
		sendMailAction.setText(Messages.ExterneDokumente_sendEmail);
		sendMailAction.setImageDescriptor(Images.IMG_MAIL.getImageDescriptor());
		sendMailAction.setToolTipText(Messages.ExterneDokumente_sendEmailTip);
		openFolderAction = new Action() {
			public void run() {
				List<File> directories = ListFiles.getDirectoriesForActPatient(actPatient);
				if (directories.size() == 0) {
					if (actPatient != null) {
						logger.info("No active directories for " + actPatient.getPersonalia());
					}
					return;
				}
				for (File directory : directories) {
					logger.info("will launch folder: " + directory.toString());
					if (Program.launch("file://" + directory.toString()) == false) {
						logger.info("Could not open directory " + directory.toString());
					}
				}
			}
		};
		openFolderAction.setText(Messages.ExterneDokumente_openFolder);
		openFolderAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.iatrix", "rsc/folder.png"));
		openFolderAction.setToolTipText(Messages.ExterneDokumente_openFolderTip);

		openAction = new Action() {
			public void run() {
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if (selection != null) {
					Object element = selection.getFirstElement();
					if (element instanceof File) {
						File file = (File) element;
						String path = file.getAbsolutePath();
						Program.launch(path);
					}
				}
			}
		};
		openAction.setText(Messages.ExterneDokumente_open);
		openAction.setToolTipText(Messages.ExterneDokumente_OpenFileTip);
		openAction.setImageDescriptor(Images.IMG_DOCUMENT_TEXT.getImageDescriptor());
		doubleClickAction = new Action() {
			@Override
			public void run() {
				openAction.run();
			}
		};

		editAction = new Action() {
			@Override
			public void run() {
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if (selection != null) {
					Object element = selection.getFirstElement();
					if (element instanceof File) {
						openFileEditorDialog((File) element);
					}
				}
			}
		};
		editAction.setText(Messages.ExterneDokumente_propeties);
		editAction.setToolTipText(Messages.ExterneDokumente_rename_or_change_date);
		editAction.setActionDefinitionId("ch.elexis.extdoc.commands.edit_properties");
		editAction.setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
		GlobalActions.registerActionHandler(this, editAction);

		deleteAction = new Action() {
			public void run() {
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if (selection != null) {
					Object element = selection.getFirstElement();
					if (element instanceof File) {
						File file = (File) element;

						if (SWTHelper.askYesNo(Messages.ExterneDokumente_delete_doc,
								Messages.ExterneDokumente_shold_doc_be_delted + file.getName())) {
							logger.info("Datei Löschen: " + file.getAbsolutePath()); //$NON-NLS-1$
							file.delete();
							refreshInternal();
						}
					}
				}
			}
		};
		deleteAction.setText(Messages.ExterneDokumente_delete);
		deleteAction.setToolTipText(Messages.ExterneDokumente_delete_files);
		deleteAction.setActionDefinitionId(GlobalActions.DELETE_COMMAND);
		GlobalActions.registerActionHandler(this, deleteAction);

		renameAction = new Action() {
			public void run() {
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if (selection != null) {
					Object element = selection.getFirstElement();
					if (element instanceof File) {
						openFileEditorDialog((File) element);
					}
				}
			}
		};
		renameAction.setText(Messages.ExterneDokumente_renaming_file);
		renameAction.setToolTipText(Messages.ExterneDokumente_renaming_file);
		renameAction.setActionDefinitionId("ch.elexis.extdoc.commands.rename");
		GlobalActions.registerActionHandler(this, renameAction);

		verifyAction = new Action() {
			public void run() {
				new VerifierDialog(getViewSite().getShell(), actPatient).open();
				// files may have been renamed
				refreshInternal();
			}
		};
		verifyAction.setText(Messages.ExterneDokumente_verify_files);
		verifyAction.setToolTipText(Messages.ExterneDokumente_verify_files_Belong_to_patient);
		moveIntoSubDirsActions = new ch.elexis.extdoc.dialogs.MoveIntoSubDirsDialog();
		moveIntoSubDirsActions.setText(Messages.ExterneDokumente_move_into_subdir);
		moveIntoSubDirsActions.setToolTipText(Messages.ExterneDokumente_move_into_subdir_tooltip);
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void refreshInternal() {
		PreferenceConstants.PathElement[] prefs = PreferenceConstants.getPrefenceElements();
		for (int j = 0; j < prefs.length; j++) {
			if (pathCheckBoxes[j] != null && pathCheckBoxes[j].getSelection()) {
				activePaths[j] = prefs[j].baseDir;
				PreferenceConstants.pathSetSelected(j, true);
			} else {
				activePaths[j] = null;
				PreferenceConstants.pathSetSelected(j, false);
			}
		}
		PreferenceConstants.saveSelected();
		globalJob.invalidate();
		viewer.refresh(true);
	}

	/*
	 * private void showMessage(String message) { MessageDialog.openInformation(
	 * viewer.getControl().getShell(), "Externe Dokumente", message); }
	 */

	private void openFileEditorDialog(File file) {
		FileEditDialog fed = new FileEditDialog(getViewSite().getShell(), file);
		fed.open();
		refreshInternal();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Wichtig! Alle Listeners, die eine View einhängt, müssen in dispose() wieder
	 * ausgehängt werden. Sonst kommt es zu Exceptions, wenn der Anwender eine View
	 * schliesst und später ein Objekt selektiert.
	 */
	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
	}

	// Die Methode des SelectionListeners
	public void selectionEvent(PersistentObject obj) {
		if (obj instanceof Patient) {
		}
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}
}
