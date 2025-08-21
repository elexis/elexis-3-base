package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher.TarifMatcher;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedUtil;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.builder.IBilledBuilder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

public class TardocOptifier implements IBillableOptifier<TardocLeistung> {

	private Map<String, Object> contextMap;

	private TarifMatcher<TardocLeistung> tarifMatcher;

	@Override
	public Result<IBilled> add(TardocLeistung code, IEncounter encounter, double amount, boolean save) {
		int amountInt = doubleToInt(amount);
		boolean setNonIntAmount = amount % 1 != 0;
		Result<IBilled> result = null;
		try {
			if (!code.isChapter() && amountInt >= 1) {
				result = add(code, encounter, save);
				if (amountInt == 1) {
					return result;
				}
				for (int i = 2; i <= amountInt; i++) {
					Result<IBilled> intermediateResult = add(code, encounter, save);
					if (!intermediateResult.isOK()) {
						result.addMessage(SEVERITY.WARNING, intermediateResult.toString(), result.get());
						return result;
					} else {
						result = intermediateResult;
					}
				}
				return result;
			} else {
				return Result.OK();
			}
		} finally {
			if (setNonIntAmount && result != null && result.get() != null) {
				result.get().setAmount(amount);
				if (save) {
					CoreModelServiceHolder.get().save(result.get());
				}
			}
		}
	}

	private Result<IBilled> add(TardocLeistung code, IEncounter encounter, boolean save) {
		if (tarifMatcher == null) {
			tarifMatcher = new TarifMatcher<TardocLeistung>(this);
		}

		boolean bOptify = TarmedUtil.getConfigValue(getClass(), IMandator.class, Preferences.LEISTUNGSCODES_OPTIFY,
				true);

		boolean bAllowOverrideStrict = TarmedUtil.getConfigValue(getClass(), IUser.class,
				Preferences.LEISTUNGSCODES_ALLOWOVERRIDE_STRICT, false);

		IBilled newVerrechnet = initializeBilled(code, encounter, false);

		Result<IBilled> matcherResult = tarifMatcher.evaluate(newVerrechnet, encounter);

		if (matcherResult.isOK()) {
			if (save) {
				CoreModelServiceHolder.get().save(encounter);
				CoreModelServiceHolder.get().save(matcherResult.get());
			}
		} else {
			CoreModelServiceHolder.get().refresh(encounter, true);
		}

		return matcherResult;
	}

	@Override
	public void putContext(String key, Object value) {
		if (contextMap == null) {
			contextMap = new HashMap<String, Object>();
		}
		contextMap.put(key, value);
	}

	@Override
	public void clearContext() {
		if (contextMap != null) {
			contextMap.clear();
		}
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

	private IBilled initializeBilled(TardocLeistung code, IEncounter kons, boolean save) {
		IContact biller = ContextServiceHolder.get().getActiveUserContact().get();
		IBilled ret = new IBilledBuilder(CoreModelServiceHolder.get(), code, kons, biller).build();
		ret.setPoints(code.getAL(kons.getMandator()) + code.getIPL());
		Optional<IBillingSystemFactor> systemFactor = getFactor(kons);
		if (systemFactor.isPresent()) {
			ret.setFactor(systemFactor.get().getFactor());
		} else {
			ret.setFactor(1.0);
		}
		if (save) {
			CoreModelServiceHolder.get().save(ret);
		}
		return ret;
	}

	/**
	 * Get double as int rounded half up.
	 *
	 * @param value
	 * @return
	 */
	private int doubleToInt(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(0, RoundingMode.HALF_UP);
		if (bd.intValue() > 0) {
			return bd.intValue();
		} else {
			return 1;
		}
	}
}
