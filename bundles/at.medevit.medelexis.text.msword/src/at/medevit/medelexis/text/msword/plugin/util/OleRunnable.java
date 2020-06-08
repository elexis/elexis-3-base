/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;

/**
 * Runnable capable of carrying return values for executing OLE/COM commands.
 * 
 * @author thomashu
 * 
 */
public abstract class OleRunnable implements Runnable {
	
	protected Variant returnVariant;
	protected OleAutomation returnAutomation;
	protected Object returnObject;

	@Override
	public abstract void run();
}
