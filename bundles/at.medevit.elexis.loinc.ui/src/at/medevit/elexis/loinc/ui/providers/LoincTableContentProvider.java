package at.medevit.elexis.loinc.ui.providers;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;

import at.medevit.elexis.loinc.model.LoincCode;
import at.medevit.elexis.loinc.ui.LoincServiceComponent;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;

public class LoincTableContentProvider implements ICommonViewerContentProvider {

	List<LoincCode> elements;

	@Override
	public Object[] getElements(Object inputElement) {
		if (elements == null) {
			elements = LoincServiceComponent.getService().getAllCodes();
		}
		return elements.toArray();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
	}

	@Override
	public void changed(HashMap<String, String> values) {
		elements = LoincServiceComponent.getService().getAllCodes();
	}

	@Override
	public void reorder(String field) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
	}

	@Override
	public void stopListening() {
		// TODO Auto-generated method stub
	}

}
