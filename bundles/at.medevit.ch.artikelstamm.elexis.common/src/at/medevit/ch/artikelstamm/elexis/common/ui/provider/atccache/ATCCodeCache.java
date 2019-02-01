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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeService;
import at.medevit.ch.artikelstamm.elexis.common.internal.ATCCodeServiceConsumer;
import at.medevit.ch.artikelstamm.elexis.common.service.ModelServiceHolder;
import at.medevit.ch.artikelstamm.elexis.common.service.VersionUtil;
import ch.elexis.core.model.IBlobSecondary;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.UiDesk;

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
		return NAMED_BLOB_PREFIX + "_" + VersionUtil.getCurrentVersion();
	}
	
	@SuppressWarnings("unchecked")
	private static void deserializeFromDatabase(String id)
		throws IOException, ClassNotFoundException{
		
		Optional<IBlobSecondary> cacheStorage =
			CoreModelServiceHolder.get().load(id, IBlobSecondary.class);
		if (cacheStorage.isPresent()) {
			ByteArrayInputStream ba = new ByteArrayInputStream(cacheStorage.get().getContent());
			ObjectInputStream oba = new ObjectInputStream(ba);
			cache = (HashMap<String, Integer>) oba.readObject();
			oba.close();
			ba.close();
			
		} else {
			ProgressMonitorDialog pmd = new ProgressMonitorDialog(UiDesk.getTopShell());
			try {
				pmd.run(false, false, new IRunnableWithProgress() {
					
					@Override
					public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException{
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
		
		Integer value = cache.get(element.atcCode);
		return (value != null) ? value : 0;
	}
	
	public static void rebuildCache(IProgressMonitor monitor){
		if (atcCodeService == null) {
			return;
		}
		List<ATCCode> allATCCodes = atcCodeService.getAllATCCodes();
		int numberOfATCCodes = allATCCodes.size();
		
		monitor.beginTask("Rebuilding index of available articles per ATC Code",
			numberOfATCCodes + 1);
		
		cache = new HashMap<String, Integer>(numberOfATCCodes);
		
		TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
		String queryString = "SELECT DISTINCT(atc) FROM artikelstamm_ch";
		log.debug("ArtikelstammImporter {} numberOfATCCodes using query {}:", numberOfATCCodes,
			queryString);
		ModelServiceHolder.get().executeNativeQuery(queryString).forEach(o -> {
			if (o instanceof String) {
				String atc = (String) o;
				if (atc != null) {
					if (!tm.containsKey(atc)) {
						tm.put(atc, 0);
					}
					Integer integer = tm.get(atc);
					tm.put(atc, integer + 1);
				}
			}
		});
		
		for (ATCCode atcCode : allATCCodes) {
			int foundElements = 0;
			
			ATCCode next = atcCodeService.getNextInHierarchy(atcCode);
			SortedMap<String, Integer> subMap;
			if (next != null) {
				subMap = tm.subMap(atcCode.atcCode, next.atcCode);
			} else {
				subMap = tm.tailMap(atcCode.atcCode);
			}
			
			for (Iterator<String> a = subMap.keySet().iterator(); a.hasNext();) {
				String val = a.next();
				foundElements += tm.get(val);
			}
			cache.put(atcCode.atcCode, foundElements);
			
			monitor.worked(1);
		}
		
		monitor.subTask("Persisting ATC Code product cache to database");
		// clear old caches
		IQuery<IBlobSecondary> query = ModelServiceHolder.get().getQuery(IBlobSecondary.class);
		query.and(ModelPackage.Literals.IBLOB__DATE, COMPARATOR.LESS, LocalDate.now());
		for (IBlobSecondary oldCache : query.execute()) {
			ModelServiceHolder.get().remove(oldCache);
		}
		
		// serialize the cache
		try {
			String id = determineBlobId();
			IBlobSecondary cacheStorage =
				ModelServiceHolder.get().load(id, IBlobSecondary.class).orElse(null);
			if (cacheStorage == null) {
				cacheStorage = ModelServiceHolder.get().create(IBlobSecondary.class);
				cacheStorage.setId(determineBlobId());
			}
			ByteArrayOutputStream ba = new ByteArrayOutputStream();
			ObjectOutputStream oba = new ObjectOutputStream(ba);
			oba.writeObject(cache);
			oba.close();
			cacheStorage.setContent(ba.toByteArray());
			ba.close();
			ModelServiceHolder.get().save(cacheStorage);
			monitor.worked(1);
		} catch (IOException e) {
			log.error("Error on cache generation", e);
		}
		
		monitor.done();
	}
	
}
