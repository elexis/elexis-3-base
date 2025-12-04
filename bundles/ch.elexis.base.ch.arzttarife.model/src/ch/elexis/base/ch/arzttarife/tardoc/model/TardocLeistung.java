package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.base.ch.arzttarife.util.TarmedDefinitionenUtil;
import ch.elexis.core.jpa.entities.TardocLeistung.MandantType;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.VatInfo;
import ch.rgw.tools.Money;

public class TardocLeistung extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TardocLeistung>
		implements Identifiable, ITardocLeistung {

	public static final String STS_CLASS = "ch.elexis.data.TardocLeistung";

	private IBillableOptifier<TardocLeistung> tardocOptifier;
	private IBillableVerifier verifier;

	private ITardocExtension extension;

	public static LocalDate curTimeHelper = LocalDate.now();

	public TardocLeistung(ch.elexis.core.jpa.entities.TardocLeistung entity) {
		super(entity);
		verifier = new TardocVerifier();
	}

	@Override
	public VatInfo getVatInfo() {
		return VatInfo.VAT_CH_ISTREATMENT;
	}

	@Override
	public int getMinutes() {
		double min = NumberUtils.toDouble((String) getExtension().getExtInfo("LSTGIMES_MIN"));
		min += NumberUtils.toDouble((String) getExtension().getExtInfo("VBNB_MIN"));
		min += NumberUtils.toDouble((String) getExtension().getExtInfo("BEFUND_MIN"));
		min += NumberUtils.toDouble((String) getExtension().getExtInfo("WECHSEL_MIN"));
		return (int) Math.round(min);
	}

	@Override
	public int getAL() {
		String tp_al = getExtension().getLimits().get(TardocConstants.TardocLeistung.EXT_FLD_TP_AL);
		return (int) Math.round(NumberUtils.toDouble(tp_al) * 100);
	}

	/**
	 * Get the AL value of the {@link TardocLeistung}. The {@link IMandator} is
	 * needed to determine special scaling factors. On billing of the
	 * {@link TardocLeistung} the values for AL and TL should be set to the ExtInfo
	 * of the {@link IBilled} for later use.
	 *
	 * @param mandant
	 * @return
	 * @since 3.4
	 */
	@Override
	public int getAL(IMandator mandant) {
		String tp_al = getExtension().getLimits().get(ch.elexis.core.jpa.entities.TardocLeistung.EXT_FLD_TP_AL);
		return (int) Math.round(NumberUtils.toDouble(tp_al) * getALScaling(mandant));
	}

	/**
	 * Get the AL scaling value to be used when billing this {@link TardocLeistung}
	 * for the provided {@link Mandant}.
	 *
	 * @param mandant
	 * @return
	 */
	public double getALScaling(IMandator mandant) {
		double scaling = 100;
		if (mandant != null) {
			MandantType type = getMandantType(mandant);
			if (type == MandantType.PRACTITIONER) {
				String f_al_r = getExtension().getLimits()
						.get(ch.elexis.core.jpa.entities.TardocLeistung.EXT_FLD_F_AL_R);
				double alScaling = NumberUtils.toDouble(f_al_r);
				if (alScaling > 0.1) {
					scaling *= alScaling;
				}
			}
		}
		return scaling;
	}

	@Override
	public int getIPL() {
		String tp_tl = getExtension().getLimits().get(ch.elexis.core.jpa.entities.TardocLeistung.EXT_FLD_TP_TL);
		return (int) Math.round(NumberUtils.toDouble(tp_tl) * 100);
	}

	@Override
	public String getDigniQuali() {
		return getEntity().getDigniQuali();
	}

	@Override
	public String getDigniQuanti() {
		return getEntity().getDigniQuanti();
	}

	@Override
	public String getExclusion() {
		curTimeHelper = LocalDate.now();
		return getExclusion(curTimeHelper);
	}

	@Override
	public synchronized IBillableOptifier<TardocLeistung> getOptifier() {
		if (tardocOptifier == null) {
			tardocOptifier = new TardocOptifier();
		}
		return tardocOptifier;
	}

	@Override
	public IBillableVerifier getVerifier() {
		return verifier;
	}

	@Override
	public String getCodeSystemName() {
		return ch.elexis.core.jpa.entities.TardocLeistung.CODESYSTEM_NAME;
	}

	@Override
	public String getCodeSystemCode() {
		return "007";
	}

	@Override
	public String getCode() {
		return getEntity().getCode_();
	}

	@Override
	public void setCode(String value) {
		getEntity().setCode_(value);
	}

	@Override
	public String getText() {
		return getEntity().getTx255();
	}

	@Override
	public void setText(String value) {
		getEntity().setTx255(value);
	}

	@Override
	public ITardocExtension getExtension() {
		if (extension == null) {
			INamedQuery<ITardocExtension> query = ArzttarifeModelServiceHolder.get()
					.getNamedQuery(ITardocExtension.class, "code");
			List<ITardocExtension> found = query.executeWithParameters(query.getParameterMap("code", getId()));
			if (!found.isEmpty()) {
				extension = found.get(0);
			}
		}
		return extension;
	}

	@Override
	public ITardocLeistung getParent() {
		String parent = getEntity().getParent();
		if (parent != null && !"NIL".equals(parent)) {
			return ArzttarifeModelServiceHolder.get().load(parent, ITardocLeistung.class).get();
		}
		return null;
	}

	@Override
	public LocalDate getValidFrom() {
		return getEntity().getGueltigVon();
	}

	@Override
	public LocalDate getValidTo() {
		return getEntity().getGueltigBis();
	}

	@Override
	public boolean requiresSide() {
		if (getExtension() != null) {
			String value = getExtension().getLimits().get(Constants.FLD_EXT_SIDE.toUpperCase());
			return "1".equals(value);
		}
		return false;
	}

	@Override
	public String getServiceTyp() {
		return StringUtils.defaultString(getExtension().getLimits().get("LEISTUNG_TYP"));
	}

	@Override
	public String getLaw() {
		return getEntity().getLaw();
	}

	@Override
	public String getSparte() {
		return getEntity().getSparte();
	}

	@Override
	public boolean isChapter() {
		return getEntity().isChapter();
	}

	public static String getSide(IBilled v) {
		IBillable vv = v.getBillable();
		if (vv instanceof TardocLeistung) {
			String side = (String) v.getExtInfo(Constants.FLD_EXT_SIDE);
			if (Constants.SIDE_L.equalsIgnoreCase(side)) {
				return Constants.LEFT;
			} else if (Constants.SIDE_R.equalsIgnoreCase(side)) {
				return Constants.RIGHT;
			}
		}
		return "none";
	}

	public List<TardocLimitation> getLimitations() {
		String lim = getExtension().getLimits().get("limits"); //$NON-NLS-1$
		if (lim != null && !lim.isEmpty()) {
			List<TardocLimitation> ret = new ArrayList<>();
			String[] lines = lim.split("#"); //$NON-NLS-1$
			for (String line : lines) {
				ret.add(TardocLimitation.of(line).setTardocLeistung(this));
			}
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
	public List<String> getServiceBlocks(LocalDate date) {
		List<String> ret = new ArrayList<>();
		List<String> blocks = getExtStringListField(TardocConstants.TardocLeistung.EXT_FLD_SERVICE_BLOCKS);
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
	public List<String> getServiceGroups(LocalDate date) {
		List<String> ret = new ArrayList<>();
		List<String> groups = getExtStringListField(TardocConstants.TardocLeistung.EXT_FLD_SERVICE_GROUPS);
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
	@Override
	public List<String> getHierarchy(LocalDate date) {
		List<String> ret = new ArrayList<>();
		List<String> hierarchy = getExtStringListField(
				ch.elexis.core.jpa.entities.TardocLeistung.EXT_FLD_HIERARCHY_SLAVES);
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
	 * Get the exclusions valid now as String, containing the service and chapter
	 * codes. Group exclusions are NOT part of the String.
	 *
	 * @param kons
	 *
	 * @return
	 */
	public List<TardocExclusion> getExclusions(IEncounter kons) {
		if (kons == null) {
			curTimeHelper = LocalDate.now();
		} else {
			curTimeHelper = kons.getDate();
		}
		return getExclusions(curTimeHelper);
	}

	/**
	 * Get {@link TarmedExclusion} objects with this {@link TardocLeistung} as
	 * master.
	 *
	 * @param date
	 * @return
	 */
	public List<TardocExclusion> getExclusions(LocalDate date) {
		return TardocKumulation.getExclusions(getCode(),
				isChapter() ? TardocKumulationArt.CHAPTER : TardocKumulationArt.SERVICE, date, getLaw());
	}

	/**
	 * Get the exclusions valid at the paramater date as String, containing the
	 * service and chapter codes. Group exclusions are NOT part of the String.
	 *
	 * @param date
	 * @return
	 */
	public String getExclusion(LocalDate date) {
		String exclusions = TardocKumulation.getExclusions(getCode(), date);
		if (exclusions == null) {
			Map<String, String> map = getExtension().getLimits();
			if (map == null) {
				return StringUtils.EMPTY;
			}
			return StringUtils.defaultString(map.get("exclusion"));
		}
		return StringUtils.defaultString(exclusions);
	}

	private boolean isDateWithinDatesString(LocalDate localDate, String datesString) {
		String[] parts = datesString.split("\\|");
		if (parts.length == 2) {
			LocalDate from = LocalDate.parse(parts[0]);
			LocalDate to = LocalDate.parse(parts[1]);
			return (from.isBefore(localDate) || from.isEqual(localDate))
					&& (to.isAfter(localDate) || to.isEqual(localDate));
		}
		return false;
	}

	private List<String> getExtStringListField(String extKey) {
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

	public String getSparteAsText() {
		return StringUtils.defaultString(TarmedDefinitionenUtil.getTextForSparte(getSparte()));
	}

	@Override
	public boolean isZuschlagsleistung() {
		String typ = getServiceTyp();
		boolean becauseOfType = typ != null && typ.equals("Z");
		if (becauseOfType) {
			String text = getText();
			return text.startsWith("+") || text.startsWith("-");
		}
		return false;
	}

	/**
	 * Get the {@link MandantType} of the {@link IMandator}. If not found the
	 * default value is {@link MandantType#SPECIALIST}.
	 *
	 * @param mandant
	 * @return
	 * @since 3.4
	 */
	public static MandantType getMandantType(IMandator mandant) {
		Object typeObj = mandant.getExtInfo(ch.elexis.core.jpa.entities.TardocLeistung.MANDANT_TYPE_EXTINFO_KEY);
		if (typeObj instanceof String) {
			return MandantType.valueOf((String) typeObj);
		}
		return MandantType.SPECIALIST;
	}

	/**
	 * Query for a {@link TardocLeistung} using the code. The returned
	 * {@link TardocLeistung} will be valid on date, and will be from the cataloge
	 * specified by law.
	 *
	 * @param code
	 * @param date
	 * @param law
	 * @return null if no matching {@link TardocLeistung} found
	 * @since 3.4
	 */
	public static TardocLeistung getFromCode(final String code, LocalDate date, String law) {
		IQuery<ITardocLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(ITardocLeistung.class);
		query.and("code_", COMPARATOR.EQUALS, code);

		if (law != null) {
			if (!ArzttarifeUtil.isAvailableLaw(law)) {
				query.startGroup();
				query.or("law", COMPARATOR.EQUALS, StringUtils.EMPTY);
				query.or("law", COMPARATOR.EQUALS, null);
				query.andJoinGroups();
			} else {
				query.and("law", COMPARATOR.EQUALS, law, true);
			}
		}
		List<ITardocLeistung> leistungen = query.execute();
		for (ITardocLeistung tarmedLeistung : leistungen) {
			if ((date.isAfter(tarmedLeistung.getValidFrom()) || date.equals(tarmedLeistung.getValidFrom()))
					&& (date.isBefore(tarmedLeistung.getValidTo()) || date.equals(tarmedLeistung.getValidTo()))) {
				return (TardocLeistung) tarmedLeistung;
			}
		}
		return null;
	}

	public static TardocLeistung getFromCode(final String code, String law) {
		return getFromCode(code, LocalDate.now(), law);
	}

	@Override
	public String getNickname() {
		return StringUtils.defaultString(getEntity().getNickname());
	}

	@Override
	public void setNickname(String value) {
		getEntity().setNickname(value);
	}

	@Override
	public List<ITardocKumulation> getKumulations(TardocKumulationArt type) {
		IQuery<ITardocKumulation> query = ArzttarifeModelServiceHolder.get().getQuery(ITardocKumulation.class);
		if (getLaw() != null && !getLaw().isEmpty()) {
			query.and("law", COMPARATOR.EQUALS, getLaw());
		}
		query.startGroup();
		query.and("masterCode", COMPARATOR.EQUALS, getCode());
		query.and("masterArt", COMPARATOR.EQUALS, type.getArt());
		query.startGroup();
		query.and("slaveCode", COMPARATOR.EQUALS, getCode());
		query.and("slaveArt", COMPARATOR.EQUALS, type.getArt());
		query.orJoinGroups();
		query.andJoinGroups();

		List<ITardocKumulation> kumulations = query.execute();
		if (kumulations == null || kumulations.isEmpty()) {
			return Collections.emptyList();
		}
		return kumulations.stream().filter(k -> k.isValidKumulation(getValidFrom())).collect(Collectors.toList());
	}

	@Override
	public String getLabel() {
		return getCode() + StringUtils.SPACE + getText()
				+ ((getLaw() != null && !getLaw().isEmpty()) ? " (" + getLaw() + ")" : StringUtils.EMPTY);
	}

	@Override
	public Money getPrice() {
		Money ret = getNetPrice();
		Optional<IBillingSystemFactor> systemFactor = BillingServiceHolder.get()
				.getBillingSystemFactor(getCodeSystemName(), LocalDate.now());
		if (systemFactor.isPresent()) {
			return ret.multiply(systemFactor.get().getFactor());
		}
		return ret;
	}

	@Override
	public void setPrice(Money value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getNetPrice() {
		return new Money(getAL() + getIPL());
	}

	@Override
	public void setNetPrice(Money value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMinutes(int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}

	@Override
	public IXid getXid(String domain) {
		return XidServiceHolder.get().getXid(this, domain);
	}
}
