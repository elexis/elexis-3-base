package ch.elexis.base.ch.arzttarife.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;

import ch.elexis.data.Leistungsblock;

public class UpdateLeistungsblock extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEclipseContext iEclipseContext = PlatformUI.getWorkbench().getService(IEclipseContext.class);
		StructuredSelection selection = (StructuredSelection) iEclipseContext
				.get(event.getCommand().getId().concat(".selection"));
		iEclipseContext.remove(event.getCommand().getId().concat(".selection"));
		if (selection != null && !selection.isEmpty()) {
			for (Object object : selection.toList()) {
				if (object instanceof Leistungsblock) {
					System.out.println(object);
				}
			}
		}
		return null;
	}
}
