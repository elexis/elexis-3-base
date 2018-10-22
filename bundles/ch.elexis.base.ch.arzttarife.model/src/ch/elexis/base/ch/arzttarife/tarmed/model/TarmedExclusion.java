package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.util.List;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
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
			List<String> groups = tarmedLeistung.getServiceGroups(date.toLocalDate());
			return groups.contains(slaveCode);
		}
		return false;
	}
	
	private boolean isMatchingService(ITarmedLeistung tarmedLeistung){
		return slaveCode.equals(tarmedLeistung.getCode());
	}
	
	private boolean isMatchingChapter(ITarmedLeistung tarmedLeistung){
		if (slaveCode.equals(tarmedLeistung.getCode())) {
			return true;
		} else {
			ITarmedLeistung parent = tarmedLeistung.getParent();
			if (parent != null) {
				return isMatchingChapter(parent);
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
