package at.medevit.elexis.ehc.ui.vacdoc.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import at.medevit.elexis.ehc.ui.vacdoc.service.EhcCoreServiceHolder;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public class CreateXdmHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String paramAttachments = event.getParameter("at.medevit.elexis.ehc.ui.vacdoc.attachments");
		String paramPatientId = event.getParameter("at.medevit.elexis.ehc.ui.vacdoc.patient.id");
		String paramTmpDir = event.getParameter("at.medevit.elexis.ehc.ui.vacdoc.tmp.dir");
		
		if (paramAttachments != null && paramPatientId != null && paramTmpDir != null) {
			Patient patient = Patient.load(paramPatientId);
			if (patient.exists() && !paramAttachments.isEmpty()) {
				Mandant mandant = ElexisEventDispatcher.getSelectedMandator();
				List<File> files = new ArrayList<>();
				
				for (String attachmentPath : paramAttachments.split(":::")) {
					File f = new File(attachmentPath);
					files.add(f);
				}
				return EhcCoreServiceHolder.getService().createXdmContainer(patient, mandant, files,
					paramTmpDir + File.separator + "export.xdm");
			}
		}
		return null;
		
	}
}
