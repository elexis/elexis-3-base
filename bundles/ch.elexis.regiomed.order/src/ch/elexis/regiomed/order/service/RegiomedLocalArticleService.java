package ch.elexis.regiomed.order.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.holder.CodeElementServiceHolder;

public class RegiomedLocalArticleService {

	private static final Logger log = LoggerFactory.getLogger(RegiomedLocalArticleService.class);

	public IArticle findLocalArticle(String ean, int pharmaCode, String prodName) {
		if (StringUtils.isNotBlank(ean)) {
			IArticle article = findArticleByCode(ean);
			if (article != null) {
				return article;
			}
		}
		if (pharmaCode > 0) {
			IArticle article = findArticleByCode(String.valueOf(pharmaCode));

			if (article != null) {
				return article;
			}
		}
		return null;
	}

	private IArticle findArticleByCode(String code) {
		try {
			List<ICodeElementServiceContribution> articleContributions = CodeElementServiceHolder.get()
					.getContributionsByTyp(CodeElementTyp.ARTICLE);
			for (ICodeElementServiceContribution contribution : articleContributions) {
				Optional<ICodeElement> loadFromCode = contribution.loadFromCode(code);
				if (loadFromCode.isPresent()) {
					ICodeElement element = loadFromCode.get();
					if (element instanceof IArticle) {
						return (IArticle) element;
					}
				}
			}
		} catch (Exception e) {
			log.error("Error resolving article by code {}", code, e);
		}
		return null;
	}

}