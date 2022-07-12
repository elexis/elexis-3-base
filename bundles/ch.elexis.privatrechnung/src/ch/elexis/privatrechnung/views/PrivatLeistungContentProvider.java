package ch.elexis.privatrechnung.views;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;

import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewer.Message;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.privatrechnung.model.IPrivatLeistung;
import ch.elexis.privatrechnung.model.PrivatModelServiceHolder;

public class PrivatLeistungContentProvider implements ICommonViewerContentProvider, ITreeContentProvider {

	private CommonViewer commonViewer;

	private INamedQuery<IPrivatLeistung> childrenQuery;

	private String nameFilter;

	private String codeFilter;

	public PrivatLeistungContentProvider(CommonViewer commonViewer) {
		this.commonViewer = commonViewer;

		this.childrenQuery = PrivatModelServiceHolder.get().getNamedQuery(IPrivatLeistung.class, "parent"); //$NON-NLS-1$

	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<IPrivatLeistung> roots = childrenQuery
				.executeWithParameters(childrenQuery.getParameterMap("parent", "NIL")); //$NON-NLS-1$ //$NON-NLS-2$
		return roots.toArray(new Object[roots.size()]);
	}

	@Override
	public void changed(HashMap<String, String> values) {
		nameFilter = values.get("name"); //$NON-NLS-1$
		codeFilter = values.get("shortName"); //$NON-NLS-1$
		commonViewer.notify(Message.update_keeplabels);
	}

	@Override
	public void reorder(String field) {

	}

	@Override
	public void selected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startListening() {
		commonViewer.getConfigurer().getControlFieldProvider().addChangeListener(this);
	}

	@Override
	public void stopListening() {
		commonViewer.getConfigurer().getControlFieldProvider().removeChangeListener(this);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IPrivatLeistung) {
			IPrivatLeistung parentLeistung = (IPrivatLeistung) parentElement;
			List<IPrivatLeistung> ret = childrenQuery
					.executeWithParameters(childrenQuery.getParameterMap("parent", parentLeistung.getCode())); //$NON-NLS-1$
			ret = getFiltered(ret);
			return ret.toArray(new Object[ret.size()]);
		}
		return null;
	}

	private List<IPrivatLeistung> getFiltered(List<IPrivatLeistung> ret) {
		if (StringUtils.isNotBlank(nameFilter)) {
			ret = ret.stream()
					.filter(pl -> hasChildren(pl) || pl.getText().toLowerCase().contains(nameFilter.toLowerCase()))
					.collect(Collectors.toList());
		}
		if (StringUtils.isNotBlank(codeFilter)) {
			ret = ret.stream()
					.filter(pl -> hasChildren(pl) || pl.getCode().toLowerCase().contains(codeFilter.toLowerCase()))
					.collect(Collectors.toList());
		}
		return ret;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof IPrivatLeistung) {
			IPrivatLeistung parentLeistung = (IPrivatLeistung) parentElement;
			List<IPrivatLeistung> ret = childrenQuery
					.executeWithParameters(childrenQuery.getParameterMap("parent", parentLeistung.getCode())); //$NON-NLS-1$
			ret = getFiltered(ret);
			return !ret.isEmpty();
		}
		return false;
	}
}
