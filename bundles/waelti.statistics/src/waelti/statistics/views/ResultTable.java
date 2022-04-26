package waelti.statistics.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import waelti.statistics.queries.AbstractQuery;
import ch.elexis.core.ui.UiDesk;

/**
 * A composite describing the lower half of the OutputView. It contains all
 * composites needed to display the query results in a table. It will show a
 * default text until the query is evaluated and then display the result.
 */
public class ResultTable extends Composite {

	/** This label will be displayed as long as the results are not calculated. */
	private Label waitText;

	/**
	 * standard constructor
	 *
	 * @param query the query to be displayed in this resultTable.
	 */
	public ResultTable(Composite parent, int style, AbstractQuery query) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.setBackground(UiDesk.getColor(UiDesk.COL_WHITE));

		initWaitLabel();
	}

	private void initWaitLabel() {
		this.waitText = new Label(this, SWT.WRAP | SWT.CENTER);
		this.waitText.setBackground(UiDesk.getColor(UiDesk.COL_WHITE));

		GridData data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		this.waitText.setLayoutData(data);

		this.waitText.setText("Bitte warten Sie, bis die Daten aufbereitet sind.");
	}

	/**
	 * Creates the table with the given query. Make sure the query has already run.
	 */
	public void createTable(AbstractQuery query) {

		TableViewerFactory factory = new TableViewerFactory(this, query);
		factory.createTableViewer();

		this.waitText.dispose();
		this.layout();
	}

}
