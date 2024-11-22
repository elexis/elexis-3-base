package ch.elexis.mednet.webapi.ui.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.fhir.util.UIStyleHelper;
import ch.elexis.mednet.webapi.ui.handler.DataHandler;

public class FormComposite {
	private Composite parent;
	private Integer customerId;
	private Integer providerId;

	public FormComposite(Composite parent) {
		this.parent = parent;
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
		parent.setLayout(UIStyleHelper.createStyledGridLayout());

		Table formTable = UIStyleHelper.createStyledTable(parent);

		String[] columnHeaders = { Messages.FormComposite_formId };
		int[] columnWidths = { 100 };

		UIStyleHelper.addTableColumns(formTable, columnHeaders, columnWidths);

		TableColumn nameColumn = new TableColumn(formTable, SWT.NONE);
		nameColumn.setText(Messages.FormComposite_formName);
		nameColumn.setResizable(true);

		DataHandler.fetchAndDisplayFormsForProvider(formTable, providerId, customerId);

		formTable.addListener(SWT.Resize, event -> {
			int tableWidth = formTable.getClientArea().width;
			int totalWidth = 0;
			for (int i = 0; i < formTable.getColumnCount() - 1; i++) {
				totalWidth += formTable.getColumn(i).getWidth();
			}

			nameColumn.setWidth(Math.max(100, tableWidth - totalWidth));
		});

		formTable.addListener(SWT.MouseDoubleClick, event -> {
			TableItem[] selection = formTable.getSelection();
			if (selection.length > 0) {
				TableItem selectedItem = selection[0];
				try {
					Integer formId = Integer.parseInt(selectedItem.getText(0));
					if (formId != null) {
						DataHandler.fillPatientData(customerId, providerId, formId);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});

		parent.layout();
	}
}
