/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.xml.elements;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IPrescription;
import ch.itmed.fop.printing.data.ArticleData;
import ch.itmed.fop.printing.data.ConsultationData;

public final class ArticlesElement {
	public static Element create(Document doc) throws Exception {
		return create(doc, true);
	}

	public static Element create(Document doc, IArticle a) {
		ArticleData articleData = new ArticleData(a);
		Element p = doc.createElement("Articles"); //$NON-NLS-1$

		Element article = doc.createElement("Article"); //$NON-NLS-1$

		Element name = doc.createElement("Name"); //$NON-NLS-1$
		name.appendChild(doc.createTextNode(articleData.getName()));
		article.appendChild(name);

		Element price = doc.createElement("Price"); //$NON-NLS-1$
		price.appendChild(doc.createTextNode(articleData.getPrice()));
		article.appendChild(price);

		Element delivery = doc.createElement("DeliveryDate"); //$NON-NLS-1$
		delivery.appendChild(doc.createTextNode(articleData.getDeliveryDate()));
		article.appendChild(delivery);

		p.appendChild(article);

		return p;
	}

	public static Element create(Document doc, boolean includeMedication) throws Exception {
		ConsultationData cd = new ConsultationData();
		List<ArticleData> articles = cd.load();

		if (!includeMedication) {
			articles = articles.stream().filter(ad -> !isMedication(ad, cd)).collect(Collectors.toList());
		}

		Element p = doc.createElement("Articles"); //$NON-NLS-1$

		for (ArticleData a : articles) {
			Element article = doc.createElement("Article"); //$NON-NLS-1$

			Element name = doc.createElement("Name"); //$NON-NLS-1$
			name.appendChild(doc.createTextNode(a.getName()));
			article.appendChild(name);

			Element price = doc.createElement("Price"); //$NON-NLS-1$
			price.appendChild(doc.createTextNode(a.getPrice()));
			article.appendChild(price);

			Element delivery = doc.createElement("DeliveryDate"); //$NON-NLS-1$
			delivery.appendChild(doc.createTextNode(a.getDeliveryDate()));
			article.appendChild(delivery);

			p.appendChild(article);
		}

		return p;
	}

	private static boolean isMedication(ArticleData ad, ConsultationData cd) {
		List<IPrescription> medication = cd.getMedication();
		if (!medication.isEmpty()) {
			Optional<IPrescription> found = medication.stream()
					.filter(m -> ad.getArticle().getId().equals(m.getArticle().getId())).findFirst();
			if (found.isPresent()) {
				return true;
			}
		}
		return false;
	}
}
