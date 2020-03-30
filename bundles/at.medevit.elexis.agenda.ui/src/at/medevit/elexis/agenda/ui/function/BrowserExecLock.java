package at.medevit.elexis.agenda.ui.function;

import org.eclipse.swt.browser.Browser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RAP {@link Browser#execute(String)} might throw an
 * {@link IllegalStateException} if script execution is not synchronized. Hence
 * if we face a RAP environment we enforce synchronization.
 */
public class BrowserExecLock {

	private static Logger log = LoggerFactory.getLogger(BrowserExecLock.class);

	public static boolean executeScript(final Browser browser, final String script) {

		log.info("executeScript: {}", script); //$NON-NLS-1$

		if (isRapBrowser(browser)) {
			synchronized (BrowserExecLock.class) {
				if (!browser.isDisposed()) {
					try {
						return browser.execute(script);
					} catch (IllegalStateException ise) {
						log.warn("Catched IllegalStateException in script [{}]", script);
					}
				}
				return false;
			}
		} else {
			return browser.execute(script);
		}
	}

	private static boolean isRapBrowser(Browser browser) {
		// TODO switch to get type in 3.13
		// TODO returns true on OS X which is wrong
		return browser.getWebBrowser() == null;
	}

}
