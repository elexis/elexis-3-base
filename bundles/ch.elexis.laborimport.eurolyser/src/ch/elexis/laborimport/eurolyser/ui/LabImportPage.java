package ch.elexis.laborimport.eurolyser.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.LabItem;
import ch.elexis.laborimport.eurolyser.EurolyserImporter;

public class LabImportPage extends ImporterPage {

	private static Logger logger = LoggerFactory.getLogger(LabImportPage.class);

	private ILaboratory eurolyserLabor;
	private Shell parentShell;

	private File archiveDir;

	public LabImportPage() {
		eurolyserLabor = LabImportUtilHolder.get().getOrCreateLabor("Eurolyser");
	}

	@Override
	public String getTitle() {
		return "Eurolyser";
	}

	@Override
	public List<java.lang.String> getObjectClass() {
		return Arrays.asList(ILabResult.class.getName(), "ch.elexis.omnivore.model.IDocumentHandle");
	}

	@Override
	public String getDescription() {
		return "Bitte wählen Sie ein Verzeichnis mit Dateien im Eurolyser-Format für den Import aus";
	}

	@Override
	public Composite createPage(Composite parent) {
		parentShell = parent.getShell();
		Composite ret = new ImporterPage.DirectoryBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return ret;
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		String dirname = results[0];
		List<File> files = getEurolyserFiles(dirname);
		monitor.beginTask("Eurolyser import.", files.size());
		for (File file : files) {
			try {
				EurolyserImporter importer = new EurolyserImporter(eurolyserLabor, file);
				if (importer.createResults()) {
					archiveFile(file);
				}
				monitor.worked(1);
			} catch (final IllegalStateException e) {
				parentShell.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.openError(parentShell, "Fehler",
								"Die Datei konnte nicht eingelesen werden.\n[" + e.getLocalizedMessage() + "]");
					}
				});
			}
		}

		ElexisEventDispatcher.reload(LabItem.class);
		return Status.OK_STATUS;
	}

	private List<File> getEurolyserFiles(String dirname) {
		ArrayList<File> ret = new ArrayList<File>();
		File dir = new File(dirname);
		File[] list = dir.listFiles();
		for (File file : list) {
			if (isEurolyserFile(file)) {
				ret.add(file);
			}
		}
		if (!ret.isEmpty()) {
			archiveDir = createArchiveDir(dir);
		}

		return ret;
	}

	private File createArchiveDir(File dir) {
		File ret = new File(dir, "archiv");
		if (!ret.exists()) {
			ret.mkdir();
		}
		return ret;
	}

	private void archiveFile(File file) {
		String prefix = StringUtils.EMPTY;
		while (!file.renameTo(new File(archiveDir, prefix + file.getName()))) {
			prefix += "_";
		}
	}

	private boolean isEurolyserFile(File file) {
		try (FileInputStream input = new FileInputStream(file)) {
			byte[] buffer = new byte[256];
			int len = input.read(buffer);
			// a file should have more than 30 bytes and stat with a line with 5 parts
			// separated by ;
			if (len > 30) {
				ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
				BufferedReader reader = new BufferedReader(new InputStreamReader(bis));
				String line = reader.readLine().trim();
				if (line != null && !line.isEmpty()) {
					String[] parts = line.split(";");
					if (parts.length == 5) {
						reader.close();
						return true;
					}
				}
				reader.close();
			}
		} catch (IOException e) {

		}
		logger.warn("File [" + file.getAbsolutePath() + "] is not in eurolyser format.");
		return false;
	}

}
