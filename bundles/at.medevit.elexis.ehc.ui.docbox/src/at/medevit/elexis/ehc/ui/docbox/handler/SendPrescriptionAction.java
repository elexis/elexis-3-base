package at.medevit.elexis.ehc.ui.docbox.handler;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.IServiceLocator;

public class SendPrescriptionAction extends Action {

	@Override
	public void runWithEvent(Event event) {
		IServiceLocator serviceLocator = PlatformUI.getWorkbench();
		ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);

		// Lookup commmand with its ID
		Command command = commandService.getCommand("at.medevit.elexis.ehc.ui.docbox.sendPrescription"); //$NON-NLS-1$

		// Optionally pass a ExecutionEvent instance, default no-param arg creates blank
		// event
		try {
			command.executeWithChecks(new ExecutionEvent());
		} catch (ExecutionException e) {
			error(e);
		} catch (NotDefinedException e) {
			error(e);
		} catch (NotEnabledException e) {
			error(e);
		} catch (NotHandledException e) {
			error(e);
		}
	}

	private void error(Exception e) {
		MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				"Das Rezept konnte nicht gesendet werden. \n\n" + e.getMessage());
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.ehc.ui.docbox", "/icons/docbox16.png"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
