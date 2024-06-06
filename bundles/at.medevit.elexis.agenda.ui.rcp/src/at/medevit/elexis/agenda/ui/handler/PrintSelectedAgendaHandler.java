package at.medevit.elexis.agenda.ui.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.function.LoadEventsFunction;
import at.medevit.elexis.agenda.ui.utils.PdfUtils;
import at.medevit.elexis.agenda.ui.view.AgendaView;
import at.medevit.elexis.agenda.ui.xml.AreaPeriodsLetter;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.services.holder.ConfigServiceHolder;


public class PrintSelectedAgendaHandler {

    @Execute
    public Object execute(MPart part, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
        if (part.getObject() instanceof AgendaView) {
            AgendaView agendaView = (AgendaView) part.getObject();
            LoadEventsFunction loadEventsFunction = agendaView.getLoadEventsFunction();

            List<IPeriod> periods = loadEventsFunction.getCurrentPeriods();
            Map<String, List<IPeriod>> areaPeriodMap = getAreaPeriodMap(periods);

            for (String area : areaPeriodMap.keySet()) {
                AreaPeriodsLetter letter = AreaPeriodsLetter.of(area, areaPeriodMap.get(area));
                List<Map<String, String>> appointments = new ArrayList<>();
                Map<String, String> colors = new HashMap<>();
                for (IPeriod period : areaPeriodMap.get(area)) {
                    if (period instanceof IAppointment) {
                        IAppointment appointment = (IAppointment) period;
                        String type = appointment.getType();
                        String typeColor = getColorForType(type);
                        if ("gesperrt".equals(type)) {
                            continue;
                        }
                        Map<String, String> appointmentData = new HashMap<>();
						appointmentData.put("Datum", appointment.getStartTime().toLocalDate().toString());
                        appointmentData.put("Area", letter.getArea());
                        appointmentData.put("ID", appointment.getId());
                        appointmentData.put("Von", appointment.getStartTime().toLocalTime().toString());
                        appointmentData.put("Bis", appointment.getEndTime().toLocalTime().toString());
                        appointmentData.put("Personalien", appointment.getSubjectOrPatient());
                        appointmentData.put("Grund", appointment.getReason());
                        appointments.add(appointmentData);
						colors.put(appointment.getId(), typeColor != null ? typeColor : "FFFFFFFF");
                    }
                }
                FileOutputStream fout = null;
                File file = null;
                try {
					file = File.createTempFile(letter.getArea().replaceAll("\\s+", "_") + "_", ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
                    fout = new FileOutputStream(file);
					PdfUtils.saveFile(fout, appointments, colors);
                } catch (IOException e) {
                    Display.getDefault().syncExec(() -> {
                        MessageDialog.openError(shell, "Fehler", "Fehler beim PDF anlegen.\n" + e.getMessage());
                    });
                    LoggerFactory.getLogger(getClass()).error("Error creating PDF", e); //$NON-NLS-1$
                } finally {
                    if (fout != null) {
                        try {
                            fout.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
                if (file != null) {
                    Program.launch(file.getAbsolutePath());
                }
			}
        }
		return null;
    }

	private String getColorForType(String type) {
		String colorDesc = ConfigServiceHolder.getUser(PreferenceConstants.AG_TYPCOLOR_PREFIX + type, "FFFFFFFF");
		return colorDesc;
	}

    private Map<String, List<IPeriod>> getAreaPeriodMap(List<IPeriod> periods) {
		if (periods != null && !periods.isEmpty()) {
            if (periods.get(0) instanceof IAppointment) {
                Map<String, List<IPeriod>> ret = new HashMap<>();
                for (IPeriod iPeriod : periods) {
                    String area = ((IAppointment) iPeriod).getSchedule();
                    List<IPeriod> list = ret.get(area);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(iPeriod);
                    ret.put(area, list);
                }
                return ret;
            } else {
                throw new IllegalStateException("Can not determine area of period " + periods.get(0));
            }
        }
        return Collections.emptyMap();
    }
}
