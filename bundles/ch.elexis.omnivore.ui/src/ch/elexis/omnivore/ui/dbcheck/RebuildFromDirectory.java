package ch.elexis.omnivore.ui.dbcheck;

import static ch.elexis.omnivore.PreferenceConstants.STOREFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.service.ConfigServiceHolder;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.core.ui.dialogs.base.InputDialog;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.ui.service.OmnivoreModelServiceHolder;

public class RebuildFromDirectory extends ExternalMaintenance {
	
	private String importPath;
	
	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
		Display display = Display.getDefault();
		display.syncExec(() -> {
			if (!ConfigServiceHolder.get().get(STOREFS, false)) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Omnivore Konfiguration Speichern im Dateisystem nicht aktiv.");
			}
			InputDialog dlg = new InputDialog(Display.getDefault().getActiveShell(), "Verzeichnis",
				"Absoluter Pfad zum Verzeichnis aus dem wieder hergestellt werden soll.", "",
				new IInputValidator() {
					
					@Override
					public String isValid(String newText){
						File file = new File(newText);
						if (file.exists() && file.isDirectory()) {
							return null;
						}
						return "[" + newText + "] ist kein vorhandenes Verzeichnis";
					}
				}, SWT.NONE);
			if (dlg.open() == Window.OK) {
				if (dlg.getValue() != null && !dlg.getValue().isEmpty()) {
					importPath = dlg.getValue();
				}
			}
		});
		int imported = 0;
		int total = 0;
		if (StringUtils.isNotBlank(importPath)) {
			File importDir = new File(importPath);
			for (File importFile : importDir.listFiles()) {
				if (importFile.isFile()) {
					total++;
					String fileId = FilenameUtils.getBaseName(importFile.getName());
					Optional<IDocumentHandle> documentHandle =
						OmnivoreModelServiceHolder.get().load(fileId, IDocumentHandle.class, true);
					if (documentHandle.isPresent()) {
						try (FileInputStream fi = new FileInputStream(importFile)) {
							documentHandle.get().setContent(fi);
							imported++;
						} catch (IOException e) {
							LoggerFactory.getLogger(getClass()).error("Error importing file", e);
						}
					} else {
						LoggerFactory.getLogger(getClass())
							.error("No DB entry for file [" + importFile.getName() + "]");
					}
				}
			}
		}
		return "Es wurden " + imported + " Dateien von " + total + " importiert";
	}
	
	@Override
	public String getMaintenanceDescription(){
		return "Omnivore aus Verzeichnis (Dateiname entspricht ID in DB) wieder herstellen.";
	}
}
