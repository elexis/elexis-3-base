package ch.itmed.fop.printing.console;

import java.io.InputStream;
import java.util.Collections;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.console.AbstractConsoleCommandProvider;
import ch.elexis.core.console.CmdAdvisor;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IModelService;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.Setting;
import ch.itmed.fop.printing.print.PrintProvider;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.AppointmentCard;
import ch.itmed.fop.printing.xml.documents.FoTransformer;

@Component(service = CommandProvider.class, immediate = true)
public class ConsoleCommandProvider extends AbstractConsoleCommandProvider {

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private IModelService coreModelService;

	@Activate
	public void activate() {
		register(this.getClass());
	}

	@CmdAdvisor(description = "itmed")
	public void _itmed(CommandInterpreter ci) {
		executeCommand("itmed", ci);
	}

	public void __itmed_print(String appointmentId, String patientId, String mandatorId) {

		IAppointment appointment = coreModelService.load(appointmentId, IAppointment.class)
				.orElseThrow(() -> new IllegalArgumentException());
		IPatient patient = coreModelService.load(patientId, IPatient.class)
				.orElseThrow(() -> new IllegalArgumentException());
		IMandator mandator = coreModelService.load(mandatorId, IMandator.class)
				.orElseThrow(() -> new IllegalArgumentException());

		try {
			InputStream xmlDoc = AppointmentCard.create(Collections.singletonList(appointment), patient, mandator);
			InputStream fo = FoTransformer.transformXmlToFo(xmlDoc,
					ResourceProvider.getXslTemplateFile(PreferenceConstants.APPOINTMENT_CARD_ID));

			String docName = PreferenceConstants.APPOINTMENT_CARD;

			String printerName = Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 0));
			logger.info("Printing document AppointmentCard on printer: " + printerName); //$NON-NLS-1$
			PrintProvider.print(fo, printerName);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

	}

}
