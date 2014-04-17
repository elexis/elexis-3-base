/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2012
 * 
 *******************************************************************************/
package ch.elexis.laborimport.viollier.v2_test.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.labor.viollier.v2.data.ViollierLaborImportSettings;
import ch.elexis.labor.viollier.v2.labimport.LabOrderImport;
import ch.elexis.labor.viollier.v2.labimport.LabOrderImport.SaveResult;
import ch.elexis.labor.viollier.v2.labimport.PatientLabor;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;

/**
 * Eigentliche JUnit Tests zum Laborimporter Viollier Testet die Testfälle von Thomas Weilenmann,
 * Viollier
 * 
 */
public class Test_doImport {
	
	private final String rscDir = getPluginDirectory() + "rsc/"; //$NON-NLS-1$
	private ViollierLaborImportSettings settings;
	private final boolean overwriteOlderEntries = false;
	
	private static String TESTPAT_M_NAME = "Testpatient"; //$NON-NLS-1$
	private static String TESTPAT_M_VORNAME = "Garfield"; //$NON-NLS-1$
	private static String TESTPAT_M_GEBDAT = "19121213"; //$NON-NLS-1$
	private static String TESTPAT_M_SEX = "m"; //$NON-NLS-1$
	
	private static String TESTPAT_F_NAME = "Testpatientin"; //$NON-NLS-1$
	private static String TESTPAT_F_VORNAME = "Minnie Mouse"; //$NON-NLS-1$
	private static String TESTPAT_F_GEBDAT = "13.12.1912"; //$NON-NLS-1$
	private static String TESTPAT_F_SEX = "w"; //$NON-NLS-1$
	
	private static String TEST_CATEGORY = "Laborbefunde Viollier"; //$NON-NLS-1$
	
	private Patient patient = null;
	
	/**
	 * Prüft, ob das vorhandene PDF auch tatsächlich in Omnivore abgelegt wird
	 */
	@Test
	public void Test01_PDFBefunde(){
		try {
			if (settings == null)
				settings = new ViollierLaborImportSettings((CoreHub.actMandant));
			
			settings.setGlobalDocumentCategory(TEST_CATEGORY);
			
			patient = findExactTestPatientM();
			if (patient != null)
				patient.delete(true);
			
			String file2Import = rscDir + "Test01.HL7"; //$NON-NLS-1$
			File hl7File = new File(file2Import);
			File downloadDir = new File(rscDir);
			AtomicReference<File> pdfFileRef = new AtomicReference<File>();
			SaveResult result;
			String test = "Test01_PDFBefunde"; //$NON-NLS-1$
			
			// Omnivore initialisiern
			IDocumentManager dm = null;
			Object os = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
			if (os != null) {
				dm = (IDocumentManager) os;
			}
			
			// Zunächst ein File importieren, ohne den Patienten dazu erfasst zu haben
			// -> muss fehlschlagen
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen ", result, SaveResult.ERROR); //$NON-NLS-1$
			
			// Dann den Patienten erfassen und das File nochmals importieren
			// -> muss erfolgreich sein
			patient =
				new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201202151448"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", "", "", LabItem.typ.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "01104450159201202151448.pdf", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "07200", "Proteine gesamt", "60 - 80", "", "g/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "61", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35022", "Albumin", "56.0 - 69.0", "", "%", LabItem.typ.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "63.9", //$NON-NLS-1$
				"http://salclab0/0010614911635039.wmf"); //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35024", "Alpha-1-Globulin", "2.7 - 5.4", "", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "4.7", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35026", "Alpha-2-Globulin", "6.5 - 13.2", "", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "9.9", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35030", "Gamma-Globulin", "9.6 - 17.5", "", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "11.7", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35036", "Beta-1-Globulin", "4.6 - 6.9", "", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "5.7", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35037", "Beta-2-Globulin", "2.8 - 5.9", "", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "4.1", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "MIKROBIOLOG1", "Mikrobiologie", "", "", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(
				test,
				patient,
				item,
				hl7TimeStamp,
				"Text", //$NON-NLS-1$
				"01.04.2010 08:15\\.br\\Urin: Mittelstra\\.br\\\\.br\\Gesamtkeimzahl: 1.000.000 cfu/mL\\.br\\Aerob:  1.                 koagulasenegative Staphylokokken\\.br\\                               1 . \\.br\\                               -   \\.br\\  Penicillin                 . S .  \\.br\\  Amoxicillin                . S .  \\.br\\  Oxacillin                  . S .  \\.br\\  Co-Amoxiclav               . S .  \\.br\\  Cefalotin                  . S .  \\.br\\  Cefuroxim                  . S .  \\.br\\  Ceftriaxon                 . S .  \\.br\\  Erythromycin               . S .  \\.br\\  Clindamycin                . S .  \\.br\\  Gentamicin                 . S .  \\.br\\  Tobramycin                 . S .  \\.br\\  Tetracyclin                . S .  \\.br\\  Co-trimoxazol              . S .  \\.br\\  Nitrofurantoin             . S .  \\.br\\  Ciprofloxacin              . S .  \\.br\\  Norfloxacin                . S .  \\.br\\  Levofloxacin               . S .  \\.br\\  Moxifloxacin               . S .  \\.br\\  Vancomycin                 . S .  \\.br\\  Teicoplanin                . S .  \\.br\\  Rifampicin                 . S .  \\.br\\  Fusidinsäure               . S .  \\.br\\  Fosfomycin                 . S .  \\.br\\  Linezolid                  . S .  \\.br\\\\.br\\"); //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(
					test,
					"doc", //$NON-NLS-1$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameDokumentLaborParameter,
					"", "", "pdf", LabItem.typ.DOCUMENT, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			
			// omnivore kontrollieren
			List<IOpaqueDocument> items = dm.listDocuments(patient, null, null, null, null, null);
			assertEquals(test + ": Falsche Anzahl Dokumenten", items.size(), 1); //$NON-NLS-1$
			if (items.size() == 1) {
				IOpaqueDocument doc = items.get(0);
				String cat = TEST_CATEGORY;
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				String date = sdf.format(new TimeTool().getTime());
				// Kategorien sind nur bei Omnivore plus oder Omnivore direct, aber nicht bei
				// Omnivore unterstützt
				if (dm.getCategories() == null)
					cat = "";
				assertEquals(test + ": Falscher Wert bei Dokument: Kategorie", doc.getCategory(), //$NON-NLS-1$
					cat);
				assertEquals(test + ": Falscher Wert bei Dokument: Titel", doc.getTitle(), //$NON-NLS-1$
					"Laborbefund 2012-02-15 14:48:00.pdf"); //$NON-NLS-1$
				assertEquals(test + ": Falscher Wert bei Dokument: Datum", doc.getCreationDate(), //$NON-NLS-1$
					date); //$NON-NLS-1$
				assertEquals(test + ": Falscher Wert bei Dokument: Keywords", doc.getKeywords(), //$NON-NLS-1$
					"1_1_0002834_3_20120215145502_01105727278_20100401_Testpatient_19121213_1234_12004.pdf"); //$NON-NLS-1$
			}
			
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Prüft, ob die Bemerkungen zum Auftrag und die Bemerkungen zum Laborresultat richtig
	 * importiert werden
	 */
	@Test
	public void Test02_Bemerkungen(){
		try {
			if (settings == null)
				settings = new ViollierLaborImportSettings((CoreHub.actMandant));
			
			String file2Import = rscDir + "Test02.HL7"; //$NON-NLS-1$
			File hl7File = new File(file2Import);
			File downloadDir = new File(rscDir);
			AtomicReference<File> pdfFileRef = new AtomicReference<File>();
			SaveResult result;
			String test = "Test02_Bemerkungen"; //$NON-NLS-1$
			
			patient = findExactTestPatientM();
			if (patient == null)
				patient =
					new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201204200828"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item = checkLabItem(test, "Kommentar", "Kommentar", "", "", "", LabItem.typ.TEXT, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"00 Kommentar", PatientLabor.DEFAULT_PRIO); //$NON-NLS-1$
			checkLabWert(
				test,
				patient,
				item,
				hl7TimeStamp,
				"Text", //$NON-NLS-1$
				"-> Die Namensbeschriftung auf dem Auftragsformular und dem\\.br\\Untersuchungsmaterial war nicht korrekt.\\.br\\Gemäss telefonischer Rücksprache ist der Auftrag\\.br\\mit folgendem Namen falsch beschriftet: Testpatient Odie\\.br\\Die Korrektur wurde via Fax bestätigt."); //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", "", "", LabItem.typ.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "01100019516201204200828.pdf", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "23000", "Natrium", "135 - 147", "", "mmol/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "145", //$NON-NLS-1$
				"-> Resultat mit Vorbehalt, da zu wenig Untersuchungsmaterial \\.br\\vorhanden war."); //$NON-NLS-1$
			
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Prüft, ob die 3 unterschiedlichen Mikrobiologiebefunde auch tatsächlich alle importiert
	 * werden (sie haben leider alle den selben Text zur Testidentifikation. Deshalb muss hier das
	 * Kürzel hinzugenommen werden...
	 */
	@Test
	public void Test03_Mikrobiologie(){
		try {
			if (settings == null)
				settings = new ViollierLaborImportSettings((CoreHub.actMandant));
			
			String file2Import = rscDir + "Test03.HL7"; //$NON-NLS-1$
			File hl7File = new File(file2Import);
			File downloadDir = new File(rscDir);
			AtomicReference<File> pdfFileRef = new AtomicReference<File>();
			SaveResult result;
			String test = "Test03_Mikrobiologie"; //$NON-NLS-1$
			
			patient = findExactTestPatientM();
			if (patient == null)
				patient =
					new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201205111018"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", "", "", LabItem.typ.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "01100020737201205111018.pdf", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "MIKROBIOLOG1", "Mikrobiologie", "", "", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(
				test,
				patient,
				item,
				hl7TimeStamp,
				"Text", //$NON-NLS-1$
				"10.05.2012 10:15\\.br\\Biopsie vs: Hoden\\.br\\\\.br\\Aerob: nach Anreicherung: \\.br\\ 1.                 Escherichia coli\\.br\\                               1 . \\.br\\                               -   \\.br\\  Amoxicillin                . R .  \\.br\\  Co-Amoxiclav               . S .  \\.br\\  Piperacillin/Tazobactam    . S .  \\.br\\  Cefalotin                  . I .  \\.br\\  Cefuroxim                  . S .  \\.br\\  Cefuroxim Axetil           . S .  \\.br\\  Cefpodoxim                 . S .  \\.br\\  Ceftazidim                 . S .  \\.br\\  Ceftriaxon                 . S .  \\.br\\  Cefepim                    . S .  \\.br\\  Imipenem                   . S .  \\.br\\  Meropenem                  . S .  \\.br\\  Amikacin                   . S .  \\.br\\  Gentamicin                 . S .  \\.br\\  Tobramycin                 . S .  \\.br\\  Co-trimoxazol              . R .  \\.br\\  Ciprofloxacin              . S .  \\.br\\\\.br\\Anaerob: kein Wachstum\\.br\\Direktpräparat: kein Nachweis von säurefesten Stäbchen \\.br\\-> Resultat mit Vorbehalt, da zu wenig Untersuchungsmaterial \\.br\\vorhanden war.\\.br\\Kultur: +               säurefeste Stäbchen \\.br\\genaue Identifizierung: \\.br\\ 1.                 Mycobacterium tuberculosis\\.br\\                               1 . \\.br\\                               -   \\.br\\  Ethambutol 5.0 mg/L        . S .  \\.br\\  Isoniazid 0.1 mg/L         . S .  \\.br\\  Pyrazinamid 100 mg/L       . S .  \\.br\\  Rifampicin 1.0 mg/L        . S .  \\.br\\  Streptomycin 1.0 mg/L      . S .  \\.br\\\\.br\\Epithelien: 0\\.br\\Leukozyten: 0\\.br\\Grampositive Stäbchen: 0\\.br\\Grampositive Kokken: 0\\.br\\Gramnegative Stäbchen: 0\\.br\\Gramnegative Diplokokken: 0\\.br\\Sprosspilze: 0\\.br\\"); //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "MIKROBIOLOG2", "Mikrobiologie", "", "", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(
				test,
				patient,
				item,
				hl7TimeStamp,
				"Text", //$NON-NLS-1$
				"10.05.2012 10:15\\.br\\Biopsie vs: II\\.br\\\\.br\\Aerob:  1. +               Pseudomonas aeruginosa \\.br\\-> P. aeruginosa zeigt eine natürliche Resistenz gegenüber \\.br\\Amoxicillin - Clavulansäure, Cephalosporine 1./2. Generation \\.br\\sowie Ceftriaxon und Co-trimoxazol. \\.br\\ 2. +               Proteus vulgaris \\.br\\-> 70-80 Prozent der Stämme weisen eine induzierbare \\.br\\Cephalosporinase auf. Die Therapie mit 3. Generation \\.br\\Cephalosporinen wie Ceftriaxon oder Ceftazidim, sowie mit \\.br\\Piperazillin/Tazobactam ist aus diesem Grund nicht empfohlen. \\.br\\ 3. +               Escherichia coli\\.br\\                               1 .  2 .  3 . \\.br\\                               -    -    -   \\.br\\  Amoxicillin                .   .  R .  R .  \\.br\\  Co-Amoxiclav               .   .  S .  S .  \\.br\\  Piperacillin/Tazobactam    . S .  S .  S .  \\.br\\  Cefalotin                  .   .  R .  I .  \\.br\\  Cefuroxim                  .   .  R .  S .  \\.br\\  Cefuroxim Axetil           .   .  R .  S .  \\.br\\  Cefpodoxim                 .   .  S .  S .  \\.br\\  Ceftazidim                 . S .  S .  S .  \\.br\\  Ceftriaxon                 .   .  S .  S .  \\.br\\  Cefepim                    . S .  S .  S .  \\.br\\  Imipenem                   . S .  S .  S .  \\.br\\  Meropenem                  . S .  S .  S .  \\.br\\  Aztreonam                  . I .    .    .  \\.br\\  Amikacin                   . S .  S .  S .  \\.br\\  Gentamicin                 . S .  S .  S .  \\.br\\  Tobramycin                 . S .  S .  S .  \\.br\\  Co-trimoxazol              . R .  S .  R .  \\.br\\  Ciprofloxacin              . S .  S .  S .  \\.br\\  Colistin                   . S .    .    .  \\.br\\\\.br\\Anaerob: +               Peptostreptococcus species\\.br\\Direktpräparat: kein Nachweis von säurefesten Stäbchen\\.br\\Kultur: +               Mycobacterium tuberculosis\\.br\\Epithelien: 0\\.br\\Leukozyten: 0\\.br\\Grampositive Stäbchen: 0\\.br\\Grampositive Kokken: 0\\.br\\Gramnegative Stäbchen: 0\\.br\\Gramnegative Diplokokken: 0\\.br\\Sprosspilze: 0\\.br\\"); //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "MIKROBIOLOG3", "Mikrobiologie", "", "", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(
				test,
				patient,
				item,
				hl7TimeStamp,
				"Text", //$NON-NLS-1$
				"10.05.2012 10:15\\.br\\Wundab.tie: \\.br\\\\.br\\Aerob:  1. +               Pseudomonas aeruginosa \\.br\\-> P. aeruginosa zeigt eine natürliche Resistenz gegenüber \\.br\\Amoxicillin - Clavulansäure, Cephalosporine 1./2. Generation \\.br\\sowie Ceftriaxon und Co-trimoxazol. \\.br\\ 2. +               Escherichia coli\\.br\\                               1 .  2 . \\.br\\                               -    -   \\.br\\  Amoxicillin                .   .  R .  \\.br\\  Co-Amoxiclav               .   .  S .  \\.br\\  Piperacillin/Tazobactam    . S .  S .  \\.br\\  Cefalotin                  .   .  I .  \\.br\\  Cefuroxim                  .   .  S .  \\.br\\  Cefuroxim Axetil           .   .  S .  \\.br\\  Cefpodoxim                 .   .  S .  \\.br\\  Ceftazidim                 . S .  S .  \\.br\\  Ceftriaxon                 .   .  S .  \\.br\\  Cefepim                    . S .  S .  \\.br\\  Imipenem                   . S .  S .  \\.br\\  Meropenem                  . S .  S .  \\.br\\  Aztreonam                  . I .    .  \\.br\\  Amikacin                   . S .  S .  \\.br\\  Gentamicin                 . S .  S .  \\.br\\  Tobramycin                 . S .  S .  \\.br\\  Co-trimoxazol              . R .  R .  \\.br\\  Ciprofloxacin              . S .  S .  \\.br\\  Colistin                   . S .    .  \\.br\\\\.br\\Anaerob: kein Wachstum\\.br\\Epithelien: 0\\.br\\Leukozyten: +\\.br\\Grampositive Stäbchen: 0\\.br\\Grampositive Kokken: 0\\.br\\Gramnegative Stäbchen: 0\\.br\\Gramnegative Diplokokken: 0\\.br\\Sprosspilze: 0\\.br\\"); //$NON-NLS-1$
			
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Prüft, die Referenzbereiche korrekt zu Männer und Frauen übernommen werden
	 */
	@Test
	public void Test04_ReferenzbereicheMW(){
		try {
			if (settings == null)
				settings = new ViollierLaborImportSettings((CoreHub.actMandant));
			
			String file2Import = rscDir + "Test04_m.HL7"; //$NON-NLS-1$
			File hl7File = new File(file2Import);
			File downloadDir = new File(rscDir);
			AtomicReference<File> pdfFileRef = new AtomicReference<File>();
			SaveResult result;
			String test = "Test04_ReferenzbereicheMW"; //$NON-NLS-1$
			
			patient = findExactTestPatientM();
			if (patient == null)
				patient =
					new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201205101059"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", "", "", LabItem.typ.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "01100019516201205101059.pdf", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "22751", "Hämoglobin", "135 - 175", "", "g/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "150", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// Weiblicher Testpatient erstellen
			patient =
				new Patient(TESTPAT_F_NAME, TESTPAT_F_VORNAME, TESTPAT_F_GEBDAT, TESTPAT_F_SEX);
			
			file2Import = rscDir + "Test04_w.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			hl7TimeStamp = "201205101103"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", "", "", LabItem.typ.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "01100020726201205101103.pdf", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "22751", "Hämoglobin", "135 - 175", "120 - 160", "g/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "130", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Prüft, ob die Einstellung zum Überschreiben von Referenzbereichen richtig angewendet wird
	 */
	@Test
	public void Test05_ReferenzbereichChange(){
		try {
			if (settings == null)
				settings = new ViollierLaborImportSettings((CoreHub.actMandant));
			
			String file2Import = rscDir + "Test05_1.HL7"; //$NON-NLS-1$
			File hl7File = new File(file2Import);
			File downloadDir = new File(rscDir);
			AtomicReference<File> pdfFileRef = new AtomicReference<File>();
			SaveResult result;
			String test = "Test05_ReferenzbereichChange"; //$NON-NLS-1$
			
			patient = findExactTestPatientM();
			if (patient == null)
				patient =
					new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			
			// Konfiguration: Ohne Überschreiben
			settings.setGlobalSaveRefRange(false);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201205110827"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "11700", "Paracetamol", "65 - 130", "", "µmol/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "66.0", //$NON-NLS-1$
				"-> Diese Analyse wird in einem externen Labor durchgeführt.\ntoxisch> 790"); //$NON-NLS-1$
			
			file2Import = rscDir + "Test05_2.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.REF_RANGE_MISMATCH); //$NON-NLS-1$
			
			hl7TimeStamp = "201205110833"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "11700", "Paracetamol", "65 - 130", "", "µmol/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "72.6", //$NON-NLS-1$
				"-> Diese Analyse wird in einem externen Labor durchgeführt.\ntoxisch> 790"); //$NON-NLS-1$
			
			// Konfiguration: Mit Überschreiben
			settings.setGlobalSaveRefRange(true);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			hl7TimeStamp = "201205110833"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "11700", "Paracetamol", "70 - 130", "", "µmol/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "72.6", //$NON-NLS-1$
				"-> Diese Analyse wird in einem externen Labor durchgeführt.\ntoxisch> 790"); //$NON-NLS-1$
			
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Prüft, ob die richtigen Timestamps verwendet werden
	 */
	@Test
	public void Test06_TeilbefundOhneEntnahmedatum(){
		try {
			if (settings == null)
				settings = new ViollierLaborImportSettings((CoreHub.actMandant));
			
			String file2Import = rscDir + "Test06_1.HL7"; //$NON-NLS-1$
			File hl7File = new File(file2Import);
			File downloadDir = new File(rscDir);
			AtomicReference<File> pdfFileRef = new AtomicReference<File>();
			SaveResult result;
			String test = "Test06_TeilbefundOhneEntnahmedatum"; //$NON-NLS-1$
			
			patient = findExactTestPatientM();
			if (patient == null)
				patient =
					new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			
			// Konfiguration: Ohne Überschreiben
			settings.setGlobalSaveRefRange(false);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201204180853"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(
					test,
					"07170", "Harnsäure", "220 - 530", "", "µmol/L", LabItem.typ.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "?", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			file2Import = rscDir + "Test06_2.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			hl7TimeStamp = "201204190855"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(
					test,
					"07170", "Harnsäure", "220 - 530", "", "µmol/L", LabItem.typ.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "?", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			file2Import = rscDir + "Test06_3.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			hl7TimeStamp = "201204200859"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "07170", "Harnsäure", "220 - 530", "", "µmol/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "360", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Prüft, ob die Einstellung zum Überschreiben von neueren durch ältere Laborresultate richtig
	 * angewendet wird.
	 */
	@Test
	public void Test07_ErneuteÜbermittlungFrühererResultate(){
		try {
			// Erneuter Import einer alten Datei (ohne Überschreiben)
			if (settings == null)
				settings = new ViollierLaborImportSettings((CoreHub.actMandant));
			
			String file2Import = rscDir + "Test07_1.HL7"; //$NON-NLS-1$
			File hl7File = new File(file2Import);
			File downloadDir = new File(rscDir);
			AtomicReference<File> pdfFileRef = new AtomicReference<File>();
			SaveResult result;
			String test = "Test07_ErneuteÜbermittlungFrühererResultate"; //$NON-NLS-1$
			
			patient = findExactTestPatientM();
			if (patient == null)
				patient =
					new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			
			// Konfiguration: Ohne Überschreiben
			settings.setGlobalSaveRefRange(false);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201205180800"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "10024", "Pankreas-Amylase", "< 65", "", "U/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "?", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			file2Import = rscDir + "Test07_2.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			hl7TimeStamp = "201205182100"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "10024", "Pankreas-Amylase", "< 65", "", "U/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "55", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			file2Import = rscDir + "Test07_1.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			hl7TimeStamp = "201205182100"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "10024", "Pankreas-Amylase", "< 65", "", "U/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "55", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// Erneuter Import einer alten Datei (mit Überschreiben)
			file2Import = rscDir + "Test07_1.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result = LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings, true, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			hl7TimeStamp = "201205180800"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "10024", "Pankreas-Amylase", "< 65", "", "U/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItem.typ.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "?", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	@Test
	public void Test08_ImportMitVioNummer(){
		try {
			// Erneuter Import einer alten Datei (ohne Überschreiben)
			if (settings == null)
				settings = new ViollierLaborImportSettings((CoreHub.actMandant));
			
			String file2Import = rscDir + "Test08.HL7"; //$NON-NLS-1$
			File hl7File = new File(file2Import);
			File downloadDir = new File(rscDir);
			AtomicReference<File> pdfFileRef = new AtomicReference<File>();
			SaveResult result;
			String test = "Test08_ImportMitVioNummer"; //$NON-NLS-1$
			
			patient = findExactTestPatientM();
			if (patient == null)
				patient =
					new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			
			// Konfiguration: Mit Überschreiben
			settings.setGlobalSaveRefRange(false);
			result = LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings, true, false);
			assertEquals(test + ": Import fehlgeschlagen", result, SaveResult.SUCCESS); //$NON-NLS-1$
			
			String vioNr = LabOrderImport.getVioNr(patient);
			assertEquals(test + ": VioNummer falsch", vioNr, "1779500");
			
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Liefert den Pfad auf das Plugindirectory zurück
	 * 
	 * @return Plugindirectory
	 */
	public static String getPluginDirectory(){
		String filePath = null;
		Bundle bundle = Platform.getBundle("ch.elexis.laborimport.viollier.v2_test"); //$NON-NLS-1$
		if (bundle != null) {
			Path path = new Path("/"); //$NON-NLS-1$
			URL url = FileLocator.find(bundle, path, null);
			
			try {
				filePath = FileLocator.toFileURL(url).getPath();
				filePath = filePath.substring(1);
			} catch (IOException e) {
				ExHandler.handle(e);
			}
		}
		return filePath;
	}
	
	/**
	 * Prüft, ob ein Labortest-Eintrag in Elexis vorhanden ist, der den Parametern entspricht
	 * 
	 * @param test
	 *            Beschreibung des Testfalles
	 * @param kuerzel
	 *            Kurzbezeichnung des Labortests
	 * @param titel
	 *            Bezeichnung des Labortests
	 * @param refMann
	 *            Referenzbereich für Männer
	 * @param refFrau
	 *            Referenzbereich für Frauen
	 * @param einheit
	 *            Einheit
	 * @param typ
	 *            Typ (Text, Zahl, ...)
	 * @param gruppe
	 *            Gruppe
	 * @param prio
	 *            Sequenz
	 * @return Gefundenes LaborItem oder null
	 */
	private LabItem checkLabItem(String test, String kuerzel, String titel, String refMann,
		String refFrau, String einheit, LabItem.typ typ, String gruppe, String prio){
		LabItem item = null;
		Query<LabItem> q = new Query<LabItem>(LabItem.class);
		q.add(LabItem.SHORTNAME, Query.EQUALS, kuerzel);
		List<LabItem> items = q.execute();
		assertEquals(test + ": Falsche Anzahl LabItems", items.size(), 1); //$NON-NLS-1$
		if (items.size() == 1) {
			item = items.get(0);
			assertEquals(test + ": Falscher Wert bei LabItem: Titel", item.getName(), titel); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: RefMann", item.getRefM(), refMann); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: RefFrau", item.getRefW(), refFrau); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: Einheit", item.getEinheit(), einheit); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: Typ", item.getTyp(), typ); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: Gruppe", item.getGroup(), gruppe); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: Prio", item.getPrio(), prio); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: Export", item.getExport(), ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return item;
	}
	
	/**
	 * Prüft, ob ein Laborwert in Elexis vorhanden ist, der den Parametern entspricht
	 * 
	 * @param test
	 *            Beschreibung des Testfalles
	 * @param pat
	 *            Patient, nach dem gesucht werden soll
	 * @param labItem
	 *            Labortest, nach dem gesucht werden soll
	 * @param hl7TimeStamp
	 *            Relevanter Zeitpunkt
	 * @param result
	 *            Eigentlicher Laborwert
	 * @param comment
	 *            Kommentar zum Laborwert
	 */
	private void checkLabWert(String test, Patient pat, LabItem labItem, String hl7TimeStamp,
		String result, String comment){
		while (hl7TimeStamp.length() < 14) {
			hl7TimeStamp += "0"; //$NON-NLS-1$
		}
		Query<LabResult> q = new Query<LabResult>(LabResult.class);
		q.add(LabResult.PATIENT_ID, Query.EQUALS, pat.getId());
		q.add(LabResult.ITEM_ID, Query.EQUALS, labItem.getId());
		q.add(LabResult.DATE, Query.EQUALS, hl7TimeStamp.substring(0, 8));
		q.add(LabResult.TIME, Query.EQUALS, hl7TimeStamp.substring(8, 14));
		List<LabResult> items = q.execute();
		assertEquals(test + ": Falsche Anzahl LabResults", items.size(), 1); //$NON-NLS-1$
		if (items.size() == 1) {
			LabResult item = items.get(0);
			assertEquals(test + ": Falscher Wert bei LabResult: Resultat", item.getResult(), result); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabResult: Kommentar", item.getComment(), //$NON-NLS-1$
				comment);
		}
	}
	
	/**
	 * Sucht den männlichen Testpatienten
	 * 
	 * @return Patient wenn gefunden, sonst null
	 */
	private static Patient findExactTestPatientM(){
		Patient retVal = null;
		List<Patient> patientList =
			LabOrderImport.readPatienten(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT,
				TESTPAT_M_SEX);
		if (patientList.size() == 1)
			retVal = patientList.get(0);
		return retVal;
	}
	
	/**
	 * Sucht die weibliche Testpatientin
	 * 
	 * @return Patient wenn gefunden, sonst null
	 */
	private static Patient findExactTestPatientW(){
		Patient retVal = null;
		List<Patient> patientList =
			LabOrderImport.readPatienten(TESTPAT_F_NAME, TESTPAT_F_VORNAME, TESTPAT_F_GEBDAT,
				TESTPAT_F_SEX);
		if (patientList.size() == 1)
			retVal = patientList.get(0);
		return retVal;
	}
}
