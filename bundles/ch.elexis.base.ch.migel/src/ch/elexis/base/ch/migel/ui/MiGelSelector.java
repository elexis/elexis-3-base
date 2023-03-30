/*******************************************************************************
 * Copyright (c) 2006-2017, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    M. Descher - several changes
 *    T. Huster - copied from ch.elexis.base.ch.artikel
 *
 *******************************************************************************/

package ch.elexis.base.ch.migel.ui;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import ch.elexis.artikel_ch.data.service.MiGelCodeElementService;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.ui.actions.ToggleVerrechenbarFavoriteAction;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class MiGelSelector extends CodeSelectorFactory {

	private ToggleVerrechenbarFavoriteAction tvfa = new ToggleVerrechenbarFavoriteAction();
	private ISelectionChangedListener selChangeListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			TableViewer tv = (TableViewer) event.getSource();
			StructuredSelection ss = (StructuredSelection) tv.getSelection();
			tvfa.updateSelection(ss.isEmpty() ? null : ss.getFirstElement());

			if (!ss.isEmpty()) {
				ContextServiceHolder.get().getRootContext().setNamed("ch.elexis.base.ch.migel.ui.selection", //$NON-NLS-1$
						(IArticle) ss.getFirstElement());
			} else {
				ContextServiceHolder.get().getRootContext().setNamed("ch.elexis.base.ch.migel.ui.selection", null); //$NON-NLS-1$
			}
		}
	};

	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv) {
		MenuManager menu = new MenuManager();
		menu.add(tvfa);

		cv.setNamedSelection("ch.elexis.base.ch.migel.ui.selection"); //$NON-NLS-1$
		cv.setContextMenu(menu);
		cv.setSelectionChangedListener(selChangeListener);

		DefaultControlFieldProvider controlFieldProvider = new DefaultControlFieldProvider(cv,
				new String[] { "SubID=Code", "Name" //$NON-NLS-1$ //$NON-NLS-2$
				});

		ViewerConfigurer vc = new ViewerConfigurer(new MiGelContentProvider(cv, controlFieldProvider),
				new LabelProvider() {
					@Override
					public String getText(Object element) {
						if (element instanceof IArticle) {
							return ((IArticle) element).getCode() + StringUtils.SPACE + ((IArticle) element).getName();
						}
						return super.getText(element);
					}
				}, controlFieldProvider, new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		vc.setContentType(ContentType.GENERICOBJECT);
		return vc;
	}

	@Override
	public Class getElementClass() {
		return IArticle.class;
	}

	@Override
	public void dispose() {
	}

	@Override
	public String getCodeSystemName() {
		return MiGelCodeElementService.MIGEL_NAME;
	}

	private class MiGelContentProvider extends CommonViewerContentProvider {

		private ControlFieldProvider controlFieldProvider;

		public MiGelContentProvider(CommonViewer commonViewer, ControlFieldProvider controlFieldProvider) {
			super(commonViewer);
			this.controlFieldProvider = controlFieldProvider;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			IQuery<?> query = getBaseQuery();

			// apply filters from control field provider
			controlFieldProvider.setQuery(query);
			applyQueryFilters(query);
			query.orderBy("SubID", ORDER.ASC); //$NON-NLS-1$
			List<?> elements = query.execute();

			return elements.toArray(new Object[elements.size()]);
		}

		@Override
		protected IQuery<?> getBaseQuery() {
			IQuery<IArticle> query = CoreModelServiceHolder.get().getQuery(IArticle.class);
			query.and(ModelPackage.Literals.IARTICLE__TYP, COMPARATOR.EQUALS, ArticleTyp.MIGEL);
			return query;
		}
	}
}
