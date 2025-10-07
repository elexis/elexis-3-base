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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.actions.ToggleVerrechenbarFavoriteAction;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class TardocCodeSelectorFactory extends CodeSelectorFactory {
	TardocSelectorPanelProvider slp;
	CommonViewer cv;
	int eventType = SWT.KeyDown;

	private ShowDetailsAction showDetailsAction = new ShowDetailsAction();
	private ToggleVerrechenbarFavoriteAction tvfa = new ToggleVerrechenbarFavoriteAction();
	private ISelectionChangedListener selChangeListener = new ISelectionChangedListener() {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			TreeViewer tv = (TreeViewer) event.getSource();
			StructuredSelection ss = (StructuredSelection) tv.getSelection();
			tvfa.updateSelection(ss.isEmpty() ? null : ss.getFirstElement());
			if (!ss.isEmpty()) {
				ITardocLeistung selected = (ITardocLeistung) ss.getFirstElement();
				ContextServiceHolder.get().getRootContext().setNamed("ch.elexis.views.codeselector.tardoc.selection",
						selected);
			} else {
				ContextServiceHolder.get().getRootContext().setNamed("ch.elexis.views.codeselector.tardoc.selection",
						null);
			}
		}
	};

	public TardocCodeSelectorFactory() {
	}

	@Override
	public ViewerConfigurer createViewerConfigurer(final CommonViewer cv) {
		this.cv = cv;
		cv.setSelectionChangedListener(selChangeListener);
		// add keyListener to search field
		Listener keyListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.type == eventType) {
					if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
						slp.fireChangedEvent();
					}
				}
			}
		};
		slp = new TardocSelectorPanelProvider(cv);

		slp.addActions(new ToggleFiltersAction());

		MenuManager menu = new MenuManager();
		menu.add(tvfa);
		menu.add(showDetailsAction);
		cv.setContextMenu(menu);

		ViewerConfigurer vc = new ViewerConfigurer(new TardocCodeSelectorContentProvider(cv),
				new DefaultLabelProvider(), slp, new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
		return vc.setContentType(ContentType.GENERICOBJECT);
	}

	@Override
	public Class getElementClass() {
		return ITardocLeistung.class;
	}

	@Override
	public void dispose() {
		cv.dispose();
	}

	@Override
	public String getCodeSystemName() {
		return "TARDOC"; //$NON-NLS-1$
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

	private class ShowDetailsAction extends Action {
		@Override
		public String getText() {
			return Messages.TarmedCodeSelectorFactoryDetailsButton;
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return Images.IMG_CLIPBOARD.getImageDescriptor();
		}

		@Override
		public void run() {
			Object[] selection = cv.getSelection();
			if (selection != null && selection.length > 0) {
				if (selection[0] instanceof ITarmedLeistung) {
					showDetailsFor((ITarmedLeistung) selection[0]);
				}
			}
		}

		private void showDetailsFor(ITarmedLeistung selectedObject) {
			Shell detailShell = new Shell(Display.getCurrent().getActiveShell(), SWT.SHELL_TRIM | SWT.MODELESS);
			detailShell.setLayout(new FillLayout());
			TarmedDetailDisplay detailDisplay = new TarmedDetailDisplay();
			detailDisplay.createDisplayFromDeteils(detailShell, null);
			detailDisplay.display(selectedObject);
			detailShell.setSize(400, 400);
			detailShell.setText(Messages.TarmedCodeSelectorFactoryDetailsTitel);
			detailShell.open();
		}
	}
}
