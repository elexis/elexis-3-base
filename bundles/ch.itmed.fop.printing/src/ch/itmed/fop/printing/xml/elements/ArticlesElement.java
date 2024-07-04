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

import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IArticleDefaultSignature;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.itmed.fop.printing.data.ArticleData;
import ch.itmed.fop.printing.data.ConsultationData;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.resources.Messages;

public final class ArticlesElement {

	public static Element create(Document doc) throws Exception {
		return create(doc, true);
	}

	public static Element create(Document doc, IArticle article) {
		Element articlesElement = doc.createElement("Articles"); //$NON-NLS-1$
		articlesElement.appendChild(createArticleElement(doc, article));
		return articlesElement;
	}

	public static Element create(Document doc, boolean includeMedication) throws Exception {
		ConsultationData cd = new ConsultationData();
		List<ArticleData> articles = cd.load();
		if (!includeMedication) {
			articles = articles.stream().filter(ad -> !isMedication(ad, cd)).collect(Collectors.toList());
		}
		Element articlesElement = doc.createElement("Articles"); //$NON-NLS-1$
		for (ArticleData articleData : articles) {
			articlesElement.appendChild(createArticleElement(doc, articleData.getArticle()));
		}
		return articlesElement;
	}

	private static Element createArticleElement(Document doc, IArticle article) {
		ArticleData articleData = new ArticleData(article);
		Element articleElement = doc.createElement("Article"); //$NON-NLS-1$
		appendChildWithText(doc, articleElement, "Name", articleData.getName()); //$NON-NLS-1$
		appendChildWithText(doc, articleElement, "Price", articleData.getPrice()); //$NON-NLS-1$
		appendChildWithText(doc, articleElement, "DeliveryDate", articleData.getDeliveryDate()); //$NON-NLS-1$
		Optional<IArticleDefaultSignature> signatureOpt = MedicationServiceHolder.get().getDefaultSignature(article);
		String preferenceKey = PreferenceConstants
				.getDocPreferenceConstant(PreferenceConstants.ARTICLE_MEDIC_LABEL.toString(), 0);
		String printerNameCheck = getPrinterNameFromScopes(preferenceKey);
		if (signatureOpt.isPresent() && printerNameCheck != null && !printerNameCheck.isEmpty()) {
			IArticleDefaultSignature signature = signatureOpt.get();
			String dosageInstructions = signature.getComment();
			if (dosageInstructions != null && !dosageInstructions.isEmpty()) {
				appendChildWithText(doc, articleElement, "DosageInstructions", dosageInstructions); //$NON-NLS-1$
				appendDoseTable(doc, articleElement, signature);
			}
		}
		return articleElement;
	}

	private static void appendDoseTable(Document doc, Element articleElement, IArticleDefaultSignature signature) {
		Element doseTableHeader = doc.createElement("DoseTableHeader"); //$NON-NLS-1$
		appendChildWithText(doc, doseTableHeader, "HeaderItem", Messages.Medication_Dose_Morning); //$NON-NLS-1$
		appendChildWithText(doc, doseTableHeader, "HeaderItem", Messages.Medication_Dose_Midday); //$NON-NLS-1$
		appendChildWithText(doc, doseTableHeader, "HeaderItem", Messages.Medication_Dose_Evening); //$NON-NLS-1$
		appendChildWithText(doc, doseTableHeader, "HeaderItem", Messages.Medication_Dose_Night); //$NON-NLS-1$
		articleElement.appendChild(doseTableHeader);
		Element doseTableBody = doc.createElement("DoseTableBody"); //$NON-NLS-1$
		String[] doses = { signature.getMorning(), signature.getNoon(), signature.getEvening(), signature.getNight() };
		for (String dose : doses) {
			appendChildWithText(doc, doseTableBody, "DoseItem", dose != null ? dose : ""); //$NON-NLS-1$
		}
		articleElement.appendChild(doseTableBody);
	}

	private static void appendChildWithText(Document doc, Element parent, String tagName, String textContent) {
		Element child = doc.createElement(tagName);
		child.appendChild(doc.createTextNode(textContent));
		parent.appendChild(child);
	}

	private static boolean isMedication(ArticleData ad, ConsultationData cd) {
		List<IPrescription> medication = cd.getMedication();
		if (!medication.isEmpty()) {
			return medication.stream().anyMatch(m -> ad.getArticle().getId().equals(m.getArticle().getId()));
		}
		return false;
	}

	private static String getPrinterNameFromScopes(String preferenceKey) {
		IPreferenceStore globalSettingsStore = new ConfigServicePreferenceStore(Scope.GLOBAL);
		String printerName = globalSettingsStore.getString(preferenceKey);
		if (printerName == null || printerName.isEmpty()) {
			IPreferenceStore localSettingsStore = new ConfigServicePreferenceStore(Scope.LOCAL);
			printerName = localSettingsStore.getString(preferenceKey);
		}
		return printerName;
	}
}
