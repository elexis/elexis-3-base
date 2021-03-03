/*******************************************************************************
 * Copyright (c) 2006-2016, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    <office@medevit.at> - Share a common base
 *******************************************************************************/

package ch.elexis.omnivore.data;

import static ch.elexis.omnivore.Constants.CATEGORY_MIMETYPE;
import static ch.elexis.omnivore.Constants.DEFAULT_CATEGORY;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.omnivore.dialog.FileImportDialog;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.MimeTool;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class DocHandle extends PersistentObject implements IOpaqueDocument {
	
	private static Logger log = LoggerFactory.getLogger(DocHandle.class);
	
	private TimeTool toStringTool = new TimeTool();
	
	public static final String FLD_CAT = "Cat"; //$NON-NLS-1$
	public static final String FLD_TITLE = "Titel"; //$NON-NLS-1$
	public static final String FLD_MIMETYPE = "Mimetype"; //$NON-NLS-1$
	public static final String FLD_DOC = "Doc"; //$NON-NLS-1$
	public static final String FLD_PATH = "Path"; //$NON-NLS-1$
	public static final String FLD_KEYWORDS = "Keywords"; //$NON-NLS-1$
	public static final String FLD_PATID = "PatID"; //$NON-NLS-1$
	public static final String FLD_CREATION_DATE = "CreationDate"; //$NON-NLS-1$ 
	
	public static final String TABLENAME = "CH_ELEXIS_OMNIVORE_DATA"; //$NON-NLS-1$
	public static final String DBVERSION = "2.0.4"; //$NON-NLS-1$
	
	protected static final String VERSION = "1";
	
	//@formatter:off
	public static final String createDB = 
		"CREATE TABLE " +	TABLENAME + " ("
		+ "ID		    VARCHAR(25) primary key,"
		+ "lastupdate   BIGINT,"
		+ "deleted      CHAR(1) default '0',"  
		+ "PatID	    VARCHAR(25),"
		+ "Datum		CHAR(8),"
		+ "CreationDate CHAR(8),"
		+ "Category		VARCHAR(80) default null,"
		+ "Title 		VARCHAR(255)," 
		+ "Mimetype		VARCHAR(255),"
		+ "Keywords		VARCHAR(255)," 
		+ "Path			VARCHAR(255),"
		+ "Doc			BLOB);" 
		+ "CREATE INDEX OMN1 ON " + TABLENAME + " (PatID);"
		+ "CREATE INDEX OMN2 ON " + TABLENAME + " (Keywords);" 
		+ "CREATE INDEX OMN3 ON " + TABLENAME + " (Category);" 
		+ "CREATE INDEX OMN4 ON " + TABLENAME + " (Mimetype);" 
		+ "CREATE INDEX OMN5 ON " + TABLENAME + " (deleted);"
		+ "CREATE INDEX OMN6 ON " + TABLENAME + " (Title);" 
		+ "INSERT INTO " + TABLENAME + " (ID, TITLE) VALUES ('1','"+ DBVERSION + "');";
	//@formatter:on
	
	public static final String upd120 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " MODIFY Mimetype VARCHAR(255);" + "ALTER TABLE " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " MODIFY Keywords VARCHAR(255);" + "ALTER TABLE " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " Modify Path VARCHAR(255);"; //$NON-NLS-1$
	public static final String upd200 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " ADD Category VARCHAR(80) default null;" //$NON-NLS-1$
		+ "CREATE INDEX OMN3 ON " + TABLENAME + " (Category);" //$NON-NLS-1$ //$NON-NLS-2$
		+ "ALTER TABLE " + TABLENAME + " MODIFY Title VARCHAR(255);"; //$NON-NLS-1$ //$NON-NLS-2$	
	public static final String upd201 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " ADD lastupdate BIGINT default 0;"; //$NON-NLS-1$	
	public static final String upd202 = "CREATE INDEX OMN4 ON " + TABLENAME //$NON-NLS-1$
		+ " (Mimetype);"; //$NON-NLS-1$
	public static final String upd203 = "CREATE INDEX OMN5 ON " + TABLENAME //$NON-NLS-1$
		+ " (deleted);" + "CREATE INDEX OMN6 ON " + TABLENAME + " (Title);"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	public static final String upd204 = "ALTER TABLE " + TABLENAME + " ADD CreationDate CHAR(8);";//$NON-NLS-1$ 
	
	private static List<DocHandle> main_categories = null;
	
	static {
		addMapping(TABLENAME, FLD_PATID, FLD_CAT + "=Category", DATE_COMPOUND, //$NON-NLS-1$
			FLD_CREATION_DATE + "=S:D:" + FLD_CREATION_DATE, FLD_TITLE + "=Title", FLD_KEYWORDS,
			FLD_PATH, FLD_DOC, FLD_MIMETYPE);
		DocHandle start = load(StringConstants.ONE);
		if (!tableExists(TABLENAME)) {
			init();
		} else {
			VersionInfo vi = new VersionInfo(start.get(FLD_TITLE));
			if (vi.isOlder(DBVERSION)) {
				if (vi.isOlder("1.1.0")) { //$NON-NLS-1$
					getConnection().exec("ALTER TABLE " + TABLENAME //$NON-NLS-1$
						+ " ADD if not exists deleted CHAR(1) default '0';"); //$NON-NLS-1$
				}
				if (vi.isOlder("1.2.0")) { //$NON-NLS-1$
					createOrModifyTable(upd120);
				}
				if (vi.isOlder("2.0.0")) { //$NON-NLS-1$
					createOrModifyTable(upd200);
				}
				if (vi.isOlder("2.0.1")) { //$NON-NLS-1$
					createOrModifyTable(upd201);
				}
				if (vi.isOlder("2.0.2")) { //$NON-NLS-1$
					createOrModifyTable(upd202);
				}
				if (vi.isOlder("2.0.3")) { //$NON-NLS-1$
					createOrModifyTable(upd203);
				}
				if (vi.isOlder("2.0.4")) {
					createOrModifyTable(upd204);
				}
				start.set(FLD_TITLE, DBVERSION);
			}
		}
	}
	
	public DocHandle(IOpaqueDocument doc) throws ElexisException{
		create(doc.getGUID());
		
		String category = doc.getCategory();
		if (category == null || category.length() < 1) {
			category = DocHandle.getDefaultCategory().getCategoryName();
		} else {
			DocHandle.ensureCategoryAvailability(category);
		}
		set(new String[] {
			FLD_CAT, FLD_PATID, FLD_DATE, FLD_CREATION_DATE, FLD_TITLE, FLD_KEYWORDS, FLD_MIMETYPE
		}, category, doc.getPatient().getId(), doc.getCreationDate(), doc.getCreationDate(),
			doc.getTitle(), doc.getKeywords(), doc.getMimeType());
		store(doc.getContentsAsBytes());
	}
	
	public DocHandle(String category, byte[] doc, Patient pat, String title, String mime,
		String keyw){
		this(category, doc, pat, new Date(), title, mime, keyw);
	}
	
	public DocHandle(String category, byte[] doc, Patient pat, Date creationDate, String title,
		String mime, String keyw){
		if ((doc == null) || (doc.length == 0)) {
			SWTHelper.showError(Messages.DocHandle_readErrorCaption,
				Messages.DocHandle_readErrorText);
			return;
		}
		create(null);
		
		if (category == null || category.length() < 1) {
			category = DocHandle.getDefaultCategory().getCategoryName();
		} else {
			DocHandle.ensureCategoryAvailability(category);
		}
		
		if (creationDate == null) {
			creationDate = new Date();
		}
		
		if (category == null || category.length() == 0) {
			set(new String[] {
				FLD_PATID, FLD_DATE, FLD_CREATION_DATE, FLD_TITLE, FLD_KEYWORDS, FLD_MIMETYPE
			}, pat.getId(), new TimeTool().toString(TimeTool.DATE_GER),
				new TimeTool(creationDate).toString(TimeTool.DATE_COMPACT), title, keyw, mime);
		} else {
			set(new String[] {
				FLD_CAT, FLD_PATID, FLD_DATE, FLD_CREATION_DATE, FLD_TITLE, FLD_KEYWORDS,
				FLD_MIMETYPE
			}, category, pat.getId(), new TimeTool().toString(TimeTool.DATE_GER),
				new TimeTool(creationDate).toString(TimeTool.DATE_COMPACT), title, keyw, mime);
			
		}
		store(doc);
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	protected DocHandle(String id){
		super(id);
	}
	
	protected DocHandle(){}
	
	/**
	 * We need a default category as a fallback for invalid or not defined categories.
	 * 
	 * @return the default category in case no other category is defined
	 */
	public static DocHandle getDefaultCategory(){
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
		qbe.add(FLD_MIMETYPE, Query.EQUALS, CATEGORY_MIMETYPE);
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
		if (main_categories == null) {
			Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
			qbe.add(FLD_MIMETYPE, Query.EQUALS, CATEGORY_MIMETYPE);
			main_categories = qbe.execute();
		}
		return main_categories;
	}
	
	public static void addMainCategory(String name){
		if (findCategory(name) == null) {
			DocHandle dh = new DocHandle();
			dh.create(null);
			dh.set(new String[] {
				FLD_TITLE, FLD_CAT, FLD_MIMETYPE
			}, name, name, CATEGORY_MIMETYPE);
			main_categories = null;
		}
	}
	
	private static String findCategory(String name){
		List<DocHandle> categories = getMainCategories();
		for (DocHandle docHandle : categories) {
			String catName = docHandle.getCategoryName().toLowerCase();
			if (catName.equals(name.toLowerCase())) {
				return docHandle.getCategory();
			}
		}
		return null;
	}
	
	public static void renameCategory(String old, String newn){
		String oldname = old.trim();
		String newName = newn.trim();
		
		if (findCategory(newName) == null) {
			getConnection().exec("update CH_ELEXIS_OMNIVORE_DATA set Category="
				+ JdbcLink.wrap(newName) + " where Category= " + JdbcLink.wrap(oldname));
			getConnection().exec("update CH_ELEXIS_OMNIVORE_DATA set Title="
				+ JdbcLink.wrap(newName) + " where Title=" + JdbcLink.wrap(oldname)
				+ " and mimetype=" + JdbcLink.wrap("text/category"));
			main_categories = null;
			log.info("Renaming category [{}], moving entries to category [{}]", old, newn);
		} else {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openWarning(shell, Messages.Dochandle_errorCatNameAlreadyTaken,
				MessageFormat.format(Messages.DocHandle_errorCatNameAlreadyTakenMsg, newName));
			
		}
		clearCache();
	}
	
	public static void removeCategory(String name, String destName){
		getConnection().exec("update CH_ELEXIS_OMNIVORE_DATA set Category="
			+ JdbcLink.wrap(destName) + " where Category= " + JdbcLink.wrap(name));
		getConnection().exec("update CH_ELEXIS_OMNIVORE_DATA set deleted='1' where Title="
			+ JdbcLink.wrap(name) + " AND mimetype=" + JdbcLink.wrap("text/category"));
		main_categories = null;
		log.info("Removing category [{}], moving entries to category [{}]", name, destName);
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
	
	private void store(byte[] doc){
		try {
			storeContent(doc);
		} catch (PersistenceException e) {
			SWTHelper.showError(Messages.DocHandle_writeErrorCaption,
				Messages.DocHandle_writeErrorText + "; " + e.getMessage());
			delete();
		} catch (ElexisException e) {
			ExHandler.handle(e);
			SWTHelper.showError(Messages.DocHandle_73, Messages.DocHandle_writeErrorHeading,
				MessageFormat.format(Messages.DocHandle_writeErrorText2 + e.getCause(),
					e.getMessage()));
			delete();
		}
	}
	
	public void storeContent(byte[] doc) throws PersistenceException, ElexisException{
		File file = getStorageFile(false);
		if (file == null) {
			setBinary(FLD_DOC, doc);
		} else {
			try (BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file))) {
				bout.write(doc);
			}
			catch (Exception e) {
				throw new ElexisException(file.getAbsolutePath(), e);
			}
		}
	}
	

	/**
	 * If force is set or the preference Preferences.STOREFS is true a new File object is created.
	 * Else the file is a BLOB in the db and null is returned.
	 * 
	 * The path of the new file will be: Preferences.BASEPATH/PatientCode/
	 * 
	 * The name of the new file will be: PersistentObjectId.FileExtension
	 * 
	 * @param force
	 *            access to the file system
	 * @return File to read from, or write to, or null
	 */
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
					if (!file.exists()) {
						file = new File(subdir, getId() + "." //$NON-NLS-1$
							+ getFileExtension());
					}
					return file;
				}
			}
			if (Preferences.storeInFilesystem()) {
				configError();
			}
		}
		return null;
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
	
	public String getTitle(){
		return get(FLD_TITLE);
	}
	
	public String getKeywords(){
		return get(FLD_KEYWORDS);
	}
	
	public String getDate(){
		toStringTool.set(get(FLD_DATE));
		return toStringTool.toString(TimeTool.DATE_GER);
	}
	
	public void setDate(Date d){
		TimeTool tt = new TimeTool();
		tt.setTime(d);
		set(FLD_DATE, tt.toString(TimeTool.DATE_COMPACT));
	}
	
	@Override
	public String getCreationDate(){
		toStringTool.set(get(FLD_CREATION_DATE));
		return toStringTool.toString(TimeTool.DATE_GER);
		
	}
	
	public void setCreationDate(Date d){
		TimeTool tt = new TimeTool();
		tt.setTime(d);
		set(FLD_CREATION_DATE, tt.toString(TimeTool.DATE_COMPACT));
	}
	
	public byte[] getContents(){
		byte[] ret = getBinary(FLD_DOC);
		if (ret == null) {
			File file = getStorageFile(true);
			if (file != null) {
				try {
					byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
					// if we stored the file in the file system but decided
					// later to store it in the
					// database: copy the file from the file system to the
					// database
					if (!Preferences.storeInFilesystem()) {
						try {
							setBinary(FLD_DOC, bytes);
						} catch (PersistenceException pe) {
							SWTHelper.showError(Messages.DocHandle_readErrorCaption,
								Messages.DocHandle_importErrorText + "; " + pe.getMessage());
						}
					}
					
					return bytes;
				} catch (Exception ex) {
					ExHandler.handle(ex);
					SWTHelper.showError(Messages.DocHandle_readErrorHeading,
						Messages.DocHandle_importError2,
						MessageFormat.format(Messages.DocHandle_importErrorText2 + ex.getMessage(),
							file.getAbsolutePath()));
				}
			}
		}
		return ret;
	}
	
	public void execute(){
		try {
			String ext = StringConstants.SPACE; //""; //$NON-NLS-1$
			File temp = createTemporaryFile(getTitle());
			log.debug("execute {} readable {}", temp.getAbsolutePath(), Files.isReadable(temp.toPath()));

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
			SWTHelper.showError(Messages.DocHandle_runErrorHeading, ex.getMessage());
		}
	}
	
	private String getFileExtension() {
		String mimetype = get(FLD_MIMETYPE);
		String fileExtension = MimeTool.getExtension(mimetype);
		if (StringUtils.isBlank(fileExtension)) {
			fileExtension = FileTool.getExtension(mimetype);
			if (StringUtils.isBlank(fileExtension)) {
				fileExtension = FileTool.getExtension(get(FLD_TITLE));
			}
			if (StringUtils.isBlank(fileExtension) && StringUtils.isNotBlank(mimetype)) {
				fileExtension = mimetype;
			}
		}
		return fileExtension;
	}
	
	/**
	 * create a temporary file
	 * 
	 * @return temporary file
	 */
	public File createTemporaryFile(String title){
		String fileExtension = getFileExtension();
		
		if (fileExtension == null) {
			fileExtension = "";
		}
		
		String config_temp_filename = Utils.createNiceFileName(this);
		File temp = null;
		try {
			Path tmpDir = Files.createTempDirectory("elexis");
			if (config_temp_filename.length() > 0) {
				temp = new File(tmpDir.toString(), config_temp_filename + "." + fileExtension);
				
			} else {
				// use title if given
				if (title != null && !title.isEmpty()) {
					// Remove all characters that shall not appear in the generated filename
					String cleanTitle = title.replaceAll(java.util.regex.Matcher
							.quoteReplacement(Preferences.cotf_unwanted_chars), "_");
					if (!cleanTitle.toLowerCase().contains("." + fileExtension.toLowerCase())) {
						temp = new File(tmpDir.toString(), cleanTitle + "." + fileExtension);
					} else {
						temp = new File(tmpDir.toString(), cleanTitle);
					}
				} else {
					temp = Files.createTempFile(tmpDir, "omni_", "_vore." + fileExtension).toFile();
				}
			}
			tmpDir.toFile().deleteOnExit();
			temp.deleteOnExit();
	
			byte[] b = getContents(); // getBinary(FLD_DOC);
			if (b == null) {
				SWTHelper.showError(Messages.DocHandle_readErrorCaption2,
					Messages.DocHandle_loadErrorText);
				return temp;
			}
			try (FileOutputStream fos = new FileOutputStream(temp)) {
				fos.write(b);
			}
			log.debug("createTemporaryFile {} size {} ext {} ", temp.getAbsolutePath(),
				Files.size(temp.toPath()), fileExtension);
		} catch (FileNotFoundException e) {
			log.debug("File not found " + e, Log.WARNINGS);
		} catch (IOException e) {
			log.debug("Error creating file " + e, Log.WARNINGS);
		}
		
		return temp;
	}
	
	public String getMimetype(){
		return get(FLD_MIMETYPE);
	}
	
	public boolean storeExternal(String filename){
		byte[] b = getContents();
		if (b == null) {
			SWTHelper.showError(Messages.DocHandle_readErrorCaption2,
				Messages.DocHandle_couldNotLoadError);
			return false;
		}
		try (FileOutputStream fos = new FileOutputStream(filename)) {
			fos.write(b);
			return true;
		} catch (IOException ios) {
			ExHandler.handle(ios);
			SWTHelper.showError(Messages.DocHandle_writeErrorCaption2,
				Messages.DocHandle_writeErrorCaption2, ios.getMessage());
			return false;
		}
	}
	
	public static List<DocHandle> assimilate(List<ImageData> images){
		List<DocHandle> ret = new ArrayList<DocHandle>();
		FileImportDialog fid = new FileImportDialog(Messages.DocHandle_scannedImageDialogCaption);
		if (fid.open() == Dialog.OK) {
			try {
				Document pdf = new Document(PageSize.A4);
				pdf.setMargins(0, 0, 0, 0);
				ByteArrayOutputStream baos = new ByteArrayOutputStream(100000);
				PdfWriter.getInstance(pdf, baos);
				pdf.open();
				ImageLoader il = new ImageLoader();
				for (int i = 0; i < images.size(); i++) {
					ImageData[] id = new ImageData[] {
						images.get(i)
					};
					il.data = id;
					ByteArrayOutputStream bimg = new ByteArrayOutputStream();
					il.save(bimg, SWT.IMAGE_PNG);
					Image image = Image.getInstance(bimg.toByteArray());
					int width = id[0].width;
					int height = id[0].height;
					// 210mm = 8.27 In = 595 px bei 72dpi
					// 297mm = 11.69 In = 841 px
					if ((width > 595) || (height > 841)) {
						image.scaleToFit(595, 841);
					}
					pdf.add(image);
				}
				pdf.close();
				DocHandle docHandle = new DocHandle(fid.category, baos.toByteArray(),
					ElexisEventDispatcher.getSelectedPatient(), fid.originDate, fid.title,
					"image.pdf", fid.keywords); //$NON-NLS-1$
				Utils.archiveFile(docHandle.getStorageFile(true), docHandle);
				ret.add(docHandle);
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(Messages.DocHandle_readError,
					Messages.DocHandle_readErrorText2);
			}
		}
		return ret;
	}
	
	public static DocHandle assimilate(String f){
		Patient act = ElexisEventDispatcher.getSelectedPatient();
		if (act == null) {
			SWTHelper.showError(Messages.DocHandle_noPatientSelected,
				Messages.DocHandle_pleaseSelectPatient);
			return null;
		}
		File file = new File(f);
		if (!file.canRead()) {
			SWTHelper.showError(Messages.DocHandle_cantReadCaption,
				MessageFormat.format(Messages.DocHandle_cantReadText, f));
			return null;
		}
		
		// can't import complete directory
		if (file.isDirectory()) {
			SWTHelper.showError(Messages.DocHandle_importErrorDirectory,
				Messages.DocHandle_importErrorDirectoryText);
			return null;
		}
		
		FileImportDialog fid = new FileImportDialog(file.getName());
		if (fid.open() == Dialog.OK) {
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
				int in;
				while ((in = bis.read()) != -1) {
					baos.write(in);
				}
				String nam = file.getName();
				if (nam.length() > 255) {
					SWTHelper.showError(Messages.DocHandle_readErrorCaption3,
						Messages.DocHandle_fileNameTooLong);
					return null;
				}
				String category = fid.category;
				if (category == null || category.length() == 0) {
					category = DocHandle.getDefaultCategory().getCategoryName();
				}
				DocHandle dh = new DocHandle(category, baos.toByteArray(), act, fid.originDate,
					fid.title, file.getName(), fid.keywords);
				if (Preferences.getDateModifiable()) {
					dh.setDate(fid.saveDate);
					dh.setCreationDate(fid.originDate);
				}
				return dh;
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(Messages.DocHandle_readErrorCaption3,
					Messages.DocHandle_readErrorText2);
			}
		}
		return null;
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
				String.format(Messages.DocHandle_cantReadMessage, f));
			return null;
		}
		
		// can't import complete directory
		if (file.isDirectory()) {
			SWTHelper.showError(Messages.DocHandle_importErrorDirectory,
				Messages.DocHandle_importErrorDirectoryText);
			return null;
		}
		
		Integer maxOmnivoreFilenameLength = Preferences.getOmnivoreMax_Filename_Length();
		
		String nam = file.getName();
		if (nam.length() > maxOmnivoreFilenameLength) {
			SWTHelper.showError(Messages.DocHandle_importErrorCaption, MessageFormat
				.format(Messages.DocHandle_importErrorMessage, maxOmnivoreFilenameLength));
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
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
					ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				
				int in;
				while ((in = bis.read()) != -1) {
					baos.write(in);
				}
				
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
				dh = new DocHandle(category, baos.toByteArray(), act, fid.title.trim(),
					file.getName(), fid.keywords.trim());
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(Messages.DocHandle_importErrorCaption,
					Messages.DocHandle_importErrorMessage2);
				return null;
			}
			Utils.archiveFile(file, dh);
		}
		return dh;
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
	
	/**
	 * Move the DocHandle from the db to the file system and delete the BLOB afterwards.
	 */
	public boolean exportToFileSystem(){
		byte[] doc = getBinary(FLD_DOC);
		// return true if doc is already on file system
		if (doc == null)
			return true;
		File file = getStorageFile(true);
		try (BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file))) {
			bout.write(doc);
			setBinary(FLD_DOC, null);
		} catch (IOException ios) {
			ExHandler.handle(ios);
			log.warn("Exporting dochandle [{}] to filesystem fails.", getId(), ios);
			SWTHelper.showError(Messages.DocHandle_writeErrorCaption2,
				Messages.DocHandle_writeErrorCaption2, ios.getMessage());
			return false;
		}
		return true;
	}
	
}
