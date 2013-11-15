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

import org.eclipse.swt.SWT;

import ch.elexis.core.data.Query;
import ch.elexis.core.ui.actions.ReadOnceTreeLoader;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.TarmedLeistung;

public class TarmedCodeSelectorFactory extends CodeSelectorFactory {
	SelectorPanelProvider slp;
	ReadOnceTreeLoader tdl;
	CommonViewer cv;
	FieldDescriptor<?>[] fields = {
		new FieldDescriptor<TarmedLeistung>("Ziffer", TarmedLeistung.FLD_CODE, Typ.STRING, null),
		new FieldDescriptor<TarmedLeistung>("Text", TarmedLeistung.FLD_TEXT, null)
	};
	
	public TarmedCodeSelectorFactory(){
		
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		this.cv = cv;
		slp = new TarmedSelectorPanelProvider(cv, fields, true);
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
}
