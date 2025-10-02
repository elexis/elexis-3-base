package ch.elexis.base.ch.arzttarife.ambulatory.model;

import java.util.Optional;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher.TarifMatcher;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IModelService;
import ch.rgw.tools.Result;

public class AmbulatoryAllowanceOptifier extends AbstractOptifier<AmbulatoryAllowance> {

	private TarifMatcher<AmbulatoryAllowance> tarifMatcher;

	public AmbulatoryAllowanceOptifier(IModelService coreModelService, IContextService contextService) {
		super(coreModelService, contextService);
	}

	@Override
	protected void setPrice(AmbulatoryAllowance billable, IBilled billed) {
		billed.setFactor(1.0);
		billed.setPoints(billable.getPrice(billed.getEncounter()).getCents());
	}

	@Override
	public Optional<IBillingSystemFactor> getFactor(IEncounter encounter) {
		return Optional.empty();
	}

	@Override
	public Result<IBilled> add(AmbulatoryAllowance billable, IEncounter encounter, double amount, boolean save) {
		if (tarifMatcher == null) {
			tarifMatcher = new TarifMatcher<AmbulatoryAllowance>(this);
		}

		IBilled billed = super.add(billable, encounter, amount, false).get();

		Result<IBilled> matcherResult = tarifMatcher.evaluate(billed, encounter);

		if (matcherResult.isOK()) {
			if (save) {
				// make sure trigger gets added
				CoreModelServiceHolder.get().save(billed);
				CoreModelServiceHolder.get().save(encounter);
				CoreModelServiceHolder.get().save(matcherResult.get());
			}
		} else {
			CoreModelServiceHolder.get().refresh(encounter, true);
		}

		return matcherResult;
	}


}
