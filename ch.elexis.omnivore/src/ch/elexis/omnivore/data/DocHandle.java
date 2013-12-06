/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.omnivore.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;

import java.security.SecureRandom;		//Omnivore_js: To generate random string for unique_temp_ID
import java.math.BigInteger;						//Omnivore_js: To generate random string for unique_temp_ID

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.program.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.omnivore.views.FileImportDialog;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

import ch.elexis.omnivore.preferences.PreferencePage;



public class DocHandle extends PersistentObject implements IOpaqueDocument {
	public static final String TABLENAME = "CH_ELEXIS_OMNIVORE_DATA"; //$NON-NLS-1$
	public static final String DBVERSION = "1.2.1"; //$NON-NLS-1$
	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$ //$NON-NLS-2$
		"ID				VARCHAR(25) primary key," + //$NON-NLS-1$
		"lastupdate     BIGINT," + //$NON-NLS-1$
		"deleted        CHAR(1) default '0'," + //$NON-NLS-1$
		"PatID			VARCHAR(25)," + //$NON-NLS-1$
		"Datum			CHAR(8)," + //$NON-NLS-1$
		"Title 			VARCHAR(80)," + //$NON-NLS-1$
		"Mimetype		VARCHAR(255)," + //$NON-NLS-1$
		"Keywords		VARCHAR(255)," + //$NON-NLS-1$
		"Path			VARCHAR(255)," + //$NON-NLS-1$
		"Doc			BLOB);" + //$NON-NLS-1$
		"CREATE INDEX OMN1 ON " + TABLENAME + " (PatID);" + //$NON-NLS-1$ //$NON-NLS-2$
		"CREATE INDEX OMN2 ON " + TABLENAME + " (Keywords);" + //$NON-NLS-1$ //$NON-NLS-2$
		"INSERT INTO " + TABLENAME + " (ID, TITLE) VALUES ('1','" + DBVERSION + "');"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	public static final String upd120 =
		"ALTER TABLE " + TABLENAME + " MODIFY Mimetype VARCHAR(255);" + //$NON-NLS-1$ //$NON-NLS-2$
			"ALTER TABLE " + TABLENAME + " MODIFY Keywords VARCHAR(255);" + //$NON-NLS-1$ //$NON-NLS-2$
			"ALTER TABLE " + TABLENAME + " Modify Path VARCHAR(255);"; //$NON-NLS-1$ //$NON-NLS-2$
	
	private static final String upd121 = "ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;"; //$NON-NLS-1$ //$NON-NLS-2$
	private static Logger log = LoggerFactory.getLogger("ch.elexis.omnivore.DocHandle"); //$NON-NLS-1$
	
	static {
		addMapping(TABLENAME, "PatID", "Datum=S:D:Datum", "Titel=Title", "Keywords", "Path", "Doc", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			"Mimetype"); //$NON-NLS-1$
		DocHandle start = load("1"); //$NON-NLS-1$
		if (start == null) {
			init();
		} else {
			VersionInfo vi = new VersionInfo(start.get("Titel")); //$NON-NLS-1$
			if (vi.isOlder(DBVERSION)) {
				if (vi.isOlder("1.1.0")) { //$NON-NLS-1$
					getConnection().exec(
						"ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';"); //$NON-NLS-1$ //$NON-NLS-2$
					start.set("Titel", DBVERSION); //$NON-NLS-1$
				}
				if (vi.isOlder("1.2.0")) { //$NON-NLS-1$
					createOrModifyTable(upd120);
					start.set("Titel", DBVERSION); //$NON-NLS-1$
				}
				if (vi.isOlder("1.2.1")) { //$NON-NLS-1$
					createOrModifyTable(upd121);
					start.set("Titel", DBVERSION); //$NON-NLS-1$
				}
				
			}
		}
	}
	
	public DocHandle(byte[] doc, Patient pat, String title, String mime, String keyw){
		if ((doc == null) || (doc.length == 0)) {
			SWTHelper.showError(Messages.DocHandle_docErrorCaption,
				Messages.DocHandle_docErrorMessage);
			return;
		}
		create(null);
		try {
			setBinary("Doc", doc); //$NON-NLS-1$
			set(new String[] {
				"PatID", "Datum", "Titel", "Keywords", "Mimetype" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			}, pat.getId(), new TimeTool().toString(TimeTool.DATE_GER), title, keyw, mime);
		} catch (PersistenceException pe) {
			log.error(Messages.DocHandle_dataNotWritten + "; " + pe.getMessage());
		}
	}
	
	/**
	 * Tabelle neu erstellen
	 */
	public static void init(){
		createOrModifyTable(createDB);
	}
	
	public static DocHandle load(String id){
		DocHandle ret = new DocHandle(id);
		if (ret.exists()) {
			return ret;
		}
		return null;
	}
	
	@Override
	public String getLabel(){
		StringBuilder sb = new StringBuilder();
		sb.append(get("Datum")).append(" ").append(get("Titel")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return sb.toString();
	}
	
	@Override
	public boolean delete(){
		return super.delete();
	}
	
	public byte[] getContentsAsBytes(){
		byte[] ret = getBinary("Doc"); //$NON-NLS-1$
		return ret;
	}
	
	public InputStream getContentsAsStream(){
		byte[] bytes = getContentsAsBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		return bais;
	}
	
	public void execute(){
		try {
			String ext = ""; //$NON-NLS-1$
			String typname = get("Mimetype"); //$NON-NLS-1$
			int r = typname.lastIndexOf('.');
			if (r == -1) {
				typname = get("Titel"); //$NON-NLS-1$
				r = typname.lastIndexOf('.');
			}
			
			if (r != -1) {
				ext = typname.substring(r + 1);
			}
			
  			//Make the temporary filename configurable
			StringBuffer configured_temp_filename=new StringBuffer();
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("constant1",""));
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("PID",getPatient().getKuerzel()));	//getPatient() liefert in etwa: ch.elexis.com@1234567; getPatient().getId() eine DB-ID; getPatient().getKuerzel() die Patientennummer.
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("fn",getPatient().getName()));
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("gn",getPatient().getVorname()));
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("dob",getPatient().getGeburtsdatum()));
			log.debug("configured_temp_filename="+configured_temp_filename.toString());

			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("dt",getTitle()));				//not more than 80 characters, laut javadoc
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("dk",getKeywords()));
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			//Da könnten auch noch Felder wie die Document Create Time etc. rein - siehe auch unten, die Methoden getPatient() etc.
			
			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("dguid",getGUID()));
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			
			//N.B.: We may NOT REALLY assume for sure that another filename, derived from a createTempFile() result, where the random portion would be moved forward in the name, may also be guaranteed unique!
			//So *if* we should use createTempFile() to obtain such a filename, we should put constant2 away from configured_temp_filename and put it in the portion provided with "ext", if a unique_temp_id was requested.
			//And, we should probably not cut down the size of that portion, so it would be best to do nothing for that but offer a checkbox.
			
			//Es muss aber auch gar nicht mal unique sein - wenn die Datei schon existiert UND von einem anderen Prozess, z.B. Word, mit r/w geöffnet ist, erscheint ein sauberer Dialog mit einer Fehlermeldung. Wenn sie nicht benutzt wird, kann sie überschrieben werden.
			
			//Der Fall, dass hier auf einem Rechner / von einem User bei dem aus Daten erzeugten Filenamen zwei unterschiedliche Inhalte mit gleichem Namen im gleichen Tempdir gleichzeitig nur r/o geöffnet werden und einander in die Quere kommen, dürfte unwahrscheinlich sein.
			//Wie wohl... vielleicht doch nicht. Wenn da jemand beim selben Patienten den Titel 2x einstellt nach: "Bericht Dr. Müller", und das dann den Filenamen liefert, ist wirklich alles gleich.
			//So we should ... possibly really add some random portion; or use any other property of the file in that filename (recommendation: e.g. like in AnyQuest Server :-)  )
			
			//Ganz notfalls naoch ein Feld mit der Uhrzeit machen... oder die Temp-ID je nach eingestellten num_digits aus den clockticks speisen. Und das File mit try createn, notfalls wiederholen mit anderem clocktick - dann ist das so gut wie ein createTempFile().
			//For now, I compute my own random portion - by creating a random BigInteger with a sufficient number of bits to represent  PreferencePage.nOmnivore_jsPREF_cotf_element_digits_max decimal digits.
			//And I accept the low chance of getting an existing random part, i.e. I don't check the file is already there.
			
			SecureRandom random = new SecureRandom();
			int  needed_bits = (int) Math.round(Math.ceil(Math.log(PreferencePage.nOmnivore_jsPREF_cotf_element_digits_max)/Math.log(2)));
			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("random",new BigInteger(needed_bits , random).toString() ));
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			
			configured_temp_filename.append(PreferencePage.getOmnivore_jsTemp_Filename_Element("constant2",""));
			log.debug("configured_temp_filename="+configured_temp_filename.toString());
			
			File temp;
			if (configured_temp_filename.length()>0) {
				//The following file will have a unique variable part after the configured_temp_filename_and before the .ext,
				//but will be located in the temporary directory.
				File uniquetemp = File.createTempFile(configured_temp_filename.toString()+"_","."+ext); //$NON-NLS-1$ //$NON-NLS-2$
				String temp_pathname=uniquetemp.getParent();
				uniquetemp.delete(); 
				
				//remove the _unique variable part from the temporary filename and create a new file in the same directory as the previously automatically created unique temp file
				log.debug(temp_pathname);
				log.debug(configured_temp_filename+"."+ext);
				temp = new File(temp_pathname,configured_temp_filename+"."+ext);
				temp.createNewFile();
			}
			else {
				//if special rules for the filename are not configured, then generate it simply as before Omnivore_js Version 1.4.4
				temp = File.createTempFile("omni_", "_vore." + ext); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			temp.deleteOnExit();
			byte[] b = getBinary("Doc"); //$NON-NLS-1$
			if (b == null) {
				SWTHelper.showError(Messages.DocHandle_readErrorCaption,
					Messages.DocHandle_readErrorMessage);
				return;
			}
			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(b);
			fos.close();
			Program proggie = Program.findProgram(ext);
			if (proggie != null) {
				proggie.execute(temp.getAbsolutePath());
			} else {
				if (Program.launch(temp.getAbsolutePath()) == false) {
					Runtime.getRuntime().exec(temp.getAbsolutePath());
				}
				
			}
			
		} catch (Exception ex) {
			ExHandler.handle(ex);
			SWTHelper.showError(Messages.DocHandle_execError, ex.getMessage());
		}
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	protected DocHandle(String id){
		super(id);
	}
	
	protected DocHandle(){}
	
	public static void assimilate(String f){
		Patient act = ElexisEventDispatcher.getSelectedPatient();
		if (act == null) {
			SWTHelper.showError(Messages.DocHandle_noPatientSelected,
				Messages.DocHandle_pleaseSelectPatient);
			return;
		}
		File file = new File(f);
		if (!file.canRead()) {
			SWTHelper.showError(Messages.DocHandle_cantReadCaption,
				MessageFormat.format(Messages.DocHandle_cantReadMessage, f));
			return;
		}

		//FIXME: Warum ist es eigentlich so, dass nur 80 Zeichen funktionieren? Notfalls könnte ja auch Omnivore das Umbenennen veranlassen, und den Namen einfach (oder interaktiv) kürzen, statt eine Fehlermeldung auszugeben...
		
		//From Omnivore version 1.4.2 to Omnivore_js version 1.4.3,
		//I changed the fixed limit from 255 for the last part of the filename to a configurable one with default 80 chars. 
		//Linux and MacOS may be able to handle longer filenames,
		//but we observed and verified that Windows 7 64-bit would not import files with names longer than 80 chars.
		//Also, put the filename length check *before* displaying the file import dialog.
		//Otherwise, users would have to type in a bunch of text first, and learn only afterwards, that that would be discarded.
	    Integer maxOmnivoreFilenameLength=ch.elexis.omnivore.preferences.PreferencePage.getOmnivore_jsMax_Filename_Length();

	    String nam = file.getName();
		if (nam.length() > maxOmnivoreFilenameLength) {																											//The checked limit is now configurable.
			SWTHelper.showError(Messages.DocHandle_importErrorCaption,
				MessageFormat.format(Messages.DocHandle_importErrorMessage, maxOmnivoreFilenameLength));	//The error message is also dynamically generated.
			return;
		}		
		
		FileImportDialog fid = new FileImportDialog(file.getName());
		if (fid.open() == Dialog.OK) {
			try {
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int in;
				while ((in = bis.read()) != -1) {
					baos.write(in);
				}
				bis.close();
				baos.close();

				//FIXME: The original file name should be preserved in a separate field when the file content is imported into the database.			
				new DocHandle(baos.toByteArray(), act, fid.title.trim(), file.getName(), fid.keywords.trim());	//Added trim() to title and keywords, to avoid unnecessary extra lines in the omnivore content listing.
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(Messages.DocHandle_importErrorCaption,
					Messages.DocHandle_importErrorMessage2);
				//Wenn das Importieren einen Fehler wirft, nacher die Datei nicht automatisch wegarchivieren. Deshalb hier return eingefügt.
				return;
			}
			
			//Now, process all defined rules for automatic archiving of imported documents, as configured in omnivore_js preferences.

			//Anything will be done *only* when SrcPattern and DestDir are both at least defined strings.
			//A rule, where both SrcPattern and DestDir are empty strings, will be ignored and have no effect. Especially, it will not stop further rules from being evaluated.

			//A (usually final) rule that is matched by all files may contain either strings like ".", oder be empty.
			
			//If DestDir ist not an empty string, the program will test:
			//	if it is a file, it will be overwritten by the source file (hopefully...), except if it has the same name as the source file, where nothing will happen.
			//	if it is a directory, the program will try to put the source file into it - but before doing so, will ensure that neither a dir nor a file of the same name are already there.
			//To automatically delete source files after importing, you may either specify one fixed filename for all targets (they will overwrite each other), or /dev/null or c:\NUL or the like.
			
			//If you specify an empty string for DestDir, but a non-empty String for the SrcPattern, that will protect matching files from being automatically archived.
			
			//Every file will only be handled by the first matching rule.
			
			try	{
				for (Integer i=0;i<ch.elexis.omnivore.preferences.PreferencePage.getOmnivore_jsnRulesForAutoArchiving();i++) {
					String SrcPattern =ch.elexis.omnivore.preferences.PreferencePage.getOmnivore_jsRuleForAutoArchivingSrcPattern(i);
					String DestDir = ch.elexis.omnivore.preferences.PreferencePage.getOmnivore_jsRuleForAutoArchivingDestDir(i);
				
					if ((SrcPattern != null) && (DestDir != null) && ( (SrcPattern != "" || DestDir != ""))) {
						//Unter win für Dateien vom Explorer hineingezogen liefern getAbsolutePath und getAbsoluteFile dasselbe - jeweils laufwerk+path+dateiname.
						//getCanonicalXYZ liefert schon einen Fehler in Eclipse
						//getName liefert nur den Dateinamen ohne Laufwerk und/oder Pfad.
					
						log.debug("Automatic archiving found matching rule #"+(i+1)+" (1-based index):");	
						log.debug("file.getAbsolutePath(): "+file.getAbsolutePath());	
						log.debug("Pattern: "+SrcPattern);
						log.debug("DestDir: "+DestDir);
						
						if (file.getAbsolutePath().contains(SrcPattern)) {
							log.debug("SrcPattern found in file.getAbsolutePath()"+i);	
							
							if (DestDir == "") {
								log.debug("DestDir is empty. No more rules will be evaluated for this file. Returning.");	
								return;
							}	//An empty DestDir protects a matching file from being processed by any further rules.
							
							//renameTo is (a) platform dependent, and (b) needs a file, not just a string, as a target.
							//I.e. I must check whether the DestDir ist a directory, make a valid filename from it, etc.
							//So before I would do that, let me try move first, which javas doc says should be platform independent etc.
							//Well, move would truly operate on PathNames, rather than files, but still require some overhead.

							/* All this doesn't work, because the "browse" button in the settings returns a selected directory WITHOUT a trailing sepchar.
							 * Don't expect users to add that.
							 * Instead, I will check whether DestDir is already a directory, and add the filename, if yes. 
							//For now, I support TargetDirectory names only if they end with separatorChar; NOT with pathSeparatorChar (i.e.: ":" on Windows).
							//First, it is not so probable that anybody wants to put the result to c:\ or the like.
							//Second, they can wirte c:\ as DestDir, no need to especially support c: as well.
							//Third, formally, c: might also mean: use the "current working directory" (whatever that may be) for that drive. In DOS shell, it is (or may be?) like that.
							//Fourth, it would require more code lines, including checking which system we're running on. Not now.
							
							File NewFile;
							if (DestDir.endsWith(file.separatorChar+""))	{		//I use +"" to get the desired String argument for endsWith
							//If DestDir supplies a directory name, then the renameTo destination file name is that + old file name without the old path: 
									NewFile = new File(DestDir+file.getName());
							} else {
							//If DestDir supplies a single file (like /dev/nul or \\anyserver\anywhere\the-file-that-was-last-imported-by-omnivore.dat) then use that directly.
								NewFile = new File(DestDir);
							}
							*/ 
													
							//So, first I use DestDir as supplied.
							//If that's a simple file already (like /dev/null), I just use it as rename destination.
							//If that's a directory however, I add the name of the source file to it.
							//And just to make sure: If that is *STILL* a directory - that may not occur often, but is still possible -
							//the user probably selected the wrong target, or tried to import a file that should not be stored in the target folder because a directory of the same name is already there.
							//In that case, we don't rename, in order to protect that directory from being overwritten.
							//Even if DestDir should end with a separatorChar, that should not hurt, because sequences of separatorChar in a filename should probably be treated as a single one.
							File NewFile = new File (DestDir);
							if (NewFile.isDirectory() ) {
								log.debug("DestDir is a directory. Adding file.getName()...");	
								NewFile = new File(DestDir+file.separatorChar+file.getName());
							}
						
							//First, make sure that any destination name is NOT currently a directory.
							//If we copy or move a simple file over a directory name, on some systems, that might lose the original directory with its previous content completely!
							//If the target exists, java on windows would currently just not carry out the renameTo(). But the user would not be informed.
							//So I'll provide a separate error message for that case.
							if (NewFile.isDirectory()) {
								log.debug("NewFile.isDirectory==true; renaming not attempted");	
								SWTHelper.showError(Messages.DocHandle_jsMoveErrorCaption,MessageFormat.format(Messages.DocHandle_jsMoveErrorDestIsDir,DestDir,file.getName()));
							} else {					
								if (NewFile.isFile()) {
									log.debug("NewFile.isFile==true; renaming not attempted");	
									SWTHelper.showError(Messages.DocHandle_jsMoveErrorCaption,MessageFormat.format(Messages.DocHandle_jsMoveErrorDestIsFile,DestDir,file.getName()));
								} else {
									log.debug("renaming incoming file to: "+NewFile.getAbsolutePath());
								
									if (file.renameTo(NewFile)) {
										log.debug("renaming ok");	
										//do nothing, everything worked out fine
									} else {
										log.debug("renaming attempted, but returned false.");
										log.debug("However, I may probably have observed this after successful moves?! So I won't show an error dialog here. js");	
										log.debug("So I won't show an error dialog here; if a real exception occured, that would suffice to trigger it.");	
										//SWTHelper.showError(Messages.DocHandle_jsMoveErrorCaption,Messages.DocHandle_jsMoveError);
									}
								}
							}
							
							//Java's concept of files and their filenames differs substantially from filenames and file handles of other environments.
							//I hope I can just re-use the NewFile object on something different if the first attempt does not return what I wanted,
							//and after the "renaming" has taken place - no matter how that is actually carried out on a given platform -
							//that all of the involved temporary constructs are reliably removed from memory again.
							//Well, after all, Java promises just to take care of that...

						break;	//SrcPattern matched. Only one the first matching rule shall be processed per file.
						}
					} //if SrcPattern, DestPattern <> null & either one <>""
				} //for i... 
			} catch (Throwable throwable) {
					ExHandler.handle(throwable);
					SWTHelper.showError(Messages.DocHandle_jsMoveErrorCaption,
						Messages.DocHandle_jsMoveError);
			}
		
		}
		
	}
	
	@Override
	public String getTitle(){
		return checkNull(get("Titel")); //$NON-NLS-1$
	}
	
	@Override
	public String getMimeType(){
		return checkNull(get("Mimetype")); //$NON-NLS-1$
	}
	
	@Override
	public String getKeywords(){
		return checkNull(get("Keywords")); //$NON-NLS-1$
	}
	
	@Override
	public String getCategory(){
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public String getCreationDate(){
		return get("Datum"); //$NON-NLS-1$
	}
	
	@Override
	public Patient getPatient(){
		return Patient.load(get("PatID")); //$NON-NLS-1$
	}
	
	@Override
	public String getGUID(){
		return getId();
	}
	
}
