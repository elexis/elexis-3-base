package at.medevit.elexis.inbox.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class MandantSelectorDialog extends TitleAreaDialog {
	List<Mandant> lMandant;
	org.eclipse.swt.widgets.List lbMandant;
	Mandant selMandant;

	public MandantSelectorDialog(Shell parentShell) {
		super(parentShell);
		selMandant = ElexisEventDispatcher.getSelectedMandator();
	}

	@Override
	public Control createDialogArea(final Composite parent) {
		setTitle("Mandant ändern");
		setMessage("Bitte wählen Sie einen Mandanten");

		lbMandant = new org.eclipse.swt.widgets.List(parent, SWT.BORDER | SWT.SINGLE);
		lbMandant.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Query<Mandant> qbe = new Query<Mandant>(Mandant.class);
		lMandant = qbe.execute();
		for (PersistentObject m : lMandant) {
			lbMandant.add(m.get(Kontakt.FLD_NAME2) + " " + m.get(Kontakt.FLD_NAME1) + " - " + m.getLabel());
		}
		return lbMandant;
	}

	@Override
	protected void okPressed() {
		int idx = lbMandant.getSelectionIndex();
		if (idx > -1) {
			selMandant = lMandant.get(idx);
		}
		super.okPressed();
	}

	public Mandant getSelectedMandant() {
		return selMandant;
	}
}
