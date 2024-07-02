package ch.itmed.fop.printing.xml.elements;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IArticleDefaultSignature;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.itmed.fop.printing.data.ArticleData;
import ch.itmed.fop.printing.data.ConsultationData;
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
		if (signatureOpt.isPresent()) {
			IArticleDefaultSignature signature = signatureOpt.get();
			String dosageInstructions = signature.getComment();
			if (!dosageInstructions.isEmpty()) {
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
}
