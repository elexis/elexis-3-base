
package ch.elexis.covid.cert.ui.handler;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.findings.codes.IValueSetService;
import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ICoverageService;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.ILocalDocumentService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.covid.cert.service.CertificateInfo;
import ch.elexis.covid.cert.service.CertificatesService;
import ch.elexis.covid.cert.service.rest.model.TestModel;
import ch.rgw.tools.Result;

public class CovidAntigenKk {

	@Inject
	private IValueSetService valueSetService;

	@Inject
	private CertificatesService service;

	@Inject
	private IContextService contextService;

	@Inject
	@Service(filterExpression = "(storeid=ch.elexis.data.store.omnivore)")
	private IDocumentStore omnivoreStore;

	@Inject
	private ILocalDocumentService localDocumentService;

	@Execute
	public void execute() {
		Optional<IPatient> activePatient = contextService.getActivePatient();
		activePatient.ifPresent(patient -> {
			Map<String, ICodeElementBlock> blocks = CovidHandlerUtil.getConfiguredBlocks();
			if (!blocks.isEmpty()) {
				Optional<ICoverage> kkCoverage = CoverageServiceHolder.get().getCoverageWithLaw(patient,
						CovidHandlerUtil.KK_LAWS);
				Optional<CertificateInfo> todayCertificate = CovidHandlerUtil.getCertificateAtWithType(patient,
						LocalDate.now(), CertificateInfo.Type.TEST);
				if (kkCoverage.isPresent()) {
					if (todayCertificate.isPresent()) {
						if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
								"Vorhandenes Test Zertifikat",
								"Es wurde heute bereits ein Test Zertifikat ausgestellt.\nMöchten Sie dieses anzeigen?")) {
							CovidHandlerUtil.openCertDocument(todayCertificate.get(), omnivoreStore,
									localDocumentService);
						}
					} else {
						Optional<IEncounter> antigenEncounter = billAntigen(kkCoverage.get());
						if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Test Resultat",
								"Wurde der Patient positiv getestet?")) {
							if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "PCR Test",
									"Wurde ein PCR Test gemacht?")) {
								if (antigenEncounter.isPresent()) {
									Optional<IEncounter> pcrEncounter = billPcr(kkCoverage.get());
									pcrEncounter.ifPresent(encounter -> {
										if (CovidHandlerUtil.isBilled(antigenEncounter.get(), "01.01.1100")) {
											CovidHandlerUtil.removeBilled(encounter, "01.01.1100");
										}
									});
								}
							}
						} else {
							createCert(kkCoverage.get());
						}
					}
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Kein Fall",
							"Es wurde noch kein Fall mit Gesetz KVG angelegt.");
				}
			}
		});
	}

	private void createCert(ICoverage coverage) {
		TestModel model = CovidHandlerUtil.getTestModel(coverage.getPatient(), service, valueSetService, "antigen");
		if (model != null) {
			try {
				Result<String> result = service.createTestCertificate(coverage.getPatient(), model);
				if (result.isOK()) {
					CertificateInfo newCert = CertificateInfo.of(coverage.getPatient()).stream()
							.filter(c -> c.getUvci().equals(result.get())).findFirst().orElse(null);
					if (newCert != null) {
						CovidHandlerUtil.openCertDocument(newCert, omnivoreStore, localDocumentService);
					}
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, coverage.getPatient());
					CovidHandlerUtil.showResultInfos(result);
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Es ist folgender Fehler aufgetreten.\n\n" + result.getMessages().stream()
									.map(m -> m.getText()).collect(Collectors.joining(", ")));
				}
			} catch (Exception ex) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
						"Es ist ein Fehler beim Aufruf der API aufgetreten.");
				LoggerFactory.getLogger(getClass()).error("Error getting test certificate", ex);
			}
		}
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
					"Kein Krankenkassen Antigen Block konfiguriert.");
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