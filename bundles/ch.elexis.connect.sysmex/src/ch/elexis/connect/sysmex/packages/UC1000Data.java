package ch.elexis.connect.sysmex.packages;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.LabResultConstants;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class UC1000Data extends AbstractUrinData {
	
	private TimeTool date;
	private String patId;
	private ResultInfo uro;
	private ResultInfo bid;
	private ResultInfo bil;
	private ResultInfo ket;
	private ResultInfo glu;
	private ResultInfo pro;
	private ResultInfo ph;
	private ResultInfo nit;
	private ResultInfo leu;
	private ResultInfo sg;
	private ResultInfo cre;
	private ResultInfo alb;
	private ResultInfo pcr;
	private ResultInfo acr;
	private ResultInfo sgr;
	private ResultInfo color;
	private ResultInfo tur;
	
	public int getSize(){
		return 518;
	}
	
	protected TimeTool getDate(final String content){
		int year = Integer.parseInt(content.substring(49, 53));
		int month = Integer.parseInt(content.substring(54, 56));
		int day = Integer.parseInt(content.substring(57, 59));
		TimeTool timetool = new TimeTool();
		timetool.set(year, month - 1, day);
		return timetool;
	}
	
	@Override
	protected String getPatientId(String content){
		String patId = content.substring(0, 14).trim();
		if (patId != null && !patId.isEmpty()) {
			// remove leading zeros
			try {
				patId = Integer.valueOf(patId).toString();
			} catch (NumberFormatException e) {
				// non numeric, but ok
			}
		}
		return patId;
	}
	
	protected Value getValue(final String paramName) throws PackageException{
		return Value.getValueUC1000(paramName);
	}
	
	public ResultInfo getResultInfo(final String paramName){
		switch (paramName.toLowerCase()) {
		case "uro":
			return uro;
		case "bid":
			return bid;
		case "bil":
			return bil;
		case "ket":
			return ket;
		case "glu":
			return glu;
		case "pro":
			return pro;
		case "ph":
			return ph;
		case "nit":
			return nit;
		case "leu":
			return leu;
		case "sg":
			return sg;
		case "cre":
			return cre;
		case "alb":
			return alb;
		}
		throw new IllegalStateException("Unknown parameter name [" + paramName + "]");
	}
	
	@Override
	protected int getDataIndex(){
		return 69;
	}
	
	@Override
	public String getPatientId(){
		return patId;
	}
	
	@Override
	public void write(Patient patient) throws PackageException{
		if (uro.isAnalyzed()) {
			write(uro, getValue("URO"), patient);
		}
		if (bid.isAnalyzed()) {
			if (bid.getSemiQualitativValue().indexOf(".") != -1) {
				write(bid, getValue("HEM"), patient);
			} else {
				write(bid, getValue("RBC"), patient);
			}
		}
		if (bil.isAnalyzed()) {
			write(bil, getValue("BIL"), patient);
		}
		if (ket.isAnalyzed()) {
			write(ket, getValue("KET"), patient);
		}
		if (glu.isAnalyzed()) {
			write(glu, getValue("GLU"), patient);
		}
		if (pro.isAnalyzed()) {
			write(pro, getValue("PRO"), patient);
		}
		if (ph.isAnalyzed()) {
			write(ph, getValue("PH"), patient);
		}
		if (nit.isAnalyzed()) {
			write(nit, getValue("NIT"), patient);
		}
		if (leu.isAnalyzed()) {
			write(leu, getValue("LEU"), patient);
		}
		if (sg.isAnalyzed()) {
			write(sg, getValue("SG"), patient);
		}
		if (cre.isAnalyzed()) {
			write(cre, getValue("CRE"), patient);
		}
		if (alb.isAnalyzed()) {
			write(alb, getValue("ALB"), patient);
		}
	}
	
	private void write(ResultInfo resultInfo, Value value, Patient patient){
		String result = "";
		String comment = "";
		if (StringUtils.isNotBlank(resultInfo.getSemiQualitativValue())) {
			result = resultInfo.getSemiQualitativValue();
			if (StringUtils.isNotBlank(resultInfo.getQualitativValue())) {
				comment = "Qualitativ Wert: " + resultInfo.getQualitativValue();
			}
		} else {
			result = resultInfo.getQualitativValue();
			if (StringUtils.isNotBlank(resultInfo.getSemiQualitativValue())) {
				comment = "Semiqualitativ Wert: " + resultInfo.getSemiQualitativValue();
			}
		}
		Integer patho = 0;
		if (StringUtils.isNotBlank(resultInfo.getQualitativValue())) {
			if (Character.isDigit(resultInfo.getQualitativValue().charAt(0))
				&& resultInfo.getQualitativValue().contains("+")) {
				patho |= LabResultConstants.PATHOLOGIC;
			}
		}
		value.fetchValue(patient, result, patho, date, comment);
	}
		
	@Override
	public void parse(String content){
		// Datum
		date = getDate(content);
		patId = getPatientId(content);
		
		uro = ResultInfo.parse(69, 101, content);
		bid = ResultInfo.parse(101, 133, content);
		bil = ResultInfo.parse(133, 165, content);
		ket = ResultInfo.parse(165, 197, content);
		glu = ResultInfo.parse(197, 229, content);
		pro = ResultInfo.parse(229, 261, content);
		ph = ResultInfo.parse(261, 293, content);
		nit = ResultInfo.parse(293, 325, content);
		leu = ResultInfo.parse(325, 357, content);
		sg = ResultInfo.parse(357, 389, content);
		cre = ResultInfo.parse(389, 421, content);
		alb = ResultInfo.parse(421, 453, content);
		pcr = ResultInfo.parse(453, 468, content);
		acr = ResultInfo.parse(468, 483, content);
		sgr = ResultInfo.parse(483, 498, content);
		color = ResultInfo.parse(498, 518, content);
		tur = ResultInfo.parse(498, 518, content);
	}
}
