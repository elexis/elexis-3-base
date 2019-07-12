package ch.itmed.fop.printing.handlers;

import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.util.SWTHelper;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.itmed.fop.printing.print.PrintProvider;
import ch.itmed.fop.printing.resources.Messages;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.AppointmentCard;
import ch.itmed.fop.printing.xml.documents.FoTransformer;
import ch.rgw.io.Settings;

public final class AppointmentCardHandler extends AbstractHandler {
	private static Logger logger = LoggerFactory.getLogger(AppointmentCardHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			InputStream xmlDoc = AppointmentCard.create();
			InputStream fo = FoTransformer.transformXmlToFo(xmlDoc,
					ResourceProvider.getXslTemplateFile(PreferenceConstants.APPOINTMENT_CARD_ID));

			String docName = PreferenceConstants.APPOINTMENT_CARD;
			Settings settingsStore = SettingsProvider.getStore(docName);

			String printerName = settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 0), "");
			logger.info("Printing document AppointmentCard on printer: " + printerName);
			PrintProvider.print(fo, printerName);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg != null) {
				if (msg.equals("No appointment selected")) {
					// Make sure we don't show 2 error messages.
					return null;
				}
			}
			SWTHelper.showError(Messages.DefaultError_Title, Messages.DefaultError_Message);
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}