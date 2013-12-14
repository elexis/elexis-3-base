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
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.security.SecureRandom; //Omnivore: To generate random string for unique_temp_ID
import java.math.BigInteger; //Omnivore: To generate random string for unique_temp_ID

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.omnivore.views.FileImportDialog;
import ch.elexis.omnivore.views.Preferences;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class DocHandle extends PersistentObject implements IOpaqueDocument {
	public static final String FLD_CAT = "Cat"; //$NON-NLS-1$
	public static final String FLD_TITLE = "Titel"; //$NON-NLS-1$
	public static final String FLD_MIMETYPE = "Mimetype"; //$NON-NLS-1$
	public static final String FLD_DOC = "Doc"; //$NON-NLS-1$
	public static final String FLD_PATH = "Path"; //$NON-NLS-1$
	public static final String FLD_KEYWORDS = "Keywords"; //$NON-NLS-1$
	public static final String FLD_PATID = "PatID"; //$NON-NLS-1$
	public static final String TABLENAME = "CH_ELEXIS_OMNIVORE_DATA"; //$NON-NLS-1$
	public static final String DBVERSION = "2.0.3"; //$NON-NLS-1$
	public static final String DEFAULT_CATEGORY = "default"; //$NON-NLS-1$
	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" //$NON-NLS-1$ //$NON-NLS-2$
		+ "ID				VARCHAR(25) primary key," //$NON-NLS-1$
		+ "deleted        CHAR(1) default '0'," + "lastupdate BIGINT," + "PatID			VARCHAR(25)," //$NON-NLS-1$ //$NON-NLS-2$
		+ "Datum			CHAR(8)," + "Category		VARCHAR(80) default null," //$NON-NLS-1$ //$NON-NLS-2$
		+ "Title 			VARCHAR(255)," + "Mimetype		VARCHAR(255)," //$NON-NLS-1$ //$NON-NLS-2$
		+ "Keywords		VARCHAR(255)," + "Path			VARCHAR(255)," //$NON-NLS-1$ //$NON-NLS-2$
		+ "Doc			BLOB);" + "CREATE INDEX OMN1 ON " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " (PatID);" + "CREATE INDEX OMN2 ON " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " (Keywords);" + "CREATE INDEX OMN3 ON " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " (Category);" + "CREATE INDEX OMN5 ON " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " (deleted);" + "CREATE INDEX OMN6 ON " + TABLENAME + " (Title);" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		+ "INSERT INTO " + TABLENAME + " (ID, TITLE) VALUES ('1','" //$NON-NLS-1$ //$NON-NLS-2$
		+ DBVERSION + "');"; //$NON-NLS-1$
	
	public static final String upd120 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " MODIFY Mimetype VARCHAR(255);" + "ALTER TABLE " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " MODIFY Keywords VARCHAR(255);" + "ALTER TABLE " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " Modify Path VARCHAR(255);"; //$NON-NLS-1$
	//private static final String upd121 = "ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;"; //$NON-NLS-1$ //$NON-NLS-2$
	
	public static final String upd200 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " ADD Category VARCHAR(80) default null;" //$NON-NLS-1$
		+ "CREATE INDEX OMN3 ON " + TABLENAME + " (Category);" //$NON-NLS-1$ //$NON-NLS-2$
		+ "ALTER TABLE " + TABLENAME + " MODIFY Title VARCHAR(255);"; //$NON-NLS-1$ //$NON-NLS-2$
	
	public static final String upd201 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " ADD lastupdate BIGINT;"; //$NON-NLS-1$
	
	public static final String upd202 = "CREATE INDEX OMN4 ON " + TABLENAME //$NON-NLS-1$
		+ " (Mimetype);"; //$NON-NLS-1$
	
	public static final String upd203 = "CREATE INDEX OMN5 ON " + TABLENAME //$NON-NLS-1$
		+ " (deleted);" + "CREATE INDEX OMN6 ON " + TABLENAME + " (Title);"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	private static final String CATEGORY_MIMETYPE = "text/category"; //$NON-NLS-1$
	private static Logger log = LoggerFactory.getLogger("ch.elexis.omnivore.DocHandle"); //$NON-NLS-1$
	
	static {
		addMapping(TABLENAME, FLD_PATID, "Cat=Category", DATE_COMPOUND, //$NON-NLS-1$
			"Titel=Title", FLD_KEYWORDS, FLD_PATH, FLD_DOC, FLD_MIMETYPE); //$NON-NLS-1$
		DocHandle start = load("1"); //$NON-NLS-1$
		if (start == null) {
			init();
		} else {
			VersionInfo vi = new VersionInfo(start.get(FLD_TITLE));
			if (vi.isOlder(DBVERSION)) {
				if (vi.isOlder("1.1.0")) { //$NON-NLS-1$
					try {
						getConnection().exec("ALTER TABLE " + TABLENAME //$NON-NLS-1$
							+ " ADD deleted CHAR(1) default '0';"); //$NON-NLS-1$
					} finally {
						System.out.println("Finally");
						// do nothing. Field is probably already present;
					}
					start.set(FLD_TITLE, DBVERSION);
				} else if (vi.isOlder("1.2.0")) { //$NON-NLS-1$
					createOrModifyTable(upd120);
					start.set(FLD_TITLE, DBVERSION);
				} else if (vi.isOlder("2.0.0")) { //$NON-NLS-1$
					createOrModifyTable(upd200);
					start.set(FLD_TITLE, DBVERSION);
				} else if (vi.isOlder("2.0.1")) { //$NON-NLS-1$
					createOrModifyTable(upd201);
					start.set(FLD_TITLE, DBVERSION);
				} else if (vi.isOlder("2.0.2")) { //$NON-NLS-1$
					createOrModifyTable(upd202);
					start.set(FLD_TITLE, DBVERSION);
				} else {
					MessageDialog.openError(UiDesk.getTopShell(),
						Messages.DocHandle_versionConflictCaption,
						Messages.DocHandle_versionConflictText);
				}
				
			}
		}
	}
	
	public File getStorageFile(boolean force) {
		if (force || CoreHub.localCfg.get(Preferences.STOREFS, false)) {
			String pathname = CoreHub.localCfg.get(Preferences.BASEPATH, null);
			if (pathname != null) {
				File dir = new File(pathname);
				if (dir.isDirectory()) {
					Patient pat = Patient.load(get(FLD_PATID));
					File subdir = new File(dir, pat.getPatCode());
					if (!subdir.exists()) {
						subdir.mkdir();
					}
					File file = new File(subdir, getId() + "." //$NON-NLS-1$
							+ FileTool.getExtension(get(FLD_MIMETYPE)));
					return file;
				}
			}
			configError();
		}
		return null;
	}

    /** 
     * Creates a new omnivore document.
     * Adds it to the defaultCategory if doc does not provide one
     *
     * @param doc document
	 */
	public DocHandle(IOpaqueDocument doc) throws ElexisException {
		create(doc.getGUID());

		String category = doc.getCategory();
		if (category == null || category.length() < 1) {
			category = DocHandle.getDefaultCategory().getCategoryName();
		} else {
			DocHandle.ensureCategoryAvailability(category);
		}
		set(new String[] { FLD_CAT, FLD_PATID, FLD_DATE, FLD_TITLE,
				FLD_KEYWORDS, FLD_MIMETYPE }, category, doc.getPatient()
				.getId(), doc.getCreationDate(), doc.getTitle(),
				doc.getKeywords(), doc.getMimeType());
		store(doc.getContentsAsBytes());

	}

    /** 
     * Creates a new omnivore document.
     *
     * @param category Category. If null, adds it to the defaultCategory
     * @param doc the document to add
     * @param pat document belongs to this patient
     * @param title 
     * @param mime MIME-type, e.g. pdf
     * @param keyw keywords
	 */
	public DocHandle(String category, byte[] doc, Patient pat, String title,
			String mime, String keyw) {
		if ((doc == null) || (doc.length == 0)) {
			SWTHelper.showError(Messages.DocHandle_documentErrorCaption,
					Messages.DocHandle_documentErrorText1);
			return;
		}
		create(null);

		if (category == null || category.length() < 1) {
			category = DocHandle.getDefaultCategory().getCategoryName();
		} else {
			DocHandle.ensureCategoryAvailability(category);
		}
		set(new String[] { FLD_CAT, FLD_PATID, FLD_DATE, FLD_TITLE,
				FLD_KEYWORDS, FLD_MIMETYPE }, category, pat.getId(),
				new TimeTool().toString(TimeTool.DATE_GER), title, keyw, mime);
		store(doc);
	}

	private void store(byte[] doc) {
		File file = getStorageFile(false);
		if (file == null) {
			try {
				setBinary(FLD_DOC, doc);
			} catch (PersistenceException pe) {
				SWTHelper.showError(
						Messages.DocHandle_documentErrorCaption,
						Messages.DocHandle_documentErrorText2 + "; "
								+ pe.getMessage());
				delete();
			}
		} else {
			try {
				BufferedOutputStream bout = new BufferedOutputStream(
						new FileOutputStream(file));
				bout.write(doc);
				bout.close();
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(
						"write error", Messages.DocHandle_writeErrorHeading, //$NON-NLS-1$
						"Konnte die Datei " + file.getAbsolutePath()
								+ " nicht schreiben." + ex.getMessage());
				delete();
			}
		}
	}
	/**
	 * We need a default category as a fallback for invalid or not defined categories.
	 * 
	 * @return the default category in case no other category is defined
	 */
	public static DocHandle getDefaultCategory(){
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
		qbe.add(FLD_MIMETYPE, "=", CATEGORY_MIMETYPE); //$NON-NLS-1$
		qbe.add(FLD_TITLE, "=", DEFAULT_CATEGORY);
		List<DocHandle> qre = qbe.execute();
		if (qre.size() < 1) {
			addMainCategory(DEFAULT_CATEGORY);
			return DocHandle.getDefaultCategory();
		}
		return qre.get(0);
	}
	
	/**
	 * Ensure that a certain requested category is available in the system. If the category is
	 * already here, nothing happens, else it is created
	 * 
	 * @param categoryName
	 *            the respective category name
	 */
	public static void ensureCategoryAvailability(String categoryName){
		List<DocHandle> ldh = getMainCategories();
		boolean found = false;
		for (DocHandle dh : ldh) {
			if (dh.get(FLD_TITLE).equalsIgnoreCase(categoryName)
				|| dh.get(FLD_CAT).equalsIgnoreCase(categoryName)) {
				found = true;
				continue;
			}
		}
		if (found) {
			return;
		} else {
			DocHandle.addMainCategory(categoryName);
		}
	}
	
	public static List<String> getMainCategoryNames(){
		List<DocHandle> dox = getMainCategories();
		ArrayList<String> ret = new ArrayList<String>(dox.size());
		for (DocHandle doch : dox) {
			ret.add(doch.get(FLD_TITLE));
		}
		return ret;
	}
	
	public static List<DocHandle> getMainCategories(){
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
		qbe.add(FLD_MIMETYPE, "=", CATEGORY_MIMETYPE); //$NON-NLS-1$
		List<DocHandle> dox = qbe.execute();
		return dox;
	}
	
	public static void addMainCategory(String name){
		DocHandle dh = new DocHandle();
		dh.create(null);
		dh.set(new String[] {
			FLD_TITLE, FLD_CAT, FLD_MIMETYPE
		}, name, name, CATEGORY_MIMETYPE);
	}
	
	public static void renameCategory(String oldname, String newName){
		getConnection().exec(
			"update CH_ELEXIS_OMNIVORE_DATA set Category=" + JdbcLink.wrap(newName)
				+ " where Category= " + JdbcLink.wrap(oldname));
		getConnection().exec(
			"update CH_ELEXIS_OMNIVORE_DATA set Title=" + JdbcLink.wrap(newName) + " where Title="
				+ JdbcLink.wrap(oldname) + " and mimetype=" + JdbcLink.wrap("text/category"));
	}
	
	public static void removeCategory(String name, String destName){
		getConnection().exec(
			"update CH_ELEXIS_OMNIVORE_DATA set Category=" + JdbcLink.wrap(destName)
				+ " where Category= " + JdbcLink.wrap(name));
		getConnection().exec(
			"update CH_ELEXIS_OMNIVORE_DATA set deleted='1' where Title=" + JdbcLink.wrap(name)
				+ " AND mimetype=" + JdbcLink.wrap("text/category"));
		
	}
	
	public String getCategoryName(){
		return checkNull(get(FLD_CAT));
	}
	
	public boolean isCategory(){
		return get(FLD_MIMETYPE).equals(CATEGORY_MIMETYPE);
	}
	
	public DocHandle getCategoryDH(){
		String name = getCategoryName();
		if (!StringTool.isNothing(name)) {
			List<DocHandle> ret = new Query<DocHandle>(DocHandle.class, FLD_TITLE, name).execute();
			if (ret != null && ret.size() > 0) {
				return ret.get(0);
			}
		}
		return null;
	}
	
	public List<DocHandle> getMembers(Patient pat){
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class, FLD_CAT, get(FLD_TITLE));
		if (pat != null) {
			qbe.add(FLD_PATID, "=", pat.getId()); //$NON-NLS-1$
		}
		return qbe.execute();
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
	
	public byte[] getContents(){
		byte[] ret = getBinary(FLD_DOC);
		if (ret == null) {
			File file = getStorageFile(true);
			if (file != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					int b;
					while ((b = bis.read()) != -1) {
						baos.write(b);
					}
					bis.close();
					baos.close();
					return baos.toByteArray();
				} catch (Exception ex) {
					ExHandler.handle(ex);
					SWTHelper.showError("read error", //$NON-NLS-1$
						Messages.DocHandle_readErrorHeading, "Die Datei " + file.getAbsolutePath()
							+ " konnte nicht gelesen werden. " + ex.getMessage());
				}
			}
		}
		return ret;
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
			
			// Make the temporary filename configurable
			StringBuffer configured_temp_filename = new StringBuffer();
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element(
				"constant1", ""));
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("PID",
				getPatient().getKuerzel())); // getPatient() liefert in etwa: ch.elexis.com@1234567;
// getPatient().getId() eine DB-ID; getPatient().getKuerzel() die Patientennummer.
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("fn",
				getPatient().getName()));
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("gn",
				getPatient().getVorname()));
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("dob",
				getPatient().getGeburtsdatum()));
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("dt",
				getTitle())); // not more than 80 characters, laut javadoc
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("dk",
				getKeywords()));
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			// Da könnten auch noch Felder wie die Document Create Time etc. rein - siehe auch
// unten, die Methoden getPatient() etc.
			
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("dguid",
				getGUID()));
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			
			// N.B.: We may NOT REALLY assume for sure that another filename, derived from a
// createTempFile() result, where the random portion would be moved forward in the name, may also be
// guaranteed unique!
			// So *if* we should use createTempFile() to obtain such a filename, we should put
// constant2 away from configured_temp_filename and put it in the portion provided with "ext", if a
// unique_temp_id was requested.
			// And, we should probably not cut down the size of that portion, so it would be best to
// do nothing for that but offer a checkbox.
			
			// Es muss aber auch gar nicht mal unique sein - wenn die Datei schon existiert UND von
// einem anderen Prozess, z.B. Word, mit r/w geöffnet ist, erscheint ein sauberer Dialog mit einer
// Fehlermeldung. Wenn sie nicht benutzt wird, kann sie überschrieben werden.
			
			// Der Fall, dass hier auf einem Rechner / von einem User bei dem aus Daten erzeugten
// Filenamen zwei unterschiedliche Inhalte mit gleichem Namen im gleichen Tempdir gleichzeitig nur
// r/o geöffnet werden und einander in die Quere kommen, dürfte unwahrscheinlich sein.
			// Wie wohl... vielleicht doch nicht. Wenn da jemand beim selben Patienten den Titel 2x
// einstellt nach: "Bericht Dr. Müller", und das dann den Filenamen liefert, ist wirklich alles
// gleich.
			// So we should ... possibly really add some random portion; or use any other property
// of the file in that filename (recommendation: e.g. like in AnyQuest Server :-) )
			
			// Ganz notfalls naoch ein Feld mit der Uhrzeit machen... oder die Temp-ID je nach
// eingestellten num_digits aus den clockticks speisen. Und das File mit try createn, notfalls
// wiederholen mit anderem clocktick - dann ist das so gut wie ein createTempFile().
			// For now, I compute my own random portion - by creating a random BigInteger with a
// sufficient number of bits to represent PreferencePage.nPreferences_cotf_element_digits_max
// decimal digits.
			// And I accept the low chance of getting an existing random part, i.e. I don't check
// the file is already there.
			
			SecureRandom random = new SecureRandom();
			int needed_bits =
				(int) Math.round(Math.ceil(Math
					.log(Preferences.nPreferences_cotf_element_digits_max) / Math.log(2)));
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("random",
				new BigInteger(needed_bits, random).toString()));
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			
			configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element(
				"constant2", ""));
			log.debug("configured_temp_filename=" + configured_temp_filename.toString());
			
			File temp;
			if (configured_temp_filename.length() > 0) {
				// The following file will have a unique variable part after the
// configured_temp_filename_and before the .ext,
				// but will be located in the temporary directory.
				File uniquetemp =
					File.createTempFile(configured_temp_filename.toString() + "_", "." + ext); //$NON-NLS-1$ //$NON-NLS-2$
				String temp_pathname = uniquetemp.getParent();
				uniquetemp.delete();
				
				// remove the _unique variable part from the temporary filename and create a new
// file in the same directory as the previously automatically created unique temp file
				log.debug(temp_pathname);
				log.debug(configured_temp_filename + "." + ext);
				temp = new File(temp_pathname, configured_temp_filename + "." + ext);
				temp.createNewFile();
			} else {
				// if special rules for the filename are not configured, then generate it simply as
// before Omnivore Version 1.4.4
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
	
	public boolean storeExternal(String filename) {
		byte[] b = getContents();
		if (b == null) {
			SWTHelper.showError(Messages.DocHandle_readErrorCaption,
					Messages.DocHandle_readErrorText);
			return false;
		}
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(b);
			fos.close();
			return true;
		} catch (IOException ios) {
			ExHandler.handle(ios);
			SWTHelper.showError(Messages.DocHandle_writeErrorCaption,
					Messages.DocHandle_writeErrorCaption, ios.getMessage());
			return false;
		}
	}
	
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
		
		// FIXME: Warum ist es eigentlich so, dass nur 80 Zeichen funktionieren? Notfalls könnte ja
// auch Omnivore das Umbenennen veranlassen, und den Namen einfach (oder interaktiv) kürzen, statt
// eine Fehlermeldung auszugeben...
		
		// From Omnivore version 1.4.2 to Omnivore version 1.4.3,
		// I changed the fixed limit from 255 for the last part of the filename to a configurable
// one with default 80 chars.
		// Linux and MacOS may be able to handle longer filenames,
		// but we observed and verified that Windows 7 64-bit would not import files with names
// longer than 80 chars.
		// Also, put the filename length check *before* displaying the file import dialog.
		// Otherwise, users would have to type in a bunch of text first, and learn only afterwards,
// that that would be discarded.
		Integer maxOmnivoreFilenameLength = 
			ch.elexis.omnivore.views.Preferences.getOmnivoreMax_Filename_Length();
		
		String nam = file.getName();
		if (nam.length() > maxOmnivoreFilenameLength) { // The checked limit is now configurable.
			SWTHelper.showError(Messages.DocHandle_importErrorCaption, MessageFormat.format(
				Messages.DocHandle_importErrorMessage, maxOmnivoreFilenameLength)); // The error
// message is also dynamically generated.
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
				
				// FIXME: The original file name should be preserved in a separate field when the
// file content is imported into the database.
				new DocHandle(null, baos.toByteArray(), act, fid.title.trim(), file.getName(),
					fid.keywords.trim()); // Added trim() to title and keywords, to avoid
// unnecessary extra lines in the omnivore content listing.
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(Messages.DocHandle_importErrorCaption,
					Messages.DocHandle_importErrorMessage2);
				// Wenn das Importieren einen Fehler wirft, nacher die Datei nicht automatisch
// wegarchivieren. Deshalb hier return eingefügt.
				return;
			}
			
			// Now, process all defined rules for automatic archiving of imported documents, as
// configured in omnivore preferences.
			
			// Anything will be done *only* when SrcPattern and DestDir are both at least defined
// strings.
			// A rule, where both SrcPattern and DestDir are empty strings, will be ignored and have
// no effect. Especially, it will not stop further rules from being evaluated.
			
			// A (usually final) rule that is matched by all files may contain either strings like
// ".", oder be empty.
			
			// If DestDir ist not an empty string, the program will test:
			// if it is a file, it will be overwritten by the source file (hopefully...), except if
// it has the same name as the source file, where nothing will happen.
			// if it is a directory, the program will try to put the source file into it - but
// before doing so, will ensure that neither a dir nor a file of the same name are already there.
			// To automatically delete source files after importing, you may either specify one
// fixed filename for all targets (they will overwrite each other), or /dev/null or c:\NUL or the
// like.
			
			// If you specify an empty string for DestDir, but a non-empty String for the
// SrcPattern, that will protect matching files from being automatically archived.
			
			// Every file will only be handled by the first matching rule.
			
			try {
				for (Integer i = 0; i < ch.elexis.omnivore.views.Preferences
					.getOmnivorenRulesForAutoArchiving(); i++) {
					String SrcPattern =
						ch.elexis.omnivore.views.Preferences
							.getOmnivoreRuleForAutoArchivingSrcPattern(i);
					String DestDir =
						ch.elexis.omnivore.views.Preferences
							.getOmnivoreRuleForAutoArchivingDestDir(i);
					
					if ((SrcPattern != null) && (DestDir != null)
						&& ((SrcPattern != "" || DestDir != ""))) {
						// Unter win für Dateien vom Explorer hineingezogen liefern getAbsolutePath
// und getAbsoluteFile dasselbe - jeweils laufwerk+path+dateiname.
						// getCanonicalXYZ liefert schon einen Fehler in Eclipse
						// getName liefert nur den Dateinamen ohne Laufwerk und/oder Pfad.
						
						log.debug("Automatic archiving found matching rule #" + (i + 1)
							+ " (1-based index):");
						log.debug("file.getAbsolutePath(): " + file.getAbsolutePath());
						log.debug("Pattern: " + SrcPattern);
						log.debug("DestDir: " + DestDir);
						
						if (file.getAbsolutePath().contains(SrcPattern)) {
							log.debug("SrcPattern found in file.getAbsolutePath()" + i);
							
							if (DestDir == "") {
								log.debug("DestDir is empty. No more rules will be evaluated for this file. Returning.");
								return;
							} // An empty DestDir protects a matching file from being processed by
// any further rules.
							
							// renameTo is (a) platform dependent, and (b) needs a file, not just a
// string, as a target.
							// I.e. I must check whether the DestDir ist a directory, make a valid
// filename from it, etc.
							// So before I would do that, let me try move first, which javas doc
// says should be platform independent etc.
							// Well, move would truly operate on PathNames, rather than files, but
// still require some overhead.
							
							/*
							 * All this doesn't work, because the "browse" button in the settings
							 * returns a selected directory WITHOUT a trailing sepchar. Don't expect
							 * users to add that. Instead, I will check whether DestDir is already a
							 * directory, and add the filename, if yes. //For now, I support
							 * TargetDirectory names only if they end with separatorChar; NOT with
							 * pathSeparatorChar (i.e.: ":" on Windows). //First, it is not so
							 * probable that anybody wants to put the result to c:\ or the like.
							 * //Second, they can wirte c:\ as DestDir, no need to especially
							 * support c: as well. //Third, formally, c: might also mean: use the
							 * "current working directory" (whatever that may be) for that drive. In
							 * DOS shell, it is (or may be?) like that. //Fourth, it would require
							 * more code lines, including checking which system we're running on.
							 * Not now.
							 * 
							 * File NewFile; if (DestDir.endsWith(file.separatorChar+"")) { //I use
							 * +"" to get the desired String argument for endsWith //If DestDir
							 * supplies a directory name, then the renameTo destination file name is
							 * that + old file name without the old path: NewFile = new
							 * File(DestDir+file.getName()); } else { //If DestDir supplies a single
							 * file (like /dev/nul or
							 * \\anyserver\anywhere\the-file-that-was-last-imported-by-omnivore.dat)
							 * then use that directly. NewFile = new File(DestDir); }
							 */
							
							// So, first I use DestDir as supplied.
							// If that's a simple file already (like /dev/null), I just use it as
// rename destination.
							// If that's a directory however, I add the name of the source file to
// it.
							// And just to make sure: If that is *STILL* a directory - that may not
// occur often, but is still possible -
							// the user probably selected the wrong target, or tried to import a
// file that should not be stored in the target folder because a directory of the same name is
// already there.
							// In that case, we don't rename, in order to protect that directory
// from being overwritten.
							// Even if DestDir should end with a separatorChar, that should not
// hurt, because sequences of separatorChar in a filename should probably be treated as a single
// one.
							File NewFile = new File(DestDir);
							if (NewFile.isDirectory()) {
								log.debug("DestDir is a directory. Adding file.getName()...");
								NewFile = new File(DestDir + file.separatorChar + file.getName());
							}
							
							// First, make sure that any destination name is NOT currently a
// directory.
							// If we copy or move a simple file over a directory name, on some
// systems, that might lose the original directory with its previous content completely!
							// If the target exists, java on windows would currently just not carry
// out the renameTo(). But the user would not be informed.
							// So I'll provide a separate error message for that case.
							if (NewFile.isDirectory()) {
								log.debug("NewFile.isDirectory==true; renaming not attempted");
								SWTHelper.showError(Messages.DocHandle_MoveErrorCaption,
									MessageFormat.format(Messages.DocHandle_MoveErrorDestIsDir,
										DestDir, file.getName()));
							} else {
								if (NewFile.isFile()) {
									log.debug("NewFile.isFile==true; renaming not attempted");
									SWTHelper.showError(Messages.DocHandle_MoveErrorCaption,
										MessageFormat.format(Messages.DocHandle_MoveErrorDestIsFile,
											DestDir, file.getName()));
								} else {
									log.debug("renaming incoming file to: "
										+ NewFile.getAbsolutePath());
									if (Files.move(file.toPath(), NewFile.toPath(), REPLACE_EXISTING) != null) {
										log.debug("renaming ok");
										// do nothing, everything worked out fine
									} else {
										log.debug("renaming attempted, but returned false.");
										log.debug("However, I may probably have observed this after successful moves?! So I won't show an error dialog here. js");
										log.debug("So I won't show an error dialog here; if a real exception occured, that would suffice to trigger it.");
										// SWTHelper.showError(Messages.DocHandleMoveErrorCaption,Messages.DocHandleMoveError);
									}
								}
							}
							
							// Java's concept of files and their filenames differs substantially
// from filenames and file handles of other environments.
							// I hope I can just re-use the NewFile object on something different if
// the first attempt does not return what I wanted,
							// and after the "renaming" has taken place - no matter how that is
// actually carried out on a given platform -
							// that all of the involved temporary constructs are reliably removed
// from memory again.
							// Well, after all, Java promises just to take care of that...
							
							break; // SrcPattern matched. Only one the first matching rule shall be
// processed per file.
						}
					} // if SrcPattern, DestPattern <> null & either one <>""
				} // for i...
			} catch (Throwable throwable) {
				ExHandler.handle(throwable);
				SWTHelper
					.showError(Messages.DocHandle_MoveErrorCaption, Messages.DocHandle_MoveError);
			}
			
		}
		
	}
	
	public String getTitle(){
		return checkNull(get("Titel")); //$NON-NLS-1$
	}
	
	public String getKeywords(){
		return checkNull(get("Keywords")); //$NON-NLS-1$
	}
	
	public String getDate() {
		return get(FLD_DATE);
	}
	
	public void setDate(Date d) {
		TimeTool tt = new TimeTool();
		tt.setTime(d);
		set(FLD_DATE, tt.toString(TimeTool.DATE_GER));
	}

	private void configError(){
		SWTHelper.showError("config error", Messages.DocHandle_configErrorCaption, //$NON-NLS-1$
			Messages.DocHandle_configErrorText);
	}
	
	// IDocument
	@Override
	public String getCategory(){
		return getCategoryName();
	}
	
	@Override
	public String getMimeType(){
		return checkNull(get(FLD_MIMETYPE));
	}
	
	@Override
	public String getCreationDate(){
		return get(FLD_DATE);
	}
	
	@Override
	public Patient getPatient(){
		return Patient.load(get(FLD_PATID));
	}
	
	@Override
	public InputStream getContentsAsStream() throws ElexisException{
		return new ByteArrayInputStream(getContents());
	}
	
	@Override
	public byte[] getContentsAsBytes() throws ElexisException{
		return getContents();
	}
	
	@Override
	public String getGUID(){
		return getId();
	}
	
}
