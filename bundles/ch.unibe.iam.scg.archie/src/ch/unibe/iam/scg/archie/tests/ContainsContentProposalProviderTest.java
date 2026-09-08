/*******************************************************************************
 * Copyright (c) 2026.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ch.unibe.iam.scg.archie.tests;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.junit.Assert;
import org.junit.Test;

import ch.unibe.iam.scg.archie.ui.widgets.ContainsContentProposalProvider;
import junit.framework.JUnit4TestAdapter;

public class ContainsContentProposalProviderTest {

	private final ContainsContentProposalProvider provider = new ContainsContentProposalProvider(
			"Medelexis FIBU: Leistungsstatistik", "Patientenstatistik");

	@Test
	public void findsWordsRegardlessOfCaseOrPosition() {
		assertContents(new String[] { "Medelexis FIBU: Leistungsstatistik" }, provider.getProposals("fIbU", 4));
	}

	@Test
	public void requiresEverySearchWordButNotTheirOrder() {
		assertContents(new String[] { "Medelexis FIBU: Leistungsstatistik" },
				provider.getProposals("leistungs FIBU", 16));
	}

	private void assertContents(String[] expected, IContentProposal[] actual) {
		Assert.assertEquals(expected.length, actual.length);
		for (int index = 0; index < expected.length; index++) {
			Assert.assertEquals(expected[index], actual[index].getContent());
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ContainsContentProposalProviderTest.class);
	}
}
