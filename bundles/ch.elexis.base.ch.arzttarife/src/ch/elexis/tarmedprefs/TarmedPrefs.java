/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.tarmedprefs;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.preferences.inputs.MultiplikatorEditor;

public class TarmedPrefs extends PreferencePage implements IWorkbenchPreferencePage {
	
	@Override
	protected Control createContents(final Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		new Label(ret, SWT.NONE).setText(Messages.TarmedPrefs_TPKVG);
		new MultiplikatorEditor(ret, "ch.elexis.data.TarmedLeistung" + "KVG"); //$NON-NLS-1$
		new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
		new Label(ret, SWT.NONE).setText(Messages.TarmedPrefs_TPUVG);
		new MultiplikatorEditor(ret, "ch.elexis.data.TarmedLeistung" + "UVG"); //$NON-NLS-1$
		new Label(ret, SWT.NONE).setText(Messages.TarmedPrefs_TPIV); 
		new MultiplikatorEditor(ret, "ch.elexis.data.TarmedLeistung" + "IV"); //$NON-NLS-1$
		new Label(ret, SWT.NONE).setText(Messages.TarmedPrefs_TPMV); 
		new MultiplikatorEditor(ret, "ch.elexis.data.TarmedLeistung" + "MV"); //$NON-NLS-1$
		return ret;
	}
	
	public void init(final IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
}
