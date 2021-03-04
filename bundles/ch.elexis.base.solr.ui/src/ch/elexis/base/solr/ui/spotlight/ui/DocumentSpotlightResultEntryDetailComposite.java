package ch.elexis.base.solr.ui.spotlight.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.spotlight.ISpotlightResultEntry;
import ch.elexis.core.spotlight.ISpotlightResultEntry.Category;
import ch.elexis.core.spotlight.ui.ISpotlightResultEntryDetailComposite;

public class DocumentSpotlightResultEntryDetailComposite extends Composite
		implements ISpotlightResultEntryDetailComposite {
	
	private Text txtDocument;
	
	public DocumentSpotlightResultEntryDetailComposite(Composite parent, int style){
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		txtDocument = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		txtDocument.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}
	
	@Override
	protected void checkSubclass(){
		// Disable the check that prevents subclassing of SWT components
	}
	
	@Override
	public void setSpotlightEntry(ISpotlightResultEntry resultEntry){
		
		txtDocument.setText("");
		
		if (resultEntry != null) {
			String text = (String) resultEntry.getObject().get();
			txtDocument.setText(text.trim());
		}
		
	}
	
	@Override
	public Category appliedForCategory(){
		return Category.DOCUMENT;
	}

	@Override
	public boolean handleAltKeyPressed(int keyCode){
		return true;
	}
}
