package ch.elexis.covid.cert.dbcheck;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICodeElementBlock;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.rgw.tools.Result;

public class MoveSzBillingToSzCoverage extends ExternalMaintenance {

	private int movedCount;
	private int canNotMoveCount;

	private ICodeElementBlock kkBlock;
	private IMandator activeMandator;

	private List<IPatient> notBillablePatients;

	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion) {
		activeMandator = ContextServiceHolder.get().getActiveMandator()
				.orElseThrow(() -> new IllegalStateException("Es ist kein Mandant angemeldet."));

		IQuery<IEncounter> encountersQuery = CoreModelServiceHolder.get().getQuery(IEncounter.class);

		movedCount = 0;
		canNotMoveCount = 0;

		try (IQueryCursor<IEncounter> cursor = encountersQuery.executeAsCursor()) {
			pm.beginTask("Bitte warten, COVID Test Zertifikat Selbstzahler Verrechnung wird korrigiert ...",
					cursor.size());
			while (cursor.hasNext()) {
				IEncounter encounter = cursor.next();
				if (isSzEncounter(encounter) && shouldMove(encounter)) {
					if (canMove(encounter)) {
						move(encounter);
						movedCount++;
					} else {
						canNotMoveCount++;
					}
				}
				pm.worked(1);
			}
		}

		return "Es wurden " + movedCount + " Selbstzahler Konsultationen verschoben."
				+ (canNotMoveCount > 0
						? "\nEs gab " + canNotMoveCount
								+ " Selbstzahler Konsultationen die nicht verschoben werden konnten."
						: StringUtils.EMPTY);
	}

	private void move(IEncounter encounter) {
		ICoverage privateCoverage = getPrivateCoverage(encounter.getPatient()).orElse(null);
		if (privateCoverage == null) {
			privateCoverage = createPrivateCoverage(encounter.getPatient());
		}
		Result<IEncounter> result = EncounterServiceHolder.get().transferToCoverage(encounter, privateCoverage, false);
		if (!result.isOK()) {
			System.err.println("Move failed for encounter " + encounter.getLabel() + " [" + result.toString() + "]");
		}
	}

	private ICoverage createPrivateCoverage(IPatient patient) {
		return new ICoverageBuilder(CoreModelServiceHolder.get(), patient, "Selbstzahler",
				ICoverageBuilder.getDefaultCoverageReason(ConfigServiceHolder.get()), BillingLaw.privat.name())
						.buildAndSave();

	}

	private Optional<ICoverage> getPrivateCoverage(IPatient patient) {
		ICoverage bestMatch = null;
		for (ICoverage coverage : patient.getCoverages()) {
			if (coverage.isOpen()) {
				if (coverage.getBillingSystem().getLaw() == BillingLaw.privat
						|| coverage.getBillingSystem().getLaw() == BillingLaw.VVG) {
					bestMatch = coverage;
				}
			}
		}
		return Optional.ofNullable(bestMatch);
	}

	private boolean canMove(IEncounter encounter) {
		Result<IEncounter> editableResult = BillingServiceHolder.get().isEditable(encounter);
		if (!editableResult.isOK()) {
			System.err
					.println("Can not move encounter " + encounter.getLabel() + " [" + editableResult.toString() + "]");
		}
		return editableResult.isOK();
	}

	private boolean shouldMove(IEncounter encounter) {
		if (encounter.getCoverage() != null && encounter.getCoverage().getBillingSystem() != null) {
			BillingLaw law = encounter.getCoverage().getBillingSystem().getLaw();
			return law != BillingLaw.privat && law != BillingLaw.VVG;
		}
		return false;
	}

	private boolean isSzEncounter(IEncounter encounter) {
		Optional<IBilled> szBilled = encounter.getBilled().stream()
				.filter(b -> b.getBillable() != null && b.getBillable().getCodeSystemName() != null
						&& b.getBillable().getCodeSystemName().equalsIgnoreCase("Pandemie")
						&& b.getCode().startsWith("01.99"))
				.findFirst();
		return szBilled.isPresent();
	}

	@Override
	public String getMaintenanceDescription() {
		return "COVID Test Zertifikat Selbstzahler Verrechnung korrigieren.";
	}
}
