package at.medevit.elexis.inbox.ui.part.provider;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;

public class InboxElementContentProvider implements ITreeContentProvider {
	private ArrayList<PatientInboxElements> items;
	
	public Object[] getElements(Object inputElement){
		return items.toArray();
	}
	
	public Object[] getChildren(Object parentElement){
		if (parentElement instanceof PatientInboxElements) {
			return ((PatientInboxElements) parentElement).getElements().toArray();
		} else {
			return null;
		}
	}
	
	public boolean hasChildren(Object element){
		return (element instanceof PatientInboxElements);
	}
	
	public Object[] getParent(Object element){
		return null;
	}
	
	public void dispose(){
		// nothing to do
	}
	
	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		if (newInput instanceof ArrayList<?>) {
			items = (ArrayList<PatientInboxElements>) newInput;
		}
	}
}