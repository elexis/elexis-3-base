/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation, adapted from JavaAgenda
 *
 *******************************************************************************/
package ch.elexis.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.acl.ACLContributor;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.icons.Images;

/**
 * Some common actions for the agenda
 *
 * @author gerry
 *
 */
public class AgendaActions {

	/** modify an appointment */
	public static IAction changeTerminStatusAction;
	/** delete an appointment */
	public static IAction delTerminAction;
	/** Display or change the state of an appointment */
	private static IAction terminStatusAction;

	/**
	 * Reflect the user's rights on the agenda actions
	 */
	public static void updateActions(){
		getTerminStatusAction().setEnabled(CoreHub.acl.request(ACLContributor.USE_AGENDA));
		getDelTerminAction();
	}

	public static IAction getDelTerminAction(){
		if (delTerminAction == null) {
			makeActions();
		}
		return delTerminAction;
	}

	public static IAction getTerminStatusAction(){
		if (terminStatusAction == null) {
			makeActions();
		}
		return terminStatusAction;
	}

	private static void makeActions(){

		delTerminAction = new Action(Messages.AgendaActions_deleteDate) {
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
				setToolTipText(Messages.AgendaActions_deleteDate);
			}

			@Override
			public void run(){
				Termin t = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
				t.delete();
				ElexisEventDispatcher.reload(Termin.class);
			}
		};
		terminStatusAction = new Action(Messages.AgendaActions_state, Action.AS_DROP_DOWN_MENU) {
			Menu mine = null;
			{
				setMenuCreator(new IMenuCreator() {
					@Override
					public void dispose(){
						if (mine != null) {
							mine.dispose();
						}
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

				});
			}

			void fillMenu(){
				for (String t : Termin.TerminStatus) {
					MenuItem it = new MenuItem(mine, SWT.NONE);
					it.setText(t);
					it.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e){
							Termin act = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
							MenuItem it = (MenuItem) e.getSource();
							act.setStatus(it.getText());
							ElexisEventDispatcher.reload(Termin.class);
						}
					});
				}
			}
		};
	}
}
