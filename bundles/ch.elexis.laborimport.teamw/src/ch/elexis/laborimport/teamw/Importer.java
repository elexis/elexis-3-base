/**
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Adapted from Viollier to Bioanalytica by Daniel Lutz <danlutz@watz.ch>
 * Important changes:
 * - OpenMedical Library configurable
 * - Easier handling of direct import
 * - Non-unique patients can be assigned to existing patients by user
 *   (instead of creating new patients)
 *   
 * Adapted to Risch by Gerry Weirich
 * Changes:
 * -  Improved detection of Patient ID by evaluation the fields PATIENT_ID and PLACER_ORDER_NUMBER
 * -  Improved matching of Names to the database
 */

package ch.elexis.laborimport.teamw;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.data.util.UtilFile;
import ch.elexis.core.importer.div.importers.DefaultPersistenceHandler;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.importer.div.importers.multifile.MultiFileParser;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.importer.div.importers.DefaultHL7Parser;
import ch.elexis.core.ui.importer.div.importers.multifile.strategy.DefaultImportStrategyFactory;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.ngiger.comm.ftp.FtpSemaException;
import ch.ngiger.comm.ftp.FtpServer;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

public class Importer extends ImporterPage {
	public static final String MY_LAB = "Team W"; //$NON-NLS-1$
	public static final String PLUGIN_ID = "ch.elexis.laborimport_teamw"; //$NON-NLS-1$
	
	protected static Log log = Log.get(PLUGIN_ID); //$NON-NLS-1$
	
	private static final String COMMENT_NAME = "Kommentar"; //$NON-NLS-1$
	private static final String COMMENT_CODE = "kommentar"; //$NON-NLS-1$
	private static final String COMMENT_GROUP = "00 Kommentar"; //$NON-NLS-1$
	
	private static final String PRAXIS_SEMAPHORE = "praxis.sem"; //$NON-NLS-1$
	private static final String TEAMW_SEMAPHORE = "teamw.sem"; //$NON-NLS-1$
	
	// importer type
	private static final int FILE = 1;
	private static final int DIRECT = 2;
	
	private MultiFileParser mfParser = new MultiFileParser(MY_LAB);
	private HL7Parser hl7parser = new DefaultHL7Parser(MY_LAB);
	
	public Importer(){}
	
	@Override
	public Composite createPage(final Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		LabImporter labImporter = new LabImporter(ret, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		labImporter.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		return ret;
	}
	
	/**
	 * Equivalent to importFile(new File(file), null)
	 * 
	 * @param filepath
	 *            the file to be imported (full path)
	 * @return
	 */
	private Result<?> importFile(final String filepath){
		File file = new File(filepath);
		Result<?> result = mfParser.importFromFile(file,
			new DefaultImportStrategyFactory()
				.setPDFImportCategory(ConfigServiceHolder.getGlobal(
					PreferencePage.DOCUMENT_CATEGORY, PreferencePage.DEFAULT_DOCUMENT_CATEGORY))
				.setMoveAfterImport(true),
			hl7parser, new DefaultPersistenceHandler());
		return result;
	}
	
	private Result<?> importDirect(){
		String batchOrFtp = ConfigServiceHolder.getGlobal(PreferencePage.BATCH_OR_FTP, PreferencePage.FTP);
		if (PreferencePage.BATCH.equals(batchOrFtp)) {
			return importDirectBatch();
		}
		
		return importDirectFtp();
	}
	
	private Result<?> importDirectFtp(){
		Result<String> result =
			new Result<String>(ch.elexis.laborimport.teamw.Messages.Importer_ok); //$NON-NLS-1$
		
		String ftpHost =
			ConfigServiceHolder.getGlobal(PreferencePage.FTP_HOST, PreferencePage.DEFAULT_FTP_HOST);
		String user =
			ConfigServiceHolder.getGlobal(PreferencePage.FTP_USER, PreferencePage.DEFAULT_FTP_USER);
		String pwd = ConfigServiceHolder.getGlobal(PreferencePage.FTP_PWD, PreferencePage.DEFAULT_FTP_PWD);
		String downloadDir =
			UtilFile.getCorrectPath(ConfigServiceHolder.getGlobal(PreferencePage.DL_DIR,
				PreferencePage.DEFAULT_DL_DIR));
		
		FtpServer ftp = new FtpServer();
		try {
			List<String> hl7FileList = new Vector<String>();
			try {
				ftp.openConnection(ftpHost, user, pwd);
				ftp.addSemaphore(downloadDir, PRAXIS_SEMAPHORE, TEAMW_SEMAPHORE);
				
				String[] filenameList = ftp.listNames();
				log.log("Verbindung mit Labor " + MY_LAB //$NON-NLS-1$
					+ " erfolgreich. " + filenameList.length //$NON-NLS-1$
					+ " Dateien gefunden.", Log.INFOS); //$NON-NLS-1$
				for (String filename : filenameList) {
					if (filename.toUpperCase().endsWith("HL7") //$NON-NLS-1$
						|| filename.toUpperCase().endsWith("PDF")) { //$NON-NLS-1$
						ftp.downloadFile(filename, downloadDir + filename);
						log.log("Datei <" + filename + "> downloaded.", //$NON-NLS-1$ //$NON-NLS-2$
							Log.INFOS);
						hl7FileList.add(filename);
						// Zeile um Files auf FTP zu löschen.
						ftp.deleteFile(filename);
					}
				}
			} finally {
				ftp.removeSemaphore();
				ftp.closeConnection();
			}
			
			String header =
				MessageFormat.format(
					ch.elexis.laborimport.teamw.Messages.Importer_import_header, //$NON-NLS-1$
					new Object[] {
						MY_LAB
					});
			String question =
				MessageFormat.format(
					ch.elexis.laborimport.teamw.Messages.Importer_import_message, //$NON-NLS-1$
					new Object[] {
						hl7FileList.size(), downloadDir
					});
			if (SWTHelper.askYesNo(header, question)) {
				for (String filename : hl7FileList) {
					importFile(downloadDir + filename);
					log.log("Datei <" + filename + "> verarbeitet.", Log.INFOS); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		} catch (IOException e) {
			result = new Result<String>(SEVERITY.ERROR, 1, e.getMessage(), MY_LAB, true);
			ResultAdapter.displayResult(result,
				ch.elexis.laborimport.teamw.Messages.Importer_error_import); //$NON-NLS-1$
		} catch (FtpSemaException e) {
			result = new Result<String>(SEVERITY.WARNING, 1, e.getMessage(), MY_LAB, true);
			ResultAdapter.displayResult(result,
				ch.elexis.laborimport.teamw.Messages.Importer_error_import); //$NON-NLS-1$
		}
		
		return result;
	}
	
	private Result<?> importDirectBatch(){
		Result<String> result =
			new Result<String>(ch.elexis.laborimport.teamw.Messages.Importer_ok); //$NON-NLS-1$
		
		String batchFile =
			UtilFile.getCorrectPath(ConfigServiceHolder.getGlobal(PreferencePage.BATCH_DATEI, "")); //$NON-NLS-1$
		String downloadDir =
			UtilFile.getCorrectPath(ConfigServiceHolder.getGlobal(PreferencePage.DL_DIR,
				PreferencePage.DEFAULT_DL_DIR));
		
		if (batchFile == null || batchFile.length() == 0) {
			return new Result<String>(SEVERITY.ERROR, 1,
				ch.elexis.laborimport.teamw.Messages.Importer_leereBatchdatei_error, //$NON-NLS-1$
				MY_LAB, true);
		}
		
		try {
			Process process = Runtime.getRuntime().exec(batchFile);
			int exitValue = -1;
			try {
				exitValue = process.waitFor();
			} catch (InterruptedException e) {
				log.log(e.getMessage(), Log.INFOS);
			}
			if (exitValue != 0) {
				return new Result<String>(
					SEVERITY.ERROR,
					1,
					ch.elexis.laborimport.teamw.Messages.Importer_batchFehler_error + process.exitValue(), //$NON-NLS-1$
					MY_LAB, true);
			}
			
			List<String> hl7FileList = new Vector<String>();
			File ddDir = new File(downloadDir);
			
			String[] filenameList = ddDir.list();
			for (String filename : filenameList) {
				if (filename.toUpperCase().endsWith("HL7")) { //$NON-NLS-1$
					log.log("Datei <" + filename + "> downloaded.", //$NON-NLS-1$ //$NON-NLS-2$
						Log.INFOS);
					hl7FileList.add(filename);
				}
			}
			
			String header =
				MessageFormat.format(
					ch.elexis.laborimport.teamw.Messages.Importer_import_header, //$NON-NLS-1$
					new Object[] {
						MY_LAB
					});
			String question =
				MessageFormat.format(
					ch.elexis.laborimport.teamw.Messages.Importer_import_message, //$NON-NLS-1$
					new Object[] {
						hl7FileList.size(), downloadDir
					});
			if (SWTHelper.askYesNo(header, question)) {
				for (String filename : hl7FileList) {
					importFile(downloadDir + filename);
					log.log("Datei <" + filename + "> verarbeitet.", Log.INFOS); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		} catch (IOException e) {
			result = new Result<String>(SEVERITY.ERROR, 1, e.getMessage(), MY_LAB, true);
			ResultAdapter.displayResult(result,
				ch.elexis.laborimport.teamw.Messages.Importer_error_import); //$NON-NLS-1$
		}
		
		return result;
	}
	
	@Override
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		int type;
		try {
			String sType = results[0];
			type = Integer.parseInt(sType);
		} catch (NumberFormatException ex) {
			type = FILE;
		}
		
		if ((type != FILE) && (type != DIRECT)) {
			type = FILE;
		}
		
		if (type == FILE) {
			String filename = results[1];
			return ResultAdapter.getResultAsStatus(importFile(filename));
		} else {
			return ResultAdapter.getResultAsStatus(importDirect());
		}
	}
	
	@Override
	public String getDescription(){
		return ch.elexis.laborimport.teamw.Messages.Importer_title_description; //$NON-NLS-1$
	}
	
	@Override
	public String getTitle(){
		return ch.elexis.laborimport.teamw.Messages.Importer_lab + MY_LAB; //$NON-NLS-1$
	}
	
	String getBasePath(){
		try {
			URL url = Platform.getBundle(PLUGIN_ID).getEntry("/"); //$NON-NLS-1$
			url = FileLocator.toFileURL(url);
			String bundleLocation = url.getPath();
			File file = new File(bundleLocation);
			bundleLocation = file.getAbsolutePath();
			return bundleLocation;
		} catch (Throwable throwable) {
			return null;
		}
	}
	
	/**
	 * An importer that lets the user select a file to import or directly import the data from the
	 * lab. The chosen type (file or direct import) is stored in results[0] (FILE or DIRECT). If
	 * FILE is chosen, the file path is stored in results[1].
	 * 
	 * @author gerry, danlutz
	 * 
	 */
	private class LabImporter extends Composite {
		private final Button bFile;
		private final Button bDirect;
		
		private final Text tFilename;
		
		public LabImporter(final Composite parent, final ImporterPage home){
			super(parent, SWT.BORDER);
			setLayout(new GridLayout(3, false));
			
			bFile = new Button(this, SWT.RADIO);
			bFile.setText(ch.elexis.laborimport.teamw.Messages.Importer_label_importFile); //$NON-NLS-1$
			bFile.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
			
			Label lFile = new Label(this, SWT.NONE);
			lFile.setText("    " + Messages.ImporterPage_file); //$NON-NLS-1$ //$NON-NLS-2$
			GridData gd = SWTHelper.getFillGridData(1, false, 1, false);
			gd.horizontalAlignment = GridData.END;
			gd.widthHint = lFile.getSize().x + 20;
			
			tFilename = new Text(this, SWT.BORDER);
			tFilename.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			
			Button bBrowse = new Button(this, SWT.PUSH);
			bBrowse.setText(Messages.ImporterPage_browse); //$NON-NLS-1$
			
			String batchOrFtp =
				ConfigServiceHolder.getGlobal(PreferencePage.BATCH_OR_FTP, PreferencePage.FTP);
			String direktHerkunft =
				ch.elexis.laborimport.teamw.Messages.Importer_ftp_label; //$NON-NLS-1$
			if (PreferencePage.BATCH.equals(batchOrFtp)) {
				direktHerkunft =
					ch.elexis.laborimport.teamw.Messages.Importer_batch_label; //$NON-NLS-1$
			}
			bDirect = new Button(this, SWT.RADIO);
			bDirect.setText(ch.elexis.laborimport.teamw.Messages
				.Importer_label_importDirect + " (" + direktHerkunft + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			bDirect.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
			
			int type = CoreHub.localCfg.get("ImporterPage/" + home.getTitle() + "/type", FILE); //$NON-NLS-1$ //$NON-NLS-2$
			
			home.results = new String[2];
			
			if (type == FILE) {
				bFile.setSelection(true);
				bDirect.setSelection(false);
				
				String filename =
					CoreHub.localCfg.get("ImporterPage/" + home.getTitle() + "/filename", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				tFilename.setText(filename);
				
				home.results[0] = new Integer(FILE).toString();
				home.results[1] = filename;
			} else {
				bFile.setSelection(false);
				bDirect.setSelection(true);
				
				tFilename.setText(""); //$NON-NLS-1$
				
				home.results[0] = new Integer(DIRECT).toString();
				home.results[1] = ""; //$NON-NLS-1$
			}
			
			SelectionAdapter sa = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					Button button = (Button) e.getSource();
					
					// only handle selection == true
					if (!button.getSelection()) {
						return;
					}
					
					int type = FILE;
					
					if (button == bFile) {
						type = FILE;
					} else if (button == bDirect) {
						type = DIRECT;
					}
					
					if (type == FILE) {
						bFile.setSelection(true);
						bDirect.setSelection(false);
						
						String filename = tFilename.getText();
						
						home.results[0] = new Integer(FILE).toString();
						home.results[1] = filename;
						
						CoreHub.localCfg.set("ImporterPage/" + home.getTitle() + "/type", FILE); //$NON-NLS-1$ //$NON-NLS-2$
						CoreHub.localCfg.set(
							"ImporterPage/" + home.getTitle() + "/filename", filename); //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						bFile.setSelection(false);
						bDirect.setSelection(true);
						
						tFilename.setText(""); //$NON-NLS-1$
						
						home.results[0] = new Integer(DIRECT).toString();
						home.results[1] = ""; //$NON-NLS-1$
						
						CoreHub.localCfg.set("ImporterPage/" + home.getTitle() + "/type", DIRECT); //$NON-NLS-1$ //$NON-NLS-2$
						CoreHub.localCfg.set("ImporterPage/" + home.getTitle() + "/filename", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			};
			
			bFile.addSelectionListener(sa);
			bDirect.addSelectionListener(sa);
			
			bBrowse.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e){
					bFile.setSelection(true);
					bDirect.setSelection(false);
					
					FileDialog fdl = new FileDialog(parent.getShell(), SWT.OPEN);
					fdl.setFilterExtensions(new String[] {
						"*"}); //$NON-NLS-1$
					fdl.setFilterNames(new String[] {
						Messages.ImporterPage_allFiles}); //$NON-NLS-1$
					String filename = fdl.open();
					if (filename == null) {
						filename = ""; //$NON-NLS-1$
					}
					
					tFilename.setText(filename);
					home.results[0] = new Integer(FILE).toString();
					home.results[1] = filename;
					
					CoreHub.localCfg.set("ImporterPage/" + home.getTitle() + "/type", FILE); //$NON-NLS-1$ //$NON-NLS-2$
					CoreHub.localCfg.set("ImporterPage/" + home.getTitle() + "/filename", filename); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
			});
		}
	}
}
