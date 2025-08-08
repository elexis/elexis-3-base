package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt;
import ch.rgw.tools.TimeTool;

public class TardocExclusive {

	private String slaveCode;
	private TardocKumulationArt slaveType;

	public TardocExclusive(ITardocKumulation kumulation) {
		slaveCode = kumulation.getSlaveCode();
		slaveType = kumulation.getSlaveArt();
	}

	public boolean isMatching(ITardocLeistung tarmedLeistung, TimeTool date) {
		if (slaveType == TardocKumulationArt.CHAPTER) {
			return isMatchingChapter(tarmedLeistung);
		} else if (slaveType == TardocKumulationArt.SERVICE) {
			return slaveCode.equals(tarmedLeistung.getCode());
		} else if (slaveType == TardocKumulationArt.GROUP) {
			List<String> groups = tarmedLeistung.getServiceGroups(date.toLocalDate());
			return groups.contains(slaveCode);
		} else if (slaveType == TardocKumulationArt.BLOCK) {
			List<String> blocks = tarmedLeistung.getServiceBlocks(date.toLocalDate());
			return blocks.contains(slaveCode);
		}
		return false;
	}

	private boolean isMatchingChapter(ITardocLeistung tarmedLeistung) {
		if (slaveCode.equals(tarmedLeistung.getCode())) {
			return true;
		} else {
			ITardocLeistung parent = tarmedLeistung.getParent();
			if (parent != null) {
				return isMatchingChapter(parent);
			} else {
				return false;
			}
		}
	}

	public boolean isMatching(TardocGroup tarmedGroup) {
		if (slaveType != TardocKumulationArt.GROUP) {
			return false;
		}
		return slaveCode.equals(tarmedGroup.getCode());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TardocKumulationArt.toString(slaveType)).append(StringUtils.SPACE).append(slaveCode);
		return sb.toString();
	}

	public TardocKumulationArt getSlaveType() {
		return slaveType;
	}
}
