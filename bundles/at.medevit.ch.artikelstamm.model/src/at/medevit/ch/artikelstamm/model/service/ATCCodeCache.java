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
package at.medevit.ch.artikelstamm.model.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeService;
import at.medevit.ch.artikelstamm.ATCCodeCacheService;
import at.medevit.ch.artikelstamm.model.importer.VersionUtil;
import ch.elexis.core.model.IBlobSecondary;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

/**
 * Provide a cache for the number of elements available in the Artikelstamm set
 * for each ATC code.
 *
 */
@Component
public class ATCCodeCache implements ATCCodeCacheService {

	private static HashMap<String, Integer> cache;

	private static Logger log = LoggerFactory.getLogger(ATCCodeCache.class);
	private static final String NAMED_BLOB_PREFIX = "ATC_ARTSTAMM_CACHE"; //$NON-NLS-1$

	private static void initCache() throws IOException, ClassNotFoundException {
		deserializeFromDatabase(determineBlobId());
	}

	@Activate
	public void activate() {
		try {
			initCache();
		} catch (Exception e) {
			log.warn("Error initializing cache failed [" + e.getMessage() + "] activating anyway"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private static String determineBlobId() {
		return NAMED_BLOB_PREFIX + "_" + VersionUtil.getCurrentVersion(); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	private static void deserializeFromDatabase(String id) throws IOException, ClassNotFoundException {

		Optional<IBlobSecondary> cacheStorage = CoreModelServiceHolder.get().load(id, IBlobSecondary.class);
		if (cacheStorage.isPresent()) {
			ByteArrayInputStream ba = new ByteArrayInputStream(cacheStorage.get().getContent());
			ObjectInputStream oba = new ObjectInputStream(ba);
			cache = (HashMap<String, Integer>) oba.readObject();
			oba.close();
			ba.close();
		} else {
			rebuildCache(new NullProgressMonitor());
		}
	}

	/**
	 *
	 * @param element
	 * @return the number of elements found, or -1 in case of any error
	 */
	public int getAvailableArticlesByATCCode(Object element) {
		if (element instanceof ATCCode) {
			if (cache == null) {
				try {
					initCache();
				} catch (IOException | ClassNotFoundException e) {
					log.error("Error initializing cache", e); //$NON-NLS-1$
					cache = null;
					return -1;
				}
			}

			Integer value = cache.get(((ATCCode) element).atcCode);
			return (value != null) ? value : 0;
		}
		return 0;
	}

	public static void rebuildCache(IProgressMonitor monitor) {
		ATCCodeService atcCodeService = AtcCodeServiceHolder.get().orElse(null);
		if (atcCodeService == null) {
			log.error("No ATCCodeService available"); //$NON-NLS-1$
			return;
		} else {
			log.info("Start rebuilding ATCCodeCache ..."); //$NON-NLS-1$
		}
		List<ATCCode> allATCCodes = atcCodeService.getAllATCCodes();
		int numberOfATCCodes = allATCCodes.size();
		monitor.beginTask("Rebuilding index of available articles per ATC Code", numberOfATCCodes + 1);
		cache = new HashMap<String, Integer>(numberOfATCCodes);

		TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
		String queryString = "SELECT DISTINCT(atc) FROM artikelstamm_ch"; //$NON-NLS-1$
		log.debug("ArtikelstammImporter {} numberOfATCCodes using query {}:", numberOfATCCodes, queryString); //$NON-NLS-1$
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
		IQuery<IBlobSecondary> query = CoreModelServiceHolder.get().getQuery(IBlobSecondary.class);
		query.and(ModelPackage.Literals.IBLOB__DATE, COMPARATOR.LESS, LocalDate.now());
		for (IBlobSecondary oldCache : query.execute()) {
			CoreModelServiceHolder.get().remove(oldCache);
		}

		// serialize the cache
		try {
			String id = determineBlobId();
			IBlobSecondary cacheStorage = CoreModelServiceHolder.get().load(id, IBlobSecondary.class).orElse(null);
			if (cacheStorage == null) {
				cacheStorage = CoreModelServiceHolder.get().create(IBlobSecondary.class);
				cacheStorage.setId(determineBlobId());
			}
			ByteArrayOutputStream ba = new ByteArrayOutputStream();
			ObjectOutputStream oba = new ObjectOutputStream(ba);
			oba.writeObject(cache);
			oba.close();
			cacheStorage.setContent(ba.toByteArray());
			ba.close();
			CoreModelServiceHolder.get().save(cacheStorage);
			monitor.worked(1);
		} catch (IOException e) {
			log.error("Error on cache generation", e); //$NON-NLS-1$
		}
		log.info("Rebuilding ATCCodeCache finished"); //$NON-NLS-1$
		monitor.done();
	}
}
