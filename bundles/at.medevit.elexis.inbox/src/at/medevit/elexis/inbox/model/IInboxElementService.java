/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.inbox.model;

import java.util.List;

import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;

public interface IInboxElementService {
	// do not change order, as we save the ordinal to the db, only adding new state is allowed
	public enum State {
		NEW, SEEN;
	}

	/**
	 * Create a new InboxElement and tell registered listeners about it.
	 * 
	 * @param mandant
	 * @param patient
	 * @param object
	 * @return
	 */
	public void createInboxElement(Patient patient, Kontakt mandant, PersistentObject object);
	
	/**
	 * Create a new InboxElement and tell registered listeners about it.
	 * 
	 * @param mandant
	 * @param patient
	 * @param file
	 * @param copyFile
	 * @return
	 */
	public void createInboxElement(Patient patient, Kontakt mandant, String file,
		boolean copyFile);
	
	/**
	 * Change the state of a InboxElement and tell registered listeners about it.
	 * 
	 * @param mandant
	 * @param patient
	 * @param state
	 * @return
	 */
	public void changeInboxElementState(InboxElement element, State state);

	/**
	 * Register a listener to the set of listeners.
	 * 
	 * @param listener
	 */
	public void addUpdateListener(IInboxUpdateListener listener);
	
	/**
	 * Deregister a listener from the set of listeners.
	 * 
	 * @param listener
	 */
	public void removeUpdateListener(IInboxUpdateListener listener);

	/**
	 * Get all InboxElements with matching mandant, patient and state. By setting parameters to null
	 * the query can be broadened.
	 * 
	 * @param mandant
	 * @param patient
	 * @param state
	 * @return list of matching InboxElement objects
	 */
	public List<InboxElement> getInboxElements(Mandant mandant, Patient patient, State state);
	
	/**
	 * Deactivate all {@link IInboxElementsProvider} implementations. Useful if for some reason no
	 * new {@link InboxElement} should be created. For example on initial import of data from other
	 * database.
	 */
	public void deactivateProviders();
	
	/**
	 * Activate all {@link IInboxElementsProvider} implementations. Re activate after using
	 * {@link IInboxElementService#deactivateProviders()}. Initially all providers are active.
	 */
	public void activateProviders();
}
