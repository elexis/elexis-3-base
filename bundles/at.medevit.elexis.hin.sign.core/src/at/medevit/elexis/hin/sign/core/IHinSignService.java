package at.medevit.elexis.hin.sign.core;

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
	 * @param chmed
	 * @return
	 */
	public ObjectStatus<?> verifyPrescription(String chmed);

	/**
	 * Mark a signed ePrescription QR Code as revoked
	 * 
	 * @param signedchmed
	 * @return
	 */
	public ObjectStatus<?> revokePrescription(String chmed);

}
