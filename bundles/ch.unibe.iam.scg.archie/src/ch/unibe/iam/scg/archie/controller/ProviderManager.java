/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.controller;

import java.util.Observable;

import ch.unibe.iam.scg.archie.model.AbstractDataProvider;

/**
 * <p>
 * Singleton class that manages the existence of a provider during the lifecycle
 * of this application. This class is used by different views and GUI classes
 * that need to have access to the currently <em>selected</em> provider and
 * facilitates this access by providing one central place for a provider being
 * used.
 * </p>
 *
 * $Id: ProviderManager.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ProviderManager extends Observable {

	/**
	 * Instance of this provider manager. There's always only one thorugh the entire
	 * lifecycle of this application.
	 */
	private static ProviderManager INSTANCE;

	/**
	 * The currently managed data provider
	 */
	private AbstractDataProvider provider;

	/**
	 * Private constructor.
	 */
	private ProviderManager() {
		this.provider = null;
	}

	/**
	 * Returns an instance of this provider manager.
	 *
	 * @return An instance of this provider manager.
	 */
	public static ProviderManager getInstance() {
		if (ProviderManager.INSTANCE == null) {
			ProviderManager.INSTANCE = new ProviderManager();
		}
		return ProviderManager.INSTANCE;
	}

	/**
	 * Returns the currently set provider of this provider manager.
	 *
	 * @return A data provider.
	 */
	public AbstractDataProvider getProvider() {
		return this.provider;
	}

	/**
	 * Sets the given provider for this manager.
	 *
	 * @param provider A data provider.
	 */
	public void setProvider(AbstractDataProvider provider) {
		this.provider = provider;
		this.setChanged();
		this.notifyObservers(this.getProvider());
	}

	/**
	 * Checks whether a provider for this manager has already been set or no.
	 *
	 * @return True if this manager has a provider, false else.
	 */
	public boolean hasProvider() {
		return this.provider != null;
	}
}