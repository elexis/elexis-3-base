/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.startup;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;

public class Startup implements IStartup {
	private static final String CONTEXT_ID = "ch.elexis.context.itmed.fop.printing";

	@Override
	public void earlyStartup() {
		IContextService contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
		contextService.activateContext(CONTEXT_ID, null, true);
	}

}