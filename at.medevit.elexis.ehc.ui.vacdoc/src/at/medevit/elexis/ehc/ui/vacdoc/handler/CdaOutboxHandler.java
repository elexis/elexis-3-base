package at.medevit.elexis.ehc.ui.vacdoc.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.ehc.ui.vacdoc.wizard.ExportVaccinationsWizard;
import at.medevit.elexis.ehc.ui.vacdoc.wizard.ExportVaccinationsWizard.ExportType;

public class CdaOutboxHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		ExportVaccinationsWizard wizard = new ExportVaccinationsWizard(ExportType.CDA);
		WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		dialog.open();
		return null;
	}
}
