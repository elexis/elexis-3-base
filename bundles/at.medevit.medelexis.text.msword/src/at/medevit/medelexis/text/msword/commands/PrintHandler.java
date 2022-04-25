/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import at.medevit.medelexis.text.msword.plugin.WordTextPlugin;

public class PrintHandler extends AbstractHandler implements IHandler {

	public static final String ID = "at.medevit.medelexis.text.msword.printDialog"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String wordTextPluginHash = event.getParameter("at.medevit.medelexis.text.msword.WordTextPluginHash"); //$NON-NLS-1$
		WordTextPlugin.openPrintDialog(wordTextPluginHash);
		return null;
	}

}
