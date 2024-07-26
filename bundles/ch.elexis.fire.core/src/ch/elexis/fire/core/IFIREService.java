package ch.elexis.fire.core;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hl7.fhir.r4.model.Bundle;

import ch.elexis.core.model.IConfig;
import ch.elexis.core.model.IPatient;

public interface IFIREService {

	/**
	 * Initial FIRE export of all {@link IPatient}s and related medical data of the
	 * practice. A timestamp fo the exporting instance is written to {@link IConfig}
	 * initialExport.
	 * 
	 * @param progressMonitor
	 * 
	 * @return json files containing FHIR bundles
	 */
	public List<File> initialExport(IProgressMonitor progressMonitor);

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
	 * @return json files containing FHIR bundles
	 */
	public List<File> incrementalExport(Long timestamp, IProgressMonitor progressMonitor);

	/**
	 * Get the timestamp of the last incremental export. Returns -1 if no
	 * incremental export performed yet.
	 * 
	 * @return
	 */
	public Long getIncrementalTimestamp();

	/**
	 * Upload the file containing json FHIR bundle to the fire server.
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean uploadBundle(File file);

	/**
	 * Read the FHIR bundle from the file.
	 * 
	 * @param file
	 * @return
	 */
	public Bundle readBundle(File file);
}
