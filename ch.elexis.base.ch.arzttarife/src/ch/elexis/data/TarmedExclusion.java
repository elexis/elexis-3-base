package ch.elexis.data;

import java.util.List;

import ch.rgw.tools.TimeTool;

public class TarmedExclusion {
	
	public enum TarmedExclusionType {
			SERVICE("L"), GROUP("G"), CHAPTER("K");
		
		private String art;
		
		TarmedExclusionType(String art){
			this.art = art;
		}
		
		public String getArt(){
			return art;
		}
		
		public static TarmedExclusionType ofArt(String slaveArt){
			if ("L".equals(slaveArt)) {
				return SERVICE;
			} else if ("G".equals(slaveArt)) {
				return GROUP;
			} else if ("K".equals(slaveArt)) {
				return CHAPTER;
			}
			return null;
		}
		
		public static String toString(TarmedExclusionType type){
			if (type == SERVICE) {
				return "Leistung";
			} else if (type == GROUP) {
				return "Gruppe";
			} else if (type == CHAPTER) {
				return "Kapitel";
			}
			return null;
		}
	}
	
	private String slaveCode;
	private TarmedExclusionType slaveType;
	
	public TarmedExclusion(TarmedKumulation kumulation){
		slaveCode = kumulation.getSlaveCode();
		slaveType = TarmedExclusionType.ofArt(kumulation.getSlaveArt());
	}
	
	public boolean isMatching(TarmedLeistung tarmedLeistung, TimeTool date){
		if (slaveType == TarmedExclusionType.CHAPTER) {
			return isMatchingChapter(tarmedLeistung);
		} else if (slaveType == TarmedExclusionType.SERVICE) {
			return slaveCode.equals(tarmedLeistung.getCode());
		} else if (slaveType == TarmedExclusionType.GROUP) {
			List<String> groups = tarmedLeistung.getServiceGroups(date);
			return groups.contains(slaveCode);
		}
		return false;
	}
	
	private boolean isMatchingChapter(TarmedLeistung tarmedLeistung){
		if (slaveCode.equals(tarmedLeistung.getCode())) {
			return true;
		} else {
			String parentId = tarmedLeistung.getParent();
			if (parentId != null && !parentId.equals("NIL")) {
				return isMatchingChapter(TarmedLeistung.load(parentId));
			} else {
				return false;
			}
		}
	}
	
	public boolean isMatching(TarmedGroup tarmedGroup){
		if (slaveType != TarmedExclusionType.GROUP) {
			return false;
		}
		return slaveCode.equals(tarmedGroup.getCode());
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(TarmedExclusionType.toString(slaveType)).append(" ").append(slaveCode);
		return sb.toString();
	}
}
