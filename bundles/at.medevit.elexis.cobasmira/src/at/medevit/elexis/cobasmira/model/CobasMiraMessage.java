package at.medevit.elexis.cobasmira.model;

import org.apache.commons.lang3.StringUtils;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.rgw.tools.TimeTool;

public class CobasMiraMessage {
	private static Logger logger = LoggerFactory.getLogger(CobasMiraMessage.class);

	public static int BLOCK_TYPE_NOBLOCK_IDLEBLOCK = 0;
	public static int BLOCK_TYPE_PATIENT_RESULTS = 3;
	public static int BLOCK_TYPE_ERROR_MESSAGE = 71;
	public static int BLOCK_TYPE_RACK_INFORMATION = 70;

	public static int ELEXIS_RESULT_IGNORED = 112;
	public static int ELEXIS_RESULT_INTEGRATION_OK = 113;
	public static int ELEXIS_RESULT_PATIENT_NOT_FOUND = 114;
	public static int ELEXIS_RESULT_LABITEM_NOT_FOUND = 115;
	public static int ELEXIS_RESULT_CONTROL_OK = 116;
	public static final int ELEXIS_RESULT_RESULT_ALREADY_HERE = 117;
	public static int ELEXIS_RESULT_CONTROL_ERR = 118;

	private int instrumentCode;
	private int blockType;
	private int elexisState = 0; // What is the state of Elexis on this message?
	private String systemIdentification;
	private TimeTool entryDate;
	private LinkedList<CobasMiraPatientResult> patientResults = new LinkedList<CobasMiraPatientResult>();
	private CobasMiraErrorMessage errMessage = null;

	public CobasMiraMessage() {
		this.entryDate = new TimeTool();
	}

	public LinkedList<CobasMiraPatientResult> getPatientResults() {
		return patientResults;
	}

	public TimeTool getEntryDate() {
		return entryDate;
	}

	public int getBlockType() {
		return blockType;
	}

	public void setHeader(String in) {
		try {
			String header = in.trim();
			instrumentCode = Integer.parseInt(header.substring(0, 2));
			systemIdentification = header.substring(3, 19).trim();
			blockType = Integer.parseInt(header.substring(20, 22));
			logger.debug("HEADER instrumentCode/sysId/blockType: " + instrumentCode + "/" + systemIdentification + "/"
					+ blockType);
		} catch (NumberFormatException e) {
			logger.error("Error parsing String: " + in + " due to ", e);
		}

	}

	public void setText(String in) {
		String[] results = in.trim().split(StringUtils.LF);
		logger.debug("setText(), blockType=" + blockType + " results.length: " + results.length);
		for (int i = 0; i < results.length; i++) {
			logger.debug("(for) i:" + i + StringUtils.SPACE + blockType + "==" + BLOCK_TYPE_PATIENT_RESULTS);
			if (blockType == BLOCK_TYPE_PATIENT_RESULTS) {
				logger.debug("Adding patientResult: " + results[i]);
				patientResults.add(new CobasMiraPatientResult(results[i]));
			} else if (blockType == BLOCK_TYPE_ERROR_MESSAGE) {
				logger.debug("Got error Message");
				errMessage = new CobasMiraErrorMessage(results[i]);
			} else {
				logger.warn("No valid Message type.");
			}
		}
	}

	/**
	 *
	 * @return Block type string for this CobasMiraMessage
	 */
	public String getBlockTypeString() {
		return CobasMiraMessage.getBlockTypeString(getBlockType());
	}

	/**
	 * @return: Beschreibung des Block-Types dieser Cobas Mira Message
	 */
	public static String getBlockTypeString(int blockType) {
		switch (blockType) {
		case 0:
			return "No block, idle block";
		case 1:
			return "Calibration and control results";
		case 3:
			return "Patient results";
		case 5:
			return "Quality control results";
		case 10:
			return "Worklist input";
		case 11:
			return "Worklist input confirmation";
		case 15:
			return "Worklist deletion";
		case 16:
			return "Worklist deletion confirmation";
		case 70:
			return "Rack information";
		case 71:
			return "Error message";
		case 94:
			return "Instrument control";
		case 95:
			return "Instrument initialization";
		default:
			return "Unknown block type";
		}
	}

	public int getNoPatientResults() {
		return patientResults.size();
	}

	public String getErrorMessageString() {
		if (errMessage != null)
			return errMessage.getErrorDescription();
		return StringUtils.EMPTY;
	}

	public int getElexisStatus() {
		return this.elexisState;
	}

	public void setElexisStatus(int elst) {
		elexisState = elst;
	}

	public String getSinglePatientResultInfo() {
		if (getNoPatientResults() == 1) {
			String testName = patientResults.get(0).getTestName();
			float testResult = patientResults.get(0).getConcentration();
			String patientID = patientResults.get(0).getPatientIdentification();
			return patientID + StringUtils.SPACE + testName + StringUtils.SPACE + testResult;
		}
		return "More than 1 result contained";
	}

	public int getInstrumentCode() {
		return instrumentCode;
	}

	public String getSystemIdentification() {
		return systemIdentification;
	}
}