package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt;

public class TardocExclusion {

	private String slaveCode;
	private TardocKumulationArt slaveType;
	private boolean validSide;

	public TardocExclusion(ITardocKumulation kumulation) {
		slaveCode = kumulation.getSlaveCode();
		slaveType = kumulation.getSlaveArt();
		validSide = "1".equals(kumulation.getValidSide());
	}

	public boolean isMatching(TardocLeistung tarmedLeistung, LocalDate date) {
		if (slaveType == TardocKumulationArt.CHAPTER) {
			return isMatchingChapter(tarmedLeistung);
		} else if (slaveType == TardocKumulationArt.SERVICE) {
			return isMatchingService(tarmedLeistung);
		} else if (slaveType == TardocKumulationArt.GROUP) {
			List<String> groups = tarmedLeistung.getServiceGroups(date);
			return groups.contains(slaveCode);
		}
		return false;
	}

	private boolean isMatchingService(ITardocLeistung tarmedLeistung) {
		return slaveCode.equals(tarmedLeistung.getCode());
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

	public boolean isValidSide() {
		return validSide;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TardocKumulationArt.toString(slaveType)).append(StringUtils.SPACE).append(slaveCode);
		return sb.toString();
	}
}
