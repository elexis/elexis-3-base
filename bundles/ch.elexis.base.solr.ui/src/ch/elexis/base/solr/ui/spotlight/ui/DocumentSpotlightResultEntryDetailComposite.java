package ch.elexis.base.solr.ui.spotlight.ui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.spotlight.ISpotlightResultEntry;
import ch.elexis.core.spotlight.ISpotlightResultEntry.Category;
import ch.elexis.core.spotlight.ui.ISpotlightResultEntryDetailComposite;
import ch.elexis.core.spotlight.ui.controls.SpotlightSearchHelper;
import ch.elexis.core.spotlight.ui.internal.SpotlightShell;

public class DocumentSpotlightResultEntryDetailComposite extends Composite
		implements ISpotlightResultEntryDetailComposite {

	private StyledText txtDocument;

	public DocumentSpotlightResultEntryDetailComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		txtDocument = new StyledText(this,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		txtDocument.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setSpotlightEntry(ISpotlightResultEntry resultEntry) {

		txtDocument.setText(StringUtils.EMPTY);

		if (resultEntry != null) {
			String text = (String) resultEntry.getObject().get();
			txtDocument.setText(text.trim());
		}
		if (resultEntry != null) {
			// Hole den aktuellen Suchtext aus der Shell
			SpotlightShell shell = (SpotlightShell) getShell();
			String currentSearchText = shell.getSearchText().toLowerCase();

			// Überprüfe, ob der Suchtext nicht leer ist
			if (!currentSearchText.isEmpty()) {
				SpotlightSearchHelper.highlightSearchText(txtDocument, currentSearchText);
			}
		}
	}

	@Override
	public Category appliedForCategory() {
		return Category.DOCUMENT;
	}

	@Override
	public boolean handleAltKeyPressed(int keyCode) {
		return true;
	}
}
