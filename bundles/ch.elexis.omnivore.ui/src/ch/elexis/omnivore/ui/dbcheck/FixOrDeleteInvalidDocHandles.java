package ch.elexis.omnivore.ui.dbcheck;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.util.Utils;
import ch.elexis.omnivore.ui.service.OmnivoreModelServiceHolder;

public class FixOrDeleteInvalidDocHandles extends ExternalMaintenance{
	
	private Logger logger = LoggerFactory.getLogger(FixOrDeleteInvalidDocHandles.class);
	
	private int deleteCount;
	private int repairCount;
	
	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
		// query all except version and categories
		IQuery<IDocumentHandle> query =
			OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
		query.and("id", COMPARATOR.NOT_EQUALS, "1");
		query.and(ModelPackage.Literals.IDOCUMENT__MIME_TYPE, COMPARATOR.NOT_EQUALS,
			Constants.CATEGORY_MIMETYPE);
		List<IDocumentHandle> allDocHandles = query.execute();
		pm.beginTask("Bitte warten, Omnivore Eiträge werden geprüft ...", allDocHandles.size());
		deleteCount = 0;
		repairCount = 0;
		for (IDocumentHandle docHandle : allDocHandles) {
			InputStream ret = docHandle.getContent();
			if (ret == null) {
				File file = Utils.getStorageFile(docHandle, true);
				if (file != null && !file.exists()) {
					// perform lookup in directory with id
					File directory = file.getParentFile();
					if (directory != null && directory.exists()) {
						File[] matchingFiles = directory.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name){
								return FilenameUtils.getBaseName(name).equals(docHandle.getId());
							}
						});
						if (matchingFiles.length > 0) {
							repair(docHandle, matchingFiles[0]);
						} else {
							delete(docHandle);
						}
					} else {
						delete(docHandle);
					}
				}
			}
		}
		return "Es wurden " + deleteCount + " Einträge entfernt (Details siehe Log)\nEs wurden "
			+ repairCount + " Einträge repariert (Details siehe Log)";
	}
	
	private void repair(IDocumentHandle docHandle, File file){
		repairCount++;
		logger.warn("Repair DocHandle [" + docHandle.getLabel() + "] of patient ["
			+ (docHandle.getPatient() != null ? docHandle.getPatient().getPatientNr()
					: "no patient")
			+ "] with file [" + file.getName() + "]");
		docHandle.setMimeType(file.getName());
	}
	
	private void delete(IDocumentHandle docHandle){
		deleteCount++;
		logger.warn("Delete DocHandle [" + docHandle.getLabel() + "] of patient ["
			+ (docHandle.getPatient() != null ? docHandle.getPatient().getPatientNr()
					: "no patient")
			+ "]");
		OmnivoreModelServiceHolder.get().delete(docHandle);
	}
	
	@Override
	public String getMaintenanceDescription(){
		return "Omnivore Einträge überprüfen, reparieren oder entfernen";
	}
	
}
