/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Sponsoring:
 * 	 mediX Notfallpaxis, diepraxen Stauffacher AG, Zürich
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.ui;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.statushandlers.StatusManager;

import ch.elexis.actions.Activator;
import ch.elexis.actions.AgendaActions;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.acl.ACLContributor;
import ch.elexis.agenda.data.ICalTransfer;
import ch.elexis.agenda.data.IPlannable;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IUser;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.dialogs.TerminDialog;
import ch.elexis.dialogs.TerminListeDruckenDialog;
import ch.elexis.dialogs.TermineDruckenDialog;
import ch.rgw.tools.TimeTool;

/**
 * Abstract base class for an agenda window.
 *
 * @author Gerry
 *
 */
public abstract class BaseView extends ViewPart implements HeartListener, IActivationListener {
	public BaseView() {
	}

	private static final String DEFAULT_PIXEL_PER_MINUTE = "1.0"; //$NON-NLS-1$

	public IAction newTerminAction, blockAction;
	public IAction dayLimitsAction, newViewAction, printAction, exportAction, importAction;
	public IAction printPatientAction, todayAction, refreshAction;
	MenuManager menu = new MenuManager();
	protected Activator agenda = Activator.getDefault();

	@Optional
	@Inject
	void reloadAppointment(@UIEventTopic(ElexisEventTopics.EVENT_RELOAD) Class<?> clazz) {
		if (IAppointment.class.equals(clazz)) {
			Display.getDefault().asyncExec(() -> {
				internalRefresh();
			});
		}
	}

	@Inject
	void activeUser(@Optional IUser user) {
		Display.getDefault().asyncExec(() -> {
			if (user != null) {
				userChanged();
			}
		});
	}

	private void userChanged() {
		updateActions();
		agenda.setActResource(ConfigServiceHolder.getUser(PreferenceConstants.AG_BEREICH, agenda.getActResource()));
	}

	@Override
	public void createPartControl(Composite parent) {
		makeActions();
		create(parent);
		GlobalEventDispatcher.addActivationListener(this, this);
		internalRefresh();
	}

	@Override
	public void dispose() {
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}

	abstract protected void create(Composite parent);

	abstract protected void refresh();

	abstract protected IPlannable getSelection();

	private void internalRefresh() {
		if (AccessControlServiceHolder.get().request(ACLContributor.DISPLAY_APPOINTMENTS)) {
			UiDesk.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					refresh();
				}
			});
		}
	}

	protected void checkDay(String resource, TimeTool date) {
		if (date == null) {
			date = agenda.getActDate();
		}
		if (resource == null) {
			resource = agenda.getActResource();
		}
		OsgiServiceUtil.getService(IAppointmentService.class).get().assertBlockTimes(date.toLocalDate(), resource);
	}

	protected void updateActions() {
		dayLimitsAction.setEnabled(AccessControlServiceHolder.get().request(ACLContributor.CHANGE_DAYSETTINGS));
		boolean canChangeAppointments = AccessControlServiceHolder.get().request(ACLContributor.CHANGE_APPOINTMENTS);
		newTerminAction.setEnabled(canChangeAppointments);
		AgendaActions.updateActions();
		internalRefresh();
	}

	public void heartbeat() {
		internalRefresh();
	}

	public void activation(boolean mode) {

	}

	public void visible(boolean mode) {
		if (mode) {
			CoreHub.heart.addListener(this);
		} else {
			CoreHub.heart.removeListener(this);
		}
	}

	/**
	 * Return the scale factor, i.e. the number of Pixels to use for one minute.
	 *
	 * @return thepixel-per-minute scale.
	 */
	public static double getPixelPerMinute() {
		String ppm = CoreHub.localCfg.get(PreferenceConstants.AG_PIXEL_PER_MINUTE, DEFAULT_PIXEL_PER_MINUTE);
		try {
			double ret = Double.parseDouble(ppm);
			return ret;
		} catch (NumberFormatException ne) {
			CoreHub.localCfg.set(PreferenceConstants.AG_PIXEL_PER_MINUTE, DEFAULT_PIXEL_PER_MINUTE);
			return Double.parseDouble(DEFAULT_PIXEL_PER_MINUTE);
		}
	}

	protected void makeActions() {
		dayLimitsAction = new Action(Messages.BaseView_dayLimits) {
			@Override
			public void run() {
				// new TagesgrenzenDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				// .getShell(), agenda.getActDate().toString(TimeTool.DATE_COMPACT), agenda
				// .getActResource()).open();
				ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
						.getService(ICommandService.class);

				Command cmd = commandService.getCommand("org.eclipse.ui.window.preferences");
				try {
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("preferencePageId", "ch.elexis.agenda.tageseinteilung");
					ExecutionEvent ev = new ExecutionEvent(cmd, hm, null, null);
					cmd.executeWithChecks(ev);
				} catch (Exception exception) {
					Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
							"Error opening preference page ch.elexis.agenda.tageseinteilung", exception);
					StatusManager.getManager().handle(status, StatusManager.SHOW);
				}
				refresh();
			}
		};

		blockAction = new Action(Messages.TagesView_lockPeriod) {
			@Override
			public void run() {
				IPlannable p = getSelection();
				if (p != null) {
					if (p instanceof Termin.Free) {
						new Termin(agenda.getActResource(), agenda.getActDate().toString(TimeTool.DATE_COMPACT),
								p.getStartMinute(), p.getDurationInMinutes() + p.getStartMinute(),
								Termin.typReserviert(), Termin.statusLeer());
						ElexisEventDispatcher.reload(Termin.class);
					}
				}

			}
		};
		newTerminAction = new Action(Messages.TagesView_newTermin) {
			{
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
				setToolTipText(Messages.TagesView_createNewTermin);
			}

			@Override
			public void run() {
				new TerminDialog(null).open();
				internalRefresh();
			}
		};
		printAction = new Action(Messages.BaseView_printDayPaapintments) {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setToolTipText(Messages.BaseView_printAPpointmentsOfSelectedDay);
			}

			@Override
			public void run() {
				IPlannable[] liste = Plannables.loadDay(agenda.getActResource(), agenda.getActDate());
				new TerminListeDruckenDialog(getViewSite().getShell(), liste).open();
				internalRefresh();
			}
		};
		printPatientAction = new Action(Messages.BaseView_printAppointments) {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setToolTipText(Messages.BaseView_printFutureAppointmentsOfSelectedPatient);
			}

			@Override
			public void run() {
				Patient patient = ElexisEventDispatcher.getSelectedPatient();
				if (patient != null) {
					Query<Termin> qbe = new Query<Termin>(Termin.class);
					qbe.add("Wer", "=", patient.getId());
					qbe.add("deleted", "<>", "1");
					qbe.add("Tag", ">=", new TimeTool().toString(TimeTool.DATE_COMPACT));
					qbe.orderBy(false, "Tag", "Beginn");
					java.util.List<Termin> list = qbe.execute();
					if (list != null) {
						boolean directPrint = CoreHub.localCfg.get(
								PreferenceConstants.AG_PRINT_APPOINTMENTCARD_DIRECTPRINT,
								PreferenceConstants.AG_PRINT_APPOINTMENTCARD_DIRECTPRINT_DEFAULT);

						TermineDruckenDialog dlg = new TermineDruckenDialog(getViewSite().getShell(),
								list.toArray(new Termin[0]));
						if (directPrint) {
							dlg.setBlockOnOpen(false);
							dlg.open();
							if (dlg.doPrint()) {
								dlg.close();
							} else {
								SWTHelper.alert(Messages.BaseView_errorWhilePrinting,
										Messages.BaseView_errorHappendPrinting);
							}
						} else {
							dlg.setBlockOnOpen(true);
							dlg.open();
						}
					}
				}
			}
		};
		exportAction = new Action(Messages.BaseView_exportAgenda) {
			{
				setToolTipText(Messages.BaseView_exportAppojntmentsOfMandator);
				setImageDescriptor(Images.IMG_GOFURTHER.getImageDescriptor());
			}

			@Override
			public void run() {
				ICalTransfer ict = new ICalTransfer();
				ict.doExport(agenda.getActDate(), agenda.getActDate(), agenda.getActResource());
			}
		};

		importAction = new Action(Messages.BaseView_importAgenda) {
			{
				setToolTipText(Messages.BaseView_importFromICal);
				setImageDescriptor(Images.IMG_IMPORT.getImageDescriptor());
			}

			@Override
			public void run() {
				ICalTransfer ict = new ICalTransfer();
				ict.doImport(agenda.getActResource());
			}
		};

		todayAction = new Action(Messages.BaseView_today) {
			{
				setToolTipText(Messages.BaseView_showToday);
				setImageDescriptor(Activator.getImageDescriptor("icons/calendar_view_day.png")); //$NON-NLS-1$
			}

			@Override
			public void run() {
				agenda.setActDate(new TimeTool());
				internalRefresh();
			}
		};

		refreshAction = new Action(Messages.BaseView_refresh) {
			{
				setToolTipText(Messages.BaseView_refresh);
				setImageDescriptor(Images.IMG_REFRESH.getImageDescriptor());
			}

			@Override
			public void run() {
				internalRefresh();
			}
		};

		IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
		mgr.add(dayLimitsAction);
		mgr.add(exportAction);
		mgr.add(importAction);
		mgr.add(printAction);
		mgr.add(printPatientAction);
		IToolBarManager tmr = getViewSite().getActionBars().getToolBarManager();
		tmr.add(refreshAction);
		tmr.add(todayAction);
	}

	@Optional
	@Inject
	public void setFixLayout(MPart part, @Named(Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}
}
