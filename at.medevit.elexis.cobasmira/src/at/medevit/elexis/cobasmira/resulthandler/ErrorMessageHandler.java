package at.medevit.elexis.cobasmira.resulthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.cobasmira.model.CobasMiraMessage;

public class ErrorMessageHandler {
	private static Logger logger = LoggerFactory.getLogger(ErrorMessageHandler.class);
	
	public static void handleError(CobasMiraMessage add){
		logger.warn("Cobas Mira Error Message: "
			+ CobasMiraMessage.getBlockTypeString(add.getBlockType()));
		//TODO: Send the messages to the respective person
	}
}
