package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;

public class TarmedExclusion {

	private String slaveCode;
	private TarmedKumulationArt slaveType;
	private boolean validSide;

	public TarmedExclusion(ITarmedKumulation kumulation) {
		slaveCode = kumulation.getSlaveCode();
		slaveType = kumulation.getSlaveArt();
		validSide = "1".equals(kumulation.getValidSide());
	}

	public boolean isMatching(TarmedLeistung tarmedLeistung, LocalDate date) {
		if (slaveType == TarmedKumulationArt.CHAPTER) {
			return isMatchingChapter(tarmedLeistung);
		} else if (slaveType == TarmedKumulationArt.SERVICE) {
			return isMatchingService(tarmedLeistung);
		} else if (slaveType == TarmedKumulationArt.GROUP) {
			List<String> groups = tarmedLeistung.getServiceGroups(date);
			return groups.contains(slaveCode);
		}
		return false;
	}

	private boolean isMatchingService(ITarmedLeistung tarmedLeistung) {
		return slaveCode.equals(tarmedLeistung.getCode());
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

	public boolean isValidSide() {
		return validSide;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TarmedKumulationArt.toString(slaveType)).append(StringUtils.SPACE).append(slaveCode);
		return sb.toString();
	}
}
