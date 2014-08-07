package at.medevit.elexis.ehc.core.internal;

import java.io.InputStream;

import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.ch.CDACH;
import org.openhealthtools.mdht.uml.cda.ch.CHFactory;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.core.EhcCoreService;
import at.medevit.elexis.ehc.core.internal.document.CdaChImpl;
import ch.elexis.data.Patient;
import ehealthconnector.cda.documents.ch.CdaCh;

public class EhcCoreServiceImpl implements EhcCoreService {
	
	private static Logger logger = LoggerFactory.getLogger(EhcCoreServiceImpl.class);
	
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
			}
		} catch (Exception e) {
			logger.warn("Error loading document.", e);
		}
		return null;
	}
}
