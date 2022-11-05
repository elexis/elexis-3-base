package ch.elexis.laborimport.eurolyser.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.l10n.Messages;
import ch.elexis.laborimport.eurolyser.ui.dialog.LabImportDialog;

public class LabImportAction extends Action {

	private static ImageDescriptor imageDescriptor;

	public LabImportAction() {
		super("Eurolyser Import");
		setImageDescriptor(getImageDescriptor());
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		if (imageDescriptor == null) {
			Image image = new Image(Display.getDefault(), getClass().getResourceAsStream("/rsc/eurolyser_16x16.png"));
			imageDescriptor = ImageDescriptor.createFromImage(image);
		}
		return imageDescriptor;
	}

	@Override
	public void run() {
		LabImportDialog dlg = new LabImportDialog(Display.getDefault().getActiveShell());
		dlg.create();
		dlg.getShell().setText(Messages.CodeDetailView_importerCaption); // $NON-NLS-1$
		if (dlg.open() == Dialog.OK) {
			dlg.run();
		}
	}
}
