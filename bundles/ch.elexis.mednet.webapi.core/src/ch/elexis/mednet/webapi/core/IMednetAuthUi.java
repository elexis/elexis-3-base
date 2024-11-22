package ch.elexis.mednet.webapi.core;

import java.util.Optional;
import java.util.function.Supplier;

public interface IMednetAuthUi {

	public void openBrowser(String url);

	public Optional<String> openInputDialog(String title, String message);

	public Object getWithCancelableProgress(String name, Supplier<?> supplier);

}
