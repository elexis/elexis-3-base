package at.medevit.ch.artikelstamm.model;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.model.common.preference.MargePreference;
import at.medevit.ch.artikelstamm.model.service.ArtikelstammModelServiceHolder;
import at.medevit.ch.artikelstamm.model.service.CoreModelServiceHolder;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.billable.AbstractNoObligationOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.ArticleSubTyp;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.types.VatInfo;
import ch.rgw.tools.Money;

public class ArtikelstammItem extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.ArtikelstammItem>
		implements Identifiable, IArtikelstammItem {

	private static final String EXTINFO_VAL_VAT_OVERRIDEN = "VAT_OVERRIDE"; //$NON-NLS-1$
	private static final String EXTINFO_VAL_PPUB_OVERRIDE_STORE = "PPUB_OVERRIDE_STORE"; //$NON-NLS-1$
	private static final String EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE = "PKG_SIZE_OVERRIDE_STORE"; //$NON-NLS-1$

	private static IBillableOptifier<ArtikelstammItem> optifier;
	private IBillableVerifier verifier;

	public ArtikelstammItem(ch.elexis.core.jpa.entities.ArtikelstammItem entity) {
		super(entity);
		verifier = new DefaultVerifier();
	}

	@Override
	public synchronized IBillableOptifier<ArtikelstammItem> getOptifier() {
		if (optifier == null) {
			optifier = new AbstractNoObligationOptifier<ArtikelstammItem>(CoreModelServiceHolder.get(),
					ConfigServiceHolder.get(), ContextServiceHolder.get()) {

				@Override
				protected void setPrice(ArtikelstammItem billable, IBilled billed) {
					billed.setFactor(1.0);
					billed.setNetPrice(billable.getPurchasePrice());
					Money sellingPrice = billable.getSellingPrice();
					if (sellingPrice.isZero()) {
						sellingPrice = MargePreference.calculateVKP(getPurchasePrice());
					}
					int vkPreis = sellingPrice.getCents();
					double pkgSize = Math.abs(billable.getPackageSize());
					double vkUnits = billable.getSellingSize();
					if ((pkgSize > 0.0) && (vkUnits > 0.0) && (pkgSize != vkUnits)) {
						billed.setPoints((int) Math.round(vkUnits * (vkPreis / pkgSize)));
					} else {
						billed.setPoints((int) Math.round(vkPreis));
					}
				}

				@Override
				protected boolean isNoObligation(ArtikelstammItem billable) {
					return !billable.isInSLList();
				}

				@Override
				public Optional<IBillingSystemFactor> getFactor(IEncounter encounter) {
					return Optional.empty();
				}
			};
		}
		return optifier;
	}

	@Override
	public IBillableVerifier getVerifier() {
		return verifier;
	}

	@Override
	public String getCodeSystemName() {
		return ArtikelstammConstants.CODESYSTEM_NAME;
	}

	@Override
	public String getCode() {
		String gtin = getGtin();
		if (gtin != null && gtin.length() > 3) {
			return gtin;
		}
		return getEntity().getPhar();
	}

	@Override
	public String getText() {
		return getEntity().getDscr();
	}

	@Override
	public void setText(String value) {
		getEntity().setDscr(value);
	}

	@Override
	public Object getExtInfo(Object key) {
		return extInfoHandler.getExtInfo(key);
	}

	@Override
	public void setExtInfo(Object key, Object value) {
		extInfoHandler.setExtInfo(key, value);
	}

	@Override
	public Map<Object, Object> getMap() {
		return extInfoHandler.getMap();
	}

	@Override
	public String getGtin() {
		return getEntity().getGTIN();
	}

	@Override
	public void setGtin(String value) {
		getEntity().setGtin(value);
	}

	@Override
	public @NonNull String getAtcCode() {
		return StringUtils.defaultString(getEntity().getAtc());
	}

	@Override
	public void setAtcCode(String value) {
		getEntity().setAtc(value);
	}

	@Override
	public int getSellingSize() {
		return getEntity().getVerkaufseinheit();
	}

	@Override
	public void setSellingSize(int value) {
		getEntityMarkDirty().setVerkaufseinheit(value);
	}

	@Override
	public int getPackageSize() {
		return Math.abs(getEntity().getPkg_size());
	}

	@Override
	public void setPackageSize(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("value must not be lower than 0"); //$NON-NLS-1$
		}
		getEntity().setPkg_size(value);
	}

	@Override
	public String getPackageUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPackageUnit(String value) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getProductId() {
		if (isProduct()) {
			return getId();
		} else {
			return getEntity().getProdno();
		}
	}

	@Override
	public IArticle getProduct() {
		String prodno = getEntity().getProdno();
		if (prodno != null) {
			return ArtikelstammModelServiceHolder.get().load(prodno, IArtikelstammItem.class).orElse(null);
		}
		return null;
	}

	@Override
	public void setProduct(IArticle value) {
		getEntity().setProdno((value != null) ? value.getId() : null);
	}

	@Override
	public Money getPurchasePrice() {
		String priceString = getEntity().getPexf();
		if (StringUtils.isNotBlank(priceString)) {
			try {
				return new Money(priceString);
			} catch (ParseException e) {
			}
		}
		return new Money();
	}

	@Override
	public void setPurchasePrice(Money value) {
		getEntity().setPexf((value != null) ? Double.toString(value.doubleValue()) : null);
	}

	@Override
	public Money getSellingPrice() {
		String priceString = getEntity().getPpub();
		if (StringUtils.isNotBlank(priceString)) {
			try {
				Money value = new Money(priceString);
				return (isUserDefinedPrice()) ? value.negate() : value;
			} catch (ParseException e) {
			}
		}
		return new Money();
	}

	@Override
	public void setSellingPrice(Money value) {
		if (value != null && isUserDefinedPrice() && !value.isNegative()) {
			value.negate();
		}
		getEntityMarkDirty().setPpub((value != null) ? Double.toString(value.doubleValue()) : null);
	}

	@Override
	public boolean isObligation() {
		return isInSLList();
	}

	@Override
	public void setObligation(boolean value) {
		// TODO Auto-generated method stub
	}

	@Override
	public ArticleTyp getTyp() {
		return ArticleTyp.ARTIKELSTAMM;
	}

	@Override
	public void setTyp(ArticleTyp value) {
	}

	@Override
	public ArticleSubTyp getSubTyp() {
		String type = getEntity().getType();
		if (type != null) {
			if (TYPE.P.name().equals(type)) {
				return ArticleSubTyp.PHARMA;
			} else if (TYPE.N.name().equals(type)) {
				return ArticleSubTyp.NONPHARMA;
			}
		}
		return ArticleSubTyp.UNKNOWN;
	}

	@Override
	public void setSubTyp(ArticleSubTyp value) {
		if (value != null) {
			if (ArticleSubTyp.PHARMA.equals(value)) {
				getEntity().setType(TYPE.P.name());
			} else if (ArticleSubTyp.NONPHARMA.equals(value)) {
				getEntity().setType(TYPE.N.name());
			}
		}
		getEntity().setType(null);
	}

	@Override
	public boolean isProduct() {
		return TYPE.X.name().equalsIgnoreCase(getEntity().getType());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IArticle> getPackages() {
		IQuery<IArtikelstammItem> query = ArtikelstammModelServiceHolder.get().getQuery(IArtikelstammItem.class);
		query.and("prodno", COMPARATOR.EQUALS, getId()); //$NON-NLS-1$
		query.and("type", COMPARATOR.NOT_EQUALS, "X"); //$NON-NLS-1$ //$NON-NLS-2$
		return (List<IArticle>) (List<?>) query.execute();
	}

	@Override
	public void setCode(String code) {
		getEntity().setPhar(code);
	}

	@Override
	public String getPHAR() {
		return getEntity().getPhar();
	}

	@Override
	public TYPE getType() {
		return TYPE.valueOf(getEntity().getType());
	}

	@Override
	public String getManufacturerLabel() {
		return getEntity().getComp_name();
	}

	@Override
	public boolean isInSLList() {
		return getEntity().isSl_entry() || getEntity().isK70_entry();
	}

	@Override
	public void setInK70(boolean value) {
		getEntityMarkDirty().setK70_entry(value);
	}

	@Override
	public boolean isInK70() {
		return getEntity().isK70_entry();
	}

	@Override
	public String getSwissmedicCategory() {
		return getEntity().getIkscat();
	}

	@Override
	public String getGenericType() {
		return getEntity().getGeneric_type();
	}

	@Override
	public Integer getDeductible() {
		try {
			return Integer.valueOf(getEntity().getDeductible());
		} catch (NumberFormatException nfe) {
		}
		return null;
	}

	@Override
	public boolean isNarcotic() {
		return StringConstants.ONE.equals(getEntity().getNarcotic_cas());
	}

	@Override
	public boolean isInLPPV() {
		return getEntity().isLppv();
	}

	@Override
	public boolean isLimited() {
		return getEntity().isLimitation();
	}

	@Override
	public String getLimitationPoints() {
		return getEntity().getLimitation_pts();
	}

	@Override
	public String getLimitationText() {
		return getEntity().getLimitation_txt();
	}

	@Override
	public boolean isCalculatedPrice() {
		String value = getEntity().getPpub();
		if (StringUtils.isNotBlank(value)) {
			return false;
		}
		String exfValue = getEntity().getPexf();
		if (exfValue != null && !exfValue.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void restoreOriginalSellingPrice() {
		if (isUserDefinedPrice()) {
			String overridenPrice = (String) getExtInfo(ArtikelstammConstants.EXTINFO_VAL_PPUB_OVERRIDE_STORE);
			if (overridenPrice != null) {
				setUserDefinedPrice(false);
			}
		}
	}

	@Override
	public boolean isUserDefinedPrice() {
		String ppub = getEntity().getPpub();
		if (StringUtils.isNotBlank(ppub)) {
			return ppub.startsWith("-"); //$NON-NLS-1$
		}
		return false;
	}

	@Override
	public void setUserDefinedPrice(boolean activate) {
		if (activate) {
			String ppub = StringUtils.defaultString(getEntity().getPpub(), "0"); //$NON-NLS-1$
			setExtInfo(ArtikelstammConstants.EXTINFO_VAL_PPUB_OVERRIDE_STORE, ppub);
			double value = 0;
			try {
				value = Double.valueOf(ppub);
			} catch (NumberFormatException nfe) {
				LoggerFactory.getLogger(getClass()).error("Error #setUserDefinedPrice [{}] value is [{}], setting 0", //$NON-NLS-1$
						getId(), ppub);
			}
			setUserDefinedPriceValue(new Money(value));
		} else {
			String ppubStored = (String) getExtInfo(ArtikelstammConstants.EXTINFO_VAL_PPUB_OVERRIDE_STORE);
			getEntityMarkDirty().setPpub(ppubStored);
			setExtInfo(ArtikelstammConstants.EXTINFO_VAL_PPUB_OVERRIDE_STORE, null);
		}
	}

	@Override
	public String getName() {
		return getEntity().getDscr();
	}

	@Override
	public void setName(String value) {
		getEntity().setDscr(value);
	}

	@Override
	public void setUserDefinedPriceValue(Money value) {
		String originalPpub = (String) getExtInfo(ArtikelstammConstants.EXTINFO_VAL_PPUB_OVERRIDE_STORE);
		if (originalPpub == null) {
			setExtInfo(ArtikelstammConstants.EXTINFO_VAL_PPUB_OVERRIDE_STORE,
					(getSellingPrice() != null) ? Double.toString(getSellingPrice().doubleValue()) : StringUtils.EMPTY);
		}
		// setSellingPrice((value != null) ? value.negate() : null);
		getEntity().setPpub((value != null) ? "-" + Double.toString(value.doubleValue()) : null); //$NON-NLS-1$
	}

	@Override
	public String getAdditionalDescription() {
		return getEntity().getAdddscr();
	}

	@Override
	public void setAdditionalDescription(String value) {
		getEntity().setAdddscr(value);
	}

	@Override
	public boolean isBlackBoxed() {
		return !("0".equals(getEntity().getBb())); //$NON-NLS-1$
	}

	@Override
	public VatInfo getVatInfo() {
		if (isOverrideVatInfo()) {
			return VatInfo.valueOf((String) extInfoHandler.getExtInfo(EXTINFO_VAL_VAT_OVERRIDEN));
		}
		return getOriginalVatInfo();
	}

	private VatInfo getOriginalVatInfo() {
		switch (getType()) {
		case P:
			return VatInfo.VAT_CH_ISMEDICAMENT;
		case N:
			return VatInfo.VAT_CH_NOTMEDICAMENT;
		case X:
		}
		return VatInfo.VAT_NONE;
	}

	@Override
	public void overrideVatInfo(VatInfo vatInfo) {
		VatInfo originalVatInfo = getOriginalVatInfo();
		if (vatInfo == originalVatInfo) {
			extInfoHandler.setExtInfo(EXTINFO_VAL_VAT_OVERRIDEN, null);
		} else {
			extInfoHandler.setExtInfo(EXTINFO_VAL_VAT_OVERRIDEN, vatInfo.toString());
		}
	}

	@Override
	public boolean isOverrideVatInfo() {
		Object value = extInfoHandler.getExtInfo(EXTINFO_VAL_VAT_OVERRIDEN);
		return value instanceof String;
	}

	@Override
	public String getLabel() {
		return (getAdditionalDescription() != null && getAdditionalDescription().length() > 0)
				? getName() + " (" + getAdditionalDescription() + ")" //$NON-NLS-1$ //$NON-NLS-2$
				: getName();
	}

	@Override
	public boolean isUserDefinedPkgSize() {
		return getEntity().getPkg_size() < 0;
	}

	@Override
	public void restoreOriginalPackageSize() {
		if (isUserDefinedPkgSize()) {
			String ppubStored = (String) getExtInfo(EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE);
			if (ppubStored != null && !ppubStored.isEmpty()) {
				setPackageSize(Integer.parseInt(ppubStored));
				setExtInfo(EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE, null);
			}
		}
	}

	@Override
	public int getUserDefinedPkgSize() {
		int oldValue = getEntity().getPkg_size();
		if (oldValue < 0) {
			return oldValue * -1;
		}
		return ch.elexis.core.jpa.entities.ArtikelstammItem.IS_USER_DEFINED_PKG_SIZE;
	}

	/**
	 * Set the price as user-defined (i.e. overridden) price. This will internally
	 * store the price as negative value.
	 *
	 * @param value
	 */
	@Override
	public void setUserDefinedPkgSizeValue(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("value must not be lower than 0"); //$NON-NLS-1$
		}
		int pkgSize = getPackageSize();
		setExtInfo(EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE, Integer.toString(pkgSize));

		getEntity().setPkg_size(value * -1);
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}

	@Override
	public IXid getXid(String domain) {
		return XidServiceHolder.get().getXid(this, domain);
	}

	@Override
	public boolean isVaccination() {
		String atcCode = getAtcCode();
		if (atcCode != null) {
			if (atcCode.toUpperCase().startsWith("J07") && !atcCode.toUpperCase().startsWith("J07AX")) { //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
		}
		return false;
	}

	@Override
	public String getCodeSystemCode() {
		String gtin = getGtin();
		if (gtin != null && gtin.length() > 3) {
			if (getType() == TYPE.P || isInSLList()) {
				return "402"; //$NON-NLS-1$
			} else if (getType() == TYPE.N) {
				return "406"; //$NON-NLS-1$
			}
		}
		return "999"; //$NON-NLS-1$
	}

	@Override
	public String getPackageSizeString() {
		return StringUtils.EMPTY;
	}

	@Override
	public void setPackageSizeString(String value) {
		throw new UnsupportedOperationException();
	}
}
