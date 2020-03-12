package at.medevit.elexis.ehc.core.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.ehealth_connector.common.mdht.Address;
import org.ehealth_connector.common.mdht.Identificator;
import org.ehealth_connector.common.mdht.Name;
import org.ehealth_connector.common.mdht.Patient;
import org.ehealth_connector.common.mdht.enums.AdministrativeGender;
import org.ehealth_connector.communication.AffinityDomain;
import org.ehealth_connector.communication.ConvenienceMasterPatientIndexV3;
import org.ehealth_connector.communication.Destination;
import org.junit.Test;

import ch.elexis.core.services.ISSLStoreService;
import ch.elexis.core.utils.OsgiServiceUtil;

/**
 * Test the Elexis ehc integration, with the ehealthsuisse <a
 * href=https://ehealthsuisse.ihe-europe.net/>„EPD-Referenzumgebung“ (EPD-RU)</a>.
 * 
 * @author thomas
 *
 */
public class EHealthSuisseTest {
	
	private static ISSLStoreService sslStoreService;
	
	/**
	 * (ITI-47) Endpoint
	 */
	public static final String PDQ_ENDPOINT_URL =
		"https://ehealthsuisse.ihe-europe.net:10443/PAMSimulator-ejb/PDQSupplier_Service/PDQSupplier_PortType";
	
	public static final String PDQ_RECV_DEVICE_OID = "1.3.6.1.4.1.12559.11.25.1.14";
	
	public static final String PDQ_RECV_ORG_OID = "1.3.6.1.4.1.12559.11.25.1.12";
	
	/**
	 * (ITI-45 / ITI-44) Endpoint
	 */
	public static final String PIX_ENDPOINT_URL =
		"https://ehealthsuisse.ihe-europe.net:10443/PAMSimulator-ejb/PIXManager_Service/PIXManager_PortType";
	
	public static final String PIX_RECV_DEVICE_OID = "1.3.6.1.4.1.12559.11.25.1.10";
	
	public static final String PIX_RECV_ORG_OID = "1.3.6.1.4.1.12559.11.25.1.12";
	
	// use dummy sub id of ehc dev OID for now -> TODO get an Elexis OID
	public static final String ORGANIZATIONAL_ID = "2.16.756.5.30.1.139.1.1.3.9999";
	
	public static final String ORGANIZATIONAL_PATIENTS_ID = ORGANIZATIONAL_ID + ".100";
	
	private static AffinityDomain affinityDomain;
	
	private static AffinityDomain getAffinityDomain() throws URISyntaxException{
		sslStoreService = OsgiServiceUtil.getService(ISSLStoreService.class)
			.orElseThrow(() -> new IllegalStateException("No ISSLStoreService available"));
		Optional<KeyStore> currentTrustStore = sslStoreService.loadKeyStore(
			AffinityDomain.class.getResourceAsStream("/rsc/cert/gazelle.jks"), "gazelle", "JKS");
		currentTrustStore.ifPresent(store -> sslStoreService.addTrustStore(store));
		
		Optional<KeyStore> currentKeyStore = sslStoreService.loadKeyStore(
			AffinityDomain.class.getResourceAsStream("/rsc/cert/gazelle.jks"), "gazelle", "JKS");
		currentKeyStore.ifPresent(store -> sslStoreService.addKeyStore(store, "gazelle"));
		
		if (affinityDomain == null) {
			// create PDQ and PIX destinations
			Destination pdqDestination =
				new Destination(ORGANIZATIONAL_ID, new URI(PDQ_ENDPOINT_URL));
			pdqDestination.setSenderApplicationOid(ORGANIZATIONAL_ID);
			pdqDestination.setReceiverApplicationOid(PDQ_RECV_DEVICE_OID);
			pdqDestination.setReceiverFacilityOid(PDQ_RECV_ORG_OID);
			
			Destination pixDestination =
				new Destination(ORGANIZATIONAL_ID, new URI(PIX_ENDPOINT_URL));
			pdqDestination.setSenderApplicationOid(ORGANIZATIONAL_ID);
			pdqDestination.setReceiverApplicationOid(PIX_RECV_DEVICE_OID);
			pdqDestination.setReceiverFacilityOid(PIX_RECV_ORG_OID);
			
			affinityDomain = new AffinityDomain();
			affinityDomain.setPdqDestination(pdqDestination);
			affinityDomain.setPixDestination(pixDestination);
		}
		// TODO add XDS registry and repository if EPD-RU supports it
		//		Destination xdsRegistryDestination =
		//			new Destination(ORGANIZATIONAL_ID, new URI(getXdsRegistryUrl()));
		//		Destination xdsRepositoryDestination =
		//			new Destination(ORGANIZATIONAL_ID, new URI(getXdsRepositoryUrl()));
		//		xdsRegistryDestination.setReceiverApplicationOid(XDS_REPOSITORY_OID);
		//		xdsRegistryDestination.setReceiverFacilityOid(XDS_REPOSITORY_OID);
		
		// TODO add ATNA if EPD-RU supports it
		//		AtnaConfig atnaConfig = new AtnaConfig();
		//		atnaConfig.setAuditRepositoryUri(getAtnaUrl());
		//		atnaConfig.setAuditSourceId("EHC-Elexis");
		//		ret.setAtnaConfig(atnaConfig);
		
		return affinityDomain;
	}
	
	private Patient getTestPatient(){
		Patient ret = new Patient();
		ret.addName(new Name("Elexis", "Alpha"));
		ret.setAdministrativeGender(AdministrativeGender.FEMALE);
		LocalDate dob = LocalDate.of(2007, 6, 30);
		ret.setBirthday(Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		Address address = new Address("Testweg", "123", "123", "Testhausen");
		address.setAddressline1("Testweg 123");
		ret.addAddress(address);
		ret.setNation("CHE");
		ret.addId(new Identificator(ORGANIZATIONAL_ID, "123"));
		return ret;
	}
	
	@Test
	public void pixAdd() throws URISyntaxException{
		Patient patient = getTestPatient();
		
		final boolean ret = ConvenienceMasterPatientIndexV3.addPatientDemographics(patient,
			ORGANIZATIONAL_ID, getAffinityDomain());
		assertTrue("addPatientDemographics failed", ret);
	}
	
	@Test
	public void pixQuery() throws URISyntaxException{
		Patient patient = getTestPatient();
		
		final String domainToReturnOids[] = new String[1];
		domainToReturnOids[0] = ORGANIZATIONAL_ID;
		
		List<Identificator> ids = ConvenienceMasterPatientIndexV3.queryPatientId(patient,
			ORGANIZATIONAL_ID, domainToReturnOids, getAffinityDomain());
		
		assertNotNull(ids);
		assertTrue(ids.size() > 0);
	}
}
