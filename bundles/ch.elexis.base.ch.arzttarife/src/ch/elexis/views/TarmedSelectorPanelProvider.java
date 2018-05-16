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

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.TimeTool;

public class TarmedSelectorPanelProvider extends SelectorPanelProvider {
	private CommonViewer commonViewer;
	private StructuredViewer viewer;
	
	private TarmedLawFilter lawFilter = new TarmedLawFilter();
	private TarmedValidDateFilter validDateFilter = new TarmedValidDateFilter();
	private FilterKonsultationListener konsFilter =
		new FilterKonsultationListener(Konsultation.class);
	
	private Konsultation previousKons;
	private boolean dirty;
	
	public TarmedSelectorPanelProvider(CommonViewer cv,
		FieldDescriptor<? extends PersistentObject>[] fields, boolean bExlusive){
		super(fields, bExlusive);
		commonViewer = cv;
	}
	
	@Override
	public void setFocus(){
		super.setFocus();
		if (viewer == null) {
			Konsultation selectedKons =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			
			viewer = commonViewer.getViewerWidget();
			IContentProvider contentProvider = viewer.getContentProvider();
			if (contentProvider instanceof TarmedCodeSelectorContentProvider) {
				if (selectedKons != null) {
					updateLawFilter(selectedKons);
				}
				viewer.addFilter(lawFilter);
			}
			if (selectedKons != null) {
				updateValidFilter(selectedKons);
			}
			viewer.addFilter(validDateFilter);
			ElexisEventDispatcher.getInstance().addListeners(konsFilter);
		}
		refreshViewer();
	}
	
	private void refreshViewer() {
		if (viewer != null && dirty) {
			dirty = false;
			viewer.getControl().setRedraw(false);
			viewer.setSelection(new StructuredSelection());
			viewer.refresh();
			viewer.getControl().setRedraw(true);
		}
	}
	
	private void updateValidFilter(Konsultation kons){
		validDateFilter.setValidDate(new TimeTool(kons.getDatum()));
	}
	
	private void updateLawFilter(Konsultation kons){
		Fall fall = kons.getFall();
		String law = "";
		if (fall != null) {
			String konsLaw = fall.getConfiguredBillingSystemLaw().name();
			if (TarmedLeistung.isAvailableLaw(konsLaw)) {
				law = konsLaw;
			}
		}
		lawFilter.setLaw(law);
	}
	
	private void updateDirty(Konsultation kons){
		if (kons != previousKons) {
			dirty = true;
			previousKons = kons;
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
			if (selectedKons != null) {
				IContentProvider contentProvider = viewer.getContentProvider();
				if (contentProvider instanceof TarmedCodeSelectorContentProvider) {
					updateLawFilter(selectedKons);
				}
				updateValidFilter(selectedKons);
				updateDirty(selectedKons);
			} else {
				// clear filters
				IContentProvider contentProvider = viewer.getContentProvider();
				if (contentProvider instanceof TarmedCodeSelectorContentProvider) {
					lawFilter.setLaw(null);
				}
				validDateFilter.setValidDate(null);
				updateDirty(null);
			}
		}
	}
	
	public void toggleFilters(){
		validDateFilter.setDoFilter(!validDateFilter.getDoFilter());
		lawFilter.setDoFilter(!lawFilter.getDoFilter());
		dirty = true;
		refreshViewer();
	}
}
