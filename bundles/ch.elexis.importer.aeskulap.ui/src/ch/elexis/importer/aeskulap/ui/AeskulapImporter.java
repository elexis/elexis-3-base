package ch.elexis.importer.aeskulap.ui;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile.Type;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.elexis.importer.aeskulap.ui.service.AeskulapImporterServiceHolder;

public class AeskulapImporter extends ImporterPage {

	private boolean overwrite = false;

	private boolean keepPatientNumber = true;

	@Override
	public String getTitle() {
		return "Aeskulap";
	}

	@Override
	public List<String> getObjectClass() {
		return Arrays.asList(IPatient.class.getName(), ILabResult.class.getName(), ILabItem.class.getName(),
				IContact.class.getName(), IDocumentLetter.class.getName());
	}

	@Override
	public String getDescription() {
		return "Datenimport Aeskulap";
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		String filename = results[0];
		if (StringUtils.isBlank(filename)) {
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
						"Kein Verzeichnis ausgewählt.");
			});
			return new Status(Status.ERROR, "ch.elexis", "No file given"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		File directory = new File(filename);
		if (!directory.exists() || !directory.isDirectory()) {
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
						"Das Verzeichnis " + filename + " existiert nicht, oder ist kein Verzeichnis.");
			});
			return new Status(Status.ERROR, "ch.elexis", "Invalid file given"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		List<IAeskulapImportFile> files = AeskulapImporterServiceHolder.get().setImportDirectory(directory);
		if (!files.isEmpty()) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, files.size());
			System.setProperty(IAeskulapImporter.PROP_KEEPPATIENTNUMBER, Boolean.toString(keepPatientNumber));
			List<IAeskulapImportFile> problems = AeskulapImporterServiceHolder.get().importFiles(files, overwrite,
					subMonitor);
			if (problems.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				files.stream().filter(af -> af.getType() != Type.LETTERDIRECTORY)
						.forEach(f -> sb.append(f.getFile().getName() + StringUtils.LF));
				Display.getDefault().asyncExec(() -> {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Info",
							"Die Dateien\n\n" + sb.toString() + "\nwurden erfolgreich importiert.");
				});
			} else {
				StringBuilder sb = new StringBuilder();
				problems.stream().filter(af -> af.getType() != Type.LETTERDIRECTORY)
						.forEach(f -> sb.append(f.getFile().getName() + StringUtils.LF));
				Display.getDefault().asyncExec(() -> {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Bei folgenden Dateien\n\n" + sb.toString() + "\nist ein Problem aufgetreten.");
				});
			}
		} else {
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Info",
						"Im Verzeichnis " + filename + " wurden keine Dateien zum Import gefunden.");
			});
		}

		return Status.OK_STATUS;
	}

	@Override
	public Composite createPage(Composite parent) {
		DirectoryBasedImporter fbi = new DirectoryBasedImporter(parent, this);
		fbi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Button overwriteBtn = new Button(fbi, SWT.CHECK);
		overwriteBtn.setText("Bereits importierte Daten überschreiben.");
		overwriteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				overwrite = overwriteBtn.getSelection();
			}
		});
		overwriteBtn.setSelection(overwrite);

		Button keepPatientNumberBtn = new Button(fbi, SWT.CHECK);
		keepPatientNumberBtn.setText("Patientennummer aus csv übernehmen.");
		keepPatientNumberBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				keepPatientNumber = overwriteBtn.getSelection();
			}
		});
		keepPatientNumberBtn.setSelection(keepPatientNumber);
		return fbi;
	}
}
