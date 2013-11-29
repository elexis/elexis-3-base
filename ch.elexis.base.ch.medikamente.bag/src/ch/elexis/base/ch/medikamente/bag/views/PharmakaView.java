/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.base.ch.medikamente.bag.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultContentProvider;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.medikamente.bag.data.Substance;

public class PharmakaView extends ViewPart {
	FormToolkit tk = UiDesk.getToolkit();
	Form form;
	CommonViewer cv;
	
	public PharmakaView(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(final Composite parent){
		form = tk.createForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new GridLayout(2, true));
		cv = new CommonViewer();
		ViewerConfigurer vc =
			new ViewerConfigurer(new DefaultContentProvider(cv, Substance.class, new String[] {
				"name"
			}, false), new DefaultLabelProvider(), new DefaultControlFieldProvider(cv,
				new String[] {
					"name=Name"
				}), new ViewerConfigurer.DefaultButtonProvider(cv, Substance.class),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LIST, SWT.V_SCROLL, cv));
		cv.create(vc, body, SWT.NONE, getViewSite());
		vc.getContentProvider().startListening();
		Composite right = new Composite(body, SWT.NONE);
		right.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		right.setLayout(new GridLayout());
		tk.createLabel(right, "Interaktionen mit...");
		SWTHelper.createText(tk, right, 5, SWT.BORDER);
		
	}
	
	@Override
	public void dispose(){
		cv.getConfigurer().getContentProvider().stopListening();
		super.dispose();
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
}
