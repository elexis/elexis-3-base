package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.util.List;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.rgw.tools.TimeTool;

public class TarmedExclusive {
	
	private String slaveCode;
	private TarmedKumulationType slaveType;
	
	public TarmedExclusive(TarmedKumulation kumulation){
		slaveCode = kumulation.getSlaveCode();
		slaveType = TarmedKumulationType.ofArt(kumulation.getSlaveArt());
	}
	
	public boolean isMatching(ITarmedLeistung tarmedLeistung, TimeTool date){
		if (slaveType == TarmedKumulationType.CHAPTER) {
			return isMatchingChapter(tarmedLeistung);
		} else if (slaveType == TarmedKumulationType.SERVICE) {
			return slaveCode.equals(tarmedLeistung.getCode());
		} else if (slaveType == TarmedKumulationType.GROUP) {
			List<String> groups = tarmedLeistung.getServiceGroups(date.toLocalDate());
			return groups.contains(slaveCode);
		} else if (slaveType == TarmedKumulationType.BLOCK) {
			List<String> blocks = tarmedLeistung.getServiceBlocks(date.toLocalDate());
			return blocks.contains(slaveCode);
		}
		return false;
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
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(TarmedKumulationType.toString(slaveType)).append(" ").append(slaveCode);
		return sb.toString();
	}
	
	public TarmedKumulationType getSlaveType(){
		return slaveType;
	}
}
