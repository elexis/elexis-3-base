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

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung;
import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class ComplementaryCodeSelectorFactory extends CodeSelectorFactory {

	public ComplementaryCodeSelectorFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv) {
		cv.setSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				TableViewer tv = (TableViewer) event.getSource();
				StructuredSelection ss = (StructuredSelection) tv.getSelection();
				if (!ss.isEmpty()) {
					IComplementaryLeistung selected = (IComplementaryLeistung) ss.getFirstElement();
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.views.codeselector.complementary.selection", selected);
				} else {
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.views.codeselector.complementary.selection", null);
				}
			}
		});
		FieldDescriptor<?>[] fieldDescriptors = new FieldDescriptor<?>[] {
				new FieldDescriptor<IComplementaryLeistung>("Code", "code", null),
				new FieldDescriptor<IComplementaryLeistung>("Text", "codeText", null), };
		SelectorPanelProvider slp = new SelectorPanelProvider(fieldDescriptors, true);
		ViewerConfigurer vc = new ViewerConfigurer(new ComplementaryContentProvider(cv, slp),
				new DefaultLabelProvider(), slp, new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, cv));
		return vc.setContentType(ContentType.GENERICOBJECT);
	}

	private class ComplementaryContentProvider extends CommonViewerContentProvider {

		private ControlFieldProvider controlFieldProvider;

		public ComplementaryContentProvider(CommonViewer commonViewer, ControlFieldProvider controlFieldProvider) {
			super(commonViewer);
			this.controlFieldProvider = controlFieldProvider;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			IQuery<?> query = getBaseQuery();

			java.util.Optional<IEncounter> encounter = ContextServiceHolder.get().getTyped(IEncounter.class);
			encounter.ifPresent(e -> {
				query.and("validFrom", COMPARATOR.LESS_OR_EQUAL, e.getDate());
				query.and("validTo", COMPARATOR.GREATER_OR_EQUAL, e.getDate());
			});

			// apply filters from control field provider
			controlFieldProvider.setQuery(query);
			applyQueryFilters(query);
			query.orderBy("code", ORDER.ASC);
			List<?> elements = query.execute();

			return elements.toArray(new Object[elements.size()]);
		}

		@Override
		protected IQuery<?> getBaseQuery() {
			IQuery<IComplementaryLeistung> query = ArzttarifeModelServiceHolder.get()
					.getQuery(IComplementaryLeistung.class);
			query.and("id", COMPARATOR.NOT_EQUALS, "VERSION");
			return query;
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCodeSystemName() {
		return "Komplementärmedizin";
	}

	@Override
	public Class<?> getElementClass() {
		return IComplementaryLeistung.class;
	}
}
