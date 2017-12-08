package waelti.statistics.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.rgw.tools.Money;

/**
 * A ViewerSorter which can sort top down and bottom up depending on the setting of the reverse
 * boolean.
 */
public class ColumnSorter extends ViewerSorter {
	
	/** Reverse ordering */
	private boolean reverse = false;
	
	/** Index of the column which should be used to sort the results. */
	private int index;
	
	public ColumnSorter(int index){
		this.index = index;
	}
	
	// TODO number sorting. meta data?
	@Override
	public int compare(Viewer viewer, Object e1, Object e2){
		Object o1 = ((Object[]) e1)[index];
		Object o2 = ((Object[]) e2)[index];
		
		int result;
		if (o1.getClass() == Money.class && o2.getClass() == Money.class) {
			result = ((Money) o1).compareTo((Money) o2);
			
		} else if (o1.getClass().getSuperclass() == Number.class
			&& o2.getClass().getSuperclass() == Number.class) {
			Double d1 = ((Number) o1).doubleValue();
			Double d2 = ((Number) o2).doubleValue();
			System.out.println(o1.getClass());
			result = d1.compareTo(d2);
			
		} else {
			result = o1.toString().compareTo(o2.toString());
		}
		
		return (reverse ? result * (-1) : result); // invert result if reverse
	}
	
	/** reverse ordering */
	public void setReverse(boolean reverse){
		this.reverse = reverse;
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
}
