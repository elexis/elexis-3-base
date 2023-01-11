package at.medevit.elexis.hin.auth.core;

import java.util.Optional;
import java.util.function.Supplier;

public interface IHinAuthUi {

	public void openBrowser(String url);

	public Optional<String> openInputDialog(String title, String message);

	public Object getWithCancelableProgress(String name, Supplier<?> supplier);

}
