package ch.medshare.mediport.gui;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.util.SWTHelper;
import ch.medshare.awt.Desktop;
import ch.medshare.mediport.config.Client;
import ch.medshare.mediport.util.MediPortHelper;
import ch.medshare.util.UtilFile;
import ch.rgw.tools.ExHandler;

public class ErrorInvoiceForm extends Composite {

	private final Client client;

	ConfigServicePreferenceStore prefs = new ConfigServicePreferenceStore(Scope.GLOBAL);

	public ErrorInvoiceForm(Composite parent, int style, Client client) {
		super(parent, style);
		this.client = client;
		createArea();
	}

	private String addStyleSheetLine(String xmlContent, String xmlFilename) {
		int ssIndex = xmlContent.indexOf("<?xml-stylesheet"); //$NON-NLS-1$
		if (ssIndex >= 0) {
			int hrefIndex1 = xmlContent.indexOf("href=", ssIndex); //$NON-NLS-1$
			int hrefIndex2 = xmlContent.indexOf("?>", hrefIndex1); //$NON-NLS-1$
			return xmlContent.substring(0, hrefIndex1) + "href='" + xmlFilename //$NON-NLS-1$
					+ "'" + xmlContent.substring(hrefIndex2); //$NON-NLS-1$

		}
		String newLine = "<?xml-stylesheet type='text/xsl' href='" //$NON-NLS-1$
				+ xmlFilename + "'?>"; //$NON-NLS-1$

		int index = xmlContent.indexOf("?>"); //$NON-NLS-1$
		if (index >= 0) {
			int index2 = xmlContent.indexOf("?>\n"); //$NON-NLS-1$
			if (index2 == -1) {
				newLine = newLine + StringUtils.LF;
			}
			return xmlContent.substring(0, index + 2) + StringUtils.LF + newLine + xmlContent.substring(index + 2);
		}
		return newLine;
	}

	private void copyStylesheet(String toDir) throws IOException {

		String ssAbsolutePath = UtilFile.getCorrectPath(client.getStylesheet());
		if (ssAbsolutePath.startsWith("\\") && !(ssAbsolutePath.startsWith("\\\\"))) {
			ssAbsolutePath = "\\" + ssAbsolutePath;
		}
		if (ssAbsolutePath.startsWith("/") && !(ssAbsolutePath.startsWith("//"))) {
			ssAbsolutePath = "//" + ssAbsolutePath;
		}
		if (ssAbsolutePath != null && ssAbsolutePath.length() > 0) {
			File fromFile = new File(ssAbsolutePath);
			if (fromFile.exists()) {
				// Stylesheet kopieren
				String ssFilename = fromFile.getName();
				String toFilenamePath = toDir + File.separator + ssFilename;
				File testFile = new File(toFilenamePath);
				if (testFile.exists()) {
					testFile.delete();
				}
				UtilFile.copyFile(ssAbsolutePath, toFilenamePath);
				// Alle xml mit stylesheet ergänzen
				File dir = new File(toDir);
				for (File file : dir.listFiles(MediPortHelper.XML_FILTER)) {
					String xmlContent = UtilFile.readTextFile(file.getAbsolutePath());
					UtilFile.writeTextFile(file.getAbsolutePath(), addStyleSheetLine(xmlContent, ssFilename));
				}
			} else {
				String message = MessageFormat.format(Messages.ErrorInvoiceForm_msg_copyStylesheet,
						new Object[] { fromFile.getAbsolutePath() }); // $NON-NLS-1$
				MessageDialog.openError(getShell(), Messages.ErrorInvoiceForm_error_copyStylesheet, // $NON-NLS-1$
						message);
			}
		}
	}

	private void openErrorDir(Shell shell, File directory) {
		if (directory.isDirectory()) {
			try {
				Desktop.open(directory);
			} catch (Exception ex) {
				ExHandler.handle(ex);
				MessageDialog.openError(shell, Messages.ErrorInvoiceForm_msg_Fehlerverzeichnis, ex // $NON-NLS-1$
						.getMessage());
			}
		}
	}

	private void openReceiveDir(Shell shell, File directory) {
		if (directory.isDirectory()) {
			try {
				copyStylesheet(directory.getAbsolutePath());
				Desktop.open(directory);
			} catch (Exception ex) {
				ExHandler.handle(ex);
				MessageDialog.openError(shell, Messages.ErrorInvoiceForm_msg_Antwortverzeichnis, ex // $NON-NLS-1$
						.getMessage());
			}
		}
	}

	private void createArea() {
		setLayout(new GridLayout(1, false));
		setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		final File errorDir = new File(client.getError_dir());
		final File receiveDir = new File(client.getReceive_dir());

		int countError = 0;
		if (errorDir != null && errorDir.isDirectory()) {
			countError = errorDir.list(MediPortHelper.XML_FILTER).length;
		}

		int countReceive = 0;
		if (receiveDir != null && receiveDir.isDirectory()) {
			countReceive = receiveDir.list(MediPortHelper.XML_FILTER).length;
		}

		Button btnErrorDir = new Button(this, SWT.PUSH);
		String msgErrorDir = MessageFormat.format(Messages.ErrorInvoiceForm_msg_Fehlerverzeichnis,
				new Object[] { new Integer(countError) }); // $NON-NLS-1$
		btnErrorDir.setText(msgErrorDir);
		btnErrorDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Button btnReceiveDir = new Button(this, SWT.PUSH);
		String msgReceiveDir = MessageFormat.format(Messages.ErrorInvoiceForm_msg_Antwortverzeichnis,
				new Object[] { new Integer(countReceive) }); // $NON-NLS-1$
		btnReceiveDir.setText(msgReceiveDir);
		btnReceiveDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		btnErrorDir.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openErrorDir(getShell(), errorDir);
			}
		});

		btnReceiveDir.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openReceiveDir(getShell(), receiveDir);
			}
		});
	}
}
