package at.medevit.elexis.cobasmira.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobasMiraPatientResult {
	private static Logger logger = LoggerFactory.getLogger(CobasMiraPatientResult.class);

	// 0 3 5 8 10 20 30 40 50 55
	// | | | | | | | | | |
	// xx w tt nnnn ti scno iiiiiiiiii resulttttttt dp uu t f rrLF
	private int lineCode;
	private int testResultIndex;
	private int sampleCupNumber;
	private int noOfDigitsBehindDecimalPoint;
	private int unitCode;
	private int remark;

	private float concentration;

	private char worklistType;
	private char resultType;
	private char flag;

	private String testNumber;
	private String testName;
	private String patientIdentification;

	public CobasMiraPatientResult(String input) {
		try {
			if (Integer.parseInt(input.substring(0, 2)) == 20) {
				this.lineCode = 20;
				this.worklistType = input.charAt(3);
				this.testNumber = input.substring(5, 7).trim();
				this.testName = input.substring(8, 12).trim();
				this.testResultIndex = Integer.parseInt(input.substring(13, 15));
				this.sampleCupNumber = Integer.parseInt(input.substring(16, 20));
				this.patientIdentification = input.substring(21, 31).trim();
				this.concentration = Float.parseFloat(input.substring(32, 44));
				this.noOfDigitsBehindDecimalPoint = Integer.parseInt(input.substring(45, 47));
				this.unitCode = Integer.parseInt(input.substring(48, 50));
				this.resultType = input.charAt(51);
				this.flag = input.charAt(53);
				this.remark = Integer.parseInt(input.substring(55, 57));
			} else {
				logger.debug("LineCode is not 20 its " + input.substring(0, 2));
			}
		} catch (NumberFormatException e) {
			logger.warn("NumberFormatException: " + e.getMessage());
			return;
		} catch (IndexOutOfBoundsException e) {
			logger.warn("IndexOutOfBoundsException: " + e.getMessage());
			return;
		}

	}

	/**
	 * @return Beschreibung es angegebenen Zeilencodes
	 */
	public static String getLineCodeString(int lineCode) {
		switch (lineCode) {
		case 20:
			return "Patient Result";
		case 40:
			return "Raw data T1 to T4 (To measurements following a transfer)";
		case 41:
			return "Raw data A1 to A50 (cyclic measurements";
		case 42:
			return "Raw data of cuvette blank";
		case 45:
			return "Raw data from ISE";
		default:
			return "unknwon line code";
		}
	}

	public int getLineCode() {
		return lineCode;
	}

	public int getTestResultIndex() {
		return testResultIndex;
	}

	public int getSampleCupNumber() {
		return sampleCupNumber;
	}

	public int getNoOfDigitsBehindDecimalPoint() {
		return noOfDigitsBehindDecimalPoint;
	}

	public int getUnitCode() {
		return unitCode;
	}

	public int getRemark() {
		return remark;
	}

	public float getConcentration() {
		return concentration;
	}

	public char getWorklistType() {
		return worklistType;
	}

	public char getResultType() {
		return resultType;
	}

	public char getFlag() {
		return flag;
	}

	public String getTestNumber() {
		return testNumber;
	}

	public String getTestName() {
		return testName;
	}

	public String getPatientIdentification() {
		return patientIdentification;
	}
}
