/*******************************************************************************
 * Copyright (c) 2026.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ch.unibe.iam.scg.archie.ui.widgets;

import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class ContainsContentProposalProvider implements IContentProposalProvider {

	private String[] proposals;

	public ContainsContentProposalProvider(String... proposals) {
		this.proposals = proposals;
	}

	public void setProposals(String... proposals) {
		this.proposals = proposals;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		ArrayList<IContentProposal> matches = new ArrayList<>();

		for (String proposal : proposals) {
			if (matches(proposal, contents)) {
				matches.add(new ContentProposal(proposal));
			}
		}

		return matches.toArray(new IContentProposal[matches.size()]);
	}
	
	public static boolean matches(String proposal, String contents) {
		String[] searchTerms = contents.toLowerCase(Locale.ROOT).trim().split("\\s+");
		return containsAll(proposal.toLowerCase(Locale.ROOT), searchTerms);
	}

	private static boolean containsAll(String proposal, String[] searchTerms) {
		for (String searchTerm : searchTerms) {
			if (!searchTerm.isEmpty() && !proposal.contains(searchTerm)) {
				return false;
			}
		}
		return true;
	}
}
