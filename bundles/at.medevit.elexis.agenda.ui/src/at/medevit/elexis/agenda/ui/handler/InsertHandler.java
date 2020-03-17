package at.medevit.elexis.agenda.ui.handler;

import java.util.List;
import java.util.Optional;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.function.AbstractBrowserFunction;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.ui.views.controls.GenericSelectionComposite.GenericSelectionDialog;

public class InsertHandler {
	
	@Execute
	public Object execute(MPart part){
		Optional<SideBarComposite> activeSideBar = AbstractBrowserFunction.getActiveSideBar(part);
		activeSideBar.ifPresent(sideBar -> {
			sideBar.getMoveInformation().ifPresent(moveInformation -> {
				List<IPeriod> moveablePeriods = moveInformation.getMoveablePeriods();
				if (moveablePeriods.size() == 1) {
					moveInformation.movePeriod(moveablePeriods.get(0));
				} else if (moveablePeriods.size() > 1) {
					GenericSelectionDialog dialog = new GenericSelectionDialog(
						Display.getDefault().getActiveShell(), moveablePeriods);
					if (dialog.open() == GenericSelectionDialog.OK) {
						IStructuredSelection selection = dialog.getSelection();
						if (selection != null && !selection.isEmpty()) {
							@SuppressWarnings("unchecked")
							List<IPeriod> selected = (List<IPeriod>) (List<?>) selection.toList();
							for (IPeriod iPeriod : selected) {
								moveInformation.movePeriod(iPeriod);
							}
						}
					}
				}
			});
		});
		return null;
	}
}
