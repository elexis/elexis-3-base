package at.medevit.elexis.cobasmira.ui;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import at.medevit.elexis.cobasmira.model.CobasMiraMappingLabitem;

public class CobasMiraMappingContentProvider implements IStructuredContentProvider {
	
	@Override
	public Object[] getElements(Object inputElement){
		List<CobasMiraMappingLabitem> list = (List<CobasMiraMappingLabitem>) inputElement;
		return list.toArray();
	}
	
	@Override
	public void dispose(){}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){}
	
}
