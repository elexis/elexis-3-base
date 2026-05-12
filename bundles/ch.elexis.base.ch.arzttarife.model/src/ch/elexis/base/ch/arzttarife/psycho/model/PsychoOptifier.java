package ch.elexis.base.ch.arzttarife.psycho.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.jpa.entities.TarmedLeistung.MandantType;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.builder.IBilledBuilder;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.rgw.tools.Result;

public class PsychoOptifier extends AbstractOptifier<PsychoLeistung> {

	private PsychoVerifier verifier;

	public PsychoOptifier(IModelService coreModelService, IContextService contextService) {
		super(coreModelService, contextService);
		verifier = new PsychoVerifier();
	}

	@Override
	public Result<IBilled> add(PsychoLeistung billable, IEncounter encounter, double amount, boolean save) {
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
		return new Result<>(billed);
	}

	@Override
	public Result<IBilled> remove(IBilled billed, IEncounter encounter) {
		Result<IBilled> ret = super.remove(billed, encounter);
		if (!isPercent((PsychoLeistung) billed.getBillable())) {
			updatePercent(billed);
		}
		return ret;
	}

	@Override
	protected void setPrice(PsychoLeistung billable, IBilled billed) {
		Optional<IBillingSystemFactor> billingFactor = getFactor(billed.getEncounter());
		if (billingFactor.isPresent()) {
			billed.setFactor(billingFactor.get().getFactor());
		} else {
			billed.setFactor(1.0);
		}
		int points = 0;
		if (billable.getTP() != null) {
			if (!isPercent(billable)) {
				try {
					points = getPoints(billable, billed.getEncounter().getMandator());
				} catch (NumberFormatException ne) {
					// ignore ...
				}
			}
		}
		billed.setPoints(points);
		updatePercent(billed);
	}

	private int getPoints(PsychoLeistung billable, IMandator mandator) {
		MandantType type = TarmedLeistung.getMandantType(mandator);
		if (type == MandantType.TARPSYAPPRENTICE) {
			return Integer.valueOf(billable.getTP()) * 90;
		} else {
			return Integer.valueOf(billable.getTP()) * 100;
		}
	}

	private void updatePercent(IBilled billed) {
		List<IBilled> allPsycho = getAllPsycho(billed);
		for (IBilled psychoBilled : allPsycho) {
			if (isPercent((PsychoLeistung) psychoBilled.getBillable())) {
				setPercent(psychoBilled, allPsycho);
			}
		}
	}

	/**
	 * Get all {@link IBilled} of the encounter, including the provided
	 * {@link IBilled} instead of an possible old version from the encounter.
	 *
	 * @param billed
	 * @return
	 */
	private List<IBilled> getAllPsycho(IBilled billed) {
		List<IBilled> ret = new ArrayList<>();
		List<IBilled> encounterPsycho = new ArrayList<>(getPsycho(billed.getEncounter().getBilled()));
		for (IBilled encounterBilled : encounterPsycho) {
			if (!encounterBilled.getId().equals(billed.getId())) {
				ret.add(encounterBilled);
			}
		}
		if (!billed.isDeleted()) {
			ret.add(billed);
		}
		return ret;
	}

	private boolean isPercent(PsychoLeistung billable) {
		return billable.getTP().startsWith("%");
	}

	private void setPercent(IBilled billed, List<IBilled> allPsycho) {
		int sumTP = 0;
		for (IBilled psychoBilled : allPsycho) {
			if (!isPercent((PsychoLeistung) psychoBilled.getBillable())) {
				sumTP += getPoints((PsychoLeistung) psychoBilled.getBillable(),
						psychoBilled.getEncounter().getMandator()) * psychoBilled.getAmount();
			}
		}
		int percent = getPercent((PsychoLeistung) billed.getBillable());
		billed.setPoints(sumTP);
		billed.setPrimaryScale(percent);
		CoreModelServiceHolder.get().save(billed);
		ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, billed);
	}

	private List<IBilled> getPsycho(List<IBilled> billed) {
		return billed.stream().filter(b -> b.getBillable() instanceof PsychoLeistung).collect(Collectors.toList());
	}

	private int getPercent(PsychoLeistung billedPsycho) {
		return Integer.parseInt(billedPsycho.getTP().substring(1));
	}

	@Override
	public Optional<IBillingSystemFactor> getFactor(IEncounter encounter) {
		return BillingServiceHolder.get().getBillingSystemFactor(encounter.getCoverage().getBillingSystem().getName(),
				encounter.getDate());
	}
}
