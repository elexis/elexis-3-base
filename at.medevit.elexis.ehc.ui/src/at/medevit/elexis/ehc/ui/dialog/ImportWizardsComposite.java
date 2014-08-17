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
package at.medevit.elexis.ehc.ui.dialog;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import at.medevit.elexis.ehc.ui.extension.ImportWizardsExtension;

public class ImportWizardsComposite extends Composite {
	
	private TreeViewer viewer;
	
	public ImportWizardsComposite(Composite parent, int style){
		super(parent, style);
		
		createContent();
	}
	
	private void createContent(){
		setLayout(new FillLayout());
		
		viewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.MULTI);
		
		viewer.setLabelProvider(new WizardLabelProvider());
		
		viewer.setContentProvider(new WizardContentProvider());
		
		viewer.setInput(ImportWizardsExtension.getCategories(false));
	}
	
	public TreeViewer getViewer(){
		return viewer;
	}
}
