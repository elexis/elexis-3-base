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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import at.medevit.elexis.weblinks.model.WebLinkElement;

public class WebLinkEditor extends Composite {
	
	private WebLinkElement editElement;
	private WebLinkPreferencePage editPage;

	private Text webLinkText;
	private Text webLinkLink;
	private Button removeButton;
	
	public WebLinkEditor(WebLinkElement element, WebLinkPreferencePage page,
		Composite parent, int style){
		super(parent, style);

		editElement = element;
		editPage = page;

		setLayout(new GridLayout(3, false));
		
		webLinkText = new Text(this, SWT.BORDER);
		webLinkText.setText(element.getText());
		webLinkText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e){
				editElement.setText(webLinkText.getText());
			}
		});
		webLinkText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		webLinkText.setMessage("Name");
		
		webLinkLink = new Text(this, SWT.BORDER);
		webLinkLink.setText(element.getLink());
		webLinkLink.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e){
				editElement.setLink(webLinkLink.getText());
			}
		});
		webLinkLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		webLinkLink.setMessage("Link");

		removeButton = new Button(this, SWT.PUSH);
		removeButton.setText("löschen");
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				editPage.getElements().remove(editElement);
				editPage.refreshEditors();
			}
		});
	}
}
