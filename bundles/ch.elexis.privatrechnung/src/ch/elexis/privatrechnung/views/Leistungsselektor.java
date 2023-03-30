/*******************************************************************************
 * Copyright (c) 2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.privatrechnung.views;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.ui.actions.ToggleVerrechenbarFavoriteAction;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.privatrechnung.model.IPrivatLeistung;

/**
 * This is the Composite that lets the user select codes and drag them into the
 * billing-field. It will be lined up next to the CodeSelectorFactories of all
 * other Billing-Plugins
 *
 * @author Gerry
 *
 */
public class Leistungsselektor extends CodeSelectorFactory {

	private CommonViewer cv;

	private ToggleVerrechenbarFavoriteAction tvfa = new ToggleVerrechenbarFavoriteAction();
	private ISelectionChangedListener selChangeListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			TreeViewer tv = (TreeViewer) event.getSource();
			StructuredSelection ss = (StructuredSelection) tv.getSelection();
			tvfa.updateSelection(ss.isEmpty() ? null : ss.getFirstElement());

			if (!ss.isEmpty()) {
				IPrivatLeistung ea = (IPrivatLeistung) ss.getFirstElement();
				ContextServiceHolder.get().getRootContext().setNamed("ch.elexis.privatrechnung.views.selection", ea); //$NON-NLS-1$
			} else {
				ContextServiceHolder.get().getRootContext().setNamed("ch.elexis.privatrechnung.views.selection", null); //$NON-NLS-1$
			}
		}
	};

	public Leistungsselektor() {
	}

	/**
	 * Here we create the populator for the CodeSelector. We must provide a viewer
	 * widget, a content provider, a label provider, a ControlFieldProvider and a
	 * ButtonProvider Again, we simply use existing classes to keep things easy.
	 */
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv) {
		this.cv = cv;
		ViewerConfigurer vc = new ViewerConfigurer(new PrivatLeistungContentProvider(cv), new DefaultLabelProvider(),
				new PrivatSelectorPanelProvider(cv), // $NON-NLS-1$
				new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));

		cv.setNamedSelection("ch.elexis.privatrechnung.views.selection"); //$NON-NLS-1$
		cv.setSelectionChangedListener(selChangeListener);

		return vc.setContentType(ContentType.GENERICOBJECT);
	}

	@Override
	public void dispose() {
		cv.dispose();
	}

	@Override
	public String getCodeSystemName() {
		return "Privat";
	}

	@Override
	public Class<?> getElementClass() {
		return IPrivatLeistung.class;
	}

	private static class PrivatSelectorPanelProvider extends SelectorPanelProvider {

		private static FieldDescriptor<?>[] fields = {
				new FieldDescriptor<IPrivatLeistung>("Kuerzel", "shortName", Typ.STRING, null), //$NON-NLS-2$
				new FieldDescriptor<IPrivatLeistung>("Name", "name", Typ.STRING, null) }; //$NON-NLS-2$

		private CommonViewer commonViewer;

		public PrivatSelectorPanelProvider(CommonViewer viewer) {
			super(fields, true);
			commonViewer = viewer;
			CoreUiUtil.injectServicesWithContext(this);
		}
	}
}
