package ch.docbox.elexis.ui.dbcheck;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.model.util.ElexisIdGenerator;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.data.Query;

public class FixDocboxTerminIds extends ExternalMaintenance {

	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion) {
		pm.beginTask("Bitte warten, Ids werden aktualisiert ...", IProgressMonitor.UNKNOWN);
		StringBuilder output = new StringBuilder();

		Query<Termin> terminQuery = new Query<Termin>(Termin.class);
		terminQuery.add("id", "LIKE", "%docbox%");
		List<Termin> execute = terminQuery.execute();
		for (Termin termin : execute) {
			String docboxTerminId = termin.getId();
			String id = ElexisIdGenerator.generateId();
			termin.set(new String[] { "id", Termin.FLD_EXTENSION }, id, docboxTerminId);
			output.append(docboxTerminId + "->" + id + "\n");
			pm.worked(1);
		}

		pm.done();

		return output.toString();
	}

	@Override
	public String getMaintenanceDescription() {
		return "[25256] IDs von Docbox Terminen web-kompatibel machen";
	}

}
