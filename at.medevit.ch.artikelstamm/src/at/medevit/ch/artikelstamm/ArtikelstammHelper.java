/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm;

import java.io.File;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import at.medevit.ch.artikelstamm.ARTIKELSTAMM.ITEM;
import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;

public class ArtikelstammHelper {
	private static Logger log = LoggerFactory.getLogger(ArtikelstammHelper.class);
	
	public static String PHARMA_XSD_LOCATION = "Elexis_Artikelstamm_v001.xsd";
	private static URL schemaLocationUrl = null;
	
	private static SchemaFactory schemaFactory = SchemaFactory
		.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	static DateFormat dateFormat = new SimpleDateFormat("ddMMyy");
	public static DateFormat monthAndYearWritten = new SimpleDateFormat("MMM yyyy");
	
	static {
		try {
			schemaLocationUrl =
				new URL("platform:/plugin/at.medevit.ch.artikelstamm/lib/" + PHARMA_XSD_LOCATION);
		} catch (MalformedURLException e) {
			log.error("Error resolving Artikelstamm schema", e);
		}
	}
	
	/**
	 * 
	 * @param year
	 *            the creation year of the dataset
	 * @param month
	 *            the creation month of the dataset (jan = 1, dec = 12)
	 * @return the cummulated version number
	 */
	public static int getCummulatedVersionNumber(int year, int month){
		return ((year - 2013) * 12) + month;
	}
	
	/**
	 * @param cumulatedVersionNo
	 * @return the date value represented by the given cumulatedVersionNo
	 */
	public static Date getDateFromCumulatedVersionNumber(int cumulatedVersionNo){
		int year = 2013 + (cumulatedVersionNo / 12);
		int month = (cumulatedVersionNo % 12) - 1;
		return new GregorianCalendar(year, month, 1).getTime();
	}
	
	/**
	 * The deterministic id of an {@link ArtikelstammItem} item. Fixed length 25 chars. Assembled as
	 * follows:<br>
	 * <li>Characters 0-13: GTIN, if gtin less 14 chars, left padded with zeros <li>Characters
	 * 14-20: Pharmacode, if Pharmacode less 7 chars, left padded with zeros <li>Character 21: P or
	 * N, depending on {@link ArtikelstammConstants.TYPE} <li>Character 22-24: cummulatedVersion, if
	 * >999 or <0 set to 0 <br>
	 * 
	 * @param cummulatedVersion
	 * @param type
	 * @param gtin
	 * @param phar
	 * @return deterministic uuid of an {@link ARTIKELSTAMM} item
	 */
	public static String createUUID(int cummulatedVersion, TYPE type, String gtin, BigInteger phar){
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%014d", Long.parseLong(gtin)));
		if (phar != null) {
			sb.append(String.format("%07d", phar));
		} else {
			sb.append("0000000");
		}
		sb.append(type.name());
		if (cummulatedVersion > 999 || cummulatedVersion < 0)
			cummulatedVersion = 0;
		sb.append(String.format("%03d", cummulatedVersion));
		return sb.toString();
	}
	
	/**
	 * 
	 * @param xmlFile
	 * @return {@link ARTIKELSTAMM}
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public static ARTIKELSTAMM unmarshallFile(File xmlFile) throws JAXBException, SAXException{
		Unmarshaller u = JAXBContext.newInstance(ARTIKELSTAMM.class).createUnmarshaller();
		Schema schema = schemaFactory.newSchema(schemaLocationUrl);
		u.setSchema(schema);
		return (ARTIKELSTAMM) u.unmarshal(xmlFile);
	}
	
	public static void marshallToFileSystem(Object newData, File outputFile) throws SAXException,
		JAXBException{
		Schema validationSchema = schemaFactory.newSchema(schemaLocationUrl);
		Marshaller m = JAXBContext.newInstance(ARTIKELSTAMM.class).createMarshaller();
		m.setSchema(validationSchema);
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(newData, outputFile);
	}
	
	/**
	 * Determine an output file for a given input dataset and filename, use this to maintain
	 * consistent output file names. Output file is in the same directory as input file
	 * 
	 * @param converted
	 * @param inboundFileObj
	 * @param string
	 * @return
	 */
	public static File determineOutputFileName(ARTIKELSTAMM converted, File inboundFileObj,
		String string){
		TYPE type = ArtikelstammConstants.TYPE.valueOf(converted.getTYPE());
		Date outputDate = converted.getCREATIONDATETIME().toGregorianCalendar().getTime();
		String filename =
			"artikelstamm_" + type.name() + "_" + dateFormat.format(outputDate) + "_" + string
				+ ".xml";
		return new File(inboundFileObj.getParent(), filename);
	}
	
	private static HashMap<String, ITEM> itemPharmacodeCache = null;
	
	/**
	 * WARNING do not change parameter artikelstamm after first calling of this method, cache will
	 * not be re-initialized!
	 * 
	 * @param artikelstamm
	 * @param pharmacode
	 * @return the {@link ITEM} or <code>null</code> if not found
	 */
	public static ITEM getItemInListByPharmacode(ARTIKELSTAMM artikelstamm, String pharmacode){
		if (itemPharmacodeCache == null) {
			if (artikelstamm.getTYPE().equals(ArtikelstammConstants.TYPE.N))
				throw new IllegalArgumentException("Trying to enrich Non-Pharma artikelstamm data");
			
			itemPharmacodeCache = new HashMap<String, ITEM>(artikelstamm.getITEM().size());
			for (ITEM item : artikelstamm.getITEM()) {
				itemPharmacodeCache.put(item.getPHAR().toString(), item);
			}
		}
		if (itemPharmacodeCache.containsKey(pharmacode))
			return itemPharmacodeCache.get(pharmacode);
		return null;
	}
	
	private static HashMap<String, ITEM> itemGTINCache = null;
	
	/**
	 * WARNING do not change parameter artikelstamm after first calling of this method, cache will
	 * not be re-initialized!
	 * 
	 * @param artikelstamm
	 * @param gtin
	 * @return
	 */
	public static ITEM getItemInListByGTIN(ARTIKELSTAMM artikelstamm, String gtin){
		if (itemGTINCache == null) {
			itemGTINCache = new HashMap<String, ITEM>(artikelstamm.getITEM().size());
			for (ITEM item : artikelstamm.getITEM()) {
				itemGTINCache.put(item.getGTIN(), item);
			}
		}
		if (itemGTINCache.containsKey(gtin)) {
			System.out.println("[INFO] Resolved over GTIN " + gtin);
			return itemGTINCache.get(gtin);
		}
		return null;
	}
	
	/**
	 * Returns the SwissmedicNo8 if the article is registered by GTIN for Switzerland ("76") and
	 * Swissmedic ("80").
	 * 
	 * @param item
	 * @return an 8-char-length string with the SwissmedicNo8, if not applicable <code>null</code>
	 */
	public String getSwissmedicNo8ForArtikelstammItem(ARTIKELSTAMM.ITEM item){
		String gtin = item.getGTIN();
		if (gtin != null && gtin.startsWith("7680"))
			return gtin.substring(4, 12);
		return null;
	}
	
}
