/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
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

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.elexis.core.ui.actions.ReadOnceTreeLoader;
import ch.elexis.core.ui.actions.ToggleVerrechenbarFavoriteAction;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.TarmedLeistung;

public class TarmedCodeSelectorFactory extends CodeSelectorFactory {
	SelectorPanelProvider slp;
	ReadOnceTreeLoader tdl;
	CommonViewer cv;
	FieldDescriptor<?>[] fields = {
		new FieldDescriptor<TarmedLeistung>("Ziffer", TarmedLeistung.FLD_CODE, Typ.STRING, null),
		new FieldDescriptor<TarmedLeistung>("Text", TarmedLeistung.FLD_TEXT, null)
	};
	int eventType = SWT.KeyDown;
	
	private ToggleVerrechenbarFavoriteAction tvfa = new ToggleVerrechenbarFavoriteAction();
	private ISelectionChangedListener selChangeListener = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event){
			TreeViewer tv = (TreeViewer) event.getSource();
			StructuredSelection ss = (StructuredSelection) tv.getSelection();
			System.out.println(ss.getFirstElement());
			if (ss.isEmpty()) {
				tvfa.updateSelection(null);
				return;
			}
			PersistentObject o = (PersistentObject) ss.getFirstElement();
			tvfa.updateSelection((o.isDragOK()) ? o : null);
		}
	};
	
	public TarmedCodeSelectorFactory(){}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(final CommonViewer cv){
		this.cv = cv;
		cv.setSelectionChangedListener(selChangeListener);
		// add keyListener to search field
		Listener keyListener = new Listener() {
			@Override
			public void handleEvent(Event event){
				if (event.type == eventType) {
					if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
						slp.fireChangedEvent();
					}
				}
			}
		};
		for (FieldDescriptor<?> fd : fields) {
			fd.setAssignedListener(eventType, keyListener);
		}
		slp = new TarmedSelectorPanelProvider(cv, fields, true);
		
		MenuManager menu = new MenuManager();
		menu.add(tvfa);
		cv.setContextMenu(menu);
		
		tdl =
			new ReadOnceTreeLoader(cv, new Query<TarmedLeistung>(TarmedLeistung.class), "Parent",
				"ID");
		ViewerConfigurer vc =
			new ViewerConfigurer(tdl, new DefaultLabelProvider(), slp,
				new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
		return vc;
	}
	
	@Override
	public Class getElementClass(){
		return TarmedLeistung.class;
	}
	
	@Override
	public void dispose(){
		cv.dispose();
		tdl.dispose();
		
	}
	
	@Override
	public String getCodeSystemName(){
		return "Tarmed"; //$NON-NLS-1$
	}
	
	@Override
	public PersistentObject findElement(String code){
		return (PersistentObject) TarmedLeistung.getFromCode(code);
	}
}
