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
package at.medevit.ch.artikelstamm.elexis.common.importer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.statushandlers.StatusManager;
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
import at.medevit.ch.artikelstamm.SALECDType;
import at.medevit.ch.artikelstamm.elexis.common.PluginConstants;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.atccache.ATCCodeCache;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.core.data.util.LocalLock;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class ArtikelstammImporter {
	private static Logger log = LoggerFactory.getLogger(ArtikelstammImporter.class);
	
	private static Map<String, PRODUCT> products = new HashMap<String, PRODUCT>();
	private static Map<String, LIMITATION> limitations = new HashMap<String, LIMITATION>();
	private static volatile boolean userCanceled = false;
	
	/**
	 * 
	 * @param monitor
	 * @param input
	 * @param version
	 *            the version to set. If <code>null</code> the current version will be simply
	 *            increased by one
	 * @return
	 */
	public static IStatus performImport(IProgressMonitor monitor, InputStream input,
		@Nullable Integer newVersion){
		LocalLock lock = new LocalLock("ArtikelstammImporter");
		
		if (!lock.tryLock()) {
			UiDesk.syncExec(new Runnable() {
				
				@Override
				public void run(){
					if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "",
						"Der Importer ist durch einen anderen Benutzer gestartet.\nDie Artikelstammeinträge werden bereits importiert.\n\n"
							+ "Startzeit: "
							+ new TimeTool(lock.getLockCurrentMillis()).toString(TimeTool.LARGE_GER)
							+ "\nGestartet durch: " + lock.getLockMessage()
							+ "\n\nWollen Sie den Importer trotzdem nochmal starten ?")) {
						lock.unlock();
						lock.tryLock();
						userCanceled = false;
					} else {
						userCanceled = true;
					}
				}
			});
		}
		if (userCanceled) {
			userCanceled = false;
			return Status.OK_STATUS;
		}
		try {
			SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
			String bundleVersion =  Platform.getBundle("at.medevit.ch.artikelstamm.elexis.common").getVersion().toString();
			
			subMonitor.setTaskName("Einlesen der Aktualisierungsdaten");
			ARTIKELSTAMM importStamm = null;
			try {
				importStamm = ArtikelstammHelper.unmarshallInputStream(input);
			} catch (JAXBException | SAXException je) {
				String msg = "Fehler beim Einlesen der Import-Datei";
				Status status = new ElexisStatus(IStatus.ERROR, PluginConstants.PLUGIN_ID,
					ElexisStatus.CODE_NOFEEDBACK, msg, je);
				StatusManager.getManager().handle(status, StatusManager.SHOW);
				log.error(msg, je);
				lock.unlock();
				return Status.CANCEL_STATUS;
			}
			subMonitor.worked(10);
			
			if (newVersion == null) {
				int month = importStamm.getBUILDDATETIME().getMonth();
				int year = importStamm.getBUILDDATETIME().getYear();
				newVersion = Integer.valueOf("" + (year - 2000) + month);
				log.info("[PI] No newVersion provided. Setting to [{}].", newVersion);
			}
			
			try {
				DATASOURCEType datasourceType = ArtikelstammItem.getDatasourceType();
				String message = "Trying to import dataset sourced ["
					+ importStamm.getDATASOURCE().value() + "] while existent database is sourced ["
					+ datasourceType.value() + "]. Please contact support. Exiting.";
				if (importStamm.getDATASOURCE() != datasourceType) {
					log.error(message);
					lock.unlock();
					return new Status(Status.ERROR, PluginConstants.PLUGIN_ID, message);
				}
			} catch (IllegalArgumentException iae) {
				ArtikelstammItem.setDataSourceType(importStamm.getDATASOURCE());
			}
			
			int currentVersion = ArtikelstammItem.getCurrentVersion();
			
			log.info("[PI] Aktualisiere {} vom {} von v{} auf v{}. Importer-Version {}",
				importStamm.getDATASOURCE(),
				importStamm.getCREATIONDATETIME().toGregorianCalendar().getTime(), currentVersion,
				newVersion,
				bundleVersion);
			
			subMonitor.setTaskName("Lese Produkte und Limitationen...");
			populateProducsAndLimitationsMap(importStamm);
			subMonitor.worked(5);
			
			subMonitor.setTaskName("Setze alle Elemente auf inaktiv...");
			inactivateNonBlackboxedItems();
			subMonitor.worked(5);
			
			long startTime = System.currentTimeMillis();
			subMonitor.setTaskName(
				"Importiere Artikelstamm " + importStamm.getCREATIONDATETIME().getMonth() + "/"
					+ importStamm.getCREATIONDATETIME().getYear());
			
			updateOrAddItems(newVersion, importStamm, subMonitor.split(50));
			updateOrAddProducts(newVersion, importStamm, subMonitor.split(20));
			
			// update the version number for type importStammType
			subMonitor.setTaskName("Setze neue Versionsnummer");
			
			ArtikelstammItem.setCurrentVersion(newVersion);
			ArtikelstammItem.setImportSetCreationDate(
				importStamm.getCREATIONDATETIME().toGregorianCalendar().getTime());
			
			subMonitor.worked(5);
			long endTime = System.currentTimeMillis();
			ElexisEventDispatcher.reload(ArtikelstammItem.class);
			
			log.info(
				"[PI] Artikelstamm import took " + ((endTime - startTime) / 1000)
					+ "sec.Used {} {} version {}. . Importer-Version {}. Will rebuild ATCCodeCache",
				ArtikelstammItem.getDatasourceType().toString(),
				ArtikelstammItem.getImportSetCreationDate(), newVersion, bundleVersion);
			
			ATCCodeCache.rebuildCache(subMonitor.split(5));
			log.info("[PI] Artikelstamm finished rebuilding ATCCodeCache");
		} finally {
			lock.unlock();
		}
		
		return Status.OK_STATUS;
	}
	
	private static void inactivateNonBlackboxedItems(){
		log.debug("[BB] Setting all items inactive...");
		Stm stm = PersistentObject.getConnection().getStatement();
		String cmd = "UPDATE " + ArtikelstammItem.TABLENAME + " SET "
			+ ArtikelstammItem.FLD_BLACKBOXED + Query.EQUALS
			+ JdbcLink.wrap(Integer.toString(BlackBoxReason.INACTIVE.getNumercialReason()))
			+ " WHERE " + ArtikelstammItem.FLD_BLACKBOXED + Query.EQUALS
			+ JdbcLink.wrap(Integer.toString(BlackBoxReason.NOT_BLACKBOXED.getNumercialReason()));
		log.debug("Executing {}", cmd);
		stm.exec(cmd);
		PersistentObject.getConnection().releaseStatement(stm);
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
		for (PRODUCT product : importProductList) {
			String prodno = product.getPRODNO();
			
			ArtikelstammItem foundProduct = ArtikelstammItem.load(prodno);
			if (!foundProduct.exists()) {
				
				String trimmedDscr = trimDSCR(product.getDSCR(), product.getPRODNO());
				foundProduct = new ArtikelstammItem(newVersion, TYPE.X, product.getPRODNO(), null,
					trimmedDscr, StringConstants.EMPTY);
				log.trace("[IP] Adding product " + foundProduct.getId() + " ("
					+ foundProduct.getDSCR() + ")");
			}
			log.trace(
				"[IP] Updating product " + foundProduct.getId() + " (" + product.getDSCR() + ")");
			setValuesOnArtikelstammProdukt(foundProduct, product, newVersion);
			
			subMonitor.worked(1);
		}
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
		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();
		
		fields.add(ArtikelstammItem.FLD_BLACKBOXED);
		values.add(Integer.toString(BlackBoxReason.NOT_BLACKBOXED.getNumercialReason()));
		
		fields.add(ArtikelstammItem.FLD_CUMMULATED_VERSION);
		values.add(cummulatedVersion + "");
		
		fields.add(ArtikelstammItem.FLD_ATC);
		values.add(product.getATC());
		
		fields.add(ArtikelstammItem.FLD_DSCR);
		values.add(trimDSCR(product.getDSCR(), product.getPRODNO()));
		
		ai.set(fields.toArray(new String[0]), values.toArray(new String[0]));
	}
	
	private static void updateOrAddItems(int newVersion, ARTIKELSTAMM importStamm,
		IProgressMonitor monitor){
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
		
		List<ITEM> importItemList = importStamm.getITEMS().getITEM();
		subMonitor.beginTask("Importiere " + importItemList.size() + " items",
			importItemList.size());
		
		log.debug("[II] Update or import {} items...", importItemList.size());
		for (ITEM item : importItemList) {
			String pharmaCode = String.format("%07d", item.getPHAR());
			Query<ArtikelstammItem> qre = new Query<ArtikelstammItem>(ArtikelstammItem.class);
			qre.add(ArtikelstammItem.FLD_GTIN, Query.LIKE, item.getGTIN());
			ArtikelstammItem foundItem = null;
			List<ArtikelstammItem> result = qre.execute();
			if (result.size() == 0) {
				foundItem =  ArtikelstammItem.loadByPHARNo(pharmaCode);
				log.debug("[II] Found using loadByPHARNo {} item {}", pharmaCode,foundItem == null ? "null"  : foundItem.getId());
			} else if (result.size() == 1) {
				foundItem = result.get(0);
			} else if (result.size() > 1) {
				log.warn("[II] Found multiple items for GTIN [" + item.getGTIN() + "]");
				// Is the case in Stauffacher DB, where legacy articles have been imported
				for (ArtikelstammItem artikelstammItem : result) {
					if (artikelstammItem.getBlackBoxReason() == BlackBoxReason.INACTIVE) {
						foundItem = artikelstammItem;
						log.warn("[II] Selected ID [" + foundItem.getId() + "] to update.");
					}
				}
			}
			
			boolean keepOverriddenPublicPrice = false;
			boolean keepOverriddenPkgSize = false;
			
			if (foundItem == null) {
				String trimmedDscr = trimDSCR(item.getDSCR(), item.getGTIN());
				TYPE pharmaType = TYPE.X;
				if (item.getPHARMATYPE() != null)
				{
					String ptString = Character.toString(item.getPHARMATYPE().charAt(0));
					pharmaType = TYPE.valueOf(ptString.toUpperCase());
				}
				foundItem = new ArtikelstammItem(newVersion, pharmaType, item.getGTIN(),
					item.getPHAR(), trimmedDscr, StringConstants.EMPTY);
				log.trace("[II] Adding article " + foundItem.getId() + " (" + item.getDSCR() + ")");
			} else {
				// check if article has overridden public price
				keepOverriddenPublicPrice = foundItem.isUserDefinedPrice();
				keepOverriddenPkgSize = foundItem.isUserDefinedPkgSize();
			}
			log.trace("[II] Updating article " + foundItem.getId() + " (" + item.getDSCR() + ")");
			
			setValuesOnArtikelstammItem(foundItem, item, newVersion, keepOverriddenPublicPrice, keepOverriddenPkgSize);
			subMonitor.worked(1);
		}

		subMonitor.done();
	}
	
	/**
	 * 
	 * @param ai	The artikelstamm as seen by Elexis
	 * @param item  The new item to be imported
	 * @param cummulatedVersion version of the artikelstamm to be imported
	 * @param keepOverriddenPublicPrice Must keep the user overriden price
	 * @param keepOverriddenPkgSize Must keep the user overriden PKG_SIZE, aka PackungsGroesse
	 */
	private static void setValuesOnArtikelstammItem(ArtikelstammItem ai, ITEM item,
		final int cummulatedVersion, boolean keepOverriddenPublicPrice, boolean keepOverriddenPkgSize){
		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();
		
		fields.add(ArtikelstammItem.FLD_CUMMULATED_VERSION);
		values.add(cummulatedVersion + "");
		
		fields.add(ArtikelstammItem.FLD_PHAR);
		values.add((item.getPHAR() != null) ? String.format("%07d", item.getPHAR()) : null);
		
		fields.add(ArtikelstammItem.FLD_BLACKBOXED);
		SALECDType salecd = item.getSALECD();
		if (SALECDType.A == salecd) {
			values.add(Integer.toString(BlackBoxReason.NOT_BLACKBOXED.getNumercialReason()));
			log.debug("{} Clearing blackboxed as salecd {} is A isSL {}", item.getGTIN(), salecd,
				item.isSLENTRY());
		} else {
			log.debug("{} Setting blackboxed as salecd {} != {} SALECDTypt.A  isSL {}",
				item.getGTIN(), salecd, SALECDType.A, salecd, item.isSLENTRY());
			values.add(Integer.toString(BlackBoxReason.INACTIVE.getNumercialReason()));
		}
		
		fields.add(ArtikelstammItem.FLD_GTIN);
		values.add(item.getGTIN());
		
		fields.add(ArtikelstammItem.FLD_DSCR);
		values.add(trimDSCR(item.getDSCR(), item.getGTIN()));
		
		PRODUCT product = (item.getPRODNO() != null) ? products.get(item.getPRODNO()) : null;
		if (product == null) {
			product = new PRODUCT();
		}
		
		fields.add(ArtikelstammItem.FLD_ATC);
		values.add(product.getATC());
		fields.add(ArtikelstammItem.FLD_PRODNO);
		values.add(item.getPRODNO());
		
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
		fields.add(ArtikelstammItem.FLD_LIMITATION);
		values.add(limitation != null ? StringConstants.ONE : StringConstants.ZERO);
		fields.add(ArtikelstammItem.FLD_LIMITATION_PTS);
		values.add((limitationPts != null) ? limitationPts.toString() : null);
		fields.add(ArtikelstammItem.FLD_LIMITATION_TEXT);
		values.add(limitationDscr);
		
		String compName = null;
		String compGln = null;
		if (item.getCOMP() != null) {
			compName = item.getCOMP().getNAME();
			compGln = item.getCOMP().getGLN();
		}
		fields.add(ArtikelstammItem.FLD_COMP_NAME);
		values.add(compName);
		fields.add(ArtikelstammItem.FLD_COMP_GLN);
		values.add(compGln);
		
		fields.add(ArtikelstammItem.FLD_PEXF);
		values.add((item.getPEXF() != null) ? item.getPEXF().toString() : null);
		if (!keepOverriddenPublicPrice) {
			fields.add(ArtikelstammItem.FLD_PPUB);
			values.add((item.getPPUB() != null) ? item.getPPUB().toString() : null);
		} else {
			if(item.getPPUB()!=null) {
				ai.setExtInfoStoredObjectByKey(ArtikelstammItem.EXTINFO_VAL_PPUB_OVERRIDE_STORE,
					item.getPPUB().toString());
				log.info("[II] [{}] Updating ppub override store to [{}]", ai.getId(), item.getPPUB());
			}
		}
		
		fields.add(ArtikelstammItem.FLD_SL_ENTRY);
		values.add((item.isSLENTRY() != null && item.isSLENTRY()) ? StringConstants.ONE
				: StringConstants.ZERO);
		
		fields.add(ArtikelstammItem.FLD_DEDUCTIBLE);
		values.add((item.getDEDUCTIBLE() != null) ? item.getDEDUCTIBLE().toString() : null);
		
		fields.add(ArtikelstammItem.FLD_GENERIC_TYPE);
		values.add(item.getGENERICTYPE());
		
		fields.add(ArtikelstammItem.FLD_IKSCAT);
		values.add(item.getIKSCAT());
		
		fields.add(ArtikelstammItem.FLD_NARCOTIC);
		values.add((item.isNARCOTIC() != null && item.isNARCOTIC()) ? StringConstants.ONE
				: StringConstants.ZERO);
		
		fields.add(ArtikelstammItem.FLD_LPPV);
		values.add(
			(item.isLPPV() != null && item.isLPPV()) ? StringConstants.ONE : StringConstants.ZERO);
		
		if (!keepOverriddenPkgSize) {
			fields.add(ArtikelstammItem.FLD_PKG_SIZE);
			String pkgSize = (item.getPKGSIZE() != null) ? item.getPKGSIZE().toString() : null;
			values.add((pkgSize != null && pkgSize.length() > 6) ? pkgSize.substring(0, 6).toString()
					: pkgSize);
			if (pkgSize != null && pkgSize.length() > 6) {
				log.warn("[II] Delimited pkg size for [{}] being [{}] to 6 characters.", ai.getId(),
					item.getPKGSIZE().toString());
			}
		} else {
			if(item.getPKGSIZE()!=null) {
				ai.setExtInfoStoredObjectByKey(ArtikelstammItem.EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE,
					item.getPKGSIZE().toString());
				log.info("[II] [{}] Updating PKG_SIZE override store to [{}] fld {}", ai.getId(), item.getPKGSIZE(), ai.get(ArtikelstammItem.FLD_PKG_SIZE));
				}
		}
		
		ai.set(fields.toArray(new String[0]), values.toArray(new String[0]));
	}
}
