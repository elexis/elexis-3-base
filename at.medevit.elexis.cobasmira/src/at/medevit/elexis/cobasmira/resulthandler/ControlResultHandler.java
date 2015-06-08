package at.medevit.elexis.cobasmira.resulthandler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.cobasmira.model.CobasMiraMessage;
import at.medevit.elexis.cobasmira.model.CobasMiraPatientResult;
import at.medevit.elexis.cobasmira.ui.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.rgw.tools.TimeTool;

public class ControlResultHandler {
	private static Logger logger = LoggerFactory.getLogger(ControlResultHandler.class);
	
	public static int writeControlResult(CobasMiraPatientResult cmp, TimeTool entryDate){
		FileOutputStream fos;
		try {
			fos =
				new FileOutputStream(CoreHub.localCfg.get(Preferences.CONTROLLOGFILE,
					"controlLog.txt"), true);
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
			out.write(entryDate.toDBString(true) + "," + cmp.getPatientIdentification() + ","
				+ cmp.getTestName() + "," + Float.toString(cmp.getConcentration()) + "\n");
			out.close();
		} catch (FileNotFoundException e) {
			logger
				.warn("Logfileexists but is a directory rather than a regular file, "
					+ "does not exist but cannot be created, or cannot be opened for any other reason.");
			return CobasMiraMessage.ELEXIS_RESULT_CONTROL_ERR;
		} catch (UnsupportedEncodingException e) {
			logger.warn(e.getLocalizedMessage());
			return CobasMiraMessage.ELEXIS_RESULT_CONTROL_ERR;
		} catch (IOException e) {
			logger.warn(e.getLocalizedMessage());
			return CobasMiraMessage.ELEXIS_RESULT_CONTROL_ERR;
		}
		return CobasMiraMessage.ELEXIS_RESULT_CONTROL_OK;
	}
}
