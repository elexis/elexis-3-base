package at.medevit.elexis.hin.auth.ui;

import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;

import at.medevit.elexis.hin.auth.core.IHinAuthUi;

@Component
public class HinAuthUi implements IHinAuthUi {

	private String inputValue;

	@Override
	public void openBrowser(String url) {
		Program.launch(url);
	}

	@Override
	public Optional<String> openInputDialog(String title, String message) {
		inputValue = null;
		Display.getDefault().syncExec(() -> {
			InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), title, message, null, null);
			if (dialog.open() == Dialog.OK) {
				inputValue = dialog.getValue();
			}
		});
		return Optional.ofNullable(inputValue);
	}
}
