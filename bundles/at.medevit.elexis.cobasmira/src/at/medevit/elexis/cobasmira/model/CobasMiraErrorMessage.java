package at.medevit.elexis.cobasmira.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobasMiraErrorMessage {
	private static Logger logger = LoggerFactory.getLogger(CobasMiraErrorMessage.class);
	// 73 ernoLF
	int lineCode;
	int errorNumber;
	
	public CobasMiraErrorMessage(String input){
		try {
			this.lineCode = Integer.parseInt(input.substring(0, 2));
			this.errorNumber = Integer.parseInt(input.substring(3, 7));
			
			logger.debug("CobasMira Error: " + getErrorDescription(errorNumber));
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			logger.error("CobasMira - can't resolve error message", e);
		}
	}
	
	public String getErrorDescription(){
		return CobasMiraErrorMessage.getErrorDescription(errorNumber);
	}
	
	/**
	 * @param errorNumer
	 *            : Error Code Number
	 * @return Error Text
	 */
	public static String getErrorDescription(int errorNumber){
		switch (errorNumber) {
		case 1:
			return "PHOTOMETER OVERFLOW";
		case 2:
			return "PHOTOMETEROFFSET TOO LOW";
		case 3:
			return "PHOTOMETER OVERFLOW FIXED AGAIN";
		case 4:
			return "PHOTOMETER TIMING INVALID";
		case 5:
			return "PHOTOMETEROFFSET TOO HIGH";
		case 6:
			return "PHOTOMETER NO RESPONSE";
		case 7:
			return "PHOTOMETER NO INTERRUPT";
		case 8:
			return "TIMEOUT CYCLIC MEASUREMENT";
		case 9:
			return "TIMEOUT FILTER CHANGE";
		case 10:
			return "TIMEOUT TO MEASUREMENT";
		case 11:
			return "BACKGROUND OVERFLOW SAMPLE";
		case 12:
			return "BACKGROUND UNDERFLOW SAMPLE";
		case 13:
			return "BACKGROUND OVERFLOW REFERENCE";
		case 14:
			return "BACKGROUND UNDERFLOW REFERENCE";
		case 15:
			return "TRANSFER NOT INITIALIZED";
		case 16:
			return "P-SAMPLE NOT INITIALIZED";
		case 17:
			return "P-REAGENT NOT INITIALIZED";
		case 18:
			return "ANALYZER NOT INITIALIZED";
		case 19:
			return "FILTER NOT INITIIALIZED";
		case 20:
			return "X-MOTOR INIT NOT POSSIBLE";
		case 21:
			return "ANALYZER INIT NOT POSSIBLE";
		case 22:
			return "FILTER INIT NOT POSSIBLE";
		case 23:
			return "Y-MOTOR INIT NOT POSSIBLE";
		case 24:
			return "P-SAMPLE INIT NOT POSSIBLE";
		case 27:
			return "P-REAGENT INIT NOT POSSIBLE";
		case 29:
			return "ANALYZER COVER OPEN";
		case 30:
			return "TRANSFER AREA BLOCKED";
		case 31:
			return "ANALYZER STEP ERROR";
		case 32:
			return "FILTER STEP ERROR";
		case 33:
			return "Y-MOTOR STEP ERROR";
		case 34:
			return "P-SAMPLE STEP EROR";
		case 35:
			return "Z-SAMPLE POSITION ERROR";
		case 37:
			return "P-REAGENT STEP ERROR";
		case 38:
			return "Z-REAGENT POSITION ERROR";
		case 39:
			return "Z-BARCODE POSITION ERROR";
		case 40:
			return "MOTOR-CONTROL I NO RESPONSE";
		case 41:
			return "MOTOR-CONTROL II NO RESPONSE";
		case 42:
			return "MOTOR-CONTROL II DATA ERROR";
		case 46:
			return "ROM CHECK FAILED";
		case 47:
			return "REFERENCE < LIMIT";
		case 48:
			return "LEVEL DETECTION ERROR";
		case 49:
			return "PRINTER MOTOR DEFECT";
		case 50:
			return "BAD TEMPERATURES";
		case 51:
			return "BAD ANALYZER TEMPERATURE";
		case 52:
			return "BAD REAGENT TEMPERATURE";
		case 53:
			return "ANALYZER TEMP. OUT OF RANGE";
		case 54:
			return "REAGENT TEMP. OUT OF RANGE";
		case 55:
			return "REAGENT/ANALYZER TEMPERATURE NOT WITHIN RANGE YET";
		case 56:
			return "HOST INTERFACE NO RESPONSE";
		case 57:
			return "HOST INTERFACE PROTOCOL ERROR";
		case 58:
			return "SYSTEM BUSY";
		case 59:
			return "COMMUNICATION-CONTROL NO RESPONSE";
		case 60:
			return "INVALID SAMPLE BARCODE POS: ____";
		case 61:
			return "DILUENT EMPTY - DIL: ____";
		case 62:
			return "ISE ACTIVATOR EMPTY - POS: ____";
		case 63:
			return "SAMPLE EMPTY - POS: ____";
		case 64:
			return "CONTROL EMPTY - POS: ____";
		case 65:
			return "PROGRAM ERROR: ____";
		case 66:
			return "REAGENT EMPTY - TEST: ____";
		case 67:
			return "ISE ELECTRODE: ____ OVERRANGE";
		case 68:
			return "ISE A/D CONV.: ____ TIMEOUT";
		case 69:
			return "INVALID SAMPLE POSITION: ____";
		case 70:
			return "STANDARD EMPTY - POS: ____";
		case 71:
			return "START REAG. 1 EMPTY - TEST";
		case 72:
			return "START REAG. 1 EMPTY - TEST";
		case 73:
			return "BLANK SOLUTION EMPTY - POS: ____";
		case 74:
			return "MOTOR-CONTR II FUSE ERR: ____";
		case 75:
			return "ADJUST-WASH ____ NOT POSSIBLE";
		case 76:
			return "ADJUST-ANAL. ____ NOT POSSIBLE";
		case 77:
			return "ADJUST-BARC. ____ NOT POSSIBLE";
		case 80:
			return "(BELL)";
		case 81:
			return "ISE INTERFACE NO RESPONSE";
		case 82:
			return "ISE MODULE NO RESPONSE";
		case 83:
			return "ISE MODULE BUSY";
		case 84:
			return "ISE INTERFACE PARITY ERROR";
		case 85:
			return "ISE RAM ERROR";
		case 86:
			return "ISE LEAKAGE ERROR";
		case 87:
			return "ISE CLEANER EMPTY";
		case 88:
			return "ISE ETCHER EMPTY";
		case 89:
			return "RESULT ERROR IN TEST RESULTS!";
		case 90:
			return "LEAVE TEST RESULTS, PLEASE!";
		case 91:
			return "INVALID BARCODE INPUT";
		case 92:
			return "INCORRECT RATIO TEST-GROUP";
		case 93:
			return "INCORRECT RATIO FORMULA";
		case 94:
			return "WORKLIST COMPLETED";
		case 95:
			return "MISSING RACK";
		case 96:
			return "TIP CLEANER EMPTY";
		case 97:
			return "NO WATER AVAILABLE";
		case 98:
			return "TEST RESULTS OCCUPIED";
		case 100:
			return "MEMORY CAPACITY OVERFLOW";
		case 101:
			return "SYSTEM INITIALIZED";
		case 102:
			return "TEST DOESN'T EXIST";
		case 103:
			return "RATIO DOESN'T EXIST";
		case 104:
			return "PROFILE DOESN'T EXIST";
		case 105:
			return "SAMPLE NOT FOUND";
		case 106:
			return "TEST NOT FOUND";
		case 107:
			return "NO RACK FOUND";
		case 108:
			return "TEST/RACK PROGRAMMING ERROR";
		case 110:
			return "LOW CPU-BATTERY";
		case 111:
			return "PRINTER BUSY";
		case 112:
			return "PRIVILEGED ACTION";
		case 113:
			return "TEST LOCKED";
		case 114:
			return "TEST IN WORKLIST AND QUALITY";
		case 115:
			return "RATIO IN WORKLIST";
		case 116:
			return "ALL TESTS IN WORKLIST";
		case 117:
			return "SYSTEM IN PROCESS";
		case 118:
			return "SYSTEM CHECKS IN PROCESS";
		case 119:
			return "WORKLIST NOT EMPTY";
		case 121:
			return "TEST/DILUTION ALREADY ASSIGNED";
		case 122:
			return "CONFIRM BY <ENTER>";
		case 123:
			return "<ENTER> TO DELETE THE ALREADY PRINTED RESULTS";
		case 125:
			return "ISE TESTS NOT ASSIGNABLE";
		case 126:
			return "TEST IN WORKLIST";
		case 127:
			return "TEST IN QUALITY";
		case 128:
			return "PRINTER LEVER OPEN";
		case 129:
			return "NO PRINTER PAPER";
		case 130:
			return "SEGMENT INPUT EMPTY";
		case 131:
			return "SEGMENT OUTPUT FULL";
		case 132:
			return "CHANGER X-MOTOR TIMEOUT";
		case 133:
			return "CHANGER Y-MOTOR TIMEOUT";
		case 134:
			return "CHANGER Z-MOTOR TIMEOUT";
		case 135:
			return "CHANGER X NOT IN POSITION";
		case 136:
			return "CHANGER Y NOT IN POSITION";
		case 137:
			return "CHANGER Z NOT IN POSITION";
		case 138:
			return "CHANGER/COOLING-CONTROL NO RESPONSE";
		case 139:
			return "CHANGER TIMING INVALID";
		case 140:
			return "RACK TEMPERATURE TOO HIGH";
		case 141:
			return "ROUTINE WORKLIST NOT COMPLETED";
		case 142:
			return "STAT WORKLIST NOT COMPLETED";
		case 143:
			return "INVALID INPUT";
		case 144:
			return "MOTOR-CTRL II HARDWARE-ERROR";
		case 145:
			return "MOTOR-CTRL II LIGHTB.-ERROR";
		case 147:
			return "TOUCH ERROR";
		case 148:
			return "ADJUST-RACKRD. NOT POSSIBLE";
		default:
			return "UNKNOWN ERROR";
		}
	}
	
}
