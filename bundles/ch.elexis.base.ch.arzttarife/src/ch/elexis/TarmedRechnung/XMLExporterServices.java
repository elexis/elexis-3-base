/*******************************************************************************
 * Copyright (c) 2006-2015, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    MEDEVIT <office@medevit.at> - #4078 remove dependency to c.e.base.ch.artikel
 *******************************************************************************/
package ch.elexis.TarmedRechnung;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.TarmedRechnung.XMLExporter.VatRateSum;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystem;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.ICustomService;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.types.ArticleSubTyp;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.data.Verrechnet;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.XMLTool;

public class XMLExporterServices {
	private static Logger logger = LoggerFactory.getLogger(XMLExporterServices.class);

	public static final String ELEMENT_SERVICES = "services"; //$NON-NLS-1$
	public static final String ELEMENT_DETAIL = "detail"; //$NON-NLS-1$

	private static final String ATTR_RECORD_ID = "record_id"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_OTHER = "record_other"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_PARAMED = "record_paramed"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_MIGEL = "record_migel"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_DRUG = "record_drug"; //$NON-NLS-1$
	private static final String ATTR_UNIT_FACTOR = "unit_factor"; //$NON-NLS-1$
	private static final String ATTR_UNIT = "unit"; //$NON-NLS-1$
	private static final String ELEMENT_RECORD_LAB = "record_lab"; //$NON-NLS-1$
	private static final String ATTR_OBLIGATION = "obligation"; //$NON-NLS-1$
	private static final String ATTR_VALIDATE = "validate"; //$NON-NLS-1$
	private static final String ATTR_EXTERNAL_FACTOR_TT = "external_factor_tt"; //$NON-NLS-1$
	private static final String ATTR_SCALE_FACTOR_TT = "scale_factor_tt"; //$NON-NLS-1$
	private static final String ATTR_UNIT_FACTOR_TT = "unit_factor_tt"; //$NON-NLS-1$
	private static final String ATTR_UNIT_TT = "unit_tt"; //$NON-NLS-1$
	private static final String ATTR_EXTERNAL_FACTOR_MT = "external_factor_mt"; //$NON-NLS-1$
	private static final String ATTR_SCALE_FACTOR_MT = "scale_factor_mt"; //$NON-NLS-1$
	private static final String ATTR_UNIT_FACTOR_MT = "unit_factor_mt"; //$NON-NLS-1$
	private static final String ATTR_UNIT_MT = "unit_mt"; //$NON-NLS-1$
	private static final String ATTR_BODY_LOCATION = "body_location"; //$NON-NLS-1$
	private static final String ATTR_MEDICAL_ROLE = "medical_role"; //$NON-NLS-1$
	private static final String ATTR_BILLING_ROLE = "billing_role"; //$NON-NLS-1$
	private static final String ATTR_EAN_RESPONSIBLE = "responsible_id"; //$NON-NLS-1$
	private static final String ATTR_EAN_PROVIDER = "provider_id"; //$NON-NLS-1$
	private static final String ATTR_TREATMENT = "treatment"; //$NON-NLS-1$
	private static final String ATTR_DATE_BEGIN = "date_begin"; //$NON-NLS-1$
	private static final String ATTR_SESSION = "session"; //$NON-NLS-1$

	private static final String ELEMENT_RECORD_TARMED = "record_tarmed"; //$NON-NLS-1$

	private static final String TARMED_FALSE = "false"; //$NON-NLS-1$
	private static final String TARMED_TRUE = "true"; //$NON-NLS-1$

	private Element servicesElement;

	private double sumTarmedAL = 0.0;
	private double sumTarmedTL = 0.0;

	private double tpTarmedTL = 0;
	private double tpTarmedAL = 0;

	private Money mTarmed;
	private Money mKant;
	private Money mUebrige;
	private Money mAnalysen;
	private Money mMigel;
	private Money mPhysio;
	private Money mMedikament;

	private Money mObligations;

	boolean initialized = false;

	public XMLExporterServices(Element services) {
		this.servicesElement = services;

		mTarmed = new Money();
		mKant = new Money();
		mUebrige = new Money();
		mAnalysen = new Money();
		mMigel = new Money();
		mPhysio = new Money();
		mMedikament = new Money();

		mObligations = new Money();
	}

	public Element getElement() {
		return servicesElement;
	}

	public double getSumTarmedAL() {
		if (!initialized) {
			initFromElement();
		}
		return sumTarmedAL;
	}

	public double getSumTarmedTL() {
		if (!initialized) {
			initFromElement();
		}
		return sumTarmedTL;
	}

	public double getTpTarmedAL() {
		if (!initialized) {
			initFromElement();
		}
		return tpTarmedAL;
	}

	public double getTpTarmedTL() {
		if (!initialized) {
			initFromElement();
		}
		return tpTarmedTL;
	}

	public Money getTarmedMoney() {
		if (!initialized) {
			initFromElement();
		}
		return mTarmed;
	}

	public Money getKantMoney() {
		if (!initialized) {
			initFromElement();
		}
		return mKant;
	}

	public Money getUebrigeMoney() {
		if (!initialized) {
			initFromElement();
		}
		return mUebrige;
	}

	public Money getAnalysenMoney() {
		if (!initialized) {
			initFromElement();
		}
		return mAnalysen;
	}

	public Money getMigelMoney() {
		if (!initialized) {
			initFromElement();
		}
		return mMigel;
	}

	/**
	 * paramed and physio can be seen identical as in XML4.4 physio got replaced
	 * with paramed
	 *
	 * @return
	 */
	public Money getPhysioMoney() {
		if (!initialized) {
			initFromElement();
		}
		return mPhysio;
	}

	public Money getMedikamentMoney() {
		if (!initialized) {
			initFromElement();
		}
		return mMedikament;
	}

	public Money getObligationsMoney() {
		if (!initialized) {
			initFromElement();
		}
		return mObligations;
	}

	public void negateAll() {
		@SuppressWarnings("unchecked")
		List<Element> sr = servicesElement.getChildren();
		for (Element el : sr) {
			try {
				XMLExporterUtil.negate(el, XMLExporter.ATTR_QUANTITY);// $NON-NLS-1$
				XMLExporterUtil.negate(el, XMLExporter.ATTR_AMOUNT_MT);// $NON-NLS-1$
				XMLExporterUtil.negate(el, XMLExporter.ATTR_AMOUNT_TT);// $NON-NLS-1$
				XMLExporterUtil.negate(el, XMLExporter.ATTR_AMOUNT);// $NON-NLS-1$
			} catch (Exception ex) {
				ExHandler.handle(ex);
			}
		}
	}

	private void initFromElement() {
		// calculate sum per type
		List<?> records = servicesElement.getChildren();
		for (Object object : records) {
			if (object instanceof Element) {
				Element element = (Element) object;
				try {
					if (element.getName().equals(ELEMENT_RECORD_TARMED)) {
						mTarmed.addAmount(element.getAttributeValue(XMLExporter.ATTR_AMOUNT));
					} else if (element.getName().equals(ELEMENT_RECORD_LAB)) {
						mAnalysen.addAmount(element.getAttributeValue(XMLExporter.ATTR_AMOUNT));
					} else if (element.getName().equals(ELEMENT_RECORD_DRUG)) {
						mMedikament.addAmount(element.getAttributeValue(XMLExporter.ATTR_AMOUNT));
					} else if (element.getName().equals(ELEMENT_RECORD_MIGEL)) {
						mMigel.addAmount(element.getAttributeValue(XMLExporter.ATTR_AMOUNT));
					} else if (element.getName().equals(ELEMENT_RECORD_PARAMED)) {
						mPhysio.addAmount(element.getAttributeValue(XMLExporter.ATTR_AMOUNT));
					} else if (element.getName().equals(ELEMENT_RECORD_OTHER)) {
						mUebrige.addAmount(element.getAttributeValue(XMLExporter.ATTR_AMOUNT));
					}

					String obligation = element.getAttributeValue(ATTR_OBLIGATION);
					if (obligation != null && TARMED_TRUE.equals(obligation)) {
						mObligations.addAmount(obligation);
					}
				} catch (ParseException e) {
					logger.error("Error parsing services " + e);
				}
			}
		}
		initialized = true;
	}

	public static XMLExporterServices buildServices(IInvoice invoice, VatRateSum vatSummer) {
		XMLExporterServices ret = new XMLExporterServices(new Element(ELEMENT_SERVICES, XMLExporter.nsinvoice));

		if (invoice != null) {
			List<IEncounter> encounters = invoice.getEncounters();

			// To make the validator happy, the attribute date_begin must duplicate
			// exactly
			// the date of the first billing position end date_end must duplicate
			// exactly
			// the date of the last billed consultation. If we have non-billed
			// entries in
			// the patient's record we must forget these for the sake of
			// xml-confirmity
			// so we use ttFirst and ttLast to check he the dates (instead of the
			// begin end end
			// dates that are stored in the bill
			int recordNumber = 1;
			LocalDate lastEncounterDate = null;
			int session = 1;
			for (IEncounter encounter : encounters) {
				List<IBilled> encounterBilled = encounter.getBilled();
				// konsultationen list is ordered by date, so we can just compare with previous
				LocalDate encounterDate = encounter.getDate();
				if (encounterDate.equals(lastEncounterDate)) {
					session++;
				} else {
					lastEncounterDate = encounterDate;
					session = 1;
				}
				String dateForTarmed = XMLExporterUtil.makeTarmedDatum(encounterDate);
				ICoverage coverage = encounter.getCoverage();
				IBillingSystem billingSystem = coverage.getBillingSystem();
				BillingLaw law = billingSystem.getLaw();

				boolean bRFE = false; // RFE already encoded

				for (IBilled billed : encounterBilled) {
					Element el;
					double amount = billed.getAmount();
					IBillable billable = billed.getBillable();

					if (billable == null) {
						logger.error(
								Messages.XMLExporter_ErroneusBill + invoice.getNumber() + " Null-Verrechenbar bei Kons " //$NON-NLS-1$
										+ encounter.getLabel());
						continue;
					}
					if (billable instanceof ITarmedLeistung) {
						ITarmedLeistung tl = (ITarmedLeistung) billable;
						double primaryScale = billed.getPrimaryScaleFactor();
						double secondaryScale = 1.0;
						if (!billed.isNonIntegerAmount()) {
							secondaryScale = billed.getSecondaryScaleFactor();
						}

						double tlTL, tlAL, mult;
						mult = billed.getFactor();
						tlAL = ArzttarifeUtil.getAL(billed);
						tlTL = ArzttarifeUtil.getTL(billed);
						// build monetary values of this TarmedLeistung
						Money mAL = ArzttarifeUtil.getALMoney(billed);
						Money mTL = ArzttarifeUtil.getTLMoney(billed);
						Money mAmountLocal = billed.getTotal();

						// sum tax points and monetary value
						ret.tpTarmedTL += tlTL * amount;
						ret.tpTarmedAL += tlAL * amount;

						ret.sumTarmedAL += mAL.doubleValue();
						ret.sumTarmedTL += mTL.doubleValue();

						ret.mTarmed.addCent(mAmountLocal.getCents());

						el = new Element(ELEMENT_RECORD_TARMED, XMLExporter.nsinvoice); // 22000
						el.setAttribute(ATTR_TREATMENT, "ambulatory"); // 22050 //$NON-NLS-1$
						el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, "001"); // 22060 //$NON-NLS-1$
						String bezug = (String) tl.getExtension().getExtInfo("Bezug");// 22360 //$NON-NLS-1$
						if (StringTool.isNothing(bezug)) {
							bezug = (String) billed.getExtInfo("Bezug");
						}
						if (!StringTool.isNothing(bezug)) {
							el.setAttribute("ref_code", bezug); //$NON-NLS-1$
						}
						el.setAttribute(ATTR_EAN_PROVIDER, TarmedRequirements.getEAN(encounter.getMandator()));
						el.setAttribute(ATTR_EAN_RESPONSIBLE, XMLExporterUtil.getResponsibleEAN(encounter));
						el.setAttribute(ATTR_BILLING_ROLE, "both"); // 22410 //$NON-NLS-1$
						el.setAttribute(ATTR_MEDICAL_ROLE, "self_employed"); // 22430 //$NON-NLS-1$

						el.setAttribute(ATTR_BODY_LOCATION, ArzttarifeUtil.getSide(billed)); // 22450

						el.setAttribute(ATTR_UNIT_MT, XMLTool.doubleToXmlDouble(tlAL / 100.0, 2)); // 22470
						XMLExporterUtil.getALNotScaled(billed).ifPresent(d -> {
							el.setAttribute(ATTR_UNIT_MT, XMLTool.doubleToXmlDouble(d / 100.0, 2)); // 22470
						});

						el.setAttribute(ATTR_UNIT_FACTOR_MT, XMLTool.doubleToXmlDouble(mult, 2)); // 22480
						// (strebt
						// gegen
						// 0)
						el.setAttribute(ATTR_SCALE_FACTOR_MT, XMLTool.doubleToXmlDouble(primaryScale, 1)); // 22490
						XMLExporterUtil.getALScalingFactor(billed).ifPresent(f -> {
							f = f * primaryScale;
							el.setAttribute(ATTR_SCALE_FACTOR_MT, XMLTool.doubleToXmlDouble(f, 1)); // 22500
						});
						el.setAttribute(ATTR_EXTERNAL_FACTOR_MT, XMLTool.doubleToXmlDouble(secondaryScale, 1)); // 22500
						el.setAttribute(XMLExporter.ATTR_AMOUNT_MT, XMLTool.moneyToXmlDouble(mAL)); // 22510

						el.setAttribute(ATTR_UNIT_TT, XMLTool.doubleToXmlDouble(tlTL / 100.0, 2)); // 22520
						el.setAttribute(ATTR_UNIT_FACTOR_TT, XMLTool.doubleToXmlDouble(mult, 2)); // 22530
						el.setAttribute(ATTR_SCALE_FACTOR_TT, XMLTool.doubleToXmlDouble(primaryScale, 1)); // 22540
						el.setAttribute(ATTR_EXTERNAL_FACTOR_TT, XMLTool.doubleToXmlDouble(secondaryScale, 1)); // 22550
						el.setAttribute(XMLExporter.ATTR_AMOUNT_TT, XMLTool.moneyToXmlDouble(mTL)); // 22560
						el.setAttribute(XMLExporter.ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal)); // 22570
						XMLExporterUtil.setVatAttribute(billed, mAmountLocal, el, vatSummer); // 22590 //$NON-NLS-1$
						el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 22620

						if (ArzttarifeUtil.isObligation(billed)) {
							el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
							ret.mObligations.addMoney(mAmountLocal);
						} else {
							el.setAttribute(ATTR_OBLIGATION, TARMED_FALSE); // 28630
						}

						if (!bRFE) {
							List<IReasonForEncounter> rfes = XMLExporterUtil.getReasonsForEncounter(encounter);
							if (rfes.size() > 0) {
								StringBuilder sb = new StringBuilder();
								for (IReasonForEncounter rfe : rfes) {
									sb.append("551_").append(rfe.getCode()).append(StringUtils.SPACE); //$NON-NLS-1$
								}
								el.setAttribute(XMLExporter.ATTR_REMARK, sb.toString());
							}
							bRFE = true;
						}

					} else if (billable instanceof ILaborLeistung) {
						el = new Element(ELEMENT_RECORD_LAB, XMLExporter.nsinvoice); // 28000
						el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, billable.getCodeSystemCode());
						el.setAttribute(ATTR_EAN_PROVIDER, TarmedRequirements.getEAN(encounter.getMandator()));
						el.setAttribute(ATTR_EAN_RESPONSIBLE, XMLExporterUtil.getResponsibleEAN(encounter));
						double mult = billed.getFactor();
						Money preis = billed.getScaledPrice();
						double korr = preis.getCents() / mult;
						el.setAttribute(ATTR_UNIT, XMLTool.doubleToXmlDouble(korr / 100.0, 2)); // 28470
						el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2)); // 28480
						Money mAmountLocal = billed.getTotal();
						el.setAttribute(XMLExporter.ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal)); // 28570
						XMLExporterUtil.setVatAttribute(billed, mAmountLocal, el, vatSummer); // 28590
						el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
						ret.mObligations.addMoney(mAmountLocal);

						el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 28620
						ret.mAnalysen.addMoney(mAmountLocal);
					} else if ("Medikamente".equals(billable.getCodeSystemName()) //$NON-NLS-1$
							|| "Medicals".equals(billable.getCodeSystemName()) //$NON-NLS-1$
							|| "400".equals(billable.getCodeSystemCode()) //$NON-NLS-1$
							|| "402".equals(billable.getCodeSystemCode())) {
						el = new Element(ELEMENT_RECORD_DRUG, XMLExporter.nsinvoice);
						IArticle art = (IArticle) billable;
						double mult = billed.getFactor();
						el.setAttribute(ATTR_UNIT, XMLTool.moneyToXmlDouble(billed.getPrice()));
						el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2));
						if ("true".equals((String) billed.getExtInfo(Verrechnet.INDICATED))) {
							el.setAttribute("name", billed.getText() + " (medizinisch indiziert: 207)");
						}
						el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, billable.getCodeSystemCode());
						if ("402".equals(billable.getCodeSystemCode())) { // GTIN-basiert //$NON-NLS-1$
							String gtin = ((IArticle) billable).getGtin();
							el.setAttribute(XMLExporter.ATTR_CODE, gtin);
						} else if ("400".equals(billable.getCodeSystemCode())) { // Pharmacode-basiert //$NON-NLS-1$
							String pk = getPharmaCode(((IArticle) billable));
							el.setAttribute(XMLExporter.ATTR_CODE, StringTool.pad(StringTool.LEFT, '0', pk, 7));
						} else {
							logger.warn("Unknown medical code " + billable.getCodeSystemCode() + " encountered for "
									+ billable.getCodeSystemName() + "@" + billable);
						}
						el.setAttribute(XMLExporter.ATTR_AMOUNT, XMLTool.moneyToXmlDouble(billed.getTotal()));
						XMLExporterUtil.setVatAttribute(billed, billed.getTotal(), el, vatSummer);
						if (art.isObligation()) {
							el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE);
							ret.mObligations.addMoney(billed.getTotal());
						} else {
							el.setAttribute(ATTR_OBLIGATION, TARMED_FALSE);
						}
						el.setAttribute(ATTR_VALIDATE, TARMED_TRUE);
						el.setAttribute(ATTR_EAN_PROVIDER, TarmedRequirements.getEAN(encounter.getMandator()));
						el.setAttribute(ATTR_EAN_RESPONSIBLE, XMLExporterUtil.getResponsibleEAN(encounter));
						ret.mMedikament.addMoney(billed.getTotal());
					} else if ("MiGeL".equals(billable.getCodeSystemName())) {
						el = new Element(ELEMENT_RECORD_MIGEL, XMLExporter.nsinvoice);
						// Money preis = vv.getEffPreis(); // b.getEffPreis(v);
						Money preis = billed.getScaledPrice();
						el.setAttribute(ATTR_UNIT, XMLTool.moneyToXmlDouble(preis));
						el.setAttribute(ATTR_UNIT_FACTOR, "1.0"); //$NON-NLS-1$
						el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, "452"); // MiGeL ab 2001-basiert //$NON-NLS-1$
						el.setAttribute(XMLExporter.ATTR_CODE, billable.getCode());
						el.setAttribute(ATTR_EAN_PROVIDER, TarmedRequirements.getEAN(encounter.getMandator()));
						el.setAttribute(ATTR_EAN_RESPONSIBLE, XMLExporterUtil.getResponsibleEAN(encounter));
						Money mAmountLocal = new Money(preis);
						mAmountLocal.multiply(amount);
						el.setAttribute(XMLExporter.ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal));
						XMLExporterUtil.setVatAttribute(billed, mAmountLocal, el, vatSummer);
						el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE);
						ret.mObligations.addMoney(mAmountLocal);

						el.setAttribute(ATTR_VALIDATE, TARMED_TRUE);
						ret.mMigel.addMoney(mAmountLocal);
					} else if (billable instanceof IPhysioLeistung) {
						el = new Element(ELEMENT_RECORD_PARAMED, XMLExporter.nsinvoice);
						el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, billable.getCodeSystemCode()); // 28060
						if (law == BillingLaw.KVG) {
							el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, "312"); // 28060
						}
						double mult = billed.getFactor();
						Money preis = billed.getScaledPrice();
						double korr = preis.getCents() / mult;
						el.setAttribute(ATTR_UNIT, XMLTool.doubleToXmlDouble(korr / 100.0, 2)); // 28470
						el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2)); // 28480
						Money mAmountLocal = new Money(preis);
						mAmountLocal.multiply(amount);
						el.setAttribute(XMLExporter.ATTR_AMOUNT, XMLTool.moneyToXmlDouble(mAmountLocal)); // 28570
						XMLExporterUtil.setVatAttribute(billed, mAmountLocal, el, vatSummer); // 28590
						el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
						ret.mObligations.addMoney(mAmountLocal);

						el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 28620
						// get EAN provider
						String ean = TarmedRequirements.getEAN(encounter.getMandator());
						if (ean.equals(TarmedRequirements.EAN_PSEUDO))
							ean = "unknown";
						el.setAttribute(ATTR_EAN_PROVIDER, ean);
						// get EAN resposible
						ean = XMLExporterUtil.getResponsibleEAN(encounter);
						if (ean.equals(TarmedRequirements.EAN_PSEUDO))
							ean = "unknown";
						el.setAttribute(ATTR_EAN_RESPONSIBLE, ean);

						ret.mPhysio.addMoney(mAmountLocal);
					} else {
						el = new Element(ELEMENT_RECORD_OTHER, XMLExporter.nsinvoice);
						String codeSystemCode = billable.getCodeSystemCode();
						el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, codeSystemCode);
						// all 406 will have code 2000
						if ("406".equals(codeSystemCode) && !isCovid(billable)) {
							el.setAttribute(XMLExporter.ATTR_CODE, "2000");
							el.setAttribute("name", billed.getText() + " [" + getServiceCode(billed) + "]"); // 22340
						}
						if ("590".equals(codeSystemCode) && billable instanceof IArticle) {
							el.setAttribute(XMLExporter.ATTR_CODE, "1310");
						}
						el.setAttribute(ATTR_UNIT, XMLTool.moneyToXmlDouble(billed.getPrice()));
						el.setAttribute(ATTR_UNIT_FACTOR, "1.0"); //$NON-NLS-1$
						el.setAttribute(XMLExporter.ATTR_AMOUNT, XMLTool.moneyToXmlDouble(billed.getTotal()));
						XMLExporterUtil.setVatAttribute(billed, billed.getTotal(), el, vatSummer);
						el.setAttribute(ATTR_VALIDATE, TARMED_TRUE);
						// all pandemie and nutrition are obligations
						if ("351".equals(codeSystemCode) || "510".equals(codeSystemCode)) {
							el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE);
						} else {
							el.setAttribute(ATTR_OBLIGATION, "false"); //$NON-NLS-1$
						}
						el.setAttribute("external_factor", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$

						el.setAttribute(ATTR_EAN_PROVIDER, TarmedRequirements.getEAN(encounter.getMandator()));
						el.setAttribute(ATTR_EAN_RESPONSIBLE, XMLExporterUtil.getResponsibleEAN(encounter));

						ret.mUebrige.addMoney(billed.getTotal());
					}
					el.setAttribute(ATTR_SESSION, Integer.toString(session));
					el.setAttribute(ATTR_RECORD_ID, Integer.toString(recordNumber++)); // 22010
					el.setAttribute(XMLExporter.ATTR_QUANTITY, Double.toString(amount)); // 22350
					el.setAttribute(ATTR_DATE_BEGIN, dateForTarmed); // 22370
					if (el.getAttribute("name") == null) {
						el.setAttribute("name", billed.getText()); // 22340
					}
					// 22330 set code if still empty
					if (el.getAttribute(XMLExporter.ATTR_CODE) == null) {
						XMLExporterUtil.setAttributeWithDefault(el, XMLExporter.ATTR_CODE, getServiceCode(billed),
								StringConstants.ZERO); // 22330
					}
					ret.servicesElement.addContent(el);
				}
			}
		}
		ret.initialized = true;
		return ret;
	}

	private static String getPharmaCode(IArticle iArticle) {
		String ret = StringUtils.EMPTY;
		String systemName = iArticle.getCodeSystemName();
		if (systemName != null && systemName.equals("Artikelstamm")) {
			try {
				ret = BeanUtils.getProperty(iArticle, "PHAR");
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				logger.warn("Could not get pharma code from [" + iArticle + "]", e);
			}
		} else {
			try {
				ret = BeanUtils.getProperty(iArticle, "pharmaCode");
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				logger.warn("Could not get pharma code from [" + iArticle + "]", e);
			}
		}
		return ret;
	}

	/**
	 * Filter codes of {@link IBilled} where ID is used as code. This is relevant
	 * for {@link ICustomService} and {@link IArticle} of typ
	 * {@link ArticleTyp#EIGENARTIKEL}.
	 *
	 * @param lst
	 * @return
	 */
	private static String getServiceCode(IBilled verrechnet) {
		String ret = verrechnet.getCode();
		IBillable billable = verrechnet.getBillable();
		if (billable instanceof ICustomService
				|| (billable instanceof IArticle && ((IArticle) billable).getTyp() == ArticleTyp.EIGENARTIKEL)) {
			if (billable.getId().equals(ret)) {
				ret = StringUtils.EMPTY;
			}
		}
		return ret;
	}

	private static boolean isCovid(IBillable billable) {
		if (billable instanceof IArticle) {
			return ((IArticle) billable).getTyp() == ArticleTyp.EIGENARTIKEL
					&& ((IArticle) billable).getSubTyp() == ArticleSubTyp.COVID;
		}
		return false;
	}
}
