/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.base.ch.labortarif_2009.ui;

import org.eclipse.swt.SWT;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.PersistentObject;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.rgw.tools.TimeTool;

public class Labor2009Selector extends CodeSelectorFactory {
	CommonViewer cv;
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		this.cv = cv;
		ViewerConfigurer vc =
			new ViewerConfigurer(new Labor2009ContentProvider(), new DefaultLabelProvider(),
				new Labor2009ControlFieldProvider(cv),
				new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		
		ElexisEventDispatcher.getInstance().addListeners(
			new UpdateEventListener(cv, Labor2009Tarif.class, ElexisEvent.EVENT_RELOAD));
		
		return vc;
	}
	
	@Override
	public void dispose(){
		cv.dispose();
	}
	
	@Override
	public String getCodeSystemName(){
		return Labor2009Tarif.CODESYSTEM_NAME;
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return Labor2009Tarif.class;
	}
	
	@Override
	public PersistentObject findElement(String code){
		return Labor2009Tarif.getFromCode(code, new TimeTool());
	}
	
	private class UpdateEventListener extends ElexisUiEventListenerImpl {
		
		CommonViewer viewer;
		
		UpdateEventListener(CommonViewer viewer, final Class<?> clazz, int mode){
			super(clazz, mode);
			this.viewer = viewer;
		}
		
		@Override
		public void runInUi(ElexisEvent ev){
			viewer.notify(CommonViewer.Message.update);
		}
	}
}
