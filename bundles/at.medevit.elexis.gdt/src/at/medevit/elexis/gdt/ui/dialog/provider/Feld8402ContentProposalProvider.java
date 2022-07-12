/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.ui.dialog.provider;

import java.util.ArrayList;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class Feld8402ContentProposalProvider implements IContentProposalProvider {

	private String[] proposals;
	private String[] labels;
	private String[] detailDescription;
	private IContentProposal[] contentProposals;
	private boolean filterProposals = false;

	public Feld8402ContentProposalProvider(String[] proposals, String[] labels, String[] detailDescription) {
		super();
		this.proposals = proposals;
		this.labels = labels;
		this.detailDescription = detailDescription;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		if (filterProposals) {
			ArrayList<IContentProposal> list = new ArrayList<IContentProposal>();
			for (int i = 0; i < proposals.length; i++) {
				if (proposals[i].length() >= contents.length()
						&& proposals[i].substring(0, contents.length()).equalsIgnoreCase(contents)) {
					list.add(makeContentProposal(proposals[i], labels[i], detailDescription[i]));
				}
			}
			return (IContentProposal[]) list.toArray(new IContentProposal[list.size()]);
		}
		if (contentProposals == null) {
			refreshProposals();
		}
		return contentProposals;
	}

	public void setProposals(String[] proposals, String[] labels, String[] detailDescription) {
		this.proposals = proposals;
		this.labels = labels;
		this.detailDescription = detailDescription;
		contentProposals = null;
		refreshProposals();
	}

	public void setFiltering(boolean filterProposals) {
		this.filterProposals = filterProposals;
		contentProposals = null;
	}

	private void refreshProposals() {
		contentProposals = new IContentProposal[proposals.length];

		for (int i = 0; i < proposals.length; i++) {
			String detailDescriptionA = null;
			if (detailDescription != null && detailDescription[i] != null)
				detailDescriptionA = detailDescription[i];
			contentProposals[i] = makeContentProposal(proposals[i], labels[i], detailDescriptionA);
		}
	}

	private IContentProposal makeContentProposal(final String proposal, final String label,
			final String detailDescription) {
		return new IContentProposal() {

			public String getContent() {
				return proposal;
			}

			public String getDescription() {
				return detailDescription;
			}

			public String getLabel() {
				return proposal + " - " + label; //$NON-NLS-1$
			}

			public int getCursorPosition() {
				return proposal.length();
			}
		};
	}
}
