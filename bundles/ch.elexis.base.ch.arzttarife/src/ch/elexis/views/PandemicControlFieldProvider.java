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
package ch.elexis.views;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldListener;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PandemieLeistung;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.TimeTool;

public class PandemicControlFieldProvider implements ControlFieldProvider {
	
	private CommonViewer commonViewer;
	private StructuredViewer viewer;
	
	private Text txtFilter;
	
	private String previousKonsTime;
	private String newKonsTime;
	private TimeTool konsTime = new TimeTool();
	
	private UpdateDateEventListener updateDateListener = new UpdateDateEventListener();
	private ValidDateViewerFilter filter;
	
	public PandemicControlFieldProvider(final CommonViewer viewer){
		commonViewer = viewer;
		filter = new ValidDateViewerFilter();
		ElexisEventDispatcher.getInstance().addListeners(updateDateListener);
	}
	
	private class ValidDateViewerFilter extends ViewerFilter {
		
		private TimeTool validDate;
		private String searchString;
		
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element){
			PandemieLeistung pandemic = (PandemieLeistung) element;
			if (pandemic.getId().equals("VERSION")) {
				return false;
			}
			String org = pandemic.get(PandemieLeistung.FLD_ORG);
			if (StringUtils.isNotBlank(org) && !org.toLowerCase().contains("arztpraxis")) {
				return false;
			}
			if (StringUtils.isNotBlank(searchString) && searchString.length() > 1) {
				if (!(pandemic.getText().contains(searchString)
					|| pandemic.getCode().contains(searchString))) {
					return false;
				}
			}
			if (validDate != null) {
				return pandemic.isValid(validDate);
			}
			return true;
		}
		
		public void setValidDate(TimeTool validDate){
			this.validDate = validDate;
		}
		
		public void setSearchText(String txt){
			this.searchString = txt;
		}
	}
	
	private class UpdateDateEventListener extends ElexisEventListenerImpl {
		public UpdateDateEventListener(){
			super(Konsultation.class, ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_DESELECTED);
		}
		
		@Override
		public void catchElexisEvent(ElexisEvent ev){
			Konsultation selectedKons =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			if (selectedKons != null) {
				newKonsTime = selectedKons.getDatum();
				konsTime.set(newKonsTime);
				filter.setValidDate(konsTime);
			} else {
				newKonsTime = null;
				filter.setValidDate(null);
			}
			if (needsRefresh()) {
				refreshViewer();
			}
		}
		
		private boolean needsRefresh(){
			boolean ret = true;
			if (previousKonsTime == null && newKonsTime == null) {
				ret = false;
			} else if (previousKonsTime != null && newKonsTime != null) {
				ret = !previousKonsTime.equals(newKonsTime);
			}
			previousKonsTime = newKonsTime;
			return ret;
		}
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
			Query<Labor2009Tarif> qbe = new Query<Labor2009Tarif>(Labor2009Tarif.class);
			qbe.add(Labor2009Tarif.FLD_ID, Query.NOT_EQUAL, "1");
			viewer.setInput(qbe.execute());
			viewer.addFilter(filter);
			txtFilter.addKeyListener(new FilterKeyListener(txtFilter));
			txtFilter.setFocus();
			// trigger initial filtering
			updateDateListener.catchElexisEvent(null);
		}
	}
	
	private void refreshViewer(){
		// update the view async
		if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run(){
					if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
						viewer.getControl().setRedraw(false);
						viewer.refresh();
						viewer.getControl().setRedraw(true);
					}
				}
			});
		}
	}

	private class FilterKeyListener extends KeyAdapter {
		private Text text;

		FilterKeyListener(Text filterTxt){
			text = filterTxt;
		}
		
		public void keyReleased(KeyEvent ke){
			String txt = text.getText();
			if (txt.length() > 1) {
				filter.setSearchText(txt);
			} else {
				filter.setSearchText(null);
			}
			refreshViewer();
		}
	}
}
