/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui.provider.atccache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.NamedBlob2;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.TimeTool;
import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeService;
import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;
import at.medevit.ch.artikelstamm.elexis.common.internal.ATCCodeServiceConsumer;

/**
 * Provide a cache for the number of elements available in the Artikelstamm set for each ATC code.
 * 
 */
public class ATCCodeCache {
	
	private static ATCCodeService atcCodeService = ATCCodeServiceConsumer.getATCCodeService();
	private static HashMap<String, Integer> cache;
	
	private static Logger log = LoggerFactory.getLogger(ATCCodeCache.class);
	private static final String NAMED_BLOB_PREFIX = "ATC_ARTSTAMM_CACHE";
	
	private static void initCache() throws IOException, ClassNotFoundException{
		deserializeFromDatabase(determineBlobId());
	}
	
	private static String determineBlobId(){
		return NAMED_BLOB_PREFIX + "_P" + ArtikelstammItem.getImportSetCumulatedVersion(TYPE.P)
			+ "_N" + ArtikelstammItem.getImportSetCumulatedVersion(TYPE.N);
	}
	
	@SuppressWarnings("unchecked")
	private static void deserializeFromDatabase(String id) throws IOException,
		ClassNotFoundException{
		
		NamedBlob2 cacheStorage = NamedBlob2.load(id);
		if (cacheStorage != null) {
			ByteArrayInputStream ba = new ByteArrayInputStream(cacheStorage.getBytes());
			ObjectInputStream oba = new ObjectInputStream(ba);
			cache = (HashMap<String, Integer>) oba.readObject();
			oba.close();
			ba.close();
			
		} else {
			ProgressMonitorDialog pmd = new ProgressMonitorDialog(UiDesk.getTopShell());
			try {
				pmd.run(false, false, new IRunnableWithProgress() {
					
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException{
						rebuildCache(monitor);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param element
	 * @return the number of elements found, or -1 in case of any error
	 */
	public static int getAvailableArticlesByATCCode(ATCCode element){
		if (cache == null) {
			try {
				initCache();
			} catch (IOException | ClassNotFoundException e) {
				log.error("Error initializing cache", e);
				cache = null;
				return -1;
			}
		}
		return cache.get(element.atcCode);
	}
	
	public static void rebuildCache(IProgressMonitor monitor){
		List<ATCCode> allATCCodes = atcCodeService.getAllATCCodes();
		int numberOfATCCodes = allATCCodes.size();
		
		monitor.beginTask("Rebuilding index of available articles per ATC Code", numberOfATCCodes+1);
		
		cache = new HashMap<String, Integer>(numberOfATCCodes);
		
		for (ATCCode atcCode : allATCCodes) {
			String query =
				"SELECT COUNT(*) FROM " + ArtikelstammItem.TABLENAME + " WHERE "
					+ ArtikelstammItem.FLD_ATC + " " + Query.LIKE + " "
					+ JdbcLink.wrap(atcCode.atcCode + "%");
			
			int foundElements = PersistentObject.getConnection().queryInt(query);
			cache.put(atcCode.atcCode, foundElements);
			monitor.worked(1);
		}
		
		monitor.subTask("Persisting cache to database");
		// clear old caches
		NamedBlob2.cleanup(NAMED_BLOB_PREFIX, new TimeTool());
		
		// serialize the cache		
		try {
			NamedBlob2 cacheStorage = NamedBlob2.create(determineBlobId(), false);
			ByteArrayOutputStream ba = new ByteArrayOutputStream();
			ObjectOutputStream oba = new ObjectOutputStream(ba);
			oba.writeObject(cache);
			oba.close();
			cacheStorage.putBytes(ba.toByteArray());
			ba.close();
			monitor.worked(1);
		} catch (IOException e) {
			log.error("Error on cache generation", e);
		}
		
		monitor.done();
	}
	
}
