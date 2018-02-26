package ch.elexis.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.tarmedprefs.PreferenceConstants;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TarmedLimitation {
	
	private int amount;
	
	private String per;
	private String operator;
	
	private LimitationUnit limitationUnit;
	private int limitationAmount;
	
	private int electronicBilling;
	
	private boolean skip = false;
	
	private TarmedLeistung tarmedLeistung;
	private TarmedGroup tarmedGroup;
	
	public enum LimitationUnit {
			LOCATION_SESSION, SIDE, SESSION, PATIENT_SESSION, COVERAGE, STAY, TESTSERIES, PREGNANCY,
			BIRTH, RADIANTEXPOSURE, TRANSMITTAL, AUTOPSY, EXPERTISE, INTERVENTION_SESSION,
			CATEGORY_DAY, DAY, WEEK, MONTH, YEAR, JOINTREGION, REGION_SIDE, JOINTREGION_SIDE,
			MAINSERVICE, SESSION_YEAR, SESSION_COVERAGE, SESSION_PATIENT;
		
		public static LimitationUnit from(int parseInt){
			switch (parseInt) {
			case 6:
				return LOCATION_SESSION;
			case 7:
				return SESSION;
			case 8:
				return COVERAGE;
			case 9:
				return PATIENT_SESSION;
			case 10:
				return SIDE;
			case 11:
				return STAY;
			case 12:
				return TESTSERIES;
			case 13:
				return PREGNANCY;
			case 14:
				return BIRTH;
			case 15:
			case 31:
				return RADIANTEXPOSURE;
			case 16:
				return TRANSMITTAL;
			case 17:
				return AUTOPSY;
			case 18:
				return EXPERTISE;
			case 19:
				return INTERVENTION_SESSION;
			case 20:
				return CATEGORY_DAY;
			case 21:
				return DAY;
			case 22:
				return WEEK;
			case 23:
				return MONTH;
			case 26:
				return YEAR;
			case 40:
				return JOINTREGION;
			case 41:
				return REGION_SIDE;
			case 42:
				return JOINTREGION_SIDE;
			case 45:
				return MAINSERVICE;
			case 51:
				return SESSION_YEAR;
			case 52:
				return SESSION_COVERAGE;
			case 53:
			case 54:
				return SESSION_PATIENT;
			}
			return null;
		}
		
	}
	
	/**
	 * Factory method for creating {@link TarmedLimitation} objects of {@link TarmedLeistung}
	 * limitations.
	 * 
	 * @param limitation
	 * @return
	 */
	public static TarmedLimitation of(String limitation){
		TarmedLimitation ret = new TarmedLimitation();
		
		String[] parts = limitation.split(","); //$NON-NLS-1$
		if (parts.length >= 5) {
			if (parts[0] != null && !parts[0].isEmpty()) {
				ret.operator = parts[0].trim();
			}
			if (parts[1] != null && !parts[1].isEmpty()) {
				ret.amount = Float.valueOf(parts[1].trim()).intValue();
			}
			if (parts[2] != null && !parts[2].isEmpty()) {
				ret.limitationAmount = Float.valueOf(parts[2].trim()).intValue();
			}
			if (parts[3] != null && !parts[3].isEmpty()) {
				ret.per = parts[3].trim();
			}
			if (parts[4] != null && !parts[4].isEmpty()) {
				ret.limitationUnit = LimitationUnit.from(Float.valueOf(parts[4].trim()).intValue());
			}
		}
		if (parts.length >= 6) {
			if (parts[5] != null && !parts[5].isEmpty()) {
				ret.electronicBilling = Float.valueOf(parts[5].trim()).intValue();
			}
		} else {
			ret.electronicBilling = 0;
		}
		return ret;
	}
	
	public TarmedLimitation setTarmedLeistung(TarmedLeistung tarmedLeistung){
		this.tarmedLeistung = tarmedLeistung;
		return this;
	}
	
	public TarmedLimitation setTarmedGroup(TarmedGroup tarmedGroup){
		this.tarmedGroup = tarmedGroup;
		return this;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if (limitationUnit == LimitationUnit.SESSION) {
			sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
				+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perSession);
		} else if (limitationUnit == LimitationUnit.SIDE) {
			sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
				+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perSide);
		} else if (limitationUnit == LimitationUnit.DAY) {
			sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
				+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perDay);
		} else if (limitationUnit == LimitationUnit.WEEK) {
			if (tarmedGroup != null) {
				sb.append(
					String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_groupmax,
						tarmedGroup.getCode())
						+ amount
						+ String.format(
							ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perWeeks,
							limitationAmount));
			} else {
				sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
					+ String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perWeeks,
						limitationAmount));
			}
		} else if (limitationUnit == LimitationUnit.MONTH) {
			if (tarmedGroup != null) {
				sb.append(
					String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_groupmax,
						tarmedGroup.getCode())
						+ amount
						+ String.format(
							ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perMonth,
							limitationAmount));
			} else {
				sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
					+ String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perMonth,
						limitationAmount));
			}
		} else if (limitationUnit == LimitationUnit.YEAR) {
			if (tarmedGroup != null) {
				sb.append(
					String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_groupmax,
						tarmedGroup.getCode())
						+ amount
						+ String.format(
							ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perYears,
							limitationAmount));
			} else {
				sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
					+ String.format(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perYears,
						limitationAmount));
			}
		} else if (limitationUnit == LimitationUnit.COVERAGE) {
			sb.append(ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_codemax + amount
				+ ch.elexis.arzttarife_schweiz.Messages.TarmedOptifier_perCoverage);
		} else {
			sb.append("amount " + amount + "x unit " + limitationAmount + "x" + limitationUnit);
		}
		return sb.toString();
	}
	
	public boolean isTestable(){
		return limitationUnit == LimitationUnit.SIDE || limitationUnit == LimitationUnit.SESSION
			|| limitationUnit == LimitationUnit.DAY || limitationUnit == LimitationUnit.WEEK
			|| limitationUnit == LimitationUnit.MONTH || limitationUnit == LimitationUnit.YEAR
			|| limitationUnit == LimitationUnit.COVERAGE;
	}
	
	public Result<IVerrechenbar> test(Konsultation kons, Verrechnet newVerrechnet){
		if (limitationUnit == LimitationUnit.SIDE || limitationUnit == LimitationUnit.SESSION) {
			return testSideOrSession(kons, newVerrechnet);
		} else if (limitationUnit == LimitationUnit.DAY) {
			return testDay(kons, newVerrechnet);
		} else if (limitationUnit == LimitationUnit.WEEK || limitationUnit == LimitationUnit.MONTH
			|| limitationUnit == LimitationUnit.YEAR) {
			return testDuration(kons, newVerrechnet);
		} else if (limitationUnit == LimitationUnit.COVERAGE) {
			return testCoverage(kons, newVerrechnet);
		}
		return new Result<IVerrechenbar>(null);
	}
	
	private Result<IVerrechenbar> testCoverage(Konsultation kons, Verrechnet verrechnet){
		Result<IVerrechenbar> ret = new Result<IVerrechenbar>(null);
		if (shouldSkipTest()) {
			return ret;
		}
		if (operator.equals("<=")) {
			if (tarmedGroup == null) {
				List<Verrechnet> verrechnetByCoverage =
					getVerrechnetByCoverageAndCode(kons, tarmedLeistung.getCode());
				if (getVerrechnetCount(verrechnetByCoverage) > amount) {
					ret = new Result<IVerrechenbar>(Result.SEVERITY.WARNING,
						TarmedOptifier.KUMULATION, toString(), null, false);
				}
			} else {
				List<Verrechnet> allVerrechnetOfGroup = new ArrayList<>();
				List<String> serviceCodes = tarmedGroup.getServices();
				for (String code : serviceCodes) {
					allVerrechnetOfGroup.addAll(getVerrechnetByCoverageAndCode(kons, code));
				}
				if (getVerrechnetCount(allVerrechnetOfGroup) > amount) {
					ret = new Result<IVerrechenbar>(Result.SEVERITY.WARNING,
						TarmedOptifier.KUMULATION, toString(), null, false);
				}
			}
		}
		return ret;
	}
	
	private Result<IVerrechenbar> testDuration(Konsultation kons, Verrechnet verrechnet){
		Result<IVerrechenbar> ret = new Result<IVerrechenbar>(null);
		if (shouldSkipTest()) {
			return ret;
		}
		if (operator.equals("<=")) {
			if (tarmedGroup == null) {
				List<Verrechnet> verrechnetByMandant = getVerrechnetByMandantAndCodeDuring(kons,
					verrechnet.getVerrechenbar().getCode());
				if (getVerrechnetCount(verrechnetByMandant) > amount) {
					ret = new Result<IVerrechenbar>(Result.SEVERITY.WARNING,
						TarmedOptifier.KUMULATION, toString(), null, false);
				}
			} else {
				List<Verrechnet> allVerrechnetOfGroup = new ArrayList<>();
				List<String> serviceCodes = tarmedGroup.getServices();
				for (String code : serviceCodes) {
					allVerrechnetOfGroup.addAll(getVerrechnetByMandantAndCodeDuring(kons, code));
				}
				if (getVerrechnetCount(allVerrechnetOfGroup) > amount) {
					ret = new Result<IVerrechenbar>(Result.SEVERITY.WARNING,
						TarmedOptifier.KUMULATION, toString(), null, false);
				}
			}
		}
		return ret;
	}
	
	private int getVerrechnetCount(List<Verrechnet> verrechnete){
		int ret = 0;
		for (Verrechnet verrechnet : verrechnete) {
			ret += verrechnet.getZahl();
		}
		return ret;
	}
	
	// @formatter:off
	private static final String VERRECHNET_BYMANDANT_ANDCODE_DURING = "SELECT leistungen.ID FROM leistungen, behandlungen, faelle"
	+ " WHERE leistungen.deleted = '0'" 
	+ " AND leistungen.deleted = behandlungen.deleted"
	+ " AND leistungen.BEHANDLUNG = behandlungen.ID"
	+ " AND leistungen.KLASSE = 'ch.elexis.data.TarmedLeistung'"
	+ " AND faelle.ID = behandlungen.fallID"
	+ " AND faelle.PatientID = ?"
	+ " AND leistungen.LEISTG_CODE like ?"
	+ " AND behandlungen.Datum >= ?"
	+ " AND behandlungen.MandantID = ?";
	// @formatter:on
	
	private List<Verrechnet> getVerrechnetByMandantAndCodeDuring(Konsultation kons, String code){
		LocalDate fromDate = getDuringStartDate(kons);
		Mandant mandant = kons.getMandant();
		List<Verrechnet> ret = new ArrayList<>();
		if (fromDate != null && mandant != null) {
			PreparedStatement pstm = PersistentObject.getDefaultConnection()
				.getPreparedStatement(VERRECHNET_BYMANDANT_ANDCODE_DURING);
			try {
				pstm.setString(1, kons.getFall().getPatient().getId());
				pstm.setString(2, code + "%");
				pstm.setString(3, fromDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
				pstm.setString(4, mandant.getId());
				ResultSet resultSet = pstm.executeQuery();
				while (resultSet.next()) {
					ret.add(Verrechnet.load(resultSet.getString(1)));
				}
				resultSet.close();
			} catch (SQLException e) {
				LoggerFactory.getLogger(getClass()).error("Error during lookup", e);
			} finally {
				PersistentObject.getDefaultConnection().releasePreparedStatement(pstm);
			}
		}
		return ret;
	}
	
	// @formatter:off
	private static final String VERRECHNET_BYCOVERAGE_ANDCODE = "SELECT leistungen.ID FROM leistungen, behandlungen"
	+ " WHERE leistungen.deleted = '0'" 
	+ " AND leistungen.deleted = behandlungen.deleted"
	+ " AND leistungen.BEHANDLUNG = behandlungen.ID"
	+ " AND leistungen.KLASSE = 'ch.elexis.data.TarmedLeistung'"
	+ " AND leistungen.LEISTG_CODE like ?"
	+ " AND behandlungen.FallID = ?";
	// @formatter:on
	
	private List<Verrechnet> getVerrechnetByCoverageAndCode(Konsultation kons, String code){
		List<Verrechnet> ret = new ArrayList<>();
		if (kons != null && kons.getFall() != null) {
			PreparedStatement pstm = PersistentObject.getDefaultConnection()
				.getPreparedStatement(VERRECHNET_BYCOVERAGE_ANDCODE);
			try {
				pstm.setString(1, code + "%");
				pstm.setString(2, kons.getFall().getId());
				ResultSet resultSet = pstm.executeQuery();
				while (resultSet.next()) {
					ret.add(Verrechnet.load(resultSet.getString(1)));
				}
				resultSet.close();
			} catch (SQLException e) {
				LoggerFactory.getLogger(getClass()).error("Error during lookup", e);
			} finally {
				PersistentObject.getDefaultConnection().releasePreparedStatement(pstm);
			}
		}
		return ret;
	}
	
	private LocalDate getDuringStartDate(Konsultation kons) {
		LocalDate konsDate = new TimeTool(kons.getDatum()).toLocalDate();
		LocalDate ret = null;
		if (limitationUnit == LimitationUnit.WEEK) {
			ret = konsDate.minus(limitationAmount, ChronoUnit.WEEKS);
		} else if (limitationUnit == LimitationUnit.MONTH) {
			ret = konsDate.minus(limitationAmount, ChronoUnit.MONTHS);
		} else if (limitationUnit == LimitationUnit.YEAR) {
			ret = konsDate.minus(limitationAmount, ChronoUnit.YEARS);
		}
		if (tarmedLeistung != null && ret != null) {
			LocalDate leistungDate = tarmedLeistung.getGueltigVon().toLocalDate();
			if (ret.isBefore(leistungDate)) {
				ret = leistungDate;
			}
		}
		return ret;
	}
	
	private Result<IVerrechenbar> testDay(Konsultation kons, Verrechnet verrechnet){
		Result<IVerrechenbar> ret = new Result<IVerrechenbar>(null);
		if (shouldSkipTest()) {
			return ret;
		}
		if (limitationAmount == 1 && operator.equals("<=")) {
			if (verrechnet.getZahl() > amount) {
				ret = new Result<IVerrechenbar>(Result.SEVERITY.WARNING, TarmedOptifier.KUMULATION,
					toString(), null, false);
			}
		}
		return ret;
	}
	
	private Result<IVerrechenbar> testSideOrSession(Konsultation kons, Verrechnet verrechnet){
		Result<IVerrechenbar> ret = new Result<IVerrechenbar>(null);
		if (shouldSkipTest()) {
			return ret;
		}
		if (limitationAmount == 1 && operator.equals("<=")) {
			if (verrechnet.getZahl() > amount) {
				if (limitationUnit == LimitationUnit.SESSION) {
					ret = new Result<IVerrechenbar>(Result.SEVERITY.WARNING,
						TarmedOptifier.KUMULATION, toString(), null, false);
				} else if (limitationUnit == LimitationUnit.SIDE) {
					ret = new Result<IVerrechenbar>(Result.SEVERITY.WARNING,
						TarmedOptifier.KUMULATION, toString(), null, false);
				}
			}
		}
		return ret;
	}
	
	private boolean shouldSkipTest(){
		if (skip) {
			return skip;
		}
		return shouldSkipElectronicBilling();
	}
	
	private boolean shouldSkipElectronicBilling(){
		if (electronicBilling > 0) {
			if (CoreHub.mandantCfg != null
				&& CoreHub.mandantCfg.get(PreferenceConstants.BILL_ELECTRONICALLY, false)) {
				return true;
			}
		}
		return false;
	}
	
	public LimitationUnit getLimitationUnit(){
		return limitationUnit;
	}
	
	public int getAmount(){
		return amount;
	}
	
	public void setSkip(boolean value){
		this.skip = true;
	}
}
