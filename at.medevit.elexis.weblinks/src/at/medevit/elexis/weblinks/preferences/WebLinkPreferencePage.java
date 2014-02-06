/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/
package at.medevit.elexis.weblinks.preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.weblinks.model.WebLinkElement;
import at.medevit.elexis.weblinks.model.WebLinkElementUtil;

public class WebLinkPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private List<WebLinkElement> elements;

	private List<WebLinkEditor> editors = new ArrayList<WebLinkEditor>();

	private Composite editorsComposite;
	private Composite prefAreaComposite;

	@Override
	public void init(IWorkbench workbench){
		elements = WebLinkElementUtil.loadElements();
	}
	
	@Override
	protected Control createContents(Composite parent){
		prefAreaComposite = new Composite(parent, SWT.NONE);
		prefAreaComposite.setLayout(new GridLayout());

		Button addBtn = new Button(prefAreaComposite, SWT.PUSH);
		addBtn.setText("Web Link hinzufügen");
		addBtn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				elements.add(new WebLinkElement(Long.toString(Calendar.getInstance()
					.getTimeInMillis())));
				refreshEditors();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});

		editorsComposite = new Composite(prefAreaComposite, SWT.NONE);
		editorsComposite.setLayout(new GridLayout());
		editorsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		refreshEditors();

		return prefAreaComposite;
	}
	
	public void refreshEditors(){
		// remove old editors
		for (WebLinkEditor editor : editors) {
			editor.dispose();
		}
		// create new editors
		for (WebLinkElement element : elements) {
			WebLinkEditor editor = new WebLinkEditor(element, this, editorsComposite, SWT.NONE);
			editor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			editors.add(editor);
		}
		
		prefAreaComposite.layout(true, true);
	}

	@Override
	public boolean performOk(){
		WebLinkElementUtil.saveElements(elements);
		return true;
	}
	
	public List<WebLinkElement> getElements(){
		return elements;
	}
}
