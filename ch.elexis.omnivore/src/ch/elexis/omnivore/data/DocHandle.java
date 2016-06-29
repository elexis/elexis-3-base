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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger; //Omnivore: To generate random string for unique_temp_ID
import java.nio.file.Files;
import java.security.SecureRandom; //Omnivore: To generate random string for unique_temp_ID
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.omnivore.views.FileImportDialog;
import ch.elexis.omnivore.views.Preferences;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class DocHandle extends PersistentObject implements IOpaqueDocument {
	private static Logger log = LoggerFactory.getLogger("ch.elexis.omnivore.DocHandle"); //$NON-NLS-1$
	
	public static final String FLD_CAT = "Cat"; //$NON-NLS-1$
	public static final String FLD_TITLE = "Titel"; //$NON-NLS-1$
	public static final String FLD_MIMETYPE = "Mimetype"; //$NON-NLS-1$
	public static final String FLD_DOC = "Doc"; //$NON-NLS-1$
	public static final String FLD_PATH = "Path"; //$NON-NLS-1$
	public static final String FLD_KEYWORDS = "Keywords"; //$NON-NLS-1$
	public static final String FLD_PATID = "PatID"; //$NON-NLS-1$
	public static final String FLD_DATE = "Datum"; //$NON-NLS-1$
	
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
	
	protected static final String VERSION = "1";
	
	static {
		addMapping(TABLENAME, FLD_PATID, FLD_CAT + "=Category", DATE_COMPOUND, //$NON-NLS-1$
			FLD_TITLE + "=Title", FLD_KEYWORDS, FLD_PATH, FLD_DOC, FLD_MIMETYPE); //$NON-NLS-1$
		DocHandle start = load(VERSION); //$NON-NLS-1$
		if (!tableExists(TABLENAME)) {
			init();
		} else {
			VersionInfo vi = new VersionInfo(start.get(FLD_TITLE));
			if (vi.isOlder(DBVERSION)) {
				if (vi.isOlder("1.1.0")) { //$NON-NLS-1$
					String statement = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
						+ " ADD IF NOT EXISTS deleted CHAR(1) default '0';"; //$NON-NLS-1$
					getConnection().exec(statement);
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
					ElexisStatus status =
						new ElexisStatus(ElexisStatus.WARNING, "ch.elexis.omnivore", //$NON-NLS-1$
							ElexisStatus.CODE_NONE, "Error on static initialization of"
								+ DocHandle.class.getName()+": "+vi, null, ElexisStatus.LOG_WARNINGS);
					StatusManager.getManager().handle(status, StatusManager.SHOW);
				}
			}
		}
	}
	
	public File getStorageFile(boolean force){
		if (force || Preferences.storeInFilesystem()) {
			String pathname = Preferences.getBasepath();
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
			if (Preferences.storeInFilesystem()) {
				configError();
			}
		}
		return null;
	}
	
	/**
	 * Creates a new omnivore document. Adds it to the defaultCategory if doc does not provide one
	 * 
	 * @param doc
	 *            document
	 */
	public DocHandle(IOpaqueDocument doc) throws ElexisException{
		create(doc.getGUID());
		
		String category = doc.getCategory();
		if (category == null || category.length() < 1) {
			category = DocHandle.getDefaultCategory().getCategoryName();
		} else {
			DocHandle.ensureCategoryAvailability(category);
		}
		set(new String[] {
			FLD_CAT, FLD_PATID, FLD_DATE, FLD_TITLE, FLD_KEYWORDS, FLD_MIMETYPE
		}, category, doc.getPatient().getId(), doc.getCreationDate(), doc.getTitle(),
			doc.getKeywords(), doc.getMimeType());
		store(doc.getContentsAsBytes());
		
	}
	
	/**
	 * Creates a new omnivore document.
	 * 
	 * @param category
	 *            Category. If null, adds it to the defaultCategory
	 * @param doc
	 *            the document to add
	 * @param pat
	 *            document belongs to this patient
	 * @param title
	 * @param mime
	 *            MIME-type, e.g. pdf
	 * @param keyw
	 *            keywords
	 */
	public DocHandle(String category, byte[] doc, Patient pat, String title, String mime,
		String keyw){
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
		set(new String[] {
			FLD_CAT, FLD_PATID, FLD_DATE, FLD_TITLE, FLD_KEYWORDS, FLD_MIMETYPE
		}, category, pat.getId(), new TimeTool().toString(TimeTool.DATE_GER), title, keyw, mime);
		store(doc);
	}
	
	private void store(byte[] doc){
		File file = getStorageFile(false);
		if (file == null) {
			try {
				setBinary(FLD_DOC, doc);
			} catch (PersistenceException pe) {
				SWTHelper.showError(Messages.DocHandle_documentErrorCaption,
					Messages.DocHandle_documentErrorText2 + "; " + pe.getMessage());
				delete();
			}
		} else {
			try {
				BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
				bout.write(doc);
				bout.close();
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError("write error", Messages.DocHandle_writeErrorHeading, //$NON-NLS-1$
					"Konnte die Datei " + file.getAbsolutePath() + " nicht schreiben."
						+ ex.getMessage());
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
		qbe.add(FLD_MIMETYPE, Query.EQUALS, CATEGORY_MIMETYPE); //$NON-NLS-1$
		qbe.add(FLD_TITLE, Query.EQUALS, DEFAULT_CATEGORY);
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
		qbe.add(FLD_MIMETYPE, Query.EQUALS, CATEGORY_MIMETYPE);
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
		clearCache();
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
			qbe.add(FLD_PATID, Query.EQUALS, pat.getId());
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
		return new DocHandle(id);
	}
	
	@Override
	public String getLabel(){
		StringBuilder sb = new StringBuilder();
		
		// avoid adding only a space - causes trouble in renaming of categories
		String date = get(FLD_DATE);
		if (date != null && !date.isEmpty()) {
			sb.append(get(FLD_DATE));
			sb.append(StringConstants.SPACE);
		}
		sb.append(get(FLD_TITLE));
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
			String ext = StringConstants.SPACE;
			File temp = createTemporaryFile(null);
			
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
	
	/**
	 * create a temporary file
	 * 
	 * @param titel
	 *            optional (can as well be null - name will be created using preferences settings)
	 * @return temporary file
	 */
	public File createTemporaryFile(String title){
		String ext = "";
		String typname = get(FLD_MIMETYPE);
		int r = typname.lastIndexOf('.');
		if (r == -1) {
			typname = get(FLD_TITLE);
			r = typname.lastIndexOf('.');
		}
		
		if (r != -1) {
			ext = typname.substring(r + 1);
		}
		// Make the temporary filename configurable
		StringBuffer configured_temp_filename;
		if (title == null || title.equals("")) {
			// Make the temporary filename configurable
			configured_temp_filename = generateConfiguredTempFilename();
		} else {
			configured_temp_filename = new StringBuffer();
			configured_temp_filename.append(title);
		}
		
		File temp = null;
		try {
			if (configured_temp_filename.length() > 0) {
				File uniquetemp =
					File.createTempFile(configured_temp_filename.toString() + "_", "." + ext); //$NON-NLS-1$ //$NON-NLS-2$
				String temp_pathname = uniquetemp.getParent();
				uniquetemp.delete();
				
				log.debug(temp_pathname);
				log.debug(configured_temp_filename + "." + ext);
				temp = new File(temp_pathname, configured_temp_filename + "." + ext);
				temp.createNewFile();
			} else {
				temp = File.createTempFile("omni_", "_vore." + ext); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			temp.deleteOnExit();
			byte[] b = getContents();
			if (b == null) {
				SWTHelper.showError(Messages.DocHandle_readErrorCaption,
					Messages.DocHandle_readErrorMessage);
				return temp;
			}
			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(b);
			fos.close();
		} catch (FileNotFoundException e) {
			log.debug("File not found " + e, Log.WARNINGS);
		} catch (IOException e) {
			log.debug("Error creating file " + e, Log.WARNINGS);
		}
		
		return temp;
	}
	
	private StringBuffer generateConfiguredTempFilename(){
		StringBuffer configured_temp_filename = new StringBuffer();
		log.debug("configured_temp_filename=" + configured_temp_filename.toString());
		configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("constant1",
			""));
		log.debug("configured_temp_filename=" + configured_temp_filename.toString());
		configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("PID",
			getPatient().getKuerzel()));
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
		
		configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("dguid",
			getGUID()));
		log.debug("configured_temp_filename=" + configured_temp_filename.toString());
		
		SecureRandom random = new SecureRandom();
		int needed_bits =
			(int) Math.round(Math.ceil(Math.log(Preferences.nPreferences_cotf_element_digits_max)
				/ Math.log(2)));
		configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("random",
			new BigInteger(needed_bits, random).toString()));
		log.debug("configured_temp_filename=" + configured_temp_filename.toString());
		
		configured_temp_filename.append(Preferences.getOmnivoreTemp_Filename_Element("constant2",
			""));
		log.debug("configured_temp_filename=" + configured_temp_filename.toString());
		return configured_temp_filename;
	}
	
	public String getMimetype(){
		return get(FLD_MIMETYPE);
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	protected DocHandle(String id){
		super(id);
	}
	
	protected DocHandle(){}
	
	public boolean storeExternal(String filename){
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
	
	public static DocHandle assimilate(String f){
		return assimilate(f, null);
	}
	
	public static DocHandle assimilate(String f, String selectedCategory){
		Patient act = ElexisEventDispatcher.getSelectedPatient();
		if (act == null) {
			SWTHelper.showError(Messages.DocHandle_noPatientSelected,
				Messages.DocHandle_pleaseSelectPatient);
			return null;
		}
		File file = new File(f);
		if (!file.canRead()) {
			SWTHelper.showError(Messages.DocHandle_cantReadCaption,
				MessageFormat.format(Messages.DocHandle_cantReadMessage, f));
			return null;
		}
		
		// can't import complete directory
		if (file.isDirectory()) {
			SWTHelper.showError(Messages.DocHandle_importErrorDirectory,
				Messages.DocHandle_importErrorDirectoryText);
			return null;
		}
		
		Integer maxOmnivoreFilenameLength =
			ch.elexis.omnivore.views.Preferences.getOmnivoreMax_Filename_Length();
		
		String nam = file.getName();
		if (nam.length() > maxOmnivoreFilenameLength) {
			SWTHelper.showError(Messages.DocHandle_importErrorCaption, MessageFormat.format(
				Messages.DocHandle_importErrorMessage, maxOmnivoreFilenameLength));
			return null;
		}
		
		FileImportDialog fid;
		if (selectedCategory == null) {
			fid = new FileImportDialog(file.getName());
		} else {
			fid = new FileImportDialog(file.getName(), selectedCategory);
		}
		
		DocHandle dh = null;
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
				
				String fileName = file.getName();
				if (fileName.length() > 255) {
					SWTHelper.showError(Messages.DocHandle_readErrorCaption,
						Messages.DocHandle_fileNameTooLong);
					return null;
				}
				String category = fid.category;
				if (category == null || category.length() == 0) {
					category = DocHandle.getDefaultCategory().getCategoryName();
				}
				dh = new DocHandle(category, baos.toByteArray(), act, fid.title.trim(), file.getName(),
					fid.keywords.trim());
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(Messages.DocHandle_importErrorCaption,
					Messages.DocHandle_importErrorMessage2);
				return null;
			}
			
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
						log.debug("Automatic archiving found matching rule #" + (i + 1)
							+ " (1-based index):");
						log.debug("file.getAbsolutePath(): " + file.getAbsolutePath());
						log.debug("Pattern: " + SrcPattern);
						log.debug("DestDir: " + DestDir);
						
						if (file.getAbsolutePath().contains(SrcPattern)) {
							log.debug("SrcPattern found in file.getAbsolutePath()" + i);
							
							if (DestDir == "") {
								log.debug("DestDir is empty. No more rules will be evaluated for this file. Returning.");
								return dh;
							}
							
							File NewFile = new File(DestDir);
							if (NewFile.isDirectory()) {
								log.debug("DestDir is a directory. Adding file.getName()...");
								NewFile = new File(DestDir + File.separatorChar + file.getName());
							}
							
							if (NewFile.isDirectory()) {
								log.debug("NewFile.isDirectory==true; renaming not attempted");
								SWTHelper.showError(Messages.DocHandle_MoveErrorCaption,
									MessageFormat.format(Messages.DocHandle_MoveErrorDestIsDir,
										DestDir, file.getName()));
							} else {
								if (NewFile.isFile()) {
									log.debug("NewFile.isFile==true; renaming not attempted");
									SWTHelper.showError(Messages.DocHandle_MoveErrorCaption,
										MessageFormat.format(
											Messages.DocHandle_MoveErrorDestIsFile, DestDir,
											file.getName()));
								} else {
									log.debug("renaming incoming file to: "
										+ NewFile.getAbsolutePath());
									if (Files.move(file.toPath(), NewFile.toPath(),
										REPLACE_EXISTING) != null) {
										log.debug("renaming ok");
									} else {
										log.debug("renaming attempted, but returned false.");
										log.debug("However, I may probably have observed this after successful moves?! So I won't show an error dialog here. js");
										log.debug("So I won't show an error dialog here; if a real exception occured, that would suffice to trigger it.");
										// SWTHelper.showError(Messages.DocHandleMoveErrorCaption,Messages.DocHandleMoveError);
									}
								}
							}
							
							break;
						}
					}
				}
			} catch (Throwable throwable) {
				ExHandler.handle(throwable);
				SWTHelper.showError(Messages.DocHandle_MoveErrorCaption,
					Messages.DocHandle_MoveError);
			}
		}
		return dh;
	}
	
	public String getTitle(){
		return checkNull(get(FLD_TITLE));
	}
	
	public String getKeywords(){
		return checkNull(get(FLD_KEYWORDS));
	}
	
	public String getDate(){
		return get(FLD_DATE);
	}
	
	public void setDate(Date d){
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
