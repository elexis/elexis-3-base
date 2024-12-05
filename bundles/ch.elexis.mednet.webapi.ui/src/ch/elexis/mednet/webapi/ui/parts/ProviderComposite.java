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
import ch.elexis.mednet.webapi.ui.handler.PatientFetcher;
import ch.elexis.mednet.webapi.ui.handler.TableHelper;
import ch.elexis.mednet.webapi.ui.util.UIStyleTableHelper;

public class ProviderComposite {
    private Composite parent;
    private Consumer<Integer> loadFormsCallback;
	private IProviderSelectionListener providerSelectionListener;
	private PatientFetcher patientFetcher;
	private boolean ascending = true;

	public ProviderComposite(Composite parent, IProviderSelectionListener providerSelectionListener,
			PatientFetcher patientFetcher) {
        this.parent = parent;
		this.providerSelectionListener = providerSelectionListener;
		this.patientFetcher = patientFetcher;
    }

	public interface IProviderSelectionListener {
		void onProviderSelected(Integer providerId, String providerName);
	}


	public void show(Integer customerId) {
		parent.setLayout(UIStyleTableHelper.createStyledGridLayout());

		Text searchBox = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH | SWT.CANCEL);
		searchBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		searchBox.setMessage(Messages.Composite_searchBoxMessage);

		Table providerTable = UIStyleTableHelper.createStyledTable(parent);

		String[] columnHeaders = { Messages.ProviderComposite_Anbieter_ID, Messages.ProviderComposite_Anbieter_Name };
		int[] columnWidths = { 0, 250 };

		TableColumn[] columns = new TableColumn[columnHeaders.length];

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(providerTable, SWT.NONE);
			column.setText(columnHeaders[i]);
			column.setWidth(columnWidths[i]);
			columns[i] = column;

			if (i == 0) {
				column.setResizable(false);
			}

			final int columnIndex = i;
			column.addListener(SWT.Selection, event -> {
				TableHelper.sortTable(providerTable, columnIndex); // Use TableHelper
			});
		}

		DataHandler.fetchAndDisplayProviders(providerTable, customerId);

		searchBox.addModifyListener(event -> {
			String searchText = searchBox.getText().toLowerCase();
			TableHelper.filterTable(providerTable, searchText); // Use TableHelper
		});

		providerTable.addListener(SWT.Resize, event -> {
			int tableWidth = providerTable.getClientArea().width;
			int totalWidth = 0;

			// Calculate the width of all columns except the last one
			for (int i = 0; i < providerTable.getColumnCount() - 1; i++) {
				totalWidth += providerTable.getColumn(i).getWidth();
			}

			// Adjust the last column to fill the remaining space
			int remainingWidth = Math.max(100, tableWidth - totalWidth);
			columns[columns.length - 1].setWidth(remainingWidth);
		});

		providerTable.addListener(SWT.MouseDoubleClick, event -> {
			TableItem[] selection = providerTable.getSelection();
			if (selection.length > 0) {
				TableItem selectedItem = selection[0];
				Integer providerId = Integer.parseInt(selectedItem.getText(0));
				String providerName = selectedItem.getText(1);
				if (providerId != null) {
					providerSelectionListener.onProviderSelected(providerId, providerName);
				}
			}
		});

		parent.layout();
	}

}
