package at.medevit.ch.artikelstamm;

import org.eclipse.core.runtime.IProgressMonitor;

public interface ATCCodeCacheService {

	public void rebuildCache(IProgressMonitor progressMonitor);

	/**
	 *
	 * @param element
	 * @return the number of elements found, or -1 in case of any error
	 */
	public int getAvailableArticlesByATCCode(Object element);
}
