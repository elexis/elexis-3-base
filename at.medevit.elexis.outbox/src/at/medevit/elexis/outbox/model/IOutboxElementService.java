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
package at.medevit.elexis.outbox.model;

import java.util.List;

import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;

public interface IOutboxElementService {
	// do not change order, as we save the ordinal to the db, only adding new state is allowed
	public enum State {
		NEW, SEEN;
	}

	/**
	 * Create a new OutboxElement and tell registered listeners about it.
	 * 
	 * @param mandant
	 * @param patient
	 * @param state
	 * @return
	 */
	public void createOutboxElement(Patient patient, Kontakt mandant, PersistentObject object);
	
	/**
	 * Change the state of a OutboxElement and tell registered listeners about it.
	 * 
	 * @param mandant
	 * @param patient
	 * @param state
	 * @return
	 */
	public void changeOutboxElementState(OutboxElement element, State state);

	/**
	 * Get all OutboxElements with matching mandant, patient and state. By setting parameters to
	 * null the query can be broadened.
	 * 
	 * @param mandant
	 * @param patient
	 * @param state
	 * @return list of matching OutboxElement objects
	 */
	public List<OutboxElement> getOutboxElements(Mandant mandant, Patient patient, State state);
	
	/**
	 * Register a listener to the set of listeners.
	 * 
	 * @param listener
	 */
	public void addUpdateListener(IOutboxUpdateListener listener);
	
	/**
	 * Deregister a listener from the set of listeners.
	 * 
	 * @param listener
	 */
	public void removeUpdateListener(IOutboxUpdateListener listener);
}
