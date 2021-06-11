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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.acl.ACLContributor;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IPersistentObject;
import ch.elexis.core.ui.actions.RestrictedAction;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.core.ui.locks.LockRequestingRestrictedAction;

/**
 * Some common actions for the agenda
 * 
 * @author gerry
 * 
 */
public class AgendaActions {
	
	/** delete an appointment */
	private static LockRequestingRestrictedAction<Termin> delTerminAction;
	/** Display or change the state of an appointment */
	private static IAction terminStatusAction;
	
	/**
	 * Reflect the user's rights on the agenda actions
	 */
	public static void updateActions(){
		getTerminStatusAction().setEnabled(CoreHub.acl.request(ACLContributor.USE_AGENDA));
		((RestrictedAction) getDelTerminAction()).reflectRight();
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
		
		delTerminAction = new LockRequestingRestrictedAction<Termin>(ACLContributor.DELETE_APPOINTMENTS,
				Messages.AgendaActions_deleteDate) {
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
				setToolTipText(Messages.AgendaActions_deleteDate);
			}

			@Override
			public Termin getTargetedObject() {
				return (Termin) ElexisEventDispatcher.getSelected(Termin.class);
			}

			@Override
			public void doRun(Termin element) {
				element.delete();
				ElexisEventDispatcher.reload(Termin.class);
			}
		};
		terminStatusAction = new Action(Messages.AgendaActions_state, Action.AS_DROP_DOWN_MENU) {
			Menu mine = null;
			Listener showListener = null;
			{
				setMenuCreator(new IMenuCreator() {
					public void dispose(){
						if (mine != null) {
							removeShowListener();
							mine.dispose();
						}
					}
					
					public Menu getMenu(Control parent){
						mine = new Menu(parent);
						fillMenu();
						addShowListener();
						return mine;
					}
					
					public Menu getMenu(Menu parent){
						mine = new Menu(parent);
						fillMenu();
						addShowListener();
						return mine;
					}
					
					private void removeShowListener(){
						if (mine != null && showListener != null) {
							mine.removeListener(SWT.Show, showListener);
						}
					}
					
					private void addShowListener(){
						if (mine != null) {
							removeShowListener();
							showListener = new Listener() {
								@Override
								public void handleEvent(Event event){
									Menu menu = (Menu) event.widget;
									Termin actTermin =
										(Termin) ElexisEventDispatcher.getSelected(Termin.class);
									if (actTermin != null) {
										String actTerminStatus = actTermin.getStatus();
										if (actTerminStatus != null) {
											for (MenuItem menuItem : menu.getItems()) {
												menuItem.setSelection(StringUtils
													.equals(actTerminStatus, menuItem.getText()));
											}
										}
									}
								}
							};
							mine.addListener(SWT.Show, showListener);
						}
					}
				});
			}
			
			void fillMenu(){
				for (String t : Termin.TerminStatus) {
					MenuItem it = new MenuItem(mine, SWT.CHECK);
					it.setText(t);
					it.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e){
							Termin act = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
							AcquireLockBlockingUi.aquireAndRun((IPersistentObject) act,
								new ILockHandler() {
								@Override
								public void lockFailed(){
									// do nothing
									
								}
								
								@Override
								public void lockAcquired(){
									MenuItem it = (MenuItem) e.getSource();
									act.setStatus(it.getText());
									ElexisEventDispatcher.reload(Termin.class);
								}
							});
						}
					});
				}
			}
		};
	}
}
