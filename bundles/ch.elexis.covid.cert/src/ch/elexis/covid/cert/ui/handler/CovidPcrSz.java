
package ch.elexis.covid.cert.ui.handler;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;

public class CovidPcrSz {

	@Inject
	private IContextService contextService;

	@Execute
	public void execute() {
		Optional<IPatient> activePatient = contextService.getActivePatient();
		activePatient.ifPresent(patient -> {
			Map<String, ICodeElementBlock> blocks = CovidHandlerUtil.getConfiguredBlocks();
			if (!blocks.isEmpty()) {
				Optional<ICoverage> szCoverage = CoverageServiceHolder.get().getCoverageWithLaw(patient,
						CovidHandlerUtil.SZ_LAWS);
				Optional<IEncounter> pcrEncounter = CovidHandlerUtil
						.getEncountersAt(patient, LocalDate.now(), (BillingLaw[]) null).stream()
						.filter(e -> CovidHandlerUtil.isPcrBilled(e)).findFirst();
				if (pcrEncounter.isEmpty()) {
					if (szCoverage.isEmpty()) {
						szCoverage = CovidHandlerUtil.createSzCoverage(patient);
					}
					bill(szCoverage.get());
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Verrechnet",
							"Es wurde ein Selbstzahler PCR Test verrechnet.");
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Bereits verrechnet",
							"Es wurde bereits ein PCR Test heute verrechnet.");
				}
			}
		});
	}

	private void bill(ICoverage coverage) {
		ICodeElementBlock szBlock = CovidHandlerUtil.getConfiguredBlocks().get(CovidHandlerUtil.CFG_SZ_PCR_BLOCKID);
		if (szBlock != null) {
			IEncounter encounter = new IEncounterBuilder(CoreModelServiceHolder.get(), coverage,
					contextService.getActiveMandator().get()).buildAndSave();
			CovidHandlerUtil.addBlockToEncounter(szBlock, encounter);
			contextService.getRootContext().setTyped(encounter);
		} else {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Kein Selbstzahler PCR Block konfiguriert.");
		}

	}
}