package at.medevit.ch.artikelstamm;

public interface ATCCodeCacheService {
	
	/**
	 * 
	 * @param element
	 * @return the number of elements found, or -1 in case of any error
	 */
	public int getAvailableArticlesByATCCode(Object element);
}
