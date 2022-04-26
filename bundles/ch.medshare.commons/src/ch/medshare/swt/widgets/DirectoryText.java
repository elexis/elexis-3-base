package ch.medshare.swt.widgets;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import ch.medshare.util.UtilMisc;

public class DirectoryText extends AbstractBrowseText {

	public DirectoryText(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Helper that opens the directory chooser dialog.
	 *
	 * @param startingDirectory The directory the dialog will open in.
	 * @return File File or <code>null</code>.
	 *
	 */
	private static File getDirectory(Shell shell, File startingDirectory) {

		DirectoryDialog fileDialog = new DirectoryDialog(shell, SWT.OPEN);
		if (startingDirectory != null) {
			fileDialog.setFilterPath(startingDirectory.getPath());
		}
		String dir = fileDialog.open();
		if (dir != null) {
			dir = dir.trim();
			if (dir.length() > 0) {
				return new File(dir);
			}
		}

		return null;
	}

	@Override
	protected SelectionAdapter getBrowseSelectionAdapter() {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				File f = new File(getText());
				if (!f.exists()) {
					f = null;
				}
				File d = getDirectory(getShell(), f);
				if (d != null) {
					setText(UtilMisc.replaceWithForwardSlash(d.getAbsolutePath()));
					setFocus();
				}
			}

		};
	}

}