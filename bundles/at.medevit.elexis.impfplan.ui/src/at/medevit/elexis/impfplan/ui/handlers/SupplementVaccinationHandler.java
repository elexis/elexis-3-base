package at.medevit.elexis.impfplan.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.dialogs.SupplementVaccinationDialog;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class SupplementVaccinationHandler extends AbstractHandler {
	private static Logger logger = LoggerFactory.getLogger(SupplementVaccinationHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Patient sp = ElexisEventDispatcher.getSelectedPatient();
		if (sp == null) {
			return null;
		}
		SupplementVaccinationDialog svd = new SupplementVaccinationDialog(UiDesk.getTopShell(), sp);
		int retVal = svd.open();
		if (retVal == TitleAreaDialog.OK) {
			String patientId = sp.getId();
			String administratorString = svd.getAdministratorString();
			String lotNo = svd.getLotNo();
			TimeTool doa = svd.getDateOfAdministration();
			String articleString = svd.getArticleString();
			Identifiable art = StoreToStringServiceHolder.get().loadFromString(articleString).orElse(null);

			if (art instanceof IArticle) {
				IArticle article = (IArticle) art;
				new Vaccination(patientId, articleString, article.getLabel(), article.getGtin(), article.getAtcCode(),
						doa.getTime(), lotNo, administratorString);
			} else {
				Vaccination v = new Vaccination(patientId, null, articleString, null, null, doa.getTime(), lotNo,
						administratorString);
				v.setVaccAgainst(svd.getVaccAgainst());
			}
			logger.debug("Supplement vaccination: " + articleString + " added"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
}
