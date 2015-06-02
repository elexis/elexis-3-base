package at.medevit.elexis.ehc.vacdoc.service;

import java.io.InputStream;
import java.util.List;

import org.ehealth_connector.cda.Consumable;
import org.ehealth_connector.cda.Immunization;
import org.ehealth_connector.cda.ch.CdaChVacd;
import org.ehealth_connector.cda.ch.DocumentProcessor;
import org.ehealth_connector.cda.ch.enums.CodeSystems;
import org.ehealth_connector.common.Author;
import org.ehealth_connector.common.Code;
import org.ehealth_connector.common.DateUtil;
import org.ehealth_connector.common.Identificator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.data.Artikel;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObjectFactory;
import ch.elexis.data.Query;

public class VacdocService {
	
	private static Logger logger = LoggerFactory.getLogger(VacdocService.class);

	/**
	 * Get empty vaccination document.
	 * 
	 * @param patient
	 * @param mandant
	 * @return vaccination document
	 */
	public CdaChVacd getVacdocDocument(Patient patient, Mandant mandant){
		CdaChVacd doc = EhcServiceComponent.getService().getVaccinationsDocument(patient, mandant);
		
		return doc;
	}
	
	/**
	 * Add all vaccinations of the patient referenced in the document.
	 * 
	 * @param doc
	 * @param vaccinations
	 */
	public void addAllVaccinations(CdaChVacd doc){
		org.ehealth_connector.common.Patient ehcPatient = doc.getPatient();
		Patient elexisPatient = EhcCoreMapper.getElexisPatient(ehcPatient);
		
		Query<Vaccination> query = new Query<Vaccination>(Vaccination.class);
		query.add(Vaccination.FLD_PATIENT_ID, Query.EQUALS, elexisPatient.getId());
		List<Vaccination> vaccinations = query.execute();
		addVaccinations(doc, vaccinations);
	}

	/**
	 * Add the vaccinations to the document.
	 * 
	 * @param doc
	 * @param vaccinations
	 */
	public void addVaccinations(CdaChVacd doc, List<Vaccination> vaccinations){
		if (!vaccinations.isEmpty()) {
			for (Vaccination vaccination : vaccinations) {
				Consumable consumable = new Consumable(vaccination.getShortBusinessName());
				consumable.setLotNr(vaccination.getLotNo());
				
				String code = vaccination.getAtcCode();
				if (code != null && !code.isEmpty()) {
					Code atc = new Code(CodeSystems.WHOATCCode, code);
					consumable.setWhoAtcCode(atc);
				}
				
				String identifier = vaccination.get(Vaccination.FLD_EAN);
				if (identifier != null && !identifier.isEmpty()) {
					Identificator ean = new Identificator(CodeSystems.GTIN, identifier);
					consumable.setManufacturedProductId(ean);
				}
				
				Author author = null;
				if (isVaccinationMandantKnown(vaccination)) {
					author = EhcCoreMapper.getEhcAuthor(getVaccinationMandant(vaccination));
				} else {
					String administratorName = getVaccinationAdministrator(vaccination);
					author = new Author(EhcCoreMapper.getEhcName(administratorName));
				}

				Immunization immunization =
					new Immunization(consumable, author, DateUtil.date(vaccination
						.getDateOfAdministrationLabel()),
						null, null);
				doc.addImmunization(immunization);
			}
		}
	}
	
	private boolean isVaccinationMandantKnown(Vaccination vaccination){
		String value = vaccination.get(Vaccination.FLD_ADMINISTRATOR);
		if (value.startsWith(Mandant.class.getName())) {
			Mandant mandant = (Mandant) new PersistentObjectFactory().createFromString(value);
			
			if (mandant != null && mandant.exists()) {
				return true;
			}
		}
		return false;
	}
	
	private Mandant getVaccinationMandant(Vaccination vaccination){
		String value = vaccination.get(Vaccination.FLD_ADMINISTRATOR);
		if (value.startsWith(Mandant.class.getName())) {
			Mandant mandant = (Mandant) new PersistentObjectFactory().createFromString(value);
			
			if (mandant != null && mandant.exists()) {
				return mandant;
			}
		}
		return null;
	}
	
	private String getVaccinationAdministrator(Vaccination vaccination){
		return vaccination.get(Vaccination.FLD_ADMINISTRATOR);
	}

	public CdaChVacd getVacdocDocument(InputStream document) throws Exception{
		return DocumentProcessor.loadFromStream(document);
	}

	public void importImmunizations(Patient elexisPatient, List<Immunization> immunizations){
		for (Immunization immunization : immunizations) {
			Consumable consumable = immunization.getConsumable();
			
			Code atcCode = consumable.getWhoAtcCode();
			Identificator gtin = consumable.getManufacturedProductId();
			Artikel article = resolveArticle(gtin.getExtension(), atcCode.getCode());
			
			if(article != null) {
				new Vaccination(elexisPatient.getId(), article, immunization.getApplyDate(),
					consumable.getLotNr(), "TODO");
			} else {
				logger.warn("Article [" + consumable.getTradeName() + "] not found GTIN [" + gtin.getExtension() + "]");
			}
		}
	}
	
	private Artikel resolveArticle(String gtin, String atc){
		Query<ArtikelstammItem> query = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		
		query.add(ArtikelstammItem.FLD_GTIN, Query.EQUALS, gtin);
		List<ArtikelstammItem> articles = query.execute();
		if (articles == null || articles.isEmpty()) {
			query = new Query<ArtikelstammItem>(ArtikelstammItem.class);
			
			query.add(ArtikelstammItem.FLD_ATC, Query.EQUALS, atc);
			articles = query.execute();
		}
		return null;
	}

	public Patient getElexisPatient(CdaChVacd ehcDocument){
		// TODO Auto-generated method stub
		return null;
	}
}
