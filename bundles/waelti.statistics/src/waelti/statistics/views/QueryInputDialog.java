package waelti.statistics.views;

import java.util.Hashtable;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.util.SWTHelper;
import waelti.statistics.actions.NewQueryAction;
import waelti.statistics.queries.AbstractQuery;
import waelti.statistics.queries.Consultations;
import waelti.statistics.queries.PatientCosts;

/**
 * This Dialog represents the dialog in which the user can choose which query
 * should be run and set all query specific settings.
 */
public class QueryInputDialog extends TitleAreaDialog {

	/** List of all available queries. TODO: reflection of query package */
	private Hashtable<String, AbstractQuery> queryTable;

	private OptionPanel options;

	private Combo combo;

	private Text description;

	private NewQueryAction action;

	public QueryInputDialog(Shell parentShell) {
		super(parentShell);

		queryTable = new Hashtable<String, AbstractQuery>();

		this.fillQueryTable();
	}

	/** Adds all registered queries to the query list. */
	private void fillQueryTable() {
		// TODO fill via extension point
		Consultations cons = new Consultations();
		queryTable.put(cons.getTitle(), cons);

		PatientCosts patient = new PatientCosts();
		queryTable.put(patient.getTitle(), patient);

		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor("Waelti.Statistics.Query");

		for (IConfigurationElement element : elements) {
			try {
				AbstractQuery query = (AbstractQuery) element.createExecutableExtension("class");

				queryTable.put(query.getTitle(), query);

			} catch (CoreException e) {
				// TODO Log
				System.out.println(e);
			}
		}

	}

	public QueryInputDialog(Shell shell, NewQueryAction newQueryAction) {
		this(shell);
		this.action = newQueryAction;
	}

	@Override
	public void create() {
		super.create();
		this.setTitle("Neue Auswertung starten");
		this.setMessage("Wählen Sie eine Auswertung aus und definieren Sie die Parameter.");
	}

	@Override
	public Control createDialogArea(Composite parent) {

		Composite area = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(1, false);
		area.setLayout(layout);

		GridData gridData = SWTHelper.getFillGridData(1, true, 1, true);
		gridData.widthHint = this.convertHorizontalDLUsToPixels(350);
		area.setLayoutData(gridData);

		this.initCombo(area);
		this.initDescription(area);

		return area;
	}

	private void initDescription(Composite parent) {
		this.description = new Text(parent, SWT.WRAP | SWT.READ_ONLY);
		this.description.setText("Keine Auswertung ausgewählt. Wählen Sie " + "bitte eine Auswertung aus.");

		this.description.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
	}

	private void initCombo(Composite parent) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;

		combo = new Combo(parent, SWT.READ_ONLY);
		combo.setLayoutData(gridData);

		// populate
		Set<String> titleSet = queryTable.keySet();
		for (String title : titleSet) {
			combo.add(title);
		}

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = combo.getText();
				AbstractQuery selectedQuery = queryTable.get(text);
				updateDescription(selectedQuery);
				if (options == null) {
					initOptions(combo.getParent());
				}
				options.updateContent(selectedQuery);
				getShell().pack(); // resize the dialog
			}
		});
	}

	private void initOptions(Composite parent) {
		this.options = new OptionPanel(parent);
		this.options.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
	}

	private void updateDescription(AbstractQuery selectedQuery) {
		this.description.setText(selectedQuery.getDescription());
	}

	// not used any more. may be still useful later on.
	/*
	 * private String cutString(String desc) { StringBuffer buf = new
	 * StringBuffer(desc); for (int i = 100; i < buf.length(); i += 100) {
	 *
	 * int j = i; // deviation to the right while (buf.charAt(j) != ' ') { j++; }
	 *
	 * int y = i; // deviation to the left while (buf.charAt(y) != ' ') { y--; }
	 *
	 * i = (y > j) ? j : y; // smaller deviation is preferred.
	 *
	 * buf.deleteCharAt(i); // replace space with a new line char. buf.insert(i,
	 * "\n"); } return buf.toString(); }
	 */

	/**
	 * When "ok" is pressed, the input data will be read, a query object created and
	 * set in the given action. If there is any exception setting any of the data
	 * given by the input window, a message will be displayed and nothing happens.
	 */
	@Override
	protected void okPressed() {
		if (this.createQuery()) {
			super.okPressed();
		}
	}

	/**
	 * Returns true if the query was created. False if any exception occured. The
	 * exception message will be shown.
	 */
	private boolean createQuery() {
		try {
			AbstractQuery query = this.options.getQuery();
			this.action.setConfiguredQuery(query);
			return true;
		} catch (Exception e) {
			SWTHelper.showError("Input Error", e.getMessage());
			return false;
		}

	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
