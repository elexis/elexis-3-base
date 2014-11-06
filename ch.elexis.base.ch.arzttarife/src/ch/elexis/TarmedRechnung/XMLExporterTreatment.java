package ch.elexis.TarmedRechnung;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom.Element;

import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.TimeTool;

public class XMLExporterTreatment {
	private static final String BY_CONTRACT = "by_contract"; //$NON-NLS-1$
	private static final String ICPC = "ICPC"; //$NON-NLS-1$

	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$

	private Element insuranceElement;
	private List<IDiagnose> diagnoses;
	
	private XMLExporterTreatment(Element insuranceElement){
		this.insuranceElement = insuranceElement;
	}
	
	public Element getElement(){
		return insuranceElement;
	}
	
	public static XMLExporterTreatment buildTreatment(Rechnung rechnung, XMLExporter xmlExporter){
		
		Fall actFall = rechnung.getFall();
		Mandant actMandant = rechnung.getMandant();
		
		Element element = new Element("treatment", XMLExporter.nsinvoice);
		element.setAttribute(
			"date_begin", //$NON-NLS-1$
			XMLExporterUtil.makeTarmedDatum(XMLExporterUtil.getFirstKonsDate(rechnung).toString(
				TimeTool.DATE_GER)));
		element.setAttribute(
			"date_end", //$NON-NLS-1$
			XMLExporterUtil.makeTarmedDatum(XMLExporterUtil.getLastKonsDate(rechnung).toString(
				TimeTool.DATE_GER)));
		element.setAttribute("canton", actMandant.getInfoString(XMLExporter.ta.KANTON)); //$NON-NLS-1$
		element.setAttribute("reason", match_type(actFall.getGrund())); //$NON-NLS-1$
		
		List<IDiagnose> diagnosen = getDiagnosen(rechnung);
		//diagnosis
		for (IDiagnose diagnose : diagnosen) {
			Element diagnosis = new Element("diagnosis", XMLExporter.nsinvoice); //$NON-NLS-1$
			String diagnosisType = match_diag(diagnose.getCodeSystemName());
			diagnosis.setAttribute(ATTR_TYPE, diagnosisType); // 15510
			String code = diagnose.getCode();
			if (diagnosisType.equalsIgnoreCase(XMLExporter.FREETEXT)) {
				diagnosis.setText(diagnose.getText());
			} else {
				if (code.length() > 12) {
					code = code.substring(0, 12);
				}
				diagnosis.setAttribute(XMLExporter.ATTR_CODE, code);
			}
			element.addContent(diagnosis);
		}

		XMLExporterTreatment ret = new XMLExporterTreatment(element);
		ret.diagnoses = diagnosen;
		
		return ret;
	}
	
	private static List<IDiagnose> getDiagnosen(Rechnung rechnung){
		ArrayList<IDiagnose> ret = new ArrayList<IDiagnose>();
		List<Konsultation> lb = rechnung.getKonsultationen();
		for (Konsultation b : lb) {
			List<IDiagnose> ld = b.getDiagnosen();
			for (IDiagnose dg : ld) {
				String dgc = dg.getCode();
				if (dgc != null) {
					ret.add(dg);
				}
			}
		}
		return ret;
	}

	private static String match_type(final String type){
		if (type == null) {
			return XMLExporter.DISEASE;
		}
		if (type.equalsIgnoreCase(Fall.TYPE_DISEASE)) {
			return XMLExporter.DISEASE;
		}
		if (type.equalsIgnoreCase(Fall.TYPE_ACCIDENT)) {
			return "accident"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(Fall.TYPE_MATERNITY)) {
			return "maternity"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(Fall.TYPE_PREVENTION)) {
			return "prevention"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(Fall.TYPE_BIRTHDEFECT)) {
			return XMLExporter.BIRTHDEFECT;
		}
		return XMLExporter.DISEASE;
	}
	
	private static String match_diag(final String name){
		if (name.equalsIgnoreCase(XMLExporter.FREETEXT)) {
			return XMLExporter.FREETEXT;
		}
		if (name.equalsIgnoreCase("ICD-10")) { //$NON-NLS-1$
			return "ICD10"; //$NON-NLS-1$
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
		return BY_CONTRACT;
	}
	
	public List<IDiagnose> getDiagnoses(){
		if (diagnoses == null) {
			return Collections.emptyList();
		}
		return diagnoses;
	}
}
