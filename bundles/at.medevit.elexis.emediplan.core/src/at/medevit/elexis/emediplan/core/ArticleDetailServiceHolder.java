package at.medevit.elexis.emediplan.core;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component
public class ArticleDetailServiceHolder {

	private static IArticleDetailService articleDetailService;

	public ArticleDetailServiceHolder() {
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
	public void setReference(IArticleDetailService articleDetailService) {
		ArticleDetailServiceHolder.articleDetailService = articleDetailService;
	}

	public void unsetReference(IArticleDetailService articleDetailService) {
		if (ArticleDetailServiceHolder.articleDetailService == articleDetailService) {
			ArticleDetailServiceHolder.articleDetailService = null;
		}
	}

	public static Optional<IArticleDetailService> getService() {
		return Optional.ofNullable(articleDetailService);
	}
}
