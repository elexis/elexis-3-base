package at.medevit.elexis.hin.sign.core;

import java.io.InputStream;

import ch.elexis.core.status.ObjectStatus;

public interface IHinSignService {

	public enum Mode {
		TEST, PROD
	}

	public void setMode(Mode mode);

	/**
	 * Create a signed ePrescription QR Code.
	 * 
	 * @param data
	 * @return
	 */
	public ObjectStatus<?> createPrescription(InputStream data);

	/**
	 * Mark a signed ePrescription QR Code as revoked
	 * 
	 * @param data
	 * @return
	 */
	public ObjectStatus<?> revokePrescription(InputStream data);
}
