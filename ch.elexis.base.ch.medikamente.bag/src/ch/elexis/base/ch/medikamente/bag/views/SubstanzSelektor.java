/*******************************************************************************
 * Copyright (c) 2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *****************************************************************************/

package ch.elexis.base.ch.medikamente.bag.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultContentProvider;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.medikamente.bag.data.Substance;

public class SubstanzSelektor extends Dialog {
	CommonViewer cv;
	Substance result;
	
	public SubstanzSelektor(Shell shell){
		super(shell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		cv = new CommonViewer();
		ViewerConfigurer vc =
			new ViewerConfigurer(new DefaultContentProvider(cv, Substance.class, new String[] {
				"name"
			}, false), new DefaultLabelProvider(), new DefaultControlFieldProvider(cv,
				new String[] {
					"name=Name"
				}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_LIST, SWT.V_SCROLL, null));
		cv.create(vc, parent, SWT.NONE, parent);
		vc.getContentProvider().startListening();
		return cv.getViewerWidget().getControl();
	}
	
	@Override
	public boolean close(){
		cv.getConfigurer().getContentProvider().stopListening();
		result = (Substance) cv.getSelection()[0];
		return super.close();
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText("Bitte Substanz auswählen");
	}
	
}
