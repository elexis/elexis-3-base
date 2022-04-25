package ch.elexis.laborimport.eurolyser.ui.dialog;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.laborimport.eurolyser.ui.LabImportPage;

public class LabImportDialog extends TitleAreaDialog {
	private ImporterPage importer = new LabImportPage();

	public LabImportDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(importer.getTitle());
		setMessage(importer.getDescription());

		return importer.createPage(parent);
	}

	public void run() {
		importer.run(true);
	}
}
