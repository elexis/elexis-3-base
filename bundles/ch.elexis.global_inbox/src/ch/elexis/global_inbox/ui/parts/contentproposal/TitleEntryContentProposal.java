package ch.elexis.global_inbox.ui.parts.contentproposal;

import org.eclipse.jface.fieldassist.ContentProposal;

import ch.elexis.global_inbox.preferencepage.TitleEntry;

public class TitleEntryContentProposal extends ContentProposal {

	private TitleEntry entry;

	public TitleEntryContentProposal(TitleEntry entry) {
		super(entry.getTitle() + " (" + entry.getCategoryName() + ")", null);
		this.entry = entry;
	}

	public TitleEntry getTitleEntry() {
		return entry;
	}
}
