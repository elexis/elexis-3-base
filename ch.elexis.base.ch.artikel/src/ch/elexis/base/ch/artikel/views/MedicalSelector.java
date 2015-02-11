/*******************************************************************************
 * Copyright (c) 2006-2015, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    M. Descher - several changes
 *******************************************************************************/
package ch.elexis.base.ch.artikel.views;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import ch.elexis.artikel_ch.data.ArtikelFactory;
import ch.elexis.artikel_ch.data.Medical;
import ch.elexis.base.ch.artikel.model.MedicalLoader;
import ch.elexis.core.ui.actions.ToggleVerrechenbarFavoriteAction;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.artikel.ArtikelContextMenu;
import ch.elexis.core.ui.views.artikel.ArtikelLabelProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.Artikel;

public class MedicalSelector extends CodeSelectorFactory {
	
	private ToggleVerrechenbarFavoriteAction tvfa = new ToggleVerrechenbarFavoriteAction();
	private ISelectionChangedListener selChangeListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event){
			TableViewer tv = (TableViewer) event.getSource();
			StructuredSelection ss = (StructuredSelection) tv.getSelection();
			tvfa.updateSelection(ss.isEmpty() ? null : ss.getFirstElement());
		}
	};
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		ArtikelFactory af = new ArtikelFactory();
		Artikel artikelTemplate = (Artikel) af.createTemplate(Medical.class);
		ArtikelContextMenu artikelContextMenu =
			new ArtikelContextMenu((Medical) artikelTemplate, cv);
		artikelContextMenu.addAction(tvfa);
		cv.setSelectionChangedListener(selChangeListener);
		ViewerConfigurer vc =
			new ViewerConfigurer(
			// new LazyContentProvider(cv,dataloader,null),
				new MedicalLoader(cv), new ArtikelLabelProvider(), new MedicalControlFieldProvider(
					cv, new String[] {
						"Name"
					}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		
		return vc;
	}
	
	@Override
	public Class getElementClass(){
		return Medical.class;
	}
	
	@Override
	public void dispose(){
		// TODO Automatisch erstellter Methoden-Stub
		
	}
	
	@Override
	public String getCodeSystemName(){
		return "Medicals";
	}
	
}
