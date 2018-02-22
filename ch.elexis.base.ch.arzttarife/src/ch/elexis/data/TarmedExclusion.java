package ch.elexis.data;

import java.util.List;

import ch.elexis.data.TarmedKumulation.TarmedKumulationType;
import ch.rgw.tools.TimeTool;

public class TarmedExclusion {
	
	private String slaveCode;
	private TarmedKumulationType slaveType;
	private boolean validSide;
	
	public TarmedExclusion(TarmedKumulation kumulation){
		slaveCode = kumulation.getSlaveCode();
		slaveType = TarmedKumulationType.ofArt(kumulation.getSlaveArt());
		validSide = "1".equals(kumulation.getValidSide());
	}
	
	public boolean isMatching(TarmedLeistung tarmedLeistung, TimeTool date){
		if (slaveType == TarmedKumulationType.CHAPTER) {
			return isMatchingChapter(tarmedLeistung);
		} else if (slaveType == TarmedKumulationType.SERVICE) {
			return isMatchingService(tarmedLeistung);
		} else if (slaveType == TarmedKumulationType.GROUP) {
			List<String> groups = tarmedLeistung.getServiceGroups(date);
			return groups.contains(slaveCode);
		}
		return false;
	}
	
	private boolean isMatchingService(TarmedLeistung tarmedLeistung){
		return slaveCode.equals(tarmedLeistung.getCode());
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
		if (slaveType != TarmedKumulationType.GROUP) {
			return false;
		}
		return slaveCode.equals(tarmedGroup.getCode());
	}
	
	public boolean isValidSide(){
		return validSide;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(TarmedKumulationType.toString(slaveType)).append(" ").append(slaveCode);
		return sb.toString();
	}
}
