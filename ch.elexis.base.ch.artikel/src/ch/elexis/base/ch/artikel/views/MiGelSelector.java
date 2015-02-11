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
import ch.elexis.artikel_ch.data.MiGelArtikel;
import ch.elexis.base.ch.artikel.model.MigelLoader;
import ch.elexis.core.ui.actions.ToggleVerrechenbarFavoriteAction;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.artikel.ArtikelContextMenu;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class MiGelSelector extends CodeSelectorFactory {
	
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
		ArtikelContextMenu artikelContextMenu =
			new ArtikelContextMenu(
				(MiGelArtikel) new ArtikelFactory().createTemplate(MiGelArtikel.class), cv);
		artikelContextMenu.addAction(tvfa);
		cv.setSelectionChangedListener(selChangeListener);
		
		return new ViewerConfigurer(new MigelLoader(cv), new DefaultLabelProvider(),
			new DefaultControlFieldProvider(cv, new String[] {
				"SubID=Code", "Name" //$NON-NLS-1$ //$NON-NLS-2$
			}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
	}
	
	@Override
	public Class getElementClass(){
		return MiGelArtikel.class;
	}
	
	@Override
	public void dispose(){}
	
	@Override
	public String getCodeSystemName(){
		return MiGelArtikel.MIGEL_NAME;
	}
	
}
