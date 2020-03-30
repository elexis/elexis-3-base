package at.medevit.elexis.agenda.ui.composite;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.slf4j.Logger;

public class HtmlBaseUrlResolver {

	/**
	 * Resolve the html base url provided via the e4 application model. The MPart
	 * entry has to provide a <code>htmlBaseUrl</code> context property. This allows
	 * to provide different relative urls in rcp and rap.
	 * 
	 * @param part
	 * @param html
	 * @param logger
	 * @return
	 */
	protected static String resolve(MPart part, String html, Logger logger) {
		String htmlBaseUrl = part.getProperties().get("htmlBaseUrl");
		try {
			if (htmlBaseUrl != null) {
				if (htmlBaseUrl.startsWith("platform:/plugin")) {
					URL resolved = FileLocator.resolve(new URL(htmlBaseUrl + html));
					return resolved.toString();
				} else {
					return htmlBaseUrl + html;
				}
			}

		} catch (IOException ioe) {
			logger.error("Could not resolve url - {} {}", htmlBaseUrl, html);
		}

		return null;
	}

}
