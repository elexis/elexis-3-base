/*******************************************************************************
 * Copyright (c) 2007-2011, MEDEVIT, MEDELEXIS and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    M. Descher - Initial implementation
 *
 *******************************************************************************/
package ch.elexis.agenda.ui;

import java.util.stream.Collectors;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.elexis.actions.Activator;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.holder.AppointmentServiceHolder;

public class BereichMenuCreator implements IMenuCreator {

	Menu mine;
	Activator agenda = Activator.getDefault();

	@Override
	public void dispose() {
	}

	public BereichMenuCreator() {

	}

	@Override
	public Menu getMenu(Control parent) {
		mine = new Menu(parent);
		addAboutToShow();
		return mine;
	}

	@Override
	public Menu getMenu(Menu parent) {
		mine = new Menu(parent);
		addAboutToShow();
		return mine;
	}

	/**
	 * Add custom about to show listener for dynamic area menu items based on
	 * {@link IAppointmentService#getAoboAreas()}.
	 * 
	 */
	private void addAboutToShow() {
		mine.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(Event event) {
				for (MenuItem item : mine.getItems()) {
					if (item != null && !item.isDisposed()) {
						item.dispose();
					}
				}
				
				String[] sMandanten = AppointmentServiceHolder.get().getAoboAreas().stream().map(a -> a.getName())
						.collect(Collectors.toList()).toArray(new String[0]);
				for (String m : sMandanten) {
					MenuItem it = new MenuItem(mine, SWT.RADIO);
					it.setText(m);
					if (agenda.getActResource().equalsIgnoreCase(m)) {
						it.setSelection(true);
					} else {
						it.setSelection(false);
					}
					it.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							MenuItem mi = (MenuItem) e.getSource();
							agenda.setActResource(mi.getText());
						}
					});
				}
			}
		});
	}
}
