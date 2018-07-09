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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.Bundle;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.labor.viollier.v2.data.ViollierLaborImportSettings;
import ch.elexis.labor.viollier.v2.labimport.LabOrderImport;
import ch.elexis.labor.viollier.v2.labimport.LabOrderImport.SaveResult;
import ch.elexis.labor.viollier.v2.labimport.PatientLabor;
import ch.rgw.tools.ExHandler;

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
	
	@After
	public void teardown() throws Exception{
		PlatformUI.getWorkbench().saveAllEditors(false); // do not confirm saving
		PlatformUI.getWorkbench().saveAll(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), PlatformUI.getWorkbench().getActiveWorkbenchWindow(), null, false);
		if (PlatformUI.getWorkbench() != null) // null if run from Eclipse-IDE
		{
			// needed if run as surefire test from using mvn install
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllPerspectives(false, true);
			} catch (Exception e) {

				System.out.println(e.getMessage());
			}
		}
	}
	
	@BeforeClass
	public static void beforeClass() {
		LabOrderImport.setTestMode(true);
	}
	
	@Before
	public void before(){
		 List<LabResult> execute = new Query<LabResult>(LabResult.class).execute();
		 for (LabResult labResult : execute) {
			labResult.delete();
		}
	}

	/**
	 * Prüft, ob das vorhandene PDF auch tatsächlich in Omnivore abgelegt wird
	 */
	@Test
	@Ignore("TODO add omnivore to surefire dependencies")
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
			
			Assert.isNotNull(dm);

			// Zunächst ein File importieren, ohne den Patienten dazu erfasst zu haben
			// -> muss fehlschlagen
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen ", SaveResult.ERROR, result); //$NON-NLS-1$
			
			// Dann den Patienten erfassen und das File nochmals importieren
			// -> muss erfolgreich sein
			patient =
				new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			boolean ok = false;
			if (result == SaveResult.SUCCESS) {
				ok = true;
			}
			assertEquals(test + ": Import fehlgeschlagen", true, ok); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201202151448"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", LabItemTyp.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp,
				"01104450159201202151448.pdf", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			hl7TimeStamp = "201004010835"; //$NON-NLS-1$
			item =
				checkLabItem(test, "07200", "Proteine gesamt", "g/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "61", "", "60 - 80", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35024", "Alpha-1-Globulin", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "4.7", "", "2.7 - 5.4", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35026", "Alpha-2-Globulin", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "9.9", "", "6.5 - 13.2", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35030", "Gamma-Globulin", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "11.7", "", "9.6 - 17.5", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35036", "Beta-1-Globulin", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "5.7", "", "4.6 - 6.9", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "35037", "Beta-2-Globulin", "%", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "4.1", "", "2.8 - 5.9", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "MIKROBIOLOG1", "Mikrobiologie", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(
				test,
				patient,
				item,
				hl7TimeStamp,
				"text", //$NON-NLS-1$
				"01.04.2010 08:15\n" + 
				"Urin: Mittelstra\n" + 
				"\n" +
				"Gesamtkeimzahl: 1.000.000 cfu/mL\n" + 
				"Aerob:  1.                 koagulasenegative Staphylokokken\n" + 
				"                               1 . \n" + 
				"                               -   \n" + 
				"  Penicillin                 . S .  \n" + 
				"  Amoxicillin                . S .  \n" + 
				"  Oxacillin                  . S .  \n" + 
				"  Co-Amoxiclav               . S .  \n" + 
				"  Cefalotin                  . S .  \n" + 
				"  Cefuroxim                  . S .  \n" + 
				"  Ceftriaxon                 . S .  \n" + 
				"  Erythromycin               . S .  \n" + 
				"  Clindamycin                . S .  \n" + 
				"  Gentamicin                 . S .  \n" + 
				"  Tobramycin                 . S .  \n" + 
				"  Tetracyclin                . S .  \n" + 
				"  Co-trimoxazol              . S .  \n" + 
				"  Nitrofurantoin             . S .  \n" + 
				"  Ciprofloxacin              . S .  \n" + 
				"  Norfloxacin                . S .  \n" + 
				"  Levofloxacin               . S .  \n" + 
				"  Moxifloxacin               . S .  \n" + 
				"  Vancomycin                 . S .  \n" + 
				"  Teicoplanin                . S .  \n" + 
				"  Rifampicin                 . S .  \n" + 
				"  Fusidinsäure               . S .  \n" + 
				"  Fosfomycin                 . S .  \n" + 
				"  Linezolid                  . S .  \n" + 
				"\n" + 
				"\n", 
				"", 
				""); //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(
					test,
					"doc", //$NON-NLS-1$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameDokumentLaborParameter,
					"pdf", LabItemTyp.DOCUMENT, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			
			// omnivore kontrollieren
			List<IOpaqueDocument> items = dm.listDocuments(patient, null, null, null, null, null);
			assertEquals(test + ": Falsche Anzahl Dokumenten", 1, items.size()); //$NON-NLS-1$
			if (items.size() == 1) {
				IOpaqueDocument doc = items.get(0);
				String cat = TEST_CATEGORY;
				// Kategorien sind nur bei Omnivore plus oder Omnivore direct, aber nicht bei
				// Omnivore unterstützt
				if (dm.getCategories() == null)
					cat = "";
				assertEquals(test + ": Falscher Wert bei Dokument: Kategorie", doc.getCategory(), //$NON-NLS-1$
					cat);
				assertEquals(
					test + ": Falscher Wert bei Dokument: Keywords", "1_1_0002834_3_20120215145502_01105727278_20100401_Testpatient_19121213_1234_12004.pdf", doc.getKeywords()); //$NON-NLS-1$
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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
			assertEquals(test + ": Import fehlgeschlagen", SaveResult.SUCCESS, result); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "20120420082827"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item = checkLabItem(test, "kommentar", "Kommentar", "", LabItemTyp.TEXT, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"00 Kommentar", "0"); //$NON-NLS-1$
			checkLabWert(
				test,
				patient,
				item,
				hl7TimeStamp,
				"text", //$NON-NLS-1$
				"-> Die Namensbeschriftung auf dem Auftragsformular und dem\\.br\\Untersuchungsmaterial war nicht korrekt.\\.br\\Gemäss telefonischer Rücksprache ist der Auftrag\\.br\\mit folgendem Namen falsch beschriftet: Testpatient Odie\\.br\\Die Korrektur wurde via Fax bestätigt.", "", ""); //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			hl7TimeStamp = "201204200828"; //$NON-NLS-1$
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", LabItemTyp.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp,
				"01100019516201204200828.pdf", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			hl7TimeStamp = "201204200820"; //$NON-NLS-1$
			item =
				checkLabItem(test, "23000", "Natrium", "mmol/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "145", //$NON-NLS-1$
				"-> Resultat mit Vorbehalt, da zu wenig Untersuchungsmaterial \\.br\\vorhanden war.", "135 - 147", ""); //$NON-NLS-1$
			
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
			assertEquals(test + ": Import fehlgeschlagen", SaveResult.SUCCESS, result); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201205111018"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", LabItemTyp.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp,
				"01100020737201205111018.pdf", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			hl7TimeStamp = "201205110851";
			item =
				checkLabItem(test, "MIKROBIOLOG1", "Mikrobiologie", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(
				test,
				patient,
				item,
				hl7TimeStamp,
				"text", //$NON-NLS-1$
				"10.05.2012 10:15\n" + 
				"Biopsie vs: Hoden\n" + 
				"\n" + 
				"Aerob: nach Anreicherung: \n" + 
				" 1.                 Escherichia coli\n" + 
				"                               1 . \n" + 
				"                               -   \n" + 
				"  Amoxicillin                . R .  \n" + 
				"  Co-Amoxiclav               . S .  \n" + 
				"  Piperacillin/Tazobactam    . S .  \n" + 
				"  Cefalotin                  . I .  \n" + 
				"  Cefuroxim                  . S .  \n" + 
				"  Cefuroxim Axetil           . S .  \n" + 
				"  Cefpodoxim                 . S .  \n" + 
				"  Ceftazidim                 . S .  \n" + 
				"  Ceftriaxon                 . S .  \n" + 
				"  Cefepim                    . S .  \n" + 
				"  Imipenem                   . S .  \n" + 
				"  Meropenem                  . S .  \n" + 
				"  Amikacin                   . S .  \n" + 
				"  Gentamicin                 . S .  \n" + 
				"  Tobramycin                 . S .  \n" + 
				"  Co-trimoxazol              . R .  \n" + 
				"  Ciprofloxacin              . S .  \n" + 
				"\n" + 
				"Anaerob: kein Wachstum\n" + 
				"Direktpräparat: kein Nachweis von säurefesten Stäbchen \n" + 
				"-> Resultat mit Vorbehalt, da zu wenig Untersuchungsmaterial \n" + 
				"vorhanden war.\n" + 
				"Kultur: +               säurefeste Stäbchen \n" + 
				"genaue Identifizierung: \n" + 
				" 1.                 Mycobacterium tuberculosis\n" + 
				"                               1 . \n" + 
				"                               -   \n" + 
				"  Ethambutol 5.0 mg/L        . S .  \n" + 
				"  Isoniazid 0.1 mg/L         . S .  \n" + 
				"  Pyrazinamid 100 mg/L       . S .  \n" + 
				"  Rifampicin 1.0 mg/L        . S .  \n" + 
				"  Streptomycin 1.0 mg/L      . S .  \n" + 
				"\n" + 
				"Epithelien: 0\n" + 
				"Leukozyten: 0\n" + 
				"Grampositive Stäbchen: 0\n" + 
				"Grampositive Kokken: 0\n" + 
				"Gramnegative Stäbchen: 0\n" + 
				"Gramnegative Diplokokken: 0\n" + 
				"Sprosspilze: 0\n" + 
				"\n" + 
				"",
				"", "");
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "MIKROBIOLOG2", "Mikrobiologie", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient,
				item,
				hl7TimeStamp,
				"text", //$NON-NLS-1$
				null,
				"", "");
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "MIKROBIOLOG3", "Mikrobiologie", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(
				test,
				patient,
				item,
				hl7TimeStamp,
				"text", //$NON-NLS-1$
				null,
				"", "");
			
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
			assertEquals(test + ": Import fehlgeschlagen", SaveResult.SUCCESS, result); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201205101059"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", LabItemTyp.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp,
				"01100019516201205101059.pdf", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			hl7TimeStamp = "201205101049"; //$NON-NLS-1$
			item =
				checkLabItem(test, "22751", "Hämoglobin", "g/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "150", "", "135 - 175", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// Weiblicher Testpatient erstellen
			patient =
				new Patient(TESTPAT_F_NAME, TESTPAT_F_VORNAME, TESTPAT_F_GEBDAT, TESTPAT_F_SEX);
			
			file2Import = rscDir + "Test04_w.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			assertEquals(test + ": Import fehlgeschlagen", SaveResult.SUCCESS, result); //$NON-NLS-1$
			
			hl7TimeStamp = "201205101103"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "REPPDF", "PDF Report", "", LabItemTyp.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp,
				"01100020726201205101103.pdf", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// --------------------------------------------------------------------------------
			hl7TimeStamp = "201205101102"; //$NON-NLS-1$
			item =
				checkLabItem(test, "22751", "Hämoglobin", "g/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "130", "", "", "120 - 160"); //$NON-NLS-1$ //$NON-NLS-2$
			
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
			
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			
			assertEquals(test + ": Import fehlgeschlagen", SaveResult.SUCCESS, result); //$NON-NLS-1$

			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201205110817"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "11700", "Paracetamol", "µmol/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "66.0", //$NON-NLS-1$
				"-> Diese Analyse wird in einem externen Labor durchgeführt.\ntoxisch> 790", "65 - 130", ""); //$NON-NLS-1$
			
			file2Import = rscDir + "Test05_2.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);

			assertEquals(test + ": Import fehlgeschlagen", SaveResult.SUCCESS, result); //$NON-NLS-1$
			
			hl7TimeStamp = "201205110831"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "11700", "Paracetamol", "µmol/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "72.6", //$NON-NLS-1$
				"-> Diese Analyse wird in einem externen Labor durchgeführt.\ntoxisch> 790", "70 - 130", ""); //$NON-NLS-1$
			
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
			boolean ok = false;
			
			patient = findExactTestPatientM();
			if (patient == null)
				patient =
					new Patient(TESTPAT_M_NAME, TESTPAT_M_VORNAME, TESTPAT_M_GEBDAT, TESTPAT_M_SEX);
			
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			ok = false;
			if (result.equals(SaveResult.SUCCESS)) {
				ok = true;
			}
			assertEquals(test + ": Import fehlgeschlagen", true, ok); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201204200849"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(
					test,
 "07170", "Harnsäure", "µmol/L", LabItemTyp.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "?", "", "220 - 530", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			file2Import = rscDir + "Test06_2.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			ok = false;
			if (result.equals(SaveResult.SUCCESS)) {
				ok = true;
			}
			assertEquals(test + ": Import fehlgeschlagen", true, ok); //$NON-NLS-1$
			
			hl7TimeStamp = "201204200849"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "07170", "Harnsäure", "µmol/L", LabItemTyp.NUMERIC, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "?", "", "220 - 530", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			file2Import = rscDir + "Test06_3.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			ok = false;
			if (result.equals(SaveResult.SUCCESS)) {
				ok = true;
			}
			assertEquals(test + ": Import fehlgeschlagen", true, ok); //$NON-NLS-1$
			
			hl7TimeStamp = "201204200849"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "07170", "Harnsäure", "µmol/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "360", "", "220 - 530", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
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
			
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			boolean ok = false;
			if (result.equals(SaveResult.SUCCESS)) {
				ok = true;
			}
			assertEquals(test + ": Import fehlgeschlagen", true, ok); //$NON-NLS-1$
			
			// Laboritems und LaborWerte kontrollieren
			LabItem item;
			String hl7TimeStamp = "201205181720"; //$NON-NLS-1$
			
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "10024", "Pankreas-Amylase", "U/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "?", "", "< 65", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			file2Import = rscDir + "Test07_2.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			ok = false;
			if (result.equals(SaveResult.SUCCESS)) {
				ok = true;
			}
			assertEquals(test + ": Import fehlgeschlagen", true, ok); //$NON-NLS-1$
			
			hl7TimeStamp = "201205181720"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "10024", "Pankreas-Amylase", "U/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "55", "", "< 65", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			file2Import = rscDir + "Test07_1.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result =
				LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings,
					overwriteOlderEntries, false);
			ok = false;
			if (result.equals(SaveResult.SUCCESS)) {
				ok = true;
			}
			assertEquals(test + ": Import fehlgeschlagen", true, ok); //$NON-NLS-1$
			
			hl7TimeStamp = "201205181720"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "10024", "Pankreas-Amylase", "U/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "?", "", "< 65", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// Erneuter Import einer alten Datei (mit Überschreiben)
			file2Import = rscDir + "Test07_1.HL7"; //$NON-NLS-1$
			hl7File = new File(file2Import);
			result = LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings, true, false);
			ok = false;
			if (result.equals(SaveResult.SUCCESS)) {
				ok = true;
			}
			assertEquals(test + ": Import fehlgeschlagen", true, ok); //$NON-NLS-1$
			
			hl7TimeStamp = "201205181720"; //$NON-NLS-1$
			// --------------------------------------------------------------------------------
			item =
				checkLabItem(test, "10024", "Pankreas-Amylase", "U/L", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					LabItemTyp.NUMERIC,
					ch.elexis.labor.viollier.v2.Messages.PatientLabor_nameViollierLabor,
					PatientLabor.DEFAULT_PRIO);
			checkLabWert(test, patient, item, hl7TimeStamp, "?", "", "< 65", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
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
			
			result = LabOrderImport.doImportOneFile(hl7File, pdfFileRef, settings, true, false);
			assertEquals(test + ": Import fehlgeschlagen", SaveResult.SUCCESS, result); //$NON-NLS-1$
			
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
				// filePath = filePath.substring(1);
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
	private LabItem checkLabItem(String test, String kuerzel, String titel, String einheit,
		LabItemTyp typ, String gruppe, String prio){
		LabItem item = null;
		Query<LabItem> q = new Query<LabItem>(LabItem.class);
		q.add(LabItem.TITLE, Query.EQUALS, titel);
		q.add(LabItem.SHORTNAME, Query.EQUALS, kuerzel);
		List<LabItem> items = q.execute();
		assertEquals(test + ": Falsche Anzahl LabItems", 1, items.size()); //$NON-NLS-1$
		if (items.size() == 1) {
			item = items.get(0);
			assertEquals(test + ": Falscher Wert bei LabItem: Titel", titel, item.getName()); //$NON-NLS-1$
			// reference values are at LabResult since Elexis 3.0
			//			assertEquals(test + ": Falscher Wert bei LabItem: RefMann", refMann, item.getRefM()); //$NON-NLS-1$
			//			assertEquals(test + ": Falscher Wert bei LabItem: RefFrau", refFrau, item.getRefW()); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: Einheit", einheit, item.getEinheit()); //$NON-NLS-1$
			// skip typ test ... viollier sends everything as ST and expected is Numeric
			// assertEquals(test + ": Falscher Wert bei LabItem: Typ", typ, item.getTyp()); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: Gruppe", gruppe, item.getGroup()); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: Prio", prio, item.getPrio()); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabItem: Export", "", item.getExport()); //$NON-NLS-1$ //$NON-NLS-2$
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
	 *            Kommentar zum Laborwert (tested only if != null)
	 */
	private void checkLabWert(String test, Patient pat, LabItem labItem, String hl7TimeStamp,
		String result, String comment, String refM, String refW){
		while (hl7TimeStamp.length() < 14) {
			hl7TimeStamp += "0"; //$NON-NLS-1$
		}
		Query<LabResult> q = new Query<LabResult>(LabResult.class);
		q.add(LabResult.PATIENT_ID, Query.EQUALS, pat.getId());
		q.add(LabResult.ITEM_ID, Query.EQUALS, labItem.getId());
		q.add(LabResult.ANALYSETIME, Query.EQUALS, hl7TimeStamp);
		q.or();
		q.add(LabResult.OBSERVATIONTIME, Query.EQUALS, hl7TimeStamp);
		List<LabResult> items = q.execute();
		assertEquals(test + ": Falsche Anzahl LabResults", 1, items.size()); //$NON-NLS-1$
		if (items.size() == 1) {
			LabResult item = items.get(0);
			assertEquals(test + ": Falscher Wert bei LabResult: Resultat", result, item.getResult()); //$NON-NLS-1$
			// Behaviour changed in 3.1 - see #3618
			assertEquals(
				test + ": Falscher Wert bei LabResult: ref female", refW, item.get(LabResult.REFFEMALE)); //$NON-NLS-1$
			assertEquals(test + ": Falscher Wert bei LabResult: ref male", refM, item.get(LabResult.REFMALE)); //$NON-NLS-1$
			if(comment != null) {
				assertEquals(test + ": Falscher Wert bei LabResult: Kommentar", comment,
						item.getComment());
			}
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
