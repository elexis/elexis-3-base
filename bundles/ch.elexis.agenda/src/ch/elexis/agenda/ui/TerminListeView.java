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

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.agenda.CollisionErrorLevel;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
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
import ch.rgw.tools.TimeTool;

public class TerminListeView extends ViewPart implements IRefreshable {
	public static final String ID = "ch.elexis.agenda.Terminliste";
	ScrolledForm form;
	CommonViewer cv = new CommonViewer();
	LockRequestingRestrictedAction<IAppointment> terminAendernAction;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	@Inject
	void activePatient(@Optional IPatient patient) {
		Display.getDefault().asyncExec(() -> {
			refresh();
		});
	}

	@Inject
	@Optional
	public void reload(@UIEventTopic(ElexisEventTopics.EVENT_UPDATE) IAppointment appointment) {
		if (cv != null) {
			cv.notify(CommonViewer.Message.update, appointment);
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

		CommonViewerContentProvider contentProvider = new ch.elexis.core.ui.util.viewers.CommonViewerContentProvider(
				cv) {

			private static final int QUERY_LIMIT = 50;

			@Override
			public Object[] getElements(final Object inputElement) {
				java.util.Optional<IPatient> actPat = ContextServiceHolder.get().getActivePatient();
				IQuery<?> query = getBaseQuery();
				if (actPat.isPresent()) {
					query.and("patId", COMPARATOR.EQUALS, actPat.get().getId());
				} else {
					return new Object[0];
				}
				query.orderBy("tag", ORDER.DESC);
				List<?> elements = query.execute();
				commonViewer.setLimitReached(elements.size() == QUERY_LIMIT, QUERY_LIMIT);
				return elements.toArray(new Object[elements.size()]);
			}

			@Override
			protected IQuery<?> getBaseQuery() {
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

		ViewerConfigurer vc = new ViewerConfigurer(contentProvider, new LabelProvider() {
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

						// start time
						sbLabel.append(tt.toString(TimeTool.TIME_SMALL));
					} else {
						sbLabel.append("?");
					}
					sbLabel.append(" - ");

					if (termin.getEndTime() != null) {
						TimeTool te = new TimeTool(termin.getEndTime());
						// end time
						sbLabel.append(te.toString(TimeTool.TIME_SMALL));
					} else {
						sbLabel.append("?");
					}

					// type
					sbLabel.append(" (");
					sbLabel.append(termin.getType());
					sbLabel.append(", ");
					// status
					sbLabel.append(termin.getState());
					sbLabel.append("), ");

					// bereich
					sbLabel.append(termin.getSchedule());

					// grund if set
					if (termin.getReason() != null && !termin.getReason().isEmpty()) {
						sbLabel.append(" (");
						sbLabel.append(termin.getReason());
						sbLabel.append(")");
					}
					return sbLabel.toString();
				}
				return super.getText(element);
			}
		}, new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_TABLE, SWT.V_SCROLL | SWT.FULL_SELECTION, cv));
		vc.setContentType(ContentType.GENERICOBJECT);
		cv.create(vc, body, SWT.NONE, this);
		cv.getViewerWidget().addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				terminAendernAction.run();
			}
		});

		getSite().getPage().addPartListener(udpateOnVisible);
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
		} else {
			form.setText(patient.getLabel());
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
			updateSelection(ContextServiceHolder.get().getActivePatient().orElse(null));
		}
	}
}
