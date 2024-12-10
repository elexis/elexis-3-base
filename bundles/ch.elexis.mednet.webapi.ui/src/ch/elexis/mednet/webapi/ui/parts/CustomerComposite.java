package ch.elexis.mednet.webapi.ui.parts;

import java.util.function.Consumer;

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

public class CustomerComposite {
	private Composite parent;
	private Consumer<Integer> loadProvidersCallback;
	private ICustomerSelectionListener customerSelectionListener;

	public interface ICustomerSelectionListener {
		void onCustomerSelected(Integer customerId, String customerName);
	}

	public CustomerComposite(Composite parent, ICustomerSelectionListener customerSelectionListener) {
		this.parent = parent;
		this.customerSelectionListener = customerSelectionListener;
	}

	public void show() {
		parent.setLayout(UIStyleTableHelper.createStyledGridLayout());

		Text searchBox = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH | SWT.CANCEL);
		searchBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		searchBox.setMessage(Messages.Composite_searchBoxMessage);

		Table customerTable = UIStyleTableHelper.createStyledTable(parent);

		String[] columnHeaders = { Messages.CustomerComposite_customerID, Messages.CustomerComposite_customerFirstName,
				Messages.CustomerComposite_customerLastName };
		int[] columnWidths = { 0, 150, 150 };

		TableColumn[] columns = new TableColumn[columnHeaders.length];

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(customerTable, SWT.NONE);
			column.setText(columnHeaders[i]);
			column.setWidth(columnWidths[i]);
			columns[i] = column;

			if (i == 0) {
				column.setResizable(false);
			}

			final int columnIndex = i;
			column.addListener(SWT.Selection, event -> {
				TableHelper.sortTable(customerTable, columnIndex); // Use TableHelper
			});
		}

		DataHandler.fetchAndDisplayCustomers(customerTable);

		searchBox.addModifyListener(event -> {
			String searchText = searchBox.getText().toLowerCase();
			TableHelper.filterTable(customerTable, searchText); // Use TableHelper
		});

		customerTable.addListener(SWT.Resize, event -> {
			int tableWidth = customerTable.getClientArea().width;
			int totalWidth = 0;

			// Calculate the width of all columns except the last one
			for (int i = 0; i < customerTable.getColumnCount() - 1; i++) {
				totalWidth += customerTable.getColumn(i).getWidth();
			}

			// Adjust the last column to fill the remaining space
			int remainingWidth = Math.max(100, tableWidth - totalWidth);
			columns[columns.length - 1].setWidth(remainingWidth);
		});

		customerTable.addListener(SWT.MouseDoubleClick, event -> {
			TableItem[] selection = customerTable.getSelection();
			if (selection.length > 0) {
				TableItem selectedItem = selection[0];
				Integer customerId = Integer.parseInt(selectedItem.getText(0));
				String customerFirstName = selectedItem.getText(1);
				String customerLastName = selectedItem.getText(2);
				String customerName = customerFirstName + " " + customerLastName;
				if (customerId != null) {
					customerSelectionListener.onCustomerSelected(customerId, customerName);
				}
			}
		});


		parent.layout();
	}

}
