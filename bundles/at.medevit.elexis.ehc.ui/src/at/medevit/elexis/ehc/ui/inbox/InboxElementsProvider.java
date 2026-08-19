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
package at.medevit.elexis.ehc.ui.inbox;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.model.EhcDocument;
import at.medevit.elexis.ehc.ui.service.ServiceComponent;
import at.medevit.elexis.inbox.model.IInboxElementsProvider;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;

public class InboxElementsProvider implements IInboxElementsProvider, InboxListener {

	private static Logger logger = LoggerFactory.getLogger(InboxElementsProvider.class);

	private InboxWatcher watcher;

	public InboxElementsProvider() {
	}

	@Override
	public void activate() {
		if (watcher != null) {
			watcher.stop();
		}
		watcher = new InboxWatcher();
		watcher.addInboxListener(this);
		watcher.start();
	}

	@Override
	public void deactivate() {
		if (watcher != null) {
			watcher.stop();
		}
	}

	@Override
	public void documentCreated(EhcDocument document) {
		Optional<IPatient> patient = NoPoUtil.loadAsIdentifiable(document.getPatient(), IPatient.class);
		patient.ifPresent(p -> {
			ServiceComponent.getInboxService().getInboxElementMandator("at.medevit.elexis.ehc.ui.inbox.uiprovider", p)
					.ifPresent(m -> {
						ServiceComponent.getInboxService().createInboxElement(p, m, document);
					});
		});
		
		
	}
}
