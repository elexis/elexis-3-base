/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.base.ch.diagnosecodes.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IDiagnosisTree;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.holder.CodeElementServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class TICodeSelectorFactory extends CodeSelectorFactory {

	private CommonViewer commonViewer;

	public TICodeSelectorFactory() {
		CoreUiUtil.injectServices(this);
	}

	@Inject
	void selectedEncounter(@Optional IEncounter encounter) {
		if (commonViewer != null && commonViewer.getViewerWidget() != null) {
			commonViewer.getViewerWidget().refresh();
		}
	}

	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv) {
		commonViewer = cv;
		ViewerConfigurer vc = new ViewerConfigurer(new TICodeContentProvider(cv), new TICodeLabelProvider(),
				new DefaultControlFieldProvider(cv, new String[] { "Text" }), //$NON-NLS-1$
				new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));

		cv.setNamedSelection("ch.elexis.base.ch.diagnosecodes.ti.selection"); //$NON-NLS-1$
		vc.setContentType(ContentType.GENERICOBJECT);
		return vc;
	}

	static class TICodeContentProvider implements ITreeContentProvider, ICommonViewerContentProvider {
		private CommonViewer viewer;
		private String value;
		private String TICKey = "Text";

		private ICodeElementServiceContribution tiCodeElementContribution;

		public TICodeContentProvider(CommonViewer viewer) {
			this.viewer = viewer;
			tiCodeElementContribution = CodeElementServiceHolder.get()
					.getContribution(CodeElementTyp.DIAGNOSE, "TI-Code") //$NON-NLS-1$
					.orElseThrow(() -> new IllegalStateException("No TI CodeElementContribution available")); //$NON-NLS-1$

			value = StringUtils.EMPTY;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IDiagnosisTree) {
				return ((IDiagnosisTree) parentElement).getChildren().toArray();
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			IDiagnosisTree c = (IDiagnosisTree) element;
			return c.getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			IDiagnosisTree c = (IDiagnosisTree) element;
			if (c.getChildren() == null) {
				return false;
			}
			return !c.getChildren().isEmpty();
		}

		@Override
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			Map<Object, Object> context = CodeElementServiceHolder.createContext();
			context.put(ICodeElementService.ContextKeys.TREE_ROOTS, Boolean.TRUE);
			List<ICodeElement> roots = tiCodeElementContribution.getElements(context);

			// get all children if no search value is set
			if (value == null || value.isEmpty()) {
				return roots.toArray();
			}

			List<IDiagnosisTree> foundSubs = ((Collection<? extends IDiagnosisTree>) tiCodeElementContribution
					.getElements(context)).stream().map(ce -> (IDiagnosisTree) ce)
							.filter(dt -> matchFilter(dt)).collect(Collectors.toList());
			List<IDiagnosisTree> foundRoots = ((Collection<? extends IDiagnosisTree>) roots).stream()
					.map(ce -> (IDiagnosisTree) ce).filter(dt -> matchFilter(dt)).collect(Collectors.toList());
			List<IDiagnosisTree> foundElements = new ArrayList<>(foundRoots);
			foundElements.addAll(foundSubs);

			return foundElements.toArray(new Object[foundElements.size()]);
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void startListening() {
			viewer.getConfigurer().getControlFieldProvider().addChangeListener(this);
		}

		@Override
		public void stopListening() {
			viewer.getConfigurer().getControlFieldProvider().removeChangeListener(this);
		}

		@Override
		public void changed(HashMap<String, String> values) {
			String filterText = values.get(TICKey).toLowerCase();
			if (filterText == null || filterText.isEmpty() || filterText.equals("%")) { //$NON-NLS-1$
				setFilterValue(StringUtils.EMPTY);
			} else {
				setFilterValue(filterText);
			}
			// update view
			viewer.notify(CommonViewer.Message.update);
		}

		public boolean matchFilter(IDiagnosisTree element) {
			if (StringUtils.isNotBlank(value)) {
				return (element.getCode() + StringUtils.SPACE + element.getText().toLowerCase())
						.contains(value.toLowerCase());
			}
			return true;
		}

		@Override
		public void reorder(String field) {
		}

		@Override
		public void selected() {
		}

		@Override
		public void init() {
			// TODO Auto-generated method stub

		}

		private void setFilterValue(String value) {
			this.value = value;
		}
	}

	static class TICodeLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			IDiagnosisTree c = (IDiagnosisTree) element;
			return c.getCode() + StringUtils.SPACE + c.getText();
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}

	}

	@Override
	public Class getElementClass() {
		return IDiagnosisTree.class;
	}

	@Override
	public void dispose() {
	}

	@Override
	public String getCodeSystemName() {
		return "TI-Code"; //$NON-NLS-1$
	}
}
