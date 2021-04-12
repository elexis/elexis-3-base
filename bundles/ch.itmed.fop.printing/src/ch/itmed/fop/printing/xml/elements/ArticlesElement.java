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

import ch.elexis.data.Prescription;
import ch.itmed.fop.printing.data.ArticleData;
import ch.itmed.fop.printing.data.ConsultationData;

public final class ArticlesElement {
	public static Element create(Document doc) throws Exception {
		return create(doc, true);
	}
	
	public static Element create(Document doc, boolean includeMedication) throws Exception{
		ConsultationData cd = new ConsultationData();
		List<ArticleData> articles = cd.load();
		
		if (!includeMedication) {
			articles =
				articles.stream().filter(ad -> !isMedication(ad, cd))
					.collect(Collectors.toList());
		}

		Element p = doc.createElement("Articles");

		for (ArticleData a : articles) {
			Element article = doc.createElement("Article");

			Element name = doc.createElement("Name");
			name.appendChild(doc.createTextNode(a.getName()));
			article.appendChild(name);

			Element price = doc.createElement("Price");
			price.appendChild(doc.createTextNode(a.getPrice()));
			article.appendChild(price);

			Element delivery = doc.createElement("DeliveryDate");
			delivery.appendChild(doc.createTextNode(a.getDeliveryDate()));
			article.appendChild(delivery);

			p.appendChild(article);
		}

		return p;
	}
	
	private static boolean isMedication(ArticleData ad, ConsultationData cd){
		List<Prescription> medication = cd.getMedication();
		if (!medication.isEmpty()) {
			Optional<Prescription> found = medication.stream()
				.filter(m -> ad.getArticle().getId().equals(m.getArtikel().getId())).findFirst();
			if (found.isPresent()) {
				return true;
			}
		}
		return false;
	}
}
