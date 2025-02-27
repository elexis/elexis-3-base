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

package ch.itmed.fop.printing.xml.documents;

import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IArticleDefaultSignature;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.xml.elements.ArticlesElement;
import ch.itmed.fop.printing.xml.elements.MandatorElement;
import ch.itmed.fop.printing.xml.elements.PatientElement;

public class ArticleLabel {

	/**
	 * Creates the XML file and returns it as an InputStream.
	 *
	 * @return The generated XML as an InputStream
	 */
	public static InputStream create() throws Exception {
		return create(true);
	}

	public static InputStream create(boolean includeMedication) throws Exception {
		Document doc = DomDocument.newDocument();

		Element page = PageProperties.setProperties(doc, PreferenceConstants.ARTICLE_LABEL);
		PageProperties.setCurrentDate(page);
		doc.appendChild(page);
		Element patient = PatientElement.create(doc, false);
		page.appendChild(patient);

		Element mandator = MandatorElement.create(doc, null);
		if (mandator != null) {
			page.appendChild(mandator);
		}

		Element articles = ArticlesElement.create(doc, includeMedication);
		page.appendChild(articles);

		return DomDocument.toInputStream(doc);
	}

	public static InputStream create(IArticle article) throws Exception {
		Document doc = DomDocument.newDocument();
		Optional<IArticleDefaultSignature> signatureOpt = ArticlesElement.getDefaultSignature(article);
		Element page = PageProperties.setProperties(doc, PreferenceConstants.ARTICLE_LABEL);
		if (signatureOpt.isPresent()) {
			IArticleDefaultSignature signature = signatureOpt.get();
			String dosageInstructions = signature.getComment();
			String signatureAsDosisString = signature.getSignatureAsDosisString();
			if (StringUtils.isNotBlank(dosageInstructions) || StringUtils.isNotBlank(signatureAsDosisString)) {
				page = PageProperties.setProperties(doc, PreferenceConstants.ARTICLE_MEDIC_LABEL);
				PageProperties.setCurrentDate(page);
			}
		}
		doc.appendChild(page);
		Element patient = PatientElement.create(doc, false);
		page.appendChild(patient);

		Element mandator = MandatorElement.create(doc, null);
		if (mandator != null) {
			page.appendChild(mandator);
		}

		Element articles = ArticlesElement.create(doc, article);
		page.appendChild(articles);

		return DomDocument.toInputStream(doc);
	}
}
