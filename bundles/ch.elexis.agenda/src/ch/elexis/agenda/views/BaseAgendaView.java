/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.agenda.views;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.actions.Activator;
import ch.elexis.actions.AgendaActions;
import ch.elexis.actions.IBereichSelectionEvent;
import ch.elexis.agenda.BereichSelectionHandler;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.ICalTransfer;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.ui.BereichMenuCreator;
import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.agenda.CollisionErrorLevel;
import ch.elexis.core.model.builder.IAppointmentBuilder;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.core.services.holder.AppointmentHistoryServiceHolder;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.AppointmentState;
import ch.elexis.core.types.AppointmentType;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.RestrictedAction;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.core.ui.locks.LockRequestingRestrictedAction;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.dialogs.AppointmentDialog;
import ch.elexis.dialogs.RecurringAppointmentDialog;
import ch.elexis.dialogs.TagesgrenzenDialog;
import ch.elexis.dialogs.TerminListeDruckenDialog;
import ch.elexis.dialogs.TermineDruckenDialog;
import ch.rgw.tools.Log;
import ch.rgw.tools.TimeTool;

public abstract class BaseAgendaView extends ViewPart implements IRefreshable, IBereichSelectionEvent {
	protected SelectionListener sListen = new SelectionListener();
	TableViewer tv;
	BaseAgendaView self;
	protected LockRequestingRestrictedAction<IAppointment> terminAendernAction, terminKuerzenAction,
			terminVerlaengernAction;
	protected RestrictedAction newTerminAction;
	protected IAction blockAction;
	protected IAction dayLimitsAction, newViewAction, printAction, exportAction, importAction;
	protected IAction printPatientAction;
	private BereichMenuCreator bmc = new BereichMenuCreator();
	MenuManager menu = new MenuManager();
	protected Log log = Log.get("Agenda"); //$NON-NLS-1$
	Activator agenda = Activator.getDefault();

	private Timer timer;

	private IAppointmentService appointmentService;

	@Optional
	@Inject
	void reloadAppointment(@UIEventTopic(ElexisEventTopics.EVENT_RELOAD) Class<?> clazz) {
		if (IAppointment.class.equals(clazz)) {
			CoreUiUtil.runAsyncIfActive(() -> {
				tv.refresh(true);
			}, tv);
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

	protected void userChanged() {
		updateActions();
		CoreUiUtil.runAsyncIfActive(() -> {
			tv.getControl().setFont(UiDesk.getFont(Preferences.USR_DEFAULTFONT));
		}, tv);
		setBereich(ConfigServiceHolder.getUser(PreferenceConstants.AG_BEREICH, agenda.getActResource()));
	}

	private IMenuManager mgr;
	private IAction bereichMenu;

	private long highestLastUpdate;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	protected BaseAgendaView() {
		self = this;
		BereichSelectionHandler.addBereichSelectionListener(this);

		appointmentService = OsgiServiceUtil.getService(IAppointmentService.class).get();
	}

	abstract public void create(Composite parent);

	@Override
	public void createPartControl(Composite parent) {
		setBereich(ConfigServiceHolder.getUser(PreferenceConstants.AG_BEREICH, agenda.getActResource()));
		create(parent);
		makeActions();
		tv.setContentProvider(new AgendaContentProvider());
		tv.setUseHashlookup(true);
		tv.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IAppointment pl = getSelection();
				if (pl == null) {
					newTerminAction.run();
				} else {
					if (pl.isRecurring()) {
						AppointmentServiceHolder.get().getAppointmentSeries(pl).ifPresent(s -> {
							RecurringAppointmentDialog dlg = new RecurringAppointmentDialog(s);
							dlg.open();
							tv.refresh(true);
						});
					} else {
						if (appointmentService.getType(AppointmentType.FREE).equals(pl.getType())) {
							pl.setEndTime(pl.getStartTime().plusMinutes(30));
							pl.setType(AppointmentServiceHolder.get().getType(AppointmentType.DEFAULT));
							pl.setState(AppointmentServiceHolder.get().getState(AppointmentState.DEFAULT));
							pl.setSubjectOrPatient(ContextServiceHolder.get().getActivePatient().map(p -> p.getId())
									.orElse(StringUtils.EMPTY));
							AppointmentDialog dlg = new AppointmentDialog(pl);
							dlg.setCollisionErrorLevel(CollisionErrorLevel.ERROR);
							dlg.setExpanded(true);
							dlg.open();
						} else {
							terminAendernAction.run();
						}
						tv.refresh(true);
					}
				}
			}
		});

		menu.setRemoveAllWhenShown(true);
		menu.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				java.util.Optional<IAppointment> selectedAppointment = ContextServiceHolder.get()
						.getTyped(IAppointment.class);
				if (selectedAppointment.isEmpty()) {
					manager.add(newTerminAction);
					manager.add(blockAction);
				} else {
					manager.add(AgendaActions.getTerminStatusAction());
					manager.add(terminKuerzenAction);
					manager.add(terminVerlaengernAction);
					manager.add(terminAendernAction);
					manager.add(AgendaActions.getDelTerminAction());
				}
				updateActions();
			}

		});

		Menu cMenu = menu.createContextMenu(tv.getControl());
		tv.getControl().setMenu(cMenu);

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				long lastUpdate = CoreModelServiceHolder.get().getHighestLastUpdate(IAppointment.class);
				log.log("Agenda [" + lastUpdate + "]", Log.DEBUGMSG); //$NON-NLS-1$
				if (lastUpdate > highestLastUpdate) {
					highestLastUpdate = lastUpdate;
					refresh();
				}
			}
		}, 10000, 10000);

		getSite().getPage().addPartListener(udpateOnVisible);

		tv.setInput(getViewSite());
		updateActions();
		tv.addSelectionChangedListener(sListen);
	}

	public IAppointment getSelection() {
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		if ((sel == null || (sel.isEmpty()))) {
			return null;
		} else {
			IAppointment pl = (IAppointment) sel.getFirstElement();
			return pl;
		}
	}

	@Override
	public void dispose() {
		timer.cancel();
		getSite().getPage().removePartListener(udpateOnVisible);
		super.dispose();
	}

	@Override
	public void setFocus() {
		tv.getControl().setFocus();
	}

	@Override
	public void refresh() {
		updateActions();
		reloadAppointment(IAppointment.class);
	}

	public void setBereich(String b) {
		agenda.setActResource(b);
	}

	public abstract void setAppointment(IAppointment t);

	class AgendaContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			if (AccessControlServiceHolder.get().evaluate(EvACE.of(IAppointment.class, Right.VIEW))) {
				String resource = agenda.getActResource();
				TimeTool date = agenda.getActDate();
				appointmentService.assertBlockTimes(date.toLocalDate(), resource);
				return appointmentService.getAppointments(resource, date.toLocalDate(), true).toArray();
			} else {
				return new Object[0];
			}

		}

		@Override
		public void dispose() { /* leer */
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {/* leer */
		}

	};

	class SelectionListener implements ISelectionChangedListener {

		StructuredViewer sv;

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection sel = (IStructuredSelection) event.getSelection();
			if ((sel == null) || sel.isEmpty()) {
				ContextServiceHolder.get().removeTyped(IAppointment.class);
			} else {
				Object o = sel.getFirstElement();
				if (o instanceof IAppointment) {
					IAppointment appointment = (IAppointment) o;
					if (appointmentService.getType(AppointmentType.FREE).equals(appointment.getType())) {
						ContextServiceHolder.get().removeTyped(IAppointment.class);
					} else {
						setAppointment(appointment);
					}
				}
			}
		}
	}

	protected void updateActions() {
		dayLimitsAction.setEnabled(AccessControlServiceHolder.get().evaluate(EvACE.of(IAppointment.class, Right.UPDATE).and(Right.EXECUTE)));
		newTerminAction.reflectRight();
		terminKuerzenAction.reflectRight();
		terminVerlaengernAction.reflectRight();
		terminAendernAction.reflectRight();
		AgendaActions.updateActions();
	}

	protected void makeActions() {
		dayLimitsAction = new Action(Messages.BaseAgendaView_dayLimits) {
			@Override
			public void run() {
				new TagesgrenzenDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						agenda.getActDate().toString(TimeTool.DATE_COMPACT), agenda.getActResource()).open();
				tv.refresh(true);
			}
		};
		dayLimitsAction.setId("ch.elexis.agenda.actions.dayLimitsAction");

		blockAction = new Action(Messages.TagesView_lockPeriod) {
			@Override
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
				if (sel != null && !sel.isEmpty()) {
					IAppointment appointment = (IAppointment) sel.getFirstElement();
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
		terminAendernAction = new LockRequestingRestrictedAction<IAppointment>(
				EvACE.of(IAppointment.class, Right.UPDATE),
				Messages.TagesView_changeTermin) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(Messages.TagesView_changeThisTermin);
			}

			@Override
			public IAppointment getTargetedObject() {
				return ContextServiceHolder.get().getTyped(IAppointment.class).orElse(null);
			}

			@Override
			public void doRun(IAppointment element) {
				AcquireLockBlockingUi.aquireAndRun(element, new ILockHandler() {

					@Override
					public void lockFailed() {
						// do nothing
					}

					@Override
					public void lockAcquired() {
						AppointmentDialog dlg = new AppointmentDialog(element);
						dlg.setCollisionErrorLevel(CollisionErrorLevel.ERROR);
						dlg.setExpanded(true);
						if (dlg.open() == Dialog.OK) {
							AppointmentHistoryServiceHolder.get().logAppointmentEdit(element);
						}
					}
				});
				if (tv != null) {
					tv.refresh(true);
				}
			}
		};
		terminKuerzenAction = new LockRequestingRestrictedAction<IAppointment>(
				EvACE.of(IAppointment.class, Right.UPDATE),
				Messages.TagesView_shortenTermin) {
			@Override
			public IAppointment getTargetedObject() {
				return ContextServiceHolder.get().getTyped(IAppointment.class).orElse(null);
			}

			@Override
			public void doRun(IAppointment element) {
				element.setEndTime(element.getStartTime().plusMinutes(element.getDurationMinutes() >> 1));
				CoreModelServiceHolder.get().save(element);
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
			}
		};
		terminVerlaengernAction = new LockRequestingRestrictedAction<IAppointment>(
				EvACE.of(IAppointment.class, Right.UPDATE),
				Messages.TagesView_enlargeTermin) {
			@Override
			public IAppointment getTargetedObject() {
				return ContextServiceHolder.get().getTyped(IAppointment.class).orElse(null);
			}

			@Override
			public void doRun(IAppointment t) {
				LocalDateTime oldEndTime = t.getEndTime();
				agenda.setActDate(new TimeTool(t.getStartTime().toLocalDate()));
				List<IAppointment> appointments = appointmentService.getAppointments(agenda.getActResource(),
						agenda.getActDate().toLocalDate(), false);
				appointments.stream().filter(a -> a.getStartTime().isAfter(t.getEndTime())).findFirst().ifPresent(a -> {
					t.setEndTime(a.getStartTime());
					if (AppointmentHistoryServiceHolder.get() != null) {
						AppointmentHistoryServiceHolder.get().logAppointmentDurationChange(t, oldEndTime,
								t.getEndTime());
					}
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
				});
			}
		};

		newTerminAction = new RestrictedAction(EvACE.of(IAppointment.class, Right.CREATE), Messages.TagesView_newTermin) {
			{
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
				setToolTipText(Messages.TagesView_createNewTermin);
			}

			@Override
			public void doRun() {
				LocalDateTime start = agenda.getActDate().toLocalDateTime();
				LocalDateTime end = start.plusMinutes(30);
				IAppointment appointment = new IAppointmentBuilder(CoreModelServiceHolder.get(),
						agenda.getActResource(), start, end,
						AppointmentServiceHolder.get().getType(AppointmentType.DEFAULT),
						AppointmentServiceHolder.get().getState(AppointmentState.DEFAULT)).build();
				AppointmentDialog dlg = new AppointmentDialog(appointment);
				dlg.setCollisionErrorLevel(CollisionErrorLevel.ERROR);
				dlg.open();
				if (tv != null) {
					tv.refresh(true);
				}
			}
		};
		printAction = new Action(Messages.BaseAgendaView_printDayList) {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setToolTipText(Messages.BaseAgendaView_printListOfDay);
			}

			@Override
			public void run() {
				List<IAppointment> appointments = appointmentService.getAppointments(agenda.getActResource(),
						agenda.getActDate().toLocalDate(), true);
				TerminListeDruckenDialog dlg = new TerminListeDruckenDialog(getViewSite().getShell(), appointments);
				dlg.open();
				if (tv != null) {
					tv.refresh(true);
				}
			}
		};
		printPatientAction = new Action(Messages.BaseAgendaView_printPatAppointments) {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setToolTipText(Messages.BaseAgendaView_printFutureAppsOfSelectedPatient);
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
								SWTHelper.alert(Messages.BaseAgendaView_errorWhileprinting,
										Messages.BaseAgendaView_errorHappendPrinting);
							}
						} else {
							dlg.setBlockOnOpen(true);
							dlg.open();
						}
					}
				}
			}
		};
		exportAction = new Action(Messages.BaseAgendaView_exportAgenda) {
			{
				setToolTipText(Messages.BaseAgendaView_exportAppointsments);
				setImageDescriptor(Images.IMG_GOFURTHER.getImageDescriptor());
			}

			@Override
			public void run() {
				ICalTransfer ict = new ICalTransfer();
				ict.doExport(agenda.getActDate(), agenda.getActDate(), agenda.getActResource());
			}
		};

		importAction = new Action(Messages.BaseAgendaView_importAgenda) {
			{
				setToolTipText(Messages.BaseAgendaView_importFromIcal);
				setImageDescriptor(Images.IMG_IMPORT.getImageDescriptor());
			}

			@Override
			public void run() {
				ICalTransfer ict = new ICalTransfer();
				ict.doImport(agenda.getActResource());
			}
		};
		bereichMenu = new Action(Messages.TagesView_bereich, Action.AS_DROP_DOWN_MENU) {
			Menu mine;
			{
				setToolTipText(Messages.TagesView_selectBereich);
				setMenuCreator(bmc);
			}

		};

		mgr = getViewSite().getActionBars().getMenuManager();
		mgr.add(bereichMenu);
		mgr.add(dayLimitsAction);
		mgr.add(newViewAction);
		mgr.add(exportAction);
		mgr.add(importAction);
		mgr.add(printAction);
		mgr.add(printPatientAction);
	}

	@Override
	public void bereichSelectionEvent(String bereich) {
		setPartName("Agenda " + bereich);
		reloadAppointment(IAppointment.class);
	}

	@Optional
	@Inject
	public void setFixLayout(MPart part, @Named(Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}

	protected Color getTypColor(IAppointment p) {
		String coldesc = ConfigServiceHolder.getUserCached(PreferenceConstants.AG_TYPCOLOR_PREFIX + p.getType(),
				"FFFFFF"); //$NON-NLS-1$
		return UiDesk.getColorFromRGB(coldesc);
	}

	protected Color getStateColor(IAppointment p) {
		if (appointmentService.getType(AppointmentType.BOOKED).equals(p.getType())) {
			String coldesc = ConfigServiceHolder.getUserCached(PreferenceConstants.AG_TYPCOLOR_PREFIX + p.getType(),
					"000000"); //$NON-NLS-1$
			return UiDesk.getColorFromRGB(coldesc);
		}
		String coldesc = ConfigServiceHolder.getUserCached(PreferenceConstants.AG_STATCOLOR_PREFIX + p.getState(),
				"000000"); //$NON-NLS-1$
		return UiDesk.getColorFromRGB(coldesc);
	}
}
