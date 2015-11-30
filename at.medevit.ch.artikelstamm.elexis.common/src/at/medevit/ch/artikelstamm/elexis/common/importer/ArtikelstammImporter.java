/*******************************************************************************
 * Copyright (c) 2013-2014 MEDEVIT.
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
import java.util.List;

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
import at.medevit.ch.artikelstamm.ARTIKELSTAMM.ITEM;
import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;
import at.medevit.ch.artikelstamm.ArtikelstammHelper;
import at.medevit.ch.artikelstamm.elexis.common.PluginConstants;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.atccache.ATCCodeCache;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.artikelstamm.elexis.common.BlackBoxReason;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.JdbcLink;

public class ArtikelstammImporter {
	private static Logger log = LoggerFactory.getLogger(ArtikelstammImporter.class);
	
	/**
	 * 
	 * @param monitor
	 * @param input
	 * @param version
	 *            if <code>null</code> use the version from the import file, else the provided
	 *            version value
	 * @return
	 */
	public static IStatus performImport(IProgressMonitor monitor, InputStream input, Integer version){
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		String msg = "Aktualisierung des Artikelstamms";
		log.info(msg + " ");
		monitor.beginTask(msg, 6);
		monitor.subTask("Einlesen der Aktualisierungsdaten");
		ARTIKELSTAMM importStamm = null;
		try {
			importStamm = ArtikelstammHelper.unmarshallInputStream(input);
		} catch (JAXBException | SAXException je) {
			msg = "Fehler beim Einlesen der Import-Datei";
			Status status =
				new ElexisStatus(IStatus.ERROR, PluginConstants.PLUGIN_ID,
					ElexisStatus.CODE_NOFEEDBACK, msg, je);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			log.info(msg);
			return Status.CANCEL_STATUS;
		}
		monitor.worked(1);
		
		int importStammVersion = importStamm.getCUMULVER();
		// the type of import articles in the file (PHARMA or NONPHARMA)
		TYPE importStammType = ArtikelstammConstants.TYPE.valueOf(importStamm.getTYPE());
		// the current version stored in the database for importStammType
		int currentStammVersion = ArtikelstammItem.getImportSetCumulatedVersion(importStammType);
		
		log.info("Importing " + currentStammVersion + " -> " + importStammVersion);
		
		long startTime = System.currentTimeMillis();
		// clean all blackbox marks, as we will determine them newly
		monitor.subTask("Black-Box Markierung zurücksetzen");
		resetAllBlackboxMarks(importStammType);
		monitor.worked(1);
		// mark all items of type importStammType still referenced as blackbox
		setBlackboxOnAllReferencedItems(monitor, importStammType);
		// delete all items of type importStammType not blackboxed
		monitor.subTask("Lösche nicht Black-Box Artikel");
		removeAllNonBlackboxedWithVersion(importStammType, currentStammVersion, monitor);
		monitor.worked(1);
		// import the new dataset for type importStammType
		monitor.subTask("Importiere Datensatz " + importStamm.getTYPE() + " "
			+ importStamm.getMONTH() + "/" + importStamm.getYEAR());
		importNewItemsIntoDatabase(importStammType, importStamm, monitor);
		// update the version number for type importStammType
		monitor.subTask("Setze neue Versionsnummer");
		
		if(version != null) {
			ArtikelstammItem.setImportSetCumulatedVersion(importStammType, version);
		} else {
			ArtikelstammItem.setImportSetCumulatedVersion(importStammType, importStammVersion);
		}

		ArtikelstammItem.setImportSetDataQuality(importStammType, importStamm.getDATAQUALITY());
		ArtikelstammItem.setImportSetCreationDate(importStammType, importStamm.getCREATIONDATETIME().toGregorianCalendar().getTime());
		
		monitor.worked(1);
		long endTime = System.currentTimeMillis();
		ElexisEventDispatcher.reload(ArtikelstammItem.class);
		
		log.info("Artikelstamm import of" + importStammType + ": " + importStammVersion + " took "
			+ ((endTime - startTime) / 1000) + "sec");
		
		ATCCodeCache.rebuildCache(new SubProgressMonitor(monitor, 1));
		monitor.done();
		
		return Status.OK_STATUS;
	}
	
	/**
	 * reset all black-box marks for the item to zero, we have to determine them fresh, otherwise
	 * once blackboxed - always blackboxed
	 * 
	 * @param importStammType
	 */
	private static void resetAllBlackboxMarks(TYPE importStammType){
		PersistentObject.getConnection().exec(
			"UPDATE " + ArtikelstammItem.TABLENAME + " SET " + ArtikelstammItem.FLD_BLACKBOXED
				+ "=" + StringConstants.ZERO + " WHERE " + ArtikelstammItem.FLD_ITEM_TYPE
				+ " LIKE " + JdbcLink.wrap(importStammType.name()));
	}
	
	/**
	 * Set {@link ArtikelstammItem#FLD_BLACKBOXED} = 1 to all items of type importStammType still
	 * being referenced by {@link Prescription}, ...
	 * 
	 * @param monitor
	 * 
	 * @param importStammType
	 */
	private static void setBlackboxOnAllReferencedItems(IProgressMonitor monitor,
		TYPE importStammType){
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
				if (ai.get(ArtikelstammItem.FLD_ITEM_TYPE).equalsIgnoreCase(importStammType.name()))
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
			if (vr.getVerrechenbar() != null
				&& vr.getVerrechenbar().getCodeSystemName()
					.equals(ArtikelstammConstants.CODESYSTEM_NAME)) {
				ArtikelstammItem ai = ArtikelstammItem.load(vr.getVerrechenbar().getId());
				if (ai == null || ai.get(ArtikelstammItem.FLD_ITEM_TYPE) == null) {
					log.error("Invalid ArtikestammItem or missing item type in " + ai);
					subMonitor.worked(1);
					continue;
				}
				if (ai.get(ArtikelstammItem.FLD_ITEM_TYPE).equalsIgnoreCase(importStammType.name()))
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
			if (ai.get(ArtikelstammItem.FLD_ITEM_TYPE).equalsIgnoreCase(importStammType.name()))
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
	 * @param importStammType
	 * @param currentStammVersion
	 * @param monitor
	 */
	private static void removeAllNonBlackboxedWithVersion(TYPE importStammType,
		int currentStammVersion, IProgressMonitor monitor){
		Query<ArtikelstammItem> qbe = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		
		qbe.add(ArtikelstammItem.FLD_BLACKBOXED, Query.EQUALS, StringConstants.ZERO);
		qbe.add(ArtikelstammItem.FLD_ITEM_TYPE, Query.EQUALS, importStammType.name());
		qbe.add(ArtikelstammItem.FLD_CUMMULATED_VERSION, Query.LESS_OR_EQUAL, currentStammVersion
			+ "");
		
		monitor.subTask("Suche nach zu entfernenden Artikeln ...");
		List<ArtikelstammItem> qre = qbe.execute();
		
		monitor.subTask("Entferne " + qre.size() + " nicht referenzierte Artikel ...");
		boolean success = ArtikelstammItem.purgeEntries(qre);
		if (!success)
			log.warn("Error purging items");
	}
	
	private static void importNewItemsIntoDatabase(TYPE importStammType, ARTIKELSTAMM importStamm,
		IProgressMonitor monitor){
		SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		List<ITEM> importItemList = importStamm.getITEM();
		subMonitor.beginTask("", importItemList.size());
		
		ArtikelstammItem ai = null;
		for (ITEM item : importItemList) {
			String itemUuid =
				ArtikelstammHelper.createUUID(importStamm.getCUMULVER(), importStammType,
					item.getGTIN(), item.getPHAR(), false);
			// Is the item to be imported already in the database? This should only happen
			// if one re-imports an already imported dataset and the item was marked as black-box
			int foundElements =
				PersistentObject.getConnection().queryInt(
					"SELECT COUNT(*) FROM " + ArtikelstammItem.TABLENAME + " WHERE "
						+ ArtikelstammItem.FLD_ID + " " + Query.LIKE + " "
						+ JdbcLink.wrap(itemUuid + "%"));
			
			if (foundElements == 0) {
				ai =
					new ArtikelstammItem(importStamm.getCUMULVER(), importStammType,
						item.getGTIN(), item.getPHAR(), item.getDSCR(), item.getADDSCR());
				setValuesOnArtikelstammItem(ai, item, false, -1);
			} else if (foundElements == 1) {
				String itemId =
					PersistentObject.getConnection().queryString(
						"SELECT ID FROM " + ArtikelstammItem.TABLENAME + " WHERE "
							+ ArtikelstammItem.FLD_ID + " " + Query.LIKE + " "
							+ JdbcLink.wrap(itemUuid + "%"));
				ai = ArtikelstammItem.load(itemId);
				log.info("Updating article " + ai.getId() + " (" + item.getDSCR() + ")");
				setValuesOnArtikelstammItem(ai, item, true, importStamm.getCUMULVER());
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
		
		if (allValues) {
			// include header values
			fields.add(ArtikelstammItem.FLD_DSCR);
			values.add(item.getDSCR());
			fields.add(ArtikelstammItem.FLD_ADDDSCR);
			values.add(item.getADDSCR());
			fields.add(ArtikelstammItem.FLD_CUMMULATED_VERSION);
			values.add(cummulatedVersion + "");
		}
		
		if (item.getATC() != null) {
			fields.add(ArtikelstammItem.FLD_ATC);
			values.add(item.getATC());
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
		if (item.isLIMITATION() != null) {
			fields.add(ArtikelstammItem.FLD_LIMITATION);
			values.add((item.isLIMITATION()) ? StringConstants.ONE : StringConstants.ZERO);
		}
		if (item.getLIMITATIONPTS() != null) {
			fields.add(ArtikelstammItem.FLD_LIMITATION_PTS);
			values.add(item.getLIMITATIONPTS().toString());
		}
		if (item.getLIMITATIONTEXT() != null) {
			fields.add(ArtikelstammItem.FLD_LIMITATION_TEXT);
			values.add(item.getLIMITATIONTEXT());
		}
		if (item.getPKGSIZE() != null) {
			fields.add(ArtikelstammItem.FLD_PKG_SIZE);
			values.add(item.getPKGSIZE().toString());
		}
		if(item.getPRODNO()!=null) {
			fields.add(ArtikelstammItem.FLD_PRODNO);
			values.add(item.getPRODNO());
		}
		
		ai.set(fields.toArray(new String[0]), values.toArray(new String[0]));
	}
}
