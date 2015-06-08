package at.medevit.elexis.cobasmira.model;

public class CobasMiraMappingLabitem {
	// CSV File
	//TestNameCM;TestNameShort;TestName;LaborwertID;refM;refW;noDecPlaces
	private String testNameCM;
	private String testNameShort;
	private String testName;
	private String laborwertID;
	private String refM;
	private String refW;
	private String noDecPlaces;
	
	public String getTestNameCM(){
		return testNameCM;
	}
	
	public void setTestNameCM(String testNameCM){
		this.testNameCM = testNameCM;
	}
	
	public String getTestNameShort(){
		return testNameShort;
	}
	
	public void setTestNameShort(String testNameShort){
		this.testNameShort = testNameShort;
	}
	
	public String getTestName(){
		return testName;
	}
	
	public void setTestName(String testName){
		this.testName = testName;
	}
	
	public String getLaborwertID(){
		return laborwertID;
	}
	
	public void setLaborwertID(String laborwertID){
		this.laborwertID = laborwertID;
	}
	
	public String getRefM(){
		return refM;
	}
	
	public void setRefM(String refM){
		this.refM = refM;
	}
	
	public String getRefW(){
		return refW;
	}
	
	public void setRefW(String refW){
		this.refW = refW;
	}
	
	public String getNoDecPlaces(){
		return noDecPlaces;
	}
	
	public void setNoDecPlaces(String noDecPlaces){
		this.noDecPlaces = noDecPlaces;
	}
	
}
