package at.medevit.elexis.bluemedication.core;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

import ch.elexis.core.model.IPatient;
import ch.rgw.tools.Result;

public interface BlueMedicationService {

	/**
	 * Upload a document as {@link File} to the bluemedication service. Result typ
	 * is chmed.
	 *
	 * @param patient
	 * @param document
	 * @return
	 */
	public default Result<UploadResult> uploadDocument(IPatient patient, File document) {
		return uploadDocument(patient, document, "chmed");
	}

	/**
	 * Upload a document as {@link File} to the bluemedication service.<br />
	 * Result typ:
	 * <li>chmed - emediplan can be imported in patients medication</li>
	 * <li>pdf - pdf including medication info, can be imported in patients
	 * documents</li>
	 *
	 * @param patient
	 * @param document
	 * @param resulttyp
	 * @return
	 */
	public Result<UploadResult> uploadDocument(IPatient patient, File document, String resulttyp);

	/**
	 * Generate a chmed emediplan with the active medication of the patient, and
	 * upload it to the medication check of the bluemedication service.<br />
	 *
	 * @param patient
	 * @return
	 */
	public Result<UploadResult> uploadCheck(IPatient patient);

	/**
	 * Notify the bluemedication service that an chmed emediplan was generated.
	 *
	 * @param patient
	 * @return
	 */
	public Result<String> emediplanNotification(IPatient patient);

	/**
	 * Download the results of upload and user interaction on the browser, from the
	 * bluemedication service. Result typ of upload was chmed.
	 *
	 * @param uploadResult
	 * @return
	 */
	public Result<String> downloadEMediplan(UploadResult uploadResult);

	/**
	 * Download the results of upload and user interaction on the browser, from the
	 * bluemedication service. Result typ of upload was pdf.
	 *
	 * @param uploadResult
	 * @return
	 */
	public Result<String> downloadPdf(UploadResult uploadResult);

	/**
	 * Download the results of upload and user interaction on the browser. The
	 * result is the original pdf extended with the extracted emediplan.
	 * 
	 * @param uploadResult
	 * @return
	 */
	public Result<String> downloadExtendedPdf(UploadResult uploadResult);

	/**
	 * Add a pending {@link UploadResult} to the map of pending results. One object
	 * can only have one {@link UploadResult}.
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
	 * Remove a pending {@link UploadResult} for the object. If none is found the
	 * method just returns.
	 *
	 * @param object
	 */
	public void removePendingUploadResult(Object object);

	/**
	 * Start long polling for the result in background thread. The {@link Consumer}
	 * onSuccess is called with the provided object if the result is success.
	 *
	 * @param object
	 * @param uploadResult
	 * @param onSuccess
	 */
	public void startPollForResult(Object object, UploadResult uploadResult, Consumer<Object> onSuccess);

}
