/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui.handlers;

import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.elexis.impfplan.ui.billing.AddVaccinationToKons;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.CodeSelectorHandler;
import ch.elexis.core.ui.util.GenericObjectDropTarget;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.codesystems.LeistungenView;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class ApplyVaccinationHandler extends AbstractHandler {
	private static Logger logger = LoggerFactory.getLogger(ApplyVaccinationHandler.class);

	private static boolean inProgress = false;
	private GenericObjectDropTarget dropTarget;
	private static TimeTool doa;
	private static IEncounter actEncounter;
	private LeistungenView leistungenView;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (dropTarget == null) {
			dropTarget = new GenericObjectDropTarget("Impfplan", UiDesk.getTopShell(), new DropReceiver()); //$NON-NLS-1$
		}

		// open the LeistungenView
		try {
			if (StringTool.isNothing(LeistungenView.ID)) {
				SWTHelper.alert("Fehler", "LeistungenView.ID");
			}

			leistungenView = (LeistungenView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(LeistungenView.ID);
			CodeSelectorHandler csHandler = CodeSelectorHandler.getInstance();
			csHandler.setCodeSelectorTarget(dropTarget);
			csHandler.getCodeSelectorTarget().registered(false);

			for (CTabItem cti : leistungenView.ctab.getItems()) {
				if (cti.getText().equalsIgnoreCase(ArtikelstammConstants.CODESYSTEM_NAME)) {
					leistungenView.setSelected(cti);
					leistungenView.setFocus();
					leistungenView.ctab.setSelection(cti);
				}
			}
		} catch (Exception e) {
			logger.error("Error trying to open LeistungenView", e); //$NON-NLS-1$
		}
		return null;
	}

	public static Date getKonsDate() {
		doa = new TimeTool(actEncounter.getDate());
		return doa.getTime();
	}

	public static boolean inProgress() {
		return inProgress;
	}

	/**
	 * waits for dropps/double-clicks on vaccinations
	 *
	 */
	private final class DropReceiver implements GenericObjectDropTarget.IReceiver {
		public void dropped(List<Object> list, DropTargetEvent ev) {
			for (Object object : list) {
				if (object instanceof IArticle) {
					IArticle artikel = (IArticle) object;
					// only accept vaccinations
					if (artikel.isVaccination()) {
						AddVaccinationToKons addVacToKons = new AddVaccinationToKons(
								ContextServiceHolder.get().getActivePatient().orElse(null), artikel, artikel.getGtin());

						actEncounter = addVacToKons.findOrCreateKons();
						if (actEncounter == null) {
							logger.warn("Could not insert vaccination as no consultation was found for this patient"); //$NON-NLS-1$
							MessageDialog.openError(Display.getDefault().getActiveShell(), "Nicht erstellbar",
									"Konnte Impfung nich eintragen, da keine Konsultation vorhanden ist.");
						}
						inProgress = true;
					} else {
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Nicht erstellbar",
								"Der gewählte Artikel ist kein Impfstoff.");
					}
				}
			}
		}

		public boolean accept(List<Object> list) {
			if (ContextServiceHolder.get().getActivePatient().isPresent()) {
				for (Object object : list) {
					if (object instanceof IArticle) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
