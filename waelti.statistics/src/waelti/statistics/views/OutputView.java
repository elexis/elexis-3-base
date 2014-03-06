package waelti.statistics.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import waelti.statistics.actions.ExportAction;
import waelti.statistics.actions.NewQueryAction;
import waelti.statistics.actions.RefreshQueryAction;
import waelti.statistics.queries.AbstractQuery;
import ch.elexis.core.ui.UiDesk;

/**
 * This class contains all methods needed to display the output created by any query.
 * 
 * @author Michael Waelti
 */

public class OutputView extends ViewPart {
	
	private Composite parent;
	
	private OptionPanel queryOptions;
	
	private AbstractQuery query;
	
	/**
	 * Header block containing all information displayed at the head of the output. E.g. creating
	 * date, queries name.
	 */
	private Label header;
	
	/** A composite containing the result */
	private ResultTable resultView;
	
	/** this action starts the query and redraws the view. */
	private NewQueryAction newQueryAction;
	
	private RefreshQueryAction refreshQueryAction;
	
	private ExportAction exportAction;
	
	/**
	 * The constructor.
	 */
	public OutputView(){}
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent){
		this.parent = parent;
		this.parent.setLayout(new GridLayout());
		this.parent.setBackground(UiDesk.getColor(UiDesk.COL_WHITE));
		
		this.makeAction();
		this.contributeAction();
		
		// header
		initHeader("Keine Auswertung ausgewählt.");
	}
	
	private void initHeader(String labelText){
		header = new Label(this.parent, SWT.WRAP);
		header.setText(labelText);
		header.setFont(new Font(UiDesk.getDisplay(), "Helvetica", 11, SWT.BOLD));
		header.setBackground(UiDesk.getColor(UiDesk.COL_WHITE));
		
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		header.setLayoutData(data);
	}
	
	private void makeAction(){
		this.newQueryAction = new NewQueryAction(this);
		
		this.refreshQueryAction = new RefreshQueryAction(this);
		this.refreshQueryAction.setEnabled(false); // not yet a query selected
		
		this.exportAction = new ExportAction(this);
		this.exportAction.setEnabled(false); // not yet a query selected.
	}
	
	private void contributeAction(){
		IActionBars bars = this.getViewSite().getActionBars();
		this.fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalToolBar(IToolBarManager manager){
		manager.add(this.exportAction);
		manager.add(this.refreshQueryAction);
		manager.add(this.newQueryAction);
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus(){
		// viewer.getControl().setFocus();
	}
	
	/**
	 * Sets the header to be displayed for the result composite. E.g. contains information like the
	 * date of the query, the query's name.
	 */
	public void setHeader(String headerText){
		this.header.setText(headerText);
		header.setFont(new Font(UiDesk.getDisplay(), "Helvetica", 11, SWT.BOLD));
		this.parent.layout();
	}
	
	/**
	 * Enable or disable all buttons in the menu bar.
	 */
	public void setButtonsEnabled(boolean enabled){
		this.newQueryAction.setEnabled(enabled);
		this.refreshQueryAction.setEnabled(enabled);
		this.exportAction.setEnabled(enabled);
	}
	
	public NewQueryAction getNewQueryAction(){
		return newQueryAction;
	}
	
	public RefreshQueryAction getRefreshQueryAction(){
		return refreshQueryAction;
	}
	
	public ResultTable getResultView(){
		return resultView;
	}
	
	public void setResultView(ResultTable resultView){
		if (this.resultView != null) {
			this.resultView.dispose();
		}
		this.resultView = resultView;
	}
	
	public Composite getParent(){
		return parent;
	}
	
	public AbstractQuery getQuery(){
		return query;
	}
	
	public void setQuery(AbstractQuery query){
		this.query = query;
	}
	
	public OptionPanel getQueryOptions(){
		return queryOptions;
	}
	
	public void setQueryOptions(OptionPanel queryOptions){
		if (this.queryOptions != null) {
			this.queryOptions.dispose();
		}
		this.queryOptions = queryOptions;
	}
}