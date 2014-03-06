package waelti.statistics.actions;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import waelti.statistics.queries.SetDataException;
import waelti.statistics.views.OutputView;
import ch.elexis.core.ui.util.SWTHelper;

public class RefreshQueryAction extends NewQueryAction {
	
	public RefreshQueryAction(OutputView outputView){
		super(outputView);
		this.setText("Auswertung aktualisieren.");
		this.setToolTipText("Aktualisiert die aktuelle Auswertung mit " + "den neuen Daten.");
		this.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("Waelti.Statistics",
			"icons/database_refresh.png"));
	}
	
	@Override
	protected void getInput(){
		// TODO: remove this hot fix as soon as gw changed backgroundjobListener
		this.getView().getQuery().removeListener(this.getView().getNewQueryAction());
		try {
			this.setConfiguredQuery(this.getView().getQueryOptions().getQuery());
		} catch (SetDataException e) {
			this.setConfiguredQuery(null);
			SWTHelper.showError("Input Error", e.getMessage());
		}
		
	}
}
