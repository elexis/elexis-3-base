package ch.elexis.base.ch.arzttarife.ui.handler;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.LoggerFactory;

import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.utils.CoreUtil;

/**
 * Perform arzttarife initialization and validation.
 *
 * @author thomas
 *
 */
@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class StartupHandler implements EventHandler {

	private static String CFG_UPDATE_BILLINGSYSTEMS_2026 = "ch.elexis.base.ch.arzttarife.billingsystems.2026";
	private static String PROP_UPDATE_BILLINGSYSTEMS_2026 = "/rsc/billingsystemfactor2026.properties";

	@Override
	public void handleEvent(Event event) {
		LoggerFactory.getLogger(getClass()).info("APPLICATION STARTUP COMPLETE"); //$NON-NLS-1$

		if (!CoreUtil.isTestMode() && !isUpdateExecuted(CFG_UPDATE_BILLINGSYSTEMS_2026)
				&& getClass().getResourceAsStream(PROP_UPDATE_BILLINGSYSTEMS_2026) != null) {
			CompletableFuture.runAsync(() -> {
				try {
					Properties billingsystems2026 = new Properties();
					billingsystems2026.load(getClass().getResourceAsStream(PROP_UPDATE_BILLINGSYSTEMS_2026)); // $NON-NLS-1$

					Optional<String> kanton = getKanton();

					List<IBillingSystemFactor> billingSystemFactors = CoreModelServiceHolder.get()
							.getQuery(IBillingSystemFactor.class).execute();
					LocalDate validFrom = LocalDate.of(2026, 1, 1);
					for (IBillingSystemFactor billingSystemFactor : billingSystemFactors) {
						// do not update non valid factors
						if (billingSystemFactor.getValidTo().isBefore(validFrom)) {
							continue;
						}
						if (billingSystemFactor.getSystem() != null) {
							if (isMatchingLaw(billingSystemFactor, BillingLaw.UVG)) {
								updateBillingSystemFactor(billingSystemFactor, null, BillingLaw.UVG, validFrom,
										billingsystems2026);
							} else if (isMatchingLaw(billingSystemFactor, BillingLaw.IV)) {
								updateBillingSystemFactor(billingSystemFactor, null, BillingLaw.IV, validFrom,
										billingsystems2026);
							} else if (isMatchingLaw(billingSystemFactor, BillingLaw.MV)) {
								updateBillingSystemFactor(billingSystemFactor, null, BillingLaw.MV, validFrom,
										billingsystems2026);
							} else if (isMatchingLaw(billingSystemFactor, BillingLaw.VVG)) {
								updateBillingSystemFactor(billingSystemFactor, null, BillingLaw.VVG, validFrom,
										billingsystems2026);
							} else if (isMatchingLaw(billingSystemFactor, BillingLaw.ORG)) {
								updateBillingSystemFactor(billingSystemFactor, null, BillingLaw.ORG, validFrom,
										billingsystems2026);
							} else if (isMatchingLaw(billingSystemFactor, BillingLaw.KVG)) {
								updateBillingSystemFactor(billingSystemFactor, kanton.orElse(null), BillingLaw.KVG,
										validFrom, billingsystems2026);
							}
						}
					}
					setUpdateExecuted(CFG_UPDATE_BILLINGSYSTEMS_2026);
				} catch (Exception e) {
					LoggerFactory.getLogger(getClass()).error("Error updating billingsystems", e); //$NON-NLS-1$
				}
			});
		}
	}

	private boolean isMatchingLaw(IBillingSystemFactor billingSystemFactor, BillingLaw law) {
		if (law == BillingLaw.IV) {
			return billingSystemFactor.getSystem().toUpperCase().startsWith(law.name())
					|| billingSystemFactor.getSystem().toUpperCase().endsWith(law.name())
					|| billingSystemFactor.getSystem().toUpperCase().startsWith("IVG")
					|| billingSystemFactor.getSystem().toUpperCase().endsWith("IVG");
		} else if (law == BillingLaw.MV) {
			return billingSystemFactor.getSystem().toUpperCase().startsWith(law.name())
					|| billingSystemFactor.getSystem().toUpperCase().endsWith(law.name())
					|| billingSystemFactor.getSystem().toUpperCase().startsWith("MVG")
					|| billingSystemFactor.getSystem().toUpperCase().endsWith("MVG");
		}
		return billingSystemFactor.getSystem().toUpperCase().startsWith(law.name())
				|| billingSystemFactor.getSystem().toUpperCase().endsWith(law.name());
	}

	private boolean isUpdateExecuted(String string) {
		return ConfigServiceHolder.get().get(string, false);
	}

	private boolean setUpdateExecuted(String string) {
		return ConfigServiceHolder.get().set(string, true);
	}

	private void updateBillingSystemFactor(IBillingSystemFactor billingSystemFactor, String kanton, BillingLaw law,
			LocalDate validFrom, Properties billingFactors) {
		LocalDate currentEnd = billingSystemFactor.getValidTo();
		double currentValue = billingSystemFactor.getFactor();
		String propertyName = getPropertyName(law, kanton);
		if (StringUtils.isNotBlank(propertyName)) {
			Double newValue = Double.valueOf((String) billingFactors.get(propertyName));
			if (currentValue != newValue) {
				if (currentEnd.isAfter(validFrom)) {
					LoggerFactory.getLogger(getClass())
							.info("Update BillingSystemFactor [" + billingSystemFactor.getSystem() + "] ["
									+ currentValue + " -> " + newValue + "]");
					IBillingSystemFactor newBillingSystemFactor = CoreModelServiceHolder.get()
							.create(IBillingSystemFactor.class);
					newBillingSystemFactor.setFactor(newValue);
					newBillingSystemFactor.setSystem(billingSystemFactor.getSystem());
					newBillingSystemFactor.setValidFrom(validFrom);
					newBillingSystemFactor.setValidTo(LocalDate.of(9999, 12, 31));
					CoreModelServiceHolder.get().save(newBillingSystemFactor);
					billingSystemFactor.setValidTo(validFrom.minusDays(1));
					CoreModelServiceHolder.get().save(billingSystemFactor);
				} else {
					LoggerFactory.getLogger(getClass())
							.info("BillingSystemFactor [" + billingSystemFactor.getSystem() + "] already correct");
				}
			}
		}
	}

	private String getPropertyName(BillingLaw law, String kanton) {
		if (law == BillingLaw.KVG) {
			if (StringUtils.isBlank(kanton)) {
				LoggerFactory.getLogger(getClass()).warn("No Kanton for KVG"); //$NON-NLS-1$
				return StringUtils.EMPTY;
			}
			return law + "_" + kanton.toUpperCase();
		}
		return law.name().toUpperCase();
	}

	private Optional<String> getKanton() {
		HashSet<String> kanton = new HashSet<>();
		List<IMandator> mandators = CoreModelServiceHolder.get().getQuery(IMandator.class).execute();
		for (IMandator mandator : mandators) {
			if (StringUtils.isNotBlank((String) mandator.getExtInfo(TarmedACL.getInstance().KANTON))) {
				kanton.add((String) mandator.getExtInfo(TarmedACL.getInstance().KANTON));
			}
		}
		if (!kanton.isEmpty()) {
			if (kanton.size() == 1) {
				return Optional.of(kanton.stream().findFirst().get());
			} else {
				LoggerFactory.getLogger(getClass())
						.warn("More than one kanton found " + Arrays.toString(kanton.toArray())); //$NON-NLS-1$
			}
		}
		return Optional.empty();
	}
}
