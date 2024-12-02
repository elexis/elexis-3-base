package ch.elexis.mednet.webapi.ui.parts;

import java.util.function.Consumer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.fhir.util.UIStyleHelper;
import ch.elexis.mednet.webapi.ui.handler.DataHandler;

public class CustomerComposite {
	private Composite parent;
	private Consumer<Integer> loadProvidersCallback;

	public CustomerComposite(Composite parent, Consumer<Integer> loadProvidersCallback) {
		this.parent = parent;
		this.loadProvidersCallback = loadProvidersCallback;
	}

	public void show() {
		parent.setLayout(UIStyleHelper.createStyledGridLayout());
		Table customerTable = UIStyleHelper.createStyledTable(parent);

		// Define column headers including new columns for "First Name" and "Last Name"
		String[] columnHeaders = { Messages.CustomerComposite_customerID, Messages.CustomerComposite_customerFirstName,
				Messages.CustomerComposite_customerLastName };
		int[] columnWidths = { 100, 150, 150 }; // Adjust widths as needed

		UIStyleHelper.addTableColumns(customerTable, columnHeaders, columnWidths);

		// Fetch and display customers in the table
		DataHandler.fetchAndDisplayCustomers(customerTable);

		// Adjust column widths on table resize
		customerTable.addListener(SWT.Resize, event -> {
			int tableWidth = customerTable.getClientArea().width;
			int totalWidth = 0;
			for (int i = 0; i < customerTable.getColumnCount() - 1; i++) {
				totalWidth += customerTable.getColumn(i).getWidth();
			}
			int remainingWidth = Math.max(100, tableWidth - totalWidth);
			customerTable.getColumn(customerTable.getColumnCount() - 1).setWidth(remainingWidth);
		});

		// Handle double-click events to load providers
		customerTable.addListener(SWT.MouseDoubleClick, event -> {
			TableItem[] selection = customerTable.getSelection();
			if (selection.length > 0) {
				TableItem selectedItem = selection[0];
				Integer customerId = Integer.parseInt(selectedItem.getText(0));
				if (customerId != null) {
					loadProvidersCallback.accept(customerId);
				}
			}
		});

		parent.layout();
	}
}
