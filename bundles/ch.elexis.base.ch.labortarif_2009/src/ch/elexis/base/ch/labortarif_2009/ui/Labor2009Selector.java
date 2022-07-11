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

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.base.ch.labortarif.LaborTarifConstants;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.actions.AddVerrechenbarToLeistungsblockAction;
import ch.elexis.core.ui.actions.ToggleVerrechenbarFavoriteAction;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class Labor2009Selector extends CodeSelectorFactory {
	CommonViewer cv;

	private ToggleVerrechenbarFavoriteAction tvfa = new ToggleVerrechenbarFavoriteAction();
	private AddVerrechenbarToLeistungsblockAction atla = new AddVerrechenbarToLeistungsblockAction(
			"ch.elexis.base.ch.labortarif_2009.ui.selection"); //$NON-NLS-1$

	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv) {
		this.cv = cv;

		cv.setSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				TableViewer tv = (TableViewer) event.getSource();
				StructuredSelection ss = (StructuredSelection) tv.getSelection();
				tvfa.updateSelection(ss.isEmpty() ? null : ss.getFirstElement());
				if (!ss.isEmpty()) {
					ILaborLeistung selected = (ILaborLeistung) ss.getFirstElement();
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.base.ch.labortarif_2009.ui.selection", selected); //$NON-NLS-1$
				} else {
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.base.ch.labortarif_2009.ui.selection", null); //$NON-NLS-1$
				}
			}
		});

		MenuManager menu = new MenuManager();
		menu.add(atla);
		menu.add(tvfa);
		cv.setContextMenu(menu);

		Labor2009ControlFieldProvider controlFieldProvider = new Labor2009ControlFieldProvider(cv);
		Labor2009ContentProvider contentProvider = new Labor2009ContentProvider(cv, controlFieldProvider);
		ViewerConfigurer vc = new ViewerConfigurer(contentProvider, new DefaultLabelProvider(), controlFieldProvider,
				new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		return vc.setContentType(ContentType.GENERICOBJECT);
	}

	@Override
	public void dispose() {
		cv.dispose();
	}

	@Override
	public String getCodeSystemName() {
		return LaborTarifConstants.CODESYSTEM_NAME;
	}

	@Override
	public Class<?> getElementClass() {
		return ILaborLeistung.class;
	}
}
