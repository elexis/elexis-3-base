package at.medevit.elexis.inbox.ui.dialog;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.PersistentObject;

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
		lMandant = getMandantors();
		for (PersistentObject m : lMandant) {
			lbMandant.add(
					m.get(Kontakt.FLD_NAME2) + StringUtils.SPACE + m.get(Kontakt.FLD_NAME1) + " - " + m.getLabel()); //$NON-NLS-1$
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
	
	private List<Mandant> getMandantors() {
		IQuery<IUser> userQuery = CoreModelServiceHolder.get().getQuery(IUser.class);
		userQuery.and(ModelPackage.Literals.IUSER__ASSIGNED_CONTACT, COMPARATOR.NOT_EQUALS, null);
		List<IUser> users = userQuery.execute();
		return users.stream().filter(u -> isActive(u) && isMandator(u)).map(u -> Mandant.load(u.getAssignedContact().getId()))
				.collect(Collectors.toList());
	}

	private boolean isMandator(IUser user) {
		return user.getAssignedContact() != null && user.getAssignedContact().isMandator();
	}
	
	private boolean isActive(IUser user) {
		if (user == null || user.getAssignedContact() == null) {
			return false;
		}
		if (!user.isActive()) {
			return false;
		}
		if (user.getAssignedContact() != null && user.getAssignedContact().isMandator()) {
			IMandator mandator = CoreModelServiceHolder.get().load(user.getAssignedContact().getId(), IMandator.class)
					.orElse(null);
			if (mandator != null && !mandator.isActive()) {
				return false;
			}
		}
		return true;
	}
}
