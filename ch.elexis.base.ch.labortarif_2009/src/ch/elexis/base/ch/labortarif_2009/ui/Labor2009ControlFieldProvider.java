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

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldListener;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.TimeTool;

public class Labor2009ControlFieldProvider implements ControlFieldProvider {
	
	private CommonViewer commonViewer;
	private StructuredViewer viewer;
	
	private Text txtFilter;
	
	private Labor2009CodeTextValidFilter filter;
	private FilterKonsultationListener konsFilter;
	
	public Labor2009ControlFieldProvider(final CommonViewer viewer){
		commonViewer = viewer;
		konsFilter = new FilterKonsultationListener(Konsultation.class);
		filter = new Labor2009CodeTextValidFilter();
	}
	
	public Composite createControl(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new FormLayout());
		
		Label lblFilter = new Label(ret, SWT.NONE);
		lblFilter.setText("Filter: ");
		
		txtFilter = new Text(ret, SWT.BORDER | SWT.SEARCH);
		txtFilter.setText(""); //$NON-NLS-1$
		
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 5);
		lblFilter.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(lblFilter, 5);
		fd.right = new FormAttachment(100, -5);
		txtFilter.setLayoutData(fd);
		
		return ret;
	}
	
	public void addChangeListener(ControlFieldListener cl){
		// TODO Auto-generated method stub
		
	}
	
	public void removeChangeListener(ControlFieldListener cl){
		// TODO Auto-generated method stub
		
	}
	
	public String[] getValues(){
		// TODO Auto-generated method stub
		return null;
	}
	
	public void clearValues(){
		// TODO Auto-generated method stub
		
	}
	
	public boolean isEmpty(){
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setQuery(Query<? extends PersistentObject> q){
		// TODO Auto-generated method stub
		
	}
	
	public IFilter createFilter(){
		// TODO Auto-generated method stub
		return null;
	}
	
	public void fireChangedEvent(){
		// TODO Auto-generated method stub
		
	}
	
	public void fireSortEvent(String text){
		// TODO Auto-generated method stub
		
	}
	
	public void setFocus(){
		// apply filter to viewer on focus as the creation in common viewer is done
		// first filter then viewer -> viewer not ready on createControl.
		if (viewer == null) {
			viewer = commonViewer.getViewerWidget();
			viewer.addFilter(filter);
			ElexisEventDispatcher.getInstance().addListeners(konsFilter);
			txtFilter.addKeyListener(new FilterKeyListener(txtFilter, viewer));
			txtFilter.setFocus();
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
				filter.setValidDate(new TimeTool(selectedKons.getDatum()));
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			} else {
				filter.setValidDate(null);
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			}
		}
	}
	
	private class FilterKeyListener extends KeyAdapter {
		private Text text;
		private StructuredViewer viewer;
		
		FilterKeyListener(Text filterTxt, StructuredViewer viewer){
			text = filterTxt;
			this.viewer = viewer;
		}
		
		public void keyReleased(KeyEvent ke){
			String txt = text.getText();
			if (txt.length() > 1) {
				filter.setSearchText(txt);
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			} else {
				filter.setSearchText(null);
				viewer.getControl().setRedraw(false);
				viewer.refresh();
				viewer.getControl().setRedraw(true);
			}
		}
	}
}
