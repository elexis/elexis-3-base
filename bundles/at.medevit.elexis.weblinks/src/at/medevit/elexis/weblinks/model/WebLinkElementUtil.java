/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/
package at.medevit.elexis.weblinks.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class WebLinkElementUtil {
	private static final String CFG_WEBLINK = "elexis.weblink";
	private static final String CFG_WEBLINK_TEXT = CFG_WEBLINK + ".text";
	private static final String CFG_WEBLINK_LINK = CFG_WEBLINK + ".link";
	private static final String CFG_WEBLINK_IDS = CFG_WEBLINK + ".ids";

	private static final String ID_DELIMITER = "||";
	private static final String ID_DELIMITER_ESCAPED = "\\|\\|";

	private static HashMap<String, String> placeholders = new HashMap<String, String>();

	public static String replacePlaceholders(String url) {
		while (hasPlaceholder(url)) {
			url = replacePlaceholder(url);
		}
		return url;
	}

	private static String replacePlaceholder(String url) {
		StringBuilder sb = new StringBuilder();
		int startIdx = url.indexOf('[');
		int endIdx = url.indexOf(']');

		sb.append(url.substring(0, startIdx));
		String placeholder = url.substring(startIdx + 1, endIdx);
		if (placeholder != null) {
			sb.append(getPlaceholder(placeholder));
		}
		sb.append(url.substring(endIdx + 1));

		return sb.toString();
	}

	public static String getPlaceholder(String placeholder) {
		return placeholders.get(placeholder);
	}

	public static void setPlaceholder(String placeholder, String value) {
		placeholders.put(placeholder, value);
	}

	private static boolean hasPlaceholder(String url) {
		int startIdx = url.indexOf('[');
		int endIdx = url.indexOf(']');
		return startIdx != -1 && endIdx != -1;
	}

	public static List<WebLinkElement> loadElements() {
		List<String> ids = loadIds();
		ArrayList<WebLinkElement> ret = new ArrayList<WebLinkElement>();
		for (String id : ids) {
			ret.add(new WebLinkElement(id));
		}
		return ret;
	}

	public static void saveElements(List<WebLinkElement> elements) {
		List<String> ids = new ArrayList<String>();
		for (WebLinkElement element : elements) {
			element.save();
			ids.add(element.id);
		}
		saveIds(ids);
	}

	private static List<String> loadIds() {
		String allIds = ConfigServiceHolder.getUser(CFG_WEBLINK_IDS, "");
		String[] ids = allIds.split(ID_DELIMITER_ESCAPED);
		ArrayList<String> ret = new ArrayList<String>();
		for (String id : ids) {
			if (!id.isEmpty()) {
				ret.add(id);
			}
		}
		return ret;
	}

	private static void saveIds(List<String> ids) {
		StringBuilder sb = new StringBuilder();

		if (ids.isEmpty()) {
			ConfigServiceHolder.setUser(CFG_WEBLINK_IDS, "");
		} else {
			for (String string : ids) {
				sb.append(string);
				sb.append(ID_DELIMITER);
			}
			sb.setLength(sb.length() - ID_DELIMITER.length());
			ConfigServiceHolder.setUser(CFG_WEBLINK_IDS, sb.toString());
		}
	}

	public static String getTextConfig(String id) {
		return CFG_WEBLINK_TEXT + "." + id;
	}

	public static String getLinkConfig(String id) {
		return CFG_WEBLINK_LINK + "." + id;
	}
}
