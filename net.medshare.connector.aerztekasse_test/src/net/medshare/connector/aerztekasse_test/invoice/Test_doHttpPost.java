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
package net.medshare.connector.aerztekasse_test.invoice;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.medshare.connector.aerztekasse.data.AerztekasseSettings;
import net.medshare.connector.aerztekasse.invoice.InvoiceOutputter;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Mandant;
import ch.rgw.tools.ExHandler;

public class Test_doHttpPost {
	private static String username = "elexis";
	private static String password = "elexis";
	private static String url = "https://xmlfacture.cdm.smis.ch/Upload/PROGupload.aspx";
	
	private static String filenameOk = "test_ok";
	private static String filenameOk1 = "test_ok1.xml";
	private static String filenameOk2 = "test_ok2.xml";
	private static String filenameFailure = "test_failure.xml";
	
	@Test
	public void testDoHttpPost(){
		try {
			String pluginDir = getPluginDirectory() + "rsc/";
			
			String filepathOk = pluginDir + filenameOk;
			String filepathFailure = pluginDir + filenameFailure;
			
			Mandant mandant = CoreHub.actMandant;
			
			// Allfällige vorbestehende Dateien löschen
			InvoiceOutputter.deleteFile(filepathOk + ".zip");
			InvoiceOutputter.deleteFile(filepathFailure + ".zip");
			InvoiceOutputter.deleteFile(filepathOk + ".html");
			InvoiceOutputter.deleteFile(filepathFailure + ".html");
			
			// Instanz von InvoiceOutputter erstellen und die Einstellungen setzen
			InvoiceOutputter outputter = new InvoiceOutputter();
			if (outputter.settings == null) {
				outputter.settings = new AerztekasseSettings(mandant);
			}
			outputter.settings.setGlobalUsername(username);
			outputter.settings.setGlobalPassword(password);
			outputter.settings.setGlobalUrl(url);
			outputter.settings.setMandantUsingGlobalSettings(true);
			outputter.settings.setMachineUsingGlobalSettings(true);
			
			ArrayList<String> filenames = new ArrayList<String>(3);
			
			// Test 1: korrekte Rechnung übermitteln
			filenames.add(filenameOk1);
			filenames.add(filenameOk2); // Kontrolle ob Übermittlung erfolgreich war
			assertTrue("Http Post schlug fehl", outputter.doHttpPost(
				zipXml(filenames, filepathOk, pluginDir), filepathOk + ".html"));
			assertTrue("Korrekte Rechnung soll erfolgreich Übermittelt werden",
				outputter.transferState);
			
			// Test 2: fehlerhafte Rechnung übermitteln (ZSR Nummer falsch)
			filenames.add(filenameFailure);
			assertTrue(
				"Http Post schlug fehl",
				outputter.doHttpPost(zipXml(filenames, filepathFailure, pluginDir), filepathFailure
					+ ".html"));
			// Kontrolle ob Übermittlung nicht erfolgreich war
			assertTrue("Fehlerhafte Rechnung darf nicht erfolgreich übermittelt werden",
				!outputter.transferState);
			
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage());
		}
	}
	
	public static String getPluginDirectory(){
		String filePath = null;
		Bundle bundle = Platform.getBundle("net.medshare.connector.aerztekasse_test");
		if (bundle != null) {
			Path path = new Path("/");
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
	 * Erstellt aus dem XML ein Zip File
	 * 
	 * @param filenameXml
	 *            Dateiname des XML Files
	 * @param filepathXml
	 *            Vollständiger Dateipfad des XML Files
	 * @return
	 */
	private static String zipXml(ArrayList<String> filenamesXml, String filepathZip,
		String filepathXml){
		
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];
		
		// Name of the ZIP file
		String filePathZip = filepathZip + ".zip"; //$NON-NLS-1$
		try {
			// Delete the ZIP file if it already exists
			deleteFile(filePathZip);
			
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(filePathZip));
			
			for (String filenameXml : filenamesXml) {
				// Compress the file
				FileInputStream in = new FileInputStream(filepathXml + filenameXml);
				
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(filenameXml));
				
				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				// Complete the entry
				out.closeEntry();
				in.close();
				
			}
			
			// Complete the ZIP file
			out.finish();
			out.flush();
			out.close();
		} catch (Exception e) {
			fail("Fehler beim erstelen des Zip Files (" + e.toString() + "): " + e.getMessage());
		}
		
		return filePathZip;
	}
	
	/**
	 * Löscht das angegebene File
	 * 
	 * @param filename
	 */
	private static boolean deleteFile(String filename){
		File f = new File(filename);
		boolean deleted = f.delete();
		return deleted;
	}
}
