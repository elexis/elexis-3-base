package ch.elexis.omnivore.ui.dbcheck;

import static ch.elexis.omnivore.PreferenceConstants.STOREFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.INativeQuery;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.core.ui.dialogs.base.InputDialog;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.ui.service.OmnivoreModelServiceHolder;
import ch.elexis.scripting.CSVWriter;

public class RebuildFromDirectory extends ExternalMaintenance {
	
	private String importPath;
	
	private boolean matchDbId;
	private boolean matchMimetype;
	
	// @formatter:off
	private static final String DOCHANDLE_MIME_QUERY = "SELECT ID FROM ch_elexis_omnivore_data"
			+ " WHERE deleted = '0'"
			+ " AND mimetype = ?1";
	// @formatter:on
	private INativeQuery mimeQuery;
	
	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
		mimeQuery = null;
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
				}, SWT.NONE) {
				@Override
				protected Control createDialogArea(Composite parent){
					Composite c = (Composite)super.createDialogArea(parent);
					
					Button bMatchId = new Button(c, SWT.CHECK);
					bMatchId.setText("Dateiname ist DB ID");
					bMatchId.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e){
							matchDbId = bMatchId.getSelection();
						}
					});
					
					Button bMatchMime = new Button(c, SWT.CHECK);
					bMatchMime.setText("Dateiname ist Mimetype");
					bMatchMime.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e){
							matchMimetype = bMatchMime.getSelection();
						}
					});
					
					return c;
				}
			};
			if (dlg.open() == Window.OK) {
				if (dlg.getValue() != null && !dlg.getValue().isEmpty()) {
					importPath = dlg.getValue();
				}
			}
		});
		int imported = 0;
		int total = 0;
		if (matchDbId || matchMimetype) {
			if (StringUtils.isNotBlank(importPath)) {
				File importDir = new File(importPath);
				File[] files = importDir.listFiles();
				pm.beginTask("Wiederherstellen aus Dateien", files.length);
				for (File importFile : files) {
					if (importFile.isFile()) {
						String fileId = FilenameUtils.getBaseName(importFile.getName());
						Optional<IDocumentHandle> documentHandle = Optional.empty();
						if (matchDbId) {
							documentHandle = OmnivoreModelServiceHolder.get().load(fileId,
								IDocumentHandle.class, true);
						} else if (matchMimetype) {
							if (mimeQuery == null) {
								mimeQuery = OmnivoreModelServiceHolder.get()
									.getNativeQuery(DOCHANDLE_MIME_QUERY);
							}
							List<?> found = mimeQuery
								.executeWithParameters(
									Collections.singletonMap(Integer.valueOf(1),
										importFile.getName()))
								.collect(Collectors.toList());
							if (!found.isEmpty()) {
								if (found.size() == 1) {
									String handleId = found.get(0).toString();
									documentHandle = OmnivoreModelServiceHolder.get().load(handleId,
										IDocumentHandle.class, true);
								} else {
									LoggerFactory.getLogger(getClass())
										.warn("Multiple DB entries for file ["
											+ importFile.getName() + "]");
								}
							}
						}
						if (documentHandle.isPresent()) {
							try (FileInputStream fi = new FileInputStream(importFile)) {
								documentHandle.get().setContent(fi);
								imported++;
							} catch (IOException e) {
								LoggerFactory.getLogger(getClass()).error("Error importing file",
									e);
							}
							moveToImported(importFile);
						} else {
							LoggerFactory.getLogger(getClass())
								.warn("No DB entry for file [" + importFile.getName() + "]");
						}
						pm.worked(1);
						total++;
					}
				}
				pm.beginTask("Überprüfen aller Omnivore Einträge", IProgressMonitor.UNKNOWN);
				IQuery<IDocumentHandle> queryAll =
					OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
				List<IDocumentHandle> invalidEntries = new ArrayList<IDocumentHandle>();
				try (IQueryCursor<IDocumentHandle> all = queryAll.executeAsCursor()) {
					while (all.hasNext()) {
						IDocumentHandle dh = all.next();
						if (!dh.isCategory() && isMissingEntry(dh)) {
							invalidEntries.add(dh);
						}
					}
				}
				if (!invalidEntries.isEmpty()) {
					writeCsv(invalidEntries, importDir);
				}
			}
		} else {
			return "Kein Dateiname Vergleich zu DB Feld ausgewählt";
		}
		return "Es wurden " + imported + " Dateien von " + total + " importiert";
	}
	
	private void moveToImported(File importFile){
		try {
			File importedDir = new File(importFile.getParentFile(), "imported");
			FileUtils.moveFileToDirectory(importFile, importedDir, true);
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error moving file to imported dir", e);
		}
	}
	
	private void writeCsv(List<IDocumentHandle> invalidEntries, File importDir){
		CSVWriter csv = null;
		try {
			csv =
				new CSVWriter(new FileWriter(new File(importDir, "invalid_omnivore.csv")));
			// @formatter:off
			String[] header = new String[] {
				"id", // line 0 
				"patnr", // line 1
				"patdesc1", // line 2
				"patdesc2", // line 3
				"category", // line 4
				"title", // line 5
				"mime", // line 6
				"keywords", // line 7
			};
			csv.writeNext(header);
			for (IDocumentHandle entry : invalidEntries) {
				IPatient pat = entry.getPatient();
				String[] line = new String[header.length];
				line[0] = entry.getId();
				if(pat != null) {
					line[1] = pat.getCode();
					line[2] = pat.getDescription1();
					line[3] = pat.getDescription2();
				} else {
					line[1] = "no pat";
					line[2] = "no pat";
					line[3] = "no pat";				
				}
				line[4] = entry.getCategory() != null ? entry.getCategory().getName() : "no cat";
				line[5] = entry.getTitle();
				line[6] = entry.getMimeType();
				line[7] = entry.getKeywords();
				csv.writeNext(line);
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass())
				.error("Error writing csv with invalid omnivore entries", e);
		}finally {
			if (csv != null) {
				try {
					csv.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
	
	private boolean isMissingEntry(IDocumentHandle dh){
		try {
			InputStream content = dh.getContent();
			content.close();
		} catch (Exception e) {
			return true;
		}
		return false;
	}
	
	@Override
	public String getMaintenanceDescription(){
		return "Omnivore aus Verzeichnis wiederherstellen.";
	}
}
