package at.medevit.elexis.hin.sign.core;

import java.io.OutputStream;
import java.util.Optional;

import ch.elexis.core.model.IRecipe;
import ch.elexis.core.status.ObjectStatus;

public interface IHinSignService {

	public enum Mode {
		TEST, PROD
	}

	public void setMode(Mode mode);

	/**
	 * Create a signed ePrescription QR Code.
	 * 
	 * @param chmed
	 * @return
	 */
	public ObjectStatus<?> createPrescription(String chmed);

	/**
	 * Verifying an e-prescription QR Code.
	 * 
	 * @param chmedUrl
	 * @return
	 */
	public ObjectStatus<?> verifyPrescription(String chmedUrl);

	/**
	 * Mark a signed ePrescription QR Code as revoked
	 * 
	 * @param signedchmed
	 * @return
	 */
	public ObjectStatus<?> revokePrescription(String chmed);

	/**
	 * Attach the prescription url to the {@link IRecipe}.
	 * 
	 * @param iRecipe
	 * @param url
	 */
	public void setPrescriptionUrl(IRecipe iRecipe, String url);

	/**
	 * Get the attached prescription url for the {@link IRecipe}.
	 * 
	 * @param iRecipe
	 * @param url
	 * @return
	 */
	public Optional<String> getPrescriptionUrl(IRecipe iRecipe);

	/**
	 * Generate a pdf with the eprescription signed url as QR code.
	 * 
	 * @param iRecipe
	 * @return
	 */
	public ObjectStatus<?> exportPrescriptionPdf(IRecipe iRecipe, OutputStream output);
}
