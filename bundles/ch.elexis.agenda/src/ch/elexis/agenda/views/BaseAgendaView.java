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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
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
import ch.elexis.agenda.data.IPlannable;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.series.SerienTermin;
import ch.elexis.agenda.series.ui.SerienTerminDialog;
import ch.elexis.agenda.ui.BereichMenuCreator;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.data.interfaces.IPersistentObject;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IUser;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.RestrictedAction;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.core.ui.locks.LockRequestingRestrictedAction;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.dialogs.TagesgrenzenDialog;
import ch.elexis.dialogs.TerminDialog;
import ch.elexis.dialogs.TerminDialog.CollisionErrorLevel;
import ch.elexis.dialogs.TerminListeDruckenDialog;
import ch.elexis.dialogs.TermineDruckenDialog;
import ch.rgw.tools.Log;
import ch.rgw.tools.TimeTool;

public abstract class BaseAgendaView extends ViewPart implements HeartListener, IRefreshable, IBereichSelectionEvent {
	protected SelectionListener sListen = new SelectionListener();
	TableViewer tv;
	BaseAgendaView self;
	protected LockRequestingRestrictedAction<Termin> terminAendernAction, terminKuerzenAction, terminVerlaengernAction;
	protected RestrictedAction newTerminAction;
	protected IAction blockAction;
	protected IAction dayLimitsAction, newViewAction, printAction, exportAction, importAction, newTerminForAction;
	protected IAction printPatientAction;
	private BereichMenuCreator bmc = new BereichMenuCreator();
	MenuManager menu = new MenuManager();
	protected Log log = Log.get("Agenda"); //$NON-NLS-1$
	Activator agenda = Activator.getDefault();

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
			public void doubleClick(DoubleClickEvent event) {
				IPlannable pl = getSelection();
				if (pl == null) {
					newTerminAction.run();
				} else {
					if (pl.isRecurringDate()) {
						// TODO Locking
						SerienTermin st = new SerienTermin(pl);
						new SerienTerminDialog(UiDesk.getTopShell(), st).open();
						tv.refresh(true);
					} else {
						if (pl instanceof Termin.Free) {
							// locking of new Termin is handled by TerminDialog
							TerminDialog dlg = new TerminDialog(pl);
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

		CoreHub.heart.addListener(this);
		getSite().getPage().addPartListener(udpateOnVisible);

		tv.setInput(getViewSite());
		updateActions();
		tv.addSelectionChangedListener(sListen);
	}

	public IPlannable getSelection() {
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		if ((sel == null || (sel.isEmpty()))) {
			return null;
		} else {
			IPlannable pl = (IPlannable) sel.getFirstElement();
			return pl;
		}
	}

	@Override
	public void dispose() {
		CoreHub.heart.removeListener(this);
		getSite().getPage().removePartListener(udpateOnVisible);
		super.dispose();
	}

	@Override
	public void setFocus() {
		tv.getControl().setFocus();
	}

	public void heartbeat() {
		long lastUpdate = Termin.getHighestLastUpdate(Termin.TABLENAME);
		log.log("Heartbeat [" + lastUpdate + "]", Log.DEBUGMSG); //$NON-NLS-1$
		if (lastUpdate > highestLastUpdate) {
			highestLastUpdate = lastUpdate;
			refresh();
		}
	}

	@Override
	public void refresh() {
		updateActions();
		reloadAppointment(IAppointment.class);
	}

	public void setBereich(String b) {
		agenda.setActResource(b);
	}

	public abstract void setTermin(Termin t);

	class AgendaContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (AccessControlServiceHolder.get().evaluate(EvACE.of(IAppointment.class, Right.VIEW))) {
				String resource = agenda.getActResource();
				TimeTool date = agenda.getActDate();
				OsgiServiceUtil.getService(IAppointmentService.class).get().assertBlockTimes(date.toLocalDate(),
						resource);
				return Plannables.loadDay(resource, date);
			} else {
				return new Object[0];
			}

		}

		public void dispose() { /* leer */
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {/* leer */
		}

	};

	class SelectionListener implements ISelectionChangedListener {

		StructuredViewer sv;

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection sel = (IStructuredSelection) event.getSelection();
			if ((sel == null) || sel.isEmpty()) {
				ContextServiceHolder.get().removeTyped(IAppointment.class);
			} else {
				Object o = sel.getFirstElement();
				if (o instanceof Termin) {
					setTermin((Termin) o);
				} else if (o instanceof Termin.Free) {
					ContextServiceHolder.get().removeTyped(IAppointment.class);
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
					IPlannable p = (IPlannable) sel.getFirstElement();
					if (p instanceof Termin.Free) {
						new Termin(agenda.getActResource(), agenda.getActDate().toString(TimeTool.DATE_COMPACT),
								p.getStartMinute(), p.getDurationInMinutes() + p.getStartMinute(),
								Termin.typReserviert(), Termin.statusLeer());
						ElexisEventDispatcher.reload(Termin.class);
					}
				}

			}
		};
		terminAendernAction = new LockRequestingRestrictedAction<Termin>(EvACE.of(IAppointment.class, Right.UPDATE),
				Messages.TagesView_changeTermin) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(Messages.TagesView_changeThisTermin);
			}

			@Override
			public Termin getTargetedObject() {
				return (Termin) ElexisEventDispatcher.getSelected(Termin.class);
			}

			@Override
			public void doRun(Termin element) {
				AcquireLockBlockingUi.aquireAndRun((IPersistentObject) element, new ILockHandler() {

					@Override
					public void lockFailed() {
						// do nothing
					}

					@Override
					public void lockAcquired() {
						TerminDialog dlg = new TerminDialog(element);
						dlg.setCollisionErrorLevel(CollisionErrorLevel.WARNING);
						dlg.open();
					}
				});
				if (tv != null) {
					tv.refresh(true);
				}
			}
		};
		terminKuerzenAction = new LockRequestingRestrictedAction<Termin>(EvACE.of(IAppointment.class, Right.UPDATE),
				Messages.TagesView_shortenTermin) {
			@Override
			public Termin getTargetedObject() {
				return (Termin) ElexisEventDispatcher.getSelected(Termin.class);
			}

			@Override
			public void doRun(Termin element) {
				element.setDurationInMinutes(element.getDurationInMinutes() >> 1);
				ElexisEventDispatcher.reload(Termin.class);
			}
		};
		terminVerlaengernAction = new LockRequestingRestrictedAction<Termin>(EvACE.of(IAppointment.class, Right.UPDATE),
				Messages.TagesView_enlargeTermin) {
			@Override
			public Termin getTargetedObject() {
				return (Termin) ElexisEventDispatcher.getSelected(Termin.class);
			}

			@Override
			public void doRun(Termin t) {
				agenda.setActDate(t.getDay());
				Termin n = Plannables.getFollowingTermin(agenda.getActResource(), agenda.getActDate(), t);
				if (n != null) {
					t.setEndTime(n.getStartTime());
					// t.setDurationInMinutes(t.getDurationInMinutes()+15);
					ElexisEventDispatcher.reload(Termin.class);
				}

			}
		};
		newTerminAction = new RestrictedAction(EvACE.of(IAppointment.class, Right.CREATE), Messages.TagesView_newTermin) {
			{
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
				setToolTipText(Messages.TagesView_createNewTermin);
			}

			@Override
			public void doRun() {
				TerminDialog dlg = new TerminDialog(null);
				dlg.open();
				if (tv != null) {
					tv.refresh(true);
				}
			}
		};

		newTerminForAction = new Action("Neuer Termin für...") {
			{
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
				setToolTipText("Dialog zum Auswählen eines Kontakts für den Termin öffnen");
			}

			@Override
			public void run() {
				KontaktSelektor ksl = new KontaktSelektor(getSite().getShell(), Kontakt.class, "Terminvergabe",
						"Bitte wählen Sie aus, wer einen Termin braucht", Kontakt.DEFAULT_SORT);
				IPlannable sel = getSelection();
				TerminDialog dlg = new TerminDialog(null);
				dlg.setCollisionErrorLevel(CollisionErrorLevel.WARNING);
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
				IPlannable[] liste = Plannables.loadDay(agenda.getActResource(), agenda.getActDate());
				TerminListeDruckenDialog dlg = new TerminListeDruckenDialog(getViewSite().getShell(), liste);
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
				Patient patient = ElexisEventDispatcher.getSelectedPatient();
				if (patient != null) {
					Query<Termin> qbe = new Query<Termin>(Termin.class);
					qbe.add(Termin.FLD_PATIENT, Query.EQUALS, patient.getId());
					qbe.add(PersistentObject.FLD_DELETED, Query.NOT_EQUAL, StringConstants.ONE);
					qbe.add(Termin.FLD_TAG, Query.GREATER_OR_EQUAL, new TimeTool().toString(TimeTool.DATE_COMPACT));
					qbe.orderBy(false, Termin.FLD_TAG, Termin.FLD_BEGINN);
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
}
