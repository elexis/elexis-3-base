package com.hilotec.elexis.kgview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;


abstract public class SimpleTextFView extends ViewPart {
	private boolean canEdit = true;
	private Text textfield;
	private String origPName;
	protected Composite area;
	private boolean modifiable = false;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		area = new Composite(parent, 0);
		area.setLayout(new GridLayout());
		
		textfield = new Text(area,
			(SWT.MULTI | SWT.WRAP | SWT.V_SCROLL));

		modifiable = false;
		textfield.setEditable(modifiable);
		textfield.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				fieldChanged();
			}
			
			public void focusGained(FocusEvent e) {}
		});
		
		// TraverseListener um das Standard-Tab-Verhalten zu ueberschreiben.
		// Wir wollen mit Tab Fokus weiterbewegen.
		textfield.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_TAB_NEXT ||
						e.detail == SWT.TRAVERSE_TAB_PREVIOUS)
					e.doit = true;
			}
		});

		GridData gd = new GridData();
		gd.horizontalAlignment = gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = gd.grabExcessVerticalSpace = true;
		textfield.setLayoutData(gd);
		
		origPName = getPartName();
		
		initialize();
	}
	
	protected void initialize() {}
	
	@Override
	public void setFocus() {}
	
	protected void setEnabled(boolean en) {
		modifiable = en && canEdit;
		textfield.setEditable(modifiable);
		if (!en) {
			setText("");
		}
	}
	
	protected boolean isEnabled() {
		return modifiable;
	}
	
	protected void setText(String text) {
		textfield.setText(text);
		setEmpty();
	}
	
	protected String getText() {
		return textfield.getText();
	}

	/**
	 * Wenn
	 */
	protected void setEmpty() {
		setPartName((isEmpty() ? "" : "* ") + origPName);
	}
	
	// Helper-Funktionen die in Subklassen ueberschrieben werden koennen,
	// muessen hier aber noch aufgerufen werden
	protected void fieldChanged() {
		setEmpty();
	}
	
	protected boolean isEmpty() {
		String text = textfield.getText();
		return text == null || text.isEmpty();
	}

	protected void setCanEdit(boolean edit) {
		canEdit = edit;
	}

	protected boolean getCanEdit() {
		return canEdit;
	}
}
