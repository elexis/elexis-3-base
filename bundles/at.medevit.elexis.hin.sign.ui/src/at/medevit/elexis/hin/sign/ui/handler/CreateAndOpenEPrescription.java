package at.medevit.elexis.hin.sign.ui.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import at.medevit.elexis.emediplan.core.EMediplanService;
import at.medevit.elexis.hin.sign.core.IHinSignService;
import ch.elexis.core.model.IRecipe;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.status.ObjectStatus;
import ch.elexis.core.utils.OsgiServiceUtil;

public class CreateAndOpenEPrescription extends AbstractHandler implements IHandler {

	private EMediplanService eMediplanService;

	private IHinSignService hinSignService;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Optional<IRecipe> selectedRecipe = ContextServiceHolder.get().getTyped(IRecipe.class);
		if (selectedRecipe.isPresent() && getEMediplanService() != null && getHinSignService() != null) {
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				eMediplanService.exportEMediplanJson(ContextServiceHolder.getActiveMandatorOrThrow(),
						selectedRecipe.get().getPatient(), selectedRecipe.get().getPrescriptions(), output);
				String chmed = IOUtils.toString(new ByteArrayInputStream(output.toByteArray()), "UTF-8");
				if (StringUtils.isNotBlank(chmed)) {
					ObjectStatus<?> status = hinSignService.createPrescription(chmed);
					if (status.isOK() && status.get() instanceof String) {
						String url = (String) status.get();
						getHinSignService().setPrescriptionUrl(selectedRecipe.get(), url);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private EMediplanService getEMediplanService() {
		if (eMediplanService == null) {
			eMediplanService = OsgiServiceUtil.getService(EMediplanService.class).orElse(null);
		}
		return eMediplanService;
	}

	private IHinSignService getHinSignService() {
		if (hinSignService == null) {
			hinSignService = OsgiServiceUtil.getService(IHinSignService.class).orElse(null);
		}
		return hinSignService;
	}
}
