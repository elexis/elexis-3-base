package ch.elexis.base.solr.ui.spotlight.ui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.spotlight.ISpotlightResultEntry;
import ch.elexis.core.spotlight.ISpotlightResultEntry.Category;
import ch.elexis.core.spotlight.ui.ISpotlightResultEntryDetailComposite;
import ch.elexis.core.spotlight.ui.controls.SpotlightSearchHelper;
import ch.elexis.core.spotlight.ui.internal.SpotlightShell;

public class EncounterSpotlightResultEntryDetailComposite extends Composite
		implements ISpotlightResultEntryDetailComposite {

	private StyledText txtEncounter;

	public EncounterSpotlightResultEntryDetailComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		txtEncounter = new StyledText(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		txtEncounter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtEncounter.setBackground(getBackground());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setSpotlightEntry(ISpotlightResultEntry resultEntry) {

		txtEncounter.setText(StringUtils.EMPTY);

		if (resultEntry != null) {
			String encounterId = resultEntry.getLoaderString();
			IEncounter encounter = CoreModelServiceHolder.get().load(encounterId, IEncounter.class).orElse(null);
			if (encounter != null) {
				txtEncounter.setText(encounter.getHeadVersionInPlaintext());
				SpotlightShell shell = (SpotlightShell) getShell();
				String currentSearchText = shell.getSearchText().toLowerCase();
				if (!currentSearchText.isEmpty()) {
					SpotlightSearchHelper.highlightSearchText(txtEncounter, currentSearchText);
				}
			}
		}
	}


	@Override
	public Category appliedForCategory() {
		return Category.ENCOUNTER;
	}

	@Override
	public boolean handleAltKeyPressed(int keyCode) {
		return true;
	}
}