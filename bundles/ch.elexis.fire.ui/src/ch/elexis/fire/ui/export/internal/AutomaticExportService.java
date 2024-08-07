package ch.elexis.fire.ui.export.internal;

import java.util.Timer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.fire.core.IFIREService;

@Component
public class AutomaticExportService {

	private Timer timer;

	@Reference
	private IFIREService fireService;

	@Reference
	private IConfigService configService;

	@Reference
	private IContextService contextService;

	@Reference
	private ITaskService taskService;

	@Activate
	public void activate() {
		timer = new Timer(true);
		timer.schedule(new AutomaticExportTask(fireService, taskService, configService, contextService), 60000, 60000);
	}

	@Deactivate
	public void deactivate() {
		timer.cancel();
	}
}
