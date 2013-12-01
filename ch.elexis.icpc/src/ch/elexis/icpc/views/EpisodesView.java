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

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.actions.ObjectFilterRegistry;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.Episode;
import ch.elexis.icpc.KonsFilter;
import ch.elexis.util.SWTHelper;
import ch.elexis.util.ViewMenus;
import ch.elexis.util.ViewMenus.IMenuPopulator;

public class EpisodesView extends ViewPart implements IActivationListener {
	public static final String ID = "ch.elexis.icpc.episodesView";
	EpisodesDisplay display;
	KonsFilter episodesFilter = new KonsFilter(this);
	private IAction addEpisodeAction, removeEpisodeAction, editEpisodeAction,
			activateEpisodeAction, konsFilterAction, removeDiagnosesAction;
	
	private ElexisEventListenerImpl eeli_kons = new ElexisEventListenerImpl(Konsultation.class,
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
	
	private ElexisEventListenerImpl eeli_pat = new ElexisEventListenerImpl(Patient.class,
		ElexisEvent.EVENT_SELECTED) {
		
		@Override
		public void runInUi(ElexisEvent ev){
			display.setPatient((Patient) ev.getObject());
		}
		
	};
	
	private ElexisEventListenerImpl eeli_episode = new ElexisEventListenerImpl(Episode.class,
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
				setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_NEW));
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
				setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_DELETE));
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
				setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_REMOVEITEM));
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
				setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_EDIT));
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
				setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_FILTER));
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
