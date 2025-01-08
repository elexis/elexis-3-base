package at.medevit.ch.artikelstamm.model.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeService;
import at.medevit.ch.artikelstamm.model.importer.VersionUtil;
import ch.elexis.core.model.IBlobSecondary;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;

public class ATCCodeCacheUtil {

	protected static final String NAMED_BLOB_PREFIX = "ATC_ARTSTAMM_CACHE"; //$NON-NLS-1$
	private Logger log = LoggerFactory.getLogger(getClass());

	private String determineBlobId(IElexisEntityManager elexisEntityManager) {
		return NAMED_BLOB_PREFIX + "_" + new VersionUtil(elexisEntityManager).getCurrentVersion(); //$NON-NLS-1$
	}

	public HashMap<String, Integer> rebuildCache(IElexisEntityManager elexisEntityManager,
			ATCCodeService atcCodeService, IProgressMonitor monitor) {
		HashMap<String, Integer> cache = new HashMap<String, Integer>();
		log.info("Start rebuilding ATCCodeCache ..."); //$NON-NLS-1$
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
		monitor.subTask("Persisting ATC Code product cache to database"); //$NON-NLS-1$
		// clear old caches
		IQuery<IBlobSecondary> query = CoreModelServiceHolder.get().getQuery(IBlobSecondary.class);
		query.and("id", COMPARATOR.LIKE, NAMED_BLOB_PREFIX + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		query.and(ModelPackage.Literals.IBLOB__DATE, COMPARATOR.LESS, LocalDate.now());
		for (IBlobSecondary oldCache : query.execute()) {
			CoreModelServiceHolder.get().remove(oldCache);
		}

		// serialize the cache
		try {
			String id = determineBlobId(elexisEntityManager);
			IBlobSecondary cacheStorage = CoreModelServiceHolder.get().load(id, IBlobSecondary.class).orElse(null);
			if (cacheStorage == null) {
				cacheStorage = CoreModelServiceHolder.get().create(IBlobSecondary.class);
				cacheStorage.setId(determineBlobId(elexisEntityManager));
				cacheStorage.setDate(LocalDate.now());
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
		return cache;
	}
}
