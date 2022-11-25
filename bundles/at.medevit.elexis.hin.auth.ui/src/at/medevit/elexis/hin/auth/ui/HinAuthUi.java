package at.medevit.elexis.hin.auth.ui;

import org.eclipse.swt.program.Program;

import at.medevit.elexis.hin.auth.core.IHinAuthUi;

public class HinAuthUi implements IHinAuthUi {

	@Override
	public void openBrowser(String url) {
		Program.launch(url);
	}

}
