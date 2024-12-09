package ch.elexis.mednet.webapi.ui.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.handler.DataHandler;
import ch.elexis.mednet.webapi.ui.handler.TableHelper;
import ch.elexis.mednet.webapi.ui.util.UIStyleTableHelper;

public class FormComposite {
	private Composite parent;
	private Integer customerId;
	private Integer providerId;

	public interface IFormSelectionListener {
		void onFormSelected(Integer customerId, Integer providerId, Integer formId, String formName);
	}

	private IFormSelectionListener formSelectionListener;

	public FormComposite(Composite parent, IFormSelectionListener formSelectionListener) {
		this.parent = parent;
		this.formSelectionListener = formSelectionListener;
	}

	/**
	 * Zeigt die Form-Tabelle für einen gegebenen Kunden und Anbieter an.
	 *
	 * @param customerId Die ID des Kunden.
	 * @param providerId Die ID des Anbieters.
	 */
	public void show(Integer customerId, Integer providerId) {
	    this.customerId = customerId;
	    this.providerId = providerId;
	    parent.setLayout(UIStyleTableHelper.createStyledGridLayout());

		// Create search text box
		Text searchBox = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH | SWT.CANCEL);
		searchBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		searchBox.setMessage(Messages.Composite_searchBoxMessage);

	    Table formTable = UIStyleTableHelper.createStyledTable(parent);

		// Define column headers
	    String[] columnHeaders = { Messages.FormComposite_formId, Messages.FormComposite_formName };
		int[] columnWidths = { 0, 150 }; // Set the ID column width to 0 to make it invisible

		TableColumn[] columns = new TableColumn[columnHeaders.length];

	    for (int i = 0; i < columnHeaders.length; i++) {
	        TableColumn column = new TableColumn(formTable, SWT.NONE);
	        column.setText(columnHeaders[i]);
	        column.setWidth(columnWidths[i]);
			columns[i] = column;

	        if (i == 0) {
				column.setResizable(false); // Disable resizing for the first (hidden) column
	        }

			final int columnIndex = i;
			column.addListener(SWT.Selection, event -> {
				TableHelper.sortTable(formTable, columnIndex); // Use TableHelper for sorting
			});
	    }

		// Fetch and display forms in the table
	    DataHandler.fetchAndDisplayFormsForProvider(formTable, providerId, customerId);

		// Add listener for search box
		searchBox.addModifyListener(event -> {
			String searchText = searchBox.getText().toLowerCase();
			TableHelper.filterTable(formTable, searchText); // Use TableHelper for filtering
		});

		// Adjust column widths dynamically to make the last column fill the remaining
		// space
	    formTable.addListener(SWT.Resize, event -> {
	        int tableWidth = formTable.getClientArea().width;
	        int totalWidth = 0;

			// Sum up the widths of all columns except the last one
			for (int i = 0; i < formTable.getColumnCount() - 1; i++) {
	            totalWidth += formTable.getColumn(i).getWidth();
	        }

			// Set the last column to fill the remaining space
	        int remainingWidth = Math.max(100, tableWidth - totalWidth);
			columns[columns.length - 1].setWidth(remainingWidth);
	    });

		// Handle double-click events to fill patient data
	    formTable.addListener(SWT.MouseDoubleClick, event -> {
	        TableItem[] selection = formTable.getSelection();
	        if (selection.length > 0) {
	            TableItem selectedItem = selection[0];
	            try {
	                Integer formId = Integer.parseInt(selectedItem.getText(0));
					String formName = selectedItem.getText(1); // Annahme: FormName ist in Spalte 1
	                if (formId != null) {
	                    DataHandler.fillPatientData(customerId, providerId, formId);
						if (formSelectionListener != null) {
							formSelectionListener.onFormSelected(customerId, providerId, formId, formName);
						}
	                }
	            } catch (NumberFormatException e) {
	                e.printStackTrace();
	            }
	        }
	    });


	    parent.layout();
	}

}
