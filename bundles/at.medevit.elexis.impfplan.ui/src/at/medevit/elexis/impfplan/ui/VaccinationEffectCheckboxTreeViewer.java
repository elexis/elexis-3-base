package at.medevit.elexis.impfplan.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel;
import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel.DiseaseDefinition;
import ch.elexis.core.constants.StringConstants;

public class VaccinationEffectCheckboxTreeViewer extends CheckboxTreeViewer implements ICheckStateProvider {

	private String initialCheckedElements = StringUtils.EMPTY;

	public VaccinationEffectCheckboxTreeViewer(Composite parent, int style, String initialCheckedElements) {
		super(parent, style);

		this.initialCheckedElements = (initialCheckedElements != null) ? initialCheckedElements : StringConstants.EMPTY;

		GridData gd_tree = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd_tree.heightHint = 200;
		getTree().setLayoutData(gd_tree);
		getTree().setHeaderVisible(false);
		getTree().setLinesVisible(true);

		TreeViewerColumn col = new TreeViewerColumn(this, SWT.NONE);
		col.getColumn().setWidth(225);

		setContentProvider(new DiseaseTreeContentProvider());
		setLabelProvider(new DiseaseTreeLabelProvider());
		setInput(DiseaseDefinitionModel.getDiseaseDefinitions());
		setCheckStateProvider(this);
	}

	public String getCheckedElementsAsCommaSeparatedString() {
		Object[] checkedElements = getCheckedElements();
		List<Object> list = Arrays.asList(checkedElements);
		Optional<String> ret = list.stream().map(o -> (DiseaseDefinition) o).map(o -> o.getATCCode())
				.reduce((u, t) -> u + StringConstants.COMMA + t);
		return ret.orElse(StringUtils.EMPTY);
	}

	private class DiseaseTreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return DiseaseDefinitionModel.getDiseaseDefinitions().toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return (DiseaseDefinition) element;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
	}

	private class DiseaseTreeLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			return ((DiseaseDefinition) element).getDiseaseLabel();
		}
	}

	@Override
	public boolean isChecked(Object element) {
		DiseaseDefinition dd = (DiseaseDefinition) element;
		return initialCheckedElements.contains(dd.getATCCode());
	}

	@Override
	public boolean isGrayed(Object element) {
		return false;
	}
}
