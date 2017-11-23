package ch.elexis.data.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.ui.importer.div.importers.AccessWrapper;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class TarmedReferenceDataImporter extends AbstractReferenceDataImporter {
	private static final Logger logger = LoggerFactory.getLogger(TarmedReferenceDataImporter.class);
	
	public static final String CFG_REFERENCEINFO_AVAILABLE =
		"ch.elexis.data.importer.TarmedReferenceDataImporter/referenceinfoavailable";
	
	public static final String ImportPrefix = "TARMED_IMPORT_";
	
	protected JdbcLink cacheDb = null; // As we have problems parsing dates using the postgresql-JdBC,
	protected String lang;
	
	private AccessWrapper aw;
	private String mdbFilename;
	private Set<String> cachedDbTables = null;
	
	protected boolean updateIDs = false;
	protected boolean showRestartDialog = true;
	
	protected int chapterCount;
	protected int servicesCount;
	
	/**
	 * Only for unit tests! Suppress dialog at end of import
	 */
	public void suppressRestartDialog(){
		showRestartDialog = false;
	}
	
	@Override
	public @NonNull Class<?> getReferenceDataTypeResponsibleFor(){
		return TarmedLeistung.class;
	}
	
	@Override
	public int getCurrentVersion(){
		return TarmedLeistung.getCurrentVersion();
	}
	
	@Override
	public IStatus performImport(@Nullable IProgressMonitor ipm, InputStream input,
		@Nullable Integer version){
		if (ipm == null) {
			ipm = new NullProgressMonitor();
		}
		
		// init database connection
		cacheDb = new JdbcLink("org.h2.Driver", "jdbc:h2:mem:tarmed_import", "h2");
		cacheDb.connect("", "");
		
		if (openAccessDatabase(input) != Status.OK_STATUS
			|| deleteCachedAccessTables() != Status.OK_STATUS
			|| importAllAccessTables() != Status.OK_STATUS) {
			cachedDbTables = null;
			return Status.CANCEL_STATUS;
		}
		
		ipm.beginTask(Messages.TarmedImporter_importLstg, chapterCount + servicesCount);
		
		lang = JdbcLink.wrap(CoreHub.localCfg.get(Preferences.ABL_LANGUAGE, "d").toUpperCase()); //$NON-NLS-1$
		ipm.subTask(Messages.TarmedImporter_connecting);
		
		// always convert ids if there are old ids in the database
		TarmedLeistung leistung = TarmedLeistung.load("00.0010");
		if (leistung.exists()) {
			updateIDs = true;
		}
		
		IStatus ret = Status.OK_STATUS;
		try {
			DeleteOldData deleteOldData = getDeleteOldData();
			ret = deleteOldData.delete(ipm);
			if (ret.isOK()) {
				DefinitionImport definitionImport = getDefinitionImport();
				ret = definitionImport.doImport(ipm);
				if (ret.isOK()) {
					ChapterImporter chapterImporter = getChapterImporter();
					chapterImporter.setChapterCount(chapterCount);
					ret = chapterImporter.doImport(ipm);
					if (ret.isOK()) {
						GroupImporter groupImporter = getGroupImporter();
						ret = groupImporter.doImport(ipm);
						if (ret.isOK()) {
							ServiceImporter serviceImporter = getServiceImporter(chapterImporter);
							serviceImporter.setServiceCount(servicesCount);
							ret = serviceImporter.doImport(ipm);
							if (ret.isOK()) {
								IStatus updateVerrechnetResult = Status.OK_STATUS;
								IStatus updateBlockResult = Status.OK_STATUS;
								IStatus updateStatistics = Status.OK_STATUS;
								if (updateIDs) {
									IdsUpdater idsUpdater = new IdsUpdater(getLaw());
									updateVerrechnetResult = idsUpdater.updateVerrechnet(ipm);
									updateBlockResult = idsUpdater.udpateLeistungsBlock(ipm);
									updateStatistics = idsUpdater.updateStatistics(ipm);
								}
								
								if (version == null) {
									TarmedLeistung
										.setVersion(new TimeTool().toString(TimeTool.DATE_COMPACT));
								} else {
									TarmedLeistung.setVersion(version.toString());
								}
								CoreHub.globalCfg.set(
									TarmedReferenceDataImporter.CFG_REFERENCEINFO_AVAILABLE, true);
								ipm.done();
								String message = Messages.TarmedImporter_successMessage;
								if (!updateBlockResult.isOK()) {
									message =
										message + "\n" + Messages.TarmedImporter_updateBlockWarning;
								}
								if (showRestartDialog) {
									SWTHelper.showInfo(Messages.TarmedImporter_successTitle,
										message);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Error importing tarmed", ex);
		} finally {
			if (deleteCachedAccessTables() != Status.OK_STATUS) {
				return Status.CANCEL_STATUS;
			}
		}
		return ret;
	}
	
	private GroupImporter getGroupImporter(){
		return new GroupImporter(cacheDb, lang, getLaw());
	}
	
	/**
	 * Get the {@link ServiceImporter} to use, should be overridden for special implementations.
	 * 
	 * @return
	 */
	protected ServiceImporter getServiceImporter(ChapterImporter chapterImporter){
		return new ServiceImporter(cacheDb, chapterImporter, lang, getLaw());
	}
	
	/**
	 * Get the {@link ChapterImporter} to use, should be overridden for special implementations.
	 * 
	 * @return
	 */
	protected ChapterImporter getChapterImporter(){
		return new ChapterImporter(cacheDb, lang, getLaw());
	}
	
	/**
	 * Get the {@link DefinitionImport} to use, should be overridden for special implementations.
	 * 
	 * @return
	 */
	protected DefinitionImport getDefinitionImport(){
		return new DefinitionImport(cacheDb, lang, getLaw());
	}
	
	/**
	 * Get the {@link DeleteOldData} to use, should be overridden for special implementations.
	 * 
	 * @return
	 */
	protected DeleteOldData getDeleteOldData(){
		return new DeleteOldData(getLaw());
	}
	
	protected String getLaw(){
		return "";
	}
	
	/**
	 * Import all Access tables (using cache cachedDbTables)
	 */
	private IStatus importAllAccessTables(){
		String tablename = "";
		Iterator<String> iter;
		try {
			chapterCount = aw.getDatabase().getTable("KAPITEL_TEXT").getRowCount();
			servicesCount = aw.getDatabase().getTable("LEISTUNG").getRowCount();
			
			iter = cachedDbTables.iterator();
			while (iter.hasNext()) {
				tablename = iter.next();
				try {
					aw.convertTable(tablename, cacheDb);
					createIndexForTable(tablename, cacheDb);
				} catch (SQLException e) {
					logger.error("Failed to import table " + tablename, e);
					return Status.CANCEL_STATUS;
				}
			}
			return Status.OK_STATUS;
		} catch (IOException e) {
			logger.error("Failed to process access file " + mdbFilename, e);
			return Status.CANCEL_STATUS;
		}
	}
	
	private void createIndexForTable(String tablename, JdbcLink cacheDb){
		String cacheTableName = ImportPrefix + tablename;
		if ("LEISTUNG_TEXT".equals(tablename)) {
			createIndexOn(cacheTableName, "_IDX1", "LNR");
			createIndexOn(cacheTableName, "_IDX2", "SPRACHE");
		} else if ("LEISTUNG_DIGNIQUALI".equals(tablename)) {
			createIndexOn(cacheTableName, "_IDX1", "LNR");
		} else if ("LEISTUNG_HIERARCHIE".equals(tablename)) {
			createIndexOn(cacheTableName, "_IDX1", "LNR_MASTER");
		} else if ("LEISTUNG_GRUPPEN".equals(tablename)) {
			createIndexOn(cacheTableName, "_IDX1", "LNR");
		} else if ("LEISTUNG_BLOECKE".equals(tablename)) {
			createIndexOn(cacheTableName, "_IDX1", "LNR");
		} else if ("LEISTUNG_KOMBINATION".equals(tablename)) {
			createIndexOn(cacheTableName, "_IDX1", "LNR_MASTER");
		} else if ("LEISTUNG_MENGEN_ZEIT".equals(tablename)) {
			createIndexOn(cacheTableName, "_IDX1", "LNR");
		} else if ("LEISTUNG_KUMULATION".equals(tablename)) {
			createIndexOn(cacheTableName, "_IDX1", "LNR_MASTER");
		}
	}
	
	private void createIndexOn(String tablename, String indexPrefix, String columnName){
		Stm stm = cacheDb.getStatement();
		try {
			stm.exec("CREATE INDEX " + tablename + indexPrefix + " on " + tablename + " ("
				+ columnName + ");");
		} finally {
			cacheDb.releaseStatement(stm);
		}
		logger.debug(
			"Created cache db index [" + tablename + indexPrefix + "] on [" + columnName + "]");
	}
	
	private IStatus openAccessDatabase(InputStream inputStream){
		File file = convertInputStreamToFile(inputStream);
		if (mdbFilename == null)
			mdbFilename = file.getName();
		try {
			aw = new AccessWrapper(file);
			aw.setPrefixForImportedTableNames(ImportPrefix);
			cachedDbTables = aw.getDatabase().getTableNames();
		} catch (IOException e) {
			logger.error("Failed to open access file " + file, e);
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
	
	private IStatus deleteCachedAccessTables(){
		String tablename = "";
		Iterator<String> iter;
		iter = cachedDbTables.iterator();
		while (iter.hasNext()) {
			tablename = iter.next();
			cacheDb.exec("DROP TABLE IF EXISTS " + tablename);//$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}
	
	private File convertInputStreamToFile(InputStream input){
		String prefix = "tarmed_db";
		String suffix = "tmp";
		
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(prefix, suffix);
			tmpFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tmpFile);
			IOUtils.copy(input, out);
		} catch (IOException e) {
			logger.error("Error reading input stream ...", e);
		}
		return tmpFile;
	}
}
