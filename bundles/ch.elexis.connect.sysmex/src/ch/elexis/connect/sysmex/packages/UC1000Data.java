package ch.elexis.connect.sysmex.packages;

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
			patId = Integer.valueOf(patId).toString();
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
		}
		throw new IllegalStateException("Unknown parameter name [" + paramName + "]");
	}
	
	@Override
	protected int getDataIndex(){
		return 69;
	}
	
	@Override
	public String getPatientId(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void write(Patient selectedPatient) throws PackageException{
		// TODO Auto-generated method stub
		
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
		sg = ResultInfo.parse(483, 498, content);
		color = ResultInfo.parse(498, 518, content);
		tur = ResultInfo.parse(498, 518, content);
	}
}
