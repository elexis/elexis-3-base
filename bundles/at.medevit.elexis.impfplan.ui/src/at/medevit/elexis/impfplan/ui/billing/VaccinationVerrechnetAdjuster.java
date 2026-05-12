package at.medevit.elexis.impfplan.ui.billing;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.dialogs.ApplicationInputDialog;
import at.medevit.elexis.impfplan.ui.handlers.ApplyVaccinationHandler;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IBilledAdjuster;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;
import ch.elexis.core.ui.UiDesk;

@Component
public class VaccinationVerrechnetAdjuster implements IBilledAdjuster {

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	@Override
	public void adjust(IBilled billed) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				IBillable billable = billed.getBillable();
				if (billable instanceof IArticle) {
					if (((IArticle) billable).isVaccination()) {
						IEncounter encounter = billed.getEncounter();
						if (encounter != null) {
							ICoverage coverage = encounter.getCoverage();
							if (coverage != null) {
								IPatient patient = coverage.getPatient();
								if (patient != null) {
									performVaccination(patient.getId(), (IArticle) billable);
								}
							}
						}
					}
				}
			}
		});
	}

	private void performVaccination(String patientId, IArticle article) {
		UiDesk.asyncExec(new Runnable() {
			@Override
			public void run() {
				Date d = new Date();
				if (ApplyVaccinationHandler.inProgress()) {
					d = ApplyVaccinationHandler.getKonsDate();
				}

				IMandator m = ContextServiceHolder.get().getActiveMandator().orElse(null);
				ApplicationInputDialog aid = new ApplicationInputDialog(UiDesk.getTopShell(), article);
				aid.open();
				String lotNo = aid.getLotNo();
				String side = aid.getSide();

				Vaccination vacc = new Vaccination(patientId, StoreToStringServiceHolder.getStoreToString(article),
						article.getLabel(), article.getGtin(), article.getAtcCode(), d, lotNo,
						StoreToStringServiceHolder.getStoreToString(m));

				if (side != null && !side.isEmpty()) {
					vacc.setSide(side);
				}
			}
		});
	}
}
