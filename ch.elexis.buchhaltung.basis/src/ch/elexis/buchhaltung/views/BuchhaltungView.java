/*******************************************************************************
 * Copyright (c) 2006, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.buchhaltung.views;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class BuchhaltungView extends ViewPart {
	public static final String ID = "ch.elexis.buchhaltung.basis.view"; //$NON-NLS-1$
	
	@Override
	public void createPartControl(Composite parent){
		new Composite(parent, SWT.BORDER);
		
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
}
