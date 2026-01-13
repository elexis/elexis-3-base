package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation.LimitationUnit;
import ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher.TarifMatcher;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedUtil;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.jpa.entities.Verrechnet;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.builder.IBilledBuilder;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

public class TardocOptifier implements IBillableOptifier<TardocLeistung> {

	private Map<String, Object> contextMap;

	private TardocVerifier verifier;

	private TarifMatcher<TardocLeistung> tarifMatcher;

	public TardocOptifier() {
		verifier = new TardocVerifier();
	}

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

		IBilled newBilled = getOrInitializeBilled(code, encounter, false);

		if (code.requiresSide()) {
			Result<IBilled> resultSide = setNewBilledSideOrIncrement(newBilled, encounter);
			if (!resultSide.isOK()) {
				return resultSide;
			} else if (!resultSide.get().equals(newBilled)) {
				newBilled = resultSide.get();
			}
		}

		if (code.isZuschlagsleistung()) {
			Result<IBilled> resultAddBezug = addBezug(newBilled, encounter);
			if (!resultAddBezug.isOK()) {
				return resultAddBezug;
			}
		}

		if (bOptify) {
			Result<IBilled> limitationsResult = verifier.checkLimitations(encounter, code, newBilled);
			if (!limitationsResult.isOK()) {
				if (bAllowOverrideStrict) {
					if (save) {
						CoreModelServiceHolder.get().save(newBilled);
					}
					return limitationsResult;
				} else {
					// reset possible modifications
					CoreModelServiceHolder.get().refresh(newBilled, true, true);
					return limitationsResult;
				}
			}

			Result<IBilled> digniResult = verifier.checkDigni(encounter, code, newBilled);
			if (!digniResult.isOK()) {
				if (bAllowOverrideStrict) {
					if (save) {
						CoreModelServiceHolder.get().save(newBilled);
					}
					return digniResult;
				} else {
					// reset possible modifications
					CoreModelServiceHolder.get().refresh(newBilled, true, true);
					return digniResult;
				}
			}
		}

		Result<IBilled> matcherResult = tarifMatcher.evaluate(newBilled, encounter);

		if (!matcherResult.isOK()) {
			if (bAllowOverrideStrict) {
				if (save) {
					CoreModelServiceHolder.get().save(newBilled);
					CoreModelServiceHolder.get().save(encounter);
					CoreModelServiceHolder.get().save(matcherResult.get());
				}
			} else {
				CoreModelServiceHolder.get().refresh(encounter, true);
			}
		} else {
			if (save) {
				CoreModelServiceHolder.get().save(encounter);
				CoreModelServiceHolder.get().save(matcherResult.get());
			}
		}

		return matcherResult;
	}

	private Result<IBilled> setNewBilledSideOrIncrement(IBilled newBilled, IEncounter encounter) {
		int countSideLeft = 0;
		IBilled leftVerrechnet = null;
		int countSideRight = 0;
		IBilled rightVerrechnet = null;

		for (IBilled v : encounter.getBilled()) {
			if (isInstance(v, newBilled.getBillable())) {
				String side = (String) v.getExtInfo(Constants.FLD_EXT_SIDE);
				if (side.equals(Constants.SIDE_L)) {
					countSideLeft += v.getAmount();
					leftVerrechnet = v;
				} else {
					countSideRight += v.getAmount();
					rightVerrechnet = v;
				}
			}
		}
		// if side is provided by context use that side
		if (isContext(Constants.FLD_EXT_SIDE)) {
			String side = (String) getContextValue(Constants.FLD_EXT_SIDE);
			if (Constants.SIDE_L.equals(side) && countSideLeft > 0 && leftVerrechnet != null) {
				newBilled = leftVerrechnet;
				newBilled.setAmount(newBilled.getAmount() + 1);
			} else if (Constants.SIDE_R.equals(side) && countSideRight > 0 && rightVerrechnet != null) {
				newBilled = rightVerrechnet;
				newBilled.setAmount(newBilled.getAmount() + 1);
			}
		}
		// toggle side if no side provided by context
		if (countSideLeft > 0 || countSideRight > 0) {
			if ((countSideLeft > countSideRight) && rightVerrechnet != null) {
				newBilled = rightVerrechnet;
				newBilled.setAmount(newBilled.getAmount() + 1);
			} else if ((countSideLeft <= countSideRight) && leftVerrechnet != null) {
				newBilled = leftVerrechnet;
				newBilled.setAmount(newBilled.getAmount() + 1);
			} else if ((countSideLeft > countSideRight) && rightVerrechnet == null) {
				newBilled.setAmount(newBilled.getAmount() - 1);
				newBilled = initializeBilled((TardocLeistung) newBilled.getBillable(), encounter, false);
				newBilled.setExtInfo(Constants.FLD_EXT_SIDE, Constants.SIDE_R);
				return new Result<IBilled>(newBilled);
			}
		}
		newBilled.setExtInfo(Constants.FLD_EXT_SIDE, Constants.SIDE_L);
		return new Result<IBilled>(newBilled);
	}

	private boolean isContext(String key) {
		return getContextValue(key) != null;
	}

	private Object getContextValue(String key) {
		if (contextMap != null) {
			return contextMap.get(key);
		}
		return null;
	}

	private boolean isInstance(IBilled billed, ICodeElement billable) {
		boolean sameCode = (billed.getBillable().getCode().equals(billable.getCode()));
		boolean sameCodeSystemCode = (billed.getBillable().getCodeSystemCode().equals(billable.getCodeSystemCode()));
		return (sameCodeSystemCode && sameCode);
	}

	/**
	 * Always toggle the side of a specific code. Starts with left, then right, then
	 * add to the respective side.
	 *
	 * @param code
	 * @param lst
	 * @return
	 */


	private Result<IBilled> addBezug(IBilled newBilled, IEncounter encounter) {
		if (StringUtils.isBlank((String) newBilled.getExtInfo("Bezug"))) {
			// lookup available masters
			List<IBilled> masters = getPossibleMasters(newBilled, encounter.getBilled());
			if (masters.isEmpty()) {
				return new Result<IBilled>(
						Result.SEVERITY.WARNING, TarifMatcher.KOMBINATION, "Für die Zuschlagsleistung "
								+ newBilled.getCode() + " konnte keine passende Hauptleistung gefunden werden.",
						null, false);
			}
			if (!masters.isEmpty()) {
				String bezug = (String) newBilled.getExtInfo("Bezug");
				if (bezug == null) {
					// set bezug to first available master
					newBilled.setExtInfo("Bezug", masters.get(0).getCode());
				} else {
					boolean found = false;
					// lookup matching, or create new Verrechnet
					for (IBilled mVerr : masters) {
						if (mVerr.getCode().equals(bezug)) {
							// just mark as found as amount is already increased
							found = true;
						}
					}
					if (!found) {
						// create a new Verrechent and decrease amount
//						newVerrechnet.setAmount(newVerrechnet.getAmount() - 1);
//						saveBilled();
//						newVerrechnet = initializeBilled(code, kons);
//						newVerrechnet.setExtInfo("Bezug", masters.get(0).getCode());
					}
				}
			}
		}
		return new Result<IBilled>(newBilled);
	}

	private List<IBilled> getPossibleMasters(IBilled newSlave, List<IBilled> lst) {
		TardocLeistung slaveTarmed = (TardocLeistung) newSlave.getBillable();
		// lookup available masters
		List<IBilled> masters = getAvailableMasters(slaveTarmed, lst);
		// check which masters are left to be referenced
		int maxPerMaster = getMaxPerMaster(slaveTarmed);
		if (maxPerMaster > 0) {
			Map<IBilled, List<IBilled>> masterSlavesMap = getMasterToSlavesMap(newSlave, lst);
			for (IBilled master : masterSlavesMap.keySet()) {
				double masterCount = master.getAmount();
				int slaveCount = 0;
				for (IBilled slave : masterSlavesMap.get(master)) {
					slaveCount += slave.getAmount();
					if (slave.equals(newSlave)) {
						slaveCount--;
					}
				}
				if (masterCount <= (slaveCount * maxPerMaster)) {
					masters.remove(master);
				}
			}
		}
		return masters;
	}

	private List<IBilled> getAvailableMasters(TardocLeistung slave, List<IBilled> lst) {
		List<IBilled> ret = new LinkedList<IBilled>();
		LocalDate konsDate = null;
		for (IBilled v : lst) {
			if (konsDate == null) {
				konsDate = v.getEncounter().getDate();
			}
			if (v.getBillable() instanceof TardocLeistung) {
				TardocLeistung tl = (TardocLeistung) v.getBillable();
				if (tl.getHierarchy(konsDate).contains(slave.getCode())) { // $NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}

	private int getMaxPerMaster(TardocLeistung slave) {
		List<TardocLimitation> limits = slave.getLimitations();
		for (TardocLimitation limit : limits) {
			if (limit.getLimitationUnit() == LimitationUnit.MAINSERVICE) {
				// only an integer makes sense here
				return limit.getAmount();
			}
		}
		// default to unknown
		return -1;
	}

	/**
	 * Creates a map of masters associated to slaves by the Bezug. This map will not
	 * contain the newSlave, as it has no Bezug set yet.
	 *
	 * @param newSlave
	 * @param lst
	 * @return
	 */
	private Map<IBilled, List<IBilled>> getMasterToSlavesMap(IBilled newSlave, List<IBilled> lst) {
		Map<IBilled, List<IBilled>> ret = new HashMap<>();
		TardocLeistung slaveTarmed = (TardocLeistung) newSlave.getBillable();
		// lookup available masters
		List<IBilled> masters = getAvailableMasters(slaveTarmed, lst);
		for (IBilled verrechnet : masters) {
			ret.put(verrechnet, new ArrayList<IBilled>());
		}
		// lookup other slaves with same code
		List<IBilled> slaves = getVerrechnetMatchingCode(lst, newSlave.getCode());
		// add slaves to separate master list
		for (IBilled slave : slaves) {
			String bezug = (String) slave.getExtInfo("Bezug");
			if (bezug != null && !bezug.isEmpty()) {
				for (IBilled master : ret.keySet()) {
					if (master.getCode().equals(bezug)) {
						ret.get(master).add(slave);
					}
				}
			}
		}
		return ret;
	}

	private List<IBilled> getVerrechnetMatchingCode(List<IBilled> lst, String code) {
		List<IBilled> ret = new ArrayList<IBilled>();
		for (IBilled v : lst) {
			if (v.getBillable() instanceof TardocLeistung) {
				TardocLeistung tl = (TardocLeistung) v.getBillable();
				if (tl.getCode().equals(code)) { // $NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
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
		List<IBilled> l = encounter.getBilled();
		l.remove(billed);
		deleteBilled(billed);
		// if no more left, check for bezug and remove
		List<IBilled> left = getVerrechnetMatchingCode(l, billed.getCode());
		if (left.isEmpty()) {
			List<IBilled> verrechnetWithBezug = getVerrechnetWithBezugMatchingCode(encounter.getBilled(),
					billed.getCode());
			for (IBilled verrechnet : verrechnetWithBezug) {
				remove(verrechnet, encounter);
			}
		}
		return new Result<IBilled>(billed);
	}

	private void deleteBilled(IBilled billed) {
		CoreModelServiceHolder.get().delete(billed);
	}

	private List<IBilled> getVerrechnetWithBezugMatchingCode(List<IBilled> lst, String code) {
		List<IBilled> ret = new ArrayList<IBilled>();
		for (IBilled v : lst) {
			if (v.getBillable() instanceof TardocLeistung) {
				if (code.equals(v.getExtInfo("Bezug"))) { //$NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}

	@Override
	public Optional<IBillingSystemFactor> getFactor(IEncounter encounter) {
		return BillingServiceHolder.get().getBillingSystemFactor(encounter.getCoverage().getBillingSystem().getName(),
				encounter.getDate());
	}

	private IBilled getOrInitializeBilled(TardocLeistung code, IEncounter kons, boolean save) {
		IBilled ret = null;
		// if code already billed increment amount
		for (IBilled billed : kons.getBilled()) {
			if (isInstance(billed, code)) {
				ret = billed;
				ret.setAmount(ret.getAmount() + 1);
				break;
			}
		}
		if (ret == null) {
			ret = initializeBilled(code, kons, save);
		}
		if (save) {
			CoreModelServiceHolder.get().save(ret);
		}
		return ret;
	}

	private IBilled initializeBilled(TardocLeistung code, IEncounter kons, boolean save) {
		IContact biller = ContextServiceHolder.get().getActiveUserContact().get();
		IBilled ret = new IBilledBuilder(CoreModelServiceHolder.get(), code, kons, biller).build();
		ret.setPoints(code.getAL(kons.getMandator()) + code.getIPL());
		ret.setExtInfo(Verrechnet.EXT_VERRRECHNET_AL, Integer.toString(code.getAL(kons.getMandator())));
		ret.setExtInfo(Verrechnet.EXT_VERRRECHNET_TL, Integer.toString(code.getIPL()));
		Optional<IBillingSystemFactor> systemFactor = getFactor(kons);
		if (systemFactor.isPresent()) {
			ret.setFactor(systemFactor.get().getFactor());
		} else {
			ret.setFactor(1.0);
		}
		if (isFactorBased(code)) {
			applyCalculateFactorBasedPrice(ret, code, kons);
		}
		return ret;
	}

	private void applyCalculateFactorBasedPrice(IBilled billed, TardocLeistung code, IEncounter encounter) {
		// lookup bezug
		Optional<String> bezug = Optional.empty();
		// lookup available masters
		List<IBilled> masters = getPossibleMasters(billed, encounter.getBilled());
		if (!masters.isEmpty()) {
			bezug = Optional.of(masters.get(0).getCode());
		}

		Double alFactor = getFactorValue(code, TardocConstants.TardocLeistung.EXT_FLD_F_AL);
		double alSum = 0.0;
		double tlSum = 0.0;
		if (alFactor > 0.0) {
			for (IBilled v : encounter.getBilled()) {
				if (v.getBillable() instanceof TardocLeistung) {
					TardocLeistung tl = (TardocLeistung) v.getBillable();
					if (bezug.isEmpty() || bezug.get().equals(tl.getCode())) {
						alSum += (tl.getAL(encounter.getMandator()) * v.getAmount());
					}
				}
			}
			billed.setPoints((int) Math.round(alSum));
			billed.setExtInfo(Verrechnet.EXT_VERRRECHNET_AL, Double.toString(alSum));
			billed.setPrimaryScale((int) (alFactor * 100));
		}
		Double tlFactor = getFactorValue(code, TardocConstants.TardocLeistung.EXT_FLD_F_TL);
		if (tlFactor > 0.0 && (alFactor == 0.0 || Double.compare(tlFactor, alFactor) == 0)) {
			for (IBilled v : encounter.getBilled()) {
				if (v.getBillable() instanceof TardocLeistung) {
					TardocLeistung tl = (TardocLeistung) v.getBillable();
					if (bezug.isEmpty() || bezug.get().equals(tl.getCode())) {
						tlSum += (tl.getIPL() * v.getAmount());
					}
				}
			}
			billed.setPoints((int) Math.round(tlSum + alSum));
			billed.setExtInfo(Verrechnet.EXT_VERRRECHNET_TL, Double.toString(tlSum));
			billed.setPrimaryScale((int) (tlFactor * 100));
		}
	}

	private boolean isFactorBased(TardocLeistung code) {
		if (code.getExtension() != null) {
			Double alFactor = getFactorValue(code, TardocConstants.TardocLeistung.EXT_FLD_F_AL);
			Double tlFactor = getFactorValue(code, TardocConstants.TardocLeistung.EXT_FLD_F_TL);
			return alFactor > 0.0 || tlFactor > 0.0;
		}
		return false;
	}

	private Double getFactorValue(TardocLeistung code, String key) {
		if (code.getExtension() != null && code.getExtension().getExtInfo(key) != null) {
			try {
				return Double.parseDouble((String) code.getExtension().getExtInfo(key));
			} catch (Exception e) {
				// ignore
			}
		}
		return Double.valueOf(0.0);
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
