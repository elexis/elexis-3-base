package ch.elexis.privatrechnung.views;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.privatrechnung.model.IPrivatLeistung;
import ch.elexis.privatrechnung.model.PrivatModelServiceHolder;

public class PrivatLeistungContentProvider
		implements ICommonViewerContentProvider, ITreeContentProvider {
	
	private CommonViewer commonViewer;
	
	private INamedQuery<IPrivatLeistung> childrenQuery;
	
	public PrivatLeistungContentProvider(CommonViewer commonViewer){
		this.commonViewer = commonViewer;
		
		this.childrenQuery =
			PrivatModelServiceHolder.get().getNamedQuery(IPrivatLeistung.class, "parent");
		
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		List<IPrivatLeistung> roots = childrenQuery.executeWithParameters(childrenQuery.getParameterMap("parent", "NIL"));
		return roots.toArray(new Object[roots.size()]);
	}
	
	@Override
	public void changed(HashMap<String, String> values){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reorder(String field){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void selected(){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void init(){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void startListening(){
		commonViewer.getConfigurer().getControlFieldProvider().addChangeListener(this);
	}
	
	@Override
	public void stopListening(){
		commonViewer.getConfigurer().getControlFieldProvider().removeChangeListener(this);
	}
	
	@Override
	public Object[] getChildren(Object parentElement){
		if (parentElement instanceof IPrivatLeistung) {
			IPrivatLeistung parentLeistung = (IPrivatLeistung) parentElement;
			return childrenQuery.executeWithParameters(
				childrenQuery.getParameterMap("parent", parentLeistung.getCode())).toArray();
		}
		return null;
	}
	
	@Override
	public Object getParent(Object element){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean hasChildren(Object parentElement){
		if (parentElement instanceof IPrivatLeistung) {
			IPrivatLeistung parentLeistung = (IPrivatLeistung) parentElement;
			return !childrenQuery.executeWithParameters(
				childrenQuery.getParameterMap("parent", parentLeistung.getCode())).isEmpty();
		}
		return false;
	}
}
