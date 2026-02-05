package ch.elexis.regiomed.order.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.regiomed.order.holder.ArtikelstammModelServiceHolder;

public class RegiomedLocalArticleService {

	private static final Logger log = LoggerFactory.getLogger(RegiomedLocalArticleService.class);

	public IArticle findLocalArticle(String ean, int pharmaCode, String prodName) {
		try {
			ICodeElementService codeService = OsgiServiceUtil.getService(ICodeElementService.class).orElse(null);
			if (StringUtils.isNotBlank(ean)) {
				if (codeService != null) {
					Optional<IArticle> article = codeService.findArticleByGtin(ean);
					if (article.isPresent()) {
						return article.get();
					}
				}
				IQuery<IArticle> q = CoreModelServiceHolder.get().getQuery(IArticle.class);
				q.and(ModelPackage.Literals.IARTICLE__GTIN, IQuery.COMPARATOR.EQUALS, ean);
				List<IArticle> results = q.execute();
				if (!results.isEmpty()) {
					return results.get(0);
				}
			}
			if (pharmaCode > 0) {
				String pharmaStr = String.valueOf(pharmaCode);
				try {

					IQuery<IArtikelstammItem> qStamm = ArtikelstammModelServiceHolder.get()
							.getQuery(IArtikelstammItem.class);
					qStamm.and("phar", IQuery.COMPARATOR.EQUALS, pharmaStr);
					List<IArtikelstammItem> results = qStamm.execute();
					if (!results.isEmpty()) {
						return results.get(0);
					}
				} catch (Throwable t) {
					log.debug("Item master item query not possible", t);
				}

			}
		} catch (Exception e) {
			log.debug("Could not find local article for {}", prodName);
		}
		return null;
	}

}
