package ch.elexis.fire.core;

import org.hl7.fhir.r4.model.Bundle;

import ch.elexis.core.model.IConfig;
import ch.elexis.core.model.IPatient;

public interface IFIREService {

	/**
	 * Initial FIRE export of all {@link IPatient}s and related medical data of the
	 * practice. A timestamp fo the exporting instance is written to {@link IConfig}
	 * initialExport.
	 * 
	 * @return
	 */
	public Bundle initialExport();

	/**
	 * Get the timestamp of the initial export. Returns -1 if no initial export
	 * performed yet.
	 * 
	 * @return
	 */
	public Long getInitialTimestamp();

	/**
	 * Incremental FIRE export of all {@link IPatient}s and related medical data
	 * changed since the last export.A timestamp fo the exporting instance is
	 * written to {@link IConfig} incrementalExport.
	 * 
	 * @param timestamp of last export to determine changes since then
	 * @return
	 */
	public Bundle incrementalExport(Long timestamp);

	/**
	 * Get the timestamp of the last incremental export. Returns -1 if no
	 * incremental export performed yet.
	 * 
	 * @return
	 */
	public Long getIncrementalTimestamp();
}
