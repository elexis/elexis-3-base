package waelti.statistics.queries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A result matrix represents the results of a query. A normal query will compute a two-dimensional
 * matrix. This class wraps a list so that all data points can be accessed by its coordinates x and
 * y. Where x is the horizontal and y the vertical dimension. x therefore denotes the position of a
 * column whereas y denotes the position of a row. The position computed from the upper left corner
 * starting with (0,0), y denoting the position downwards and x the position to the left.
 */
public class ResultMatrix implements Iterable<Object[]> {
	
	private List<Object[]> list = new ArrayList<Object[]>();
	
	/** Description of the columns */
	private List<String> headings;
	
	/** the width of the matrix. Or: How many columns exist? */
	private int width;
	
	public ResultMatrix(int width){
		this.width = width;
		this.headings = new ArrayList<String>();
		this.list = new ArrayList<Object[]>();
	}
	
	public ResultMatrix(List<Object[]> list, List<String> headings){
		this.list = list;
		this.headings = headings;
		this.width = this.list.get(0).length;
	}
	
	public void set(int x, int y, Object value){
		this.list.get(y)[x] = value;
	}
	
	public Object get(int x, int y){
		return this.list.get(y)[x];
	}
	
	public Object[] getRow(int y){
		return this.list.get(y);
	}
	
	public void setRow(int y, Object[] obj){
		this.list.set(y, obj);
	}
	
	public Object[] getColumn(int x){
		List<Object> cols = new ArrayList<Object>(this.list.size());
		
		for (Object[] objects : this.list) {
			cols.add(objects[x]);
		}
		
		return cols.toArray();
	}
	
	public void addRow(){
		this.addRow(new Object[width]);
	}
	
	public void addRow(Object[] obj){
		this.list.add(obj);
		
	}
	
	/** iterate over the rows. */
	public Iterator<Object[]> iterator(){
		return this.list.iterator();
	}
	
	public List<String> getHeadings(){
		return headings;
	}
	
	public void setHeadings(List<String> headings){
		this.headings = headings;
	}
}
