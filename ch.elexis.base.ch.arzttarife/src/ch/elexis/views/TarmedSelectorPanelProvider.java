/*******************************************************************************
 * Copyright (c) 2012 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     T. Huster - reworked for Tarmed version 1.08
 ******************************************************************************/
package ch.elexis.views;

import org.eclipse.jface.viewers.StructuredViewer;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.TimeTool;

public class TarmedSelectorPanelProvider extends SelectorPanelProvider {
	private CommonViewer commonViewer;
	private StructuredViewer viewer;
	
	private TarmedValidDateFilter validDateFilter = new TarmedValidDateFilter();
	private FilterKonsultationListener konsFilter = new FilterKonsultationListener(
		Konsultation.class);
	
	public TarmedSelectorPanelProvider(CommonViewer cv,
		FieldDescriptor<? extends PersistentObject>[] fields, boolean bExlusive){
		super(fields, bExlusive);
		commonViewer = cv;
	}
	
	@Override
	public void setFocus(){
		super.setFocus();
		if (viewer == null) {
			viewer = commonViewer.getViewerWidget();
			viewer.addFilter(validDateFilter);
			ElexisEventDispatcher.getInstance().addListeners(konsFilter);
			// call with null, event is not used in listener impl.
			konsFilter.catchElexisEvent(null);
		}
	}
	
	private class FilterKonsultationListener extends ElexisUiEventListenerImpl {
		
		public FilterKonsultationListener(Class<?> clazz){
			super(clazz);
		}
		
		@Override
		public void runInUi(ElexisEvent ev){
			Konsultation selectedKons =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			// apply the filter
			if (selectedKons != null) {
				validDateFilter.setValidDate(new TimeTool(selectedKons.getDatum()));
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			} else {
				validDateFilter.setValidDate(null);
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			}
		}
	}
}
