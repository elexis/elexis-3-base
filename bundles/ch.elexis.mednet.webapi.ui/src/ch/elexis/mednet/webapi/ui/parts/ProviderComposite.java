package ch.elexis.mednet.webapi.ui.parts;

import java.util.function.Consumer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.fhir.util.UIStyleHelper;
import ch.elexis.mednet.webapi.ui.handler.DataHandler;
import ch.elexis.mednet.webapi.ui.handler.PatientFetcher;

public class ProviderComposite {
    private Composite parent;
    private Consumer<Integer> loadFormsCallback;
	private PatientFetcher patientFetcher;

	public ProviderComposite(Composite parent, Consumer<Integer> loadFormsCallback, PatientFetcher patientFetcher) {
        this.parent = parent;
        this.loadFormsCallback = loadFormsCallback;
		this.patientFetcher = patientFetcher;
    }

    public void show(Integer customerId) {
		parent.setLayout(UIStyleHelper.createStyledGridLayout());


		Table providerTable = UIStyleHelper.createStyledTable(parent);

		String[] columnHeaders = { Messages.ProviderComposite_Anbieter_ID };
		int[] columnWidths = { 100 };

		UIStyleHelper.addTableColumns(providerTable, columnHeaders, columnWidths);

		TableColumn nameColumn = new TableColumn(providerTable, SWT.NONE);
		nameColumn.setText(Messages.ProviderComposite_Anbieter_Name);
		nameColumn.setResizable(true);

		DataHandler.fetchAndDisplayProviders(providerTable, customerId);

		providerTable.addListener(SWT.Resize, event -> {
			int tableWidth = providerTable.getClientArea().width;
			int totalWidth = 0;
			for (int i = 0; i < providerTable.getColumnCount() - 1; i++) {
				totalWidth += providerTable.getColumn(i).getWidth();
			}

			nameColumn.setWidth(Math.max(100, tableWidth - totalWidth));
		});
		providerTable.addListener(SWT.MouseDoubleClick, event -> {
			TableItem[] selection = providerTable.getSelection();
			if (selection.length > 0) {
				TableItem selectedItem = selection[0];
				Integer providerId = Integer.parseInt(selectedItem.getText(0));
				if (providerId != null) {
					loadFormsCallback.accept(providerId);
				}
			}
		});

        parent.layout();
    }
}
