package ch.elexis.agenda.data;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.data.events.ElexisClassToModelInterfaceContribution;
import ch.elexis.core.model.IAppointment;

@Component
public class ClassToModelInterfaceContribution implements ElexisClassToModelInterfaceContribution {

	@Override
	public Optional<Class<?>> getCoreModelInterfaceForElexisClass(Class<?> elexisClazz) {
		if (elexisClazz == Termin.class) {
			return Optional.of(IAppointment.class);
		}
		return Optional.empty();
	}
}
