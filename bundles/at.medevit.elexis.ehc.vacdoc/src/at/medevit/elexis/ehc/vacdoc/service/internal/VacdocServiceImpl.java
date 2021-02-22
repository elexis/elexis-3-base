package at.medevit.elexis.ehc.vacdoc.service.internal;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.ehealth_connector.cda.Consumable;
import org.ehealth_connector.cda.ch.utils.CdaChLoader;
import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.cda.ch.vacd.Immunization;
import org.ehealth_connector.common.enums.CodeSystems;
import org.ehealth_connector.common.enums.LanguageCode;
import org.ehealth_connector.common.mdht.Author;
import org.ehealth_connector.common.mdht.Code;
import org.ehealth_connector.common.mdht.Identificator;
import org.ehealth_connector.common.utils.DateUtil;
import org.openhealthtools.mdht.uml.cda.ch.CdaChVacdV1;
import org.openhealthtools.mdht.uml.cda.ch.ChPackage;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.elexis.ehc.core.EhcCoreMapper;
import at.medevit.elexis.ehc.core.EhcCoreService;
import at.medevit.elexis.ehc.vacdoc.service.VacdocService;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObjectFactory;
import ch.elexis.data.Query;

@Component
public class VacdocServiceImpl implements VacdocService {
	
	private static Logger logger = LoggerFactory.getLogger(VacdocServiceImpl.class);
	
	private EhcCoreService ehcCoreService;

	public VacdocServiceImpl(){
		EClass vacdClass = ChPackage.eINSTANCE.getCdaChVacdV1();
		if (vacdClass == null) {
			logger.warn("Could not load VACD class from ch package");
		}
	}
	
	@Reference
	public void setEhcCoreService(EhcCoreService ehcCoreService){
		this.ehcCoreService = ehcCoreService;
	}
	
	public void unsetEhcCoreService(EhcCoreService ehcCoreService){
		this.ehcCoreService = null;
	}
	
	@Override
	public InputStream getXdmAsStream(CdaChVacd document) throws Exception{
		return ehcCoreService.getXdmAsStream(document.getDoc());
	}
	
	@Override
	public CdaChVacd getVacdocDocument(Patient patient, Mandant mandant){
		// Create eVACDOC (Header)
		CdaChVacd doc = new CdaChVacd(LanguageCode.GERMAN, null, null);
		doc.setPatient(EhcCoreMapper.getEhcPatient(patient));
		doc.setCustodian(EhcCoreMapper.getEhcOrganization(mandant));
		doc.addAuthor(EhcCoreMapper.getEhcAuthor(mandant));
		doc.setLegalAuthenticator(EhcCoreMapper.getEhcAuthor(mandant));
		
		return doc;
	}
	
	/**
	 * Add all vaccinations of the patient referenced in the document.
	 * 
	 * @param doc
	 * @param vaccinations
	 */
	@Override
	public void addAllVaccinations(CdaChVacd doc){
		org.ehealth_connector.common.mdht.Patient ehcPatient = doc.getPatient();
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
	@Override
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
					Identificator ean = new Identificator("1.3.160", identifier);
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
					DateUtil.parseDate(vaccination.getDateOfAdministrationLabel()), null, null);
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
	
	@Override
	public Optional<CdaChVacd> loadVacdocDocument(InputStream document) throws Exception{
		try {
			final CdaChLoader<CdaChVacd> loader = new CdaChLoader<CdaChVacd>();
			return Optional.of(loader.loadFromStream(document, CdaChVacd.class, CdaChVacdV1.class));
		} catch (Exception e) {
			logger.error("problem loading xml document", e);
		}
		return Optional.empty();
	}
	
	@Override
	public void importImmunizations(Patient elexisPatient, List<Immunization> immunizations){
		for (Immunization immunization : immunizations) {
			Consumable consumable = immunization.getConsumable();
			
			Code atcCode = consumable.getWhoAtcCode();
			Identificator gtin = consumable.getManufacturedProductId();
			IArtikelstammItem article = resolveArticle(gtin, atcCode);
			Optional<String> articleStoreToString =
				StoreToStringServiceHolder.get().storeToString(article);
			
			Author author = immunization.getAuthor();
			
			if (article != null && articleStoreToString.isPresent()) {
				new Vaccination(elexisPatient.getId(), articleStoreToString.get(),
					article.getLabel(), article.getGtin(), article.getAtcCode(),
					immunization.getApplyDate(), consumable.getLotNr(),
					((author != null) ? author.getCompleteName() : ""));
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
	
	private IArtikelstammItem resolveArticle(Identificator gtin, Code atcCode){
		String gtinStr = (gtin != null) ? gtin.getExtension() : null;
		String atcStr = (atcCode != null) ? atcCode.getCode() : null;
		if (gtinStr != null) {
			INamedQuery<IArtikelstammItem> query =
				ArtikelstammModelServiceHolder.get().getNamedQuery(IArtikelstammItem.class, "gtin");
			return query.executeWithParametersSingleResult(query.getParameterMap("gtin", gtinStr))
				.orElse(null);
		} else if (atcStr != null && !atcStr.isEmpty()) {
			IQuery<IArtikelstammItem> query =
				ArtikelstammModelServiceHolder.get().getQuery(IArtikelstammItem.class);
			query.and("atc", COMPARATOR.EQUALS, atcStr);
			List<IArtikelstammItem> articles = query.execute();
			if (articles != null && !articles.isEmpty()) {
				String displayName =
					(atcCode != null) ? atcCode.getDisplayName().toLowerCase() : null;
				if (displayName != null && !displayName.isEmpty()) {
					for (IArtikelstammItem artikelstammItem : articles) {
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
