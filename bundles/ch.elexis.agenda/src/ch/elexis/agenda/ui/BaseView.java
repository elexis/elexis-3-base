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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
import org.slf4j.LoggerFactory;

import ch.elexis.actions.Activator;
import ch.elexis.actions.AgendaActions;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.ICalTransfer;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.IAppointmentBuilder;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.AppointmentState;
import ch.elexis.core.types.AppointmentType;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.dialogs.AppointmentDialog;
import ch.elexis.dialogs.TerminListeDruckenDialog;
import ch.elexis.dialogs.TermineDruckenDialog;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Abstract base class for an agenda window.
 *
 * @author Gerry
 *
 */
public abstract class BaseView extends ViewPart implements HeartListener, IActivationListener {
	public BaseView() {
		appointmentService = OsgiServiceUtil.getService(IAppointmentService.class).get();
	}

	private static final String DEFAULT_PIXEL_PER_MINUTE = "1.0"; //$NON-NLS-1$

	public IAction newTerminAction, blockAction;
	public IAction dayLimitsAction, newViewAction, printAction, exportAction, importAction;
	public IAction printPatientAction, todayAction, refreshAction;
	MenuManager menu = new MenuManager();
	protected Activator agenda = Activator.getDefault();

	private IAppointmentService appointmentService;

	private Timer timer;

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

	private long highestLastUpdate;

	@Override
	public void createPartControl(Composite parent) {

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				long lastUpdate = CoreModelServiceHolder.get().getHighestLastUpdate(IAppointment.class);
				LoggerFactory.getLogger(getClass()).debug("Agenda [" + lastUpdate + "]"); //$NON-NLS-1$
				if (lastUpdate > highestLastUpdate) {
					highestLastUpdate = lastUpdate;
					refresh();
				}
			}
		}, 10000, 10000);

		makeActions();
		create(parent);
		GlobalEventDispatcher.addActivationListener(this, this);
		internalRefresh();
	}

	@Override
	public void dispose() {
		timer.cancel();
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}

	abstract protected void create(Composite parent);

	abstract protected void refresh();

	abstract protected IAppointment getSelection();

	private void internalRefresh() {
		if (AccessControlServiceHolder.get().evaluate(EvACE.of(IAppointment.class, Right.VIEW))) {
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
		dayLimitsAction.setEnabled(AccessControlServiceHolder.get().evaluate(EvACE.of(IAppointment.class, Right.UPDATE).and(Right.EXECUTE)));
		boolean canChangeAppointments = AccessControlServiceHolder.get().evaluate(EvACE.of(IAppointment.class, Right.UPDATE));
		newTerminAction.setEnabled(canChangeAppointments);
		AgendaActions.updateActions();
		internalRefresh();
	}

	@Override
	public void heartbeat() {
		internalRefresh();
	}

	@Override
	public void activation(boolean mode) {

	}

	@Override
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
				ICommandService commandService = PlatformUI.getWorkbench()
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
				IAppointment appointment = getSelection();
				if (appointment != null) {
					if (appointmentService.getType(AppointmentType.FREE).equals(appointment.getType())) {
						appointment.setSchedule(agenda.getActResource());
						appointment.setType(appointmentService.getType(AppointmentType.BOOKED));
						appointment.setState(appointmentService.getState(AppointmentState.EMPTY));
						appointment.setCreated(Integer.toString(TimeTool.getTimeInSeconds() / 60));
						ContextServiceHolder.get().getActiveUser().ifPresent(au -> {
							appointment.setCreatedBy(au.getLabel());
						});
						CoreModelServiceHolder.get().save(appointment);
						ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
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
				LocalDateTime start = agenda.getActDate().toLocalDateTime();
				LocalDateTime end = start.plusMinutes(30);
				IAppointment appointment = new IAppointmentBuilder(CoreModelServiceHolder.get(),
						agenda.getActResource(), start, end,
						AppointmentServiceHolder.get().getType(AppointmentType.DEFAULT),
						AppointmentServiceHolder.get().getState(AppointmentState.DEFAULT)).build();
				AppointmentDialog dlg = new AppointmentDialog(appointment);
				dlg.open();
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
				new TerminListeDruckenDialog(getViewSite().getShell(), appointmentService
						.getAppointments(agenda.getActResource(), agenda.getActDate().toLocalDate(), true)).open();
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
				IPatient patient = ContextServiceHolder.get().getActivePatient().orElse(null);
				if (patient != null) {
					IQuery<IAppointment> query = CoreModelServiceHolder.get().getQuery(IAppointment.class);
					query.and(ModelPackage.Literals.IAPPOINTMENT__SUBJECT_OR_PATIENT, COMPARATOR.EQUALS,
							patient.getId());
					query.and("tag", COMPARATOR.GREATER_OR_EQUAL, LocalDate.now());
					query.orderBy("Tag", ORDER.ASC);
					query.orderByLeftPadded("Beginn", ORDER.ASC);
					java.util.List<IAppointment> list = query.execute();
					if (list != null) {
						boolean directPrint = LocalConfigService.get(
								PreferenceConstants.AG_PRINT_APPOINTMENTCARD_DIRECTPRINT,
								PreferenceConstants.AG_PRINT_APPOINTMENTCARD_DIRECTPRINT_DEFAULT);

						TermineDruckenDialog dlg = new TermineDruckenDialog(getViewSite().getShell(), list);
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
