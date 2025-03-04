package ch.elexis.fire.ui;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.fire.core.IFIREService;
import ch.elexis.fire.core.task.FIREExportTaskDescriptor;
import ch.elexis.fire.ui.export.internal.AutomaticExportTask;
import jakarta.inject.Inject;

public class FirePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	@Inject
	private IFIREService fireService;

	@Inject
	private ITaskService taskService;

	@Inject
	private IConfigService configService;

	@Inject
	private IContextService contextService;

	private ITaskDescriptor taskDescriptor;

	private Label synchInfo;

	boolean initialExportDone;

	@Override
	public void init(IWorkbench workbench) {
		CoreUiUtil.injectServices(this);
		try {
			taskDescriptor = FIREExportTaskDescriptor.getOrCreate(taskService);
		} catch (TaskException e) {
			setErrorMessage(e.getMessage());
			LoggerFactory.getLogger(getClass()).error("Could not init taskDescriptor", e);
		}
		initialExportDone = false;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayout(new GridLayout());

		StringBuilder sb = new StringBuilder();
		if (fireService.getInitialTimestamp() != -1) {
			initialExportDone = true;
			Instant instant = Instant.ofEpochMilli(fireService.getInitialTimestamp());
			LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			sb.append("Der initiale Export war am " + DateTimeFormatter.ofPattern("dd.MM.yyyy").format(localDateTime)
					+ " um " + DateTimeFormatter.ofPattern("HH:mm").format(localDateTime));
		} else {
			sb.append("Noch kein initialier Export.");
		}
		sb.append("\n");
		if (fireService.getIncrementalTimestamp() != -1) {
			Instant instant = Instant.ofEpochMilli(fireService.getIncrementalTimestamp());
			LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			sb.append("Der letzte inkrementelle Export war am "
					+ DateTimeFormatter.ofPattern("dd.MM.yyyy").format(localDateTime) + " um "
					+ DateTimeFormatter.ofPattern("HH:mm").format(localDateTime));
		} else {
			sb.append("Noch kein inkrementeller Export.");
		}
		synchInfo = new Label(area, SWT.NONE);
		synchInfo.setText(sb.toString());

		Label separator = new Label(area, SWT.HORIZONTAL);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.verticalIndent = 10;
		separator.setLayoutData(gd);

		Button automaticExport = new Button(area, SWT.CHECK);
		automaticExport.setText("Auf dieser Station automatisch an FIRE übermitteln.");
		automaticExport.setSelection(contextService.getStationIdentifier()
				.equals(configService.get(AutomaticExportTask.SCHEDULED_STATION, null)));
		automaticExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (automaticExport.getSelection()) {
					configService.set(AutomaticExportTask.SCHEDULED_STATION, contextService.getStationIdentifier());
				} else {
					configService.set(AutomaticExportTask.SCHEDULED_STATION, null);
				}
			}
		});

//		separator = new Label(area, SWT.HORIZONTAL);
//		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
//		gd.verticalIndent = 10;
//		separator.setLayoutData(gd);

//		Button triggerExport = new Button(area, SWT.PUSH);
//		if (initialExportDone) {
//			triggerExport.setText("Inkrementeller Export starten");
//		} else {
//			triggerExport.setText("Initialer Export starten");
//		}
//		triggerExport.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				Job job = new Job(initialExportDone ? "FIRE inkrementeller Export" : "FIRE initialer Export") {
//					@Override
//					protected IStatus run(IProgressMonitor monitor) {
//						monitor.beginTask("FIRE Export", IProgressMonitor.UNKNOWN);
//						try {
//							if (taskDescriptor.getOwner() == null) {
//								taskDescriptor.setOwner(ContextServiceHolder.get().getActiveUser().get());
//								taskService.saveTaskDescriptor(taskDescriptor);
//							}
//							ITask task = taskService.triggerSync(taskDescriptor, monitor, TaskTriggerType.MANUAL,
//									Collections.emptyMap());
//							if (task.getState() == TaskState.COMPLETED || task.getState() == TaskState.COMPLETED_MANUAL
//									|| task.getState() == TaskState.COMPLETED_WARN) {
//								Map<String, ?> taskResult = task.getResult();
//								if (taskResult != null
//										&& taskResult.get(ReturnParameter.RESULT_DATA) instanceof String) {
//									Display.getDefault().syncExec(() -> {
//										MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Resultat",
//												(String) taskResult.get(ReturnParameter.RESULT_DATA));
//									});
//								}
//								return Status.OK_STATUS;
//							}
//						} catch (TaskException e) {
//							LoggerFactory.getLogger(getClass()).error("Error performing FIRE export", e);
//							Display.getDefault().syncExec(() -> {
//								MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
//										"Der Export ist fehlgeschlagen.");
//							});
//						}
//						return Status.CANCEL_STATUS;
//					}
//				};
//				job.schedule();
//			}
//		});

		area.layout();
		return area;
	}
}
