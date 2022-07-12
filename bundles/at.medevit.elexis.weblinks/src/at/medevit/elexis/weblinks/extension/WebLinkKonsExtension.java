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

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.StyleRange;

import at.medevit.elexis.weblinks.model.WebLinkElement;
import at.medevit.elexis.weblinks.model.WebLinkElementUtil;
import ch.elexis.core.ui.text.EnhancedTextField;
import ch.elexis.core.ui.text.IRichTextDisplay;
import ch.elexis.core.ui.util.IKonsExtension;
import ch.rgw.tools.GenericRange;
import ch.rgw.tools.StringTool;

public class WebLinkKonsExtension implements IKonsExtension {

	private static IRichTextDisplay textField;

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public String connect(IRichTextDisplay tf) {
		textField = (EnhancedTextField) tf;
		return "at.medevit.elexis.decisionsupport.generic.GenericKonsExtension"; //$NON-NLS-1$
	}

	@Override
	public boolean doLayout(StyleRange styleRange, String provider, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doXRef(String refProvider, String refID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IAction[] getActions() {
		List<WebLinkAction> actions = new ArrayList<WebLinkAction>();
		List<WebLinkElement> elements = WebLinkElementUtil.loadElements();
		for (WebLinkElement decisionSupportElement : elements) {
			actions.add(new WebLinkAction(decisionSupportElement));
		}
		return actions.toArray(new WebLinkAction[actions.size()]);
	}

	@Override
	public void insert(Object o, int pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeXRef(String refProvider, String refID) {
		// TODO Auto-generated method stub

	}

	public static void updatePlaceholders() {
		String selection = StringUtils.EMPTY;
		if (textField != null) {
			String text = textField.getContentsPlaintext();
			GenericRange gr = textField.getSelectedRange();
			if (gr.getLength() == 0) {
				selection = StringTool.getWordAtIndex(text, gr.getPos());
			} else {
				selection = text.substring(gr.getPos(), gr.getPos() + gr.getLength());
			}
			selection = selection.trim().replace("\r\n", StringUtils.SPACE); //$NON-NLS-1$
		}

		WebLinkElementUtil.setPlaceholder("text.selection", selection); //$NON-NLS-1$
	}

}
