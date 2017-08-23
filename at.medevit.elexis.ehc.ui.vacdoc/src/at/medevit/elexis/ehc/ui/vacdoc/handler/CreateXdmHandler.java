package at.medevit.elexis.ehc.ui.vacdoc.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.ehealth_connector.communication.ConvenienceCommunication;
import org.ehealth_connector.communication.DocumentMetadata;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public class CreateXdmHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String attachments = event.getParameter("at.medevit.elexis.ehc.ui.vacdoc.attachments");
		String patientId = event.getParameter("at.medevit.elexis.ehc.ui.vacdoc.patient.id");
		String tmpDir = event.getParameter("at.medevit.elexis.ehc.ui.vacdoc.tmp.dir");
		
		if (attachments != null && patientId != null && tmpDir != null) {
			Patient p = Patient.load(patientId);
			if (p.exists()) {
				if (attachments != null && !attachments.isEmpty()) {
					ConvenienceCommunication conCom = new ConvenienceCommunication();
					Mandant mandant = ElexisEventDispatcher.getSelectedMandator();
					int size = 0;
					for (String attachmentPath : attachments.split(":::")) {
						if (attachmentPath.toLowerCase().endsWith(".xml")) {
							try {
								File f = new File(attachmentPath);
								FileInputStream in = FileUtils.openInputStream(f);
								DocumentMetadata metaData =
									conCom.addDocument(DocumentDescriptor.CDA_R2, in);
								metaData.setPatient(EhcCoreMapper.getEhcPatient(p));
								metaData.addAuthor(EhcCoreMapper.getEhcAuthor(mandant));
								IOUtils.closeQuietly(in);
								size++;
							} catch (IOException e) {
								LoggerFactory.getLogger(CreateXdmHandler.class)
									.warn("cannot add file [{}]", attachmentPath, e);
							}
						}
						
					}
					if (size > 0) {
						String xdmPath = tmpDir + File.separator + "export.xdm";
						conCom.createXdmContents(xdmPath);
						return xdmPath;
					}
				}
			}
		}
		
		return null;
	}

	
}
