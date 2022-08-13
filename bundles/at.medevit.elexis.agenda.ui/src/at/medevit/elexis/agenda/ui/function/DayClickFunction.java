package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;

import com.equo.chromium.swt.Browser;

import at.medevit.elexis.agenda.ui.dialog.AppointmentDialog;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.builder.IAppointmentBuilder;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.AppointmentState;
import ch.elexis.core.types.AppointmentType;

public class DayClickFunction extends AbstractBrowserFunction {

	private List<String> selectedResources;

	public DayClickFunction(Browser browser, String name) {
		super(browser, name);
	}

	@Override
	public Object function(Object[] arguments) {
		IAppointment appointment = null;
		if (arguments.length == 1) {
			LocalDateTime date = getDateTimeArg(arguments[0]);
			if (selectedResources != null && !selectedResources.isEmpty()) {
				Integer preferredDuration = getPreferredDuration(selectedResources.get(0),
						AppointmentServiceHolder.get().getType(AppointmentType.DEFAULT));
				appointment = new IAppointmentBuilder(CoreModelServiceHolder.get(), selectedResources.get(0), date,
						date.plusMinutes(preferredDuration),
						AppointmentServiceHolder.get().getType(AppointmentType.DEFAULT),
						AppointmentServiceHolder.get().getState(AppointmentState.DEFAULT)).build();
			} else {
				MessageDialog.openInformation(getBrowser().getShell(), Messages.AgendaUI_DayClick_info,
						Messages.AgendaUI_DayClick_no_resource_selected);
			}
		} else if (arguments.length == 2) {
			LocalDateTime date = getDateTimeArg(arguments[0]);
			String resource = (String) arguments[1];
			Integer preferredDuration = getPreferredDuration(resource,
					AppointmentServiceHolder.get().getType(AppointmentType.DEFAULT));
			appointment = new IAppointmentBuilder(CoreModelServiceHolder.get(), resource, date,
					date.plusMinutes(preferredDuration),
					AppointmentServiceHolder.get().getType(AppointmentType.DEFAULT),
					AppointmentServiceHolder.get().getState(AppointmentState.DEFAULT)).build();
		}
		if (appointment != null) {
			final IAppointment editAppointment = appointment;
			ContextServiceHolder.get().getActivePatient()
					.ifPresent(p -> editAppointment.setSubjectOrPatient(p.getId()));
			AppointmentDialog dlg = new AppointmentDialog(editAppointment);
			dlg.open();
		}
		return null;
	}

	private Integer getPreferredDuration(String areaName, String type) {
		Map<String, Integer> preferredDurations = AppointmentServiceHolder.get().getPreferredDurations(areaName);
		if (preferredDurations.containsKey(type)) {
			return preferredDurations.get(type);
		}
		return 30;
	}

	public void setSelectedResources(List<String> selectedResources) {
		this.selectedResources = selectedResources;
	}
}
