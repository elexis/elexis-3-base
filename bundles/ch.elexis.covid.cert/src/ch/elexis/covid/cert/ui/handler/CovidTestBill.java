
package ch.elexis.covid.cert.ui.handler;

import java.util.Map;
import java.util.Optional;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ICoverageService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import jakarta.inject.Inject;

public class CovidTestBill {

	@Inject
	private IContextService contextService;
	
	@Inject 
	private ICoverageService coverageService;

	@Execute
	public void execute() {
		if (ConfigServiceHolder.get().get(CovidHandlerUtil.CFG_AUTO_BILLING, false)) {
			Optional<IPatient> activePatient = contextService.getActivePatient();
			activePatient.ifPresent(patient -> {
				Map<String, ICodeElementBlock> blocks = CovidHandlerUtil.getConfiguredBlocks();
				if (!blocks.isEmpty()) {
					ICodeElementBlock block = selectBlock(blocks);
					if (block != null) {
						// get or create coverage depending on selected block
						ICoverage coverage = null;
						if (block == blocks.get(CovidHandlerUtil.CFG_KK_BLOCKID)
								|| block == blocks.get(CovidHandlerUtil.CFG_KK_PCR_BLOCKID)) {
							coverage = coverageService.getCoverageWithLaw(patient, CovidHandlerUtil.KK_LAWS)
									.orElse(null);
							if (coverage == null) {
								Display.getDefault()
										.syncExec(() -> MessageDialog.openError(Display.getDefault().getActiveShell(),
												"Keine Verrechnung",
												"Es wurde noch kein Fall mit Gesetz KVG angelegt."));
							}
						} else {
							coverage = coverageService.getCoverageWithLaw(patient, CovidHandlerUtil.SZ_LAWS)
									.orElse(null);
							if (coverage == null) {
								coverage = createPrivateCoverage(patient);
							}
						}
						if (coverage != null) {
							IEncounter encounter = new IEncounterBuilder(CoreModelServiceHolder.get(), coverage,
									contextService.getActiveMandator().get()).buildAndSave();
							CovidHandlerUtil.addBlockToEncounter(block, encounter);
							contextService.getRootContext().setTyped(encounter);
						}
					}
				}
			});
		}
	}

	private ICodeElementBlock selectBlock(Map<String, ICodeElementBlock> blocks) {
		if (blocks.size() == 1) {
			return (ICodeElementBlock) blocks.values().toArray()[0];
		} else {
			int ret = MessageDialog.open(MessageDialog.QUESTION, Display.getDefault().getActiveShell(),
					"Verrechnungsblock Auswahl", "Welcher Leistungsblock soll verrechnet werden.", SWT.SHEET,
					"Krankenkasse Antigen", "Selbszahler Antigen", "Krankenkasse PCR", "Selbszahler PCR");
			if (ret == 0) {
				return blocks.get(CovidHandlerUtil.CFG_KK_BLOCKID);
			} else if (ret == 1) {
				return blocks.get(CovidHandlerUtil.CFG_SZ_BLOCKID);
			} else if (ret == 2) {
				return blocks.get(CovidHandlerUtil.CFG_KK_PCR_BLOCKID);
			} else if (ret == 3) {
				return blocks.get(CovidHandlerUtil.CFG_SZ_PCR_BLOCKID);
			}
		}
		return null;
	}

	private ICoverage createPrivateCoverage(IPatient patient) {
		return new ICoverageBuilder(CoreModelServiceHolder.get(), patient, "Selbstzahler",
				ICoverageBuilder.getDefaultCoverageReason(ConfigServiceHolder.get()), BillingLaw.privat.name())
						.buildAndSave();

	}
}