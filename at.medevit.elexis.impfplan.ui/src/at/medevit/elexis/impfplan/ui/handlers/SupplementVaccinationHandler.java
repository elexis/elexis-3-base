package at.medevit.elexis.impfplan.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TitleAreaDialog;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.dialogs.SupplementVaccinationDialog;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObjectFactory;
import ch.rgw.tools.TimeTool;

public class SupplementVaccinationHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		SupplementVaccinationDialog svd = new SupplementVaccinationDialog(UiDesk.getTopShell());
		int retVal = svd.open();
		if (retVal == TitleAreaDialog.OK) {
			String patientId = ElexisEventDispatcher.getSelectedPatient().getId();
			String administratorString = svd.getAdministratorString();
			String lotNo = svd.getLotNo();
			TimeTool doa = svd.getDateOfAdministration();
			String ean = svd.getEAN();
			String articleString = svd.getArticleString();
			Artikel art = (Artikel) new PersistentObjectFactory().createFromString(articleString);
			
			if (art != null) {
				new Vaccination(patientId, art, doa.getTime(), lotNo, administratorString);
			} else {
				String articleAtcCode = svd.getAtcCode();
				new Vaccination(patientId, null, articleString, ean, articleAtcCode, doa.getTime(),
					lotNo, administratorString);
			}
		}
		return null;
	}
}
