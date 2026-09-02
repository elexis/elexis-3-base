package at.medevit.elexis.emediplan.core;

import java.util.List;
import java.util.Optional;

import ch.elexis.core.model.IArticle;

/**
 * Provides details of an article that are not part of the local article data,
 * the picture of the dose form and the unit used for prescribing. Implementing
 * this service requires access to an article information provider, and is
 * therefore optional. If no implementation is available, the eMediplan is
 * printed without pictures and without units.
 */
public interface IArticleDetailService {

	/**
	 * Get the picture of the dose form of the article as JPEG.
	 *
	 * @param article
	 * @return the picture, or empty if the article has none
	 */
	public Optional<byte[]> getImage(IArticle article);

	/**
	 * Get the unit the article is prescribed in, e.g. Stk.
	 *
	 * @param article
	 * @return the unit, or empty if it is not known
	 */
	public Optional<String> getPrescriptionUnit(IArticle article);

	/**
	 * Get the substances the article contains, printed below its name on the
	 * eMediplan.
	 *
	 * @param article
	 * @return the substances, empty if they are not known
	 */
	public List<String> getSubstances(IArticle article);

	/**
	 * Load the details of the articles into the cache of the implementation.
	 * Called once before a series of {@link #getImage(IArticle)} and
	 * {@link #getPrescriptionUnit(IArticle)} calls, so the details do not have to
	 * be loaded one after the other.
	 *
	 * @param articles
	 */
	public default void loadDetails(List<IArticle> articles) {
		// optional operation
	}
}
