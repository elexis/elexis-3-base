package at.medevit.ch.artikelstamm.elexis.common.service;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.model.article.IArticle;
import ch.elexis.core.services.IArticleService;

@Component
public class ArticleService implements IArticleService {
	
	@Override
	public Optional<? extends IArticle> findAnyByGTIN(String gtin){
		ArtikelstammItem artikelstammItem = ArtikelstammItem.findByEANorGTIN(gtin);
		if (artikelstammItem != null) {
			return Optional.of(artikelstammItem);
		}
		return Optional.empty();
	}
}
