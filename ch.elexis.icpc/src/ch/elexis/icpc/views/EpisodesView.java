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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.actions.ObjectFilterRegistry;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.Episode;
import ch.elexis.icpc.KonsFilter;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.util.ViewMenus.IMenuPopulator;

public class EpisodesView extends ViewPart implements IActivationListener {
	public static final String ID = "ch.elexis.icpc.episodesView";
	EpisodesDisplay display;
	KonsFilter episodesFilter = new KonsFilter(this);
	private IAction addEpisodeAction, removeEpisodeAction, editEpisodeAction,
			activateEpisodeAction, konsFilterAction, removeDiagnosesAction;
	
	private ElexisUiEventListenerImpl eeli_kons = new ElexisUiEventListenerImpl(Konsultation.class,
		ElexisEvent.EVENT_CREATE) {
		
		@Override
		public void catchElexisEvent(ElexisEvent ev){
			switch (ev.getType()) {
			case ElexisEvent.EVENT_CREATE:
				/*
				 * Konsultation k = (Konsultation) ev.getObject(); Samdas entry = k.getEntryRaw();
				 * Record record = entry.getRecord(); break;
				 */
			}
		}
		
	};
	
	private ElexisUiEventListenerImpl eeli_pat = new ElexisUiEventListenerImpl(Patient.class,
		ElexisEvent.EVENT_SELECTED) {
		
		@Override
		public void runInUi(ElexisEvent ev){
			display.setPatient((Patient) ev.getObject());
		}
		
	};
	
	private ElexisUiEventListenerImpl eeli_episode = new ElexisUiEventListenerImpl(Episode.class,
		ElexisEvent.EVENT_DESELECTED | ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_UPDATE) {
		@Override
		public void runInUi(ElexisEvent ev){
			Episode ep = (Episode) ev.getObject();
			switch (ev.getType()) {
			case ElexisEvent.EVENT_SELECTED:
				if (ep.getStatus() == Episode.ACTIVE) {
					activateEpisodeAction.setChecked(true);
				} else {
					activateEpisodeAction.setChecked(false);
				}
				if (konsFilterAction.isChecked()) {
					episodesFilter.setProblem(ep);
				}
				break;
			case ElexisEvent.EVENT_DESELECTED:
				episodesFilter.setProblem(null);
				break;
			case ElexisEvent.EVENT_UPDATE:
				display.tvEpisodes.refresh();
				break;
			
			}
			
		}
	};
	
	@Override
	public void createPartControl(final Composite parent){
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
			public IAction[] fillMenu(){
				return new IAction[] {
					activateEpisodeAction, editEpisodeAction, null, removeEpisodeAction,
					removeDiagnosesAction
				};
				
			}
		});
		
		menu.createToolbar(konsFilterAction, addEpisodeAction, editEpisodeAction);
		GlobalEventDispatcher.addActivationListener(this, getViewSite().getPart());
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	public void clearEvent(final Class<? extends PersistentObject> template){
		// TODO Auto-generated method stub
		
	}
	
	public void activation(final boolean mode){
		// TODO Auto-generated method stub
		
	}
	
	public void visible(final boolean mode){
		if (mode) {
			display.setPatient(ElexisEventDispatcher.getSelectedPatient());
			ElexisEventDispatcher.getInstance().addListeners(eeli_episode, eeli_kons, eeli_pat);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_episode, eeli_kons, eeli_pat);
		}
		
	}
	
	private void makeActions(){
		addEpisodeAction = new Action("Neues Problem") {
			{
				setToolTipText("Eine neues Problem erstellen");
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
			}
			
			@Override
			public void run(){
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
			public void run(){
				Episode act = display.getSelectedEpisode();
				if (act != null) {
					act.delete();
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
			public void run(){
				Episode act = display.getSelectedEpisode();
				if (act != null) {
					act.removeFromList("DiagLink");
					display.tvEpisodes.refresh();
				}
			}
		};
		
		editEpisodeAction = new Action("Problem bearbeiten") {
			{
				setToolTipText("Titel des Problems ändern");
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			}
			
			@Override
			public void run(){
				Episode ep = display.getSelectedEpisode();
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
			public void run(){
				Episode ep = display.getSelectedEpisode();
				if (ep != null) {
					ep.setStatus(activateEpisodeAction.isChecked() ? Episode.ACTIVE
							: Episode.INACTIVE);
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
			public void run(){
				if (!isChecked()) {
					ObjectFilterRegistry.getInstance().unregisterObjectFilter(Konsultation.class,
						episodesFilter);
				} else {
					ObjectFilterRegistry.getInstance().registerObjectFilter(Konsultation.class,
						episodesFilter);
					Episode ep = display.getSelectedEpisode();
					episodesFilter.setProblem(ep);
				}
			}
		};
		
	}
	
	public void activateKonsFilterAction(final boolean bActivate){
		konsFilterAction.setChecked(bActivate);
	}
	
}
