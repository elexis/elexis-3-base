package org.iatrix.data;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IDiagnose;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.icpc.Episode;
import ch.rgw.tools.StringTool;

public class KonsDiagnosisUpdateForIatrix extends ExternalMaintenance {
	
	protected static Logger log = LoggerFactory.getLogger(KonsDiagnosisUpdateForIatrix.class);
	private static int nrItems = -1;
	private static int curItem = -1;
	
	/**
	 * https://redmine.medelexis.ch/issues/14971
	 * Migration der KG Iatrix-Diagnosen nach Patientendetails
	 * 
	 * @since 3.8
	 */
	public KonsDiagnosisUpdateForIatrix(){}
	
	private void addDiagnoseIfNotExists(Patient actPat, String newDiag) {
		if (newDiag.length() == 0 || newDiag.contentEquals(" ")) {
			log.debug("{}/{} {} skip empty diagnose {}",curItem, nrItems, actPat.getPersonalia(), newDiag);
			return;
		}
		String oldDiag = actPat.getDiagnosen();
		if (!oldDiag.contains(newDiag)) {
			log.debug("{}/{} {} added {}",curItem, nrItems, actPat.getPersonalia(), newDiag);
			if (oldDiag.length() == 0) {
				actPat.setDiagnosen(newDiag);
			} else {
				actPat.setDiagnosen(oldDiag + StringTool.crlf + newDiag.toString());
			}
		}
	}
	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
		StringBuilder sb = new StringBuilder();
		Query<Episode> query = new Query<Episode>(Episode.class);
		query.clear(true);
		query.add(Episode.FLD_LASTUPDATE, Query.NOT_EQUAL, null);
		query.add(Episode.FLD_DELETED, Query.EQUALS, "0"); // not deleted
		query.add(Episode.FLD_STATUS, Query.EQUALS, "1"); // status active
		List<Episode> alleIatricIcpcEpisoden = query.execute();
		log.debug("Found {} items", alleIatricIcpcEpisoden.size());
		nrItems = alleIatricIcpcEpisoden.size();
		pm.beginTask("Moving (Iatrix) ICPC Episodes#Diagnosis ...", nrItems);
		curItem = 0;
		for (Episode episode : alleIatricIcpcEpisoden) {
			curItem++;
			Patient actPat = episode.getPatient();
			if (episode.getId().contentEquals("1")) {
				continue; // Version
			}
			String newDiag = episode.getTitle();
			if (episode.getStartDate().length() > 0) {
				newDiag = episode.getStartDate() + " " + episode.getTitle();
			}
			if (actPat != null) {
				List<IDiagnose> diagnoses = episode.getDiagnoses();
				if (diagnoses.size() > 0) {
					for (IDiagnose diag : diagnoses) {
						if (newDiag.length() == 0) {
							newDiag = diag.getLabel();
						} else {
							newDiag = newDiag + " " + diag.getLabel();
						}
						addDiagnoseIfNotExists(actPat, newDiag);
						newDiag = "";
					}
				} else {
					addDiagnoseIfNotExists(actPat, episode.getStartDate() + " " + episode.getTitle());
				}
			}
			pm.worked(1);
		}
		pm.done();
		return sb.toString();
	}


	@Override
	public String getMaintenanceDescription() {
		return "[14971] Übertragung Diagnose aus Iatrix zur Patientenübersicht";
	}
	
}
