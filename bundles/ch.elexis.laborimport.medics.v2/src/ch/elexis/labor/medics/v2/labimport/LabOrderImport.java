package ch.elexis.labor.medics.v2.labimport;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.importer.div.importers.DefaultPersistenceHandler;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.importer.div.importers.multifile.MultiFileParser;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.ui.importer.div.importers.DefaultLabContactResolver;
import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.core.ui.importer.div.importers.ImporterPatientResolver;
import ch.elexis.core.ui.importer.div.importers.multifile.strategy.DefaultImportStrategyFactory;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.labor.medics.v2.MedicsPreferencePage;
import ch.elexis.labor.medics.v2.Messages;
import ch.elexis.labor.medics.v2.util.MedicsLogger;
import ch.elexis.laborimport.medics.v2.dbcheck.UpdateLabItemCode;
import ch.rgw.tools.Result;

public class LabOrderImport extends ImporterPage {
	protected final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$
	
	private MultiFileParser mfParser;
	private HL7Parser hl7parser;
	
	public LabOrderImport(){
		mfParser = new MultiFileParser(PatientLabor.KUERZEL) {
			@Override
			protected IVirtualFilesystemHandle[] sortListHandles(
				IVirtualFilesystemHandle[] iVirtualFilesystemHandles){
				Arrays.parallelSort(iVirtualFilesystemHandles,
					new Comparator<IVirtualFilesystemHandle>() {
						
						@Override
						public int compare(IVirtualFilesystemHandle left,
							IVirtualFilesystemHandle right){
							String[] leftParts = left.getName().split("_");
							String[] rightParts = right.getName().split("_");
							if (leftParts.length > 1 && StringUtils.isNotBlank(leftParts[1])
								&& rightParts.length > 1 && StringUtils.isNotBlank(rightParts[1])) {
								return leftParts[1].compareTo(rightParts[1]);
							}
							return left.getName().compareTo(right.getName());
						}
					});
				return iVirtualFilesystemHandles;
			}
		};
		
		hl7parser = new HL7Parser(PatientLabor.KUERZEL, new ImporterPatientResolver(),
			new DefaultLabImportUiHandler(),
			new DefaultLabContactResolver(),
			CoreHub.localCfg.get(HL7Parser.CFG_IMPORT_ENCDATA, false));
	}
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		MedicsLogger.getLogger().println(
			MessageFormat.format("{0}: Medics Laborimport gestartet", df.format(new Date()))); //$NON-NLS-1$
		MedicsLogger.getLogger().println(
			"=============================================================="); //$NON-NLS-1$
		
		if(!UpdateLabItemCode.wasExecuted()) {
			UpdateLabItemCode.execute();
		}
		
		File downloadDir = new File(MedicsPreferencePage.getDownloadDir());
		Result<Object> result = null;
		MedicsLogger.getLogger()
			.println(MessageFormat.format("HL7 Dateien in Verzeichnis {0} lesen..", downloadDir)); //$NON-NLS-1$
		if (downloadDir.isDirectory()) {
			result = mfParser.importFromDirectory(downloadDir,
				new DefaultImportStrategyFactory().setPDFImportCategory(MedicsPreferencePage.getDokumentKategorie()).setMoveAfterImport(true), hl7parser,
				new DefaultPersistenceHandler());
		}
		
		MedicsLogger.getLogger().println(
			MessageFormat.format("{0}: Medics Laborimport beendet", df.format(new Date()))); //$NON-NLS-1$
		MedicsLogger.getLogger().println(""); //$NON-NLS-1$
		
		// Bereinigung der alten Archiv Dateien
		deleteOldArchivFiles();
		
		return ResultAdapter.getResultAsStatus(result);
	}
	
	/**
	 * Anhand der Einstellungen (Default 30 Tage) werden alle Dateien im Archiv Verzeichnis gelöscht
	 * die älter als die konfigurierten Tage sind.
	 * 
	 * @return
	 */
	private void deleteOldArchivFiles(){
		int archivDeleted = 0;
		MedicsLogger.getLogger().println("Alte Archiv Dateien werden bereinigt.."); //$NON-NLS-1$
		
		int days = MedicsPreferencePage.getDeleteArchivDays();
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -days);
		long lastTime = cal.getTime().getTime();
		
		new File(MedicsPreferencePage.getDownloadDir());
		
		// Archiv löschen
		File downloadDir = new File(MedicsPreferencePage.getDownloadDir());
		File archiveDir = new File(downloadDir, "archive");
		if (archiveDir.exists() && archiveDir.isDirectory()) {
			for (File archivFile : archiveDir.listFiles()) {
				if (archivFile.lastModified() < lastTime) {
					if (archivFile.delete()) {
						archivDeleted++;
					}
				}
			}
			MedicsLogger.getLogger().println(MessageFormat
				.format("{0} Dateien aus Archiv Verzeichnis gelöscht.", archivDeleted)); //$NON-NLS-1$
		}
		
		MedicsLogger.getLogger().println(""); //$NON-NLS-1$
	}
	
	@Override
	public String getTitle(){
		return Messages.LabOrderImport_titleImport;
	}
	
	@Override
	public String getDescription(){
		return Messages.LabOrderImport_descriptionImport;
	}
	
	@Override
	public Composite createPage(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		composite.setLayout(new GridLayout(2, false));
		
		// Rechnung Verzeichnis
		Label lblDownloadDir = new Label(composite, SWT.NONE);
		lblDownloadDir.setText(Messages.LabOrderImport_labelDownloadDir);
		
		final Text txtDownloadDir = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txtDownloadDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		String downloadDir = MedicsPreferencePage.getDownloadDir();
		if (downloadDir != null) {
			txtDownloadDir.setText(downloadDir);
		}
		
		// Kategorie Verzeichnis
		Label lblKategorie = new Label(composite, SWT.NONE);
		lblKategorie.setText(Messages.LabOrderImport_labelDocumentCategory);
		
		final Text txtKategorie = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txtKategorie.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		String kategorie = MedicsPreferencePage.getDokumentKategorie();
		if (kategorie != null) {
			txtKategorie.setText(kategorie);
		}
		
		return composite;
	}
	
}
