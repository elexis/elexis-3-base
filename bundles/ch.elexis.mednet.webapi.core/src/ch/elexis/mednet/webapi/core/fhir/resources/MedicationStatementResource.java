package ch.elexis.mednet.webapi.core.fhir.resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Reference;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;
import ch.elexis.mednet.webapi.core.service.ArtikelstammModelServiceHolder;

public class MedicationStatementResource {

	public List<MedicationStatement> createMedicationStatementsFromPrescriptions(Reference patientReference,
			List<IPrescription> prescriptions, FhirResourceFactory resourceFactory) {
		List<MedicationStatement> medicationStatements = new ArrayList<>();

		for (IPrescription prescription : prescriptions) {
			IArticle article = prescription.getArticle();
			if (article != null) {
				Medication medication = resourceFactory.getResource(article, IArticle.class, Medication.class);
				if (medication == null) {
					medication = new Medication();
					medication.setId("unknown");
				}
				if (medication.getCode() != null
						&& (medication.getCode().getText() == null || medication.getCode().getText().isEmpty())) {
					String defaultText = article.getName() != null ? article.getName() : "Undefined Medication";
					medication.getCode().setText(defaultText);
				}

				String pharmacode = getArticlePharmaCode(article);
				if (pharmacode != null && !pharmacode.isEmpty()) {
					Coding coding = medication.getCode().addCoding();
					coding.setSystem("urn:oid:2.16.756.5.30.2.6.1");
					coding.setCode(pharmacode);
				}

				String productNumber = getArticleProductNumber(article);

				if (productNumber != null && !productNumber.isEmpty()) {
					Coding coding = medication.getCode().addCoding();
					coding.setSystem("https://mednet.swiss/fhir/productNumber");
					coding.setCode(productNumber);
				}
				adjustCodingSystems(medication);

				String medicationId = medication.getId();
				if (medicationId != null && medicationId.startsWith("Medication/")) {
					medicationId = medicationId.substring("Medication/".length());
					medication.setId(medicationId);
				}

				MedicationStatement medicationStatement = resourceFactory.getResource(prescription, IPrescription.class,
						MedicationStatement.class);

				if (medication.getId() == null || medication.getId().isEmpty()) {
					medication.setId(medicationStatement.getId() + "-med"); 
				}
				medicationStatement.addContained(medication);
				Reference medicationReference = new Reference("#" + medication.getId());
				medicationStatement.setMedication(medicationReference);
				medicationStatement.setSubject(patientReference);
				medicationStatements.add(medicationStatement);
			}
		}
		return medicationStatements;
	}


	private String getArticlePharmaCode(IArticle article) {
		String ret = StringUtils.EMPTY;
		try {
			Method method = article.getClass().getMethod("getPHAR");
			ret = (String) method.invoke(article);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

		}
		if (StringUtils.isBlank(ret)) {
			Object value = article.getExtInfo("PharmaCode"); 
			if (value instanceof String && ((String) value).length() == 7) {
				ret = (String) value;
			}
		}
		return StringUtils.defaultString(ret);
	}


	private void adjustCodingSystems(Medication medication) {
		if (medication.getCode() != null && medication.getCode().hasCoding()) {
			for (Coding coding : medication.getCode().getCoding()) {
				String system = coding.getSystem();
				if ("urn:oid:1.3.160".equals(system)) {
					coding.setSystem("urn:oid:2.51.1.1");
				} else if ("https://index.hcisolutions.ch/DataDoc/element/ARTICLE/ART/PHARMACODE".equals(system)
						|| "2.16.756.5.30.2.6.1".equals(system)) {
					coding.setSystem("urn:oid:2.16.756.5.30.2.6.1");
				} else if ("urn:oid:2.16.840.1.113883.6.73".equals(system) || "2.16.840.1.113883.6.73".equals(system)) {

				}
			}
		}

	}

	private String getArticleProductNumber(IArticle article) {
	    if (article != null) {
	        IQuery<IArtikelstammItem> query = ArtikelstammModelServiceHolder.get().getQuery(IArtikelstammItem.class);
	        query.and("id", COMPARATOR.EQUALS, article.getId());
	        List<IArtikelstammItem> results = query.execute();
	        if (!results.isEmpty()) {
	            IArtikelstammItem item = results.get(0);
				return item.getProductId();
	        }
	    }
	    return null;
	}

}
