package ch.elexis.omnivore.data.dbcheck;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.data.Query;
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.data.DocHandle;

public class FixOrDeleteInvalidDocHandles extends ExternalMaintenance{
	
	private Logger logger = LoggerFactory.getLogger(FixOrDeleteInvalidDocHandles.class);
	
	private int deleteCount;
	private int repairCount;
	
	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
		// query all except version and categories
		Query<DocHandle> query = new Query<>(DocHandle.class);
		query.add(DocHandle.FLD_ID, Query.NOT_EQUAL, "1");
		query.add(DocHandle.FLD_MIMETYPE , Query.NOT_EQUAL, Constants.CATEGORY_MIMETYPE);
		List<DocHandle> allDocHandles = query.execute();
		pm.beginTask("Bitte warten, Omnivore Eiträge werden geprüft ...", allDocHandles.size());
		deleteCount = 0;
		repairCount = 0;
		for (DocHandle docHandle : allDocHandles) {
			byte[] ret = docHandle.getBinary(DocHandle.FLD_DOC);
			if (ret == null) {
				File file = docHandle.getStorageFile(true);
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
	
	private void repair(DocHandle docHandle, File file){
		repairCount++;
		logger.warn("Repair DocHandle [" + docHandle.getLabel() + "] of patient ["
			+ (docHandle.getPatient() != null ? docHandle.getPatient().getPatCode() : "no patient")
			+ "] with file [" + file.getName() + "]");
		docHandle.set(DocHandle.FLD_MIMETYPE, file.getName());
	}
	
	private void delete(DocHandle docHandle){
		deleteCount++;
		logger.warn("Delete DocHandle [" + docHandle.getLabel() + "] of patient ["
			+ (docHandle.getPatient() != null ? docHandle.getPatient().getPatCode() : "no patient")
			+ "]");
		docHandle.delete();
	}
	
	@Override
	public String getMaintenanceDescription(){
		return "Omnivore Einträge überprüfen, reparieren oder entfernen";
	}
	
}
