/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/
package at.medevit.elexis.weblinks.extension;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.program.Program;

import at.medevit.elexis.weblinks.model.WebLinkElement;
import at.medevit.elexis.weblinks.model.WebLinkElementUtil;

public class WebLinkAction extends Action {

	private WebLinkElement element;

	public WebLinkAction(WebLinkElement element) {
		this.element = element;
	}

	@Override
	public String getText() {
		return element.getText();
	}

	@Override
	public void run() {
		WebLinkKonsExtension.updatePlaceholders();

		String link = element.getLink();
		link = WebLinkElementUtil.replacePlaceholders(link);

		Program.launch(link);
	}
}
