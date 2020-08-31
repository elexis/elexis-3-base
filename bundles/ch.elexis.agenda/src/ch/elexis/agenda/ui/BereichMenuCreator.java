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
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class BereichMenuCreator implements IMenuCreator {
	
	Menu mine;
	Activator agenda = Activator.getDefault();
	String[] sMandanten;
	MenuItem[] menuItems;
	
	@Override
	public void dispose(){}
	
	public BereichMenuCreator(){
		sMandanten =
			ConfigServiceHolder.getGlobal(PreferenceConstants.AG_BEREICHE, Messages.TagesView_praxis)
				.split(","); //$NON-NLS-1$
		menuItems = new MenuItem[sMandanten.length];
	}
	
	@Override
	public Menu getMenu(Control parent){
		mine = new Menu(parent);
		fillMenu();
		return mine;
	}
	
	@Override
	public Menu getMenu(Menu parent){
		mine = new Menu(parent);
		fillMenu();
		return mine;
	}
	
	private void fillMenu(){
		mine.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(Event event){
				MenuItem[] menuItems = mine.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					if (menuItems[i].getText().equalsIgnoreCase(agenda.getActResource())) {
						menuItems[i].setSelection(true);
					} else {
						menuItems[i].setSelection(false);
					}
				}
			}
		});
		
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
				public void widgetSelected(SelectionEvent e){
					MenuItem mi = (MenuItem) e.getSource();
					agenda.setActResource(mi.getText());
				}
				
			});
		}
	}
}
