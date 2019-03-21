/*******************************************************************************
 * Copyright (c) 2013-2016 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.model.importer;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import at.medevit.ch.artikelstamm.ARTIKELSTAMM;
import at.medevit.ch.artikelstamm.ARTIKELSTAMM.ITEMS.ITEM;
import at.medevit.ch.artikelstamm.ARTIKELSTAMM.LIMITATIONS.LIMITATION;
import at.medevit.ch.artikelstamm.ARTIKELSTAMM.PRODUCTS.PRODUCT;
import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;
import at.medevit.ch.artikelstamm.ArtikelstammHelper;
import at.medevit.ch.artikelstamm.BlackBoxReason;
import at.medevit.ch.artikelstamm.DATASOURCEType;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.SALECDType;
import at.medevit.ch.artikelstamm.model.service.ATCCodeCache;
import at.medevit.ch.artikelstamm.model.service.ModelServiceHolder;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.jpa.entities.ArtikelstammItem;
import ch.elexis.core.jpa.model.util.JpaModelUtil;
import ch.elexis.core.services.holder.ContextServiceHolder;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=artikelstamm_v5")
public class ArtikelstammImporter extends AbstractReferenceDataImporter
		implements IReferenceDataImporter {
	private static Logger log = LoggerFactory.getLogger(ArtikelstammImporter.class);
	
	private static Map<String, PRODUCT> products = new HashMap<String, PRODUCT>();
	private static Map<String, LIMITATION> limitations = new HashMap<String, LIMITATION>();
	private static boolean isOddb2xml = false;
	
	/**
	 * @param monitor
	 * @param input
	 * @param version
	 *            the version to set. If <code>null</code> the current version will be simply
	 *            increased by one
	 * @return
	 */
	public IStatus performImport(IProgressMonitor monitor, InputStream input,
		@Nullable Integer newVersion){
		return performImport(monitor, input, true, true, newVersion);
	}
	
	/**
	 * @since 3.8 Allow import of only Pharma or NonPharma products
	 * @param monitor
	 * @param input
	 * @param bPharm
	 *            import pharma products (aka medical drugs)
	 * @param nbNonPharma
	 *            import Non-Pharma products
	 * @param version
	 *            the version to set. If <code>null</code> the current version will be simply
	 *            increased by one
	 * @return status of import action
	 */
	public IStatus performImport(IProgressMonitor monitor, InputStream input, boolean bPharma,
		boolean bNonPharma, @Nullable Integer newVersion){
		
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		String bundleVersion =
			Platform.getBundle("at.medevit.ch.artikelstamm.model").getVersion().toString();
		
		subMonitor.setTaskName("Einlesen der Aktualisierungsdaten");
		ARTIKELSTAMM importStamm = null;
		try {
			importStamm = ArtikelstammHelper.unmarshallInputStream(input);
		} catch (JAXBException | SAXException je) {
			String msg = "Fehler beim Einlesen der Import-Datei";
			Status status = new Status(IStatus.ERROR, "at.medevit.ch.artikelstamm.model.importer",
				0x01, msg, je);
			log.error(msg, je);
			return status;
		}
		subMonitor.worked(10);
		
		if (newVersion == null) {
			int month = importStamm.getBUILDDATETIME().getMonth();
			int year = importStamm.getBUILDDATETIME().getYear();
			newVersion = Integer.valueOf("" + (year - 2000) + month);
			log.info("[PI] No newVersion provided. Setting to [{}].", newVersion);
		}
		
		try {
			DATASOURCEType datasourceType = VersionUtil.getDatasourceType();
			String message = "Trying to import dataset sourced ["
				+ importStamm.getDATASOURCE().value() + "] while existent database is sourced ["
				+ datasourceType.value() + "]. Please contact support. Exiting.";
			if (importStamm.getDATASOURCE() != datasourceType) {
				log.error(message);
				return new Status(Status.ERROR, "at.medevit.ch.artikelstamm.model.importer",
					message);
			}
		} catch (IllegalArgumentException iae) {
			VersionUtil.setDataSourceType(importStamm.getDATASOURCE());
		}
		
		int currentVersion = VersionUtil.getCurrentVersion();
		
		log.info("[PI] Aktualisiere{}{} {} vom {} von v{} auf v{}. Importer-Version {}",
			bPharma ? " Pharma" : "", bNonPharma ? " NonPharma" : "", importStamm.getDATASOURCE(),
			importStamm.getCREATIONDATETIME().toGregorianCalendar().getTime(), currentVersion,
			newVersion, bundleVersion);
		
		subMonitor.setTaskName("Lese Produkte und Limitationen...");
		subMonitor.subTask("Lese Produkt-Details");
		populateProducsAndLimitationsMap(importStamm);
		subMonitor.worked(5);
		
		subMonitor.setTaskName("Setze alle Elemente auf inaktiv...");
		subMonitor.subTask("Setze Elemente auf inaktiv");
		isOddb2xml = importStamm.getDATASOURCE().equals(DATASOURCEType.ODDB_2_XML);
		inactivateNonBlackboxedItems();
		subMonitor.worked(5);
		
		long startTime = System.currentTimeMillis();
		subMonitor
			.setTaskName("Importiere Artikelstamm " + importStamm.getCREATIONDATETIME().getMonth()
				+ "/" + importStamm.getCREATIONDATETIME().getYear());
		if (bPharma) {
			subMonitor.subTask("Importiere Pharma Products");
			updateOrAddProducts(newVersion, importStamm, subMonitor.split(20));
		}
		subMonitor.subTask("Importiere Artikel");
		updateOrAddItems(newVersion, importStamm, bPharma, bNonPharma, subMonitor.split(50));
		
		// update the version number for type importStammType
		subMonitor.setTaskName("Setze neue Versionsnummer");
		
		VersionUtil.setCurrentVersion(newVersion);
		VersionUtil.setImportSetCreationDate(
			importStamm.getCREATIONDATETIME().toGregorianCalendar().getTime());
		
		subMonitor.worked(5);
		long endTime = System.currentTimeMillis();
		if (ContextServiceHolder.get() != null) {
			ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD,
				IArtikelstammItem.class);
		}
		
		log.info(
			"[PI] Artikelstamm import took " + ((endTime - startTime) / 1000)
				+ "sec.Used {} {} version {}. . Importer-Version {}. Will rebuild ATCCodeCache",
			VersionUtil.getDatasourceType().toString(), VersionUtil.getImportSetCreationDate(),
			newVersion, bundleVersion);
		
		ATCCodeCache.rebuildCache(subMonitor.split(2));
		log.info("[PI] Artikelstamm finished rebuilding ATCCodeCache");
		
		return Status.OK_STATUS;
	}
	
	private static void inactivateNonBlackboxedItems(){
		log.debug("[BB] Setting all items inactive for isOddb2xml {}...", isOddb2xml);
		String cmd = "UPDATE ARTIKELSTAMM_CH SET BB='"
			+ Integer.toString(BlackBoxReason.INACTIVE.getNumercialReason()) + "' WHERE BB='"
			+ Integer.toString(BlackBoxReason.NOT_BLACKBOXED.getNumercialReason()) + "'";
		if (isOddb2xml) {
			cmd += " AND TYPE='P'";
		}
		log.debug("Executing {}", cmd);
		ModelServiceHolder.get().executeNativeUpdate(cmd);
		// refresh cache after native update ...
		ModelServiceHolder.get().getQuery(IArtikelstammItem.class, true, false).execute();
		log.debug("Done Executing {}", cmd);
	}
	
	private static void populateProducsAndLimitationsMap(ARTIKELSTAMM importStamm){
		products = importStamm.getPRODUCTS().getPRODUCT().stream()
			.collect(Collectors.toMap(p -> p.getPRODNO(), p -> p));
		limitations = importStamm.getLIMITATIONS().getLIMITATION().stream()
			.collect(Collectors.toMap(l -> l.getLIMNAMEBAG(), l -> l));
	}
	
	/**
	 * 
	 * @param version
	 * @param importStamm
	 * @param monitor
	 */
	private static void updateOrAddProducts(int newVersion, ARTIKELSTAMM importStamm,
		IProgressMonitor monitor){
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
		
		List<PRODUCT> importProductList = importStamm.getPRODUCTS().getPRODUCT();
		subMonitor.beginTask("Importiere " + importProductList.size() + " Produkte",
			importProductList.size());
		
		log.debug("[IP] Update or import {} products...", importProductList.size());
		List<Object> products = new ArrayList<>();
		for (PRODUCT product : importProductList) {
			String prodno = product.getPRODNO();
			
			ArtikelstammItem foundProduct = EntityUtil.load(prodno, ArtikelstammItem.class);
			if (foundProduct == null) {
				String trimmedDscr = trimDSCR(product.getDSCR(), product.getPRODNO());
				foundProduct = new ArtikelstammItem();
				foundProduct.setId(product.getPRODNO());
				foundProduct.setCummVersion(Integer.toString(newVersion));
				foundProduct.setType(TYPE.X.name());
				foundProduct.setDscr(trimmedDscr);
				foundProduct.setBb(StringConstants.ZERO);
				foundProduct.setAdddscr(StringConstants.EMPTY);
				
				log.trace("[IP] Adding product " + foundProduct.getId() + " ("
					+ foundProduct.getDscr() + ")");
			}
			log.trace(
				"[IP] Updating product " + foundProduct.getId() + " (" + product.getDSCR() + ")");
			setValuesOnArtikelstammProdukt(foundProduct, product, newVersion);
			products.add(foundProduct);
			subMonitor.worked(1);
		}
		EntityUtil.save(products);
		subMonitor.done();
	}
	
	private static String trimDSCR(String dscr, String itemId){
		if (dscr.length() > 100) {
			log.trace("[IP] Delimiting dscr [{}] for product/item [{}] to 100 characters.", itemId,
				dscr);
			dscr = dscr.substring(0, 100);
		}
		return dscr;
	}
	
	private static void setValuesOnArtikelstammProdukt(ArtikelstammItem ai, PRODUCT product,
		final int cummulatedVersion){
		ai.setBb(Integer.toString(BlackBoxReason.NOT_BLACKBOXED.getNumercialReason()));
		ai.setCummVersion(cummulatedVersion + "");
		ai.setAtc(product.getATC());
		ai.setDscr(trimDSCR(product.getDSCR(), product.getPRODNO()));
	}
	
	private static void updateOrAddItems(int newVersion, ARTIKELSTAMM importStamm, boolean bPharma,
		boolean bNonPharma, IProgressMonitor monitor){
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
		
		List<ITEM> importItemList = importStamm.getITEMS().getITEM();
		subMonitor.beginTask("Importiere " + importItemList.size() + " items",
			importItemList.size());
		
		log.debug("[II] Update or import {} items...", importItemList.size());
		List<Object> foundItems = new ArrayList<>();
		for (ITEM item : importItemList) {
			ArtikelstammItem foundItem = null;
			List<ArtikelstammItem> result = EntityUtil.loadByNamedQuery(Collections.singletonMap("gtin", item.getGTIN()), ArtikelstammItem.class);
			if (result.size() > 0) {
				if (result.size() == 1) {
					foundItem = result.get(0);
				} else {
					log.warn("[II] Found multiple items ({}) for GTIN [{}] type {}", result.size(),
						item.getGTIN(), item.getPHARMATYPE());
					// Is the case in Stauffacher DB, where legacy articles have been imported
					for (ArtikelstammItem artikelstammItem : result) {
						BlackBoxReason bbReason =
							BlackBoxReason.getByInteger(Integer.parseInt(artikelstammItem.getBb()));
						if (bbReason == BlackBoxReason.INACTIVE
							|| (isOddb2xml && bbReason == BlackBoxReason.NOT_BLACKBOXED)
								&& artikelstammItem.getType() != null
								&& artikelstammItem.getType().equals("N")) {
							foundItem = artikelstammItem;
							log.warn("[II] isOddb2xml {} Selected ID [{}] of {} items to update.",
								isOddb2xml, foundItem.getId(), result.size());
							break;
						}
					}
				}
			}
			
			if ((bPharma && item.getPHARMATYPE().contentEquals("P"))
				|| (bNonPharma && item.getPHARMATYPE().contentEquals("N"))) {
				boolean keepOverriddenPublicPrice = false;
				boolean keepOverriddenPkgSize = false;
				
				if (foundItem == null) {
					String trimmedDscr = trimDSCR(item.getDSCR(), item.getGTIN());
					TYPE pharmaType = TYPE.X;
					if (item.getPHARMATYPE() != null) {
						String ptString = Character.toString(item.getPHARMATYPE().charAt(0));
						pharmaType = TYPE.valueOf(ptString.toUpperCase());
					}
					BigInteger code = (item.getPHAR() != null) ? item.getPHAR() : BigInteger.ZERO;
					String pharmaCode = String.format("%07d", code);
					
					foundItem = new ArtikelstammItem();
					foundItem.setId(
						ArtikelstammHelper.createUUID(newVersion, item.getGTIN(), code));
					foundItem.setCummVersion(Integer.toString(newVersion));
					foundItem.setType(pharmaType.name());
					foundItem.setGtin(item.getGTIN());
					foundItem.setPhar(pharmaCode);
					foundItem.setDscr(trimmedDscr);
					foundItem.setBb(StringConstants.ZERO);
					foundItem.setAdddscr(StringConstants.EMPTY);
					log.trace(
						"[II] Adding article " + foundItem.getId() + " (" + item.getDSCR() + ")");
				} else {
					// check if article has overridden public price
					keepOverriddenPublicPrice = isUserDefinedPrice(foundItem);
					keepOverriddenPkgSize = isUserDefinedPkgSize(foundItem);
				}
				log.trace("[II] Updating article {} {}  {} {} ({})", item.getPHARMATYPE(),
					bPharma && item.getPHARMATYPE().contentEquals("P"),
					bNonPharma && item.getPHARMATYPE().contentEquals("N"), foundItem.getId());
				setValuesOnArtikelstammItem(foundItem, item, newVersion, keepOverriddenPublicPrice,
					keepOverriddenPkgSize);
			}
			subMonitor.worked(1);
			if (foundItem != null) {
				foundItems.add(foundItem);
			}
		}
		EntityUtil.save(foundItems);
		subMonitor.done();
	}
	
	private static Double getUserDefinedPriceValue(ArtikelstammItem item){
		String ppub = item.getPpub();
		if (ppub != null && ppub.startsWith("-")) {
			try {
				return Double.valueOf(ppub);
			} catch (NumberFormatException nfe) {
				log.error("Error #getUserDefinedPrice [{}] value is [{}], setting 0", item.getId(),
					ppub);
			}
		}
		return null;
	}
	
	private static boolean isUserDefinedPrice(ArtikelstammItem item){
		return getUserDefinedPriceValue(item) != null;
	}
	
	private static boolean isUserDefinedPkgSize(ArtikelstammItem item){
		return item.getPkg_size() < 0;
	}
	
	/**
	 * 
	 * @param ai
	 *            The artikelstamm as seen by Elexis
	 * @param item
	 *            The new item to be imported
	 * @param cummulatedVersion
	 *            version of the artikelstamm to be imported
	 * @param keepOverriddenPublicPrice
	 *            Must keep the user overriden price
	 * @param keepOverriddenPkgSize
	 *            Must keep the user overriden PKG_SIZE, aka PackungsGroesse
	 */
	private static void setValuesOnArtikelstammItem(ArtikelstammItem ai, ITEM item,
		final int cummulatedVersion, boolean keepOverriddenPublicPrice,
		boolean keepOverriddenPkgSize){
		
		ai.setCummVersion(cummulatedVersion + "");
		ai.setPhar((item.getPHAR() != null) ? String.format("%07d", item.getPHAR()) : null);
		
		SALECDType salecd = item.getSALECD();
		// For ODDB2XML we must override the SALECD == N for NonPharma as
		// ZurRoses is eliminating way too many articles
		boolean oddb2xmlOverride = (isOddb2xml && item.getPHARMATYPE().contentEquals("N"));
		if (SALECDType.A == salecd || oddb2xmlOverride) {
			ai.setBb(Integer.toString(BlackBoxReason.NOT_BLACKBOXED.getNumercialReason()));
			log.debug("{} Clearing blackboxed as salecd {} is A isSL {} or oddb2xml override {}",
				item.getGTIN(), salecd, item.isSLENTRY(), oddb2xmlOverride);
		} else {
			log.debug("{} Setting blackboxed as 5 {} != {} SALECDTypt.A  isSL {}", item.getGTIN(),
				salecd, SALECDType.A, salecd, item.isSLENTRY());
			ai.setBb(Integer.toString(BlackBoxReason.INACTIVE.getNumercialReason()));
		}
		
		ai.setGtin(item.getGTIN());
		ai.setType(item.getPHARMATYPE().contentEquals("P") ? "P" : "N");
		ai.setDscr(trimDSCR(item.getDSCR(), item.getGTIN()));
		
		PRODUCT product = (item.getPRODNO() != null) ? products.get(item.getPRODNO()) : null;
		if (product == null) {
			product = new PRODUCT();
		}
		
		ai.setAtc(product.getATC());
		ai.setProdno(item.getPRODNO());
		
		// LIMITATION
		String limnamebag = product.getLIMNAMEBAG();
		LIMITATION limitation = null;
		Integer limitationPts = null;
		String limitationDscr = null;
		if (limnamebag != null) {
			limitation = limitations.get(limnamebag);
			if (limitation != null) {
				limitationPts = limitation.getLIMITATIONPTS();
				limitationDscr = limitation.getDSCR();
			}
		}
		ai.setLimitation(limitation != null ? true : false);
		ai.setLimitation_pts((limitationPts != null) ? limitationPts.toString() : null);
		ai.setLimitation_txt(limitationDscr);
		
		String compName = null;
		String compGln = null;
		if (item.getCOMP() != null) {
			compName = item.getCOMP().getNAME();
			compGln = item.getCOMP().getGLN();
		}
		ai.setComp_name(compName);
		ai.setComp_gln(compGln);
		
		ai.setPexf((item.getPEXF() != null) ? item.getPEXF().toString() : null);
		if (!keepOverriddenPublicPrice) {
			ai.setPpub((item.getPPUB() != null) ? item.getPPUB().toString() : null);
		} else {
			if (item.getPPUB() != null) {
				setExtInfo(ArtikelstammItem.EXTINFO_VAL_PPUB_OVERRIDE_STORE,
					item.getPPUB().toString(), ai);
				log.info("[II] [{}] Updating ppub override store to [{}]", ai.getId(),
					item.getPPUB());
			}
		}
		
		ai.setSl_entry((item.isSLENTRY() != null && item.isSLENTRY()) ? true : false);
		ai.setDeductible((item.getDEDUCTIBLE() != null) ? item.getDEDUCTIBLE().toString() : null);
		ai.setGeneric_type(item.getGENERICTYPE());
		ai.setIkscat(item.getIKSCAT());
		ai.setNarcotic_cas((item.isNARCOTIC() != null && item.isNARCOTIC()) ? StringConstants.ONE
				: StringConstants.ZERO);
		ai.setLppv((item.isLPPV() != null && item.isLPPV()) ? true : false);
		if (!keepOverriddenPkgSize) {
			String pkgSize = (item.getPKGSIZE() != null) ? item.getPKGSIZE().toString() : null;
			try {
				int value = Integer.parseInt(
					(pkgSize != null && pkgSize.length() > 6) ? pkgSize.substring(0, 6).toString()
							: pkgSize);
				ai.setPkg_size(value);
			} catch (NumberFormatException e) {
				log.warn("[II] Non numeric pkg size for [{}] being [{}].", ai.getId(), pkgSize);
			}
			if (pkgSize != null && pkgSize.length() > 6) {
				log.warn("[II] Delimited pkg size for [{}] being [{}] to 6 characters.", ai.getId(),
					item.getPKGSIZE().toString());
			}
		} else {
			if (item.getPKGSIZE() != null) {
				setExtInfo(ArtikelstammItem.EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE,
					item.getPKGSIZE().toString(), ai);
				log.info("[II] [{}] Updating PKG_SIZE override store to [{}] fld {}", ai.getId(),
					item.getPKGSIZE(), ai.getPkg_size());
			}
		}
	}
	
	@Override
	public int getCurrentVersion(){
		return VersionUtil.getCurrentVersion();
	}
	
	private static void setExtInfo(Object key, Object value, ArtikelstammItem item){
		Map<Object, Object> extInfo = new Hashtable<>();
		byte[] bytes = item.getExtInfo();
		if (bytes != null) {
			extInfo = JpaModelUtil.extInfoFromBytes(bytes);
		}
		if (value == null) {
			extInfo.remove(key);
		} else {
			extInfo.put(key, value);
		}
		item.setExtInfo(JpaModelUtil.extInfoToBytes(extInfo));
	}
}
