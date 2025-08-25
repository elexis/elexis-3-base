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
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
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
					medication.setId("unknown"); //$NON-NLS-1$
				}

				if (!medication.getMeta().hasProfile(FHIRConstants.PROFILE_MEDICATION)) {
					medication.getMeta().addProfile(FHIRConstants.PROFILE_MEDICATION);
				}

				if (medication.getCode() != null
						&& (medication.getCode().getText() == null || medication.getCode().getText().isEmpty())) {
					String defaultText = article.getName() != null ? article.getName() : "Undefined Medication"; //$NON-NLS-1$
					medication.getCode().setText(defaultText);
				}

				String pharmacode = getArticlePharmaCode(article);
				if (pharmacode != null && !pharmacode.isEmpty()) {
					medication.getCode()
							.addCoding(new Coding().setSystem(FHIRConstants.PHARMACODE_SYSTEM).setCode(pharmacode));
				}
				String productNumber = getArticleProductNumber(article);
				if (productNumber != null && !productNumber.isEmpty()) {
					medication.getCode().addCoding(
							new Coding().setSystem(FHIRConstants.PRODUCT_NUMBER_SYSTEM).setCode(productNumber));
				}
				adjustCodingSystems(medication);

				stripFreeTextCodes(medication);

				medication.setExtension(null);
				ensureAbsoluteExtensionUrls(medication);

				String medicationId = medication.getId();
				if (medicationId != null && medicationId.startsWith("Medication/")) { //$NON-NLS-1$
					medicationId = medicationId.substring("Medication/".length()); //$NON-NLS-1$
					medication.setId(medicationId);
				}

				MedicationStatement ms = resourceFactory.getResource(prescription, IPrescription.class,
						MedicationStatement.class);

				if (!ms.getMeta().hasProfile(FHIRConstants.PROFILE_MEDICATION_STATEMENT)) {
					ms.getMeta().addProfile(FHIRConstants.PROFILE_MEDICATION_STATEMENT);
				}
				ms.setExtension(null);
				ensureAbsoluteIdentifierSystems(ms);
				ensureDosageMeetsSlice(ms);

				if (!medication.hasId() || medication.getId().isEmpty()) {
					medication.setId(ms.getId() + "-med"); //$NON-NLS-1$
				}
				ms.addContained(medication);
				ms.setMedication(new Reference("#" + medication.getId())); //$NON-NLS-1$
				ms.setSubject(patientReference);

				medicationStatements.add(ms);
			}
		}
		return medicationStatements;
	}


	private String getArticlePharmaCode(IArticle article) {
		String ret = StringUtils.EMPTY;
		try {
			Method method = article.getClass().getMethod("getPHAR"); //$NON-NLS-1$
			ret = (String) method.invoke(article);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

		}
		if (StringUtils.isBlank(ret)) {
			Object value = article.getExtInfo("PharmaCode");  //$NON-NLS-1$
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
				if ("urn:oid:1.3.160".equals(system)) { //$NON-NLS-1$
					coding.setSystem(FHIRConstants.GTIN_SYSTEM);
				} else if ("https://index.hcisolutions.ch/DataDoc/element/ARTICLE/ART/PHARMACODE".equals(system) //$NON-NLS-1$
						|| "2.16.756.5.30.2.6.1".equals(system)) { //$NON-NLS-1$
					coding.setSystem(FHIRConstants.PHARMACODE_SYSTEM);
				} else if ("urn:oid:2.16.840.1.113883.6.73".equals(system) || "2.16.840.1.113883.6.73".equals(system)) { //$NON-NLS-1$ //$NON-NLS-2$

				}
			}
		}

	}

	private String getArticleProductNumber(IArticle article) {
	    if (article != null) {
	        IQuery<IArtikelstammItem> query = ArtikelstammModelServiceHolder.get().getQuery(IArtikelstammItem.class);
	        query.and("id", COMPARATOR.EQUALS, article.getId()); //$NON-NLS-1$
	        List<IArtikelstammItem> results = query.execute();
	        if (!results.isEmpty()) {
	            IArtikelstammItem item = results.get(0);
				return item.getProductId();
	        }
	    }
	    return null;
	}

	private static boolean hasTextOrSpaces(String s) {
		return s != null && s.trim().contains(StringUtils.SPACE);
	}

	private static void ensureAbsoluteExtensionUrls(org.hl7.fhir.r4.model.DomainResource r) {
		if (!r.hasExtension())
			return;
		r.getExtension().forEach(ex -> {
			if (ex.hasUrl()) {
				String u = ex.getUrl();
				if (u.startsWith("www.")) //$NON-NLS-1$
					ex.setUrl("https://" + u); //$NON-NLS-1$
			}
		});
	}

	private static void stripFreeTextCodes(Medication med) {
		if (!med.hasCode() || !med.getCode().hasCoding())
			return;
		List<Coding> keep = new ArrayList<>();
		for (Coding c : med.getCode().getCoding()) {
			String sys = c.getSystem();
			if (FHIRConstants.GTIN_SYSTEM.equals(sys) || FHIRConstants.PHARMACODE_SYSTEM.equals(sys)
					|| FHIRConstants.PRODUCT_NUMBER_SYSTEM.equals(sys)) {
				if (c.hasCode() && hasTextOrSpaces(c.getCode())) {
					String d = c.hasDisplay() ? c.getDisplay() + StringUtils.SPACE : StringUtils.EMPTY;
					c.setDisplay(d + c.getCode());
					c.setCode(null);
				}
				keep.add(c);
			}
		}
		med.getCode().setCoding(keep);
	}

	private static void ensureDosageMeetsSlice(MedicationStatement ms) {
		if (!ms.hasDosage())
			ms.addDosage();
		ms.getDosage().forEach(d -> {
			d.setAdditionalInstruction(null);
			d.setText(null);
			if (!d.hasTiming()) {
				org.hl7.fhir.r4.model.Timing t = new org.hl7.fhir.r4.model.Timing();
				t.setRepeat(new org.hl7.fhir.r4.model.Timing.TimingRepeatComponent().setFrequency(1).setPeriod(1)
						.setPeriodUnit(org.hl7.fhir.r4.model.Timing.UnitsOfTime.D));
				d.setTiming(t);
			}
			if (d.getDoseAndRate().isEmpty()) {
				d.addDoseAndRate().setDose(new org.hl7.fhir.r4.model.Quantity().setValue(1));
			}
		});
	}

	private static void ensureAbsoluteIdentifierSystems(MedicationStatement ms) {
		if (!ms.hasIdentifier()) {
			ms.addIdentifier().setSystem("https://elexis.ch/fhir/identifier/medicationstatement").setValue(ms.getId()); //$NON-NLS-1$
		}
		ms.getIdentifier().forEach(id -> {
			if (id.hasSystem() && id.getSystem().startsWith("www.")) { //$NON-NLS-1$
				id.setSystem("https://" + id.getSystem()); //$NON-NLS-1$
			}
		});
	}

}
