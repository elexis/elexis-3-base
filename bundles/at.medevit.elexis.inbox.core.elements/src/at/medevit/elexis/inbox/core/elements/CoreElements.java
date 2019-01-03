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
package at.medevit.elexis.inbox.core.elements;

import at.medevit.elexis.inbox.model.IInboxElementsProvider;
import ch.elexis.core.data.events.ElexisEventDispatcher;

public class CoreElements implements IInboxElementsProvider {
	
	private LabResultCreateListener labResultListener;
	
	@Override
	public void activate(){
		// add inbox creation of LabResult
		labResultListener = new LabResultCreateListener();
		ElexisEventDispatcher.getInstance().addListeners(labResultListener);
	}
	
	@Override
	public void deactivate(){
		if (labResultListener != null) {
			ElexisEventDispatcher.getInstance().removeListeners(labResultListener);
			labResultListener.shutdown();
		}
		labResultListener = null;
	}
}
