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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import jakarta.inject.Inject;

public class PhysioLeistungsCodeSelectorFactory extends CodeSelectorFactory {

	PhysioSelectorPanelProvider slp;
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
					IPhysioLeistung selected = (IPhysioLeistung) ss.getFirstElement();
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.views.codeselector.physio.selection", selected);
				} else {
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.views.codeselector.physio.selection", null);
				}
			}
		});

		slp = new PhysioSelectorPanelProvider(cv);

		slp.addActions(new ToggleFiltersAction());

		vc = new ViewerConfigurer(new PhysioContentProvider(cv, slp), new DefaultLabelProvider(), slp,
				new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, cv));
		return vc.setContentType(ContentType.GENERICOBJECT);

	}

	private class PhysioContentProvider extends CommonViewerContentProvider {

		private ControlFieldProvider controlFieldProvider;

		public PhysioContentProvider(CommonViewer commonViewer, ControlFieldProvider controlFieldProvider) {
			super(commonViewer);
			this.controlFieldProvider = controlFieldProvider;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			IQuery<?> query = getBaseQuery();

			// apply filters from control field provider
			controlFieldProvider.setQuery(query);
			applyQueryFilters(query);
			query.orderBy("ziffer", ORDER.ASC);
			List<?> elements = query.execute();

			return elements.toArray(new Object[elements.size()]);
		}

		@Override
		protected IQuery<?> getBaseQuery() {
			IQuery<IPhysioLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(IPhysioLeistung.class);
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
		return "Physiotherapie";
	}

	@Override
	public Class<?> getElementClass() {
		return IPhysioLeistung.class;
	}

	private class ToggleFiltersAction extends Action {

		public ToggleFiltersAction() {
			super(StringUtils.EMPTY, Action.AS_CHECK_BOX);
			// initial state, active filters
			setChecked(true);
		}

		@Override
		public String getToolTipText() {
			return "Kontext (Konsultation, Fall, etc.) Filter (de)aktivieren";
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return Images.IMG_FILTER.getImageDescriptor();
		}

		@Override
		public void run() {
			slp.toggleFilters();
		}
	}
}
