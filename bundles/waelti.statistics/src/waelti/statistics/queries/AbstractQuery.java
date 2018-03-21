package waelti.statistics.queries;

import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;

import waelti.statistics.queries.annotations.GetProperty;
import waelti.statistics.queries.annotations.SetProperty;
import ch.elexis.core.ui.actions.BackgroundJob;

/**
 * This abstract class represents all queries. A query returns a matrix containing all information
 * which are computed. The Headings are used to identify the information of each column.<br>
 * The view for each query is constructed by a meta model which checks all classes for getter/setter
 * methods annotated with the SetProperty and GetProperty annotations. For each getter/setter pair
 * there is one text field displayed. All parameters and return values of the annotated methods have
 * to be strings. These methods may throw a SetDataException if the setter parameter received from
 * the text field is wrongly formatted. The error message of the exception will be displayed to the
 * user. For a detailed example see Consultations.
 * 
 * @author michael waelti
 * @see SetProperty
 * @see GetProperty
 * @see Consultations
 */
public abstract class AbstractQuery extends BackgroundJob {
	
	/**
	 * The internal result list of this object. This list has to be converted before being used in
	 * any table.
	 */
	private List<Object[]> list;
	
	private ITableLabelProvider labelProvider;
	
	private IStructuredContentProvider contentProvider;
	
	/** standard constructor */
	protected AbstractQuery(String name){
		super(name);
	}
	
	/** Returns the list containing of all columns header, e.g. for a table. */
	public abstract List<String> getTableHeadings();
	
	/**
	 * Returns a string which describes the actual query with all its settings. Used as a header
	 * before the result table.
	 */
	public String getHeader(){
		String header = "Auswertungsname: ";
		header += this.getTitle() + "\n";
		header += "Auswertungsdatum: " + QueryUtil.convertFromCalendar(Calendar.getInstance());
		
		return header;
	}
	
	/**
	 * getName() used by BackgroundJob. Returns the name of this query. Used by the metamodel.
	 */
	public abstract String getTitle();
	
	/**
	 * a short description to be displayed in the query dialog by the metamodel.
	 */
	public abstract String getDescription();
	
	/**
	 * This method is used to actually execute the query. Inherited from BackgroundJob.
	 */
	@Override
	public abstract IStatus execute(IProgressMonitor monitor);
	
	/**
	 * Returns a content provider containing the model which is represented by this query.
	 */
	public IStructuredContentProvider getContentProvider(){
		return this.contentProvider;
	}
	
	/** Returns the query's output as a label provider */
	public ITableLabelProvider getLabelProvider(){
		return this.labelProvider;
	}
	
	/** Returns a result matrix object. */
	public ResultMatrix getMatrix(){
		return new ResultMatrix(this.list, this.getTableHeadings());
	}
	
	protected void setList(List<Object[]> list){
		this.list = list;
	}
	
	protected List<Object[]> getList(){
		return list;
	}
	
	protected void setLabelProvider(ITableLabelProvider labelProvider){
		this.labelProvider = labelProvider;
	}
	
	protected void setContentProvider(IStructuredContentProvider contentProvider){
		this.contentProvider = contentProvider;
	}
}
