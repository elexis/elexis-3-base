package ch.elexis.base.ch.arzttarife.tardoc.model;

import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.rgw.tools.Result;

public class TardocVerifier implements IBillableVerifier {

	@Override
	public Result<IBillable> verifyAdd(IBillable billable, IEncounter encounter, double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<IBilled> verify(IEncounter encounter) {
		// TODO Auto-generated method stub
		return null;
	}

}
