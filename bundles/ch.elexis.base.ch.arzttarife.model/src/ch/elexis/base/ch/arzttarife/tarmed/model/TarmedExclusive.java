package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;
import ch.rgw.tools.TimeTool;

public class TarmedExclusive {

	private String slaveCode;
	private TarmedKumulationArt slaveType;

	public TarmedExclusive(ITarmedKumulation kumulation) {
		slaveCode = kumulation.getSlaveCode();
		slaveType = kumulation.getSlaveArt();
	}

	public boolean isMatching(ITarmedLeistung tarmedLeistung, TimeTool date) {
		if (slaveType == TarmedKumulationArt.CHAPTER) {
			return isMatchingChapter(tarmedLeistung);
		} else if (slaveType == TarmedKumulationArt.SERVICE) {
			return slaveCode.equals(tarmedLeistung.getCode());
		} else if (slaveType == TarmedKumulationArt.GROUP) {
			List<String> groups = tarmedLeistung.getServiceGroups(date.toLocalDate());
			return groups.contains(slaveCode);
		} else if (slaveType == TarmedKumulationArt.BLOCK) {
			List<String> blocks = tarmedLeistung.getServiceBlocks(date.toLocalDate());
			return blocks.contains(slaveCode);
		}
		return false;
	}

	private boolean isMatchingChapter(ITarmedLeistung tarmedLeistung) {
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

	public boolean isMatching(TarmedGroup tarmedGroup) {
		if (slaveType != TarmedKumulationArt.GROUP) {
			return false;
		}
		return slaveCode.equals(tarmedGroup.getCode());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TarmedKumulationArt.toString(slaveType)).append(StringUtils.SPACE).append(slaveCode);
		return sb.toString();
	}

	public TarmedKumulationArt getSlaveType() {
		return slaveType;
	}
}
