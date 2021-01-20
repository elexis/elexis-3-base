package ch.elexis.global_inbox.ui.parts.contentproposal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.global_inbox.Preferences;
import ch.elexis.global_inbox.preferencepage.TitleEntry;

public class TitleContentProposalProvider implements IContentProposalProvider {
	
	private List<TitleEntry> entries;
	
	public TitleContentProposalProvider(Text txtTitle){
		entries = ConfigServiceHolder.get()
			.getAsList(Preferences.PREF_TITLE_COMPLETION, new ArrayList<String>()).stream()
			.map(val -> new TitleEntry(val)).collect(Collectors.toList());
	}
	
	@Override
	public IContentProposal[] getProposals(String searchString, int position){
		List<IContentProposal> ret = new ArrayList<IContentProposal>();
		if (searchString != null && !searchString.isEmpty()) {
			 entries.stream()
				.filter(o -> o.getTitle().toLowerCase().contains(searchString.trim().toLowerCase()))
				.map(o -> new TitleEntryContentProposal(o)).forEach(ret::add);
		}
		return ret.toArray(new IContentProposal[ret.size()]);
	}
	
}
