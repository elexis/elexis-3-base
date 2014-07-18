package ch.medshare.swt.widgets;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import ch.medshare.util.UtilMisc;

public class FileText extends AbstractBrowseText {
	private String[] extensions = new String[0];
	
	public FileText(Composite parent, int style){
		super(parent, style);
	}
	
	public void setExtensions(final String[] extensions){
		this.extensions = extensions;
	}
	
	/**
	 * Helper to open the file chooser dialog.
	 * 
	 * @param startingDirectory
	 *            the directory to open the dialog on.
	 * @return File The File the user selected or <code>null</code> if they do not.
	 */
	private static File getFile(Shell shell, String[] extensions, File startingDirectory){
		
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		if (startingDirectory != null) {
			dialog.setFileName(startingDirectory.getPath());
		}
		if (extensions != null) {
			dialog.setFilterExtensions(extensions);
		}
		String file = dialog.open();
		if (file != null) {
			file = file.trim();
			if (file.length() > 0) {
				return new File(file);
			}
		}
		
		return null;
	}
	
	@Override
	protected SelectionAdapter getBrowseSelectionAdapter(){
		return new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				File f = new File(getText());
				if (!f.exists()) {
					f = null;
				}
				File d = getFile(getShell(), extensions, f);
				if (d != null) {
					setText(UtilMisc.replaceWithForwardSlash(d.getAbsolutePath()));
					setFocus();
				}
			}
		};
	}
	
}
