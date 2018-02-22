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

import java.text.ParseException;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.TarmedRechnung.XMLExporter.VatRateSum;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Artikel;
import ch.elexis.data.Eigenleistung;
import ch.elexis.data.Konsultation;
import ch.elexis.data.LaborLeistung;
import ch.elexis.data.PhysioLeistung;
import ch.elexis.data.RFE;
import ch.elexis.data.Rechnung;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
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
	private static final String TL = "TL"; //$NON-NLS-1$
	private static final String AL = "AL"; //$NON-NLS-1$
	
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
	
	public XMLExporterServices(Element services){
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
	
	public Element getElement(){
		return servicesElement;
	}
	
	public double getSumTarmedAL(){
		if (!initialized) {
			initFromElement();
		}
		return sumTarmedAL;
	}
	
	public double getSumTarmedTL(){
		if (!initialized) {
			initFromElement();
		}
		return sumTarmedTL;
	}
	
	public double getTpTarmedAL(){
		if (!initialized) {
			initFromElement();
		}
		return tpTarmedAL;
	}
	
	public double getTpTarmedTL(){
		if (!initialized) {
			initFromElement();
		}
		return tpTarmedTL;
	}
	
	public Money getTarmedMoney(){
		if (!initialized) {
			initFromElement();
		}
		return mTarmed;
	}
	
	public Money getKantMoney(){
		if (!initialized) {
			initFromElement();
		}
		return mKant;
	}
	
	public Money getUebrigeMoney(){
		if (!initialized) {
			initFromElement();
		}
		return mUebrige;
	}
	
	public Money getAnalysenMoney(){
		if (!initialized) {
			initFromElement();
		}
		return mAnalysen;
	}
	
	public Money getMigelMoney(){
		if (!initialized) {
			initFromElement();
		}
		return mMigel;
	}
	
	/**
	 * paramed and physio can be seen identical as in XML4.4 physio got replaced with paramed
	 * 
	 * @return
	 */
	public Money getPhysioMoney(){
		if (!initialized) {
			initFromElement();
		}
		return mPhysio;
	}
	
	public Money getMedikamentMoney(){
		if (!initialized) {
			initFromElement();
		}
		return mMedikament;
	}
	
	public Money getObligationsMoney(){
		if (!initialized) {
			initFromElement();
		}
		return mObligations;
	}
	
	public void negateAll(){
		@SuppressWarnings("unchecked")
		List<Element> sr = servicesElement.getChildren();
		for (Element el : sr) {
			try {
				XMLExporterUtil.negate(el, XMLExporter.ATTR_QUANTITY);//$NON-NLS-1$
				XMLExporterUtil.negate(el, XMLExporter.ATTR_AMOUNT_MT);//$NON-NLS-1$
				XMLExporterUtil.negate(el, XMLExporter.ATTR_AMOUNT_TT);//$NON-NLS-1$
				XMLExporterUtil.negate(el, XMLExporter.ATTR_AMOUNT);//$NON-NLS-1$
			} catch (Exception ex) {
				ExHandler.handle(ex);
			}
		}
	}
	
	private void initFromElement(){
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
	
	public static XMLExporterServices buildServices(Rechnung rechnung, VatRateSum vatSummer){
		XMLExporterServices ret =
			new XMLExporterServices(new Element(ELEMENT_SERVICES, XMLExporter.nsinvoice));
			
		List<Konsultation> konsultationen = rechnung.getKonsultationen();
		
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
		String lastKonsDatum = null;
		int session = 1;
		for (Konsultation konsultation : konsultationen) {
			List<Verrechnet> leistungen = konsultation.getLeistungen();
			// konsultationen list is ordered by date, so we can just compare with previous
			String konsDatum = konsultation.getDatum();
			if (konsDatum.equals(lastKonsDatum)) {
				session++;
			} else {
				lastKonsDatum = konsDatum;
				session = 1;
			}
			
			TimeTool tt = new TimeTool(konsDatum);
			String dateForTarmed = XMLExporterUtil.makeTarmedDatum(konsultation.getDatum());
			
			boolean bRFE = false; // RFE already encoded
			
			for (Verrechnet verrechnet : leistungen) {
				Element el;
				double zahl = verrechnet.getZahl();
				IVerrechenbar v = verrechnet.getVerrechenbar();
				
				if (v == null) {
					logger.error(Messages.XMLExporter_ErroneusBill + rechnung.getNr()
						+ " Null-Verrechenbar bei Kons " //$NON-NLS-1$
						+ konsultation.getLabel());
					continue;
				}
				if (v instanceof TarmedLeistung) {
					TarmedLeistung tl = (TarmedLeistung) v;
					double primaryScale = verrechnet.getPrimaryScaleFactor();
					double secondaryScale = verrechnet.getSecondaryScaleFactor();
					
					double tlTL, tlAL, mult;
					mult = tl.getVKMultiplikator(tt, rechnung.getFall());
					
					tlAL = TarmedLeistung.getAL(verrechnet);
					tlTL = TarmedLeistung.getTL(verrechnet);
					// build monetary values of this TarmedLeistung
					Money mAL = new Money(
						(int) Math.round(tlAL * mult * zahl * primaryScale * secondaryScale));
					Money mTL = new Money(
						(int) Math.round(tlTL * mult * zahl * primaryScale * secondaryScale));
					Money mAmountLocal = new Money((int) Math
						.round((tlAL + tlTL) * mult * zahl * primaryScale * secondaryScale));
						
					// sum tax points and monetary value
					ret.tpTarmedTL += tlTL * zahl;
					ret.tpTarmedAL += tlAL * zahl;
					
					ret.sumTarmedAL += tlAL * mult * zahl * primaryScale * secondaryScale;
					ret.sumTarmedTL += tlTL * mult * zahl * primaryScale * secondaryScale;
					
					ret.mTarmed.addCent(mAmountLocal.getCents());
					
					el = new Element(ELEMENT_RECORD_TARMED, XMLExporter.nsinvoice); // 22000
					el.setAttribute(ATTR_TREATMENT, "ambulatory"); // 22050 //$NON-NLS-1$
					el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, "001"); // 22060 //$NON-NLS-1$
					Hashtable<String, String> ext = tl.loadExtension();
					String bezug = ext.get("Bezug"); // 22360 //$NON-NLS-1$
					if (StringTool.isNothing(bezug)) {
						bezug = verrechnet.getDetail("Bezug");
					}
					if (!StringTool.isNothing(bezug)) {
						el.setAttribute("ref_code", bezug); //$NON-NLS-1$
					}
					el.setAttribute(ATTR_EAN_PROVIDER,
						TarmedRequirements.getEAN(konsultation.getMandant()));
					el.setAttribute(ATTR_EAN_RESPONSIBLE,
						XMLExporterUtil.getResponsibleEAN(konsultation));
					el.setAttribute(ATTR_BILLING_ROLE, "both"); // 22410 //$NON-NLS-1$
					el.setAttribute(ATTR_MEDICAL_ROLE, "self_employed"); // 22430 //$NON-NLS-1$
					
					el.setAttribute(ATTR_BODY_LOCATION, TarmedLeistung.getSide(verrechnet)); // 22450
					
					el.setAttribute(ATTR_UNIT_MT, XMLTool.doubleToXmlDouble(tlAL / 100.0, 2)); // 22470
					getALNotScaled(verrechnet).ifPresent(d -> {
						el.setAttribute(ATTR_UNIT_MT, XMLTool.doubleToXmlDouble(d / 100.0, 2)); // 22470
					});
					
					el.setAttribute(ATTR_UNIT_FACTOR_MT, XMLTool.doubleToXmlDouble(mult, 2)); // 22480
					// (strebt
					// gegen
					// 0)
					el.setAttribute(ATTR_SCALE_FACTOR_MT,
						XMLTool.doubleToXmlDouble(primaryScale, 1)); // 22490
					el.setAttribute(ATTR_EXTERNAL_FACTOR_MT,
						XMLTool.doubleToXmlDouble(secondaryScale, 1)); // 22500
					getALScalingFactor(verrechnet).ifPresent(f -> {
						el.setAttribute(ATTR_EXTERNAL_FACTOR_MT, XMLTool.doubleToXmlDouble(f, 1)); // 22500
					});
					el.setAttribute(XMLExporter.ATTR_AMOUNT_MT, XMLTool.moneyToXmlDouble(mAL)); // 22510
					
					el.setAttribute(ATTR_UNIT_TT, XMLTool.doubleToXmlDouble(tlTL / 100.0, 2)); // 22520
					el.setAttribute(ATTR_UNIT_FACTOR_TT, XMLTool.doubleToXmlDouble(mult, 2)); // 22530
					el.setAttribute(ATTR_SCALE_FACTOR_TT,
						XMLTool.doubleToXmlDouble(primaryScale, 1)); // 22540
					el.setAttribute(ATTR_EXTERNAL_FACTOR_TT,
						XMLTool.doubleToXmlDouble(secondaryScale, 1)); // 22550
					el.setAttribute(XMLExporter.ATTR_AMOUNT_TT, XMLTool.moneyToXmlDouble(mTL)); // 22560
					el.setAttribute(XMLExporter.ATTR_AMOUNT,
						XMLTool.moneyToXmlDouble(mAmountLocal)); // 22570
					XMLExporterUtil.setVatAttribute(verrechnet, mAmountLocal, el, vatSummer); // 22590 //$NON-NLS-1$
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 22620
					
					if (TarmedLeistung.isObligation(verrechnet)) {
						el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
						ret.mObligations.addMoney(mAmountLocal);
					} else {
						el.setAttribute(ATTR_OBLIGATION, TARMED_FALSE); // 28630
					}
					
					if (!bRFE) {
						List<RFE> rfes = RFE.getRfeForKons(konsultation.getId());
						if (rfes.size() > 0) {
							StringBuilder sb = new StringBuilder();
							for (RFE rfe : rfes) {
								sb.append("551_").append(rfe.getCode()).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
							}
							el.setAttribute(XMLExporter.ATTR_REMARK, sb.toString());
						}
						bRFE = true;
					}
					
				} else if (v instanceof Labor2009Tarif) {
					el = new Element(ELEMENT_RECORD_LAB, XMLExporter.nsinvoice); // 28000
					el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, v.getCodeSystemCode());
					el.setAttribute(ATTR_EAN_PROVIDER,
						TarmedRequirements.getEAN(konsultation.getMandant()));
					el.setAttribute(ATTR_EAN_RESPONSIBLE,
						XMLExporterUtil.getResponsibleEAN(konsultation));
					Labor2009Tarif ll = (Labor2009Tarif) v;
					double mult = ll.getFactor(tt, rechnung.getFall());
					Money preis = verrechnet.getNettoPreis();
					double korr = preis.getCents() / mult;
					el.setAttribute(ATTR_UNIT, XMLTool.doubleToXmlDouble(korr / 100.0, 2)); // 28470
					el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2)); // 28480
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(XMLExporter.ATTR_AMOUNT,
						XMLTool.moneyToXmlDouble(mAmountLocal)); // 28570
					XMLExporterUtil.setVatAttribute(verrechnet, mAmountLocal, el, vatSummer); // 28590
					el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
					ret.mObligations.addMoney(mAmountLocal);
					
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 28620
					ret.mAnalysen.addMoney(mAmountLocal);
				} else if (v instanceof LaborLeistung) {
					el = new Element(ELEMENT_RECORD_LAB, XMLExporter.nsinvoice); // 28000
					el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, "316"); // 28060 //$NON-NLS-1$
					LaborLeistung ll = (LaborLeistung) v;
					double mult = ll.getFactor(tt, rechnung.getFall());
					// Money preis = vv.getEffPreis(); // b.getEffPreis(v);
					Money preis = verrechnet.getNettoPreis();
					double korr = preis.getCents() / mult;
					el.setAttribute(ATTR_UNIT, XMLTool.doubleToXmlDouble(korr / 100.0, 2)); // 28470
					el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2)); // 28480
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(XMLExporter.ATTR_AMOUNT,
						XMLTool.moneyToXmlDouble(mAmountLocal)); // 28570
					XMLExporterUtil.setVatAttribute(verrechnet, mAmountLocal, el, vatSummer); // 28590 //$NON-NLS-1$
					el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
					ret.mObligations.addMoney(mAmountLocal);
					
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 28620
					ret.mAnalysen.addMoney(mAmountLocal);
				} else if ("Medikamente".equals(v.getCodeSystemName()) //$NON-NLS-1$
					|| "Medicals".equals(v.getCodeSystemName()) //$NON-NLS-1$
					|| "400".equals(v.getCodeSystemCode()) //$NON-NLS-1$
					|| "402".equals(v.getCodeSystemCode())) {
					el = new Element(ELEMENT_RECORD_DRUG, XMLExporter.nsinvoice);
					Artikel art = (Artikel) v;
					double mult = art.getFactor(tt, rechnung.getFall());
					Money preis = verrechnet.getNettoPreis();
					Money mAmountLocal = new Money(preis);
					// new as of 3/2011: Correct handling of package fractions
					Money einzelpreis = verrechnet.getBruttoPreis();
					einzelpreis.multiply(verrechnet.getPrimaryScaleFactor());
					
					double cnt = verrechnet.getSecondaryScaleFactor();
					if (cnt != 1.0) {
						zahl *= cnt;
					} else {
						mAmountLocal.multiply(zahl);
					}
					
					// end corrections
					el.setAttribute(ATTR_UNIT, XMLTool.moneyToXmlDouble(einzelpreis));
					el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2));
					if ("true".equals(verrechnet.getDetail(Verrechnet.INDICATED))) {
						el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, "207");
					} else {
						el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, v.getCodeSystemCode());
					}
					if ("402".equals(v.getCodeSystemCode())) { // GTIN-basiert //$NON-NLS-1$
						String gtin = ((Artikel) v).getEAN();
						el.setAttribute(XMLExporter.ATTR_CODE, gtin);
					} else if ("400".equals(v.getCodeSystemCode())) { // Pharmacode-basiert //$NON-NLS-1$
						String pk = ((Artikel) v).getPharmaCode();
						el.setAttribute(XMLExporter.ATTR_CODE,
							StringTool.pad(StringTool.LEFT, '0', pk, 7));
					} else {
						logger.warn("Unknown medical code " + v.getCodeSystemCode()
							+ " encountered for " + v.getCodeSystemName() + "@" + v);
					}
					el.setAttribute(XMLExporter.ATTR_AMOUNT,
						XMLTool.moneyToXmlDouble(mAmountLocal));
					XMLExporterUtil.setVatAttribute(verrechnet, mAmountLocal, el, vatSummer);
					String ckzl = art.getExt("Kassentyp"); // cf. MedikamentImporter#KASSENTYP
					if (ckzl.equals("1")) {
						el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE);
						ret.mObligations.addMoney(mAmountLocal);
					} else {
						el.setAttribute(ATTR_OBLIGATION, TARMED_FALSE);
					}
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE);
					el.setAttribute(ATTR_EAN_PROVIDER,
						TarmedRequirements.getEAN(konsultation.getMandant()));
					el.setAttribute(ATTR_EAN_RESPONSIBLE,
						XMLExporterUtil.getResponsibleEAN(konsultation));
					ret.mMedikament.addMoney(mAmountLocal);
				} else if ("MiGeL".equals(v.getCodeSystemName())) {
					el = new Element(ELEMENT_RECORD_MIGEL, XMLExporter.nsinvoice);
					// Money preis = vv.getEffPreis(); // b.getEffPreis(v);
					Money preis = verrechnet.getNettoPreis();
					el.setAttribute(ATTR_UNIT, XMLTool.moneyToXmlDouble(preis));
					el.setAttribute(ATTR_UNIT_FACTOR, "1.0"); //$NON-NLS-1$
					el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, "452"); // MiGeL ab 2001-basiert //$NON-NLS-1$
					el.setAttribute(XMLExporter.ATTR_CODE, v.getCode());
					el.setAttribute(ATTR_EAN_PROVIDER,
						TarmedRequirements.getEAN(konsultation.getMandant()));
					el.setAttribute(ATTR_EAN_RESPONSIBLE,
						XMLExporterUtil.getResponsibleEAN(konsultation));
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(XMLExporter.ATTR_AMOUNT,
						XMLTool.moneyToXmlDouble(mAmountLocal));
					XMLExporterUtil.setVatAttribute(verrechnet, mAmountLocal, el, vatSummer);
					el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE);
					ret.mObligations.addMoney(mAmountLocal);
					
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE);
					ret.mMigel.addMoney(mAmountLocal);
				} else if (v instanceof PhysioLeistung) {
					el = new Element(ELEMENT_RECORD_PARAMED, XMLExporter.nsinvoice);
					el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, v.getCodeSystemCode()); // 28060
					PhysioLeistung pl = (PhysioLeistung) v;
					double mult = pl.getFactor(tt, rechnung.getFall());
					Money preis = verrechnet.getNettoPreis();
					double korr = preis.getCents() / mult;
					el.setAttribute(ATTR_UNIT, XMLTool.doubleToXmlDouble(korr / 100.0, 2)); // 28470
					el.setAttribute(ATTR_UNIT_FACTOR, XMLTool.doubleToXmlDouble(mult, 2)); // 28480
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(XMLExporter.ATTR_AMOUNT,
						XMLTool.moneyToXmlDouble(mAmountLocal)); // 28570
					XMLExporterUtil.setVatAttribute(verrechnet, mAmountLocal, el, vatSummer); // 28590
					el.setAttribute(ATTR_OBLIGATION, TARMED_TRUE); // 28630
					ret.mObligations.addMoney(mAmountLocal);
					
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE); // 28620
					// get EAN provider
					String ean = TarmedRequirements.getEAN(konsultation.getMandant());
					if (ean.equals(TarmedRequirements.EAN_PSEUDO))
						ean = "unknown";
					el.setAttribute(ATTR_EAN_PROVIDER, ean);
					// get EAN resposible
					ean = XMLExporterUtil.getResponsibleEAN(konsultation);
					if (ean.equals(TarmedRequirements.EAN_PSEUDO))
						ean = "unknown";
					el.setAttribute(ATTR_EAN_RESPONSIBLE, ean);
					
					ret.mPhysio.addMoney(mAmountLocal);
				} else {
					Money preis = verrechnet.getNettoPreis();
					el = new Element(ELEMENT_RECORD_OTHER, XMLExporter.nsinvoice);
					String codeSystemCode = v.getCodeSystemCode();
					el.setAttribute(XMLExporter.ATTR_TARIFF_TYPE, codeSystemCode);
					// all 406 will have code 2000
					if ("406".equals(codeSystemCode)) {
						el.setAttribute(XMLExporter.ATTR_CODE, "2000");
						el.setAttribute("name",
							verrechnet.getText() + " [" + verrechnet.getCode() + "]"); // 22340
					}
					el.setAttribute(ATTR_UNIT, XMLTool.moneyToXmlDouble(preis));
					el.setAttribute(ATTR_UNIT_FACTOR, "1.0"); //$NON-NLS-1$
					Money mAmountLocal = new Money(preis);
					mAmountLocal.multiply(zahl);
					el.setAttribute(XMLExporter.ATTR_AMOUNT,
						XMLTool.moneyToXmlDouble(mAmountLocal));
					XMLExporterUtil.setVatAttribute(verrechnet, mAmountLocal, el, vatSummer);
					el.setAttribute(ATTR_VALIDATE, TARMED_TRUE);
					el.setAttribute(ATTR_OBLIGATION, "false"); //$NON-NLS-1$
					el.setAttribute("external_factor", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
					
					el.setAttribute(ATTR_EAN_PROVIDER,
						TarmedRequirements.getEAN(konsultation.getMandant()));
					el.setAttribute(ATTR_EAN_RESPONSIBLE,
						XMLExporterUtil.getResponsibleEAN(konsultation));
						
					ret.mUebrige.addMoney(mAmountLocal);
				}
				el.setAttribute(ATTR_SESSION, Integer.toString(session));
				el.setAttribute(ATTR_RECORD_ID, Integer.toString(recordNumber++)); // 22010
				el.setAttribute(XMLExporter.ATTR_QUANTITY, Double.toString(zahl)); // 22350
				el.setAttribute(ATTR_DATE_BEGIN, dateForTarmed); // 22370
				if (el.getAttribute("name") == null) {
					el.setAttribute("name", verrechnet.getText()); // 22340
				}
				// 22330 set code if still empty
				if (el.getAttribute(XMLExporter.ATTR_CODE) == null) {
					XMLExporterUtil.setAttributeWithDefault(el, XMLExporter.ATTR_CODE,
						getServiceCode(verrechnet), StringConstants.ZERO); // 22330
				}
				ret.servicesElement.addContent(el);
			}
		}
		ret.initialized = true;
		return ret;
	}
	
	private static Optional<Double> getALScalingFactor(Verrechnet verrechnet){
		String scalingFactor = verrechnet.getDetail("AL_SCALINGFACTOR");
		if (scalingFactor != null && !scalingFactor.isEmpty()) {
			try {
				return Optional.of(Double.parseDouble(scalingFactor));
			} catch (NumberFormatException ne) {
				// return empty if not parseable
			}
		}
		return Optional.empty();
	}
	
	private static Optional<Double> getALNotScaled(Verrechnet verrechnet){
		String notScaled = verrechnet.getDetail("AL_NOTSCALED");
		if (notScaled != null && !notScaled.isEmpty()) {
			try {
				return Optional.of(Double.parseDouble(notScaled));
			} catch (NumberFormatException ne) {
				// return empty if not parseable
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Filter codes of {@link Verrechnet} where ID is used as code. This is relevant for
	 * {@link Eigenleistung} and Eigenartikel.
	 * 
	 * @param lst
	 * @return
	 */
	private static String getServiceCode(Verrechnet verrechnet){
		String ret = verrechnet.getCode();
		IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
		if (verrechenbar instanceof Eigenleistung || (verrechenbar instanceof Artikel
			&& ((Artikel) verrechenbar).get(Artikel.FLD_TYP).equals("Eigenartikel"))) {
			if (verrechenbar.getId().equals(ret)) {
				ret = "";
			}
		}
		return ret;
	}
}
