
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

public class CovidPcrKk {

	@Inject
	private IContextService contextService;

	@Execute
	public void execute() {
		Optional<IPatient> activePatient = contextService.getActivePatient();
		activePatient.ifPresent(patient -> {
			Map<String, ICodeElementBlock> blocks = CovidHandlerUtil.getConfiguredBlocks();
			if (!blocks.isEmpty()) {
				Optional<ICoverage> kkCoverage = CoverageServiceHolder.get().getCoverageWithLaw(patient,
						CovidHandlerUtil.KK_LAWS);
				Optional<IEncounter> pcrEncounter = CovidHandlerUtil
						.getEncountersAt(patient, LocalDate.now(), (BillingLaw[]) null).stream()
						.filter(e -> CovidHandlerUtil.isPcrBilled(e)).findFirst();
				if (kkCoverage.isPresent()) {
					if (pcrEncounter.isEmpty()) {
						bill(kkCoverage.get());
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Verrechnet",
								"Es wurde ein Krankenkassen PCR Test verrechnet.");
					} else {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Bereits verrechnet",
								"Es wurde bereits ein PCR Test heute verrechnet.");
					}
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Kein Fall",
							"Es wurde noch kein Fall mit Gesetz KVG angelegt.");
				}
			}
		});
	}

	private void bill(ICoverage coverage) {
		ICodeElementBlock kkBlock = CovidHandlerUtil.getConfiguredBlocks().get(CovidHandlerUtil.CFG_KK_PCR_BLOCKID);
		if (kkBlock != null) {
			IEncounter encounter = new IEncounterBuilder(CoreModelServiceHolder.get(), coverage,
					contextService.getActiveMandator().get()).buildAndSave();
			CovidHandlerUtil.addBlockToEncounter(kkBlock, encounter);
			contextService.getRootContext().setTyped(encounter);
		} else {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Kein Krankenkassen PCR Block konfiguriert.");
		}

	}
}