package ch.itmed.fop.printing.fhir;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.Setting;
import ch.itmed.fop.printing.print.PrintProvider;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.AppointmentCard;
import ch.itmed.fop.printing.xml.documents.FoTransformer;

/**
 * Consumed in es.fhir.rest.core.resources.AppointmentResourceProvider
 **/
@SuppressWarnings({ "unchecked", "rawtypes" })
@Component(property = "service.name=ch.itmed.fop.printing.fhir.PrintAppointmentCardFunction")
public class PrintAppointmentCardFunction implements Function {

	@Override
	public Object apply(Object input) {

		Logger logger = LoggerFactory.getLogger(getClass());

		Map<String, Object> _input = (Map<String, Object>) input;
		IAppointment appointment = (IAppointment) _input.get("appointment");
		IPatient patient = (IPatient) _input.get("patient");
		IMandator mandator = (IMandator) _input.get("mandator");

		try {
			InputStream xmlDoc = AppointmentCard.create(appointment, patient, mandator);
			InputStream fo = FoTransformer.transformXmlToFo(xmlDoc,
					ResourceProvider.getXslTemplateFile(PreferenceConstants.APPOINTMENT_CARD_ID));

			String docName = PreferenceConstants.APPOINTMENT_CARD;

			String printerName = Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 0));
			logger.info("Printing document AppointmentCard on printer: " + printerName); //$NON-NLS-1$
			PrintProvider.print(fo, printerName);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return Status.error(e.getMessage());
		}

		return Status.OK_STATUS;
	}

}
