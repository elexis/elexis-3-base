package com.hilotec.elexis.kgview;

import java.lang.reflect.ParameterizedType;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.PersistentObject;

/**
 * Helper-Klasse fuer Code der auf dem Laufenden bleiben muss bezueglich eines aktuell ausgewaehlten
 * PersistentObjects.
 */
public abstract class POSelectionListener<P extends PersistentObject> implements
		ElexisEventListener {
	private P current = null;
	private Class<P> pclass;
	private ElexisEventDispatcher eed;
	
	/** Initialisiert den Listener */
	@SuppressWarnings("unchecked")
	public void init(){
		// Hack um ans .class von P zu kommen.
		pclass =
			(Class<P>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		
		eetmpl =
			new ElexisEvent(null, pclass, ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_DESELECTED
				| ElexisEvent.EVENT_DELETE);
		eed = ElexisEventDispatcher.getInstance();
		eed.addListeners(this);
		
		System.out.println("POSELECTION INIT " + pclass);
		
		current = (P) ElexisEventDispatcher.getSelected(pclass);
		if (current != null)
			selected(current);
	}
	
	/** Deakitviert den Listener */
	public void destroy(){
		eed.removeListeners(this);
	}
	
	public void catchElexisEvent(ElexisEvent ev){
		@SuppressWarnings("unchecked")
		P k = (P) ev.getObject();
		
		if (ev.getType() == ElexisEvent.EVENT_DESELECTED && current != null) {
			// PO nicht mehr ausgewaehlt
			runDeselected(current);
			current = null;
		} else if (ev.getType() == ElexisEvent.EVENT_DELETE && k != null && k.equals(current)) {
			// PO geloescht
			runDeselected(current);
			current = null;
		} else if (ev.getType() == ElexisEvent.EVENT_SELECTED && !k.equals(current)) {
			// Wenn noch ein anderer ausgewaehlt war (sollte nicht passieren)
			if (current != null)
				runDeselected(current);
			current = k;
			runSelected(k);
		}
	}
	
	private ElexisEvent eetmpl;
	
	public ElexisEvent getElexisEventFilter(){
		return eetmpl;
	}
	
	private void runDeselected(final P kons){
		UiDesk.syncExec(new Runnable() {
			public void run(){
				deselected(kons);
			}
		});
	}
	
	private void runSelected(final P kons){
		UiDesk.syncExec(new Runnable() {
			public void run(){
				selected(kons);
			}
		});
	}
	
	/**
	 * Wird aufgerufen wenn das aktuelle PersistentObject nicht mehr ausgewaehlt ist. Wird im GUI
	 * Thread gestartet.
	 */
	protected void deselected(P po){}
	
	/**
	 * Wird aufgerufen wenn ein neues PersistentObject ausgewaehlt wurde. Wird im GUI Thread
	 * gestartet.
	 */
	protected void selected(P po){}
}
