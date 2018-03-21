package waelti.statistics.actions;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import waelti.statistics.views.ColumnSorter;

/**
 * Sorts the columns of a TableViewer in a simple manner. It does not consider any sorting done
 * before.
 * 
 * @author michael waelti
 */
public class ColumnSorterAdapter extends SelectionAdapter {
	
	private TableViewer viewer;
	private int index;
	private boolean reverse = false;
	
	public ColumnSorterAdapter(TableViewer viewer, int index){
		this.viewer = viewer;
		this.index = index;
	}
	
	public void widgetSelected(SelectionEvent event){
		// debugPopUp(event);
		
		ColumnSorter sorter = (ColumnSorter) this.viewer.getSorter();
		
		if (sorter == null) {
			sorter = new ColumnSorter(0);
			viewer.setSorter(sorter);
		}
		
		if (sorter.getIndex() == this.index) {
			reverse = reverse ? false : true; // flip flop.
			sorter.setReverse(reverse);
		} else { // start with top down sorting.
			sorter.setIndex(this.index);
			reverse = false;
			sorter.setReverse(false);
		}
		
		this.viewer.refresh(); // sort
	}
}
