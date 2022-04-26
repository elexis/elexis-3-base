/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.artofsolving.jodconverter.office.OfficeException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import at.medevit.medelexis.text.msword.Messages;
import at.medevit.medelexis.text.msword.plugin.util.CommunicationFile;
import at.medevit.medelexis.text.msword.plugin.util.CommunicationFile.FileType;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Brief;
import ch.elexis.data.Query;

public class MSWordPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String MSWORD_SPELLING_CHECK = "at.medevit.medelexis.text.msword/spelling";
	public static final String MSWORD_OPEN_EXTERN = "at.medevit.medelexis.text.msword/openextern";

	public MSWordPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	public MSWordPreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public MSWordPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new GridLayout(2, true));

		Label lbl = new Label(content, SWT.NONE);
		lbl.setText(Messages.MSWordPreferencePage_EnableSpellcheck);

		final Button spellingBtn = new Button(content, SWT.CHECK);
		spellingBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.userCfg.set(MSWORD_SPELLING_CHECK, spellingBtn.getSelection());
			}
		});
		spellingBtn.setSelection(CoreHub.userCfg.get(MSWORD_SPELLING_CHECK, false));

		lbl = new Label(content, SWT.NONE);
		lbl.setText("Dokumente ausserhalb von Elexis öffnen");

		final Button externBtn = new Button(content, SWT.CHECK);
		externBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.userCfg.set(MSWORD_OPEN_EXTERN, externBtn.getSelection());
			}
		});
		externBtn.setSelection(CoreHub.userCfg.get(MSWORD_OPEN_EXTERN, false));

		lbl = new Label(content, SWT.NONE);
		lbl.setText(Messages.MSWordPreferencePage_ConvertAll);

		Button btn = new Button(content, SWT.PUSH);
		btn.setText(Messages.MSWordPreferencePage_StartConvert);
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				ConversionJob job = new ConversionJob();
				try {
					dialog.run(true, false, job);
				} catch (InvocationTargetException ex) {
					ex.printStackTrace();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}

				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.MSWordPreferencePage_ConvertDone,
						Messages.MSWordPreferencePage_ConvertDone_0 + job.okCnt
								+ Messages.MSWordPreferencePage_ConvertDone_1 + job.errCnt
								+ Messages.MSWordPreferencePage_ConvertDone_2);
			}
		});

		return content;
	}

	private class ConversionJob implements IRunnableWithProgress {

		int okCnt = 0;
		int errCnt = 0;

		@Override
		public void run(final IProgressMonitor monitor) {
			Query<Brief> query = new Query<Brief>(Brief.class);
			final List<Brief> briefe = query.execute();

			monitor.beginTask(Messages.MSWordPreferencePage_ConvertLetters, briefe.size());

			CommunicationFile file = new CommunicationFile();
			for (final Brief brief : briefe) {
				monitor.subTask(Messages.MSWordPreferencePage_LoadLetter + brief.getLabel());
				// load
				byte[] arr = brief.loadBinary();
				if (arr == null) {
					monitor.worked(1);
					continue;
				}
				FileType type = CommunicationFile.guessFileType(arr);
				if (type == FileType.ODT || type == FileType.SXW) {
					try {
						monitor.subTask(Messages.MSWordPreferencePage_ConvertLetter + brief.getLabel());

						// writer will start conversion
						file.write(arr);
						brief.save(file.getContentAsByteArray(), "doc"); //$NON-NLS-1$
						monitor.worked(1);
					} catch (OfficeException e) {
						errCnt++;
					}
					okCnt++;
				} else {
					monitor.worked(1);
				}
			}
			file.dispose();
		}
	}
}
