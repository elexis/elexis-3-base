package waelti.statistics.queries.providers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A standard content provider for the queries if no special data should be represented. Each row
 * will be handled as data object in this content provider. E.g. If you want to have the patient as
 * the model represented, you need another content provider.
 */
public class QueryContentProvider implements IStructuredContentProvider {
	
	private List<Object[]> items;
	
	public QueryContentProvider(List<Object[]> items){
		this.items = items;
	}
	
	public Object[] getElements(Object inputElement){
		return this.items.toArray();
	}
	
	public void dispose(){}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){}
	
}
