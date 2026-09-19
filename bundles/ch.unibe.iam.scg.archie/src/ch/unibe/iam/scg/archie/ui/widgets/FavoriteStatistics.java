/*******************************************************************************
 * Copyright (c) 2026.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ch.unibe.iam.scg.archie.ui.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles the storage format and display order of favorite statistics.
 */
public final class FavoriteStatistics {

	private static final String SEPARATOR = "\u001f"; //$NON-NLS-1$

	private FavoriteStatistics() {
	}

	public static Set<String> deserialize(String value) {
		Set<String> favorites = new HashSet<>();
		if (value == null || value.isEmpty()) {
			return favorites;
		}

		String[] titles = value.split(SEPARATOR, -1);
		for (String title : titles) {
			if (!title.isEmpty()) {
				favorites.add(title);
			}
		}
		return favorites;
	}

	public static String serialize(Collection<String> favorites) {
		StringBuilder value = new StringBuilder();
		for (String favorite : favorites) {
			if (value.length() > 0) {
				value.append(SEPARATOR);
			}
			value.append(favorite);
		}
		return value.toString();
	}

	/**
	 * Moves favorites to the top while preserving the original order within both
	 * groups.
	 */
	public static List<String> sort(Collection<String> titles, Set<String> favorites) {
		List<String> sorted = new ArrayList<>();
		for (String title : titles) {
			if (favorites.contains(title)) {
				sorted.add(title);
			}
		}
		for (String title : titles) {
			if (!favorites.contains(title)) {
				sorted.add(title);
			}
		}
		return sorted;
	}
}
