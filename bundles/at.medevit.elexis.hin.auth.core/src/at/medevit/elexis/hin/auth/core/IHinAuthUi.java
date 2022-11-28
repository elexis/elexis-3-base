package at.medevit.elexis.hin.auth.core;

import java.util.Optional;

public interface IHinAuthUi {

	public void openBrowser(String url);

	public Optional<String> openInputDialog(String title, String message);

}
