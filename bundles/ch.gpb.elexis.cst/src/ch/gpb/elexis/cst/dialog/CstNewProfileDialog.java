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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.data.Mandant;
import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstProfile;

/**
 * @author daniel ludin ludin@swissonline.ch 27.06.2015
 *
 */

public class CstNewProfileDialog extends TitleAreaDialog {

	private Text txtFirstName;
	private Text lastNameText;

	CstProfile selectedProfile;

	private String groupName;
	private String groupDescription;
	Combo combo;
	Mandant mandant;

	HashMap hash = new HashMap();
	ArrayList<CstProfile> options = new ArrayList();

	public CstNewProfileDialog(Shell parentShell, Mandant mandant) {
		super(parentShell);
		this.mandant = mandant;

	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.Cst_Text_create_cstprofile);
		setMessage(Messages.Cst_Text_Enter_name_for_cstprofile, IMessageProvider.INFORMATION);
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

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText(Messages.CstCategoryDialog_lblNewLabel_text);

		combo = new Combo(container, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		List<CstProfile> cstProfiles = CstProfile.getAllProfiles(mandant.getId());
		Collections.sort(cstProfiles);

		for (CstProfile cstProfile : cstProfiles) {

			if (!cstProfile.getTemplate().equals("1")) {
				continue;
			}

			String line = cstProfile.getName();
			if (cstProfile.getDescription().length() > 0) {
				line += " (" + cstProfile.getDescription() + ")";
			}
			combo.add(line);
			options.add(cstProfile);
		}

		return area;
	}

	private void createFirstName(Composite container) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText(Messages.CstProfile_name);

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
		if (combo.getSelectionIndex() > -1) {
			selectedProfile = options.get(combo.getSelectionIndex());
		}
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

	public CstProfile getProfileToCopyFrom() {
		return selectedProfile;
	}
}