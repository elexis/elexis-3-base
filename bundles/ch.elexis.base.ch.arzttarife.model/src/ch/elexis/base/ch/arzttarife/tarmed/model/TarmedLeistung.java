package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelAdapterFactory;
import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation.LimitationUnit;
import ch.elexis.core.jpa.entities.TarmedLeistung.MandantType;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.rgw.tools.TimeTool;

public class TarmedLeistung
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedLeistung>
		implements IdentifiableWithXid, ITarmedLeistung {
	
	public static final String STS_CLASS = "ch.elexis.data.TarmedLeistung";
	
	private IBillableOptifier<TarmedLeistung> tarmedOptifier;
	private IBillableVerifier verifier;
	
	public static LocalDate curTimeHelper = LocalDate.now();
	
	public TarmedLeistung(ch.elexis.core.jpa.entities.TarmedLeistung entity){
		super(entity);
		verifier = new DefaultVerifier();
	}
	
	@Override
	public int getMinutes(){
		// TODO Auto-generated method stub
//		Hashtable<String, String> map = loadExtension();
//		double min = checkZeroDouble(map.get("LSTGIMES_MIN")); //$NON-NLS-1$
//		min += checkZeroDouble(map.get("VBNB_MIN")); //$NON-NLS-1$
//		min += checkZeroDouble(map.get("BEFUND_MIN")); //$NON-NLS-1$
//		min += checkZeroDouble(map.get("WECHSEL_MIN")); //$NON-NLS-1$
//		return (int) Math.round(min);
		return 0;
	}
	
	@Override
	public int getAL(){
		String tp_al = getExtension().getLimits()
			.get(TarmedConstants.TarmedLeistung.EXT_FLD_TP_AL);
		return NumberUtils.toInt(tp_al) * 100;
	}
	
	/**
	 * Get the AL value of the {@link TarmedLeistung}. The {@link IMandator} is needed to determine
	 * special scaling factors. On billing of the {@link TarmedLeistung} the values for AL and TL
	 * should be set to the ExtInfo of the {@link IBilled} for later use.
	 * 
	 * @param mandant
	 * @return
	 * @since 3.4
	 */
	public int getAL(IMandator mandant){
		String tp_al = getExtension().getLimits()
			.get(ch.elexis.core.jpa.entities.TarmedLeistung.EXT_FLD_TP_AL);
		return (int) Math.round(NumberUtils.toInt(tp_al) * getALScaling(mandant));
	}
	
	/**
	 * Get the AL scaling value to be used when billing this {@link TarmedLeistung} for the provided
	 * {@link Mandant}.
	 * 
	 * @param mandant
	 * @return
	 */
	public double getALScaling(IMandator mandant){
		double scaling = 100;
		if (mandant != null) {
			MandantType type = getMandantType(mandant);
			if (type == MandantType.PRACTITIONER) {
				String f_al_r = getExtension().getLimits()
					.get(ch.elexis.core.jpa.entities.TarmedLeistung.EXT_FLD_F_AL_R);
				double alScaling = NumberUtils.toDouble(f_al_r);
				if (alScaling > 0.1) {
					scaling *= alScaling;
				}
			}
		}
		return scaling;
	}
	
	@Override
	public int getTL(){
		String tp_al = getExtension().getLimits()
			.get(ch.elexis.core.jpa.entities.TarmedLeistung.EXT_FLD_TP_TL);
		return NumberUtils.toInt(tp_al) * 100;
	}
	
	@Override
	public String getDigniQuali(){
		return getEntity().getDigniQuali();
	}
	
	@Override
	public String getDigniQuanti(){
		return getEntity().getDigniQuanti();
	}
	
	@Override
	public String getExclusion(){
		curTimeHelper = LocalDate.now();
		return getExclusion(curTimeHelper);
	}
	
	@Override
	public synchronized IBillableOptifier<TarmedLeistung> getOptifier(){
		if (tarmedOptifier == null) {
			tarmedOptifier = new TarmedOptifier();
		}
		return tarmedOptifier;
	}
	
	@Override
	public IBillableVerifier getVerifier(){
		return verifier;
	}
	
	@Override
	public String getCodeSystemName(){
		return ch.elexis.core.jpa.entities.TarmedLeistung.CODESYSTEM_NAME;
	}
	
	@Override
	public String getCode(){
		return getEntity().getCode_();
	}
	
	@Override
	public void setCode(String value){
		getEntity().setCode_(value);
	}
	
	@Override
	public String getText(){
		return getEntity().getTx255();
	}
	
	@Override
	public void setText(String value){
		getEntity().setTx255(value);
	}
	
	@Override
	public ITarmedExtension getExtension(){
		return ArzttarifeModelAdapterFactory.getInstance().getAdapter(getEntity().getExtension(),
			ITarmedExtension.class, true);
	}
	
	@Override
	public ITarmedLeistung getParent(){
		String parent = getEntity().getParent();
		if (parent != null && !"NIL".equals(parent)) {
			return ArzttarifeModelServiceHolder.get().load(parent, ITarmedLeistung.class).get();
		}
		return null;
	}
	
	@Override
	public LocalDate getValidFrom(){
		return getEntity().getGueltigVon();
	}
	
	@Override
	public LocalDate getValidTo(){
		return getEntity().getGueltigBis();
	}
	
	@Override
	public boolean requiresSide(){
		if (getExtension() != null) {
			String value =
				getExtension().getLimits().get(TarmedConstants.TarmedLeistung.SIDE.toUpperCase());
			return "1".equals(value);
		}
		return false;
	}
	
	@Override
	public String getServiceTyp(){
		return getExtension().getLimits().get("LEISTUNG_TYP");
	}
	
	@Override
	public String getLaw(){
		return getEntity().getLaw();
	}
	
	@Override
	public String getSparte(){
		return getEntity().getSparte();
	}
	
	@Override
	public boolean isChapter(){
		return getEntity().isChapter();
	}
	
	public static String getSide(IBilled v){
		IBillable vv = v.getBillable();
		if (vv instanceof TarmedLeistung) {
			String side = (String) v.getExtInfo(ch.elexis.core.jpa.entities.TarmedLeistung.SIDE);
			if (ch.elexis.core.jpa.entities.TarmedLeistung.SIDE_L.equalsIgnoreCase(side)) {
				return ch.elexis.core.jpa.entities.TarmedLeistung.LEFT;
			} else if (ch.elexis.core.jpa.entities.TarmedLeistung.SIDE_R.equalsIgnoreCase(side)) {
				return ch.elexis.core.jpa.entities.TarmedLeistung.RIGHT;
			}
		}
		return "none";
	}
	
	public List<TarmedLimitation> getLimitations(){
		String lim = (String) getExtension().getLimits().get("limits"); //$NON-NLS-1$
		if (lim != null && !lim.isEmpty()) {
			List<TarmedLimitation> ret = new ArrayList<>();
			String[] lines = lim.split("#"); //$NON-NLS-1$
			for (String line : lines) {
				ret.add(TarmedLimitation.of(line).setTarmedLeistung(this));
			}
			fix9533(ret);
			return ret;
		}
		return Collections.emptyList();
	}
	
	/**
	 * Get the list of service blocks this service is part of.
	 * 
	 * @return
	 */
	@Override
	public List<String> getServiceBlocks(LocalDate date){
		List<String> ret = new ArrayList<>();
		List<String> blocks = getExtStringListField(
			ch.elexis.core.jpa.entities.TarmedLeistung.EXT_FLD_SERVICE_BLOCKS);
		if (!blocks.isEmpty()) {
			for (String string : blocks) {
				int dateStart = string.indexOf('[');
				String datesString = string.substring(dateStart + 1, string.length() - 1);
				String blockString = string.substring(0, dateStart);
				if (isDateWithinDatesString(date, datesString)) {
					ret.add(blockString);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Get the list of service groups this service is part of.
	 * 
	 * @return
	 */
	@Override
	public List<String> getServiceGroups(LocalDate date){
		List<String> ret = new ArrayList<>();
		List<String> groups = getExtStringListField(
			ch.elexis.core.jpa.entities.TarmedLeistung.EXT_FLD_SERVICE_GROUPS);
		if (!groups.isEmpty()) {
			for (String string : groups) {
				int dateStart = string.indexOf('[');
				String datesString = string.substring(dateStart + 1, string.length() - 1);
				String groupString = string.substring(0, dateStart);
				if (isDateWithinDatesString(date, datesString)) {
					ret.add(groupString);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Get the list of codes of the possible slave services allowed by tarmed.
	 * 
	 * @return
	 */
	public List<String> getHierarchy(LocalDate date){
		List<String> ret = new ArrayList<>();
		List<String> hierarchy = getExtStringListField(
			ch.elexis.core.jpa.entities.TarmedLeistung.EXT_FLD_HIERARCHY_SLAVES);
		if (!hierarchy.isEmpty()) {
			for (String string : hierarchy) {
				int dateStart = string.indexOf('[');
				String datesString = string.substring(dateStart + 1, string.length() - 1);
				String codeString = string.substring(0, dateStart);
				if (isDateWithinDatesString(date, datesString)) {
					ret.add(codeString);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Get the exclusions valid now as String, containing the service and chapter codes. Group
	 * exclusions are NOT part of the String.
	 * 
	 * @param kons
	 * 
	 * @return
	 */
	public List<TarmedExclusion> getExclusions(IEncounter kons){
		if (kons == null) {
			curTimeHelper = LocalDate.now();
		} else {
			curTimeHelper = kons.getDate();
		}
		return getExclusions(curTimeHelper);
	}
	
	/**
	 * Get {@link TarmedExclusion} objects with this {@link TarmedLeistung} as master.
	 * 
	 * @param date
	 * @return
	 */
	public List<TarmedExclusion> getExclusions(LocalDate date){
		return TarmedKumulation.getExclusions(getCode(),
			isChapter() ? TarmedKumulationType.CHAPTER : TarmedKumulationType.SERVICE, date,
			getLaw());
	}
	
	/**
	 * Get the exclusions valid at the paramater date as String, containing the service and chapter
	 * codes. Group exclusions are NOT part of the String.
	 * 
	 * @param date
	 * @return
	 */
	public String getExclusion(LocalDate date){
		String exclusions = TarmedKumulation.getExclusions(getCode(), date);
		if (exclusions == null) {
			Map<String, String> map = getExtension().getLimits();
			if (map == null) {
				return "";
			}
			return StringUtils.defaultString(map.get("exclusion"));
		}
		return StringUtils.defaultString(exclusions);
	}
	
	private boolean isDateWithinDatesString(LocalDate localDate, String datesString){
		String[] parts = datesString.split("\\|");
		if (parts.length == 2) {
			LocalDate from = LocalDate.parse(parts[0]);
			LocalDate to = LocalDate.parse(parts[1]);
			return (from.isBefore(localDate) || from.isEqual(localDate))
				&& (to.isAfter(localDate) || to.isEqual(localDate));
		}
		return false;
	}
	
	private List<String> getExtStringListField(String extKey){
		List<String> ret = new ArrayList<>();
		Map<String, String> map = getExtension().getLimits();
		String values = map.get(extKey);
		if (values != null && !values.isEmpty()) {
			String[] parts = values.split(", ");
			for (String string : parts) {
				ret.add(string);
			}
		}
		return ret;
	}
	
	/**
	 * Method marks {@link LimitationUnit#COVERAGE} {@link TarmedLimitation} as skip if in
	 * combination with a {@link LimitationUnit#SESSION}. This is a WORKAROUND and should be REMOVED
	 * after reason is fixed.
	 * 
	 * @param ret
	 */
	private void fix9533(List<TarmedLimitation> ret){
		boolean sessionfound = false;
		for (TarmedLimitation tarmedLimitation : ret) {
			if (tarmedLimitation.getLimitationUnit() == LimitationUnit.SESSION) {
				sessionfound = true;
				break;
			}
		}
		if (sessionfound) {
			for (TarmedLimitation tarmedLimitation : ret) {
				if (tarmedLimitation.getLimitationUnit() == LimitationUnit.COVERAGE) {
					tarmedLimitation.setSkip(true);
				}
			}
		}
	}
	
	public String getSparteAsText(){
		return StringUtils.defaultString(TarmedLeistung.getTextForSparte(getSparte()));
	}
	
	/**
	 * Get the {@link MandantType} of the {@link IMandator}. If not found the default value is
	 * {@link MandantType#SPECIALIST}.
	 * 
	 * @param mandant
	 * @return
	 * @since 3.4
	 */
	public static MandantType getMandantType(IMandator mandant){
		Object typeObj =
			mandant.getExtInfo(ch.elexis.core.jpa.entities.TarmedLeistung.MANDANT_TYPE_EXTINFO_KEY);
		if (typeObj instanceof String) {
			return MandantType.valueOf((String) typeObj);
		}
		return MandantType.SPECIALIST;
	}
	
	/** Text zu einem Code der qualitativen Dignität holen */
	public static String getTextForDigniQuali(final String kuerzel){
		return TarmedDefinitionen.getTitle("DIGNI_QUALI", kuerzel);
	}
	
	/** Kurz-Code für eine qualitative Dignität holen */
	public static String getCodeForDigniQuali(final String titel){
		return TarmedDefinitionen.getKuerzel("DIGNI_QUALI", titel);
	}
	
	/** Text für einen Code für quantitative Dignität holen */
	public static String getTextForDigniQuanti(final String kuerzel){
		return TarmedDefinitionen.getTitle("DIGNI_QUALI", kuerzel);
	}
	
	/** Text für einen Sparten-Code holen */
	public static String getTextForSparte(final String kuerzel){
		return TarmedDefinitionen.getTitle("SPARTE", kuerzel);
	}
	
	/** Text für eine Anästhesie-Risikoklasse holen */
	public static String getTextForRisikoKlasse(final String kuerzel){
		return TarmedDefinitionen.getTitle("ANAESTHESIE", kuerzel);
	}
	
	/** Text für einen ZR_EINHEIT-Code holen (Sitzung, Monat usw.) */
	public static String getTextForZR_Einheit(final String kuerzel){
		return TarmedDefinitionen.getTitle("ZR_EINHEIT", kuerzel);
	}
	
	private static List<String> availableLawsCache;
	
	public static List<String> getAvailableLaws(){
		return ArzttarifeModelServiceHolder.get()
			.getNamedQueryByName(String.class, ITarmedLeistung.class, "TarmedLeistungDistinctLaws")
			.executeWithParameters(Collections.emptyMap());
	}
	
	public static boolean isAvailableLaw(String law){
		if (availableLawsCache == null) {
			availableLawsCache = getAvailableLaws();
		}
		for (String available : availableLawsCache) {
			if (available.equalsIgnoreCase(law)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Query for a {@link TarmedLeistung} using the code. The returned {@link TarmedLeistung} will
	 * be valid on date, and will be from the cataloge specified by law.
	 * 
	 * @param code
	 * @param date
	 * @param law
	 * @return null if no matching {@link TarmedLeistung} found
	 * @since 3.4
	 */
	public static TarmedLeistung getFromCode(final String code, LocalDate date, String law){
		if (availableLawsCache == null) {
			availableLawsCache = getAvailableLaws();
		}
		
		IQuery<ITarmedLeistung> query =
			ArzttarifeModelServiceHolder.get().getQuery(ITarmedLeistung.class);
		query.and("code_", COMPARATOR.EQUALS, code);
		
		if (law != null) {
			if (!isAvailableLaw(law)) {
				query.startGroup();
				query.and("law", COMPARATOR.EQUALS, "");
				query.or("law", COMPARATOR.EQUALS, null);
			} else {
				query.and("law", COMPARATOR.EQUALS, law, true);
			}
		}
		List<ITarmedLeistung> leistungen = query.execute();
		TimeTool _date = new TimeTool(date);
		for (ITarmedLeistung tarmedLeistung : leistungen) {
			TimeTool validFrom = new TimeTool(tarmedLeistung.getValidFrom());
			TimeTool validTo = new TimeTool(tarmedLeistung.getValidTo());
			if (_date.isAfterOrEqual(validFrom) && _date.isBeforeOrEqual(validTo)) {
				return (TarmedLeistung) tarmedLeistung;
			}
			
		}
		return null;
	}
	
}
