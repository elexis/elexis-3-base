package ch.elexis.base.solr.ui.spotlight.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.spotlight.ISpotlightResultEntry;
import ch.elexis.core.spotlight.ISpotlightResultEntry.Category;
import ch.elexis.core.spotlight.ui.ISpotlightResultEntryDetailComposite;

public class EncounterSpotlightResultEntryDetailComposite extends Composite
		implements ISpotlightResultEntryDetailComposite {
	
	private Text txtEncounter;
	
	public EncounterSpotlightResultEntryDetailComposite(Composite parent, int style){
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		txtEncounter = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		txtEncounter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}
	
	@Override
	protected void checkSubclass(){
		// Disable the check that prevents subclassing of SWT components
	}
	
	@Override
	public void setSpotlightEntry(ISpotlightResultEntry resultEntry){
		
		txtEncounter.setText("");
		
		if (resultEntry != null) {
			String encounterId = resultEntry.getLoaderString();
			IEncounter encounter =
				CoreModelServiceHolder.get().load(encounterId, IEncounter.class).orElse(null);
			if (encounter != null) {
				txtEncounter.setText(encounter.getHeadVersionInPlaintext());
			} else {
				txtEncounter.setText("not found");
			}
		}
		
	}
	
	@Override
	public Category appliedForCategory(){
		return Category.ENCOUNTER;
	}

	@Override
	public boolean handleAltKeyPressed(int keyCode){
		return true;
	}
}
