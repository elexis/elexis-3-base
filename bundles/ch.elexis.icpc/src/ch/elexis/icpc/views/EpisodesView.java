/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.icpc.views;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.ui.actions.ObjectFilterRegistry;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.util.ViewMenus.IMenuPopulator;
import ch.elexis.data.Konsultation;
import ch.elexis.icpc.KonsFilter;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.service.IcpcModelServiceHolder;
import jakarta.inject.Inject;

public class EpisodesView extends ViewPart {
	public static final String ID = "ch.elexis.icpc.episodesView"; //$NON-NLS-1$
	EpisodesDisplay display;
	KonsFilter episodesFilter = new KonsFilter(this);
	private IAction addEpisodeAction, removeEpisodeAction, editEpisodeAction, activateEpisodeAction, konsFilterAction,
			removeDiagnosesAction;

	@Inject
	void activePatient(@Optional IPatient patient) {
		if (display != null && !display.isDisposed()) {
			Display.getDefault().asyncExec(() -> {
				display.setPatient(patient);
			});
		}
	}

	@Inject
	void selectedEpisode(@Optional IcpcEpisode episode) {
		if (display != null && !display.isDisposed()) {
			Display.getDefault().asyncExec(() -> {
				if (episode != null) {
					if (episode.getStatus() == 1) {
						activateEpisodeAction.setChecked(true);
					} else {
						activateEpisodeAction.setChecked(false);
					}
					if (konsFilterAction.isChecked()) {
						episodesFilter.setProblem(episode);
					}
				} else {
					episodesFilter.setProblem(null);
				}
			});
		}
	}

	@Optional
	@Inject
	void updateEpisode(@UIEventTopic(ElexisEventTopics.EVENT_UPDATE) IcpcEpisode episode) {
		if (display != null && !display.isDisposed()) {
			display.tvEpisodes.refresh();
		}
	}

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout());
		display = new EpisodesDisplay(parent);
		display.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		makeActions();
		ViewMenus menu = new ViewMenus(getViewSite());

		/*
		 * menu.createViewerContextMenu(display.tvEpisodes, activateEpisodeAction,
		 * editEpisodeAction, null, removeEpisodeAction);
		 */
		menu.createControlContextMenu(display.tvEpisodes.getControl(), new IMenuPopulator() {
			@Override
			public IAction[] fillMenu() {
				return new IAction[] { activateEpisodeAction, editEpisodeAction, null, removeEpisodeAction,
						removeDiagnosesAction };

			}
		});

		menu.createToolbar(konsFilterAction, addEpisodeAction, editEpisodeAction);
	}

	@Override
	public void setFocus() {
		display.setFocus();
	}

	private void makeActions() {
		addEpisodeAction = new Action("Neues Problem") {
			{
				setToolTipText("Eine neues Problem erstellen");
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
			}

			@Override
			public void run() {
				EditEpisodeDialog dlg = new EditEpisodeDialog(getViewSite().getShell(), null);
				if (dlg.open() == Dialog.OK) {
					display.tvEpisodes.refresh();
				}
			}
		};
		removeEpisodeAction = new Action("Problem löschen") {
			{
				setToolTipText("Das gewählte Problem unwiderruflich löschen");
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}

			@Override
			public void run() {
				IcpcEpisode act = display.getSelectedEpisode();
				if (act != null) {
					IcpcModelServiceHolder.get().delete(act);
					display.tvEpisodes.refresh();
				}
			}
		};

		removeDiagnosesAction = new Action("Diagnosen entfernen") {
			{
				setToolTipText("Entfernt die Verknüpfungen mit Diagnosen");
				setImageDescriptor(Images.IMG_REMOVEITEM.getImageDescriptor());
			}

			@Override
			public void run() {
				IcpcEpisode act = display.getSelectedEpisode();
				if (act != null) {
					act.getDiagnosis().forEach(d -> act.removeDiagnosis(d));
					IcpcModelServiceHolder.get().save(act);
					display.tvEpisodes.refresh();
				}
			}
		};

		editEpisodeAction = new Action("Problem bearbeiten") {
			{
				setToolTipText("Problem bearbeiten");
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			}

			@Override
			public void run() {
				IcpcEpisode ep = display.getSelectedEpisode();
				if (ep != null) {
					EditEpisodeDialog dlg = new EditEpisodeDialog(getViewSite().getShell(), ep);
					if (dlg.open() == Dialog.OK) {
						display.tvEpisodes.refresh();
					}
				}
			}
		};
		activateEpisodeAction = new Action("Aktiv", Action.AS_CHECK_BOX) {
			{
				setToolTipText("Problem aktivieren oder deaktivieren");
			}

			@Override
			public void run() {
				IcpcEpisode ep = display.getSelectedEpisode();
				if (ep != null) {
					ep.setStatus(activateEpisodeAction.isChecked() ? 1 : 0);
					display.tvEpisodes.refresh();
				}
			}

		};

		konsFilterAction = new Action("Konsultationen filtern", Action.AS_CHECK_BOX) {
			{
				setToolTipText("Konsultationslisten auf markiertes Problem begrenzen");
				setImageDescriptor(Images.IMG_FILTER.getImageDescriptor());
			}

			@Override
			public void run() {
				if (!isChecked()) {
					ObjectFilterRegistry.getInstance().unregisterObjectFilter(Konsultation.class, episodesFilter);
				} else {
					ObjectFilterRegistry.getInstance().registerObjectFilter(Konsultation.class, episodesFilter);
					IcpcEpisode ep = display.getSelectedEpisode();
					episodesFilter.setProblem(ep);
				}
			}
		};

	}

	public void activateKonsFilterAction(final boolean bActivate) {
		konsFilterAction.setChecked(bActivate);
	}

}
