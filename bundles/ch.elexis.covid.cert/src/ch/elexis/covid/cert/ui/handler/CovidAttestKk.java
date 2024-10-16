
package ch.elexis.covid.cert.ui.handler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ILocalDocumentService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.data.Brief;
import ch.elexis.data.Konsultation;

public class CovidAttestKk {

	@Inject
	private ILocalDocumentService localDocumentService;

	@Inject
	private IContextService contextService;

	private TextContainer textContainer;

	@Execute
	public void execute() {
		if (textContainer == null) {
			textContainer = new TextContainer();
		}
		Optional<IPatient> activePatient = contextService.getActivePatient();
		activePatient.ifPresent(patient -> {
			Map<String, ICodeElementBlock> blocks = CovidHandlerUtil.getConfiguredBlocks();
			if (!blocks.isEmpty()) {
				Optional<ICoverage> kkCoverage = CoverageServiceHolder.get().getCoverageWithLaw(patient,
						CovidHandlerUtil.KK_LAWS);
				if (kkCoverage.isPresent()) {
					List<IDocumentLetter> existingLetters = CovidHandlerUtil.getLettersAt(patient, LocalDate.now(),
							new String[] { CovidHandlerUtil.ATTEST_POSITIV_LETTER_NAME,
									CovidHandlerUtil.ATTEST_NEGATIV_LETTER_NAME });
					if (existingLetters.isEmpty()) {
						Optional<IEncounter> antigenEncounter;
						if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Test Resultat",
								"Wurde der Patient positiv getestet?")) {
							if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "PCR Test",
									"Wurde ein PCR Test gemacht?")) {
								antigenEncounter = billAntigen(kkCoverage.get());
								if (antigenEncounter.isPresent()) {
									Optional<IEncounter> pcrEncounter = billPcr(kkCoverage.get());
									pcrEncounter.ifPresent(encounter -> {
										if (CovidHandlerUtil.isBilled(antigenEncounter.get(), "01.01.1100")) {
											CovidHandlerUtil.removeBilled(encounter, "01.01.1100");
										}
									});
								}
							} else {
								antigenEncounter = billAntigen(kkCoverage.get());
							}
							Konsultation letterKons = (Konsultation) NoPoUtil
									.loadAsPersistentObject(antigenEncounter.get());
							Brief letterPositiv = textContainer.createFromTemplateName(letterKons,
									CovidHandlerUtil.ATTEST_POSITIV_LETTER_NAME, Brief.UNKNOWN, null, null);
							CovidHandlerUtil.openLetter(
									NoPoUtil.loadAsIdentifiable(letterPositiv, IDocumentLetter.class).get(),
									localDocumentService);
						} else {
							antigenEncounter = billAntigen(kkCoverage.get());
							Konsultation letterKons = (Konsultation) NoPoUtil
									.loadAsPersistentObject(antigenEncounter.get());
							Brief letterNegativ = textContainer.createFromTemplateName(letterKons,
									CovidHandlerUtil.ATTEST_NEGATIV_LETTER_NAME, Brief.UNKNOWN, null, null);
							CovidHandlerUtil.openLetter(
									NoPoUtil.loadAsIdentifiable(letterNegativ, IDocumentLetter.class).get(),
									localDocumentService);
						}
						ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, patient);
					} else {
						if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
								"Vorhandene Test Bescheinigung",
								"Es wurde heute bereits ein Test Bescheinigung ausgestellt.\nMöchten Sie diese anzeigen?")) {
							CovidHandlerUtil.openLetter(existingLetters.get(0), localDocumentService);
						}
					}
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Kein Fall",
							"Es wurde noch kein Fall mit Gesetz KVG angelegt.");
				}
			}
		});
	}

	private Optional<IEncounter> billAntigen(ICoverage coverage) {
		ICodeElementBlock kkBlock = CovidHandlerUtil.getConfiguredBlocks().get(CovidHandlerUtil.CFG_KK_BLOCKID);
		if (kkBlock != null) {
			IEncounter encounter = new IEncounterBuilder(CoreModelServiceHolder.get(), coverage,
					contextService.getActiveMandator().get()).buildAndSave();
			CovidHandlerUtil.addBlockToEncounter(kkBlock, encounter);
			contextService.getRootContext().setTyped(encounter);
			return Optional.of(encounter);
		} else {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Kein Krankenkassen PCR Block konfiguriert.");
		}
		return Optional.empty();
	}

	private Optional<IEncounter> billPcr(ICoverage coverage) {
		ICodeElementBlock kkBlock = CovidHandlerUtil.getConfiguredBlocks().get(CovidHandlerUtil.CFG_KK_PCR_BLOCKID);
		if (kkBlock != null) {
			IEncounter encounter = new IEncounterBuilder(CoreModelServiceHolder.get(), coverage,
					contextService.getActiveMandator().get()).buildAndSave();
			CovidHandlerUtil.addBlockToEncounter(kkBlock, encounter);
			contextService.getRootContext().setTyped(encounter);
			return Optional.of(encounter);
		} else {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Kein Krankenkassen PCR Block konfiguriert.");
		}
		return Optional.empty();
	}
}