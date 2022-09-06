/*******************************************************************************
 * Copyright (c) 2014-2022 MEDEVIT.
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeService;
import at.medevit.ch.artikelstamm.ATCCodeCacheService;
import at.medevit.ch.artikelstamm.model.importer.VersionUtil;
import ch.elexis.core.model.IBlobSecondary;
import ch.elexis.core.services.IElexisEntityManager;

/**
 * Provide a cache for the number of elements available in the Artikelstamm set
 * for each ATC code.
 *
 */
@Component
public class ATCCodeCache implements ATCCodeCacheService {

	private HashMap<String, Integer> cache;

	private Logger log = LoggerFactory.getLogger(ATCCodeCache.class);

	private void initCache() throws IOException, ClassNotFoundException {
		String blobId = determineBlobId();
		deserializeFromDatabase(blobId);
	}

	@Reference
	private ATCCodeService atcCodeService;

	@Reference
	private IElexisEntityManager elexisEntityManager;

	private String determineBlobId() {
		return ATCCodeCacheUtil.NAMED_BLOB_PREFIX + "_" + new VersionUtil(elexisEntityManager).getCurrentVersion(); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	private void deserializeFromDatabase(String id) throws IOException, ClassNotFoundException {

		Optional<IBlobSecondary> cacheStorage = CoreModelServiceHolder.get().load(id, IBlobSecondary.class);
		if (cacheStorage.isPresent()) {
			try (ByteArrayInputStream ba = new ByteArrayInputStream(cacheStorage.get().getContent());
					ObjectInputStream oba = new ObjectInputStream(ba);) {
				cache = (HashMap<String, Integer>) oba.readObject();
			}
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

	@Override
	public void rebuildCache(IProgressMonitor progressMonitor) {
		cache = new ATCCodeCacheUtil().rebuildCache(elexisEntityManager, atcCodeService, progressMonitor);
	}

}
