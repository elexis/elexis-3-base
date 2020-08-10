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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;

public interface IOutboxElementService {
	// do not change order, as we save the ordinal to the db, only adding new state is allowed
	public enum State {
			NEW, SENT;
	}
	
	/**
	 * Create a new OutboxElement and tell registered listeners about it.
	 * 
	 * @param mandant
	 * @param patient
	 * @param uri
	 * @return
	 */
	public IOutboxElement createOutboxElement(IPatient patient, IMandator mandant, String uri);
	
	/**
	 * Delete OutboxElement and tell registered listeners about it.
	 * 
	 * @param outboxElement
	 * @return
	 */
	public void deleteOutboxElement(IOutboxElement outboxElement);
	
	/**
	 * Change the state of a OutboxElement and tell registered listeners about it.
	 * 
	 * @param mandant
	 * @param patient
	 * @param state
	 * @return
	 */
	public void changeOutboxElementState(IOutboxElement element, State state);

	/**
	 * Get all OutboxElements with matching mandant, patient and state. By setting parameters to
	 * null the query can be broadened.
	 * 
	 * @param mandant
	 * @param patient
	 * @param state
	 * @return list of matching OutboxElement objects
	 */
	public List<IOutboxElement> getOutboxElements(IMandator mandant, IPatient patient, State state);
	
	/**
	 * Get all OutboxElements with matching uri and state. By setting parameters to null the query
	 * can be broadened.
	 * 
	 * @param uri
	 * @param state
	 * @return
	 */
	public List<IOutboxElement> getOutboxElements(String uri, State state);
	
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
	

	/**
	 * Returns the contents as {@link InputStream}
	 * 
	 * @param outboxElement
	 * @return
	 */
	public InputStream getContentsAsStream(IOutboxElement outboxElement)
		throws IOException;
	
	/**
	 * Returns the created temp file with the contents in it.
	 * 
	 * @param folder
	 *            the location for temp files
	 * @param outboxElement
	 * @return
	 */
	public Optional<File> createTempFileWithContents(File folder, IOutboxElement outboxElement)
		throws IOException;
}
