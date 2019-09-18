package at.medevit.elexis.bluemedication.core;

import java.io.File;
import java.util.Optional;

import ch.elexis.data.Patient;
import ch.rgw.tools.Result;

public interface BlueMedicationService {
	
	/**
	 * Upload a document as {@link File} to the bluemedication service.
	 * 
	 * @param patient
	 * @param document
	 * @return
	 */
	public Result<UploadResult> uploadDocument(Patient patient, File document);
	
	/**
	 * Download the results of upload and user interaction on the browser, from the bluemedication
	 * service.
	 * 
	 * @param uploadResult
	 * @return
	 */
	public Result<String> downloadEMediplan(UploadResult uploadResult);
	
	/**
	 * Add a pending {@link UploadResult} to the map of pending results. One object can only have
	 * one {@link UploadResult}.
	 * 
	 * @param object
	 * @param uploadResult
	 */
	public void addPendingUploadResult(Object object, UploadResult uploadResult);
	
	/**
	 * Get a pending {@link UploadResult} for the object.
	 * 
	 * @param object
	 * @return
	 */
	public Optional<UploadResult> getPendingUploadResult(Object object);
	
	/**
	 * Remove a pending {@link UploadResult} for the object. If none is found the method just
	 * returns.
	 * 
	 * @param object
	 */
	public void removePendingUploadResult(Object object);
}
