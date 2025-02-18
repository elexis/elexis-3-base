/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import jakarta.inject.Inject;

public class TarmedAllowanceCodeSelectorFactory extends CodeSelectorFactory {

	private ViewerConfigurer vc;

	@Inject
	public void selectedEncounter(@Optional IEncounter encounter) {
		if (vc != null && vc.getControlFieldProvider() != null) {
			vc.getControlFieldProvider().fireChangedEvent();
		}
	}

	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv) {
		cv.setSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				TableViewer tv = (TableViewer) event.getSource();
				StructuredSelection ss = (StructuredSelection) tv.getSelection();
				if (!ss.isEmpty()) {
					ITarmedAllowance selected = (ITarmedAllowance) ss.getFirstElement();
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.views.codeselector.tarmedallowance.selection", selected);
				} else {
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.views.codeselector.tarmedallowance.selection", null);
				}
			}
		});
		FieldDescriptor<?>[] fd = new FieldDescriptor<?>[] {
				new FieldDescriptor<ITarmedAllowance>("Ziffer", "code", null),
				new FieldDescriptor<ITarmedAllowance>("Titel", "text", null), };
		SelectorPanelProvider slp = new SelectorPanelProvider(fd, true);
		vc = new ViewerConfigurer(new TarmedAllowanceContentProvider(cv, slp), new DefaultLabelProvider(), slp,
				new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, cv));
		return vc.setContentType(ContentType.GENERICOBJECT);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCodeSystemName() {
		return "Tarmedpauschalen";
	}

	@Override
	public Class<?> getElementClass() {
		return ITarmedAllowance.class;
	}
}
