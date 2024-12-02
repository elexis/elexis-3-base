package ch.elexis.mednet.webapi.ui.parts;

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
import ch.elexis.mednet.webapi.ui.fhir.util.UIStyleHelper;
import ch.elexis.mednet.webapi.ui.handler.DataHandler;
import ch.elexis.mednet.webapi.ui.handler.ImportOmnivore;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class SubmittedFormsComposite {
    private static final Logger logger = LoggerFactory.getLogger(SubmittedFormsComposite.class);

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
		refreshButton.setText("Refresh");
		refreshButton.setImage(Images.IMG_REFRESH.getImage());
        refreshButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		refreshButton.addListener(SWT.Selection, e -> refreshTableData());

        customerCombo = UIStyleHelper.createStyledCCombo(parent);
		DataHandler.loadCustomersFromApi(customerCombo);
		customerCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        submittedFormsTable = UIStyleHelper.createStyledTable(parent);
        String[] columnHeaders = {"Order No.", "Date", "Patient No.", "Patient Name", "Birthdate", "Type", "Exports and Attachments", "Sender", "Receiver"};
        int[] columnWidths = {80, 120, 90, 120, 100, 50, 120, 100, 100};
        UIStyleHelper.addTableColumns(submittedFormsTable, columnHeaders, columnWidths);
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
		try {
			String[] parts = selectedCustomer.split("-");
			if (parts.length > 0) {
				String idPart = parts[0].trim();
				return Integer.parseInt(idPart);
			}
		} catch (NumberFormatException ex) {
			logger.error("Error parsing customer ID from selected customer: {}", selectedCustomer, ex);
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
			String downloadUrl = (String) selectedItem.getData("downloadUrl");
			String packageId = (String) selectedItem.getData("packageId");
			String objectId = extractObjectIdFromDownloadUrl(downloadUrl);
			String createDate = selectedItem.getText(1);
			String patientNr = selectedItem.getText(2);
			String patientName = selectedItem.getText(3);
			String exportType = selectedItem.getText(6);
			String receiver = selectedItem.getText(8);
			String sender = selectedItem.getText(7);
			@SuppressWarnings("unchecked")
			List<Map<String, String>> downloadHeadersList = (List<Map<String, String>>) selectedItem
					.getData("downloadHeaders");
			if (downloadUrl != null && objectId != null) {
				SingleFileDownloaderHandler singleDownloader = new SingleFileDownloaderHandler();
				singleDownloader.downloadSingleFile(downloadUrl, patientNr, patientName, exportType, receiver, sender,
						downloadHeadersList, packageId, createDate);
			} else {
				logger.warn("Download URL or Object ID is missing.");
			}

			try {
				IStatus status = new ImportOmnivore().run();
				if (!status.isOK()) {
					throw new TaskException(TaskException.EXECUTION_ERROR,
							"Import failed with status: " + status.getMessage());
				}
			} catch (Exception ex) {
				logger.error("Error executing import task: {}", ex.getMessage(), ex);
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

    private String extractObjectIdFromDownloadUrl(String downloadUrl) {
        if (downloadUrl == null) return null;
        try {
            String path = URI.create(downloadUrl).getPath();
            String[] segments = path.split("/");
            if (segments.length >= 4) {
                String objectIdWithExtension = segments[4];
                int dotIndex = objectIdWithExtension.lastIndexOf('.');
                if (dotIndex > 0) {
                    return objectIdWithExtension.substring(0, dotIndex);
                } else {
                    return objectIdWithExtension;
                }
            }
        } catch (Exception ex) {
            logger.error("Error extracting objectId: {}", ex.getMessage(), ex);
        }
        return null;
    }

	public void refreshTableData() {
		if (customerCombo == null || customerCombo.isDisposed()) {
			logger.warn("customerCombo ist disposed oder null. refreshTableData wird abgebrochen.");
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
