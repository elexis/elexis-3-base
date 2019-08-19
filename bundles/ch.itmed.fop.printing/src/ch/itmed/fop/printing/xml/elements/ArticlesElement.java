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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.data.ArticleData;
import ch.itmed.fop.printing.data.ConsultationData;

public final class ArticlesElement {
	public static Element create(Document doc) throws Exception {
		ConsultationData cd = new ConsultationData();
		List<ArticleData> articles = cd.load();

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

}
