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
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.util.AppointmentUtil;
import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
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
	private static LockRequestingRestrictedAction<IAppointment> delTerminAction;
	/** Display or change the state of an appointment */
	private static IAction terminStatusAction;

	/**
	 * Reflect the user's rights on the agenda actions
	 */
	public static void updateActions() {
		getTerminStatusAction().setEnabled(AccessControlServiceHolder.get().evaluate(EvACE.of(IAppointment.class, Right.VIEW).and(Right.UPDATE)));
		((RestrictedAction) getDelTerminAction()).reflectRight();
	}

	public static IAction getDelTerminAction() {
		if (delTerminAction == null) {
			makeActions();
		}
		return delTerminAction;
	}

	public static IAction getTerminStatusAction() {
		if (terminStatusAction == null) {
			makeActions();
		}
		return terminStatusAction;
	}

	private static void makeActions() {
		delTerminAction = new LockRequestingRestrictedAction<IAppointment>(EvACE.of(IAppointment.class, Right.DELETE),
				Messages.AgendaActions_deleteDate) {
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
				setToolTipText(Messages.AgendaActions_deleteDate);
			}

			@Override
			public IAppointment getTargetedObject() {
			    return ContextServiceHolder.get()
						.getTyped(IAppointment.class)
			        .orElse(null);
			}

			@Override
			public void doRun(IAppointment element) {
				if (AppointmentUtil.isLocked(element))
					return;
				ECommandService cmdSvc = PlatformUI.getWorkbench().getService(ECommandService.class);
				EHandlerService hdlSvc = PlatformUI.getWorkbench().getService(EHandlerService.class);
				ParameterizedCommand cmd = cmdSvc.createCommand("ch.elexis.agenda.commands.delete", //$NON-NLS-1$
						java.util.Collections.emptyMap());
				hdlSvc.executeHandler(cmd);
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
			}
		};

		terminStatusAction = new Action(Messages.AgendaActions_state, Action.AS_DROP_DOWN_MENU) {
			Menu mine = null;
			Listener showListener = null;
			{
				setMenuCreator(new IMenuCreator() {
					@Override
					public void dispose() {
						if (mine != null) {
							removeShowListener();
							mine.dispose();
						}
					}

					@Override
					public Menu getMenu(Control parent) {
						mine = new Menu(parent);
						fillMenu();
						addShowListener();
						return mine;
					}

					@Override
					public Menu getMenu(Menu parent) {
						mine = new Menu(parent);
						fillMenu();
						addShowListener();
						return mine;
					}

					private void removeShowListener() {
						if (mine != null && showListener != null) {
							mine.removeListener(SWT.Show, showListener);
						}
					}

					private void addShowListener() {
						if (mine != null) {
							removeShowListener();
							showListener = event -> {
								Menu menu = (Menu) event.widget;
								ContextServiceHolder.get().getTyped(IAppointment.class).ifPresent(appt -> {
									String actStatus = appt.getState();
									if (actStatus != null) {
										for (MenuItem menuItem : menu.getItems()) {
											menuItem.setSelection(StringUtils.equals(actStatus, menuItem.getText()));
										}
									}
								});
							};
							mine.addListener(SWT.Show, showListener);
						}

					}
				});
			}

			void fillMenu() {
				for (String t : Termin.TerminStatus) {
					MenuItem it = new MenuItem(mine, SWT.CHECK);
					it.setText(t);
					it.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							ContextServiceHolder.get().getTyped(IAppointment.class).ifPresent(appt -> {
								AcquireLockBlockingUi.aquireAndRun(appt, new ILockHandler() {
									@Override
									public void lockFailed() {
										/* nichts */
									}

									@Override
									public void lockAcquired() {
										MenuItem it = (MenuItem) e.getSource();
										appt.setState(it.getText());
										CoreModelServiceHolder.get().save(appt);
										ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD,
												IAppointment.class);
										ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, appt);
									}
								});
							});
						}

					});
				}
			}
		};
	}
}
