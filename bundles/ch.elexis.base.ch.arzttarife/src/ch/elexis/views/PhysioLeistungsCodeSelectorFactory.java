/*******************************************************************************
 * Copyright (c) 2009, G. Weirich, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.swt.SWT;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.actions.PersistentObjectLoader;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.PhysioLeistung;
import ch.elexis.data.Query;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.TimeTool;

public class PhysioLeistungsCodeSelectorFactory extends CodeSelectorFactory {
	private Query<PhysioLeistung> qbe;
	private ViewerConfigurer vc;
	private UpdateDateEventListener updateListener;
	
	public PhysioLeistungsCodeSelectorFactory(){
		this.updateListener = new UpdateDateEventListener();
		ElexisEventDispatcher.getInstance().addListeners(updateListener);
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		FieldDescriptor<?>[] fd =
			new FieldDescriptor<?>[] {
				new FieldDescriptor<PhysioLeistung>("Ziffer", "Ziffer", null),
				new FieldDescriptor<PhysioLeistung>("Text", "Text", null),
			};
		qbe = new Query<PhysioLeistung>(PhysioLeistung.class);
		qbe.addPostQueryFilter(new IFilter() {
			
			private TimeTool validFrom = new TimeTool();
			private TimeTool validTo = new TimeTool();
			
			public boolean select(Object toTest){
				if (toTest instanceof PhysioLeistung) {
					PhysioLeistung physio = (PhysioLeistung) toTest;
					if(physio.getId().equals("VERSION")) {
						return false;
					}
					Konsultation selectedKons =
						(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
					if (selectedKons != null) {
						TimeTool validDate = new TimeTool(selectedKons.getDatum());
						validTo.set(physio.get(PhysioLeistung.FLD_BIS));
						if(validDate.isBefore(validTo)) {
							validFrom.set(physio.get(PhysioLeistung.FLD_VON));
							return validDate.isAfterOrEqual(validFrom);
						} else {
							return false;
						}
					} else {
						return true;
					}
				}
				return false;
			}
		});
		PersistentObjectLoader fdl = new FlatDataLoader(cv, qbe);
		SelectorPanelProvider slp = new SelectorPanelProvider(fd, true);
		vc =
			new ViewerConfigurer(fdl, new DefaultLabelProvider(), slp,
				new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, cv));
		return vc;
		
	}
	
	@Override
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getCodeSystemName(){
		return PhysioLeistung.CODESYSTEMNAME;
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return PhysioLeistung.class;
	}
	
	@Override
	public PersistentObject findElement(String code){
		String id = new Query<PhysioLeistung>(PhysioLeistung.class).findSingle("Ziffer", "=", code);
		return PhysioLeistung.load(id);
	}
	
	private class UpdateDateEventListener extends ElexisEventListenerImpl {
		public UpdateDateEventListener(){
			super(Konsultation.class, ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_DESELECTED);
		}
		
		@Override
		public void catchElexisEvent(ElexisEvent ev){
			if (vc != null && vc.getControlFieldProvider() != null) {
				vc.getControlFieldProvider().fireChangedEvent();
			}
		}
	}
}
