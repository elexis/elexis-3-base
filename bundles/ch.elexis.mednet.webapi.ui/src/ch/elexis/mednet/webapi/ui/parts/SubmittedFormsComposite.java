package ch.elexis.mednet.webapi.ui.parts;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.mednet.webapi.core.handler.SingleFileDownloaderHandler;
import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.handler.DataHandler;
import ch.elexis.mednet.webapi.ui.handler.ImportOmnivore;
import ch.elexis.mednet.webapi.ui.util.UIStyleTableHelper;

public class SubmittedFormsComposite {

	private static final Logger log = LoggerFactory.getLogger(SubmittedFormsComposite.class);

	private Composite parent;
	private CCombo customerCombo;
	private Label noFormsLabel;
	private Table submittedFormsTable;

	public SubmittedFormsComposite(Composite parent) {
		this.parent = parent;
	}

	public void showSubmittedForms() {
		parent.setLayout(new GridLayout(1, false));
		Composite labelComposite = new Composite(parent, SWT.NONE);
		labelComposite.setLayout(new GridLayout(2, false));
		GridData labelCompositeData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelComposite.setLayoutData(labelCompositeData);

		noFormsLabel = new Label(labelComposite, SWT.NONE);
		noFormsLabel.setText(Messages.SubmittedFormsComposite_noErrorForms);
		noFormsLabel.setBackground(UiDesk.getColor(UiDesk.COL_GREEN));
		noFormsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		noFormsLabel.setVisible(false);

		Button refreshButton = new Button(labelComposite, SWT.PUSH);
		refreshButton.setText("Refresh"); //$NON-NLS-1$
		refreshButton.setImage(Images.IMG_REFRESH.getImage());
		refreshButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		refreshButton.addListener(SWT.Selection, event -> refreshTableData());

		customerCombo = UIStyleTableHelper.createStyledCCombo(parent);
		DataHandler.loadCustomersFromApi(customerCombo);
		customerCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		submittedFormsTable = UIStyleTableHelper.createStyledTable(parent);
		String[] columnHeaders = { Messages.ColumnHeaders_OrderNo, Messages.ColumnHeaders_Date,
				Messages.ColumnHeaders_PatientNo, Messages.ColumnHeaders_PatientName, Messages.ColumnHeaders_Birthdate,
				Messages.ColumnHeaders_Type, Messages.ColumnHeaders_ExportsAndAttachments,
				Messages.ColumnHeaders_Sender, Messages.ColumnHeaders_Receiver };
		int[] columnWidths = { 80, 120, 90, 120, 100, 50, 120, 100, 100 };
		UIStyleTableHelper.addTableColumns(submittedFormsTable, columnHeaders, columnWidths);
		submittedFormsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		loadSubmittedFormsCombo();

		customerCombo.addListener(SWT.Selection, event -> {
			int selectedIndex = customerCombo.getSelectionIndex();
			if (selectedIndex >= 0) {
				String selectedCustomer = customerCombo.getItem(selectedIndex);
				Integer customerId = extractCustomerId(selectedCustomer);
				if (customerId != null) {
					loadSubmittedFormsData(customerId);
				}
			}
		});

		submittedFormsTable.addListener(SWT.MouseDoubleClick, event -> handleTableDoubleClick());
		submittedFormsTable.addListener(SWT.Resize, event -> adjustTableColumnWidths());
	}

	private Integer extractCustomerId(String selectedCustomer) {
		if (selectedCustomer == null || selectedCustomer.isBlank()) {
			return null;
		}
		try {
			String[] parts = selectedCustomer.split("-"); //$NON-NLS-1$
			if (parts.length > 0) {
				String idPart = parts[0].trim();
				return Integer.parseInt(idPart);
			}
		} catch (NumberFormatException exception) {
			log.error("Error parsing customer ID from selected customer: [{}]", selectedCustomer, exception);
		}
		return null;
	}

	private void loadSubmittedFormsData(Integer customerId) {
		submittedFormsTable.removeAll();
		boolean hasData = DataHandler.loadSubmittedFormsData(submittedFormsTable, customerId);
		noFormsLabel.setVisible(!hasData);
	}

	private void loadSubmittedFormsCombo() {
		int selectedIndex = customerCombo.getSelectionIndex();
		if (selectedIndex >= 0) {
			String selectedCustomer = customerCombo.getItem(selectedIndex);
			Integer customerId = extractCustomerId(selectedCustomer);
			if (customerId != null) {
				boolean hasData = DataHandler.loadSubmittedFormsData(submittedFormsTable, customerId);
				noFormsLabel.setVisible(!hasData);
			}
		}
	}

	private void handleTableDoubleClick() {
		TableItem[] selection = submittedFormsTable.getSelection();
		if (selection.length > 0) {
			TableItem selectedItem = selection[0];

			String downloadUrl = (String) selectedItem.getData("downloadUrl"); //$NON-NLS-1$
			String packageId = (String) selectedItem.getData("packageId"); //$NON-NLS-1$

			String createDate = selectedItem.getText(1);
			String patientNr = selectedItem.getText(2);
			String patientName = selectedItem.getText(3);
			String exportType = selectedItem.getText(6);
			String sender = selectedItem.getText(7);
			String receiver = selectedItem.getText(8);

			@SuppressWarnings("unchecked")
			List<Map<String, String>> downloadHeadersList = (List<Map<String, String>>) selectedItem
					.getData("downloadHeaders"); //$NON-NLS-1$
			if (downloadUrl != null && packageId != null) {
				SingleFileDownloaderHandler singleDownloader = new SingleFileDownloaderHandler();
				singleDownloader.downloadSingleFile(downloadUrl, patientNr, patientName, exportType, receiver, sender,
						downloadHeadersList, packageId, createDate);
			} else {
				log.warn("Download URL or Package ID is missing. Cannot initiate file download.");
			}

			try {
				IStatus status = new ImportOmnivore().run();
				if (!status.isOK()) {
					throw new TaskException(TaskException.EXECUTION_ERROR,
							"Import failed with status: " + status.getMessage());
				}
			} catch (Exception exception) {
				log.error("Error executing import task.", exception);
			}
			refreshTableData();
		}
	}

	private void adjustTableColumnWidths() {
		int tableWidth = submittedFormsTable.getClientArea().width;
		int totalWidth = 0;
		for (int i = 0; i < submittedFormsTable.getColumnCount() - 1; i++) {
			totalWidth += submittedFormsTable.getColumn(i).getWidth();
		}

		if (submittedFormsTable.getColumnCount() > 0) {
			submittedFormsTable.getColumn(submittedFormsTable.getColumnCount() - 1)
					.setWidth(Math.max(100, tableWidth - totalWidth));
		}
	}

	public void refreshTableData() {
		if (customerCombo == null || customerCombo.isDisposed()) {
			log.warn("CustomerCombo is disposed or null. refreshTableData aborted.");
			return;
		}
		int selectedIndex = customerCombo.getSelectionIndex();
		if (selectedIndex >= 0) {
			String selectedCustomer = customerCombo.getItem(selectedIndex);
			Integer customerId = extractCustomerId(selectedCustomer);
			if (customerId != null) {
				loadSubmittedFormsData(customerId);
			}
		}
	}

	public boolean isDisposed() {
		return parent == null || parent.isDisposed();
	}
}