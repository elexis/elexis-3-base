package ch.elexis.base.ch.arzttarife.tarmed.model;

public enum TarmedKumulationType {
		SERVICE("L"), GROUP("G"), CHAPTER("K"), BLOCK("B");
	
	private String art;
	
	TarmedKumulationType(String art){
		this.art = art;
	}
	
	public String getArt(){
		return art;
	}
	
	public static TarmedKumulationType ofArt(String slaveArt){
		if ("L".equals(slaveArt)) {
			return SERVICE;
		} else if ("G".equals(slaveArt)) {
			return GROUP;
		} else if ("K".equals(slaveArt)) {
			return CHAPTER;
		} else if ("B".equals(slaveArt)) {
			return BLOCK;
		}
		return null;
	}
	
	public static String toString(TarmedKumulationType type){
		if (type == SERVICE) {
			return "Leistung";
		} else if (type == GROUP) {
			return "Gruppe";
		} else if (type == CHAPTER) {
			return "Kapitel";
		} else if (type == BLOCK) {
			return "Block";
		}
		return null;
	}
}
