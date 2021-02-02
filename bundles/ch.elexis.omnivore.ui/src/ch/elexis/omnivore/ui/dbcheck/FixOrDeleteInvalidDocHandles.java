package ch.elexis.omnivore.ui.dbcheck;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.util.Utils;
import ch.elexis.omnivore.ui.service.OmnivoreModelServiceHolder;

public class FixOrDeleteInvalidDocHandles extends ExternalMaintenance {
	
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
		try (IQueryCursor<IDocumentHandle> cursor = query.executeAsCursor()) {
			pm.beginTask("Bitte warten, Omnivore Eiträge werden geprüft ...", cursor.size());
			deleteCount = 0;
			repairCount = 0;
			while (cursor.hasNext()) {
				IDocumentHandle docHandle = cursor.next();
				logger.info("Loading content of DocHandle [" + docHandle.getId() + "]");
				InputStream ret = docHandle.getContent();
				if (ret == null) {
					IVirtualFilesystemHandle vfsHandle = Utils.getStorageFile(docHandle, true);
					try {
						if (vfsHandle != null && !vfsHandle.exists()) {
							// perform lookup in directory with id
							IVirtualFilesystemHandle directory = vfsHandle.getParent();
							if (directory != null && directory.exists()) {
								IVirtualFilesystemHandle[] handles =
									directory.listHandles(handle -> Objects.equals(
										FilenameUtils.getBaseName(handle.getName()),
										docHandle.getId()));
								if (handles.length > 0) {
									repair(docHandle, handles[0]);
								} else {
									delete(docHandle);
								}
							} else {
								delete(docHandle);
							}
						}
					} catch (IOException e) {
						logger.warn("DocHandle [" + docHandle.getId() + "]", e);
					}
				}
			}
		}
		return "Es wurden " + deleteCount + " Einträge entfernt (Details siehe Log)\nEs wurden "
			+ repairCount + " Einträge repariert (Details siehe Log)";
	}
	
	private void repair(IDocumentHandle docHandle, IVirtualFilesystemHandle file){
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
