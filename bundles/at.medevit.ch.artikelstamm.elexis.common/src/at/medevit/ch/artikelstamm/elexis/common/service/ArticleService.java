package at.medevit.ch.artikelstamm.elexis.common.service;

import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.services.IArticleService;
import ch.elexis.core.services.INamedQuery;

@Component
public class ArticleService implements IArticleService {
	
	@Override
	public Optional<? extends IArticle> findAnyByGTIN(String gtin){
		INamedQuery<IArtikelstammItem> query =
			ModelServiceHolder.get().getNamedQuery(IArtikelstammItem.class, "gtin");
		List<IArtikelstammItem> found =
			query.executeWithParameters(query.getParameterMap("gtin", gtin));
		if (found != null && !found.isEmpty()) {
			if (found.size() > 1) {
				LoggerFactory.getLogger(getClass())
					.warn("Found [" + found.size() + "] articles with GTIN [" + gtin + "]");
			}
			return Optional.of(found.get(0));
		}
		return Optional.empty();
	}
}
