/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.dialog;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.gpb.elexis.cst.preferences.Messages;

/**
 * 
 * /**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 * 
 */

public class CstCategoryDialog extends TitleAreaDialog {

    private Text txtFirstName;
    private Text lastNameText;


    private String groupName;
    private String groupDescription;

    public CstCategoryDialog(Shell parentShell) {
	super(parentShell);

    }

    @Override
    public void create() {
	super.create();
	setTitle(Messages.Cst_Text_cstgroup_name);
	setMessage(Messages.Cst_Text_Enter_name_for_cstgroup, IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

	Composite area = (Composite) super.createDialogArea(parent);
	Composite container = new Composite(area, SWT.NONE);
	container.setLayoutData(new GridData(GridData.FILL_BOTH));
	GridLayout layout = new GridLayout(2, false);
	container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	container.setLayout(layout);

	createFirstName(container);
	createLastName(container);


	return area;
    }

    private void createFirstName(Composite container) {
	Label lbtFirstName = new Label(container, SWT.NONE);
	lbtFirstName.setText(Messages.Cst_Text_cstgroup_name);

	GridData dataFirstName = new GridData();
	dataFirstName.grabExcessHorizontalSpace = true;
	dataFirstName.horizontalAlignment = GridData.FILL;

	txtFirstName = new Text(container, SWT.BORDER);
	txtFirstName.setLayoutData(dataFirstName);
    }

    private void createLastName(Composite container) {
	Label lbtLastName = new Label(container, SWT.NONE);

	lbtLastName.setText(Messages.CstCategory_description);

	GridData dataLastName = new GridData();
	dataLastName.grabExcessHorizontalSpace = true;
	dataLastName.horizontalAlignment = GridData.FILL;
	lastNameText = new Text(container, SWT.BORDER);
	lastNameText.setLayoutData(dataLastName);
    }

    @Override
    protected boolean isResizable() {
	return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    // this method seems to be a hook and is called on close
    private void saveInput() {
	groupName = txtFirstName.getText();
	groupDescription = lastNameText.getText();
    }

    @Override
    protected void okPressed() {
	saveInput();
	super.okPressed();
    }

    public String getGroupName() {
	return groupName;
    }

    public String getGroupDescription() {
	return groupDescription;
    }

}