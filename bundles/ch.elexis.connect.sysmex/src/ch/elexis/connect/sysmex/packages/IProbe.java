package ch.elexis.connect.sysmex.packages;

import ch.elexis.data.Patient;

public interface IProbe {
	
	/**
	 * Get the patient id of the probe, can be null or empty.
	 * 
	 * @return
	 */
	public String getPatientId();
	
	/**
	 * Write the laboratory data of the parsed probe for the provided patient.
	 * 
	 * @param selectedPatient
	 * @throws PackageException
	 */
	public void write(Patient selectedPatient) throws PackageException;
	
	/**
	 * Get the required size of data for the probe.
	 * 
	 * @return
	 */
	public int getSize();
	
	/**
	 * Parse the content data of right size and prepare the data for write.
	 * 
	 * @param content
	 */
	public void parse(String content);
	
}
