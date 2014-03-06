package waelti.statistics.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import waelti.statistics.queries.AbstractQuery;
import waelti.statistics.views.OptionPanel;
import waelti.statistics.views.OutputView;
import waelti.statistics.views.QueryInputDialog;
import waelti.statistics.views.ResultTable;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.BackgroundJob;
import ch.elexis.core.ui.actions.BackgroundJob.BackgroundJobListener;
import ch.elexis.core.ui.util.SWTHelper;

/**
 * This action is responsible for the whole procedure of creating a new query: getting all
 * information needed of the user, starting the query in the background and updating the view in the
 * end.
 * 
 * @author michael waelti
 */
public class NewQueryAction extends Action implements BackgroundJobListener {
	
	private OutputView view;
	
	private AbstractQuery configuredQuery;
	
	/** constructor */
	public NewQueryAction(){
		super();
		this.setText("neue Auswertung");
		this.setToolTipText("Startet eine neue Auswertung.");
		this.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("Waelti.Statistics",
			"icons/database_go.png"));
	}
	
	/** Standard constructor which should be used normally. */
	public NewQueryAction(OutputView view){
		this();
		this.view = view;
	}
	
	@Override
	public void run(){
		// cannot start new query while another is still running.
		this.getView().setButtonsEnabled(false);
		
		this.getInput();
		
		if (this.configuredQuery == null) { // user aborted
			this.getView().setButtonsEnabled(true);
		} else { // user did not cancel
			this.configuredQuery.addListener(this);
			this.view.setQuery(this.configuredQuery);
			
			this.createQueryOption();
			this.createContentAndTable();
			this.view.setHeader(this.configuredQuery.getTitle());
		}
	}
	
	private void createQueryOption(){
		OptionPanel panel =
			new OptionPanel(this.view.getParent(), UiDesk.getColor(UiDesk.COL_WHITE));
		panel.updateContent(this.configuredQuery);
		panel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		this.view.setQueryOptions(panel);
	}
	
	private void createContentAndTable(){
		
		if (this.getView().getResultView() != null) {
			this.getView().getResultView().dispose();
		}
		
		ResultTable table =
			new ResultTable(this.getView().getParent(), SWT.BORDER, this.configuredQuery);
		
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		table.setLayoutData(data);
		
		this.getView().setResultView(table);
		
		this.configuredQuery.schedule();
		
		this.getView().getParent().layout();
	}
	
	/**
	 * Opens a dialog which asks the user to define a new query which then is set by the opened
	 * dialog to this.configuredQuery. If null, the user aborted.
	 */
	protected void getInput(){
		QueryInputDialog dialog = new QueryInputDialog(this.view.getSite().getShell(), this);
		if (dialog.open() != Window.OK) {
			this.configuredQuery = null; // user aborted
		}
	}
	
	/** This action is enabled as soon as the last job finished. */
	public void jobFinished(BackgroundJob j){
		this.getView().setButtonsEnabled(true);
		this.getView().getResultView().createTable(this.configuredQuery);
		// j.removeListener(this); //Exception in BackgroundJob
	}
	
	public void setConfiguredQuery(AbstractQuery configuredQuery){
		this.configuredQuery = configuredQuery;
	}
	
	protected OutputView getView(){
		return view;
	}
	
	protected AbstractQuery getConfiguredQuery(){
		return configuredQuery;
	}
}
