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
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import at.medevit.ch.artikelstamm.ARTIKELSTAMM;
import at.medevit.ch.artikelstamm.ARTIKELSTAMM.ITEMS.ITEM;
import at.medevit.ch.artikelstamm.ARTIKELSTAMM.LIMITATIONS.LIMITATION;
import at.medevit.ch.artikelstamm.ARTIKELSTAMM.PRODUCTS.PRODUCT;
import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.ArtikelstammHelper;
import at.medevit.ch.artikelstamm.BlackBoxReason;
import at.medevit.ch.artikelstamm.elexis.common.PluginConstants;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.atccache.ATCCodeCache;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;

public class ArtikelstammImporter {
	private static Logger log = LoggerFactory.getLogger(ArtikelstammImporter.class);
	
	private static Map<String, PRODUCT> products = new HashMap<String, PRODUCT>();
	private static Map<String, LIMITATION> limitations = new HashMap<String, LIMITATION>();
	
	/**
	 * 
	 * @param monitor
	 * @param input
	 * @param version
	 *            if <code>null</code> use the version from the import file, else the provided
	 *            version value
	 * @return
	 */
	public static IStatus performImport(IProgressMonitor monitor, InputStream input,
		Integer newVersion){
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		String msg = "Aktualisierung des Artikelstamms";
		log.info(msg + " ");
		monitor.beginTask(msg, 7);
		monitor.subTask("Einlesen der Aktualisierungsdaten");
		ARTIKELSTAMM importStamm = null;
		try {
			importStamm = ArtikelstammHelper.unmarshallInputStream(input);
		} catch (JAXBException | SAXException je) {
			msg = "Fehler beim Einlesen der Import-Datei";
			Status status = new ElexisStatus(IStatus.ERROR, PluginConstants.PLUGIN_ID,
				ElexisStatus.CODE_NOFEEDBACK, msg, je);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			log.info(msg);
			return Status.CANCEL_STATUS;
		}
		monitor.worked(1);
		
		monitor.subTask("Lese Produkte und Limitationen...");
		populateProducsAndLimitationsMap(importStamm);
		monitor.worked(2);
		
		int currentVersion = ArtikelstammItem.getCurrentVersion();
		if (newVersion < currentVersion) {
			log.warn("Downgrade initiated v" + currentVersion + " -> v" + newVersion);
		}
		
		log.info("Aktualisiere v" + currentVersion + " auf v" + newVersion);
		
		long startTime = System.currentTimeMillis();
		// clean all blackbox marks, as we will determine them newly
		monitor.subTask("Black-Box Markierung zurücksetzen");
		resetAllBlackboxMarks();
		monitor.worked(1);
		// mark all items of type importStammType still referenced as blackbox
		setBlackboxOnAllReferencedItems(monitor);
		// delete all items of type importStammType not blackboxed
		monitor.subTask("Lösche nicht Black-Box Artikel");
		removeAllNonBlackboxedWithVersionLower(currentVersion, monitor);
		monitor.worked(1);
		// import the new dataset for type importStammType
		monitor.subTask(
			"Importiere Artikelstamm " + importStamm.getMONTH() + "/" + importStamm.getYEAR());
			
		importNewItemsIntoDatabase(newVersion, importStamm, monitor);
		
		importProductsForExistingItemsIntoDatabase(newVersion, importStamm, monitor);
		
		// update the version number for type importStammType
		monitor.subTask("Setze neue Versionsnummer");
		
		ArtikelstammItem.setCurrentVersion(newVersion);
		ArtikelstammItem.setImportSetCreationDate(
			importStamm.getCREATIONDATETIME().toGregorianCalendar().getTime());
			
		monitor.worked(1);
		long endTime = System.currentTimeMillis();
		ElexisEventDispatcher.reload(ArtikelstammItem.class);
		
		log.info("Artikelstamm import took " + ((endTime - startTime) / 1000) + "sec");
		
		ATCCodeCache.rebuildCache(new SubProgressMonitor(monitor, 1));
		monitor.done();
		
		return Status.OK_STATUS;
	}
	
	private static void populateProducsAndLimitationsMap(ARTIKELSTAMM importStamm){
		products = importStamm.getPRODUCTS().getPRODUCT().stream()
			.collect(Collectors.toMap(p -> p.getPRODNO(), p -> p));
		limitations = importStamm.getLIMITATIONS().getLIMITATION().stream()
			.collect(Collectors.toMap(l -> l.getLIMNAMEBAG(), l -> l));
	}
	
	/**
	 * reset all black-box marks for the item to zero, we have to determine them fresh, otherwise
	 * once blackboxed - always blackboxed
	 * 
	 * @param importStammType
	 */
	private static void resetAllBlackboxMarks(){
		Stm stm = PersistentObject.getConnection().getStatement();
		stm.exec("UPDATE " + ArtikelstammItem.TABLENAME + " SET " + ArtikelstammItem.FLD_BLACKBOXED
			+ "=" + StringConstants.ZERO);
		PersistentObject.getConnection().releaseStatement(stm);
	}
	
	/**
	 * Set {@link ArtikelstammItem#FLD_BLACKBOXED} = 1 to all items of type importStammType still
	 * being referenced by {@link Prescription}, ...
	 * 
	 * @param monitor
	 * 			
	 * @param importStammType
	 */
	private static void setBlackboxOnAllReferencedItems(IProgressMonitor monitor){
		// black box all ArtikelStammItem referenced by a prescription
		SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		
		Query<Prescription> query = new Query<Prescription>(Prescription.class);
		query.add(Prescription.FLD_ARTICLE, Query.LIKE, ArtikelstammItem.class.getName() + "%");
		List<Prescription> resultPrescription = query.execute();
		
		monitor.subTask("BlackBox Markierung für Medikationen");
		subMonitor.beginTask("", resultPrescription.size());
		for (Prescription p : resultPrescription) {
			if (p.getArtikel() instanceof ArtikelstammItem) {
				ArtikelstammItem ai = (ArtikelstammItem) p.getArtikel();
				if (ai == null || ai.get(ArtikelstammItem.FLD_ITEM_TYPE) == null) {
					log.error("Invalid ArtikestammItem or missing item type in " + ai);
					subMonitor.worked(1);
					continue;
				}
				ai.set(ArtikelstammItem.FLD_BLACKBOXED,
					BlackBoxReason.IS_REFERENCED_IN_FIXMEDICATION.getNumericalReasonString());
			}
			subMonitor.worked(1);
		}
		subMonitor.done();
		
		// black box all import ArtikelStammItem reference by a konsultations leistung
		SubProgressMonitor subMonitor2 = new SubProgressMonitor(monitor, 1);
		
		Query<Verrechnet> queryVer = new Query<Verrechnet>(Verrechnet.class);
		queryVer.add(Verrechnet.CLASS, Query.LIKE, ArtikelstammItem.class.getName());
		List<Verrechnet> resultVerrechnet = queryVer.execute();
		
		monitor.subTask("BlackBox Markierung für Artikel in Konsultationen");
		subMonitor2.beginTask("", resultVerrechnet.size());
		for (Verrechnet vr : resultVerrechnet) {
			// should be ArtikelstammItem already?! NO?
			if (vr.getVerrechenbar() != null && vr.getVerrechenbar().getCodeSystemName()
				.equals(ArtikelstammConstants.CODESYSTEM_NAME)) {
				// why do you  load again??? is not vr already ai??
				ArtikelstammItem ai = ArtikelstammItem.load(vr.getVerrechenbar().getId());
				if (ai == null || ai.get(ArtikelstammItem.FLD_ITEM_TYPE) == null) {
					log.error("Invalid ArtikestammItem or missing item type in " + ai);
					subMonitor.worked(1);
					continue;
				}
				ai.set(ArtikelstammItem.FLD_BLACKBOXED,
					BlackBoxReason.IS_REFERENCED_IN_CONSULTATION.getNumericalReasonString());
			}
			subMonitor2.worked(1);
		}
		subMonitor2.done();
		
		// Wenn ein Artikel auf Lager ist, darf er auch nicht gelöscht werden!
		SubProgressMonitor subMonitor3 = new SubProgressMonitor(monitor, 1);
		List<ArtikelstammItem> resultLagerartikel = ArtikelstammItem.getAllArticlesOnStock();
		monitor.subTask("BlackBox Markierung für Lagerartikel");
		subMonitor3.beginTask("", resultLagerartikel.size());
		for (ArtikelstammItem ai : resultLagerartikel) {
			if (ai.isLagerartikel())
				ai.set(ArtikelstammItem.FLD_BLACKBOXED,
					BlackBoxReason.IS_ON_STOCK.getNumericalReasonString());
			subMonitor3.worked(1);
		}
		subMonitor3.done();
	}
	
	/**
	 * remove all articles of importStammType with the cummulatedVersion smaller equal
	 * currentStammVersion not marked as black-boxed
	 * 
	 * @param currentStammVersion
	 * @param monitor
	 */
	private static void removeAllNonBlackboxedWithVersionLower(int currentStammVersion,
		IProgressMonitor monitor){
		Query<ArtikelstammItem> qbe = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		
		qbe.add(ArtikelstammItem.FLD_BLACKBOXED, Query.EQUALS, StringConstants.ZERO);
		qbe.add(ArtikelstammItem.FLD_CUMMULATED_VERSION, Query.LESS_OR_EQUAL,
			currentStammVersion + "");
			
		monitor.subTask("Suche nach zu entfernenden Artikeln ...");
		List<ArtikelstammItem> qre = qbe.execute();
		
		monitor.subTask("Entferne " + qre.size() + " nicht referenzierte Artikel ...");
		boolean success = ArtikelstammItem.purgeEntries(qre);
		if (!success)
			log.warn("Error purging items");
	}
	
	/**
	 * Delete all products, collect all defined PRODNO entries and generate the resp. PRODUCT entry for it
	 * @param version
	 * @param importStamm
	 * @param monitor
	 */
	private static void importProductsForExistingItemsIntoDatabase(int version,
		ARTIKELSTAMM importStamm, IProgressMonitor monitor){
		// delete all product entries
		ArtikelstammItem.purgeProducts();
		// find all defined PRODNO values
		Stm stm = PersistentObject.getConnection().getStatement();
		ResultSet rs = stm.query("SELECT DISTINCT(" + ArtikelstammItem.FLD_PRODNO + ") FROM "
			+ ArtikelstammItem.TABLENAME);
		List<String> productList = new ArrayList<String>();
		try {
			while (rs.next()) {
				String prodNo = rs.getString(ArtikelstammItem.FLD_PRODNO);
				if(prodNo!=null) {
					productList.add(prodNo);
				}
			}
		} catch (SQLException e) {
			log.error("Error executing distinct product selection", e);
		}
		PersistentObject.getConnection().releaseStatement(stm);
		// for each defined PRODNO value generate the resp. product entry		
		productList.stream().forEachOrdered(s -> {
			PRODUCT product = products.get(s);
			if(product!=null) {
				ArtikelstammItem productItem = new ArtikelstammItem(version, 'X', StringConstants.EMPTY,
					new BigInteger(product.getPRODNO()), product.getDSCR(), StringConstants.EMPTY);
				String atc = product.getATC();
				if(atc!=null) {
					productItem.setATCCode(atc);
				}
			} else {
				log.error("Product is null for {}", s);
			}
			
		});
		// TODO fixed length for prodno?
	}
	
	private static void importNewItemsIntoDatabase(int newVersion, ARTIKELSTAMM importStamm,
		IProgressMonitor monitor){
		SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		List<ITEM> importItemList = importStamm.getITEMS().getITEM();
		subMonitor.beginTask("", importItemList.size());
		
		ArtikelstammItem ai = null;
		for (ITEM item : importItemList) {
			String itemUuid =
				ArtikelstammHelper.createUUID(newVersion, item.getGTIN(), item.getPHAR(), false);
			// Is the item to be imported already in the database? This should only happen
			// if one re-imports an already imported dataset and the item was marked as black-box
			
			Stm stm = PersistentObject.getConnection().getStatement();
			int foundElements = stm.queryInt("SELECT COUNT(*) FROM " + ArtikelstammItem.TABLENAME
				+ " WHERE " + ArtikelstammItem.FLD_ID + " " + Query.LIKE + " "
				+ JdbcLink.wrap(itemUuid + "%"));
			PersistentObject.getConnection().releaseStatement(stm);
			
			if (foundElements == 0) {
				ai = new ArtikelstammItem(newVersion, item.getPHARMATYPE().charAt(0),
					item.getGTIN(), item.getPHAR(), item.getDSCR(), StringConstants.EMPTY);
				setValuesOnArtikelstammItem(ai, item, false, -1);
			} else if (foundElements == 1) {
				String itemId = PersistentObject.getConnection()
					.queryString("SELECT ID FROM " + ArtikelstammItem.TABLENAME + " WHERE "
						+ ArtikelstammItem.FLD_ID + " " + Query.LIKE + " "
						+ JdbcLink.wrap(itemUuid + "%"));
				ai = ArtikelstammItem.load(itemId);
				log.info("Updating article " + ai.getId() + " (" + item.getDSCR() + ")");
				setValuesOnArtikelstammItem(ai, item, true, newVersion);
			} else {
				log.error("Found " + foundElements + " items for " + itemUuid + ".");
			}
			
			subMonitor.worked(1);
		}
		subMonitor.done();
	}
	
	private static void setValuesOnArtikelstammItem(ArtikelstammItem ai, ITEM item,
		boolean allValues, final int cummulatedVersion){
		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();
		
		// reset blackbox as we updated the article
		fields.add(ArtikelstammItem.FLD_BLACKBOXED);
		values.add(StringConstants.ZERO);
		
		fields.add(ArtikelstammItem.FLD_GTIN);
		values.add(item.getGTIN());
		
		if (allValues) {
			// include header values
			fields.add(ArtikelstammItem.FLD_DSCR);
			values.add(item.getDSCR());
			fields.add(ArtikelstammItem.FLD_CUMMULATED_VERSION);
			values.add(cummulatedVersion + "");
		}
		
		String prodno = item.getPRODNO();
		if (prodno != null) {
			PRODUCT product = products.get(prodno);
			if (product != null) {
				fields.add(ArtikelstammItem.FLD_ATC);
				values.add(product.getATC());
				fields.add(ArtikelstammItem.FLD_PRODNO);
				values.add(prodno);
				
				String limnamebag = product.getLIMNAMEBAG();
				if (limnamebag != null) {
					LIMITATION limitation = limitations.get(limnamebag);
					
					fields.add(ArtikelstammItem.FLD_LIMITATION);
					values.add(limitation != null ? StringConstants.ONE : StringConstants.ZERO);
					
					if (limitation != null) {
						if(limitation.getLIMITATIONPTS()!=null) {
							fields.add(ArtikelstammItem.FLD_LIMITATION_PTS);
							values.add(limitation.getLIMITATIONPTS().toString());
						}

						fields.add(ArtikelstammItem.FLD_LIMITATION_TEXT);
						values.add(limitation.getDSCR());
					}
				}
				
			}
		}
		
		if (item.getCOMP() != null) {
			if (item.getCOMP().getNAME() != null) {
				fields.add(ArtikelstammItem.FLD_COMP_NAME);
				values.add(item.getCOMP().getNAME());
			}
			if (item.getCOMP().getGLN() != null) {
				fields.add(ArtikelstammItem.FLD_COMP_GLN);
				values.add(item.getCOMP().getGLN());
			}
		}
		if (item.getPEXF() != null) {
			fields.add(ArtikelstammItem.FLD_PEXF);
			values.add(item.getPEXF().toString());
		}
		if (item.getPPUB() != null) {
			fields.add(ArtikelstammItem.FLD_PPUB);
			values.add(item.getPPUB().toString());
		}
		if (item.isSLENTRY() != null) {
			fields.add(ArtikelstammItem.FLD_SL_ENTRY);
			values.add((item.isSLENTRY()) ? StringConstants.ONE : StringConstants.ZERO);
		}
		if (item.getDEDUCTIBLE() != null) {
			fields.add(ArtikelstammItem.FLD_DEDUCTIBLE);
			values.add(item.getDEDUCTIBLE().toString());
		}
		if (item.getGENERICTYPE() != null) {
			fields.add(ArtikelstammItem.FLD_GENERIC_TYPE);
			values.add(item.getGENERICTYPE());
		}
		if (item.getIKSCAT() != null) {
			fields.add(ArtikelstammItem.FLD_IKSCAT);
			values.add(item.getIKSCAT());
		}
		if (item.isNARCOTIC() != null) {
			fields.add(ArtikelstammItem.FLD_NARCOTIC);
			values.add((item.isNARCOTIC()) ? StringConstants.ONE : StringConstants.ZERO);
		}
		if (item.isLPPV() != null) {
			fields.add(ArtikelstammItem.FLD_LPPV);
			values.add((item.isLPPV()) ? StringConstants.ONE : StringConstants.ZERO);
		}
		
		if (item.getPKGSIZE() != null) {
			fields.add(ArtikelstammItem.FLD_PKG_SIZE);
			values.add(item.getPKGSIZE().toString());
		}
		
		ai.set(fields.toArray(new String[0]), values.toArray(new String[0]));
	}
}
