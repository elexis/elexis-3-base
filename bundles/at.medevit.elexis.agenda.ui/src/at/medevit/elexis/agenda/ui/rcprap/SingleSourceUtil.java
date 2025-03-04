package at.medevit.elexis.agenda.ui.rcprap;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.equo.chromium.swt.Browser;

public class SingleSourceUtil {

	private static Logger log = LoggerFactory.getLogger(SingleSourceUtil.class);

	private static final boolean IS_RAP;
	private static final String HTML_BASE_URL;

	static {
		Class<?> clazz = null;
		try {
			clazz = Class.forName("org.eclipse.rap.rwt.RWT"); //$NON-NLS-1$
		} catch (ClassNotFoundException cnfe) {
		}
		IS_RAP = clazz != null;

		if (IS_RAP) {
			String baseUrl = System.getenv("BASE_URL"); //$NON-NLS-1$
			if (baseUrl == null) {
				baseUrl = StringUtils.EMPTY;
			}
			HTML_BASE_URL = baseUrl + "/agenda/static/html/"; //$NON-NLS-1$
		} else {
			String _baseUrl = null;
			try {
				URL url = FileLocator
						.toFileURL(FrameworkUtil.getBundle(SingleSourceUtil.class).getResource("/rsc/html/")); //$NON-NLS-1$
				_baseUrl = url.toString();
			} catch (IOException e) {
				log.error("Initialization error", e); //$NON-NLS-1$
			}
			HTML_BASE_URL = _baseUrl;
		}

	}

	public static boolean isRap() {
		return IS_RAP;
	}

	/**
	 * Resolve the html base url, i.e. the location of the fullcalendar html
	 * resources.
	 *
	 * @param html
	 * @return
	 */
	public static String resolve(String html) {
		return HTML_BASE_URL + html;
	}

	/**
	 * The RAP {@link Browser#execute(String)} might throw an
	 * {@link IllegalStateException} if script execution is not synchronized. Hence
	 * if we face a RAP environment we enforce synchronization.
	 *
	 * @param browser
	 * @param script
	 * @return
	 */
	public static boolean executeScript(final Browser browser, final String script) {
		log.debug("script [{}]: {}", script.hashCode(), script); //$NON-NLS-1$
		if (!browser.isDisposed()) {
			if (IS_RAP) {
				synchronized (BrowserLock.class) {
					try {
						return browser.execute(script);
					} catch (IllegalStateException ise) {
						log.warn("Catched IllegalStateException in script [{}]", script); //$NON-NLS-1$
					}

				}
			} else {
				return browser.execute(script);
			}
		}
		return false;
	}

	private class BrowserLock {
	}

}
