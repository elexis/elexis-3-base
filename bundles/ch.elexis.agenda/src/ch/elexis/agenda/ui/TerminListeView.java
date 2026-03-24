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

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.actions.AgendaActions;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IAppointmentSeries;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.agenda.CollisionErrorLevel;
import ch.elexis.core.model.builder.IAppointmentBuilder;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.AppointmentState;
import ch.elexis.core.types.AppointmentType;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.core.ui.locks.LockRequestingRestrictedAction;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.dialogs.AppointmentDialog;
import ch.elexis.dialogs.TermineDruckenDialog;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;

public class TerminListeView extends ViewPart implements IRefreshable {
	public static final String ID = "ch.elexis.agenda.Terminliste";
	ScrolledForm form;
	CommonViewer cv = new CommonViewer();
	LockRequestingRestrictedAction<IAppointment> terminAendernAction;
	IAction newTerminAction;
	IAction printAction;
	IAction printSeriesAction;
	IAction delAction;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);
	private CommonViewerContentProvider contentProvider;

	@Inject
	void activePatient(@org.eclipse.e4.core.di.annotations.Optional IPatient patient) {
		Display.getDefault().asyncExec(() -> {
			refresh();
		});
	}

	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	public void reload(@UIEventTopic(ElexisEventTopics.EVENT_UPDATE) IAppointment appointment) {
		if (cv != null) {
			cv.notify(CommonViewer.Message.update, appointment);
		}
	}

	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	public void reloadDelete(@UIEventTopic(ElexisEventTopics.EVENT_RELOAD) Object payload) {
		if (payload == IAppointment.class || payload instanceof IAppointment) {
			if (cv != null) {
				Display.getDefault().asyncExec(() -> {
					cv.notify(CommonViewer.Message.update);
				});
			}
		}
	}

	public TerminListeView() {
		terminAendernAction = new LockRequestingRestrictedAction<IAppointment>(
				EvACE.of(IAppointment.class, Right.UPDATE),
				ch.elexis.agenda.Messages.TagesView_changeTermin) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(ch.elexis.agenda.Messages.TagesView_changeThisTermin);
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
						dlg.open();
					}
				});
				if (cv != null) {
					cv.notify(CommonViewer.Message.update);
				}
			}
		};
	}

	@Override
	public void createPartControl(Composite parent) {
		form = UiDesk.getToolkit().createScrolledForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new GridLayout());

		contentProvider = new CommonViewerContentProvider(cv) {

			private static final int QUERY_LIMIT = 15;

			@Override
			public Object[] getElements(final Object inputElement) {
				Optional<IPatient> actPat = ContextServiceHolder.get().getActivePatient();
				IQuery<IAppointment> query = getBaseQuery();
				if (actPat.isPresent()) {
					query.and("patId", COMPARATOR.EQUALS, actPat.get().getId());
				} else {
					return new Object[0];
				}
				query.orderBy("tag", ORDER.DESC);
				List<IAppointment> elements = query.execute().stream().filter(a -> Plannables.isNotAllDay(a)).toList();
				commonViewer.setLimitReached(elements.size() == QUERY_LIMIT, QUERY_LIMIT);
				return elements.toArray(new Object[elements.size()]);
			}

			@Override
			protected IQuery<IAppointment> getBaseQuery() {
				IQuery<IAppointment> ret = CoreModelServiceHolder.get().getQuery(IAppointment.class);
				if (!ignoreLimit) {
					ret.limit(QUERY_LIMIT);
				}
				return ret;
			}

			@Override
			public void init() {
				super.init();
				setIgnoreLimit(false);
			}
		};

		ViewerConfigurer vc = new ViewerConfigurer(contentProvider, new AppointmentLabelProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_TABLE, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI,
						cv));
		vc.setContentType(ContentType.GENERICOBJECT);
		cv.create(vc, body, SWT.NONE, this);
		sort(SWT.DOWN);
		cv.getViewerWidget().addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				terminAendernAction.run();
			}
		});

		cv.getViewerWidget().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				boolean isRecurring = false;
				boolean hasSelection = false;
				int selectionCount = 0;

				if (sel != null && !sel.isEmpty()) {
					hasSelection = true;
					selectionCount = sel.size();
					Object o = sel.getFirstElement();
					if (o instanceof IAppointment) {
						IAppointment app = (IAppointment) o;
						ContextServiceHolder.get().setTyped(app);
						isRecurring = app.isRecurring();
					}
				} else {
					ContextServiceHolder.get().removeTyped(IAppointment.class);
				}

				if (terminAendernAction != null) {
					terminAendernAction.setEnabled(selectionCount == 1);
				}

				if (printAction != null) {
					printAction.setEnabled(hasSelection);
				}
				if (printSeriesAction != null) {
					printSeriesAction.setEnabled(isRecurring);
				}
				if (delAction != null) {
					AgendaActions.updateActions();
				}
			}
		});

		makeActions();

		getSite().getPage().addPartListener(udpateOnVisible);
	}

	private void makeActions() {
		delAction = AgendaActions.getDelTerminAction();

		newTerminAction = new Action(Messages.TagesView_newTermin) {
			{
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
				setToolTipText(Messages.TagesView_createNewTermin);
			}

			@Override
			public void run() {
				String resource = ConfigServiceHolder.getUser(PreferenceConstants.AG_BEREICH, StringUtils.EMPTY);
				LocalDateTime start = LocalDateTime.now();
				int minute = start.getMinute();
				int mod = minute % 15;
				start = start.plusMinutes(15 - mod).withSecond(0).withNano(0);
				LocalDateTime end = start.plusMinutes(30);
				IAppointment appointment = new IAppointmentBuilder(CoreModelServiceHolder.get(), resource, start, end,
						AppointmentServiceHolder.get().getType(AppointmentType.DEFAULT),
						AppointmentServiceHolder.get().getState(AppointmentState.DEFAULT)).build();
				Optional<IPatient> pat = ContextServiceHolder.get().getActivePatient();
				if (pat.isPresent()) {
					appointment.setSubjectOrPatient(pat.get().getId());
				}

				AppointmentDialog dlg = new AppointmentDialog(appointment);
				dlg.setCollisionErrorLevel(CollisionErrorLevel.ERROR);
				dlg.setExpanded(true);
				dlg.open();
				refresh();
			}
		};

		printAction = new Action(Messages.TerminListeView_PrintSelected) {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setToolTipText(Messages.TerminListeView_PrintSelectedTooltip);
			}

			@Override
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) cv.getViewerWidget().getSelection();
				if (sel != null && !sel.isEmpty()) {
					List<IAppointment> list = new ArrayList<>();
					for (Object obj : sel.toList()) {
						if (obj instanceof IAppointment) {
							list.add((IAppointment) obj);
						}
					}
					Collections.sort(list, new Comparator<IAppointment>() {
						@Override
						public int compare(IAppointment o1, IAppointment o2) {
							return o1.getStartTime().compareTo(o2.getStartTime());
						}
					});

					TermineDruckenDialog dlg = new TermineDruckenDialog(getViewSite().getShell(), list);
					dlg.setBlockOnOpen(true);
					dlg.open();
				}
			}
		};

		printSeriesAction = new Action(Messages.TerminListeView_PrintSeries) {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setToolTipText(Messages.TerminListeView_PrintSeriesTooltip);
				setEnabled(false);
			}

			@Override
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) cv.getViewerWidget().getSelection();
				if (sel != null && !sel.isEmpty()) {
					Object o = sel.getFirstElement();
					if (o instanceof IAppointment) {
						IAppointment app = (IAppointment) o;
						if (app.isRecurring()) {
							Optional<IAppointmentSeries> series = AppointmentServiceHolder.get()
									.getAppointmentSeries(app);
							if (series.isPresent()) {
								List<IAppointment> list = series.get().getAppointments();
								Collections.sort(list, (o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()));

								TermineDruckenDialog dlg = new TermineDruckenDialog(getViewSite().getShell(), list);
								dlg.setBlockOnOpen(true);
								dlg.open();
							}
						}
					}
				}
			}
		};
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection sel = (IStructuredSelection) cv.getViewerWidget().getSelection();
				boolean isRecurring = false;
				int count = 0;

				if (sel != null && !sel.isEmpty()) {
					count = sel.size();
					Object obj = sel.getFirstElement();
					if (obj instanceof IAppointment) {
						isRecurring = ((IAppointment) obj).isRecurring();
					}
				}

				manager.add(newTerminAction);
				manager.add(new Separator());

				if (count == 1) {
					manager.add(terminAendernAction);
					manager.add(AgendaActions.getTerminStatusAction());
				}

				manager.add(printAction);

				if (isRecurring) {
					manager.add(printSeriesAction);
				}

				manager.add(new Separator());
				manager.add(delAction);
				AgendaActions.updateActions();
			}
		});

		Menu menu = menuMgr.createContextMenu(cv.getViewerWidget().getControl());
		cv.getViewerWidget().getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, cv.getViewerWidget());
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.add(newTerminAction);
		IMenuManager viewMenu = actionBars.getMenuManager();
		viewMenu.add(printAction);
		viewMenu.add(printSeriesAction);

		actionBars.updateActionBars();
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
	}

	@Override
	public void setFocus() {
	}

	private void updateSelection(IPatient patient) {
		if (patient == null) {
			form.setText(Messages.TerminListView_noPatientSelected);
			ContextServiceHolder.get().removeTyped(IAppointment.class);
			AgendaActions.updateActions();

			if (printAction != null)
				printAction.setEnabled(false);
			if (printSeriesAction != null)
				printSeriesAction.setEnabled(false);
			if (terminAendernAction != null)
				terminAendernAction.setEnabled(false);
			if (newTerminAction != null)
				newTerminAction.setEnabled(false);

		} else {
			form.setText(patient.getLabel());
			if (newTerminAction != null)
				newTerminAction.setEnabled(true);
			if (cv.getViewerWidget().getContentProvider() instanceof CommonViewerContentProvider) {
				contentProvider.resetLimit();
			}
			cv.notify(CommonViewer.Message.update);
		}
	}

	/**
	 * Sorts the appointments in the TerminListView. Use SWT.UP for ascending and
	 * SWT.DOWN for descending.
	 *
	 * @param sortDirection
	 */
	public void sort(final int sortDirection) {
		cv.getViewerWidget().setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof IAppointment && e2 instanceof IAppointment) {
					int rc = ((IAppointment) e1).getStartTime().compareTo(((IAppointment) e2).getStartTime());
					// If descending order, flip the direction
					if (sortDirection == SWT.DOWN) {
						rc = -rc;
					}
					return rc;
				}
				return super.compare(viewer, e1, e2);
			}
		});
	}

	@Override
	public void refresh() {
		if (CoreUiUtil.isActiveControl(form)) {
			if (cv.getViewerWidget().getLabelProvider() instanceof AppointmentLabelProvider) {
				((AppointmentLabelProvider) cv.getViewerWidget().getLabelProvider()).reloadColors();
			}
			updateSelection(ContextServiceHolder.get().getActivePatient().orElse(null));
		}
	}

	private static final class AppointmentLabelProvider extends LabelProvider implements IColorProvider {

		private Color colorPast;
		private Color colorFuture;
		private LocalDateTime cachedNow;

		public AppointmentLabelProvider() {
			reloadColors();
		}

		public void reloadColors() {
			cachedNow = LocalDateTime.now();
			boolean useGlobal = ConfigServiceHolder.getGlobal(PreferenceConstants.TL_USE_GLOBAL_SETTINGS, false);

			String hexPast;
			String hexFuture;

			if (useGlobal) {
				hexPast = ConfigServiceHolder.getGlobal(PreferenceConstants.TL_PAST_BG_COLOR,
						PreferenceConstants.TL_PAST_BG_COLOR_DEFAULT);
				hexFuture = ConfigServiceHolder.getGlobal(PreferenceConstants.TL_FUTURE_BG_COLOR,
						PreferenceConstants.TL_FUTURE_BG_COLOR_DEFAULT);
			} else {
				hexPast = ConfigServiceHolder.getUser(PreferenceConstants.TL_PAST_BG_COLOR,
						PreferenceConstants.TL_PAST_BG_COLOR_DEFAULT);
				hexFuture = ConfigServiceHolder.getUser(PreferenceConstants.TL_FUTURE_BG_COLOR,
						PreferenceConstants.TL_FUTURE_BG_COLOR_DEFAULT);
			}

			colorPast = UiDesk.getColorFromRGB(hexPast);
			colorFuture = UiDesk.getColorFromRGB(hexFuture);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof IAppointment) {
				IAppointment termin = (IAppointment) element;
				StringBuilder sbLabel = new StringBuilder();

				// day
				if (termin.getStartTime() != null) {
					TimeTool tt = new TimeTool(termin.getStartTime());
					sbLabel.append(tt.toString(TimeTool.DATE_GER));
					String dayShort = termin.getStartTime().getDayOfWeek().getDisplayName(TextStyle.SHORT,
							Locale.getDefault());
					if (dayShort != null) {
						sbLabel.append(" (" + dayShort + ")");
					}
					sbLabel.append(", ");
					sbLabel.append(tt.toString(TimeTool.TIME_SMALL));
				} else {
					sbLabel.append("?");
				}
				sbLabel.append(" - ");

				if (termin.getEndTime() != null) {
					TimeTool te = new TimeTool(termin.getEndTime());
					sbLabel.append(te.toString(TimeTool.TIME_SMALL));
				} else {
					sbLabel.append("?");
				}

				sbLabel.append(" (");
				sbLabel.append(termin.getType());
				sbLabel.append(", ");
				sbLabel.append(termin.getState());
				sbLabel.append("), ");

				sbLabel.append(termin.getSchedule());

				if (termin.getReason() != null && !termin.getReason().isEmpty()) {
					sbLabel.append(" (");
					sbLabel.append(termin.getReason());
					sbLabel.append(")");
				}

				return sbLabel.toString();
			}
			return super.getText(element);
		}

		@Override
		public Color getBackground(Object element) {
			if (isPastAppointment(element)) {
				return colorPast;
			} else {
				return colorFuture;
			}
		}

		@Override
		public Color getForeground(Object element) {
			Color bg = getBackground(element);
			if (bg == null) {
				return null;
			}
			return isDark(bg) ? Display.getDefault().getSystemColor(SWT.COLOR_WHITE)
					: Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		}

		private boolean isDark(Color color) {
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			double luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0;
			return luminance < 0.55;
		}

		private boolean isPastAppointment(Object element) {
			if (!(element instanceof IAppointment)) {
				return false;
			}
			IAppointment a = (IAppointment) element;
			LocalDateTime now = (cachedNow != null) ? cachedNow : LocalDateTime.now();
			if (a.getEndTime() != null) {
				return a.getEndTime().isBefore(now);
			}
			if (a.getStartTime() != null) {
				return a.getStartTime().isBefore(now);
			}
			return false;
		}
	}
}
