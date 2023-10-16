/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.dynamic;

import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import at.medevit.elexis.gdt.command.DatenEinerUntersuchungAnzeigen;
import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.data.GDTProtokoll;
import ch.elexis.core.model.Identifiable;
import ch.elexis.data.Patient;

public class UntersuchungAnzeigenMenu extends ContributionItem {

	public static final String ID = "at.medevit.elexis.gdt.dynamic.UntersuchungAnzeigenMenu"; //$NON-NLS-1$

	public UntersuchungAnzeigenMenu() {
	}

	public UntersuchungAnzeigenMenu(String id) {
		super(id);
	}

	@Override
	public void fill(Menu menu, int index) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getActivePage().getSelection();
		IStructuredSelection strucSelection = (IStructuredSelection) selection;
		Patient pat = getAsPatient(strucSelection);

		if (pat != null) {
			List<GDTProtokoll> prot = GDTProtokoll.getEntriesForPatient(pat.getId(), null, null);
			int counter = 0;
			for (GDTProtokoll gdtProtokoll : prot) {
				if (Integer.parseInt(
						gdtProtokoll.getMessageType()) == GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN) {
					final GDTProtokoll gd = gdtProtokoll;
					counter++;
					MenuItem item = new MenuItem(menu, SWT.PUSH, index);
					item.setText(gdtProtokoll.getMenuLabel());
					item.addListener(SWT.Selection, new Listener() {
						@Override
						public void handleEvent(Event event) {
							// Lets call our command
							IHandlerService handlerService = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getService(IHandlerService.class);
							ICommandService commandService = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getService(ICommandService.class);
							try {
								Command cmd = commandService.getCommand(DatenEinerUntersuchungAnzeigen.ID);
								ParameterizedCommand pc = new ParameterizedCommand(cmd,
										new Parameterization[] { new Parameterization(
												cmd.getParameter(DatenEinerUntersuchungAnzeigen.PARAM_ID),
												gd.getId()) });

								handlerService.executeCommand(pc, event);
							} catch (Exception ex) {
								throw new RuntimeException(DatenEinerUntersuchungAnzeigen.ID, ex);
							}
						}
					});
				}
			}
			if (counter == 0) {
				MenuItem item = new MenuItem(menu, SWT.PUSH, index);
				item.setText("Keine Untersuchungsdaten vorhanden.");
				item.setEnabled(false);
			}

		}
	}

	private Patient getAsPatient(IStructuredSelection strucSelection) {
		Patient ret = null;
		if (strucSelection.getFirstElement() instanceof Identifiable) {
			ret = Patient.load(((Identifiable) strucSelection.getFirstElement()).getId());
		} else {
			ret = (Patient) strucSelection.getFirstElement();
		}
		return ret;
	}
}
