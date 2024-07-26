package ch.elexis.fire.ui.export.internal;

import java.util.Timer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.fire.core.IFIREService;

@Component
public class AutomaticExportService {

	private Timer timer = new Timer(true);

	@Reference
	private IFIREService fireService;

	@Activate
	public void activate() {
		timer.schedule(new AutomaticExportTask(fireService), 30000, 30000);
	}

	@Deactivate
	public void deactivate() {
		timer.cancel();
	}
}
