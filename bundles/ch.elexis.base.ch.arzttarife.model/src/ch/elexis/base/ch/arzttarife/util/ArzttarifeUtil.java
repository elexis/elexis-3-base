package ch.elexis.base.ch.arzttarife.util;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.MandantType;
import ch.elexis.core.jpa.entities.TarmedLeistung;
import ch.elexis.core.jpa.entities.Verrechnet;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;

public class ArzttarifeUtil {
	
	private static String MANDANT_TYPE_EXTINFO_KEY = "ch.elexis.data.tarmed.mandant.type";
	
	/**
	 * Set the {@link MandantType} of the {@link IMandator}.
	 * 
	 * @param mandant
	 * @param type
	 */
	public static void setMandantType(IMandator mandator, MandantType type){
		mandator.setExtInfo(MANDANT_TYPE_EXTINFO_KEY, type.name());
	}
	
	/**
	 * Get the {@link MandantType} of the {@link IMandator}. If not found the default value is
	 * {@link MandantType#SPECIALIST}.
	 * 
	 * @param mandant
	 * @return
	 * @since 3.4
	 */
	public static MandantType getMandantType(IMandator mandator){
		Object typeObj = mandator.getExtInfo(MANDANT_TYPE_EXTINFO_KEY);
		if (typeObj instanceof String) {
			return MandantType.valueOf((String) typeObj);
		}
		return MandantType.SPECIALIST;
	}
	
	/**
	 * Get the configured factor for the billing system of the {@link ICoverage} at the provided
	 * date.
	 * 
	 * @param date
	 * @param coverage
	 * @return
	 */
	public static double getFactor(LocalDate date, ICoverage coverage){
		Optional<IBillingSystemFactor> billingSystemFactor =
			getBillingSystemFactor(coverage.getBillingSystem().getName(), date);
		if (billingSystemFactor.isPresent()) {
			return billingSystemFactor.get().getFactor();
		}
		LoggerFactory.getLogger(ArzttarifeUtil.class).warn("No IBillingSystemFactor for system ["
			+ coverage.getBillingSystem().getName() + "] on [" + date + "]");
		return 1.0;
	}
	
	/**
	 * Get a valid {@link IBillingSystemFactor} object that is matching the system and valid on the
	 * provided date.
	 * 
	 * @param system
	 * @param date
	 * @return
	 */
	private static Optional<IBillingSystemFactor> getBillingSystemFactor(String system,
		LocalDate date){
		IQuery<IBillingSystemFactor> query =
			CoreModelServiceHolder.get().getQuery(IBillingSystemFactor.class);
		query.and(ModelPackage.Literals.IBILLING_SYSTEM_FACTOR__SYSTEM, COMPARATOR.EQUALS, system);
		query.and(ModelPackage.Literals.IBILLING_SYSTEM_FACTOR__VALID_FROM,
			COMPARATOR.LESS_OR_EQUAL, date);
		query.and(ModelPackage.Literals.IBILLING_SYSTEM_FACTOR__VALID_TO,
			COMPARATOR.GREATER_OR_EQUAL, date);
		return query.executeSingleResult();
	}
	
	/**
	 * Get the AL value of the {@link IBilled} from the ext info of it. If there is no such
	 * information present and the {@link IBillable} linked with the {@link IBilled} is a
	 * {@link ITarmedLeistung}, that AL value is returned as fallback.
	 * 
	 * @param billed
	 * @return
	 */
	public static double getAL(IBilled billed){
		// if price was changed, use TP as AL
		boolean changedPrice = billed.isChangedPrice();
		if (changedPrice) {
			return billed.getPoints();
		}
		String alString = (String) billed.getExtInfo(Verrechnet.EXT_VERRRECHNET_AL);
		if (alString != null) {
			try {
				return (int) Double.parseDouble(alString);
			} catch (NumberFormatException ne) {
				// ignore, try resolve from IVerrechenbar
			}
		}
		IBillable billable = billed.getBillable();
		if (billable instanceof ITarmedLeistung) {
			IEncounter encounter = billed.getEncounter();
			return encounter != null ? ((ITarmedLeistung) billable).getAL(encounter.getMandator())
					: ((ITarmedLeistung) billable).getAL();
		}
		return 0;
	}
	
	/**
	 * Get the TL value of the {@link IBilled} from the ext info of it. If there is no such
	 * information present and the {@link IBillable} linked with the {@link IBilled} is a
	 * {@link ITarmedLeistung}, that TL value is returned as fallback.
	 * 
	 * @param billed
	 * @return
	 */
	public static double getTL(IBilled billed){
		String tlString = (String) billed.getExtInfo(Verrechnet.EXT_VERRRECHNET_TL);
		if (tlString != null) {
			try {
				return (int) Double.parseDouble(tlString);
			} catch (NumberFormatException ne) {
				// ignore, try resolve from IVerrechenbar
			}
		}
		IBillable billable = billed.getBillable();
		if (billable instanceof ITarmedLeistung) {
			return ((ITarmedLeistung) billable).getTL();
		}
		return 0;
	}
	
	/**
	 * Get the tarmed side information of the {@link IBilled}. If there is no
	 * {@link ITarmedLeistung} referenced from the {@link IBilled} the returned value is always
	 * "none".
	 * 
	 * @param billed
	 * @return
	 */
	public static String getSide(IBilled billed){
		IBillable billable = billed.getBillable();
		if (billable instanceof ITarmedLeistung) {
			String side = (String) billed.getExtInfo(TarmedLeistung.SIDE);
			if (TarmedLeistung.SIDE_L.equalsIgnoreCase(side)) {
				return TarmedLeistung.LEFT;
			} else if (TarmedLeistung.SIDE_R.equalsIgnoreCase(side)) {
				return TarmedLeistung.RIGHT;
			}
		}
		return "none";
	}
	
	/**
	 * Test if the billed is marked as an obligation. If there is no {@link ITarmedLeistung}
	 * referenced from the {@link IBilled} the returned value is always false.
	 * 
	 * @param billed
	 * @return
	 */
	public static boolean isObligation(IBilled billed){
		IBillable billable = billed.getBillable();
		if (billable instanceof ITarmedLeistung) {
			String obli = (String) billed.getExtInfo(TarmedLeistung.PFLICHTLEISTUNG);
			if ((obli == null) || (Boolean.parseBoolean(obli))) {
				return true;
			}
		}
		return false;
	}
}
