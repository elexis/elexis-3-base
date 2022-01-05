 
package ch.elexis.covid.cert.ui.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class CovidTestBill {
	
	public static final String CFG_AUTO_BILLING = "ch.elexis.covid.cert.ui/automatic_billing";
	public static final String CFG_KK_BLOCKID = "ch.elexis.covid.cert.ui/kk_blockid";
	public static final String CFG_SZ_BLOCKID = "ch.elexis.covid.cert.ui/sz_blockid";
	
	private static BillingLaw[] KK_LAWS = {
		BillingLaw.KVG
	};
	private static BillingLaw[] SZ_LAWS = {
		BillingLaw.privat, BillingLaw.VVG
	};
	
	@Inject
	private IContextService contextService;
	
	@Execute
	public void execute() {
		if (ConfigServiceHolder.get().get(CovidTestBill.CFG_AUTO_BILLING, false)) {
			Optional<IPatient> activePatient = contextService.getActivePatient();
			activePatient.ifPresent(patient -> {
				Map<String, ICodeElementBlock> blocks = getConfiguredBlocks();
				if (!blocks.isEmpty()) {
					ICodeElementBlock block = selectBlock(blocks);
					if (block != null) {
						// get or create coverage depending on selected block
						ICoverage coverage = null;
						if (block == blocks.get(CFG_KK_BLOCKID)) {
							coverage = getCoverage(patient, KK_LAWS).orElse(null);
							if (coverage == null) {
								Display.getDefault()
									.syncExec(() -> MessageDialog.openError(
										Display.getDefault().getActiveShell(), "Keine Verrechnung",
										"Es wurde noch kein Fall mit Gesetz KVG angelegt."));
							}
						} else {
							coverage = getCoverage(patient, SZ_LAWS).orElse(null);
							if (coverage == null) {
								coverage = createPrivateCoverage(patient);
							}
						}
						if (coverage != null) {
							IEncounter encounter =
								new IEncounterBuilder(CoreModelServiceHolder.get(), coverage,
									contextService.getActiveMandator().get()).buildAndSave();
							// bill the block
							block.getElements(encounter).stream()
								.filter(el -> el instanceof IBillable).map(el -> (IBillable) el)
								.forEach(billable -> BillingServiceHolder.get().bill(billable,
									encounter, 1));
							contextService.getRootContext().setTyped(encounter);
						}
					}
				}
			});
		}
	}
	
	private ICodeElementBlock selectBlock(Map<String, ICodeElementBlock> blocks){
		if (blocks.size() == 1) {
			return (ICodeElementBlock) blocks.values().toArray()[0];
		} else {
			int ret = MessageDialog.open(MessageDialog.QUESTION,
				Display.getDefault().getActiveShell(),
				"Verrechnungsblock Auswahl", "Welcher Leistungsblock soll verrechnet werden.",
				SWT.SHEET, "Krankenkasse", "Selbszahler");
			if (ret == 0) {
				return blocks.get(CFG_KK_BLOCKID);
			} else if (ret == 1) {
				return blocks.get(CFG_SZ_BLOCKID);
			}
		}
		return null;
	}
	
	private ICoverage createPrivateCoverage(IPatient patient){
		return new ICoverageBuilder(CoreModelServiceHolder.get(), patient, "Selbstzahler",
			ICoverageBuilder.getDefaultCoverageReason(ConfigServiceHolder.get()),
			BillingLaw.privat.name()).buildAndSave();
		
	}
	
	private Optional<ICoverage> getCoverage(IPatient patient, BillingLaw[] laws){
		ICoverage bestMatch = null;
		for (ICoverage coverage : patient.getCoverages()) {
			if (coverage.isOpen()) {
				if (Arrays.asList(laws).contains(coverage.getBillingSystem().getLaw())) {
					bestMatch = coverage;
				}
			}
		}
		return Optional.ofNullable(bestMatch);
	}
	
	private Map<String, ICodeElementBlock> getConfiguredBlocks(){
		Map<String, ICodeElementBlock> ret = new HashMap<>();
		if (ConfigServiceHolder.get().get(CFG_KK_BLOCKID, null) != null) {
			CoreModelServiceHolder.get().load(ConfigServiceHolder.get().get(CFG_KK_BLOCKID, null),
				ICodeElementBlock.class).ifPresent(bl -> ret.put(CFG_KK_BLOCKID, bl));
		}
		if (ConfigServiceHolder.get().get(CFG_SZ_BLOCKID, null) != null) {
			CoreModelServiceHolder.get()
				.load(ConfigServiceHolder.get().get(CFG_SZ_BLOCKID, null), ICodeElementBlock.class)
				.ifPresent(bl -> ret.put(CFG_SZ_BLOCKID, bl));
		}
		return ret;
	}
}