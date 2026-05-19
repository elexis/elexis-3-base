package at.medevit.elexis.impfplan.ui.billing;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.IBilledAdjuster;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Verrechnet;

@Component
public class VaccinationVerrechnetAdjuster implements IBilledAdjuster {

	private List<String> vaccineConsultationCodes = List.of("AA.00.0090", "CG.00.0010", "CG.00.0020", "CG.00.0030",
			"CG.00.0040", "CG.00.0050", "CG.00.0060", "CG.00.0070", "CG.00.0080", "CG.00.0090", "CG.00.0100",
			"CG.00.0110", "CG.00.0120", "CG.00.0130", "CG.00.0140", "CG.00.0150", "CG.00.0160", "CG.00.0170");

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
									if (isFranchsiseFree(encounter, billed)) {
										billed.setExtInfo(Constants.FLD_EXT_FRANCHISEFREE, Boolean.TRUE.toString());
										CoreModelServiceHolder.get().save(billed);
									}
								}
							}
						}
						billed.setExtInfo(Verrechnet.VATSCALE, Double.toString(0.0));
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

	private boolean isFranchsiseFree(IEncounter encounter, IBilled billed) {
		Optional<IBilled> vaccineConsultationService = encounter.getBilled().stream()
				.filter(b -> vaccineConsultationCodes.contains(b.getCode())).findFirst();
		return vaccineConsultationService.isPresent();
	}
}
