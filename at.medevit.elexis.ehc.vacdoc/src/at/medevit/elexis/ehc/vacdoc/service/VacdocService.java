package at.medevit.elexis.ehc.vacdoc.service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.ehealth_connector.cda.Consumable;
import org.ehealth_connector.cda.ch.utils.CdaChLoader;
import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.cda.ch.vacd.Immunization;
import org.ehealth_connector.common.Author;
import org.ehealth_connector.common.Code;
import org.ehealth_connector.common.Identificator;
import org.ehealth_connector.common.enums.CodeSystems;
import org.ehealth_connector.common.utils.DateUtil;
import org.openhealthtools.mdht.uml.cda.ch.CHPackage;
import org.openhealthtools.mdht.uml.cda.ch.VACD;
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
	
	public VacdocService(){
		EClass vacdClass = CHPackage.eINSTANCE.getVACD();
		if (vacdClass == null) {
			logger.warn("Could not load VACD class from ch package");
		}
	}
	
	/**
	 * Get an InputStream containing the
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public InputStream getXdmAsStream(CdaChVacd document) throws Exception{
		return EhcServiceComponent.getService().getXdmAsStream(document.getDoc());
	}
	
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
				
				Immunization immunization = new Immunization(consumable, author,
					DateUtil.date(vaccination.getDateOfAdministrationLabel()), null, null);
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
	
	public Optional<CdaChVacd> getVacdocDocument(InputStream document) throws Exception{
		try {
			final CdaChLoader<CdaChVacd> loader = new CdaChLoader<CdaChVacd>();
			return Optional.of(loader.loadFromStream(document, CdaChVacd.class, VACD.class));
		} catch (Exception e) {
			logger.error("problem loading xml document", e);
		}
		return Optional.empty();
	}
	
	public void importImmunizations(Patient elexisPatient, List<Immunization> immunizations){
		for (Immunization immunization : immunizations) {
			Consumable consumable = immunization.getConsumable();
			
			Code atcCode = consumable.getWhoAtcCode();
			Identificator gtin = consumable.getManufacturedProductId();
			Artikel article = resolveArticle(gtin, atcCode);
			
			Author author = immunization.getAuthor();
			
			if (article != null) {
				new Vaccination(elexisPatient.getId(), article, immunization.getApplyDate(),
					consumable.getLotNr(), ((author != null) ? author.getCompleteName() : ""));
			} else {
				logger.warn("Article [" + consumable.getTradeName() + "] not found GTIN ["
					+ ((gtin != null) ? gtin.getExtension() : "") + "]");
				new Vaccination(elexisPatient.getId(), "", consumable.getTradeName(),
					((gtin != null) ? gtin.getExtension() : ""),
					((atcCode != null) ? atcCode.getCode() : ""), immunization.getApplyDate(),
					consumable.getLotNr(), ((author != null) ? author.getCompleteName() : ""));
			}
		}
	}
	
	private Artikel resolveArticle(Identificator gtin, Code atcCode){
		String gtinStr = (gtin != null) ? gtin.getExtension() : null;
		String atcStr = (atcCode != null) ? atcCode.getCode() : null;
		if (gtinStr != null) {
			Query<ArtikelstammItem> query = new Query<>(ArtikelstammItem.class);
			query.add(ArtikelstammItem.FLD_GTIN, Query.EQUALS, gtinStr);
			List<ArtikelstammItem> articles = query.execute();
			if (articles != null && !articles.isEmpty()) {
				return articles.get(0);
			}
		} else if (atcStr != null && !atcStr.isEmpty()) {
			Query<ArtikelstammItem> query = new Query<>(ArtikelstammItem.class);
			query.add(ArtikelstammItem.FLD_ATC, Query.EQUALS, atcStr);
			List<ArtikelstammItem> articles = query.execute();
			if (articles != null && !articles.isEmpty()) {
				String displayName =
					(atcCode != null) ? atcCode.getDisplayName().toLowerCase() : null;
				if (displayName != null && !displayName.isEmpty()) {
					for (ArtikelstammItem artikelstammItem : articles) {
						if (artikelstammItem.getName().toLowerCase().contains(displayName)) {
							return artikelstammItem;
						}
					}
				}
				return articles.get(0);
			}
		}
		return null;
	}
}
