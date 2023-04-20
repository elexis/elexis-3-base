package ch.elexis.covid.cert.ui.handler;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.IValueSetService;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.ILocalDocumentService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.covid.cert.service.CertificateInfo;
import ch.elexis.covid.cert.service.CertificateInfo.Type;
import ch.elexis.covid.cert.service.CertificatesService;
import ch.elexis.covid.cert.service.rest.model.TestModel;
import ch.elexis.covid.cert.ui.dialogs.TestModelDialog;
import ch.rgw.tools.Result;

public class CovidHandlerUtil {

	public static final String CFG_AUTO_BILLING = "ch.elexis.covid.cert.ui/automatic_billing";
	public static final String CFG_KK_BLOCKID = "ch.elexis.covid.cert.ui/kk_blockid";
	public static final String CFG_KK_PCR_BLOCKID = "ch.elexis.covid.cert.ui/kk_pcr_blockid";
	public static final String CFG_SZ_BLOCKID = "ch.elexis.covid.cert.ui/sz_blockid";
	public static final String CFG_SZ_PCR_BLOCKID = "ch.elexis.covid.cert.ui/sz_pcr_blockid";

	public static final String ATTEST_POSITIV_LETTER_NAME = "COVID Antigen positiv";
	public static final String ATTEST_NEGATIV_LETTER_NAME = "COVID Antigen negativ";

	public static BillingLaw[] KK_LAWS = { BillingLaw.KVG };
	public static BillingLaw[] SZ_LAWS = { BillingLaw.privat, BillingLaw.VVG };

	/**
	 * Search for the first open {@link ICoverage} of the {@link IPatient} with a
	 * matching law.
	 *
	 * @param patient
	 * @param law
	 * @return
	 */
	public static Optional<ICoverage> getCoverageWithLaw(IPatient patient, BillingLaw... laws) {
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

	/**
	 * Get all configured covid {@link ICodeElementBlock} instances as map with id
	 * as key.
	 *
	 * @return
	 */
	public static Map<String, ICodeElementBlock> getConfiguredBlocks() {
		Map<String, ICodeElementBlock> ret = new HashMap<>();
		if (ConfigServiceHolder.get().get(CFG_KK_BLOCKID, null) != null) {
			CoreModelServiceHolder.get()
					.load(ConfigServiceHolder.get().get(CFG_KK_BLOCKID, null), ICodeElementBlock.class)
					.ifPresent(bl -> ret.put(CFG_KK_BLOCKID, bl));
		}
		if (ConfigServiceHolder.get().get(CFG_KK_PCR_BLOCKID, null) != null) {
			CoreModelServiceHolder.get()
					.load(ConfigServiceHolder.get().get(CFG_KK_PCR_BLOCKID, null), ICodeElementBlock.class)
					.ifPresent(bl -> ret.put(CFG_KK_PCR_BLOCKID, bl));
		}
		if (ConfigServiceHolder.get().get(CFG_SZ_BLOCKID, null) != null) {
			CoreModelServiceHolder.get()
					.load(ConfigServiceHolder.get().get(CFG_SZ_BLOCKID, null), ICodeElementBlock.class)
					.ifPresent(bl -> ret.put(CFG_SZ_BLOCKID, bl));
		}
		if (ConfigServiceHolder.get().get(CFG_SZ_PCR_BLOCKID, null) != null) {
			CoreModelServiceHolder.get()
					.load(ConfigServiceHolder.get().get(CFG_SZ_PCR_BLOCKID, null), ICodeElementBlock.class)
					.ifPresent(bl -> ret.put(CFG_SZ_PCR_BLOCKID, bl));
		}

		return ret;
	}

	/**
	 * Get the first {@link CertificateInfo} of the {@link IPatient} matching the at
	 * {@link LocalDate} and {@link Type}.
	 *
	 * @param patient
	 * @param at
	 * @param type
	 * @return
	 */
	public static Optional<CertificateInfo> getCertificateAtWithType(IPatient patient, LocalDate at, Type type) {
		List<CertificateInfo> certificates = CertificateInfo.of(patient);
		if (!certificates.isEmpty()) {
			return certificates.stream()
					.filter(ci -> ci.getType() == type && ci.getTimestamp().toLocalDate().equals(at)).findFirst();
		}
		return Optional.empty();
	}

	public static void openCertDocument(CertificateInfo newCert, IDocumentStore omnivoreStore,
			ILocalDocumentService localDocumentService) {
		java.util.Optional<IDocument> document = omnivoreStore.loadDocument(newCert.getDocumentId());
		if (document.isPresent()) {
			java.util.Optional<File> file = localDocumentService.getTempFile(document.get());
			if (file.isPresent()) {
				Program.launch(file.get().getAbsolutePath());
			} else {
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.Core_Error,
						Messages.Core_Document_Not_Opened_Locally);
			}
		}
	}

	public static void openLetter(IDocumentLetter letter, ILocalDocumentService localDocumentService) {
		if (letter != null) {
			java.util.Optional<File> file = localDocumentService.getTempFile(letter);
			if (file.isPresent()) {
				Program.launch(file.get().getAbsolutePath());
			} else {
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.Core_Error,
						Messages.Core_Document_Not_Opened_Locally);
			}
		}
	}

	/**
	 * Lookup encounters at date of the newest open coverage with KVG law.
	 *
	 * @param patient
	 * @param localDate
	 * @return
	 */
	public static List<IEncounter> getEncountersAt(IPatient patient, LocalDate localDate, BillingLaw... laws) {
		if (patient.getCoverages() != null) {
			List<ICoverage> coverages = patient.getCoverages();
			coverages.sort(new Comparator<ICoverage>() {
				@Override
				public int compare(ICoverage o1, ICoverage o2) {
					return o2.getDateFrom().compareTo(o1.getDateFrom());
				}
			});
			return coverages.stream().filter(coverage -> coverage.isOpen()
					&& (laws != null ? Arrays.asList(laws).contains(coverage.getBillingSystem().getLaw()) : true))
					.flatMap(coverage -> coverage.getEncounters().stream())
					.filter(encounter -> encounter.getDate().equals(localDate)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Test if a antigen test is billed on the {@link IEncounter}.
	 *
	 * @param encounter
	 * @return
	 */
	public static boolean isAntigenBilled(IEncounter encounter) {
		Optional<IBilled> found = encounter.getBilled().stream().filter(billed -> isAntigenBilled(billed)).findFirst();
		return found.isPresent();
	}

	// pandemie & 1300 -> antigen
	public static boolean isAntigenBilled(IBilled billed) {
		IBillable billable = billed.getBillable();
		if (billable != null && "351".equals(billable.getCodeSystemCode())) {
			return "01.01.1300".equals(billable.getCode()) || "01.99.1300".equals(billable.getCode());
		}
		return false;
	}

	// pandemie & !antigen
	public static boolean isPcrBilled(IEncounter encounter) {
		Optional<IBilled> found = encounter.getBilled().stream().filter(billed -> isPandemieBilled(billed)).findFirst();
		if (found.isPresent()) {
			return !isAntigenBilled(encounter);
		}
		return false;
	}

	public static boolean isPandemieBilled(IBilled billed) {
		IBillable billable = billed.getBillable();
		if (billable != null && "351".equals(billable.getCodeSystemCode())) {
			return true;
		}
		return false;
	}

	public static Optional<ICoverage> createSzCoverage(IPatient patient) {
		return Optional.of(new ICoverageBuilder(CoreModelServiceHolder.get(), patient, "Selbstzahler",
				ICoverageBuilder.getDefaultCoverageReason(ConfigServiceHolder.get()), BillingLaw.privat.name())
						.buildAndSave());
	}

	public static TestModel getTestModel(IPatient patient, CertificatesService service,
			IValueSetService valueSetService, String typeDisplay) {
		TestModel ret = new TestModel().initDefault(patient, service.getOtp());

		List<ICoding> testsTypeValueSet = valueSetService.getValueSet("covid-19-test-type");
		Optional<ICoding> matchingType = testsTypeValueSet.stream()
				.filter(c -> c.getDisplay().toLowerCase().contains(typeDisplay.toLowerCase())).findFirst();
		if (matchingType.isPresent()) {
			ret.getTestInfo()[0].setTypeCode(matchingType.get().getCode());
		}

		TestModelDialog dialog = new TestModelDialog(ret, Display.getDefault().getActiveShell());
		if (dialog.open() == Dialog.OK) {
			return ret;
		}
		return null;
	}

	public static void showResultInfos(Result<String> result) {
		if (result.getMessages().size() > 1) {
			for (int i = 1; i < result.getMessages().size(); i++) {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Info",
						result.getMessages().get(i).getText());
			}
		}
	}

	public static List<IDocumentLetter> getLettersAt(IPatient patient, LocalDate at, String... subjects) {
		IQuery<IDocumentLetter> query = CoreModelServiceHolder.get().getQuery(IDocumentLetter.class);
		query.and(ModelPackage.Literals.IDOCUMENT__PATIENT, COMPARATOR.EQUALS, patient);
		if (subjects != null) {
			if (subjects.length == 1) {
				query.and("subject", COMPARATOR.EQUALS, subjects[0]);
			} else if (subjects.length > 1) {
				query.startGroup();
				for (String subject : subjects) {
					query.or("subject", COMPARATOR.EQUALS, subject);
				}
				query.andJoinGroups();
			}
		}
		if (at != null) {
			return query.execute().stream()
					.filter(d -> LocalDate.ofInstant(d.getCreated().toInstant(), ZoneId.systemDefault()).equals(at))
					.collect(Collectors.toList());
		} else {
			return query.execute();
		}
	}

	public static void addBlockToEncounter(ICodeElementBlock block, IEncounter encounter) {
		// add diagnosis
		block.getElements(encounter).stream().filter(el -> el instanceof IDiagnosis).map(el -> (IDiagnosis) el)
				.forEach(diagnosis -> encounter.addDiagnosis(diagnosis));
		CoreModelServiceHolder.get().save(encounter);
		// bill the block
		block.getElements(encounter).stream().filter(el -> el instanceof IBillable).map(el -> (IBillable) el)
				.forEach(billable -> BillingServiceHolder.get().bill(billable, encounter, 1));
	}

	public static boolean isBilled(IEncounter encounter, String code) {
		return encounter.getBilled().stream().filter(b -> code.equals(b.getCode())).findFirst().isPresent();
	}

	public static void removeBilled(IEncounter encounter, String code) {
		for (IBilled billed : new ArrayList<>(encounter.getBilled())) {
			if (code.equals(billed.getCode())) {
				BillingServiceHolder.get().removeBilled(billed, encounter);
			}
		}
	}
}
