/*******************************************************************************
 * Copyright (c) 2026.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ch.unibe.iam.scg.archie.tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import ch.unibe.iam.scg.archie.ui.widgets.FavoriteStatistics;
import junit.framework.JUnit4TestAdapter;

public class FavoriteStatisticsTest {

	@Test
	public void movesFavoritesToTheTopWithoutChangingTheirRelativeOrder() {
		List<String> titles = new ArrayList<>();
		titles.add("Alpha");
		titles.add("Bravo");
		titles.add("Charlie");
		Set<String> favorites = new HashSet<>();
		favorites.add("Charlie");
		favorites.add("Alpha");

		List<String> sorted = FavoriteStatistics.sort(titles, favorites);

		Assert.assertEquals("Alpha", sorted.get(0));
		Assert.assertEquals("Charlie", sorted.get(1));
		Assert.assertEquals("Bravo", sorted.get(2));
	}

	@Test
	public void preservesFavoriteTitlesWhenStoredAndReadAgain() {
		Set<String> favorites = new HashSet<>();
		favorites.add("Medelexis FIBU: Leistungsstatistik");
		favorites.add("Patientenstatistik");

		Set<String> restored = FavoriteStatistics.deserialize(FavoriteStatistics.serialize(favorites));

		Assert.assertEquals(favorites, restored);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(FavoriteStatisticsTest.class);
	}
}
