package at.medevit.elexis.cobasmira.model;

public class CobasMiraCodes {
	/**
	 *
	 * @param rt
	 * @return Result Type String
	 */
	public static String getResultTypeString(char rt) {
		switch (rt) {
		case 'O':
			return "original result";
		case 'A':
			return "additional result";
		default:
			return "unknown";
		}
	}

	/**
	 *
	 * @param flag
	 * @return Flag Type String
	 */
	public static String getFlagString(char flag) {
		switch (flag) {
		case 'N':
			return "no flag";
		case 'C':
			return "calibrator/control error";
		case 'D':
			return "disabled";
		case 'R':
			return "recalculated result";
		case 'U':
			return "unit change";
		case (char) 135:
			return "concentration";
		case (char) 136:
			return "dilution";
		case 'A':
			return "accepted result";
		default:
			return "unknown";
		}
	}

	/**
	 * @return Beschreibung des angegebenen Arbeitslisten-Typus
	 */
	public static String getWorklistTypeString(char worklistType) {
		switch (worklistType) {
		case 'R':
			return "routine";
		case 'S':
			return "stat with stat rack";
		case 'M':
			return "stat with routine rack";
		default:
			return "unknown worklist type";
		}
	}

	/**
	 *
	 * @param unit
	 * @return Unit Type String
	 */
	public static String getUnitString(int unit) {
		switch (unit) {
		case 2:
			return "mol/L";
		case 3:
			return "mmol/L";
		case 4:
			return "µmol/L";
		case 5:
			return "nmol/L";
		case 6:
			return "pmol/L";
		case 7:
			return "g/L";
		case 8:
			return "mg/L";
		case 9:
			return "µg/L";
		case 10:
			return "ng/L";
		case 11:
			return "g/dL";
		case 12:
			return "mg/dL";
		case 13:
			return "µg/dL";
		case 14:
			return "ng/dL";
		case 15:
			return "mg/mL";
		case 16:
			return "µg/mL";
		case 17:
			return "ng/mL";
		case 18:
			return "pg/mL";
		case 19:
			return "µkat/L";
		case 20:
			return "nkat/L";
		case 21:
			return "U/L";
		case 22:
			return "mU/L";
		case 23:
			return "U/mL";
		case 24:
			return "mU/mL";
		case 25:
			return "lU/L";
		case 26:
			return "mlU/L";
		case 27:
			return "lU/mL";
		case 28:
			return "mlU/mL";
		case 29:
			return "mval/L";
		case 30:
			return "meq/L";
		case 31:
			return "ΔA";
		case 32:
			return "ΔA/min";
		case 33:
			return "%";
		case 34:
			return "Δ%";
		case 35:
			return "s";
		case 36:
			return "kU/L";
		case 37:
			return "klU/L";
		default:
			return "unknown";
		}
	}

	/**
	 * @return Remark Type String
	 */
	public static String getRemarkString(int remark) {
		switch (remark) {
		case 0:
			return "no remark";
		case 1:
			return "high absorbance";
		case 2:
			return "non linear";
		case 3:
			return "noise";
		case 4:
			return "sign";
		case 5:
			return "reaction limit";
		case 6:
			return "high activity";
		case 7:
			return "< test range";
		case 8:
			return "> test range";
		case 9:
			return "< reagent range";
		case 10:
			return "> reagent range";
		case 11:
			return "< blank range";
		case 12:
			return "> blank range";
		case 13:
			return "< deviation";
		case 14:
			return "> deviation";
		case 15:
			return "< confidence limit";
		case 16:
			return "> confidence limit";
		case 17:
			return "antigen excess";
		case 18:
			return "sample limit";
		case 19:
			return "< normal range";
		case 20:
			return "> normal range";
		case 21:
			return "calculation error";
		case 22:
			return "antigen calculation error";
		case 23:
			return "< calculation range";
		case 24:
			return "> calculation range";
		case 31:
			return "< slope range";
		case 32:
			return "> slope range";
		case 34:
			return "unstable reference";
		case 35:
			return "unstable";
		case 36:
			return "< test range (check = off)";
		case 37:
			return "> test range (check = off)";
		default:
			return "unknown";
		}
	}

	/**
	 *
	 * @param evmodel
	 * @return Evalutation Model String
	 */
	public static String getEvaluationModelString(int evmodel) {
		switch (evmodel) {
		case 1:
			return "linear (photometry)";
		case 2:
			return "linear (ion selective electrode)";
		case 11:
			return "4 parameter log/logit";
		case 12:
			return "5 parameter log/logit";
		case 13:
			return "5 parameter exponential";
		case 14:
			return "5 parameter polynomial (discontinued)";
		case 15:
			return "linear interpolation";
		case 16:
			return "linear regression";
		case 17:
			return "factor";
		default:
			return "unknown";
		}
	}

}
