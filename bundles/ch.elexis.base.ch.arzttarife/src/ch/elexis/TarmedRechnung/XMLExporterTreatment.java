package ch.elexis.TarmedRechnung;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.jdom.Element;

import ch.elexis.core.model.FallConstants;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IDiagnosisReference;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;

public class XMLExporterTreatment {
	private static final String BY_CONTRACT = "by_contract"; //$NON-NLS-1$
	private static final String ICPC = "ICPC"; //$NON-NLS-1$

	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$

	private Element insuranceElement;
	List<IDiagnosisReference> invoiceDiagnosis;
	
	private XMLExporterTreatment(Element insuranceElement){
		this.insuranceElement = insuranceElement;
	}
	
	public Element getElement(){
		return insuranceElement;
	}
	
	public static XMLExporterTreatment buildTreatment(IInvoice invoice, XMLExporter xmlExporter){
		
		ICoverage actFall = invoice.getCoverage();
		IMandator actMandant = invoice.getMandator();
		
		Element element = new Element("treatment", XMLExporter.nsinvoice);
		element.setAttribute("date_begin", //$NON-NLS-1$
			XMLExporterUtil.makeTarmedDatum(invoice.getDateFrom()));
		element.setAttribute("date_end", //$NON-NLS-1$
			XMLExporterUtil.makeTarmedDatum(invoice.getDateTo()));
		element.setAttribute("canton", (String) actMandant.getExtInfo(XMLExporter.ta.KANTON)); //$NON-NLS-1$
		element.setAttribute("reason", match_type(actFall.getReason())); //$NON-NLS-1$
		
		List<IDiagnosisReference> invoiceDiagnosis = getDiagnosen(invoice);
		//diagnosis
		for (IDiagnosisReference invoiceDiagnose : invoiceDiagnosis) {
			Element diagnosis = new Element("diagnosis", XMLExporter.nsinvoice); //$NON-NLS-1$
			String diagnosisType = match_diag(invoiceDiagnose.getCodeSystemName());
			diagnosis.setAttribute(ATTR_TYPE, diagnosisType); // 15510
			String code = invoiceDiagnose.getCode();
			if (diagnosisType.equalsIgnoreCase(XMLExporter.FREETEXT)) {
				diagnosis.setText(invoiceDiagnose.getText());
			} else {
				if (code.length() > 12) {
					code = code.substring(0, 12);
				}
				diagnosis.setAttribute(XMLExporter.ATTR_CODE, code);
			}
			element.addContent(diagnosis);
		}

		XMLExporterTreatment ret = new XMLExporterTreatment(element);
		ret.invoiceDiagnosis = invoiceDiagnosis;
		
		return ret;
	}
	
	private static List<IDiagnosisReference> getDiagnosen(IInvoice invoice){
		HashSet<String> seen = new HashSet<>();
		ArrayList<IDiagnosisReference> ret = new ArrayList<IDiagnosisReference>();
		List<IEncounter> encounters = invoice.getEncounters();
		for (IEncounter encounter : encounters) {
			List<IDiagnosisReference> encounterDiagnosis = encounter.getDiagnoses();
			for (IDiagnosisReference encounterDiagnose : encounterDiagnosis) {
				String dgc = encounterDiagnose.getCode();
				if (dgc != null) {
					// each diag code and system only once
					if (seen
						.add(encounterDiagnose.getCode() + encounterDiagnose.getCodeSystemName())) {
						ret.add(encounterDiagnose);
					}
				}
			}
		}
		return ret;
	}

	private static String match_type(final String type){
		if (type == null) {
			return XMLExporter.DISEASE;
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_DISEASE)) {
			return XMLExporter.DISEASE;
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_ACCIDENT)) {
			return "accident"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_MATERNITY)) {
			return "maternity"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_PREVENTION)) {
			return "prevention"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_BIRTHDEFECT)) {
			return XMLExporter.BIRTHDEFECT;
		}
		return XMLExporter.DISEASE;
	}
	
	private static String match_diag(final String name){
		if (name != null) {
			if (name.equalsIgnoreCase(XMLExporter.FREETEXT)) {
				return XMLExporter.FREETEXT;
			}
			if (name.equalsIgnoreCase("ICD-10")) { //$NON-NLS-1$
				return "ICD"; //$NON-NLS-1$
			}
			if (name.equalsIgnoreCase("by contract")) { //$NON-NLS-1$
				return BY_CONTRACT;
			}
			if (name.equalsIgnoreCase(ICPC)) {
				return ICPC;
			}
			if (name.equalsIgnoreCase(XMLExporter.BIRTHDEFECT)) {
				return XMLExporter.BIRTHDEFECT;
			}
		}
		return BY_CONTRACT;
	}
	
	public List<IDiagnosisReference> getDiagnoses(){
		if (invoiceDiagnosis == null) {
			return Collections.emptyList();
		}
		return invoiceDiagnosis;
	}
}
