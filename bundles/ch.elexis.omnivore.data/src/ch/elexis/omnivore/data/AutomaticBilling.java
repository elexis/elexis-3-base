package ch.elexis.omnivore.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.events.MessageEvent;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.core.ui.dialogs.ResultDialog;
import ch.elexis.core.ui.services.EncounterServiceHolder;
import ch.elexis.omnivore.PreferenceConstants;
import ch.rgw.tools.Result;

public class AutomaticBilling {

	public static boolean isEnabled() {
		String blockId = ConfigServiceHolder.get().getLocal(PreferenceConstants.AUTO_BILLING_BLOCK, StringUtils.EMPTY);
		return ConfigServiceHolder.get().getLocal(PreferenceConstants.AUTO_BILLING, false) && !blockId.isEmpty();
	}

	private static Executor executor = Executors.newSingleThreadExecutor();

	private IPatient patient;
	private IDocument docHandle;

	public AutomaticBilling(IDocument docHandle) {
		this.patient = docHandle.getPatient();
		this.docHandle = docHandle;
	}

	public void bill() {
		if (isEnabled() && docHandle != null) {
			// do actual billing in a separate thread
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						IEncounter encounter = getEncounter();
						if (encounter != null) {
							String blockId = ConfigServiceHolder.get().getLocal(PreferenceConstants.AUTO_BILLING_BLOCK,
									StringUtils.EMPTY);
							if (StringUtils.isNotBlank(blockId)) {
								ICodeElementBlock block = CoreModelServiceHolder.get()
										.load(blockId, ICodeElementBlock.class).orElse(null);
								if (block != null) {
									if (encounter != null && BillingServiceHolder.get().isEditable(encounter).isOK()) {
										addBlockToEncounter(block, encounter);
									} else {
										LoggerFactory.getLogger(getClass()).warn(String.format(
												"Could not add block [%s] for document of patient [%s] because no valid kons found.", //$NON-NLS-1$
												block.getCode(), patient.getLabel()));
									}
								}
							}
						}
					} catch (Exception e) {
						MessageEvent.fireError("Error",
								"Es ist ein Fehler bei der automatischen Verrechnung aufgetreten.");
						LoggerFactory.getLogger(getClass()).error("Error billing block", e); //$NON-NLS-1$
					}
				}
			});
		}
	}

	private void addBlockToEncounter(ICodeElementBlock block, IEncounter encounter) {
		List<ICodeElement> elements = block.getElements(encounter);
		StringJoiner notOkResults = new StringJoiner(StringUtils.LF);
		for (ICodeElement element : elements) {
			if (element instanceof IBillable) {
				Result<IBilled> result = BillingServiceHolder.get().bill((IBillable) element, encounter, 1);
				if (!result.isOK()) {
					String message = patient.getLabel() + "\nDokument import Verrechnung von [" + element.getCode()
							+ "]\n\n" + ResultDialog.getResultMessage(result); //$NON-NLS-1$
					if (!notOkResults.toString().contains(message)) {
						notOkResults.add(message);
					}
				}
			}
		}
		if (!notOkResults.toString().isEmpty()) {
			MessageEvent.fireWarninig(Messages.VerrechnungsDisplay_imvalidBilling, notOkResults.toString());
		}
	}

	private IEncounter getEncounter() {
		Optional<IEncounter> encounter = EncounterServiceHolder.get().getLatestEncounter(patient);
		if (!encounter.isPresent() || !EncounterServiceHolder.get().isEditable(encounter.get())) {
			encounter = createEncounter();
		}
		return encounter.orElse(null);
	}

	private Optional<IEncounter> createEncounter() {
		Optional<IEncounter> lastEncounter = EncounterServiceHolder.get().getLatestEncounter(patient); // patient.getLastKonsultation();
		ICoverage coverage = null;
		if (lastEncounter.isPresent()) {
			coverage = lastEncounter.get().getCoverage();
		}
		if (coverage == null || !coverage.isOpen()) {
			List<ICoverage> openFall = getOpenFall();
			if (openFall.isEmpty()) {
				coverage = new ICoverageBuilder(CoreModelServiceHolder.get(), patient,
						CoverageServiceHolder.get().getDefaultCoverageLabel(),
						CoverageServiceHolder.get().getDefaultCoverageReason(),
						CoverageServiceHolder.get().getDefaultCoverageLaw()).buildAndSave();
			} else {
				coverage = openFall.get(0);
			}
		}
		if (coverage != null) {
			return Optional.of(new IEncounterBuilder(CoreModelServiceHolder.get(), coverage,
					ContextServiceHolder.get().getActiveMandator().orElse(null)).buildAndSave());
		}
		return Optional.empty();
	}

	private List<ICoverage> getOpenFall() {
		ArrayList<ICoverage> ret = new ArrayList<>();
		List<ICoverage> coverages = patient.getCoverages();
		for (ICoverage f : coverages) {
			if (f.isOpen()) {
				ret.add(f);
			}
		}
		ret.sort(new Comparator<ICoverage>() {
			@Override
			public int compare(ICoverage o1, ICoverage o2) {
				if (o1.getDateFrom() != null && o2.getDateFrom() != null) {
					return o1.getDateFrom().compareTo(o2.getDateFrom());
				}
				return Long.compare(o1.getLastupdate(), o2.getLastupdate());
			}
		});
		return ret;
	}
}
