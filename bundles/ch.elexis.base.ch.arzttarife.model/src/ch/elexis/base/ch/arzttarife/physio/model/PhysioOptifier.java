package ch.elexis.base.ch.arzttarife.physio.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.builder.IBilledBuilder;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.Result;

public class PhysioOptifier extends AbstractOptifier<PhysioLeistung> {

	private PhysioVerifier verifier;

	public PhysioOptifier(IModelService coreModelService, IContextService contextService) {
		super(coreModelService, contextService);
		verifier = new PhysioVerifier();
	}

	@Override
	public Result<IBilled> add(PhysioLeistung billable, IEncounter encounter, double amount, boolean save) {
		boolean added = false;
		IBilled billed = null;
		// lookup existing billed, add if found
		List<IBilled> existingBilled = encounter.getBilled();
		for (IBilled iBilled : existingBilled) {
			IBillable existing = iBilled.getBillable();
			if (existing != null && existing.equals(billable)) {
				setAmount(iBilled, iBilled.getAmount() + amount);
				Result<IBilled> limitationsResult = verifier.checkLimitations(encounter, billable, iBilled);
				if (!limitationsResult.isOK()) {
					// reset possible modifications
					CoreModelServiceHolder.get().refresh(iBilled, true, true);
					return limitationsResult;
				}
				if (save) {
					coreModelService.save(iBilled);
				}
				billed = iBilled;
				added = true;
				break;
			}
		}
		if (!added) {
			IContact activeUserContact = contextService.getActiveUserContact().get();
			billed = new IBilledBuilder(coreModelService, billable, encounter, activeUserContact).build();
			setAmount(billed, amount);
			setPrice(billable, billed);
			Result<IBilled> limitationsResult = verifier.checkLimitations(encounter, billable, billed);
			if (!limitationsResult.isOK()) {
				// reset possible modifications
				encounter.removeBilled(billed);
				return limitationsResult;
			}
			if (save) {
				coreModelService.save(billed);
			}
		}
		autoBill(billable, encounter, save);
		return new Result<>(billed);
	}

	/**
	 * Perform {@link PhysioLimitation} type AUTO_BILL if not already billed.
	 *
	 * @param billable
	 * @param encounter
	 * @param save
	 */
	private void autoBill(PhysioLeistung billable, IEncounter encounter, boolean save) {
		for (PhysioLimitation limitation : PhysioLimitation.get(billable)) {
			if (limitation.getType() == PhysioLimitation.TYPE.AUTO_BILL) {
				String markerCode = limitation.getAutoBillCode();
				boolean alreadyBilled = encounter.getBilled().stream()
						.anyMatch(b -> markerCode.equals(b.getCode()) && b.getBillable() instanceof PhysioLeistung);
				if (!alreadyBilled) {
					Optional<IPhysioLeistung> marker = loadValidAt(markerCode, encounter.getDate());
					if (marker.isPresent() && marker.get() instanceof PhysioLeistung) {
						IContact activeUserContact = contextService.getActiveUserContact().get();
						IBilled markerBilled = new IBilledBuilder(coreModelService, marker.get(), encounter,
								activeUserContact).build();
						setAmount(markerBilled, 1);
						setPrice((PhysioLeistung) marker.get(), markerBilled);
						if (save) {
							coreModelService.save(markerBilled);
						}
					}
				}
			}
		}
	}

	/**
	 * Load the {@link IPhysioLeistung} with the given code (Ziffer) that is valid
	 * at the given date.
	 *
	 * @param code
	 * @param date
	 * @return
	 */
	private Optional<IPhysioLeistung> loadValidAt(String code, LocalDate date) {
		IQuery<IPhysioLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(IPhysioLeistung.class);
		query.and("ziffer", COMPARATOR.EQUALS, code);
		for (IPhysioLeistung leistung : query.execute()) {
			if ((leistung.getValidFrom() == null || !date.isBefore(leistung.getValidFrom()))
					&& (leistung.getValidTo() == null || !date.isAfter(leistung.getValidTo()))) {
				return Optional.of(leistung);
			}
		}
		return Optional.empty();
	}

	@Override
	protected void setPrice(PhysioLeistung billable, IBilled billed) {
		Optional<IBillingSystemFactor> billingFactor = getFactor(billed.getEncounter());
		if (billingFactor.isPresent()) {
			billed.setFactor(billingFactor.get().getFactor());
		} else {
			billed.setFactor(1.0);
		}
		int points = 0;
		if (billable.getTP() != null) {
			try {
				points = Integer.valueOf(billable.getTP());
			} catch (NumberFormatException ne) {
				// ignore ...
			}
		}
		billed.setPoints(points);
	}

	@Override
	public Optional<IBillingSystemFactor> getFactor(IEncounter encounter) {
		return BillingServiceHolder.get().getBillingSystemFactor(encounter.getCoverage().getBillingSystem().getName(),
				encounter.getDate());
	}
}
