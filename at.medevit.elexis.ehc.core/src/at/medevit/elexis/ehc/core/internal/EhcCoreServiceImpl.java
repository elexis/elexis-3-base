/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.core.internal;

import java.io.InputStream;
import java.util.List;

import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Consumable;
import org.openhealthtools.mdht.uml.cda.ManufacturedProduct;
import org.openhealthtools.mdht.uml.cda.Material;
import org.openhealthtools.mdht.uml.cda.StrucDocText;
import org.openhealthtools.mdht.uml.cda.SubstanceAdministration;
import org.openhealthtools.mdht.uml.cda.ch.CDACH;
import org.openhealthtools.mdht.uml.cda.ch.CHFactory;
import org.openhealthtools.mdht.uml.cda.ihe.IHEFactory;
import org.openhealthtools.mdht.uml.cda.ihe.MedicationsSection;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.EN;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_PQ;
import org.openhealthtools.mdht.uml.hl7.vocab.NullFlavor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.core.EhcCoreService;
import at.medevit.elexis.ehc.core.internal.document.CdaChImpl;
import ch.elexis.data.Patient;
import ch.elexis.data.Rezept;
import ehealthconnector.cda.documents.ch.CdaCh;
import ehealthconnector.cda.documents.ch.Phone;

public class EhcCoreServiceImpl implements EhcCoreService {
	
	private static Logger logger = LoggerFactory.getLogger(EhcCoreServiceImpl.class);
	
	public EhcCoreServiceImpl(){
		// make sure CDACH is registered and initialized
		CHFactory.eINSTANCE.createCDACH().init();
	}
	
	@Override
	public CdaCh getPatientDocument(Patient patient){
		CdaChImpl ret = new CdaChImpl(CHFactory.eINSTANCE.createCDACH().init());
		
		ret.cSetPatient(EhcCoreMapper.getEhcPatient(patient));
		return ret;
	}
	
	@Override
	public CdaCh getDocument(InputStream document){
		ClinicalDocument clinicalDocument;
		try {
			clinicalDocument = CDAUtil.load(document);
			if (clinicalDocument instanceof CDACH) {
				return new CdaChImpl((CDACH) clinicalDocument);
			} else {
				logger.warn("Loaded document is not a subclass of CDACH.");
			}
		} catch (Exception e) {
			logger.warn("Error loading document.", e);
		}
		return null;
	}
	
	@Override
	public void importPatient(ehealthconnector.cda.documents.ch.Patient ehcPatient){
		Patient patient = EhcCoreMapper.getElexisPatient(ehcPatient);
		EhcCoreMapper.importEhcAddress(patient, ehcPatient.cGetAddresses().get(0));
		List<Phone> phones = ehcPatient.cGetPhones();
		for (Phone phone : phones) {
			EhcCoreMapper.importEhcPhone(patient, phone);
		}
	}
	
	@Override
	public CdaCh getPrescriptionDocument(Rezept rezept){
		CDACH clinicalDocument = CHFactory.eINSTANCE.createCDACH().init();
		CdaChImpl ret = new CdaChImpl(clinicalDocument);
		
		ret.cSetPatient(EhcCoreMapper.getEhcPatient(rezept.getPatient()));
		
		// TODO OID muss richtig sein
		// create medication section
		MedicationsSection medicationsSection = IHEFactory.eINSTANCE.createMedicationsSection();
		medicationsSection.setTitle(DatatypesFactory.eINSTANCE.createST("Aktuelle Medikation"));
		StrucDocText sdText = CDAFactory.eINSTANCE.createStrucDocText();
		sdText.addText("Lisinopril HCT Streuli Tbl. 10/12.5 mg: 1-0-0");
		medicationsSection.setText(sdText);
		medicationsSection.setCode(DatatypesFactory.eINSTANCE.createCE("10160-0",
			"2.16.840.1.113883.6.1", "LOINC", "HISTORY OF MEDICATION USE"));
		medicationsSection.setId(DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.1.105.1.6"));
		
		// define substance administration
		SubstanceAdministration substanceAdministration =
			CDAFactory.eINSTANCE.createSubstanceAdministration();
		substanceAdministration.getTemplateIds().add(
			DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.1.1.1.1.1"));
		substanceAdministration.getIds().add(
			DatatypesFactory.eINSTANCE.createII("2.16.756.5.30.2.6.1"));
		// code, system, systemname, displayname
		substanceAdministration.setCode(DatatypesFactory.eINSTANCE.createCD("DRUG",
			"2.16.840.1.113883.5.4", "", "Medikamentöse Therapie"));
		substanceAdministration.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));
		substanceAdministration.getEffectiveTimes().add(
			DatatypesFactory.eINSTANCE.createIVL_TS("20140807"));
		substanceAdministration.setPriorityCode(DatatypesFactory.eINSTANCE.createCE("R",
			"2.16.840.1.113883.5.7", "ActPriority", "Routine"));
		substanceAdministration.setRouteCode(DatatypesFactory.eINSTANCE.createCE("PO",
			"2.16.840.1.113883.5.112", "RouteOfAdministration", "schlucken, oral"));
		
		// set quantities
		IVL_PQ ivlQuantity = DatatypesFactory.eINSTANCE.createIVL_PQ();
		ivlQuantity.setCenter(DatatypesFactory.eINSTANCE.createPQ(1, "1"));
		substanceAdministration.setDoseQuantity(ivlQuantity);
		
		IVL_PQ ivlRateQuantity = DatatypesFactory.eINSTANCE.createIVL_PQ();
		ivlRateQuantity.setNullFlavor(NullFlavor.UNK);
		substanceAdministration.setRateQuantity(ivlQuantity);
		
		// set consumable medication
		Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
		ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
		Material material = CDAFactory.eINSTANCE.createMaterial();
		CE materialCode =
			DatatypesFactory.eINSTANCE.createCE("C09BA03SETN000000010TABL", "2.16.756.5.30.2.6.2");
		
		// set original text UNK
		
		materialCode.getTranslations().add(
			DatatypesFactory.eINSTANCE.createCD("C09BA03", "2.16.840.1.113883.6.73", "", ""));
		material.setCode(DatatypesFactory.eINSTANCE.createCE("C09BA03SETN000000010TABL",
			"2.16.756.5.30.2.6.2"));
		EN name = DatatypesFactory.eINSTANCE.createEN();
		name.addText("LISINOPRIL HCT Streuli Tabl 10/12.5 30 Stk");
		material.setName(name);
		manufacturedProduct.setManufacturedMaterial(material);
		consumable.setManufacturedProduct(manufacturedProduct);
		
		substanceAdministration.setConsumable(consumable);
		medicationsSection.addSubstanceAdministration(substanceAdministration);
		
		clinicalDocument.addSection(medicationsSection);
		
		return ret;
	}
}
