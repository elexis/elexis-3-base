package at.medevit.ch.artikelstamm.elexis.common.ui.dbcheck;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;

public class FixPrescriptionArtikelstammReferences extends ExternalMaintenance {
	
	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
		StringBuilder sb = new StringBuilder();
		Query<Prescription> qre = new Query<Prescription>(Prescription.class);
		qre.add(Prescription.FLD_ARTICLE, Query.LIKE, ArtikelstammItem.class.getName() + "%");
		List<Prescription> execute = qre.execute();
		pm.beginTask("Fixing references", qre.size());
		int total = execute.size();
		int fixed = 0;
		int failed = 0;
		for (Prescription pres : execute) {
			Artikel artikel = pres.getArtikel();
			if (!artikel.exists()) {
				// ch.artikelstamm.elexis.common.ArtikelstammItem::076805130701151538776P022
				String string = pres.get(Prescription.FLD_ARTICLE);
				String replaceAll =
					string.replaceAll("ch.artikelstamm.elexis.common.ArtikelstammItem::", "");
				String substring = replaceAll.substring(0, replaceAll.length() - 4);
				
				String itemId = PersistentObject.getConnection()
					.queryString("SELECT ID FROM " + ArtikelstammItem.TABLENAME + " WHERE "
						+ ArtikelstammItem.FLD_ID + " " + Query.LIKE + " "
						+ JdbcLink.wrap(substring + "%"));
				ArtikelstammItem load = ArtikelstammItem.load(itemId);
				if (load.exists()) {
					fixed++;
					sb.append(
						"Fixing [" + pres.getId() + "] " + string + " -> " + load.getId() + "\n");
					pres.set(Prescription.FLD_ARTICLE, load.storeToString());
				} else {
					failed++;
					sb.append("Error fixing [" + pres.getId() + "] on article ["
						+ pres.get(Prescription.FLD_ARTICLE) + "]\n");
				}
			}
			
			pm.worked(1);
		}
		sb.append("Total " + total + " / Fixed " + fixed + "/ Failed " + failed + "\n");
		pm.done();
		return sb.toString();
	}
	
	@Override
	public String getMaintenanceDescription(){
		return "[5285] Fix references from Prescriptions to ArtikelstammItems";
	}
}
