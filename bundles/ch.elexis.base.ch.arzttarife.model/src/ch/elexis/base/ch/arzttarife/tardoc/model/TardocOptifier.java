package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.util.Optional;

import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IEncounter;
import ch.rgw.tools.Result;

public class TardocOptifier implements IBillableOptifier<TardocLeistung> {

	@Override
	public Result<IBilled> add(TardocLeistung billable, IEncounter encounter, double amount, boolean save) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putContext(String key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearContext() {
		// TODO Auto-generated method stub

	}

	@Override
	public Result<IBilled> remove(IBilled billed, IEncounter encounter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<IBillingSystemFactor> getFactor(IEncounter encounter) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}
